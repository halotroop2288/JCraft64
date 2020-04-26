package me.hydos.J64.audio;

import java.nio.ByteBuffer;

/**
 *
 * @author Jason
 */
public class HLEMain {

	public static ByteBuffer dmem;
	public static ByteBuffer rdram;
	public static int inst2, inst1;

	static int UCData, UDataLen;

	// Audio UCode lists
	// Dummy UCode Handler for UCode detection... (Will always assume UCode1 until
	// the nth list is executed)
	// extern void (*SafeABI[0x20])();
	// ---------------------------------------------------------------------------------------------
	//
	// ABI 1 : Mario64, WaveRace USA, Golden Eye 007, Quest64, SF Rush
	// 60% of all games use this. Distributed 3rd Party ABI
	//
	// extern void (*ABI1[0x20])();
	// ---------------------------------------------------------------------------------------------
	//
	// ABI 2 : WaveRace JAP, MarioKart 64, Mario64 JAP RumbleEdition,
	// Yoshi Story, Pokemon Games, Zelda64, Zelda MoM (miyamoto)
	// Most NCL or NOA games (Most commands)
	// extern void (*ABI2[0x20])();
	// ---------------------------------------------------------------------------------------------
	//
	// ABI 3 : DK64, Perfect Dark, Banjo Kazooi, Banjo Tooie
	// All RARE games except Golden Eye 007
	//
	// extern void (*ABI3[0x20])();
	// ---------------------------------------------------------------------------------------------
	//
	// ABI 5 : Factor 5 - MoSys/MusyX
	// Rogue Squadron, Tarzan, Hydro Thunder, and TWINE
	// Indiana Jones and Battle for Naboo (?)
	// ---------------------------------------------------------------------------------------------
	//
	// ABI ? : Unknown or unsupported UCode
	//
	// extern void (*ABIUnknown[0x20])();
	// ---------------------------------------------------------------------------------------------

	public static Runnable[] ABI = ABI1.ABI1;

	public static Runnable SPU = new Runnable() {
		public void run() {
		}
	};

	public HLEMain() {
	}

	private static int audio_ucode_detect() {
		if (rdram.getInt(UCData + 0) != 0x1) {
			if ((rdram.get(UCData + 3) & 0xFF) == 0xF) {
				return 4;
			} else {
				return 3;
			}
		} else {
			if (rdram.getInt(UCData + 0x30) == 0xF0000F00) {
				return 1;
			} else {
				return 2;
			}
		}
	}

	public static void HLEStart() {
		int List = dmem.getInt(0xFF0); // address
		int ListLen = dmem.getInt(0xFF4);
		byte[] ram = rdram.array();

		UCData = dmem.getInt(0xFD8);
		ABI1.loopval = 0;
		ListLen = ListLen >>> 2;

		switch (audio_ucode_detect()) {
		case 1: // mario ucode
			ABI = ABI1.ABI1;
//                System.out.println("Audio ucode: ABI1");
			break;
		case 2: // banjo kazooie ucode
			ABI = ABI2.ABI2;
//                System.out.println("Audio ucode: ABI2");
			break;
		case 3: // zelda ucode
			ABI = ABI3.ABI3;
//                System.out.println("Audio ucode: ABI3");
			break;
		default: {
			System.out.println("unknown audio ucode: " + audio_ucode_detect());
			return;
		}
		}

		for (int x = 0; x < ListLen; x += 2) {
			int pAddr = List + (x << 2);
			inst1 = (ram[pAddr] << 24) | ((ram[pAddr + 1] & 0xff) << 16) | ((ram[pAddr + 2] & 0xff) << 8)
					| (ram[pAddr + 3] & 0xff);
			inst2 = (ram[pAddr + 4] << 24) | ((ram[pAddr + 5] & 0xff) << 16) | ((ram[pAddr + 6] & 0xff) << 8)
					| (ram[pAddr + 7] & 0xff);
			ABI[inst1 >>> 24].run();
		}
	}

	public static Runnable[] ABIUnknown = { // Unknown ABI
			SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU,
			SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU, SPU };

}
