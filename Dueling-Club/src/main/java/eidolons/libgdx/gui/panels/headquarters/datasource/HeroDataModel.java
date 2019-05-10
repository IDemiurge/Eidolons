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
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
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
        PICK_UP, DROP, UNEQUIP, UNEQUIP_QUICK_SLOT, EQUIP, EQUIP_QUICK_SLOT, UNEQUIP_JEWELRY,
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

        BUY, SELL, UNSTASH, STASH,



    }

    public class HeroOperation {
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