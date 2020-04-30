package me.hydos.J64.gfx;

public class Gbi {
    
    //Fixed point conversion factors
    public static final float FIXED2FLOATRECIP1=	0.5f;
    public static final float FIXED2FLOATRECIP2=	0.25f;
    public static final float FIXED2FLOATRECIP3=	0.125f;
    public static final float FIXED2FLOATRECIP4=	0.0625f;
    public static final float FIXED2FLOATRECIP5=	0.03125f;
    public static final float FIXED2FLOATRECIP6=	0.015625f;
    public static final float FIXED2FLOATRECIP7=	0.0078125f;
    public static final float FIXED2FLOATRECIP8=	0.00390625f;
    public static final float FIXED2FLOATRECIP9=	0.001953125f;
    public static final float FIXED2FLOATRECIP10=	0.0009765625f;
    public static final float FIXED2FLOATRECIP11=	0.00048828125f;
    public static final float FIXED2FLOATRECIP12=	0.00024414063f;
    public static final float FIXED2FLOATRECIP13=	0.00012207031f;
    public static final float FIXED2FLOATRECIP14=	6.1035156e-05f;
    public static final float FIXED2FLOATRECIP15=	3.0517578e-05f;
    public static final float FIXED2FLOATRECIP16=	1.5258789e-05f;
    
    public static final int SR_MASK_0 = 0; // 0x0
    public static final int SR_MASK_1 = (0x01 << 1) - 1; // 0x1
    public static final int SR_MASK_2 = (0x01 << 2) - 1; // 0x3
    public static final int SR_MASK_3 = (0x01 << 3) - 1; // 0x7
    public static final int SR_MASK_4 = (0x01 << 4) - 1; // 0xf
    public static final int SR_MASK_5 = (0x01 << 5) - 1; // 0x1f
    public static final int SR_MASK_6 = (0x01 << 6) - 1; // 0x3f
    public static final int SR_MASK_7 = (0x01 << 7) - 1; // 0x7f
    public static final int SR_MASK_8 = (0x01 << 8) - 1; // 0xff
    public static final int SR_MASK_9 = (0x01 << 9) - 1; // 0x1ff
    public static final int SR_MASK_10 = (0x01 << 10) - 1; // ox3ff
    public static final int SR_MASK_11 = (0x01 << 11) - 1; // 0x7ff
    public static final int SR_MASK_12 = (0x01 << 12) - 1; // 0xfff
    public static final int SR_MASK_13 = (0x01 << 13) - 1; // 0x1fff
    public static final int SR_MASK_14 = (0x01 << 14) - 1; // 0x3fff
    public static final int SR_MASK_15 = (0x01 << 15) - 1; // 0x7fff
    public static final int SR_MASK_16 = (0x01 << 16) - 1; // 0xffff
    public static final int SR_MASK_17 = (0x01 << 17) - 1; // 0x1ffff
    public static final int SR_MASK_18 = (0x01 << 18) - 1; // 0x3ffff
    public static final int SR_MASK_19 = (0x01 << 19) - 1; // 0x7ffff
    public static final int SR_MASK_20 = (0x01 << 20) - 1; // 0xfffff
    public static final int SR_MASK_21 = (0x01 << 21) - 1; // 0x1fffff
    public static final int SR_MASK_22 = (0x01 << 22) - 1; // 0x3fffff
    public static final int SR_MASK_23 = (0x01 << 23) - 1; // 0x7fffff
    public static final int SR_MASK_24 = (0x01 << 24) - 1; // 0xffffff
    public static final int SR_MASK_25 = (0x01 << 25) - 1; // 0x1ffffff
    public static final int SR_MASK_26 = (0x01 << 26) - 1; // 0x3ffffff
    public static final int SR_MASK_27 = (0x01 << 27) - 1; // 0x7ffffff
    public static final int SR_MASK_28 = (0x01 << 28) - 1; // 0xfffffff
    public static final int SR_MASK_29 = (0x01 << 29) - 1; // 0x1fffffff
    public static final int SR_MASK_30 = (0x01 << 30) - 1; // 0x3fffffff
    public static final int SR_MASK_31 = (0x01 << 31) - 1; // 0x7fffffff
    
    // BG flags
    public static final int	G_BGLT_LOADBLOCK=	0x0033;
    public static final int	G_BGLT_LOADTILE=		0xfff4;
    
    public static final int	G_BG_FLAG_FLIPS=		0x01;
    public static final int	G_BG_FLAG_FLIPT=		0x10;
    
    // Sprite object render modes
    public static final int	G_OBJRM_NOTXCLAMP=		0x01;
    public static final int	G_OBJRM_XLU=				0x02;	/* Ignored */
    public static final int	G_OBJRM_ANTIALIAS=		0x04;	/* Ignored */
    public static final int	G_OBJRM_BILERP=			0x08;
    public static final int	G_OBJRM_SHRINKSIZE_1=	0x10;
    public static final int	G_OBJRM_SHRINKSIZE_2=	0x20;
    public static final int	G_OBJRM_WIDEN=			0x40;
    
    // Sprite texture loading types
    public static final int	G_OBJLT_TXTRBLOCK=	0x00001033;
    public static final int	G_OBJLT_TXTRTILE=	0x00fc1034;
    public static final int	G_OBJLT_TLUT=		0x00000030;
    
    // These are all the constant flags
    public static final int G_ZBUFFER=				0x00000001;
    public static final int G_SHADE=					0x00000004;
    public static final int G_FOG=					0x00010000;
    public static final int G_LIGHTING=				0x00020000;
    public static final int G_TEXTURE_GEN=			0x00040000;
    public static final int G_TEXTURE_GEN_LINEAR=	0x00080000;
    public static final int G_LOD=					0x00100000;
    
    public static final int G_MV_MMTX=		2;
    public static final int G_MV_PMTX=		6;
    public static final int G_MV_LIGHT=		10;
    public static final int G_MV_POINT=		12;
    public static final int G_MV_MATRIX=		14;
    
    public static final int G_MVO_LOOKATX=	0;
    public static final int G_MVO_LOOKATY=	24;
    public static final int G_MVO_L0=		48;
    public static final int G_MVO_L1=		72;
    public static final int G_MVO_L2=		96;
    public static final int G_MVO_L3=		120;
    public static final int G_MVO_L4=		144;
    public static final int G_MVO_L5=		168;
    public static final int G_MVO_L6=		192;
    public static final int G_MVO_L7=		216;
    
    public static final int G_MV_LOOKATY=	0x82;
    public static final int G_MV_LOOKATX=	0x84;
    public static final int G_MV_L0=                        0x86;
    public static final int G_MV_L1=			0x88;
    public static final int G_MV_L2=			0x8a;
    public static final int G_MV_L3=			0x8c;
    public static final int G_MV_L4=			0x8e;
    public static final int G_MV_L5=			0x90;
    public static final int G_MV_L6=			0x92;
    public static final int G_MV_L7=			0x94;
    public static final int G_MV_TXTATT=		0x96;
    public static final int G_MV_MATRIX_1=	0x9E;
    public static final int G_MV_MATRIX_2=	0x98;
    public static final int G_MV_MATRIX_3=	0x9A;
    public static final int G_MV_MATRIX_4=	0x9C;
    
    public static final int G_MW_MATRIX=			0x00;
    public static final int G_MW_NUMLIGHT=			0x02;
    public static final int G_MW_CLIP=				0x04;
    public static final int G_MW_SEGMENT=			0x06;
    public static final int G_MW_FOG=				0x08;
    public static final int G_MW_LIGHTCOL=			0x0A;
    public static final int G_MW_FORCEMTX=			0x0C;
    public static final int G_MW_POINTS=			0x0C;
    public static final int	G_MW_PERSPNORM=			0x0E;
    public static final int G_MW_COORD_MOD=			0x10;
    
    public static final int G_MWO_NUMLIGHT=		0x00;
    public static final int G_MWO_CLIP_RNX=		0x04;
    public static final int G_MWO_CLIP_RNY=		0x0c;
    public static final int G_MWO_CLIP_RPX=		0x14;
    public static final int G_MWO_CLIP_RPY=		0x1c;
    public static final int G_MWO_SEGMENT_0=		0x00;
    public static final int G_MWO_SEGMENT_1=		0x01;
    public static final int G_MWO_SEGMENT_2=		0x02;
    public static final int G_MWO_SEGMENT_3=		0x03;
    public static final int G_MWO_SEGMENT_4=		0x04;
    public static final int G_MWO_SEGMENT_5=		0x05;
    public static final int G_MWO_SEGMENT_6=		0x06;
    public static final int G_MWO_SEGMENT_7=		0x07;
    public static final int G_MWO_SEGMENT_8=		0x08;
    public static final int G_MWO_SEGMENT_9=		0x09;
    public static final int G_MWO_SEGMENT_A=		0x0a;
    public static final int G_MWO_SEGMENT_B=		0x0b;
    public static final int G_MWO_SEGMENT_C=		0x0c;
    public static final int G_MWO_SEGMENT_D=		0x0d;
    public static final int G_MWO_SEGMENT_E=		0x0e;
    public static final int G_MWO_SEGMENT_F=		0x0f;
    public static final int G_MWO_FOG=			0x00;
    
    public static final int G_MWO_MATRIX_XX_XY_I=	0x00;
    public static final int G_MWO_MATRIX_XZ_XW_I=	0x04;
    public static final int G_MWO_MATRIX_YX_YY_I=	0x08;
    public static final int G_MWO_MATRIX_YZ_YW_I=	0x0C;
    public static final int G_MWO_MATRIX_ZX_ZY_I=	0x10;
    public static final int G_MWO_MATRIX_ZZ_ZW_I=	0x14;
    public static final int G_MWO_MATRIX_WX_WY_I=	0x18;
    public static final int G_MWO_MATRIX_WZ_WW_I=	0x1C;
    public static final int G_MWO_MATRIX_XX_XY_F=	0x20;
    public static final int G_MWO_MATRIX_XZ_XW_F=	0x24;
    public static final int G_MWO_MATRIX_YX_YY_F=	0x28;
    public static final int G_MWO_MATRIX_YZ_YW_F=	0x2C;
    public static final int G_MWO_MATRIX_ZX_ZY_F=	0x30;
    public static final int G_MWO_MATRIX_ZZ_ZW_F=	0x34;
    public static final int G_MWO_MATRIX_WX_WY_F=	0x38;
    public static final int G_MWO_MATRIX_WZ_WW_F=	0x3C;
    public static final int G_MWO_POINT_RGBA=		0x10;
    public static final int G_MWO_POINT_ST=			0x14;
    public static final int G_MWO_POINT_XYSCREEN=	0x18;
    public static final int G_MWO_POINT_ZSCREEN=		0x1C;
    
    public static final String[] MWOPointText = {
        "G_MWO_POINT_RGBA",
        "G_MWO_POINT_ST",
        "G_MWO_POINT_XYSCREEN",
        "G_MWO_POINT_ZSCREEN"
    };
    
    public static final String [] MWOMatrixText = {
        "G_MWO_MATRIX_XX_XY_I",	"G_MWO_MATRIX_XZ_XW_I",	"G_MWO_MATRIX_YX_YY_I",	"G_MWO_MATRIX_YZ_YW_I",
        "G_MWO_MATRIX_ZX_ZY_I",	"G_MWO_MATRIX_ZZ_ZW_I",	"G_MWO_MATRIX_WX_WY_I",	"G_MWO_MATRIX_WZ_WW_I",
        "G_MWO_MATRIX_XX_XY_F",	"G_MWO_MATRIX_XZ_XW_F",	"G_MWO_MATRIX_YX_YY_F",	"G_MWO_MATRIX_YZ_YW_F",
        "G_MWO_MATRIX_ZX_ZY_F",	"G_MWO_MATRIX_ZZ_ZW_F",	"G_MWO_MATRIX_WX_WY_F",	"G_MWO_MATRIX_WZ_WW_F"
    };
    
//    // Image formats
//    public static final int G_IM_FMT_RGBA=	0;
//    public static final int G_IM_FMT_YUV=	1;
//    public static final int G_IM_FMT_CI=		2;
//    public static final int G_IM_FMT_IA=		3;
//    public static final int G_IM_FMT_I=		4;
//    
//    // Image sizes
//    public static final int G_IM_SIZ_4b=		0;
//    public static final int G_IM_SIZ_8b=		1;
//    public static final int G_IM_SIZ_16b=	2;
//    public static final int G_IM_SIZ_32b=	3;
//    public static final int G_IM_SIZ_DD=		5;
    
    public static final int G_TX_MIRROR=		0x1;
    public static final int G_TX_CLAMP=		0x2;
    
    public static final String[] ImageFormatText = {
        "G_IM_FMT_RGBA",
        "G_IM_FMT_YUV",
        "G_IM_FMT_CI",
        "G_IM_FMT_IA",
        "G_IM_FMT_I",
        "G_IM_FMT_INVALID",
        "G_IM_FMT_INVALID",
        "G_IM_FMT_INVALID"
    };
    
    public static final String [] ImageSizeText = {
        "G_IM_SIZ_4b",
        "G_IM_SIZ_8b",
        "G_IM_SIZ_16b",
        "G_IM_SIZ_32b"
    };
    
    public static final String[] SegmentText = {
        "G_MWO_SEGMENT_0", "G_MWO_SEGMENT_1", "G_MWO_SEGMENT_2", "G_MWO_SEGMENT_3",
        "G_MWO_SEGMENT_4", "G_MWO_SEGMENT_5", "G_MWO_SEGMENT_6", "G_MWO_SEGMENT_7",
        "G_MWO_SEGMENT_8", "G_MWO_SEGMENT_9", "G_MWO_SEGMENT_A", "G_MWO_SEGMENT_B",
        "G_MWO_SEGMENT_C", "G_MWO_SEGMENT_D", "G_MWO_SEGMENT_E", "G_MWO_SEGMENT_F"
    };
    
    public static final int G_NOOP=					0x00;
    
    public static final int G_IMMFIRST=				-65;
    
    // These gbi commands are common to all ucodes
    public static final int	G_SETCIMG=				0xFF;	/*  -1 */
    public static final int	G_SETZIMG=				0xFE;	/*  -2 */
    public static final int	G_SETTIMG=				0xFD;	/*  -3 */
    public static final int	G_SETCOMBINE=			0xFC;	/*  -4 */
    public static final int	G_SETENVCOLOR=			0xFB;	/*  -5 */
    public static final int	G_SETPRIMCOLOR=			0xFA;	/*  -6 */
    public static final int	G_SETBLENDCOLOR=			0xF9;	/*  -7 */
    public static final int	G_SETFOGCOLOR=			0xF8;	/*  -8 */
    public static final int	G_SETFILLCOLOR=			0xF7;	/*  -9 */
    public static final int	G_FILLRECT=				0xF6;	/* -10 */
    public static final int	G_SETTILE=				0xF5;	/* -11 */
    public static final int	G_LOADTILE=				0xF4;	/* -12 */
    public static final int	G_LOADBLOCK=				0xF3;	/* -13 */
    public static final int	G_SETTILESIZE=			0xF2;	/* -14 */
    public static final int	G_LOADTLUT=				0xF0;	/* -16 */
    public static final int	G_RDPSETOTHERMODE=		0xEF;	/* -17 */
    public static final int	G_SETPRIMDEPTH=			0xEE;	/* -18 */
    public static final int	G_SETSCISSOR=			0xED;	/* -19 */
    public static final int	G_SETCONVERT=			0xEC;	/* -20 */
    public static final int	G_SETKEYR=				0xEB;	/* -21 */
    public static final int	G_SETKEYGB=				0xEA;	/* -22 */
    public static final int	G_RDPFULLSYNC=			0xE9;	/* -23 */
    public static final int	G_RDPTILESYNC=			0xE8;	/* -24 */
    public static final int	G_RDPPIPESYNC=			0xE7;	/* -25 */
    public static final int	G_RDPLOADSYNC=			0xE6;	/* -26 */
    public static final int G_TEXRECTFLIP=			0xE5;	/* -27 */
    public static final int G_TEXRECT=				0xE4;	/* -28 */
    
    public static final int G_TRI_FILL=				0xC8;	/* fill triangle:            11001000 */
    public static final int G_TRI_SHADE=				0xCC;	/* shade triangle:           11001100 */
    public static final int G_TRI_TXTR=				0xCA;	/* texture triangle:         11001010 */
    public static final int G_TRI_SHADE_TXTR=		0xCE;	/* shade, texture triangle:  11001110 */
    public static final int G_TRI_FILL_ZBUFF=		0xC9;	/* fill, zbuff triangle:     11001001 */
    public static final int G_TRI_SHADE_ZBUFF=		0xCD;	/* shade, zbuff triangle:    11001101 */
    public static final int G_TRI_TXTR_ZBUFF=		0xCB;	/* texture, zbuff triangle:  11001011 */
    public static final int G_TRI_SHADE_TXTR_ZBUFF=	0xCF;	/* shade, txtr, zbuff trngl: 11001111 */
    
    /*
     * G_SETOTHERMODE_L sft: shift count
     */
    public static final int	G_MDSFT_ALPHACOMPARE=	0;
    public static final int	G_MDSFT_ZSRCSEL=			2;
    public static final int	G_MDSFT_RENDERMODE=		3;
    public static final int	G_MDSFT_BLENDER=			16;
    
    /*
     * G_SETOTHERMODE_H sft: shift count
     */
    public static final int	G_MDSFT_BLENDMASK=		0;	/* unsupported */
    public static final int	G_MDSFT_ALPHADITHER=		4;
    public static final int	G_MDSFT_RGBDITHER=		6;
    
    public static final int	G_MDSFT_COMBKEY=			8;
    public static final int	G_MDSFT_TEXTCONV=		9;
    public static final int	G_MDSFT_TEXTFILT=		12;
    public static final int	G_MDSFT_TEXTLUT=			14;
    public static final int	G_MDSFT_TEXTLOD=			16;
    public static final int	G_MDSFT_TEXTDETAIL=		17;
    public static final int	G_MDSFT_TEXTPERSP=		19;
    public static final int	G_MDSFT_CYCLETYPE=		20;
    public static final int	G_MDSFT_COLORDITHER=		22;	/* unsupported in HW 2.0 */
    public static final int	G_MDSFT_PIPELINE=		23;
    
    /* G_SETOTHERMODE_H gPipelineMode */
    public static final int	G_PM_1PRIMITIVE=			1;
    public static final int	G_PM_NPRIMITIVE=			0;
    
    /* G_SETOTHERMODE_H gSetCycleType */
    public static final int	G_CYC_1CYCLE=			0;
    public static final int	G_CYC_2CYCLE=			1;
    public static final int	G_CYC_COPY=				2;
    public static final int	G_CYC_FILL=				3;
    
    /* G_SETOTHERMODE_H gSetTexturePersp */
    public static final int G_TP_NONE=				0;
    public static final int G_TP_PERSP=				1;
    
    /* G_SETOTHERMODE_H gSetTextureDetail */
    public static final int G_TD_CLAMP=				0;
    public static final int G_TD_SHARPEN=			1;
    public static final int G_TD_DETAIL=				2;
    
    /* G_SETOTHERMODE_H gSetTextureLOD */
    public static final int G_TL_TILE=				0;
    public static final int G_TL_LOD=				1;
    
    /* G_SETOTHERMODE_H gSetTextureLUT */
    public static final int G_TT_NONE=				0;
    public static final int G_TT_RGBA16=				2;
    public static final int G_TT_IA16=				3;
    
    /* G_SETOTHERMODE_H gSetTextureFilter */
    public static final int G_TF_POINT=			0;
    public static final int G_TF_AVERAGE=			3;
    public static final int G_TF_BILERP=			2;
    
    /* G_SETOTHERMODE_H gSetTextureConvert */
    public static final int G_TC_CONV=				0;
    public static final int G_TC_FILTCONV=			5;
    public static final int G_TC_FILT=				6;
    
    /* G_SETOTHERMODE_H gSetCombineKey */
    public static final int G_CK_NONE=				0;
    public static final int G_CK_KEY=				1;
    
    /* G_SETOTHERMODE_H gSetColorDither */
    public static final int	G_CD_MAGICSQ=			0;
    public static final int	G_CD_BAYER=				1;
    public static final int	G_CD_NOISE=				2;
    
    public static final int	G_CD_DISABLE=			3;
    public static final int	G_CD_ENABLE=				G_CD_NOISE;	/* HW 1.0 compatibility mode */
    
    /* G_SETOTHERMODE_H gSetAlphaDither */
    public static final int	G_AD_PATTERN=			0;
    public static final int	G_AD_NOTPATTERN=			1;
    public static final int	G_AD_NOISE=				2;
    public static final int	G_AD_DISABLE=			3;
    
    /* G_SETOTHERMODE_L gSetAlphaCompare */
    public static final int	G_AC_NONE=				0;
    public static final int	G_AC_THRESHOLD=			1;
    public static final int	G_AC_DITHER=				3;
    
    /* G_SETOTHERMODE_L gSetDepthSource */
    public static final int	G_ZS_PIXEL=				0;
    public static final int	G_ZS_PRIM=				1;
    
    /* G_SETOTHERMODE_L gSetRenderMode */
    public static final int	AA_EN=					1;
    public static final int	Z_CMP=					1;
    public static final int	Z_UPD=					1;
    public static final int	IM_RD=					1;
    public static final int	CLR_ON_CVG=				1;
    public static final int	CVG_DST_CLAMP=			0;
    public static final int	CVG_DST_WRAP=			1;
    public static final int	CVG_DST_FULL=			2;
    public static final int	CVG_DST_SAVE=			3;
    public static final int	ZMODE_OPA=				0;
    public static final int	ZMODE_INTER=				1;
    public static final int	ZMODE_XLU=				2;
    public static final int	ZMODE_DEC=				3;
    public static final int	CVG_X_ALPHA=				1;
    public static final int	ALPHA_CVG_SEL=			1;
    public static final int	FORCE_BL=				1;
    public static final int	TEX_EDGE=				0; // not used
    
    public static final int	G_SC_NON_INTERLACE=		0;
    public static final int	G_SC_EVEN_INTERLACE=		2;
    public static final int	G_SC_ODD_INTERLACE=		3;
    
    public static final String AAEnableText = "AA_EN";
    public static final String DepthCompareText = "Z_CMP";
    public static final String DepthUpdateText = "Z_UPD";
    public static final String ClearOnCvgText = "CLR_ON_CVG";
    public static final String CvgXAlphaText = "CVG_X_ALPHA";
    public static final String AlphaCvgSelText = "ALPHA_CVG_SEL";
    public static final String ForceBlenderText = "FORCE_BL";
    
    public static final String[] AlphaCompareText = {
        "G_AC_NONE", "G_AC_THRESHOLD", "G_AC_INVALID", "G_AC_DITHER"
    };
    
    public static final String[] DepthSourceText = {
        "G_ZS_PIXEL", "G_ZS_PRIM"
    };
    
    public static final String[] AlphaDitherText = {
        "G_AD_PATTERN", "G_AD_NOTPATTERN", "G_AD_NOISE", "G_AD_DISABLE"
    };
    
    public static final String[] ColorDitherText = {
        "G_CD_MAGICSQ", "G_CD_BAYER", "G_CD_NOISE", "G_CD_DISABLE"
    };
    
    public static final String[] CombineKeyText = {
        "G_CK_NONE", "G_CK_KEY"
    };
    
    public static final String[] TextureConvertText = {
        "G_TC_CONV", "G_TC_INVALID", "G_TC_INVALID", "G_TC_INVALID", "G_TC_INVALID", "G_TC_FILTCONV", "G_TC_FILT", "G_TC_INVALID"
    };
    
    public static final String[] TextureFilterText = {
        "G_TF_POINT", "G_TF_INVALID", "G_TF_BILERP", "G_TF_AVERAGE"
    };
    
    public static final String[] TextureLUTText = {
        "G_TT_NONE", "G_TT_INVALID", "G_TT_RGBA16", "G_TT_IA16"
    };
    
    public static final String[] TextureLODText = {
        "G_TL_TILE", "G_TL_LOD"
    };
    
    public static final String[] TextureDetailText = {
        "G_TD_CLAMP", "G_TD_SHARPEN", "G_TD_DETAIL"
    };
    
    public static final String[] TexturePerspText = {
        "G_TP_NONE", "G_TP_PERSP"
    };
    
    public static final String[] CycleTypeText = {
        "G_CYC_1CYCLE", "G_CYC_2CYCLE", "G_CYC_COPY", "G_CYC_FILL"
    };
    
    public static final String[] PipelineModeText = {
        "G_PM_NPRIMITIVE", "G_PM_1PRIMITIVE"
    };
    
    public static final String[] CvgDestText = {
        "CVG_DST_CLAMP", "CVG_DST_WRAP", "CVG_DST_FULL", "CVG_DST_SAVE"
    };
    
    public static final String[] DepthModeText = {
        "ZMODE_OPA", "ZMODE_INTER", "ZMODE_XLU", "ZMODE_DEC"
    };
    
    public static final String[] ScissorModeText = {
        "G_SC_NON_INTERLACE", "G_SC_INVALID", "G_SC_EVEN_INTERLACE", "G_SC_ODD_INTERLACE"
    };
    
    /* Color combiner constants: */
    public static final int G_CCMUX_COMBINED=		0;
    public static final int G_CCMUX_TEXEL0=			1;
    public static final int G_CCMUX_TEXEL1=			2;
    public static final int G_CCMUX_PRIMITIVE=		3;
    public static final int G_CCMUX_SHADE=			4;
    public static final int G_CCMUX_ENVIRONMENT=		5;
    public static final int G_CCMUX_CENTER=			6;
    public static final int G_CCMUX_SCALE=			6;
    public static final int G_CCMUX_COMBINED_ALPHA=	7;
    public static final int G_CCMUX_TEXEL0_ALPHA=	8;
    public static final int G_CCMUX_TEXEL1_ALPHA=	9;
    public static final int G_CCMUX_PRIMITIVE_ALPHA=	10;
    public static final int G_CCMUX_SHADE_ALPHA=		11;
    public static final int G_CCMUX_ENV_ALPHA=		12;
    public static final int G_CCMUX_LOD_FRACTION=	13;
    public static final int G_CCMUX_PRIM_LOD_FRAC=	14;
    public static final int G_CCMUX_NOISE=			7;
    public static final int G_CCMUX_K4=				7;
    public static final int G_CCMUX_K5=				15;
    public static final int G_CCMUX_1=				6;
    public static final int G_CCMUX_0=				31;
    
    /* Alpha combiner constants: */
    public static final int G_ACMUX_COMBINED=		0;
    public static final int G_ACMUX_TEXEL0=			1;
    public static final int G_ACMUX_TEXEL1=			2;
    public static final int G_ACMUX_PRIMITIVE=		3;
    public static final int G_ACMUX_SHADE=			4;
    public static final int G_ACMUX_ENVIRONMENT=		5;
    public static final int G_ACMUX_LOD_FRACTION=	0;
    public static final int G_ACMUX_PRIM_LOD_FRAC=	6;
    public static final int G_ACMUX_1=				6;
    public static final int G_ACMUX_0=				7;
    
    public static final String[] saRGBText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"NOISE",			"1",
        "0",				"0",				"0",				"0",
        "0",				"0",				"0",				"0"
    };
    
    public static final String[] sbRGBText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"CENTER",			"K4",
        "0",				"0",				"0",				"0",
        "0",				"0",				"0",				"0"
    };
    
    public static final String[] mRGBText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"SCALE",			"COMBINED_ALPHA",
        "TEXEL0_ALPHA",		"TEXEL1_ALPHA",		"PRIMITIVE_ALPHA",	"SHADE_ALPHA",
        "ENV_ALPHA",		"LOD_FRACTION",		"PRIM_LOD_FRAC",	"K5",
        "0",				"0",				"0",				"0",
        "0",				"0",				"0",				"0",
        "0",				"0",				"0",				"0",
        "0",				"0",				"0",				"0"
    };
    
    public static final String[] aRGBText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"1",				"0",
    };
    
    public static final String[] saAText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"1",				"0",
    };
    
    public static final String[] sbAText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"1",				"0",
    };
    
    public static final String[] mAText = {
        "LOD_FRACTION",		"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"PRIM_LOD_FRAC",	"0",
    };
    
    public static final String[] aAText = {
        "COMBINED",			"TEXEL0",			"TEXEL1",			"PRIMITIVE",
        "SHADE",			"ENVIRONMENT",		"1",				"0",
    };
    
    public static final int LIGHT_1=	1;
    public static final int LIGHT_2=	2;
    public static final int LIGHT_3=	3;
    public static final int LIGHT_4=	4;
    public static final int LIGHT_5=	5;
    public static final int LIGHT_6=	6;
    public static final int LIGHT_7=	7;
    public static final int LIGHT_8=	8;
    
    public static final int G_DL_PUSH=		0x00;
    public static final int G_DL_NOPUSH=		0x01;

    public static int G_RDPHALF_1, G_RDPHALF_2, G_RDPHALF_CONT;
    public static int G_SPNOOP;
    public static int G_SETOTHERMODE_H, G_SETOTHERMODE_L;
    public static int G_DL, G_ENDDL, G_CULLDL, G_BRANCH_Z;
    public static int G_LOAD_UCODE;
    public static int G_MOVEMEM, G_MOVEWORD;
    public static int G_MTX, G_POPMTX;
    public static int G_GEOMETRYMODE, G_SETGEOMETRYMODE, G_CLEARGEOMETRYMODE;
    public static int G_TEXTURE;
    public static int G_DMA_IO, G_DMA_DL, G_DMA_TRI, G_DMA_MTX, G_DMA_VTX, G_DMA_OFFSETS;
    public static int G_SPECIAL_1, G_SPECIAL_2, G_SPECIAL_3;
    public static int G_VTX, G_MODIFYVTX, G_VTXCOLORBASE;
    public static int G_TRI1, G_TRI2, G_TRI4;
    public static int G_QUAD, G_LINE3D;
    public static int G_RESERVED0, G_RESERVED1, G_RESERVED2, G_RESERVED3;
    public static int G_SPRITE2D_BASE;
    public static int G_BG_1CYC, G_BG_COPY;
    public static int G_OBJ_RECTANGLE, G_OBJ_SPRITE, G_OBJ_MOVEMEM;
    public static int G_SELECT_DL, G_OBJ_RENDERMODE, G_OBJ_RECTANGLE_R;
    public static int G_OBJ_LOADTXTR, G_OBJ_LDTX_SPRITE, G_OBJ_LDTX_RECT, G_OBJ_LDTX_RECT_R;
    public static int G_RDPHALF_0;
    
    public static int G_MTX_STACKSIZE;
    public static int G_MTX_MODELVIEW;
    public static int G_MTX_PROJECTION;
    public static int G_MTX_MUL;
    public static int G_MTX_LOAD;
    public static int G_MTX_NOPUSH;
    public static int G_MTX_PUSH;
    
    public static int G_TEXTURE_ENABLE;
    public static int G_SHADING_SMOOTH;
    public static int G_CULL_FRONT;
    public static int G_CULL_BACK;
    public static int G_CULL_BOTH;
    public static int G_CLIPPING;
    
    public static int G_MV_VIEWPORT;
    
    public static int G_MWO_aLIGHT_1, G_MWO_bLIGHT_1;
    public static int G_MWO_aLIGHT_2, G_MWO_bLIGHT_2;
    public static int G_MWO_aLIGHT_3, G_MWO_bLIGHT_3;
    public static int G_MWO_aLIGHT_4, G_MWO_bLIGHT_4;
    public static int G_MWO_aLIGHT_5, G_MWO_bLIGHT_5;
    public static int G_MWO_aLIGHT_6, G_MWO_bLIGHT_6;
    public static int G_MWO_aLIGHT_7, G_MWO_bLIGHT_7;
    public static int G_MWO_aLIGHT_8, G_MWO_bLIGHT_8;
    
    // used by Gsp, GLN64jPlugin
//    public static Microcodes gbi = new Microcodes();
    
    /**
     * Creates a new instance of gbi
     */
    private Gbi() {
    }
    
//    // used by Opcodes
//    // Allows easier setting of gbi commands
//    public static int GBI_SetGBI(int value, GBIFunc function) {
//        gbi.cmd[value] = function;
//        return value;
//    }
    
}
