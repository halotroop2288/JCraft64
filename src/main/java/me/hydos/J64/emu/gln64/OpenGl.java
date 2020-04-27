package me.hydos.J64.emu.gln64;

import me.hydos.J64.emu.gln64.rsp.Gsp;
import org.lwjgl.opengl.GL30;

import javax.swing.JFrame;

public class OpenGl {
    
    private static class OGL_EventListener{

        public void init() {
            Rsp.gsp.changed = Rsp.gdp.changed = 0xFFFFFFFF;
        }
        
        public void display() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    };
    public static int uc_start;
    public static int uc_dstart;

    private static boolean fog;

    public static void config() {
        fog = true; //TODO: add this if fog is on program?
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
