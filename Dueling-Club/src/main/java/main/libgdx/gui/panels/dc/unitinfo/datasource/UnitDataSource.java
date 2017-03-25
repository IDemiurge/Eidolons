package main.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.PARAMS;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.dialog.ValueTooltip;
import main.libgdx.gui.panels.dc.ValueContainer;
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
        MainAttributesSource, ResistSource, StatsDataSource {
    private DC_Obj unit;

    public UnitDataSource(DC_Obj unit) {
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
            final String leftVal =ActionTooltipMaster.getValueForTableParam(pair.getLeft(), el);
            final String rightVal =ActionTooltipMaster.getValueForTableParam(pair.getRight(), el);
            MultiValueContainer mvc;
            if (!ImageManager.isImage(imagePath) ){
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
        return String.valueOf(unit.getIntParam(PARAMS.STRENGTH));
    }

    @Override
    public String getVitality() {
        return String.valueOf(unit.getIntParam(PARAMS.VITALITY));
    }

    @Override
    public String getAgility() {
        return String.valueOf(unit.getIntParam(PARAMS.AGILITY));
    }

    @Override
    public String getDexterity() {
        return String.valueOf(unit.getIntParam(PARAMS.DEXTERITY));
    }

    @Override
    public String getWillpower() {
        return String.valueOf(unit.getIntParam(PARAMS.WILLPOWER));
    }

    @Override
    public String getSpellpower() {
        return String.valueOf(unit.getIntParam(PARAMS.SPELLPOWER));
    }

    @Override
    public String getIntelligence() {
        return String.valueOf(unit.getIntParam(PARAMS.INTELLIGENCE));
    }

    @Override
    public String getKnowledge() {
        return String.valueOf(unit.getIntParam(PARAMS.KNOWLEDGE));
    }

    @Override
    public String getWisdom() {
        return String.valueOf(unit.getIntParam(PARAMS.WISDOM));
    }

    @Override
    public String getCharisma() {
        return String.valueOf(unit.getIntParam(PARAMS.CHARISMA));
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
    public String getInitiative() {
        int c = unit.getIntParam(PARAMS.C_INITIATIVE);
        int m = unit.getIntParam(PARAMS.INITIATIVE);
        return c + "/" + m;
    }

    @Override
    public String getActionPoints() {
        int c = unit.getIntParam(PARAMS.C_N_OF_ACTIONS);
        int m = unit.getIntParam(PARAMS.N_OF_ACTIONS);
        return c + "/" + m;
    }

    @Override
    public List<Pair<TextureRegion, String>> getEffects() {
        return unit.getBuffs().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(obj -> new ImmutablePair<>(getOrCreateR(obj.getType().getProperty(G_PROPS.IMAGE)), obj.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair<TextureRegion, String>> getAbilities() {
        return unit.getPassives().stream()
                .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                .map(obj -> new ImmutablePair<>(getOrCreateR(obj.getType().getProperty(G_PROPS.IMAGE)), obj.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getResistance() {
        return String.valueOf(unit.getIntParam(PARAMS.RESISTANCE));
    }

    @Override
    public String getDefense() {
        return String.valueOf(unit.getIntParam(PARAMS.DEFENSE));
    }

    @Override
    public String getArmor() {
        return String.valueOf(unit.getIntParam(PARAMS.ARMOR));
    }

    @Override
    public String getFortitude() {
        return String.valueOf(unit.getIntParam(PARAMS.FORTITUDE));
    }

    @Override
    public String getSpirit() {
        return String.valueOf(unit.getIntParam(PARAMS.SPIRIT));
    }

    @Override
    public List<Pair<PARAMETER, String>> getMagickResists() {
        return Arrays.stream(RESISTANCES).map(p -> {
            String ps = String.valueOf(unit.getIntParam(p));
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Pair<PARAMETER, String>> getArmorResists() {
        return Arrays.stream(ARMOR_VS_DAMAGE_TYPES).map(p -> {
            String ps = String.valueOf(unit.getIntParam(p));
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Pair<PARAMETER, String>> getDurabilityResists() {
        return Arrays.stream(DURABILITY_VS_DAMAGE_TYPES).map(p -> {
            String ps = String.valueOf(unit.getIntParam(p));
            return new ImmutablePair<>(p, ps);
        }).collect(Collectors.toList());
    }

    @Override
    public ValueContainer getOffWeapon() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj mainWeapon = unit.getSecondWeapon();

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getOffWeaponDetailInfo() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj weapon = unit.getWeapon(true);

        return getWeaponDetail(weapon);
    }

    @Override
    public ValueContainer getNaturalOffWeapon() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj mainWeapon = unit.getNaturalWeapon(true);

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getNaturalOffWeaponDetailInfo() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj weapon = unit.getNaturalWeapon(true);

        return getWeaponDetail(weapon);
    }

    @Override
    public ValueContainer getMainWeapon() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj mainWeapon = unit.getMainWeapon();

        return getWeaponValueContainer(mainWeapon);
    }

    @Override
    public List<ValueContainer> getMainWeaponDetailInfo() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj mainWeapon = unit.getMainWeapon();

        return getWeaponDetail(mainWeapon);
    }

    @Override
    public ValueContainer getNaturalMainWeapon() {
        Unit unit = (Unit) this.unit;
        DC_WeaponObj weapon = unit.getNaturalWeapon(false);

        return getWeaponValueContainer(weapon);
    }

    @Override
    public List<ValueContainer> getNaturalMainWeaponDetailInfo() {
        Unit unit = (Unit) this.unit;
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
                                    String value = String.valueOf(unit.getIntParam(p));
                                    String name = p.getName();
                                    ValueContainer valueContainer = new ValueContainer(name, value);
                                    ValueTooltip valueTooltip = new ValueTooltip();
                                    valueTooltip.setUserObject((Supplier) () -> Arrays.asList(new ValueContainer(name, value)));
                                    valueContainer.addListener(valueTooltip.getController());
                                    return valueContainer;
                                }).collect(Collectors.toList())
                ));
        return values;
    }
}
