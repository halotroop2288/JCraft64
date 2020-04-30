package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.emu.util.debug.Debug.DEBUG_UNKNOWN;
import static me.hydos.J64.emu.util.debug.Debug.DebugMsg;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class Rdpfuncs {
    
    protected static Gsp gsp;
    protected static Gdp gdp;
    
    public static GBIFunc RDP_Unknown = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            if (DEBUG_MICROCODE) {
                DebugMsg(DEBUG_UNKNOWN, "RDP_Unknown\r\n");
                DebugMsg(DEBUG_UNKNOWN, "\tUnknown RDP opcode %02X\r\n", (w0>>24)&SR_MASK_8);
            }
        }
    };
    
    public static GBIFunc RDP_NoOp = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPNoOp();
        }
    };
    
    public static GBIFunc RDP_SetCImg = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetColorImage((w0>>21)&SR_MASK_3,	// fmt
                    (w0>>19)&SR_MASK_2,					// size
                    (w0&SR_MASK_12) + 1,				// width
                    gsp.RSP_SegmentToPhysical(w1));		// img
        }
    };
    
    public static GBIFunc RDP_SetZImg = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetDepthImage(gsp.RSP_SegmentToPhysical(w1));	// img
        }
    };

    public static GBIFunc RDP_SetTImg = (w0, w1) -> {
    	gdp.gDPSetTextureImage(( w0>>21)&SR_MASK_3,	// fmt
    						( w0>>19)&SR_MASK_2,		// siz
    						( w0>>0)&SR_MASK_12 + 1,	// width
    						w1 );						// img
    };
    
    public static GBIFunc RDP_SetCombine = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetCombine(w0&SR_MASK_24,	// muxs0
                    w1);		// muxs1
        }
    };
    
    public static GBIFunc RDP_SetEnvColor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetEnvColor((w1>>24)&SR_MASK_8,	// r
                    (w1>>16)&SR_MASK_8,	// g
                    (w1>>8)&SR_MASK_8,	// b
                    w1&SR_MASK_8);         // a
        }
    };
    
    public static GBIFunc RDP_SetPrimColor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetPrimColor(
                    //_SHIFTL(w0,  8, 8),	// m
                    (w0&SR_MASK_8)<<8,
                    //_SHIFTL(w0,  0, 8),	// l
                    w0&SR_MASK_8,
                    (w1>>24)&SR_MASK_8,	// r
                    (w1>>16)&SR_MASK_8,	// g
                    (w1>>8)&SR_MASK_8,	// b
                    w1&SR_MASK_8);	// a
        }
    };
    
    public static GBIFunc RDP_SetBlendColor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetBlendColor((w1>>24)&SR_MASK_8,	// r
                    (w1>>16)&SR_MASK_8,	// g
                    (w1>>8)&SR_MASK_8,	// b
                    w1&SR_MASK_8);	// a
        }
    };
    
    public static GBIFunc RDP_SetFogColor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetFogColor((w1>>24)&SR_MASK_8,	// r
                    (w1>>16)&SR_MASK_8,	// g
                    (w1>>8)&SR_MASK_8,	// b
                    w1&SR_MASK_8);         // a
        }
    };
    
    public static GBIFunc RDP_SetFillColor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetFillColor(w1);
        }
    };
    
    public static GBIFunc RDP_FillRect = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPFillRectangle((w1>>14)&SR_MASK_10,         // ulx
                    (w1>>2)&SR_MASK_10,          // uly
                    (w0>>14)&SR_MASK_10,         // lrx
                    (w0>>2)&SR_MASK_10);        // lry
        }
    };
    
    public static GBIFunc RDP_SetTile = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetTile((w0>>21)&SR_MASK_3,         // fmt
                    (w0>>19)&SR_MASK_2,         // siz
                    (w0>>9)&SR_MASK_9,          // line
                    w0&SR_MASK_9,               // tmem
                    (w1>>24)&SR_MASK_3,         // tile
                    (w1>>20)&SR_MASK_4,         // palette
                    (w1>>18)&SR_MASK_2,         // cmt
                    (w1>>8)&SR_MASK_2,          // cms
                    (w1>>14)&SR_MASK_4,         // maskt
                    (w1>>4)&SR_MASK_4,          // masks
                    (w1>>10)&SR_MASK_4,         // shiftt
                    w1&SR_MASK_4);             // shifts
        }
    };
    
    public static GBIFunc RDP_LoadTile = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPLoadTile((w1>>24)&SR_MASK_3,	// tile
                    (w0>>12)&SR_MASK_12,	// uls
                    w0&SR_MASK_12,		// ult
                    (w1>>12)&SR_MASK_12,	// lrs
                    w1&SR_MASK_12);           // lrt
        }
    };
    
    public static GBIFunc RDP_LoadBlock = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPLoadBlock((w1>>24)&SR_MASK_3,	// tile
                    (w0>>12)&SR_MASK_12,	// uls
                    w0&SR_MASK_12,            // ult
                    (w1>>12)&SR_MASK_12,	// lrs
                    w1&SR_MASK_12);          // dxt
        }
    };
    
    public static GBIFunc RDP_SetTileSize = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetTileSize((w1>>24)&SR_MASK_3,	// tile
                    (w0>>12)&SR_MASK_12,	// uls
                    w0&SR_MASK_12,          // ult
                    (w1>>12)&SR_MASK_12,	// lrs
                    w1&SR_MASK_12);        // lrt
        }
    };
    
    public static GBIFunc RDP_LoadTLUT = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPLoadTLUT((w1>>24)&SR_MASK_3,        // tile
                    (w0>>12)&SR_MASK_12,       // uls
                    w0&SR_MASK_12,             // ult
                    (w1>>12)&SR_MASK_12,       // lrs
                    w1&SR_MASK_12);           // lrt
        }
    };
    
    public static GBIFunc RDP_SetOtherMode = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetOtherMode(w0&SR_MASK_24,         // mode0
                    w1);			// mode1
        }
    };
    
    public static GBIFunc RDP_SetPrimDepth = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetPrimDepth((w1>>16)&SR_MASK_16,	// z
                    w1&SR_MASK_16);	// dz
        }
    };
    
    public static GBIFunc RDP_SetScissor = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetScissor((w1>>24)&SR_MASK_2,			// mode
                    ((w0>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,	// ulx
                    (w0&SR_MASK_12) * FIXED2FLOATRECIP2,            // uly
                    ((w1>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,	// lrx
                    (w1&SR_MASK_12) * FIXED2FLOATRECIP2);          // lry
        }
    };
    
    public static GBIFunc RDP_SetConvert = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetConvert((w0>>13)&SR_MASK_9,                              // k0
                    (w0>>4)&SR_MASK_9,                               // k1
                    ((w0&SR_MASK_4)<<5) | ((w1>>25)&SR_MASK_5),	// k2
                    (w1>>18)&SR_MASK_9,                              // k3
                    (w1>>9)&SR_MASK_9,                               // k4
                    w1&SR_MASK_9);                                  // k5
        }
    };
    
    public static GBIFunc RDP_SetKeyR = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetKeyR((w1>>8)&SR_MASK_8,		// cR
                    w1&SR_MASK_8,		// sR
                    (w1>>16)&SR_MASK_12);	// wR
        }
    };
    
    public static GBIFunc RDP_SetKeyGB = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPSetKeyGB((w1>>24)&SR_MASK_8,		// cG
                    (w1>>16)&SR_MASK_8,		// sG
                    (w0>>12)&SR_MASK_12,		// wG
                    (w1>>8)&SR_MASK_8,                 // cB
                    w1&SR_MASK_8,                      // SB
                    w0&SR_MASK_12);                   // wB
        }
    };
    
    public static GBIFunc RDP_FullSync = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPFullSync();
        }
    };
    
    public static GBIFunc RDP_TileSync = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPTileSync();
        }
    };
    
    public static GBIFunc RDP_PipeSync = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPPipeSync();
        }
    };
    
    public static GBIFunc RDP_LoadSync = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
            gdp.gDPLoadSync();
        }
    };
    
    public static GBIFunc RDP_TexRectFlip = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
//            int w2 = N64.RDRAM.getInt(PC[PCi] + 4);
//            PC[PCi] += 8;
            int w2 = gsp.getCmd();
            
//            int w3 = N64.RDRAM.getInt(PC[PCi] + 4);
//            PC[PCi] += 8;
            int w3 = gsp.getCmd();
            
            gdp.gDPTextureRectangleFlip(((w1>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,                // ulx
                    (w1&SR_MASK_12) * FIXED2FLOATRECIP2,                      // uly
                    ((w0>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,                // lrx
                    (w0&SR_MASK_12) * FIXED2FLOATRECIP2,                      // lry
                    (w1>>24)&SR_MASK_3,                                    // tile
                    ((short)((w2>>16)&SR_MASK_16)) * FIXED2FLOATRECIP5,       // s
                    ((short)(w2&SR_MASK_16)) * FIXED2FLOATRECIP5,             // t
                    ((short)((w3>>16)&SR_MASK_16)) * FIXED2FLOATRECIP10,      // dsdx
                    ((short)(w3&SR_MASK_16)) * FIXED2FLOATRECIP10);          // dsdy
        }
    };
    
    public static GBIFunc RDP_TexRect = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
//            int w2 = N64.RDRAM.getInt(PC[PCi] + 4);
//            PC[PCi] += 8;
            int w2 = gsp.getCmd();
            
//            int w3 = N64.RDRAM.getInt(PC[PCi] + 4);
//            PC[PCi] += 8;
            int w3 = gsp.getCmd();
            
            gdp.gDPTextureRectangle(((w1>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,                    // ulx
                    (w1&SR_MASK_12) * FIXED2FLOATRECIP2,                          // uly
                    ((w0>>12)&SR_MASK_12) * FIXED2FLOATRECIP2,                    // lrx
                    (w0&SR_MASK_12) * FIXED2FLOATRECIP2,                          // lry
                    (w1>>24)&SR_MASK_3,                                        // tile
                    ((short)((w2>>16)&SR_MASK_16)) * FIXED2FLOATRECIP5,           // s
                    ((short)(w2&SR_MASK_16)) * FIXED2FLOATRECIP5,                 // t
                    ((short)((w3>>16)&SR_MASK_16)) * FIXED2FLOATRECIP10,          // dsdx
                    ((short)(w3&SR_MASK_16)) * FIXED2FLOATRECIP10,
                    false);              // dsdy
        }
    };
    
    public static void RDP_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        // Initialize RDP commands to RDP_UNKNOWN
        for (int i = 0xC8; i <= 0xCF; i++)
            gsp.setGBI(i, RDP_Unknown);
        for (int i = 0xE4; i <= 0xFF; i++)
            gsp.setGBI(i, RDP_Unknown);
        
        gsp.setGBI(G_NOOP, RDP_NoOp);
        gsp.setGBI(G_SETCIMG, RDP_SetCImg);
        gsp.setGBI(G_SETZIMG, RDP_SetZImg);
        gsp.setGBI(G_SETTIMG, RDP_SetTImg);
        gsp.setGBI(G_SETCOMBINE, RDP_SetCombine);
        gsp.setGBI(G_SETENVCOLOR, RDP_SetEnvColor);
        gsp.setGBI(G_SETPRIMCOLOR, RDP_SetPrimColor);
        gsp.setGBI(G_SETBLENDCOLOR, RDP_SetBlendColor);
        gsp.setGBI(G_SETFOGCOLOR, RDP_SetFogColor);
        gsp.setGBI(G_SETFILLCOLOR, RDP_SetFillColor);
        gsp.setGBI(G_FILLRECT, RDP_FillRect);
        gsp.setGBI(G_SETTILE, RDP_SetTile);
        gsp.setGBI(G_LOADTILE, RDP_LoadTile);
        gsp.setGBI(G_LOADBLOCK, RDP_LoadBlock);
        gsp.setGBI(G_SETTILESIZE, RDP_SetTileSize);
        gsp.setGBI(G_LOADTLUT, RDP_LoadTLUT);
        gsp.setGBI(G_RDPSETOTHERMODE, RDP_SetOtherMode);
        gsp.setGBI(G_SETPRIMDEPTH, RDP_SetPrimDepth);
        gsp.setGBI(G_SETSCISSOR, RDP_SetScissor);
        gsp.setGBI(G_SETCONVERT, RDP_SetConvert);
        gsp.setGBI(G_SETKEYR, RDP_SetKeyR);
        gsp.setGBI(G_SETKEYGB, RDP_SetKeyGB);
        gsp.setGBI(G_RDPFULLSYNC, RDP_FullSync);
        gsp.setGBI(G_RDPTILESYNC, RDP_TileSync);
        gsp.setGBI(G_RDPPIPESYNC, RDP_PipeSync);
        gsp.setGBI(G_RDPLOADSYNC, RDP_LoadSync);
        gsp.setGBI(G_TEXRECTFLIP, RDP_TexRectFlip);
        gsp.setGBI(G_TEXRECT, RDP_TexRect);
        
        if (DEBUG_MICROCODE) System.out.println("Initialized RDP opcodes");
    }

}
