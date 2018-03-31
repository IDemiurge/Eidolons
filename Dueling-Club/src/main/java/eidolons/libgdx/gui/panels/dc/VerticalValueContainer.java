package eidolons.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

public class VerticalValueContainer extends ValueContainer {
    public VerticalValueContainer(TextureRegion texture, String name, String value) {
        super(texture, name, value);
    }

    public VerticalValueContainer(TextureRegion texture, String value) {
        super(texture, value);
    }

    public VerticalValueContainer(String name, String value) {
        super(name, value);
    }

    @Override
    protected boolean isVertical() {
        return true;
    }

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, name, value);
        setValueAlignment(Align.center);
        setNameAlignment(Align.center);
    }
}
