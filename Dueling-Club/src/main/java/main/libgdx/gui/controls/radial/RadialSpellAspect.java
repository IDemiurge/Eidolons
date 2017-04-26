package main.libgdx.gui.controls.radial;

import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.entity.active.DC_SpellObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.controls.radial.SpellRadialManager.SPELL_ASPECT;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class RadialSpellAspect implements RADIAL_ITEM {
    private SPELL_ASPECT aspect;
    private int maxPlainSize = 40;

    public RadialSpellAspect(SPELL_ASPECT aspect) {
        this.aspect = aspect;
    }

    @Override
    public List<RADIAL_ITEM> getItems(Unit source) {
        LinkedList<RADIAL_ITEM> list = new LinkedList<>();
        List<DC_SpellObj> spells = new LinkedList<>(source.getSpells());
        if (spells.size() < maxPlainSize) {
            spells.forEach(spell -> list.add(new EntityNode(spell)));
            return list;
        }

        spells.removeIf(spell -> !spell.getAspect().toString().equalsIgnoreCase(aspect.toString()));
        for (SPELL_GROUP g : aspect.groups) {
            List<DC_SpellObj> group = new LinkedList<>(spells);
            group.removeIf(spell -> !spell.getSpellGroup().equals(g));
            if (group.size() > 0) {
                list.add(new RadialSpellGroup(g));
            }
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

}
