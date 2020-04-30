package me.hydos.J64.emu.debug;

import java.io.BufferedReader;
import java.io.PrintWriter;


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

	private static PrintWriter out;
	private static BufferedReader in;

	/** Creates a new instance of Debug */
	private Debug() {
	}

	public static void init() {
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
}
