package gln64j.rdp;

import gln64j.OpenGlGdp;
import gln64j.Registers;
import gln64j.Gbi;
import gln64j.rdp.combiners.Combiners;

public class Gdp {

    public static final int CHANGED_RENDERMODE = 0x001;
    public static final int CHANGED_CYCLETYPE = 0x002;
    public static final int CHANGED_SCISSOR = 0x004;
    public static final int CHANGED_COLORBUFFER = 0x008;
    public static final int CHANGED_TILE = 0x010;
    public static final int CHANGED_COMBINE_COLORS = 0x020;
    public static final int CHANGED_COMBINE = 0x040;
    public static final int CHANGED_ALPHACOMPARE = 0x080;
    public static final int CHANGED_FOGCOLOR = 0x100;
    public static final int CHANGED_TEXTURE = 0x200;
    public static final int CHANGED_VIEWPORT = 0x400;

    public static class OtherMode {
        public int w0;
        public int w1;
    }

    public static final int PIPELINE_MODE = 0x800000;
    public static final int CYCLE_TYPE = 0x300000;
    public static final int TEX_PERSP = 0x80000;
    public static final int TEX_DETAIL = 0x60000;
    public static final int TEX_LOD = 0x10000;
    public static final int TEX_LUT = 0xC000;
    public static final int TEX_FILTER = 0x3000;
    public static final int TEX_CONVERT = 0xE00;
    public static final int COMBINE_KEY = 0x100;
    public static final int COLOR_DITHER = 0xC0;
    public static final int ALPHA_DITHER = 0x30;
    public static final int DEPTH_SOURCE = 0x4;
    public static final int ALPHA_COMPARE = 0x3;

    public static final int RDP_GETOM_CYCLE_TYPE(OtherMode om) {
        return (((om).w0 >> 20) & 0x3);
    }

    public static final int RDP_GETOM_TLUT_TYPE(OtherMode om) {
        return (((om).w0 >> 14) & 0x3);
    }

    public static final int RDP_GETOM_SAMPLE_TYPE(OtherMode om) {
        return (((om).w0 >> 12) & 0x3);
    }

    public static final int RDP_GETOM_FORCE_BLEND(OtherMode om) {
        return (((om).w1 & 0x4000) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_ALPHA_CVG_SELECT(OtherMode om) {
        return (((om).w1 & 0x2000) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_CVG_TIMES_ALPHA(OtherMode om) {
        return (((om).w1 & 0x1000) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_Z_MODE(OtherMode om) {
        return (((om).w1 >> 10) & 0x3);
    }

    public static final int RDP_GETOM_Z_UPDATE_EN(OtherMode om) {
        return (((om).w1 & 0x20) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_Z_COMPARE_EN(OtherMode om) {
        return (((om).w1 & 0x10) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_Z_SOURCE_SEL(OtherMode om) {
        return (((om).w1 & 0x04) != 0 ? 1 : 0);
    }

    public static final int RDP_GETOM_ALPHA_COMPARE_EN(OtherMode om) {
        return (((om).w1 >> 0) & 0x3);
    }


    public static class FillColor {
        public float[] color = new float[4];
        public float z;
        public float dz;
    }

    public static class PrimDepth {
        public float z;
        public float deltaZ;
    }

    public static class ColorImage {
        public int format;
        public int size;
        public int width;
        public int height;
        public int bpl;
        public int address;
        public boolean changed;
        public int depthImage;
    }

    public static class Scissor {
        public int mode;
        public float ulx;
        public float uly;
        public float lrx;
        public float lry;
    }

    public static class Convert {
        public int k0;
        public int k1;
        public int k2;
        public int k3;
        public int k4;
        public int k5;
    }

    public static class Key {
        public float[] center = new float[4];
        public float[] scale = new float[4];
        public float[] width = new float[4];
    }

    public int DList;
    public OtherMode otherMode = new OtherMode();
    public int changed;
    public float[] fogColor = new float[4];
    public float[] blendColor = new float[4];
    public Scissor scissor = new Scissor();
    public ColorImage colorImage = new ColorImage();

    protected PrimDepth primDepth = new PrimDepth();
    protected DepthBufferStack depthBuffers = new DepthBufferStack();
    protected DepthBufferStack.DepthBuffer current;
    protected int depthImageAddress;
    protected FillColor fillColor = new FillColor();
    protected Runnable checkInterrupts;
    protected Registers reg;


    public Gdp(Runnable checkInterrupts, Registers regs) {
        this.checkInterrupts = checkInterrupts;
        this.reg = regs;

        OpenGlGdp.cache.construct();

        current = null;
        depthBuffers.init();
    }

    public void gDPTexture(float sc, float tc, int level, int tile, int on) {
        OpenGlGdp.cache.setTexture(sc, tc, level, tile, on);
        changed |= CHANGED_TEXTURE;
    }

    public void gDPPipelineMode(int mode) {
        otherMode.w0 &= ~PIPELINE_MODE;
        otherMode.w0 |= ((mode & 0x1) << 23);

    }

    public void gDPSetCycleType(int type) {
        otherMode.w0 &= ~CYCLE_TYPE;
        otherMode.w0 |= ((type & 0x3) << 20);
        changed |= CHANGED_CYCLETYPE;
    }

    public void gDPSetTexturePersp(int enable) {
        otherMode.w0 &= ~TEX_PERSP;
        otherMode.w0 |= ((enable & 0x1) << 19);
    }

    public void gDPSetTextureDetail(int type) {
        otherMode.w0 &= ~TEX_DETAIL;
        otherMode.w0 |= ((type & 0x3) << 17);
    }

    public void gDPSetTextureLOD(int mode) {
        otherMode.w0 &= ~TEX_LOD;
        otherMode.w0 |= ((mode & 0x1) << 16);
    }

    public void gDPSetTextureLUT(int mode) {
        otherMode.w0 &= ~TEX_LUT;
        otherMode.w0 |= ((mode & 0x3) << 14);
    }

    public void gDPSetTextureFilter(int type) {
        otherMode.w0 &= ~TEX_FILTER;
        otherMode.w0 |= ((type & 0x3) << 12);
    }

    public void gDPSetTextureConvert(int type) {
        otherMode.w0 &= ~TEX_CONVERT;
        otherMode.w0 |= ((type & 0x7) << 9);
    }

    public void gDPSetCombineKey(int type) {
        otherMode.w0 &= ~COMBINE_KEY;
        otherMode.w0 |= ((type & 0x1) << 8);
    }

    public void gDPSetColorDither(int type) {
        otherMode.w0 &= ~COLOR_DITHER;
        otherMode.w0 |= ((type & 0x3) << 6);
    }

    public void gDPSetAlphaDither(int type) {
        otherMode.w0 &= ~ALPHA_DITHER;
        otherMode.w0 |= ((type & 0x3) << 4);
    }

    public void gDPSetAlphaCompare(int mode) {
        otherMode.w1 &= ~ALPHA_COMPARE;
        otherMode.w1 |= ((mode & 0x3));
        changed |= CHANGED_ALPHACOMPARE;
    }

    public void gDPSetDepthSource(int source) {
        otherMode.w1 &= ~DEPTH_SOURCE;
        otherMode.w1 |= ((source & 0x1) << 2);
    }

    public void gDPSetRenderMode(int mode1, int mode2) {
        otherMode.w1 = (otherMode.w1 & 0x00000007);
        otherMode.w1 = (otherMode.w1 | mode1 | mode2);
        changed |= CHANGED_RENDERMODE;
    }

    public void gDPSetOtherMode(int mode0, int mode1) {
        otherMode.w0 = mode0;
        otherMode.w1 = mode1;
        changed |= CHANGED_RENDERMODE | CHANGED_CYCLETYPE | CHANGED_ALPHACOMPARE;
    }

    public void gDPSetPrimDepth(int z, int dz) {
        primDepth.z = z;
        primDepth.deltaZ = dz;
        OpenGlGdp.setZDepth(z);
    }

    public void gDPSetCombine(int muxs0, int muxs1) {
        OpenGlGdp.combiners.combine.setMuxs0(muxs0);
        OpenGlGdp.combiners.combine.setMuxs1(muxs1);
        changed |= CHANGED_COMBINE;
    }

    public void gDPSetColorImage(int format, int size, int width, int address) {
        if (colorImage.address != address) {
            colorImage.changed = false;

            if (width == OpenGlGdp.screenWidth)
                colorImage.height = OpenGlGdp.screenHeight;
            else
                colorImage.height = 1;
        }

        colorImage.format = format;
        colorImage.size = size;
        colorImage.width = width;
        colorImage.address = address;
    }

    public void gDPSetTextureImage(int format, int size, int width, int address) {
        OpenGlGdp.cache.setTextureImage(format, size, width, address);
    }

    public void gDPSetDepthImage(int address) {
        DepthBufferStack.DepthBuffer buffer = depthBuffers.top;
        while (buffer != null) {
            if (buffer.address == address) {
                depthBuffers.moveToTop(buffer);
                current = buffer;
                return;
            }
            buffer = buffer.lower;
        }
        buffer = new DepthBufferStack.DepthBuffer();
        depthBuffers.addTop(buffer);
        buffer.address = address;
        buffer.cleared = true;
        current = buffer;
        if (current.cleared)
            OpenGlGdp.OGL_ClearDepthBuffer(RDP_GETOM_Z_UPDATE_EN(otherMode) != 0);
        depthImageAddress = address;
    }

    public void gDPSetEnvColor(int r, int g, int b, int a) {
        Combiners.envColor[0] = r * 0.0039215689f; // r / 255
        Combiners.envColor[1] = g * 0.0039215689f; // g / 255
        Combiners.envColor[2] = b * 0.0039215689f; // b / 255
        Combiners.envColor[3] = a * 0.0039215689f; // a / 255
        changed |= CHANGED_COMBINE_COLORS;
    }

    public void gDPSetBlendColor(int r, int g, int b, int a) {
        blendColor[0] = r * 0.0039215689f; // r / 255
        blendColor[1] = g * 0.0039215689f; // g / 255
        blendColor[2] = b * 0.0039215689f; // b / 255
        blendColor[3] = a * 0.0039215689f; // a / 255
    }

    public void gDPSetFogColor(int r, int g, int b, int a) {
        fogColor[0] = r * 0.0039215689f; // r / 255
        fogColor[1] = g * 0.0039215689f; // g / 255
        fogColor[2] = b * 0.0039215689f; // b / 255
        fogColor[3] = a * 0.0039215689f; // a / 255
        changed |= CHANGED_FOGCOLOR;
    }

    public void gDPSetFillColor(int c) {
        fillColor.color[0] = ((c >> 11) & 0x1f) * 0.032258064f;
        fillColor.color[1] = ((c >> 6) & 0x1f) * 0.032258064f;
        fillColor.color[2] = ((c >> 1) & 0x1f) * 0.032258064f;
        fillColor.color[3] = c & 0x1;
        fillColor.z = (c >> 2) & 0x3fff;
        fillColor.dz = c & 0x3;
    }

    public void gDPSetPrimColor(int m, int l, int r, int g, int b, int a) {
        // rdpState.primColor = w2; r|g|b|a
        Combiners.primColor.m = m;
        Combiners.primColor.l = l * 0.0039215689f; // l / 255
        Combiners.primColor.r = r * 0.0039215689f; // r / 255
        Combiners.primColor.g = g * 0.0039215689f; // g / 255
        Combiners.primColor.b = b * 0.0039215689f; // b / 255
        Combiners.primColor.a = a * 0.0039215689f; // a / 255
        changed |= CHANGED_COMBINE_COLORS;
    }

    public void gDPSetTile(int format, int size, int line, int tmem, int tilenum, int palette, int cmt, int cms, int maskt, int masks, int shiftt, int shifts) {
        OpenGlGdp.cache.setTile(format, size, line, tmem, tilenum, palette, cmt, cms, maskt, masks, shiftt, shifts);
    }

    public void gDPSetTileSize(int tilenum, int uls, int ult, int lrs, int lrt) {
        OpenGlGdp.cache.setTileSize(tilenum,
                (uls >> 2) & 0x3ff,
                (ult >> 2) & 0x3ff,
                (lrs >> 2) & 0x3ff,
                (lrt >> 2) & 0x3ff,
                uls * 0.25f,  // uls / 4
                ult * 0.25f,  // ult / 4
                lrs * 0.25f,  // lrs / 4
                lrt * 0.25f); // lrt / 4
        changed |= CHANGED_TILE;
    }

    public void gDPLoadTile(int tilenum, int uls, int ult, int lrs, int lrt) {
        gDPSetTileSize(tilenum, uls, ult, lrs, lrt);
        OpenGlGdp.cache.loadTile(tilenum);
    }

    public void gDPLoadBlock(int tilenum, int uls, int ult, int lrs, int dxt) {
        gDPSetTileSize(tilenum, uls, ult, lrs, dxt);
        OpenGlGdp.cache.loadBlock(tilenum, uls, ult, lrs, dxt);
    }

    public void gDPLoadTLUT(int tilenum, int uls, int ult, int lrs, int lrt) {
        gDPSetTileSize(tilenum, uls, ult, lrs, lrt);
        OpenGlGdp.cache.loadLUT(tilenum);
    }

    public void gDPSetScissor(int mode, float ulx, float uly, float lrx, float lry) {
        scissor.mode = mode;
        scissor.ulx = ulx;
        scissor.uly = uly;
        scissor.lrx = lrx;
        scissor.lry = lry;
        changed |= CHANGED_SCISSOR;
    }

    public void gDPFillRectangle(int ulx, int uly, int lrx, int lry) {
        DepthBufferStack.DepthBuffer buffer = depthBuffers.findBuffer(colorImage.address);

        if (buffer != null)
            buffer.cleared = true;

        if (depthImageAddress == colorImage.address) {
            OpenGlGdp.OGL_ClearDepthBuffer(RDP_GETOM_Z_UPDATE_EN(otherMode) != 0);
            return;
        }

        if (RDP_GETOM_CYCLE_TYPE(otherMode) == Gbi.G_CYC_FILL) {
            lrx++;
            lry++;

            if ((ulx == 0) && (uly == 0) && (lrx == OpenGlGdp.screenWidth) && (lry == OpenGlGdp.screenHeight)) {
                OpenGlGdp.OGL_ClearColorBuffer(fillColor.color);
                return;
            }
        }

        OpenGlGdp.OGL_DrawRect(ulx, uly, lrx, lry, (RDP_GETOM_CYCLE_TYPE(otherMode) == Gbi.G_CYC_FILL) ? fillColor.color : blendColor, RDP_GETOM_Z_SOURCE_SEL(otherMode));

        if (current != null)
            current.cleared = false;
        colorImage.changed = true;
        colorImage.height = StrictMath.max(colorImage.height, lry);
    }

    public void gDPSetConvert(int k0, int k1, int k2, int k3, int k4, int k5) {
    }

    public void gDPSetKeyR(int cR, int sR, int wR) {
    }

    public void gDPSetKeyGB(int cG, int sG, int wG, int cB, int sB, int wB) {
    }

    public void update() {
        if (current != null)
            current.cleared = false;
        colorImage.changed = true;
        colorImage.height = (int) StrictMath.max(colorImage.height, scissor.lry);
    }

    public void gDPTextureRectangle(float ulx, float uly, float lrx, float lry, int tilenum, float s, float t, float dsdx, float dtdy, boolean flip) {
        if (RDP_GETOM_CYCLE_TYPE(otherMode) == Gbi.G_CYC_COPY) {
            dsdx = 1.0f;
            lrx += 1.0f;
            lry += 1.0f;
        }

        float lrs = s + (lrx - ulx - 1) * dsdx;
        float lrt = t + (lry - uly - 1) * dtdy;

        OpenGlGdp.cache.setTextureTile(lrs, lrt, tilenum, s, t, dsdx, dtdy);

        if (lrs > s) {
            if (lrt > t)
                OpenGlGdp.OGL_DrawTexturedRect(ulx, uly, lrx, lry, s, t, lrs, lrt, flip, RDP_GETOM_Z_SOURCE_SEL(otherMode), RDP_GETOM_CYCLE_TYPE(otherMode));
            else
                OpenGlGdp.OGL_DrawTexturedRect(ulx, lry, lrx, uly, s, lrt, lrs, t, flip, RDP_GETOM_Z_SOURCE_SEL(otherMode), RDP_GETOM_CYCLE_TYPE(otherMode));
        } else {
            if (lrt > t)
                OpenGlGdp.OGL_DrawTexturedRect(lrx, uly, ulx, lry, lrs, t, s, lrt, flip, RDP_GETOM_Z_SOURCE_SEL(otherMode), RDP_GETOM_CYCLE_TYPE(otherMode));
            else
                OpenGlGdp.OGL_DrawTexturedRect(lrx, lry, ulx, uly, lrs, lrt, s, t, flip, RDP_GETOM_Z_SOURCE_SEL(otherMode), RDP_GETOM_CYCLE_TYPE(otherMode));
        }

        OpenGlGdp.cache.resetTextureTile();

        if (current != null)
            current.cleared = false;
        colorImage.changed = true;
        colorImage.height = (int) StrictMath.max(colorImage.height, scissor.lry);
    }

    public void gDPTextureRectangleFlip(float ulx, float uly, float lrx, float lry, int tilenum, float s, float t, float dsdx, float dtdy) {
        gDPTextureRectangle(ulx, uly, lrx, lry, tilenum, s + (lrx - ulx) * dsdx, t + (lry - uly) * dtdy, -dsdx, -dtdy, true);
    }

    public void gDPFullSync() {
        reg.MI_Registers[reg.MI_INTR] |= Registers.MI_INTR_DP;
        checkInterrupts.run();
    }

    public void gDPTileSync() {

    }

    public void gDPPipeSync() {

    }

    public void gDPLoadSync() {

    }

    public void gDPNoOp() {

    }

}
