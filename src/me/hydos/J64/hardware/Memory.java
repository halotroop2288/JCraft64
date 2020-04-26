package me.hydos.J64.hardware;

import java.nio.ByteBuffer;
import java.util.HashMap;

import me.hydos.J64.DirectMemoryAccess;
import me.hydos.J64.Pif;
import me.hydos.J64.RegisterSP;
import me.hydos.J64.Registers;
import me.hydos.J64.savechips.FlashRam;
import plugin.AudioPlugin;
import plugin.GfxPlugin;

public class Memory {

	private static final boolean DEBUG_MEMORY = false;

	// RegRI  Registers
	private static final int RI_MODE_REG = 0;
	private static final int RI_CONFIG_REG = 1;
	private static final int RI_CURRENT_LOAD_REG = 2;
	private static final int RI_SELECT_REG = 3;
	private static final int RI_REFRESH_REG = 4;
	private static final int RI_LATENCY_REG = 5;
	private static final int RI_RERROR_REG = 6;
	private static final int RI_WERROR_REG = 7;

	// RDRAM registerssssssss
	private static final int RDRAM_CONFIG_REG = 0;
	private static final int RDRAM_DEVICE_ID_REG = 1;
	private static final int RDRAM_DELAY_REG = 2;
	private static final int RDRAM_MODE_REG = 3;
	private static final int RDRAM_REF_INTERVAL_REG = 4;
	private static final int RDRAM_REF_ROW_REG = 5;
	private static final int RDRAM_RAS_INTERVAL_REG = 6;
	private static final int RDRAM_MIN_INTERVAL_REG = 7;
	private static final int RDRAM_ADDR_SELECT_REG = 8;
	private static final int RDRAM_DEVICE_MANUF_REG = 9;

	private static final int IMEM_START = 0x00001000;

	public int[] audioIntrReg = new int[1];

	public ByteBuffer RDRAM;
	private final byte[] rdram;

	public ByteBuffer DMEM;
	private final byte[] dmem;
	public ByteBuffer IMEM;

	private Cop0 cop0;
	private RegisterSP rsp;
	private Video video;
	private Audio audio;
	private Pif pif;
	private DirectMemoryAccess dma;
	private FlashRam flashRam;
	private int[] regSP;
	private int[] regVI;
	private int[] regSI;
	private int[] regAI;
	private int[] regPI;
	private int[] regMI;
	private int[] regDPC;
	private Registers regs;
	private boolean writtenToRom = false;
	private int wroteToRom;
	private final HashMap<Integer, Byte> bMem = new HashMap<>();
	private int[] regRDRAM = new int[10];
	private int[] regRI = new int[8];
	private Runnable checkInterrupts;
	private ByteBuffer rom;

	public long millisInAudio;

	public static class MemoryException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5727050717734087308L;

		public MemoryException(String message) {
			super(message);
		}
	}

	/** Creates a new instance of Memory */
	public Memory(int size) throws MemoryException {
//        this.checkInterrupts = checkInterrupts;

		try {
//            RDRAM = ByteBuffer.allocate(0x00800000); // (8MB)
			RDRAM = ByteBuffer.allocate(size);
			rdram = RDRAM.array();
			DMEM = ByteBuffer.allocate(0x2000); // 8,192b
			dmem = DMEM.array();
			DMEM.position(IMEM_START);
			IMEM = DMEM.slice();
		} catch (OutOfMemoryError ex) {
			throw new MemoryException("Not enough memory for RDRAM!");
		}
	}

	public void map(Runnable checkInterrupts, RegisterSP rsp, Video video, Audio audio, Pif pif, DirectMemoryAccess dma, FlashRam flashRam,
			Registers regs, ByteBuffer rom) {
		this.checkInterrupts = checkInterrupts;
		this.rsp = rsp;
		this.video = video;
		this.audio = audio;
		this.pif = pif;
		this.dma = dma;
		this.flashRam = flashRam;
		this.regAI = audio.regAI;
		this.regDPC = regs.regDPC;
		this.regMI = regs.regMI;
		this.regPI = dma.regPI;
		this.regSI = pif.regSI;
		this.regSP = rsp.regSP;
		this.regVI = video.regVI;
		this.audioIntrReg = audio.audioIntrReg;
		this.regs = regs;
		this.rom = rom;
	}

	// called by Main
	public void setTimer(Cop0 cop0) {
		this.cop0 = cop0;
	}

	// called by Cpu
	public byte loadByte(int pAddr) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("LB:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
			return RDRAM.get(pAddr);
		else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
			return RDRAM.get(pAddr);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory LB access: " + Integer.toHexString(pAddr));
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
			return DMEM.get(pAddr - 0x04000000);
		else
			return lbNonMemory(pAddr);
	}

	// called by Cpu
	public short loadHalfWord(int pAddr) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("LH:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
			return RDRAM.getShort(pAddr);
		else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
			return RDRAM.getShort(pAddr);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory LH access: " + Integer.toHexString(pAddr));
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
			return DMEM.getShort(pAddr - 0x04000000);
		else
			return lhNonMemory(pAddr);
	}

	// called by Cpu
	public final int loadWord(int pAddr) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("LW:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00800000) // RAM = 0x00000000 to 0x003FFFFF, Extended RAM = 0x00400000 to
														// 0x007FFFFF
			return (rdram[pAddr] << 24) | ((rdram[pAddr + 1] & 0xff) << 16) | ((rdram[pAddr + 2] & 0xff) << 8)
					| (rdram[pAddr + 3] & 0xff);
//        if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
//            return RDRAM.getInt(pAddr);
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
//            return DMEM.getInt(pAddr - 0x04000000);
			return (dmem[pAddr - 0x04000000] << 24) | ((dmem[pAddr - 0x03FFFFFF] & 0xff) << 16)
					| ((dmem[pAddr - 0x03FFFFFE] & 0xff) << 8) | (dmem[pAddr - 0x03FFFFFD] & 0xff);
//        else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
//            return RDRAM.getInt(pAddr);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory LW access: " + Integer.toHexString(pAddr));
		else
			return lwNonMemory(pAddr);
	}

	// called by Cpu
	public long loadDoubleWord(int pAddr) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("LD:%X ", pAddr);
		return (((long) loadWord(pAddr)) << 32) | (((long) loadWord(pAddr + 4)) & 0xFFFFFFFFL);
	}

	// called by Cpu
	public void storeByte(int pAddr, byte value) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("SB:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
			RDRAM.put(pAddr, value);
		else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
			RDRAM.put(pAddr, value);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory SB access: " + Integer.toHexString(pAddr));
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
			DMEM.put(pAddr - 0x04000000, value);
		else
			sbNonMemory(pAddr, value);
	}

	// called by Cpu
	public void storeHalfWord(int pAddr, short value) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("SH:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
			RDRAM.putShort(pAddr, value);
		else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
			RDRAM.putShort(pAddr, value);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory SH access: " + Integer.toHexString(pAddr));
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
			DMEM.putShort(pAddr - 0x04000000, value);
		else
			shNonMemory(pAddr, value);
	}

	// called by Cpu
	public void storeWord(int pAddr, int value) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("SW:%X ", pAddr);
		if (pAddr >= 0x00000000 && pAddr < 0x00400000) // RAM
			RDRAM.putInt(pAddr, value);
		else if (pAddr >= 0x00400000 && pAddr < 0x00800000) // Extended RAM
			RDRAM.putInt(pAddr, value);
		else if (pAddr >= 0x00800000 && pAddr < 0x03F00000) // ?? Unused
			throw new MemoryException("Illegal Memory  SW access: " + Integer.toHexString(pAddr));
		else if (pAddr >= 0x04000000 && pAddr < 0x04002000)
			DMEM.putInt(pAddr - 0x04000000, value);
		else
			swNonMemory(pAddr, value);
	}

	// called by Cpu
	public void storeDoubleWord(int pAddr, long value) throws MemoryException {
		if (DEBUG_MEMORY)
			System.out.printf("SD:%X ", pAddr);
		storeWord(pAddr, (int) (value >> 32));
		storeWord(pAddr + 4, (int) value);
	}

// Private Methods /////////////////////////////////////////////////////////////

	private byte lbNonMemory(int pAddr) throws MemoryException {
		if (pAddr >= 0x10000000 && pAddr < 0x16000000) {
			if (writtenToRom)
				throw new MemoryException("Illegal LB: writtenToRom");
			if ((pAddr - 0x10000000) < rom.capacity())
				return rom.get(pAddr - 0x10000000);
			else
				return 0;
		}

		switch (pAddr & 0xFFF00000) {
		default:
			Byte b = (Byte) bMem.get(pAddr);
			return ((b == null) ? 0 : b);
		}
	}

	private short lhNonMemory(int pAddr) throws MemoryException {
		switch (pAddr & 0xFFF00000) {
		default:
			Byte bb1 = (Byte) bMem.get(pAddr);
			int b1 = ((bb1 == null) ? 0 : bb1) & 0xFF;
			Byte bb2 = (Byte) bMem.get(pAddr + 1);
			int b2 = ((bb2 == null) ? 0 : bb2) & 0xFF;
			return (short) ((b1 << 8) | b2);
		}
	}

	private int lwNonMemory(int pAddr) throws MemoryException {
		if (pAddr >= 0x10000000 && pAddr < 0x16000000) {
			if (writtenToRom) {
				writtenToRom = false;
				return wroteToRom;
			}
			if ((pAddr - 0x10000000) < rom.capacity())
				return rom.getInt(pAddr - 0x10000000);
			else
				return ((pAddr & 0xFFFF) << 16) | (pAddr & 0xFFFF);
		}

		switch (pAddr & 0xFFF00000) {
		case 0x03F00000:
			switch (pAddr) {
			case 0x03F00000:
				return regRDRAM[RDRAM_CONFIG_REG];
			case 0x03F00004:
				return regRDRAM[RDRAM_DEVICE_ID_REG];
			case 0x03F00008:
				return regRDRAM[RDRAM_DELAY_REG];
			case 0x03F0000C:
				return regRDRAM[RDRAM_MODE_REG];
			case 0x03F00010:
				return regRDRAM[RDRAM_REF_INTERVAL_REG];
			case 0x03F00014:
				return regRDRAM[RDRAM_REF_ROW_REG];
			case 0x03F00018:
				return regRDRAM[RDRAM_RAS_INTERVAL_REG];
			case 0x03F0001C:
				return regRDRAM[RDRAM_MIN_INTERVAL_REG];
			case 0x03F00020:
				return regRDRAM[RDRAM_ADDR_SELECT_REG];
			case 0x03F00024:
				return regRDRAM[RDRAM_DEVICE_MANUF_REG];
			default:
				throw new MemoryException("Illegal RDRAM Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04000000:
			switch (pAddr) {
			case 0x04040010:
				return regSP[RegisterSP.SP_STATUS_REG];
			case 0x04040014:
				return regSP[RegisterSP.SP_DMA_FULL_REG];
			case 0x04040018:
				return regSP[RegisterSP.SP_DMA_BUSY_REG];
			case 0x04080000:
				return regSP[RegisterSP.SP_PC_REG];
			default:
				throw new MemoryException("Illegal SP Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04100000:
			switch (pAddr) {
			case 0x0410000C:
				return regDPC[Registers.DPC_STATUS_REG];
			case 0x04100010:
				return regDPC[Registers.DPC_CLOCK_REG];
			case 0x04100014:
				return regDPC[Registers.DPC_BUFBUSY_REG];
			case 0x04100018:
				return regDPC[Registers.DPC_PIPEBUSY_REG];
			case 0x0410001C:
				return regDPC[Registers.DPC_TMEM_REG];
			default:
				throw new MemoryException("Illegal DPC Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04300000:
			switch (pAddr) {
			case 0x04300000:
				return regMI[Registers.MI_MODE_REG];
			case 0x04300004:
				return regMI[Registers.MI_VERSION_REG];
			case 0x04300008:
				return regMI[Registers.MI_INTR_REG];
			case 0x0430000C:
				return regMI[Registers.MI_INTR_MASK_REG];
			default:
				throw new MemoryException("Illegal MI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04400000:
			switch (pAddr) {
			case 0x04400000:
				return regVI[GfxPlugin.VI_STATUS_REG];
			case 0x04400004:
				return regVI[GfxPlugin.VI_ORIGIN_REG];
			case 0x04400008:
				return regVI[GfxPlugin.VI_WIDTH_REG];
			case 0x0440000C:
				return regVI[GfxPlugin.VI_INTR_REG];
			case 0x04400010:
				if (video.gfxPlugin != null) {
					cop0.update();
//                            return video.gfxPlugin.updateCurrentHalfLine(cop0.timer);
					return video.updateCurrentHalfLine(cop0.timer);
				}
				// return video.updateCurrentHalfLine();
			case 0x04400014:
				return regVI[GfxPlugin.VI_BURST_REG];
			case 0x04400018:
				return regVI[GfxPlugin.VI_V_SYNC_REG];
			case 0x0440001C:
				return regVI[GfxPlugin.VI_H_SYNC_REG];
			case 0x04400020:
				return regVI[GfxPlugin.VI_LEAP_REG];
			case 0x04400024:
				return regVI[GfxPlugin.VI_H_START_REG];
			case 0x04400028:
				return regVI[GfxPlugin.VI_V_START_REG];
			case 0x0440002C:
				return regVI[GfxPlugin.VI_V_BURST_REG];
			case 0x04400030:
				return regVI[GfxPlugin.VI_X_SCALE_REG];
			case 0x04400034:
				return regVI[GfxPlugin.VI_Y_SCALE_REG];
			default:
				throw new MemoryException("Illegal VI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04500000:
			switch (pAddr) {
			case 0x04500004:
				if (audio.audioPlugin != null)
					return audio.audioPlugin.aiReadLength();
				else
					return 0;
			case 0x0450000C:
				return regAI[AudioPlugin.AI_STATUS_REG];
			default:
				throw new MemoryException("Illegal AI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04600000:
			switch (pAddr) {
			case 0x04600010:
				return regPI[DirectMemoryAccess.PI_STATUS_REG];
			case 0x04600014:
				return regPI[DirectMemoryAccess.PI_DOMAIN1_REG];
			case 0x04600018:
				return regPI[DirectMemoryAccess.PI_BSD_DOM1_PWD_REG];
			case 0x0460001C:
				return regPI[DirectMemoryAccess.PI_BSD_DOM1_PGS_REG];
			case 0x04600020:
				return regPI[DirectMemoryAccess.PI_BSD_DOM1_RLS_REG];
			case 0x04600024:
				return regPI[DirectMemoryAccess.PI_DOMAIN2_REG];
			case 0x04600028:
				return regPI[DirectMemoryAccess.PI_BSD_DOM2_PWD_REG];
			case 0x0460002C:
				return regPI[DirectMemoryAccess.PI_BSD_DOM2_PGS_REG];
			case 0x04600030:
				return regPI[DirectMemoryAccess.PI_BSD_DOM2_RLS_REG];
			default:
				throw new MemoryException("Illegal PI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04700000:
			switch (pAddr) {
			case 0x04700000:
				return regRI[Memory.RI_MODE_REG];
			case 0x04700004:
				return regRI[Memory.RI_CONFIG_REG];
			case 0x04700008:
				return regRI[Memory.RI_CURRENT_LOAD_REG];
			case 0x0470000C:
				return regRI[Memory.RI_SELECT_REG];
			case 0x04700010:
				return regRI[Memory.RI_REFRESH_REG];
			case 0x04700014:
				return regRI[Memory.RI_LATENCY_REG];
			case 0x04700018:
				return regRI[Memory.RI_RERROR_REG];
			case 0x0470001C:
				return regRI[Memory.RI_WERROR_REG];
			default:
				throw new MemoryException("Illegal RI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x04800000:
			switch (pAddr) {
			case 0x04800018:
				return regSI[Pif.SI_STATUS_REG];
			default:
				throw new MemoryException("Illegal SI Register LW: " + Integer.toHexString(pAddr));
			}
		case 0x05000000:
			return ((pAddr & 0xFFFF) << 16) | (pAddr & 0xFFFF);
		case 0x08000000:
			if (regs.saveUsing == Registers.AUTO)
				regs.saveUsing = Registers.FLASHRAM;
			if (regs.saveUsing != Registers.FLASHRAM)
				return ((pAddr & 0xFFFF) << 16) | (pAddr & 0xFFFF);
			return flashRam.readFromFlashStatus(pAddr);
		case 0x1FC00000:
			if (pAddr < 0x1FC007C0) {
				return (pif.pifRom[pAddr - 0x1FC00000] << 24) | (pif.pifRom[pAddr - 0x1FC00000 + 1] << 16)
						| (pif.pifRom[pAddr - 0x1FC00000 + 2] << 8) | (pif.pifRom[pAddr - 0x1FC00000 + 3]);
			} else if (pAddr < 0x1FC00800) {
				return pif.pifRam.getInt(pAddr - 0x1FC007C0);
			} else {
				throw new MemoryException("Illegal PifRam LW: " + Integer.toHexString(pAddr));
			}
		default:
			Byte bb1 = (Byte) bMem.get(pAddr);
			int b1 = ((bb1 == null) ? 0 : bb1) & 0xFF;
			Byte bb2 = (Byte) bMem.get(pAddr + 1);
			int b2 = ((bb2 == null) ? 0 : bb2) & 0xFF;
			Byte bb3 = (Byte) bMem.get(pAddr + 2);
			int b3 = ((bb3 == null) ? 0 : bb3) & 0xFF;
			Byte bb4 = (Byte) bMem.get(pAddr + 3);
			int b4 = ((bb4 == null) ? 0 : bb4) & 0xFF;
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}
	}

	private void sbNonMemory(int pAddr, byte value) throws MemoryException {
		switch (pAddr & 0xFFF00000) {
		case 0x00000000:
		case 0x00100000:
		case 0x00200000:
		case 0x00300000:
		case 0x00400000:
		case 0x00500000:
		case 0x00600000:
		case 0x00700000:
			throw new MemoryException("Illegal RAM Memory SB: " + Integer.toHexString(pAddr));
		default:
			bMem.put(pAddr, value);
		}
	}

	private void shNonMemory(int pAddr, short value) throws MemoryException {
		switch (pAddr & 0xFFF00000) {
		case 0x00000000:
		case 0x00100000:
		case 0x00200000:
		case 0x00300000:
		case 0x00400000:
		case 0x00500000:
		case 0x00600000:
		case 0x00700000:
			throw new MemoryException("Illegal RAM Memory SH: " + Integer.toHexString(pAddr));
		default:
			bMem.put(pAddr, (byte) ((value >> 8) & 0xFFFF));
			bMem.put(pAddr + 1, (byte) ((value) & 0xFFFF));
		}
	}

	private void swNonMemory(int pAddr, int value) throws MemoryException {
		if (pAddr >= 0x10000000 && pAddr < 0x16000000) {
			if ((pAddr - 0x10000000) < rom.capacity()) {
				writtenToRom = true;
				wroteToRom = value;
			} else {
				throw new MemoryException("Illegal ROM Memory SW: " + Integer.toHexString(pAddr));
			}
		}

		switch (pAddr & 0xFFF00000) {
		case 0x00000000:
		case 0x00100000:
		case 0x00200000:
		case 0x00300000:
		case 0x00400000:
		case 0x00500000:
		case 0x00600000:
		case 0x00700000:
			throw new MemoryException("Illegal RAM Memory SW: " + Integer.toHexString(pAddr));
		case 0x03F00000: // RDRAM Registers
			switch (pAddr) {
			case 0x03F00000:
				regRDRAM[RDRAM_CONFIG_REG] = value;
				break;
			case 0x03F00004:
				regRDRAM[RDRAM_DEVICE_ID_REG] = value;
				break;
			case 0x03F00008:
				regRDRAM[RDRAM_DELAY_REG] = value;
				break;
			case 0x03F0000C:
				regRDRAM[RDRAM_MODE_REG] = value;
				break;
			case 0x03F00010:
				regRDRAM[RDRAM_REF_INTERVAL_REG] = value;
				break;
			case 0x03F00014:
				regRDRAM[RDRAM_REF_ROW_REG] = value;
				break;
			case 0x03F00018:
				regRDRAM[RDRAM_RAS_INTERVAL_REG] = value;
				break;
			case 0x03F0001C:
				regRDRAM[RDRAM_MIN_INTERVAL_REG] = value;
				break;
			case 0x03F00020:
				regRDRAM[RDRAM_ADDR_SELECT_REG] = value;
				break;
			case 0x03F00024:
				regRDRAM[RDRAM_DEVICE_MANUF_REG] = value;
				break;
			case 0x03F04004:
				break;
			case 0x03F08004:
				break;
			case 0x03F80004:
				break;
			case 0x03F80008:
				break;
			case 0x03F8000C:
				break;
			case 0x03F80014:
				break;
			default:
				throw new MemoryException("Illegal RDRAM Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04000000:
			if (pAddr < 0x04002000) {
				throw new MemoryException("Illegal DMEM Memory SW: " + Integer.toHexString(pAddr));
			}
			switch (pAddr) {
				case 0x04040000 -> regSP[RegisterSP.SP_MEM_ADDR_REG] = value;
				case 0x04040004 -> regSP[RegisterSP.SP_DRAM_ADDR_REG] = value;
				case 0x04040008 -> {
					regSP[RegisterSP.SP_RD_LEN_REG] = value;
					dma.spDmaRead();
				}
				case 0x0404000C -> {
					regSP[RegisterSP.SP_WR_LEN_REG] = value;
					System.out.println("SP_DMA_WRITE");
				}
				case 0x04040010 -> {
					if ((value & RegisterSP.SP_CLR_HALT) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_HALT;
					}
					if ((value & RegisterSP.SP_SET_HALT) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_HALT;
					}
					if ((value & RegisterSP.SP_CLR_BROKE) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_BROKE;
					}
					if ((value & RegisterSP.SP_CLR_INTR) != 0) {
						regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_SP;
						checkInterrupts.run();
					}
					if ((value & RegisterSP.SP_SET_INTR) != 0)
						System.err.print("SP_SET_INTR\n");
					if ((value & RegisterSP.SP_CLR_SSTEP) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SSTEP;
					}
					if ((value & RegisterSP.SP_SET_SSTEP) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SSTEP;
					}
					if ((value & RegisterSP.SP_CLR_INTR_BREAK) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_INTR_BREAK;
					}
					if ((value & RegisterSP.SP_SET_INTR_BREAK) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_INTR_BREAK;
					}
					if ((value & RegisterSP.SP_CLR_SIG0) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG0;
					}
					if ((value & RegisterSP.SP_SET_SIG0) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG0;
					}
					if ((value & RegisterSP.SP_CLR_SIG1) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG1;
					}
					if ((value & RegisterSP.SP_SET_SIG1) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG1;
					}
					if ((value & RegisterSP.SP_CLR_SIG2) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG2;
					}
					if ((value & RegisterSP.SP_SET_SIG2) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG2;
					}
					if ((value & RegisterSP.SP_CLR_SIG3) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG3;
					}
					if ((value & RegisterSP.SP_SET_SIG3) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG3;
					}
					if ((value & RegisterSP.SP_CLR_SIG4) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG4;
					}
					if ((value & RegisterSP.SP_SET_SIG4) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG4;
					}
					if ((value & RegisterSP.SP_CLR_SIG5) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG5;
					}
					if ((value & RegisterSP.SP_SET_SIG5) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG5;
					}
					if ((value & RegisterSP.SP_CLR_SIG6) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG6;
					}
					if ((value & RegisterSP.SP_SET_SIG6) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG6;
					}
					if ((value & RegisterSP.SP_CLR_SIG7) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_SIG7;
					}
					if ((value & RegisterSP.SP_SET_SIG7) != 0) {
						regSP[RegisterSP.SP_STATUS_REG] |= RegisterSP.SP_STATUS_SIG7;
					}
					rsp.runRsp();
				}
				case 0x0404001C -> regSP[RegisterSP.SP_SEMAPHORE_REG] = 0;
				case 0x04080000 -> regSP[RegisterSP.SP_PC_REG] = value & 0xFFC;
				default -> throw new MemoryException("Illegal SP Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04100000: // DP Command Registers (RDP)
			switch (pAddr) {
			case 0x04100000:
				regDPC[Registers.DPC_START_REG] = value;
				regDPC[Registers.DPC_CURRENT_REG] = value;
				break;
			case 0x04100004:
				regDPC[Registers.DPC_END_REG] = value;
				if (rsp.gfx != null) {
					rsp.gfx.processRDPList();
				}
				break;
			case 0x0410000C:
				if ((value & Registers.DPC_CLR_XBUS_DMEM_DMA) != 0) {
					regDPC[Registers.DPC_STATUS_REG] &= ~Registers.DPC_STATUS_XBUS_DMEM_DMA;
				}
				if ((value & Registers.DPC_SET_XBUS_DMEM_DMA) != 0) {
					regDPC[Registers.DPC_STATUS_REG] |= Registers.DPC_STATUS_XBUS_DMEM_DMA;
				}
				if ((value & Registers.DPC_CLR_FREEZE) != 0) {
					regDPC[Registers.DPC_STATUS_REG] &= ~Registers.DPC_STATUS_FREEZE;
				}
				if ((value & Registers.DPC_SET_FREEZE) != 0) {
					regDPC[Registers.DPC_STATUS_REG] |= Registers.DPC_STATUS_FREEZE;
				}
				if ((value & Registers.DPC_CLR_FLUSH) != 0) {
					regDPC[Registers.DPC_STATUS_REG] &= ~Registers.DPC_STATUS_FLUSH;
				}
				if ((value & Registers.DPC_SET_FLUSH) != 0) {
					regDPC[Registers.DPC_STATUS_REG] |= Registers.DPC_STATUS_FLUSH;
				}
				break;
			default:
				throw new MemoryException("Illegal DPC Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04300000: // MIPS Interface (MI) Registers
			switch (pAddr) {
			case 0x04300000:
				regMI[Registers.MI_MODE_REG] &= ~0x7F;
				regMI[Registers.MI_MODE_REG] |= (value & 0x7F);
				if ((value & Registers.MI_CLR_INIT) != 0) {
					regMI[Registers.MI_MODE_REG] &= ~Registers.MI_MODE_INIT;
				}
				if ((value & Registers.MI_SET_INIT) != 0) {
					regMI[Registers.MI_MODE_REG] |= Registers.MI_MODE_INIT;
				}
				if ((value & Registers.MI_CLR_EBUS) != 0) {
					regMI[Registers.MI_MODE_REG] &= ~Registers.MI_MODE_EBUS;
				}
				if ((value & Registers.MI_SET_EBUS) != 0) {
					regMI[Registers.MI_MODE_REG] |= Registers.MI_MODE_EBUS;
				}
				if ((value & Registers.MI_CLR_DP_INTR) != 0) {
					regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_DP;
					checkInterrupts.run();
				}
				if ((value & Registers.MI_CLR_RDRAM) != 0) {
					regMI[Registers.MI_MODE_REG] &= ~Registers.MI_MODE_RDRAM;
				}
				if ((value & Registers.MI_SET_RDRAM) != 0) {
					regMI[Registers.MI_MODE_REG] |= Registers.MI_MODE_RDRAM;
				}
				break;
			case 0x0430000C:
				if ((value & Registers.MI_INTR_MASK_CLR_SP) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_SP;
				}
				if ((value & Registers.MI_INTR_MASK_SET_SP) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_SP;
				}
				if ((value & Registers.MI_INTR_MASK_CLR_SI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_SI;
				}
				if ((value & Registers.MI_INTR_MASK_SET_SI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_SI;
				}
				if ((value & Registers.MI_INTR_MASK_CLR_AI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_AI;
				}
				if ((value & Registers.MI_INTR_MASK_SET_AI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_AI;
				}
				if ((value & Registers.MI_INTR_MASK_CLR_VI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_VI;
				}
				if ((value & Registers.MI_INTR_MASK_SET_VI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_VI;
				}
				if ((value & Registers.MI_INTR_MASK_CLR_PI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_PI;
				}
				if ((value & Registers.MI_INTR_MASK_SET_PI) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_PI;
				}
				if ((value & Registers.MI_INTR_MASK_CLR_DP) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] &= ~Registers.MI_INTR_MASK_DP;
				}
				if ((value & Registers.MI_INTR_MASK_SET_DP) != 0) {
					regMI[Registers.MI_INTR_MASK_REG] |= Registers.MI_INTR_MASK_DP;
				}
				break;
			default:
				throw new MemoryException("Illegal MI Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04400000: // Video Interface (VI) Registers
			switch (pAddr) {
			case 0x04400000:
				if (regVI[GfxPlugin.VI_STATUS_REG] != value) {
					regVI[GfxPlugin.VI_STATUS_REG] = value;
					if (video.gfxPlugin != null)
						video.gfxPlugin.viStatusChanged();
				}
				break;
			case 0x04400004:
				regVI[GfxPlugin.VI_ORIGIN_REG] = (value & 0xFFFFFF);
				break;
			case 0x04400008:
				if (regVI[GfxPlugin.VI_WIDTH_REG] != value) {
					regVI[GfxPlugin.VI_WIDTH_REG] = value;
					if (video.gfxPlugin != null)
						video.gfxPlugin.viWidthChanged();
				}
				break;
			case 0x0440000C:
				regVI[GfxPlugin.VI_INTR_REG] = value;
				break;
			case 0x04400010:
				regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_VI;
				checkInterrupts.run();
				break;
			case 0x04400014:
				regVI[GfxPlugin.VI_BURST_REG] = value;
				break;
			case 0x04400018:
				regVI[GfxPlugin.VI_V_SYNC_REG] = value;
				break;
			case 0x0440001C:
				regVI[GfxPlugin.VI_H_SYNC_REG] = value;
				break;
			case 0x04400020:
				regVI[GfxPlugin.VI_LEAP_REG] = value;
				break;
			case 0x04400024:
				regVI[GfxPlugin.VI_H_START_REG] = value;
				break;
			case 0x04400028:
				regVI[GfxPlugin.VI_V_START_REG] = value;
				break;
			case 0x0440002C:
				regVI[GfxPlugin.VI_V_BURST_REG] = value;
				break;
			case 0x04400030:
				regVI[GfxPlugin.VI_X_SCALE_REG] = value;
				break;
			case 0x04400034:
				regVI[GfxPlugin.VI_Y_SCALE_REG] = value;
				break;
			default:
				throw new MemoryException("Illegal VI Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04500000: // Audio Interface (AI) Registers
//                long time = System.currentTimeMillis();
			switch (pAddr) {
			case 0x04500000:
				regAI[AudioPlugin.AI_DRAM_ADDR_REG] = value;
				break;
			case 0x04500004:
				regAI[AudioPlugin.AI_LEN_REG] = value;
				long time = System.currentTimeMillis();
				if (audio.audioPlugin != null)
					audio.audioPlugin.aiLenChanged();
				millisInAudio += (System.currentTimeMillis() - time);
				break;
			case 0x04500008:
				regAI[AudioPlugin.AI_CONTROL_REG] = (value & 1);
				break;
			case 0x0450000C:
				/* Clear Interrupt */;
				audioIntrReg[0] &= ~Registers.MI_INTR_AI;
				regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_AI;
				checkInterrupts.run();
				break;
			case 0x04500010:
				regAI[AudioPlugin.AI_DACRATE_REG] = value;
				if (audio.audioPlugin != null)
					audio.audioPlugin.aiDacrateChanged(AudioPlugin.SYSTEM_NTSC);
				break;
			case 0x04500014:
				regAI[AudioPlugin.AI_BITRATE_REG] = value;
				break;
			default:
				throw new MemoryException("Illegal AI Register SW: " + Integer.toHexString(pAddr));
			}
//                millisInAudio += (System.currentTimeMillis() - time);
			break;
		case 0x04600000: // Peripheral Interface (PI) Registers
			switch (pAddr) {
			case 0x04600000:
				regPI[DirectMemoryAccess.PI_DRAM_ADDR_REG] = value;
				break;
			case 0x04600004:
				regPI[DirectMemoryAccess.PI_CART_ADDR_REG] = value;
				break;
			case 0x04600008:
				regPI[DirectMemoryAccess.PI_RD_LEN_REG] = value;
				dma.piDmaRead();
				break;
			case 0x0460000C:
				regPI[DirectMemoryAccess.PI_WR_LEN_REG] = value;
				dma.piDmaWrite();
				break;
			case 0x04600010:
				if ((value & DirectMemoryAccess.PI_CLR_INTR) != 0) {
					regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_PI;
					checkInterrupts.run();
				}
				break;
			case 0x04600014:
				regPI[DirectMemoryAccess.PI_DOMAIN1_REG] = (value & 0xFF);
				break;
			case 0x04600018:
				regPI[DirectMemoryAccess.PI_BSD_DOM1_PWD_REG] = (value & 0xFF);
				break;
			case 0x0460001C:
				regPI[DirectMemoryAccess.PI_BSD_DOM1_PGS_REG] = (value & 0xFF);
				break;
			case 0x04600020:
				regPI[DirectMemoryAccess.PI_BSD_DOM1_RLS_REG] = (value & 0xFF);
				break;
			// NEW
			case 0x04600024:
				regPI[DirectMemoryAccess.PI_DOMAIN2_REG] = (value & 0xFF);
				break;
			case 0x04600028:
				regPI[DirectMemoryAccess.PI_BSD_DOM2_PWD_REG] = (value & 0xFF);
				break;
			case 0x0460002C:
				regPI[DirectMemoryAccess.PI_BSD_DOM2_PGS_REG] = (value & 0xFF);
				break;
			case 0x04600030:
				regPI[DirectMemoryAccess.PI_BSD_DOM2_RLS_REG] = (value & 0xFF);
				break;
			default:
				throw new MemoryException("Illegal PI Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04700000: // RDRAM Interface (RI) Registers
			switch (pAddr) {
			case 0x04700000:
				regRI[Memory.RI_MODE_REG] = value;
				break;
			case 0x04700004:
				regRI[Memory.RI_CONFIG_REG] = value;
				break;
			case 0x04700008:
				regRI[Memory.RI_CURRENT_LOAD_REG] = value;
				break;
			case 0x0470000C:
				regRI[Memory.RI_SELECT_REG] = value;
				break;
			case 0x04700010:
				regRI[Memory.RI_REFRESH_REG] = value;
				break;
			case 0x04700014:
				regRI[Memory.RI_LATENCY_REG] = value;
				break;
			case 0x04700018:
				regRI[Memory.RI_RERROR_REG] = value;
				break;
			case 0x0470001C:
				regRI[Memory.RI_WERROR_REG] = value;
				break;
			default:
				throw new MemoryException("Illegal RI Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x04800000: // Serial Interface (SI) Registers
			switch (pAddr) {
			case 0x04800000:
				regSI[Pif.SI_DRAM_ADDR_REG] = value;
				break;
			case 0x04800004:
				regSI[Pif.SI_PIF_ADDR_RD64B_REG] = value;
				dma.siDmaRead();
				break;
			case 0x04800010:
				regSI[Pif.SI_PIF_ADDR_WR64B_REG] = value;
				dma.siDmaWrite();
				break;
			case 0x04800018:
				regSI[Pif.SI_STATUS_REG] &= ~Pif.SI_STATUS_INTERRUPT;
				regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_SI;
				// regSI[Pif.SI_STATUS_REG] &= ~Pif.SI_STATUS_INTERRUPT;
				checkInterrupts.run();
				break;
			default:
				throw new MemoryException("Illegal SI Register SW: " + Integer.toHexString(pAddr));
			}
			break;
		case 0x08000000: // Cartridge Domain 2 Address 2
			if (pAddr != 0x08010000)
				throw new MemoryException("Illegal FlashRam SW: " + Integer.toHexString(pAddr));
			if (regs.saveUsing == Registers.AUTO)
				regs.saveUsing = Registers.FLASHRAM;
			if (regs.saveUsing != Registers.FLASHRAM)
				return;
			flashRam.writeToFlashCommand(value);
			break;
		case 0x1FC00000: // PIF
			if (pAddr < 0x1FC007C0) { // PIF Boot rom
				throw new MemoryException("Illegal PifRam SW: " + Integer.toHexString(pAddr));
			} else if (pAddr < 0x1FC00800) { // PIF RAM
				pif.pifRam.putInt(pAddr - 0x1FC007C0, value);
				if (pAddr == 0x1FC007FC) {
					pif.pifRamWrite();
				}
				return;
			}
			throw new MemoryException("Illegal PifRam Register SW: " + Integer.toHexString(pAddr));
		default:
			bMem.put(pAddr, (byte) ((value >> 24) & 0xFFFF));
			bMem.put(pAddr + 1, (byte) ((value >> 16) & 0xFFFF));
			bMem.put(pAddr + 2, (byte) ((value >> 8) & 0xFFFF));
			bMem.put(pAddr + 3, (byte) ((value) & 0xFFFF));
		}
	}

}
