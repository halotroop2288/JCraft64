package me.hydos.J64.hardware;

import java.nio.ByteBuffer;
import java.util.Properties;
import javax.swing.JFrame;

import me.hydos.J64.Registers;
import plugin.GfxPlugin;

public class Video {

	public static final String GFX_PLUGIN = "GFX_PLUGIN";

	private static final int NUM_FRAMES = 7;

	public long millisInGfx;

	public int[] regVI = new int[14];

	public GfxPlugin gfxPlugin;

	private final Runnable checkInterrupts;
	private final ByteBuffer rDram;
	private final ByteBuffer dMem;
	private final ByteBuffer iMem;
	private final int[] regMI;
	private final int[] regDPC;
	private JFrame hWnd;
	private long lastFrame;
	private long lastTime;
	private final long[] frames = new long[NUM_FRAMES];
	private int currentFrame;
	private final long frequency;
	private int viIntrTime = 500000;
	private int viFieldNumber;
	private Cop0 cop0;
	private boolean frameLimit;

	public Runnable timerInterrupt = new Runnable() {
		public void run() {
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_VI;
			checkInterrupts.run();
			if (gfxPlugin != null) {
				cop0.update();
				long time = System.currentTimeMillis();
				try {
					cop0.changeTimer(Cop0.VI_TIMER, cop0.timer + cop0.nextTimer[Cop0.VI_TIMER] + refreshScreen());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				millisInGfx += (System.currentTimeMillis() - time);
			}
		}
	};

	/** Creates a new instance of Video */
	public Video(Runnable checkInterrupts, Registers regs, ByteBuffer rDram, ByteBuffer dMem, ByteBuffer iMem) {
		this.checkInterrupts = checkInterrupts;
		this.rDram = rDram;
		this.dMem = dMem;
		this.iMem = iMem;
		this.regMI = regs.regMI;
		this.regDPC = regs.regDPC;
		frequency = 1000;
		currentFrame = 0;
		viFieldNumber = 0;
	}

	public void setTimer(Cop0 cop0) {
		this.cop0 = cop0;
	}

	public void setFrameLimit(boolean frameLimit) {
		this.frameLimit = frameLimit;
	}

	public void setupPlugin(JFrame hWnd, Properties cfg) {
		this.hWnd = hWnd;
		String gfx_plugin = cfg.getProperty(GFX_PLUGIN, "DEFAULT_GFX_PLUGIN");
		try {
			Class<?> c = Class.forName(gfx_plugin);
			gfxPlugin = (GfxPlugin) c.newInstance();
		} catch (Exception ex) {
			System.err.println("No gfx plugin loaded.");
			ex.printStackTrace();
			return;
		}

		GfxPlugin.GfxInfo gfxInfo = new GfxPlugin.GfxInfo();

		gfxInfo.memoryBswaped = true;
		gfxInfo.checkInterrupts = checkInterrupts;
		gfxInfo.hWnd = hWnd;
		gfxInfo.rdram = rDram;
		gfxInfo.dmem = dMem;
		gfxInfo.imem = iMem;

		gfxInfo.MI_INTR_REG = Registers.MI_INTR_REG;
		gfxInfo.miRegisters = regMI;
		gfxInfo.DPC_START_REG = Registers.DPC_START_REG;
		gfxInfo.DPC_END_REG = Registers.DPC_END_REG;
		gfxInfo.DPC_CURRENT_REG = Registers.DPC_CURRENT_REG;
		gfxInfo.DPC_STATUS_REG = Registers.DPC_STATUS_REG;
		gfxInfo.DPC_CLOCK_REG = Registers.DPC_CLOCK_REG;
		gfxInfo.DPC_BUFBUSY_REG = Registers.DPC_BUFBUSY_REG;
		gfxInfo.DPC_PIPEBUSY_REG = Registers.DPC_PIPEBUSY_REG;
		gfxInfo.DPC_TMEM_REG = Registers.DPC_TMEM_REG;
		gfxInfo.dpcRegisters = regDPC;
		gfxInfo.viRegisters = regVI;

		if (!gfxPlugin.initiateGfx(gfxInfo)) {
			System.err.println("Failed to Initilize Graphics!");
		}
	}

	private int refreshScreen() throws InterruptedException {
		int oldViVsyncReg = 0;
		if (oldViVsyncReg != regVI[GfxPlugin.VI_V_SYNC_REG]) {
			viIntrTime = (regVI[GfxPlugin.VI_V_SYNC_REG] + 1) * 1500;
		}
		if ((regVI[GfxPlugin.VI_STATUS_REG] & 0x10) != 0) {
			if (viFieldNumber == 0) {
				viFieldNumber = 1;
			} else {
				viFieldNumber = 0;
			}
		} else {
			viFieldNumber = 0;
		}

		if ((currentFrame & 7) == 0) {
			long time = System.currentTimeMillis();
			frames[(currentFrame >> 3) % NUM_FRAMES] = time - lastFrame;
			lastFrame = time;
			if (currentFrame > (NUM_FRAMES << 3)) {
				long total = 0;
				for (int count = 0; count < NUM_FRAMES; count++)
					total += frames[count];
				hWnd.setTitle("FPS: " + frequency / ((double) total / (NUM_FRAMES << 3)));
			} else {
				hWnd.setTitle("FPS: -.--");
			}
		}
		currentFrame += 1;

		if (gfxPlugin != null)
			gfxPlugin.updateScreen();

		if (frameLimit) {
			while (lastTime + 17L > System.currentTimeMillis()) {
				Thread.sleep(1);
			}
			lastTime = System.currentTimeMillis();
		}

		return viIntrTime;
	}

	public int updateCurrentHalfLine(int timer) {
		if (timer < 0)
			return 0;
		int halfLine = (timer / 1500);
		halfLine &= ~1;
		halfLine += viFieldNumber;
		return halfLine;
	}

//    private void shutdownPlugins() {
//        if (gfxPlugin != null) { gfxPlugin.CloseDLL(); }
//    }

}
