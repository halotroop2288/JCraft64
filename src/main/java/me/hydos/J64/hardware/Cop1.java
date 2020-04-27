package me.hydos.J64.hardware;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Cop1 {

	public static final int REVISION_REGISTER = 0;
	public static final int FSTATUS_REGISTER = 31;

	public static final int FPCSR_C = 0x00800000; /* condition bit */

	public class MipsDword {

		public long DW;

		public int getW(int index) {
			return switch (mode32 ? index & 1 : 0) {
				case 0 -> (int) DW;
				case 1 -> (int) (DW >> 32);
				default -> 0;
			};
		}

		public void setW(int index, int w) {
			switch (mode32 ? index & 1 : 0) {
			case 0:
				DW = ((((long) w)) & 0x00000000FFFFFFFFL) | (DW & 0xFFFFFFFF00000000L);
				break;
			case 1:
				DW = ((((long) w) << 32) & 0xFFFFFFFF00000000L) | (DW & 0x00000000FFFFFFFFL);
				break;
			default:
			}
		}

		public float getF() {
			return Float.intBitsToFloat((int) DW);
		}

		public void setF(float f) {
			DW = (((long) Float.floatToIntBits(f))) & 0x00000000FFFFFFFFL;
		}

		public double getD() {
			return Double.longBitsToDouble(DW);
		}

		public void setD(double d) {
			DW = Double.doubleToLongBits(d);
		}

	}

	public int[] FPCR = new int[32];

	public MipsDword[] FPR = new MipsDword[32];

	private final MipsDword[] tmpFPR = new MipsDword[16];

	public boolean mode32;

	public int fmt;
	public int ft;
	public int fs;
	public int fd;
	public int funct;

	private static final int RC_NEAR = 0;
	private static final int RC_CHOP = 1;
	private static final int RC_UP = 2;
	private static final int RC_DOWN = 3;

	private int roundingModel = RC_NEAR;
	private final RoundingMode[] roundingMode = { RoundingMode.HALF_EVEN, RoundingMode.DOWN, RoundingMode.CEILING,
			RoundingMode.FLOOR };
	private final MathContext[] floatContext = { MathContext.DECIMAL32, new MathContext(7, RoundingMode.DOWN),
			new MathContext(7, RoundingMode.CEILING), new MathContext(7, RoundingMode.FLOOR) };

	/** Creates a new instance of Cop1 */
	public Cop1() {
		for (int i = 0; i < FPR.length; i++)
			FPR[i] = new MipsDword();
		FPCR[REVISION_REGISTER] = 0x00000511;
	}

	public void setMode32(boolean mode32) {
		if (!this.mode32 && mode32) {
			this.mode32 = true;
			for (int i = 0; i < 16; i++) {
				tmpFPR[i] = FPR[(i << 1) + 1];
				FPR[(i << 1) + 1] = FPR[i << 1];
			}
		} else if (this.mode32 && !mode32) {
			this.mode32 = false;
			for (int i = 0; i < 16; i++) {
				FPR[(i << 1) + 1] = tmpFPR[i];
			}
		}
	}

	// called by Cpu
	private void setRoundingModel(int fs) {
		switch (FPCR[fs] & 3) {
			case 0 -> roundingModel = RC_NEAR;
			case 1 -> roundingModel = RC_CHOP;
			case 2 -> roundingModel = RC_UP;
			case 3 -> roundingModel = RC_DOWN;
		}
	}

	public void lwC1(int ft, int value) {
		FPR[ft].setW(ft, value);
	}

	public int swC1(int ft) {
		return FPR[ft].getW(ft);
	}

	public void ldC1(int ft, long value) {
		FPR[ft].DW = value;
	}

	public long sdC1(int ft) {
		return FPR[ft].DW;
	}

	public int mfC1(int fs) {
		return FPR[fs].getW(fs);
	}

	public void mtC1(int fs, int value) {
		FPR[fs].setW(fs, value);
	}

	public long dmfC1(int fs) {
		return FPR[fs].DW;
	}

	public void dmtC1(int fs, long value) {
		FPR[fs].DW = value;
	}

	public void ctC1(int fs, int value) {
		if (fs != 31) {
			System.err.print("CTC1 what register are you writing to ?\n");
			return;
		}
		FPCR[fs] = value;
		setRoundingModel(fs);
	}

	public int cfC1(int fs) {
		if (fs != 31 && fs != 0) {
			System.err.print("CFC1 what register are you reading from ?\n");
			return 0;
		}
		return FPCR[fs];
	}

	/************************** COP1: S functions ************************/
	public Runnable r4300i_COP1_S_ADD = new Runnable() {
		public void run() {
			FPR[fd].setF(FPR[fs].getF() + FPR[ft].getF());
		}
	};

	public Runnable r4300i_COP1_S_SUB = new Runnable() {
		public void run() {
			FPR[fd].setF(FPR[fs].getF() - FPR[ft].getF());
		}
	};

	public Runnable r4300i_COP1_S_MUL = new Runnable() {
		public void run() {
			FPR[fd].setF(FPR[fs].getF() * FPR[ft].getF());
		}
	};

	public Runnable r4300i_COP1_S_DIV = new Runnable() {
		public void run() {
			FPR[fd].setF(FPR[fs].getF() / FPR[ft].getF());
		}
	};

	public Runnable r4300i_COP1_S_SQRT = new Runnable() {
		public void run() {
			FPR[fd].setF(BigDecimal.valueOf(StrictMath.sqrt(FPR[fs].getF())).round(floatContext[roundingModel])
					.floatValue());
		}
	};

	public Runnable r4300i_COP1_S_ABS = new Runnable() {
		public void run() {
			FPR[fd].setF(StrictMath.abs(FPR[fs].getF()));
		}
	};

	public Runnable r4300i_COP1_S_MOV = new Runnable() {
		public void run() {
			FPR[fd].setF(FPR[fs].getF());
		}
	};

	public Runnable r4300i_COP1_S_NEG = new Runnable() {
		public void run() {
			FPR[fd].setF(BigDecimal.valueOf(FPR[fs].getF()).negate(floatContext[roundingModel]).floatValue());
		}
	};

	public Runnable r4300i_COP1_S_TRUNC_L = new Runnable() {
		public void run() {
			FPR[fd].DW = (long) (double) FPR[fs].getF();
		}
	};

	public Runnable r4300i_COP1_S_TRUNC_W = new Runnable() {
		public void run() {
			FPR[fd].setW(fd, (int) FPR[fs].getF());
		}
	};

	public Runnable r4300i_COP1_S_FLOOR_W = new Runnable() {
		public void run() {
			FPR[fd].setW(fd, (int) StrictMath.floor(FPR[fs].getF()));
		}
	};

	public Runnable r4300i_COP1_S_CVT_D = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getF());
		}
	};

	public Runnable r4300i_COP1_S_CVT_W = new Runnable() {
		public void run() {
			FPR[fd].setW(fd, BigDecimal.valueOf(FPR[fs].getF()).setScale(0, roundingMode[roundingModel]).intValue());
		}
	};

	public Runnable r4300i_COP1_S_CVT_L = new Runnable() {
		public void run() {
			FPR[fd].DW = BigDecimal.valueOf(FPR[fs].getF()).setScale(0, roundingMode[roundingModel]).longValue();
		}
	};

	public Runnable r4300i_COP1_S_CMP = new Runnable() {
		public void run() {
			boolean less;
			boolean equal;
			boolean unorded;
			float temp0 = FPR[fs].getF();
			float temp1 = FPR[ft].getF();

			if (Float.isNaN(temp0) || Float.isNaN(temp1)) {
				System.err.print("Nan ?\n");
				less = false;
				equal = false;
				unorded = true;
				if ((funct & 8) != 0) {
					System.err.printf("Signal InvalidOperationException\nin r4300i_COP1_S_CMP\n%X  %ff\n%X  %ff\n", temp0, temp0, temp1, temp1);
				}
			} else {
				less = temp0 < temp1;
				equal = temp0 == temp1;
				unorded = false;
			}

			@SuppressWarnings("unused")
			boolean condition = setCondition(less, equal, unorded);
		}
	};

	private boolean setCondition(boolean less, boolean equal, boolean unorded) {
		boolean condition = (((funct & 4) != 0) && less) | (((funct & 2) != 0) && equal) | (((funct & 1) != 0) && unorded);

		if (condition) {
			FPCR[FSTATUS_REGISTER] |= FPCSR_C;
		} else {
			FPCR[FSTATUS_REGISTER] &= ~FPCSR_C;
		}
		return condition;
	}

	/************************** COP1: D functions ************************/
	public Runnable r4300i_COP1_D_ADD = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD() + FPR[ft].getD());
		}
	};

	public Runnable r4300i_COP1_D_SUB = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD() - FPR[ft].getD());
		}
	};

	public Runnable r4300i_COP1_D_MUL = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD() * FPR[ft].getD());
		}
	};

	public Runnable r4300i_COP1_D_DIV = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD() / FPR[ft].getD());
		}
	};

	public Runnable r4300i_COP1_D_SQRT = new Runnable() {
		public void run() {
			FPR[fd].setD(StrictMath.sqrt(FPR[fs].getD()));
		}
	};

	public Runnable r4300i_COP1_D_ABS = new Runnable() {
		public void run() {
			FPR[fd].setD(StrictMath.abs(FPR[fs].getD()));
		}
	};

	public Runnable r4300i_COP1_D_MOV = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD());
		}
	};

	public Runnable r4300i_COP1_D_NEG = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getD() * -1.0);
		}
	};

	public Runnable r4300i_COP1_D_ROUND_W = new Runnable() { //TODO: someone tell hydos to finish this
		public void run() {
		}
	};

	public Runnable r4300i_COP1_D_TRUNC_W = new Runnable() {
		public void run() {
			FPR[fd].setW(fd, (int) FPR[fs].getD());
		}
	};

	public Runnable r4300i_COP1_D_CVT_S = new Runnable() {
		public void run() {
			FPR[fd].setF((float) FPR[fs].getD());
		}
	};

	public Runnable r4300i_COP1_D_CVT_W = new Runnable() {
		public void run() {
			FPR[fd].setW(fd, BigDecimal.valueOf(FPR[fs].getD()).setScale(0, roundingMode[roundingModel]).intValue());
		}
	};

	public Runnable r4300i_COP1_D_CVT_L = new Runnable() {//TODO: someone tell hydos to finish this
		public void run() {
		}
	};

	public Runnable r4300i_COP1_D_CMP = new Runnable() {
		public void run() {
			boolean less;
			boolean equal;
			boolean unorded;
			double temp0 = FPR[fs].getD();
			double temp1 = FPR[ft].getD();

			if (Double.isNaN(temp0) || Double.isNaN(temp1)) {
				System.err.print("Nan ?\n");
				less = false;
				equal = false;
				unorded = true;
				if ((funct & 8) != 0) {
					System.err.print("Signal InvalidOperationException\nin r4300i_COP1_D_CMP\n");
				}
			} else {
				less = temp0 < temp1;
				equal = temp0 == temp1;
				unorded = false;
			}

			setCondition(less, equal, unorded);
		}
	};

	/************************** COP1: W functions ************************/
	public Runnable r4300i_COP1_W_CVT_S = new Runnable() {
		public void run() {
			FPR[fd].setF((float) FPR[fs].getW(fs));
		}
	};

	public Runnable r4300i_COP1_W_CVT_D = new Runnable() {
		public void run() {
			FPR[fd].setD(FPR[fs].getW(fs));
		}
	};

	/************************** COP1: L functions ************************/
	public Runnable r4300i_COP1_L_CVT_S = new Runnable() {
		public void run() {
			FPR[fd].setF(BigDecimal.valueOf(FPR[fs].DW).round(floatContext[roundingModel]).floatValue());
		}
	};

	public Runnable r4300i_COP1_L_CVT_D = new Runnable() {//TODO: someone tell hydos to finish this
		public void run() {
		}
	};
}
