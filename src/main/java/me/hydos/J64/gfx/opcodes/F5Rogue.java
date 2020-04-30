package me.hydos.J64.gfx.opcodes;

public class F5Rogue extends F3dex {
	// TODO: Fill in from https://github.com/gonetz/GLideN64/blob/master/src/uCodes/F5Rogue.cpp
	
	void F5Rogue_Init()
	{
		gsp.setupFunctions();
		// Set GeometryMode flags
		GBI_InitFlags();

//		GBI.PCStackSize = 10;

		//          Command Value				Command Function
		gsp.setGBI( F3D_SPNOOP,					F3D_SPNoOp );
//		gsp.setGBI( F3D_MTX,					F3DSWRS_Mtx );
//		gsp.setGBI( F3DSWRS_VTXCOLOR,			F3DSWRS_VertexColor );
//		gsp.setGBI( F3DSWRS_MOVEMEM,			F3DSWRS_MoveMem );
//		gsp.setGBI( F3DSWRS_VTX,				F3DSWRS_Vtx );
//		gsp.setGBI( F3DSWRS_TRI_GEN,			F3DSWRS_TriGen );
//		gsp.setGBI( F3DSWRS_DL,					F3DSWRS_DList );
//		gsp.setGBI( F3DSWRS_BRANCHDL,			F3DSWRS_BranchDList );
		gsp.setGBI( F3D_RESERVED3,				F3D_Reserved3 );

//		gsp.setGBI( F3DSWRS_TRI1,				F3DSWRS_Tri );
//		gsp.setGBI( F3DSWRS_SETOTHERMODE_H_EX,	F3DSWRS_SetOtherMode_H_EX );
//		gsp.setGBI( F3DSWRS_TEXRECT_GEN,		F3DSWRS_TexrectGen );
//		gsp.setGBI( F3DSWRS_MOVEWORD,			F3DSWRS_MoveWord );
		gsp.setGBI( F3D_TEXTURE,				F3D_Texture );
		gsp.setGBI( F3D_SETOTHERMODE_H,			F3D_SetOtherMode_H );
		gsp.setGBI( F3D_SETOTHERMODE_L,			F3D_SetOtherMode_L );
//		gsp.setGBI( F3DSWRS_ENDDL,				F3DSWRS_EndDisplayList );
		gsp.setGBI( F3D_SETGEOMETRYMODE,		F3D_SetGeometryMode );
		gsp.setGBI( F3D_CLEARGEOMETRYMODE,		F3D_ClearGeometryMode );
//		gsp.setGBI( F3DSWRS_JUMPSWDL,			F3DSWRS_JumpSWDL );
//		gsp.setGBI( F3DSWRS_TRI2,				F3DSWRS_Tri );
//		gsp.setGBI( F3DSWRS_SETOTHERMODE_L_EX,	F3DSWRS_SetOtherMode_L_EX );
	}
}
