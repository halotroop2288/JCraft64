package gln64j;


public class Registers {

    public static final int MI_INTR_DP = 0x20; // Bit 5: DP intr

    public int MI_INTR;
    public int[] MI_Registers;

    public int DPC_START;
    public int DPC_END;
    public int DPC_CURRENT;
    public int DPC_STATUS;
    public int DPC_CLOCK;
    public int DPC_BUFBUSY;
    public int DPC_PIPEBUSY;
    public int DPC_TMEM;
    public int[] DPC_Registers;

    public int VI_STATUS;
    public int VI_ORIGIN;
    public int VI_WIDTH;
    public int VI_INTR;
    public int VI_V_CURRENT_LINE;
    public int VI_TIMING;
    public int VI_V_SYNC;
    public int VI_H_SYNC;
    public int VI_LEAP;
    public int VI_H_START;
    public int VI_V_START;
    public int VI_V_BURST;
    public int VI_X_SCALE;
    public int VI_Y_SCALE;
    public int[] VI_Registers;

    /** Creates a new instance of N64 */
    public Registers() {
    }

}
