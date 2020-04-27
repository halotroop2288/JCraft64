package me.hydos.J64.audio;

import java.nio.ByteBuffer;

public class HLEMain {

	public static ByteBuffer dmem;
	public static ByteBuffer rdram;
	public static int inst2, inst1;

	static int UCData, UDataLen;

	public static Runnable[] ABI = ABI1.ABI1;

	public static Runnable SPU = new Runnable() {
		public void run() {
		}
	};

	public HLEMain() {
	}

	private static int audio_ucode_detect() {
		if (rdram.getInt(UCData) != 0x1) {
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
			case 1 -> ABI = ABI1.ABI1;
			case 2 -> ABI = ABI2.ABI2;
			case 3 -> ABI = ABI3.ABI3;
			default -> {
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
