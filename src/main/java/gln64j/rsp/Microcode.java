package gln64j.rsp;

public class Microcode {

    public static final int F3D = 0;
    public static final int F3DEX = 1;
    public static final int F3DEX2 = 2;
    public static final int L3D = 3;
    public static final int L3DEX = 4;
    public static final int L3DEX2 = 5;
    public static final int S2DEX = 6;
    public static final int S2DEX2 = 7;
    public static final int F3DPD = 8;
    public static final int F3DDKR = 9;
    public static final int F3DWRUS = 10;
    public static final int F3DEXBG = 11;
    public static final int NONE = 12;

    public static final String[] MicrocodeTypes = {
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
            "None"
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
        this.NoN = NoN;
        this.crc = crc;
        this.text = text;
    }

}
