package me.hydos.J64.gfx.opcodes;

import static me.hydos.J64.emu.util.debug.Debug.DEBUG_MICROCODE;
import static me.hydos.J64.gfx.Gbi.G_CLEARGEOMETRYMODE;
import static me.hydos.J64.gfx.Gbi.G_CULLDL;
import static me.hydos.J64.gfx.Gbi.G_DL;
import static me.hydos.J64.gfx.Gbi.G_ENDDL;
import static me.hydos.J64.gfx.Gbi.G_MOVEMEM;
import static me.hydos.J64.gfx.Gbi.G_MOVEWORD;
import static me.hydos.J64.gfx.Gbi.G_MTX;
import static me.hydos.J64.gfx.Gbi.G_POPMTX;
import static me.hydos.J64.gfx.Gbi.G_QUAD;
import static me.hydos.J64.gfx.Gbi.G_RDPHALF_1;
import static me.hydos.J64.gfx.Gbi.G_RDPHALF_2;
import static me.hydos.J64.gfx.Gbi.G_RDPHALF_CONT;
import static me.hydos.J64.gfx.Gbi.G_RESERVED0;
import static me.hydos.J64.gfx.Gbi.G_RESERVED1;
import static me.hydos.J64.gfx.Gbi.G_RESERVED2;
import static me.hydos.J64.gfx.Gbi.G_RESERVED3;
import static me.hydos.J64.gfx.Gbi.G_SETGEOMETRYMODE;
import static me.hydos.J64.gfx.Gbi.G_SETOTHERMODE_H;
import static me.hydos.J64.gfx.Gbi.G_SETOTHERMODE_L;
import static me.hydos.J64.gfx.Gbi.G_SPNOOP;
import static me.hydos.J64.gfx.Gbi.G_SPRITE2D_BASE;
import static me.hydos.J64.gfx.Gbi.G_TEXTURE;
import static me.hydos.J64.gfx.Gbi.G_TRI1;
import static me.hydos.J64.gfx.Gbi.G_TRI2;
import static me.hydos.J64.gfx.Gbi.G_VTX;

import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gfx.rsp.GBIFunc;
import me.hydos.J64.gfx.rsp.Gsp;

public class F3dwrus extends F3d {
    
    public static final int F3DWRUS_TRI2 = 0xB1;
    
    public static GBIFunc F3DWRUS_Vtx = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DWRUS_Tri1 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DWRUS_Tri2 = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static GBIFunc F3DWRUS_Quad = new GBIFunc() {
        @Override
		public void exec(int w0, int w1) {
        }
    };
    
    public static void F3DWRUS_Init(Gsp rsp, Gdp rdp) {
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
        G_RESERVED2 = F3d.F3D_RESERVED2;
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
        G_TRI2 = F3DWRUS_TRI2;
        
        gsp.pcStackSize = 10;
        
        gsp.setGBI(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setGBI(G_MTX, F3d.F3D_Mtx);
        gsp.setGBI(G_RESERVED0, F3d.F3D_Reserved0);
        gsp.setGBI(G_MOVEMEM, F3d.F3D_MoveMem);
        gsp.setGBI(G_VTX, F3DWRUS_Vtx);
        gsp.setGBI(G_RESERVED1, F3d.F3D_Reserved1);
        gsp.setGBI(G_DL, F3d.F3D_DList);
        gsp.setGBI(G_RESERVED2, F3d.F3D_Reserved2);
        gsp.setGBI(G_RESERVED3, F3d.F3D_Reserved3);
        gsp.setGBI(G_SPRITE2D_BASE, F3d.F3D_Sprite2D_Base);
        
        gsp.setGBI(G_TRI1, F3DWRUS_Tri1);
        gsp.setGBI(G_CULLDL, F3d.F3D_CullDL);
        gsp.setGBI(G_POPMTX, F3d.F3D_PopMtx);
        gsp.setGBI(G_MOVEWORD, F3d.F3D_MoveWord);
        gsp.setGBI(G_TEXTURE, F3d.F3D_Texture);
        gsp.setGBI(G_SETOTHERMODE_H, F3d.F3D_SetOtherMode_H);
        gsp.setGBI(G_SETOTHERMODE_L, F3d.F3D_SetOtherMode_L);
        gsp.setGBI(G_ENDDL, F3d.F3D_EndDL);
        gsp.setGBI(G_SETGEOMETRYMODE, F3d.F3D_SetGeometryMode);
        gsp.setGBI(G_CLEARGEOMETRYMODE, F3d.F3D_ClearGeometryMode);
        gsp.setGBI(G_QUAD, F3DWRUS_Quad);
        gsp.setGBI(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setGBI(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setGBI(G_RDPHALF_CONT, F3d.F3D_RDPHalf_Cont);
        gsp.setGBI(G_TRI2, F3DWRUS_Tri2);
        
        if (DEBUG_MICROCODE) System.out.println("Initialized F3DWRUS opcodes");
    }
    
}
