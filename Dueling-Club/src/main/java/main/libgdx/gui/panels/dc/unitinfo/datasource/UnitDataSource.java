package main.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_ArmorObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.dialog.ValueTooltip;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.MultiValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionToolTip;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster;
import main.libgdx.gui.panels.dc.unitinfo.tooltips.WeaponToolTip;
import main.libgdx.texture.TextureCache;
import main.system.images.ImageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static main.content.UNIT_INFO_PARAMS.*;
import static main.content.UNIT_INFO_PARAMS.ActionToolTipSections.*;
import static main.content.ValuePages.*;
import static main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster.getIconPathForTableRow;
import static main.libgdx.gui.panels.dc.unitinfo.tooltips.ActionTooltipMaster.getStringForTableValue;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitDataSource implements
        MainParamDataSource, ResourceSource,
        AvatarDataSource, InitiativeAndActionPointsSource,
        EffectsAndAbilitiesSource, MainWeaponDataSource, OffWeaponDataSource,
        MainAttributesSource, ResistSource, StatsDataSource,
        ArmorDataSource {
    private Unit unit;

    public UnitDataSource(Unit unit) {
        this.unit = unit;
    }

    private static List<MultiValueContainer> extractActionValues(DC_UnitAction el
            , VALUE[] baseKeys) {
        List<MultiValueContainer> list = new ArrayList<>();
        Pair<PARAMS, PARAMS> pair;
        for (VALUE key : baseKeys) {
            pair = ACTION_TOOLTIPS_PARAMS_MAP.get(key);

            String name = getStringForTableValue(key, el);
            String imagePath = getIconPathForTableRow(key);
            final String leftVal = ActionTooltipMaster.getValueForTableParam(pair.getLeft(), el);
            final String rightVal = ActionTooltipMaster.getValueForTableParam(pair.getRight(), el);
            MultiValueContainer mvc;
            if (!ImageManager.isImage(imagePath)) {
                mvc = new MultiValueContainer(name, leftVal, rightVal);
            } else {
                mvc = new MultiValueContainer(getOrCreateR(imagePath), name, leftVal, rightVal);
            }
            list.add(mvc);
        }
        return list;
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
    public Texture getAvatar() {
        return TextureCache.getOrCreate(unit.getImagePath());
    }

    @Override
    public String getName() {
        return unit.getName();
    }

    @Override
    public String getParam1() {
        return "Level: " + unit.getParam("Level");
    }

    @Override
    public String getParam2() {
        return unit.getValue("Race");
    }

    @Override
    public ValueContainer getInitiative() {
        int c = unit.getIntParam(PARAMS.C_INITIATIVE);
        int m = unit.getIntParam(PARAMS.INITIATIVE);
        final String value = c + "/" + m;

        VerticalValueContainer container = new VerticalValueContainer(getOrCreateR("UI/value icons/n_of_counters_s.png"), value);

        ValueTooltip toolTip = new ValueTooltip();
        toolTip.setUserObject(new ValueContainer(PARAMS.INITIATIVE.getName(), value));
        container.addListener(toolTip.getController());

        return container;
    }

    @Override
    public ValueContainer getActionPoints() {
        int c = unit.getIntParam(PARAMS.C_N_OF_ACTIONS);
        int m = unit.getIntParam(PARAMS.N_OF_ACTIONS);
        final String value = c + "/" + m;

        VerticalValueContainer container = new VerticalValueContainer(getOrCreateR("UI/value icons/n_of_actions_s.png"), value);

        ValueTooltip toolTip = new ValueTooltip();
        toolTip.setUserObject(new ValueContainer(PARAMS.N_OF_ACTIONS.getName(), value));
        container.addListener(toolTip.getController());

        return container;
    }

    @Override
    public List<ValueContainer> getEffects() {
        return unit.getBuffs().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(getObjValueContainerMapper())
                .collect(Collectors.toList());
    }

    @Override
    public List<ValueContainer> getAbilities() {
        return unit.getPassives().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(getObjValueContainerMapper())
                .collect(Collectors.toList());
    }

    private <T extends Obj> Function<T, ValueContainer> getObjValueContainerMapper() {
        return obj -> {
            final ValueContainer container = new ValueContainer(getOrCreateR(obj.getType().getProperty(G_PROPS.IMAGE)));

            ToolTip toolTip = new ValueTooltip();
            toolTip.setUserObject(new ValueContainer(obj.getName(), ""));
            container.addListener(toolTip.getController());

            return container;
        };
    }

    @Override
    public VerticalValueContainer getResistance() {
        final String param = unit.getStrParam(PARAMS.RESISTANCE);
        final VerticalValueContainer container =
                new VerticalValueContainer(
                        getOrCreateR("UI/value icons/resistance.jpg"),
                        param);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(PARAMS.RESISTANCE.getName(), param));
        container.addListener(tooltip.getController());

        return container;
    }

    @Override
    public VerticalValueContainer getDefense() {
        final String param = unit.getStrParam(PARAMS.DEFENSE);
        final VerticalValueContainer container =
                new VerticalValueContainer(
                        getOrCreateR("UI/value icons/Defense.jpg"),
                        param);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(PARAMS.DEFENSE.getName(), param));
        container.addListener(tooltip.getController());

        return container;
    }

    @Override
    public VerticalValueContainer getFortitude() {
        final String param = unit.getStrParam(PARAMS.FORTITUDE);
        final VerticalValueContainer container =
                new VerticalValueContainer(
                        getOrCreateR("UI/value icons/Fortitude.jpg"),
                        param);
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(PARAMS.FORTITUDE.getName(), param));
        container.addListener(tooltip.getController());

        return container;
    }

    @Override
    public VerticalValueContainer getSpirit() {
        final String param = unit.getStrParam(PARAMS.SPIRIT);
        final VerticalValueContainer container =
                new VerticalValueContainer(
                        getOrCreateR("UI/value icons/spirit.jpg"),
                        param);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(PARAMS.SPIRIT.getName(), param));
        container.addListener(tooltip.getController());
        return container;
    }

    @Override
    public VerticalValueContainer getArmor() {
        final String param = unit.getStrParam(PARAMS.ARMOR);
        final VerticalValueContainer container =
                new VerticalValueContainer(
                        getOrCreateR("UI/value icons/armor.jpg"),
                        param);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(new ValueContainer(PARAMS.ARMOR.getName(), param));
        container.addListener(tooltip.getController());

        container.addListener(tooltip.getController());
        return container;
    }

    @Override
    public ValueContainer getArmorObj() {
        final DC_ArmorObj armor = unit.getArmor();


        final ValueContainer container;

        if (armor != null) {
            container = new ValueContainer(getOrCreateR(armor.getImagePath()));

            ValueTooltip tooltip = new ValueTooltip();
            tooltip.setUserObject(new ValueContainer("", "This is my armor. There are many like it, but this one is mine.\n" +
                    "My armor is my best friend. It is my life. \n" +
                    "Without me, my armor is useless. Without my armor, I am useless."));
            container.addListener(tooltip.getController());
        } else {
            container = new ValueContainer(getOrCreateR("/mini/item/armor/empty.jpg"));
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

            values.add(new ValueContainer(PARAMS.DURABILITY.getName(), d + "/" + cd));

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

        return getWeaponDetail(mainWeapon);
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
            weapon.getAttackActions()
                    .forEach(el -> {
                        final ValueContainer valueContainer = new ValueContainer(getOrCreateR(el.getImagePath()));

                        Map<ActionToolTipSections, List<MultiValueContainer>> map = new HashMap<>();

                        Pair<PARAMS, PARAMS> pair = ACTION_TOOLTIPS_PARAMS_MAP.get(ACTION_TOOLTIP_HEADER_KEY);
                        {
                            String name = getStringForTableValue(ACTION_TOOLTIP_HEADER_KEY, el);
                            final String leftImage = ActionTooltipMaster.getIconPathForTableRow(pair.getLeft());
                            final String rightImage = ActionTooltipMaster.getIconPathForTableRow(pair.getRight());
                            MultiValueContainer mvc = new MultiValueContainer(name, leftImage, rightImage);
                            map.put(HEAD, Arrays.asList(mvc));
                        }

                        VALUE[] baseKeys = ACTION_TOOLTIP_BASE_KEYS;
                        map.put(BASE, extractActionValues(el, baseKeys));

                        baseKeys = ACTION_TOOLTIP_RANGE_KEYS;
                        map.put(RANGE, extractActionValues(el, baseKeys));

                        List/*<List<MultiValueContainer>>*/ textsList = new ArrayList<>();
                        for (PARAMS[] params : ACTION_TOOLTIP_PARAMS_TEXT) {
                            textsList.add(Arrays.stream(params).map(p ->
                                    new ValueContainer(
                                            ActionTooltipMaster.
                                                    getTextForTableValue(p, el), "")
                            ).collect(Collectors.toList()));
                        }
                        map.put(TEXT, textsList);

                        ToolTip toolTip = new ActionToolTip();
                        toolTip.setUserObject((Supplier) () -> map);
                        valueContainer.addListener(toolTip.getController());
                        result.add(valueContainer);
                    });
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
            image = getOrCreateR("UI/components/2017/generic/inventory/empty weapon.png");
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
            toolTip.setUserObject((Supplier) () -> list);
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
                                    valueTooltip.setUserObject(new ValueContainer(name, value));
                                    valueContainer.addListener(valueTooltip.getController());
                                    return valueContainer;
                                }).collect(Collectors.toList())
                ));
        return values;
    }
}
