package libgdx.gui.dungeon.controls.radial;

import eidolons.entity.active.Spell;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 12/30/2016.
 */
public class RadialSpellAspect implements RADIAL_ITEM {
    private final SpellRadialManager.SPELL_ASPECT aspect;

    public RadialSpellAspect(SpellRadialManager.SPELL_ASPECT aspect) {
        this.aspect = aspect;
    }

    @Override
    public List<RADIAL_ITEM> getItems(Unit source) {
        ArrayList<RADIAL_ITEM> list = new ArrayList<>();
        List<Spell> spells = new ArrayList<>(source.getSpells());
        int maxPlainSize = 40;
        if (spells.size() < maxPlainSize) {
            spells.forEach(spell -> list.add(new EntityNode(spell)));
            return list;
        }

        spells.removeIf(spell -> !spell.getAspect().toString().equalsIgnoreCase(aspect.toString()));
        for (SPELL_GROUP g : aspect.groups) {
            List<Spell> group = new ArrayList<>(spells);
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
