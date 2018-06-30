package eidolons.libgdx.gui.panels.headquarters.datasource.hero;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.libgdx.gui.datasource.EntityDataSource;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.VerticalValueContainer;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.*;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.system.auxiliary.StringMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqHeroDataSource extends EntityDataSource<HeroDataModel>
 implements
 AttributesDataSource, ResourceSource,
 AvatarDataSource, CounterAndActionPointsSource,
 EffectsAndAbilitiesSource, MainWeaponDataSource<ValueContainer>, OffWeaponDataSource,
 MainAttributesSource, ResistSource, StatsDataSource,
 ArmorDataSource
{

    protected final UnitDataSource unitDataSource;
    protected boolean editable;

    public HqHeroDataSource(HeroDataModel entity) {
        super(entity);
        unitDataSource = new UnitDataSource(entity);
    }

    public UnitDataSource getUnitDataSource() {
        return unitDataSource;
    }

    public int getLevel() {
        return  entity.getLevel() ;
    }

    public int getDefaultAttribute(PARAMS sub) {
        return entity.getIntParam(ContentValsManager.
         getDefaultAttribute(sub));
    }

    public String getFullPreviewImagePath() {
        return StringMaster.getAppendedImageFile(getImagePath(), " full");
    }

    public boolean isDead() {
        return entity.isDead();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String getStrength() {
        return unitDataSource.getStrength();
    }

    @Override
    public String getVitality() {
        return unitDataSource.getVitality();
    }

    @Override
    public String getAgility() {
        return unitDataSource.getAgility();
    }

    @Override
    public String getDexterity() {
        return unitDataSource.getDexterity();
    }

    @Override
    public String getWillpower() {
        return unitDataSource.getWillpower();
    }

    @Override
    public String getSpellpower() {
        return unitDataSource.getSpellpower();
    }

    @Override
    public String getIntelligence() {
        return unitDataSource.getIntelligence();
    }

    @Override
    public String getKnowledge() {
        return unitDataSource.getKnowledge();
    }

    @Override
    public String getWisdom() {
        return unitDataSource.getWisdom();
    }

    @Override
    public String getCharisma() {
        return unitDataSource.getCharisma();
    }

    @Override
    public String getAttribute(String name) {
        return unitDataSource.getAttribute(name);
    }

    @Override
    public String getToughness() {
        return unitDataSource.getToughness();
    }

    @Override
    public String getEndurance() {
        return unitDataSource.getEndurance();
    }

    @Override
    public String getStamina() {
        return unitDataSource.getStamina();
    }

    @Override
    public String getMorale() {
        return unitDataSource.getMorale();
    }

    @Override
    public String getEssence() {
        return unitDataSource.getEssence();
    }

    @Override
    public String getFocus() {
        return unitDataSource.getFocus();
    }

    @Override
    public String getParam(PARAMS param) {
        return unitDataSource.getParam(param);
    }

    @Override
    public TextureRegion getAvatar() {
        return unitDataSource.getAvatar();
    }

    @Override
    public String getParam1() {
        return unitDataSource.getParam1();
    }

    @Override
    public String getParam2() {
        return unitDataSource.getParam2();
    }

    @Override
    public ValueContainer getCounterPoints() {
        return unitDataSource.getCounterPoints();
    }

    @Override
    public ValueContainer getActionPoints() {
        return unitDataSource.getActionPoints();
    }

    public VerticalValueContainer getParamContainer(PARAMETER parameter) {
        return unitDataSource.getParamContainer(parameter);
    }

    @Override
    public VerticalValueContainer getResistance() {
        return unitDataSource.getResistance();
    }

    @Override
    public VerticalValueContainer getDefense() {
        return unitDataSource.getDefense();
    }

    public VerticalValueContainer getValueContainer(PARAMETER param, String string) {
        return unitDataSource.getValueContainer(param, string);
    }

    @Override
    public VerticalValueContainer getFortitude() {
        return unitDataSource.getFortitude();
    }

    @Override
    public VerticalValueContainer getSpirit() {
        return unitDataSource.getSpirit();
    }

    @Override
    public VerticalValueContainer getArmor() {
        return unitDataSource.getArmor();
    }

    @Override
    public List<ValueContainer> getBuffs() {
        return unitDataSource.getBuffs();
    }

    @Override
    public List<ValueContainer> getAbilities() {
        return unitDataSource.getAbilities();
    }

    @Override
    public ValueContainer getArmorObj() {
        return unitDataSource.getArmorObj();
    }

    @Override
    public List<ValueContainer> getParamValues() {
        return unitDataSource.getParamValues();
    }

    @Override
    public List<Pair<PARAMETER, String>> getMagickResists() {
        return unitDataSource.getMagickResists();
    }

    @Override
    public List<Pair<PARAMETER, String>> getArmorResists() {
        return unitDataSource.getArmorResists();
    }

    @Override
    public List<Pair<PARAMETER, String>> getDurabilityResists() {
        return unitDataSource.getDurabilityResists();
    }

    @Override
    public ValueContainer getOffWeapon() {
        return unitDataSource.getOffWeapon();
    }

    @Override
    public List<ValueContainer> getOffWeaponDetailInfo() {
        return unitDataSource.getOffWeaponDetailInfo();
    }

    @Override
    public ValueContainer getNaturalOffWeapon() {
        return unitDataSource.getNaturalOffWeapon();
    }

    @Override
    public List<ValueContainer> getNaturalOffWeaponDetailInfo() {
        return unitDataSource.getNaturalOffWeaponDetailInfo();
    }

    @Override
    public ValueContainer getMainWeapon() {
        return unitDataSource.getMainWeapon();
    }

    @Override
    public List<ValueContainer> getMainWeaponDetailInfo() {
        return unitDataSource.getMainWeaponDetailInfo();
    }

    @Override
    public ValueContainer getNaturalMainWeapon() {
        return unitDataSource.getNaturalMainWeapon();
    }

    @Override
    public List<ValueContainer> getNaturalMainWeaponDetailInfo() {
        return unitDataSource.getNaturalMainWeaponDetailInfo();
    }

    @Override
    public List<List<ValueContainer>> getGeneralStats() {
        return unitDataSource.getGeneralStats();
    }

    @Override
    public List<List<ValueContainer>> getCombatStats() {
        return unitDataSource.getCombatStats();
    }

    public List<ValueContainer> getFullStats() {
        return unitDataSource.getFullStats();
    }

    @Override
    public List<List<ValueContainer>> getMagicStats() {
        return unitDataSource.getMagicStats();
    }

    @Override
    public List<List<ValueContainer>> getMiscStats() {
        return unitDataSource.getMiscStats();
    }

    public List<VALUE> getStatsValueList(VALUE[][] paramsGeneral) {
        return unitDataSource.getStatsValueList(paramsGeneral);
    }

    public List<VALUE> getStatsValues() {
        return unitDataSource.getStatsValues();
    }
}
