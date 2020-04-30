package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.emu.util.debug.Debug.DEBUG_UNKNOWN;
import static me.hydos.J64.emu.util.debug.Debug.DebugMsg;
import static me.hydos.J64.gfx.Gbi.*;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class Rdpfuncs2 {
    
    protected static Gsp gsp;
    protected static Gdp gdp;
    
    public static GBIFunc RDP_Unknown = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            if (DEBUG_MICROCODE) {
                DebugMsg(DEBUG_UNKNOWN, "RDP_Unknown\r\n");
                DebugMsg(DEBUG_UNKNOWN, "\tUnknown RDP opcode %02X\r\n", (w1 >> 24) & 0xff);
            }
        }
    };
    
    public static GBIFunc RDP_NoOp = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPNoOp();
        }
    };
    
    public static GBIFunc RDP_SetCImg = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetColorImage(
                    (w1 >> 21) & 0x7, // rdpFbFormat
                    (w1 >> 19) & 0x3, // rdpFbSize
                    (w1 & 0xfff) + 1, // rdpFbWidth (0x3ff)
                    gsp.RSP_SegmentToPhysical(w2)); // rdpFbAddress (w2 & 0x01ffffff)
        }
    };
    
    public static GBIFunc RDP_SetZImg = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetDepthImage(
                    gsp.RSP_SegmentToPhysical(w2)); // rdpZbAddress (w2 & 0x01ffffff)
        }
    };
    
    public static GBIFunc RDP_SetTImg = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetTextureImage(
                    (w1 >> 21) & 0x7, // rdpTiFormat
                    (w1 >> 19) & 0x3, // rdpTiSize
                    (w1 & 0xfff) + 1, // rdpTiWidth (0x3ff)
                    gsp.RSP_SegmentToPhysical(w2)); // rdpTiAddress (w2 & 0x01ffffff)
        }
    };
    
    public static GBIFunc RDP_SetCombine = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetCombine(
                    w1 & 0xffffff, // muxs0 rdpState.combineModes.w1
                    w2);	   // muxs1 rdpState.combineModes.w2
        }
    };
    
    public static GBIFunc RDP_SetEnvColor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetEnvColor( // rdpState.envColor = w2;
                    (w2 >> 24) & 0xff,	// r
                    (w2 >> 16) & 0xff,	// g
                    (w2 >> 8) & 0xff,	// b
                    (w2 >> 0) & 0xff);  // a
        }
    };
    
    public static GBIFunc RDP_SetPrimColor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetPrimColor( // rdpState.primColor = w2;
                    (w1 & 0xff) << 8,  // m minlevel	
                    (w1 >> 0) & 0xff,  // l level
                    
                    (w2 >> 24) & 0xff, // r
                    (w2 >> 16) & 0xff, // g
                    (w2 >> 8) & 0xff,  // b
                    (w2 >> 0) & 0xff); // a
        }
    };
    
    public static GBIFunc RDP_SetBlendColor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetBlendColor( // rdpState.blendColor = w2;
                    (w2 >> 24) & 0xff, // r
                    (w2 >> 16) & 0xff, // g
                    (w2 >> 8) & 0xff,  // b
                    (w2 >> 0) & 0xff); // a
        }
    };
    
    public static GBIFunc RDP_SetFogColor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetFogColor( // rdpState.fogColor = w2;
                    (w2 >> 24) & 0xff, // r
                    (w2 >> 16) & 0xff, // g
                    (w2 >> 8) & 0xff,  // b
                    (w2 >> 0) & 0xff); // a
        }
    };
    
    public static GBIFunc RDP_SetFillColor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetFillColor(w2); // rdpState.fillColor
        }
    };
    
    public static GBIFunc RDP_FillRect = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPFillRectangle(
                    (w2 >> 14) & 0x3ff, // ulx rect.xh (w2 >> 12) & 0xfff
                    (w2 >> 2) & 0x3ff,  // uly rect.yh (w2 >> 0) & 0xfff
                    (w1 >> 14) & 0x3ff, // lrx rect.xl (w1 >> 12) & 0xfff
                    (w1 >> 2) & 0x3ff); // lry rect.yl (w1 >> 0) & 0xfff
        }
    };
    
    public static GBIFunc RDP_SetTile = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetTile( // rdpTiles[tilenum]
                    (w1 >> 21) & 0x7,  // format
                    (w1 >> 19) & 0x3,  // size
                    (w1 >> 9) & 0x1ff, // line / 8
                    (w1 >> 0) & 0x1ff, // tmem / 8
                    (w2 >> 24) & 0x7,  // tilenum
                    (w2 >> 20) & 0xf,  // palette
                    (w2 >> 18) & 0x3,  // mt/ct ((w2 >> 18) & 0x1) | ((w2 >> 19) & 0x1)
                    (w2 >> 8) & 0x3,   // ms/cs ((w2 >> 8) & 0x1) | ((w2 >> 9) & 0x1)
                    (w2 >> 14) & 0xf,  // mask_t
                    (w2 >> 4) & 0xf,   // mask_s
                    (w2 >> 10) & 0xf,  // shift_t
                    (w2 >> 0) & 0xf);  // shift_s
        }
    };
    
    public static GBIFunc RDP_LoadTile = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPLoadTile(
                    (w2 >> 24) & 0x7,	// tilenum
                    (w1 >> 12) & 0xfff,	// uls sl * 4
                    (w1 >> 0) & 0xfff,  // ult tl * 4
                    (w2 >> 12) & 0xfff,	// lrs sh * 4
                    (w2 >> 0) & 0xfff); // lrt th * 4
        }
    };
    
    public static GBIFunc RDP_LoadBlock = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPLoadBlock(
                    (w2 >> 24) & 0x7,	// tilenum
                    (w1 >> 12) & 0xfff,	// uls sl
                    (w1 >> 0) & 0xfff,  // ult tl >> 11
                    (w2 >> 12) & 0xfff,	// lrs sh
                    (w2 >> 0) & 0xfff); // dxt
        }
    };
    
    public static GBIFunc RDP_SetTileSize = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetTileSize( // rdpTiles[tilenum]
                    (w2 >> 24) & 0x7,	// tilenum
                    (w1 >> 12) & 0xfff,	// uls sl
                    (w1 >> 0) & 0xfff,  // ult tl
                    (w2 >> 12) & 0xfff,	// lrs sh
                    (w2 >> 0) & 0xfff); // lrt th
        }
    };
    
    public static GBIFunc RDP_LoadTLUT = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPLoadTLUT( // rdpTiles[tilenum]
                    (w2 >> 24) & 0x7,   // tilenum
                    (w1 >> 12) & 0xfff, // uls sl
                    (w1 >> 0) & 0xfff,  // ult tl
                    (w2 >> 12) & 0xfff, // lrs sh
                    (w2 >> 0) & 0xfff); // lrt th
        }
    };
    
    public static GBIFunc RDP_SetOtherMode = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetOtherMode(
                    w1 & 0xffffff, // mode0 rdpState.otherModes.w1
                    w2);           // mode1 rdpState.otherModes.w2
        }
    };
    
    public static GBIFunc RDP_SetPrimDepth = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetPrimDepth(
                    (w2 >> 16) & 0xffff, // z rdpState.primitiveZ (0xfff)
                    w2 & 0xffff);        // dz rdpState.primitiveDeltaZ (w1 & 0xfff)
        }
    };
    
    public static GBIFunc RDP_SetScissor = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetScissor(
                    (w2 >> 24) & 0x3,             // rdpState.clipMode
                    ((w1 >> 12) & 0xfff) * 0.25f, // ulx rdpState.clip.xh * 4
                    ((w1 >> 0) & 0xfff) * 0.25f,  // uly rdpState.clip.yh * 4
                    ((w2 >> 12) & 0xfff) * 0.25f, // lrx rdpState.clip.xl * 4
                    ((w2 >> 0) & 0xfff) * 0.25f); // lry rdpState.clip.yl * 4
        }
    };
    
    public static GBIFunc RDP_SetConvert = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetConvert(
                    (w1 >> 13) & 0x1ff,                      // k0
                    (w1 >> 4) & 0x1ff,                       // k1
                    ((w1 & 0xf) << 5) | ((w2 >> 25) & 0x1f), // k2
                    (w2 >> 18) & 0x1ff,                      // k3
                    (w2 >> 9) & 0x1ff,                       // k4
                    (w2 >> 0) & 0x1ff);                      // k5 rdpState.k5 (0xff)
        }
    };
    
    public static GBIFunc RDP_SetKeyR = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetKeyR(
                    (w2 >> 8) & 0xff,    // cR
                    (w2 >> 0) & 0xff,    // sR
                    (w2 >> 16) & 0xfff); // wR
        }
    };
    
    public static GBIFunc RDP_SetKeyGB = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPSetKeyGB(
                    (w2 >> 24) & 0xff,  // cG
                    (w2 >> 16) & 0xff,  // sG
                    (w1 >> 12) & 0xfff, // wG
                    (w2 >> 8) & 0xff,   // cB
                    (w2 >> 0) & 0xff,   // SB
                    (w1 >> 0) & 0xfff); // wB
        }
    };
    
    public static GBIFunc RDP_FullSync = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPFullSync();
        }
    };
    
    public static GBIFunc RDP_TileSync = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPTileSync();
        }
    };
    
    public static GBIFunc RDP_PipeSync = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPPipeSync();
        }
    };
    
    public static GBIFunc RDP_LoadSync = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            gdp.gDPLoadSync();
        }
    };
    
    public static GBIFunc RDP_TexRectFlip = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            int w3 = gsp.getCmd();
            int w4 = gsp.getCmd();
            
            gdp.gDPTextureRectangleFlip(
                    ((w2 >> 12) & 0xfff) * 0.25f,                   // ulx rect.xh * 4
                    ((w2 >> 0) & 0xfff) * 0.25f,                    // uly rect.yh * 4
                    ((w1 >> 12) & 0xfff) * 0.25f,                   // lrx rect.xl * 4
                    ((w1 >> 0) & 0xfff) * 0.25f,                    // lry rect.yl * 4
                    (w2 >> 24) & 0x7,                               // rect.tilenum
//                    ((short)((w3 >> 16) & 0xffff)) * 0.03125f,      // s rect.t * 32
//                    ((short)((w3 >> 0) & 0xffff)) * 0.03125f,       // t rect.s * 32
//                    ((short)((w4 >> 16) & 0xffff)) * 0.0009765625f, // dsdx rect.dtdy * 1024
//                    ((short)((w4 >> 0) & 0xffff)) * 0.0009765625f); // dsdy rect.dsdx * 1024
                    ((w3 >> 16) & 0xffff) * 0.03125f,      // s rect.t * 32
                    ((w3 >> 0) & 0xffff) * 0.03125f,       // t rect.s * 32
                    ((w4 >> 16) & 0xffff) * 0.0009765625f, // dsdx rect.dtdy * 1024
                    ((w4 >> 0) & 0xffff) * 0.0009765625f); // dsdy rect.dsdx * 1024
        }
    };
    
    public static GBIFunc RDP_TexRect = new GBIFunc() {
        @Override
		public void exec(int w1, int w2) {
            int w3 = gsp.getCmd();
            int w4 = gsp.getCmd();
            
            gdp.gDPTextureRectangle(
                    ((w2 >> 12) & 0xfff) * 0.25f,                   // ulx rect.xh * 4
                    ((w2 >> 0) & 0xfff) * 0.25f,                    // uly rect.yh * 4
                    ((w1 >> 12) & 0xfff) * 0.25f,                   // lrx rect.xl * 4
                    ((w1 >> 0) & 0xfff) * 0.25f,                    // lry rect.yl * 4
                    (w2 >> 24) & 0x7,                               // rect.tilenum
//                    ((short)((w3 >> 16) & 0xffff)) * 0.03125f,      // s rect.s * 32
//                    ((short)((w3 >> 0) & 0xffff)) * 0.03125f,       // t rect.t * 32
//                    ((short)((w4 >> 16) & 0xffff)) * 0.0009765625f, // dsdx rect.dsdx * 1024
//                    ((short)((w4 >> 0) & 0xffff)) * 0.0009765625f,  // dsdy rect.dtdy * 1024
                    ((w3 >> 16) & 0xffff) * 0.03125f,      // s rect.s * 32
                    ((w3 >> 0) & 0xffff) * 0.03125f,       // t rect.t * 32
                    ((w4 >> 16) & 0xffff) * 0.0009765625f, // dsdx rect.dsdx * 1024
                    ((w4 >> 0) & 0xffff) * 0.0009765625f,  // dsdy rect.dtdy * 1024
                    false);
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
        
        if (DEBUG_MICROCODE) System.out.println("Initialized RDP2 opcodes");
    }
    
}
