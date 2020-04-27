package me.hydos.J64;

import java.nio.ByteBuffer;

import me.hydos.J64.savechips.FlashRam;
import me.hydos.J64.savechips.Sram;

public class DirectMemoryAccess {

	// RegPI Peripheral Interface (PI) Registers
	public static final int PI_DRAM_ADDR_REG = 0;
	public static final int PI_CART_ADDR_REG = 1;
	public static final int PI_RD_LEN_REG = 2;
	public static final int PI_WR_LEN_REG = 3;
	public static final int PI_STATUS_REG = 4;
	public static final int PI_BSD_DOM1_LAT_REG = 5;
	public static final int PI_DOMAIN1_REG = 5;
	public static final int PI_BSD_DOM1_PWD_REG = 6;
	public static final int PI_BSD_DOM1_PGS_REG = 7;
	public static final int PI_BSD_DOM1_RLS_REG = 8;
	public static final int PI_BSD_DOM2_LAT_REG = 9;
	public static final int PI_DOMAIN2_REG = 9;
	public static final int PI_BSD_DOM2_PWD_REG = 10;
	public static final int PI_BSD_DOM2_PGS_REG = 11;
	public static final int PI_BSD_DOM2_RLS_REG = 12;

	public static final int PI_STATUS_DMA_BUSY = 0x01;
	public static final int PI_STATUS_IO_BUSY = 0x02;
	public static final int PI_STATUS_ERROR = 0x04;

	public static final int PI_SET_RESET = 0x01;
	public static final int PI_CLR_INTR = 0x02;

	// used by Memory, Dma, Cpu(TimerDone)
	public int[] regPI = new int[13];

	private boolean dmaUsed;
	private Runnable checkInterrupts;
	private int[] regMI;
	private int[] regSI;
	private int[] regSP;
	private Registers regs;
	private ByteBuffer rDram;
	private ByteBuffer dMem;
	private ByteBuffer rom;
	private ByteBuffer pifRam;
	private Pif pif;
	private Sram sram;
	private FlashRam flashRam;
	private boolean showUnhandledMemory;

	public Runnable timerInterrupt = new Runnable() {
		public void run() {
			regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
			checkInterrupts.run();
		}
	};

	/** Creates a new instance of Dma */
	public DirectMemoryAccess(Runnable checkInterrupts, Registers regs, ByteBuffer rDram, ByteBuffer dMem) {
		this.checkInterrupts = checkInterrupts;
		this.regs = regs;
		this.regMI = regs.regMI;
		this.rDram = rDram;
		this.dMem = dMem;
	}

	public void showUnhandledMemory(boolean show) {
		showUnhandledMemory = show;
	}

	// called by Main
	public void setSaveChips(Sram sram, FlashRam flashRam) {
		this.sram = sram;
		this.flashRam = flashRam;
	}

	// called by Main
	public void connect(Pif pif, RegisterSP rsp, ByteBuffer rom) {
		this.pif = pif;
		this.pifRam = pif.pifRam;
		this.regSI = pif.regSI;
		this.regSP = rsp.regSP;
		this.rom = rom;
	}

	// called by Memory
	public void piDmaRead() {
		if (regPI[PI_DRAM_ADDR_REG] + regPI[PI_RD_LEN_REG] + 1 > rDram.capacity()) {
			System.err.printf("PI_DMA_READ not in Memory\n");
			regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
			checkInterrupts.run();
			return;
		}

		if (regPI[PI_CART_ADDR_REG] >= 0x08000000 && regPI[PI_CART_ADDR_REG] <= 0x08010000) {
			if (regs.saveUsing == Registers.AUTO)
				regs.saveUsing = Registers.SRAM;

			if (regs.saveUsing == Registers.SRAM) {
				rDram.position(regPI[PI_DRAM_ADDR_REG]);
				sram.dmaToSram(rDram.slice(), regPI[PI_CART_ADDR_REG] - 0x08000000, regPI[PI_RD_LEN_REG] + 1);
				regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
				regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
				checkInterrupts.run();
				return;
			}
			if (regs.saveUsing == Registers.FLASHRAM) {
				rDram.position(regPI[PI_DRAM_ADDR_REG]);
				flashRam.dmaToFlashram(rDram.slice(), regPI[PI_CART_ADDR_REG] - 0x08000000,
//                        regPI[PI_WR_LEN_REG] + 1
						regPI[PI_RD_LEN_REG] + 1);
				regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
				regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
				checkInterrupts.run();
				return;
			}
		}
		if (regs.saveUsing == Registers.FLASHRAM) {
			System.err.printf("**** FLashRam DMA Read address %X *****\n", regPI[PI_CART_ADDR_REG]);
			regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
			checkInterrupts.run();
			return;
		}
		System.err.printf("PI_DMA_READ where are you dmaing to ?\n");
		regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
		regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
		checkInterrupts.run();
		return;
	}

	// called by Memory
	public void piDmaWrite() {
		regPI[PI_STATUS_REG] |= PI_STATUS_DMA_BUSY;
		if (regPI[PI_DRAM_ADDR_REG] + regPI[PI_WR_LEN_REG] + 1 > rDram.capacity()) {
			System.err.printf("PI_DMA_WRITE not in Memory\n");
			regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
			checkInterrupts.run();
			return;
		}

		if (regPI[PI_CART_ADDR_REG] >= 0x08000000 && regPI[PI_CART_ADDR_REG] <= 0x08010000) {
			if (regs.saveUsing == Registers.AUTO)
				regs.saveUsing = Registers.SRAM;

			if (regs.saveUsing == Registers.SRAM) {
				rDram.position(regPI[PI_DRAM_ADDR_REG]);
				sram.dmaFromSram(rDram.slice(), regPI[PI_CART_ADDR_REG] - 0x08000000, regPI[PI_WR_LEN_REG] + 1);
				regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
				regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
				checkInterrupts.run();
				return;
			}
			if (regs.saveUsing == Registers.FLASHRAM) {
				rDram.position(regPI[PI_DRAM_ADDR_REG]);
				flashRam.dmaFromFlashram(rDram.slice(), regPI[PI_CART_ADDR_REG] - 0x08000000, regPI[PI_WR_LEN_REG] + 1);
				regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
				regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
				checkInterrupts.run();
			}
			return;
		}

		if (regPI[PI_CART_ADDR_REG] >= 0x10000000 && regPI[PI_CART_ADDR_REG] <= 0x1FBFFFFF) {
			regPI[PI_CART_ADDR_REG] -= 0x10000000;
			if (regPI[PI_CART_ADDR_REG] + regPI[PI_WR_LEN_REG] + 1 < rom.capacity()) {
				for (int i = 0; i < regPI[PI_WR_LEN_REG] + 1; i++) {
					rDram.put((regPI[PI_DRAM_ADDR_REG] + i), rom.get((regPI[PI_CART_ADDR_REG] + i)));
				}
			} else {
				int len = rom.capacity() - regPI[PI_CART_ADDR_REG];
				for (int i = 0; i < len; i++) {
					rDram.put((regPI[PI_DRAM_ADDR_REG] + i), rom.get((regPI[PI_CART_ADDR_REG] + i)));
				}
				for (int i = len; i < regPI[PI_WR_LEN_REG] + 1 - len; i++) {
					rDram.put((regPI[PI_DRAM_ADDR_REG] + i), (byte) 0);
				}
			}
			regPI[PI_CART_ADDR_REG] += 0x10000000;

			if (!dmaUsed)
				firstDma();

			regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
			checkInterrupts.run();
			// cop0.checkTimer();
			return;
		}

		if (showUnhandledMemory)
			System.err.printf("PI_DMA_WRITE not in ROM\n");
		regPI[PI_STATUS_REG] &= ~PI_STATUS_DMA_BUSY;
		regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_PI;
		checkInterrupts.run();
	}

	// called by Memory
	public void siDmaRead() {
		if ((int) regSI[Pif.SI_DRAM_ADDR_REG] > (int) rDram.capacity()) {
			System.err.printf("SI DMA READ\nSI_DRAM_ADDR_REG not in RDRam space\n");
			return;
		}

		pif.pifRamRead();
		regSI[Pif.SI_DRAM_ADDR_REG] &= 0xFFFFFFF8;
		if ((int) regSI[Pif.SI_DRAM_ADDR_REG] < 0) {
			int rdramPos = (int) regSI[Pif.SI_DRAM_ADDR_REG];
			for (int count = 0; count < 0x40; count++, rdramPos++) {
				if (rdramPos < 0) {
					continue;
				}
				rDram.put(rdramPos, pifRam.get(count));
			}
		} else {
			int rdramPos = (int) regSI[Pif.SI_DRAM_ADDR_REG];
			for (int i = 0; i < 64; i += 4, rdramPos += 4)
				rDram.putInt(rdramPos, pifRam.getInt(i));
		}

		regSI[Pif.SI_STATUS_REG] |= Pif.SI_STATUS_INTERRUPT;
		regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_SI;
		checkInterrupts.run();
	}

	// called by Memory
	public void siDmaWrite() {
		if ((int) regSI[Pif.SI_DRAM_ADDR_REG] > (int) rDram.capacity()) {
			System.err.printf("SI DMA WRITE\nSI_DRAM_ADDR_REG not in RDRam space\n");
			return;
		}

		regSI[Pif.SI_DRAM_ADDR_REG] &= 0xFFFFFFF8;
		if ((int) regSI[Pif.SI_DRAM_ADDR_REG] < 0) {
			int rdramPos = (int) regSI[Pif.SI_DRAM_ADDR_REG];
			for (int count = 0; count < 0x40; count++, rdramPos++) {
				if (rdramPos < 0) {
					pifRam.put(count, (byte) 0);
					continue;
				}
				pifRam.put(count, rDram.get(rdramPos));
			}
		} else {
			int rdramPos = (int) regSI[Pif.SI_DRAM_ADDR_REG];
			for (int i = 0; i < 64; i += 4, rdramPos += 4)
				pifRam.putInt(i, rDram.getInt(rdramPos));
		}

		pif.pifRamWrite();

		regSI[Pif.SI_STATUS_REG] |= Pif.SI_STATUS_INTERRUPT;
		regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_SI;
		checkInterrupts.run();
	}

	// called by Memory
	public void spDmaRead() {
		regSP[RegisterSP.SP_DRAM_ADDR_REG] &= 0x1FFFFFFF;

		if (regSP[RegisterSP.SP_DRAM_ADDR_REG] > rDram.capacity()) {
			System.err.printf("SP DMA READ\nSP_DRAM_ADDR_REG not in RDRam space\n");
			regSP[RegisterSP.SP_DMA_BUSY_REG] = 0;
			regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_DMA_BUSY;
			return;
		}

		if (regSP[RegisterSP.SP_RD_LEN_REG] + 1 + (regSP[RegisterSP.SP_MEM_ADDR_REG] & 0xFFF) > 0x1000) {
			System.err.printf("SP DMA READ\ncould not fit copy in memory segement\n");
			return;
		}

		System.arraycopy(rDram.array(), regSP[RegisterSP.SP_DRAM_ADDR_REG], dMem.array(), regSP[RegisterSP.SP_MEM_ADDR_REG] & 0x1FFF,
				regSP[RegisterSP.SP_RD_LEN_REG] + 1);

		regSP[RegisterSP.SP_DMA_BUSY_REG] = 0;
		regSP[RegisterSP.SP_STATUS_REG] &= ~RegisterSP.SP_STATUS_DMA_BUSY;
	}

//void SP_DMA_WRITE (void) {
//	if (SP_DRAM_ADDR_REG > RdramSize) {
//		MessageBox(NULL,"SP DMA WRITE\nSP_DRAM_ADDR_REG not in RDRam space","Error",MB_OK);
//		return;
//	}
//
//	if (SP_WR_LEN_REG + 1 + (SP_MEM_ADDR_REG & 0xFFF) > 0x1000) {
//		MessageBox(NULL,"SP DMA WRITE\ncould not fit copy in memory segement","Error",MB_OK);
//		return;
//	}
//
//	if ((SP_MEM_ADDR_REG & 3) != 0) { _asm int 3 }
//	if ((SP_DRAM_ADDR_REG & 3) != 0) { _asm int 3 }
//	if (((SP_WR_LEN_REG + 1) & 3) != 0) { _asm int 3 }
//
//	memcpy( N64MEM + SP_DRAM_ADDR_REG, DMEM + (SP_MEM_ADDR_REG & 0x1FFF),
//		SP_WR_LEN_REG + 1);
//
//	SP_DMA_BUSY_REG = 0;
//	SP_STATUS_REG  &= ~SP_STATUS_DMA_BUSY;
//}

	private void firstDma() {
		dmaUsed = true;
		switch (pif.getCicChipID(rom)) {
		case 1:
			rDram.putInt(0x318, rDram.capacity());
			break;
		case 2:
			rDram.putInt(0x318, rDram.capacity());
			break;
		case 3:
			rDram.putInt(0x318, rDram.capacity());
			break;
		case 5:
			rDram.putInt(0x3F0, rDram.capacity());
			break;
		case 6:
			rDram.putInt(0x318, rDram.capacity());
			break;
		default:
			System.err.printf("Unhandled CicChip(%d) in first DMA\n", pif.getCicChipID(rom));
		}
	}

}
