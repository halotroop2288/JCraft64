package me.hydos.J64;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.JFrame;

import me.hydos.J64.savechips.Eeprom;
import me.hydos.J64.savechips.Mempak;
import plugin.InputPlugin;

public class Pif {

	public static final String CONTROLLER_PLUGIN = "CONTROLLER_PLUGIN";

	// RegSI Serial Interface (SI) Registers (PIF)
	public static final int SI_DRAM_ADDR_REG = 0;
	public static final int SI_PIF_ADDR_RD64B_REG = 1;
	public static final int SI_PIF_ADDR_WR64B_REG = 2;
	public static final int SI_STATUS_REG = 3;

	public static final int SI_STATUS_DMA_BUSY = 0x0001;
	public static final int SI_STATUS_RD_BUSY = 0x0002;
	public static final int SI_STATUS_DMA_ERROR = 0x0008;
	public static final int SI_STATUS_INTERRUPT = 0x1000;

	// used by Main
	public InputPlugin inputPlugin;

	// used by Memory
	public byte[] pifRom = new byte[0x7C0];

	// used by Memory, Dma
	public ByteBuffer pifRam = ByteBuffer.wrap(new byte[0x40]);

	// Serial Interface (SI) Registers (PIF)
	// used by Memory, Dma
	public int[] regSI = new int[4];

	private InputPlugin.Control[] controllers = new InputPlugin.Control[4];
	private Runnable checkInterrupts;
	private int[] regMI;
	private Registers regs;
	private Eeprom eeprom;
	private Mempak mempak;
	private boolean showPifRamErrors;

	public Runnable timerInterrupt = new Runnable() {
		public void run() {
			regSI[SI_STATUS_REG] |= SI_STATUS_INTERRUPT;
			regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_SI;
			checkInterrupts.run();
		}
	};

	/** Creates a new instance of Pif */
	public Pif(Runnable checkInterrupts, Registers regs) {
		this.checkInterrupts = checkInterrupts;
		this.regs = regs;
		this.regMI = regs.regMI;
	}

	// called by Main
	public void setSaveChips(Eeprom eeprom, Mempak mempak) {
		this.eeprom = eeprom;
		this.mempak = mempak;
	}

	public void showPifRamErrors(boolean show) {
		showPifRamErrors = show;
	}

	// called by Main
	public void initalizeRegisters(int cicChip) {
		switch (cicChip) {
		case 1:
			pifRam.put(36, (byte) 0x00);
			pifRam.put(37, (byte) 0x06);
			pifRam.put(38, (byte) 0x3F);
			pifRam.put(39, (byte) 0x3F);
			break;
		case 2:
			pifRam.put(36, (byte) 0x00);
			pifRam.put(37, (byte) 0x02);
			pifRam.put(38, (byte) 0x3F);
			pifRam.put(39, (byte) 0x3F);
			break;
		case 3:
			pifRam.put(36, (byte) 0x00);
			pifRam.put(37, (byte) 0x02);
			pifRam.put(38, (byte) 0x78);
			pifRam.put(39, (byte) 0x3F);
			break;
		case 5:
			pifRam.put(36, (byte) 0x00);
			pifRam.put(37, (byte) 0x02);
			pifRam.put(38, (byte) 0x91);
			pifRam.put(39, (byte) 0x3F);
			break;
		case 6:
			pifRam.put(36, (byte) 0x00);
			pifRam.put(37, (byte) 0x02);
			pifRam.put(38, (byte) 0x85);
			pifRam.put(39, (byte) 0x3F);
			break;
		}
	}

	// called by Main
	public boolean loadPifRom(int country) {
		switch (country) {
		case 0x44: // Germany
		case 0x46: // french
		case 0x49: // Italian
		case 0x50: // Europe
		case 0x53: // Spanish
		case 0x55: // Australia
		case 0x58: // X (PAL)
		case 0x59: // X (PAL)
			break;
		case 0: // None
		case 0x37: // 7 (Beta)
		case 0x41: // ????
		case 0x45: // USA
		case 0x4A: // Japan
			break;
		default:
			System.err.printf("Unknown country in LoadPifRom\n");
		}
		Arrays.fill(pifRom, 0, 0x7C0, (byte) 0);
		return false;
	}

	public boolean setupPlugin(JFrame hWnd, Properties cfg) {
		// shutdownPlugins();
		String controller_plugin = cfg.getProperty(CONTROLLER_PLUGIN, "DEFAULT_CONTROLLER_PLUGIN");
		try {
			Class c = Class.forName(controller_plugin);
			inputPlugin = (InputPlugin) c.newInstance();
		} catch (Exception ex) {
			System.err.println("No controller plugin loaded.");
			ex.printStackTrace();
			return false;
		}

		controllers[0] = new InputPlugin.Control();
		controllers[0].present = false;
		controllers[0].rawData = false;
		controllers[0].plugin = InputPlugin.PLUGIN_NONE;

		controllers[1] = new InputPlugin.Control();
		controllers[1].present = false;
		controllers[1].rawData = false;
		controllers[1].plugin = InputPlugin.PLUGIN_NONE;

		controllers[2] = new InputPlugin.Control();
		controllers[2].present = false;
		controllers[2].rawData = false;
		controllers[2].plugin = InputPlugin.PLUGIN_NONE;

		controllers[3] = new InputPlugin.Control();
		controllers[3].present = false;
		controllers[3].rawData = false;
		controllers[3].plugin = InputPlugin.PLUGIN_NONE;

		inputPlugin.initiateControllers(hWnd, controllers);
		return true;
	}

	// called by Dma, Main
	public int getCicChipID(ByteBuffer romData) {
		long crc = 0;

		for (int count = 0x40; count < 0x1000; count += 4)
			crc += (((long) romData.getInt(count)) & 0xFFFFFFFFL);

		if (crc == 0x000000D0027FDF31L)
			return 1;
		else if (crc == 0x000000CFFB631223L)
			return 1;
		else if (crc == 0x000000D057C85244L)
			return 2;
		else if (crc == 0x000000D6497E414BL)
			return 3;
		else if (crc == 0x0000011A49F60E96L)
			return 5;
		else if (crc == 0x000000D6D5BE5580L)
			return 6;
		else
			return -1;
	}

	// called by Dma
	public void pifRamRead() {
		int channel = 0;
		int curPos = 0;

		do {
			switch (pifRam.get(curPos) & 0xFF) {
			case 0x00:
				channel += 1;
				if (channel > 6) {
					curPos = 0x40;
				}
				break;
			case 0xFE:
				curPos = 0x40;
				break;
			case 0xFF:
				break;
			case 0xB4:
			case 0x56:
			case 0xB8:
				break; /* ??? */
			default:
				if ((pifRam.get(curPos) & 0xC0) == 0) {
					if (channel < 4) {
						pifRam.position(curPos);
						if (controllers[channel].present && controllers[channel].rawData) {
							if (inputPlugin != null)
								inputPlugin.readController(channel, pifRam.slice());
						} else {
							readControllerCommand(channel, pifRam.slice());
						}
						pifRam.position(0);
					}
					curPos += (pifRam.get(curPos) & 0xFF) + (pifRam.get(curPos + 1) & 0x3F) + 1;
					channel += 1;
				} else {
					if (showPifRamErrors)
						System.err.printf("Unknown Command in PifRamRead(%X)\n", pifRam.get(curPos));
					curPos = 0x40;
				}
				break;
			}
			curPos += 1;
		} while (curPos < 0x40);

		if (inputPlugin != null)
			inputPlugin.readController(-1, null);
	}

	// called by Dma, Memory
	public void pifRamWrite() {
		int channel = 0;

		if ((pifRam.get(0x3F) & 0xFF) > 0x1) {
			switch (pifRam.get(0x3F) & 0xFF) {
			case 0x08:
				pifRam.put(0x3F, (byte) 0);
				regSI[SI_STATUS_REG] |= SI_STATUS_INTERRUPT;
				regMI[Registers.MI_INTR_REG] |= Registers.MI_INTR_SI;
				checkInterrupts.run();
				break;
			case 0x10:
				Arrays.fill(pifRom, 0, 0x7C0, (byte) 0);
				break;
			case 0x30:
				pifRam.put(0x3F, (byte) 0x80);
				break;
			case 0xC0:
				Arrays.fill(pifRam.array(), 0, 0x40, (byte) 0);
				break;
			default:
				if (showPifRamErrors)
					System.err.printf("Unkown PifRam control: %d\n", pifRam.get(0x3F));
			}
			return;
		}

		for (int curPos = 0; curPos < 0x40; curPos++) {
			switch (pifRam.get(curPos) & 0xFF) {
			case 0x00:
				channel += 1;
				if (channel > 6) {
					curPos = 0x40;
				}
				break;
			case 0xFE:
				curPos = 0x40;
				break;
			case 0xFF:
				break;
			case 0xB4:
			case 0x56:
			case 0xB8:
				break; /* ??? */
			default:
				if ((pifRam.get(curPos) & 0xC0) == 0) {
					if (channel < 4) {
						pifRam.position(curPos);
						if (controllers[channel].present && controllers[channel].rawData) {
							if (inputPlugin != null)
								inputPlugin.controllerCommand(channel, pifRam.slice());
						} else {
							processControllerCommand(channel, pifRam.slice());
						}
						pifRam.position(0);
					} else if (channel == 4) {
						pifRam.position(curPos);
						if (regs.saveUsing == Registers.AUTO)
							regs.saveUsing = Registers.EEPROM_4K;
						eeprom.eepromCommand(pifRam.slice(), regs.saveUsing, showPifRamErrors);
						pifRam.position(0);
					} else {
						System.err.printf("Command on channel 5?\n");
					}
					curPos += (pifRam.get(curPos) & 0xFF) + (pifRam.get(curPos + 1) & 0x3F) + 1;
					channel += 1;
				} else {
					if (showPifRamErrors)
						System.err.printf("Unknown Command in PifRamWrite(%X)\n", pifRam.get(curPos));
					curPos = 0x40;
				}
				break;
			}
		}
		pifRam.put(0x3F, (byte) 0);

		if (inputPlugin != null)
			inputPlugin.controllerCommand(-1, null);
	}

	private void processControllerCommand(int control, ByteBuffer command) {
		switch (command.get(2) & 0xFF) {
		case 0x00: // check
		case 0xFF: // reset & check ?
			if ((command.get(1) & 0x80) != 0) {
				break;
			}
			if (controllers[control].present) {
				command.put(3, (byte) 0x05);
				command.put(4, (byte) 0x00);
				switch (controllers[control].plugin) {
				case InputPlugin.PLUGIN_MEMPAK:
					command.put(5, (byte) 1);
					break;
				case InputPlugin.PLUGIN_RAW:
					command.put(5, (byte) 1);
					break;
				default:
					command.put(5, (byte) 0);
					break;
				}
			} else {
				command.put(1, (byte) ((command.get(1) & 0xFF) | 0x80));
			}
			break;
		case 0x01: // read controller
			if (!controllers[control].present) {
				command.put(1, (byte) ((command.get(1) & 0xFF) | 0x80));
			}
			break;
		case 0x02: // read from controller pack
			if (controllers[control].present) {
				int address = (((command.get(3) & 0xFF) << 8) | (command.get(4) & 0xFF));
				switch (controllers[control].plugin) {
				case InputPlugin.PLUGIN_MEMPAK: {
					command.position(5);
					mempak.readFromMempak(control, address, command.slice());
					break;
				}
				case InputPlugin.PLUGIN_RAW: {
					if (inputPlugin != null)
						inputPlugin.controllerCommand(control, command);
					break;
				}
				default:
					Arrays.fill(command.array(), 5, 5 + 0x20, (byte) 0);
					command.put(0x25, (byte) 0);
				}
			} else {
				command.put(1, (byte) ((command.get(1) & 0xFF) | 0x80));
			}
			break;
		case 0x03: // write controller pak
			if (controllers[control].present) {
				int address = (((command.get(3) & 0xFF) << 8) | (command.get(4) & 0xFF));
				switch (controllers[control].plugin) {
				case InputPlugin.PLUGIN_MEMPAK: {
					command.position(5);
					mempak.writeToMempak(control, address, command.slice());
					break;
				}
				case InputPlugin.PLUGIN_RAW: {
					if (inputPlugin != null)
						inputPlugin.controllerCommand(control, command);
					break;
				}
				default:
					command.position(5);
					command.put(0x25, mempak.mempacksCalulateCrc(command.slice()));
				}
			} else {
				command.put(1, (byte) ((command.get(1) & 0xFF) | 0x80));
			}
			break;
		default:
			if (showPifRamErrors)
				System.err.printf("Unknown ControllerCommand %d\n", command.get(2));
		}
	}

	private void readControllerCommand(int control, ByteBuffer command) {
		switch (command.get(2) & 0xFF) {
		case 0x01: // read controller
			if (controllers[control].present) {
				if (inputPlugin != null) {
					InputPlugin.Buttons Keys = new InputPlugin.Buttons();
					inputPlugin.getKeys(control, Keys);
					command.putInt(3, Keys.value);
				} else {
					command.putInt(3, 0);
				}
			}
			break;
		case 0x02: // read from controller pack
			if (controllers[control].present) {
				switch (controllers[control].plugin) {
				case InputPlugin.PLUGIN_RAW: {
					if (inputPlugin != null)
						inputPlugin.readController(control, command);
					break;
				}
				}
			}
			break;
		case 0x03: // write controller pak
			if (controllers[control].present) {
				switch (controllers[control].plugin) {
				case InputPlugin.PLUGIN_RAW: {
					if (inputPlugin != null)
						inputPlugin.readController(control, command);
					break;
				}
				}
			}
			break;
		}
	}

//    private void shutdownPlugins() {
//        if (controllerPlugin != null) { controllerPlugin.CloseDLL(); }
//    }

}
