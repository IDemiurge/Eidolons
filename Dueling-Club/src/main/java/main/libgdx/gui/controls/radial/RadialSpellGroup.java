package main.libgdx.gui.controls.radial;

import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.entity.active.DC_SpellObj;
import main.entity.obj.unit.Unit;
import main.system.images.ImageManager;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/30/2016.
 */
public class RadialSpellGroup implements RADIAL_ITEM {

    private SPELL_GROUP group;

    public RadialSpellGroup(SPELL_GROUP g) {
        this.group = g;
    }

    @Override
    public List<RADIAL_ITEM> getItems(Unit source) {
        LinkedList<RADIAL_ITEM> nodes = new LinkedList<>();
        List<DC_SpellObj> spells = source.getSpells()
                .stream()
                .filter(spell -> spell.getSpellGroup().equals(group))
                .collect(Collectors.toList());
        spells.forEach(s -> {
            nodes.add(new EntityNode(s));
        });
        return nodes;
    }

    @Override
    public Object getContents() {
        return
                group;
    }

    @Override
    public String getTexturePath() {
        return ImageManager.getValueIconsPath() + "masteries\\"
                + group.toString() + ".png";
    }

    @Override
    public float getWidth() {
        return 42;
    }

    @Override
    public float getHeight() {
        return 42;
    }
}
