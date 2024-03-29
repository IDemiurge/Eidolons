package libgdx.gui.dungeon.controls.radial;

import eidolons.entity.feat.active.Spell;
import eidolons.entity.unit.Unit;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/30/2016.
 */
public class RadialSpellGroup implements RADIAL_ITEM {

    private final SPELL_GROUP group;

    public RadialSpellGroup(SPELL_GROUP g) {
        this.group = g;
    }

    @Override
    public List<RADIAL_ITEM> getItems(Unit source) {
        ArrayList<RADIAL_ITEM> nodes = new ArrayList<>();
        List<Spell> spells = source.getSpells()
         .stream()
         .filter(spell -> spell.getSpellGroup() == (group))
         .collect(Collectors.toList());
        spells.forEach(s -> {
            nodes.add(new EntityNode(s));
        });
        return nodes;
    }

    @Override
    public Object getContents() {
        return group;
    }

    @Override
    public String getTexturePath() {
        return ImageManager.getValueIconsPath() + "masteries/"
         + StringMaster.format(group.toString()) + ".png";
    }
}
