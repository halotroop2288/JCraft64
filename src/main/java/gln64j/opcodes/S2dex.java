package gln64j.opcodes;

import gln64j.rdp.Gdp;
import gln64j.rsp.Gsp;

import static me.hydos.J64.util.debug.Debug.*;

import gln64j.rsp.GBIFunc;

import static gln64j.Gbi.*;

public class S2dex extends F3dex {

    public static final int G_BGLT_LOADBLOCK = 0x0033;
    public static final int G_BGLT_LOADTILE = 0xfff4;

    public static final int G_BG_FLAG_FLIPS = 0x01;
    public static final int G_BG_FLAG_FLIPT = 0x10;

    public static final int S2DEX_BG_1CYC = 0x01;
    public static final int S2DEX_BG_COPY = 0x02;
    public static final int S2DEX_OBJ_RECTANGLE = 0x03;
    public static final int S2DEX_OBJ_SPRITE = 0x04;
    public static final int S2DEX_OBJ_MOVEMEM = 0x05;
    public static final int S2DEX_LOAD_UCODE = 0xAF;
    public static final int S2DEX_SELECT_DL = 0xB0;
    public static final int S2DEX_OBJ_RENDERMODE = 0xB1;
    public static final int S2DEX_OBJ_RECTANGLE_R = 0xB2;
    public static final int S2DEX_OBJ_LOADTXTR = 0xC1;
    public static final int S2DEX_OBJ_LDTX_SPRITE = 0xC2;
    public static final int S2DEX_OBJ_LDTX_RECT = 0xC3;
    public static final int S2DEX_OBJ_LDTX_RECT_R = 0xC4;
    public static final int S2DEX_RDPHALF_0 = 0xE4;

    /**
     * Creates a new instance of S2dex
     */
    public S2dex() {
    }

    public static GBIFunc S2DEX_BG_1Cyc = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_BG_Copy = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Rectangle = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Sprite = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_MoveMem = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Select_DL = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_RenderMode = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Rectangle_R = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LoadTxtr = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Sprite = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Rect = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Rect_R = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };

    public static void S2DEX_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3dex.GBI_InitFlags();
        G_SPNOOP = F3d.F3D_SPNOOP;
        G_BG_1CYC = S2DEX_BG_1CYC;
        G_BG_COPY = S2DEX_BG_COPY;
        G_OBJ_RECTANGLE = S2DEX_OBJ_RECTANGLE;
        G_OBJ_SPRITE = S2DEX_OBJ_SPRITE;
        G_OBJ_MOVEMEM = S2DEX_OBJ_MOVEMEM;
        G_DL = F3d.F3D_DL;
        G_SELECT_DL = S2DEX_SELECT_DL;
        G_OBJ_RENDERMODE = S2DEX_OBJ_RENDERMODE;
        G_OBJ_RECTANGLE_R = S2DEX_OBJ_RECTANGLE_R;
        G_OBJ_LOADTXTR = S2DEX_OBJ_LOADTXTR;
        G_OBJ_LDTX_SPRITE = S2DEX_OBJ_LDTX_SPRITE;
        G_OBJ_LDTX_RECT = S2DEX_OBJ_LDTX_RECT;
        G_OBJ_LDTX_RECT_R = S2DEX_OBJ_LDTX_RECT_R;
        G_MOVEWORD = F3d.F3D_MOVEWORD;
        G_SETOTHERMODE_H = F3d.F3D_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3d.F3D_SETOTHERMODE_L;
        G_ENDDL = F3d.F3D_ENDDL;
        G_RDPHALF_1 = F3d.F3D_RDPHALF_1;
        G_RDPHALF_2 = F3d.F3D_RDPHALF_2;
        G_LOAD_UCODE = S2DEX_LOAD_UCODE;

        gsp.geometryMode = 0;

        gsp.pcStackSize = 18;

        gsp.setUcode(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setUcode(G_BG_1CYC, S2DEX_BG_1Cyc);
        gsp.setUcode(G_BG_COPY, S2DEX_BG_Copy);
        gsp.setUcode(G_OBJ_RECTANGLE, S2DEX_Obj_Rectangle);
        gsp.setUcode(G_OBJ_SPRITE, S2DEX_Obj_Sprite);
        gsp.setUcode(G_OBJ_MOVEMEM, S2DEX_Obj_MoveMem);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_SELECT_DL, S2DEX_Select_DL);
        gsp.setUcode(G_OBJ_RENDERMODE, S2DEX_Obj_RenderMode);
        gsp.setUcode(G_OBJ_RECTANGLE_R, S2DEX_Obj_Rectangle_R);
        gsp.setUcode(G_OBJ_LOADTXTR, S2DEX_Obj_LoadTxtr);
        gsp.setUcode(G_OBJ_LDTX_SPRITE, S2DEX_Obj_LdTx_Sprite);
        gsp.setUcode(G_OBJ_LDTX_RECT, S2DEX_Obj_LdTx_Rect);
        gsp.setUcode(G_OBJ_LDTX_RECT_R, S2DEX_Obj_LdTx_Rect_R);
        gsp.setUcode(G_MOVEWORD, F3d.F3D_MoveWord);
        gsp.setUcode(G_SETOTHERMODE_H, F3d.F3D_SetOtherMode_H);
        gsp.setUcode(G_SETOTHERMODE_L, F3d.F3D_SetOtherMode_L);
        gsp.setUcode(G_ENDDL, F3d.F3D_EndDL);
        gsp.setUcode(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setUcode(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setUcode(G_LOAD_UCODE, F3dex.F3DEX_Load_uCode);

        if (DEBUG_MICROCODE) System.out.println("Initialized S2DEX opcodes");
    }

}
