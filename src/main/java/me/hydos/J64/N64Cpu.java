package me.hydos.J64;

import me.hydos.J64.hardware.Cop0;
import me.hydos.J64.hardware.Cop0.TlbException;
import me.hydos.J64.hardware.Cop1;
import me.hydos.J64.hardware.Memory;
import me.hydos.J64.hardware.Memory.MemoryException;
import me.hydos.J64.util.EmuManager;

public class N64Cpu {

	public static boolean dump_inst;

	//OpCodes
	public static final int R4300i_SPECIAL = 0;
	public static final int R4300i_REGIMM = 1;
	public static final int R4300i_J = 2;
	public static final int R4300i_JAL = 3;
	public static final int R4300i_BEQ = 4;
	public static final int R4300i_BNE = 5;
	public static final int R4300i_BLEZ = 6;
	public static final int R4300i_BGTZ = 7;
	public static final int R4300i_ADDI = 8;
	public static final int R4300i_ADDIU = 9;
	public static final int R4300i_SLTI = 10;
	public static final int R4300i_SLTIU = 11;
	public static final int R4300i_ANDI = 12;
	public static final int R4300i_ORI = 13;
	public static final int R4300i_XORI = 14;
	public static final int R4300i_LUI = 15;
	public static final int R4300i_CP0 = 16;
	public static final int R4300i_CP1 = 17;
	public static final int R4300i_BEQL = 20;
	public static final int R4300i_BNEL = 21;
	public static final int R4300i_BLEZL = 22;
	public static final int R4300i_BGTZL = 23;
	public static final int R4300i_DADDI = 24;
	public static final int R4300i_DADDIU = 25;
	public static final int R4300i_LDL = 26;
	public static final int R4300i_LDR = 27;
	public static final int R4300i_LB = 32;
	public static final int R4300i_LH = 33;
	public static final int R4300i_LWL = 34;
	public static final int R4300i_LW = 35;
	public static final int R4300i_LBU = 36;
	public static final int R4300i_LHU = 37;
	public static final int R4300i_LWR = 38;
	public static final int R4300i_LWU = 39;
	public static final int R4300i_SB = 40;
	public static final int R4300i_SH = 41;
	public static final int R4300i_SWL = 42;
	public static final int R4300i_SW = 43;
	public static final int R4300i_SDL = 44;
	public static final int R4300i_SDR = 45;
	public static final int R4300i_SWR = 46;
	public static final int R4300i_CACHE = 47;
	public static final int R4300i_LL = 48;
	public static final int R4300i_LWC1 = 49;
	public static final int R4300i_LWC2 = 0x32;
	public static final int R4300i_LLD = 0x34;
	public static final int R4300i_LDC1 = 53;
	public static final int R4300i_LDC2 = 0x36;
	public static final int R4300i_LD = 55;
	public static final int R4300i_SC = 0x38;
	public static final int R4300i_SWC1 = 57;
	public static final int R4300i_SWC2 = 0x3A;
	public static final int R4300i_SCD = 0x3C;
	public static final int R4300i_SDC1 = 61;
	public static final int R4300i_SDC2 = 62;
	public static final int R4300i_SD = 63;

	/* Special opcodes */
	public static final int R4300i_SPECIAL_SLL = 0;
	public static final int R4300i_SPECIAL_SRL = 2;
	public static final int R4300i_SPECIAL_SRA = 3;
	public static final int R4300i_SPECIAL_SLLV = 4;
	public static final int R4300i_SPECIAL_SRLV = 6;
	public static final int R4300i_SPECIAL_SRAV = 7;
	public static final int R4300i_SPECIAL_JR = 8;
	public static final int R4300i_SPECIAL_JALR = 9;
	public static final int R4300i_SPECIAL_SYSCALL = 12;
	public static final int R4300i_SPECIAL_BREAK = 13;
	public static final int R4300i_SPECIAL_SYNC = 15;
	public static final int R4300i_SPECIAL_MFHI = 16;
	public static final int R4300i_SPECIAL_MTHI = 17;
	public static final int R4300i_SPECIAL_MFLO = 18;
	public static final int R4300i_SPECIAL_MTLO = 19;
	public static final int R4300i_SPECIAL_DSLLV = 20;
	public static final int R4300i_SPECIAL_DSRLV = 22;
	public static final int R4300i_SPECIAL_DSRAV = 23;
	public static final int R4300i_SPECIAL_MULT = 24;
	public static final int R4300i_SPECIAL_MULTU = 25;
	public static final int R4300i_SPECIAL_DIV = 26;
	public static final int R4300i_SPECIAL_DIVU = 27;
	public static final int R4300i_SPECIAL_DMULT = 28;
	public static final int R4300i_SPECIAL_DMULTU = 29;
	public static final int R4300i_SPECIAL_DDIV = 30;
	public static final int R4300i_SPECIAL_DDIVU = 31;
	public static final int R4300i_SPECIAL_ADD = 32;
	public static final int R4300i_SPECIAL_ADDU = 33;
	public static final int R4300i_SPECIAL_SUB = 34;
	public static final int R4300i_SPECIAL_SUBU = 35;
	public static final int R4300i_SPECIAL_AND = 36;
	public static final int R4300i_SPECIAL_OR = 37;
	public static final int R4300i_SPECIAL_XOR = 38;
	public static final int R4300i_SPECIAL_NOR = 39;
	public static final int R4300i_SPECIAL_SLT = 42;
	public static final int R4300i_SPECIAL_SLTU = 43;
	public static final int R4300i_SPECIAL_DADD = 44;
	public static final int R4300i_SPECIAL_DADDU = 45;
	public static final int R4300i_SPECIAL_DSUB = 46;
	public static final int R4300i_SPECIAL_DSUBU = 47;
	public static final int R4300i_SPECIAL_TGE = 48;
	public static final int R4300i_SPECIAL_TGEU = 49;
	public static final int R4300i_SPECIAL_TLT = 50;
	public static final int R4300i_SPECIAL_TLTU = 51;
	public static final int R4300i_SPECIAL_TEQ = 52;
	public static final int R4300i_SPECIAL_TNE = 54;
	public static final int R4300i_SPECIAL_DSLL = 56;
	public static final int R4300i_SPECIAL_DSRL = 58;
	public static final int R4300i_SPECIAL_DSRA = 59;
	public static final int R4300i_SPECIAL_DSLL32 = 60;
	public static final int R4300i_SPECIAL_DSRL32 = 62;
	public static final int R4300i_SPECIAL_DSRA32 = 63;

	/* R4300i RegImm opcodes */
	public static final int R4300i_REGIMM_BLTZ = 0;
	public static final int R4300i_REGIMM_BGEZ = 1;
	public static final int R4300i_REGIMM_BLTZL = 2;
	public static final int R4300i_REGIMM_BGEZL = 3;
	public static final int R4300i_REGIMM_TGEI = 0x08;
	public static final int R4300i_REGIMM_TGEIU = 0x09;
	public static final int R4300i_REGIMM_TLTI = 0x0A;
	public static final int R4300i_REGIMM_TLTIU = 0x0B;
	public static final int R4300i_REGIMM_TEQI = 0x0C;
	public static final int R4300i_REGIMM_TNEI = 0x0E;
	public static final int R4300i_REGIMM_BLTZAL = 0x10;
	public static final int R4300i_REGIMM_BGEZAL = 17;
	public static final int R4300i_REGIMM_BLTZALL = 0x12;
	public static final int R4300i_REGIMM_BGEZALL = 0x13;

	/* R4300i COP0 opcodes */
	public static final int R4300i_COP0_MF = 0;
	public static final int R4300i_COP0_MT = 4;

	/* R4300i COP0 CO opcodes */
	public static final int R4300i_COP0_CO_TLBR = 1;
	public static final int R4300i_COP0_CO_TLBWI = 2;
	public static final int R4300i_COP0_CO_TLBWR = 6;
	public static final int R4300i_COP0_CO_TLBP = 8;
	public static final int R4300i_COP0_CO_ERET = 24;

	/* R4300i COP1 opcodes */
	public static final int R4300i_COP1_MF = 0;
	public static final int R4300i_COP1_DMF = 1;
	public static final int R4300i_COP1_CF = 2;
	public static final int R4300i_COP1_MT = 4;
	public static final int R4300i_COP1_DMT = 5;
	public static final int R4300i_COP1_CT = 6;
	public static final int R4300i_COP1_BC = 8;
	public static final int R4300i_COP1_S = 16;
	public static final int R4300i_COP1_D = 17;
	public static final int R4300i_COP1_W = 20;
	public static final int R4300i_COP1_L = 21;

	/* R4300i COP1 BC opcodes */
	public static final int R4300i_COP1_BC_BCF = 0;
	public static final int R4300i_COP1_BC_BCT = 1;
	public static final int R4300i_COP1_BC_BCFL = 2;
	public static final int R4300i_COP1_BC_BCTL = 3;

	public static final int R4300i_COP1_FUNCT_ADD = 0;
	public static final int R4300i_COP1_FUNCT_SUB = 1;
	public static final int R4300i_COP1_FUNCT_MUL = 2;
	public static final int R4300i_COP1_FUNCT_DIV = 3;
	public static final int R4300i_COP1_FUNCT_SQRT = 4;
	public static final int R4300i_COP1_FUNCT_ABS = 5;
	public static final int R4300i_COP1_FUNCT_MOV = 6;
	public static final int R4300i_COP1_FUNCT_NEG = 7;
	public static final int R4300i_COP1_FUNCT_ROUND_L = 8;
	public static final int R4300i_COP1_FUNCT_TRUNC_L = 9;
	public static final int R4300i_COP1_FUNCT_CEIL_L = 10;
	public static final int R4300i_COP1_FUNCT_FLOOR_L = 11;
	public static final int R4300i_COP1_FUNCT_ROUND_W = 12;
	public static final int R4300i_COP1_FUNCT_TRUNC_W = 13;
	public static final int R4300i_COP1_FUNCT_CEIL_W = 14;
	public static final int R4300i_COP1_FUNCT_FLOOR_W = 15;
	public static final int R4300i_COP1_FUNCT_CVT_S = 32;
	public static final int R4300i_COP1_FUNCT_CVT_D = 33;
	public static final int R4300i_COP1_FUNCT_CVT_W = 36;
	public static final int R4300i_COP1_FUNCT_CVT_L = 37;
	public static final int R4300i_COP1_FUNCT_C_F = 48;
	public static final int R4300i_COP1_FUNCT_C_UN = 49;
	public static final int R4300i_COP1_FUNCT_C_EQ = 50;
	public static final int R4300i_COP1_FUNCT_C_UEQ = 51;
	public static final int R4300i_COP1_FUNCT_C_OLT = 52;
	public static final int R4300i_COP1_FUNCT_C_ULT = 53;
	public static final int R4300i_COP1_FUNCT_C_OLE = 54;
	public static final int R4300i_COP1_FUNCT_C_ULE = 55;
	public static final int R4300i_COP1_FUNCT_C_SF = 56;
	public static final int R4300i_COP1_FUNCT_C_NGLE = 57;
	public static final int R4300i_COP1_FUNCT_C_SEQ = 58;
	public static final int R4300i_COP1_FUNCT_C_NGL = 59;
	public static final int R4300i_COP1_FUNCT_C_LT = 60;
	public static final int R4300i_COP1_FUNCT_C_NGE = 61;
	public static final int R4300i_COP1_FUNCT_C_LE = 62;
	public static final int R4300i_COP1_FUNCT_C_NGT = 63;

	private static class ACTION {
		public boolean DoSomething;
		public boolean CheckInterrupts;
		public boolean DoInterrupt;
	};

	private static class CachedOpcode {
		public int inst;
		public boolean cached;
		public Runnable code;
	}

	private static final int OP = 26;
	private static final int RS = 21;
	private static final int RT = 16;
	private static final int RD = 11;
	private static final int SA = 6;

	// used by Main(init), DebugOps
	public int pc;
	public long[] GPR = new long[32];

	// used by DebugOps
	protected Cop0 cop0;
	protected Cop1 cop1;
	protected int jumpToLocation;
	protected long HI;
	protected long LO;
	protected int tmpWord;
	protected long tmpDouble;
	protected int inst;
	protected int target;
	protected int rs;
	protected int base;
	protected int rt;
	protected short offset;
	protected int rd;
	protected int sa;
	protected int funct;
	protected int currentInstr;

	private int addr;
	private int llBit;
	private int llAddr;
	private int[] regMI; // CheckInterrupts
	private int[] regCop0;
	private CachedOpcode[] cachedOpcodes;
	private CachedOpcode cachedOp;
	private Runnable cachedCode;
	private boolean inDelaySlot;
	private ACTION cpuAction;
	private Memory mem;
	private Runnable[] r4300i_Opcode;
	private Runnable[] r4300i_Special;
	private Runnable[] r4300i_Regimm;
	private Runnable[] r4300i_CoP0;
	private Runnable[] r4300i_CoP0_Function;
	private Runnable[] r4300i_CoP1;
	private Runnable[] r4300i_CoP1_BC;
	private Runnable[] r4300i_CoP1_S;
	private Runnable[] r4300i_CoP1_D;
	private Runnable[] r4300i_CoP1_W;
	private Runnable[] r4300i_CoP1_L;
	private long[] tmpDmultu = new long[3];
	private int tmpOp;
	private int tmpRs;
	private int tmpRt;
	private int tmpRd;
	private int tmpFunct;
	private boolean cacheInstructions;

	/** Creates a new instance of Cpu */
	public N64Cpu(Registers regs, Memory mem) {
		this.regMI = regs.regMI;
		this.mem = mem;
		LO = 0x0;
		HI = 0x0;
	}

	public void cacheInstructions(boolean cache) {
		cacheInstructions = cache;
		if (cacheInstructions) {
			cachedOpcodes = new CachedOpcode[0x402000 >>> 2]; // 1050624b (1Mb)
			for (int i = 0; i < (0x402000 >>> 2); i++)
				cachedOpcodes[i] = new CachedOpcode();
		} else {
			cachedOpcodes = null;
		}
	}

	// called by Main
	public void setOps(Runnable[][] ops) {
		r4300i_Opcode = ops[0];
		r4300i_Special = ops[1];
		r4300i_Regimm = ops[2];
		r4300i_CoP0 = ops[3];
		r4300i_CoP0_Function = ops[4];
		r4300i_CoP1 = ops[5];
		r4300i_CoP1_BC = ops[6];
		r4300i_CoP1_S = ops[7];
		r4300i_CoP1_D = ops[8];
		r4300i_CoP1_W = ops[9];
		r4300i_CoP1_L = ops[10];
	}

	// called by Main
	public void connect(Cop0 cop0, Cop1 cop1) {
		// this.regMI = regs.regMI;
		this.cop0 = cop0;
		this.cop1 = cop1;
//        this.mem = mem;

		this.regCop0 = cop0.CP0;
//        LO = 0x0;
//        HI = 0x0;
	}

	// Misc ////////////////////////////////////////////////////////////////////

	// used by Main [Audio]
	public final Runnable AiCheckInterrupts = new Runnable() {
		@Override
		public void run() {
			cpuAction.CheckInterrupts = true;
			cpuAction.DoSomething = true;
		}
	};

	// used by Main [Memory, Dma, Cop0, Rsp, Pif, Video]
	public final Runnable CheckInterrupts = new Runnable() {
		@Override
		public void run() {
			regMI[Registers.MI_INTR_REG] &= ~Registers.MI_INTR_AI;
			regMI[Registers.MI_INTR_REG] |= (mem.audioIntrReg[0] & Registers.MI_INTR_AI);

			if ((regMI[Registers.MI_INTR_MASK_REG] & regMI[Registers.MI_INTR_REG]) != 0) {
				cop0.CP0[Cop0.FAKE_CAUSE_REGISTER] |= Cop0.CAUSE_IP2;
			} else {
				cop0.CP0[Cop0.FAKE_CAUSE_REGISTER] &= ~Cop0.CAUSE_IP2;
			}
			if ((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_IE) == 0) {
				return;
			}
			if ((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_EXL) != 0) {
				return;
			}
			if ((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_ERL) != 0) {
				return;
			}

			if ((cop0.CP0[Cop0.STATUS_REGISTER] & cop0.CP0[Cop0.FAKE_CAUSE_REGISTER] & 0xFF00) != 0) {
				if (!cpuAction.DoInterrupt) {
					cpuAction.DoSomething = true;
					cpuAction.DoInterrupt = true;
				}
			}
		}
	};

	////////////////////////////////////////////////////////////////////////////

	// called by Main
	public void startEmulation() {
		cpuAction = new ACTION();
		cop0.initTimers();
		startInterpreterCPU(); // make this a thread later
	}

	private void addressErrorException(int address, boolean fromRead) {
		if (inDelaySlot)
			jumpToLocation = cop0.doAddressError(true, address, fromRead, pc) + 4;
		else
			jumpTo(cop0.doAddressError(false, address, fromRead, pc));
	}

	private boolean testCop1UsableException() {
		if ((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_CU1) != 0)
			return false;
		if (inDelaySlot) {
			jumpToLocation = cop0.doCopUnusableException(true, 1, pc) + 4;
			return false;
		} else {
			jumpTo(cop0.doCopUnusableException(false, 1, pc));
			return true;
		}
	}

	private void tlbReadException(int address) {
		if (inDelaySlot)
			jumpToLocation = cop0.doTlbMiss(true, address, pc) + 4;
		else
			jumpTo(cop0.doTlbMiss(false, address, pc));
	}

	// The main emulation loop
	private void startInterpreterCPU() {
//        java.util.Map dump = new java.util.HashMap();
		while (true) {
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(pc);
					cachedOp = cachedOpcodes[(pAddr < 0x400000) ? (pAddr >>> 2) : ((pAddr - 0x3C00000) >>> 2)];
					if (cachedOp.cached) {
						inst = cachedOp.inst;
						cachedCode = cachedOp.code;
					} else {
						cachedOp.cached = true;
						cachedOp.inst = inst = mem.loadWord(pAddr);
						cachedCode = cachedOp.code = (((inst >> OP) & 0x3F) == 0) ? r4300i_Special[inst & 0x3F]
								: r4300i_Opcode[(inst >> OP) & 0x3F];
					}
				} else {
					inst = mem.loadWord(cop0.translateVaddr(pc));
//                    if (dump_inst && !dump.containsKey(pc)) {
//                        dump.put(pc, inst);
//                        System.out.println(Integer.toHexString(pc)+" : "+Integer.toBinaryString(inst));
//                    }
				}
			} catch (TlbException e) {
				pc = cop0.doTlbMiss(inDelaySlot, pc, pc);
				continue;
			} catch (MemoryException e) {
				e.printStackTrace();
				continue;
			}

			cop0.tick++;

			if (cacheInstructions)
				cachedCode.run();
			else
				r4300i_Opcode[((inst >> OP) & 0x3F)].run();

			pc += 4;
//            Thread.yield();
		}
	}

	private boolean executeInstruction() {
		try {
			if (cacheInstructions) {
				int pAddr = cop0.translateVaddr(pc);
				cachedOp = cachedOpcodes[(pAddr < 0x400000) ? (pAddr >>> 2) : ((pAddr - 0x3C00000) >>> 2)];
				if (cachedOp.cached) {
					inst = cachedOp.inst;
					cachedCode = cachedOp.code;
				} else {
					cachedOp.cached = true;
					cachedOp.inst = inst = mem.loadWord(pAddr);
					cachedCode = cachedOp.code = (((inst >> OP) & 0x3F) == 0) ? r4300i_Special[inst & 0x3F]
							: r4300i_Opcode[(inst >> OP) & 0x3F];
				}
			} else {
				inst = mem.loadWord(cop0.translateVaddr(pc));
			}
		} catch (TlbException e) {
			pc = cop0.doTlbMiss(inDelaySlot, pc, pc);
			return false;
		} catch (MemoryException e) {
			e.printStackTrace();
			return false;
		}

		cop0.tick++;

		if (cacheInstructions)
			cachedCode.run();
		else
			r4300i_Opcode[((inst >> OP) & 0x3F)].run();

		return true;
	}

	private int testInterpreterJump(int pc, int targetPC, int reg1, int reg2) {
		if (pc != targetPC)
			return targetPC;

		if (delaySlotEffectsCompare(pc, reg1, reg2))
			return targetPC;

		if (cpuAction.DoInterrupt)
			return targetPC;

		if (cop0.inPermLoop()) {
//            exit("In a permanent loop that can not be exited\n\nEmulation will now stop");
			System.err.printf("In a permanent loop that can not be exited\n\nEmulation will now stop\n");
			System.exit(0);
		}

		return targetPC;
	}

	private boolean delaySlotEffectsCompare(int pc, int reg1, int reg2) {
		try {
			int instr;
			if (cacheInstructions) {
				int pAddr = cop0.translateVaddr(pc + 4);
				cachedOp = cachedOpcodes[(pAddr < 0x400000) ? (pAddr >>> 2) : ((pAddr - 0x3C00000) >>> 2)];
				if (cachedOp.cached) {
					instr = cachedOp.inst;
				} else {
					cachedOp.cached = true;
					instr = cachedOp.inst = mem.loadWord(pAddr);
					cachedOp.code = (((instr >> 26) & 0x3F) == 0) ? r4300i_Special[instr & 0x3F]
							: r4300i_Opcode[(instr >> 26) & 0x3F];
				}
			} else {
				instr = mem.loadWord(cop0.translateVaddr(pc + 4));
			}
			tmpOp = (instr >> 26) & 0x3F;
			tmpRs = (instr >> 21) & 0x1F;
			tmpRt = (instr >> 16) & 0x1F;
			tmpRd = (instr >> 11) & 0x1F;
			tmpFunct = (instr) & 0x3F;
		} catch (TlbException e) {
			return true;
		} catch (MemoryException e) {
			e.printStackTrace();
			return true;
		}

		switch (tmpOp) {
		case R4300i_SPECIAL:
			switch (tmpFunct) {
			case R4300i_SPECIAL_SLL:
			case R4300i_SPECIAL_SRL:
			case R4300i_SPECIAL_SRA:
			case R4300i_SPECIAL_SLLV:
			case R4300i_SPECIAL_SRLV:
			case R4300i_SPECIAL_SRAV:
			case R4300i_SPECIAL_MFHI:
			case R4300i_SPECIAL_MTHI:
			case R4300i_SPECIAL_MFLO:
			case R4300i_SPECIAL_MTLO:
			case R4300i_SPECIAL_DSLLV:
			case R4300i_SPECIAL_DSRLV:
			case R4300i_SPECIAL_DSRAV:
			case R4300i_SPECIAL_ADD:
			case R4300i_SPECIAL_ADDU:
			case R4300i_SPECIAL_SUB:
			case R4300i_SPECIAL_SUBU:
			case R4300i_SPECIAL_AND:
			case R4300i_SPECIAL_OR:
			case R4300i_SPECIAL_XOR:
			case R4300i_SPECIAL_NOR:
			case R4300i_SPECIAL_SLT:
			case R4300i_SPECIAL_SLTU:
			case R4300i_SPECIAL_DADD:
			case R4300i_SPECIAL_DADDU:
			case R4300i_SPECIAL_DSUB:
			case R4300i_SPECIAL_DSUBU:
			case R4300i_SPECIAL_DSLL:
			case R4300i_SPECIAL_DSRL:
			case R4300i_SPECIAL_DSRA:
			case R4300i_SPECIAL_DSLL32:
			case R4300i_SPECIAL_DSRL32:
			case R4300i_SPECIAL_DSRA32:
				if (tmpRd == 0) {
					return false;
				}
				if (tmpRd == reg1) {
					return true;
				}
				if (tmpRd == reg2) {
					return true;
				}
				break;
			case R4300i_SPECIAL_MULT:
			case R4300i_SPECIAL_MULTU:
			case R4300i_SPECIAL_DIV:
			case R4300i_SPECIAL_DIVU:
			case R4300i_SPECIAL_DMULT:
			case R4300i_SPECIAL_DMULTU:
			case R4300i_SPECIAL_DDIV:
			case R4300i_SPECIAL_DDIVU:
				break;
			default:
				return true;
			}
			break;
		case R4300i_CP0:
			switch (tmpRs) {
			case R4300i_COP0_MT:
				break;
			case R4300i_COP0_MF:
				if (tmpRt == 0) {
					return false;
				}
				if (tmpRt == reg1) {
					return true;
				}
				if (tmpRt == reg2) {
					return true;
				}
				break;
			default:
				if ((tmpRs & 0x10) != 0) {
					switch ((inst & 0x3F)) {
					case R4300i_COP0_CO_TLBR:
						break;
					case R4300i_COP0_CO_TLBWI:
						break;
					case R4300i_COP0_CO_TLBWR:
						break;
					case R4300i_COP0_CO_TLBP:
						break;
					default:
						return true;
					}
				} else {
					return true;
				}
			}
			break;
		case R4300i_CP1:
			switch (tmpRs) {
			case R4300i_COP1_MF:
				if (tmpRt == 0) {
					return false;
				}
				if (tmpRt == reg1) {
					return true;
				}
				if (tmpRt == reg2) {
					return true;
				}
				break;
			case R4300i_COP1_CF:
				break;
			case R4300i_COP1_MT:
				break;
			case R4300i_COP1_CT:
				break;
			case R4300i_COP1_S:
				break;
			case R4300i_COP1_D:
				break;
			case R4300i_COP1_W:
				break;
			case R4300i_COP1_L:
				break;
			default:
				return true;
			}
			break;
		case R4300i_ANDI:
		case R4300i_ORI:
		case R4300i_XORI:
		case R4300i_LUI:
		case R4300i_ADDI:
		case R4300i_ADDIU:
		case R4300i_SLTI:
		case R4300i_SLTIU:
		case R4300i_DADDI:
		case R4300i_DADDIU:
		case R4300i_LB:
		case R4300i_LH:
		case R4300i_LW:
		case R4300i_LWL:
		case R4300i_LWR:
		case R4300i_LDL:
		case R4300i_LDR:
		case R4300i_LBU:
		case R4300i_LHU:
		case R4300i_LD:
		case R4300i_LWC1:
		case R4300i_LDC1:
			if (tmpRt == 0) {
				return false;
			}
			if (tmpRt == reg1) {
				return true;
			}
			if (tmpRt == reg2) {
				return true;
			}
			break;
		case R4300i_CACHE:
			break;
		case R4300i_SB:
			break;
		case R4300i_SH:
			break;
		case R4300i_SW:
			break;
		case R4300i_SWR:
			break;
		case R4300i_SWL:
			break;
		case R4300i_SWC1:
			break;
		case R4300i_SDC1:
			break;
		case R4300i_SD:
			break;
		default:
			return true;
		}
		return false;
	}

	private void jumpTo(int address) {
		inDelaySlot = false;
		pc = address;
		cop0.timerDone();
		if (cpuAction.DoSomething) {
//            if (cpuAction.CloseCPU) {
//                System.out.println("Closing CPU");
//                System.exit(0);
//            }
			if (cpuAction.CheckInterrupts) {
				cpuAction.CheckInterrupts = false;
				CheckInterrupts.run();
			}
			if (cpuAction.DoInterrupt) {
				cpuAction.DoInterrupt = false;
				pc = cop0.doIntrException(false, pc);
			}

			cpuAction.DoSomething = false;

			if (cpuAction.DoInterrupt) {
				cpuAction.DoSomething = true;
			}
		}
		pc -= 4;
	}

	private int compareUnsignedLongs(long a, long b) {
		if (a == b)
			return 0;
		if (((a >> 32) & 0xFFFFFFFFL) < ((b >> 32) & 0xFFFFFFFFL))
			return -1;
		if (((a >> 32) & 0xFFFFFFFFL) > ((b >> 32) & 0xFFFFFFFFL))
			return 1;
		if (((a >> 32) & 0xFFFFFFFFL) == ((b >> 32) & 0xFFFFFFFFL) && (a & 0xFFFFFFFFL) < (b & 0xFFFFFFFFL))
			return -1;
		return 1;
	}

	public Runnable R4300i_opcode_SPECIAL = new Runnable() {
		@Override
		public void run() {
			r4300i_Special[inst & 0x3F].run();
		}
	};

	public Runnable R4300i_opcode_REGIMM = new Runnable() {
		@Override
		public void run() {
			r4300i_Regimm[(inst >> RT) & 0x1F].run();
		}
	};

	public Runnable R4300i_opcode_COP0 = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP0[(inst >> 21) & 0x1F].run();
		}
	};

	public Runnable R4300i_opcode_COP0_CO = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP0_Function[inst & 0x3F].run();
		}
	};

	public Runnable R4300i_opcode_COP1 = new Runnable() {
		@Override
		public void run() {
			if (testCop1UsableException())
				return;
			cop1.ft = (inst >> RT) & 0x1F;
			cop1.fs = (inst >> RD) & 0x1F;
			cop1.fd = (inst >> SA) & 0x1F;
			cop1.funct = inst & 0x3F;
			r4300i_CoP1[cop1.fmt = (inst >> RS) & 0x1F].run();
		}
	};

	public Runnable R4300i_opcode_COP1_BC = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP1_BC[(inst >> RT) & 0x1F].run();
		}
	};

	public Runnable R4300i_opcode_COP1_S = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP1_S[inst & 0x3F].run();
		}
	};

	public Runnable R4300i_opcode_COP1_D = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP1_D[inst & 0x3F].run();
		}
	};

	public Runnable R4300i_opcode_COP1_W = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP1_W[inst & 0x3F].run();
		}
	};

	public Runnable R4300i_opcode_COP1_L = new Runnable() {
		@Override
		public void run() {
			r4300i_CoP1_L[inst & 0x3F].run();
		}
	};

	/************************* OpCode functions *************************/
	public Runnable r4300i_J = new Runnable() {
		@Override
		public void run() {
			jumpToLocation = testInterpreterJump(pc, (pc & 0xF0000000) + ((inst & 0x3FFFFFF) << 2), 0, 0);
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_JAL = new Runnable() {
		@Override
		public void run() {
			jumpToLocation = testInterpreterJump(pc, (pc & 0xF0000000) + ((inst & 0x3FFFFFF) << 2), 0, 0);
			GPR[31] = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_BEQ = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] == GPR[(inst >> RT) & 0x1F])
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F,
						(inst >> RT) & 0x1F);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_BNE = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] != GPR[(inst >> RT) & 0x1F])
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F,
						(inst >> RT) & 0x1F);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_BLEZ = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] <= 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_BGTZ = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] > 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_ADDI = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0)
				return;
			GPR[(inst >> RT) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
		}
	};

	public Runnable r4300i_ADDIU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
		}
	};

	public Runnable r4300i_SLTI = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] < ((short) inst))
				GPR[(inst >> RT) & 0x1F] = 1L;
			else
				GPR[(inst >> RT) & 0x1F] = 0L;
		}
	};

	public Runnable r4300i_SLTIU = new Runnable() {
		@Override
		public void run() {
			if (compareUnsignedLongs(GPR[(inst >> RS) & 0x1F], ((short) inst)) < 0)
				GPR[(inst >> RT) & 0x1F] = 1L;
			else
				GPR[(inst >> RT) & 0x1F] = 0L;
		}
	};

	public Runnable r4300i_ANDI = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = GPR[(inst >> RS) & 0x1F] & (inst & 0xFFFF);
		}
	};

	public Runnable r4300i_ORI = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = GPR[(inst >> RS) & 0x1F] | (inst & 0xFFFF);
		}
	};

	public Runnable r4300i_XORI = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = GPR[(inst >> RS) & 0x1F] ^ (inst & 0xFFFF);
		}
	};

	public Runnable r4300i_LUI = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0)
				return;
			GPR[(inst >> RT) & 0x1F] = (inst & 0xFFFF) << 16;
		}
	};

	public Runnable r4300i_BEQL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] == GPR[(inst >> RT) & 0x1F]) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F,
						(inst >> RT) & 0x1F);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_BNEL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] != GPR[(inst >> RT) & 0x1F]) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F,
						(inst >> RT) & 0x1F);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_BLEZL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] <= 0L) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_BGTZL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] > 0L) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_DADDIU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = GPR[(inst >> RS) & 0x1F] + ((short) inst);
		}
	};

	private static long[] LDL_MASK = { 0x00000000000000L, 0x000000000000FFL, 0x0000000000FFFFL, 0x00000000FFFFFFL,
			0x000000FFFFFFFFL, 0x0000FFFFFFFFFFL, 0x00FFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL };
	private static int[] LDL_SHIFT = { 0, 8, 16, 24, 32, 40, 48, 56 };

	public Runnable r4300i_LDL = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpDouble = mem.loadDoubleWord(cop0.translateVaddr(addr & ~7));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LDL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
			GPR[(inst >> RT) & 0x1F] = (GPR[(inst >> RT) & 0x1F] & LDL_MASK[addr & 7])
					+ (tmpDouble << LDL_SHIFT[addr & 7]);
		}
	};

	private static long[] LDR_MASK = { 0xFFFFFFFFFFFFFF00L, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFF000000L,
			0xFFFFFFFF00000000L, 0xFFFFFF0000000000L, 0xFFFF000000000000L, 0xFF00000000000000L, 0x0000000000000000L };
	private static int[] LDR_SHIFT = { 56, 48, 40, 32, 24, 16, 8, 0 };

	public Runnable r4300i_LDR = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpDouble = mem.loadDoubleWord(cop0.translateVaddr(addr & ~7));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LDL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
			GPR[(inst >> RT) & 0x1F] = (GPR[(inst >> RT) & 0x1F] & LDR_MASK[addr & 7])
					+ (tmpDouble >> LDR_SHIFT[addr & 7]);
		}
	};

	public Runnable r4300i_LB = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0) {
				return;
			}
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadByte(cop0.translateVaddr(addr));
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LB TLB: %X\n", addr);
				}
				tlbReadException(addr);
			} catch (MemoryException e) {
				e.printStackTrace();
			}
		}
	};

	public Runnable r4300i_LH = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 1) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadHalfWord(cop0.translateVaddr(addr));
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LH TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private static int[] LWL_MASK = { 0x000000, 0x0000FF, 0x00FFFF, 0xFFFFFF };
	private static int[] LWL_SHIFT = { 0, 8, 16, 24 };

	public Runnable r4300i_LWL = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpWord = mem.loadWord(cop0.translateVaddr(addr & ~3));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LWL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
			GPR[(inst >> RT) & 0x1F] = ((int) GPR[(inst >> RT) & 0x1F] & LWL_MASK[addr & 3])
					+ (tmpWord << LWL_SHIFT[addr & 3]);
		}
	};

	public Runnable r4300i_LW = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0) {
				return;
			}
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadWord(cop0.translateVaddr(addr));
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LW TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_LBU = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadByte(cop0.translateVaddr(addr)) & 0xFFL;
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LBU TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_LHU = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 1) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadHalfWord(cop0.translateVaddr(addr)) & 0xFFFFL;
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LHU TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private static int[] LWR_MASK = { 0xFFFFFF00, 0xFFFF0000, 0xFF000000, 0x00000000 };
	private static int[] LWR_SHIFT = { 24, 16, 8, 0 };

	public Runnable r4300i_LWR = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpWord = mem.loadWord(cop0.translateVaddr(addr & ~3));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LWR TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
			GPR[(inst >> RT) & 0x1F] = ((int) GPR[(inst >> RT) & 0x1F] & LWR_MASK[addr & 3])
					+ (tmpWord >> LWR_SHIFT[addr & 3]);
		}
	};

	public Runnable r4300i_LWU = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0) {
				return;
			}
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadWord(cop0.translateVaddr(addr)) & 0xFFFFFFFFL;
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LWU TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SB = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000)
						cachedOpcodes[pAddr >>> 2].cached = false;
					else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
					mem.storeByte(pAddr, (byte) GPR[(inst >> RT) & 0x1F]);
				} else {
					mem.storeByte(cop0.translateVaddr(addr), (byte) GPR[(inst >> RT) & 0x1F]);
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SB TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SH = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 1) != 0) {
				addressErrorException(addr, false);
				return;
			}
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000)
						cachedOpcodes[pAddr >>> 2].cached = false;
					else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
					mem.storeHalfWord(pAddr, (short) GPR[(inst >> RT) & 0x1F]);
				} else {
					mem.storeHalfWord(cop0.translateVaddr(addr), (short) GPR[(inst >> RT) & 0x1F]);
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SH TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private static int[] SWL_MASK = { 0x00000000, 0xFF000000, 0xFFFF0000, 0xFFFFFF00 };
	private static int[] SWL_SHIFT = { 0, 8, 16, 24 };

	public Runnable r4300i_SWL = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpWord = (mem.loadWord(cop0.translateVaddr(addr & ~3)) & SWL_MASK[addr & 3])
						+ ((int) GPR[(inst >> RT) & 0x1F] >>> SWL_SHIFT[addr & 3]);
				try {
					if (cacheInstructions) {
						int pAddr = cop0.translateVaddr(addr) & ~0x03;
						if (pAddr < 0x400000)
							cachedOpcodes[pAddr >>> 2].cached = false;
						else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
							cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
						mem.storeWord(pAddr, tmpWord);
					} else {
						mem.storeWord(cop0.translateVaddr(addr) & ~0x03, tmpWord);
					}
				} catch (TlbException e) {
					e.printStackTrace();
					System.err.printf("SWL TLB: %X\n", addr);
				} catch (MemoryException e) {
					e.printStackTrace();
					return;
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SWL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SW = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, false);
				return;
			}
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000)
						cachedOpcodes[pAddr >>> 2].cached = false;
					else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
					mem.storeWord(pAddr, (int) GPR[(inst >> RT) & 0x1F]);
				} else {
					mem.storeWord(cop0.translateVaddr(addr), (int) GPR[(inst >> RT) & 0x1F]);
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SW TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private static long[] SDL_MASK = { 0x0000000000000000L, 0xFF00000000000000L, 0xFFFF000000000000L,
			0xFFFFFF0000000000L, 0xFFFFFFFF00000000L, 0xFFFFFFFFFF000000L, 0xFFFFFFFFFFFF0000L, 0xFFFFFFFFFFFFFF00L };
	private static int[] SDL_SHIFT = { 0, 8, 16, 24, 32, 40, 48, 56 };

	public Runnable r4300i_SDL = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpDouble = (mem.loadDoubleWord(cop0.translateVaddr(addr & ~7)) & SDL_MASK[addr & 7])
						+ (GPR[(inst >> RT) & 0x1F] >> SDL_SHIFT[addr & 7]);
				try {
					cacheInstructions();
				} catch (TlbException e) {
					e.printStackTrace();
					System.err.printf("SDL TLB: %X\n", addr);
				} catch (MemoryException e) {
					e.printStackTrace();
					return;
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SDL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private static long[] SDR_MASK = { 0x00FFFFFFFFFFFFFFL, 0x0000FFFFFFFFFFFFL, 0x000000FFFFFFFFFFL,
			0x00000000FFFFFFFFL, 0x0000000000FFFFFFL, 0x000000000000FFFFL, 0x00000000000000FFL, 0x0000000000000000L };
	private static int[] SDR_SHIFT = { 56, 48, 40, 32, 24, 16, 8, 0 };

	public Runnable r4300i_SDR = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpDouble = (mem.loadDoubleWord(cop0.translateVaddr(addr & ~7)) & SDR_MASK[addr & 7])
						+ (GPR[(inst >> RT) & 0x1F] << SDR_SHIFT[addr & 7]);
				try {
					cacheInstructions();
				} catch (TlbException e) {
					e.printStackTrace();
					System.err.printf("SDR TLB: %X\n", addr);
				} catch (MemoryException e) {
					e.printStackTrace();
					return;
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SDR TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	private void cacheInstructions() throws TlbException, MemoryException {
		if (cacheInstructions) {
			int pAddr = cop0.translateVaddr(addr & ~7);
			if (pAddr < 0x400000) {
				cachedOpcodes[pAddr >>> 2].cached = false;
				cachedOpcodes[(pAddr + 4) >>> 2].cached = false;
			} else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length) {
				cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
				cachedOpcodes[((pAddr + 4) - 0x3C00000) >>> 2].cached = false;
			}
			mem.storeDoubleWord(pAddr, tmpDouble);
		} else {
			mem.storeDoubleWord(cop0.translateVaddr(addr & ~7), tmpDouble);
		}
	}

	private static int[] SWR_MASK = { 0x00FFFFFF, 0x0000FFFF, 0x000000FF, 0x00000000 };
	private static int[] SWR_SHIFT = { 24, 16, 8, 0 };

	public Runnable r4300i_SWR = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			try {
				tmpWord = (mem.loadWord(cop0.translateVaddr(addr & ~3)) & SWR_MASK[addr & 3])
						+ ((int) GPR[(inst >> RT) & 0x1F] << SWR_SHIFT[addr & 3]);
				try {
					if (cacheInstructions) {
						int pAddr = cop0.translateVaddr(addr) & ~0x03;
						if (pAddr < 0x400000)
							cachedOpcodes[pAddr >>> 2].cached = false;
						else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
							cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
						mem.storeWord(pAddr, tmpWord);
					} else {
						mem.storeWord(cop0.translateVaddr(addr) & ~0x03, tmpWord);
					}
				} catch (TlbException e) {
					e.printStackTrace();
					System.err.printf("SWL TLB: %X\n", addr);
				} catch (MemoryException e) {
					e.printStackTrace();
					return;
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SWL TLB: %X\n", addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_CACHE = new Runnable() {
		@Override
		public void run() {
		}
	};

	public Runnable r4300i_LL = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RT) & 0x1F) == 0) {
				return;
			}
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadWord(cop0.translateVaddr(addr));
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LW TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
			llBit = 1;
			llAddr = addr;
			try {
				llAddr = cop0.translateVaddr(llAddr);
			} catch (TlbException e) {
				e.printStackTrace();
			}
		}
	};

	public Runnable r4300i_LWC1 = new Runnable() {
		@Override
		public void run() {
			if (testCop1UsableException())
				return;
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				cop1.lwC1((inst >> RT) & 0x1F, mem.loadWord(cop0.translateVaddr(addr)));
			} catch (TlbException e) {
				if (EmuManager.GLOBAL_DEBUG) {
					e.printStackTrace();
					System.err.printf("LWC1 TLB: %X\n", addr);
				}
				tlbReadException(addr);
				return;
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SC = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, false);
				return;
			}
			if (llBit == 1) {
				try {
					if (cacheInstructions) {
						int pAddr = cop0.translateVaddr(addr);
						if (pAddr < 0x400000)
							cachedOpcodes[pAddr >>> 2].cached = false;
						else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
							cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
						mem.storeWord(pAddr, (int) GPR[(inst >> RT) & 0x1F]);
					} else {
						mem.storeWord(cop0.translateVaddr(addr), (int) GPR[(inst >> RT) & 0x1F]);
					}
				} catch (TlbException e) {
					e.printStackTrace();
					System.err.printf("SC TLB: %X\n", addr);
				} catch (MemoryException e) {
					e.printStackTrace();
					return;
				}
			}
			GPR[(inst >> RT) & 0x1F] = (llBit & 0x00000000FFFFFFFFL) | (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFF00000000L);
		}
	};

	public Runnable r4300i_LD = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 7) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				GPR[(inst >> RT) & 0x1F] = mem.loadDoubleWord(cop0.translateVaddr(addr));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LD TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_LDC1 = new Runnable() {
		@Override
		public void run() {
			if (testCop1UsableException())
				return;
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 7) != 0) {
				addressErrorException(addr, true);
				return;
			}
			try {
				cop1.ldC1((inst >> RT) & 0x1F, mem.loadDoubleWord(cop0.translateVaddr(addr)));
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("LDC1 TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SWC1 = new Runnable() {
		@Override
		public void run() {
			if (testCop1UsableException())
				return;
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 3) != 0) {
				addressErrorException(addr, false);
				return;
			}
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000)
						cachedOpcodes[pAddr >>> 2].cached = false;
					else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length)
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
					mem.storeWord(pAddr, cop1.swC1((inst >> RT) & 0x1F));
				} else {
					mem.storeWord(cop0.translateVaddr(addr), cop1.swC1((inst >> RT) & 0x1F));
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SWC1 TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SDC1 = new Runnable() {
		@Override
		public void run() {
			if (testCop1UsableException())
				return;
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 7) != 0) {
				addressErrorException(addr, false);
				return;
			}
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000) {
						cachedOpcodes[pAddr >>> 2].cached = false;
						cachedOpcodes[(pAddr + 4) >>> 2].cached = false;
					} else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length) {
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
						cachedOpcodes[((pAddr + 4) - 0x3C00000) >>> 2].cached = false;
					}
					mem.storeDoubleWord(pAddr, cop1.sdC1((inst >> RT) & 0x1F));
				} else {
					mem.storeDoubleWord(cop0.translateVaddr(addr), cop1.sdC1((inst >> RT) & 0x1F));
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SDC1 TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	public Runnable r4300i_SD = new Runnable() {
		@Override
		public void run() {
			addr = (int) GPR[(inst >> RS) & 0x1F] + (short) inst;
			if ((addr & 7) != 0) {
				addressErrorException(addr, false);
				return;
			}
			try {
				if (cacheInstructions) {
					int pAddr = cop0.translateVaddr(addr);
					if (pAddr < 0x400000) {
						cachedOpcodes[pAddr >>> 2].cached = false;
						cachedOpcodes[(pAddr + 4) >>> 2].cached = false;
					} else if (((pAddr - 0x3C00000) >>> 2) < cachedOpcodes.length) {
						cachedOpcodes[(pAddr - 0x3C00000) >>> 2].cached = false;
						cachedOpcodes[((pAddr + 4) - 0x3C00000) >>> 2].cached = false;
					}
					mem.storeDoubleWord(pAddr, GPR[(inst >> RT) & 0x1F]);
				} else {
					mem.storeDoubleWord(cop0.translateVaddr(addr), GPR[(inst >> RT) & 0x1F]);
				}
			} catch (TlbException e) {
				e.printStackTrace();
				System.err.printf("SD TLB: %X\n", addr);
			} catch (MemoryException e) {
				e.printStackTrace();
				return;
			}
		}
	};

	/********************** R4300i OpCodes: Special **********************/
	public Runnable r4300i_SPECIAL_SLL = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] << ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_SRL = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] >>> ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_SRA = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] >> ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_SLLV = new Runnable() {
		@Override
		public void run() {
			if (((inst >> RD) & 0x1F) == 0)
				return;
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] << (GPR[(inst >> RS) & 0x1F] & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_SRLV = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] >>> (GPR[(inst >> RS) & 0x1F] & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_SRAV = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RT) & 0x1F] >> (GPR[(inst >> RS) & 0x1F] & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_JR = new Runnable() {
		@Override
		public void run() {
			jumpToLocation = (int) GPR[(inst >> RS) & 0x1F];
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_SPECIAL_JALR = new Runnable() {
		@Override
		public void run() {
			jumpToLocation = (int) GPR[(inst >> RS) & 0x1F];
			GPR[(inst >> RD) & 0x1F] = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_SPECIAL_SYSCALL = new Runnable() {
		@Override
		public void run() {
			if (inDelaySlot)
				jumpToLocation = cop0.doSysCallException(true, pc) + 4;
			else
				jumpTo(cop0.doSysCallException(false, pc));
		}
	};

	public Runnable r4300i_SPECIAL_BREAK = new Runnable() {
		@Override
		public void run() {
			if (inDelaySlot)
				jumpToLocation = cop0.doBreakException(true, pc) + 4;
			else
				jumpTo(cop0.doBreakException(false, pc));
		}
	};

	public Runnable r4300i_SPECIAL_SYNC = new Runnable() {
		@Override
		public void run() {
		}
	};

	public Runnable r4300i_SPECIAL_MFHI = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = HI;
		}
	};

	public Runnable r4300i_SPECIAL_MTHI = new Runnable() {
		@Override
		public void run() {
			HI = GPR[(inst >> RS) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_MFLO = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = LO;
		}
	};

	public Runnable r4300i_SPECIAL_MTLO = new Runnable() {
		@Override
		public void run() {
			LO = GPR[(inst >> RS) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DSLLV = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] << (GPR[(inst >> RS) & 0x1F] & 0x3F);
		}
	};

	public Runnable r4300i_SPECIAL_DSRLV = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >>> (GPR[(inst >> RS) & 0x1F] & 0x3F);
		}
	};

	public Runnable r4300i_SPECIAL_DSRAV = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >> (GPR[(inst >> RS) & 0x1F] & 0x3F);
		}
	};

	public Runnable r4300i_SPECIAL_MULT = new Runnable() {
		@Override
		public void run() {
			HI = (long) ((int) GPR[(inst >> RS) & 0x1F]) * (long) ((int) GPR[(inst >> RT) & 0x1F]);
			LO = (int) HI;
			HI = (int) (HI >> 32);
		}
	};

	public Runnable r4300i_SPECIAL_MULTU = new Runnable() {
		@Override
		public void run() {
			HI = (GPR[(inst >> RS) & 0x1F] & 0xFFFFFFFFL) * (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFFL);
			LO = (int) HI;
			HI = (int) (HI >> 32);
		}
	};

	public Runnable r4300i_SPECIAL_DIV = new Runnable() {
		@Override
		public void run() {
			LO = (int) GPR[(inst >> RS) & 0x1F] / (int) GPR[(inst >> RT) & 0x1F];
			HI = (int) GPR[(inst >> RS) & 0x1F] % (int) GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DIVU = new Runnable() {
		@Override
		public void run() {
			LO = (GPR[(inst >> RS) & 0x1F] & 0xFFFFFFFFL) / (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFFL);
			HI = (GPR[(inst >> RS) & 0x1F] & 0xFFFFFFFFL) % (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFFL);
		}
	};

	public Runnable r4300i_SPECIAL_DMULT = new Runnable() {
		@Override
		public void run() {
		}
	};

	public Runnable r4300i_SPECIAL_DMULTU = new Runnable() {
		@Override
		public void run() {
			LO = (GPR[(inst >> RS) & 0x1F] & 0xFFFFFFFFL) * (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFFL);
			tmpDmultu[0] = (GPR[(inst >> RS) & 0x1F] >>> 32) * (GPR[(inst >> RT) & 0x1F] & 0xFFFFFFFFL);
			tmpDmultu[1] = (GPR[(inst >> RS) & 0x1F] & 0xFFFFFFFFL) * (GPR[(inst >> RT) & 0x1F] >>> 32);
			HI = (GPR[(inst >> RS) & 0x1F] >>> 32) * (GPR[(inst >> RT) & 0x1F] >>> 32);
			tmpDmultu[2] = (LO >>> 32) + (tmpDmultu[0] & 0xFFFFFFFFL) + (tmpDmultu[1] & 0xFFFFFFFFL);
			LO += ((tmpDmultu[0] & 0xFFFFFFFFL) + (tmpDmultu[1] & 0xFFFFFFFFL)) << 32;
			HI += (tmpDmultu[0] >>> 32) + (tmpDmultu[1] >>> 32) + (tmpDmultu[2] >>> 32);
		}
	};

	public Runnable r4300i_SPECIAL_DDIV = new Runnable() {
		@Override
		public void run() {
			LO = GPR[(inst >> RS) & 0x1F] / GPR[(inst >> RT) & 0x1F];
			HI = GPR[(inst >> RS) & 0x1F] % GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DDIVU = new Runnable() {
		@Override
		public void run() {
			LO = GPR[(inst >> RS) & 0x1F] / GPR[(inst >> RT) & 0x1F];
			HI = GPR[(inst >> RS) & 0x1F] % GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_ADD = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] + (int) GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_ADDU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] + (int) GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_SUB = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] - (int) GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_SUBU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = (int) GPR[(inst >> RS) & 0x1F] - (int) GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_AND = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] & GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_OR = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] | GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_XOR = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] ^ GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_NOR = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = ~(GPR[(inst >> RS) & 0x1F] | GPR[(inst >> RT) & 0x1F]);
		}
	};

	public Runnable r4300i_SPECIAL_SLT = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] < GPR[(inst >> RT) & 0x1F]) // signed comparison
				GPR[(inst >> RD) & 0x1F] = 1L;
			else
				GPR[(inst >> RD) & 0x1F] = 0L;
		}
	};

	public Runnable r4300i_SPECIAL_SLTU = new Runnable() {
		@Override
		public void run() {
			if (compareUnsignedLongs(GPR[(inst >> RS) & 0x1F], GPR[(inst >> RT) & 0x1F]) < 0)
				GPR[(inst >> RD) & 0x1F] = 1L;
			else
				GPR[(inst >> RD) & 0x1F] = 0L;
		}
	};

	public Runnable r4300i_SPECIAL_DADD = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] + GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DADDU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] + GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DSUB = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] - GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_DSUBU = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RS) & 0x1F] - GPR[(inst >> RT) & 0x1F];
		}
	};

	public Runnable r4300i_SPECIAL_TEQ = new Runnable() {
		@Override
		public void run() {
//            if (GPR[(inst>>RS)&0x1F] == GPR[(inst>>RT)&0x1F])
//                System.err.printf("Should trap this ???\n");
		}
	};

	public Runnable r4300i_SPECIAL_DSLL = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] << ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_DSRL = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >>> ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_DSRA = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >> ((inst >> SA) & 0x1F);
		}
	};

	public Runnable r4300i_SPECIAL_DSLL32 = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] << (((inst >> SA) & 0x1F) + 32);
		}
	};

	public Runnable r4300i_SPECIAL_DSRL32 = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >>> (((inst >> SA) & 0x1F) + 32);
		}
	};

	public Runnable r4300i_SPECIAL_DSRA32 = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RD) & 0x1F] = GPR[(inst >> RT) & 0x1F] >> (((inst >> SA) & 0x1F) + 32);
		}
	};

	/********************** R4300i OpCodes: RegImm **********************/
	public Runnable r4300i_REGIMM_BLTZ = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] < 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_REGIMM_BGEZ = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] >= 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_REGIMM_BLTZL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] < 0L) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_REGIMM_BGEZL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] >= 0L) {
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_REGIMM_BLTZAL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] < 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			GPR[31] = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_REGIMM_BGEZAL = new Runnable() {
		@Override
		public void run() {
			if (GPR[(inst >> RS) & 0x1F] >= 0L)
				jumpToLocation = testInterpreterJump(pc, pc + (((short) inst) << 2) + 4, (inst >> RS) & 0x1F, 0);
			else
				jumpToLocation = pc + 8;
			GPR[31] = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	/************************** COP1 functions **************************/
	public Runnable r4300i_COP1_MF = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = cop1.mfC1((inst >> RD) & 0x1F);
		}
	};

	public Runnable r4300i_COP1_DMF = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = cop1.dmfC1((inst >> RD) & 0x1F);
		}
	};

	public Runnable r4300i_COP1_CF = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = cop1.cfC1((inst >> RD) & 0x1F);
		}
	};

	public Runnable r4300i_COP1_MT = new Runnable() {
		@Override
		public void run() {
			cop1.mtC1((inst >> RD) & 0x1F, (int) GPR[(inst >> RT) & 0x1F]);
		}
	};

	public Runnable r4300i_COP1_DMT = new Runnable() {
		@Override
		public void run() {
			cop1.dmtC1((inst >> RD) & 0x1F, GPR[(inst >> RT) & 0x1F]);
		}
	};

	public Runnable r4300i_COP1_CT = new Runnable() {
		@Override
		public void run() {
			cop1.ctC1((inst >> RD) & 0x1F, (int) GPR[(inst >> RT) & 0x1F]);
		}
	};

	/************************* COP1: BC1 functions ***********************/
	public Runnable r4300i_COP1_BCF = new Runnable() {
		@Override
		public void run() {
			if ((cop1.FPCR[Cop1.FSTATUS_REGISTER] & Cop1.FPCSR_C) == 0)
				jumpToLocation = pc + (((short) inst) << 2) + 4;
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_COP1_BCT = new Runnable() {
		@Override
		public void run() {
			if ((cop1.FPCR[Cop1.FSTATUS_REGISTER] & Cop1.FPCSR_C) != 0)
				jumpToLocation = pc + (((short) inst) << 2) + 4;
			else
				jumpToLocation = pc + 8;
			pc += 4;
			inDelaySlot = true;
			if (!executeInstruction())
				return;
			jumpTo(jumpToLocation);
		}
	};

	public Runnable r4300i_COP1_BCFL = new Runnable() {
		@Override
		public void run() {
			if ((cop1.FPCR[Cop1.FSTATUS_REGISTER] & Cop1.FPCSR_C) == 0) {
				jumpToLocation = pc + (((short) inst) << 2) + 4;
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	public Runnable r4300i_COP1_BCTL = new Runnable() {
		@Override
		public void run() {
			if ((cop1.FPCR[Cop1.FSTATUS_REGISTER] & Cop1.FPCSR_C) != 0) {
				jumpToLocation = pc + (((short) inst) << 2) + 4;
				pc += 4;
				inDelaySlot = true;
				if (!executeInstruction())
					return;
				jumpTo(jumpToLocation);
			} else {
				pc += 4;
			}
		}
	};

	/************************** COP0 functions **************************/
	public Runnable r4300i_COP0_MF = new Runnable() {
		@Override
		public void run() {
			GPR[(inst >> RT) & 0x1F] = cop0.mfC0((inst >> RD) & 0x1F);
		}
	};

	public Runnable r4300i_COP0_MT = new Runnable() {
		@Override
		public void run() {
			cop0.mtC0((inst >> RD) & 0x1F, (int) GPR[(inst >> RT) & 0x1F]);
			if (((inst >> RD) & 0x1F) == Cop0.STATUS_REGISTER) {
				cop1.setMode32((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_FR) == 0);
				CheckInterrupts.run();
			}
		}
	};

	public Runnable r4300i_COP0_CO_ERET = new Runnable() {
		@Override
		public void run() {
			if ((cop0.CP0[Cop0.STATUS_REGISTER] & Cop0.STATUS_ERL) != 0) {
				jumpToLocation = cop0.CP0[Cop0.ERROREPC_REGISTER];
				cop0.CP0[Cop0.STATUS_REGISTER] &= ~Cop0.STATUS_ERL;
			} else {
				jumpToLocation = cop0.CP0[Cop0.EPC_REGISTER];
				cop0.CP0[Cop0.STATUS_REGISTER] &= ~Cop0.STATUS_EXL;
			}
			llBit = 0;
			CheckInterrupts.run();
			jumpTo(jumpToLocation);
		}
	};

	/************************** Other functions **************************/
	public Runnable R4300i_UnknownOpcode = new Runnable() {
		@Override
		public void run() {
			System.err.printf("PC:%X ,Unhandled r4300i OpCode:%X\n", pc, inst);

		}
	};

}
