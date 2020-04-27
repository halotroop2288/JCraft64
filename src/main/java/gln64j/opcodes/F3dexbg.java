package gln64j.opcodes;

import gln64j.rdp.Gdp;
import gln64j.rsp.Gsp;

import static me.hydos.J64.util.debug.Debug.*;

import gln64j.rsp.GBIFunc;

import static gln64j.Gbi.*;

public class F3dexbg extends F3dex2 {

    public static GBIFunc F3DEXBG_Vtx = new GBIFunc() {
        public void exec(int w0, int w1) {
            gsp.gSPVertexBg(gsp.RSP_SegmentToPhysical(w1), (w0 >> 12) & 0xFF, ((w0 >> 1) & 0x7F));
        }
    };

    public static GBIFunc F3DEXBG_MoveWord = new GBIFunc() {
        public void exec(int w0, int w1) {
            int index = (w0 >> 16) & 0xFF;
            int offset = w0 & 0xFFFF;
            int data = w1;

            switch (index) {
                case 0x02: //G_MW_NUMLIGHT:
                    // CHANGED
                    gsp.gSPNumLights(data / 48 /*24*/);
                    break;
                case 0x04: //G_MW_CLIP:
                    break;
                case 0x06: //G_MW_SEGMENT:
                    gsp.gSPSegment(offset >> 2, data & 0x00FFFFFF);
                    break;
                case 0x08: //G_MW_FOG:
                    gsp.gSPFogFactor((short) ((data >> 16) & 0xFFFF), (short) (data & 0xFFFF));
                    break;
                case 0x0C: //G_MW_FORCEMTX:
                    // Handled in movemem
                    break;
                case 0x0E: //G_MW_PERSPNORM:
                    break;
                case 0x10: { // moveword coord mod
                    int n = offset >> 2;

                    if ((w0 & 8) != 0)
                        return;
                    gsp.gSPCoordMod(w1, (w0 >> 1) & 3, w0 & 0x30);
                    break;
                }
                default:
                    System.err.printf("moveword unknown (index: 0x%08X, offset 0x%08X)\n", index, offset);
            }
        }
    };

    public static GBIFunc F3DEXBG_MoveMem = new GBIFunc() {
        public void exec(int w0, int w1) {
            int idx = w0 & 0xFF;
            switch (idx) {
                case 8: //F3DEX2_MV_VIEWPORT:
                    gsp.gSPViewport(gsp.RSP_SegmentToPhysical(w1));
                    break;
                case 10: //G_MV_LIGHT:
                    // CHANGED
//                    int n = (ofs / 48);
                    gsp.gSPLightBg(gsp.RSP_SegmentToPhysical(w1), ((w0 >> 5) & 0x3FFF) / 48);
//                    if (n < 2) {
//                        return;
//                    }
//                    n -= 2;
//
//                    int col = N64.RDRAM.get(addr+0)&0xFF;
//                    Rsp.gsp.lights[n].color[0] = (float)col / 255.0f;
//                    Rsp.gsp.lights[n].nonblack = col;
//                    col = N64.RDRAM.get(addr+1)&0xFF;
//                    Rsp.gsp.lights[n].color[1] = (float)col / 255.0f;
//                    Rsp.gsp.lights[n].nonblack += col;
//                    col = N64.RDRAM.get(addr+2)&0xFF;
//                    Rsp.gsp.lights[n].color[2] = (float)col / 255.0f;
//                    Rsp.gsp.lights[n].nonblack += col;
//                    Rsp.gsp.lights[n].a = 1.0f;
//                    
//                    Rsp.gsp.lights[n].vec[0] = (float)N64.RDRAM.get(addr+8) / 127.0f;
//                    Rsp.gsp.lights[n].vec[1] = (float)N64.RDRAM.get(addr+9) / 127.0f;
//                    Rsp.gsp.lights[n].vec[2] = (float)N64.RDRAM.get(addr+10) / 127.0f;
//                    
//                    int a = addr >> 1;
//                    Rsp.gsp.lights[n].x = (float)N64.RDRAM.asShortBuffer().get(a+16);
//                    Rsp.gsp.lights[n].y = (float)N64.RDRAM.asShortBuffer().get(a+17);
//                    Rsp.gsp.lights[n].z = (float)N64.RDRAM.asShortBuffer().get(a+18);
//                    Rsp.gsp.lights[n].w = (float)N64.RDRAM.asShortBuffer().get(a+19);
//                    Rsp.gsp.lights[n].nonzero = N64.RDRAM.get(addr+12);
//                    Rsp.gsp.lights[n].ca = (float)Rsp.gsp.lights[n].nonzero / 16.0f;
                    break;
                case 14: // Normales
                    // CHANGED
//                    Rsp.gsp.uc8_normale_addr = Rsp.RSP_SegmentToPhysical(w1);
                    gsp.gSPNormals(gsp.RSP_SegmentToPhysical(w1));
                    break;
                default:
                    System.err.printf("movemem unknown (%d)\n", idx);
            }
        }
    };

    public static GBIFunc F3DEXBG_Tri4 = new GBIFunc() {
        public void exec(int w0, int w1) {
            gsp.gSP4Triangles(
                    (w0 >> 23) & 0x1F, (w0 >> 18) & 0x1F, ((((w0 >> 15) & 0x7) << 2) | ((w1 >> 30) & 0x3)),
                    (w0 >> 10) & 0x1F, (w0 >> 5) & 0x1F, (w0 >> 0) & 0x1F,
                    (w1 >> 25) & 0x1F, (w1 >> 20) & 0x1F, (w1 >> 15) & 0x1F,
                    (w1 >> 10) & 0x1F, (w1 >> 5) & 0x1F, (w1 >> 0) & 0x1F);
        }
    };

    public static void F3DEXBG_Init(Gsp rsp, Gdp rdp) {
        gsp = rsp;
        gdp = rdp;
        F3dex2.GBI_InitFlags();
        G_RDPHALF_2 = F3dex2.F3DEX2_RDPHALF_2;
        G_SETOTHERMODE_H = F3dex2.F3DEX2_SETOTHERMODE_H;
        G_SETOTHERMODE_L = F3dex2.F3DEX2_SETOTHERMODE_L;
        G_RDPHALF_1 = F3dex2.F3DEX2_RDPHALF_1;
        G_SPNOOP = F3dex2.F3DEX2_SPNOOP;
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
        G_TRI1 = F3dex2.F3DEX2_TRI1;
        G_TRI2 = F3dex2.F3DEX2_TRI2;
        G_QUAD = F3dex2.F3DEX2_QUAD;

        gsp.pcStackSize = 18;

        gsp.setUcode(G_RDPHALF_2, F3d.F3D_RDPHalf_2);
        gsp.setUcode(G_SETOTHERMODE_H, F3dex2.F3DEX2_SetOtherMode_H);
        gsp.setUcode(G_SETOTHERMODE_L, F3dex2.F3DEX2_SetOtherMode_L);
        gsp.setUcode(G_RDPHALF_1, F3d.F3D_RDPHalf_1);
        gsp.setUcode(G_SPNOOP, F3d.F3D_SPNoOp);
        gsp.setUcode(G_ENDDL, F3d.F3D_EndDL);
        gsp.setUcode(G_DL, F3d.F3D_DList);
        gsp.setUcode(G_LOAD_UCODE, F3dex.F3DEX_Load_uCode);
        gsp.setUcode(G_MOVEMEM, F3DEXBG_MoveMem);
        gsp.setUcode(G_MOVEWORD, F3DEXBG_MoveWord);
        gsp.setUcode(G_MTX, F3dex2.F3DEX2_Mtx);
        gsp.setUcode(G_GEOMETRYMODE, F3dex2.F3DEX2_GeometryMode);
        gsp.setUcode(G_POPMTX, F3dex2.F3DEX2_PopMtx);
        gsp.setUcode(G_TEXTURE, F3dex2.F3DEX2_Texture);
        gsp.setUcode(G_DMA_IO, F3dex2.F3DEX2_DMAIO);
        gsp.setUcode(G_SPECIAL_1, F3dex2.F3DEX2_Special_1);
        gsp.setUcode(G_SPECIAL_2, F3dex2.F3DEX2_Special_2);
        gsp.setUcode(G_SPECIAL_3, F3dex2.F3DEX2_Special_3);

        gsp.setUcode(G_VTX, F3DEXBG_Vtx);
        gsp.setUcode(G_MODIFYVTX, F3dex.F3DEX_ModifyVtx);
        gsp.setUcode(G_CULLDL, F3dex.F3DEX_CullDL);
        gsp.setUcode(G_BRANCH_Z, F3dex.F3DEX_Branch_Z);
        gsp.setUcode(G_TRI1, F3dex2.F3DEX2_Tri1);
        gsp.setUcode(G_TRI2, F3dex.F3DEX_Tri2);
        gsp.setUcode(G_QUAD, F3dex2.F3DEX2_Quad);

        for (int i = 0x10; i < 0x20; i++)
            gsp.setUcode(i, F3DEXBG_Tri4);

        if (DEBUG_MICROCODE) System.out.println("Initialized F3DEXBG opcodes");
    }

}
