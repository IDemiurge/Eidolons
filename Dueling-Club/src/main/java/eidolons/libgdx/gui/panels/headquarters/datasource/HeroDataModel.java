package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.ability.InventoryTransactionManager;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import main.entity.type.ObjType;
import main.system.datatypes.DequeImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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
    List<HqOperation> modificationList;
    private Unit hero;
    private boolean resetting;

    public HeroDataModel(Unit hero) {
        super(new ObjType(hero.getType(), true), hero.getX(), hero.getY(),
         hero.getOriginalOwner(),
         Simulation.getGame(), hero.getRef().getCopy());
        copyDynamicParams(hero); //for dynamic params!
        this.hero = hero;
        reset();
//        cacheSimItems();
        modificationList = new ArrayList<>();
    }

    @Override
    public void init() {
        type.copyValues(getHero(), InventoryTransactionManager.INV_PROPS);
        copyValues(getHero(), InventoryTransactionManager.INV_PROPS);
        super.init();
    }

    private void cacheSimItems() {
        cacheSimItemContainer(hero.getInventory(), getInventory());
        cacheSimItemContainer(hero.getQuickItems(), getQuickItems());
        cacheSimItemContainer(hero.getJewelry(), getJewelry());

        HqMaster.getSimCache().addSim(hero.getWeapon(true), hero.getWeapon(true));
        HqMaster.getSimCache().addSim(hero.getWeapon(false), hero.getWeapon(false));
        HqMaster.getSimCache().addSim(hero.getNaturalWeapon(true), hero.getNaturalWeapon(true));
        HqMaster.getSimCache().addSim(hero.getNaturalWeapon(false), hero.getNaturalWeapon(false));
        HqMaster.getSimCache().addSim(hero.getArmor(), hero.getArmor());
    }

    private void cacheSimItemContainer(DequeImpl<? extends DC_HeroItemObj> inventory,
                                       DequeImpl<? extends DC_HeroItemObj> inventory1) {
        int i = 0;
        for (DC_HeroItemObj real : inventory) {
            HqMaster.getSimCache().addSim(real, inventory1.get(i++));
        }
    }

    public void modified(HQ_OPERATION operation, Object... arg) {
        modificationList.add(new HqOperation(operation, arg));
    }

    public List<HqOperation> getModificationList() {
        return modificationList;
    }

    public void setModificationList(List<HqOperation> modificationList) {
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

    public HeroDataModel(ObjType type, Pair<ParamMap, PropMap> pair) {
        super(type);
        cloneMaps(pair.getRight(), pair.getLeft());
    }


    public Unit getHero() {
        return hero;
    }

    @Override
    public void modified(ModifyValueEffect modifyValueEffect) {
        super.modified(modifyValueEffect);
        //full reset?
    }

    @Override
    public DC_HeroItemObj getItem(ITEM_SLOT slot) {
        return super.getItem(slot);
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return (DC_FeatObj) getGame().getSimulationObj(this, type,
         skill ? PROPS.SKILLS : PROPS.CLASSES);
    }

    @Override
    public void addItemToInventory(DC_HeroItemObj item) {
        super.addItemToInventory(item);
    }

    @Override
    public boolean dropItemFromInventory(DC_HeroItemObj item) {
        return super.dropItemFromInventory(item);
    }

    @Override
    public boolean equip(DC_HeroItemObj item, ITEM_SLOT slot) {
        return super.equip(item, slot);
    }

    public enum HQ_OPERATION {
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT,UNEQUIP_JEWELRY,

        ATTRIBUTE_INCREMENT,
        MASTERY_INCREMENT,
        NEW_MASTERY,

        NEW_SKILL, SKILL_RANK,
        NEW_CLASS, CLASS_RANK,

        SPELL_LEARNED,
        SPELL_MEMORIZED,
        SPELL_EN_VERBATIM,
        SPELL_UNMEMORIZED, NEW_PERK, LEVEL_UP,


    }

    public class HqOperation {
        HQ_OPERATION operation;
        Object[] arg;

        public HqOperation(HQ_OPERATION operation, Object... arg) {
            this.operation = operation;
            this.arg = arg;
        }

        public HQ_OPERATION getOperation() {
            return operation;
        }

        public Object[] getArg() {
            return arg;
        }
    }
}
