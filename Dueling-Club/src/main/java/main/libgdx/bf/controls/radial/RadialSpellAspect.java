package main.libgdx.bf.controls.radial;

import main.content.CONTENT_CONSTS.SPELL_GROUP;
import main.entity.obj.DC_HeroObj;
import main.libgdx.bf.controls.radial.SpellRadialManager.SPELL_ASPECT;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class RadialSpellAspect implements RADIAL_ITEM {
    private SPELL_ASPECT aspect;

    public RadialSpellAspect(SPELL_ASPECT aspect) {
        this.aspect = aspect;
    }

    @Override
    public List<RADIAL_ITEM> getItems(DC_HeroObj source) {
        LinkedList<RADIAL_ITEM> list = new LinkedList<>();
        for (SPELL_GROUP g : aspect.groups) {
            list.add(new RadialSpellGroup(g));
        }
        return list;

    }

    @Override
    public Object getContents() {
        return aspect;
    }

    @Override
    public String getTexturePath() {
        return "ui\\value icons\\aspects\\" + aspect.name().toLowerCase() + ".png";
    }
    //        ASPECT

}
