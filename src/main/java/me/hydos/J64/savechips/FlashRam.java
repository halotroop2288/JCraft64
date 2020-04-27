package me.hydos.J64.savechips;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class FlashRam {

	private static final int FLASHRAM_MODE_NOPES = 0;
	private static final int FLASHRAM_MODE_ERASE = 1;
	private static final int FLASHRAM_MODE_WRITE = 2;
	private static final int FLASHRAM_MODE_READ = 3;
	private static final int FLASHRAM_MODE_STATUS = 4;

	private int flashRamOffset;
	private int flashFlag = FLASHRAM_MODE_NOPES;
	private RandomAccessFile hFlashRamFile;
	private File file;
	private ByteBuffer flashRamPointer;
	private long flashStatus = 0;

	/** Creates a new instance of FlashRam */
	public FlashRam(File file) {
		this.file = file;
	}

	public FlashRam(String name) {
		this(new File(name));
	}

//    // called by Main
//    public void close() {
//        if (hFlashRamFile != null) {
//            try {
//                hFlashRamFile.close();
//                hFlashRamFile = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

	// called by Dma
	public void dmaFromFlashram(ByteBuffer dest, int startOffset, int len) {
		switch (flashFlag) {
		case FLASHRAM_MODE_READ:
			if (hFlashRamFile == null) {
				if (!loadFlashram())
					return;
			}
			if (len > 0x10000) {
				System.err.printf("DmaFromFlashram FlipBuffer to small (len: %d)\n", len);
				len = 0x10000;
			}
			if ((len & 3) != 0) {
				System.err.printf("Unaligned flash ram read ???\n");
				return;
			}
			startOffset = startOffset << 1;
			try {
				hFlashRamFile.seek(startOffset);
				hFlashRamFile.read(dest.array(), dest.arrayOffset(), len);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case FLASHRAM_MODE_STATUS:
			if (startOffset != 0 && len != 8)
				System.err.printf("Reading flashstatus not being handled correctly\nStart: %X len: %X\n", startOffset,
						len);
			dest.putInt(0, (int) (flashStatus >> 32));
			dest.putInt(4, (int) (flashStatus));
			break;
		default:
			System.err.printf("DmaFromFlashram Start: %X, Offset: %X len: %X\n", dest.arrayOffset(), startOffset, len);
		}
	}

	// called by Dma
	public void dmaToFlashram(ByteBuffer source, int startOffset, int len) {
		switch (flashFlag) {
		case FLASHRAM_MODE_WRITE:
			flashRamPointer = source;
			break;
		default:
			System.err.printf("DmaToFlashram Start: %X, Offset: %X len: %X\n", source.arrayOffset(), startOffset, len);
		}
	}

	// called by Memory
	public int readFromFlashStatus(int pAddr) {
		switch (pAddr) {
		case 0x08000000:
			return (int) (flashStatus >> 32);
		default:
			System.err.printf("Reading from flash ram status (%X)\n", pAddr);
			break;
		}
		return (int) (flashStatus >> 32);
	}

	// called by Memory
	public void writeToFlashCommand(int flashRamCommand) {
		byte[] emptyBlock = new byte[128];

		switch (flashRamCommand & 0xFF000000) {
		case 0xD2000000:
			switch (flashFlag) {
			case FLASHRAM_MODE_NOPES:
				break;
			case FLASHRAM_MODE_READ:
				break;
			case FLASHRAM_MODE_STATUS:
				break;
			case FLASHRAM_MODE_ERASE:
				Arrays.fill(emptyBlock, (byte) 0xFF);
				if (hFlashRamFile == null) {
					if (!loadFlashram()) {
						return;
					}
				}
				try {
					hFlashRamFile.seek(flashRamOffset);
					hFlashRamFile.write(emptyBlock, 0, 128);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case FLASHRAM_MODE_WRITE:
				if (hFlashRamFile == null) {
					if (!loadFlashram()) {
						return;
					}
				}
				try {
					hFlashRamFile.seek(flashRamOffset);
					hFlashRamFile.write(flashRamPointer.array(), flashRamPointer.arrayOffset(), 128);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				System.err.printf("Writing %X to flash ram command register\nFlashFlag: %d\n", flashRamCommand,
						flashFlag);
			}
			flashFlag = FLASHRAM_MODE_NOPES;
			break;
		case 0xE1000000:
			flashFlag = FLASHRAM_MODE_STATUS;
			flashStatus = 0x1111800100C20000L;
			break;
		case 0xF0000000:
			flashFlag = FLASHRAM_MODE_READ;
			flashStatus = 0x11118004F0000000L;
			break;
		case 0x4B000000:
			flashRamOffset = (flashRamCommand & 0xffff) * 128;
			break;
		case 0x78000000:
			flashFlag = FLASHRAM_MODE_ERASE;
			flashStatus = 0x1111800800C20000L;
			break;
		case 0xB4000000:
			flashFlag = FLASHRAM_MODE_WRITE; // ????
			break;
		case 0xA5000000:
			flashRamOffset = (flashRamCommand & 0xffff) * 128;
			flashStatus = 0x1111800400C20000L;
			break;
		default:
			System.err.printf("Writing %X to flash ram command register\n", flashRamCommand);
		}
	}

	private boolean loadFlashram() {
		try {
			hFlashRamFile = new RandomAccessFile(file, "rwd");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
