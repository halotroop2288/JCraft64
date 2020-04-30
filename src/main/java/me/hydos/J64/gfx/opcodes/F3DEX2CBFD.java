package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rsp.Gsp;

public class F3DEX2CBFD extends F3dex2
{
	// TODO: Fill in from https://github.com/gonetz/GLideN64/blob/master/src/uCodes/F3DEX2CBFD.cpp
	// FIXME: I tried - Caroline
	
	static void F3DEX2CBFD_Vtx( int w0, short w1 )
	{
		int n = (w0>>12)&SR_MASK_8;

		gsp.gspCBFDVertex( w1, n, ((w0>>1)&SR_MASK_7) - n );
	}
	
	static void F3DEX2CBFD_CoordMod(int _w0, short _w1)
	{
		System.out.println("gSPCoordMod("+ _w0 + ", " + _w1 + ");");
		if ((_w0 & 8) != 0)
			return;
		int idx = (_w0>>1)&SR_MASK_2;
		int pos = _w0 & 0x30;
		if (pos == 0) {
			Gsp.Cbfd.vertexCoordMod[0 + idx] = _w1>>16&SR_MASK_16;
			Gsp.Cbfd.vertexCoordMod[1 + idx] = _w1>>0&SR_MASK_16;
		}
		else if (pos == 0x10) {
			Gsp.Cbfd.vertexCoordMod[4 + idx] = (_w1>>16)&SR_MASK_16;
			Gsp.Cbfd.vertexCoordMod[5 + idx] = (_w1>>0)&SR_MASK_16;
			Gsp.Cbfd.vertexCoordMod[12 + idx] = Gsp.Cbfd.vertexCoordMod[0 + idx] + Gsp.Cbfd.vertexCoordMod[4 + idx];
			Gsp.Cbfd.vertexCoordMod[13 + idx] = Gsp.Cbfd.vertexCoordMod[1 + idx] + Gsp.Cbfd.vertexCoordMod[5 + idx];
		}
		else if (pos == 0x20) {
			Gsp.Cbfd.vertexCoordMod[8 + idx] = _w1>>16&SR_MASK_16;
			Gsp.Cbfd.vertexCoordMod[9 + idx] = _w1>>0&SR_MASK_16;
		}
	}
	
	static void F3DEX2CBFD_MoveWord( int w0, int w1 )
	{
		switch ((w0>>16)&SR_MASK_8)
		{
			case G_MW_NUMLIGHT:
				gsp.gSPNumLights( w1 / 48 );
				break;
			case G_MW_CLIP:
				gsp.gSPClipRatio( w1 );
				break;
			case G_MW_SEGMENT:
				gsp.gSPSegment( ( w0>>0)&SR_MASK_16 >> 2, w1 & 0x00FFFFFF );
				break;
			case G_MW_FOG:
				gsp.gSPFogFactor( (short)((w1>>16)&SR_MASK_16), (short)((w1>>0)&SR_MASK_16));
				break;
			case G_MW_PERSPNORM:
				gsp.gSPPerspNormalize( (short)w1 );
				break;
			case G_MW_COORD_MOD:
				F3DEX2CBFD_CoordMod( w0, (short)w1 );
				break;
		}
	}
}
