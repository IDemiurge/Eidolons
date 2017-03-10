package main.libgdx.gui.panels.dc.unitinfo.datasource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static main.content.ValuePages.*;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitDataSource implements
        MainParamDataSource, ResourceSource,
        AvatarDataSource, InitiativeAndActionPointsSource,
        EffectsAndAbilitiesSource, MainWeaponDataSource, OffWeaponDataSource,
        ArmorDataSource, DefenceDataSource, MainAttributesSource,
        ResistSource {
    private DC_Obj unit;

    public UnitDataSource(DC_Obj unit) {
        this.unit = unit;
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
        int c = unit.getIntParam(PARAMS.C_TOUGHNESS);
        int m = unit.getIntParam(PARAMS.TOUGHNESS);
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
                    .forEach(el -> result.add(new ValueContainer(getOrCreateR(el.getImagePath()))));
        }

        return result;
    }

    private ValueContainer getWeaponValueContainer(DC_WeaponObj weapon) {
        TextureRegion image;
        if (weapon != null) {
            image = getOrCreateR(weapon.getImagePath());
        } else {
            image = getOrCreateR("UI/components/2017/generic/inventory/empty weapon.png");
        }

        return new ValueContainer(image);
    }
}
