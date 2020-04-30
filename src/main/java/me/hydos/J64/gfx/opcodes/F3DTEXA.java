package me.hydos.J64.gfx.opcodes;

import me.hydos.J64.gfx.rsp.GBIFunc;

public class F3DTEXA extends F3dex {

	int F3DTEXA_LOADTEX			= 0xB5;
	int F3DTEXA_SETTILESIZE		= 0xBE;
	
	int G_TEXA_LOADTEX, G_TEXA_SETTILESIZE;

	GBIFunc F3DTTEXA_LoadTex = (w0, w1) -> {
		System.err.println("ERROR: F3DTTEXA_LoadTex not imlemented!");
//		Rdpfuncs.RDP_SetTImg(0x3d100000, w1);
//		Rdpfuncs.RDP_SetTile(0x35100000, 0x07000000);
//		Rdpfuncs.RDP_LoadBlock(0x33000000, 0x27000000 | ((w0>>0)&SR_MASK_24));
	};
	
	GBIFunc F3DTTEXA_SetTileSize = (int w0, int w1) -> { // FIXME RDP needs to be rewritten entirely.
		System.err.println("ERROR: F3DTTEXA_SetTileSize not imlemented!");
//		final int firstHalf = ( ( ( w1>>24 )&SR_MASK_8 )<< 9 )&SR_MASK_8;
//		Rdpfuncs.RDP_SetTile(0x35400000 | firstHalf, (w0>>0)&SR_MASK_24);
//		Rdpfuncs.RDP_SetTileSize(0x32000000, _SHIFTR(w1, 0, 24));
	};

	void F3DTEXA_Init()
	{
		gsp.setupFunctions();
		// Set GeometryMode flags
		GBI_InitFlags();
	
//		GBI.PCStackSize = 18;
	
		//          Command Value			Command Function
		gsp.setGBI(F3D_SPNOOP, 				F3D_SPNoOp);
		gsp.setGBI(F3D_MTX, 				F3D_Mtx);
		gsp.setGBI(F3D_RESERVED0, 			F3D_Reserved0);
		gsp.setGBI(F3D_MOVEMEM, 			F3D_MoveMem);
		gsp.setGBI(F3D_VTX, 				F3DEX_Vtx);
		gsp.setGBI(F3D_RESERVED1, 			F3D_Reserved1);
		gsp.setGBI(F3D_DL, 					F3D_DList);
		gsp.setGBI(F3D_RESERVED2, 			F3D_Reserved2);
		gsp.setGBI(F3D_RESERVED3, 			F3D_Reserved3);
		gsp.setGBI(F3D_SPRITE2D_BASE, 		F3D_Sprite2D_Base);
	
		gsp.setGBI(F3D_TRI1, 				F3DEX_Tri1);
		gsp.setGBI(F3DTEXA_SETTILESIZE, 	F3DTTEXA_SetTileSize);
		gsp.setGBI(F3D_POPMTX, 				F3D_PopMtx);
		gsp.setGBI(F3D_MOVEWORD, 			F3D_MoveWord);
		gsp.setGBI(F3D_TEXTURE, 			F3D_Texture);
		gsp.setGBI(F3D_SETOTHERMODE_H, 		F3D_SetOtherMode_H);
		gsp.setGBI(F3D_SETOTHERMODE_L, 		F3D_SetOtherMode_L);
		gsp.setGBI(F3D_ENDDL, 				F3D_EndDL);
		gsp.setGBI(F3D_SETGEOMETRYMODE, 	F3D_SetGeometryMode);
		gsp.setGBI(F3D_CLEARGEOMETRYMODE, 	F3D_ClearGeometryMode);
		gsp.setGBI(F3DTEXA_LOADTEX, 		F3DTTEXA_LoadTex);
		gsp.setGBI(F3D_RDPHALF_1, 			F3D_RDPHalf_1);
		gsp.setGBI(F3D_RDPHALF_2, 			F3D_RDPHalf_2);
		gsp.setGBI(F3DEX_MODIFYVTX, 		F3DEX_ModifyVtx);
		gsp.setGBI(F3DEX_TRI2, 				F3DEX_Tri2);
		gsp.setGBI(F3DEX_BRANCH_Z, 			F3DEX_Branch_Z);
		gsp.setGBI(F3DEX_LOAD_UCODE, 		F3DEX_Load_uCode);
	}
}
