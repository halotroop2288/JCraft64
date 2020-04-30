package me.hydos.J64.gfx.opcodes;

import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class ZSortBOSS extends ZSort
{
	// TODO: Fill in from https://github.com/gonetz/GLideN64/blob/master/src/uCodes/ZSortBOSS.cpp
	
	GBIFunc ZSortBOSS_SetOtherMode = (int _w0, int _w1) -> {
//		gdp.otherMode.h = (_w0 & gstate.updatemask[0]) | (gdp.otherMode.h & ~gstate.updatemask[0]);
//		gdp.otherMode.l = (_w1 & gstate.updatemask[1]) | (gdp.otherMode.l & ~gstate.updatemask[1]);

		final int w0 = gdp.otherMode.h;
		final int w1 = gdp.otherMode.l;

//		gDPSetOtherMode( ( this.w0>>0)&RS_MASK_24,	// mode0
//						 	w1 );					// mode1

		System.out.println("ZSortBOSS_SetOtherMode (mode0: 0x" + gdp.otherMode.h + ", mode1: 0x" + gdp.otherMode.l + ")");
	};

	GBIFunc ZSortBOSS_TriangleCommand = ( int a, int _w1 ) -> {
		assert(((_w1 >> 8) & 0x3f) == 0x0e);	//Shade, Texture Triangle
		Gsp.texture.scales = 1.0f;
		Gsp.texture.scalet = 1.0f;
		Gsp.texture.level = (_w1 >> 3) & 0x7;
		Gsp.texture.on = 1;
		Gsp.texture.tile = _w1 & 0x7;
	
//		Gsp.SetGeometryMode(G_SHADING_SMOOTH | G_SHADE);
		System.out.println("ZSortBOSS_TriangleCommand (cmd: 0x" + ((_w1 >> 8) & 0x3f) + ", level: " + Gsp.texture.level + ", tile: " + Gsp.texture.tile + ")");
	};
	
	GBIFunc ZSortBOSS_FlushRDPCMDBuffer = (int a, int b) -> {
		System.out.println("ZSortBOSS_FlushRDPCMDBuffer Ignored");
	};
	
	GBIFunc ZSortBOSS_Reserved = (int a, int b) -> {
		assert(false);
	};
	
	private final int
	G_ZSBOSS_ENDMAINDL			= 0x02,
	G_ZSBOSS_MOVEMEM			= 0x04,
	G_ZSBOSS_MTXCAT				= 0x0A,
	G_ZSBOSS_MULT_MPMTX			= 0x0C,
	G_ZSBOSS_MOVEWORD			= 0x06,
	G_ZSBOSS_TRANSPOSEMTX		= 0x08,
	G_ZSBOSS_RDPCMD				= 0x0E,
	G_ZSBOSS_OBJ				= 0x10,
	G_ZSBOSS_WAITSIGNAL			= 0x12,
	G_ZSBOSS_LIGHTING			= 0x14,
	G_ZSBOSS_RESERVED0			= 0x16,
	G_ZSBOSS_TRANSFORMLIGHTS	= 0x18,
	G_ZSBOSS_ENDSUBDL			= 0x1A,
	G_ZSBOSS_AUDIO2				= 0x1C,
	G_ZSBOSS_CLEARBUFFER		= 0x1E,
	G_ZSBOSS_RESERVED1			= 0x20,
	G_ZSBOSS_AUDIO3				= 0x22,
	G_ZSBOSS_AUDIO4				= 0x24,
	G_ZSBOSS_AUDIO1				= 0x26,
	G_ZSBOSS_UPDATEMASK			= 0xDD,
	G_ZSBOSS_TRIANGLECOMMAND	= 0xDE,
	G_ZSBOSS_FLUSHRDPCMDBUFFER	= 0xDF,
	G_ZSBOSS_RDPHALF_1			= 0xE1,
	G_ZSBOSS_SETOTHERMODE_H		= 0xE3,
	G_ZSBOSS_SETOTHERMODE_L		= 0xE2,
	G_ZSBOSS_RDPSETOTHERMODE	= 0xEF,
	G_ZSBOSS_RDPHALF_2			= 0xF1;
	
	int G_ZENDMAINDL, G_ZENDSUBDL, G_ZUPDATEMASK, G_ZSETOTHERMODE, G_ZFLUSHRDPCMDBUFFER, G_ZMOVEWORD, G_ZCLEARBUFFER,
	G_ZTRIANGLECOMMAND, G_ZTRANSPOSEMTX, G_ZTRANSFORMLIGHTS, G_ZAUDIO1, G_ZAUDIO2, G_ZAUDIO3, G_ZAUDIO4;
	
	void ZSortBOSS_Init()
	{
		gsp.setupFunctions();
		// Set GeometryMode flags
		GBI_InitFlags();

//		GBI.PCStackSize = 10;

//		memset(&gstate, 0, sizeof(gstate));
//		gstate.invw_factor = 10.0f;

		//			Command Value				Command Function
		gsp.setGBI( F3D_SPNOOP,					F3D_SPNoOp );
//		gsp.setGBI( G_ZSBOSS_ENDMAINDL,			ZSortBOSS_EndMainDL );
//		gsp.setGBI( G_ZSBOSS_MOVEMEM,			ZSortBOSS_MoveMem );
//		gsp.setGBI( G_ZSBOSS_MOVEWORD,			ZSortBOSS_MoveWord );
//		gsp.setGBI( G_ZSBOSS_TRANSPOSEMTX,		ZSortBOSS_TransposeMTX );
//		gsp.setGBI( G_ZSBOSS_MTXCAT,			ZSortBOSS_MTXCAT );
//		gsp.setGBI( G_ZSBOSS_MULT_MPMTX,		ZSortBOSS_MultMPMTX );
//		gsp.setGBI( G_ZSBOSS_RDPCMD,			ZSort_RDPCMD );
//		gsp.setGBI( G_ZSBOSS_OBJ,				ZSortBOSS_Obj );
//		gsp.setGBI( G_ZSBOSS_WAITSIGNAL,		ZSortBOSS_WaitSignal );
//		gsp.setGBI( G_ZSBOSS_LIGHTING,			ZSortBOSS_Lighting );
		gsp.setGBI( G_ZSBOSS_RESERVED0,			ZSortBOSS_Reserved );
//		gsp.setGBI( G_ZSBOSS_TRANSFORMLIGHTS,	ZSortBOSS_TransformLights );
//		gsp.setGBI( G_ZSBOSS_ENDSUBDL,			ZSortBOSS_EndSubDL );
//		gsp.setGBI( G_ZSBOSS_AUDIO2,			ZSortBOSS_Audio2 );
//		gsp.setGBI( G_ZSBOSS_CLEARBUFFER,		ZSortBOSS_ClearBuffer );
		gsp.setGBI( G_ZSBOSS_RESERVED1,			ZSortBOSS_Reserved );
//		gsp.setGBI( G_ZSBOSS_AUDIO3,			ZSortBOSS_Audio3 );
//		gsp.setGBI( G_ZSBOSS_AUDIO4,			ZSortBOSS_Audio4 );
//		gsp.setGBI( G_ZSBOSS_AUDIO1,			ZSortBOSS_Audio1 );

		// RDP Commands
//		gsp.setGBI( G_ZSBOSS_UPDATEMASK,		ZSortBOSS_UpdateMask );
		gsp.setGBI( G_ZSBOSS_TRIANGLECOMMAND,	ZSortBOSS_TriangleCommand );
		gsp.setGBI( G_ZSBOSS_FLUSHRDPCMDBUFFER,	ZSortBOSS_FlushRDPCMDBuffer );
		gsp.setGBI( G_ZSBOSS_RDPHALF_1,			F3D_RDPHalf_1 );
//		gsp.setGBI( G_ZSBOSS_SETOTHERMODE_L,	ZSortBOSS_SetOtherMode_L );
//		gsp.setGBI( G_ZSBOSS_SETOTHERMODE_H,	ZSortBOSS_SetOtherMode_H );
		gsp.setGBI( G_ZSBOSS_RDPSETOTHERMODE,	ZSortBOSS_SetOtherMode );
		gsp.setGBI( G_ZSBOSS_RDPHALF_2,			F3D_RDPHalf_2 );
	}
}
