package gln64j.rdp.textures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.media.opengl.GL;

public class ImageFormat {

    public GetTexelFunc Get16;
    public int glType16;
    public int glInternalFormat16;
    public GetTexelFunc Get32;
    public int glType32;
    public int glInternalFormat32;
    public int autoFormat;
    public int lineShift;
    public int maxTexels;

    private static ByteBuffer tmem;

    private ImageFormat(GetTexelFunc Get16, int glType16, int glInternalFormat16, GetTexelFunc Get32, int glType32, int glInternalFormat32, int autoFormat, int lineShift, int maxTexels) {
        this.Get16 = Get16;
        this.glType16 = glType16;
        this.glInternalFormat16 = glInternalFormat16;
        this.Get32 = Get32;
        this.glType32 = glType32;
        this.glInternalFormat32 = glInternalFormat32;
        this.autoFormat = autoFormat;
        this.lineShift = lineShift;
        this.maxTexels = maxTexels;
    }

    public static void settMem(ByteBuffer mem) {
        tmem = mem;
    }

    public static interface GetTexelFunc {
        public int GetTexel(ByteBuffer src, int x, int i, int palette);
    }

    public static final GetTexelFunc GetNone = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return 0x00000000;
        }
    };

    public static final GetTexelFunc GetCI4IA_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetCI4IA_RGBA4444");
            return 0x0000F0FF;
        }
    };

    public static final GetTexelFunc GetCI4IA_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            int color4B = src.get((x >> 1) ^ (i << 1)) & 0xFF;
            if ((x & 1) != 0)
                return IA88_RGBA8888(tmem.getShort((256 + (palette << 4) + (color4B & 0x0F)) * 8));
            else
                return IA88_RGBA8888(tmem.getShort((256 + (palette << 4) + (color4B >> 4)) * 8));
        }
    };

    public static final GetTexelFunc GetCI4RGBA_RGBA5551 = new GetTexelFunc() {
        public final int GetTexel(ByteBuffer src, int x, int i, int palette) {
            int color4B = src.get((x >> 1) ^ (i << 1)) & 0xFF;
            if ((x & 1) != 0)
                return RGBA5551_RGBA5551(tmem.getShort((256 + (palette << 4) + (color4B & 0x0F)) * 8)) & 0xFFFF;
            else
                return RGBA5551_RGBA5551(tmem.getShort((256 + (palette << 4) + (color4B >> 4)) * 8)) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetCI4RGBA_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetCI4RGBA_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetIA31_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetIA31_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetIA31_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            int color4B = src.get((x >> 1) ^ (i << 1)) & 0xFF;
            return IA31_RGBA4444(((x & 1) != 0) ? (byte) (color4B & 0x0F) : (byte) (color4B >> 4)) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetI4_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetI4_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetI4_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            int color4B = src.get((x >> 1) ^ (i << 1)) & 0xFF;
            return I4_RGBA4444(((x & 1) != 0) ? (byte) (color4B & 0x0F) : (byte) (color4B >> 4)) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetCI8IA_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetCI8IA_RGBA4444");
            return 0x0000F0FF;
        }
    };

    public static final GetTexelFunc GetCI8IA_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetCI8IA_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetCI8RGBA_RGBA5551 = new GetTexelFunc() {
        public final int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return RGBA5551_RGBA5551(tmem.getShort((256 + (src.get(x ^ (i << 1)) & 0xFF)) * 8)) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetCI8RGBA_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetCI8RGBA_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetIA44_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetIA44_RGBA8888");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetIA44_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return IA44_RGBA4444(src.get(x ^ (i << 1))) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetI8_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return I8_RGBA8888(src.get(x ^ (i << 1)));
        }
    };

    public static final GetTexelFunc GetI8_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetI8_RGBA4444");
            return 0x0000F0FF;
        }
    };

    public static final GetTexelFunc GetRGBA5551_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetRGBA5551_RGBA8888 ");
            return 0xFF00FFFF;
        }
    };

    public static final GetTexelFunc GetRGBA5551_RGBA5551 = new GetTexelFunc() {
        public final int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return RGBA5551_RGBA5551(src.asShortBuffer().get(x ^ i)) & 0xFFFF;
        }
    };

    public static final GetTexelFunc GetIA88_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            return IA88_RGBA8888(src.asShortBuffer().get(x ^ i));
        }
    };

    public static final GetTexelFunc GetIA88_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetIA88_RGBA4444");
            return 0x0000F0FF;
        }
    };

    public static final GetTexelFunc GetRGBA8888_RGBA8888 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            src.order(ByteOrder.LITTLE_ENDIAN);
            return src.asIntBuffer().get(x ^ i);
        }
    };

    public static final GetTexelFunc GetRGBA8888_RGBA4444 = new GetTexelFunc() {
        public int GetTexel(ByteBuffer src, int x, int i, int palette) {
            System.out.println("GetRGBA8888_RGBA4444");
            return 0x0000F0FF;
        }
    };

    public static ImageFormat[][] imageFormat =
            { //                            Get16			glType16			glInternalFormat16	Get32			glType32	glInternalFormat32	autoFormat
                    { // 4-bit
                            new ImageFormat(GetCI4RGBA_RGBA5551, GL.GL_UNSIGNED_SHORT_5_5_5_1, GL.GL_RGB5_A1, GetCI4RGBA_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGB5_A1, 4, 4096), // CI (Banjo-Kazooie uses this, doesn't make sense, but it works...)
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 4, 8192), // YUV
                            new ImageFormat(GetCI4RGBA_RGBA5551, GL.GL_UNSIGNED_SHORT_5_5_5_1, GL.GL_RGB5_A1, GetCI4RGBA_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGB5_A1, 4, 4096), // CI
                            new ImageFormat(GetIA31_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetIA31_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 4, 8192), // IA
                            new ImageFormat(GetI4_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetI4_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 4, 8192), // I
                    },
                    { // 8-bit
                            new ImageFormat(GetCI8RGBA_RGBA5551, GL.GL_UNSIGNED_SHORT_5_5_5_1, GL.GL_RGB5_A1, GetCI8RGBA_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGB5_A1, 3, 2048), // RGBA
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 4096), // YUV
                            new ImageFormat(GetCI8RGBA_RGBA5551, GL.GL_UNSIGNED_SHORT_5_5_5_1, GL.GL_RGB5_A1, GetCI8RGBA_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGB5_A1, 3, 2048), // CI
                            new ImageFormat(GetIA44_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetIA44_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 3, 4096), // IA
                            new ImageFormat(GetI8_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetI8_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA8, 3, 4096), // I
                    },
                    { // 16-bit
                            new ImageFormat(GetRGBA5551_RGBA5551, GL.GL_UNSIGNED_SHORT_5_5_5_1, GL.GL_RGB5_A1, GetRGBA5551_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGB5_A1, 2, 2048), // RGBA
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 2, 2048), // YUV
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 2048), // CI
                            new ImageFormat(GetIA88_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetIA88_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA8, 2, 2048), // IA
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 2048), // I
                    },
                    { // 32-bit
                            new ImageFormat(GetRGBA8888_RGBA4444, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetRGBA8888_RGBA8888, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA8, 2, 1024), // RGBA
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 1024), // YUV
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 1024), // CI
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 1024), // IA
                            new ImageFormat(GetNone, GL.GL_UNSIGNED_SHORT_4_4_4_4, GL.GL_RGBA4, GetNone, GL.GL_UNSIGNED_BYTE, GL.GL_RGBA8, GL.GL_RGBA4, 0, 1024), // I
                    }
            };


    private static final int[] Three2Four = {
            0, // 000 = 0000
            2, // 001 = 0010
            4, // 010 = 0100
            6, // 011 = 0110
            9, // 100 = 1001
            11, // 101 = 1011
            13, // 110 = 1101
            15, // 111 = 1111
    };

    private static final int[] One2Four = {
            0, // 0 = 0000
            15, // 1 = 1111
    };

    // Just swaps the word
    private static final short RGBA5551_RGBA5551(short color) {
        return color;
    }

    private static final int IA88_RGBA8888(short color) {
        int al = color & 0xFF;
        int ah = (color >> 8) & 0xFF;
        return (al << 24) | (ah << 16) | (ah << 8) | ah;
    }

    private static final short IA44_RGBA4444(byte color) {
        int cl = color & 0xFF;
        int al = cl;
        cl = (cl >> 4) & 0xFF;
        int ah = cl;
        cl = (cl << 4) & 0xFF;
        ah = ah | cl;
        return (short) ((ah << 8) | al);
    }

    private static final short IA31_RGBA4444(byte color) {
        int cl = color & 0xFF;
        int bl = cl;
        bl = (bl >> 1) & 0xFF;
        int ch = Three2Four[bl];
        int ah = ch;
        ch = (ch << 4) & 0xFF;
        ah |= ch;
        int al = ch;
        bl = cl;
        bl &= 1;
        ch = One2Four[bl];
        al |= ch;
        return (short) ((ah << 8) | al);
    }

    private static final int I8_RGBA8888(byte color) {
        int cl = color & 0xFF;
        return (cl << 24) | (cl << 16) | (cl << 8) | (cl);
    }

    private static final short I4_RGBA4444(byte color) {
        int cl = color & 0xFF;
        int al = cl;
        cl = (cl << 4) & 0xFF;
        al |= cl;
        int ah = al;
        return (short) (((ah & 0xFF) << 8) | (al & 0xFF));
    }
}
