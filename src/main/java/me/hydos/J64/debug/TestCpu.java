package me.hydos.J64.debug;

import static me.hydos.J64.hardware.Cop1.FSTATUS_REGISTER;

import me.hydos.J64.OpcodeBuilder;
import me.hydos.J64.N64Cpu;
import me.hydos.J64.Main;
import me.hydos.J64.hardware.Memory;
import me.hydos.J64.Registers;

public class TestCpu extends N64Cpu {

	public String getITypeDebug(String name, long rs, long rt, int immediate) {
		return String.format("%X:%s:%d:%d:%X %X:%X:%X ", pc, name, rs, rt, immediate, rs, rt, immediate);
	}

	public String getRTypeDebug(String name, long rs, long rt, long rd) {
		return String.format("%X:%s:%d:%d:%d:%d %X:%X:%X ", pc, name, rs, rt, rd, sa, rs, rt, rd);
	}

	/** Creates a new instance of InterpreterOps */
	public TestCpu(Registers regs, Memory mem) {
		super(regs, mem);
	}

	/**
	 * used to test the cpu state
	 */
	public void debugState() {
		System.out.print("CPU State:\n");
		System.out.printf("PC=%x\n", pc);
		for (int j = 0; j < 16; j++)
			System.out.printf("reg[%2d]:%8x%8x        reg[%d]:%8x%8x\n", j, (int) (GPR[j] >> 32), (int) (GPR[j]),
					j + 16, (int) (GPR[j + 16] >> 32), (int) (GPR[j + 16]));
		System.out.printf("hi:%8x%8x        lo:%8x%8x\n", (int) (HI >> 32), (int) (HI), (int) (LO >> 32), (int) (LO));
	}

	public Runnable r4300i_J_Debug = () -> r4300i_J.run();

	public Runnable r4300i_JAL_Debug = () -> r4300i_JAL.run();

	public Runnable r4300i_BEQ_Debug = () -> r4300i_BEQ.run();

	public Runnable r4300i_BNE_Debug = () -> r4300i_BNE.run();

	public Runnable r4300i_BLEZ_Debug = () -> r4300i_BLEZ.run();

	public Runnable r4300i_BGTZ_Debug = () -> r4300i_BGTZ.run();

	public Runnable r4300i_ADDI_Debug = () -> r4300i_ADDI.run();

	public Runnable r4300i_ADDIU_Debug = () -> r4300i_ADDIU.run();

	public Runnable r4300i_SLTI_Debug = () -> r4300i_SLTI.run();

	public Runnable r4300i_SLTIU_Debug = () -> r4300i_SLTIU.run();

	public Runnable r4300i_ANDI_Debug = () -> r4300i_ANDI.run();

	public Runnable r4300i_ORI_Debug = () -> r4300i_ORI.run();

	public Runnable r4300i_XORI_Debug = () -> r4300i_XORI.run();

	public Runnable r4300i_LUI_Debug = () -> r4300i_LUI.run();

	public Runnable r4300i_BEQL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BEQL", GPR[rs], GPR[rt], (offset << 2) + 4));

		r4300i_BEQL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_BNEL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BNEL", GPR[rs], GPR[rt], (offset << 2) + 4));

		r4300i_BNEL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_BLEZL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BLEZL", GPR[rs], 0, (offset << 2) + 4));

		r4300i_BLEZL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_BGTZL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BGTZL", GPR[rs], 0, (offset << 2) + 4));

		r4300i_BGTZL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_DADDIU_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getITypeDebug("DADDIU", GPR[rs], GPR[rt], offset));

		r4300i_DADDIU.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_LDL_Debug = () -> r4300i_LDL.run();

	public Runnable r4300i_LDR_Debug = () -> r4300i_LDR.run();

	public Runnable r4300i_LB_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LB", GPR[base], GPR[rt], offset));

		r4300i_LB.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LH_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LH", GPR[base], GPR[rt], offset));

		r4300i_LH.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LWL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LWL", GPR[base], GPR[rt], offset));

		r4300i_LWL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_LW_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LW", GPR[base], GPR[rt], offset));

		r4300i_LW.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LBU_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LBU", GPR[base], GPR[rt], offset));

		r4300i_LBU.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LHU_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LHU", GPR[base], GPR[rt], offset));

		r4300i_LHU.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LWR_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LWR", GPR[base], GPR[rt], offset));

		r4300i_LWR.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_SB_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SB", GPR[base], GPR[rt], offset));

		r4300i_SB.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset),
					(byte) (short) (((byte) (GPR[rt] & 0xFF)) & 0xFF));
	};

	public Runnable r4300i_SH_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SH", GPR[base], GPR[rt], offset));

		r4300i_SH.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset),
					(short) (((int) ((short) (GPR[rt] & 0xFFFF))) & 0xFFFF));
	};

	public Runnable r4300i_SWL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SWL", GPR[base], GPR[rt], offset));

		r4300i_SWL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
//                System.out.printf("VA=%X *=%X\n", (int)(GPR[base].getUW(0) + (short)offset) & ~0x03, tmpWord);
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset) & ~0x03, tmpWord);
	};

	public Runnable r4300i_SW_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SW", GPR[base], GPR[rt], offset));

		r4300i_SW.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
//                System.out.printf("VA=%X *=%X\n", (int)(GPR[base].getUW(0) + (short)offset), (int)GPR[rt].getUW(0));
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset),
					(int) (GPR[rt] & 0x00000000FFFFFFFFL));
	};

	public Runnable r4300i_SDL_Debug = () -> r4300i_SDL.run();

	public Runnable r4300i_SDR_Debug = () -> r4300i_SDR.run();

	public Runnable r4300i_SWR_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SWR", GPR[base], GPR[rt], offset));

		r4300i_SWR.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset) & ~0x03, tmpWord);
	};

	public Runnable r4300i_CACHE_Debug = () -> r4300i_CACHE.run();

	public Runnable r4300i_LWC1_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(
					getITypeDebug("LWC1", GPR[base], cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW, offset));

		r4300i_LWC1.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("ft=%X VA=%X\n", cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW,
					(int) ((GPR[base] & 0x00000000FFFFFFFFL) + (int) offset));
	};

	public Runnable r4300i_LD_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.print(getITypeDebug("LD", GPR[base], GPR[rt], offset));

		r4300i_LD.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES)
			System.out.printf("rt=%X VA=%X\n", GPR[rt], (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset));
	};

	public Runnable r4300i_LDC1_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(
					getITypeDebug("LDC1", GPR[base], cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW, offset));

		r4300i_LDC1.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_LD_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("ft=%X VA=%X\n", cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW,
					(int) ((GPR[base] & 0x00000000FFFFFFFFL) + (int) offset));
	};

	public Runnable r4300i_SWC1_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(
					getITypeDebug("SWC1", GPR[base], cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW, offset));

		r4300i_SWC1.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset),
					cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW);
	};

	public Runnable r4300i_SDC1_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(
					getITypeDebug("SDC1", GPR[base], cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW, offset));

		r4300i_SDC1.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset),
					cop1.FPR[cop1.mode32 ? rt >> 1 : rt].DW);
	};

	public Runnable r4300i_SD_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.print(getITypeDebug("SD", GPR[base], GPR[rt], offset));

		r4300i_SD.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_ST_OPCODES)
			System.out.printf("VA=%X *=%X\n", (int) ((GPR[base] & 0x00000000FFFFFFFFL) + offset), GPR[rt]);
	};

	/********************** R4300i OpCodes: Special **********************/
	public Runnable r4300i_SPECIAL_SLL_Debug = () -> r4300i_SPECIAL_SLL.run();

	public Runnable r4300i_SPECIAL_SRL_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SRL", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SRL.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SRA_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SRA", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SRA.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SLLV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SLLV", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SLLV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SRLV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SRLV", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SRLV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SRAV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SRAV", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SRAV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_JR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getRTypeDebug("JR", GPR[rs], 0, 0));

		r4300i_SPECIAL_JR.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_SPECIAL_JALR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getRTypeDebug("JALR", GPR[rs], 0, GPR[rd]));

		r4300i_SPECIAL_JALR.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X rd=%X\n", jumpToLocation, GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_MFHI_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MFHI", 0, 0, GPR[rd]));

		r4300i_SPECIAL_MFHI.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_MTHI_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MTHI", GPR[rs], 0, 0));

		r4300i_SPECIAL_MTHI.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("HI=%X\n", HI);
	};

	public Runnable r4300i_SPECIAL_MFLO_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MFL0", 0, 0, GPR[rd]));

		r4300i_SPECIAL_MFLO.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_MTLO_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MTL0", GPR[rs], 0, 0));

		r4300i_SPECIAL_MTLO.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X\n", LO);
	};

	public Runnable r4300i_SPECIAL_DSLLV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSLLV", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSLLV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DSRLV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSRLV", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSRLV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_MULT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MULT", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_MULT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_MULTU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("MULTU", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_MULTU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_DIV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DIV", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_DIV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_DIVU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DIVU", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_DIVU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_DMULTU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DMULTU", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_DMULTU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_DDIV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DDIV", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_DDIV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_DDIVU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DDIVU", GPR[rs], GPR[rt], 0));

		r4300i_SPECIAL_DDIVU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("LO=%X HI=%X\n", LO, HI);
	};

	public Runnable r4300i_SPECIAL_ADD_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("ADD", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_ADD.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_ADDU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("ADDU", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_ADDU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SUB_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SUB", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SUB.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SUBU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SUBU", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SUBU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_AND_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("AND", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_AND.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_OR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("OR", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_OR.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_XOR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("XOR", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_XOR.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_NOR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("NOR", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_NOR.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SLT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SLT", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SLT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_SLTU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("SLTU", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_SLTU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DADDU_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DADDU", GPR[rs], GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DADDU.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DSLL_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSLL", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSLL.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DSRL_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSRL", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSRL.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};


	public Runnable r4300i_SPECIAL_DSLL32_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSLL32", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSLL32.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DSRL32_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSRL32", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSRL32.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	public Runnable r4300i_SPECIAL_DSRA32_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.print(getRTypeDebug("DSRA32", 0, GPR[rt], GPR[rd]));

		r4300i_SPECIAL_DSRA32.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_OP_OPCODES)
			System.out.printf("rd=%X\n", GPR[rd]);
	};

	/********************** R4300i OpCodes: RegImm **********************/
	public Runnable r4300i_REGIMM_BLTZ_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BLTZ", GPR[rs], 0, (offset << 2) + 4));

		r4300i_REGIMM_BLTZ.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_REGIMM_BGEZ_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BGEZ", GPR[rs], 0, (offset << 2) + 4));

		r4300i_REGIMM_BGEZ.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_REGIMM_BLTZL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BLTZL", GPR[rs], 0, (offset << 2) + 4));

		r4300i_REGIMM_BLTZL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_REGIMM_BGEZL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BGEZL", GPR[rs], 0, (offset << 2) + 4));

		r4300i_REGIMM_BGEZL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_REGIMM_BLTZAL_Debug = () -> r4300i_REGIMM_BLTZAL.run();

	public Runnable r4300i_REGIMM_BGEZAL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.print(getITypeDebug("BGEZAL", GPR[rs], 0, (offset << 2) + 4));

		r4300i_REGIMM_BGEZAL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES)
			System.out.printf("JP=%X 31=%X\n", jumpToLocation, GPR[31]);
	};

	/************************** COP0 functions **************************/
	public Runnable r4300i_COP0_MF_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MFC0", 0, GPR[rt], cop0.CP0[rd] & 0xFFFFFFFFL));

		r4300i_COP0_MF.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_COP0_MT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MTC0", 0, GPR[rt], cop0.CP0[rd] & 0xFFFFFFFFL));

		r4300i_COP0_MT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("rd=%X\n", cop0.CP0[rd]);
	};

	/************************** COP0 CO functions ***********************/
	public Runnable r4300i_COP0_CO_TLBR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("%s.\n", getRTypeDebug("TLBR", 0, 0, 0));

		cop0.r4300i_COP0_CO_TLBR.run();
	};

	public Runnable r4300i_COP0_CO_TLBWI_Debug = () -> cop0.r4300i_COP0_CO_TLBWI.run();

	public Runnable r4300i_COP0_CO_TLBWR_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("%s.\n", getRTypeDebug("TLBWR", 0, 0, 0));

		cop0.r4300i_COP0_CO_TLBWR.run();
	};

	public Runnable r4300i_COP0_CO_TLBP_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("%s.\n", getRTypeDebug("TLBP", 0, 0, 0));

		cop0.r4300i_COP0_CO_TLBP.run();
	};

	public Runnable r4300i_COP0_CO_ERET_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("ERET", 0, 0, 0));

		r4300i_COP0_CO_ERET.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	/************************** COP1 functions **************************/
	public Runnable r4300i_COP1_MF_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MFC1", 0, GPR[rt], cop1.FPR[cop1.mode32 ? rd >> 1 : rd].DW));

		r4300i_COP1_MF.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_COP1_DMF_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("DMFC1", 0, GPR[rt], cop1.FPCR[rd]));

		r4300i_COP1_DMF.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_COP1_CF_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CFC1", 0, GPR[rt], cop1.FPCR[rd]));

		r4300i_COP1_CF.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("rt=%X\n", GPR[rt]);
	};

	public Runnable r4300i_COP1_MT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MTC1", 0, GPR[rt], cop1.FPR[cop1.mode32 ? rd >> 1 : rd].DW));

		r4300i_COP1_MT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fs=%X\n", cop1.FPR[cop1.mode32 ? rd >> 1 : rd].DW);
	};

	public Runnable r4300i_COP1_DMT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("DMTC1", 0, GPR[rt], cop1.FPCR[rd]));

		r4300i_COP1_DMT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fs=%X\n", cop1.FPR[cop1.mode32 ? rd >> 1 : rd].DW);
	};

	public Runnable r4300i_COP1_CT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CTC1", 0, GPR[rt], cop1.FPCR[rd]));

		r4300i_COP1_CT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fs=%X\n", cop1.FPCR[rd]);
	};

	/************************* COP1: BC1 functions ***********************/
	public Runnable r4300i_COP1_BCF_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getITypeDebug("BCF", 0, 0, (offset << 2) + 4));

		r4300i_COP1_BCF.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_COP1_BCT_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getITypeDebug("BCT", 0, 0, (offset << 2) + 4));

		r4300i_COP1_BCT.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_COP1_BCFL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getITypeDebug("BCFL", 0, 0, (offset << 2) + 4));

		r4300i_COP1_BCFL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	public Runnable r4300i_COP1_BCTL_Debug = () -> {
		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getITypeDebug("BCTL", 0, 0, (offset << 2) + 4));

		r4300i_COP1_BCTL.run();

		if (Debug.DEBUG_I_OPCODES || Debug.DEBUG_BR_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("JP=%X\n", jumpToLocation);
	};

	/************************** COP1: S functions ************************/
	public Runnable r4300i_COP1_S_ADD_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("ADD.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_ADD.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_SUB_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("SUB.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_SUB.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_MUL_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MUL.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_MUL.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_DIV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("DIV.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_DIV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_SQRT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("SQRT.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_SQRT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_ABS_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("ABS.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_ABS.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_MOV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MOV.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_MOV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_NEG_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("NEG.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_NEG.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_TRUNC_L_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("TRUNC.L.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_TRUNC_L.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_TRUNC_W_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("TRUNC.W.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_TRUNC_W.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_FLOOR_W_Debug = () -> cop1.r4300i_COP1_S_FLOOR_W.run();

	public Runnable r4300i_COP1_S_CVT_D_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.D.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_CVT_D.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_CVT_W_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.W.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_CVT_W.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_CVT_L_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.L.S", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_CVT_L.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_S_CMP_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CMP.S", 0, cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_S_CMP.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("31=%X\n", cop1.FPCR[FSTATUS_REGISTER]);
	};

	/************************** COP1: D functions ************************/
	public Runnable r4300i_COP1_D_ADD_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("ADD.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_ADD.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_SUB_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("SUB.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_SUB.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_MUL_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MUL.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_MUL.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_DIV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("DIV.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW,
					cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_DIV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_SQRT_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("SQRT.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_SQRT.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_ABS_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("ABS.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_ABS.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_MOV_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("MOV.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_MOV.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_NEG_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("NEG.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_NEG.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_TRUNC_W_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("TRUNC.W.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_TRUNC_W.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_CVT_S_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.S.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_CVT_S.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_CVT_W_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.W.D", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_CVT_W.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_D_CMP_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CMP.D", 0, cop1.FPR[cop1.mode32 ? cop1.ft >> 1 : cop1.ft].DW,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_D_CMP.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("31=%X\n", cop1.FPCR[FSTATUS_REGISTER]);
	};

	/************************** COP1: W functions ************************/
	public Runnable r4300i_COP1_W_CVT_S_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.S.W", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_W_CVT_S.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	public Runnable r4300i_COP1_W_CVT_D_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.D.W", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_W_CVT_D.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};

	/************************** COP1: L functions ************************/
	public Runnable r4300i_COP1_L_CVT_S_Debug = () -> {
		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.print(getRTypeDebug("CVT.S.L", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW, 0,
					cop1.FPR[cop1.mode32 ? cop1.fs >> 1 : cop1.fs].DW));

		cop1.r4300i_COP1_L_CVT_S.run();

		if (Debug.DEBUG_R_OPCODES || Debug.DEBUG_CP_OPCODES)
			System.out.printf("fd=%X\n", cop1.FPR[cop1.mode32 ? cop1.fd >> 1 : cop1.fd].DW);
	};


	/************************** Other functions **************************/
	public Runnable R4300i_UnknownOpcode_Debug = () -> {
//		String Message = String.format("Unhandled R4300i OpCode at: %08X\n%s\n%X\n", pc,
//				R4300iCmd.R4300iOpcodeName(currentInstr, pc), currentInstr);
//		Message += "Stoping Emulation !";
//		System.out.printf("-%X:Unhandled R4300i OpCode DEBUG:%X\n", pc, currentInstr);
//		System.err.printf("%s\n", Message);
	};
}
