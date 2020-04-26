package plugin;

import java.nio.ByteBuffer;

import javax.swing.JFrame;

public interface GfxPlugin {

	public static final int PLUGIN_TYPE_GFX = 2;

	// Video Interface (VI) Registers
	public static final int VI_STATUS_REG = 0;
	public static final int VI_ORIGIN_REG = 1;
	public static final int VI_WIDTH_REG = 2;
	public static final int VI_INTR_REG = 3;
	public static final int VI_CURRENT_REG = 4;
	public static final int VI_BURST_REG = 5;
	public static final int VI_V_SYNC_REG = 6;
	public static final int VI_H_SYNC_REG = 7;
	public static final int VI_LEAP_REG = 8;
	public static final int VI_H_START_REG = 9;
	public static final int VI_V_START_REG = 10;
	public static final int VI_V_BURST_REG = 11;
	public static final int VI_X_SCALE_REG = 12;
	public static final int VI_Y_SCALE_REG = 13;

	public static class PluginInfo {
		public int version; /* Set to 0x0103 */
		public int type; /* Set to PLUGIN_TYPE_GFX */
		public String name; /* name of the Plugin */

		/*
		 * If Plugin supports these memory options then set them to TRUE, or FALSE if it
		 * does not support it
		 */
		public boolean normalMemory; /* a normal BYTE array */
		public boolean memoryBswaped; /*
										 * a normal BYTE array where the memory has been pre bswap on a dword (32 bits)
										 * boundry
										 */
	}

	public static class GfxInfo {
		public JFrame hWnd; /* Render window */

		public boolean memoryBswaped; // If this is set to TRUE, then the memory has been pre
		// bswap on a dword (32 bits) boundry
		// eg. the first 8 bytes are stored like this:
		// 4 3 2 1 8 7 6 5

		public ByteBuffer header; // This is the rom header (first 40h bytes of the rom
		// This will be in the same memory format as the rest of the memory.
		public ByteBuffer rdram;
		public ByteBuffer dmem;
		public ByteBuffer imem;

		public int MI_INTR_REG;
		public int[] miRegisters;

		public int DPC_START_REG;
		public int DPC_END_REG;
		public int DPC_CURRENT_REG;
		public int DPC_STATUS_REG;
		public int DPC_CLOCK_REG;
		public int DPC_BUFBUSY_REG;
		public int DPC_PIPEBUSY_REG;
		public int DPC_TMEM_REG;
		public int[] dpcRegisters;

//        public int VI_STATUS_REG;
//        public int VI_ORIGIN_REG;
//        public int VI_WIDTH_REG;
//        public int VI_INTR_REG;
//        public int VI_V_CURRENT_LINE_REG;
//        public int VI_TIMING_REG;
//        public int VI_V_SYNC_REG;
//        public int VI_H_SYNC_REG;
//        public int VI_LEAP_REG;
//        public int VI_H_START_REG;
//        public int VI_V_START_REG;
//        public int VI_V_BURST_REG;
//        public int VI_X_SCALE_REG;
//        public int VI_Y_SCALE_REG;
		public int[] viRegisters;

		public Runnable checkInterrupts;
	}

	/**
	 * Function: captureScreen Purpose: This function dumps the current frame to a
	 * file input: pointer to the directory to save the file to output: none
	 */
	public void captureScreen(String directory);

	/**
	 * Function: changeWindow Purpose: to change the window between fullscreen and
	 * window mode. If the window was in fullscreen this should change the screen to
	 * window mode and vice vesa. input: none output: none
	 */
	public void changeWindow();

	/**
	 * Function: closePlugin Purpose: This function is called when the emulator is
	 * closing down allowing the Plugin to de-initialise. input: none output: none
	 */
	public void closePlugin();

	/**
	 * Function: pluginAbout Purpose: This function is optional function that is
	 * provided to give further information about the Plugin. input: a handle to the
	 * window that calls this function output: none
	 */
	public void pluginAbout(JFrame hParent);

	/**
	 * Function: pluginConfig Purpose: This function is optional function that is
	 * provided to allow the user to configure the Plugin input: a handle to the
	 * window that calls this function output: none
	 */
	public void pluginConfig(JFrame hParent);

	/**
	 * Function: pluginTest Purpose: This function is optional function that is
	 * provided to allow the user to test the Plugin input: a handle to the window
	 * that calls this function output: none
	 */
	public void pluginTest(JFrame hParent);

	/**
	 * Function: drawScreen Purpose: This function is called when the emulator
	 * receives a WM_PAINT message. This allows the gfx to fit in when it is being
	 * used in the desktop. input: none output: none
	 */
	public void drawScreen();

	/**
	 * Function: getPluginInfo Purpose: This function allows the emulator to gather
	 * information about the Plugin by filling in the pluginInfo structure. input: a
	 * pointer to a pluginInfo stucture that needs to be filled by the function.
	 * (see def above) output: none
	 */
	public void getPluginInfo(PluginInfo pluginInfo);

	/**
	 * Function: initiateGfx Purpose: This function is called when the Plugin is
	 * started to give information from the emulator that the n64 graphics uses.
	 * This is not called from the emulation thread. Input: gfxInfo is passed to
	 * this function which is defined above. Output: TRUE on success FALSE on
	 * failure to initialise note on interrupts **: To generate an interrupt set the
	 * appropriate bit in MI_INTR_REG and then call the function checkInterrupts to
	 * tell the emulator that there is a waiting interrupt.
	 */
	public boolean initiateGfx(GfxInfo gfxInfo);

	/**
	 * Function: moveScreen Purpose: This function is called in response to the
	 * emulator receiving a WM_MOVE passing the xpos and ypos passed from that
	 * message. input: xpos - the x-coordinate of the upper-left corner of the
	 * client area of the window. ypos - y-coordinate of the upper-left corner of
	 * the client area of the window. output: none
	 */
	public void moveScreen(int xpos, int ypos);

//    /**
//     * Function: getProcessDList
//     * Purpose:  This function is called when there is a Dlist to be
//     *           processed. (High level GFX list)
//     * input:    none
//     * output:   none
//     */
//    public Runnable getProcessDList();
//    
//    /**
//     * Function: getProcessRDPList
//     * Purpose:  This function is called when there is a Dlist to be
//     *           processed. (Low level GFX list)
//     * input:    none
//     * output:   none
//     */
//    public Runnable getProcessRDPList();

	/**
	 * Function: processDList Purpose: This function is called when there is a Dlist
	 * to be processed. (High level GFX list) input: none output: none
	 */
	public void processDList();

	/**
	 * Function: processRDPList Purpose: This function is called when there is a
	 * Dlist to be processed. (Low level GFX list) input: none output: none
	 */
	public void processRDPList();

	/**
	 * Function: romClosed Purpose: This function is called when a rom is closed.
	 * input: none output: none
	 */
	public void romClosed();

	/**
	 * Function: romOpen Purpose: This function is called when a rom is open. (from
	 * the emulation thread) input: none output: none
	 */
	public void romOpen();

	/**
	 * Function: showCFB Purpose: Useally once Dlists are started being displayed,
	 * cfb is ignored. This function tells the Plugin to start displaying them
	 * again. input: none output: none
	 */
	public void showCFB();

	/**
	 * Function: updateScreen Purpose: This function is called in response to a
	 * vsync of the screen were the VI bit in MI_INTR_REG has already been set
	 * input: none output: none
	 */
	public void updateScreen();

	/**
	 * Function: viStatusChanged Purpose: This function is called to notify the
	 * Plugin that the ViStatus registers value has been changed. input: none
	 * output: none
	 */
	public void viStatusChanged();

	/**
	 * Function: viWidthChanged Purpose: This function is called to notify the
	 * Plugin that the ViWidth registers value has been changed. input: none output:
	 * none
	 */
	public void viWidthChanged();

//    public int updateCurrentHalfLine(int timer);

}
