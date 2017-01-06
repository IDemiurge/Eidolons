package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public interface Borderable {
    void setBorder(Image image);

    int getW();

    int getH();

    Image getBorder();
}