package gln64j.rdp.textures;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.media.opengl.GL;

public class TextureCache {

    public static final int G_IM_FMT_RGBA = 0;
    public static final int G_IM_FMT_YUV = 1;
    public static final int G_IM_FMT_CI = 2;
    public static final int G_IM_FMT_IA = 3;
    public static final int G_IM_FMT_I = 4;
    public static final int G_IM_SIZ_4b = 0;
    public static final int G_IM_SIZ_8b = 1;
    public static final int G_IM_SIZ_16b = 2;
    public static final int G_IM_SIZ_32b = 3;
    public static final int G_IM_SIZ_DD = 5;


    public static final int TEXTUREMODE_NORMAL = 0;
    public static final int TEXTUREMODE_TEXRECT = 1;
    public static final int TEXTUREMODE_BGIMAGE = 2;

    public static final int LOADTYPE_BLOCK = 0;
    public static final int LOADTYPE_TILE = 1;

    public static final int CHANGED_TMEM = 0x008;

    private static final int CRC32_POLYNOMIAL = 0x04C11DB7;

    private static class BgImage {
        public int address;
        public int width;
        public int height;
        public int format;
        public int size;
        public int palette;
    }

    private static class TextureImage {
        public int format;
        public int size;
        public int width;
        public int bpl;
        public int address;
    }

    private static class TexRect {
        public int width;
        public int height;
    }

    private static class Texture {
        public float scales;
        public float scalet;
        public int level;
        public int on;
        public int tile;
    }

    public static class gDPTile {
        int format, size, line, tmem, palette;

        public int maskt, masks;
        public int shiftt, shifts;
        public float fuls, fult, flrs, flrt;
        public int uls, ult, lrs, lrt;

        public int mirrort;
        public int clampt;
        public int pad0;

        public int mirrors;
        public int clamps;
        public int pad1;

        public void setCmt(int value) {
            pad0 = (value >> 2) & 0x3FFFFFFF;
            clampt = (value >> 1) & 1;
            mirrort = (value) & 1;
        }

        public int getCmt() {
            return ((pad0 & 0x3FFFFFFF) << 2) | ((clampt & 1) << 1) | (mirrort & 1);
        }

        public void setCms(int value) {
            pad1 = (value >> 2) & 0x3FFFFFFF;
            clamps = (value >> 1) & 1;
            mirrors = (value) & 1;
        }

        public int getCms() {
            return ((pad1 & 0x3FFFFFFF) << 2) | ((clamps & 1) << 1) | (mirrors & 1);
        }

        public final int powof(int dim) {
            int num = 1;
            int i = 0;
            while (num < dim) {
                num <<= 1;
                i++;
            }
            return i;
        }

    }

    ;

    private interface InterleaveFunc {
        void Interleave(byte[] mem, int off, int numDWords);
    }

    ;

    private final InterleaveFunc DWordInterleave = new InterleaveFunc() {
        public final void Interleave(byte[] mem, int off, int numDWords) {
            numDWords = off + (numDWords << 3);
            for (int i = off; i < numDWords; i += 8) {
                System.arraycopy(mem, i, tmp, 0, 4);
                System.arraycopy(mem, i + 4, mem, i, 4);
                System.arraycopy(tmp, 0, mem, i + 4, 4);
            }
        }
    };

    private final InterleaveFunc QWordInterleave = new InterleaveFunc() {
        public final void Interleave(byte[] mem, int off, int numDWords) {
            numDWords = off + (numDWords << 3);
            for (int i = off; i < numDWords; i += 16) {
                System.arraycopy(mem, i, tmp, 0, 4);
                System.arraycopy(mem, i + 8, mem, i, 4);
                System.arraycopy(tmp, 0, mem, i + 8, 4);
                System.arraycopy(mem, i + 4, tmp, 0, 4);
                System.arraycopy(mem, i + 12, mem, i + 4, 4);
                System.arraycopy(tmp, 0, mem, i + 12, 4);
            }
        }
    };

    public int changed;
    public gDPTile[] textureTile = new gDPTile[2];
    public CachedTexture[] current = new CachedTexture[2];

    private boolean ARB_multitexture; // TNT, GeForce, Rage 128, Radeon
    private int cachedBytes;
    private CachedTextureStack stack = new CachedTextureStack();
    private Texture texture = new Texture();
    private gDPTile[] tiles = new gDPTile[8];
    private gDPTile loadTile = new gDPTile();
    private TexRect texRect = new TexRect();
    private TextureImage textureImage = new TextureImage();
    private int loadType;
    private ByteBuffer paletteCRC16 = ByteBuffer.allocate(64);
    private ByteBuffer paletteCRC256 = ByteBuffer.allocate(4);
    private int textureMode;
    private GL gl;
    private int maxBytes;
    private int textureBitDepth;
    private final byte[] tmp = new byte[4];
    private int hits;
    private int misses;
    private int[] glNoiseNames = new int[32];
    private CachedTexture dummy;
    private BgImage bgImage = new BgImage();
    private Checksum crc32;
    private ByteBuffer rdram;
    private ByteBuffer tmem;
    private int rdramSize;
    private boolean enable2xSaI;
    private int[] crcTable = new int[256];
    private int maxTextureUnits; // TNT = 2, GeForce = 2-4, Rage 128 = 2, Radeon = 3-6

    public void construct() {
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = new gDPTile();
        for (int i = 0; i < textureTile.length; i++)
            textureTile[i] = new gDPTile();
        loadTile = tiles[7];
        textureTile[0] = tiles[0];
        textureTile[1] = tiles[1];
    }

    public void config(int maxBytes, int textureBitDepth) {
        this.maxBytes = maxBytes;
        this.textureBitDepth = textureBitDepth;
        enable2xSaI = false;
    }

    public void init(GL gl, ByteBuffer rdram, ByteBuffer tmem, int maxTextureUnits, boolean ARB_multitexture) {
        this.gl = gl;
        this.rdram = rdram;
        this.tmem = tmem;
        this.maxTextureUnits = maxTextureUnits;
        this.ARB_multitexture = ARB_multitexture;
        ImageFormat.settMem(tmem);
        this.rdramSize = rdram.capacity();
        ByteBuffer dummyTexture = ByteBuffer.allocateDirect(16 * 4);

        current[0] = null;
        current[1] = null;
        stack.init(gl);
        cachedBytes = 0;

        gl.glGenTextures(32, glNoiseNames, 0);

        ByteBuffer noise = ByteBuffer.allocateDirect(64 * 64 * 4);
        for (int i = 0; i < 32; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, glNoiseNames[i]);

            Random rand = new Random();

            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 64; x++) {
                    byte random = (byte) (rand.nextInt() & 0xFF);
                    noise.put(y * 64 * 4 + x * 4, random);
                    noise.put(y * 64 * 4 + x * 4 + 1, random);
                    noise.put(y * 64 * 4 + x * 4 + 2, random);
                    noise.put(y * 64 * 4 + x * 4 + 3, random);
                }
            }
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, 64, 64, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, noise);
        }

        dummy = CachedTexture.getDummy();
        prune();
        stack.addTop(dummy);

        gl.glBindTexture(GL.GL_TEXTURE_2D, dummy.glName[0]);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, 2, 2, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, dummyTexture);

        cachedBytes = dummy.textureBytes;

        crc32 = new Adler32();

        for (int i = 0; i < maxTextureUnits; i++)
            activateDummy(i);

        crcBuildTable();
    }

    public float getTexS(int t, float s) {
        return (s * current[t].shiftScaleS * texture.scales - textureTile[0].fuls + current[t].offsetS) * current[t].scaleS;
    }

    public float getTexT(int t, float s) {
        return (s * current[t].shiftScaleT * texture.scalet - textureTile[t].fult + current[t].offsetT) * current[t].scaleT;
    }

    public void activateDummy(int t) {
        if (ARB_multitexture)
            gl.glActiveTexture(GL.GL_TEXTURE0 + t);
        gl.glBindTexture(GL.GL_TEXTURE_2D, dummy.glName[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    }

    public void setTexture(float sc, float tc, int level, int tile, int on) {
        texture.scales = sc;
        texture.scalet = tc;
        if (texture.scales == 0.0f)
            texture.scales = 1.0f;
        if (texture.scalet == 0.0f)
            texture.scalet = 1.0f;
        texture.level = level;
        texture.on = on;
        texture.tile = tile;
        textureTile[0] = tiles[tile];
        textureTile[1] = tiles[(tile < 7) ? (tile + 1) : tile];
    }

    public void setTextureImage(int format, int size, int width, int address) {
        textureImage.format = format;
        textureImage.size = size;
        textureImage.width = width;
        textureImage.address = address;
        textureImage.bpl = textureImage.width << textureImage.size >> 1;
    }

    public void setTextureTile(float lrs, float lrt, int tile, float s, float t, float dsdx, float dtdy) {
        textureTile[0] = tiles[tile];
        textureTile[1] = tiles[tile < 7 ? tile + 1 : tile];

        if (textureMode == TEXTUREMODE_NORMAL)
            textureMode = TEXTUREMODE_TEXRECT;

        texRect.width = (int) (StrictMath.max(lrs, s) + dsdx);
        texRect.height = (int) (StrictMath.max(lrt, t) + dtdy);
    }

    public void resetTextureTile() {
        textureTile[0] = tiles[texture.tile];
        textureTile[1] = tiles[texture.tile < 7 ? texture.tile + 1 : texture.tile];
    }

    public void setTile(int format, int size, int line, int tmem, int tile, int palette, int cmt, int cms, int maskt, int masks, int shiftt, int shifts) {
        if (((size == G_IM_SIZ_4b) || (size == G_IM_SIZ_8b)) && (format == G_IM_FMT_RGBA))
            format = G_IM_FMT_CI;

        tiles[tile].format = format;
        tiles[tile].size = size;
        tiles[tile].line = line;
        tiles[tile].tmem = tmem;
        tiles[tile].palette = palette;
        tiles[tile].setCmt(cmt);
        tiles[tile].setCms(cms);
        tiles[tile].maskt = maskt;
        tiles[tile].masks = masks;
        tiles[tile].shiftt = shiftt;
        tiles[tile].shifts = shifts;
        if (tiles[tile].masks == 0)
            tiles[tile].clamps = 1;
        if (tiles[tile].maskt == 0)
            tiles[tile].clampt = 1;
    }

    public void setTileSize(int tile, int uls, int ult, int lrs, int lrt, float fuls, float fult, float flrs, float flrt) {
        tiles[tile].uls = uls;
        tiles[tile].ult = ult;
        tiles[tile].lrs = lrs;
        tiles[tile].lrt = lrt;
        tiles[tile].fuls = fuls;
        tiles[tile].fult = fult;
        tiles[tile].flrs = flrs;
        tiles[tile].flrt = flrt;
    }

    public void loadTile(int tile) {
        InterleaveFunc Interleave;
        int line;

        loadTile = tiles[tile];

        if (loadTile.line == 0)
            return;

        int address = textureImage.address + loadTile.ult * textureImage.bpl + (loadTile.uls << textureImage.size >> 1);
        int dest = loadTile.tmem << 3;
        int bpl = (loadTile.lrs - loadTile.uls + 1) << loadTile.size >> 1;
        int height = loadTile.lrt - loadTile.ult + 1;
        int src = address;

        if (((address + height * bpl) > rdramSize) || (((loadTile.tmem << 3) + bpl * height) > 4096))
            return;

        byte[] rdramArray = rdram.array();
        byte[] tmemArray = tmem.array();

        if (loadTile.size == G_IM_SIZ_32b) {
            line = loadTile.line << 1;
            Interleave = QWordInterleave;
        } else {
            line = loadTile.line;
            Interleave = DWordInterleave;
        }

        int bplTex = textureImage.bpl;
        int lineBytes = line << 3;
        for (int y = 0; y < height; y++) {
            System.arraycopy(rdramArray, src, tmemArray, dest, bpl);
            if ((y & 1) != 0) {
                Interleave.Interleave(tmemArray, dest, line);
            }

            src += bplTex;
            dest += lineBytes;
        }

        textureMode = TEXTUREMODE_NORMAL;
        loadType = LOADTYPE_TILE;
        changed |= CHANGED_TMEM;
    }

    public void loadBlock(int tile, int uls, int ult, int lrs, int dxt) {
        loadTile = tiles[tile];

        int bytes = (lrs + 1) << loadTile.size >>> 1;
        int address = textureImage.address + ult * textureImage.bpl + (uls << textureImage.size >>> 1);

        if ((bytes == 0) ||
                ((address + bytes) > rdramSize) ||
                (((loadTile.tmem << 3) + bytes) > 4096)) {
            return;
        }

        int src = address;
        int dest = loadTile.tmem << 3;

        byte[] rdramArray = rdram.array();
        byte[] tmemArray = tmem.array();

        if (dxt > 0) {
            InterleaveFunc Interleave;

            int line = (2047 + dxt) / dxt;
            int bpl = line << 3;
            int height = bytes / bpl;

            if (loadTile.size == G_IM_SIZ_32b)
                Interleave = QWordInterleave;
            else
                Interleave = DWordInterleave;

            for (int y = 0; y < height; y++) {
                System.arraycopy(rdramArray, src, tmemArray, dest, bpl);
                if ((y & 1) != 0) {
                    Interleave.Interleave(tmemArray, dest, line);
                }

                src += bpl;
                dest += bpl;
            }
        } else {
            System.arraycopy(rdramArray, src, tmemArray, dest, bytes);
        }

        textureMode = TEXTUREMODE_NORMAL;
        loadType = LOADTYPE_BLOCK;
        changed |= CHANGED_TMEM;
    }

    public void loadLUT(int tile) {
        int count = ((tiles[tile].lrs - tiles[tile].uls + 1) * (tiles[tile].lrt - tiles[tile].ult + 1)) & 0xFFFF;
        int address = textureImage.address + tiles[tile].ult * textureImage.bpl + (tiles[tile].uls << textureImage.size >>> 1);
        int dest = tiles[tile].tmem * 8;
        int pal = ((tiles[tile].tmem - 256) >>> 4) & 0xFFFF;

        tmem.position(0);
        ByteBuffer tmemPtr = tmem.slice();
        paletteCRC16.position(0);

        int i = 0;
        while (i < count) {
            for (int j = 0; (j < 16) && (i < count); j++, i++) {
                short color = rdram.getShort(address + (i * 2));
                tmem.putShort(dest, color);
                dest += 8;
            }

            tmemPtr.position((256 + (pal << 4)) * 8);
            paletteCRC16.asIntBuffer().put(pal, crcCalculatePalette(0xFFFFFFFF, tmemPtr.slice(), 16));
            pal++;
        }

        crc32.reset();
        crc32.update(paletteCRC16.array(), 0, 64);
        paletteCRC256.asIntBuffer().put(0, (int) crc32.getValue());
        changed |= CHANGED_TMEM;
    }

    private void prune() {
        while (cachedBytes > maxBytes) {
            if (stack.bottom != dummy) {
                cachedBytes -= stack.bottom.textureBytes;
                stack.removeBottom();
            } else if (dummy.higher != null) {
                stack.remove(dummy.higher);
                cachedBytes -= dummy.higher.textureBytes;
            }
        }
    }

    private void activateTexture(int t, CachedTexture texture, boolean linear) {
        if (ARB_multitexture)
            gl.glActiveTexture(GL.GL_TEXTURE0 + t);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.glName[0]);
        if (linear) {
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        } else {
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        }
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, texture.clampS != 0 ? GL.GL_CLAMP_TO_EDGE : GL.GL_REPEAT);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, texture.clampT != 0 ? GL.GL_CLAMP_TO_EDGE : GL.GL_REPEAT);
        stack.moveToTop(texture);
        current[t] = texture;
    }

    public void update(boolean IA16, int t, float scaleX, float scaleY, boolean linear) {
        int maxTexels;
        int tileWidth;
        int maskWidth;
        int loadWidth;
        int lineWidth;
        int clampWidth;
        int height;
        int tileHeight;
        int maskHeight;
        int loadHeight;
        int lineHeight;
        int clampHeight;
        int width;

        if (textureMode == TEXTUREMODE_BGIMAGE) {
            updateBackground(IA16, linear);
            return;
        }

        maxTexels = ImageFormat.imageFormat[textureTile[t].size][textureTile[t].format].maxTexels;

        tileWidth = textureTile[t].lrs - textureTile[t].uls + 1;
        tileHeight = textureTile[t].lrt - textureTile[t].ult + 1;

        maskWidth = 1 << textureTile[t].masks;
        maskHeight = 1 << textureTile[t].maskt;

        loadWidth = loadTile.lrs - loadTile.uls + 1;
        loadHeight = loadTile.lrt - loadTile.ult + 1;

        lineWidth = textureTile[t].line << ImageFormat.imageFormat[textureTile[t].size][textureTile[t].format].lineShift;

        if (lineWidth != 0)
            lineHeight = StrictMath.min(maxTexels / lineWidth, tileHeight);
        else
            lineHeight = 0;

        if (textureMode == TEXTUREMODE_TEXRECT) {
            int texRectWidth = texRect.width - textureTile[t].uls;
            int texRectHeight = texRect.height - textureTile[t].ult;

            if ((textureTile[t].masks != 0) && ((maskWidth * maskHeight) <= maxTexels))
                width = maskWidth;
            else if ((tileWidth * tileHeight) <= maxTexels)
                width = tileWidth;
            else if ((tileWidth * texRectHeight) <= maxTexels)
                width = tileWidth;
            else if ((texRectWidth * tileHeight) <= maxTexels)
                width = texRect.width;
            else if ((texRectWidth * texRectHeight) <= maxTexels)
                width = texRect.width;
            else if (loadType == LOADTYPE_TILE)
                width = loadWidth;
            else
                width = lineWidth;

            if ((textureTile[t].maskt != 0) && ((maskWidth * maskHeight) <= maxTexels))
                height = maskHeight;
            else if ((tileWidth * tileHeight) <= maxTexels)
                height = tileHeight;
            else if ((tileWidth * texRectHeight) <= maxTexels)
                height = texRect.height;
            else if ((texRectWidth * tileHeight) <= maxTexels)
                height = tileHeight;
            else if ((texRectWidth * texRectHeight) <= maxTexels)
                height = texRect.height;
            else if (loadType == LOADTYPE_TILE)
                height = loadHeight;
            else
                height = lineHeight;

        } else {
            if ((textureTile[t].masks != 0) && ((maskWidth * maskHeight) <= maxTexels))
                width = maskWidth; // Use mask width if set and valid
            else if ((tileWidth * tileHeight) <= maxTexels)
                width = tileWidth; // else use tile width if valid
            else if (loadType == LOADTYPE_TILE)
                width = loadWidth; // else use load width if load done with LoadTile
            else
                width = lineWidth; // else use line-based width

            if ((textureTile[t].maskt != 0) && ((maskWidth * maskHeight) <= maxTexels))
                height = maskHeight;
            else if ((tileWidth * tileHeight) <= maxTexels)
                height = tileHeight;
            else if (loadType == LOADTYPE_TILE)
                height = loadHeight;
            else
                height = lineHeight;
        }

        clampWidth = textureTile[t].clamps != 0 ? tileWidth : width;
        clampHeight = textureTile[t].clampt != 0 ? tileHeight : height;

        if (clampWidth > 256)
            textureTile[t].clamps = 0;
        if (clampHeight > 256)
            textureTile[t].clampt = 0;

        // Make sure masking is valid
        if (maskWidth > width) {
            textureTile[t].masks = textureTile[t].powof(width);
            maskWidth = 1 << textureTile[t].masks;
        }

        if (maskHeight > height) {
            textureTile[t].maskt = textureTile[t].powof(height);
            maskHeight = 1 << textureTile[t].maskt;
        }

        int src = textureTile[t].tmem << 3;
        int bpl = width << textureTile[t].size >> 1;
        int lineBytes = textureTile[t].line << 3;
        crc32.reset();
        for (int y = 0; y < height; y++) {
            crc32.update(tmem.array(), src, bpl);
            src += lineBytes;
        }
        if (textureTile[t].format == G_IM_FMT_CI) {
            if (textureTile[t].size == G_IM_SIZ_4b) {
                crc32.update(paletteCRC16.array(), textureTile[t].palette << 2, 4);
            } else if (textureTile[t].size == G_IM_SIZ_8b) {
                crc32.update(paletteCRC256.array(), 0, 4);
            }
        }
        int crc = (int) crc32.getValue();

        CachedTexture tex = stack.top;
        while (tex != null) {
            if ((tex.crc == crc) &&
                    (tex.width == width) &&
                    (tex.height == height) &&
                    (tex.clampWidth == clampWidth) &&
                    (tex.clampHeight == clampHeight) &&
                    (tex.maskS == textureTile[t].masks) &&
                    (tex.maskT == textureTile[t].maskt) &&
                    (tex.mirrorS == textureTile[t].mirrors) &&
                    (tex.mirrorT == textureTile[t].mirrort) &&
                    (tex.clampS == textureTile[t].clamps) &&
                    (tex.clampT == textureTile[t].clampt) &&
                    (tex.format == textureTile[t].format) &&
                    (tex.size == textureTile[t].size)) {
                activateTexture(t, tex, linear);
                hits++;
                return;
            }

            tex = tex.lower;
        }

        misses++;

        // If multitexturing, set the appropriate texture
        if (ARB_multitexture)
            gl.glActiveTexture(GL.GL_TEXTURE0 + t);

        current[t] = new CachedTexture();
        prune();
        stack.addTop(current[t]);
        gl.glBindTexture(GL.GL_TEXTURE_2D, current[t].glName[0]);

        current[t].address = textureImage.address;
        current[t].crc = crc;
        current[t].format = textureTile[t].format;
        current[t].size = textureTile[t].size;
        current[t].width = width;
        current[t].height = height;
        current[t].clampWidth = clampWidth;
        current[t].clampHeight = clampHeight;
        current[t].palette = textureTile[t].palette;
        current[t].maskS = textureTile[t].masks;
        current[t].maskT = textureTile[t].maskt;
        current[t].mirrorS = textureTile[t].mirrors;
        current[t].mirrorT = textureTile[t].mirrort;
        current[t].clampS = textureTile[t].clamps;
        current[t].clampT = textureTile[t].clampt;
        current[t].line = textureTile[t].line;
        current[t].tMem = textureTile[t].tmem;
//        current[t].lastDList = Rsp.gsp.DList;
//        current[t].frameBufferTexture = false;
        if (current[t].clampS != 0)
            current[t].realWidth = current[t].pow2(clampWidth);
        else if (current[t].mirrorS != 0)
            current[t].realWidth = maskWidth << 1;
        else
            current[t].realWidth = current[t].pow2(width);
        if (current[t].clampT != 0)
            current[t].realHeight = current[t].pow2(clampHeight);
        else if (current[t].mirrorT != 0)
            current[t].realHeight = maskHeight << 1;
        else
            current[t].realHeight = current[t].pow2(height);
        current[t].scaleS = 1.0f / (float) (current[t].realWidth);
        current[t].scaleT = 1.0f / (float) (current[t].realHeight);
        current[t].shiftScaleS = 1.0f;
        current[t].shiftScaleT = 1.0f;
        current[t].offsetS = enable2xSaI ? 0.25f : 0.5f;
        current[t].offsetT = enable2xSaI ? 0.25f : 0.5f;
        if (textureTile[t].shifts > 10)
            current[t].shiftScaleS = (float) (1 << (16 - textureTile[t].shifts));
        else if (textureTile[t].shifts > 0)
            current[t].shiftScaleS /= (float) (1 << textureTile[t].shifts);
        if (textureTile[t].shiftt > 10)
            current[t].shiftScaleT = (float) (1 << (16 - textureTile[t].shiftt));
        else if (textureTile[t].shiftt > 0)
            current[t].shiftScaleT /= (float) (1 << textureTile[t].shiftt);

        current[t].load(IA16, textureBitDepth, tmem, gl);
        activateTexture(t, current[t], linear);
        cachedBytes += current[t].textureBytes;
    }

    private void updateBackground(boolean IA16, boolean linear) {
        // calculate bgImage crc
        int numBytes = bgImage.width * bgImage.height << bgImage.size >>> 1;
        crc32.reset();
        crc32.update(rdram.array(), bgImage.address, numBytes);
        if (bgImage.format == G_IM_FMT_CI) {
            if (bgImage.size == G_IM_SIZ_4b) {
                crc32.update(paletteCRC16.array(), bgImage.palette << 2, 4);
            } else if (bgImage.size == G_IM_SIZ_8b) {
                crc32.update(paletteCRC256.array(), 0, 4);
            }
        }
        int crc = (int) crc32.getValue();

        CachedTexture tex = stack.top;
        while (tex != null) {
            if ((tex.crc == crc) &&
                    (tex.width == bgImage.width) &&
                    (tex.height == bgImage.height) &&
                    (tex.format == bgImage.format) &&
                    (tex.size == bgImage.size)) {
                activateTexture(0, tex, linear);
                hits++;
                return;
            }

            tex = tex.lower;
        }

        misses++;

        // If multitexturing, set the appropriate texture
        if (ARB_multitexture)
            gl.glActiveTexture(GL.GL_TEXTURE0);

        current[0] = new CachedTexture();
        prune();
        stack.addTop(current[0]);
        gl.glBindTexture(GL.GL_TEXTURE_2D, current[0].glName[0]);

        current[0].address = bgImage.address;
        current[0].crc = crc;
        current[0].format = bgImage.format;
        current[0].size = bgImage.size;
        current[0].width = bgImage.width;
        current[0].height = bgImage.height;
        current[0].clampWidth = bgImage.width;
        current[0].clampHeight = bgImage.height;
        current[0].palette = bgImage.palette;
        current[0].maskS = 0;
        current[0].maskT = 0;
        current[0].mirrorS = 0;
        current[0].mirrorT = 0;
        current[0].clampS = 1;
        current[0].clampT = 1;
        current[0].line = 0;
        current[0].tMem = 0;
        current[0].realWidth = current[0].pow2(bgImage.width);
        current[0].realHeight = current[0].pow2(bgImage.height);
        current[0].scaleS = 1.0f / (float) (current[0].realWidth);
        current[0].scaleT = 1.0f / (float) (current[0].realHeight);
        current[0].shiftScaleS = 1.0f;
        current[0].shiftScaleT = 1.0f;

        current[0].loadBackground(IA16, textureBitDepth, rdram, gl, bgImage.width, bgImage.height, bgImage.size, bgImage.address);
        activateTexture(0, current[0], linear);
        cachedBytes += current[0].textureBytes;
    }

    // called by loadLUT
    private int crcCalculatePalette(int crc, ByteBuffer buffer, int count) {
        int p = 0;
        int orig = crc;

        while ((count--) != 0) {
            crc = (crc >>> 8) ^ crcTable[(crc & 0xFF) ^ (buffer.get(p++) & 0xFF)];
            crc = (crc >>> 8) ^ crcTable[(crc & 0xFF) ^ (buffer.get(p++) & 0xFF)];
            p += 6;
        }
        return crc ^ orig;
    }

    // called by init
    private void crcBuildTable() {
        int crc;

        for (int i = 0; i <= 255; i++) {
            crc = reflect(i, 8) << 24;
            for (int j = 0; j < 8; j++)
                crc = (crc << 1) ^ (((crc & (1 << 31)) != 0) ? CRC32_POLYNOMIAL : 0);

            crcTable[i] = reflect(crc, 32);
        }
    }

    private int reflect(int ref, int ch) {
        int value = 0;
        for (int i = 1; i < (ch + 1); i++) {
            if ((ref & 1) != 0)
                value |= (1 << (ch - i));
            ref = ref >> 1;
        }
        return value;
    }

}