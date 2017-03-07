package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.Texture;

public interface AvatarDataSource {
    Texture getAvatar();

    String getName();

    String getParam1();

    String getParam2();
}
