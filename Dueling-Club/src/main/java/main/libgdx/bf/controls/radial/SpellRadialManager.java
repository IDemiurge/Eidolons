package main.libgdx.bf.controls.radial;

import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.bf.controls.radial.RadialMenu.CreatorNode;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 12/29/2016.
 */
public class SpellRadialManager {
    private static int MAX_SPELLS_DISPLAYED = 16;

    public static List<RadialMenu.CreatorNode> getSpellNodes(Unit source,
                                                             DC_Obj target) {
        List<DC_SpellObj> spells = source.getSpells()
         .stream()
         .filter(spell -> (spell.getGame().isDebugMode() || (spell.canBeActivated() && spell.canBeTargeted(target.getId()))))
         .collect(Collectors.toList());
        if (spells.size() <= MAX_SPELLS_DISPLAYED) {
            return constructPlainSpellNodes(spells, source, target);
        }


        return constructNestedSpellNodes(spells, source, target);

    }

    private static List<CreatorNode> constructNestedSpellNodes(List<DC_SpellObj> spells, Unit source, DC_Obj target) {
        List<RadialMenu.CreatorNode> nodes = new LinkedList<>();

        Set<SPELL_GROUP> spell_groups = new HashSet<>();
        List<SPELL_ASPECT> aspects = new LinkedList<>();

        for (DC_SpellObj spell : spells) {
            SPELL_GROUP group = spell.getSpellGroup();
            spell_groups.add(group);

            for (SPELL_ASPECT g : SPELL_ASPECT.values()) {
                if (!aspects.contains(g)) {
                    if (new LinkedList<>(Arrays.asList(g.groups))
                     .contains(spell.getSpellGroup())) {
                        aspects.add(g);
                    }
                }
            }

        }
        if (spell_groups.size() > 8) {
            for (SPELL_ASPECT g : aspects) {
                nodes.add(createNodeBranch(new RadialSpellAspect(g), source, target));
            }
        } else {
            for (SPELL_GROUP g : spell_groups) {
                nodes.add(createNodeBranch(new RadialSpellGroup(g), source, target));
            }
        }

        return nodes;
    }

    private static List<CreatorNode> constructPlainSpellNodes(List<DC_SpellObj> spells, Unit source, DC_Obj target) {
        List<RadialMenu.CreatorNode> nodes = new LinkedList<>();
        for (DC_SpellObj g : spells) {
            nodes.add(createNodeBranch(new EntityNode(g), source, target));
        }
        return nodes;

    }

    private static boolean checkForceTargeting(Unit source,
                                               DC_Obj target, DC_ActiveObj action) {

        return false;
    }

    private static CreatorNode createNodeBranch(RADIAL_ITEM object,
                                                Unit source, DC_Obj target) {
        CreatorNode node = new RadialMenu.CreatorNode();
        node.name = StringMaster.getWellFormattedString(object.getContents().toString());
        if (object instanceof EntityNode) {
            final DC_ActiveObj action = (DC_ActiveObj) object.getContents();

            node.texture =
            RadialManager.getTextureForActive(action, target);
            node.name = action.getName();
            node.action = new Runnable() {
                @Override
                public void run() {
                    if (checkForceTargeting(source, target, action)) {
                        action.invokeClicked();
                    } else {
                        action.getRef().setTarget(target.getId());
                        action.activatedOn(action.getRef());

                    }
                }
            };
        } else {
            node.childNodes = new LinkedList<>();

            node.texture = TextureCache.getOrCreate(object.getTexturePath());

            node.w=object.getWidth();
            node.h=object.getHeight();
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
        ARCANE(SpellEnums.SPELL_GROUP.CONJURATION, SpellEnums.SPELL_GROUP.SORCERY, SpellEnums.SPELL_GROUP.ENCHANTMENT),
        LIFE(SpellEnums.SPELL_GROUP.SAVAGE, SpellEnums.SPELL_GROUP.SYLVAN, SpellEnums.SPELL_GROUP.FIRE
         , SpellEnums.SPELL_GROUP.AIR, SpellEnums.SPELL_GROUP.EARTH, SpellEnums.SPELL_GROUP.WATER

        ),
        CHAOS(SpellEnums.SPELL_GROUP.DESTRUCTION, SpellEnums.SPELL_GROUP.DEMONOLOGY, SpellEnums.SPELL_GROUP.WARP),
        DARKNESS(SpellEnums.SPELL_GROUP.SHADOW, SpellEnums.SPELL_GROUP.WITCHERY, SpellEnums.SPELL_GROUP.PSYCHIC),
        LIGHT(SpellEnums.SPELL_GROUP.BENEDICTION, SpellEnums.SPELL_GROUP.REDEMPTION, SpellEnums.SPELL_GROUP.CELESTIAL),
        DEATH(SpellEnums.SPELL_GROUP.AFFLICTION, SpellEnums.SPELL_GROUP.BLOOD_MAGIC, SpellEnums.SPELL_GROUP.NECROMANCY),;
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
