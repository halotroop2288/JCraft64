package me.hydos.J64.gln64.rsp;

import me.hydos.J64.emu.util.debug.Debug;
import me.hydos.J64.gfx.GLN64jPlugin;
import me.hydos.J64.gfx.Gbi;
import me.hydos.J64.gfx.Rsp;
import me.hydos.J64.gfx.opcodes.F3DBETA;
import me.hydos.J64.gfx.opcodes.F3DEX2Acclaim;
import me.hydos.J64.gfx.opcodes.F3DEX2CBFD;
import me.hydos.J64.gfx.opcodes.F3DFLX2;
import me.hydos.J64.gfx.opcodes.F3DGolden;
import me.hydos.J64.gfx.opcodes.F3DSETA;
import me.hydos.J64.gfx.opcodes.F3DTEXA;
import me.hydos.J64.gfx.opcodes.F3d;
import me.hydos.J64.gfx.opcodes.F3dam;
import me.hydos.J64.gfx.opcodes.F3ddkr;
import me.hydos.J64.gfx.opcodes.F3dex;
import me.hydos.J64.gfx.opcodes.F3dex2;
import me.hydos.J64.gfx.opcodes.F3dpd;
import me.hydos.J64.gfx.opcodes.F3dzex2;
import me.hydos.J64.gfx.opcodes.F5IndiNaboo;
import me.hydos.J64.gfx.opcodes.F5Rogue;
import me.hydos.J64.gfx.opcodes.L3d;
import me.hydos.J64.gfx.opcodes.L3dex;
import me.hydos.J64.gfx.opcodes.L3dex2;
import me.hydos.J64.gfx.opcodes.Rdpfuncs2;
import me.hydos.J64.gfx.opcodes.S2dex;
import me.hydos.J64.gfx.opcodes.S2dex2;
import me.hydos.J64.gfx.opcodes.ZSort;
import me.hydos.J64.gfx.opcodes.ZSortBOSS;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;
import me.hydos.J64.gfx.rsp.Microcode;

import java.util.Arrays;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.swing.JOptionPane;

public class Microcodes {

    public GBIFunc[] cmds = new GBIFunc[256];
    public Microcode current;

    private int numMicrocodes;
    private Microcode top;
    private Microcode bottom;
    private final Checksum crc32 = new Adler32();
    private int uc_dcrc;
    private boolean m_hwlSupported = false;

    // Reference: https://github.com/gonetz/GLideN64/blob/master/src/GBI.cpp
    private final Microcode[] specialMicrocodes = {
		new Microcode(Microcode.S2DEX2,			false,	0x02c399dd, "Animal Forest"),
		new Microcode(Microcode.F3DEX,			false,	0x0ace4c3f, "Mario Kart 64"),
		new Microcode(Microcode.F3D,			true,	0x16c3a775, "AeroFighters"),
		new Microcode(Microcode.F3DEX2CBFD,		true,	0x1b4ace88, "Conker's Bad Fur Day"),
		new Microcode(Microcode.F3DPD,			true,	0x1c4f7869, "Perfect Dark"),
		new Microcode(Microcode.F3D,			false,	0x1f24cc84, "Wayne Gretzky's 3D Hockey (U)"),
		new Microcode(Microcode.F5INDI_NABOO,	false,	0x23fef05f, "SW Ep.1 Battle for Naboo"),
		new Microcode(Microcode.TURBO3D,		false,	0x2bdcfc8a, "Dark Rift, Turbo3D"),
		new Microcode(Microcode.F3DSETA,		false,	0x2edee7be, "RSP SW Version: 2.0D, 04-01-96"),
		new Microcode(Microcode.F3DGOLDEN,		true,	0x302bca09, "GoldenEye"),
		new Microcode(Microcode.F3D,			false,	0x4AED6B3B, "Vivid Dolls [ALECK64]"),
		new Microcode(Microcode.F3D,			true,	0x54c558ba, "RSP SW Version: 2.0D, 04-01-96 Pilot Wings, Blast Corps"),
		new Microcode(Microcode.ZSORTBOSS,		false,	0x553538cc, "World Driver Championship"),
		new Microcode(Microcode.F3D,			false,	0x55be9bad, "Mischief Makers, Mortal Combat Trilogy, J.League Live"),
		new Microcode(Microcode.F3DEX,			true,	0x637b4b58, "Power League 64"),
		new Microcode(Microcode.F5INDI_NABOO,	false,	0x6859bf8e, "Indiana Jones"),
		new Microcode(Microcode.F3D,			false,	0x6932365f, "Super Mario 64"),
		new Microcode(Microcode.ZSORTBOSS,		false,	0x6a76f8dd, "Stunt Racer"),
		new Microcode(Microcode.F3DDKR,			false,	0x6e6fc893, "Diddy Kong Racing"),
		new Microcode(Microcode.ZSORTBOSS,		false,	0x75ed44cc, "World Driver Championship (E)"),
		new Microcode(Microcode.F3D,			true,	0x77195a68, "Dark Rift"),
		new Microcode(Microcode.L3D,			true,	0x771ce0c4, "Blast Corps"),
		new Microcode(Microcode.F3D,			false,	0x7d372819, "Pachinko nichi 365"),
		new Microcode(Microcode.F3DDKR,			false,	0x8d91244f, "Diddy Kong Racing"),
		new Microcode(Microcode.F3DBETA,		false,	0x94c4c833, "Star Wars Shadows of Empire"),
		new Microcode(Microcode.S2DEX_1_05,		false,	0x9df31081, "RSP Gfx ucode S2DEX  1.06 Yoshitaka Yasumoto Nintendo"),
		new Microcode(Microcode.T3DUX,			false,	0xbad437f2, "Toukon Road"),
		new Microcode(Microcode.F3DJFG,			false,	0xbde9d1fb, "Jet Force Gemini, Mickey"),
		new Microcode(Microcode.T3DUX,			false,	0xd0a1aa3d, "Toukon Road 2"),
        new Microcode(Microcode.F3DWRUS,		false,	0xd17906e2, "Wave Race (U)"),
        new Microcode(Microcode.F3DZEX2MM,		true,	0xd39a0d4f, "Animal Forest"),
        new Microcode(Microcode.F3D,			false,	0xd3ab59b2, "Cruis'n USA"),
        new Microcode(Microcode.F5ROGUE,		false,	0xda51ccdb, "Star Wars RS"),
        new Microcode(Microcode.F3D,			false,	0xe01e14be, "Eikou no Saint Andrews"),
        new Microcode(Microcode.F3DEX2ACCLAIM,	true,	0xe44df568, "Accaim Games"),
        new Microcode(Microcode.F3D,			false,	0xe62a706d, "Fast3D")
    };

    private static GBIFunc gbiUnknown = (w0, w1) -> {
        if (Debug.DEBUG_GBI)
            Debug.DebugMsg(Debug.DEBUG_LOW | Debug.DEBUG_UNKNOWN, "UNKNOWN GBI COMMAND 0x%02X\n", (w0 >> 24) & Gbi.SR_MASK_8);
    };

    public void init() {
        top = null;
        bottom = null;
        current = null;
        numMicrocodes = 0;

        for (int i = 0; i <= 0xFF; i++)
            cmds[i] = gbiUnknown;
    }

    public void makeCurrent(Microcode microcode) {
        if (microcode != top) {
            if (microcode == bottom) {
                bottom = microcode.higher;
                bottom.lower = null;
            } else {
                microcode.higher.lower = microcode.lower;
                microcode.lower.higher = microcode.higher;
            }

            microcode.higher = null;
            microcode.lower = top;
            top.higher = microcode;
            top = microcode;
        }

        if (current == null || (current.type != microcode.type)) {
            for (int i = 0; i <= 0xFF; i++)
                cmds[i] = gbiUnknown;

            Rdpfuncs2.RDP_Init(Rsp.gsp, Rsp.gdp);
            switch (microcode.type) {
            case Microcode.F3D:
				F3d.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DEX:
				F3dex.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
//				gsp.clipRatio = m_pCurrent->Rej ? 2 : 1;
			break;
			case Microcode.F3DEX2:
				F3dex2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
				Gsp.clipRatio = 2;
			break;
			case Microcode.L3D:
				L3d.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.L3DEX:
				L3dex.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
//				gsp.clipRatio = m_pCurrent->Rej ? 2 : 1;
			break;
			case Microcode.L3DEX2:
				L3dex2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				Gsp.clipRatio = 2;
			break;
			case Microcode.S2DEX_1_03:
				S2dex.S2DEX_1_03Init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				break;
			case Microcode.S2DEX_1_05:
				S2dex.S2DEX_1_05Init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				break;
			case Microcode.S2DEX_1_07:
				S2dex.S2DEX_1_07Init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				break;
			case Microcode.S2DEX2:
				S2dex2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				break;
			case Microcode.F3DDKR:
				F3ddkr.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.F3DJFG:
				F3ddkr.F3dJfgInit(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.F3DBETA:
				F3DBETA.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DPD:
				F3dpd.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DAM:
				F3dam.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.TURBO3D:
				F3d.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.ZSORTP:
				ZSort.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DEX2CBFD:
				F3DEX2CBFD.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.F3DSETA:
				F3DSETA.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DGOLDEN:
				F3DGolden.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F3DZEX2OOT:
				F3dzex2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
				Gsp.clipRatio = 2;
			break;
			case Microcode.F3DZEX2MM:
				F3dzex2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				Gsp.clipRatio = 2;
			break;
			case Microcode.F3DTEXA:
				F3DTEXA.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.T3DUX:
				F3d.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.F3DEX2ACCLAIM:
				F3DEX2Acclaim.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
				Gsp.clipRatio = 2;
			break;
			case Microcode.F5ROGUE:
				F5Rogue.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
			case Microcode.F3DFLX2:
				F3DFLX2.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
				Gsp.clipRatio = 2;
			break;
			case Microcode.ZSORTBOSS:
				ZSortBOSS.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = true;
			break;
			case Microcode.F5INDI_NABOO:
				F5IndiNaboo.init(Rsp.gsp, Rsp.gdp);
				m_hwlSupported = false;
			break;
            }
        }

        current = microcode;
    }

    public Microcode detectMicrocode(int uc_start, int uc_dstart, short uc_dsize) {
        Microcode current;

        for (int i = 0; i < numMicrocodes; i++) {
            current = top;

            while (current != null) {
                if ((current.address == uc_start) && (current.dataAddress == uc_dstart) && (current.dataSize == uc_dsize))
                    return current;

                current = current.lower;
            }
        }

        current = addMicrocode();

        current.address = uc_start;
        current.dataAddress = uc_dstart;
        current.dataSize = uc_dsize;
        current.NoN = false;
        current.type = Microcode.NONE;

        crc32.reset();
        crc32.update(GLN64jPlugin.RDRAM.array(), uc_start & 0x1FFFFFFF, 4096);
        int uc_crc = (int) crc32.getValue();
        for (int i = 0; i < specialMicrocodes.length; i++) {
            if (uc_crc == specialMicrocodes[i].crc) {
                current.type = specialMicrocodes[i].type;
                if (Debug.DEBUG_MICROCODE)
                    System.out.println("Found microcode by crc. Using " + Microcode.MicrocodeTypes[current.type]);
                return current;
            }
        }

        byte[] uc_data = new byte[2048];
        System.arraycopy(GLN64jPlugin.RDRAM.array(), uc_dstart & 0x1FFFFFFF, uc_data, 0, 2048);
        String uc_str = "Not Found";

        for (int i = 0; i < 2048; i++) {
            if ((uc_data[i] == 'R') && (uc_data[i + 1] == 'S') && (uc_data[i + 2] == 'P')) {
                int j = 0;
                uc_str = "";
                while (uc_data[i + j] > 0x0A) {
                    uc_str += (char) uc_data[i + j];
                    j++;
                }

                if (Debug.DEBUG_MICROCODE)
                    System.out.println(uc_str);

                int type = Microcode.NONE;

                if ((uc_data[i + 4] == 'S') && (uc_data[i + 5] == 'W')) {
                    type = Microcode.F3D;
                } else if ((uc_data[i + 4] == 'G') && (uc_data[i + 5] == 'f') && (uc_data[i + 6] == 'x')) {
                    current.NoN = uc_str.contains(".NoN");

                    if ((uc_data[i + 14] == 'F') && (uc_data[i + 15] == '3') && (uc_data[i + 16] == 'D')) {
                        if (current.lower != null && current.lower.type == Microcode.F3DEXBG)
                            type = Microcode.F3DEXBG;
                        else if ((uc_data[i + 19] == 'B') && (uc_data[i + 20] == 'G'))
                            type = Microcode.F3DEXBG;
                        else if ((uc_data[i + 28] == '0') || (uc_data[i + 28] == '1'))
                            type = Microcode.F3DEX;
                        else if ((uc_data[i + 31] == '2'))
                            type = Microcode.F3DEX2;
                    } else if ((uc_data[i + 14] == 'L') && (uc_data[i + 15] == '3') && (uc_data[i + 16] == 'D')) {
                        if ((uc_data[i + 28] == '1'))
                            type = Microcode.L3DEX;
                        else if ((uc_data[i + 31] == '2'))
                            type = Microcode.L3DEX2;
                    } else if ((uc_data[i + 14] == 'S') && (uc_data[i + 15] == '2') && (uc_data[i + 16] == 'D')) {
                        if ((uc_data[i + 28] == '1'))
                            type = Microcode.S2DEX;
                        else if ((uc_data[i + 31] == '2'))
                            type = Microcode.S2DEX2;
                    }
                }

                if (type != Microcode.NONE) {
                    current.type = type;
                    if (Debug.DEBUG_MICROCODE)
                        System.out.println("Found microcode by standard text. Using " + Microcode.MicrocodeTypes[current.type]);
                    return current;
                }

                break;
            }
        }

        for (Microcode specialMicrocode : specialMicrocodes) {
            if (uc_str.compareTo(specialMicrocode.text) == 0) {
                current.type = specialMicrocode.type;
                if (Debug.DEBUG_MICROCODE)
                    System.out.println("Found microcode by special text. Using " + Microcode.MicrocodeTypes[current.type]);
                return current;
            }
        }

        String chosen = (String) JOptionPane.showInputDialog(null, uc_str, "Choose the Microcode", JOptionPane.QUESTION_MESSAGE, null, Microcode.MicrocodeTypes, Microcode.MicrocodeTypes[0]);
        int index = Arrays.asList(Microcode.MicrocodeTypes).indexOf(chosen);
        current.type = (index >= 0) ? index : Microcode.F3D;
        System.out.println("Couldn't find the microcode. Using " + Microcode.MicrocodeTypes[current.type]);

        return current;
    }


    private Microcode addMicrocode() {
        Microcode newtop = new Microcode();

        newtop.lower = top;
        newtop.higher = null;

        if (top != null)
            top.higher = newtop;

        if (bottom == null)
            bottom = newtop;

        top = newtop;

        numMicrocodes++;

        return newtop;
    }

}
