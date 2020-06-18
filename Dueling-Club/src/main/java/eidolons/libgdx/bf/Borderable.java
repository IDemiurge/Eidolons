package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface Borderable extends TeamColored{
    TextureRegion getBorder();

    void setBorder(TextureRegion texture);

}
