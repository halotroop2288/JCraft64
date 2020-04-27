package gln64j.opcodes;

import gln64j.rdp.Gdp;
import gln64j.rsp.Gsp;
import static me.hydos.J64.util.debug.Debug.*;
import gln64j.rsp.GBIFunc;
import static gln64j.Gbi.*;

public class F3dpd extends F3d {
    
    public static final int F3DPD_VTXCOLORBASE = 0x07;
    
    public static GBIFunc F3DPD_Vtx = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DPD_VtxColorBase = new GBIFunc() {
        public void exec(int w0, int w1) {
        }
    };
    
    public static void F3DPD_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3d.GBI_InitFlags();
        G_SPNOOP = F3d.F3D_SPNOOP;
        G_MTX = F3d.F3D_MTX;
        G_RESERVED0 = F3d.F3D_RESERVED0;
        G_MOVEMEM = F3d.F3D_MOVEMEM;
        G_VTX = F3d.F3D_VTX;
        G_RESERVED1 = F3d.F3D_RESERVED1;
        G_DL = F3d.F3D_DL;
        G_VTXCOLORBASE = F3DPD_VTXCOLORBASE;
        G_RESERVED3 = F3d.F3D_RESERVED3;
        G_SPRITE2D_BASE = F3d.F3D_SPRITE2D_BASE;
        G_TRI1 = F3d.F3D_TRI1;
        G_CULLDL = F3d.F3D_CULLDL;
        G_POPMTX = F3d.F3D_POPMTX;
        G_MOVEWORD = F3d.F3D_MOVEWORD;
        G_TEXTURE = F3d.F3D_TEXTURE;
        G_SETOTHERMODE_H = F3d.F3D_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3d.F3D_SETOTHERMODE_L;
        G_ENDDL = F3d.F3D_ENDDL;
        G_SETGEOMETRYMODE = F3d.F3D_SETGEOMETRYMODE;
        G_CLEARGEOMETRYMODE = F3d.F3D_CLEARGEOMETRYMODE;
        G_QUAD = F3d.F3D_QUAD;
        G_RDPHALF_1 = F3d.F3D_RDPHALF_1;
        G_RDPHALF_2 = F3d.F3D_RDPHALF_2;
        G_RDPHALF_CONT = F3d.F3D_RDPHALF_CONT;
        G_TRI4 = F3d.F3D_TRI4;
        
        gsp.pcStackSize = 10;
        
        gsp.setUcode(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setUcode(G_MTX, F3d.F3D_Mtx);
        gsp.setUcode(G_RESERVED0, F3d.F3D_Reserved0);
        gsp.setUcode(G_MOVEMEM, F3d.F3D_MoveMem);
        gsp.setUcode(G_VTX, F3DPD_Vtx);
        gsp.setUcode(G_RESERVED1, F3d.F3D_Reserved1);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_VTXCOLORBASE, F3DPD_VtxColorBase);
        gsp.setUcode(G_RESERVED3, F3d.F3D_Reserved3);
        gsp.setUcode(G_SPRITE2D_BASE, F3d.F3D_Sprite2D_Base);
        
        gsp.setUcode(G_TRI1, F3d.F3D_Tri1);
        gsp.setUcode(G_CULLDL, F3d.F3D_CullDL);
        gsp.setUcode(G_POPMTX, F3d.F3D_PopMtx);
        gsp.setUcode(G_MOVEWORD, F3d.F3D_MoveWord);
        gsp.setUcode(G_TEXTURE, F3d.F3D_Texture);
        gsp.setUcode(G_SETOTHERMODE_H, F3d.F3D_SetOtherMode_H);
        gsp.setUcode(G_SETOTHERMODE_L, F3d.F3D_SetOtherMode_L);
        gsp.setUcode(G_ENDDL, F3d.F3D_EndDL);
        gsp.setUcode(G_SETGEOMETRYMODE, F3d.F3D_SetGeometryMode);
        gsp.setUcode(G_CLEARGEOMETRYMODE, F3d.F3D_ClearGeometryMode);
        gsp.setUcode(G_QUAD, F3d.F3D_Quad);
        gsp.setUcode(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setUcode(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setUcode(G_RDPHALF_CONT, F3d.F3D_RDPHalf_Cont);
        gsp.setUcode(G_TRI4, F3d.F3D_Tri4);
        
        gsp.gSPSetDMAOffsets(0, 0);
        
        if (DEBUG_MICROCODE) System.out.println("Initialized F3DPD opcodes");
    }
    
}
