package main.libgdx.gui.controls.radial;

import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.stream.Collectors;

import static main.libgdx.gui.controls.radial.RadialManager.addSimpleTooltip;
import static main.libgdx.texture.TextureCache.getOrCreateGrayscaleR;
import static main.libgdx.texture.TextureCache.getOrCreateR;

/**
 * Created by JustMe on 12/29/2016.
 */
public class SpellRadialManager {
    private static int MAX_SPELLS_DISPLAYED = 16;

    public static List<RadialValueContainer> getSpellNodes(Unit source,
                                                           DC_Obj target) {
        List<DC_SpellObj> spells = source.getSpells().stream()
         .filter(spell -> (spell.getGame().isDebugMode() || (spell.canBeActivated() && spell.canBeTargeted(target.getId()))))
         .collect(Collectors.toList());
        if (spells.size() <= MAX_SPELLS_DISPLAYED) {
            return spells.stream()
             .map(el -> {
                 final RadialValueContainer valueContainer = createNodeBranch(new EntityNode(el), source, target);
                 addSimpleTooltip(valueContainer, el.getName());
                 return valueContainer;
             })
             .collect(Collectors.toList());

        }


        return constructNestedSpellNodes(spells, source, target);

    }

    private static List<RadialValueContainer> constructNestedSpellNodes(List<DC_SpellObj> spells, Unit source, DC_Obj target) {

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

    private static RadialValueContainer createNodeBranch(RADIAL_ITEM object, Unit source, DC_Obj target) {
        RadialValueContainer valueContainer;
        if (object instanceof EntityNode) {
            final DC_ActiveObj action = (DC_ActiveObj) object.getContents();
            Ref ref = action.getOwnerObj().getRef().getTargetingRef(target);
            valueContainer = new RadialValueContainer(
             !action.canBeActivated(ref) ?
              getOrCreateGrayscaleR(action.getImagePath())
              : getOrCreateR(action.getImagePath()),
             () -> {
                 if (checkForceTargeting(source, target, action)) {
                     action.activate();
                 } else {
                     action.activateOn(target);
                 }
             }
            );
            addSimpleTooltip(valueContainer, action.getName());
        } else {
            valueContainer = new RadialValueContainer(
             getOrCreateR(object.getTexturePath()),
             null
            );

            valueContainer.setChilds(object.getItems(source).stream()
             .map(el -> createNodeBranch(el, source, target))
             .collect(Collectors.toList()));

            String tooltip = StringMaster.getWellFormattedString(object.getContents().toString());
            addSimpleTooltip(valueContainer, tooltip);
        }
        return valueContainer;
    }

    public enum SPELL_ASPECT {
        NEUTRAL(SpellEnums.SPELL_GROUP.FIRE, SpellEnums.SPELL_GROUP.AIR, SpellEnums.SPELL_GROUP.WATER),
        ARCANE(SpellEnums.SPELL_GROUP.CONJURATION, SpellEnums.SPELL_GROUP.SORCERY, SpellEnums.SPELL_GROUP.ENCHANTMENT),
        LIFE(SpellEnums.SPELL_GROUP.SAVAGE, SpellEnums.SPELL_GROUP.SYLVAN, SpellEnums.SPELL_GROUP.EARTH),
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
