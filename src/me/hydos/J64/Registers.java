package me.hydos.J64;

public class Registers {

	// regDPC DP Command Registers
	public static final int DPC_START_REG = 0;
	public static final int DPC_END_REG = 1;
	public static final int DPC_CURRENT_REG = 2;
	public static final int DPC_STATUS_REG = 3;
	public static final int DPC_CLOCK_REG = 4;
	public static final int DPC_BUFBUSY_REG = 5;
	public static final int DPC_PIPEBUSY_REG = 6;
	public static final int DPC_TMEM_REG = 7;

	// regMI MIPS Interface (MI) Registers
	public static final int MI_INIT_MODE_REG = 0;
	public static final int MI_MODE_REG = 0;
	public static final int MI_VERSION_REG = 1;
	public static final int MI_NOOP_REG = 1;
	public static final int MI_INTR_REG = 2;
	public static final int MI_INTR_MASK_REG = 3;

	// Values
	public static final int DPC_CLR_XBUS_DMEM_DMA = 0x0001; /* Bit 0: clear xbus_dmem_dma */
	public static final int DPC_SET_XBUS_DMEM_DMA = 0x0002; /* Bit 1: set xbus_dmem_dma */
	public static final int DPC_CLR_FREEZE = 0x0004; /* Bit 2: clear freeze */
	public static final int DPC_SET_FREEZE = 0x0008; /* Bit 3: set freeze */
	public static final int DPC_CLR_FLUSH = 0x0010; /* Bit 4: clear flush */
	public static final int DPC_SET_FLUSH = 0x0020; /* Bit 5: set flush */
	public static final int DPC_CLR_TMEM_CTR = 0x0040; /* Bit 6: clear tmem ctr */
	public static final int DPC_CLR_PIPE_CTR = 0x0080; /* Bit 7: clear pipe ctr */
	public static final int DPC_CLR_CMD_CTR = 0x0100; /* Bit 8: clear cmd ctr */
	public static final int DPC_CLR_CLOCK_CTR = 0x0200; /* Bit 9: clear clock ctr */

	public static final int DPC_STATUS_XBUS_DMEM_DMA = 0x001; /* Bit 0: xbus_dmem_dma */
	public static final int DPC_STATUS_FREEZE = 0x002; /* Bit 1: freeze */
	public static final int DPC_STATUS_FLUSH = 0x004; /* Bit 2: flush */
	public static final int DPC_STATUS_START_GCLK = 0x008; /* Bit 3: start gclk */
	public static final int DPC_STATUS_TMEM_BUSY = 0x010; /* Bit 4: tmem busy */
	public static final int DPC_STATUS_PIPE_BUSY = 0x020; /* Bit 5: pipe busy */
	public static final int DPC_STATUS_CMD_BUSY = 0x040; /* Bit 6: cmd busy */
	public static final int DPC_STATUS_CBUF_READY = 0x080; /* Bit 7: cbuf ready */
	public static final int DPC_STATUS_DMA_BUSY = 0x100; /* Bit 8: dma busy */
	public static final int DPC_STATUS_END_VALID = 0x200; /* Bit 9: end valid */
	public static final int DPC_STATUS_START_VALID = 0x400; /* Bit 10: start valid */

	public static final int MI_CLR_INIT = 0x0080; /* Bit 7: clear init mode */
	public static final int MI_SET_INIT = 0x0100; /* Bit 8: set init mode */
	public static final int MI_CLR_EBUS = 0x0200; /* Bit 9: clear ebus test */
	public static final int MI_SET_EBUS = 0x0400; /* Bit 10: set ebus test mode */
	public static final int MI_CLR_DP_INTR = 0x0800; /* Bit 11: clear dp interrupt */
	public static final int MI_CLR_RDRAM = 0x1000; /* Bit 12: clear RDRAM reg */
	public static final int MI_SET_RDRAM = 0x2000; /* Bit 13: set RDRAM reg mode */

	public static final int MI_MODE_INIT = 0x0080; /* Bit 7: init mode */
	public static final int MI_MODE_EBUS = 0x0100; /* Bit 8: ebus test mode */
	public static final int MI_MODE_RDRAM = 0x0200; /* Bit 9: RDRAM reg mode */

	public static final int MI_INTR_MASK_CLR_SP = 0x0001; /* Bit 0: clear SP mask */
	public static final int MI_INTR_MASK_SET_SP = 0x0002; /* Bit 1: set SP mask */
	public static final int MI_INTR_MASK_CLR_SI = 0x0004; /* Bit 2: clear SI mask */
	public static final int MI_INTR_MASK_SET_SI = 0x0008; /* Bit 3: set SI mask */
	public static final int MI_INTR_MASK_CLR_AI = 0x0010; /* Bit 4: clear AI mask */
	public static final int MI_INTR_MASK_SET_AI = 0x0020; /* Bit 5: set AI mask */
	public static final int MI_INTR_MASK_CLR_VI = 0x0040; /* Bit 6: clear VI mask */
	public static final int MI_INTR_MASK_SET_VI = 0x0080; /* Bit 7: set VI mask */
	public static final int MI_INTR_MASK_CLR_PI = 0x0100; /* Bit 8: clear PI mask */
	public static final int MI_INTR_MASK_SET_PI = 0x0200; /* Bit 9: set PI mask */
	public static final int MI_INTR_MASK_CLR_DP = 0x0400; /* Bit 10: clear DP mask */
	public static final int MI_INTR_MASK_SET_DP = 0x0800; /* Bit 11: set DP mask */

	public static final int MI_INTR_MASK_SP = 0x01; /* Bit 0: SP intr mask */
	public static final int MI_INTR_MASK_SI = 0x02; /* Bit 1: SI intr mask */
	public static final int MI_INTR_MASK_AI = 0x04; /* Bit 2: AI intr mask */
	public static final int MI_INTR_MASK_VI = 0x08; /* Bit 3: VI intr mask */
	public static final int MI_INTR_MASK_PI = 0x10; /* Bit 4: PI intr mask */
	public static final int MI_INTR_MASK_DP = 0x20; /* Bit 5: DP intr mask */

	public static final int MI_INTR_SP = 0x01; /* Bit 0: SP intr */
	public static final int MI_INTR_SI = 0x02; /* Bit 1: SI intr */
	public static final int MI_INTR_AI = 0x04; /* Bit 2: AI intr */
	public static final int MI_INTR_VI = 0x08; /* Bit 3: VI intr */
	public static final int MI_INTR_PI = 0x10; /* Bit 4: PI intr */
	public static final int MI_INTR_DP = 0x20; /* Bit 5: DP intr */

	// Save Type
	public static final int AUTO = 0;
	public static final int EEPROM_4K = 1;
	public static final int EEPROM_16K = 2;
	public static final int SRAM = 3;
	public static final int FLASHRAM = 4;

	// used by Memory, Dma, Pif
	public int saveUsing = AUTO;

	// DP Command Registers
	// used by Memory, Plugin(Gfx, Rsp)
	public int[] regDPC = new int[10];

	// MIPS Interface (MI) Registers
	// used by Memory, Dma, Pif, Plugin(Gfx, Rsp), Cpu(CheckInterrupts, TimerDone)
	public int[] regMI = new int[4];

	/** Creates a new instance of Registers */
	public Registers() {
		regMI[MI_VERSION_REG] = 0x02020102;
	}

}
