package plugin;

import java.nio.ByteBuffer;
import javax.swing.JFrame;

public interface RSP_Plugin {

	/* Note: BOOL, BYTE, WORD, DWORD, TRUE, FALSE are defined in windows.h */

	public static final int PLUGIN_TYPE_RSP = 1;
	public static final int PLUGIN_TYPE_GFX = 2;
	public static final int PLUGIN_TYPE_AUDIO = 3;
	public static final int PLUGIN_TYPE_CONTROLLER = 4;

	@Deprecated
	public static class PLUGIN_INFO {
	};

	public static class RSP_INFO {
//	HINSTANCE hInst;
		public boolean MemoryBswaped; /*
										 * If this is set to TRUE, then the memory has been pre bswap on a dword (32
										 * bits) boundry
										 */
		public ByteBuffer RDRAM;
		public ByteBuffer DMEM;
		public ByteBuffer IMEM;

		public int MI_INTR_REG;
		public int[] MI_Registers;

		public int SP_MEM_ADDR_REG;
		public int SP_DRAM_ADDR_REG;
		public int SP_RD_LEN_REG;
		public int SP_WR_LEN_REG;
		public int SP_STATUS_REG;
		public int SP_DMA_FULL_REG;
		public int SP_DMA_BUSY_REG;
		public int SP_PC_REG;
		public int SP_SEMAPHORE_REG;
		public int[] SP_Registers;

		public int DPC_START_REG;
		public int DPC_END_REG;
		public int DPC_CURRENT_REG;
		public int DPC_STATUS_REG;
		public int DPC_CLOCK_REG;
		public int DPC_BUFBUSY_REG;
		public int DPC_PIPEBUSY_REG;
		public int DPC_TMEM_REG;
		public int[] DPC_Registers;

		public int VI_STATUS_REG;
		public int VI_ORIGIN_REG;
		public int VI_WIDTH_REG;
		public int VI_INTR_REG;
		public int VI_V_CURRENT_LINE_REG;
		public int VI_TIMING_REG;
		public int VI_V_SYNC_REG;
		public int VI_H_SYNC_REG;
		public int VI_LEAP_REG;
		public int VI_H_START_REG;
		public int VI_V_START_REG;
		public int VI_V_BURST_REG;
		public int VI_X_SCALE_REG;
		public int VI_Y_SCALE_REG;
		public int[] VI_Registers;

		public Runnable CheckInterrupts;
//        public Runnable ProcessAlistList;
		public AudioPlugin audioPlugin;
//        public Runnable ProcessDlistList;
//        public Runnable ProcessRdpList;
//        public Runnable ShowCFB;
		public GfxPlugin gfxPlugin;

	};

	public static class RSPDEBUG_INFO {
//	/* Menu */
//	/* Items should have an ID between 5001 and 5100 */
//	HMENU hRSPMenu;
//	void (*ProcessMenuItem) ( int ID );
//
//	/* Break Points */
//	BOOL UseBPoints;
//	char BPPanelName[20];
//	void (*Add_BPoint)      ( void );
//	void (*CreateBPPanel)   ( HWND hDlg, RECT rcBox );
//	void (*HideBPPanel)     ( void );
//	void (*PaintBPPanel)    ( PAINTSTRUCT ps );
//	void (*ShowBPPanel)     ( void );
//	void (*RefreshBpoints)  ( HWND hList );
//	void (*RemoveBpoint)    ( HWND hList, int index );
//	void (*RemoveAllBpoint) ( void );
//	
//	/* RSP command Window */
//	void (*Enter_RSP_Commands_Window) ( void );

	};

	public static class DEBUG_INFO {
//	void (*UpdateBreakPoints)( void );
//	void (*UpdateMemory)( void );
//	void (*UpdateR4300iRegisters)( void );
//	void (*Enter_BPoint_Window)( void );
//	void (*Enter_R4300i_Commands_Window)( void );
//	void (*Enter_R4300i_Register_Window)( void );
//	void (*Enter_RSP_Commands_Window) ( void );
//	void (*Enter_Memory_Window)( void );
	};

	/******************************************************************
	 * Function: CloseDLL Purpose: This function is called when the emulator is
	 * closing down allowing the dll to de-initialise. input: none output: none
	 *******************************************************************/
	public void CloseDLL();

	/******************************************************************
	 * Function: DllAbout Purpose: This function is optional function that is
	 * provided to give further information about the DLL. input: a handle to the
	 * window that calls this function output: none
	 *******************************************************************/
	public void DllAbout(JFrame hParent);

	/******************************************************************
	 * Function: DllConfig Purpose: This function is optional function that is
	 * provided to allow the user to configure the dll input: a handle to the window
	 * that calls this function output: none
	 *******************************************************************/
	public void DllConfig(JFrame hParent);

	/******************************************************************
	 * Function: DllTest Purpose: This function is optional function that is
	 * provided to allow the user to test the dll input: a handle to the window that
	 * calls this function output: none
	 *******************************************************************/
	public void DllTest(JFrame hParent);

	/******************************************************************
	 * Function: DoRspCycles Purpose: This function is to allow the RSP to run in
	 * parrel with the r4300 switching control back to the r4300 once the function
	 * ends. input: The number of cylces that is meant to be executed output: The
	 * number of cycles that was executed. This value can be greater than the number
	 * of cycles that the RSP should have performed. (this value is ignored if the
	 * RSP is stoped)
	 *******************************************************************/
	public int DoRspCycles(int Cycles);

	/******************************************************************
	 * Function: GetDllInfo Purpose: This function allows the emulator to gather
	 * information about the dll by filling in the PluginInfo structure. input: a
	 * pointer to a PLUGIN_INFO stucture that needs to be filled by the function.
	 * (see def above) output: none
	 *******************************************************************/
	public void GetDllInfo(PLUGIN_INFO PluginInfo);

	/******************************************************************
	 * Function: GetRspDebugInfo Purpose: This function allows the emulator to
	 * gather information about the debug capabilities of the dll by filling in the
	 * DebugInfo structure. input: a pointer to a RSPDEBUG_INFO stucture that needs
	 * to be filled by the function. (see def above) output: none
	 *******************************************************************/
	public void GetRspDebugInfo(RSPDEBUG_INFO RSPDebugInfo);

	/******************************************************************
	 * Function: InitiateRSP Purpose: This function is called when the DLL is
	 * started to give information from the emulator that the n64 RSP interface
	 * needs input: Rsp_Info is passed to this function which is defined above.
	 * CycleCount is the number of cycles between switching control between the RSP
	 * and r4300i core. output: none
	 *******************************************************************/
	public void InitiateRSP(RSP_INFO Rsp_Info, int[] CycleCount);

	/******************************************************************
	 * Function: InitiateRSPDebugger Purpose: This function is called when the DLL
	 * is started to give information from the emulator that the n64 RSP interface
	 * needs to intergrate the debugger with the rest of the emulator. input:
	 * DebugInfo is passed to this function which is defined above. output: none
	 *******************************************************************/
	public void InitiateRSPDebugger(DEBUG_INFO DebugInfo);

	/******************************************************************
	 * Function: RomClosed Purpose: This function is called when a rom is closed.
	 * input: none output: none
	 *******************************************************************/
	public void RomClosed();

}
