package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface Borderable {
    TextureRegion getBorder();

    void setBorder(TextureRegion texture);
}
