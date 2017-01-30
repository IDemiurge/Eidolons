package main.libgdx.bf.controls.radial;

import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.SPELL_GROUP;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.DC_SpellObj;
import main.entity.obj.top.DC_ActiveObj;
import main.libgdx.bf.controls.radial.RadialMenu.CreatorNode;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/29/2016.
 */
public class SpellRadialManager {
    private static int MAX_SPELLS_DISPLAYED=16;

    public static List<RadialMenu.CreatorNode> getSpellNodes(DC_HeroObj source,
                                                             DC_Obj target) {
        Set<CONTENT_CONSTS.SPELL_GROUP> spell_groups = new HashSet<>();
        List<SPELL_ASPECT> aspects = new LinkedList<>();
        List<RadialMenu.CreatorNode> nodes = new LinkedList<>();
        List<DC_SpellObj> spells = source.getSpells()
                .stream()
                .filter(spell -> (spell.getGame().isDebugMode() || (spell.canBeActivated() && spell.canBeTargeted(target.getId()))))
                .collect(Collectors.toList());
        if (spells.size() <= MAX_SPELLS_DISPLAYED) {
            for (DC_SpellObj g : spells)
                nodes.add(createNodeBranch(new EntityNode(g), source, target));
            return nodes;
        }
        for (DC_SpellObj spell : spells) {
            CONTENT_CONSTS.SPELL_GROUP group = spell.getSpellGroup();
            spell_groups.add(group);

            for (SPELL_ASPECT g : SPELL_ASPECT.values())
                if (!aspects.contains(g))
                    if (new LinkedList<>(Arrays.asList(g.groups))
                            .contains(spell.getSpellGroup()))
                        aspects.add(g);

        }
        if (aspects.size() > 1)
            for (SPELL_ASPECT g : aspects)
                nodes.add(createNodeBranch(new RadialSpellAspect(g), source, target));
        else {
            for (SPELL_GROUP g : spell_groups)
                nodes.add(createNodeBranch(new RadialSpellGroup(g), source, target));
        }

        return nodes;
    }

    private static boolean checkForceTargeting(DC_HeroObj source,
                                               DC_Obj target, DC_ActiveObj action) {

        return false;
    }

    private static CreatorNode createNodeBranch(RADIAL_ITEM object,
                                                DC_HeroObj source, DC_Obj target) {
        CreatorNode node = new RadialMenu.CreatorNode();
        node.name = StringMaster.getWellFormattedString(object.getContents().toString());
        if (object instanceof EntityNode) {
            final DC_ActiveObj action = (DC_ActiveObj) object.getContents();

            node.texture = TextureManager.getOrCreate(action.getImagePath());
            node.name = action.getName();
            node.action = new Runnable() {
                @Override
                public void run() {
                    if (checkForceTargeting(source, target, action))
                        action.invokeClicked();
                    else {
                        action.getRef().setTarget(target.getId());
                        action.activate(action.getRef());

                    }
                }
            };
        } else {
            node.childNodes = new LinkedList<>();

            node.texture = TextureManager.getOrCreate(object.getTexturePath());

            object.getItems(source).forEach(child -> {
                node.childNodes.add(createNodeBranch(
                        child, source, target));
            });
        }
        return node;
    }
/*
6 aspects?
spell 'types'?

 */

    public enum RADIAL_ITEM_SPELL {
        RECOMMENDED,
        LAST,

    }

    public enum SPELL_ASPECT {
        ARCANE(SPELL_GROUP.CONJURATION, SPELL_GROUP.SORCERY, SPELL_GROUP.ENCHANTMENT),
        LIFE(SPELL_GROUP.SAVAGE, SPELL_GROUP.SYLVAN, SPELL_GROUP.FIRE
                , SPELL_GROUP.AIR, SPELL_GROUP.EARTH, SPELL_GROUP.WATER

        ),
        CHAOS(SPELL_GROUP.DESTRUCTION, SPELL_GROUP.DEMONOLOGY, SPELL_GROUP.WARP),
        DARKNESS(SPELL_GROUP.SHADOW, SPELL_GROUP.WITCHERY, SPELL_GROUP.PSYCHIC),
        LIGHT(SPELL_GROUP.BENEDICTION, SPELL_GROUP.REDEMPTION, SPELL_GROUP.CELESTIAL),
        DEATH(SPELL_GROUP.AFFLICTION, SPELL_GROUP.BLOOD_MAGIC, SPELL_GROUP.NECROMANCY),;
        public SPELL_GROUP[] groups;

        SPELL_ASPECT(SPELL_GROUP... groups) {
            this.groups = groups;
        }
    }

    public enum SPELL_RADIAL_MODE {
        ASPECT,
        ALL,

    }

}
