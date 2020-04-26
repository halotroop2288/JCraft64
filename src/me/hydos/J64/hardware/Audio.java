package me.hydos.J64.hardware;

import java.nio.ByteBuffer;
import java.util.Properties;
import javax.swing.JFrame;
import plugin.AudioPlugin;

public class Audio {

	public static final String AUDIO_PLUGIN = "AUDIO_PLUGIN";

	public int[] regAI = new int[6];

	// used by Memory
	public int[] audioIntrReg = new int[1];

	// used by Rsp
	public AudioPlugin audioPlugin;

	private Runnable checkInterrupts;
	private ByteBuffer rDram;
	private ByteBuffer dMem;
	private ByteBuffer iMem;

	/** Creates a new instance of Audio */
	public Audio(Runnable checkInterrupts, ByteBuffer rDram, ByteBuffer dMem, ByteBuffer iMem) {
		this.checkInterrupts = checkInterrupts;
		this.rDram = rDram;
		this.dMem = dMem;
		this.iMem = iMem;
	}

	public boolean setupPlugin(JFrame hWnd, Properties cfg) {
		// shutdownPlugins();
		String audio_plugin = cfg.getProperty(AUDIO_PLUGIN, "NO_AUDIO_PLUGIN");
		try {
			Class c = Class.forName(audio_plugin);
			audioPlugin = (AudioPlugin) c.newInstance();
		} catch (Exception ex) {
			System.err.println("No audio plugin loaded. " + ex.getMessage());
//            ex.printStackTrace();
			return false;
		}

		AudioPlugin.AudioInfo audioInfo = new AudioPlugin.AudioInfo();

		audioInfo.hwnd = hWnd;
		audioInfo.memoryBswaped = false; // true;
		audioInfo.rdram = rDram;
		audioInfo.dmem = dMem;
		audioInfo.imem = iMem;

		audioInfo.MI_INTR_REG = 0;
		audioInfo.miRegisters = audioIntrReg;
//        audioInfo.AI_DRAM_ADDR_REG = AudioPlugin.AI_DRAM_ADDR_REG;
//        audioInfo.AI_LEN_REG = AudioPlugin.AI_LEN_REG;
//        audioInfo.AI_CONTROL_REG = AudioPlugin.AI_CONTROL_REG;
//        audioInfo.AI_STATUS_REG = AudioPlugin.AI_STATUS_REG;
//        audioInfo.AI_DACRATE_REG = AudioPlugin.AI_DACRATE_REG;
//        audioInfo.AI_BITRATE_REG = AudioPlugin.AI_BITRATE_REG;
		audioInfo.aiRegisters = regAI;
		audioInfo.checkInterrupts = checkInterrupts;

		if (!audioPlugin.initiateAudio(audioInfo)) {
			System.err.println("Failed to Initilize Audio!");
			return false;
		}

		audioPlugin.aiUpdate(true);
//        hAudioThread = CreateThread(NULL,0,(LPTHREAD_START_ROUTINE)AudioThread, (LPVOID)NULL,0, &ThreadID);

		return true;
	}

//    private void shutdownPlugins() {
//        //TerminateThread(hAudioThread,0);
//        if (audioPlugin != null) { audioPlugin.CloseDLL(); }
//    }

}
