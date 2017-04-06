package main.libgdx.gui.controls.radial;

import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.actionpanel.ActionValueContainer;

import java.util.*;
import java.util.stream.Collectors;

import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 12/29/2016.
 */
public class SpellRadialManager {
    private static int MAX_SPELLS_DISPLAYED = 16;

    public static List<MenuNodeDataSource> getSpellNodes(Unit source,
                                                         DC_Obj target) {
        List<DC_SpellObj> spells = source.getSpells().stream()
                .filter(spell -> (spell.getGame().isDebugMode() || (spell.canBeActivated() && spell.canBeTargeted(target.getId()))))
                .collect(Collectors.toList());
        if (spells.size() <= MAX_SPELLS_DISPLAYED) {
            return spells.stream()
                    .map(el -> createNodeBranch(new EntityNode(el), source, target))
                    .collect(Collectors.toList());

        }


        return constructNestedSpellNodes(spells, source, target);

    }

    private static List<MenuNodeDataSource> constructNestedSpellNodes(List<DC_SpellObj> spells, Unit source, DC_Obj target) {

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

        return spell_groups.size() > 8 ?
                aspects.stream()
                        .map(el -> createNodeBranch(new RadialSpellAspect(el), source, target))
                        .collect(Collectors.toList()) :
                spell_groups.stream()
                        .map(el -> createNodeBranch(new RadialSpellGroup(el), source, target))
                        .collect(Collectors.toList());
    }

    private static boolean checkForceTargeting(Unit source,
                                               DC_Obj target, DC_ActiveObj action) {

        return false; //TODO
    }

    private static MenuNodeDataSource createNodeBranch(RADIAL_ITEM object, Unit source, DC_Obj target) {
        MenuNodeDataSource dataSource;
        if (object instanceof EntityNode) {
            final DC_ActiveObj action = (DC_ActiveObj) object.getContents();
            dataSource = () ->
                    new ActionValueContainer(
                            RadialManager.getTextureRForActive(action, target),
                            () -> {
                                if (checkForceTargeting(source, target, action)) {
                                    action.activate();
                                } else {
                                    action.activateOn(target);
                                }
                            }
                    );
        } else {
            dataSource = new MenuNodeDataSource() {
                @Override
                public List<MenuNodeDataSource> getChilds() {
                    return object.getItems(source).stream()
                            .map(el -> createNodeBranch(el, source, target))
                            .collect(Collectors.toList());
                }

                @Override
                public ActionValueContainer getCurrent() {
                    return new ActionValueContainer(
                            getOrCreateR(object.getTexturePath()),
                            null
                    );

                }
            };
        }
        return dataSource;
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

}
