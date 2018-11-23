package eidolons.libgdx.gui.controls.radial;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.UiMaster;
import eidolons.libgdx.gui.panels.dc.actionpanel.tooltips.ActionCostTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_GROUP;
import main.system.auxiliary.StringMaster;

import java.util.*;
import java.util.stream.Collectors;

import static eidolons.libgdx.gui.controls.radial.RadialManager.addSimpleTooltip;

/**
 * Created by JustMe on 12/29/2016.
 */
public class SpellRadialManager {
    private static int MAX_SPELLS_DISPLAYED = 16;

    public static List<RadialValueContainer> getSpellNodes(Unit source,
                                                           DC_Obj target) {
        List<Spell> spells = source.getSpells().stream()
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

    private static List<RadialValueContainer> constructNestedSpellNodes(List<Spell> spells, Unit source, DC_Obj target) {

        Set<SPELL_GROUP> spell_groups = new HashSet<>();
        List<SPELL_ASPECT> aspects = new ArrayList<>();

        for (Spell spell : spells) {
            SPELL_GROUP group = spell.getSpellGroup();
            spell_groups.add(group);

            for (SPELL_ASPECT g : SPELL_ASPECT.values()) {
                if (!aspects.contains(g)) {
                    if (new ArrayList<>(Arrays.asList(g.groups))
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
            valueContainer =
             RadialManager.configureActionNode(target, action);

            valueContainer.getImageContainer().getActor().setImage(new Image(
             TextureCache.getOrCreateSizedRegion(UiMaster.getIconSize(),
             object.getTexturePath())));

            valueContainer.overrideImageSize(UiMaster.getIconSize(), UiMaster.getIconSize());
            ActionCostTooltip tooltip = new ActionCostTooltip(action);
            tooltip.setRadial(true);
            valueContainer.addListener(tooltip.getController());
        } else {
            valueContainer = new SpellRadialContainer(
             TextureCache.getOrCreateSizedRegion(UiMaster.getIconSize(),
              object.getTexturePath()),
             null
            );

            valueContainer.setChildNodes(object.getItems(source).stream()
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
