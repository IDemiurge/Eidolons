package libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import libgdx.StyleHolder;
import libgdx.gui.UiMaster;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.VerticalValueContainer;
import libgdx.gui.panels.dc.unitinfo.tooltips.*;
import libgdx.gui.panels.dc.unitinfo.tooltips.*;
import libgdx.gui.tooltips.Tooltip;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.texture.TextureCache;
import main.ability.AbilityObj;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.obj.BuffObj;
import main.system.images.ImageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.content.values.UNIT_INFO_PARAMS.*;
import static eidolons.content.values.ValuePages.*;
import static libgdx.texture.TextureCache.getOrCreateR;

public class UnitDataSource implements
 AttributesDataSource, ResourceSource,
 AvatarDataSource, CounterAndActionPointsSource,
 EffectsAndAbilitiesSource, MainWeaponDataSource<ValueContainer>, OffWeaponDataSource,
 MainAttributesSource, ResistSource, StatsDataSource,
 ArmorDataSource {
    private static List<VALUE> values;
    private final Unit unit;

    public UnitDataSource(Unit unit) {
        this.unit = unit;
    }

    public static List<VALUE> getStatsValueList(VALUE[][] paramsGeneral) {

        List<VALUE> values = new ArrayList<>();
        for (int i = 0; i < paramsGeneral.length; i++) {
            values.addAll(Arrays.asList(paramsGeneral[i]));
            values.add(null);
        }
        return values;
    }

    public static VALUE[] getStatsValuesSplit(int n, int i) {
        List<VALUE> list = getStatsValues();
        VALUE[] array = new VALUE[list.size() / n];
        int j = 1;
        int a = 0;
        if (i == n)
            i = 0;
        for (VALUE value : list) {
            if (j % n == i)
                array[a++] = value;

            j++;
        }
        return array;
    }

    public static List<VALUE> getStatsValues() {
        if (values != null)
            return values;
        values = new ArrayList<>();
        values.addAll(getStatsValueList(UNIT_INFO_PARAMS_GENERAL));
        values.add(null);
        values.addAll(getStatsValueList(UNIT_INFO_PARAMS_COMBAT));
        values.add(null);
        values.addAll(getStatsValueList(UNIT_INFO_PARAMS_MAGIC));
        values.add(null);
        values.addAll(getStatsValueList(UNIT_INFO_PARAMS_MISC));
        values.add(null);
        return values;
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
        return unit.getStrParam(name);
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
            case FOCUS:
                return getFocus();
            case TOUGHNESS:
                return getToughness();
            case ENDURANCE:
                return getEndurance();
            case ESSENCE:
                return getEssence();
        }
        return null;
    }

    @Override
    public TextureRegion getAvatar() {
        return TextureCache.getOrCreateR(unit.getImagePath());
    }

    public TextureRegion getLargeImage() {
        return TextureCache.getOrCreateR(unit.getLargeImagePath());
    }

    public TextureRegion getFullSizeImage() {
        return TextureCache.getOrCreateR(unit.getFullSizeImagePath());
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
        int c = unit.getIntParam(PARAMS.C_EXTRA_ATTACKS);
        int m = unit.getIntParam(PARAMS.EXTRA_ATTACKS);
        final String value = c + "/" + m;

        VerticalValueContainer container = new VerticalValueContainer(
         TextureCache.getOrCreateR(ImageManager.getValueIconPath(PARAMS.EXTRA_ATTACKS)), value);

        ValueTooltip toolTip = new ValueTooltip();
        toolTip.setUserObject(Collections.singletonList(
                new ValueContainer(PARAMS.EXTRA_ATTACKS.getName(), value)));
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
          TextureCache.getOrCreateR(valueIconPath),
          param);
        container.overrideImageSize(UiMaster.getSmallIconSize(), UiMaster.getSmallIconSize());
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Collections.singletonList(new ValueContainer(paramName, param)));
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
    public List<ValueContainer> getBuffs(boolean body) {
        return unit.getBuffs().stream()
         .filter(BuffObj::isDisplayed)
                .filter(obj -> obj.isPhysical()==body)
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getAbilities(boolean body) {
        return unit.getPassives().stream()
         .filter(AbilityObj::isDisplayed)
         .filter(obj -> obj.isPhysical()==body)
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

    @Override
    public ValueContainer getArmorObj() {
        final DC_ArmorObj armor = unit.getArmor();

        final ValueContainer container;

        if (armor != null) {
            container = new ValueContainer(TextureCache.getOrCreateR(armor.getImagePath()));

            SlotItemTooltip tooltip = new ArmorTooltip(armor);

            container.addListener(tooltip.getController());
        } else {
            container = new ValueContainer(TextureCache.getOrCreateR(
             VisualEnums.CELL_TYPE.ARMOR.getSlotImagePath()));
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

            values.add(new ValueContainer(StyleHolder.getHqLabelStyle(14), PARAMS.DURABILITY.getName(), cd + "/" + d));

            final String cover = armor.getStrParam(PARAMS.COVER_PERCENTAGE);

            values.add(new ValueContainer(StyleHolder.getHqLabelStyle(14), PARAMS.COVER_PERCENTAGE.getName(), cover));
        }

        return values;
    }

    @Override
    public List<Pair<PARAMETER, String>> getMagicResistList() {
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
        DC_WeaponObj mainWeapon = unit.getOffhandWeapon();

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
                final ValueContainer valueContainer = new ValueContainer(TextureCache.getOrCreateR(el.getImagePath()));
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

    public List<ValueContainer> getFullStats() {
        List<ValueContainer> list = new ArrayList<>();
        for (List<ValueContainer> sub : getCombatStats()) {
            list.addAll(sub);
            list.add(null);
        }
        list.add(null);
        for (List<ValueContainer> sub : getMagicStats()) {
            list.addAll(sub);
            list.add(null);
        }
        list.add(null);
        for (List<ValueContainer> sub : getGeneralStats()) {
            list.addAll(sub);
            list.add(null);
        }
        list.add(null);

        for (List<ValueContainer> sub : getMiscStats()) {
            list.addAll(sub);
            list.add(null);
        }
        return list;
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
            image = TextureCache.getOrCreateR(weapon.getImagePath());
        } else {
            image = TextureCache.getOrCreateR(VisualEnums.CELL_TYPE.WEAPON_MAIN.getSlotImagePath());
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

            Tooltip tooltip = new SlotItemTooltip();
            tooltip.setUserObject(new SlotItemToolTipDataSource(weapon));
            valueContainer.addListener(tooltip.getController());
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
               valueTooltip.setUserObject(Collections.singletonList(new ValueContainer(name, value)));
               valueContainer.addListener(valueTooltip.getController());
               return valueContainer;
           }).collect(Collectors.toList())
         ));
        return values;
    }
}
