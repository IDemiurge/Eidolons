package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface AvatarDataSource {
    TextureRegion getAvatar();

    String getName();

    String getParam1();

    String getParam2();

    TextureRegion getLargeImage();

    TextureRegion getFullSizeImage();
}
