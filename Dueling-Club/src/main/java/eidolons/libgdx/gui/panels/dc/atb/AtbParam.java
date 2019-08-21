package eidolons.libgdx.gui.panels.dc.atb;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AtbParam {
    private TextureRegion textureRegion;
    private int id;
    private int val;

    public AtbParam(TextureRegion textureRegion, int id, int val) {
        this.textureRegion = textureRegion;
        this.id = id;
        this.val = val;
    }

    public AtbParam(int curId) {
        id = curId;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public int getId() {
        return id;
    }

    public int getVal() {
        return val;
    }

}
