package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public interface Borderable {
    int getW();

    int getH();

    Image getBorder();

    void setBorder(Image image);
}
