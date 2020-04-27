package me.hydos.J64.debug;

import me.hydos.J64.N64Cpu;

public class R4300iCmd {
	private static int op; // Cpu
	private static int rs; // Cpu
	private static int fmt; // Cop1
	private static int rt; // Cpu
	private static int ft; // Cop1
	private static int funct; // Cpu, Cop1

	/** Creates a new instance of R4300iCommands */
	public R4300iCmd() {
	}

	// called by Main
	public static String R4300iOpcodeName(int opCode, int pc) {
		// Opcode command = new Opcode();
		// command.setHex(opCode);
		setHex(opCode);

		switch (op) {
		case N64Cpu.R4300i_SPECIAL:
			return R4300iSpecialName(opCode, pc);
		case N64Cpu.R4300i_REGIMM:
			return R4300iRegImmName(opCode, pc);
		case N64Cpu.R4300i_J:
			return "J";
		case N64Cpu.R4300i_JAL:
			return "JAL";
		case N64Cpu.R4300i_BEQ:
			return "BEQ";
		case N64Cpu.R4300i_BNE:
			return "BNE";
		case N64Cpu.R4300i_BLEZ:
			return "BLEZ";
		case N64Cpu.R4300i_BGTZ:
			return "BGTZ";
		case N64Cpu.R4300i_ADDI:
			return "ADDI";
		case N64Cpu.R4300i_ADDIU:
			return "ADDIU";
		case N64Cpu.R4300i_SLTI:
			return "SLTI";
		case N64Cpu.R4300i_SLTIU:
			return "SLTIU";
		case N64Cpu.R4300i_ANDI:
			return "ANDI";
		case N64Cpu.R4300i_ORI:
			return "ORI";
		case N64Cpu.R4300i_XORI:
			return "XORI";
		case N64Cpu.R4300i_LUI:
			return "LUI";
		case N64Cpu.R4300i_CP0:
			switch (rs) {
			case N64Cpu.R4300i_COP0_MF:
				return "COP0_MF";
			case N64Cpu.R4300i_COP0_MT:
				return "COP0_MT";
			default:
				if ((rs & 0x10) != 0) {
					switch (funct) {
					case N64Cpu.R4300i_COP0_CO_TLBR:
						return "COP0_C0_TLBR";
					case N64Cpu.R4300i_COP0_CO_TLBWI:
						return "COP0_C0_TLBWI";
					case N64Cpu.R4300i_COP0_CO_TLBWR:
						return "COP0_C0_TLBWR";
					case N64Cpu.R4300i_COP0_CO_TLBP:
						return "COP0_C0_TLBP";
					case N64Cpu.R4300i_COP0_CO_ERET:
						return "COP0_C0_ERET";
					default:
						return "Unknown COP0_C0: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode)
								+ ")";
					}
				} else {
					return "Unknown COP0: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
				}
			}
		case N64Cpu.R4300i_CP1:
			return R4300iCop1Name(opCode, pc);
		case N64Cpu.R4300i_BEQL:
			return "BEQL";
		case N64Cpu.R4300i_BNEL:
			return "BNEL";
		case N64Cpu.R4300i_BLEZL:
			return "BLEZL";
		case N64Cpu.R4300i_BGTZL:
			return "BGTZL";
		case N64Cpu.R4300i_DADDI:
			return "DADDI";
		case N64Cpu.R4300i_DADDIU:
			return "DADDIU";
		case N64Cpu.R4300i_LDL:
			return "LDL";
		case N64Cpu.R4300i_LDR:
			return "LDR";
		case N64Cpu.R4300i_LB:
			return "LB";
		case N64Cpu.R4300i_LH:
			return "LH";
		case N64Cpu.R4300i_LWL:
			return "LWL";
		case N64Cpu.R4300i_LW:
			return "LW";
		case N64Cpu.R4300i_LBU:
			return "LBU";
		case N64Cpu.R4300i_LHU:
			return "LHU";
		case N64Cpu.R4300i_LWR:
			return "LWR";
		case N64Cpu.R4300i_LWU:
			return "LWU";
		case N64Cpu.R4300i_SB:
			return "SB";
		case N64Cpu.R4300i_SH:
			return "SH";
		case N64Cpu.R4300i_SWL:
			return "SWL";
		case N64Cpu.R4300i_SW:
			return "SW";
		case N64Cpu.R4300i_SDL:
			return "SDL";
		case N64Cpu.R4300i_SDR:
			return "SDR";
		case N64Cpu.R4300i_SWR:
			return "SWR";
		case N64Cpu.R4300i_CACHE:
			return "CACHE";
		case N64Cpu.R4300i_LL:
			return "LL";
		case N64Cpu.R4300i_LWC1:
			return "LWC1";
		case N64Cpu.R4300i_LDC1:
			return "LDC1";
		case N64Cpu.R4300i_LD:
			return "LD";
		case N64Cpu.R4300i_SC:
			return "SC";
		case N64Cpu.R4300i_SWC1:
			return "SWC1";
		case N64Cpu.R4300i_SDC1:
			return "SDC1";
		case N64Cpu.R4300i_SD:
			return "SD";
		default:
			return "Unknown: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
		}
	}

	private static String R4300iRegImmName(int opCode, int PC) {
		// Opcode command = new Opcode();
		// command.setHex(opCode);

		switch (rt) {
		case N64Cpu.R4300i_REGIMM_BLTZ:
			return "REGIMM_BLTZ";
		case N64Cpu.R4300i_REGIMM_BGEZ:
			return "REGIMM_BGEZ";
		case N64Cpu.R4300i_REGIMM_BLTZL:
			return "REGIMM_BLTZL";
		case N64Cpu.R4300i_REGIMM_BGEZL:
			return "REGIMM_BGEZL";
		case N64Cpu.R4300i_REGIMM_TGEI:
			return "REGIMM_TGEI";
		case N64Cpu.R4300i_REGIMM_TGEIU:
			return "REGIMM_TGEIU";
		case N64Cpu.R4300i_REGIMM_TLTI:
			return "REGIMM_TLTI";
		case N64Cpu.R4300i_REGIMM_TLTIU:
			return "REGIMM_TLTIU";
		case N64Cpu.R4300i_REGIMM_TEQI:
			return "REGIMM_TEQI";
		case N64Cpu.R4300i_REGIMM_TNEI:
			return "REGIMM_TNEI";
		case N64Cpu.R4300i_REGIMM_BLTZAL:
			return "REGIMM_BLTZAL";
		case N64Cpu.R4300i_REGIMM_BGEZAL:
			return "REGIMM_ BGEZAL";
		case N64Cpu.R4300i_REGIMM_BLTZALL:
			return "REGIMM_BLTZALL";
		case N64Cpu.R4300i_REGIMM_BGEZALL:
			return "REGIMM_BGEZALL";
		default:
			return "Unknown REGIMM: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
		}
	}

	private static String R4300iSpecialName(int opCode, int PC) {
		// Opcode command = new Opcode();
		// command.setHex(opCode);

		switch (funct) {
		case N64Cpu.R4300i_SPECIAL_SLL:
			if (opCode != 0) {
				return "SPECIAL_SLL";
			} else {
				return "SPECIAL_NOP";
			}
		case N64Cpu.R4300i_SPECIAL_SRL:
			return "SPECIAL_SRL";
		case N64Cpu.R4300i_SPECIAL_SRA:
			return "SPECIAL_SRA";
		case N64Cpu.R4300i_SPECIAL_SLLV:
			return "SPECIAL_SLLV";
		case N64Cpu.R4300i_SPECIAL_SRLV:
			return "SPECIAL_SRLV";
		case N64Cpu.R4300i_SPECIAL_SRAV:
			return "SPECIAL_SRAV";
		case N64Cpu.R4300i_SPECIAL_JR:
			return "SPECIAL_JR";
		case N64Cpu.R4300i_SPECIAL_JALR:
			return "SPECIAL_JALR";
		case N64Cpu.R4300i_SPECIAL_SYSCALL:
			return "SPECIAL_SYSCALL";
		case N64Cpu.R4300i_SPECIAL_BREAK:
			return "SPECIAL_BREAK";
		case N64Cpu.R4300i_SPECIAL_SYNC:
			return "SPECIAL_SYNC";
		case N64Cpu.R4300i_SPECIAL_MFHI:
			return "SPECIAL_MFHI";
		case N64Cpu.R4300i_SPECIAL_MTHI:
			return "SPECIAL_MTHI";
		case N64Cpu.R4300i_SPECIAL_MFLO:
			return "SPECIAL_MFLO";
		case N64Cpu.R4300i_SPECIAL_MTLO:
			return "SPECIAL_MTLO";
		case N64Cpu.R4300i_SPECIAL_DSLLV:
			return "SPECIAL_DSLLV";
		case N64Cpu.R4300i_SPECIAL_DSRLV:
			return "SPECIAL_DSRLV";
		case N64Cpu.R4300i_SPECIAL_DSRAV:
			return "SPECIAL_DSRAV";
		case N64Cpu.R4300i_SPECIAL_MULT:
			return "SPECIAL_MULT";
		case N64Cpu.R4300i_SPECIAL_MULTU:
			return "SPECIAL_MULTU";
		case N64Cpu.R4300i_SPECIAL_DIV:
			return "SPECIAL_DIV";
		case N64Cpu.R4300i_SPECIAL_DIVU:
			return "SPECIAL_DIVU";
		case N64Cpu.R4300i_SPECIAL_DMULT:
			return "SPECIAL_DMULT";
		case N64Cpu.R4300i_SPECIAL_DMULTU:
			return "SPECIAL_DMULTU";
		case N64Cpu.R4300i_SPECIAL_DDIV:
			return "SPECIAL_DDIV";
		case N64Cpu.R4300i_SPECIAL_DDIVU:
			return "SPECIAL_DDIVU";
		case N64Cpu.R4300i_SPECIAL_ADD:
			return "SPECIAL_ADD";
		case N64Cpu.R4300i_SPECIAL_ADDU:
			return "SPECIAL_ADDU";
		case N64Cpu.R4300i_SPECIAL_SUB:
			return "SPECIAL_SUB";
		case N64Cpu.R4300i_SPECIAL_SUBU:
			return "SPECIAL_SUBU";
		case N64Cpu.R4300i_SPECIAL_AND:
			return "SPECIAL_AND";
		case N64Cpu.R4300i_SPECIAL_OR:
			return "SPECIAL_OR";
		case N64Cpu.R4300i_SPECIAL_XOR:
			return "SPECIAL_XOR";
		case N64Cpu.R4300i_SPECIAL_NOR:
			return "SPECIAL_NOR";
		case N64Cpu.R4300i_SPECIAL_SLT:
			return "SPECIAL_SLT";
		case N64Cpu.R4300i_SPECIAL_SLTU:
			return "SPECIAL_SLTU";
		case N64Cpu.R4300i_SPECIAL_DADD:
			return "SPECIAL_DADD";
		case N64Cpu.R4300i_SPECIAL_DADDU:
			return "SPECIAL_DADDU";
		case N64Cpu.R4300i_SPECIAL_DSUB:
			return "SPECIAL_DSUB";
		case N64Cpu.R4300i_SPECIAL_DSUBU:
			return "SPECIAL_DSUBU";
		case N64Cpu.R4300i_SPECIAL_TGE:
			return "SPECIAL_TGE";
		case N64Cpu.R4300i_SPECIAL_TGEU:
			return "SPECIAL_TGEU";
		case N64Cpu.R4300i_SPECIAL_TLT:
			return "SPECIAL_TLT";
		case N64Cpu.R4300i_SPECIAL_TLTU:
			return "SPECIAL_TLTU";
		case N64Cpu.R4300i_SPECIAL_TEQ:
			return "SPECIAL_TEQ";
		case N64Cpu.R4300i_SPECIAL_TNE:
			return "SPECIAL_TNE";
		case N64Cpu.R4300i_SPECIAL_DSLL:
			return "SPECIAL_DSLL";
		case N64Cpu.R4300i_SPECIAL_DSRL:
			return "SPECIAL_DSRL";
		case N64Cpu.R4300i_SPECIAL_DSRA:
			return "SPECIAL_DSRA";
		case N64Cpu.R4300i_SPECIAL_DSLL32:
			return "SPECIAL_DSLL32";
		case N64Cpu.R4300i_SPECIAL_DSRL32:
			return "SPECIAL_DSRL32";
		case N64Cpu.R4300i_SPECIAL_DSRA32:
			return "SPECIAL_DSRA32";
		default:
			return "Unknown SPECIAL: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
		}
	}

	private static String R4300iCop1Name(int opCode, int PC) {
		// Opcode command = new Opcode();
		// command.setHex(opCode);

		switch (fmt) {
		case N64Cpu.R4300i_COP1_MF:
			return "COP1_MF";
		case N64Cpu.R4300i_COP1_DMF:
			return "COP1_DMF";
		case N64Cpu.R4300i_COP1_CF:
			return "COP1_CF";
		case N64Cpu.R4300i_COP1_MT:
			return "COP1_MT";
		case N64Cpu.R4300i_COP1_DMT:
			return "COP1_DMT";
		case N64Cpu.R4300i_COP1_CT:
			return "COP1_CT";
		case N64Cpu.R4300i_COP1_BC:
			switch (ft) {
			case N64Cpu.R4300i_COP1_BC_BCF:
				return "COP1_BC_BCF";
			case N64Cpu.R4300i_COP1_BC_BCT:
				return "COP1_BC_BCT";
			case N64Cpu.R4300i_COP1_BC_BCFL:
				return "COP1_BC_BCFL";
			case N64Cpu.R4300i_COP1_BC_BCTL:
				return "COP1_BC_BCTL";
			default:
				return "Unknown COP1_BC: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
			}
		case N64Cpu.R4300i_COP1_S:
		case N64Cpu.R4300i_COP1_D:
		case N64Cpu.R4300i_COP1_W:
		case N64Cpu.R4300i_COP1_L:
			switch (funct) {
			case N64Cpu.R4300i_COP1_FUNCT_ADD:
				return "COP1_FUNCT_ADD." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_SUB:
				return "COP1_FUNCT_SUB." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_MUL:
				return "COP1_FUNCT_MUL." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_DIV:
				return "COP1_FUNCT_DIV." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_SQRT:
				return "COP1_FUNCT_SQRT." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_ABS:
				return "COP1_FUNCT_ABS." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_MOV:
				return "COP1_FUNCT_MOV." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_NEG:
				return "COP1_FUNCT_NEG." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_ROUND_L:
				return "COP1_FUNCT_ROUND_L." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_TRUNC_L:
				return "COP1_FUNCT_TRUNC_L." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CEIL_L:
				return "COP1_FUNCT_CEIL_L." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_FLOOR_L:
				return "COP1_FUNCT_FLOOR_L." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_ROUND_W:
				return "COP1_FUNCT_ROUND_W." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_TRUNC_W:
				return "COP1_FUNCT_TRUNC_W." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CEIL_W:
				return "COP1_FUNCT_CEIL_W." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_FLOOR_W:
				return "COP1_FUNCT_FLOOR_W." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CVT_S:
				return "COP1_FUNCT_CVT_S." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CVT_D:
				return "COP1_FUNCT_CVT_D." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CVT_W:
				return "COP1_FUNCT_CVT_W." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_CVT_L:
				return "COP1_FUNCT_CVT_L." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_F:
				return "COP1_FUNCT_C_F." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_UN:
				return "COP1_FUNCT_C_UN." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_EQ:
				return "COP1_FUNCT_C_EQ." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_UEQ:
				return "COP1_FUNCT_C_UEQ." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_OLT:
				return "COP1_FUNCT_C_OLT." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_ULT:
				return "COP1_FUNCT_C_ULT." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_OLE:
				return "COP1_FUNCT_C_OLE." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_ULE:
				return "COP1_FUNCT_C_ULE." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_SF:
				return "COP1_FUNCT_C_SF." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_NGLE:
				return "COP1_FUNCT_C_NGLE." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_SEQ:
				return "COP1_FUNCT_C_SEQ." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_NGL:
				return "COP1_FUNCT_C_NGL." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_LT:
				return "COP1_FUNCT_C_LT." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_NGE:
				return "COP1_FUNCT_C_NGE." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_LE:
				return "COP1_FUNCT_C_LE." + FPR_Type(fmt);
			case N64Cpu.R4300i_COP1_FUNCT_C_NGT:
				return "COP1_FUNCT_C_NGT." + FPR_Type(fmt);
			default:
				return "Unknown COP1_FUNCT: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode)
						+ ")";
			}
		default:
			return "Unknown COP1: " + Integer.toHexString(opCode) + " (" + Integer.toBinaryString(opCode) + ")";
		}
	}

	private static char FPR_Type(int Reg) {
		return (Reg == N64Cpu.R4300i_COP1_S ? 'S'
				: Reg == N64Cpu.R4300i_COP1_D ? 'D' : Reg == N64Cpu.R4300i_COP1_W ? 'W' : 'L');
	}

	private static void setHex(int hex) {
		op = (hex >> 26) & 0x3F;
		rs = fmt = (hex >> 21) & 0x1F;
		rt = ft = (hex >> 16) & 0x1F;
		funct = (hex) & 0x3F;
	}

}
