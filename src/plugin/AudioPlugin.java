package plugin;

import java.nio.ByteBuffer;
import javax.swing.JFrame;

public interface AudioPlugin {

	public static final int PLUGIN_TYPE_AUDIO = 3;

	public static final int SYSTEM_NTSC = 0;
	public static final int SYSTEM_PAL = 1;
	public static final int SYSTEM_MPAL = 2;

	public static final int AI_DRAM_ADDR_REG = 0;
	public static final int AI_LEN_REG = 1;
	public static final int AI_CONTROL_REG = 2;
	public static final int AI_STATUS_REG = 3;
	public static final int AI_DACRATE_REG = 4;
	public static final int AI_BITRATE_REG = 5;

	public static class PluginInfo {
		public int version; /* Should be set to 0x0101 */
		public int type; /* Set to PLUGIN_TYPE_AUDIO */
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
	};

	public static class AudioInfo {
		public JFrame hwnd;

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

//        public int AI_DRAM_ADDR_REG;
//        public int AI_LEN_REG;
//        public int AI_CONTROL_REG;
//        public int AI_STATUS_REG;
//        public int AI_DACRATE_REG;
//        public int AI_BITRATE_REG;
		public int[] aiRegisters;

		public Runnable checkInterrupts;
	};

	/**
	 * Function: aiDacrateChanged Purpose: This function is called to notify the
	 * Plugin that the AiDacrate registers value has been changed. input: The System
	 * type: SYSTEM_NTSC 0 SYSTEM_PAL 1 SYSTEM_MPAL 2 output: none
	 */
	public void aiDacrateChanged(int systemType);

	/**
	 * Function: aiLenChanged Purpose: This function is called to notify the Plugin
	 * that the AiLen registers value has been changed. input: none output: none
	 */
	public void aiLenChanged();

	/**
	 * Function: aiReadLength Purpose: This function is called to allow the Plugin
	 * to return the value that AI_LEN_REG should equal input: none output: The
	 * amount of bytes still left to play.
	 */
	public int aiReadLength();

	/**
	 * Function: aiUpdate Purpose: This function is called to allow the Plugin to
	 * update things on a regular basis (check how long to sound to go, copy more
	 * stuff to the buffer, anyhting you like). The function is designed to go in to
	 * the message loop of the main window ... but can be placed anywhere you like.
	 * input: if Wait is set to true, then this function should wait till there is a
	 * messgae in the its message queue. output: none
	 */
	public void aiUpdate(boolean wait);

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
	 * Function: getPluginInfo Purpose: This function allows the emulator to gather
	 * information about the Plugin by filling in the pluginInfo structure. input: a
	 * pointer to a pluginInfo stucture that needs to be filled by the function.
	 * (see def above) output: none
	 */
	public void getPluginInfo(PluginInfo pluginInfo);

	/**
	 * Function: InitiateSound Purpose: This function is called when the Plugin is
	 * started to give information from the emulator that the n64 audio interface
	 * needs Input: audioInfo is passed to this function which is defined above.
	 * Output: TRUE on success FALSE on failure to initialise note on interrupts **:
	 * To generate an interrupt set the appropriate bit in MI_INTR_REG and then call
	 * the function checkInterrupts to tell the emulator that there is a waiting
	 * interrupt.
	 */
	public boolean initiateAudio(AudioInfo audioInfo);

//    /**
//     * Function: getProcessAList
//     * Purpose:  This function is called when there is a Alist to be
//     *           processed. The Plugin will have to work out all the info
//     *           about the AList itself.
//     * input:    none
//     * output:   none
//     */
//    public Runnable getProcessAList();

	/**
	 * Function: processAList Purpose: This function is called when there is a Alist
	 * to be processed. The Plugin will have to work out all the info about the
	 * AList itself. input: none output: none
	 */
	public void processAList();

	/**
	 * Function: romClosed Purpose: This function is called when a rom is closed.
	 * input: none output: none
	 */
	public void romClosed();

}
