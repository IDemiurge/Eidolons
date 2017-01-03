package main.libgdx.gui.radial;

import main.content.CONTENT_CONSTS.SPELL_GROUP;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_SpellObj;
import main.libgdx.gui.radial.SpellRadialManager.RADIAL_ITEM;

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
    public List<RADIAL_ITEM> getItems(DC_HeroObj source) {
        LinkedList<RADIAL_ITEM> nodes = new LinkedList<>();
        List<DC_SpellObj> spells = source.getSpells()
         .stream()
         .filter(spell -> spell.getSpellGroup().equals(group))
         .collect(Collectors.toList());
        spells.forEach(s -> {
            nodes.add(new SpellNode(s));
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
        return null;
    }
}
