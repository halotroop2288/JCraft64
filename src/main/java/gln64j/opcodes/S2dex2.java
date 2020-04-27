package gln64j.opcodes;

import gln64j.rsp.Gsp;
import gln64j.rdp.Gdp;
import gln64j.rsp.GBIFunc;

import static me.hydos.J64.util.debug.Debug.*;
import static gln64j.Gbi.*;

public class S2dex2 extends F3dex2 {

    public static final int S2DEX2_OBJ_RECTANGLE_R = 0xDA;
    public static final int S2DEX2_OBJ_MOVEMEM = 0xDC;
    public static final int S2DEX2_RDPHALF_0 = 0xE4;
    public static final int S2DEX2_OBJ_RECTANGLE = 0x01;
    public static final int S2DEX2_OBJ_SPRITE = 0x02;
    public static final int S2DEX2_SELECT_DL = 0x04;
    public static final int S2DEX2_OBJ_LOADTXTR = 0x05;
    public static final int S2DEX2_OBJ_LDTX_SPRITE = 0x06;
    public static final int S2DEX2_OBJ_LDTX_RECT = 0x07;
    public static final int S2DEX2_OBJ_LDTX_RECT_R = 0x08;
    public static final int S2DEX2_BG_1CYC = 0x09;
    public static final int S2DEX2_BG_COPY = 0x0A;
    public static final int S2DEX2_OBJ_RENDERMODE = 0x0B;

    /**
     * Creates a new instance of S2dex2
     */
    public S2dex2() {
    }

    public static GBIFunc S2DEX2_BG_1Cyc = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_BG_Copy = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_Rectangle = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_Sprite = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_MoveMem = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Select_DL = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_RenderMode = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_Rectangle_R = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_LoadTxtr = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_LdTx_Sprite = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_LdTx_Rect = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX2_Obj_LdTx_Rect_R = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static void S2DEX2_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3dex2.GBI_InitFlags();
        G_SPNOOP = F3dex2.F3DEX2_SPNOOP;
        G_BG_1CYC = S2DEX2_BG_1CYC;
        G_BG_COPY = S2DEX2_BG_COPY;
        G_OBJ_RECTANGLE = S2DEX2_OBJ_RECTANGLE;
        G_OBJ_SPRITE = S2DEX2_OBJ_SPRITE;
        G_OBJ_MOVEMEM = S2DEX2_OBJ_MOVEMEM;
        G_DL = F3dex2.F3DEX2_DL;
        G_SELECT_DL = S2DEX2_SELECT_DL;
        G_OBJ_RENDERMODE = S2DEX2_OBJ_RENDERMODE;
        G_OBJ_RECTANGLE_R = S2DEX2_OBJ_RECTANGLE_R;
        G_OBJ_LOADTXTR = S2DEX2_OBJ_LOADTXTR;
        G_OBJ_LDTX_SPRITE = S2DEX2_OBJ_LDTX_SPRITE;
        G_OBJ_LDTX_RECT = S2DEX2_OBJ_LDTX_RECT;
        G_OBJ_LDTX_RECT_R = S2DEX2_OBJ_LDTX_RECT_R;
        G_MOVEWORD = F3dex2.F3DEX2_MOVEWORD;
        G_SETOTHERMODE_H = F3dex2.F3DEX2_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3dex2.F3DEX2_SETOTHERMODE_L;
        G_ENDDL = F3dex2.F3DEX2_ENDDL;
        G_RDPHALF_1 = F3dex2.F3DEX2_RDPHALF_1;
        G_RDPHALF_2 = F3dex2.F3DEX2_RDPHALF_2;
        G_LOAD_UCODE = F3dex2.F3DEX2_LOAD_UCODE;

        gsp.geometryMode = 0;

        gsp.pcStackSize = 18;

        gsp.setUcode(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setUcode(G_BG_1CYC, S2DEX2_BG_1Cyc);
        gsp.setUcode(G_BG_COPY, S2DEX2_BG_Copy);
        gsp.setUcode(G_OBJ_RECTANGLE, S2DEX2_Obj_Rectangle);
        gsp.setUcode(G_OBJ_SPRITE, S2DEX2_Obj_Sprite);
        gsp.setUcode(G_OBJ_MOVEMEM, S2DEX2_Obj_MoveMem);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_SELECT_DL, S2DEX2_Select_DL);
        gsp.setUcode(G_OBJ_RENDERMODE, S2DEX2_Obj_RenderMode);
        gsp.setUcode(G_OBJ_RECTANGLE_R, S2DEX2_Obj_Rectangle_R);
        gsp.setUcode(G_OBJ_LOADTXTR, S2DEX2_Obj_LoadTxtr);
        gsp.setUcode(G_OBJ_LDTX_SPRITE, S2DEX2_Obj_LdTx_Sprite);
        gsp.setUcode(G_OBJ_LDTX_RECT, S2DEX2_Obj_LdTx_Rect);
        gsp.setUcode(G_OBJ_LDTX_RECT_R, S2DEX2_Obj_LdTx_Rect_R);
        gsp.setUcode(G_MOVEWORD, F3dex2.F3DEX2_MoveWord);
        gsp.setUcode(G_SETOTHERMODE_H, F3dex2.F3DEX2_SetOtherMode_H);
        gsp.setUcode(G_SETOTHERMODE_L, F3dex2.F3DEX2_SetOtherMode_L);
        gsp.setUcode(G_ENDDL, F3d.F3D_EndDL);
        gsp.setUcode(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setUcode(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setUcode(G_LOAD_UCODE, F3dex.F3DEX_Load_uCode);

        if (DEBUG_MICROCODE) System.out.println("Initialized S2DEX2 opcodes");
    }

}
