package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.ability.InventoryTransactionManager;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import main.ability.effects.Effect;
import main.content.VALUE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 4/15/2018.
 * cloneMaps
 * take same items?
 * same item cannot be equipped by 2 entities...
 * <p>
 * ok, but at least share among the models ?
 * yes, just equip the one that is displayed
 */
public class HeroDataModel extends Unit {
    protected List<HeroOperation> modificationList = new ArrayList<>();
    protected Unit hero;
    protected boolean resetting;

    public HeroDataModel(Unit hero) {
        this(new ObjType(hero.getType(), true), hero.getX(), hero.getY(),
         hero.getOriginalOwner(),
         Simulation.getGame(), new Ref(Simulation.getGame()));
        copyDynamicParams(hero); //for dynamic params!
        setHero(hero);
        reset();
        //        cacheSimItems();
    }

    public HeroDataModel(ObjType type, int x, int y, Player originalOwner, DC_Game game, Ref ref) {
        super(type, x, y, originalOwner, game, ref);
    }
    public boolean isPlayerCharacter() {
        return true;
//        return getHero().isPlayerCharacter();
    }
    @Override
    public boolean isSimulation() {
        return true;
    }

    public HeroDataModel(ObjType type, Pair<ParamMap, PropMap> pair) {
        super(type);
        cloneMaps(pair.getRight(), pair.getLeft());
    }

    public void setHero(Unit hero) {
        this.hero = hero;
        type.copyValues(getHero(), InventoryTransactionManager.INV_PROPS);
        copyValues(getHero(), InventoryTransactionManager.INV_PROPS);
    }

    @Override
    public void init() {
        super.init();
    }

    protected void cacheSimItems() {
        cacheSimItemContainer(hero.getInventory(), getInventory());
        cacheSimItemContainer(hero.getQuickItems(), getQuickItems());
        cacheSimItemContainer(hero.getJewelry(), getJewelry());

        HqMaster.getSimCache().addSim(hero.getWeapon(true), hero.getWeapon(true));
        HqMaster.getSimCache().addSim(hero.getWeapon(false), hero.getWeapon(false));
        HqMaster.getSimCache().addSim(hero.getNaturalWeapon(true), hero.getNaturalWeapon(true));
        HqMaster.getSimCache().addSim(hero.getNaturalWeapon(false), hero.getNaturalWeapon(false));
        HqMaster.getSimCache().addSim(hero.getArmor(), hero.getArmor());
    }

    protected void cacheSimItemContainer(DequeImpl<? extends DC_HeroItemObj> inventory,
                                       DequeImpl<? extends DC_HeroItemObj> inventory1) {
        int i = 0;
        for (DC_HeroItemObj real : inventory) {
            HqMaster.getSimCache().addSim(real, inventory1.get(i++));
        }
    }

    public HeroOperation modified(HERO_OPERATION operation, Object... arg) {
        HeroOperation o = new HeroOperation(operation, arg);
        modificationList.add(o);
        return o;
    }

    @Override
    public String getDescription(Ref ref) {
        return getHero().getDescription(ref);
    }

    @Override
    public String getCustomValue(String value_ref) {
        return getHero().getCustomValue(value_ref);
    }

    @Override
    public String getCustomProperty(String value_ref) {
        return getHero().getCustomProperty(value_ref);
    }

    @Override
    public Integer getCounter(String value_ref) {
        return getHero().getCounter(value_ref);
    }

    @Override
    public void setGroup(String group, boolean base) {
        getHero().setGroup(group, base);
    }

    @Override
    public boolean setCounter(String name, int newValue) {
        return getHero().setCounter(name, newValue);
    }

    @Override
    public boolean setCounter(String name, int newValue, boolean strict) {
        return getHero().setCounter(name, newValue, strict);
    }

    @Override
    public void removeCounter(String name) {
        getHero().removeCounter(name);
    }

    @Override
    public boolean modifyCounter(String name, int modValue) {
        return getHero().modifyCounter(name, modValue);
    }

    @Override
    public boolean modifyCounter(UnitEnums.COUNTER counter, int modValue) {
        return getHero().modifyCounter(counter, modValue);
    }

    @Override
    public boolean modifyCounter(String name, int modValue, boolean strict) {
        return getHero().modifyCounter(name, modValue, strict);
    }

    @Override
    public String getParam(String p) {
        return getHero().getParam(p);
    }

    @Override
    public String getParam(PARAMETER param) {
        return getHero().getParam(param);
    }

    @Override
    public Double getParamDouble(PARAMETER param) {
        return getHero().getParamDouble(param);
    }

    @Override
    public Float getParamFloat(PARAMETER param) {
        return getHero().getParamFloat(param);
    }

    @Override
    public Double getParamDouble(PARAMETER param, boolean base) {
        return getHero().getParamDouble(param, base);
    }

    @Override
    public String getDoubleParam(PARAMETER param) {
        return getHero().getDoubleParam(param);
    }

    @Override
    public String getDoubleParam(PARAMETER param, boolean base) {
        return getHero().getDoubleParam(param, base);
    }

    @Override
    public Integer getIntParam(String param) {
        return getHero().getIntParam(param);
    }

    @Override
    public String getStrParam(String param) {
        return getHero().getStrParam(param);
    }

    @Override
    public String getStrParam(PARAMETER param) {
        return getHero().getStrParam(param);
    }

    @Override
    public Integer getIntParam(PARAMETER param) {
        return getHero().getIntParam(param);
    }

    @Override
    public Integer getIntParam(PARAMETER param, boolean base) {
        return getHero().getIntParam(param, base);
    }

    @Override
    public Map<PARAMETER, Integer> getIntegerMap() {
        return getHero().getIntegerMap();
    }

    @Override
    public Map<PARAMETER, Integer> getIntegerMap(boolean base) {
        return getHero().getIntegerMap(base);
    }

    @Override
    public ParamMap getParamMap() {
        return getHero().getParamMap();
    }

    @Override
    public void setParamMap(ParamMap paramMap) {
        getHero().setParamMap(paramMap);
    }

    @Override
    public void getBoolean(VALUE prop, Boolean b) {
        getHero().getBoolean(prop, b);
    }

    @Override
    public Boolean getBoolean(String prop) {
        return getHero().getBoolean(prop);
    }

    @Override
    public String getProperty(String prop) {
        return getHero().getProperty(prop);
    }

    @Override
    public String getProp(String prop) {
        return getHero().getProp(prop);
    }

    @Override
    public String getGroup() {
        return getHero().getGroup();
    }

    @Override
    public String getProperty(PROPERTY prop) {
        return getHero().getProperty(prop);
    }

    @Override
    public boolean checkValue(VALUE v) {
        return getHero().checkValue(v);
    }

    @Override
    public boolean checkValue(VALUE v, String value) {
        return getHero().checkValue(v, value);
    }

    @Override
    public boolean checkParam(PARAMETER param) {
        return getHero().checkParam(param);
    }

    @Override
    public boolean checkParameter(PARAMETER param, int value) {
        return getHero().checkParameter(param, value);
    }

    @Override
    public boolean checkParam(PARAMETER param, String value) {
        return getHero().checkParam(param, value);
    }

    @Override
    public boolean checkParam(PARAMETER param, int value) {
        return getHero().checkParam(param, value);
    }

    @Override
    public boolean checkProperty(PROPERTY p, String value) {
        return getHero().checkProperty(p, value);
    }

    @Override
    public Map<PROPERTY, Map<String, Boolean>> getPropCache(boolean base) {
        return getHero().getPropCache(base);
    }

    @Override
    public boolean checkProperty(PROPERTY p, String value, boolean base) {
        return getHero().checkProperty(p, value, base);
    }

    @Override
    public boolean checkSingleProp(String PROP, String value) {
        return getHero().checkSingleProp(PROP, value);
    }

    @Override
    public boolean checkSingleProp(PROPERTY PROP, String value) {
        return getHero().checkSingleProp(PROP, value);
    }

    @Override
    public boolean checkContainerProp(PROPERTY PROP, String value) {
        return getHero().checkContainerProp(PROP, value);
    }

    @Override
    public boolean checkContainerProp(PROPERTY PROP, String value, boolean any) {
        return getHero().checkContainerProp(PROP, value, any);
    }

    @Override
    public boolean checkSubGroup(String string) {
        return getHero().checkSubGroup(string);
    }

    @Override
    public boolean checkProperty(PROPERTY p) {
        return getHero().checkProperty(p);
    }

    @Override
    public boolean checkGroup(String string) {
        return getHero().checkGroup(string);
    }

    @Override
    public String getProperty(PROPERTY prop, boolean base) {
        return getHero().getProperty(prop, base);
    }

    @Override
    public PropMap getPropMap() {
        return getHero().getPropMap();
    }

    @Override
    public void setPropMap(PropMap propMap) {
        getHero().setPropMap(propMap);
    }

    @Override
    public Ref getRef() {
        return getHero().getRef();
    }

    @Override
    public ObjType getType() {
        return getHero().getType();
    }

    @Override
    public void setType(ObjType type) {
        getHero().setType(type);
    }

    @Override
    public String getValue(String valName) {
        return getHero().getValue(valName);
    }

    @Override
    public String getValue(VALUE valName) {
        return getHero().getValue(valName);
    }

    @Override
    public String getValue(VALUE val, boolean base) {
        return getHero().getValue(val, base);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly, String modifierKey) {
        return getHero().modifyParameter(param, amount, minMax, quietly, modifierKey);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, boolean quietly) {
        return getHero().modifyParameter(param, amount, minMax, quietly);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly) {
        return getHero().modifyParameter(param, amountString, minMax, quietly);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, String amountString, Integer minMax, boolean quietly, String modifierKey) {
        return getHero().modifyParameter(param, amountString, minMax, quietly, modifierKey);
    }

    @Override
    public Map<PARAMETER, Map<String, Double>> getModifierMaps() {
        return getHero().getModifierMaps();
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax, String modifierKey) {
        return getHero().modifyParameter(param, amount, minMax, modifierKey);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount, Integer minMax) {
        return getHero().modifyParameter(param, amount, minMax);
    }

    @Override
    public void modifyParameter(PARAMETER param, int amount, boolean base) {
        getHero().modifyParameter(param, amount, base);
    }

    @Override
    public void modifyParameter(PARAMETER param, int amount, boolean base, String modifierKey) {
        getHero().modifyParameter(param, amount, base, modifierKey);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount, String modifierKey) {
        return getHero().modifyParameter(param, amount, modifierKey);
    }

    @Override
    public boolean modifyParameter(PARAMETER param, int amount) {
        return getHero().modifyParameter(param, amount);
    }

    @Override
    public void decrementParam(PARAMETER param) {
        getHero().decrementParam(param);
    }

    @Override
    public int getContainerCount(PROPERTY p) {
        return getHero().getContainerCount(p);
    }

    @Override
    public void incrementParam(PARAMETER param) {
        getHero().incrementParam(param);
    }

    @Override
    public boolean multiplyParamByPercent(PARAMETER param, int perc, boolean base) {
        return getHero().multiplyParamByPercent(param, perc, base);
    }

    @Override
    public boolean modifyParamByPercent(PARAMETER[] params, int perc) {
        return getHero().modifyParamByPercent(params, perc);
    }

    @Override
    public boolean modifyParamByPercent(PARAMETER param, int perc) {
        return getHero().modifyParamByPercent(param, perc);
    }

    @Override
    public boolean modifyParamByPercent(PARAMETER param, int perc, boolean base) {
        return getHero().modifyParamByPercent(param, perc, base);
    }

    @Override
    public void resetDynamicParam(PARAMETER param) {
        getHero().resetDynamicParam(param);
    }

    @Override
    public void setParam(PARAMETER param, int i, boolean quietly) {
        getHero().setParam(param, i, quietly);
    }

    @Override
    public void setParamDouble(PARAMETER param, double i, boolean quietly) {
        getHero().setParamDouble(param, i, quietly);
    }

    @Override
    public void setParameter(PARAMETER param, int i) {
        getHero().setParameter(param, i);
    }

    @Override
    public void setParam(PARAMETER param, int i) {
        getHero().setParam(param, i);
    }

    @Override
    public void setParam(String param, int i) {
        getHero().setParam(param, i);
    }

    @Override
    public void setParamMax(PARAMETER p, int i) {
        getHero().setParamMax(p, i);
    }

    @Override
    public void setParamMin(PARAMETER p, int i) {
        getHero().setParamMin(p, i);
    }

    @Override
    public void modifyParameter(String param, String string) {
        getHero().modifyParameter(param, string);
    }

    @Override
    public void modifyParamByPercent(String param, String string) {
        getHero().modifyParamByPercent(param, string);
    }


    @Override
    public void resetPercentage(PARAMETER p) {
        getHero().resetPercentage(p);
    }

    @Override
    public boolean setParam(PARAMETER param, String value) {
        return getHero().setParam(param, value);
    }

    @Override
    public void setProperty(String prop, String value) {
        getHero().setProperty(prop, value);
    }

    @Override
    public void setProperty(PROPERTY prop, String value) {
        getHero().setProperty(prop, value);
    }

    @Override
    public void modifyProperty(Effect.MOD_PROP_TYPE p, PROPERTY prop, String value) {
        getHero().modifyProperty(p, prop, value);
    }

    @Override
    public void removeLastPartFromProperty(PROPERTY prop) {
        getHero().removeLastPartFromProperty(prop);
    }

    @Override
    public void removeFromProperty(PROPERTY prop, String value) {
        getHero().removeFromProperty(prop, value);
    }

    @Override
    public void appendProperty(PROPERTY prop, String value) {
        getHero().appendProperty(prop, value);
    }

    @Override
    public boolean addOrRemoveProperty(PROPERTY prop, String value) {
        return getHero().addOrRemoveProperty(prop, value);
    }

    @Override
    public boolean addProperty(PROPERTY prop, String value) {
        return getHero().addProperty(prop, value);
    }

    @Override
    public boolean addProperty(PROPERTY prop, List<String> values, boolean noDuplicates) {
        return getHero().addProperty(prop, values, noDuplicates);
    }

    @Override
    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates) {
        return getHero().addProperty(prop, value, noDuplicates);
    }

    @Override
    public boolean addProperty(PROPERTY prop, String value, boolean noDuplicates, boolean addInFront) {
        return getHero().addProperty(prop, value, noDuplicates, addInFront);
    }



    @Override
    public void addProperty(String prop, String value) {
        getHero().addProperty(prop, value);
    }

    @Override
    public boolean clearProperty(PROPERTY prop) {
        return getHero().clearProperty(prop);
    }

    @Override
    public boolean removeProperty(PROPERTY prop) {
        return getHero().removeProperty(prop);
    }

    @Override
    public boolean addProperty(boolean base, PROPERTY prop, String value) {
        return getHero().addProperty(base, prop, value);
    }

    @Override
    public boolean removeProperty(boolean base, PROPERTY prop, String value) {
        return getHero().removeProperty(base, prop, value);
    }

    @Override
    public boolean removeProperty(PROPERTY prop, String value) {
        return getHero().removeProperty(prop, value);
    }

    @Override
    public boolean removeProperty(PROPERTY prop, String value, boolean all) {
        return getHero().removeProperty(prop, value, all);
    }

    public List<HeroOperation> getModificationList() {
        return modificationList;
    }

    public void setModificationList(List<HeroOperation> modificationList) {
        this.modificationList = modificationList;
    }

    @Override
    public void reset() {
        resetting = true;
        super.reset();
        resetting = false;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public Unit getHero() {
        return hero;
    }

    @Override
    public void modified(ModifyValueEffect modifyValueEffect) {
        super.modified(modifyValueEffect);
        //full reset?
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return (DC_FeatObj) getGame().getSimulationObj(this, type,
         skill ? PROPS.SKILLS : PROPS.CLASSES);
    }

    public enum HERO_OPERATION {
        PICK_UP, DROP, UNEQUIP,  UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT, UNEQUIP_JEWELRY,
        ATTRIBUTE_INCREMENT,
        MASTERY_INCREMENT,
        NEW_MASTERY,

        NEW_SKILL, SKILL_RANK,
        NEW_CLASS, CLASS_RANK,

        SPELL_LEARNED,
        SPELL_MEMORIZED,
        SPELL_EN_VERBATIM,
        SPELL_UNMEMORIZED, NEW_PERK, LEVEL_UP,
        SET_PROPERTY, SET_PARAMETER,   ADD_PARAMETER,


        APPLY_TYPE,

        BUY, SELL, UNSTASH, STASH, EQUIP_RESERVE,



    }

    public static class HeroOperation {
        HERO_OPERATION operation;
        Object[] arg;

        public HeroOperation(HERO_OPERATION operation, Object... arg) {
            this.operation = operation;
            this.arg = arg;
        }

        public HERO_OPERATION getOperation() {
            return operation;
        }

        public Object[] getArg() {
            return arg;
        }
    }
}
