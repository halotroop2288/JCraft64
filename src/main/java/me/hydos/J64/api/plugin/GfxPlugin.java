package me.hydos.J64.api.plugin;

import java.nio.ByteBuffer;

import javax.swing.JFrame;

public interface GfxPlugin {

	int PLUGIN_TYPE_GFX = 2;

	// Video Interface (VI) Registers
	int VI_STATUS_REG = 0;
	int VI_ORIGIN_REG = 1;
	int VI_WIDTH_REG = 2;
	int VI_INTR_REG = 3;
	int VI_CURRENT_REG = 4;
	int VI_BURST_REG = 5;
	int VI_V_SYNC_REG = 6;
	int VI_H_SYNC_REG = 7;
	int VI_LEAP_REG = 8;
	int VI_H_START_REG = 9;
	int VI_V_START_REG = 10;
	int VI_V_BURST_REG = 11;
	int VI_X_SCALE_REG = 12;
	int VI_Y_SCALE_REG = 13;

	class PluginInfo {
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

	class GfxInfo {
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
	void captureScreen(String directory);

	/**
	 * Function: changeWindow Purpose: to change the window between fullscreen and
	 * window mode. If the window was in fullscreen this should change the screen to
	 * window mode and vice vesa. input: none output: none
	 */
	void changeWindow();

	/**
	 * Function: closePlugin Purpose: This function is called when the emulator is
	 * closing down allowing the Plugin to de-initialise. input: none output: none
	 */
	void closePlugin();

	/**
	 * Function: pluginAbout Purpose: This function is optional function that is
	 * provided to give further information about the Plugin. input: a handle to the
	 * window that calls this function output: none
	 */
	void pluginAbout(JFrame hParent);

	/**
	 * Function: pluginConfig Purpose: This function is optional function that is
	 * provided to allow the user to configure the Plugin input: a handle to the
	 * window that calls this function output: none
	 */
	void pluginConfig(JFrame hParent);

	/**
	 * Function: pluginTest Purpose: This function is optional function that is
	 * provided to allow the user to test the Plugin input: a handle to the window
	 * that calls this function output: none
	 */
	void pluginTest(JFrame hParent);

	/**
	 * Function: drawScreen Purpose: This function is called when the emulator
	 * receives a WM_PAINT message. This allows the gfx to fit in when it is being
	 * used in the desktop. input: none output: none
	 */
	void drawScreen();

	/**
	 * Function: getPluginInfo Purpose: This function allows the emulator to gather
	 * information about the Plugin by filling in the pluginInfo structure. input: a
	 * pointer to a pluginInfo stucture that needs to be filled by the function.
	 * (see def above) output: none
	 */
	void getPluginInfo(PluginInfo pluginInfo);

	/**
	 * Function: initiateGfx Purpose: This function is called when the Plugin is
	 * started to give information from the emulator that the n64 graphics uses.
	 * This is not called from the emulation thread. Input: gfxInfo is passed to
	 * this function which is defined above. Output: TRUE on success FALSE on
	 * failure to initialise note on interrupts **: To generate an interrupt set the
	 * appropriate bit in MI_INTR_REG and then call the function checkInterrupts to
	 * tell the emulator that there is a waiting interrupt.
	 */
	boolean initiateGfx(GfxInfo gfxInfo);

	/**
	 * Function: moveScreen Purpose: This function is called in response to the
	 * emulator receiving a WM_MOVE passing the xpos and ypos passed from that
	 * message. input: xpos - the x-coordinate of the upper-left corner of the
	 * client area of the window. ypos - y-coordinate of the upper-left corner of
	 * the client area of the window. output: none
	 */
	void moveScreen(int xpos, int ypos);

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
	void processDList();

	/**
	 * Function: processRDPList Purpose: This function is called when there is a
	 * Dlist to be processed. (Low level GFX list) input: none output: none
	 */
	void processRDPList();

	/**
	 * Function: romClosed Purpose: This function is called when a rom is closed.
	 * input: none output: none
	 */
	void romClosed();

	/**
	 * Function: romOpen Purpose: This function is called when a rom is open. (from
	 * the emulation thread) input: none output: none
	 */
	void romOpen();

	/**
	 * Function: updateScreen Purpose: This function is called in response to a
	 * vsync of the screen were the VI bit in MI_INTR_REG has already been set
	 * input: none output: none
	 */
	void updateScreen();

	/**
	 * Function: viStatusChanged Purpose: This function is called to notify the
	 * Plugin that the ViStatus registers value has been changed. input: none
	 * output: none
	 */
	void viStatusChanged();

	/**
	 * Function: viWidthChanged Purpose: This function is called to notify the
	 * Plugin that the ViWidth registers value has been changed. input: none output:
	 * none
	 */
	void viWidthChanged();

}
