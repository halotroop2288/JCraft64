
package me.hydos.J64.controller;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import javax.swing.JFrame;
import plugin.InputPlugin;

public class Default implements InputPlugin {

	public static final boolean DEBUG = false;

	private static final int NUMBER_OF_BUTTONS = 10;

	private static class Key {
		public int key;
		public int button;
	}

	private static final Buttons controller1 = new Buttons();
	private static final Key[] keys = new Key[NUMBER_OF_BUTTONS];
	private final String name;

	public Default() {
		name = this.getClass().getName();
	}

	public void closePlugin() {}

	public void controllerCommand(int control, ByteBuffer command) {}

	public void pluginAbout(JFrame hParent) {}

	public void pluginConfig(JFrame hParent) {}

	public void pluginTest(JFrame hParent) {}

	public void getPluginInfo(PluginInfo pluginInfo) {
		pluginInfo.version = 1;
		pluginInfo.type = PLUGIN_TYPE_CONTROLLER;
		pluginInfo.name = "Keyboard plugin";
	}

	public void getKeys(int control, Buttons keys) {
		if (keys == null)
			return;
		keys.value = controller1.value;
	}

	public void initiateControllers(JFrame hMainWindow, Control[] controls) {
		Buttons buttons = new Buttons();

		controls[0].present = true;
		controls[0].rawData = false;
		controls[0].plugin = PLUGIN_NONE;
		controls[1].present = false;
		controls[2].present = false;
		controls[3].present = false;

		buttons.value = 0; // Up
		buttons.setXAxis((byte) 80); // value may need to be reversed
		buttons.value |= Buttons.U_DPAD;
		keys[0] = new Key();
		keys[0].key = KeyEvent.VK_UP;
		keys[0].button = buttons.value;

		buttons.value = 0; // Down
		buttons.setXAxis((byte) -80); // value may need to be reversed
		buttons.value |= Buttons.D_DPAD;
		keys[1] = new Key();
		keys[1].key = KeyEvent.VK_DOWN;
		keys[1].button = buttons.value;

		buttons.value = 0; // left
		buttons.setYAxis((byte) -80); // value may need to be reversed
		buttons.value |= Buttons.L_DPAD;
		keys[2] = new Key();
		keys[2].key = KeyEvent.VK_LEFT;
		keys[2].button = buttons.value;

		buttons.value = 0; // Right
		buttons.setYAxis((byte) 80); // value may need to be reversed
		buttons.value |= Buttons.R_DPAD;
		keys[3] = new Key();
		keys[3].key = KeyEvent.VK_RIGHT;
		keys[3].button = buttons.value;

		buttons.value = 0; // Start button
		buttons.value |= Buttons.START_BUTTON;
		keys[4] = new Key();
		keys[4].key = KeyEvent.VK_ENTER;
		keys[4].button = buttons.value;

		buttons.value = 0; // A button
		buttons.value |= Buttons.A_BUTTON;
		keys[5] = new Key();
		keys[5].key = 'X';
		keys[5].button = buttons.value;

		buttons.value = 0; // B button
		buttons.value |= Buttons.B_BUTTON;
		keys[6] = new Key();
		keys[6].key = 'C';
		keys[6].button = buttons.value;

		// NEW

		buttons.value = 0; // L button
		buttons.value |= Buttons.L_TRIG;
		keys[7] = new Key();
		keys[7].key = 'A';
		keys[7].button = buttons.value;

		buttons.value = 0; // R button
		buttons.value |= Buttons.R_TRIG;
		keys[8] = new Key();
		keys[8].key = 'S';
		keys[8].button = buttons.value;

		buttons.value = 0; // Z button
		buttons.value |= Buttons.Z_TRIG;
		keys[9] = new Key();
		keys[9].key = 'Z';
		keys[9].button = buttons.value;
	}

	public void readController(int control, ByteBuffer command) {
		if (DEBUG)
			System.out.println("Controller Plugin (" + name + ") readController.");
	}

	public void romClosed() {
		if (DEBUG)
			System.out.println("Controller Plugin (" + name + ") romClosed.");
	}

	public void romOpen() {
		if (DEBUG)
			System.out.println("Controller Plugin (" + name + ") romOpen.");
	}

	public void wmKeyDown(int wParam, int lParam) {
		if (DEBUG)
			System.out.println("Controller Plugin (" + name + ") wmKeyDown.");
		for (int count = 0; count < NUMBER_OF_BUTTONS; count++) {
			if (keys[count].key == wParam) {
				controller1.value |= keys[count].button;
				return;
			}
		}
	}

	public void wmKeyUp(int wParam, int lParam) {
		if (DEBUG)
			System.out.println("Controller Plugin (" + name + ") wmKeyUp.");
		for (int count = 0; count < NUMBER_OF_BUTTONS; count++) {
			if (keys[count].key == wParam) {
				controller1.value &= ~keys[count].button;
				return;
			}
		}
	}

}
