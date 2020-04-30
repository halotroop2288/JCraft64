package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.*;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class F3d {
    public static final int F3D_MTX_STACKSIZE = 10;

    public static final int F3D_MTX_MODELVIEW = 0x00;
    public static final int F3D_MTX_PROJECTION = 0x01;
    public static final int F3D_MTX_MUL = 0x00;
    public static final int F3D_MTX_LOAD = 0x02;
    public static final int F3D_MTX_NOPUSH = 0x00;
    public static final int F3D_MTX_PUSH = 0x04;

    public static final int F3D_TEXTURE_ENABLE = 0x00000002;
    public static final int F3D_SHADING_SMOOTH = 0x00000200;
    public static final int F3D_CULL_FRONT = 0x00001000;
    public static final int F3D_CULL_BACK = 0x00002000;
    public static final int F3D_CULL_BOTH = 0x00003000;
    public static final int F3D_CLIPPING = 0x00000000;

    public static final int F3D_MV_VIEWPORT = 0x80;

    public static final int F3D_MWO_aLIGHT_1 = 0x00;
    public static final int F3D_MWO_bLIGHT_1 = 0x04;
    public static final int F3D_MWO_aLIGHT_2 = 0x20;
    public static final int F3D_MWO_bLIGHT_2 = 0x24;
    public static final int F3D_MWO_aLIGHT_3 = 0x40;
    public static final int F3D_MWO_bLIGHT_3 = 0x44;
    public static final int F3D_MWO_aLIGHT_4 = 0x60;
    public static final int F3D_MWO_bLIGHT_4 = 0x64;
    public static final int F3D_MWO_aLIGHT_5 = 0x80;
    public static final int F3D_MWO_bLIGHT_5 = 0x84;
    public static final int F3D_MWO_aLIGHT_6 = 0xa0;
    public static final int F3D_MWO_bLIGHT_6 = 0xa4;
    public static final int F3D_MWO_aLIGHT_7 = 0xc0;
    public static final int F3D_MWO_bLIGHT_7 = 0xc4;
    public static final int F3D_MWO_aLIGHT_8 = 0xe0;
    public static final int F3D_MWO_bLIGHT_8 = 0xe4;

    public static final int F3D_SPNOOP = 0x00;
    public static final int F3D_MTX = 0x01;
    public static final int F3D_RESERVED0 = 0x02;
    public static final int F3D_MOVEMEM = 0x03;
    public static final int F3D_VTX = 0x04;
    public static final int F3D_RESERVED1 = 0x05;
    public static final int F3D_DL = 0x06;
    public static final int F3D_RESERVED2 = 0x07;
    public static final int F3D_RESERVED3 = 0x08;
    public static final int F3D_SPRITE2D_BASE = 0x09;

    public static final int F3D_TRI1 = 0xBF;
    public static final int F3D_CULLDL = 0xBE;
    public static final int F3D_POPMTX = 0xBD;
    public static final int F3D_MOVEWORD = 0xBC;
    public static final int F3D_TEXTURE = 0xBB;
    public static final int F3D_SETOTHERMODE_H = 0xBA;
    public static final int F3D_SETOTHERMODE_L = 0xB9;
    public static final int F3D_ENDDL = 0xB8;
    public static final int F3D_SETGEOMETRYMODE = 0xB7;
    public static final int F3D_CLEARGEOMETRYMODE = 0xB6;
    public static final int F3D_QUAD = 0xB5;
    public static final int F3D_RDPHALF_1 = 0xB4;
    public static final int F3D_RDPHALF_2 = 0xB3;
    public static final int F3D_RDPHALF_CONT = 0xB2;
    public static final int F3D_TRI4 = 0xB1;

    public static int half_1, half_2;

    protected static Gsp gsp;
    protected static Gdp gdp;

    public static GBIFunc F3D_SPNoOp = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPNoOp();
        }
    };

    public static GBIFunc F3D_Mtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            if ((w0 & SR_MASK_16) != 64) {
                if (DEBUG_MICROCODE) {
                    DebugMsg(DEBUG_MEDIUM | DEBUG_HIGH | DEBUG_ERROR, "G_MTX: address = 0x%08X    length = %d    params = 0x%02X\n", w1, w0 & SR_MASK_16, (w0 >> 16) & SR_MASK_8);
                }
                return;
            }
            gsp.gSPMatrix(gsp.RSP_SegmentToPhysical(w1), (w0 >> 16) & SR_MASK_8);
        }
    };

    public static GBIFunc F3D_Reserved0 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            if (DEBUG_MICROCODE) {
                DebugMsg(DEBUG_MEDIUM | DEBUG_IGNORED | DEBUG_UNKNOWN, "G_RESERVED0: w0=0x%08X w1=0x%08X\n", w0, w1);
            }
        }
    };

    public static GBIFunc F3D_MoveMem = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            w1 = gsp.RSP_SegmentToPhysical(w1);
            switch ((w0 >> 16) & SR_MASK_8) {
                case F3D_MV_VIEWPORT:
                    gsp.gSPViewport(w1);
                    break;
                case G_MV_MATRIX_1:
                    gsp.gSPForceMatrix(w1);
                    gsp.getCmd();
                    gsp.getCmd();
                    gsp.getCmd();
                    break;
                case G_MV_L0:
                    gsp.gSPLight(w1, LIGHT_1);
                    break;
                case G_MV_L1:
                    gsp.gSPLight(w1, LIGHT_2);
                    break;
                case G_MV_L2:
                    gsp.gSPLight(w1, LIGHT_3);
                    break;
                case G_MV_L3:
                    gsp.gSPLight(w1, LIGHT_4);
                    break;
                case G_MV_L4:
                    gsp.gSPLight(w1, LIGHT_5);
                    break;
                case G_MV_L5:
                    gsp.gSPLight(w1, LIGHT_6);
                    break;
                case G_MV_L6:
                    gsp.gSPLight(w1, LIGHT_7);
                    break;
                case G_MV_L7:
                    gsp.gSPLight(w1, LIGHT_8);
                    break;
                case G_MV_LOOKATX:
                    break;
                case G_MV_LOOKATY:
                    break;
            }
        }
    };

    public static GBIFunc F3D_Vtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPVertex(gsp.RSP_SegmentToPhysical(w1), ((w0 >>> 20) & SR_MASK_4) + 1, (w0 >>> 16) & SR_MASK_4);
        }
    };

    public static GBIFunc F3D_Reserved1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc F3D_DList = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch ((w0 >> 16) & SR_MASK_8) {
                case G_DL_PUSH: gsp.gSPDisplayList(gsp.RSP_SegmentToPhysical(w1));
                case G_DL_NOPUSH: gsp.gSPBranchList(gsp.RSP_SegmentToPhysical(w1));
            }
        }
    };

    public static GBIFunc F3D_Reserved2 = (w0, w1) -> {
    };

    public static GBIFunc F3D_Reserved3 = (w0, w1) -> {
    };

    public static GBIFunc F3D_Sprite2D_Base = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.getCmd();
        }
    };

    public static GBIFunc F3D_Tri1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP1Triangle(((w1 >> 16) & SR_MASK_8) / 10,
                    ((w1 >> 8) & SR_MASK_8) / 10,
                    (w1 & SR_MASK_8) / 10,
                    (w1 >> 24) & SR_MASK_8);
        }
    };

    public static GBIFunc F3D_CullDL = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPCullDisplayList((w0 & SR_MASK_24) / 40, (w1 / 40) - 1);
        }
    };

    public static GBIFunc F3D_PopMtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPPopMatrix(w1);
        }
    };

    public static GBIFunc F3D_MoveWord = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch (w0 & SR_MASK_8) {
                case G_MW_MATRIX: // 0x00
                    gsp.gSPInsertMatrix((w0 >> 8) & SR_MASK_16, w1);
                    break;
                case G_MW_NUMLIGHT: // 0x02
                    gsp.gSPNumLights(((w1 - 0x80000000) >>> 5) - 1);
                    break;
                case G_MW_CLIP: // 0x04
                    gsp.gSPClipRatio(w1);
                    break;
                case G_MW_SEGMENT: // 0x06
                    gsp.gSPSegment(((w0 >> 8) & SR_MASK_16) >>> 2, w1 & 0x00FFFFFF);
                    break;
                case G_MW_FOG: // 0x08
                    gsp.gSPFogFactor((short) ((w1 >> 16) & SR_MASK_16), (short) (w1 & SR_MASK_16));
                    break;
                case G_MW_LIGHTCOL: // 0x0A
                    switch ((w0 >> 8) & SR_MASK_16) {
                        case F3D_MWO_aLIGHT_1:
                            gsp.gSPLightColor(LIGHT_1, w1);
                            break;
                        case F3D_MWO_aLIGHT_2:
                            gsp.gSPLightColor(LIGHT_2, w1);
                            break;
                        case F3D_MWO_aLIGHT_3:
                            gsp.gSPLightColor(LIGHT_3, w1);
                            break;
                        case F3D_MWO_aLIGHT_4:
                            gsp.gSPLightColor(LIGHT_4, w1);
                            break;
                        case F3D_MWO_aLIGHT_5:
                            gsp.gSPLightColor(LIGHT_5, w1);
                            break;
                        case F3D_MWO_aLIGHT_6:
                            gsp.gSPLightColor(LIGHT_6, w1);
                            break;
                        case F3D_MWO_aLIGHT_7:
                            gsp.gSPLightColor(LIGHT_7, w1);
                            break;
                        case F3D_MWO_aLIGHT_8:
                            gsp.gSPLightColor(LIGHT_8, w1);
                            break;
                    }
                    break;
                case G_MW_POINTS: // 0x0C
                    gsp.gSPModifyVertex(((w0 >> 8) & SR_MASK_16) / 40, (w0 & SR_MASK_8) % 40, w1);
                    break;
                case G_MW_PERSPNORM: // 0x0E
                    gsp.gSPPerspNormalize((short) w1);
                    break;
            }
        }
    };

    public static GBIFunc F3D_Texture = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPTexture(((w1 >> 16) & SR_MASK_16) * FIXED2FLOATRECIP16,
                    (w1 & SR_MASK_16) * FIXED2FLOATRECIP16,
                    (w0 >> 11) & SR_MASK_3,
                    (w0 >> 8) & SR_MASK_3,
                    w0 & SR_MASK_8);
        }
    };

    public static GBIFunc F3D_SetOtherMode_H = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch ((w0 >> 8) & SR_MASK_8) {
                case G_MDSFT_PIPELINE:
                    gdp.gDPPipelineMode(w1 >>> G_MDSFT_PIPELINE);
                    break;
                case G_MDSFT_CYCLETYPE:
                    gdp.gDPSetCycleType(w1 >>> G_MDSFT_CYCLETYPE);
                    break;
                case G_MDSFT_TEXTPERSP:
                    gdp.gDPSetTexturePersp(w1 >>> G_MDSFT_TEXTPERSP);
                    break;
                case G_MDSFT_TEXTDETAIL:
                    gdp.gDPSetTextureDetail(w1 >>> G_MDSFT_TEXTDETAIL);
                    break;
                case G_MDSFT_TEXTLOD:
                    gdp.gDPSetTextureLOD(w1 >>> G_MDSFT_TEXTLOD);
                    break;
                case G_MDSFT_TEXTLUT:
                    gdp.gDPSetTextureLUT(w1 >>> G_MDSFT_TEXTLUT);
                    break;
                case G_MDSFT_TEXTFILT:
                    gdp.gDPSetTextureFilter(w1 >>> G_MDSFT_TEXTFILT);
                    break;
                case G_MDSFT_TEXTCONV:
                    gdp.gDPSetTextureConvert(w1 >>> G_MDSFT_TEXTCONV);
                    break;
                case G_MDSFT_COMBKEY:
                    gdp.gDPSetCombineKey(w1 >>> G_MDSFT_COMBKEY);
                    break;
                case G_MDSFT_RGBDITHER:
                    gdp.gDPSetColorDither(w1 >>> G_MDSFT_RGBDITHER);
                    break;
                case G_MDSFT_ALPHADITHER:
                    gdp.gDPSetAlphaDither(w1 >>> G_MDSFT_ALPHADITHER);
                    break;
                default:
                    int shift = (w0 >> 8) & SR_MASK_8;
                    int length = w0 & SR_MASK_8;
                    int mask = ((1 << length) - 1) << shift;

//                    gdp.otherMode.setH(gdp.otherMode.getH() & ~mask);
//                    gdp.otherMode.setH(gdp.otherMode.getH() | (w1 & mask));
                    gdp.otherMode.h = (gdp.otherMode.h & ~mask);
                    gdp.otherMode.h = (gdp.otherMode.h | (w1 & mask));

                    gdp.changed |= Gdp.CHANGED_CYCLETYPE;
                    break;
            }
        }
    };

    public static GBIFunc F3D_SetOtherMode_L = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch ((w0 >> 8) & SR_MASK_8) {
                case G_MDSFT_ALPHACOMPARE:
                    gdp.gDPSetAlphaCompare(w1 >>> G_MDSFT_ALPHACOMPARE);
                    break;
                case G_MDSFT_ZSRCSEL:
                    gdp.gDPSetDepthSource(w1 >>> G_MDSFT_ZSRCSEL);
                    break;
                case G_MDSFT_RENDERMODE:
                    gdp.gDPSetRenderMode(w1 & 0xCCCCFFFF, w1 & 0x3333FFFF);
                    break;
                default:
                    int shift = (w0 >> 8) & SR_MASK_8;
                    int length = w0 & SR_MASK_8;
                    int mask = ((1 << length) - 1) << shift;
                    gdp.otherMode.l = (gdp.otherMode.l & ~mask);
                    gdp.otherMode.l = (gdp.otherMode.l | (w1 & mask));

                    gdp.changed |= Gdp.CHANGED_RENDERMODE | Gdp.CHANGED_ALPHACOMPARE;
                    break;
            }
        }
    };

    public static GBIFunc F3D_EndDL = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPEndDisplayList();
        }
    };

    public static GBIFunc F3D_SetGeometryMode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPSetGeometryMode(w1);
        }
    };

    public static GBIFunc F3D_ClearGeometryMode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPClearGeometryMode(w1);
        }
    };

    public static GBIFunc F3D_Line3D = (w0, w1) -> {
    };

    public static GBIFunc F3D_Quad = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP1Quadrangle(((w1 >> 24) & SR_MASK_8) / 10, ((w1 >> 16) & SR_MASK_8) / 10, ((w1 >> 8) & SR_MASK_8) / 10, (w1 & SR_MASK_8) / 10);
        }
    };

    public static GBIFunc F3D_RDPHalf_1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            half_1 = w1;
        }
    };

    public static GBIFunc F3D_RDPHalf_2 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            half_2 = w1;
        }
    };

    public static GBIFunc F3D_RDPHalf_Cont = (w0, w1) -> {
    };

    public static GBIFunc F3D_Tri4 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP4Triangles(w0 & SR_MASK_4, w1 & SR_MASK_4, (w1 >> 4) & SR_MASK_4,
                    (w0 >> 4) & SR_MASK_4, (w1 >> 8) & SR_MASK_4, (w1 >> 12) & SR_MASK_4,
                    (w0 >> 8) & SR_MASK_4, (w1 >> 16) & SR_MASK_4, (w1 >> 20) & SR_MASK_4,
                    (w0 >> 12) & SR_MASK_4, (w1 >> 24) & SR_MASK_4, (w1 >> 28) & SR_MASK_4);
        }
    };

    public static void GBI_InitFlags() {
        G_MTX_STACKSIZE = F3D_MTX_STACKSIZE;
        G_MTX_MODELVIEW = F3D_MTX_MODELVIEW;
        G_MTX_PROJECTION = F3D_MTX_PROJECTION;
        G_MTX_MUL = F3D_MTX_MUL;
        G_MTX_LOAD = F3D_MTX_LOAD;
        G_MTX_NOPUSH = F3D_MTX_NOPUSH;
        G_MTX_PUSH = F3D_MTX_PUSH;
        G_TEXTURE_ENABLE = F3D_TEXTURE_ENABLE;
        G_SHADING_SMOOTH = F3D_SHADING_SMOOTH;
        G_CULL_FRONT = F3D_CULL_FRONT;
        G_CULL_BACK = F3D_CULL_BACK;
        G_CULL_BOTH = F3D_CULL_BOTH;
        G_CLIPPING = F3D_CLIPPING;
        G_MV_VIEWPORT = F3D_MV_VIEWPORT;
        G_MWO_aLIGHT_1 = F3D_MWO_aLIGHT_1;
        G_MWO_bLIGHT_1 = F3D_MWO_bLIGHT_1;
        G_MWO_aLIGHT_2 = F3D_MWO_aLIGHT_2;
        G_MWO_bLIGHT_2 = F3D_MWO_bLIGHT_2;
        G_MWO_aLIGHT_3 = F3D_MWO_aLIGHT_3;
        G_MWO_bLIGHT_3 = F3D_MWO_bLIGHT_3;
        G_MWO_aLIGHT_4 = F3D_MWO_aLIGHT_4;
        G_MWO_bLIGHT_4 = F3D_MWO_bLIGHT_4;
        G_MWO_aLIGHT_5 = F3D_MWO_aLIGHT_5;
        G_MWO_bLIGHT_5 = F3D_MWO_bLIGHT_5;
        G_MWO_aLIGHT_6 = F3D_MWO_aLIGHT_6;
        G_MWO_bLIGHT_6 = F3D_MWO_bLIGHT_6;
        G_MWO_aLIGHT_7 = F3D_MWO_aLIGHT_7;
        G_MWO_bLIGHT_7 = F3D_MWO_bLIGHT_7;
        G_MWO_aLIGHT_8 = F3D_MWO_aLIGHT_8;
        G_MWO_bLIGHT_8 = F3D_MWO_bLIGHT_8;
    }

    public static void init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        GBI_InitFlags();
        G_SPNOOP = F3D_SPNOOP;
        G_MTX = F3D_MTX;
        G_RESERVED0 = F3D_RESERVED0;
        G_MOVEMEM = F3D_MOVEMEM;
        G_VTX = F3D_VTX;
        G_RESERVED1 = F3D_RESERVED1;
        G_DL = F3D_DL;
        G_RESERVED2 = F3D_RESERVED2;
        G_RESERVED3 = F3D_RESERVED3;
        G_SPRITE2D_BASE = F3D_SPRITE2D_BASE;

        G_TRI1 = F3D_TRI1;
        G_CULLDL = F3D_CULLDL;
        G_POPMTX = F3D_POPMTX;
        G_MOVEWORD = F3D_MOVEWORD;
        G_TEXTURE = F3D_TEXTURE;
        G_SETOTHERMODE_H = F3D_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3D_SETOTHERMODE_L;
        G_ENDDL = F3D_ENDDL;
        G_SETGEOMETRYMODE = F3D_SETGEOMETRYMODE;
        G_CLEARGEOMETRYMODE = F3D_CLEARGEOMETRYMODE;
        G_QUAD = F3D_QUAD;
        G_RDPHALF_1 = F3D_RDPHALF_1;
        G_RDPHALF_2 = F3D_RDPHALF_2;
        G_RDPHALF_CONT = F3D_RDPHALF_CONT;
        G_TRI4 = F3D_TRI4;

        gsp.pcStackSize = 10;

        gsp.setGBI(G_SPNOOP, F3D_SPNoOp);
        gsp.setGBI(G_MTX, F3D_Mtx);
        gsp.setGBI(G_RESERVED0, F3D_Reserved0);
        gsp.setGBI(G_MOVEMEM, F3D_MoveMem);
        gsp.setGBI(G_VTX, F3D_Vtx);
        gsp.setGBI(G_RESERVED1, F3D_Reserved1);
        gsp.setGBI(G_DL, F3D_DList);
        gsp.setGBI(G_RESERVED2, F3D_Reserved2);
        gsp.setGBI(G_RESERVED3, F3D_Reserved3);
        gsp.setGBI(G_SPRITE2D_BASE, F3D_Sprite2D_Base);

        gsp.setGBI(G_TRI1, F3D_Tri1);
        gsp.setGBI(G_CULLDL, F3D_CullDL);
        gsp.setGBI(G_POPMTX, F3D_PopMtx);
        gsp.setGBI(G_MOVEWORD, F3D_MoveWord);
        gsp.setGBI(G_TEXTURE, F3D_Texture);
        gsp.setGBI(G_SETOTHERMODE_H, F3D_SetOtherMode_H);
        gsp.setGBI(G_SETOTHERMODE_L, F3D_SetOtherMode_L);
        gsp.setGBI(G_ENDDL, F3D_EndDL);
        gsp.setGBI(G_SETGEOMETRYMODE, F3D_SetGeometryMode);
        gsp.setGBI(G_CLEARGEOMETRYMODE, F3D_ClearGeometryMode);
        gsp.setGBI(G_QUAD, F3D_Quad);
        gsp.setGBI(G_RDPHALF_1, F3D_RDPHalf_1);
        gsp.setGBI(G_RDPHALF_2, F3D_RDPHalf_2);
        gsp.setGBI(G_RDPHALF_CONT, F3D_RDPHalf_Cont);
        gsp.setGBI(G_TRI4, F3D_Tri4);

        if (DEBUG_MICROCODE) System.out.println("Initialized Fast 3D opcodes");
    }

}
