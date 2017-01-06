package main.libgdx.gui.radial;

import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_SpellObj;
import main.libgdx.gui.radial.SpellRadialManager.RADIAL_ITEM;

import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class SpellNode implements RADIAL_ITEM {
    private DC_SpellObj spell;

    public SpellNode(DC_SpellObj s) {
        spell = s;
    }

    @Override
    public List<RADIAL_ITEM> getItems(DC_HeroObj source) {
        return null;
    }

    @Override
    public Object getContents() {
        return spell;
    }

    @Override
    public String getTexturePath() {
        return ((Entity)getContents()).getImagePath();
    }
}
