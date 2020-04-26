package me.hydos.J64.audio;

import static me.hydos.J64.audio.HLEMain.inst1;
import static me.hydos.J64.audio.HLEMain.inst2;
import static me.hydos.J64.audio.HLEMain.rdram;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class ABI3 extends ABI1 {

	public static Runnable SETVOL3 = new Runnable() {
		public void run() {
			int flags = (inst1 >>> 16) & 0xFF;
			if ((flags & 0x4) != 0) { // 288
				if ((flags & 0x2) != 0) { // 290
					Vol_Left = (short) inst1; // 0x50
					Env_Dry = (short) ((int) inst2 >> 16); // 0x4E
					Env_Wet = (short) inst2; // 0x4C
				} else {
					VolTrg_Right = (short) inst1; // 0x46
					VolRamp_Right = (int) inst2; // 0x48/0x4A
				}
			} else {
				VolTrg_Left = (short) inst1; // 0x40
				VolRamp_Left = (int) inst2; // 0x42/0x44
			}
		}
	};

	public static Runnable ENVMIXER3 = new Runnable() {
		public void run() {
			int flags = (inst1 >>> 16) & 0xFF;
			int addy = inst2 & 0xFFFFFF;
			ShortBuffer inp = BufferSpaceShort;
			int inp_p = 0x4F0 / 2;
			ShortBuffer out = BufferSpaceShort;
			int out_p = 0x9D0 / 2;
			ShortBuffer aux1 = BufferSpaceShort;
			int aux1_p = 0xB40 / 2;
			ShortBuffer aux2 = BufferSpaceShort;
			int aux2_p = 0xCB0 / 2;
			ShortBuffer aux3 = BufferSpaceShort;
			int aux3_p = 0xE20 / 2;
			int MainR;
			int MainL;
			int AuxR;
			int AuxL;
			int i1, o1, a1, a2, a3;
			int LAdder, LAcc, LVol;
			int RAdder, RAcc, RVol;
			short RSig, LSig; // Most significant part of the Ramp Value
			short Wet, Dry;
			short LTrg, RTrg;

			Vol_Right = (short) inst1;

			if ((flags & A_INIT) != 0) {
				LAdder = VolRamp_Left / 8;
				LAcc = 0;
				LVol = Vol_Left;
				LSig = (short) (VolRamp_Left >> 16);
				RAdder = VolRamp_Right / 8;
				RAcc = 0;
				RVol = Vol_Right;
				RSig = (short) (VolRamp_Right >> 16);
				Wet = (short) Env_Wet;
				Dry = (short) Env_Dry;
				LTrg = VolTrg_Left;
				RTrg = VolTrg_Right;
			} else {
				System.arraycopy(rdram.array(), addy, hleMixerWorkArea.array(), 0, 80);
				Wet = hleMixerWorkArea.getShort(0 + 2); // 0-1
				Dry = hleMixerWorkArea.getShort(4 + 2); // 2-3
				LTrg = hleMixerWorkArea.getShort(8 + 2); // 4-5
				RTrg = hleMixerWorkArea.getShort(12 + 2); // 6-7
				LAdder = hleMixerWorkArea.getInt(16); // 8-9 (hleMixerWorkArea is a 16bit pointer)
				RAdder = hleMixerWorkArea.getInt(20); // 10-11
				LAcc = hleMixerWorkArea.getInt(24); // 12-13
				RAcc = hleMixerWorkArea.getInt(28); // 14-15
				LVol = hleMixerWorkArea.getInt(32); // 16-17
				RVol = hleMixerWorkArea.getInt(36); // 18-19
				LSig = hleMixerWorkArea.getShort(40 + 2); // 20-21
				RSig = hleMixerWorkArea.getShort(44 + 2); // 22-23
			}

			for (int y = 0; y < (0x170 / 2); y++) {
				// Left
				LAcc += LAdder;
				LVol += (LAcc >> 16);
				LAcc &= 0xFFFF;
				// Right
				RAcc += RAdder;
				RVol += (RAcc >> 16);
				RAcc &= 0xFFFF;

				// Clamp Left
				if (LSig >= 0) { // VLT
					if (LVol > LTrg) {
						LVol = LTrg;
					}
				} else { // VGE
					if (LVol < LTrg) {
						LVol = LTrg;
					}
				}

				// Clamp Right
				if (RSig >= 0) { // VLT
					if (RVol > RTrg) {
						RVol = RTrg;
					}
				} else { // VGE
					if (RVol < RTrg) {
						RVol = RTrg;
					}
				}

				MainL = ((Dry * LVol) + 0x4000) >> 15;
				MainR = ((Dry * RVol) + 0x4000) >> 15;

				o1 = out.get(out_p + (y ^ 1));
				a1 = aux1.get(aux1_p + (y ^ 1));
				i1 = inp.get(inp_p + (y ^ 1));

				o1 += ((i1 * MainL) + 0x4000) >> 15;
				a1 += ((i1 * MainR) + 0x4000) >> 15;

				if (o1 > 32767) {
					o1 = 32767;
				} else if (o1 < -32768) {
					o1 = -32768;
				}

				if (a1 > 32767) {
					a1 = 32767;
				} else if (a1 < -32768) {
					a1 = -32768;
				}

				out.put(out_p + (y ^ 1), (short) o1);
				aux1.put(aux1_p + (y ^ 1), (short) a1);

				a2 = aux2.get(aux2_p + (y ^ 1));
				a3 = aux3.get(aux3_p + (y ^ 1));

				AuxL = ((Wet * LVol) + 0x4000) >> 15;
				AuxR = ((Wet * RVol) + 0x4000) >> 15;

				a2 += ((i1 * AuxL) + 0x4000) >> 15;
				a3 += ((i1 * AuxR) + 0x4000) >> 15;

				if (a2 > 32767) {
					a2 = 32767;
				} else if (a2 < -32768) {
					a2 = -32768;
				}

				if (a3 > 32767) {
					a3 = 32767;
				} else if (a3 < -32768) {
					a3 = -32768;
				}

				aux2.put(aux2_p + (y ^ 1), (short) a2);
				aux3.put(aux3_p + (y ^ 1), (short) a3);
			}

			hleMixerWorkArea.putShort(0 + 2, Wet); // 0-1
			hleMixerWorkArea.putShort(4 + 2, Dry); // 2-3
			hleMixerWorkArea.putShort(8 + 2, LTrg); // 4-5
			hleMixerWorkArea.putShort(12 + 2, RTrg); // 6-7
			hleMixerWorkArea.putInt(16, LAdder); // 8-9 (hleMixerWorkArea is a 16bit pointer)
			hleMixerWorkArea.putInt(20, RAdder); // 10-11
			hleMixerWorkArea.putInt(24, LAcc); // 12-13
			hleMixerWorkArea.putInt(28, RAcc); // 14-15
			hleMixerWorkArea.putInt(32, LVol); // 16-17
			hleMixerWorkArea.putInt(36, RVol); // 18-19
			hleMixerWorkArea.putShort(40 + 2, LSig); // 20-21
			hleMixerWorkArea.putShort(44 + 2, RSig); // 22-23
			System.arraycopy(hleMixerWorkArea.array(), 0, rdram.array(), addy, 80);
		}
	};

	public static Runnable CLEARBUFF3 = new Runnable() {
		public void run() {
			int addr = (inst1 & 0xFFFF) + 0x4F0;
			int count = inst2 & 0xFFFF;
			Arrays.fill(BufferSpace.array(), addr, addr + count, (byte) 0);
		}
	};

	public static Runnable MIXER3 = new Runnable() {
		public void run() {
			int dmemin = ((inst2 >>> 16) + 0x4F0) & 0xFFFF;
			int dmemout = ((inst2 & 0xFFFF) + 0x4F0) & 0xFFFF;
			int gain = (short) (inst1 & 0xFFFF) * 2;
			int temp;

			for (int x = 0; x < 0x170; x += 2) {
				temp = (BufferSpace.getShort(dmemin + x) * gain) >> 16;
				temp += BufferSpace.getShort(dmemout + x);

				if (temp > 32767) {
					temp = 32767;
				}
				if (temp < -32768) {
					temp = -32768;
				}

				BufferSpace.putShort(dmemout + x, (short) temp);
			}
		}
	};

	public static Runnable LOADBUFF3 = new Runnable() {
		public void run() {
			int cnt = ((inst1 >>> 0xC) + 3) & 0xFFC;
			int v0 = inst2 & 0xFFFFFC;
			System.arraycopy(rdram.array(), v0, BufferSpace.array(), (inst1 & 0xFFC) + 0x4F0, cnt);
		}
	};
	public static Runnable SAVEBUFF3 = new Runnable() {
		public void run() {
			int cnt = (((inst1 >>> 0xC) + 3) & 0xFFC);
			int v0 = inst2 & 0xFFFFFC;
			System.arraycopy(BufferSpace.array(), (inst1 & 0xFFC) + 0x4F0, rdram.array(), v0, cnt);
		}
	};

	public static Runnable LOADADPCM3 = new Runnable() {
		public void run() {
			int v0 = inst2 & 0xFFFFFF;
			int cnt = (inst1 & 0xFFFF) >> 4;
			for (int x = 0; x < cnt; x++) {
				adpcmtable[0 + (x << 3)] = rdram.getShort(v0 + 0);
				adpcmtable[1 + (x << 3)] = rdram.getShort(v0 + 2);
				adpcmtable[2 + (x << 3)] = rdram.getShort(v0 + 4);
				adpcmtable[3 + (x << 3)] = rdram.getShort(v0 + 6);
				adpcmtable[4 + (x << 3)] = rdram.getShort(v0 + 8);
				adpcmtable[5 + (x << 3)] = rdram.getShort(v0 + 10);
				adpcmtable[6 + (x << 3)] = rdram.getShort(v0 + 12);
				adpcmtable[7 + (x << 3)] = rdram.getShort(v0 + 14);
				v0 += 16;
			}
		}
	};

	public static Runnable DMEMMOVE3 = new Runnable() {
		public void run() {
			int v0 = (inst1 & 0xFFFF) + 0x4F0;
			int v1 = (inst2 >>> 16) + 0x4F0;
			int count = (inst2 + 3) & 0xFFFC;
			System.arraycopy(BufferSpace.array(), v0, BufferSpace.array(), v1, count);
		}
	};

	public static Runnable SETLOOP3 = new Runnable() {
		public void run() {
			loopval = inst2 & 0xFFFFFF;
		}
	};

	public static Runnable ADPCM3 = new Runnable() {
		public void run() {
			int flags = (inst2 >>> 0x1C) & 0xFF;
			int Address = inst1 & 0xFFFFFF;
			int inPtr = (inst2 >>> 12) & 0xF;
			ByteBuffer out = BufferSpace;
			int out_p = (inst2 & 0xFFF) + 0x4F0;
			short count = (short) ((inst2 >>> 16) & 0xFFF);
			int icode;
			int code;
			int vscale;
			int index;
			int j;
			int[] a = new int[8];
			int book1_p, book2_p;

			Arrays.fill(out.array(), out_p, out_p + 32, (byte) 0);

			if ((flags & 0x1) == 0) {
				if ((flags & 0x2) != 0) {
					System.arraycopy(rdram.array(), loopval, out.array(), out_p, 32);
				} else {
					System.arraycopy(rdram.array(), Address, out.array(), out_p, 32);
				}
			}

			int l1 = out.getShort(out_p + 28);
			int l2 = out.getShort(out_p + 30);
			int[] inp1 = new int[8];
			int[] inp2 = new int[8];
			out_p += 32;
			while (count > 0) {
				// the first interation through, these values are
				// either 0 in the case of A_INIT, from a special
				// area of memory in the case of A_LOOP or just
				// the values we calculated the last time

				code = BufferSpace.get((0x4F0 + inPtr)) & 0xFF;
				index = code & 0xF;
				index <<= 4; // index into the adpcm code table
				book1_p = index;
				book2_p = book1_p + 8;
				code >>= 4; // upper nibble is scale
				vscale = (0x8000 >> ((12 - code) - 1)); // very strange. 0x8000 would be .5 in 16:16 format
														// so this appears to be a fractional scale based
														// on the 12 based inverse of the scale value. note
														// that this could be negative, in which case we do
														// not use the calculated vscale value... see the
														// if(code>12) check below
				inPtr++; // coded adpcm data lies next
				j = 0;
				while (j < 8) { // loop of 8, for 8 coded nibbles from 4 bytes which yields 8 short pcm values
					icode = BufferSpace.get((0x4F0 + inPtr)) & 0xFF;
					inPtr++;

					inp1[j] = (short) ((icode & 0xF0) << 8); // this will in effect be signed
					if (code < 12) {
						inp1[j] = ((int) ((int) inp1[j] * (int) vscale) >> 16);
					}
					j++;

					inp1[j] = (short) ((icode & 0xF) << 12);
					if (code < 12) {
						inp1[j] = ((int) ((int) inp1[j] * (int) vscale) >> 16);
					}
					j++;
				}
				j = 0;
				while (j < 8) {
					icode = BufferSpace.get((0x4F0 + inPtr)) & 0xFF;
					inPtr++;

					inp2[j] = (short) ((icode & 0xF0) << 8); // this will in effect be signed
					if (code < 12) {
						inp2[j] = ((int) ((int) inp2[j] * (int) vscale) >> 16);
					}
					j++;

					inp2[j] = (short) ((icode & 0xF) << 12);
					if (code < 12) {
						inp2[j] = ((int) ((int) inp2[j] * (int) vscale) >> 16);
					}
					j++;
				}

				a[0] = (int) adpcmtable[book1_p + 0] * (int) l1;
				a[0] += (int) adpcmtable[book2_p + 0] * (int) l2;
				a[0] += (int) inp1[0] * (int) 2048;

				a[1] = (int) adpcmtable[book1_p + 1] * (int) l1;
				a[1] += (int) adpcmtable[book2_p + 1] * (int) l2;
				a[1] += (int) adpcmtable[book2_p + 0] * inp1[0];
				a[1] += (int) inp1[1] * (int) 2048;

				a[2] = (int) adpcmtable[book1_p + 2] * (int) l1;
				a[2] += (int) adpcmtable[book2_p + 2] * (int) l2;
				a[2] += (int) adpcmtable[book2_p + 1] * inp1[0];
				a[2] += (int) adpcmtable[book2_p + 0] * inp1[1];
				a[2] += (int) inp1[2] * (int) 2048;

				a[3] = (int) adpcmtable[book1_p + 3] * (int) l1;
				a[3] += (int) adpcmtable[book2_p + 3] * (int) l2;
				a[3] += (int) adpcmtable[book2_p + 2] * inp1[0];
				a[3] += (int) adpcmtable[book2_p + 1] * inp1[1];
				a[3] += (int) adpcmtable[book2_p + 0] * inp1[2];
				a[3] += (int) inp1[3] * (int) 2048;

				a[4] = (int) adpcmtable[book1_p + 4] * (int) l1;
				a[4] += (int) adpcmtable[book2_p + 4] * (int) l2;
				a[4] += (int) adpcmtable[book2_p + 3] * inp1[0];
				a[4] += (int) adpcmtable[book2_p + 2] * inp1[1];
				a[4] += (int) adpcmtable[book2_p + 1] * inp1[2];
				a[4] += (int) adpcmtable[book2_p + 0] * inp1[3];
				a[4] += (int) inp1[4] * (int) 2048;

				a[5] = (int) adpcmtable[book1_p + 5] * (int) l1;
				a[5] += (int) adpcmtable[book2_p + 5] * (int) l2;
				a[5] += (int) adpcmtable[book2_p + 4] * inp1[0];
				a[5] += (int) adpcmtable[book2_p + 3] * inp1[1];
				a[5] += (int) adpcmtable[book2_p + 2] * inp1[2];
				a[5] += (int) adpcmtable[book2_p + 1] * inp1[3];
				a[5] += (int) adpcmtable[book2_p + 0] * inp1[4];
				a[5] += (int) inp1[5] * (int) 2048;

				a[6] = (int) adpcmtable[book1_p + 6] * (int) l1;
				a[6] += (int) adpcmtable[book2_p + 6] * (int) l2;
				a[6] += (int) adpcmtable[book2_p + 5] * inp1[0];
				a[6] += (int) adpcmtable[book2_p + 4] * inp1[1];
				a[6] += (int) adpcmtable[book2_p + 3] * inp1[2];
				a[6] += (int) adpcmtable[book2_p + 2] * inp1[3];
				a[6] += (int) adpcmtable[book2_p + 1] * inp1[4];
				a[6] += (int) adpcmtable[book2_p + 0] * inp1[5];
				a[6] += (int) inp1[6] * (int) 2048;

				a[7] = (int) adpcmtable[book1_p + 7] * (int) l1;
				a[7] += (int) adpcmtable[book2_p + 7] * (int) l2;
				a[7] += (int) adpcmtable[book2_p + 6] * inp1[0];
				a[7] += (int) adpcmtable[book2_p + 5] * inp1[1];
				a[7] += (int) adpcmtable[book2_p + 4] * inp1[2];
				a[7] += (int) adpcmtable[book2_p + 3] * inp1[3];
				a[7] += (int) adpcmtable[book2_p + 2] * inp1[4];
				a[7] += (int) adpcmtable[book2_p + 1] * inp1[5];
				a[7] += (int) adpcmtable[book2_p + 0] * inp1[6];
				a[7] += (int) inp1[7] * (int) 2048;

				for (j = 0; j < 8; j++) {
					a[j] >>= 11;
					if (a[j] > 32767) {
						a[j] = 32767;
					} else if (a[j] < -32768) {
						a[j] = -32768;
					}
					out.putShort(out_p, (short) a[j]);
					out_p += 2;
				}
				l1 = a[6];
				l2 = a[7];

				a[0] = (int) adpcmtable[book1_p + 0] * (int) l1;
				a[0] += (int) adpcmtable[book2_p + 0] * (int) l2;
				a[0] += (int) inp2[0] * (int) 2048;

				a[1] = (int) adpcmtable[book1_p + 1] * (int) l1;
				a[1] += (int) adpcmtable[book2_p + 1] * (int) l2;
				a[1] += (int) adpcmtable[book2_p + 0] * inp2[0];
				a[1] += (int) inp2[1] * (int) 2048;

				a[2] = (int) adpcmtable[book1_p + 2] * (int) l1;
				a[2] += (int) adpcmtable[book2_p + 2] * (int) l2;
				a[2] += (int) adpcmtable[book2_p + 1] * inp2[0];
				a[2] += (int) adpcmtable[book2_p + 0] * inp2[1];
				a[2] += (int) inp2[2] * (int) 2048;

				a[3] = (int) adpcmtable[book1_p + 3] * (int) l1;
				a[3] += (int) adpcmtable[book2_p + 3] * (int) l2;
				a[3] += (int) adpcmtable[book2_p + 2] * inp2[0];
				a[3] += (int) adpcmtable[book2_p + 1] * inp2[1];
				a[3] += (int) adpcmtable[book2_p + 0] * inp2[2];
				a[3] += (int) inp2[3] * (int) 2048;

				a[4] = (int) adpcmtable[book1_p + 4] * (int) l1;
				a[4] += (int) adpcmtable[book2_p + 4] * (int) l2;
				a[4] += (int) adpcmtable[book2_p + 3] * inp2[0];
				a[4] += (int) adpcmtable[book2_p + 2] * inp2[1];
				a[4] += (int) adpcmtable[book2_p + 1] * inp2[2];
				a[4] += (int) adpcmtable[book2_p + 0] * inp2[3];
				a[4] += (int) inp2[4] * (int) 2048;

				a[5] = (int) adpcmtable[book1_p + 5] * (int) l1;
				a[5] += (int) adpcmtable[book2_p + 5] * (int) l2;
				a[5] += (int) adpcmtable[book2_p + 4] * inp2[0];
				a[5] += (int) adpcmtable[book2_p + 3] * inp2[1];
				a[5] += (int) adpcmtable[book2_p + 2] * inp2[2];
				a[5] += (int) adpcmtable[book2_p + 1] * inp2[3];
				a[5] += (int) adpcmtable[book2_p + 0] * inp2[4];
				a[5] += (int) inp2[5] * (int) 2048;

				a[6] = (int) adpcmtable[book1_p + 6] * (int) l1;
				a[6] += (int) adpcmtable[book2_p + 6] * (int) l2;
				a[6] += (int) adpcmtable[book2_p + 5] * inp2[0];
				a[6] += (int) adpcmtable[book2_p + 4] * inp2[1];
				a[6] += (int) adpcmtable[book2_p + 3] * inp2[2];
				a[6] += (int) adpcmtable[book2_p + 2] * inp2[3];
				a[6] += (int) adpcmtable[book2_p + 1] * inp2[4];
				a[6] += (int) adpcmtable[book2_p + 0] * inp2[5];
				a[6] += (int) inp2[6] * (int) 2048;

				a[7] = (int) adpcmtable[book1_p + 7] * (int) l1;
				a[7] += (int) adpcmtable[book2_p + 7] * (int) l2;
				a[7] += (int) adpcmtable[book2_p + 6] * inp2[0];
				a[7] += (int) adpcmtable[book2_p + 5] * inp2[1];
				a[7] += (int) adpcmtable[book2_p + 4] * inp2[2];
				a[7] += (int) adpcmtable[book2_p + 3] * inp2[3];
				a[7] += (int) adpcmtable[book2_p + 2] * inp2[4];
				a[7] += (int) adpcmtable[book2_p + 1] * inp2[5];
				a[7] += (int) adpcmtable[book2_p + 0] * inp2[6];
				a[7] += (int) inp2[7] * (int) 2048;

				for (j = 0; j < 8; j++) {
					a[j] >>= 11;
					if (a[j] > 32767) {
						a[j] = 32767;
					} else if (a[j] < -32768) {
						a[j] = -32768;
					}
					out.putShort(out_p, (short) a[j]);
					out_p += 2;
				}
				l1 = a[6];
				l2 = a[7];

				count -= 32;
			}
			out_p -= 32;
			System.arraycopy(out.array(), out_p, rdram.array(), Address, 32);
		}
	};

	public static Runnable RESAMPLE3 = new Runnable() {
		public void run() {
			int flags = ((inst2 >>> 0x1E)) & 0xFF;
			int Pitch = ((inst2 >>> 0xE) & 0xFFFF) << 1;
			int addy = inst1 & 0xFFFFFF;
			int Accum = 0;
			int location;
			ShortBuffer dst = BufferSpaceShort;
			ShortBuffer src = BufferSpaceShort;
			int srcPtr = (((inst2 >>> 2) & 0xFFF) + 0x4F0) / 2;
			int dstPtr;
			int temp;
			int accum;

			srcPtr -= 4;

			if ((inst2 & 0x3) != 0) {
				dstPtr = 0x660 / 2;
			} else {
				dstPtr = 0x4F0 / 2;
			}

			if ((flags & 0x1) == 0) {
				for (int x = 0; x < 4; x++) {
					src.put((srcPtr + x) ^ 1, rdram.getShort((((addy / 2) + x) ^ 1) * 2));
				}
				Accum = rdram.getShort(addy + 8) & 0xFFFF;
			} else {
				for (int x = 0; x < 4; x++) {
					src.put((srcPtr + x) ^ 1, (short) 0);
				}
			}

			for (int i = 0; i < 0x170 / 2; i++) {
				location = (((Accum * 0x40) >>> 16) * 8) / 2;

				temp = ((int) src.get((srcPtr + 0) ^ 1) * ((int) ((short) ResampleLUT[location + 0])));
				accum = (temp >> 15);

				temp = ((int) src.get((srcPtr + 1) ^ 1) * ((int) ((short) ResampleLUT[location + 1])));
				accum += (temp >> 15);

				temp = ((int) src.get((srcPtr + 2) ^ 1) * ((int) ((short) ResampleLUT[location + 2])));
				accum += (temp >> 15);

				temp = ((int) src.get((srcPtr + 3) ^ 1) * ((int) ((short) ResampleLUT[location + 3])));
				accum += (temp >> 15);

				if (accum > 32767) {
					accum = 32767;
				}
				if (accum < -32768) {
					accum = -32768;
				}

				dst.put(dstPtr ^ 1, (short) accum);
				dstPtr++;
				Accum += Pitch;
				srcPtr += (Accum >>> 16);
				Accum &= 0xFFFF;
			}

			for (int x = 0; x < 4; x++) {
				rdram.putShort((((addy / 2) + x) ^ 1) * 2, src.get((srcPtr + x) ^ 1));
			}
			rdram.putShort(addy + 8, (short) Accum);
		}
	};

	public static Runnable INTERLEAVE3 = new Runnable() {
		public void run() {
			ShortBuffer outbuff = BufferSpaceShort;
			int outbuff_p = 0x4F0 / 2;
			ShortBuffer inSrcR;
			ShortBuffer inSrcL;
			int inSrcR_p;
			int inSrcL_p;

			inSrcR = BufferSpaceShort;
			inSrcR_p = 0xB40 / 2;
			inSrcL = BufferSpaceShort;
			inSrcL_p = 0x9D0 / 2;

			for (int x = 0; x < (0x170 / 4); x++) {
				outbuff.put(outbuff_p++, inSrcL.get(inSrcL_p++));
				outbuff.put(outbuff_p++, inSrcR.get(inSrcR_p++));
				outbuff.put(outbuff_p++, inSrcL.get(inSrcL_p++));
				outbuff.put(outbuff_p++, inSrcR.get(inSrcR_p++));
			}
		}
	};

	public static Runnable WHATISTHIS = new Runnable() {
		public void run() {
//            System.out.println("WHATISTHIS");
		}
	};

	public static Runnable MP3ADDY = new Runnable() {
		public void run() {
		}
	};

	public static Runnable MP3 = new Runnable() {
		public void run() {
//            System.out.println("MP3");
		}
	};

	public static Runnable DISABLE = new Runnable() {
		public void run() {
			// MessageBox (NULL, "Help", "ABI 3 Command 0", MB_OK);
			// ChangeABI (5);
		}
	};

	public static Runnable[] ABI3 = { DISABLE, ADPCM3, CLEARBUFF3, ENVMIXER3, LOADBUFF3, RESAMPLE3, SAVEBUFF3, MP3,
			MP3ADDY, SETVOL3, DMEMMOVE3, LOADADPCM3, MIXER3, INTERLEAVE3, WHATISTHIS, SETLOOP3, SPNOOP, SPNOOP, SPNOOP,
			SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP, SPNOOP };

}
