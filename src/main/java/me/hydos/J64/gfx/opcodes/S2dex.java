package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

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
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_BG_Copy = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Rectangle = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Sprite = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_MoveMem = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Select_DL = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_RenderMode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_Rectangle_R = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LoadTxtr = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Sprite = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Rect = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static GBIFunc S2DEX_Obj_LdTx_Rect_R = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };

    public static void S2DEX_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        GBI_InitFlags();
        G_SPNOOP = F3D_SPNOOP;
        G_BG_1CYC = S2DEX_BG_1CYC;
        G_BG_COPY = S2DEX_BG_COPY;
        G_OBJ_RECTANGLE = S2DEX_OBJ_RECTANGLE;
        G_OBJ_SPRITE = S2DEX_OBJ_SPRITE;
        G_OBJ_MOVEMEM = S2DEX_OBJ_MOVEMEM;
        G_DL = F3D_DL;
        G_SELECT_DL = S2DEX_SELECT_DL;
        G_OBJ_RENDERMODE = S2DEX_OBJ_RENDERMODE;
        G_OBJ_RECTANGLE_R = S2DEX_OBJ_RECTANGLE_R;
        G_OBJ_LOADTXTR = S2DEX_OBJ_LOADTXTR;
        G_OBJ_LDTX_SPRITE = S2DEX_OBJ_LDTX_SPRITE;
        G_OBJ_LDTX_RECT = S2DEX_OBJ_LDTX_RECT;
        G_OBJ_LDTX_RECT_R = S2DEX_OBJ_LDTX_RECT_R;
        G_MOVEWORD = F3D_MOVEWORD;
        G_SETOTHERMODE_H = F3D_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3D_SETOTHERMODE_L;
        G_ENDDL = F3D_ENDDL;
        G_RDPHALF_1 = F3D_RDPHALF_1;
        G_RDPHALF_2 = F3D_RDPHALF_2;
        G_LOAD_UCODE = S2DEX_LOAD_UCODE;

        gsp.geometryMode = 0;

        gsp.pcStackSize = 18;

        gsp.setGBI(G_SPNOOP, F3D_SPNoOp);
        gsp.setGBI(G_BG_1CYC, S2DEX_BG_1Cyc);
        gsp.setGBI(G_BG_COPY, S2DEX_BG_Copy);
        gsp.setGBI(G_OBJ_RECTANGLE, S2DEX_Obj_Rectangle);
        gsp.setGBI(G_OBJ_SPRITE, S2DEX_Obj_Sprite);
        gsp.setGBI(G_OBJ_MOVEMEM, S2DEX_Obj_MoveMem);
        gsp.setGBI(G_DL, F3D_DList);
        gsp.setGBI(G_SELECT_DL, S2DEX_Select_DL);
        gsp.setGBI(G_OBJ_RENDERMODE, S2DEX_Obj_RenderMode);
        gsp.setGBI(G_OBJ_RECTANGLE_R, S2DEX_Obj_Rectangle_R);
        gsp.setGBI(G_OBJ_LOADTXTR, S2DEX_Obj_LoadTxtr);
        gsp.setGBI(G_OBJ_LDTX_SPRITE, S2DEX_Obj_LdTx_Sprite);
        gsp.setGBI(G_OBJ_LDTX_RECT, S2DEX_Obj_LdTx_Rect);
        gsp.setGBI(G_OBJ_LDTX_RECT_R, S2DEX_Obj_LdTx_Rect_R);
        gsp.setGBI(G_MOVEWORD, F3D_MoveWord);
        gsp.setGBI(G_SETOTHERMODE_H, F3D_SetOtherMode_H);
        gsp.setGBI(G_SETOTHERMODE_L, F3D_SetOtherMode_L);
        gsp.setGBI(G_ENDDL, F3D_EndDL);
        gsp.setGBI(G_RDPHALF_1, F3D_RDPHalf_1);
        gsp.setGBI(G_RDPHALF_2, F3D_RDPHalf_2);
        gsp.setGBI(G_LOAD_UCODE, F3DEX_Load_uCode);

        if (DEBUG_MICROCODE) System.out.println("Initialized S2DEX opcodes");
    }

	static
	S2DEXVersion s2dexversion = S2DEXVersion.eVer1_7;

	enum S2DEXVersion
	{
		eVer1_3,
		eVer1_5,
		eVer1_7
	};

	public static void S2DEX_1_03Init(Gsp gsp, Gdp gdp)
	{
		init(gsp, gdp);
		s2dexversion = S2DEXVersion.eVer1_3;
	}
	
	public static void S2DEX_1_05Init(Gsp gsp, Gdp gdp)
	{
		init(gsp, gdp);
		s2dexversion = S2DEXVersion.eVer1_5;
	}

	public static void S2DEX_1_07Init(Gsp gsp, Gdp gdp)
	{
		init(gsp, gdp);
		s2dexversion = S2DEXVersion.eVer1_7;
	}
}
