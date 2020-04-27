package gln64j;

import com.sun.opengl.util.BufferUtil;
import me.hydos.J64.util.debug.Debug;
import gln64j.rdp.combiners.Combiners;
import gln64j.rdp.Gdp;
import gln64j.rdp.textures.TextureCache;

import java.nio.FloatBuffer;
import java.util.Random;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class OpenGlGdp {

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_GLVERTEX = 17 * SIZEOF_FLOAT;

    private static class GLSimpleVertex {
        public float x, y, z, w;

        public float[] color = new float[4]; // r,g,b,a
        public float[] secondaryColor = new float[4]; // r,g,b,a

        public float s0, t0, s1, t1;

        public float fog;
    }

    ;

    private static class GLVertex {
        public FloatBuffer vtx; // 4
        public FloatBuffer color; // 4
        public FloatBuffer secondaryColor; // 4
        public FloatBuffer tex0; // 2
        public FloatBuffer tex1; // 2
        public FloatBuffer fog; // 1

        public GLVertex() {
        }

        public GLVertex(float x, float y, float z, float w,
                        float r1, float g1, float b1, float a1,
                        float r2, float g2, float b2, float a2,
                        float s0, float t0, float s1, float t1, float f) {
            vtx = FloatBuffer.allocate(4);
            vtx.put(x);
            vtx.put(y);
            vtx.put(z);
            vtx.put(w);
            color = FloatBuffer.allocate(4);
            color.put(r1);
            color.put(g1);
            color.put(b1);
            color.put(a1);
            secondaryColor = FloatBuffer.allocate(4);
            secondaryColor.put(r2);
            secondaryColor.put(g2);
            secondaryColor.put(b2);
            secondaryColor.put(a2);
            tex0 = FloatBuffer.allocate(2);
            tex0.put(s0);
            tex0.put(t0);
            tex1 = FloatBuffer.allocate(2);
            tex1.put(s1);
            tex1.put(t1);
            fog = FloatBuffer.allocate(1);
            fog.put(f);
        }
    }

    public static GL gl;

    public static TextureCache cache = new TextureCache();
    public static Combiners combiners = new Combiners();
    public static int screenWidth;
    public static int screenHeight;

    public static GLAutoDrawable hDC;

    private static int numTriangles;
    private static GLVertex[] vertices = new GLVertex[256];
    private static FloatBuffer bigArray;
    private static float scaleX;
    private static float scaleY;
    private static float zDepth;
    private static int numVertices;
    private static float nearZ;
    private static float vTrans;
    private static float vScale;
    private static int width;
    private static int height;
    private static int heightOffset;
    private static boolean usePolygonStipple;
    private static byte[][][] stipplePattern = new byte[32][8][128];
    private static int lastStipple;
    private static final GLSimpleVertex rect0 = new GLSimpleVertex();
    private static final GLSimpleVertex rect1 = new GLSimpleVertex();
    private static int windowedWidth;
    private static int windowedHeight;

    public static void init(GL agl) {
        gl = agl;

        for (int i = 0; i < 256; i++)
            vertices[i] = new GLVertex();
        bigArray = BufferUtil.newFloatBuffer(256 * 17);
        for (int i = 0; i < 256; i++) {
            bigArray.position(i * 17);
            vertices[i].vtx = bigArray.slice();
            bigArray.position(i * 17 + 4);
            vertices[i].color = bigArray.slice();
            bigArray.position(i * 17 + 8);
            vertices[i].secondaryColor = bigArray.slice();
            bigArray.position(i * 17 + 12);
            vertices[i].tex0 = bigArray.slice();
            bigArray.position(i * 17 + 14);
            vertices[i].tex1 = bigArray.slice();
            bigArray.position(i * 17 + 16);
            vertices[i].fog = bigArray.slice();
        }

        if (Debug.DEBUG_OGL) System.out.println("GL_NV_register_combiners: " + Combiners.NV_register_combiners);
        Combiners.ARB_multitexture = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_ARB_multitexture");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_multitexture: " + Combiners.ARB_multitexture);
        if (Combiners.ARB_multitexture) {
            int[] maxTextureUnits_t = new int[1];
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, maxTextureUnits_t, 0);
            Combiners.maxTextureUnits = StrictMath.min(8, maxTextureUnits_t[0]); // The plugin only supports 8, and 4 is really enough
        }
        Combiners.EXT_fog_coord = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_EXT_fog_coord");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_fog_coord: " + Combiners.EXT_fog_coord);
        Combiners.EXT_secondary_color = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_EXT_secondary_color");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_secondary_color: " + Combiners.EXT_secondary_color);
        Combiners.ARB_texture_env_combine = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_ARB_texture_env_combine");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_texture_env_combine: " + Combiners.ARB_texture_env_combine);
        Combiners.ARB_texture_env_crossbar = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_ARB_texture_env_crossbar");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_texture_env_crossbar: " + Combiners.ARB_texture_env_crossbar);
        Combiners.EXT_texture_env_combine = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_EXT_texture_env_combine");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_texture_env_combine: " + Combiners.EXT_texture_env_combine);
        Combiners.ATI_texture_env_combine3 = gl.glGetString(GL.GL_EXTENSIONS).contains("GL_ATI_texture_env_combine3");
        if (Debug.DEBUG_OGL) System.out.println("GL_ATI_texture_env_combine3: " + Combiners.ATI_texture_env_combine3);
        if (Debug.DEBUG_OGL) System.out.println("GL_ATIX_texture_env_route: " + Combiners.ATIX_texture_env_route);
        if (Debug.DEBUG_OGL) System.out.println("GL_NV_texture_env_combine4: " + Combiners.NV_texture_env_combine4);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        bigArray.position(0);
        gl.glVertexPointer(4, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        bigArray.position(4);
        gl.glColorPointer(4, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
        gl.glEnableClientState(GL.GL_COLOR_ARRAY);
        if (Combiners.EXT_secondary_color) {
            bigArray.position(8);
            gl.glSecondaryColorPointerEXT(3, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            gl.glEnableClientState(GL.GL_SECONDARY_COLOR_ARRAY_EXT);
        }
        if (Combiners.ARB_multitexture) {
            gl.glClientActiveTexture(GL.GL_TEXTURE0);
            bigArray.position(12);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
            gl.glClientActiveTexture(GL.GL_TEXTURE1);
            bigArray.position(14);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        } else {
            bigArray.position(12);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
        }
        if (Combiners.EXT_fog_coord) {
            gl.glFogi(GL.GL_FOG_COORDINATE_SOURCE_EXT, GL.GL_FOG_COORDINATE_EXT);
            gl.glFogi(GL.GL_FOG_MODE, GL.GL_LINEAR);
            gl.glFogf(GL.GL_FOG_START, 0.0f);
            gl.glFogf(GL.GL_FOG_END, 255.0f);
            bigArray.position(16);
            gl.glFogCoordPointerEXT(GL.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            gl.glEnableClientState(GL.GL_FOG_COORDINATE_ARRAY_EXT);
        }
        gl.glPolygonOffset(-3.0f, -3.0f);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        Random rand = new Random();
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 128; k++) {
                    stipplePattern[i][j][k] = (byte) (((i > (rand.nextInt() >>> 10) ? 1 : 0) << 7) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 6) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 5) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 4) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 3) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 2) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0) << 1) |
                            ((i > (rand.nextInt() >>> 10) ? 1 : 0)));
                }
            }
        }
        usePolygonStipple = false;
        cache.init(gl, GLN64jPlugin.RDRAM, GLN64jPlugin.TMEM, Combiners.maxTextureUnits, Combiners.ARB_multitexture);
        combiners.init();
        OGL_UpdateScale();
    }

    public static void config() {
        cache.config(32 * 1048576, 1);
        windowedWidth = 640;
        windowedHeight = 480;
    }

    public static void OGL_ResizeWindow() {
        width = windowedWidth;
        height = windowedHeight;
    }

    public static void OGL_UpdateScale() {
        scaleX = width / (float) screenWidth;
        scaleY = height / (float) screenHeight;
    }

    public static void VI_UpdateSize() {
        float xScale = (GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_X_SCALE] & Gbi.SR_MASK_12) * Gbi.FIXED2FLOATRECIP10;

        float yScale = (GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_Y_SCALE] & Gbi.SR_MASK_12) * Gbi.FIXED2FLOATRECIP10;

        int hEnd = GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_H_START] & Gbi.SR_MASK_10;
        int hStart = (GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_H_START] >> 16) & Gbi.SR_MASK_10;

        int vEnd = (GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_V_START] >> 1) & Gbi.SR_MASK_9;
        int vStart = (GLN64jPlugin.REG.VI_Registers[GLN64jPlugin.REG.VI_V_START] >> 17) & Gbi.SR_MASK_9;

        screenWidth = (int) ((hEnd - hStart) * xScale);
        screenHeight = (int) ((vEnd - vStart) * yScale * 1.0126582f);

        if (screenWidth == 0)
            screenWidth = 320;
        if (screenHeight == 0)
            screenHeight = 240;
    }

    public static void viUpdateScreen() {
        if ((Rsp.gdp.changed & Gdp.CHANGED_COLORBUFFER) != 0) {
            hDC.swapBuffers();
            Rsp.gdp.changed &= ~Gdp.CHANGED_COLORBUFFER;
        }
    }

    public static void setView(float trans, float scale) {
        vTrans = trans;
        vScale = scale;
        nearZ = trans - scale;
        Rsp.gdp.changed |= Gdp.CHANGED_VIEWPORT;
    }

    public static void setZDepth(int z) {
        zDepth = StrictMath.min(1.0f, StrictMath.max(0.0f, ((z * Gbi.FIXED2FLOATRECIP15) - vTrans) / vScale));
    }

    public static void OGL_ClearDepthBuffer(boolean depthUpdate) {
        OpenGl.OGL_GspUpdateStates();

        OGL_GdpUpdateStates();
        gl.glDepthMask(true);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glDepthMask(depthUpdate);
    }

    public static void OGL_ClearColorBuffer(float[] color) {
        gl.glClearColor(color[0], color[1], color[2], color[3]);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public static void OGL_DrawRect(int ulx, int uly, int lrx, int lry, float[] color, int depthSource) {
        OpenGl.OGL_GspUpdateStates();

        OGL_GdpUpdateStates();

        boolean culling = gl.glIsEnabled(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_SCISSOR_TEST);
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, screenWidth, screenHeight, 0, 1.0f, -1.0f);
        gl.glViewport(0, heightOffset, width, height);
        if (Debug.WIREFRAME)
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); //TMP
        gl.glDepthRange(0.0f, 1.0f);
        gl.glColor4f(color[0], color[1], color[2], color[3]);

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex4f(ulx, uly, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        gl.glVertex4f(lrx, uly, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        gl.glVertex4f(lrx, lry, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        gl.glVertex4f(ulx, lry, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        gl.glEnd();

        gl.glLoadIdentity();

        if (culling)
            gl.glEnable(GL.GL_CULL_FACE);

        gl.glEnable(GL.GL_SCISSOR_TEST);
    }

    public static void OGL_DrawTexturedRect(float ulx, float uly, float lrx, float lry, float uls, float ult, float lrs, float lrt, boolean flip, int depthSource, int cycleType) {
        OpenGl.OGL_GspUpdateStates();

        OGL_GdpUpdateStates();

        rect0.x = ulx;
        rect0.y = uly;
        rect0.z = depthSource == Gbi.G_ZS_PRIM ? zDepth : nearZ;
        rect0.w = 1.0f;
        rect0.color[0] = 1.0f;
        rect0.color[1] = 1.0f;
        rect0.color[2] = 1.0f;
        rect0.color[3] = 0.0f;
        rect0.secondaryColor[0] = 1.0f;
        rect0.secondaryColor[1] = 1.0f;
        rect0.secondaryColor[2] = 1.0f;
        rect0.secondaryColor[3] = 1.0f;
        rect0.s0 = uls;
        rect0.t0 = ult;
        rect0.s1 = uls;
        rect0.t1 = ult;
        rect0.fog = 0.0f;

        rect1.x = lrx;
        rect1.y = lry;
        rect1.z = depthSource == Gbi.G_ZS_PRIM ? zDepth : nearZ;
        rect1.w = 1.0f;
        rect1.color[0] = 1.0f;
        rect1.color[1] = 1.0f;
        rect1.color[2] = 1.0f;
        rect1.color[3] = 0.0f;
        rect1.secondaryColor[0] = 1.0f;
        rect1.secondaryColor[1] = 1.0f;
        rect1.secondaryColor[2] = 1.0f;
        rect1.secondaryColor[3] = 1.0f;
        rect1.s0 = lrs;
        rect1.t0 = lrt;
        rect1.s1 = lrs;
        rect1.t1 = lrt;
        rect1.fog = 0.0f;

        boolean culling = gl.glIsEnabled(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, screenWidth, screenHeight, 0, 1.0f, -1.0f); // left, right, bottom, top, near, far
        gl.glViewport(0, heightOffset, width, height); // x, y, width, height
        if (Debug.WIREFRAME)
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE); //TMP

        if (combiners.usesT0) {
            rect0.s0 = rect0.s0 * cache.current[0].shiftScaleS - cache.textureTile[0].fuls;
            rect0.t0 = rect0.t0 * cache.current[0].shiftScaleT - cache.textureTile[0].fult;
            rect1.s0 = (rect1.s0 + 1.0f) * cache.current[0].shiftScaleS - cache.textureTile[0].fuls;
            rect1.t0 = (rect1.t0 + 1.0f) * cache.current[0].shiftScaleT - cache.textureTile[0].fult;

            if ((cache.current[0].maskS != 0) && (StrictMath.IEEEremainder(rect0.s0, cache.current[0].width) == 0.0f) && (cache.current[0].mirrorS == 0)) {
                rect1.s0 -= rect0.s0;
                rect0.s0 = 0.0f;
            }
            if ((cache.current[0].maskT != 0) && (StrictMath.IEEEremainder(rect0.t0, cache.current[0].height) == 0.0f) && (cache.current[0].mirrorT == 0)) {
                rect1.t0 -= rect0.t0;
                rect0.t0 = 0.0f;
            }

            if (Combiners.ARB_multitexture)
                gl.glActiveTexture(GL.GL_TEXTURE0);

            if ((rect0.s0 >= 0.0f) && (rect1.s0 <= cache.current[0].width)) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            }
            if ((rect0.t0 >= 0.0f) && (rect1.t0 <= cache.current[0].height)) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            }

            rect0.s0 *= cache.current[0].scaleS;
            rect0.t0 *= cache.current[0].scaleT;
            rect1.s0 *= cache.current[0].scaleS;
            rect1.t0 *= cache.current[0].scaleT;
        }

        if (combiners.usesT1 && Combiners.ARB_multitexture) {
            rect0.s1 = rect0.s1 * cache.current[1].shiftScaleS - cache.textureTile[1].fuls;
            rect0.t1 = rect0.t1 * cache.current[1].shiftScaleT - cache.textureTile[1].fult;
            rect1.s1 = (rect1.s1 + 1.0f) * cache.current[1].shiftScaleS - cache.textureTile[1].fuls;
            rect1.t1 = (rect1.t1 + 1.0f) * cache.current[1].shiftScaleT - cache.textureTile[1].fult;

            if ((cache.current[1].maskS != 0) && (StrictMath.IEEEremainder(rect0.s1, cache.current[1].width) == 0.0f) && (cache.current[1].mirrorS == 0)) {
                rect1.s1 -= rect0.s1;
                rect0.s1 = 0.0f;
            }
            if ((cache.current[1].maskT != 0) && (StrictMath.IEEEremainder(rect0.t1, cache.current[1].height) == 0.0f) && (cache.current[1].mirrorT == 0)) {
                rect1.t1 -= rect0.t1;
                rect0.t1 = 0.0f;
            }

            gl.glActiveTexture(GL.GL_TEXTURE1);

            if ((rect0.s1 == 0.0f) && (rect1.s1 <= cache.current[1].width)) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
            }
            if ((rect0.t1 == 0.0f) && (rect1.t1 <= cache.current[1].height)) {
                gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
            }

            rect0.s1 *= cache.current[1].scaleS;
            rect0.t1 *= cache.current[1].scaleT;
            rect1.s1 *= cache.current[1].scaleS;
            rect1.t1 *= cache.current[1].scaleT;
        }

        if (cycleType == Gbi.G_CYC_COPY) {
            if (Combiners.ARB_multitexture)
                gl.glActiveTexture(GL.GL_TEXTURE0);

            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        }

        combiners.setConstant(rect0.color, combiners.vertex.color, combiners.vertex.alpha);

        if (Combiners.EXT_secondary_color)
            combiners.setConstant(rect0.secondaryColor, combiners.vertex.secondaryColor, combiners.vertex.alpha);

        gl.glBegin(GL.GL_QUADS);
        gl.glColor4f(rect0.color[0], rect0.color[1], rect0.color[2], rect0.color[3]);
        if (Combiners.EXT_secondary_color)
            gl.glSecondaryColor3fEXT(rect0.secondaryColor[0], rect0.secondaryColor[1], rect0.secondaryColor[2]);

        if (Combiners.ARB_multitexture) {
            gl.glMultiTexCoord2f(GL.GL_TEXTURE0, rect0.s0, rect0.t0);
            gl.glMultiTexCoord2f(GL.GL_TEXTURE1, rect0.s1, rect0.t1);
            gl.glVertex4f(rect0.x, rect0.y, rect0.z, 1.0f);

            gl.glMultiTexCoord2f(GL.GL_TEXTURE0, rect1.s0, rect0.t0);
            gl.glMultiTexCoord2f(GL.GL_TEXTURE1, rect1.s1, rect0.t1);
            gl.glVertex4f(rect1.x, rect0.y, rect0.z, 1.0f);

            gl.glMultiTexCoord2f(GL.GL_TEXTURE0, rect1.s0, rect1.t0);
            gl.glMultiTexCoord2f(GL.GL_TEXTURE1, rect1.s1, rect1.t1);
            gl.glVertex4f(rect1.x, rect1.y, rect0.z, 1.0f);

            gl.glMultiTexCoord2f(GL.GL_TEXTURE0, rect0.s0, rect1.t0);
            gl.glMultiTexCoord2f(GL.GL_TEXTURE1, rect0.s1, rect1.t1);
            gl.glVertex4f(rect0.x, rect1.y, rect0.z, 1.0f);
        } else {
            gl.glTexCoord2f(rect0.s0, rect0.t0);
            gl.glVertex4f(rect0.x, rect0.y, rect0.z, 1.0f);

            if (flip)
                gl.glTexCoord2f(rect0.s0, rect1.t0);
            else
                gl.glTexCoord2f(rect1.s0, rect0.t0);
            gl.glVertex4f(rect1.x, rect0.y, rect0.z, 1.0f);

            gl.glTexCoord2f(rect1.s0, rect1.t0);
            gl.glVertex4f(rect1.x, rect1.y, rect0.z, 1.0f);

            if (flip)
                gl.glTexCoord2f(rect1.s0, rect0.t0);
            else
                gl.glTexCoord2f(rect0.s0, rect1.t0);
            gl.glVertex4f(rect0.x, rect1.y, rect0.z, 1.0f);
        }
        gl.glEnd();

        gl.glLoadIdentity();

        if (culling)
            gl.glEnable(GL.GL_CULL_FACE);
    }

    public static void OGL_DrawLine(float[] vtx1, float[] c1, float[] vtx2, float[] c2, float width) {
        if (Rsp.gsp.changed != 0 || Rsp.gdp.changed != 0) {
            OpenGl.OGL_GspUpdateStates();
            OGL_GdpUpdateStates();
        }

        final int VTX = 0;
        final int CLR = 1;
        float[][][] v = {{vtx1, c1}, {vtx2, c2}};

        float[] color = new float[4];

        gl.glLineWidth(width * scaleX);

        gl.glBegin(GL.GL_LINES);
        for (int i = 0; i < 2; i++) {
            float[][] spvert = v[i];
            color[0] = spvert[CLR][0];
            color[1] = spvert[CLR][1];
            color[2] = spvert[CLR][2];
            color[3] = spvert[CLR][3];
            combiners.setConstant(color, combiners.vertex.color, combiners.vertex.alpha);
            gl.glColor4fv(color, 0);

            if (Combiners.EXT_secondary_color) {
                color[0] = spvert[CLR][0];
                color[1] = spvert[CLR][1];
                color[2] = spvert[CLR][2];
                color[3] = spvert[CLR][3];
                combiners.setConstant(color, combiners.vertex.secondaryColor, combiners.vertex.alpha);
                gl.glSecondaryColor3fvEXT(color, 0);
            }

            gl.glVertex4f(spvert[VTX][0], spvert[VTX][1], spvert[VTX][2], spvert[VTX][3]);
        }
        gl.glEnd();
    }

    public static void OGL_AddTriangle(float[] vtx1, float[] c1, float[] tex1, float[] vtx2, float[] c2, float[] tex2, float[] vtx3, float[] c3, float[] tex3) {
        if (Rsp.gsp.changed != 0 || Rsp.gdp.changed != 0) {
            OpenGl.OGL_GspUpdateStates();
            OGL_GdpUpdateStates();
        }

        final int VTX = 0;
        final int CLR = 1;
        final int TEX = 2;
        float[][][] v = {{vtx1, c1, tex1}, {vtx2, c2, tex2}, {vtx3, c3, tex3}};

        for (int i = 0; i < 3; i++) {
            float[][] spvert = v[i];
            GLVertex vertex = vertices[numVertices];
            vertex.vtx.put(0, spvert[VTX][0]);
            vertex.vtx.put(1, spvert[VTX][1]);
            vertex.vtx.put(2, Gdp.RDP_GETOM_Z_SOURCE_SEL(Rsp.gdp.otherMode) == Gbi.G_ZS_PRIM ? zDepth * spvert[VTX][3] : spvert[VTX][2]);
            vertex.vtx.put(3, spvert[VTX][3]);

            vertex.color.put(0, spvert[CLR][0]);
            vertex.color.put(1, spvert[CLR][1]);
            vertex.color.put(2, spvert[CLR][2]);
            vertex.color.put(3, spvert[CLR][3]);
            combiners.setConstant(vertex.color, combiners.vertex.color, combiners.vertex.alpha);

            if (Combiners.EXT_secondary_color) {
                vertex.secondaryColor.put(0, 0.0f);
                vertex.secondaryColor.put(0, 0.0f);
                vertex.secondaryColor.put(0, 0.0f);
                vertex.secondaryColor.put(0, 1.0f);
                combiners.setConstant(vertex.secondaryColor, combiners.vertex.secondaryColor, Combiners.ONE);
            }

            if (combiners.usesT0) {
                vertex.tex0.put(0, cache.getTexS(0, spvert[TEX][0]));
                vertex.tex0.put(1, cache.getTexT(0, spvert[TEX][1]));
            }
            if (combiners.usesT1) {
                vertex.tex1.put(0, cache.getTexS(1, spvert[TEX][0]));
                vertex.tex1.put(1, cache.getTexT(1, spvert[TEX][1]));
            }
            numVertices++;
        }

        numTriangles++;

        if (numVertices >= 255)
            OGL_DrawTriangles();
    }

    public static void OGL_DrawTriangles() {
        if (numTriangles < 1)
            return;
        stipple();
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, numVertices);
        numTriangles = numVertices = 0;
    }

    private static void stipple() {
        if (usePolygonStipple && (Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_DITHER) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
            lastStipple = (lastStipple + 1) & 0x7;
            gl.glPolygonStipple(stipplePattern[(int) (Combiners.envColor[3] * 255.0f) >> 3][lastStipple], 0);
        }
    }

    private static void OGL_GdpUpdateStates() {
        if ((Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0) {
            if (Gdp.RDP_GETOM_Z_COMPARE_EN(Rsp.gdp.otherMode) != 0)
                gl.glDepthFunc(GL.GL_LEQUAL);
            else
                gl.glDepthFunc(GL.GL_ALWAYS);
            gl.glDepthMask(Gdp.RDP_GETOM_Z_UPDATE_EN(Rsp.gdp.otherMode) != 0);
            if (Gdp.RDP_GETOM_Z_MODE(Rsp.gdp.otherMode) == Gbi.ZMODE_DEC)
                gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
            else {
                gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
            }
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_ALPHACOMPARE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0) {
            if ((Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_THRESHOLD) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
                gl.glEnable(GL.GL_ALPHA_TEST);

                gl.glAlphaFunc((Rsp.gdp.blendColor[3] > 0.0f) ? GL.GL_GEQUAL : GL.GL_GREATER, Rsp.gdp.blendColor[3]);
            }
            else if (Gdp.RDP_GETOM_CVG_TIMES_ALPHA(Rsp.gdp.otherMode) != 0) {
                gl.glEnable(GL.GL_ALPHA_TEST);

                gl.glAlphaFunc(GL.GL_GEQUAL, 0.5f);
            } else {
                gl.glDisable(GL.GL_ALPHA_TEST);
            }

            if (usePolygonStipple && (Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_DITHER) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0))
                gl.glEnable(GL.GL_POLYGON_STIPPLE);
            else
                gl.glDisable(GL.GL_POLYGON_STIPPLE);
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_SCISSOR) != 0) {
            gl.glScissor((int) (Rsp.gdp.scissor.ulx * scaleX), (int) ((screenHeight - Rsp.gdp.scissor.lry) * scaleY + heightOffset),
                    (int) ((Rsp.gdp.scissor.lrx - Rsp.gdp.scissor.ulx) * scaleX), (int) ((Rsp.gdp.scissor.lry - Rsp.gdp.scissor.uly) * scaleY));
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_COMBINE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_CYCLETYPE) != 0) {
            if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_COPY) {
                combiners.setCombine(gl, false, combiners.encodeCombineMode(
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_TEXEL0,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_TEXEL0,
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_TEXEL0,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_TEXEL0));
            } else if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_FILL) {
                combiners.setCombine(gl, false, combiners.encodeCombineMode(
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_SHADE,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_1,
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_SHADE,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_1));
            } else {
                combiners.setCombine(gl, Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_2CYCLE, combiners.combine.getMux());
            }
            Rsp.gdp.changed |= Gdp.CHANGED_COMBINE_COLORS;
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_COMBINE_COLORS) != 0) {
            combiners.updateCombineColors(gl);
            Rsp.gdp.changed &= ~Gdp.CHANGED_COMBINE_COLORS;
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_TEXTURE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_TILE) != 0 || (cache.changed & TextureCache.CHANGED_TMEM) != 0) {
            combiners.beginTextureUpdate(gl);

            if (combiners.usesT0) {
                cache.update(Gdp.RDP_GETOM_TLUT_TYPE(Rsp.gdp.otherMode) == Gbi.G_TT_IA16, 0, scaleX, scaleY, (Gdp.RDP_GETOM_SAMPLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_TF_BILERP) || (Gdp.RDP_GETOM_SAMPLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_TF_AVERAGE));

                Rsp.gdp.changed &= ~Gdp.CHANGED_TEXTURE;
                Rsp.gdp.changed &= ~Gdp.CHANGED_TILE;
                cache.changed &= ~TextureCache.CHANGED_TMEM;
            } else {
                cache.activateDummy(0);
            }

            if (combiners.usesT1) {
                cache.update(Gdp.RDP_GETOM_TLUT_TYPE(Rsp.gdp.otherMode) == Gbi.G_TT_IA16, 1, scaleX, scaleY, (Gdp.RDP_GETOM_SAMPLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_TF_BILERP) || (Gdp.RDP_GETOM_SAMPLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_TF_AVERAGE));

                Rsp.gdp.changed &= ~Gdp.CHANGED_TEXTURE;
                Rsp.gdp.changed &= ~Gdp.CHANGED_TILE;
                cache.changed &= ~TextureCache.CHANGED_TMEM;
            } else {
                cache.activateDummy(1);
            }

            combiners.endTextureUpdate(gl);
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_CYCLETYPE) != 0) {
            if ((Gdp.RDP_GETOM_FORCE_BLEND(Rsp.gdp.otherMode) != 0) &&
                    (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) != Gbi.G_CYC_COPY) &&
                    (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) != Gbi.G_CYC_FILL) &&
                    (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
                gl.glEnable(GL.GL_BLEND);

                switch (Rsp.gdp.otherMode.w1 >> 16) {
                    case 0x0448, 0x055A -> gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
                    case 0x0C08, 0x0F0A -> gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO);
                    case 0x0C18, 0x0C19, 0x0050, 0x0055 -> gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    case 0x0FA5, 0x5055 -> gl.glBlendFunc(GL.GL_ZERO, GL.GL_ONE);
                    default -> gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                }
            } else {
                gl.glDisable(GL.GL_BLEND);
            }

            if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_FILL) {
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                gl.glEnable(GL.GL_BLEND);
            }
        }

        cache.changed &= TextureCache.CHANGED_TMEM;
        Rsp.gdp.changed &= Gdp.CHANGED_TILE;
        Rsp.gdp.changed &= Gdp.CHANGED_TEXTURE;
    }

}
