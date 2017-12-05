package main.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_ArmorObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.UiAnimator;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.AttackTooltip;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.AttackTooltipFactory;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponToolTip;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponToolTipDataSource;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static main.content.UNIT_INFO_PARAMS.*;
import static main.content.ValuePages.*;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitDataSource implements
 AttributesDataSource, ResourceSource,
 AvatarDataSource, CounterAndActionPointsSource,
 EffectsAndAbilitiesSource, MainWeaponDataSource<ValueContainer>, OffWeaponDataSource,
 MainAttributesSource, ResistSource, StatsDataSource,
 ArmorDataSource {
    private Unit unit;

    public UnitDataSource(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String getStrength() {
        return unit.getStrParam(PARAMS.STRENGTH);
    }

    @Override
    public String getVitality() {
        return unit.getStrParam(PARAMS.VITALITY);
    }

    @Override
    public String getAgility() {
        return unit.getStrParam(PARAMS.AGILITY);
    }

    @Override
    public String getDexterity() {
        return unit.getStrParam(PARAMS.DEXTERITY);
    }

    @Override
    public String getWillpower() {
        return unit.getStrParam(PARAMS.WILLPOWER);
    }

    @Override
    public String getSpellpower() {
        return unit.getStrParam(PARAMS.SPELLPOWER);
    }

    @Override
    public String getIntelligence() {
        return unit.getStrParam(PARAMS.INTELLIGENCE);
    }

    @Override
    public String getKnowledge() {
        return unit.getStrParam(PARAMS.KNOWLEDGE);
    }

    @Override
    public String getWisdom() {
        return unit.getStrParam(PARAMS.WISDOM);
    }

    @Override
    public String getCharisma() {
        return unit.getStrParam(PARAMS.CHARISMA);
    }

    @Override
    public String getAttribute(String name) {
        return unit.getStrParam(name );
    }

    @Override
    public String getToughness() {
        int c = unit.getIntParam(PARAMS.C_TOUGHNESS);
        int m = unit.getIntParam(PARAMS.TOUGHNESS);
        return c + "/" + m;
    }

    @Override
    public String getEndurance() {
        int c = unit.getIntParam(PARAMS.C_ENDURANCE);
        int m = unit.getIntParam(PARAMS.ENDURANCE);
        return c + "/" + m;
    }

    @Override
    public String getStamina() {
        int c = unit.getIntParam(PARAMS.C_STAMINA);
        int m = unit.getIntParam(PARAMS.STAMINA);
        return c + "/" + m;
    }

    @Override
    public String getMorale() {
        int c = unit.getIntParam(PARAMS.C_MORALE);
        int m = unit.getIntParam(PARAMS.MORALE);
        return c + "/" + m;
    }

    @Override
    public String getEssence() {
        int c = unit.getIntParam(PARAMS.C_ESSENCE);
        int m = unit.getIntParam(PARAMS.ESSENCE);
        return c + "/" + m;
    }

    @Override
    public String getFocus() {
        int c = unit.getIntParam(PARAMS.C_FOCUS);
        int m = unit.getIntParam(PARAMS.FOCUS);
        return c + "/" + m;
    }

    @Override
    public String getParam(PARAMS param) {
        switch (param) {
            case STAMINA:
                return getStamina();
            case FOCUS:
                return getFocus();
            case TOUGHNESS:
                return getToughness();
            case ENDURANCE:
                return getEndurance();
            case ESSENCE:
                return getEssence();
            case MORALE:
                return getMorale();
        }
        return null;
    }

    @Override
    public TextureRegion getAvatar() {
        return TextureCache.getOrCreateR(unit.getImagePath());
    }

    @Override
    public String getName() {
        return unit.getNameIfKnown();
    }

    @Override
    public String getParam1() {
        return "Level " + unit.getParam("Level");
    }

    @Override
    public String getParam2() {
        if (unit.checkProperty(G_PROPS.RACE)) {
            return
             unit.getValue(G_PROPS.RACE);
        }
        return
         unit.getValue(G_PROPS.GROUP);
    }

    @Override
    public ValueContainer getCounterPoints() {
        int c = unit.getIntParam(PARAMS.C_N_OF_COUNTERS);
        int m = unit.getIntParam(PARAMS.N_OF_COUNTERS);
        final String value = c + "/" + m;

        VerticalValueContainer container = new VerticalValueContainer(
         getOrCreateR(ImageManager.getValueIconPath(PARAMS.N_OF_COUNTERS)), value);

        ValueTooltip toolTip = new ValueTooltip();
        toolTip.setUserObject(Arrays.asList(
         new ValueContainer(PARAMS.N_OF_COUNTERS.getName(), value)));
        container.addListener(toolTip.getController());

        return container;
    }

    @Override
    public ValueContainer getActionPoints() {
        int c = unit.getIntParam(PARAMS.C_N_OF_ACTIONS);
        int m = unit.getIntParam(PARAMS.N_OF_ACTIONS);
        final String value = c + "/" + m;
        VerticalValueContainer  container= getValueContainer(PARAMS.N_OF_ACTIONS, value);

        ValueTooltip toolTip = new ValueTooltip();
        toolTip.setUserObject(Arrays.asList(
         new ValueContainer(PARAMS.N_OF_ACTIONS.getName(), value)));
        container.addListener(toolTip.getController());
        return container;
    }

    public VerticalValueContainer getParamContainer(PARAMETER parameter) {
        final String string = unit.getStrParam(parameter);
        return getValueContainer(
         ImageManager.getValueIconPath(parameter),
         string, parameter.getName());
    }

    @Override
    public VerticalValueContainer getResistance() {
        return getParamContainer(PARAMS.RESISTANCE);
    }

    @Override
    public VerticalValueContainer getDefense() {
        return getParamContainer(PARAMS.DEFENSE);
    }

    public VerticalValueContainer getValueContainer(PARAMETER param, String string) {
        return getValueContainer(
         ImageManager.getValueIconPath(param),
         string, param.getName());

    }

    private VerticalValueContainer getValueContainer(String valueIconPath, String param,
                                                     String paramName) {
        final VerticalValueContainer container =
         new VerticalValueContainer(
          getOrCreateR(valueIconPath),
          param);
        container.overrideImageSize(UiAnimator.getSmallIconSize(), UiAnimator.getSmallIconSize());
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(paramName , param)));
        container.addListener(tooltip.getController());
        return container;
    }

    @Override
    public VerticalValueContainer getFortitude() {
        return getParamContainer(PARAMS.FORTITUDE);
    }

    @Override
    public VerticalValueContainer getSpirit() {
        return getParamContainer(PARAMS.SPIRIT);
    }

    @Override
    public VerticalValueContainer getArmor() {
        return getParamContainer(PARAMS.ARMOR);
    }

    @Override
    public List<ValueContainer> getBuffs() {
        return unit.getBuffs().stream()
         .filter(obj -> obj.isDisplayed())
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getAbilities() {
        return unit.getPassives().stream()
         .filter(obj -> obj.isDisplayed())
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

    @Override
    public ValueContainer getArmorObj() {
        final DC_ArmorObj armor = unit.getArmor();


        final ValueContainer container;

        if (armor != null) {
            container = new ValueContainer(getOrCreateR(armor.getImagePath()));

            WeaponToolTip tooltip = new WeaponToolTip();

            tooltip.setUserObject(new WeaponToolTipDataSource() {
                @Override
                public List<ValueContainer> getMainParams() {
                    return Arrays.stream(ARMOR_TOOLTIP)
                     .map(el -> new ValueContainer(el.getName(), armor.getStrParam(el)).pad(10))
                     .collect(Collectors.toList());
                }

                @Override
                public List<ValueContainer> getBuffs() {
                    return armor.getBuffs().stream()
                     .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                     .map(AttackTooltipFactory.getObjValueContainerMapper())
                     .collect(Collectors.toList());
                }
            });

            container.addListener(tooltip.getController());
        } else {
            container = new ValueContainer(getOrCreateR(
             CELL_TYPE.ARMOR.getSlotImagePath()));
        }

        return container;
    }

    @Override
    public List<ValueContainer> getParamValues() {
        final DC_ArmorObj armor = unit.getArmor();
        List<ValueContainer> values = new ArrayList<>();
        if (armor != null) {
            final String cd = armor.getStrParam(PARAMS.C_DURABILITY);
            final String d = armor.getStrParam(PARAMS.DURABILITY);

            values.add(new ValueContainer(PARAMS.DURABILITY.getName(), cd + "/" + d));

            final String cover = armor.getStrParam(PARAMS.COVER_PERCENTAGE);

            values.add(new ValueContainer(PARAMS.COVER_PERCENTAGE.getName(), cover));
        }

        return values;
    }

    @Override
    public List<Pair<PARAMETER, String>> getMagickResists() {
        return Arrays.stream(RESISTANCES).map(p -> {
            String ps = unit.getStrParam(p);
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Pair<PARAMETER, String>> getArmorResists() {
        return Arrays.stream(ARMOR_VS_DAMAGE_TYPES).map(p -> {
            String ps = unit.getStrParam(p);
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Pair<PARAMETER, String>> getDurabilityResists() {
        return Arrays.stream(DURABILITY_VS_DAMAGE_TYPES).map(p -> {
            String ps = unit.getStrParam(p);
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public ValueContainer getOffWeapon() {
        DC_WeaponObj mainWeapon = unit.getSecondWeapon();

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getOffWeaponDetailInfo() {
        DC_WeaponObj weapon = unit.getWeapon(true);

        return getWeaponDetail(weapon);
    }

    @Override
    public ValueContainer getNaturalOffWeapon() {
        DC_WeaponObj mainWeapon = unit.getNaturalWeapon(true);

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getNaturalOffWeaponDetailInfo() {
        DC_WeaponObj weapon = unit.getNaturalWeapon(true);

        return getWeaponDetail(weapon);
    }

    @Override
    public ValueContainer getMainWeapon() {
        DC_WeaponObj mainWeapon = unit.getMainWeapon();

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getMainWeaponDetailInfo() {
        DC_WeaponObj mainWeapon = unit.getMainWeapon();
        try {
            return getWeaponDetail(mainWeapon);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return new ArrayList<>();
    }

    @Override
    public ValueContainer getNaturalMainWeapon() {
        DC_WeaponObj weapon = unit.getNaturalWeapon(false);

        return getWeaponValueContainer(weapon);
    }

    @Override
    public List<ValueContainer> getNaturalMainWeaponDetailInfo() {
        DC_WeaponObj weapon = unit.getNaturalWeapon(false);

        return getWeaponDetail(weapon);
    }

    private List<ValueContainer> getWeaponDetail(DC_WeaponObj weapon) {
        List<ValueContainer> result = new ArrayList<>();

        if (weapon != null) {
            for (DC_UnitAction el : weapon.getOrCreateAttackActions()) {
                final ValueContainer valueContainer = new ValueContainer(getOrCreateR(el.getImagePath()));
                AttackTooltip toolTip = AttackTooltipFactory.createAttackTooltip(el);
                valueContainer.addListener(toolTip.getController());
                result.add(valueContainer);
            }
        }

        return result;
    }

    @Override
    public List<List<ValueContainer>> getGeneralStats() {
        return getStatsValueContainers(UNIT_INFO_PARAMS_GENERAL);
    }

    @Override
    public List<List<ValueContainer>> getCombatStats() {
        return getStatsValueContainers(UNIT_INFO_PARAMS_COMBAT);
    }

    @Override
    public List<List<ValueContainer>> getMagicStats() {
        return getStatsValueContainers(UNIT_INFO_PARAMS_MAGIC);
    }

    @Override
    public List<List<ValueContainer>> getMiscStats() {
        return getStatsValueContainers(UNIT_INFO_PARAMS_MISC);
    }


    private ValueContainer getWeaponValueContainer(DC_WeaponObj weapon) {
        TextureRegion image;
        if (weapon != null) {
            image = getOrCreateR(weapon.getImagePath());
        } else {
            image = getOrCreateR(CELL_TYPE.WEAPON_MAIN.getSlotImagePath());
        }

        ValueContainer valueContainer = new ValueContainer(image);

        if (weapon != null) {
            List<ValueContainer> list = new ArrayList<>();

            for (int i = 0; i < WEAPON_DC_INFO_PARAMS.length; i++) {
                PARAMS p = WEAPON_DC_INFO_PARAMS[i];
                String value = String.valueOf(weapon.getIntParam(p));
                String name = p.getName();
                final ValueContainer tooltipContainer = new ValueContainer(name, value);
                tooltipContainer.pad(10);
                list.add(tooltipContainer);
            }

            ToolTip toolTip = new WeaponToolTip();
            toolTip.setUserObject(new WeaponToolTipDataSource() {
                @Override
                public List<ValueContainer> getMainParams() {
                    return list;
                }

                @Override
                public List<ValueContainer> getBuffs() {
                    return weapon.getBuffs().stream()
                     .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                     .map(AttackTooltipFactory.getObjValueContainerMapper())
                     .collect(Collectors.toList());
                }
            });
            valueContainer.addListener(toolTip.getController());
        }
        return valueContainer;
    }

    private List<List<ValueContainer>> getStatsValueContainers(PARAMS[][] unitInfoParamsGeneral) {
        List<List<ValueContainer>> values = new ArrayList<>();
        Arrays.stream(unitInfoParamsGeneral)
         .forEach(ps -> values.add(
          Arrays.stream(ps)
           .map(p -> {
               String value = unit.getStrParam(p);
               String name = p.getName();
               ValueContainer valueContainer = new ValueContainer(name, value);
               ValueTooltip valueTooltip = new ValueTooltip();
               valueTooltip.setUserObject(Arrays.asList(new ValueContainer(name, value)));
               valueContainer.addListener(valueTooltip.getController());
               return valueContainer;
           }).collect(Collectors.toList())
         ));
        return values;
    }
}
