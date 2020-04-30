package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class F3dex extends F3d {

    public static final int F3DEX_MTX_STACKSIZE = 18;

    public static final int F3DEX_MTX_MODELVIEW = 0x00;
    public static final int F3DEX_MTX_PROJECTION = 0x01;
    public static final int F3DEX_MTX_MUL = 0x00;
    public static final int F3DEX_MTX_LOAD = 0x02;
    public static final int F3DEX_MTX_NOPUSH = 0x00;
    public static final int F3DEX_MTX_PUSH = 0x04;

    public static final int F3DEX_TEXTURE_ENABLE = 0x00000002;
    public static final int F3DEX_SHADING_SMOOTH = 0x00000200;
    public static final int F3DEX_CULL_FRONT = 0x00001000;
    public static final int F3DEX_CULL_BACK = 0x00002000;
    public static final int F3DEX_CULL_BOTH = 0x00003000;
    public static final int F3DEX_CLIPPING = 0x00800000;

    public static final int F3DEX_MV_VIEWPORT = 0x80;

    public static final int F3DEX_MWO_aLIGHT_1 = 0x00;
    public static final int F3DEX_MWO_bLIGHT_1 = 0x04;
    public static final int F3DEX_MWO_aLIGHT_2 = 0x20;
    public static final int F3DEX_MWO_bLIGHT_2 = 0x24;
    public static final int F3DEX_MWO_aLIGHT_3 = 0x40;
    public static final int F3DEX_MWO_bLIGHT_3 = 0x44;
    public static final int F3DEX_MWO_aLIGHT_4 = 0x60;
    public static final int F3DEX_MWO_bLIGHT_4 = 0x64;
    public static final int F3DEX_MWO_aLIGHT_5 = 0x80;
    public static final int F3DEX_MWO_bLIGHT_5 = 0x84;
    public static final int F3DEX_MWO_aLIGHT_6 = 0xa0;
    public static final int F3DEX_MWO_bLIGHT_6 = 0xa4;
    public static final int F3DEX_MWO_aLIGHT_7 = 0xc0;
    public static final int F3DEX_MWO_bLIGHT_7 = 0xc4;
    public static final int F3DEX_MWO_aLIGHT_8 = 0xe0;
    public static final int F3DEX_MWO_bLIGHT_8 = 0xe4;

    public static final int F3DEX_MODIFYVTX = 0xB2;
    public static final int F3DEX_TRI2 = 0xB1;
    public static final int F3DEX_BRANCH_Z = 0xB0;
    public static final int F3DEX_LOAD_UCODE = 0xAF; // 0xCF


    public static GBIFunc F3DEX_Vtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPVertex(gsp.RSP_SegmentToPhysical(w1), (w0 >>> 10) & SR_MASK_6, (w0 >>> 17) & SR_MASK_7);
        }
    };

    public static GBIFunc F3DEX_Tri1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP1Triangle((w1 >> 17) & SR_MASK_7, (w1 >> 9) & SR_MASK_7, (w1 >> 1) & SR_MASK_7, 0);
        }
    };

    public static GBIFunc F3DEX_CullDL = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPCullDisplayList((w0 >> 1) & SR_MASK_15, (w1 >> 1) & SR_MASK_15);
        }
    };

    public static GBIFunc F3DEX_ModifyVtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPModifyVertex((w0 >> 1) & SR_MASK_15, (w0 >> 16) & SR_MASK_8, w1);
        }
    };

    public static GBIFunc F3DEX_Tri2 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP2Triangles((w0 >> 17) & SR_MASK_7, (w0 >> 9) & SR_MASK_7, (w0 >> 1) & SR_MASK_7, 0,
                    (w1 >> 17) & SR_MASK_7, (w1 >> 9) & SR_MASK_7, (w1 >> 1) & SR_MASK_7, 0);
        }
    };

    public static GBIFunc F3DEX_Quad = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP1Quadrangle((w1 >> 25) & SR_MASK_7, (w1 >> 17) & SR_MASK_7, (w1 >> 9) & SR_MASK_7, (w1 >> 1) & SR_MASK_7);
        }
    };

    public static GBIFunc F3DEX_Branch_Z = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPBranchLessZ(gsp.RSP_SegmentToPhysical(F3d.half_1), (w0 >> 1) & SR_MASK_11, w1);
        }
    };

    public static GBIFunc F3DEX_Load_uCode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPLoadUcodeEx(w1, F3d.half_1, (short) ((w0 & SR_MASK_16) + 1));
        }
    };

    public static void GBI_InitFlags() {
        G_MTX_STACKSIZE = F3DEX_MTX_STACKSIZE;
        G_MTX_MODELVIEW = F3DEX_MTX_MODELVIEW;
        G_MTX_PROJECTION = F3DEX_MTX_PROJECTION;
        G_MTX_MUL = F3DEX_MTX_MUL;
        G_MTX_LOAD = F3DEX_MTX_LOAD;
        G_MTX_NOPUSH = F3DEX_MTX_NOPUSH;
        G_MTX_PUSH = F3DEX_MTX_PUSH;
        G_TEXTURE_ENABLE = F3DEX_TEXTURE_ENABLE;
        G_SHADING_SMOOTH = F3DEX_SHADING_SMOOTH;
        G_CULL_FRONT = F3DEX_CULL_FRONT;
        G_CULL_BACK = F3DEX_CULL_BACK;
        G_CULL_BOTH = F3DEX_CULL_BOTH;
        G_CLIPPING = F3DEX_CLIPPING;
        G_MV_VIEWPORT = F3DEX_MV_VIEWPORT;
        G_MWO_aLIGHT_1 = F3DEX_MWO_aLIGHT_1;
        G_MWO_bLIGHT_1 = F3DEX_MWO_bLIGHT_1;
        G_MWO_aLIGHT_2 = F3DEX_MWO_aLIGHT_2;
        G_MWO_bLIGHT_2 = F3DEX_MWO_bLIGHT_2;
        G_MWO_aLIGHT_3 = F3DEX_MWO_aLIGHT_3;
        G_MWO_bLIGHT_3 = F3DEX_MWO_bLIGHT_3;
        G_MWO_aLIGHT_4 = F3DEX_MWO_aLIGHT_4;
        G_MWO_bLIGHT_4 = F3DEX_MWO_bLIGHT_4;
        G_MWO_aLIGHT_5 = F3DEX_MWO_aLIGHT_5;
        G_MWO_bLIGHT_5 = F3DEX_MWO_bLIGHT_5;
        G_MWO_aLIGHT_6 = F3DEX_MWO_aLIGHT_6;
        G_MWO_bLIGHT_6 = F3DEX_MWO_bLIGHT_6;
        G_MWO_aLIGHT_7 = F3DEX_MWO_aLIGHT_7;
        G_MWO_bLIGHT_7 = F3DEX_MWO_bLIGHT_7;
        G_MWO_aLIGHT_8 = F3DEX_MWO_aLIGHT_8;
        G_MWO_bLIGHT_8 = F3DEX_MWO_bLIGHT_8;
    }

    public static void init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        GBI_InitFlags();
        G_SPNOOP = F3d.F3D_SPNOOP;
        G_MTX = F3d.F3D_MTX;
        G_RESERVED0 = F3d.F3D_RESERVED0;
        G_MOVEMEM = F3d.F3D_MOVEMEM;
        G_VTX = F3d.F3D_VTX;
        G_RESERVED1 = F3d.F3D_RESERVED1;
        G_DL = F3d.F3D_DL;
        G_RESERVED2 = F3d.F3D_RESERVED2;
        G_RESERVED3 = F3d.F3D_RESERVED3;
        G_SPRITE2D_BASE = F3d.F3D_SPRITE2D_BASE;
        G_TRI1 = F3d.F3D_TRI1;
        G_CULLDL = F3d.F3D_CULLDL;
        G_POPMTX = F3d.F3D_POPMTX;
        G_MOVEWORD = F3d.F3D_MOVEWORD;
        G_TEXTURE = F3d.F3D_TEXTURE;
        G_SETOTHERMODE_H = F3d.F3D_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3d.F3D_SETOTHERMODE_L;
        G_ENDDL = F3d.F3D_ENDDL;
        G_SETGEOMETRYMODE = F3d.F3D_SETGEOMETRYMODE;
        G_CLEARGEOMETRYMODE = F3d.F3D_CLEARGEOMETRYMODE;
        G_QUAD = F3d.F3D_QUAD;
        G_RDPHALF_1 = F3d.F3D_RDPHALF_1;
        G_RDPHALF_2 = F3d.F3D_RDPHALF_2;
        G_MODIFYVTX = F3DEX_MODIFYVTX;
        G_TRI2 = F3DEX_TRI2;
        G_BRANCH_Z = F3DEX_BRANCH_Z;
        G_LOAD_UCODE = F3DEX_LOAD_UCODE;

        gsp.pcStackSize = 18;

        gsp.setGBI(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setGBI(G_MTX, F3d.F3D_Mtx);
        gsp.setGBI(G_RESERVED0, F3d.F3D_Reserved0);
        gsp.setGBI(G_MOVEMEM, F3d.F3D_MoveMem);
        gsp.setGBI(G_VTX, F3DEX_Vtx);
        gsp.setGBI(G_RESERVED1, F3d.F3D_Reserved1);
        gsp.setGBI(G_DL, F3d.F3D_DList);
        gsp.setGBI(G_RESERVED2, F3d.F3D_Reserved2);
        gsp.setGBI(G_RESERVED3, F3d.F3D_Reserved3);
        gsp.setGBI(G_SPRITE2D_BASE, F3d.F3D_Sprite2D_Base);

        gsp.setGBI(G_TRI1, F3DEX_Tri1);
        gsp.setGBI(G_CULLDL, F3DEX_CullDL);
        gsp.setGBI(G_POPMTX, F3d.F3D_PopMtx);
        gsp.setGBI(G_MOVEWORD, F3d.F3D_MoveWord);
        gsp.setGBI(G_TEXTURE, F3d.F3D_Texture);
        gsp.setGBI(G_SETOTHERMODE_H, F3d.F3D_SetOtherMode_H);
        gsp.setGBI(G_SETOTHERMODE_L, F3d.F3D_SetOtherMode_L);
        gsp.setGBI(G_ENDDL, F3d.F3D_EndDL);
        gsp.setGBI(G_SETGEOMETRYMODE, F3d.F3D_SetGeometryMode);
        gsp.setGBI(G_CLEARGEOMETRYMODE, F3d.F3D_ClearGeometryMode);
        gsp.setGBI(G_QUAD, F3DEX_Quad);
        gsp.setGBI(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setGBI(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setGBI(G_MODIFYVTX, F3DEX_ModifyVtx);
        gsp.setGBI(G_TRI2, F3DEX_Tri2);
        gsp.setGBI(G_BRANCH_Z, F3DEX_Branch_Z);
        gsp.setGBI(G_LOAD_UCODE, F3DEX_Load_uCode);

        if (DEBUG_MICROCODE) System.out.println("Initialized F3DEX opcodes");
    }

}
