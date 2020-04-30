package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class F3dex2 extends F3dex {
    
    public static final int F3DEX2_MTX_STACKSIZE=	18;
    
    public static final int F3DEX2_MTX_MODELVIEW=	0x00;
    public static final int F3DEX2_MTX_PROJECTION=	0x04;
    public static final int F3DEX2_MTX_MUL=		0x00;
    public static final int F3DEX2_MTX_LOAD=		0x02;
    public static final int F3DEX2_MTX_NOPUSH=		0x00;
    public static final int F3DEX2_MTX_PUSH=		0x01;
    
    public static final int F3DEX2_TEXTURE_ENABLE=	0x00000000;
    public static final int F3DEX2_SHADING_SMOOTH=	0x00200000;
    public static final int F3DEX2_CULL_FRONT=		0x00000200;
    public static final int F3DEX2_CULL_BACK=		0x00000400;
    public static final int F3DEX2_CULL_BOTH=		0x00000600;
    public static final int F3DEX2_CLIPPING=		0x00800000;
    
    public static final int F3DEX2_MV_VIEWPORT=		8;
    
    public static final int F3DEX2_MWO_aLIGHT_1=		0x00;
    public static final int F3DEX2_MWO_bLIGHT_1=		0x04;
    public static final int F3DEX2_MWO_aLIGHT_2=		0x18;
    public static final int F3DEX2_MWO_bLIGHT_2=		0x1c;
    public static final int F3DEX2_MWO_aLIGHT_3=		0x30;
    public static final int F3DEX2_MWO_bLIGHT_3=		0x34;
    public static final int F3DEX2_MWO_aLIGHT_4=		0x48;
    public static final int F3DEX2_MWO_bLIGHT_4=		0x4c;
    public static final int F3DEX2_MWO_aLIGHT_5=		0x60;
    public static final int F3DEX2_MWO_bLIGHT_5=		0x64;
    public static final int F3DEX2_MWO_aLIGHT_6=		0x78;
    public static final int F3DEX2_MWO_bLIGHT_6=		0x7c;
    public static final int F3DEX2_MWO_aLIGHT_7=		0x90;
    public static final int F3DEX2_MWO_bLIGHT_7=		0x94;
    public static final int F3DEX2_MWO_aLIGHT_8=		0xa8;
    public static final int F3DEX2_MWO_bLIGHT_8=		0xac;
    
    public static final int	F3DEX2_RDPHALF_2=			0xF1;
    public static final int	F3DEX2_SETOTHERMODE_H=		0xE3;
    public static final int	F3DEX2_SETOTHERMODE_L=		0xE2;
    public static final int	F3DEX2_RDPHALF_1=			0xE1;
    public static final int	F3DEX2_SPNOOP=				0xE0;
    public static final int	F3DEX2_ENDDL=				0xDF;
    public static final int	F3DEX2_DL=					0xDE;
    public static final int	F3DEX2_LOAD_UCODE=			0xDD;
    public static final int	F3DEX2_MOVEMEM=				0xDC;
    public static final int	F3DEX2_MOVEWORD=			0xDB;
    public static final int	F3DEX2_MTX=					0xDA;
    public static final int F3DEX2_GEOMETRYMODE=		0xD9;
    public static final int	F3DEX2_POPMTX=				0xD8;
    public static final int	F3DEX2_TEXTURE=				0xD7;
    public static final int	F3DEX2_DMA_IO=				0xD6;
    public static final int	F3DEX2_SPECIAL_1=			0xD5;
    public static final int	F3DEX2_SPECIAL_2=			0xD4;
    public static final int	F3DEX2_SPECIAL_3=			0xD3;
    
    public static final int	F3DEX2_VTX=					0x01;
    public static final int	F3DEX2_MODIFYVTX=			0x02;
    public static final int	F3DEX2_CULLDL=				0x03;
    public static final int	F3DEX2_BRANCH_Z=			0x04;
    public static final int	F3DEX2_TRI1=				0x05;
    public static final int F3DEX2_TRI2=				0x06;
    public static final int F3DEX2_QUAD=				0x07;
    
    /** Creates a new instance of F3dex2 */
    public F3dex2() {
    }
    
    public static GBIFunc F3DEX2_Mtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPMatrix(gsp.RSP_SegmentToPhysical(w1), (w0&SR_MASK_8) ^ G_MTX_PUSH);
        }
    };
    
    public static GBIFunc F3DEX2_MoveMem = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch (w0&SR_MASK_8) {
                case F3DEX2_MV_VIEWPORT:
                    gsp.gSPViewport(gsp.RSP_SegmentToPhysical(w1));
                    break;
                case G_MV_MATRIX:
                    gsp.gSPForceMatrix(gsp.RSP_SegmentToPhysical(w1));
                    // force matrix takes two commands
                    gsp.getCmd();
//                    Rsp.PC[Rsp.PCi] += 8;
                    break;
                case G_MV_LIGHT:
                    int offset = ((w0>>8)&SR_MASK_8) << 3;
                    if (offset >= 48) {
                        gsp.gSPLight(gsp.RSP_SegmentToPhysical(w1), (offset - 24) / 24);
                    }
                    break;
            }
        }
    };
    
    public static GBIFunc F3DEX2_Vtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            int n = (w0>>>12)&SR_MASK_8;
            gsp.gSPVertex(gsp.RSP_SegmentToPhysical(w1), n, ((w0>>>1)&SR_MASK_7) - n);
        }
    };
    
    public static GBIFunc F3DEX2_Reserved1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DEX2_Tri1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP1Triangle((w0>>17)&SR_MASK_7,
                    (w0>>9)&SR_MASK_7,
                    (w0>>1)&SR_MASK_7,
                    0);
        }
    };
    
    public static GBIFunc F3DEX2_PopMtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPPopMatrixN(0, w1 >>> 6);
        }
    };
    
    public static GBIFunc F3DEX2_MoveWord = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch ((w0>>16)&SR_MASK_8) {
                case G_MW_FORCEMTX:
                    // Handled in movemem
                    break;
                case G_MW_MATRIX:
                    gsp.gSPInsertMatrix(w0&SR_MASK_16, w1);
                    break;
                case G_MW_NUMLIGHT:
                    gsp.gSPNumLights(w1 / 24);
                    break;
                case G_MW_CLIP:
                    gsp.gSPClipRatio(w1);
                    break;
                case G_MW_SEGMENT:
                    gsp.gSPSegment((w0&SR_MASK_16) >>> 2, w1 & 0x00FFFFFF);
                    break;
                case G_MW_FOG:
                    gsp.gSPFogFactor((short)((w1>>16)&SR_MASK_16), (short)(w1&SR_MASK_16));
                    break;
                case G_MW_LIGHTCOL:
                    switch (w0&SR_MASK_16) {
                        case F3DEX2_MWO_aLIGHT_1:
                            gsp.gSPLightColor(LIGHT_1, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_2:
                            gsp.gSPLightColor(LIGHT_2, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_3:
                            gsp.gSPLightColor(LIGHT_3, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_4:
                            gsp.gSPLightColor(LIGHT_4, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_5:
                            gsp.gSPLightColor(LIGHT_5, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_6:
                            gsp.gSPLightColor(LIGHT_6, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_7:
                            gsp.gSPLightColor(LIGHT_7, w1);
                            break;
                        case F3DEX2_MWO_aLIGHT_8:
                            gsp.gSPLightColor(LIGHT_8, w1);
                            break;
                    }
                    break;
                case G_MW_PERSPNORM:
                    gsp.gSPPerspNormalize((short)w1);
                    break;
            }
        }
    };
    
    public static GBIFunc F3DEX2_Texture = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPTexture(((w1>>16)&SR_MASK_16) * FIXED2FLOATRECIP16,
                    (w1&SR_MASK_16) * FIXED2FLOATRECIP16,
                    (w0>>11)&SR_MASK_3,
                    (w0>>8)&SR_MASK_3,
                    (w0>>1)&SR_MASK_7);
        }
    };
    
    public static GBIFunc F3DEX2_SetOtherMode_H = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch (32 - ((w0>>8)&SR_MASK_8) - ((w0&SR_MASK_8) + 1)) {
                case G_MDSFT_PIPELINE:
                    gdp.gDPPipelineMode(w1 >>> G_MDSFT_PIPELINE);
                    break;
                case G_MDSFT_CYCLETYPE:
                    gdp.gDPSetCycleType(w1 >>> G_MDSFT_CYCLETYPE);
                    break;
                case G_MDSFT_TEXTPERSP:
                    gdp.gDPSetTexturePersp(w1 >>> G_MDSFT_TEXTPERSP);
                    break;
                case G_MDSFT_TEXTDETAIL:
                    gdp.gDPSetTextureDetail(w1 >>> G_MDSFT_TEXTDETAIL);
                    break;
                case G_MDSFT_TEXTLOD:
                    gdp.gDPSetTextureLOD(w1 >>> G_MDSFT_TEXTLOD);
                    break;
                case G_MDSFT_TEXTLUT:
                    gdp.gDPSetTextureLUT(w1 >>> G_MDSFT_TEXTLUT);
                    break;
                case G_MDSFT_TEXTFILT:
                    gdp.gDPSetTextureFilter(w1 >>> G_MDSFT_TEXTFILT);
                    break;
                case G_MDSFT_TEXTCONV:
                    gdp.gDPSetTextureConvert(w1 >>> G_MDSFT_TEXTCONV);
                    break;
                case G_MDSFT_COMBKEY:
                    gdp.gDPSetCombineKey(w1 >>> G_MDSFT_COMBKEY);
                    break;
                case G_MDSFT_RGBDITHER:
                    gdp.gDPSetColorDither(w1 >>> G_MDSFT_RGBDITHER);
                    break;
                case G_MDSFT_ALPHADITHER:
                    gdp.gDPSetAlphaDither(w1 >>> G_MDSFT_ALPHADITHER);
                    break;
                default:
                    int length = (w0&SR_MASK_8) + 1;
                    int shift = 32 - ((w0>>8)&SR_MASK_8) - length;
                    int mask = ((1 << length) - 1) << shift;
                    
//                    gdp.otherMode.setH(gdp.otherMode.getH() & ~mask);
//                    gdp.otherMode.setH(gdp.otherMode.getH() | (w1 & mask));
                    gdp.otherMode.h = (gdp.otherMode.h & ~mask);
                    gdp.otherMode.h = (gdp.otherMode.h | (w1 & mask));
                    
                    gdp.changed |= Gdp.CHANGED_CYCLETYPE;
                    break;
            }
        }
    };
    
    public static GBIFunc F3DEX2_SetOtherMode_L = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            switch (32 - ((w0>>8)&SR_MASK_8) - ((w0&SR_MASK_8) + 1)) {
                case G_MDSFT_ALPHACOMPARE:
                    gdp.gDPSetAlphaCompare(w1 >>> G_MDSFT_ALPHACOMPARE);
                    break;
                case G_MDSFT_ZSRCSEL:
                    gdp.gDPSetDepthSource(w1 >>> G_MDSFT_ZSRCSEL);
                    break;
                case G_MDSFT_RENDERMODE:
                    gdp.gDPSetRenderMode(w1 & 0xCCCCFFFF, w1 & 0x3333FFFF);
                    break;
                default:
                    int length = (w0&SR_MASK_8) + 1;
                    int shift = 32 - ((w0>>8)&SR_MASK_8) - length;
                    int mask = ((1 << length) - 1) << shift;
                    
//                    gdp.otherMode.setL(gdp.otherMode.getL() & ~mask);
//                    gdp.otherMode.setL(gdp.otherMode.getL() | (w1 & mask));
                    gdp.otherMode.l = (gdp.otherMode.l & ~mask);
                    gdp.otherMode.l = (gdp.otherMode.l | (w1 & mask));
                    
                    gdp.changed |= Gdp.CHANGED_RENDERMODE | Gdp.CHANGED_ALPHACOMPARE;
                    break;
            }
        }
    };
    
    public static GBIFunc F3DEX2_GeometryMode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSPGeometryMode(~(w0&SR_MASK_24), w1);
        }
    };
    
    public static GBIFunc F3DEX2_DMAIO = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DEX2_Special_1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DEX2_Special_2 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DEX2_Special_3 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DEX2_Quad = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gsp.gSP2Triangles((w0>>17)&SR_MASK_7,
                    (w0>>9)&SR_MASK_7,
                    (w0>>1)&SR_MASK_7,
                    0,
                    (w1>>17)&SR_MASK_7,
                    (w1>>9)&SR_MASK_7,
                    (w1>>1)&SR_MASK_7,
                    0);
        }
    };
    
    public static void GBI_InitFlags() {
        G_MTX_STACKSIZE		= F3DEX2_MTX_STACKSIZE;
        G_MTX_MODELVIEW		= F3DEX2_MTX_MODELVIEW;
        G_MTX_PROJECTION        = F3DEX2_MTX_PROJECTION;
        G_MTX_MUL		= F3DEX2_MTX_MUL;
        G_MTX_LOAD		= F3DEX2_MTX_LOAD;
        G_MTX_NOPUSH		= F3DEX2_MTX_NOPUSH;
        G_MTX_PUSH		= F3DEX2_MTX_PUSH;
        G_TEXTURE_ENABLE        = F3DEX2_TEXTURE_ENABLE;
        G_SHADING_SMOOTH        = F3DEX2_SHADING_SMOOTH;
        G_CULL_FRONT		= F3DEX2_CULL_FRONT;
        G_CULL_BACK		= F3DEX2_CULL_BACK;
        G_CULL_BOTH		= F3DEX2_CULL_BOTH;
        G_CLIPPING		= F3DEX2_CLIPPING;
        G_MV_VIEWPORT		= F3DEX2_MV_VIEWPORT;
        G_MWO_aLIGHT_1		= F3DEX2_MWO_aLIGHT_1;
        G_MWO_bLIGHT_1		= F3DEX2_MWO_bLIGHT_1;
        G_MWO_aLIGHT_2		= F3DEX2_MWO_aLIGHT_2;
        G_MWO_bLIGHT_2		= F3DEX2_MWO_bLIGHT_2;
        G_MWO_aLIGHT_3		= F3DEX2_MWO_aLIGHT_3;
        G_MWO_bLIGHT_3		= F3DEX2_MWO_bLIGHT_3;
        G_MWO_aLIGHT_4		= F3DEX2_MWO_aLIGHT_4;
        G_MWO_bLIGHT_4		= F3DEX2_MWO_bLIGHT_4;
        G_MWO_aLIGHT_5		= F3DEX2_MWO_aLIGHT_5;
        G_MWO_bLIGHT_5		= F3DEX2_MWO_bLIGHT_5;
        G_MWO_aLIGHT_6		= F3DEX2_MWO_aLIGHT_6;
        G_MWO_bLIGHT_6		= F3DEX2_MWO_bLIGHT_6;
        G_MWO_aLIGHT_7		= F3DEX2_MWO_aLIGHT_7;
        G_MWO_bLIGHT_7		= F3DEX2_MWO_bLIGHT_7;
        G_MWO_aLIGHT_8		= F3DEX2_MWO_aLIGHT_8;
        G_MWO_bLIGHT_8		= F3DEX2_MWO_bLIGHT_8;
    }
    
    public static void F3DEX2_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        GBI_InitFlags();
        G_RDPHALF_2 = F3DEX2_RDPHALF_2;
        G_SETOTHERMODE_H = F3DEX2_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3DEX2_SETOTHERMODE_L;
        G_RDPHALF_1 = F3DEX2_RDPHALF_1;
        G_SPNOOP = F3DEX2_SPNOOP;
        G_ENDDL = F3DEX2_ENDDL;
        G_DL = F3DEX2_DL;
        G_LOAD_UCODE = F3DEX2_LOAD_UCODE;
        G_MOVEMEM = F3DEX2_MOVEMEM;
        G_MOVEWORD = F3DEX2_MOVEWORD;
        G_MTX = F3DEX2_MTX;
        G_GEOMETRYMODE = F3DEX2_GEOMETRYMODE;
        G_POPMTX = F3DEX2_POPMTX;
        G_TEXTURE = F3DEX2_TEXTURE;
        G_DMA_IO = F3DEX2_DMA_IO;
        G_SPECIAL_1 = F3DEX2_SPECIAL_1;
        G_SPECIAL_2 = F3DEX2_SPECIAL_2;
        G_SPECIAL_3 = F3DEX2_SPECIAL_3;
        G_VTX = F3DEX2_VTX;
        G_MODIFYVTX = F3DEX2_MODIFYVTX;
        G_CULLDL = F3DEX2_CULLDL;
        G_BRANCH_Z = F3DEX2_BRANCH_Z;
        G_TRI1 = F3DEX2_TRI1;
        G_TRI2 = F3DEX2_TRI2;
        G_QUAD = F3DEX2_QUAD;
        
        gsp.pcStackSize = 18;
        
        gsp.setGBI(G_RDPHALF_2, F3D_RDPHalf_2);
        gsp.setGBI(G_SETOTHERMODE_H, F3DEX2_SetOtherMode_H);
        gsp.setGBI(G_SETOTHERMODE_L, F3DEX2_SetOtherMode_L);
        gsp.setGBI(G_RDPHALF_1, F3D_RDPHalf_1);
        gsp.setGBI(G_SPNOOP, F3D_SPNoOp);
        gsp.setGBI(G_ENDDL, F3D_EndDL);
        gsp.setGBI(G_DL, F3D_DList);
        gsp.setGBI(G_LOAD_UCODE, F3DEX_Load_uCode);
        gsp.setGBI(G_MOVEMEM, F3DEX2_MoveMem);
        gsp.setGBI(G_MOVEWORD, F3DEX2_MoveWord);
        gsp.setGBI(G_MTX, F3DEX2_Mtx);
        gsp.setGBI(G_GEOMETRYMODE, F3DEX2_GeometryMode);
        gsp.setGBI(G_POPMTX, F3DEX2_PopMtx);
        gsp.setGBI(G_TEXTURE, F3DEX2_Texture);
        gsp.setGBI(G_DMA_IO, F3DEX2_DMAIO);
        gsp.setGBI(G_SPECIAL_1, F3DEX2_Special_1);
        gsp.setGBI(G_SPECIAL_2, F3DEX2_Special_2);
        gsp.setGBI(G_SPECIAL_3, F3DEX2_Special_3);
        
        gsp.setGBI(G_VTX, F3DEX2_Vtx);
        gsp.setGBI(G_MODIFYVTX, F3DEX_ModifyVtx);
        gsp.setGBI(G_CULLDL, F3DEX_CullDL);
        gsp.setGBI(G_BRANCH_Z, F3DEX_Branch_Z);
        gsp.setGBI(G_TRI1, F3DEX2_Tri1);
        gsp.setGBI(G_TRI2, F3DEX_Tri2);
        gsp.setGBI(G_QUAD, F3DEX2_Quad);
        
        if (DEBUG_MICROCODE) System.out.println("Initialized F3DEX2 opcodes");
    }
    
}
