package me.hydos.J64;

import java.nio.ByteBuffer;
import plugin.AudioPlugin;
import plugin.GfxPlugin;

public class RegisterSP {

	// regSP SP Registers (RSP)
	public static final int SP_MEM_ADDR_REG = 0;
	public static final int SP_DRAM_ADDR_REG = 1;
	public static final int SP_RD_LEN_REG = 2;
	public static final int SP_WR_LEN_REG = 3;
	public static final int SP_STATUS_REG = 4;
	public static final int SP_DMA_FULL_REG = 5;
	public static final int SP_DMA_BUSY_REG = 6;
	public static final int SP_SEMAPHORE_REG = 7;
	public static final int SP_PC_REG = 8;

	public static final int SP_CLR_HALT = 0x00001; // Bit 0 = clear halt 
	public static final int SP_SET_HALT = 0x00002; // Bit 1 = set halt 
	public static final int SP_CLR_BROKE = 0x00004; // Bit 2 = clear broke 
	public static final int SP_CLR_INTR = 0x00008; // Bit 3 = clear intr 
	public static final int SP_SET_INTR = 0x00010; // Bit 4 = set intr 
	public static final int SP_CLR_SSTEP = 0x00020; // Bit 5 = clear sstep 
	public static final int SP_SET_SSTEP = 0x00040; // Bit 6 = set sstep 
	public static final int SP_CLR_INTR_BREAK = 0x00080; // Bit 7 = clear intr on break 
	public static final int SP_SET_INTR_BREAK = 0x00100; // Bit 8 = set intr on break 
	public static final int SP_CLR_SIG0 = 0x00200; // Bit 9 = clear signal 0 
	public static final int SP_SET_SIG0 = 0x00400; // Bit 10 = set signal 0 
	public static final int SP_CLR_SIG1 = 0x00800; // Bit 11 = clear signal 1 
	public static final int SP_SET_SIG1 = 0x01000; // Bit 12 = set signal 1 
	public static final int SP_CLR_SIG2 = 0x02000; // Bit 13 = clear signal 2 
	public static final int SP_SET_SIG2 = 0x04000; // Bit 14 = set signal 2 
	public static final int SP_CLR_SIG3 = 0x08000; // Bit 15 = clear signal 3 
	public static final int SP_SET_SIG3 = 0x10000; // Bit 16 = set signal 3 
	public static final int SP_CLR_SIG4 = 0x20000; // Bit 17 = clear signal 4 
	public static final int SP_SET_SIG4 = 0x40000; // Bit 18 = set signal 4 
	public static final int SP_CLR_SIG5 = 0x80000; // Bit 19 = clear signal 5 
	public static final int SP_SET_SIG5 = 0x100000; // Bit 20 = set signal 5 
	public static final int SP_CLR_SIG6 = 0x200000; // Bit 21 = clear signal 6 
	public static final int SP_SET_SIG6 = 0x400000; // Bit 22 = set signal 6 
	public static final int SP_CLR_SIG7 = 0x800000; // Bit 23 = clear signal 7 
	public static final int SP_SET_SIG7 = 0x1000000; // Bit 24 = set signal 7 

	public static final int SP_STATUS_HALT = 0x001; // Bit 0 = halt 
	public static final int SP_STATUS_BROKE = 0x002; // Bit 1 = broke 
	public static final int SP_STATUS_DMA_BUSY = 0x004; // Bit 2 = dma busy 
	public static final int SP_STATUS_DMA_FULL = 0x008; // Bit 3 = dma full 
	public static final int SP_STATUS_IO_FULL = 0x010; // Bit 4 = io full 
	public static final int SP_STATUS_SSTEP = 0x020; // Bit 5 = single step 
	public static final int SP_STATUS_INTR_BREAK = 0x040; // Bit 6 = interrupt on break 
	public static final int SP_STATUS_SIG0 = 0x080; // Bit 7 = signal 0 set 
	public static final int SP_STATUS_SIG1 = 0x100; // Bit 8 = signal 1 set 
	public static final int SP_STATUS_SIG2 = 0x200; // Bit 9 = signal 2 set 
	public static final int SP_STATUS_SIG3 = 0x400; // Bit 10 = signal 3 set 
	public static final int SP_STATUS_SIG4 = 0x800; // Bit 11 = signal 4 set 
	public static final int SP_STATUS_SIG5 = 0x1000; // Bit 12 = signal 5 set 
	public static final int SP_STATUS_SIG6 = 0x2000; // Bit 13 = signal 6 set 
	public static final int SP_STATUS_SIG7 = 0x4000; // Bit 14 = signal 7 set 

	public static final boolean DLIST = true;
	public static final boolean ALIST = true;
	public static final int R4300i_SP_INTR = 0x1;

	public int[] regSP = new int[10];

	public long millisInDlist;
	public long millisInAlist;

	private final Runnable checkInterrupts;
	private final ByteBuffer dMem;
	private final int[] regMI;
	private final int[] regDPC;
	public GfxPlugin gfx;
	private AudioPlugin audio;

	//* Creates a new instance of Rsp 
	public RegisterSP(Runnable checkInterrupts, Registers regs, ByteBuffer dMem) {
		this.checkInterrupts = checkInterrupts;
		this.dMem = dMem;
		this.regMI = regs.regMI;
		this.regDPC = regs.regDPC;
		regSP[SP_STATUS_REG] = 0x00000001;
	}

	public void setupPlugin(GfxPlugin gfx, AudioPlugin audio) {
		this.gfx = gfx;
		this.audio = audio;
	}

	public void runRsp() {
		if ((regSP[SP_STATUS_REG] & SP_STATUS_HALT) == 0) {
			if ((regSP[SP_STATUS_REG] & SP_STATUS_BROKE) == 0) {
				int taskType = dMem.getInt(0xFC0);
				if (DLIST) {
					if (taskType == 1) {
						long time = System.currentTimeMillis();
						if (gfx != null)
							gfx.processDList();
						regSP[SP_STATUS_REG] |= 0x0203;
						if ((regSP[SP_STATUS_REG] & SP_STATUS_INTR_BREAK) != 0) {
							regMI[Registers.MI_INTR_REG] |= R4300i_SP_INTR;
							checkInterrupts.run();
						}
						regDPC[Registers.DPC_STATUS_REG] &= ~2;
						millisInDlist += (System.currentTimeMillis() - time);
						return;
					}
				}
				if (ALIST) {
					if (taskType == 2) {
						long time = System.currentTimeMillis();
						if (audio != null)
							audio.processAList();
						regSP[SP_STATUS_REG] |= 0x0203;
						if ((regSP[SP_STATUS_REG] & SP_STATUS_INTR_BREAK) != 0) {
							regMI[Registers.MI_INTR_REG] |= R4300i_SP_INTR;
							checkInterrupts.run();
						}
						millisInAlist += (System.currentTimeMillis() - time);
						return;
					}
				}
				regSP[SP_STATUS_REG] |= 0x0203;
				if ((regSP[SP_STATUS_REG] & SP_STATUS_INTR_BREAK) != 0) {
					regMI[Registers.MI_INTR_REG] |= R4300i_SP_INTR;
					checkInterrupts.run();
				}
			}
		}
	}

}
