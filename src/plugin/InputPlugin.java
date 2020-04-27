package plugin;

import java.nio.ByteBuffer;
import javax.swing.JFrame;

public interface InputPlugin {

	public static final int PLUGIN_TYPE_CONTROLLER = 4;

	/*** Conteroller plugin's ****/
	public static final int PLUGIN_NONE = 1;
	public static final int PLUGIN_MEMPAK = 2;
	public static final int PLUGIN_RUMBLE_PAK = 3; // not implemeted for non raw data
	public static final int PLUGIN_TANSFER_PAK = 4; // not implemeted for non raw data
	public static final int PLUGIN_RAW = 5; // the controller plugin is passed in raw data

	public static class PluginInfo {
		public int version; /* Should be set to 0x0100 */
		public int type; /* Set to PLUGIN_TYPE_CONTROLLER */
		public String name; /* name of the plugin */
		public boolean reserved1;
		public boolean reserved2;
	};

	public static class Control {
		public boolean present;
		public boolean rawData;
		public int plugin;
	};

	public static class Buttons {
		public static final int A_BUTTON = 0x80000000;
		public static final int B_BUTTON = 0x40000000;
		public static final int Z_TRIG = 0x20000000;
		public static final int START_BUTTON = 0x10000000;
		public static final int U_DPAD = 0x08000000;
		public static final int D_DPAD = 0x04000000;
		public static final int L_DPAD = 0x02000000;
		public static final int R_DPAD = 0x01000000;

		public static final int RESERVED2 = 0x00800000;
		public static final int RESERVED1 = 0x00400000;
		public static final int L_TRIG = 0x00200000;
		public static final int R_TRIG = 0x00100000;
		public static final int U_CBUTTON = 0x00080000;
		public static final int D_CBUTTON = 0x00040000;
		public static final int L_CBUTTON = 0x00020000;
		public static final int R_CBUTTON = 0x00010000;

		public int value;

		public void setYAxis(byte b) {
			value = ((b << 8) & 0x0000FF00) | (value & 0xFFFF00FF);
		}

		public void setXAxis(byte b) {
			value = ((b) & 0x000000FF) | (value & 0xFFFFFF00);
		}
	};

	/**
	 * Function: closePlugin Purpose: This function is called when the emulator is
	 * closing down allowing the Plugin to de-initialise. input: none output: none
	 */
	public void closePlugin();

	/**
	 * Function: controllerCommand Purpose: To process the raw data that has just
	 * been sent to a specific controller. input: - Controller Number (0 to 3) and
	 * -1 signalling end of processing the pif ram. - Pointer of data to be
	 * processed. output: none
	 * 
	 * note: This function is only needed if the Plugin is allowing raw data.
	 * 
	 * the data that is being processed looks like this: initilize controller: 01 03
	 * 00 FF FF FF read controller: 01 04 01 FF FF FF FF
	 */
	public void controllerCommand(int control, ByteBuffer command);

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
	 * Function: getKeys Purpose: To get the current state of the controllers
	 * buttons. input: - Controller Number (0 to 3) - A pointer to a Buttons
	 * structure to be filled with the controller state. output: none
	 */
	public void getKeys(int control, Buttons keys);

	/**
	 * Function: initiateControllers Purpose: This function initialises how each of
	 * the controllers should be handled. input: - The handle to the main window. -
	 * A controller structure that needs to be filled for the emulator to know how
	 * to handle each controller. output: none
	 */
	public void initiateControllers(JFrame hMainWindow, Control[] controls);

	/**
	 * Function: readController Purpose: To process the raw data in the pif ram that
	 * is about to be read. input: - Controller Number (0 to 3) and -1 signalling
	 * end of processing the pif ram. - Pointer of data to be processed. output:
	 * none note: This function is only needed if the Plugin is allowing raw data.
	 */
	public void readController(int control, ByteBuffer command);

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
	 * Function: wmKeyDown Purpose: To pass the wmKeyDown message from the emulator
	 * to the plugin. input: wParam and lParam of the WM_KEYDOWN message. output:
	 * none
	 */
	public void wmKeyDown(int wParam, int lParam);

	/**
	 * Function: wmKeyUp Purpose: To pass the WM_KEYUP message from the emulator to
	 * the plugin. input: wParam and lParam of the WM_KEYUP message. output: none
	 */
	public void wmKeyUp(int wParam, int lParam);

}
