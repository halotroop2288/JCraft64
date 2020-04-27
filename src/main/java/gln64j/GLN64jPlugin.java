package gln64j;

import me.hydos.J64.util.debug.Debug;
import gln64j.rsp.Gsp;
import gln64j.rdp.Gdp;

import java.nio.ByteBuffer;
import javax.swing.JFrame;

import plugin.GfxPlugin;

public class GLN64jPlugin implements GfxPlugin {
    public static final boolean DEBUG = Debug.DEBUG_GLN64;

    private static final int NUM_FRAMES = 7;

    public static JFrame hWnd;

    public static Runnable CheckInterrupts;

    public static Registers REG = new Registers();
    public static ByteBuffer DMEM;
    public static ByteBuffer IMEM;
    public static ByteBuffer TMEM = ByteBuffer.allocate(8 * 512);
    public static ByteBuffer RDRAM;
    public static int RDRAMSize;

    private final String name;

    public GLN64jPlugin() {
        name = this.getClass().getName();
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") created.");

        OpenGl.config();
    }

    public void captureScreen(String directory) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") captureScreen.");
    }

    public void changeWindow() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") changeWindow.");
    }

    public void closePlugin() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") closePlugin.");
    }

    public void pluginAbout(JFrame hParent) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") pluginAbout.");
    }

    public void pluginConfig(JFrame hParent) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") pluginConfig.");
    }

    public void pluginTest(JFrame hParent) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") pluginTest.");
    }

    public void drawScreen() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") drawScreen.");
    }

    public void getPluginInfo(PluginInfo pluginInfo) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") getPluginInfo.");
    }

    public boolean initiateGfx(GfxInfo gfxInfo) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") initiateGfx.");

        hWnd = gfxInfo.hWnd;

        DMEM = gfxInfo.dmem;
        IMEM = gfxInfo.imem;
        RDRAM = gfxInfo.rdram;

        REG.MI_INTR = gfxInfo.MI_INTR_REG;
        REG.MI_Registers = gfxInfo.miRegisters;

        REG.DPC_START = gfxInfo.DPC_START_REG;
        REG.DPC_END = gfxInfo.DPC_END_REG;
        REG.DPC_CURRENT = gfxInfo.DPC_CURRENT_REG;
        REG.DPC_STATUS = gfxInfo.DPC_STATUS_REG;
        REG.DPC_CLOCK = gfxInfo.DPC_CLOCK_REG;
        REG.DPC_BUFBUSY = gfxInfo.DPC_BUFBUSY_REG;
        REG.DPC_PIPEBUSY = gfxInfo.DPC_PIPEBUSY_REG;
        REG.DPC_TMEM = gfxInfo.DPC_TMEM_REG;
        REG.DPC_Registers = gfxInfo.dpcRegisters;

        REG.VI_STATUS = VI_STATUS_REG;
        REG.VI_ORIGIN = VI_ORIGIN_REG;
        REG.VI_WIDTH = VI_WIDTH_REG;
        REG.VI_INTR = VI_INTR_REG;
        REG.VI_V_CURRENT_LINE = VI_CURRENT_REG;
        REG.VI_TIMING = VI_BURST_REG;
        REG.VI_V_SYNC = VI_V_SYNC_REG;
        REG.VI_H_SYNC = VI_H_SYNC_REG;
        REG.VI_LEAP = VI_LEAP_REG;
        REG.VI_H_START = VI_H_START_REG;
        REG.VI_V_START = VI_V_START_REG;
        REG.VI_V_BURST = VI_V_BURST_REG;
        REG.VI_X_SCALE = VI_X_SCALE_REG;
        REG.VI_Y_SCALE = VI_Y_SCALE_REG;
        REG.VI_Registers = gfxInfo.viRegisters;

        CheckInterrupts = gfxInfo.checkInterrupts;

        return true;
    }

    public void moveScreen(int xpos, int ypos) {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") moveScreen.");
    }

    public void processDList() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") processDList.");
        OpenGlGdp.hDC.display();
    }

    public void processRDPList() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") processRDPList.");
    }

    public void romClosed() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") romClosed.");
        OpenGl.OGL_Stop();
        if (DEBUG) Debug.CloseDebugDlg();
    }

    public void romOpen() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") romOpen.");
        OpenGl.uc_start = OpenGl.uc_dstart = 0;
        RDRAMSize = RDRAM.capacity();
        Rsp.gsp = new Gsp(RDRAM, DMEM);
        Rsp.gdp = new Gdp(CheckInterrupts, REG);
        OpenGl.OGL_Start();

        OpenGlGdp.OGL_ResizeWindow();
        if (DEBUG) Debug.OpenDebugDlg();
    }

    public void showCFB() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") showCFB.");
    }

    public void updateScreen() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") updateScreen.");
        OpenGlGdp.viUpdateScreen();
    }

    public void viStatusChanged() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") viStatusChanged.");
    }

    public void viWidthChanged() {
        if (DEBUG) System.out.println("GFX Plugin (" + name + ") viWidthChanged.");
    }
}
