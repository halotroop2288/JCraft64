package me.hydos.J64.gfx.rsp;

import me.hydos.J64.gfx.Gbi;
import me.hydos.J64.gfx.OpenGlGdp;
import me.hydos.J64.gfx.Rsp;
import me.hydos.J64.gfx.rdp.Gdp;
import me.hydos.J64.gln64.rsp.Microcodes;

import org.lwjgl.opengl.GL40;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Gsp {

    public static class Cbfd {
		public static int vertexNormalBase;
		public static float[] vertexCoordMod = new float[16];
		public static boolean advancedLighting;
	}

	public static final int CHANGED_MATRIX = 0x02;
    public static final int CHANGED_GEOMETRYMODE = 0x08;
    public static final int CHANGED_FOGPOSITION = 0x10;
	private static final int INDEXMAP_SIZE = 80;
	
	public static int clipRatio;

    public static class SPVertex {
        public float[] vtx = new float[4];
        public float[] norm = new float[4];
        public float[] color = new float[4];
        public float[] tex = new float[2];
        public float[] clip = new float[3];
        public short flag;

        public String toString() {
            return String.format("%f %f %f %f - %f %f %f %f\n",
                    vtx[0], vtx[1], vtx[2], vtx[3],
                    color[0], color[1], color[2], color[3]);
        }

        public void clip() {
            if (vtx[0] < -vtx[3]) {
                clip[0] = -1.0f;
            } else if (vtx[0] > vtx[3]) {
                clip[0] = 1.0f;
            } else {
                clip[0] = 0.0f;
            }

            if (vtx[1] < -vtx[3]) {
                clip[1] = -1.0f;
            } else if (vtx[1] > vtx[3]) {
                clip[1] = 1.0f;
            } else {
                clip[1] = 0.0f;
            }

            if (vtx[3] <= 0.0f) {
                clip[2] = -1.0f;
            } else if (vtx[2] < -vtx[3]) {
                clip[2] = -0.1f;
            } else if (vtx[2] > vtx[3]) {
                clip[2] = 1.0f;
            } else {
                clip[2] = 0.0f;
            }
        }

        public void copyVertex(SPVertex src) {
            vtx[0] = src.vtx[0];
            vtx[1] = src.vtx[1];
            vtx[2] = src.vtx[2];
            vtx[3] = src.vtx[3];
            color[0] = src.color[0];
            color[1] = src.color[1];
            color[2] = src.color[2];
            color[3] = src.color[3];
            tex[0] = src.tex[0];
            tex[1] = src.tex[1];
        }

        public void interpolateVertex(float percent, SPVertex first, SPVertex second) {
            vtx[0] = first.vtx[0] + percent * (second.vtx[0] - first.vtx[0]);
            vtx[1] = first.vtx[1] + percent * (second.vtx[1] - first.vtx[1]);
            vtx[2] = first.vtx[2] + percent * (second.vtx[2] - first.vtx[2]);
            vtx[3] = first.vtx[3] + percent * (second.vtx[3] - first.vtx[3]);
            color[0] = first.color[0] + percent * (second.color[0] - first.color[0]);
            color[1] = first.color[1] + percent * (second.color[1] - first.color[1]);
            color[2] = first.color[2] + percent * (second.color[2] - first.color[2]);
            color[3] = first.color[3] + percent * (second.color[3] - first.color[3]);
            tex[0] = first.tex[0] + percent * (second.tex[0] - first.tex[0]);
            tex[1] = first.tex[1] + percent * (second.tex[1] - first.tex[1]);
        }
    }

    public static class Matrix {
        public int modelViewi;
        public int stackSize;
        public int billboard;
        public float[][][] modelView = new float[32][4][4];
        public float[][] projection = new float[4][4];
        public float[][] combined = new float[4][4];
    }

    public static class Light {
        public float[] color = new float[3];
        public float[] vec = new float[3];
        public float x;
        public float y;
        public float z;
        public float w;
        public int nonzero;
        public int nonblack;
        public float a;
        public float ca;
    }

    public static class Fog {
        public short multiplier;
        public short offset;
    }

    public static class DMAOffsets {
        public int vtx;
        public int mtx;
    }

    public static class Viewport {
        public float[] vscale = new float[4];
        public float[] vtrans = new float[4];
    }

    public Viewport viewport = new Viewport();
    public int pcStackSize;
    public Fog fog = new Fog();
    public int geometryMode;

    public int changed;

    private int[] PC = new int[18];
    private int PCi;
    private int cmd;
    private int nextCmd;
    private boolean halt;
    private int[] segment = new int[16];
    private Matrix matrix = new Matrix();
    private int vertexi;
    private Light[] lights = new Light[12];
    private SPVertex[] vertices = new SPVertex[80];
    private int[] status = new int[4];
    private DMAOffsets DMAOffsets = new DMAOffsets();
    private int numLights;
    private int uc8_normale_addr = 0;
    private float[] uc8_coord_mod = new float[16];
    protected float[][] tmpmtx = new float[4][4];

    protected ByteBuffer rdram;
    private ByteBuffer dmem;
    private int rdramSize;
    private Microcodes gbi = new Microcodes();

    private float[][] identityMatrix = {
            {1.0f, 0.0f, 0.0f, 0.0f},
            {0.0f, 1.0f, 0.0f, 0.0f},
            {0.0f, 0.0f, 1.0f, 0.0f},
            {0.0f, 0.0f, 0.0f, 1.0f}
    };

    /**
     * Creates a new instance of GSP
     */
    public Gsp(ByteBuffer rdram, ByteBuffer dmem) {
        this.rdram = rdram;
        this.dmem = dmem;
        this.rdramSize = rdram.capacity();
        for (int i = 0; i < lights.length; i++) {
            lights[i] = new Light();
        }
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new SPVertex();
        }
        gbi.init();
    }

    public void loadUcode(int uc_start, int uc_dstart) {
        matrix.stackSize = StrictMath.min(32, dmem.getInt(0x0FE4) >> 6);
        matrix.modelViewi = 0;
        changed |= CHANGED_MATRIX;

        for (int i = 0; i < 4; i++)
            Arrays.fill(matrix.modelView[0][i], 0.0f);

        matrix.modelView[0][0][0] = 1.0f;
        matrix.modelView[0][1][1] = 1.0f;
        matrix.modelView[0][2][2] = 1.0f;
        matrix.modelView[0][3][3] = 1.0f;

        int uc_start_t = dmem.getInt(0x0FD0);
        int uc_dstart_t = dmem.getInt(0x0FD8);
        int uc_dsize_t = dmem.getInt(0x0FDC);

        if ((uc_start_t != uc_start) || (uc_dstart_t != uc_dstart))
            gSPLoadUcodeEx(uc_start_t, uc_dstart_t, (short) uc_dsize_t);
    }

    public void RSP_ProcessDList() {
        PC[0] = dmem.getInt(0x0FF0);
        PCi = 0;

        byte[] ram = rdram.array();

        halt = false;

        while (!halt) {
            if ((PC[PCi] + 8) > rdramSize) {
                break;
            }

            int pAddr = PC[PCi];
            int w0 = (ram[pAddr] << 24) | ((ram[pAddr + 1] & 0xff) << 16) | ((ram[pAddr + 2] & 0xff) << 8) | (ram[pAddr + 3] & 0xff);
            int w1 = (ram[pAddr + 4] << 24) | ((ram[pAddr + 5] & 0xff) << 16) | ((ram[pAddr + 6] & 0xff) << 8) | (ram[pAddr + 7] & 0xff);
            cmd = (w0 >> 24) & Gbi.SR_MASK_8; // &0xff

            PC[PCi] += 8;
            pAddr = PC[PCi];
            nextCmd = (ram[pAddr] << 24) | ((ram[pAddr + 1] & 0xff) << 16) | ((ram[pAddr + 2] & 0xff) << 8) | (ram[pAddr + 3] & 0xff);
            nextCmd = (nextCmd >> 24) & Gbi.SR_MASK_8;

            gbi.cmds[cmd].exec(w0, w1);
        }

        Rsp.gdp.DList++;
        Rsp.gdp.changed |= Gdp.CHANGED_COLORBUFFER;
    }

    public int getCmd() {
        int w2 = rdram.getInt(PC[PCi] + 4);
        PC[PCi] += 8;
        return w2;
    }

    public void setGBI(int cmd, GBIFunc uCode) {
        gbi.cmds[cmd] = uCode;
    }

    public final int RSP_SegmentToPhysical(int segaddr) {
        return (segment[(segaddr >> 24) & 0x0F] + (segaddr & 0x00FFFFFF)) & 0x00FFFFFF;
    }

    public void gSPNoOp() {

    }

    public void gSPEndDisplayList() {
        if (PCi > 0)
            PCi--;
        else
            halt = true;
    }

    public void gSPSetGeometryMode(int mode) {
        geometryMode |= mode;
        changed |= CHANGED_GEOMETRYMODE;
    }

    public void gSPClearGeometryMode(int mode) {
        geometryMode &= ~mode;
        changed |= CHANGED_GEOMETRYMODE;
    }

    public void gSPDisplayList(int address) {
        if ((address + 8) > rdramSize)
            return;
        if (PCi < (pcStackSize - 1)) {
            PCi++;
            PC[PCi] = address;
        }
    }

    public void gSPBranchList(int address) {
        if ((address + 8) > rdramSize)
            return;
        PC[PCi] = address;
    }

    public void gSPPopMatrix(int param) {
        if (matrix.modelViewi > 0) {
            matrix.modelViewi--;
            changed |= CHANGED_MATRIX;
        }
    }

    public void gSPViewport(int address) {
        if ((address + 16) > rdramSize) {
            return;
        }
        viewport.vscale[0] = rdram.getShort(address + 2) * Gbi.FIXED2FLOATRECIP2;
        viewport.vscale[1] = rdram.getShort(address) * Gbi.FIXED2FLOATRECIP2;
        viewport.vscale[2] = rdram.getShort(address + 6) * Gbi.FIXED2FLOATRECIP10;
        viewport.vscale[3] = rdram.getShort(address + 4);
        viewport.vtrans[0] = rdram.getShort(address + 10) * Gbi.FIXED2FLOATRECIP2;
        viewport.vtrans[1] = rdram.getShort(address + 8) * Gbi.FIXED2FLOATRECIP2;
        viewport.vtrans[2] = rdram.getShort(address + 14) * Gbi.FIXED2FLOATRECIP10;
        viewport.vtrans[3] = rdram.getShort(address + 12);

        OpenGlGdp.setView(viewport.vtrans[2], viewport.vscale[2]);
    }

    public void gSPMatrix(int address, int param) {
        if (address + 64 > rdramSize)
            return;
        loadMatrix(tmpmtx, address);
        if ((param & Gbi.G_MTX_PROJECTION) != 0) {
            if ((param & Gbi.G_MTX_LOAD) != 0)
                Math3D.copyMatrix(matrix.projection, tmpmtx);
            else
                Math3D.multMatrix(matrix.projection, tmpmtx);
        } else {
            if ((param & Gbi.G_MTX_PUSH) != 0 && (matrix.modelViewi < (matrix.stackSize - 1))) {
                Math3D.copyMatrix(matrix.modelView[matrix.modelViewi + 1], matrix.modelView[matrix.modelViewi]);
                matrix.modelViewi++;
            }
            if ((param & Gbi.G_MTX_LOAD) != 0)
                Math3D.copyMatrix(matrix.modelView[matrix.modelViewi], tmpmtx);
            else
                Math3D.multMatrix(matrix.modelView[matrix.modelViewi], tmpmtx);
        }
        changed |= CHANGED_MATRIX;
    }

    public void gSPForceMatrix(int address) {
        if (address + 64 > rdramSize)
            return;
        loadMatrix(matrix.combined, address);
        changed &= ~CHANGED_MATRIX;
    }

    public void gSPLight(int address, int n) {
        if ((address + 12) > rdramSize)
            return;
        n--;
        if (n < 12) {
            lights[n].color[0] = (rdram.get(address) & 0xFF) * 0.0039215689f;
            lights[n].color[1] = (rdram.get(address + 1) & 0xFF) * 0.0039215689f;
            lights[n].color[2] = (rdram.get(address + 2) & 0xFF) * 0.0039215689f;
            lights[n].vec[0] = rdram.get(address + 8);
            lights[n].vec[1] = rdram.get(address + 9);
            lights[n].vec[2] = rdram.get(address + 10);
            Math3D.normalize(lights[n].vec);
        }
    }

    public void gSPLightBg(int address, int n) {
        n -= 2;
        if (n >= 0) {
            int col = rdram.get(address) & 0xFF;
            lights[n].color[0] = (float) col / 255.0f;
            lights[n].nonblack = col;
            col = rdram.get(address + 1) & 0xFF;
            lights[n].color[1] = (float) col / 255.0f;
            lights[n].nonblack += col;
            col = rdram.get(address + 2) & 0xFF;
            lights[n].color[2] = (float) col / 255.0f;
            lights[n].nonblack += col;
            lights[n].a = 1.0f;
            lights[n].vec[0] = (float) rdram.get(address + 8) / 127.0f;
            lights[n].vec[1] = (float) rdram.get(address + 9) / 127.0f;
            lights[n].vec[2] = (float) rdram.get(address + 10) / 127.0f;
            int a = address >> 1;
            lights[n].x = rdram.asShortBuffer().get(a + 16);
            lights[n].y = rdram.asShortBuffer().get(a + 17);
            lights[n].z = rdram.asShortBuffer().get(a + 18);
            lights[n].w = rdram.asShortBuffer().get(a + 19);
            lights[n].nonzero = rdram.get(address + 12);
            lights[n].ca = (float) lights[n].nonzero / 16.0f;
        }
    }

    public void gSPVertex(int address, int n, int v0) {
        if ((address + 16 * n) > rdramSize)
            return;
        if ((n + v0) < (80)) {
            SPVertex vertex;
            for (int i = v0; i < n + v0; i++) {
                vertex = vertices[i];
                vertex.vtx[0] = rdram.getShort(address);
                vertex.vtx[1] = rdram.getShort(address + 2);
                vertex.vtx[2] = rdram.getShort(address + 4);

                vertex.flag = rdram.getShort(address + 6);
                vertex.tex[0] = rdram.getShort(address + 8) * Gbi.FIXED2FLOATRECIP5;
                vertex.tex[1] = rdram.getShort(address + 10) * Gbi.FIXED2FLOATRECIP5;

                if ((geometryMode & Gbi.G_LIGHTING) != 0) {
                    vertex.norm[0] = rdram.get(address + 12);
                    vertex.norm[1] = rdram.get(address + 13);
                    vertex.norm[2] = rdram.get(address + 14);
                    vertex.color[3] = (rdram.get(address + 15) & 0xFF) * 0.0039215689f;
                } else {
                    vertex.color[0] = (rdram.get(address + 12) & 0xFF) * 0.0039215689f;
                    vertex.color[1] = (rdram.get(address + 13) & 0xFF) * 0.0039215689f;
                    vertex.color[2] = (rdram.get(address + 14) & 0xFF) * 0.0039215689f;
                    vertex.color[3] = (rdram.get(address + 15) & 0xFF) * 0.0039215689f;
                }

                gSPProcessVertex(vertex);
                address += 16;
            }
        }
    }
    
    

    public void gSP1Triangle(int v0, int v1, int v2, int flag) {
        gSPTriangle(v0, v1, v2);
        gSPFlushTriangles();
    }

    public void gSP4Triangles(
            int v00, int v01, int v02,
            int v10, int v11, int v12,
            int v20, int v21, int v22,
            int v30, int v31, int v32) {
        gSPTriangle(v00, v01, v02);
        gSPTriangle(v10, v11, v12);
        gSPTriangle(v20, v21, v22);
        gSPTriangle(v30, v31, v32);
        gSPFlushTriangles();
    }

    public void gSP1Quadrangle(int v0, int v1, int v2, int v3) {
        gSPTriangle(v0, v1, v2);
        gSPTriangle(v0, v2, v3);
        gSPFlushTriangles();
    }

    public void gSPCullDisplayList(int v0, int vn) {
        if (gSPCullVertices(v0, vn)) {
            if (PCi > 0)
                PCi--;
            else
                halt = true;
        }
    }

    public void gSPSegment(int seg, int base) {
        if (seg > 0xF)
            return;
        if (base > rdramSize - 1)
            return;
        segment[seg] = base;
    }

    public void gSPClipRatio(int r) {

    }

    public void gSPInsertMatrix(int where, int num) {
        float fraction, integer;

        if ((changed & CHANGED_MATRIX) != 0)
            gSPCombineMatrices();

        if ((where & 0x3) != 0 || (where > 0x3C))
            return;

        if (where < 0x20) {
            int w = where >> 1;
            int r = w / 4;
            int c = w % 4;
            int w2 = w + 1;
            int r2 = w2 / 4;
            int c2 = w2 % 4;
            integer = (int) matrix.combined[r][c];
            fraction = matrix.combined[r][c] - integer;
            matrix.combined[r][c] = (short) ((num >> 16) & Gbi.SR_MASK_16) + StrictMath.abs(fraction);

            integer = (int) matrix.combined[r2][c2];
            fraction = matrix.combined[r2][c2] - integer;
            matrix.combined[r2][c2] = (short) (num & Gbi.SR_MASK_16) + StrictMath.abs(fraction);
        } else {
            int w = (where - 0x20) >> 1;
            int r = w / 4;
            int c = w % 4;
            int w2 = w + 1;
            int r2 = w2 / 4;
            int c2 = w2 % 4;
            float newValue;

            integer = (int) matrix.combined[r][c];
            fraction = matrix.combined[r][c] - integer;
            newValue = integer + (((num >> 16) & Gbi.SR_MASK_16) * Gbi.FIXED2FLOATRECIP16);

            if ((integer == 0.0f) && (fraction != 0.0f))
                newValue = newValue * (fraction / StrictMath.abs(fraction));

            matrix.combined[r][c] = newValue;

            integer = (int) matrix.combined[r2][c2];
            fraction = matrix.combined[r2][c2] - integer;
            newValue = integer + ((num & Gbi.SR_MASK_16) * Gbi.FIXED2FLOATRECIP16);

            if ((integer == 0.0f) && (fraction != 0.0f))
                newValue = newValue * (fraction / StrictMath.abs(fraction));

            matrix.combined[r2][c2] = newValue;
        }
    }

    public void gSPModifyVertex(int vtx, int where, int val) {
        switch (where) {
            case Gbi.G_MWO_POINT_RGBA:
                vertices[vtx].color[0] = ((val >> 24) & Gbi.SR_MASK_8) * 0.0039215689f;
                vertices[vtx].color[1] = ((val >> 16) & Gbi.SR_MASK_8) * 0.0039215689f;
                vertices[vtx].color[2] = ((val >> 8) & Gbi.SR_MASK_8) * 0.0039215689f;
                vertices[vtx].color[3] = (val & Gbi.SR_MASK_8) * 0.0039215689f;
                break;
            case Gbi.G_MWO_POINT_ST:
                vertices[vtx].tex[0] = ((short) ((val >> 16) & Gbi.SR_MASK_16)) * Gbi.FIXED2FLOATRECIP5;
                vertices[vtx].tex[1] = ((short) (val & Gbi.SR_MASK_16)) * Gbi.FIXED2FLOATRECIP5;
                break;
            case Gbi.G_MWO_POINT_XYSCREEN:
                break;
            case Gbi.G_MWO_POINT_ZSCREEN:
                break;
        }
    }

    public void gSPNumLights(int n) {
        if (n <= 8)
            numLights = n;
    }

    public void gSPLightColor(int lightNum, int packedColor) {
        lightNum--;
        if (lightNum < 12) {
            lights[lightNum].color[0] = ((packedColor >> 24) & Gbi.SR_MASK_8) * 0.0039215689f;
            lights[lightNum].color[1] = ((packedColor >> 16) & Gbi.SR_MASK_8) * 0.0039215689f;
            lights[lightNum].color[2] = ((packedColor >> 8) & Gbi.SR_MASK_8) * 0.0039215689f;
        }
    }

    public void gSPFogFactor(short fm, short fo) {}

    public void gSPPerspNormalize(short scale) {
    	System.out.println("gSPPerspNormalize(" + scale + ");");
    }

    public void gSPLoadUcodeEx(int uc_start, int uc_dstart, short uc_dsize) {
        PCi = 0;
        matrix.modelViewi = 0;
        changed |= CHANGED_MATRIX;
        status[0] = status[1] = status[2] = status[3] = 0;

        if ((((uc_start & 0x1FFFFFFF) + 4096) > rdramSize) || (((uc_dstart & 0x1FFFFFFF) + uc_dsize) > rdramSize))
            return;

        Microcode ucode = gbi.detectMicrocode(uc_start, uc_dstart, uc_dsize);

        if (ucode.type != Microcode.NONE)
            gbi.makeCurrent(ucode);
    }

    public void gSPBranchLessZ(int address, int vtx, float zval) {
        if ((address + 8) > rdramSize)
            return;
        if (vertices[vtx].vtx[2] <= zval)
            PC[PCi] = address;
    }

    public void gSP2Triangles(int v00, int v01, int v02, int flag0, int v10, int v11, int v12, int flag1) {
        gSPTriangle(v00, v01, v02);
        gSPTriangle(v10, v11, v12);
        gSPFlushTriangles();
    }

    public void gSPGeometryMode(int clear, int set) {
        geometryMode = (geometryMode & ~clear) | set;
        changed |= CHANGED_GEOMETRYMODE;
    }

    public void gSPPopMatrixN(int param, int num) {
        if (matrix.modelViewi > num - 1) {
            matrix.modelViewi -= num;
            changed |= CHANGED_MATRIX;
        }
    }

    public void gSPNormals(int address) {
        uc8_normale_addr = address;
    }

    public void gSPCoordMod(int coord, int index, int pos) {
        if (pos == 0) {
            uc8_coord_mod[0 + index] = (short) (coord >> 16);
            uc8_coord_mod[1 + index] = (short) (coord & 0xffff);
        } else if (pos == 0x10) {
            uc8_coord_mod[4 + index] = (coord >> 16) / 65536.0f;
            uc8_coord_mod[5 + index] = (coord & 0xffff) / 65536.0f;
            uc8_coord_mod[12 + index] = uc8_coord_mod[index] + uc8_coord_mod[4 + index];
            uc8_coord_mod[13 + index] = uc8_coord_mod[1 + index] + uc8_coord_mod[5 + index];
        } else if (pos == 0x20) {
            uc8_coord_mod[8 + index] = (short) (coord >> 16);
            uc8_coord_mod[9 + index] = (short) (coord & 0xffff);
        }
    }

    public void gSPVertexBg(int address, int n, int v0) {
        if ((changed & CHANGED_MATRIX) != 0)
            gSPCombineMatrices();

        if (v0 - n < 0)
            return;

        for (int i = 0; i < (n << 4); i += 16) {
            SPVertex vertex = vertices[v0 - n + (i >> 4)];
            vertex.vtx[0] = rdram.asShortBuffer().get(((address + i) >> 1));
            vertex.vtx[1] = rdram.asShortBuffer().get(((address + i) >> 1) + 1);
            vertex.vtx[2] = rdram.asShortBuffer().get(((address + i) >> 1) + 2);
            vertex.flag = rdram.asShortBuffer().get(((address + i) >> 1) + 3);
            vertex.tex[0] = (float) rdram.asShortBuffer().get(((address + i) >> 1) + 4) * 0.03125f;
            vertex.tex[1] = (float) rdram.asShortBuffer().get(((address + i) >> 1) + 5) * 0.03125f;
            vertex.color[3] = (rdram.get((address + i) + 15) & 0xFF) * 0.0039215689f;

            Math3D.transformVertex(vertex.vtx, matrix.combined);

            if (vertex.vtx[0] < -vertex.vtx[3])
                vertex.clip[0] = -1.0f;
            else if (vertex.vtx[0] > vertex.vtx[3])
                vertex.clip[0] = 1.0f;
            else
                vertex.clip[0] = 0.0f;

            if (vertex.vtx[1] < -vertex.vtx[3])
                vertex.clip[1] = -1.0f;
            else if (vertex.vtx[1] > vertex.vtx[3])
                vertex.clip[1] = 1.0f;
            else
                vertex.clip[1] = 0.0f;

            if (vertex.vtx[3] <= 0.0f)
                vertex.clip[2] = -1.0f;
            else if (vertex.vtx[2] < -vertex.vtx[3])
                vertex.clip[2] = -0.1f;
            else if (vertex.vtx[2] > vertex.vtx[3])
                vertex.clip[2] = 1.0f;
            else
                vertex.clip[2] = 0.0f;

            vertex.color[0] = (rdram.get((address + i) + 12) & 0xFF) * 0.0039215689f;
            vertex.color[1] = (rdram.get((address + i) + 13) & 0xFF) * 0.0039215689f;
            vertex.color[2] = (rdram.get((address + i) + 14) & 0xFF) * 0.0039215689f;

            if ((geometryMode & 0x00020000) != 0) {
                int shift = (v0 - n) << 1;
                vertex.norm[0] = rdram.get(uc8_normale_addr + (i >> 3) + shift);
                vertex.norm[1] = rdram.get(uc8_normale_addr + (i >> 3) + shift + 1);
                vertex.norm[2] = (byte) (vertex.flag & 0xff);

                Math3D.transformVector(vertex.norm, matrix.modelView[matrix.modelViewi]);
                Math3D.normalize(vertex.norm);
                if ((geometryMode & 0x00080000 /*Gbi.G_TEXTURE_GEN_LINEAR*/) != 0) {
                    vertex.tex[0] = (float) StrictMath.acos(vertex.norm[0]) * 325.94931f;
                    vertex.tex[1] = (float) StrictMath.acos(vertex.norm[1]) * 325.94931f;
                } else { // G_TEXTURE_GEN
                    vertex.tex[0] = (vertex.norm[0] + 1.0f) * 512.0f;
                    vertex.tex[1] = (vertex.norm[1] + 1.0f) * 512.0f;
                }

                float[] color = {lights[numLights].color[0], lights[numLights].color[1], lights[numLights].color[2]};
                float light_intensity = 0.0f;
                int l;
                if ((geometryMode & 0x00400000) != 0) {
                    Math3D.normalize(vertex.norm);
                    for (l = 0; l < numLights - 1; l++) {
                        if (lights[l].nonblack == 0)
                            continue;
                        light_intensity = Math3D.dotProduct(vertex.norm, lights[l].vec);
                        if (light_intensity < 0.0f)
                            continue;
                        if (lights[l].ca > 0.0f) {
                            float vx = (vertex.vtx[0] + uc8_coord_mod[8]) * uc8_coord_mod[12] - lights[l].x;
                            float vy = (vertex.vtx[1] + uc8_coord_mod[9]) * uc8_coord_mod[13] - lights[l].y;
                            float vz = (vertex.vtx[2] + uc8_coord_mod[10]) * uc8_coord_mod[14] - lights[l].z;
                            float vw = (vertex.vtx[3] + uc8_coord_mod[11]) * uc8_coord_mod[15] - lights[l].w;
                            float len = (vx * vx + vy * vy + vz * vz + vw * vw) / 65536.0f;
                            float p_i = lights[l].ca / len;
                            if (p_i > 1.0f) p_i = 1.0f;
                            light_intensity *= p_i;
                        }
                        color[0] += lights[l].color[0] * light_intensity;
                        color[1] += lights[l].color[1] * light_intensity;
                        color[2] += lights[l].color[2] * light_intensity;
                    }
                    light_intensity = Math3D.dotProduct(vertex.norm, lights[l].vec);
                    if (light_intensity > 0.0f) {
                        color[0] += lights[l].color[0] * light_intensity;
                        color[1] += lights[l].color[1] * light_intensity;
                        color[2] += lights[l].color[2] * light_intensity;
                    }
                } else {
                    for (l = 0; l < numLights; l++) {
                        if (lights[l].nonblack != 0 && lights[l].nonzero != 0) {
                            float vx = (vertex.vtx[0] + uc8_coord_mod[8]) * uc8_coord_mod[12] - lights[l].x;
                            float vy = (vertex.vtx[1] + uc8_coord_mod[9]) * uc8_coord_mod[13] - lights[l].y;
                            float vz = (vertex.vtx[2] + uc8_coord_mod[10]) * uc8_coord_mod[14] - lights[l].z;
                            float vw = (vertex.vtx[3] + uc8_coord_mod[11]) * uc8_coord_mod[15] - lights[l].w;
                            float len = (vx * vx + vy * vy + vz * vz + vw * vw) / 65536.0f;
                            light_intensity = lights[l].ca / len;
                            if (light_intensity > 1.0f) light_intensity = 1.0f;
                            color[0] += lights[l].color[0] * light_intensity;
                            color[1] += lights[l].color[1] * light_intensity;
                            color[2] += lights[l].color[2] * light_intensity;
                        }
                    }
                }
                if (color[0] > 1.0f)
                    color[0] = 1.0f;
                if (color[1] > 1.0f)
                    color[1] = 1.0f;
                if (color[2] > 1.0f)
                    color[2] = 1.0f;
                vertex.color[0] = vertex.color[0] * color[0];
                vertex.color[1] = vertex.color[1] * color[1];
                vertex.color[2] = vertex.color[2] * color[2];
            }
        }
    }

    public void gSPMatrixBillboard(int x) {
        matrix.billboard = x;
    }

    public void gSPMatrixModelViewi(int x) {
        matrix.modelViewi = x;
        changed |= CHANGED_MATRIX;
    }

    public void gSPDMAMatrix(int address, int index, boolean multiply) {
        address = DMAOffsets.mtx + address;
        if (address + 64 > rdramSize)
            return;

        loadMatrix(tmpmtx, address);
        matrix.modelViewi = index;

        if (multiply) {
            Math3D.copyMatrix(matrix.modelView[matrix.modelViewi], matrix.modelView[0]);
            Math3D.multMatrix(matrix.modelView[matrix.modelViewi], tmpmtx);
        } else
            Math3D.copyMatrix(matrix.modelView[matrix.modelViewi], tmpmtx);

        Math3D.copyMatrix(matrix.projection, identityMatrix);
        changed |= CHANGED_MATRIX;
    }

    public void gSPDMAVertex(int address, int n, int v0, boolean append) {
        v0 = vertexi + v0;
        if (append) {
            if ((matrix.billboard) != 0) {
                vertexi = 1;
            }
        } else {
            vertexi = 0;
        }

        address = DMAOffsets.vtx + address;

        if ((address + 10 * n) > rdramSize)
            return;

        if ((n + v0) < (80)) {
            SPVertex vertex;
            for (int i = v0; i < n + v0; i++) {
                vertex = vertices[i];
                vertex.vtx[0] = rdram.getShort(address ^ 2);
                vertex.vtx[1] = rdram.getShort((address + 2) ^ 2);
                vertex.vtx[2] = rdram.getShort((address + 4) ^ 2);

                if ((geometryMode & Gbi.G_LIGHTING) != 0) {
                    vertex.norm[0] = rdram.get((address + 6) ^ 3);
                    vertex.norm[1] = rdram.get((address + 7) ^ 3);
                    vertex.norm[2] = rdram.get((address + 8) ^ 3);
                    vertex.color[3] = (rdram.get((address + 9) ^ 3) & 0xFF) * 0.0039215689f;
                } else {
                    vertex.color[0] = (rdram.get((address + 6) ^ 3) & 0xFF) * 0.0039215689f;
                    vertex.color[1] = (rdram.get((address + 7) ^ 3) & 0xFF) * 0.0039215689f;
                    vertex.color[2] = (rdram.get((address + 8) ^ 3) & 0xFF) * 0.0039215689f;
                    vertex.color[3] = (rdram.get((address + 9) ^ 3) & 0xFF) * 0.0039215689f;
                }

                gSPProcessVertex(vertex);
                address += 10;
            }
        }

        vertexi += n;
    }

    public void gSPDMADisplayList(int dl, int n) {
        if ((dl + (n << 3)) > rdramSize)
            return;

        int curDL = PC[PCi];

        PC[PCi] = RSP_SegmentToPhysical(dl);

        while ((PC[PCi] - dl) < (n << 3)) {
            if ((PC[PCi] + 8) > rdramSize)
                break;

            int w0 = rdram.getInt(PC[PCi]);
            int w1 = rdram.getInt(PC[PCi] + 4);

            PC[PCi] += 8;
            nextCmd = (rdram.getInt(PC[PCi]) >> 24) & Gbi.SR_MASK_8;

            gbi.cmds[(w0 >> 24) & Gbi.SR_MASK_8].exec(w0, w1);
        }

        PC[PCi] = curDL;
    }

    public void gSPDMATriangles(int address, int n) {
        if (address + 16 * n > rdramSize)
            return;

        for (int i = 0; i < n; i++) {
            int v0 = rdram.get(address + 1) & 0xFF;
            vertices[v0].tex[0] = rdram.getShort(address + 4) * Gbi.FIXED2FLOATRECIP5;
            vertices[v0].tex[1] = rdram.getShort(address + 6) * Gbi.FIXED2FLOATRECIP5;

            int v1 = rdram.get(address + 2) & 0xFF;
            vertices[v1].tex[0] = rdram.getShort(address + 8) * Gbi.FIXED2FLOATRECIP5;
            vertices[v1].tex[1] = rdram.getShort(address + 10) * Gbi.FIXED2FLOATRECIP5;

            int v2 = rdram.get(address + 3) & 0xFF;
            vertices[v2].tex[0] = rdram.getShort(address + 12) * Gbi.FIXED2FLOATRECIP5;
            vertices[v2].tex[1] = rdram.getShort(address + 14) * Gbi.FIXED2FLOATRECIP5;

            gSPTriangle(v0, v1, v2);
            address += 16;
        }

        gSPFlushTriangles();

        vertexi = 0;
    }

    public void gSPSetDMAOffsets(int mtxoffset, int vtxoffset) {
        DMAOffsets.mtx = mtxoffset;
        DMAOffsets.vtx = vtxoffset;
    }

    public void gSPLine3D(int v0, int v1, int flag) {
        OpenGlGdp.OGL_DrawLine(vertices[v0].vtx, vertices[v0].color, vertices[v1].vtx, vertices[v1].color, 1.5f);
    }

    public void gSPLineW3D(int v0, int v1, int wd, int flag) {
        OpenGlGdp.OGL_DrawLine(vertices[v0].vtx, vertices[v0].color, vertices[v1].vtx, vertices[v1].color, 1.5f + wd * 0.5f);
    }

    private void loadMatrix(float[][] mtx, int address) {
        final float recip = 1.5258789e-05f;
        for (int i = 0; i < 4; i++) {
            mtx[i][0] = rdram.getShort(address) + (rdram.getShort(address + 32) & 0xFFFF) * recip;
            mtx[i][1] = rdram.getShort(address + 2) + (rdram.getShort(address + 34) & 0xFFFF) * recip;
            mtx[i][2] = rdram.getShort(address + 4) + (rdram.getShort(address + 36) & 0xFFFF) * recip;
            mtx[i][3] = rdram.getShort(address + 6) + (rdram.getShort(address + 38) & 0xFFFF) * recip;
            address += 8;
        }
    }

    private void gSPCombineMatrices() {
        Math3D.copyMatrix(matrix.combined, matrix.projection);
        Math3D.multMatrix(matrix.combined, matrix.modelView[matrix.modelViewi]);
        changed &= ~CHANGED_MATRIX;
    }

    private void gSPProcessVertex(SPVertex vert) {
        float intensity;
        float r, g, b;

        if ((changed & CHANGED_MATRIX) != 0)
            gSPCombineMatrices();

        Math3D.transformVertex(vert.vtx, matrix.combined);

        if (matrix.billboard != 0) {
            vert.vtx[0] += vertices[0].vtx[0];
            vert.vtx[1] += vertices[0].vtx[1];
            vert.vtx[2] += vertices[0].vtx[2];
            vert.vtx[3] += vertices[0].vtx[3];
        }

        if ((geometryMode & Gbi.G_ZBUFFER) == 0) {
            vert.vtx[2] = -vert.vtx[3];
        }

        if ((geometryMode & Gbi.G_LIGHTING) != 0) {
            Math3D.transformVector(vert.norm, matrix.modelView[matrix.modelViewi]);
            Math3D.normalize(vert.norm);

            r = lights[numLights].color[0];
            g = lights[numLights].color[1];
            b = lights[numLights].color[2];

            for (int i = 0; i < numLights; i++) {
                intensity = Math3D.dotProduct(vert.norm, lights[i].vec);

                if (intensity < 0.0f) {
                    intensity = 0.0f;
                }

                r += lights[i].color[0] * intensity;
                g += lights[i].color[1] * intensity;
                b += lights[i].color[2] * intensity;
            }

            vert.color[0] = r;
            vert.color[1] = g;
            vert.color[2] = b;

            if ((geometryMode & Gbi.G_TEXTURE_GEN) != 0) {
                Math3D.transformVector(vert.norm, matrix.projection);

                Math3D.normalize(vert.norm);

                if ((geometryMode & Gbi.G_TEXTURE_GEN_LINEAR) != 0) {
                    vert.tex[0] = (float) StrictMath.acos(vert.norm[0]) * 325.94931f;
                    vert.tex[1] = (float) StrictMath.acos(vert.norm[1]) * 325.94931f;
                } else { // G_TEXTURE_GEN
                    vert.tex[0] = (vert.norm[0] + 1.0f) * 512.0f;
                    vert.tex[1] = (vert.norm[1] + 1.0f) * 512.0f;
                }
            }
        }

        vert.clip();
    }

    private boolean gSPCullVertices(int v0, int vn) {
        float xClip, yClip, zClip;

        xClip = yClip = zClip = 0.0f;

        for (int i = v0; i <= vn; i++) {
            if (vertices[i].clip[0] == 0.0f)
                return false;
            else if (vertices[i].clip[0] < 0.0f) {
                if (xClip > 0.0f)
                    return false;
                else
                    xClip = vertices[i].clip[0];
            } else if (vertices[i].clip[0] > 0.0f) {
                if (xClip < 0.0f)
                    return false;
                else
                    xClip = vertices[i].clip[0];
            }

            if (vertices[i].clip[1] == 0.0f)
                return false;
            else if (vertices[i].clip[1] < 0.0f) {
                if (yClip > 0.0f)
                    return false;
                else
                    yClip = vertices[i].clip[1];
            } else if (vertices[i].clip[1] > 0.0f) {
                if (yClip < 0.0f)
                    return false;
                else
                    yClip = vertices[i].clip[1];
            }

            if (vertices[i].clip[2] == 0.0f)
                return false;
            else if (vertices[i].clip[2] < 0.0f) {
                if (zClip > 0.0f)
                    return false;
                else
                    zClip = vertices[i].clip[2];
            } else if (vertices[i].clip[2] > 0.0f) {
                if (zClip < 0.0f)
                    return false;
                else
                    zClip = vertices[i].clip[2];
            }
        }

        return true;
    }

    public void gSPFlushTriangles() {
        if ((nextCmd != Gbi.G_TRI1) &&
                (nextCmd != Gbi.G_TRI2) &&
                (nextCmd != Gbi.G_TRI4) &&
                (nextCmd != Gbi.G_QUAD) &&
                (nextCmd != Gbi.G_DMA_TRI))
            OpenGlGdp.OGL_DrawTriangles();
    }

    public void gSPTriangle(int v0, int v1, int v2) {
        if ((v0 < 80) && (v1 < 80) && (v2 < 80)) {
            if (((vertices[v0].clip[0] < 0.0f) &&
                    (vertices[v1].clip[0] < 0.0f) &&
                    (vertices[v2].clip[0] < 0.0f)) ||
                    ((vertices[v0].clip[0] > 0.0f) &&
                            (vertices[v1].clip[0] > 0.0f) &&
                            (vertices[v2].clip[0] > 0.0f)) ||
                    ((vertices[v0].clip[1] < 0.0f) &&
                            (vertices[v1].clip[1] < 0.0f) &&
                            (vertices[v2].clip[1] < 0.0f)) ||
                    ((vertices[v0].clip[1] > 0.0f) &&
                            (vertices[v1].clip[1] > 0.0f) &&
                            (vertices[v2].clip[1] > 0.0f)) ||
                    ((vertices[v0].clip[2] > 0.1f) &&
                            (vertices[v1].clip[2] > 0.1f) &&
                            (vertices[v2].clip[2] > 0.1f)) ||
                    ((vertices[v0].clip[2] < -0.1f) &&
                            (vertices[v1].clip[2] < -0.1f) &&
                            (vertices[v2].clip[2] < -0.1f)))
                return;

            if (gbi.current.NoN &&
                    ((vertices[v0].clip[2] < 0.0f) ||
                            (vertices[v1].clip[2] < 0.0f) ||
                            (vertices[v2].clip[2] < 0.0f))) {
                SPVertex[] nearVertices = new SPVertex[4];
                SPVertex[] clippedVertices = new SPVertex[4];
                for (int i = 0; i < 4; i++) { //TMP
                    nearVertices[i] = new SPVertex();
                    clippedVertices[i] = new SPVertex();
                }

                int nearIndex = 0;
                int clippedIndex = 0;

                int[] v = {v0, v1, v2};

                for (int i = 0; i < 3; i++) {
                    int j = i + 1;
                    if (j == 3) j = 0;

                    if (((vertices[v[i]].clip[2] < 0.0f) && (vertices[v[j]].clip[2] >= 0.0f)) ||
                            ((vertices[v[i]].clip[2] >= 0.0f) && (vertices[v[j]].clip[2] < 0.0f))) {
                        float percent = (-vertices[v[i]].vtx[3] - vertices[v[i]].vtx[2]) / ((vertices[v[j]].vtx[2] - vertices[v[i]].vtx[2]) + (vertices[v[j]].vtx[3] - vertices[v[i]].vtx[3]));

                        clippedVertices[clippedIndex].interpolateVertex(percent, vertices[v[i]], vertices[v[j]]);

                        nearVertices[nearIndex].copyVertex(clippedVertices[clippedIndex]);
                        nearVertices[nearIndex].vtx[2] = -nearVertices[nearIndex].vtx[3];

                        clippedIndex++;
                        nearIndex++;
                    }

                    if (((vertices[v[i]].clip[2] < 0.0f) && (vertices[v[j]].clip[2] >= 0.0f)) ||
                            ((vertices[v[i]].clip[2] >= 0.0f) && (vertices[v[j]].clip[2] >= 0.0f))) {
                        clippedVertices[clippedIndex].copyVertex(vertices[v[j]]);
                        clippedIndex++;
                    } else {
                        nearVertices[nearIndex].copyVertex(vertices[v[j]]);
                        nearVertices[nearIndex].vtx[2] = -nearVertices[nearIndex].vtx[3];
                        nearIndex++;
                    }
                }

                OpenGlGdp.OGL_AddTriangle(clippedVertices[0].vtx, clippedVertices[0].color, clippedVertices[0].tex,
                        clippedVertices[1].vtx, clippedVertices[1].color, clippedVertices[1].tex,
                        clippedVertices[2].vtx, clippedVertices[2].color, clippedVertices[2].tex);

                if (clippedIndex == 4)
                    OpenGlGdp.OGL_AddTriangle(clippedVertices[0].vtx, clippedVertices[0].color, clippedVertices[0].tex,
                            clippedVertices[2].vtx, clippedVertices[2].color, clippedVertices[2].tex,
                            clippedVertices[3].vtx, clippedVertices[3].color, clippedVertices[3].tex);

                GL40.glDisable(GL40.GL_POLYGON_OFFSET_FILL);
                OpenGlGdp.OGL_AddTriangle(nearVertices[0].vtx, nearVertices[0].color, nearVertices[0].tex,
                        nearVertices[1].vtx, nearVertices[1].color, nearVertices[1].tex,
                        nearVertices[2].vtx, nearVertices[2].color, nearVertices[2].tex);
                if (nearIndex == 4)
                    OpenGlGdp.OGL_AddTriangle(nearVertices[0].vtx, nearVertices[0].color, nearVertices[0].tex,
                            nearVertices[2].vtx, nearVertices[2].color, nearVertices[2].tex,
                            nearVertices[3].vtx, nearVertices[3].color, nearVertices[3].tex);
                if (Gdp.RDP_GETOM_Z_MODE(Rsp.gdp.otherMode) == Gbi.ZMODE_DEC)
                    GL40.glEnable(GL40.GL_POLYGON_OFFSET_FILL);

            } else {
                OpenGlGdp.OGL_AddTriangle(vertices[v0].vtx, vertices[v0].color, vertices[v0].tex,
                        vertices[v1].vtx, vertices[v1].color, vertices[v1].tex,
                        vertices[v2].vtx, vertices[v2].color, vertices[v2].tex);
            }
        }

        Rsp.gdp.update();
    }

	public void gspCBFDVertex(int a, int n, int v0) {
		System.out.println("gSPCBFDVertex n = " + n + ", v0 =" + v0 + ", from " + a);
		
		if ((n + v0) > INDEXMAP_SIZE) {
			System.err.println("Using Vertex outside buffer v0=" + v0 + ", n=" + n);
			return;
		}
	}

	static boolean g_ConkerUcode;
	
	public static class texture {
		public static float scales, scalet;
		public static int level, on, tile;
	};
	
	public void setupFunctions() {
//		g_ConkerUcode = GBI.getMicrocodeType() == F3DEX2CBFD;
	}
}
