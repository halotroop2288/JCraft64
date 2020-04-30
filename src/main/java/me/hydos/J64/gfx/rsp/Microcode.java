package me.hydos.J64.gfx.rsp;

public class Microcode {

    public static final int NONE = 0;
    public static final int F3D = 1;
    public static final int F3DEX = 2;
    public static final int F3DEX2 = 3;
    public static final int L3D = 4;
    public static final int L3DEX = 5;
    public static final int L3DEX2 = 6;
    public static final int S2DEX = 7;
    public static final int S2DEX2 = 8;
    public static final int F3DPD = 9;
    public static final int F3DDKR = 10;
    public static final int F3DWRUS = 11;
    public static final int F3DEXBG = 12;
	public static final int F5ROGUE = 13;
	public static final int F3DEX2ACCLAIM = 14;
	public static final int F3DEX2CBFD = 15;
	public static final int F5INDI_NABOO = 16;
	public static final int F3DBETA = 17;
	public static final int S2DEX_1_03 = 18;
	public static final int S2DEX_1_05 = 19;
	public static final int S2DEX_1_07 = 20;
	public static final int ZSORTBOSS = 21;
	public static final int T3DUX = 22;
	public static final int F3DJFG = 23;
	public static final int F3DZEX2MM = 24;
	public static final int TURBO3D = 25;
	public static final int F3DSETA = 26;
	public static final int F3DGOLDEN = 27;
	public static final int F3DFLX2 = 28;
	public static final int F3DAM = 29;
	public static final int ZSORTP = 30;
	public static final int F3DZEX2OOT = 31;
	public static final int F3DTEXA = 32;
    
    public static final String[] MicrocodeTypes = {
		"None",
        "Fast3D",
        "F3DEX",
        "F3DEX2",
        "Line3D",
        "L3DEX",
        "L3DEX2",
        "S2DEX",
        "S2DEX2",
        "Perfect Dark",
        "DKR/JFG",
        "Waverace US",
        "F3DEXBG",
        "F5Rogue",
        "F3DEX2ACCLAIM",
        "F3DEX2CBFD",
        "F5Indi_Naboo",
        "F3DBETA",
        "S2DEX_1_03",
        "S2DEX_1_05",
        "S2DEX_1_07",
        "ZSortBOSS",
        "T3DUX",
        "F3DJFG",
        "F3DZEX2MM",
        "Turbo3D",
        "F3DSETA",
        "F3DGOLDEN",
        "F3DFLX2",
        "F3DAM",
        "F3DZEX2OOT",
        "F3DTEXA"
    };

    public int address;
    public int dataAddress;
    public short dataSize;
    public int type;
    public boolean NoN;
    public int crc;
    public String text;
    public Microcode higher;
    public Microcode lower;

    /**
     * Creates a new instance of Microcode
     */
    public Microcode() {
    }

    public Microcode(int type, boolean NoN, int crc, String text) {
        this.type = type;
        this.NoN = NoN; // ucode does not use near clipping
        this.crc = crc;
        this.text = text;
    }

}
