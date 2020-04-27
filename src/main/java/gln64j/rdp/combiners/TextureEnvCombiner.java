package gln64j.rdp.combiners;

import java.nio.FloatBuffer;
import javax.media.opengl.GL;

public class TextureEnvCombiner implements Combiners.CompiledCombiner {

    public static final int GL_SECONDARY_COLOR_ATIX = 0x8747;
    public static final int GL_TEXTURE_OUTPUT_RGB_ATIX = 0x8748;
    public static final int GL_TEXTURE_OUTPUT_ALPHA_ATIX = 0x8749;

    private static class TexEnvCombinerArg {
        int source, operand;

        public TexEnvCombinerArg(int source, int operand) {
            this.source = source;
            this.operand = operand;
        }
    }

    private static class TexEnvCombinerStage {
        int constant;
        boolean used;
        int combine;
        TexEnvCombinerArg[] arg = new TexEnvCombinerArg[3];
        int outputTexture;

        public TexEnvCombinerStage() {
            for (int i = 0; i < arg.length; i++)
                arg[i] = new TexEnvCombinerArg(0, 0);
        }
    }

    private static TexEnvCombinerArg[] TexEnvArgs = {
            // CMB
            new TexEnvCombinerArg(GL.GL_PREVIOUS, GL.GL_SRC_COLOR),
            // T0
            new TexEnvCombinerArg(GL.GL_TEXTURE, GL.GL_SRC_COLOR),
            // T1
            new TexEnvCombinerArg(GL.GL_TEXTURE, GL.GL_SRC_COLOR),
            // PRIM
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // SHADE
            new TexEnvCombinerArg(GL.GL_PRIMARY_COLOR, GL.GL_SRC_COLOR),
            // ENV
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // CENTER
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // SCALE
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // CMBALPHA
            new TexEnvCombinerArg(GL.GL_PREVIOUS, GL.GL_SRC_ALPHA),
            // T0ALPHA
            new TexEnvCombinerArg(GL.GL_TEXTURE, GL.GL_SRC_ALPHA),
            // T1ALPHA
            new TexEnvCombinerArg(GL.GL_TEXTURE, GL.GL_SRC_ALPHA),
            // PRIMALPHA
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_ALPHA),
            // SHADEALPHA
            new TexEnvCombinerArg(GL.GL_PRIMARY_COLOR, GL.GL_SRC_ALPHA),
            // ENVALPHA
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // LODFRAC
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // PRIMLODFRAC
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // NOISE
            new TexEnvCombinerArg(GL.GL_TEXTURE, GL.GL_SRC_COLOR),
            // K4
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // K5
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // ONE
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR),
            // ZERO
            new TexEnvCombinerArg(GL.GL_CONSTANT, GL.GL_SRC_COLOR)
    };

    private boolean usesT0, usesT1, usesNoise;

    private int usedUnits;

    private static class Vertex {
        int color, secondaryColor, alpha;
    }

    private Vertex vertex = new Vertex();

    private TexEnvCombinerStage[] color = new TexEnvCombinerStage[8];
    private TexEnvCombinerStage[] alpha = new TexEnvCombinerStage[8];

    private Combiners combiner;

    public TextureEnvCombiner(Combiners.Combiner c, Combiners.Combiner a) {
        for (int i = 0; i < color.length; i++)
            color[i] = new TexEnvCombinerStage();
        for (int i = 0; i < alpha.length; i++)
            alpha[i] = new TexEnvCombinerStage();

        int curUnit, combinedUnit;

        for (int i = 0; i < Combiners.maxTextureUnits; i++) {
            color[i].combine = GL.GL_REPLACE;
            alpha[i].combine = GL.GL_REPLACE;

            SetColorCombinerValues(i, 0, GL.GL_PREVIOUS, GL.GL_SRC_COLOR);
            SetColorCombinerValues(i, 1, GL.GL_PREVIOUS, GL.GL_SRC_COLOR);
            SetColorCombinerValues(i, 2, GL.GL_PREVIOUS, GL.GL_SRC_COLOR);
            color[i].constant = Combiners.COMBINED;
            color[i].outputTexture = GL.GL_TEXTURE0 + i;

            SetAlphaCombinerValues(i, 0, GL.GL_PREVIOUS, GL.GL_SRC_ALPHA);
            SetAlphaCombinerValues(i, 1, GL.GL_PREVIOUS, GL.GL_SRC_ALPHA);
            SetAlphaCombinerValues(i, 2, GL.GL_PREVIOUS, GL.GL_SRC_ALPHA);
            alpha[i].constant = Combiners.COMBINED;
            alpha[i].outputTexture = GL.GL_TEXTURE0 + i;
        }

        usesT0 = false;
        usesT1 = false;

        vertex.color = Combiners.COMBINED;
        vertex.secondaryColor = Combiners.COMBINED;
        vertex.alpha = Combiners.COMBINED;

        curUnit = 0;

        for (int i = 0; i < a.numStages; i++) {
            for (int j = 0; j < a.stage[i].numOps; j++) {
                float sb = 0.0f;

                if (a.stage[i].op[j].param1 == Combiners.PRIMITIVE_ALPHA)
                    sb = Combiners.primColor.a;
                else if (a.stage[i].op[j].param1 == Combiners.ENV_ALPHA)
                    sb = Combiners.envColor[3];
                else if (a.stage[i].op[j].param1 == Combiners.ONE)
                    sb = 1.0f;

                if (((a.stage[i].numOps - j) >= 3) &&
                        (a.stage[i].op[j].op == Combiners.SUB) &&
                        (a.stage[i].op[j + 1].op == Combiners.MUL) &&
                        (a.stage[i].op[j + 2].op == Combiners.ADD) &&
                        (sb > 0.5f) &&
                        (Combiners.ARB_texture_env_combine)) {
                    usesT0 |= a.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA;
                    usesT1 |= a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA;

                    if (a.stage[i].op[j].param1 == Combiners.ONE) {
                        SetAlphaCombinerValues(curUnit, 0, alpha[curUnit].arg[0].source, GL.GL_ONE_MINUS_SRC_ALPHA);
                    } else {
                        alpha[curUnit].combine = GL.GL_SUBTRACT;
                        SetAlphaCombinerValues(curUnit, 1, alpha[curUnit].arg[0].source, GL.GL_SRC_ALPHA);
                        SetAlphaCombinerArg(curUnit, 0, a.stage[i].op[j].param1);

                        curUnit++;
                    }

                    j++;

                    usesT0 |= a.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA;
                    usesT1 |= a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA;

                    alpha[curUnit].combine = GL.GL_MODULATE;
                    SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);

                    curUnit++;
                    j++;

                    usesT0 |= a.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA;
                    usesT1 |= a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA;

                    alpha[curUnit].combine = GL.GL_SUBTRACT;
                    SetAlphaCombinerArg(curUnit, 0, a.stage[i].op[j].param1);

                    curUnit++;
                } else {
                    usesT0 |= a.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA;
                    usesT1 |= a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA;

                    switch (a.stage[i].op[j].op) {
                        case Combiners.LOAD -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    (a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA) && (curUnit == 0))
                                curUnit++;
                            alpha[curUnit].combine = GL.GL_REPLACE;
                            SetAlphaCombinerArg(curUnit, 0, a.stage[i].op[j].param1);
                        }
                        case Combiners.SUB -> {
                            if (!Combiners.ARB_texture_env_combine)
                                break;
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    (a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA) && (curUnit == 0))
                                curUnit++;
                            if ((j > 0) && (a.stage[i].op[j - 1].op == Combiners.LOAD) && (a.stage[i].op[j - 1].param1 == Combiners.ONE)) {
                                SetAlphaCombinerArg(curUnit, 0, a.stage[i].op[j].param1);
                                alpha[curUnit].arg[0].operand = GL.GL_ONE_MINUS_SRC_ALPHA;
                            } else if ((Combiners.ATI_texture_env_combine3) && (curUnit > 0) && (alpha[curUnit - 1].combine == GL.GL_MODULATE)) {
                                curUnit--;
                                SetAlphaCombinerValues(curUnit, 2, alpha[curUnit].arg[1].source, alpha[curUnit].arg[1].operand);
                                alpha[curUnit].combine = GL.GL_MODULATE_SUBTRACT_ATI;
                                SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);
                                curUnit++;
                            } else {
                                alpha[curUnit].combine = GL.GL_SUBTRACT;
                                SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);
                                curUnit++;
                            }
                        }
                        case Combiners.MUL -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    (a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA) && (curUnit == 0))
                                curUnit++;
                            alpha[curUnit].combine = GL.GL_MODULATE;
                            SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);
                            curUnit++;
                        }
                        case Combiners.ADD -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    (a.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA) && (curUnit == 0))
                                curUnit++;
                            if ((Combiners.ATI_texture_env_combine3) && (curUnit > 0) && (alpha[curUnit - 1].combine == GL.GL_MODULATE)) {
                                curUnit--;
                                SetAlphaCombinerValues(curUnit, 2, alpha[curUnit].arg[1].source, alpha[curUnit].arg[1].operand);
                                alpha[curUnit].combine = GL.GL_MODULATE_ADD_ATI;
                                SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);
                            } else {
                                alpha[curUnit].combine = GL.GL_ADD;
                                SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param1);
                            }
                            curUnit++;
                        }
                        case Combiners.INTER -> {
                            usesT0 |= (a.stage[i].op[j].param2 == Combiners.TEXEL0_ALPHA) || (a.stage[i].op[j].param3 == Combiners.TEXEL0_ALPHA);
                            usesT1 |= (a.stage[i].op[j].param2 == Combiners.TEXEL1_ALPHA) || (a.stage[i].op[j].param3 == Combiners.TEXEL1_ALPHA);
                            alpha[curUnit].combine = GL.GL_INTERPOLATE;
                            SetAlphaCombinerArg(curUnit, 0, a.stage[i].op[j].param1);
                            SetAlphaCombinerArg(curUnit, 1, a.stage[i].op[j].param2);
                            SetAlphaCombinerArg(curUnit, 2, a.stage[i].op[j].param3);
                            curUnit++;
                        }
                    }
                }
            }
        }

        usedUnits = StrictMath.max(curUnit, 1);

        curUnit = 0;
        for (int i = 0; i < c.numStages; i++) {
            for (int j = 0; j < c.stage[i].numOps; j++) {
                float sb = 0.0f;

                if (c.stage[i].op[j].param1 == Combiners.PRIMITIVE)
                    sb = (Combiners.primColor.r + Combiners.primColor.b + Combiners.primColor.g) / 3.0f;
                else if (c.stage[i].op[j].param1 == Combiners.ENVIRONMENT)
                    sb = (Combiners.envColor[0] + Combiners.envColor[1] + Combiners.envColor[2]) / 3.0f;

                if (((c.stage[i].numOps - j) >= 3) &&
                        (c.stage[i].op[j].op == Combiners.SUB) &&
                        (c.stage[i].op[j + 1].op == Combiners.MUL) &&
                        (c.stage[i].op[j + 2].op == Combiners.ADD) &&
                        (sb > 0.5f) &&
                        (Combiners.ARB_texture_env_combine)) {
                    usesT0 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL0) || (c.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA));
                    usesT1 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA));

                    color[curUnit].combine = GL.GL_SUBTRACT;
                    SetColorCombinerValues(curUnit, 1, color[curUnit].arg[0].source, color[curUnit].arg[0].operand);
                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);

                    curUnit++;
                    j++;

                    usesT0 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL0) || (c.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA));
                    usesT1 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA));

                    color[curUnit].combine = GL.GL_MODULATE;
                    SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);

                    curUnit++;
                    j++;

                    usesT0 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL0) || (c.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA));
                    usesT1 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA));

                    color[curUnit].combine = GL.GL_SUBTRACT;
                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);

                    curUnit++;
                } else {
                    usesT0 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL0) || (c.stage[i].op[j].param1 == Combiners.TEXEL0_ALPHA));
                    usesT1 |= ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA));

                    switch (c.stage[i].op[j].op) {
                        case Combiners.LOAD -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA)) && (curUnit == 0))
                                curUnit++;
                            color[curUnit].combine = GL.GL_REPLACE;
                            SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);
                        }
                        case Combiners.SUB -> {
                            if (!Combiners.ARB_texture_env_combine)
                                break;
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA)) && (curUnit == 0))
                                curUnit++;
                            if ((j > 0) && (c.stage[i].op[j - 1].op == Combiners.LOAD) && (c.stage[i].op[j - 1].param1 == Combiners.ONE)) {
                                SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);
                                color[curUnit].arg[0].operand = GL.GL_ONE_MINUS_SRC_COLOR;
                            } else if ((Combiners.ATI_texture_env_combine3) && (curUnit > 0) && (color[curUnit - 1].combine == GL.GL_MODULATE)) {
                                curUnit--;
                                SetColorCombinerValues(curUnit, 2, color[curUnit].arg[1].source, color[curUnit].arg[1].operand);
                                color[curUnit].combine = GL.GL_MODULATE_SUBTRACT_ATI;
                                SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);
                                curUnit++;
                            } else {
                                color[curUnit].combine = GL.GL_SUBTRACT;
                                SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);
                                curUnit++;
                            }
                        }
                        case Combiners.MUL -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA)) && (curUnit == 0))
                                curUnit++;
                            color[curUnit].combine = GL.GL_MODULATE;
                            SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);
                            curUnit++;
                        }
                        case Combiners.ADD -> {
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param1 == Combiners.TEXEL1_ALPHA)) && (curUnit == 0))
                                curUnit++;
                            if ((Combiners.ATI_texture_env_combine3) && (curUnit > 0) && (color[curUnit - 1].combine == GL.GL_MODULATE)) {
                                curUnit--;
                                SetColorCombinerValues(curUnit, 2, color[curUnit].arg[1].source, color[curUnit].arg[1].operand);
                                color[curUnit].combine = GL.GL_MODULATE_ADD_ATI;
                                SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);
                            } else {
                                color[curUnit].combine = GL.GL_ADD;
                                SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param1);
                            }
                            curUnit++;
                        }
                        case Combiners.INTER -> {
                            usesT0 |= (c.stage[i].op[j].param2 == Combiners.TEXEL0) || (c.stage[i].op[j].param3 == Combiners.TEXEL0) || (c.stage[i].op[j].param3 == Combiners.TEXEL0_ALPHA);
                            usesT1 |= (c.stage[i].op[j].param2 == Combiners.TEXEL1) || (c.stage[i].op[j].param3 == Combiners.TEXEL1) || (c.stage[i].op[j].param3 == Combiners.TEXEL1_ALPHA);
                            if (!(Combiners.ARB_texture_env_crossbar || Combiners.NV_texture_env_combine4) &&
                                    ((c.stage[i].op[j].param1 == Combiners.TEXEL1) || (c.stage[i].op[j].param2 == Combiners.TEXEL1) || (c.stage[i].op[j].param3 == Combiners.TEXEL1) || (c.stage[i].op[j].param3 == Combiners.TEXEL1_ALPHA)) && (curUnit == 0)) {
                                if (c.stage[i].op[j].param1 == Combiners.TEXEL0) {
                                    color[curUnit].combine = GL.GL_REPLACE;
                                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);
                                    c.stage[i].op[j].param1 = Combiners.COMBINED;
                                }
                                if (c.stage[i].op[j].param2 == Combiners.TEXEL0) {
                                    color[curUnit].combine = GL.GL_REPLACE;
                                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param2);

                                    c.stage[i].op[j].param2 = Combiners.COMBINED;
                                }
                                if (c.stage[i].op[j].param3 == Combiners.TEXEL0) {
                                    color[curUnit].combine = GL.GL_REPLACE;
                                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param3);
                                    c.stage[i].op[j].param3 = Combiners.COMBINED;
                                }
                                if (c.stage[i].op[j].param3 == Combiners.TEXEL0_ALPHA) {
                                    color[curUnit].combine = GL.GL_REPLACE;
                                    SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param3);
                                    c.stage[i].op[j].param3 = Combiners.COMBINED_ALPHA;
                                }

                                curUnit++;
                            }
                            color[curUnit].combine = GL.GL_INTERPOLATE;
                            SetColorCombinerArg(curUnit, 0, c.stage[i].op[j].param1);
                            SetColorCombinerArg(curUnit, 1, c.stage[i].op[j].param2);
                            SetColorCombinerArg(curUnit, 2, c.stage[i].op[j].param3);
                            curUnit++;
                        }
                    }
                }
            }
        }

        usedUnits = StrictMath.max(curUnit, usedUnits);
    }

    public static void init() {

        if ((Combiners.ARB_texture_env_crossbar) || (Combiners.NV_texture_env_combine4) || (Combiners.ATIX_texture_env_route)) {
            TexEnvArgs[Combiners.TEXEL0].source = GL.GL_TEXTURE0;
            TexEnvArgs[Combiners.TEXEL0_ALPHA].source = GL.GL_TEXTURE0;

            TexEnvArgs[Combiners.TEXEL1].source = GL.GL_TEXTURE1;
            TexEnvArgs[Combiners.TEXEL1_ALPHA].source = GL.GL_TEXTURE1;
        }

        if (Combiners.ATI_texture_env_combine3) {
            TexEnvArgs[Combiners.ONE].source = GL.GL_ONE;
            TexEnvArgs[Combiners.ZERO].source = GL.GL_ZERO;
        }
    }

    public static void BeginTextureUpdate_texture_env_combine(GL gl) {
        for (int i = 0; i < Combiners.maxTextureUnits; i++) {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    public void set(GL gl, Combiners combiner) {
        combiner.usesT0 = usesT0;
        combiner.usesT1 = usesT1;
        combiner.usesNoise = false;
        combiner.vertex.color = vertex.color;
        combiner.vertex.secondaryColor = vertex.secondaryColor;
        combiner.vertex.alpha = vertex.alpha;
        this.combiner = combiner;

        for (int i = 0; i < Combiners.maxTextureUnits; i++) {
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);

            if ((i < usedUnits) || ((i < 2) && usesT1)) {
                gl.glEnable(GL.GL_TEXTURE_2D);

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, color[i].combine);

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE0_RGB, color[i].arg[0].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_RGB, color[i].arg[0].operand);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE1_RGB, color[i].arg[1].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND1_RGB, color[i].arg[1].operand);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE2_RGB, color[i].arg[2].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND2_RGB, color[i].arg[2].operand);
                if (Combiners.ATIX_texture_env_route)
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL_TEXTURE_OUTPUT_RGB_ATIX, color[i].outputTexture);

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_ALPHA, alpha[i].combine);

                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE0_ALPHA, alpha[i].arg[0].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND0_ALPHA, alpha[i].arg[0].operand);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE1_ALPHA, alpha[i].arg[1].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND1_ALPHA, alpha[i].arg[1].operand);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SOURCE2_ALPHA, alpha[i].arg[2].source);
                gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_OPERAND2_ALPHA, alpha[i].arg[2].operand);
                if (Combiners.ATIX_texture_env_route)
                    gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL_TEXTURE_OUTPUT_ALPHA_ATIX, alpha[i].outputTexture);
            } else {
                gl.glDisable(GL.GL_TEXTURE_2D);
            }
        }
    }

    public void updateColors(GL gl) {
        FloatBuffer c = FloatBuffer.allocate(4);

        for (int i = 0; i < Combiners.maxTextureUnits; i++) {
            combiner.setConstant(c, color[i].constant, alpha[i].constant);
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            gl.glTexEnvfv(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_COLOR, c.array(), 0);
        }
    }

    private void SetColorCombinerArg(int n, int a, int i) {
        if (TexEnvArgs[i].source == GL.GL_CONSTANT) {
            if ((i > 5) && ((alpha[n].constant == Combiners.COMBINED) || (alpha[n].constant == i))) {
                alpha[n].constant = i;
                color[n].arg[a].source = GL.GL_CONSTANT;
                color[n].arg[a].operand = GL.GL_SRC_ALPHA;
            } else if ((i > 5) && ((vertex.alpha == Combiners.COMBINED) || (vertex.alpha == i))) {
                vertex.alpha = i;
                color[n].arg[a].source = GL.GL_PRIMARY_COLOR;
                color[n].arg[a].operand = GL.GL_SRC_ALPHA;
            } else if ((color[n].constant == Combiners.COMBINED) || (color[n].constant == i)) {
                color[n].constant = i;
                color[n].arg[a].source = GL.GL_CONSTANT;
                color[n].arg[a].operand = GL.GL_SRC_COLOR;
            } else if (Combiners.ATIX_texture_env_route && ((vertex.secondaryColor == Combiners.COMBINED) || (vertex.secondaryColor == i))) {
                vertex.secondaryColor = i;
                color[n].arg[a].source = GL_SECONDARY_COLOR_ATIX;
                color[n].arg[a].operand = GL.GL_SRC_COLOR;
            } else if ((vertex.color == Combiners.COMBINED) || (vertex.color == i)) {
                vertex.color = i;
                color[n].arg[a].source = GL.GL_PRIMARY_COLOR;
                color[n].arg[a].operand = GL.GL_SRC_COLOR;
            }
        } else {
            color[n].arg[a].source = TexEnvArgs[i].source;
            color[n].arg[a].operand = TexEnvArgs[i].operand;
        }
    }

    private void SetColorCombinerValues(int n, int a, int s, int o) {
        color[n].arg[a].source = s;
        color[n].arg[a].operand = o;
    }

    private void SetAlphaCombinerArg(int n, int a, int i) {
        if (TexEnvArgs[i].source == GL.GL_CONSTANT) {
            if ((alpha[n].constant == Combiners.COMBINED) || (alpha[n].constant == i)) {
                alpha[n].constant = i;
                alpha[n].arg[a].source = GL.GL_CONSTANT;
                alpha[n].arg[a].operand = GL.GL_SRC_ALPHA;
            } else if ((vertex.alpha == Combiners.COMBINED) || (vertex.alpha == i)) {
                vertex.alpha = i;
                alpha[n].arg[a].source = GL.GL_PRIMARY_COLOR;
                alpha[n].arg[a].operand = GL.GL_SRC_ALPHA;
            }
        } else {
            alpha[n].arg[a].source = TexEnvArgs[i].source;
            alpha[n].arg[a].operand = GL.GL_SRC_ALPHA;
        }
    }

    private void SetAlphaCombinerValues(int n, int a, int s, int o) {
        alpha[n].arg[a].source = s;
        alpha[n].arg[a].operand = o;
    }

}
