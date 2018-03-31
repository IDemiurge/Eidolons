package eidolons.libgdx.gui.panels.headquarters.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.DC_ContentManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.texture.TextureCache;
import main.system.images.ImageManager;

import java.util.ArrayList;
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
        List<ValueContainer> list = new ArrayList<>();
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
        return new ArrayList<>();
    }

    @Override
    public List<ValueContainer> getAvailableSkills() {
        List<ValueContainer> list = new ArrayList<>();
        hero.getSkills().forEach(skill -> {
//           list.add(new ValueContainer(StyleHolder.getSizedColoredLabelStyle(FONT.DARK, 18,
//            GdxColorMaster.getColor(FLAG_COLOR.DARK_GREEN))), );
        });
        return null;
    }

    @Override
    public List<ValueContainer> getLearnedSkills() {
        return null;
    }

    @Override
    public List<ValueContainer> getSkillsBlockedByXp() {
        return null;
    }
}
