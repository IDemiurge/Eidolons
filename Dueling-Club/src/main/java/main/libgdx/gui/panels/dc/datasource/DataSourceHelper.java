package main.libgdx.gui.panels.dc.datasource;

import main.entity.Entity;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 3/30/2017.
 */
public class DataSourceHelper {

    public static  ValueContainer getEntityValueContainer(Entity entity) {
        return new ValueContainer(TextureCache.getOrCreateR(entity.getImagePath()));
    }

}
