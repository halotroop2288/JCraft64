package gln64j.opcodes;

import gln64j.rdp.Gdp;
import gln64j.rsp.Gsp;
import static me.hydos.J64.util.debug.Debug.*;
import gln64j.rsp.GBIFunc;
import static gln64j.Gbi.*;

public class L3dex2 extends F3dex2 {
    
    public static final int L3DEX2_LINE3D = 0x08;

    public static GBIFunc L3DEX2_Line3D = new GBIFunc() {
        public void exec(int w0, int w1) {
            int wd = w0&SR_MASK_8;
            if (wd == 0)
                gsp.gSPLine3D((w0>>17)&SR_MASK_7, (w0>>9)&SR_MASK_7, 0);
            else
                gsp.gSPLineW3D((w0>>17)&SR_MASK_7, (w0>>9)&SR_MASK_7, wd, 0);
        }
    };
    
    public static void L3DEX2_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3dex2.GBI_InitFlags();
        G_RDPHALF_2 = F3dex2.F3DEX2_RDPHALF_2;
        G_SETOTHERMODE_H = F3dex2.F3DEX2_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3dex2.F3DEX2_SETOTHERMODE_L;
        G_RDPHALF_1 = F3dex2.F3DEX2_RDPHALF_1;
        G_SPNOOP = F3dex2.F3DEX2_SPNOOP;
        G_ENDDL = F3dex2.F3DEX2_ENDDL;
        G_ENDDL = F3dex2.F3DEX2_ENDDL;
        G_DL = F3dex2.F3DEX2_DL;
        G_LOAD_UCODE = F3dex2.F3DEX2_LOAD_UCODE;
        G_MOVEMEM = F3dex2.F3DEX2_MOVEMEM;
        G_MOVEWORD = F3dex2.F3DEX2_MOVEWORD;
        G_MTX = F3dex2.F3DEX2_MTX;
        G_GEOMETRYMODE = F3dex2.F3DEX2_GEOMETRYMODE;
        G_POPMTX = F3dex2.F3DEX2_POPMTX;
        G_TEXTURE = F3dex2.F3DEX2_TEXTURE;
        G_DMA_IO = F3dex2.F3DEX2_DMA_IO;
        G_SPECIAL_1 = F3dex2.F3DEX2_SPECIAL_1;
        G_SPECIAL_2 = F3dex2.F3DEX2_SPECIAL_2;
        G_SPECIAL_3 = F3dex2.F3DEX2_SPECIAL_3;
        G_VTX = F3dex2.F3DEX2_VTX;
        G_MODIFYVTX = F3dex2.F3DEX2_MODIFYVTX;
        G_CULLDL = F3dex2.F3DEX2_CULLDL;
        G_BRANCH_Z = F3dex2.F3DEX2_BRANCH_Z;
        G_LINE3D = L3DEX2_LINE3D;
        
        gsp.pcStackSize = 18;
        
        gsp.setUcode(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setUcode(G_SETOTHERMODE_H, F3dex2.F3DEX2_SetOtherMode_H);
        gsp.setUcode(G_SETOTHERMODE_L, F3dex2.F3DEX2_SetOtherMode_L);
        gsp.setUcode(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setUcode(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setUcode(G_ENDDL, F3d.F3D_EndDL);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_LOAD_UCODE, F3dex.F3DEX_Load_uCode);
        gsp.setUcode(G_MOVEMEM, F3dex2.F3DEX2_MoveMem);
        gsp.setUcode(G_MOVEWORD, F3dex2.F3DEX2_MoveWord);
        gsp.setUcode(G_MTX, F3dex2.F3DEX2_Mtx);
        gsp.setUcode(G_GEOMETRYMODE, F3dex2.F3DEX2_GeometryMode);
        gsp.setUcode(G_POPMTX, F3dex2.F3DEX2_PopMtx);
        gsp.setUcode(G_TEXTURE, F3dex2.F3DEX2_Texture);
        gsp.setUcode(G_DMA_IO, F3dex2.F3DEX2_DMAIO);
        gsp.setUcode(G_SPECIAL_1, F3dex2.F3DEX2_Special_1);
        gsp.setUcode(G_SPECIAL_2, F3dex2.F3DEX2_Special_2);
        gsp.setUcode(G_SPECIAL_3, F3dex2.F3DEX2_Special_3);
        
        gsp.setUcode(G_VTX, F3dex2.F3DEX2_Vtx);
        gsp.setUcode(G_MODIFYVTX, F3dex.F3DEX_ModifyVtx);
        gsp.setUcode(G_CULLDL, F3dex.F3DEX_CullDL);
        gsp.setUcode(G_BRANCH_Z, F3dex.F3DEX_Branch_Z);
        gsp.setUcode(G_LINE3D, L3DEX2_Line3D);
        
        if (DEBUG_MICROCODE) System.out.println("Initialized L3DEX2 opcodes");
    }
    
}
