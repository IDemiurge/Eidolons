package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class InitiativePanelParam {
    private TextureRegion textureRegion;
    private int id;
    private int val;

    public InitiativePanelParam(TextureRegion textureRegion, int id, int val) {
        this.textureRegion = textureRegion;
        this.id = id;
        this.val = val;
    }

    public InitiativePanelParam(int curId) {
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
