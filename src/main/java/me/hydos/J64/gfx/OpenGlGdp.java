package me.hydos.J64.gfx;

import com.mojang.blaze3d.systems.RenderSystem;
import me.hydos.J64.emu.util.debug.Debug;
import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rdp.combiners.Combiners;
import me.hydos.J64.gfx.rdp.textures.TextureCache;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

public class OpenGlGdp {

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_GLVERTEX = 17 * SIZEOF_FLOAT;

    private static final GLSimpleVertex rect0 = new GLSimpleVertex();
    private static final GLSimpleVertex rect1 = new GLSimpleVertex();
    
    private static class GLSimpleVertex {
        public float x, y, z, w;

        public float[] color = new float[4]; // r,g,b,a
        public float[] secondaryColor = new float[4]; // r,g,b,a

        public float s0, t0, s1, t1;

        public float fog;
    }

    private static class GLVertex {
        public FloatBuffer vtx; // 4
        public FloatBuffer color; // 4
        public FloatBuffer secondaryColor; // 4
        public FloatBuffer tex0; // 2
        public FloatBuffer tex1; // 2
        public FloatBuffer fog; // 1

        public GLVertex() {
        }

        public GLVertex(float x, float y, float z, float w, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2, float s0, float t0, float s1, float t1, float f) {
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

    public static TextureCache cache = new TextureCache();
    public static Combiners combiners = new Combiners();
    public static int screenWidth;
    public static int screenHeight;

//    public static GLAutoDrawable hDC;

    private static int numTriangles;
    private static final GLVertex[] vertices = new GLVertex[256];
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
    private static final byte[][][] stipplePattern = new byte[32][8][128];
    private static int lastStipple;
    private static int windowedWidth;
    private static int windowedHeight;

    public static void init() {
        for (int i = 0; i < 256; i++)
            vertices[i] = new GLVertex();
        bigArray = BufferUtils.createFloatBuffer(256 * 17);
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
        Combiners.ARB_multitexture = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_ARB_multitexture");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_multitexture: " + Combiners.ARB_multitexture);
        if (Combiners.ARB_multitexture) {
            int[] maxTextureUnits_t = new int[1];
            GL40.glGetIntegerv(GL40.GL_MAX_TEXTURE_UNITS, maxTextureUnits_t);//GL40.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, maxTextureUnits_t, 0);

            Combiners.maxTextureUnits = StrictMath.min(8, maxTextureUnits_t[0]); // The plugin only supports 8, and 4 is really enough
        }
        Combiners.EXT_fog_coord = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_EXT_fog_coord");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_fog_coord: " + Combiners.EXT_fog_coord);
        Combiners.EXT_secondary_color = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_EXT_secondary_color");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_secondary_color: " + Combiners.EXT_secondary_color);
        Combiners.ARB_texture_env_combine = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_ARB_texture_env_combine");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_texture_env_combine: " + Combiners.ARB_texture_env_combine);
        Combiners.ARB_texture_env_crossbar = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_ARB_texture_env_crossbar");
        if (Debug.DEBUG_OGL) System.out.println("GL_ARB_texture_env_crossbar: " + Combiners.ARB_texture_env_crossbar);
        Combiners.EXT_texture_env_combine = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_EXT_texture_env_combine");
        if (Debug.DEBUG_OGL) System.out.println("GL_EXT_texture_env_combine: " + Combiners.EXT_texture_env_combine);
        Combiners.ATI_texture_env_combine3 = GL40.glGetString(GL40.GL_EXTENSIONS).contains("GL_ATI_texture_env_combine3");
        if (Debug.DEBUG_OGL) System.out.println("GL_ATI_texture_env_combine3: " + Combiners.ATI_texture_env_combine3);
        if (Debug.DEBUG_OGL) System.out.println("GL_ATIX_texture_env_route: " + Combiners.ATIX_texture_env_route);
        if (Debug.DEBUG_OGL) System.out.println("GL_NV_texture_env_combine4: " + Combiners.NV_texture_env_combine4);

        GL40.glMatrixMode(GL40.GL_PROJECTION);
        GL40.glLoadIdentity();
        GL40.glMatrixMode(GL40.GL_MODELVIEW);
        GL40.glLoadIdentity();
        bigArray.position(0);
        GL40.glVertexPointer(4, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
        GL40.glEnableClientState(GL40.GL_VERTEX_ARRAY);
        bigArray.position(4);
        GL40.glColorPointer(4, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
        GL40.glEnableClientState(GL40.GL_COLOR_ARRAY);
        if (Combiners.EXT_secondary_color) {
            bigArray.position(8);
            GL40.glSecondaryColorPointer(3, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            GL40.glEnableClientState(GL40.GL_SECONDARY_COLOR_ARRAY);
        }
        if (Combiners.ARB_multitexture) {
            GL40.glClientActiveTexture(GL40.GL_TEXTURE0);
            bigArray.position(12);
            GL40.glTexCoordPointer(2, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            GL40.glEnableClientState(GL40.GL_TEXTURE_COORD_ARRAY);
            GL40.glClientActiveTexture(GL40.GL_TEXTURE1);
            bigArray.position(14);
            GL40.glTexCoordPointer(2, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            GL40.glEnableClientState(GL40.GL_TEXTURE_COORD_ARRAY);
        } else {
            bigArray.position(12);
            GL40.glTexCoordPointer(2, GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            GL40.glEnableClientState(GL40.GL_TEXTURE_COORD_ARRAY);
        }
        if (Combiners.EXT_fog_coord) {
            GL40.glFogi(GL40.GL_FOG_COORDINATE_SOURCE, GL40.GL_FOG_COORDINATE);
            GL40.glFogi(GL40.GL_FOG_MODE, GL40.GL_LINEAR);
            GL40.glFogf(GL40.GL_FOG_START, 0.0f);
            GL40.glFogf(GL40.GL_FOG_END, 255.0f);
            bigArray.position(16);
            GL40.glFogCoordPointer(GL40.GL_FLOAT, SIZEOF_GLVERTEX, bigArray.slice());
            GL40.glEnableClientState(GL40.GL_FOG_COORDINATE_ARRAY);
        }
        GL40.glPolygonOffset(-3.0f, -3.0f);
        GL40.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL40.glClear(GL40.GL_COLOR_BUFFER_BIT);

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
        cache.init(GLN64jPlugin.RDRAM, GLN64jPlugin.TMEM, Combiners.maxTextureUnits, Combiners.ARB_multitexture);
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
//            hDC.swapBuffers();
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
        GL40.glDepthMask(true);
        GL40.glClear(GL40.GL_DEPTH_BUFFER_BIT);
        GL40.glDepthMask(depthUpdate);
    }

    public static void OGL_ClearColorBuffer(float[] color) {
        GL40.glClearColor(color[0], color[1], color[2], color[3]);
        GL40.glClear(GL40.GL_COLOR_BUFFER_BIT);
    }

    public static void OGL_DrawRect(int ulx, int uly, int lrx, int lry, float[] color, int depthSource) {
        OpenGl.OGL_GspUpdateStates();

        OGL_GdpUpdateStates();

        boolean culling = GL40.glIsEnabled(GL40.GL_CULL_FACE);
        GL40.glDisable(GL40.GL_SCISSOR_TEST);
        GL40.glDisable(GL40.GL_CULL_FACE);
        RenderSystem.matrixMode(GL40.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0, screenWidth, screenHeight, 0, 1.0f, -1.0f);
        RenderSystem.viewport(0, 0, MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
        GL40.glDepthRange(0.0f, 1.0f);
        RenderSystem.color4f(color[0], color[1], color[2], color[3]);

        GL40.glBegin(GL40.GL_QUADS);
        GL40.glVertex4f(ulx, uly, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        GL40.glVertex4f(lrx, uly, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        GL40.glVertex4f(lrx, lry, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        GL40.glVertex4f(ulx, lry, (depthSource == Gbi.G_ZS_PRIM) ? zDepth : nearZ, 1.0f);
        GL40.glEnd();

        GL40.glLoadIdentity();

        if (culling) {
            GL40.glEnable(GL40.GL_CULL_FACE);
        } else {
            GL40.glDisable(GL40.GL_CULL_FACE);
        }

        GL40.glEnable(GL40.GL_SCISSOR_TEST);
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

        boolean culling = GL40.glIsEnabled(GL40.GL_CULL_FACE);
        GL40.glDisable(GL40.GL_CULL_FACE);
        RenderSystem.matrixMode(GL40.GL_PROJECTION);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0, screenWidth, screenHeight, 0, 1.0f, -1.0f); // left, right, bottom, top, near, far
        RenderSystem.viewport(0, 0, MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
        if (Debug.WIREFRAME)
            GL40.glPolygonMode(GL40.GL_FRONT_AND_BACK, GL40.GL_LINE); //TMP

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
                GL40.glActiveTexture(GL40.GL_TEXTURE0);

            if ((rect0.s0 >= 0.0f) && (rect1.s0 <= cache.current[0].width)) {
                GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_S, GL40.GL_CLAMP_TO_EDGE);
            }
            if ((rect0.t0 >= 0.0f) && (rect1.t0 <= cache.current[0].height)) {
                GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_T, GL40.GL_CLAMP_TO_EDGE);
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

            GL40.glActiveTexture(GL40.GL_TEXTURE1);

            if ((rect0.s1 == 0.0f) && (rect1.s1 <= cache.current[1].width)) {
                GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_S, GL40.GL_CLAMP_TO_EDGE);
            }
            if ((rect0.t1 == 0.0f) && (rect1.t1 <= cache.current[1].height)) {
                GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_WRAP_T, GL40.GL_CLAMP_TO_EDGE);
            }

            rect0.s1 *= cache.current[1].scaleS;
            rect0.t1 *= cache.current[1].scaleT;
            rect1.s1 *= cache.current[1].scaleS;
            rect1.t1 *= cache.current[1].scaleT;
        }

        if (cycleType == Gbi.G_CYC_COPY) {
            if (Combiners.ARB_multitexture)
                GL40.glActiveTexture(GL40.GL_TEXTURE0);

            GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_MIN_FILTER, GL40.GL_NEAREST);
            GL40.glTexParameteri(GL40.GL_TEXTURE_2D, GL40.GL_TEXTURE_MAG_FILTER, GL40.GL_NEAREST);
        }

        combiners.setConstant(rect0.color, combiners.vertex.color, combiners.vertex.alpha);

        if (Combiners.EXT_secondary_color)
            combiners.setConstant(rect0.secondaryColor, combiners.vertex.secondaryColor, combiners.vertex.alpha);

        GL40.glBegin(GL40.GL_QUADS);
        GL40.glColor4f(rect0.color[0], rect0.color[1], rect0.color[2], rect0.color[3]);
        if (Combiners.EXT_secondary_color)
            GL40.glSecondaryColor3f(rect0.secondaryColor[0], rect0.secondaryColor[1], rect0.secondaryColor[2]);

        if (Combiners.ARB_multitexture) {
            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE0, rect0.s0, rect0.t0);
            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE1, rect0.s1, rect0.t1);
            GL40.glVertex4f(rect0.x, rect0.y, rect0.z, 1.0f);

            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE0, rect1.s0, rect0.t0);
            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE1, rect1.s1, rect0.t1);
            GL40.glVertex4f(rect1.x, rect0.y, rect0.z, 1.0f);

            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE0, rect1.s0, rect1.t0);
            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE1, rect1.s1, rect1.t1);
            GL40.glVertex4f(rect1.x, rect1.y, rect0.z, 1.0f);

            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE0, rect0.s0, rect1.t0);
            GL40.glMultiTexCoord2f(GL40.GL_TEXTURE1, rect0.s1, rect1.t1);
            GL40.glVertex4f(rect0.x, rect1.y, rect0.z, 1.0f);
        } else {
            GL40.glTexCoord2f(rect0.s0, rect0.t0);
            GL40.glVertex4f(rect0.x, rect0.y, rect0.z, 1.0f);

            if (flip)
                GL40.glTexCoord2f(rect0.s0, rect1.t0);
            else
                GL40.glTexCoord2f(rect1.s0, rect0.t0);
            GL40.glVertex4f(rect1.x, rect0.y, rect0.z, 1.0f);

            GL40.glTexCoord2f(rect1.s0, rect1.t0);
            GL40.glVertex4f(rect1.x, rect1.y, rect0.z, 1.0f);

            if (flip)
                GL40.glTexCoord2f(rect1.s0, rect0.t0);
            else
                GL40.glTexCoord2f(rect0.s0, rect1.t0);
            GL40.glVertex4f(rect0.x, rect1.y, rect0.z, 1.0f);
        }
        GL40.glEnd();

        GL40.glLoadIdentity();

        if (culling)
            GL40.glEnable(GL40.GL_CULL_FACE);
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

        GL40.glLineWidth(width * scaleX);

        GL40.glBegin(GL40.GL_LINES);
        for (int i = 0; i < 2; i++) {
            float[][] spvert = v[i];
            color[0] = spvert[CLR][0];
            color[1] = spvert[CLR][1];
            color[2] = spvert[CLR][2];
            color[3] = spvert[CLR][3];
            combiners.setConstant(color, combiners.vertex.color, combiners.vertex.alpha);
            GL40.glColor4fv(color);

            if (Combiners.EXT_secondary_color) {
                color[0] = spvert[CLR][0];
                color[1] = spvert[CLR][1];
                color[2] = spvert[CLR][2];
                color[3] = spvert[CLR][3];
                combiners.setConstant(color, combiners.vertex.secondaryColor, combiners.vertex.alpha);
                GL40.glSecondaryColor3fv(color);
            }

            GL40.glVertex4f(spvert[VTX][0], spvert[VTX][1], spvert[VTX][2], spvert[VTX][3]);
        }
        GL40.glEnd();
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
        stippleTransparency();
        GL40.glDrawArrays(GL40.GL_TRIANGLES, 0, numVertices);
        numTriangles = numVertices = 0;
    }

    private static void stippleTransparency() {
        if (usePolygonStipple && (Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_DITHER) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
            lastStipple = (lastStipple + 1) & 0x7;
            GL40.glPolygonStipple(ByteBuffer.wrap(stipplePattern[(int) (Combiners.envColor[3] * 255.0f) >> 3][lastStipple]));//GL40.glPolygonStipple(stipplePattern[(int) (Combiners.envColor[3] * 255.0f) >> 3][lastStipple], 0);
        }
    }

    private static void OGL_GdpUpdateStates() {
        if ((Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0) {
            if (Gdp.RDP_GETOM_Z_COMPARE_EN(Rsp.gdp.otherMode) != 0)
                GL40.glDepthFunc(GL40.GL_LEQUAL);
            else
                GL40.glDepthFunc(GL40.GL_ALWAYS);
            GL40.glDepthMask(Gdp.RDP_GETOM_Z_UPDATE_EN(Rsp.gdp.otherMode) != 0);
            if (Gdp.RDP_GETOM_Z_MODE(Rsp.gdp.otherMode) == Gbi.ZMODE_DEC)
                GL40.glEnable(GL40.GL_POLYGON_OFFSET_FILL);
            else {
                GL40.glDisable(GL40.GL_POLYGON_OFFSET_FILL);
            }
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_ALPHACOMPARE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0) {
            if ((Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_THRESHOLD) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
                GL40.glEnable(GL40.GL_ALPHA_TEST);

                GL40.glAlphaFunc((Rsp.gdp.blendColor[3] > 0.0f) ? GL40.GL_GEQUAL : GL40.GL_GREATER, Rsp.gdp.blendColor[3]);
            } else if (Gdp.RDP_GETOM_CVG_TIMES_ALPHA(Rsp.gdp.otherMode) != 0) {
                GL40.glEnable(GL40.GL_ALPHA_TEST);

                GL40.glAlphaFunc(GL40.GL_GEQUAL, 0.5f);
            } else {
                GL40.glDisable(GL40.GL_ALPHA_TEST);
            }

            if (usePolygonStipple && (Gdp.RDP_GETOM_ALPHA_COMPARE_EN(Rsp.gdp.otherMode) == Gbi.G_AC_DITHER) && (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0))
                GL40.glEnable(GL40.GL_POLYGON_STIPPLE);
            else
                GL40.glDisable(GL40.GL_POLYGON_STIPPLE);
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_SCISSOR) != 0) {
            GL40.glScissor((int) (Rsp.gdp.scissor.ulx * scaleX), (int) ((screenHeight - Rsp.gdp.scissor.lry) * scaleY + heightOffset),
                    (int) ((Rsp.gdp.scissor.lrx - Rsp.gdp.scissor.ulx) * scaleX), (int) ((Rsp.gdp.scissor.lry - Rsp.gdp.scissor.uly) * scaleY));
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_COMBINE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_CYCLETYPE) != 0) {
            if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_COPY) {
                combiners.setCombine(false, combiners.encodeCombineMode(
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_TEXEL0,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_TEXEL0,
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_TEXEL0,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_TEXEL0));
            } else if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_FILL) {
                combiners.setCombine(false, combiners.encodeCombineMode(
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_SHADE,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_1,
                        Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_0, Combiners.G_CCMUX_SHADE,
                        Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_0, Combiners.G_ACMUX_1));
            } else {
                combiners.setCombine(Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_2CYCLE, combiners.combine.getMux());
            }
            Rsp.gdp.changed |= Gdp.CHANGED_COMBINE_COLORS;
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_COMBINE_COLORS) != 0) {
            combiners.updateCombineColors();
            Rsp.gdp.changed &= ~Gdp.CHANGED_COMBINE_COLORS;
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_TEXTURE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_TILE) != 0 || (cache.changed & TextureCache.CHANGED_TMEM) != 0) {
            combiners.beginTextureUpdate();

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

            combiners.endTextureUpdate();
        }

        if ((Rsp.gdp.changed & Gdp.CHANGED_RENDERMODE) != 0 || (Rsp.gdp.changed & Gdp.CHANGED_CYCLETYPE) != 0) {
            if ((Gdp.RDP_GETOM_FORCE_BLEND(Rsp.gdp.otherMode) != 0) &&
                    (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) != Gbi.G_CYC_COPY) &&
                    (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) != Gbi.G_CYC_FILL) &&
                    (Gdp.RDP_GETOM_ALPHA_CVG_SELECT(Rsp.gdp.otherMode) == 0)) {
                GL40.glEnable(GL40.GL_BLEND);

                switch (Rsp.gdp.otherMode.l >> 16) {
                    case 0x0448:
                    case 0x055A:
                        GL40.glBlendFunc(GL40.GL_ONE, GL40.GL_ONE);
                    case 0x0C08:
                    case 0x0F0A:
                        GL40.glBlendFunc(GL40.GL_ONE, GL40.GL_ZERO);
                    case 0x0C18:
                    case 0x0C19:
                    case 0x0050:
                    case 0x0055:
                        GL40.glBlendFunc(GL40.GL_SRC_ALPHA, GL40.GL_ONE_MINUS_SRC_ALPHA);
                    case 0x0FA5:
                    case 0x5055:
                        GL40.glBlendFunc(GL40.GL_ZERO, GL40.GL_ONE);
                    default:
                        GL40.glBlendFunc(GL40.GL_SRC_ALPHA, GL40.GL_ONE_MINUS_SRC_ALPHA);
                }
            } else {
                GL40.glDisable(GL40.GL_BLEND);
            }

            if (Gdp.RDP_GETOM_CYCLE_TYPE(Rsp.gdp.otherMode) == Gbi.G_CYC_FILL) {
                GL40.glBlendFunc(GL40.GL_SRC_ALPHA, GL40.GL_ONE_MINUS_SRC_ALPHA);
                GL40.glEnable(GL40.GL_BLEND);
            }
        }

        cache.changed &= TextureCache.CHANGED_TMEM;
        Rsp.gdp.changed &= Gdp.CHANGED_TILE;
        Rsp.gdp.changed &= Gdp.CHANGED_TEXTURE;
    }

    public static void setHeightOffset(int heightOffset) {
        OpenGlGdp.heightOffset = heightOffset;
    }

//    private static class GLSimpleVertex {
//        public float x, y, z, w;
//
//        public float[] color = new float[4]; // r,g,b,a
//        public float[] secondaryColor = new float[4]; // r,g,b,a
//
//        public float s0, t0, s1, t1;
//
//        public float fog;
//    }

//    private static class GLVertex {
//        public FloatBuffer vtx; // 4
//        public FloatBuffer color; // 4
//        public FloatBuffer secondaryColor; // 4
//        public FloatBuffer tex0; // 2
//        public FloatBuffer tex1; // 2
//        public FloatBuffer fog; // 1
//    }
}
