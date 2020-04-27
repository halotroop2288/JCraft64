package me.hydos.J64.audio;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import plugin.AudioPlugin;
import plugin.AudioPlugin.AudioInfo;

public class AudioCode {

	public static final int SND_IS_NOT_EMPTY = 0x4000000;
	public static final int SND_IS_FULL = 0x8000000;
	public static final int SEGMENTS = 3;
	public static final int LOCK_SIZE = 0x1000; // LOCKSIZE must not be fractional
	public static final int MAXBUFFER = (LOCK_SIZE * SEGMENTS + LOCK_SIZE);

	private AudioFormat wfm;
	private ExecutorService threadExecutor;

	private boolean audioIsPlaying;
	private SourceDataLine line;
	private int readLoc;
	private int writeLoc;
	private int SampleRate;
	private int SegmentSize;
	private int write_pos = 0, play_pos = 0;
	private int last_write = -1;
	private int dwBytes1;
	private int buffsize = 0;
	private int laststatus = 0;
	private int[] aiRegisters;
	private int[] miRegisters;
	private int MI_INTR_REG;
	private Runnable checkInterrupts;

	public interface AiCallBack {
		void callBack(int Status);
	};

	public class WriteLineData implements Runnable {
		byte[] buffer;

		public WriteLineData(byte[] source, int offset, int length) {
			buffer = new byte[length];
			System.arraycopy(source, offset, buffer, 0, length);
		}

		public void run() {
			if (line != null)
				line.write(buffer, 0, buffer.length);
		}
	};

	public AudioCode() {
		threadExecutor = Executors.newSingleThreadExecutor();
	}

	void UpdateStatus() {
		if (line == null) {
			return;
		}

		play_pos = line.getFramePosition() << 2;

		if (play_pos < buffsize) {
			write_pos = (buffsize * SEGMENTS) - buffsize;
		} else {
			write_pos = ((play_pos / buffsize) * buffsize) - buffsize;
		}

		int writediff = (write_pos - last_write);

		if (writediff < 0) {
			writediff += SEGMENTS * buffsize;
		}

		int play_seg = play_pos / buffsize;
		int write_seg = play_pos / buffsize;
		int last_seg = last_write / buffsize;

		if (last_seg == write_seg) { // The FIFO is still full and DMA is busy...
			aiRegisters[AudioPlugin.AI_STATUS_REG] |= 0xC0000000;
			aiRegisters[AudioPlugin.AI_LEN_REG] = buffsize - (play_pos - ((play_pos / buffsize) * buffsize));
			if (play_pos > write_pos) {
				aiRegisters[AudioPlugin.AI_LEN_REG] = buffsize - ((write_pos - play_pos) - buffsize);
			} else {
				aiRegisters[AudioPlugin.AI_LEN_REG] = (buffsize - play_pos);
			}
			aiRegisters[AudioPlugin.AI_LEN_REG] += buffsize;
			laststatus = 0xC0000000;
			return;
		}
		if (laststatus == 0xC0000000) { // Then we need to generate an interrupt now...
			aiRegisters[AudioPlugin.AI_LEN_REG] = buffsize;
			aiRegisters[AudioPlugin.AI_LEN_REG] = buffsize - (play_pos - ((play_pos / buffsize) * buffsize));
			aiRegisters[AudioPlugin.AI_STATUS_REG] = 0x40000000; // DMA is still busy...
			if (((play_seg - last_seg) & 7) > 3) {
				miRegisters[MI_INTR_REG] |= 0x04;
				checkInterrupts.run();
			}
			laststatus = 0x40000000;
			return;
		}
		if (laststatus == 0x40000000) {
			if (writediff > (int) (buffsize * 2)) { // This means we are doing a buffer underrun... damnit!
				aiRegisters[AudioPlugin.AI_LEN_REG] = 0;
				aiRegisters[AudioPlugin.AI_STATUS_REG] = 0x00000000; // DMA is still busy...
				if (((play_seg - last_seg) & 7) > 2) {
					miRegisters[MI_INTR_REG] |= 0x04;
					checkInterrupts.run();
				}
			}
			return;
		}

	}

	// Fills up a buffer and remixes the audio
	void FillBuffer(ByteBuffer buff, int offset, int len) {
		int write_seg = 0;
		int last_seg = 0;
		buffsize = len; // Save it globally
		play_pos = line.getFramePosition() << 2;

		if (play_pos < len) {
			write_pos = (len * SEGMENTS) - len;
		} else {
			write_pos = ((play_pos / len) * len) - len;
		}

		if (last_write == -1) {
			last_write = (write_pos - (2 * len)); // Back up 2 segments...
			if (last_write < 0) {
				last_write += (SEGMENTS * len);
			}
		}

		if (last_write == write_pos) { // Then we must freeze...

		}

		last_seg = (last_write / len);
		write_seg = (write_pos / len);

		if (last_seg == ((write_seg - 2) & 0x7)) { // Means first buffer is clear to write to...
			write_pos = (last_write + len);
			if (write_pos >= len * SEGMENTS) {
				write_pos -= (len * SEGMENTS);
			}
			// Set DMA Busy
			last_write += len;
			if (last_write >= len * SEGMENTS) {
				last_write -= (len * SEGMENTS);
			}
			aiRegisters[AudioPlugin.AI_STATUS_REG] |= 0x40000000;
			laststatus = 0x40000000;
		} else if (last_seg == ((write_seg - 1) & 0x7)) {
			// Set DMA Busy
			// Set FIFO Buffer Full
			last_write = write_pos; // Lets get it back up to speed for audio accuracy...
			aiRegisters[AudioPlugin.AI_STATUS_REG] |= 0xC0000000;
			laststatus = 0xC0000000;
		} else { // We get here if our audio stream from the game is running TOO slow...
			last_write = write_pos; // Lets get it back up to speed for audio accuracy...
			aiRegisters[AudioPlugin.AI_STATUS_REG] |= 0x00000000;
			laststatus = 0x00000000;
		}

		dwBytes1 = len;

		threadExecutor.execute(new WriteLineData(buff.array(), offset, dwBytes1));
	}

	// ------------------------------------------------------------------------
	// Setup and Teardown Functions

	boolean Initialize(AiCallBack aiCallBack, AudioInfo audioInfo) {
		audioIsPlaying = false;
		aiRegisters = audioInfo.aiRegisters;
		miRegisters = audioInfo.miRegisters;
		checkInterrupts = audioInfo.checkInterrupts;
		MI_INTR_REG = audioInfo.MI_INTR_REG;

		DeInitialize(); // Release just in case...

		wfm = new AudioFormat(44100, 16, 2, true, true);
		SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, wfm);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(wfm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		line.start();

		return true;
	}

	void DeInitialize() {
		if (line != null) {
			line.stop();
			line.close();
			line = null;
		}
	}

	// Generates nice alignment with N64 samples...
	void SetSegmentSize(int length) {
		if (SampleRate == 0) {
			return;
		}

		SegmentSize = length;

		DeInitialize();

		wfm = new AudioFormat(SampleRate, 16, 2, true, true);
		SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, wfm);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(wfm);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		line.start();
	}

	// Buffer Functions for the Audio Code

	void SetFrequency(int Frequency) {
		SampleRate = Frequency;
		SegmentSize = 0; // Trash it... we need to redo the Frequency anyway...
	}

	int AddBuffer(ByteBuffer start, int offset, int length) {
		int retVal = 0;

		if (length == 0) {
			return 0;
		}
		if (length == 0x8C0) { // TODO: This proves I need more buffering!!!
			length = 0x840;
		}
		if (length == 0x880) { // TODO: This proves I need more buffering!!!
			length = 0x840;
		}
		if (length != SegmentSize) {
			SetSegmentSize(length);
		}

		if (!audioIsPlaying) {
			StartAudio();
		}
		if (readLoc == writeLoc) { // Reset our pointer if we can...
			writeLoc = readLoc = 0;
		}

		if (readLoc != writeLoc) { // Then we have stuff in the buffer already... This is a double buffer
			retVal |= SND_IS_FULL;
		}

		retVal |= SND_IS_NOT_EMPTY; // Buffer is not empty...

//        System.arraycopy(start.array(), offset, SoundBuffer, writeLoc, length); // Buffer this audio data...
		writeLoc += length;

		FillBuffer(start, offset, length);
		readLoc = writeLoc = 0;
		UpdateStatus();

		return retVal;
	}

	// Management functions
	// TODO: For silent emulation... the Audio should still be "processed"
	// somehow...

	void StopAudio() {
		if (!audioIsPlaying) {
			return;
		}
		audioIsPlaying = false;
	}

	void StartAudio() {
		if (audioIsPlaying) {
			return;
		}
		audioIsPlaying = true;
	}

	int GetReadStatus() {
		if (buffsize == 0) {
			return 0;
		}

		UpdateStatus();
		return aiRegisters[AudioPlugin.AI_LEN_REG];
	}

}
