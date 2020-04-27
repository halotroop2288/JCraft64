package gln64j;

import gln64j.rsp.Gsp;

import java.nio.FloatBuffer;
import javax.swing.JFrame;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

public class OpenGl {

    private static class GLVertex {
        public FloatBuffer vtx; // 4
        public FloatBuffer color; // 4
        public FloatBuffer secondaryColor; // 4
        public FloatBuffer tex0; // 2
        public FloatBuffer tex1; // 2
        public FloatBuffer fog; // 1
        
        public GLVertex() { }
        
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
    };
    
    private static class OGL_EventListener implements GLEventListener {

        public void init(GLAutoDrawable gLDrawable) {
            gl = gLDrawable.getGL();
            OpenGlGdp.init(gl);
            
            Rsp.gsp.changed = Rsp.gdp.changed = 0xFFFFFFFF;
        }
        
        public void display(GLAutoDrawable gLDrawable) {
            try {
                OpenGlGdp.VI_UpdateSize();
                OpenGlGdp.OGL_UpdateScale();

                Rsp.gdp.gDPSetAlphaCompare(Gbi.G_AC_NONE); // 0
                Rsp.gdp.gDPSetDepthSource(Gbi.G_ZS_PIXEL); // 0

                Rsp.gdp.gDPSetRenderMode(0, 0);

                Rsp.gdp.gDPSetAlphaDither(Gbi.G_AD_DISABLE); // 3
                Rsp.gdp.gDPSetColorDither(Gbi.G_CD_DISABLE); // 3
                Rsp.gdp.gDPSetCombineKey(Gbi.G_CK_NONE); // 0
                Rsp.gdp.gDPSetTextureConvert(Gbi.G_TC_FILT); // 6
                Rsp.gdp.gDPSetTextureFilter(Gbi.G_TF_POINT); // 0
                Rsp.gdp.gDPSetTextureLUT(Gbi.G_TT_NONE); // 0
                Rsp.gdp.gDPSetTextureLOD(Gbi.G_TL_TILE); // 0
                Rsp.gdp.gDPSetTextureDetail(Gbi.G_TD_CLAMP); // 0
                Rsp.gdp.gDPSetTexturePersp(Gbi.G_TP_PERSP); // 1
                Rsp.gdp.gDPSetCycleType(Gbi.G_CYC_1CYCLE); // 0
                Rsp.gdp.gDPPipelineMode(Gbi.G_PM_NPRIMITIVE); // 0

                Rsp.gsp.loadUcode(uc_start, uc_dstart);
                Rsp.gsp.RSP_ProcessDList();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public void reshape(GLAutoDrawable gLDrawable, int i, int i0, int i1, int i2) {
        }
        
        public void displayChanged(GLAutoDrawable gLDrawable, boolean b, boolean b0) {
        }
        
    };
    
    public static GL gl;
    
    public static int uc_start;
    public static int uc_dstart;

    private static boolean fog;

    /** Creates a new instance of OpenGl */
    private OpenGl() {
    }
    
    public static void config() {
        fog = false; //TODO: add this if fog is on program?
        OpenGlGdp.config();
    }
    
    public static void OGL_Stop() {
    }
    
    public static void OGL_Start() {
        int displayWidth = 640;
        int displayHeight = 480;
        JFrame window = GLN64jPlugin.hWnd;
        GLCanvas canvas = new GLCanvas();
        canvas.setIgnoreRepaint(true);
        canvas.setAutoSwapBufferMode(false);
        canvas.addGLEventListener(new OGL_EventListener());
        window.getContentPane().removeAll();
        window.getContentPane().add(canvas);
        canvas.setSize(displayWidth, displayHeight);
            window.pack();
        while (!canvas.isDisplayable()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
        gl = canvas.getGL();
        OpenGlGdp.hDC = canvas;
        OpenGlGdp.hDC.swapBuffers(); //TMP
    }

    private static void OGL_UpdateCullFace() {
        if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BOTH)!=0) {
            gl.glEnable(GL.GL_CULL_FACE);
            
            if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BACK)!=0)
                gl.glCullFace(GL.GL_BACK);
            else
                gl.glCullFace(GL.GL_FRONT);
        } else
            gl.glDisable(GL.GL_CULL_FACE);
    }
    
    public static void OGL_GspUpdateStates() {
        if ((Rsp.gsp.changed & Gsp.CHANGED_GEOMETRYMODE) != 0) {
            OGL_UpdateCullFace();
                gl.glDisable(GL.GL_FOG);
            Rsp.gsp.changed &= ~Gsp.CHANGED_GEOMETRYMODE;
        }
        
        if ((Rsp.gsp.geometryMode & Gbi.G_ZBUFFER) != 0)
            gl.glEnable(GL.GL_DEPTH_TEST);
        else
            gl.glDisable(GL.GL_DEPTH_TEST);
        
        Rsp.gsp.changed &= Gsp.CHANGED_MATRIX;
    }
    
}
