package me.hydos.J64.gfx.opcodes;

import me.hydos.J64.gfx.rsp.GBIFunc;

public class F3DGolden extends F3d {
	public int F3DGOLDEN_MOVEWORD	= 0xBD;
	public int F3D_TRIX				= 0xB1;
	public int G_GOLDEN_MOVEWORD;
	
	GBIFunc F3D_TriX = (w0, w1) ->
	{
		while(w1 != 0) {
			short v0 = (short) (w1 & 0xf);
			w1 >>= 4;

			short v1 = (short) (w1 & 0xf);
			w1 >>= 4;

			short v2 = (short) (w0 & 0xf);
			w0 >>= 4;

			gsp.gSPTriangle(v0, v1, v2);
		}
		gsp.gSPFlushTriangles();
	};
	
	void F3DGOLDEN_Init()
	{
		gsp.setupFunctions();
		// Set GeometryMode flags
		GBI_InitFlags();

//		GBI.PCStackSize = 10;

		//			Command Value			Command Function
		gsp.setGBI( F3D_SPNOOP,				F3D_SPNoOp );
		gsp.setGBI( F3D_MTX,				F3D_Mtx );
		gsp.setGBI( F3D_RESERVED0,			F3D_Reserved0 );
		gsp.setGBI( F3D_MOVEMEM,			F3D_MoveMem );
		gsp.setGBI( F3D_VTX,				F3D_Vtx );
		gsp.setGBI( F3D_RESERVED1,			F3D_Reserved1 );
		gsp.setGBI( F3D_DL,					F3D_DList );
		gsp.setGBI( F3D_RESERVED2,			F3D_Reserved2 );
		gsp.setGBI( F3D_RESERVED3,			F3D_Reserved3 );
		gsp.setGBI( F3D_SPRITE2D_BASE,		F3D_Sprite2D_Base );

		gsp.setGBI( F3D_TRI1,				F3D_Tri1 );
		gsp.setGBI( F3D_CULLDL,				F3D_CullDL );
		gsp.setGBI( F3DGOLDEN_MOVEWORD,		F3D_MoveWord );
		gsp.setGBI( F3D_MOVEWORD,			F3D_MoveWord );
		gsp.setGBI( F3D_TEXTURE,			F3D_Texture );
		gsp.setGBI( F3D_SETOTHERMODE_H,		F3D_SetOtherMode_H );
		gsp.setGBI( F3D_SETOTHERMODE_L,		F3D_SetOtherMode_L );
		gsp.setGBI( F3D_ENDDL,				F3D_EndDL );
		gsp.setGBI( F3D_SETGEOMETRYMODE,	F3D_SetGeometryMode );
		gsp.setGBI( F3D_CLEARGEOMETRYMODE,	F3D_ClearGeometryMode );
		gsp.setGBI( F3D_QUAD,				F3D_Quad );
		gsp.setGBI( F3D_RDPHALF_1,			F3D_RDPHalf_1 );
		gsp.setGBI( F3D_RDPHALF_2,			F3D_RDPHalf_2 );
		gsp.setGBI( F3D_RDPHALF_CONT,		F3D_RDPHalf_Cont );
		gsp.setGBI( F3D_TRIX,				F3D_TriX );
	}
}
