package gln64j.opcodes;

import gln64j.rdp.Gdp;
import gln64j.rsp.Gsp;
import static me.hydos.J64.util.debug.Debug.*;
import gln64j.rsp.GBIFunc;
import static gln64j.Gbi.*;

public class F3ddkr extends F3d {
    
    public static final int F3DDKR_VTX_APPEND=		0x00010000;
    
    public static final int F3DDKR_DMA_MTX=		0x01;
    public static final int F3DDKR_DMA_VTX=		0x04;
    public static final int F3DDKR_DMA_TRI=		0x05;
    public static final int F3DDKR_DMA_DL=		0x07;
    public static final int F3DDKR_DMA_OFFSETS=		0xBF;
    
    public static GBIFunc F3DDKR_DMA_Mtx = new GBIFunc() {
        public void exec(int w0, int w1) {
            if ((w0&SR_MASK_16) != 64) {
                if (DEBUG_MICROCODE) {
                    DebugMsg(DEBUG_MEDIUM | DEBUG_HIGH | DEBUG_ERROR, "G_MTX: address = 0x%08X    length = %d    params = 0x%02X\n", w1, w0&SR_MASK_16, (w0>>16)&SR_MASK_8);
                }
                return;
            }
            
            int index = (w0>>16)&SR_MASK_4;
            boolean multiply;
            
            if (index == 0) { // DKR
                index = (w0>>22)&SR_MASK_2;
                multiply = false;
            } else { // Gemini
                multiply = ((w0>>23)&SR_MASK_1) != 0;
            }
            
            gsp.gSPDMAMatrix(gsp.RSP_SegmentToPhysical(w1), index, multiply);
        }
    };
    
    public static GBIFunc F3DDKR_DMA_Vtx = new GBIFunc() {
        public void exec(int w0, int w1) {

            int n = ((w0>>19)&SR_MASK_5) + 1;
            
            gsp.gSPDMAVertex(gsp.RSP_SegmentToPhysical(w1), n, (w0>>9)&SR_MASK_5, (w0 & F3DDKR_VTX_APPEND) != 0);
            
        }
    };
    
    public static GBIFunc F3DDKR_DMA_Tri = new GBIFunc() {
        public void exec(int w0, int w1) {
            gsp.gSPDMATriangles(gsp.RSP_SegmentToPhysical(w1), (w0>>4)&SR_MASK_12);
        }
    };
    
    public static GBIFunc F3DDKR_DMA_DList = new GBIFunc() {
        public void exec(int w0, int w1) {
            gsp.gSPDMADisplayList(w1, (w0>>16)&SR_MASK_8);
        }
    };
    
    public static GBIFunc F3DDKR_DMA_Offsets = new GBIFunc() {
        public void exec(int w0, int w1) {
            gsp.gSPSetDMAOffsets(w0&SR_MASK_24, w1&SR_MASK_24);
        }
    };
    
    public static GBIFunc F3DDKR_MoveWord = new GBIFunc() {
        public void exec(int w0, int w1) {
            switch (w0&SR_MASK_8) {
                case 0x02:
                    gsp.gSPMatrixBillboard(w1 & 1);
                    break;
                case 0x0A:
                    gsp.gSPMatrixModelViewi((w1>>6)&SR_MASK_2);
                    break;
                default:
                    F3d.F3D_MoveWord.exec(w0, w1);
                    break;
            }
        }
    };
    
    public static void F3DDKR_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3d.GBI_InitFlags();
        G_SPNOOP = F3d.F3D_SPNOOP;
        G_DMA_MTX = F3DDKR_DMA_MTX;
        G_MOVEMEM = F3d.F3D_MOVEMEM;
        G_DMA_VTX = F3DDKR_DMA_VTX;
        G_DL = F3d.F3D_DL;
        G_DMA_DL = F3DDKR_DMA_DL;
        G_DMA_TRI = F3DDKR_DMA_TRI;
        G_DMA_OFFSETS = F3DDKR_DMA_OFFSETS;
        G_CULLDL = F3d.F3D_CULLDL;
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
        gsp.setUcode(G_DMA_MTX, F3DDKR_DMA_Mtx);
        gsp.setUcode(G_MOVEMEM, F3d.F3D_MoveMem);
        gsp.setUcode(G_DMA_VTX, F3DDKR_DMA_Vtx);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_DMA_DL, F3DDKR_DMA_DList);
        gsp.setUcode(G_DMA_TRI, F3DDKR_DMA_Tri);
        
        gsp.setUcode(G_DMA_OFFSETS, F3DDKR_DMA_Offsets);
        gsp.setUcode(G_CULLDL, F3d.F3D_CullDL);
        gsp.setUcode(G_MOVEWORD, F3DDKR_MoveWord);
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
        
        if (DEBUG_MICROCODE) System.out.println("Initialized F3DDKR opcodes");
    }
    
}
