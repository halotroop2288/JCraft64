package me.hydos.J64.hardware;

public class Cop0 {

	private static final boolean DEBUG_EXCEPTIONS = false;
	private static final boolean DEBUG_TLB = false;

	// Timers //////////////////////////////////////////////////////////////////

	public static final int COMPARE_TIMER = 0;
	public static final int SI_TIMER = 1;
	public static final int PI_TIMER = 2;
	public static final int VI_TIMER = 3;

	private static final int MAX_TIMERS = 4;

	// used by Cpu(TimerDone)
	public int[] nextTimer = new int[MAX_TIMERS];
	public int currentTimerType;

	// used by Video, Cpu
	public int timer;
	public int countPerOp;
	public int tick;

	private Runnable compareTimerInterrupt;
	private Runnable siTimerInterrupt;
	private Runnable piTimerInterrupt;
	private Runnable viTimerInterrupt;
	private boolean[] active = new boolean[MAX_TIMERS];
	private int wired = 32;

	private int[] tlbReadMap;
	private int[] tlbWriteMap;

	// Registers

	// CP0
	public static final int INDEX_REGISTER = 0;
	public static final int RANDOM_REGISTER = 1;
	public static final int ENTRYLO0_REGISTER = 2;
	public static final int ENTRYLO1_REGISTER = 3;
	public static final int CONTEXT_REGISTER = 4;
	public static final int PAGE_MASK_REGISTER = 5;
	public static final int WIRED_REGISTER = 6;
	public static final int BAD_VADDR_REGISTER = 8;
	public static final int COUNT_REGISTER = 9;
	public static final int ENTRYHI_REGISTER = 10;
	public static final int COMPARE_REGISTER = 11;
	public static final int STATUS_REGISTER = 12;
	public static final int CAUSE_REGISTER = 13;
	public static final int EPC_REGISTER = 14;
	public static final int CONFIG_REGISTER = 16;
	public static final int TAGLO_REGISTER = 28;
	public static final int TAGHI_REGISTER = 29;
	public static final int ERROREPC_REGISTER = 30;
	public static final int FAKE_CAUSE_REGISTER = 32;

	public static final int CAUSE_EXC_CODE = 0xFF;
	public static final int CAUSE_IP0 = 0x100;
	public static final int CAUSE_IP1 = 0x200;
	public static final int CAUSE_IP2 = 0x400;
	public static final int CAUSE_IP3 = 0x800;
	public static final int CAUSE_IP4 = 0x1000;
	public static final int CAUSE_IP5 = 0x2000;
	public static final int CAUSE_IP6 = 0x4000;
	public static final int CAUSE_IP7 = 0x8000;
	public static final int CAUSE_BD = 0x80000000;

	public static final int STATUS_IE = 0x00000001;
	public static final int STATUS_EXL = 0x00000002;
	public static final int STATUS_ERL = 0x00000004;
	public static final int STATUS_IP0 = 0x00000100;
	public static final int STATUS_IP1 = 0x00000200;
	public static final int STATUS_IP2 = 0x00000400;
	public static final int STATUS_IP3 = 0x00000800;
	public static final int STATUS_IP4 = 0x00001000;
	public static final int STATUS_IP5 = 0x00002000;
	public static final int STATUS_IP6 = 0x00004000;
	public static final int STATUS_IP7 = 0x00008000;
	public static final int STATUS_BEV = 0x00400000;
	public static final int STATUS_FR = 0x04000000;
	public static final int STATUS_CU0 = 0x10000000;
	public static final int STATUS_CU1 = 0x20000000;

	// used by Cpu, DebugOps
	public int[] CP0 = new int[33];

	// Exceptions //////////////////////////////////////////////////////////////

	private static final int EXC_INT = 0 << 2; /* interrupt */
	private static final int EXC_MOD = 1 << 2; /* Tlb mod */
	private static final int EXC_RMISS = 2 << 2; /* Read Tlb Miss */
	private static final int EXC_WMISS = 3 << 2; /* Write Tlb Miss */
	private static final int EXC_RADE = 4 << 2; /* Read Address Error */
	private static final int EXC_WADE = 5 << 2; /* Write Address Error */
	private static final int EXC_IBE = 6 << 2; /* Instruction Bus Error */
	private static final int EXC_DBE = 7 << 2; /* Data Bus Error */
	private static final int EXC_SYSCALL = 8 << 2; /* SYSCALL */
	private static final int EXC_BREAK = 9 << 2; /* BREAKpoint */
	private static final int EXC_II = 10 << 2; /* Illegal Instruction */
	private static final int EXC_CPU = 11 << 2; /* CoProcessor Unusable */
	private static final int EXC_OV = 12 << 2; /* OVerflow */
	private static final int EXC_TRAP = 13 << 2; /* Trap exception */
	private static final int EXC_VCEI = 14 << 2; /* Virt. Coherency on Inst. fetch */
	private static final int EXC_FPE = 15 << 2; /* Floating Point Exception */
	private static final int EXC_WATCH = 23 << 2; /* Watchpoint reference */
	private static final int EXC_VCED = 31 << 2; /* Virt. Coherency on data read */

	// Tlb /////////////////////////////////////////////////////////////////////

	public static class TlbException extends Exception {
		public TlbException(String message) {
			super(message);
		}
	}

	private class Tlb {
		boolean entryDefined = false;
		int pageMaskZero;
		int pageMaskMask;
		int pageMaskZero2;

		public void setPageMask(int value) {
			pageMaskZero2 = (value >> 25) & 0x7F;
			pageMaskMask = (value >> 13) & 0xFFF;
			pageMaskZero = (value) & 0x1FFF;
		}

		public int getPageMask() {
			return (pageMaskZero2 << 25) | (pageMaskMask << 13) | (pageMaskZero);
		}

		int entryHiASID;
		int entryHiZero;
		int entryHiG;
		int entryHiVPN2;

		public void setEntryHi(int value) {
			entryHiVPN2 = (value >> 13) & 0x7FFFF;
			entryHiG = (value >> 12) & 0x1;
			entryHiZero = (value >> 8) & 0xF;
			entryHiASID = (value) & 0xFF;
		}

		public int getEntryHi() {
			return (entryHiVPN2 << 13) | (entryHiG << 12) | (entryHiZero << 8) | (entryHiASID);
		}

		boolean entryLo0GLOBAL;
		boolean entryLo0V;
		boolean entryLo0D;
		int entryLo0C;
		int entryLo0PFN;
		int entryLo0ZERO;

		public void setEntryLo0(int value) {
			entryLo0ZERO = (value >> 26) & 0x3F;
			entryLo0PFN = (value >> 6) & 0xFFFFF;
			entryLo0C = (value >> 3) & 0x7;
			entryLo0D = ((value >> 2) & 0x1) == 1 ? true : false;
			entryLo0V = ((value >> 1) & 0x1) == 1 ? true : false;
			entryLo0GLOBAL = ((value) & 0x1) == 1 ? true : false;
		}

		public int getEntryLo0() {
			return (entryLo0ZERO << 26) | (entryLo0PFN << 6) | (entryLo0C << 3) | ((entryLo0D ? 1 : 0) << 2)
					| ((entryLo0V ? 1 : 0) << 1) | (entryLo0GLOBAL ? 1 : 0);
		}

		boolean entryLo1GLOBAL;
		boolean entryLo1V;
		boolean entryLo1D;
		int entryLo1C;
		int entryLo1PFN;
		int entryLo1ZERO;

		public void setEntryLo1(int value) {
			entryLo1ZERO = (value >> 26) & 0x3F;
			entryLo1PFN = (value >> 6) & 0xFFFFF;
			entryLo1C = (value >> 3) & 0x7;
			entryLo1D = ((value >> 2) & 0x1) == 1 ? true : false;
			entryLo1V = ((value >> 1) & 0x1) == 1 ? true : false;
			entryLo1GLOBAL = ((value) & 0x1) == 1 ? true : false;
		}

		public int getEntryLo1() {
			return (entryLo1ZERO << 26) | (entryLo1PFN << 6) | (entryLo1C << 3) | ((entryLo1D ? 1 : 0) << 2)
					| ((entryLo1V ? 1 : 0) << 1) | (entryLo1GLOBAL ? 1 : 0);
		}
	};

	private class FastTlb {
		long vStart;
		long vEnd;
		long physStart;
		boolean valid;
		boolean dirty;
		boolean global;
		boolean validEntry = false;
	};

	private FastTlb[] fastTlb = new FastTlb[64];
	private Tlb[] tlb = new Tlb[32];
	private boolean useTlb;

	/**
	 * Creates a new instance of Cop0
	 */
	public Cop0(int countPerOp) throws TlbException {
		this.countPerOp = countPerOp;

		try {
			tlbReadMap = new int[0xFFFFF]; // 1048575, 4,194,300b (4MB)
			tlbWriteMap = new int[0xFFFFF]; // 1048575, 4,194,300b (4MB)
		} catch (OutOfMemoryError ex) {
			throw new TlbException("Not enough memory for TLB!");
		}

		for (int count = 0; count < 32; count++)
			tlb[count] = new Tlb();
		for (int count = 0; count < 64; count++)
			fastTlb[count] = new FastTlb();
		setupTlb();

		CP0[RANDOM_REGISTER] = 0x1F;
		CP0[COUNT_REGISTER] = 0x5000;
		CP0[CAUSE_REGISTER] = 0x0000005C;
		CP0[CONTEXT_REGISTER] = 0x007FFFF0;
		CP0[EPC_REGISTER] = 0xFFFFFFFF;
		CP0[BAD_VADDR_REGISTER] = 0xFFFFFFFF;
		CP0[ERROREPC_REGISTER] = 0xFFFFFFFF;
		CP0[CONFIG_REGISTER] = 0x0006E463;
		CP0[STATUS_REGISTER] = 0x34000000;
	}

	public void useTlb(boolean use) {
		useTlb = use;
	}

	public void update() {
		timer -= tick * countPerOp;
		CP0[COUNT_REGISTER] += tick * countPerOp;
		CP0[RANDOM_REGISTER] -= (tick > wired) ? tick % wired : tick;
		if (CP0[RANDOM_REGISTER] < CP0[WIRED_REGISTER])
			CP0[RANDOM_REGISTER] += wired;
		tick = 0;
	}

	public int mfC0(int rd) {
		if (rd == RANDOM_REGISTER || rd == COUNT_REGISTER)
			update();
		return CP0[rd];
	}

	public void mtC0(int rd, int value) {
		switch (rd) {
		case 0: // Index
		case 2: // EntryLo0
		case 3: // EntryLo1
		case 5: // PageMask
		case 10: // Entry Hi
		case 14: // EPC
		case 16: // Config
		case 18: // WatchLo
		case 19: // WatchHi
		case 28: // Tag lo
		case 29: // Tag Hi
			CP0[rd] = value;
			break;
		case 4: // Context
			CP0[CONTEXT_REGISTER] = value & 0xFF800000;
			break;
		case 6: // Wired
			CP0[WIRED_REGISTER] = value;
			wired = 32 - value;
			CP0[RANDOM_REGISTER] = 31;
			break;
		case 9: // Count
			CP0[COUNT_REGISTER] = value;
			changeCompareTimer();
			break;
		case 11: // Compare
			CP0[COMPARE_REGISTER] = value;
			CP0[FAKE_CAUSE_REGISTER] &= ~CAUSE_IP7;
			update();
			changeCompareTimer();
			break;
		case 12: // Status
			CP0[STATUS_REGISTER] = value;
			if ((CP0[STATUS_REGISTER] & 0x18) != 0)
				System.err.printf("Left kernel mode ??\n");
			break;
		case 13: // cause
			CP0[CAUSE_REGISTER] &= 0xFFFFCFF;
			if ((value & 0x300) != 0)
				System.err.printf("Set IP0 or IP1\n");
			break;
		default:
			System.err.printf("COP0_MT: Unknown RD: %d\n", rd);
		}
	}

	public void setTimerInterrupts(Runnable compare, Runnable si, Runnable pi, Runnable vi) {
		this.compareTimerInterrupt = compare;
		this.siTimerInterrupt = si;
		this.piTimerInterrupt = pi;
		this.viTimerInterrupt = vi;
	}

	// Timers //////////////////////////////////////////////////////////////////

	// called by Cpu(init)
	public void initTimers() {
		currentTimerType = -1;
		timer = 0;
		for (int count = 0; count < MAX_TIMERS; count++)
			active[count] = false;
		changeTimer(VI_TIMER, 5000);
		changeCompareTimer();
	}

	public void timerDone() {
		update();
		if (timer >= 0)
			return;
		switch (currentTimerType) {
		case COMPARE_TIMER:
			CP0[FAKE_CAUSE_REGISTER] |= CAUSE_IP7;
			compareTimerInterrupt.run();
			changeCompareTimer();
			break;
		case SI_TIMER:
			siTimerInterrupt.run();
			changeTimer(SI_TIMER, 0);
			break;
		case PI_TIMER:
			piTimerInterrupt.run();
			changeTimer(PI_TIMER, 0);
			break;
		case VI_TIMER:
			viTimerInterrupt.run();
			break;
		}
		checkTimer();
	}

	private void checkTimer() {
		update();
		for (int count = 0; count < MAX_TIMERS; count++) {
			if (!active[count])
				continue;
			if (!(count == COMPARE_TIMER && nextTimer[count] == 0x7FFFFFFF))
				nextTimer[count] += timer;
		}
		currentTimerType = -1;
		timer = 0x7FFFFFFF;
		for (int count = 0; count < MAX_TIMERS; count++) {
			if (!active[count])
				continue;
			if (nextTimer[count] >= timer)
				continue;
			timer = nextTimer[count];
			currentTimerType = count;
		}
		if (currentTimerType == -1) {
			System.err.printf("No active timers ???\nEmulation Stoped\n");
			System.exit(0);
		}
		for (int count = 0; count < MAX_TIMERS; count++) {
			if (!active[count])
				continue;
			if (!(count == COMPARE_TIMER && nextTimer[count] == 0x7FFFFFFF))
				nextTimer[count] -= timer;
		}

		if (nextTimer[COMPARE_TIMER] == 0x7FFFFFFF) {
			int nextCompare = CP0[COMPARE_REGISTER] - CP0[COUNT_REGISTER];
			if ((nextCompare & 0x80000000) == 0 && nextCompare != 0x7FFFFFFF)
				changeCompareTimer();
		}
	}

	private void changeCompareTimer() {
		int nextCompare = CP0[COMPARE_REGISTER] - CP0[COUNT_REGISTER];
		if ((nextCompare & 0x80000000) != 0)
			nextCompare = 0x7FFFFFFF;
		if (nextCompare == 0)
			nextCompare = 0x1;
		changeTimer(COMPARE_TIMER, nextCompare);
	}

	// called by Video(timerInterrupt)
	public void changeTimer(int type, int value) {
		if (value == 0) {
			nextTimer[type] = 0;
			active[type] = false;
			return;
		}
		update();
		nextTimer[type] = value - timer;
		active[type] = true;
		checkTimer();
	}

	// Exceptions //////////////////////////////////////////////////////////////

	public boolean inPermLoop() {
		update();
		timer -= 5;
		CP0[COUNT_REGISTER] += 5;
		/* Interrupts enabled */
		if ((CP0[STATUS_REGISTER] & STATUS_IE) == 0)
			return true;
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			return true;
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			return true;
		if ((CP0[STATUS_REGISTER] & 0xFF00) == 0)
			return true;

		/* check sound playing */
		/* check RSP running */
		/* check RDP running */

		if (timer > 0) {
			CP0[COUNT_REGISTER] += timer + 1;
			timer = -1;
		}
		return false;
	}

	// called by Cpu
	public int doAddressError(boolean delaySlot, int badVaddr, boolean fromRead, int pc) {
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			System.err.printf("EXL set in AddressError Exception\n");
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			System.err.printf("ERL set in AddressError Exception\n");

		if (fromRead)
			CP0[CAUSE_REGISTER] = EXC_RADE;
		else
			CP0[CAUSE_REGISTER] = EXC_WADE;
		CP0[BAD_VADDR_REGISTER] = badVaddr;
		if (delaySlot) {
			CP0[CAUSE_REGISTER] |= CAUSE_BD;
			CP0[EPC_REGISTER] = pc - 4;
		} else {
			CP0[EPC_REGISTER] = pc;
		}
		CP0[STATUS_REGISTER] |= STATUS_EXL;
		if (DEBUG_EXCEPTIONS)
			System.out.printf("-:DoAddressError:%X:%X:%X:%X\n", CP0[STATUS_REGISTER], CP0[CAUSE_REGISTER],
					CP0[EPC_REGISTER], CP0[BAD_VADDR_REGISTER]);
		return delaySlot ? 0x80000180 - 4 : 0x80000180;
	}

	// called by Cpu
	public int doBreakException(boolean delaySlot, int pc) {
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			System.err.printf("EXL set in Break Exception\n");
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			System.err.printf("ERL set in Break Exception\n");

		CP0[CAUSE_REGISTER] = EXC_BREAK;
		if (delaySlot) {
			CP0[CAUSE_REGISTER] |= CAUSE_BD;
			CP0[EPC_REGISTER] = pc - 4;
		} else {
			CP0[EPC_REGISTER] = pc;
		}
		CP0[STATUS_REGISTER] |= STATUS_EXL;
		if (DEBUG_EXCEPTIONS)
			System.out.printf("-:DoBreakException:%X:%X:%X\n", CP0[STATUS_REGISTER], CP0[CAUSE_REGISTER],
					CP0[EPC_REGISTER]);
		return delaySlot ? 0x80000180 - 4 : 0x80000180;
	}

	// called by Cpu
	public int doCopUnusableException(boolean delaySlot, int coprocessor, int pc) {
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			System.err.printf("EXL set in Break Exception\n");
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			System.err.printf("ERL set in Break Exception\n");

		CP0[CAUSE_REGISTER] = EXC_CPU;
		if (coprocessor == 1)
			CP0[CAUSE_REGISTER] |= 0x10000000;
		if (delaySlot) {
			CP0[CAUSE_REGISTER] |= CAUSE_BD;
			CP0[EPC_REGISTER] = pc - 4;
		} else {
			CP0[EPC_REGISTER] = pc;
		}
		CP0[STATUS_REGISTER] |= STATUS_EXL;
		if (DEBUG_EXCEPTIONS)
			System.out.printf("-:DoCopUnusableException:%X:%X:%X\n", CP0[STATUS_REGISTER], CP0[CAUSE_REGISTER],
					CP0[EPC_REGISTER]);
		return delaySlot ? 0x80000180 - 4 : 0x80000180;
	}

	// called by Cpu
	public int doSysCallException(boolean delaySlot, int pc) {
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			System.err.printf("EXL set in SysCall Exception\n");
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			System.err.printf("ERL set in SysCall Exception\n");

		CP0[CAUSE_REGISTER] = EXC_SYSCALL;
		if (delaySlot) {
			CP0[CAUSE_REGISTER] |= CAUSE_BD;
			CP0[EPC_REGISTER] = pc - 4;
		} else {
			CP0[EPC_REGISTER] = pc;
		}
		CP0[STATUS_REGISTER] |= STATUS_EXL;
		if (DEBUG_EXCEPTIONS)
			System.out.printf("-:DoSysCallException:%X:%X:%X\n", CP0[STATUS_REGISTER], CP0[CAUSE_REGISTER],
					CP0[EPC_REGISTER]);
		return delaySlot ? 0x80000180 - 4 : 0x80000180;
	}

	// called by Cpu
	public int doTlbMiss(boolean delaySlot, int badVaddr, int pc) {
		CP0[CAUSE_REGISTER] = EXC_RMISS;
		CP0[BAD_VADDR_REGISTER] = badVaddr;
		CP0[CONTEXT_REGISTER] &= 0xFF80000F;
		CP0[CONTEXT_REGISTER] |= (badVaddr >>> 9) & 0x007FFFF0;
		CP0[ENTRYHI_REGISTER] = badVaddr & 0xFFFFE000;
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) == 0) {
			if (delaySlot) {
				CP0[CAUSE_REGISTER] |= CAUSE_BD;
				CP0[EPC_REGISTER] = pc - 4;
			} else {
				CP0[EPC_REGISTER] = pc;
			}
			CP0[STATUS_REGISTER] |= STATUS_EXL;
			if (addressDefined(badVaddr))
				return delaySlot ? 0x80000180 - 4 : 0x80000180;
			else
				return delaySlot ? 0x80000000 - 4 : 0x80000000;
		} else {
			System.err.printf("EXL Set\nAddress (%X) Defined: %s\n", badVaddr,
					addressDefined(badVaddr) ? "TRUE" : "FALSE");
			return delaySlot ? 0x80000180 - 4 : 0x80000180;
		}
	}

	// called by Cpu
	public int doIntrException(boolean delaySlot, int pc) {
		if ((CP0[STATUS_REGISTER] & STATUS_IE) == 0)
			return pc;
		if ((CP0[STATUS_REGISTER] & STATUS_EXL) != 0)
			return pc;
		if ((CP0[STATUS_REGISTER] & STATUS_ERL) != 0)
			return pc;
		CP0[CAUSE_REGISTER] = CP0[FAKE_CAUSE_REGISTER];
		CP0[CAUSE_REGISTER] |= EXC_INT;
		if (delaySlot) {
			CP0[CAUSE_REGISTER] |= CAUSE_BD;
			CP0[EPC_REGISTER] = pc - 4;
		} else {
			CP0[EPC_REGISTER] = pc;
		}
		CP0[STATUS_REGISTER] |= STATUS_EXL;
		if (DEBUG_EXCEPTIONS)
			System.out.printf("-:DoIntrException:%X:%X:%X\n", CP0[STATUS_REGISTER], CP0[CAUSE_REGISTER],
					CP0[EPC_REGISTER]);
		return delaySlot ? 0x80000180 - 4 : 0x80000180;
	}

	// Tlb /////////////////////////////////////////////////////////////////////

	// called by Cpu
	public int translateVaddr(int addr) throws TlbException {
		if (!useTlb)
			return addr & 0x1FFFFFFF;
		if (tlbReadMap[addr >>> 12] == 0)
			throw new TlbException("Tlb miss");
		return tlbReadMap[addr >>> 12] + addr;
	}

	private boolean addressDefined(int vAddr) {
		long addr = vAddr & 0xFFFFFFFFL;
		if (addr >= 0x80000000L && addr <= 0xBFFFFFFFL)
			return true;
		for (int i = 0; i < 64; i++) {
			if (!fastTlb[i].validEntry)
				continue;
			if (addr >= fastTlb[i].vStart && addr <= fastTlb[i].vEnd)
				return true;
		}
		return false;
	}

	private void tlbProbe() {
		if (DEBUG_TLB)
			System.out.printf("TLBP:%X\n", CP0[ENTRYHI_REGISTER]);
		CP0[INDEX_REGISTER] |= 0x80000000;
		for (int count = 0; count < 32; count++) {
			int tlbValue = tlb[count].getEntryHi() & (~tlb[count].pageMaskMask << 13);
			int entryHi = CP0[ENTRYHI_REGISTER] & (~tlb[count].pageMaskMask << 13);

			if (tlbValue == entryHi) {
				boolean global = (tlb[count].getEntryHi() & 0x100) != 0;
				boolean sameAsid = ((tlb[count].getEntryHi() & 0xFF) == (CP0[ENTRYHI_REGISTER] & 0xFF));

				if (global || sameAsid) {
					CP0[INDEX_REGISTER] = count;
					return;
				}
			}
		}

		if (DEBUG_TLB)
			System.out.printf("0=%X\n", CP0[INDEX_REGISTER]);
	}

	private void tlbRead() {
		if (DEBUG_TLB)
			System.out.printf("TLBR:%X\n", CP0[INDEX_REGISTER]);
		int index = CP0[INDEX_REGISTER] & 0x1F;

		CP0[PAGE_MASK_REGISTER] = tlb[index].getPageMask();
		CP0[ENTRYHI_REGISTER] = (tlb[index].getEntryHi() & ~tlb[index].getPageMask());
		CP0[ENTRYLO0_REGISTER] = tlb[index].getEntryLo0();
		CP0[ENTRYLO1_REGISTER] = tlb[index].getEntryLo1();

		if (DEBUG_TLB)
			System.out.printf("5=%X 10=%X 2=%X 3=%X\n", CP0[PAGE_MASK_REGISTER], CP0[ENTRYHI_REGISTER],
					CP0[ENTRYLO0_REGISTER], CP0[ENTRYLO1_REGISTER]);
	}

	private void writeTlbEntry(int index) {
		if (tlb[index].entryDefined) {
			for (int fastIndx = index << 1; fastIndx <= (index << 1) + 1; fastIndx++) {
				if (!fastTlb[fastIndx].validEntry)
					continue;
				if (!fastTlb[fastIndx].valid)
					continue;
				for (long vAddr = fastTlb[fastIndx].vStart; vAddr < fastTlb[fastIndx].vEnd; vAddr += 0x1000L) {
					tlbReadMap[(int) (vAddr >> 12)] = 0;
					tlbWriteMap[(int) (vAddr >> 12)] = 0;
				}
			}
		}
		tlb[index].setPageMask(CP0[PAGE_MASK_REGISTER]);
		tlb[index].setEntryHi(CP0[ENTRYHI_REGISTER]);
		tlb[index].setEntryLo0(CP0[ENTRYLO0_REGISTER]);
		tlb[index].setEntryLo1(CP0[ENTRYLO1_REGISTER]);
		tlb[index].entryDefined = true;

		setupTlbEntry(index);
	}

	private void setupTlb() {
		for (int i = 0; i < tlbReadMap.length; i++)
			tlbReadMap[i] = 0;
		for (int i = 0; i < tlbWriteMap.length; i++)
			tlbWriteMap[i] = 0;
		for (long vAddr = 0x80000000L; vAddr < 0xC0000000L; vAddr += 0x1000L) {
			tlbReadMap[(int) (vAddr >> 12)] = (int) ((vAddr & 0x1FFFFFFFL) - vAddr);
			tlbWriteMap[(int) (vAddr >> 12)] = (int) ((vAddr & 0x1FFFFFFFL) - vAddr);
		}
		for (int count = 0; count < 32; count++)
			setupTlbEntry(count);
	}

	private void setupTlbEntry(int entry) {
		if (!tlb[entry].entryDefined)
			return;

		int fastIndx = entry << 1;
		fastTlb[fastIndx].vStart = ((long) tlb[entry].entryHiVPN2) << 13;
		fastTlb[fastIndx].vEnd = fastTlb[fastIndx].vStart + (tlb[entry].pageMaskMask << 12) + 0xFFF;
		fastTlb[fastIndx].physStart = ((long) tlb[entry].entryLo0PFN) << 12;
		fastTlb[fastIndx].valid = tlb[entry].entryLo0V;
		fastTlb[fastIndx].dirty = tlb[entry].entryLo0D;
		fastTlb[fastIndx].global = tlb[entry].entryLo0GLOBAL & tlb[entry].entryLo1GLOBAL;
		fastTlb[fastIndx].validEntry = false;

		fastIndx = (entry << 1) + 1;
		fastTlb[fastIndx].vStart = (((long) tlb[entry].entryHiVPN2) << 13)
				+ ((((long) tlb[entry].pageMaskMask) << 12) + 0xFFF + 1);
		fastTlb[fastIndx].vEnd = fastTlb[fastIndx].vStart + (((long) tlb[entry].pageMaskMask) << 12) + 0xFFF;
		fastTlb[fastIndx].physStart = ((long) tlb[entry].entryLo1PFN) << 12;
		fastTlb[fastIndx].valid = tlb[entry].entryLo1V;
		fastTlb[fastIndx].dirty = tlb[entry].entryLo1D;
		fastTlb[fastIndx].global = tlb[entry].entryLo0GLOBAL & tlb[entry].entryLo1GLOBAL;
		fastTlb[fastIndx].validEntry = false;

		for (fastIndx = entry << 1; fastIndx <= (entry << 1) + 1; fastIndx++) {
			if (!fastTlb[fastIndx].valid) {
				fastTlb[fastIndx].validEntry = true;
				continue;
			}
			if (fastTlb[fastIndx].vEnd <= fastTlb[fastIndx].vStart) {
				System.err.printf("Vstart = Vend for tlb mapping\n");
				continue;
			}
			if (fastTlb[fastIndx].vStart >= 0x80000000L && fastTlb[fastIndx].vEnd <= 0xBFFFFFFFL) {
				continue;
			}
			if (fastTlb[fastIndx].physStart > 0x1FFFFFFFL) {
				continue;
			}

			// test if overlap
			fastTlb[fastIndx].validEntry = true;
			for (long vAddr = fastTlb[fastIndx].vStart; vAddr < fastTlb[fastIndx].vEnd; vAddr += 0x1000L) {
				tlbReadMap[(int) (vAddr >> 12)] = (int) ((vAddr - fastTlb[fastIndx].vStart
						+ fastTlb[fastIndx].physStart) - vAddr);
				if (!fastTlb[fastIndx].dirty)
					continue;
				tlbWriteMap[(int) (vAddr >> 12)] = (int) ((vAddr - fastTlb[fastIndx].vStart
						+ fastTlb[fastIndx].physStart) - vAddr);
			}
		}
	}

	/************************** COP0 CO functions ***********************/
	public Runnable r4300i_COP0_CO_TLBR = new Runnable() {
		public void run() {
			if (!useTlb)
				return;
			tlbRead();
		}
	};

	public Runnable r4300i_COP0_CO_TLBWI = new Runnable() {
		public void run() {
			if (!useTlb)
				return;
//            if (PROGRAM_COUNTER == 0x00136260 && CP0[INDEX_REGISTER] == 0x1F)
//                System.err.printf("TLBWI\n");
//            else
			writeTlbEntry(CP0[INDEX_REGISTER] & 0x1F);
		}
	};

	public Runnable r4300i_COP0_CO_TLBWR = new Runnable() {
		public void run() {
			if (!useTlb)
				return;
			update();
			writeTlbEntry(CP0[RANDOM_REGISTER] & 0x1F);
		}
	};

	public Runnable r4300i_COP0_CO_TLBP = new Runnable() {
		public void run() {
			if (!useTlb)
				return;
			tlbProbe();
		}
	};

//    public static void main(String[] args) {
//        long num = 0;
//        long N64MEM = 0x00500000L;
//        long count = 0x80000000L;
//        for (; count < 0xC0000000L; count += 0x1000L) {
//            num++;
//        }
//        System.out.println("Count: " + ((count-0x1000L)>>12));
//        System.out.println("Num: " + num);
//        count = 0x80000000L;
//
//        System.out.println("count: " + Long.toHexString(count));
//        System.out.println("count >> 12: " + Long.toHexString(count >> 12));
//        System.out.println("count & 0x1FFFFFFFL: " + Long.toHexString(count & 0x1FFFFFFFL));
//        System.out.println(Long.toHexString(((N64MEM + (count & 0x1FFFFFFFL)) - count)&0xFFFFFFFFL));
//        count += 0x1000L;
//
//        System.out.println("count: " + Long.toHexString(count));
//        System.out.println("count >> 12: " + Long.toHexString(count >> 12));
//        System.out.println("count & 0x1FFFFFFFL: " + Long.toHexString(count & 0x1FFFFFFFL));
//        // (0x00500000L + 0x00001000) - 0x80001000 = 0x00501000 - 0x80001000 = 0x80500000
//        System.out.println(Long.toHexString(((N64MEM + (count & 0x1FFFFFFFL)) - count)&0xFFFFFFFFL));
//        count += 0x1000L;
//
//        System.out.println("count: " + Long.toHexString(count));
//        System.out.println("count >> 12: " + Long.toHexString(count >> 12));
//        System.out.println("count & 0x1FFFFFFFL: " + Long.toHexString(count & 0x1FFFFFFFL));
//        System.out.println(Long.toHexString(((N64MEM + (count & 0x1FFFFFFFL)) - count)&0xFFFFFFFFL));
//        count += 0x1000L;
//
//        System.out.println("count: " + Long.toHexString(count));
//        System.out.println("count >> 12: " + Long.toHexString(count >> 12));
//        System.out.println("count & 0x1FFFFFFFL: " + Long.toHexString(count & 0x1FFFFFFFL));
//        System.out.println(Long.toHexString(((N64MEM + (count & 0x1FFFFFFFL)) - count)&0xFFFFFFFFL));
//        count += 0x1000L;
//
//        System.out.println(count>>12);
//        count += 0x1000L;
//
//        System.out.println(count>>12);
//        count += 0x1000L;
//    }

}
