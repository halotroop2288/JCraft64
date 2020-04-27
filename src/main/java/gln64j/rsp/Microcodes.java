package gln64j.rsp;

import me.hydos.J64.util.debug.Debug;
import gln64j.GLN64jPlugin;
import gln64j.Gbi;
import gln64j.Rsp;
import gln64j.opcodes.F3d;
import gln64j.opcodes.F3ddkr;
import gln64j.opcodes.F3dex;
import gln64j.opcodes.F3dex2;
import gln64j.opcodes.F3dexbg;
import gln64j.opcodes.F3dpd;
import gln64j.opcodes.F3dwrus;
import gln64j.opcodes.L3d;
import gln64j.opcodes.L3dex;
import gln64j.opcodes.L3dex2;
import gln64j.opcodes.Rdpfuncs2;
import gln64j.opcodes.S2dex;
import gln64j.opcodes.S2dex2;

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

    private final Microcode[] specialMicrocodes = {
            new Microcode(Microcode.F3DWRUS, false, 0xd17906e2, "RSP SW Version: 2.0D, 04-01-96"),
            new Microcode(Microcode.F3DWRUS, false, 0x94c4c833, "RSP SW Version: 2.0D, 04-01-96"),
            new Microcode(Microcode.S2DEX, false, 0x9df31081, "RSP Gfx ucode S2DEX  1.06 Yoshitaka Yasumoto Nintendo."),
            new Microcode(Microcode.F3DDKR, false, 0x8d91244f, "Diddy Kong Racing"),
            new Microcode(Microcode.F3DDKR, false, 0x6e6fc893, "Diddy Kong Racing"),
            new Microcode(Microcode.F3DDKR, false, 0xbde9d1fb, "Jet Force Gemini"),
            new Microcode(Microcode.F3DPD, false, 0x1c4f7869, "Perfect Dark")
    };

    private static GBIFunc gbiUnknown = (w0, w1) -> {
        if (Debug.DEBUG_GBI)
            Debug.DebugMsg(Debug.DEBUG_LOW | Debug.DEBUG_UNKNOWN, "UNKNOWN GBI COMMAND 0x%02X\n", (w0 >> 24) & Gbi.SR_MASK_8);
    };

    public Microcodes() {
    }

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
                case Microcode.F3D -> F3d.F3D_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DEX -> F3dex.F3DEX_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DEX2 -> F3dex2.F3DEX2_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.L3D -> L3d.L3D_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.L3DEX -> L3dex.L3DEX_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.L3DEX2 -> L3dex2.L3DEX2_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.S2DEX -> S2dex.S2DEX_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.S2DEX2 -> S2dex2.S2DEX2_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DDKR -> F3ddkr.F3DDKR_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DWRUS -> F3dwrus.F3DWRUS_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DPD -> F3dpd.F3DPD_Init(Rsp.gsp, Rsp.gdp);
                case Microcode.F3DEXBG -> F3dexbg.F3DEXBG_Init(Rsp.gsp, Rsp.gdp);
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
                    System.out.printf("Found microcode by crc. Using %s\n", Microcode.MicrocodeTypes[current.type]);
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
                        System.out.printf("Found microcode by standard text. Using %s\n", Microcode.MicrocodeTypes[current.type]);
                    return current;
                }

                break;
            }
        }

        for (Microcode specialMicrocode : specialMicrocodes) {
            if (uc_str.compareTo(specialMicrocode.text) == 0) {
                current.type = specialMicrocode.type;
                if (Debug.DEBUG_MICROCODE)
                    System.out.printf("Found microcode by special text. Using %s\n", Microcode.MicrocodeTypes[current.type]);
                return current;
            }
        }

        String chosen = (String) JOptionPane.showInputDialog(null, uc_str, "Choose the Microcode", JOptionPane.QUESTION_MESSAGE, null, Microcode.MicrocodeTypes, Microcode.MicrocodeTypes[0]);
        int index = Arrays.asList(Microcode.MicrocodeTypes).indexOf(chosen);
        current.type = (index >= 0) ? index : Microcode.F3D;
        System.out.printf("Couldn't find the microcode. Using %s\n", Microcode.MicrocodeTypes[current.type]);

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
