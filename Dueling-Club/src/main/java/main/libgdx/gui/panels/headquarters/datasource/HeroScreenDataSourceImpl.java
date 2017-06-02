package main.libgdx.gui.panels.headquarters.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 6/1/2017.
 */
public class HeroScreenDataSourceImpl implements HeroScreenDataSource {
    Unit hero;

    public HeroScreenDataSourceImpl(Unit hero) {
        this.hero = hero;
    }

    @Override
    public List<ValueContainer> getUnlockedMasteries() {
        List<ValueContainer> list = new LinkedList<>();
        for (PARAMS params : DC_ContentManager.getMasteryParams()) {
            Integer v = hero.getIntParam(params);
            if (v > 0) {
                TextureRegion texture = TextureCache.getOrCreateR(ImageManager.getValueIconPath(params));
                list.add(new ValueContainer(texture, params.getName(),
                 String.valueOf(v)));
            }
        }
        return null;
    }

    @Override
    public List<ValueContainer> getLockedMasteries() {
        //unlockable?
        return new LinkedList<>();
    }
}
