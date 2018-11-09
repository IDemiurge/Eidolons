package eidolons.libgdx.gui.controls.radial;

import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.controls.radial.SpellRadialManager.SPELL_ASPECT;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;

import java.util.ArrayList;
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
        ArrayList<RADIAL_ITEM> list = new ArrayList<>();
        List<DC_SpellObj> spells = new ArrayList<>(source.getSpells());
        if (spells.size() < maxPlainSize) {
            spells.forEach(spell -> list.add(new EntityNode(spell)));
            return list;
        }

        spells.removeIf(spell -> !spell.getAspect().toString().equalsIgnoreCase(aspect.toString()));
        for (SPELL_GROUP g : aspect.groups) {
            List<DC_SpellObj> group = new ArrayList<>(spells);
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
        return "ui/value icons/aspects/" + aspect.name().toLowerCase() + ".png";
    }

}
