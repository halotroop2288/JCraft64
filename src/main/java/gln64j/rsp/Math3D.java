package gln64j.rsp;

public class Math3D {

    public static void copyMatrix(float[][] m0, float[][] m1) {
        m0[0][0] = m1[0][0];
        m0[0][1] = m1[0][1];
        m0[0][2] = m1[0][2];
        m0[0][3] = m1[0][3];
        m0[1][0] = m1[1][0];
        m0[1][1] = m1[1][1];
        m0[1][2] = m1[1][2];
        m0[1][3] = m1[1][3];
        m0[2][0] = m1[2][0];
        m0[2][1] = m1[2][1];
        m0[2][2] = m1[2][2];
        m0[2][3] = m1[2][3];
        m0[3][0] = m1[3][0];
        m0[3][1] = m1[3][1];
        m0[3][2] = m1[3][2];
        m0[3][3] = m1[3][3];
    }

    public static void multMatrix(float[][] m0, float[][] m1) {
        float tmp0 = m1[0][0] * m0[0][0] + m1[0][1] * m0[1][0] + m1[0][2] * m0[2][0] + m1[0][3] * m0[3][0];
        float tmp1 = m1[1][0] * m0[0][0] + m1[1][1] * m0[1][0] + m1[1][2] * m0[2][0] + m1[1][3] * m0[3][0];
        float tmp2 = m1[2][0] * m0[0][0] + m1[2][1] * m0[1][0] + m1[2][2] * m0[2][0] + m1[2][3] * m0[3][0];
        float tmp3 = m1[3][0] * m0[0][0] + m1[3][1] * m0[1][0] + m1[3][2] * m0[2][0] + m1[3][3] * m0[3][0];
        m0[0][0] = tmp0;
        m0[1][0] = tmp1;
        m0[2][0] = tmp2;
        m0[3][0] = tmp3;
        
        tmp0 = m1[0][0] * m0[0][1] + m1[0][1] * m0[1][1] + m1[0][2] * m0[2][1] + m1[0][3] * m0[3][1];
        tmp1 = m1[1][0] * m0[0][1] + m1[1][1] * m0[1][1] + m1[1][2] * m0[2][1] + m1[1][3] * m0[3][1];
        tmp2 = m1[2][0] * m0[0][1] + m1[2][1] * m0[1][1] + m1[2][2] * m0[2][1] + m1[2][3] * m0[3][1];
        tmp3 = m1[3][0] * m0[0][1] + m1[3][1] * m0[1][1] + m1[3][2] * m0[2][1] + m1[3][3] * m0[3][1];
        m0[0][1] = tmp0;
        m0[1][1] = tmp1;
        m0[2][1] = tmp2;
        m0[3][1] = tmp3;
        
        tmp0 = m1[0][0] * m0[0][2] + m1[0][1] * m0[1][2] + m1[0][2] * m0[2][2] + m1[0][3] * m0[3][2];
        tmp1 = m1[1][0] * m0[0][2] + m1[1][1] * m0[1][2] + m1[1][2] * m0[2][2] + m1[1][3] * m0[3][2];
        tmp2 = m1[2][0] * m0[0][2] + m1[2][1] * m0[1][2] + m1[2][2] * m0[2][2] + m1[2][3] * m0[3][2];
        tmp3 = m1[3][0] * m0[0][2] + m1[3][1] * m0[1][2] + m1[3][2] * m0[2][2] + m1[3][3] * m0[3][2];
        m0[0][2] = tmp0;
        m0[1][2] = tmp1;
        m0[2][2] = tmp2;
        m0[3][2] = tmp3;
        
        tmp0 = m1[0][0] * m0[0][3] + m1[0][1] * m0[1][3] + m1[0][2] * m0[2][3] + m1[0][3] * m0[3][3];
        tmp1 = m1[1][0] * m0[0][3] + m1[1][1] * m0[1][3] + m1[1][2] * m0[2][3] + m1[1][3] * m0[3][3];
        tmp2 = m1[2][0] * m0[0][3] + m1[2][1] * m0[1][3] + m1[2][2] * m0[2][3] + m1[2][3] * m0[3][3];
        tmp3 = m1[3][0] * m0[0][3] + m1[3][1] * m0[1][3] + m1[3][2] * m0[2][3] + m1[3][3] * m0[3][3];
        m0[0][3] = tmp0;
        m0[1][3] = tmp1;
        m0[2][3] = tmp2;
        m0[3][3] = tmp3;
    }

    public static void transpose3x3Matrix(float[][] mtx) {
        float eax = mtx[1][0];
        float ebx = mtx[0][1];
        mtx[1][0] = ebx;
        mtx[0][1] = eax;
        eax = mtx[2][0];
        ebx = mtx[0][2];
        mtx[2][0] = ebx;
        mtx[0][2] = eax;
        eax = mtx[1][2];
        ebx = mtx[2][1];
        mtx[1][2] = ebx;
        mtx[2][1] = eax;
    }
    
    public static void transformVertex(float[] vtx, float[][] mtx) { //, float perspNorm )
        float tmp0 = vtx[0] * mtx[0][0] + vtx[1] * mtx[1][0] + vtx[2] * mtx[2][0];
        float tmp1 = vtx[0] * mtx[0][1] + vtx[1] * mtx[1][1] + vtx[2] * mtx[2][1];
        float tmp2 = vtx[0] * mtx[0][2] + vtx[1] * mtx[1][2] + vtx[2] * mtx[2][2];
        float tmp3 = vtx[0] * mtx[0][3] + vtx[1] * mtx[1][3] + vtx[2] * mtx[2][3];
        vtx[0] = tmp0 + mtx[3][0];
        vtx[1] = tmp1 + mtx[3][1];
        vtx[2] = tmp2 + mtx[3][2];
        vtx[3] = tmp3 + mtx[3][3];
    }
    
    public static void transformVector(float[] vec, float[][] mtx) {
        float tmp0 = vec[0] * mtx[0][0] + vec[1] * mtx[1][0] + vec[2] * mtx[2][0];
        float tmp1 = vec[0] * mtx[0][1] + vec[1] * mtx[1][1] + vec[2] * mtx[2][1];
        float tmp2 = vec[0] * mtx[0][2] + vec[1] * mtx[1][2] + vec[2] * mtx[2][2];
        vec[0] = tmp0;
        vec[1] = tmp1;
        vec[2] = tmp2;
    }
    
    public static void normalize(float[] v) {
        float length = (float)StrictMath.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] /= length;
        v[1] /= length;
        v[2] /= length;
    }
    
    public static void normalize2D(float[] v) {
        float length = (float)StrictMath.hypot(v[0], v[1]);
        v[0] /= length;
        v[1] /= length;
    }
    
    public static float dotProduct(float[] v0, float[] v1) {
        return v0[0] * v1[0] + v0[1] * v1[1] + v0[2] * v1[2];
    }
    
}
