package me.hydos.J64.gfx;

import me.hydos.J64.gfx.rsp.Gsp;
import me.hydos.J64.gingerlite.GingerLite;
import org.lwjgl.opengl.GL40;

public class OpenGl {
    
    public static class OGL_EventListener{

        public void init() {
            OpenGlGdp.init();
            Rsp.gsp.changed = Rsp.gdp.changed = 0xFFFFFFFF;
        }
        
        public static void display() {
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
        
    };

    public static int uc_start;
    public static int uc_dstart;

    public static void config() {
        OpenGlGdp.config();
    }

    private static void OGL_UpdateCullFace() {
        if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BOTH)!=0) {
            GingerLite.enable(GL40.GL_CULL_FACE);

            if ((Rsp.gsp.geometryMode & Gbi.G_CULL_BACK)!=0)
                GL40.glCullFace(GL40.GL_BACK);
            else
                GL40.glCullFace(GL40.GL_FRONT);
        } else
            GingerLite.disable(GL40.GL_CULL_FACE);
    }
    
    public static void OGL_GspUpdateStates() {
        if ((Rsp.gsp.changed & Gsp.CHANGED_GEOMETRYMODE) != 0) {
            OGL_UpdateCullFace();
            GingerLite.disable(GL40.GL_FOG);
            Rsp.gsp.changed &= ~Gsp.CHANGED_GEOMETRYMODE;
        }

        if ((Rsp.gsp.geometryMode & Gbi.G_ZBUFFER) != 0)
            GingerLite.enable(GL40.GL_DEPTH_TEST);
        else
            GingerLite.disable(GL40.GL_DEPTH_TEST);

        Rsp.gsp.changed &= Gsp.CHANGED_MATRIX;
    }
    
}
