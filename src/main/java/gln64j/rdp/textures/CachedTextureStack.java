package gln64j.rdp.textures;

import javax.media.opengl.GL;

public class CachedTextureStack {

    private GL gl;

    public CachedTexture top;
    public CachedTexture bottom;

    public void init(GL gl) {
        this.gl = gl;
        top = null;
        bottom = null;
    }

    public void removeBottom() {
        gl.glDeleteTextures(1, bottom.glName, 0);
        if (bottom == top)
            top = null;

        bottom = bottom.higher;

        if (bottom != null)
            bottom.lower = null;

    }

    public void remove(CachedTexture texture) {
        if ((texture == bottom) && (texture == top)) {
            top = null;
            bottom = null;
        } else if (texture == bottom) {
            bottom = texture.higher;

            if (bottom != null)
                bottom.lower = null;
        } else if (texture == top) {
            top = texture.lower;

            if (top != null)
                top.higher = null;
        } else {
            texture.higher.lower = texture.lower;
            texture.lower.higher = texture.higher;
        }

        gl.glDeleteTextures(1, texture.glName, 0);
    }

    public void addTop(CachedTexture newtop) {
        gl.glGenTextures(1, newtop.glName, 0);

        newtop.lower = top;
        newtop.higher = null;

        if (top != null)
            top.higher = newtop;

        if (bottom == null)
            bottom = newtop;

        top = newtop;

    }

    public void moveToTop(CachedTexture newtop) {
        if (newtop == top)
            return;

        if (newtop == bottom) {
            bottom = newtop.higher;
            bottom.lower = null;
        } else {
            newtop.higher.lower = newtop.lower;
            newtop.lower.higher = newtop.higher;
        }

        newtop.higher = null;
        newtop.lower = top;
        top.higher = newtop;
        top = newtop;
    }

}
