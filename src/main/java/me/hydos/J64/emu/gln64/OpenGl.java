package me.hydos.J64.emu.gln64;

import me.hydos.J64.emu.gln64.rsp.Gsp;
import org.lwjgl.opengl.GL30;

public class OpenGl {
    
    public static int uc_start;
    public static int uc_dstart;

    public static void config() {
        OpenGlGdp.config();
    }
    
    public static void OGL_Stop() {
    }
    
    public static void OGL_Start() {
    }

    private static void OGL_UpdateCullFace() {
        if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BOTH)!=0) {
            GL30.glEnable(GL30.GL_CULL_FACE);
            
            if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BACK)!=0)
                GL30.glCullFace(GL30.GL_BACK);
            else
                GL30.glCullFace(GL30.GL_FRONT);
        } else
            GL30.glDisable(GL30.GL_CULL_FACE);
    }
    
    public static void OGL_GspUpdateStates() {
        if ((Rsp.gsp.changed & Gsp.CHANGED_GEOMETRYMODE) != 0) {
            OGL_UpdateCullFace();
            GL30.glDisable(GL30.GL_FOG);
            Rsp.gsp.changed &= ~Gsp.CHANGED_GEOMETRYMODE;
        }
        
        if ((Rsp.gsp.geometryMode & Gbi.G_ZBUFFER) != 0)
            GL30.glEnable(GL30.GL_DEPTH_TEST);
        else
            GL30.glDisable(GL30.GL_DEPTH_TEST);
        
        Rsp.gsp.changed &= Gsp.CHANGED_MATRIX;
    }
    
}
