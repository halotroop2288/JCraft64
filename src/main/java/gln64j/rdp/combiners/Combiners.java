package gln64j.rdp.combiners;

import java.nio.FloatBuffer;
import javax.media.opengl.GL;

public class Combiners {

    public static final int TEXTURE_ENV = 0;
    public static final int TEXTURE_ENV_COMBINE = 1;
    public static final int NV_REGISTER_COMBINERS = 2;
    public static final int NV_TEXTURE_ENV_COMBINE4 = 3;

    public static final int G_CCMUX_COMBINED = 0;
    public static final int G_CCMUX_TEXEL0 = 1;
    public static final int G_CCMUX_TEXEL1 = 2;
    public static final int G_CCMUX_PRIMITIVE = 3;
    public static final int G_CCMUX_SHADE = 4;
    public static final int G_CCMUX_ENVIRONMENT = 5;
    public static final int G_CCMUX_CENTER = 6;
    public static final int G_CCMUX_SCALE = 6;
    public static final int G_CCMUX_COMBINED_ALPHA = 7;
    public static final int G_CCMUX_TEXEL0_ALPHA = 8;
    public static final int G_CCMUX_TEXEL1_ALPHA = 9;
    public static final int G_CCMUX_PRIMITIVE_ALPHA = 10;
    public static final int G_CCMUX_SHADE_ALPHA = 11;
    public static final int G_CCMUX_ENV_ALPHA = 12;
    public static final int G_CCMUX_LOD_FRACTION = 13;
    public static final int G_CCMUX_PRIM_LOD_FRAC = 14;
    public static final int G_CCMUX_NOISE = 7;
    public static final int G_CCMUX_K4 = 7;
    public static final int G_CCMUX_K5 = 15;
    public static final int G_CCMUX_1 = 6;
    public static final int G_CCMUX_0 = 31;

    public static final int G_ACMUX_COMBINED = 0;
    public static final int G_ACMUX_TEXEL0 = 1;
    public static final int G_ACMUX_TEXEL1 = 2;
    public static final int G_ACMUX_PRIMITIVE = 3;
    public static final int G_ACMUX_SHADE = 4;
    public static final int G_ACMUX_ENVIRONMENT = 5;
    public static final int G_ACMUX_LOD_FRACTION = 0;
    public static final int G_ACMUX_PRIM_LOD_FRAC = 6;
    public static final int G_ACMUX_1 = 6;
    public static final int G_ACMUX_0 = 7;

    public static final int SR_MASK_0 = 0;
    public static final int SR_MASK_1 = (0x01 << 1) - 1;
    public static final int SR_MASK_2 = (0x01 << 2) - 1;
    public static final int SR_MASK_3 = (0x01 << 3) - 1;
    public static final int SR_MASK_4 = (0x01 << 4) - 1;
    public static final int SR_MASK_5 = (0x01 << 5) - 1;
    public static final int SR_MASK_6 = (0x01 << 6) - 1;
    public static final int SR_MASK_7 = (0x01 << 7) - 1;
    public static final int SR_MASK_8 = (0x01 << 8) - 1;
    public static final int SR_MASK_9 = (0x01 << 9) - 1;
    public static final int SR_MASK_10 = (0x01 << 10) - 1;
    public static final int SR_MASK_11 = (0x01 << 11) - 1;
    public static final int SR_MASK_12 = (0x01 << 12) - 1;
    public static final int SR_MASK_13 = (0x01 << 13) - 1;
    public static final int SR_MASK_14 = (0x01 << 14) - 1;
    public static final int SR_MASK_15 = (0x01 << 15) - 1;
    public static final int SR_MASK_16 = (0x01 << 16) - 1;
    public static final int SR_MASK_17 = (0x01 << 17) - 1;
    public static final int SR_MASK_18 = (0x01 << 18) - 1;
    public static final int SR_MASK_19 = (0x01 << 19) - 1;
    public static final int SR_MASK_20 = (0x01 << 20) - 1;
    public static final int SR_MASK_21 = (0x01 << 21) - 1;
    public static final int SR_MASK_22 = (0x01 << 22) - 1;
    public static final int SR_MASK_23 = (0x01 << 23) - 1;
    public static final int SR_MASK_24 = (0x01 << 24) - 1;
    public static final int SR_MASK_25 = (0x01 << 25) - 1;
    public static final int SR_MASK_26 = (0x01 << 26) - 1;
    public static final int SR_MASK_27 = (0x01 << 27) - 1;
    public static final int SR_MASK_28 = (0x01 << 28) - 1;
    public static final int SR_MASK_29 = (0x01 << 29) - 1;
    public static final int SR_MASK_30 = (0x01 << 30) - 1;
    public static final int SR_MASK_31 = (0x01 << 31) - 1;

    public static final int LOAD = 0;
    public static final int SUB = 1;
    public static final int MUL = 2;
    public static final int ADD = 3;
    public static final int INTER = 4;

    public static final int COMBINED = 0;
    public static final int TEXEL0 = 1;
    public static final int TEXEL1 = 2;
    public static final int PRIMITIVE = 3;
    public static final int SHADE = 4;
    public static final int ENVIRONMENT = 5;
    public static final int CENTER = 6;
    public static final int SCALE = 7;
    public static final int COMBINED_ALPHA = 8;
    public static final int TEXEL0_ALPHA = 9;
    public static final int TEXEL1_ALPHA = 10;
    public static final int PRIMITIVE_ALPHA = 11;
    public static final int SHADE_ALPHA = 12;
    public static final int ENV_ALPHA = 13;
    public static final int LOD_FRACTION = 14;
    public static final int PRIM_LOD_FRAC = 15;
    public static final int NOISE = 16;
    public static final int K4 = 17;
    public static final int K5 = 18;
    public static final int ONE = 19;
    public static final int ZERO = 20;

    public static boolean EXT_fog_coord;        // TNT, GeForce, Rage 128, Radeon
    public static boolean ATI_texture_env_combine3;    // Radeon
    public static boolean ATIX_texture_env_route;    // Radeon
    public static boolean ARB_multitexture;        // TNT, GeForce, Rage 128, Radeon
    public static boolean ARB_texture_env_combine;    // GeForce, Rage 128, Radeon
    public static boolean ARB_texture_env_crossbar;    // Radeon (GeForce supports it, but doesn't report it)
    public static boolean EXT_texture_env_combine;    // TNT, GeForce, Rage 128, Radeon
    public static boolean EXT_secondary_color;        // GeForce, Radeon
    public static boolean NV_texture_env_combine4;    // TNT, GeForce
    public static boolean NV_register_combiners;    // GeForce
    public static int maxTextureUnits;                  // TNT = 2, GeForce = 2-4, Rage 128 = 2, Radeon = 3-6

    public interface CompiledCombiner {
        void set(GL gl, Combiners combiner);

        void updateColors(GL gl);
    }

    public static class CombinerOp {
        int op;
        int param1;
        int param2;
        int param3;
    }

    public static class CombinerStage {

        int numOps;
        CombinerOp[] op = new CombinerOp[6];

        public CombinerStage() {
            for (int i = 0; i < 6; i++)
                op[i] = new CombinerOp();
        }

        public void simplifyCycle(CombineCycle cc) {
            op[0].op = LOAD;
            op[0].param1 = cc.sa;
            numOps = 1;

            if (cc.sb != ZERO) {
                if (cc.sb == op[0].param1)
                    op[0].param1 = ZERO;
                else {
                    op[1].op = SUB;
                    op[1].param1 = cc.sb;
                    numOps++;
                }
            }

            if ((numOps > 1) || (op[0].param1 != ZERO)) {
                if (cc.m == ZERO) {
                    numOps = 1;
                    op[0].op = LOAD;
                    op[0].param1 = ZERO;
                } else {
                    if ((numOps == 1) && (op[0].param1 == ONE))
                        op[0].param1 = cc.m;
                    else {
                        op[numOps].op = MUL;
                        op[numOps].param1 = cc.m;
                        numOps++;
                    }
                }
            }

            if (cc.a != ZERO) {
                if ((numOps == 1) && (op[0].param1 == ZERO))
                    op[0].param1 = cc.a;
                else {
                    op[numOps].op = ADD;
                    op[numOps].param1 = cc.a;
                    numOps++;
                }
            }

            if ((numOps == 4) && (op[1].param1 == op[3].param1)) {
                numOps = 1;
                op[0].op = INTER;
                op[0].param2 = op[1].param1;
                op[0].param3 = op[2].param1;
            }
        }

    }

    ;

    public static class Combiner {

        public int numStages;
        public CombinerStage[] stage = new CombinerStage[2];

        public void mergeStages() {
            if ((stage[0].numOps == 1) && (stage[0].op[0].op == LOAD)) {
                int combined = stage[0].op[0].param1;

                for (int i = 0; i < stage[1].numOps; i++) {
                    stage[0].op[i].op = stage[1].op[i].op;
                    stage[0].op[i].param1 = (stage[1].op[i].param1 == COMBINED) ? combined : stage[1].op[i].param1;
                    stage[0].op[i].param2 = (stage[1].op[i].param2 == COMBINED) ? combined : stage[1].op[i].param2;
                    stage[0].op[i].param3 = (stage[1].op[i].param3 == COMBINED) ? combined : stage[1].op[i].param3;
                }

                stage[0].numOps = stage[1].numOps;
                numStages = 1;
            } else if (stage[1].op[0].op != INTER) {
                int numCombined = 0;

                for (int i = 0; i < stage[1].numOps; i++)
                    if (stage[1].op[i].param1 == COMBINED)
                        numCombined++;

                if (numCombined == 0) {
                    for (int i = 0; i < stage[1].numOps; i++) {
                        stage[0].op[i].op = stage[1].op[i].op;
                        stage[0].op[i].param1 = stage[1].op[i].param1;
                        stage[0].op[i].param2 = stage[1].op[i].param2;
                        stage[0].op[i].param3 = stage[1].op[i].param3;
                    }
                    stage[0].numOps = stage[1].numOps;

                    numStages = 1;
                } else if (numCombined == 1) {
                    if (stage[1].op[0].param1 == COMBINED) {
                        for (int i = 1; i < stage[1].numOps; i++) {
                            stage[0].op[stage[0].numOps].op = stage[1].op[i].op;
                            stage[0].op[stage[0].numOps].param1 = stage[1].op[i].param1;
                            stage[0].numOps++;
                        }

                        numStages = 1;
                    } else if ((stage[1].op[1].param1 == COMBINED) && (stage[1].op[1].op != SUB)) {
                        stage[0].op[stage[0].numOps].op = stage[1].op[1].op;
                        stage[0].op[stage[0].numOps].param1 = stage[1].op[0].param1;
                        stage[0].numOps++;

                        if (stage[1].numOps > 2) {
                            stage[0].op[stage[0].numOps].op = stage[1].op[2].op;
                            stage[0].op[stage[0].numOps].param1 = stage[1].op[2].param1;
                            stage[0].numOps++;
                        }

                        numStages = 1;
                    }
                }
            }
        }

    }

    ;

    public static class CachedCombiner {

        public gDPCombine combine = new gDPCombine();
        public CompiledCombiner compiled;
        public CachedCombiner left;
        public CachedCombiner right;

    }

    public static class Vertex {
        public int color;
        public int secondaryColor;
        public int alpha;
    }

    public static class CombineCycle {
        int sa, sb, m, a;
    }

    public static class PrimColor {
        public int m;
        public float l, r, g, b, a;
    }

    public static class gDPCombine {
        public int aA1;    //: 3;
        public int sbA1;    //: 3;
        public int aRGB1;    //: 3;
        public int aA0;    //: 3;
        public int sbA0;    //: 3;
        public int aRGB0;    //: 3;
        public int mA1;    //: 3;
        public int saA1;    //: 3;
        public int sbRGB1;    //: 4;
        public int sbRGB0;    //: 4;
        public int mRGB1;    //: 5;
        public int saRGB1;    //: 4;
        public int mA0;    //: 3;
        public int saA0;    //: 3;
        public int mRGB0;    //: 5;
        public int saRGB0;    //: 4;

        public int pad8;

        public void setMux(long value) {
            setMuxs0((int) (value >> 32));
            setMuxs1((int) value);
        }

        public long getMux() {
            return (((long) getMuxs0()) << 32) | (getMuxs1() & 0xFFFFFFFFL);
        }

        public void setMuxs1(int value) {
            sbRGB0 = (value >> 28) & 15;
            sbRGB1 = (value >> 24) & 15;
            saA1 = (value >> 21) & 7;
            mA1 = (value >> 18) & 7;
            aRGB0 = (value >> 15) & 7;
            sbA0 = (value >> 12) & 7;
            aA0 = (value >> 9) & 7;
            aRGB1 = (value >> 6) & 7;
            sbA1 = (value >> 3) & 7;
            aA1 = (value) & 7;
        }

        public int getMuxs1() {
            return ((sbRGB0 & 15) << 28)
                    | ((sbRGB1 & 15) << 24) | ((saA1 & 7) << 21) | ((mA1 & 7) << 18) | ((aRGB0 & 7) << 15)
                    | ((sbA0 & 7) << 12) | ((aA0 & 7) << 9) | ((aRGB1 & 7) << 6) | ((sbA1 & 7) << 3)
                    | (aA1 & 7);
        }

        public void setMuxs0(int value) {
            pad8 = (value >> 24) & 0xFF;
            saRGB0 = (value >> 20) & 15;
            mRGB0 = (value >> 15) & 31;
            saA0 = (value >> 12) & 7;
            mA0 = (value >> 9) & 7;
            saRGB1 = (value >> 5) & 15;
            mRGB1 = (value) & 31;
        }

        public int getMuxs0() {
            return ((pad8 & 0xFF) << 24) | ((saRGB0 & 15) << 20)
                    | ((mRGB0 & 31) << 15) | ((saA0 & 7) << 12) | ((mA0 & 7) << 9)
                    | ((saRGB1 & 15) << 5) | (mRGB1 & 31);
        }
    }

    ;

    private static int saRGBExpanded[] = {
            COMBINED, TEXEL0, TEXEL1, PRIMITIVE,
            SHADE, ENVIRONMENT, ONE, NOISE,
            ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO
    };

    private static int sbRGBExpanded[] = {
            COMBINED, TEXEL0, TEXEL1, PRIMITIVE,
            SHADE, ENVIRONMENT, CENTER, K4,
            ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO
    };

    private static int mRGBExpanded[] = {
            COMBINED, TEXEL0, TEXEL1, PRIMITIVE,
            SHADE, ENVIRONMENT, SCALE, COMBINED_ALPHA,
            TEXEL0_ALPHA, TEXEL1_ALPHA, PRIMITIVE_ALPHA, SHADE_ALPHA,
            ENV_ALPHA, LOD_FRACTION, PRIM_LOD_FRAC, K5,
            ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO,
            ZERO, ZERO, ZERO, ZERO
    };

    private static int aRGBExpanded[] = {
            COMBINED, TEXEL0, TEXEL1, PRIMITIVE,
            SHADE, ENVIRONMENT, ONE, ZERO
    };

    private static int saAExpanded[] = {
            COMBINED, TEXEL0_ALPHA, TEXEL1_ALPHA, PRIMITIVE_ALPHA,
            SHADE_ALPHA, ENV_ALPHA, ONE, ZERO
    };

    private static int sbAExpanded[] = {
            COMBINED, TEXEL0_ALPHA, TEXEL1_ALPHA, PRIMITIVE_ALPHA,
            SHADE_ALPHA, ENV_ALPHA, ONE, ZERO
    };

    private static int mAExpanded[] = {
            LOD_FRACTION, TEXEL0_ALPHA, TEXEL1_ALPHA, PRIMITIVE_ALPHA,
            SHADE_ALPHA, ENV_ALPHA, PRIM_LOD_FRAC, ZERO,
    };

    private static int aAExpanded[] = {
            COMBINED, TEXEL0_ALPHA, TEXEL1_ALPHA, PRIMITIVE_ALPHA,
            SHADE_ALPHA, ENV_ALPHA, ONE, ZERO
    };

    private static int CCEncodeA[] = {
            0, 1, 2, 3, 4, 5, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 7, 15, 15, 6, 15
    };

    private static int CCEncodeB[] = {
            0, 1, 2, 3, 4, 5, 6, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 7, 15, 15, 15
    };

    private static int CCEncodeC[] = {
            0, 1, 2, 3, 4, 5, 31, 6, 7, 8, 9, 10, 11, 12, 13, 14, 31, 31, 15, 31, 31
    };

    private static int CCEncodeD[] = {
            0, 1, 2, 3, 4, 5, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 6, 15
    };

    private static long ACEncodeA[] = {
            7, 7, 7, 7, 7, 7, 7, 7, 0, 1, 2, 3, 4, 5, 7, 7, 7, 7, 7, 6, 7
    };

    private static long ACEncodeB[] = {
            7, 7, 7, 7, 7, 7, 7, 7, 0, 1, 2, 3, 4, 5, 7, 7, 7, 7, 7, 6, 7
    };

    private static long ACEncodeC[] = {
            7, 7, 7, 7, 7, 7, 7, 7, 0, 1, 2, 3, 4, 5, 7, 6, 7, 7, 7, 7, 7
    };

    private static long ACEncodeD[] = {
            7, 7, 7, 7, 7, 7, 7, 7, 0, 1, 2, 3, 4, 5, 7, 7, 7, 7, 7, 6, 7
    };

    public Vertex vertex = new Vertex();
    public CachedCombiner root;
    public CachedCombiner current;
    private int compiler;
    public boolean usesT0;
    public boolean usesT1;
    public boolean usesNoise;

    public static float[] envColor = new float[4];
    public static PrimColor primColor = new PrimColor();

    public gDPCombine combine = new gDPCombine();

    public void init() {
        if (NV_register_combiners) {
            compiler = NV_REGISTER_COMBINERS;
            NVRegisterCombiner.init();
        } else if (EXT_texture_env_combine || ARB_texture_env_combine) {
            compiler = TEXTURE_ENV_COMBINE;
            TextureEnvCombiner.init();
        } else {
            compiler = TEXTURE_ENV;
            TextureEnv.init();
        }
        root = null;
    }

    public void updateCombineColors(GL gl) {
        current.compiled.updateColors(gl);
    }

    public void endTextureUpdate(GL gl) {
        current.compiled.set(gl, this);
    }

    public void setCombine(GL gl, boolean twocycle, long mux) {
        selectCombine(twocycle, mux);
        setCombineStates(gl);
    }

    public void beginTextureUpdate(GL gl) {
        if (compiler == TEXTURE_ENV_COMBINE) {
            TextureEnvCombiner.BeginTextureUpdate_texture_env_combine(gl);
        }
    }

    private void selectCombine(boolean twocycle, long mux) {
        CachedCombiner comb = root;
        CachedCombiner parent = comb;

        while (comb != null) {
            parent = comb;

            if (mux == comb.combine.getMux())
                break;
            else if (mux < comb.combine.getMux())
                comb = comb.left;
            else
                comb = comb.right;
        }

        if (comb == null) {
            comb = compile(twocycle, mux);

            if (parent == null)
                root = comb;
            else if (parent.combine.getMux() > comb.combine.getMux())
                parent.left = comb;
            else
                parent.right = comb;
        }

        current = comb;
    }

    private void setCombineStates(GL gl) {
        current.compiled.set(gl, this);
    }

    private CachedCombiner compile(boolean twocycle, long mux) {
        gDPCombine newcombine = new gDPCombine();

        newcombine.setMux(mux);

        int numCycles;

        Combiner color = new Combiner();
        Combiner alpha = new Combiner();

        if (twocycle) {
            numCycles = 2;
            color.numStages = 2;
            alpha.numStages = 2;
        } else {
            numCycles = 1;
            color.numStages = 1;
            alpha.numStages = 1;
        }

        CombineCycle[] cc = new CombineCycle[2];
        CombineCycle[] ac = new CombineCycle[2];

        cc[0] = new CombineCycle();
        cc[0].sa = saRGBExpanded[newcombine.saRGB0];
        cc[0].sb = sbRGBExpanded[newcombine.sbRGB0];
        cc[0].m = mRGBExpanded[newcombine.mRGB0];
        cc[0].a = aRGBExpanded[newcombine.aRGB0];
        ac[0] = new CombineCycle();
        ac[0].sa = saAExpanded[newcombine.saA0];
        ac[0].sb = sbAExpanded[newcombine.sbA0];
        ac[0].m = mAExpanded[newcombine.mA0];
        ac[0].a = aAExpanded[newcombine.aA0];

        cc[1] = new CombineCycle();
        cc[1].sa = saRGBExpanded[newcombine.saRGB1];
        cc[1].sb = sbRGBExpanded[newcombine.sbRGB1];
        cc[1].m = mRGBExpanded[newcombine.mRGB1];
        cc[1].a = aRGBExpanded[newcombine.aRGB1];
        ac[1] = new CombineCycle();
        ac[1].sa = saAExpanded[newcombine.saA1];
        ac[1].sb = sbAExpanded[newcombine.sbA1];
        ac[1].m = mAExpanded[newcombine.mA1];
        ac[1].a = aAExpanded[newcombine.aA1];

        for (int i = 0; i < numCycles; i++) {
            color.stage[i] = new CombinerStage();
            alpha.stage[i] = new CombinerStage();
            color.stage[i].simplifyCycle(cc[i]);
            alpha.stage[i].simplifyCycle(ac[i]);
        }

        if (numCycles == 2) {
            color.mergeStages();
            alpha.mergeStages();
        }

        CachedCombiner cached = new CachedCombiner();

        cached.combine.setMux(newcombine.getMux());
        cached.left = null;
        cached.right = null;

        switch (compiler) {
            case TEXTURE_ENV -> cached.compiled = new TextureEnv(color, alpha);
            case TEXTURE_ENV_COMBINE -> cached.compiled = new TextureEnvCombiner(color, alpha);
            case NV_REGISTER_COMBINERS -> cached.compiled = new NVRegisterCombiner(color, alpha);
        }

        return cached;
    }

    // called by OpenGl, FrameBuffers
    public final long encodeCombineMode(
            int ccmux_a0, int ccmux_b0, int ccmux_c0, int ccmux_d0,
            int acmux_a0, int acmux_b0, int acmux_c0, int acmux_d0,
            int ccmux_a1, int ccmux_b1, int ccmux_c1, int ccmux_d1,
            int acmux_a1, int acmux_b1, int acmux_c1, int acmux_d1) {
        return ((long) (((ccmux_a0 & SR_MASK_4) << 20) | ((ccmux_c0 & SR_MASK_5) << 15) |
                ((acmux_a0 & SR_MASK_3) << 12) | ((acmux_c0 & SR_MASK_3) << 9) |
                ((ccmux_a1 & SR_MASK_4) << 5) | ((ccmux_c1 & SR_MASK_5))) << 32) |
                (long) (((ccmux_b0 & SR_MASK_4) << 28) | ((ccmux_d0 & SR_MASK_3) << 15) |
                        ((acmux_b0 & SR_MASK_3) << 12) | ((acmux_d0 & SR_MASK_3) << 9) |
                        ((ccmux_b1 & SR_MASK_4) << 24) | ((acmux_a1 & SR_MASK_3) << 21) |
                        ((acmux_c1 & SR_MASK_3) << 18) | ((ccmux_d1 & SR_MASK_3) << 6) |
                        ((acmux_b1 & SR_MASK_3) << 3) | ((acmux_d1 & SR_MASK_3)));
    }

    // called by OpenGl, TextureEnvCombine
    public final void setConstant(FloatBuffer constant, int color, int alpha) {
        switch (color) {
            case PRIMITIVE:
                constant.put(0, primColor.r);
                constant.put(1, primColor.g);
                constant.put(2, primColor.b);
                break;
            case ENVIRONMENT:
                constant.put(0, envColor[0]);
                constant.put(1, envColor[1]);
                constant.put(2, envColor[2]);
                break;
            case PRIMITIVE_ALPHA:
                constant.put(0, primColor.a);
                constant.put(1, primColor.a);
                constant.put(2, primColor.a);
                break;
            case ENV_ALPHA:
                constant.put(0, envColor[3]);
                constant.put(1, envColor[3]);
                constant.put(2, envColor[3]);
                break;
            case PRIM_LOD_FRAC:
                constant.put(0, primColor.l);
                constant.put(1, primColor.l);
                constant.put(2, primColor.l);
                break;
            case ONE:
                constant.put(0, 1.0f);
                constant.put(1, 1.0f);
                constant.put(2, 1.0f);
                break;
            case ZERO:
                constant.put(0, 0.0f);
                constant.put(1, 0.0f);
                constant.put(2, 0.0f);
                break;
        }
        switch (alpha) {
            case PRIMITIVE_ALPHA:
                constant.put(3, primColor.a);
                break;
            case ENV_ALPHA:
                constant.put(3, envColor[3]);
                break;
            case PRIM_LOD_FRAC:
                constant.put(3, primColor.l);
                break;
            case ONE:
                constant.put(3, 1.0f);
                break;
            case ZERO:
                constant.put(3, 0.0f);
                break;
        }
    }

    public final void setConstant(float[] constant, int color, int alpha) {
        switch (color) {
            case PRIMITIVE -> {
                constant[0] = primColor.r;
                constant[1] = primColor.g;
                constant[2] = primColor.b;
            }
            case ENVIRONMENT -> {
                constant[0] = envColor[0];
                constant[1] = envColor[1];
                constant[2] = envColor[2];
            }
            case PRIMITIVE_ALPHA -> {
                constant[0] = primColor.a;
                constant[1] = primColor.a;
                constant[2] = primColor.a;
            }
            case ENV_ALPHA -> {
                constant[0] = envColor[3];
                constant[1] = envColor[3];
                constant[2] = envColor[3];
            }
            case PRIM_LOD_FRAC -> {
                constant[0] = primColor.l;
                constant[1] = primColor.l;
                constant[2] = primColor.l;
            }
            case ONE -> {
                constant[0] = 1.0f;
                constant[1] = 1.0f;
                constant[2] = 1.0f;
            }
            case ZERO -> {
                constant[0] = 0.0f;
                constant[1] = 0.0f;
                constant[2] = 0.0f;
            }
        }
        switch (alpha) {
            case PRIMITIVE_ALPHA -> constant[3] = primColor.a;
            case ENV_ALPHA -> constant[3] = envColor[3];
            case PRIM_LOD_FRAC -> constant[3] = primColor.l;
            case ONE -> constant[3] = 1.0f;
            case ZERO -> constant[3] = 0.0f;
        }
    }

}
