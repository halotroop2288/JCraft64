package me.hydos.J64.emu.gln64.rdp.textures;

import org.lwjgl.opengl.GL40;

public class CachedTextureStack {

    public CachedTexture top;
    public CachedTexture bottom;

    public void init() {
        top = null;
        bottom = null;
    }

    public void removeBottom() {
        GL40.glDeleteTextures(bottom.glName);
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

        GL40.glDeleteTextures(texture.glName);
    }

    public void addTop(CachedTexture newtop) {
        GL40.glGenTextures(newtop.glName);

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
