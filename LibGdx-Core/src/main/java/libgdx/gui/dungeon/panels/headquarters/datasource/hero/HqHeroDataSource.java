package libgdx.gui.dungeon.panels.headquarters.datasource.hero;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import libgdx.gui.dungeon.datasource.EntityDataSource;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.VerticalValueContainer;
import libgdx.gui.dungeon.panels.dc.unitinfo.datasource.*;
import eidolons.system.libgdx.datasource.HeroDataModel;
import eidolons.content.consts.Sprites;
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

    public String getSpritePath() {
        return Sprites.getHeroSpritePath(getName());
    }
    public HqHeroDataSource(HeroDataModel entity) {
        super(entity);
        unitDataSource = new UnitDataSource(entity);
    }

    @Override
    public List<ValueContainer> getBuffs(boolean body) {
        return unitDataSource.getBuffs(body);
    }

    @Override
    public List<ValueContainer> getAbilities(boolean body) {
        return unitDataSource.getAbilities(body);
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
    public String getEssence() {
        return unitDataSource.getEssence();
    }

    @Override
    public String getFocus() {
        return unitDataSource.getFocus();
    }

    @Override
    public String getParam(PARAMS param) {
        return getParam(param.getName());
    }

    @Override
    public TextureRegion getAvatar() {
        return unitDataSource.getAvatar();
    }
    public TextureRegion getLargeImage() {
        return unitDataSource.getLargeImage();
    }
    public TextureRegion getFullSizeImage() {
        return unitDataSource.getFullSizeImage();
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
    public VerticalValueContainer getResistance() {
        return unitDataSource.getResistance();
    }

    @Override
    public VerticalValueContainer getDefense() {
        return unitDataSource.getDefense();
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
    public ValueContainer getArmorObj() {
        return unitDataSource.getArmorObj();
    }

    @Override
    public List<ValueContainer> getParamValues() {
        return unitDataSource.getParamValues();
    }

    @Override
    public List<Pair<PARAMETER, String>> getMagicResistList() {
        return unitDataSource.getMagicResistList();
    }

    @Override
    public List<Pair<PARAMETER, String>> getArmorResists() {
        return unitDataSource.getArmorResists();
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
        return UnitDataSource.getStatsValueList(paramsGeneral);
    }

    public List<VALUE> getStatsValues() {
        return UnitDataSource.getStatsValues();
    }

}
