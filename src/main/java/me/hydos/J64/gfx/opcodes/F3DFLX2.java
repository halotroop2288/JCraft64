package me.hydos.J64.gfx.opcodes;

public class F3DFLX2 extends F3dex2 {

	void F3DFLX2_Init()
	{
		gsp.setupFunctions();
		// Set GeometryMode flags
		GBI_InitFlags();

//		GBI.PCStackSize = 18;

		// 			Command Value				Command Function
		gsp.setGBI( F3DEX2_RDPHALF_2,			F3D_RDPHalf_2 );
		gsp.setGBI( F3DEX2_SETOTHERMODE_H,		F3DEX2_SetOtherMode_H );
		gsp.setGBI( F3DEX2_SETOTHERMODE_L,		F3DEX2_SetOtherMode_L );
		gsp.setGBI( F3DEX2_RDPHALF_1,			F3D_RDPHalf_1 );
		gsp.setGBI( F3DEX2_SPNOOP,				F3D_SPNoOp );
		gsp.setGBI( F3DEX2_ENDDL,				F3D_EndDL );
		gsp.setGBI( F3DEX2_DL,					F3D_DList );
		gsp.setGBI( F3DEX2_LOAD_UCODE,			F3DEX_Load_uCode );
//		gsp.setGBI( F3DEX2_MOVEMEM,				F3DFLX2_MoveMem );
		gsp.setGBI( F3DEX2_MOVEWORD,			F3DEX2_MoveWord );
		gsp.setGBI( F3DEX2_MTX,					F3DEX2_Mtx );
		gsp.setGBI( F3DEX2_GEOMETRYMODE,		F3DEX2_GeometryMode );
		gsp.setGBI( F3DEX2_POPMTX,				F3DEX2_PopMtx );
		gsp.setGBI( F3DEX2_TEXTURE,				F3DEX2_Texture );
		gsp.setGBI( F3DEX2_DMA_IO,				F3DEX2_DMAIO );
		gsp.setGBI( F3DEX2_SPECIAL_1,			F3DEX2_Special_1 );
		gsp.setGBI( F3DEX2_SPECIAL_2,			F3DEX2_Special_2 );
		gsp.setGBI( F3DEX2_SPECIAL_3,			F3DEX2_Special_3 );

		gsp.setGBI( F3DEX2_VTX,					F3DEX2_Vtx );
		gsp.setGBI( F3DEX2_MODIFYVTX,			F3DEX_ModifyVtx );
		gsp.setGBI(	F3DEX2_CULLDL,				F3DEX_CullDL );
		gsp.setGBI( F3DEX2_BRANCH_Z,			F3DEX_Branch_Z );
		gsp.setGBI( F3DEX2_TRI1,				F3DEX2_Tri1 );
		gsp.setGBI( F3DEX2_TRI2,				F3DEX_Tri2 );
		gsp.setGBI( F3DEX2_QUAD,				F3DEX2_Quad );
//		gsp.setGBI( F3DEX2_LINE3D,				F3DEX2_Line3D );
	}
}
