package me.hydos.J64.debug;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class Debug {

	public static final int NO_LOG = 0;
	public static final int SYNC_LOG = 1;
	public static final int FILE_LOG = 2;
	public static final int SCREEN_LOG = 3;
	public static final String OPS_SYNC_FILE = "C:/proj64output.txt";
	public static final String EXC_SYNC_FILE = "C:/proj64outputexcep.txt";
	public static final String MEM_SYNC_FILE = "C:/proj64outputmem.txt";
	public static final String COP_SYNC_FILE = "C:/proj64outputcop.txt";
	public static final String GEN_OUT_FILE = "C:/jn64output.txt";

	// ** DEBUG OPTIONS ********************************************************

	public static final int LOG_TYPE = SCREEN_LOG;
	public static final String SYNC_LOG_FILE = OPS_SYNC_FILE;
	public static final String FILE_LOG_FILE = GEN_OUT_FILE;

	public static boolean DEBUG_OPCODES = true;
	public static boolean DEBUG_I_OPCODES = true;
	public static boolean DEBUG_R_OPCODES = true;
	public static boolean DEBUG_J_OPCODES = true;
	public static boolean DEBUG_ST_OPCODES = true; // Store
	public static boolean DEBUG_LD_OPCODES = true; // Load
	public static boolean DEBUG_CP_OPCODES = true; // Coprocessor
	public static boolean DEBUG_BR_OPCODES = true; // Branch
	public static boolean DEBUG_OP_OPCODES = true; // Operations

	// ** DEBUG OPTIONS ********************************************************

	private static String logMsg = "";
	private static PrintWriter out;
	private static BufferedReader in;
	private static PrintStream systemOut;

	/** Creates a new instance of Debug */
	private Debug() {
	}

	public static void init() {
		System.setErr(new PrintStream(System.err) {
			public PrintStream printf(String format, Object... args) {
				String msg = String.format(format, args);
				JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
				return this;
			}
		});

		systemOut = System.out;
		System.setOut(new PrintStream(System.out) {
			public PrintStream printf(String format, Object... args) {
				Log(format, args);
				return this;
			}
		});

	}

	public static void close() {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void Log(String Message, Object... args) {
		logMsg += String.format(Message, args);
		if (logMsg.endsWith("\n")) {
			systemOut.printf(logMsg, args);
			logMsg = "";
		}
	}

}
