package main.entity.tools.bf.unit;

import com.graphbuilder.math.ExpressionParseException;
import main.client.cc.logic.spells.LibraryManager;
import main.client.cc.logic.spells.SpellUpgradeMaster;
import main.content.*;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.enums.entity.UnitEnums;
import main.content.enums.system.AiEnums.BEHAVIOR_MODE;
import main.content.mode.MODE;
import main.content.mode.ModeImpl;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.VariableManager;
import main.entity.item.*;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.hero.DC_Attributes;
import main.entity.obj.hero.DC_Masteries;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityMaster;
import main.entity.tools.bf.BfObjInitializer;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitInitializer extends BfObjInitializer<Unit> {

    public UnitInitializer(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    protected void initDefaults() {

    }

    @Override
    public UnitCalculator getCalculator() {
        return (UnitCalculator) super.getCalculator();
    }


    @Override
    public UnitChecker getChecker() {
        return (UnitChecker) super.getChecker();
    }

    @Override
    public UnitResetter getResetter() {
        return (UnitResetter) super.getResetter();
    }


    public void initMode() {
        String name = getProperty(G_PROPS.MODE);
        MODE mode = (new EnumMaster<STD_MODES>().retrieveEnumConst(STD_MODES.class, name));
        if (mode == null) {
            BEHAVIOR_MODE behavior = new EnumMaster<BEHAVIOR_MODE>().retrieveEnumConst(
             BEHAVIOR_MODE.class, name);
            if (behavior != null) {
                mode = new ModeImpl(behavior);
            }
        }
        if (mode == null) {
            mode = (STD_MODES.NORMAL);
        }

        getEntity().setMode(mode);
        LogMaster.log(LogMaster.CORE_DEBUG, getName() + " has mode: " + mode);

    }

    public void initHeroObjects() {
        if ((getChecker().isHero() || getChecker().checkPassive(UnitEnums.STANDARD_PASSIVES.FAVORED)) && getEntity().getDeity() != null) {
            addProperty(G_PROPS.PASSIVES, getEntity().getDeity().getProperty(G_PROPS.PASSIVES));
        }

        initItems(); // replace type names with ids for weapon/armor

        if (!initialized) {
            initClasses();
            initSkills();

            initAttributes();
            initMasteries();

        } else if (game.isSimulation()) {
            initClasses();
            initSkills();
        }
        initialized = true;
    }

    public void initActives() {
        // if (!isActivesReady()) {

        AbilityConstructor.constructActives(getEntity());
        // }
//        setActivesReady(true);
    }

    public void initSpells(boolean reset) {
        boolean initUpgrades = false;
        if (game.isSimulation()) {
            if (!ListMaster.isNotEmpty(
             getEntity().getSpells())) {
                initUpgrades = true;
            }
        }
        getEntity().setSpells(
         getGame().getManager().getSpellMaster().getSpells(getEntity(), reset));
        // TODO support spellbook changes!
        if (initUpgrades) {
            SpellUpgradeMaster.initSpellUpgrades(getEntity());
        }
    }


    public void initSpellbook() {
        LibraryManager.initSpellbook(getEntity());
        // init objects for all the known spells as well!
    }

    public void initClasses() {
        getEntity().setClasses(new DequeImpl<>());
        initFeatContainer(PROPS.CLASSES, DC_TYPE.CLASSES,
         getEntity().getClasses());
    }

    public void initSkills() {
        getEntity().setSkills(new DequeImpl<>());
        initFeatContainer(PROPS.SKILLS, DC_TYPE.SKILLS, getEntity().getSkills());
    }

    public void initAttributes() {
        getEntity().setAttrs(new DC_Attributes(getEntity()));

    }

    public void initMasteries() {
        getEntity().setMasteries(new DC_Masteries(getEntity()));

    }

    public void initInventory() {
        getEntity().setInventory(
         new DequeImpl<>(initContainedItems(PROPS.INVENTORY,
          getEntity().getInventory(), false)));
    }


    public DequeImpl<? extends DC_HeroItemObj> initContainedItems(PROPS prop,
                                                                  DequeImpl<? extends DC_HeroItemObj> list, boolean quick) {
        if (StringMaster.isEmpty(getProperty(prop))) {
            if (list == null) {
                return new DequeImpl<>();
            }
            if (list.isEmpty() || game.isSimulation()) {
                return new DequeImpl<>();
            }
        }
        if (list == null || (!game.isSimulation() && getEntity().isItemsInitialized())) {
            setProperty(prop, StringMaster.constructContainer(StringMaster.convertToIdList(list)));

        } else {
            List<String> idList = new LinkedList<>();
            Collection<DC_HeroItemObj> items = new LinkedList<>();
            for (String subString : StringMaster.openContainer(getProperty(prop))) {
                ObjType type = DataManager.getType(subString, DC_ContentManager.getTypeForProperty(prop));
//|| !StringMaster.isInteger(subString)
                DC_HeroItemObj item = null;
                if (game.isSimulation() ) {
                    item = (DC_HeroItemObj) getGame().getSimulationObj(getEntity(), type, prop);
                }
                if (item == null) {
                    if (type == null) {
                        item = (DC_HeroItemObj) game.getObjectById(StringMaster
                         .getInteger(subString));
                    } else {
                        item = ItemFactory.createItemObj(type, getEntity().getOriginalOwner(), getGame(), getRef(),
                         quick);
                    }
                    if (item != null) {
                        if (!game.isSimulation()) {
                            idList.add(item.getId() + "");
                        } else {
                            getGame().addSimulationObj(getEntity(), type, item, prop);
                        }
                    }
                }
                if (item == null) {
                    LogMaster.log(1, getName()
                     + " has null items in item container " + prop);
                } else {
                    items.add(item);
                }

            }
            list = new DequeImpl<>(items);
            if (!game.isSimulation())

            {
                setProperty(prop, StringMaster.constructContainer(idList));
            }
        }
        if (list == null) {
            return new DequeImpl<>();
        }
        return list;

    }

    public void initItems() {
        if (CoreEngine.isItemGenerationOff()) {
            main.system.auxiliary.log.LogMaster.log(1, "NO ITEMS! - Item Generation Off!");
            return;
        }
        try {
            initInventory();
        } catch (ExpressionParseException e) {
            LogMaster.log(1, "failed to parse for initQuickItems "
             + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(1, "failed to initInventory");
        }
        try {
            initJewelry();
        } catch (ExpressionParseException e) {
            LogMaster.log(1, "failed to parse for initQuickItems "
             + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(1, "failed to initJewelry");
        }
        try {
            initQuickItems();
        } catch (ExpressionParseException e) {
            LogMaster.log(1, "failed to parse for initQuickItems "
             + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            LogMaster.log(1, "failed to initQuickItems");
        }

        getEntity().setWeapon((DC_WeaponObj) initItem(getEntity().getMainWeapon(), G_PROPS.MAIN_HAND_ITEM, DC_TYPE.WEAPONS));
        if (getEntity().getNaturalWeapon(false) == null) {
            initNaturalWeapon(false);
        } else if (!getEntity().getNaturalWeapon(false).getType().getName().equalsIgnoreCase(
         getProperty(PROPS.NATURAL_WEAPON))) {
            initNaturalWeapon(false);
        }

        getEntity().setSecondWeapon((DC_WeaponObj) initItem(getEntity().getSecondWeapon(), G_PROPS.OFF_HAND_ITEM,
         DC_TYPE.WEAPONS));

        if (getEntity().getNaturalWeapon(true) == null) {
            initNaturalWeapon(true);
        } else if (!getEntity().getNaturalWeapon(true).getType().getName().equalsIgnoreCase(
         getProperty(PROPS.OFFHAND_NATURAL_WEAPON))) {
            initNaturalWeapon(true);
        }
        getEntity().setArmor((DC_ArmorObj) initItem(getEntity().getArmor(), G_PROPS.ARMOR_ITEM, DC_TYPE.ARMOR));

        getEntity().setItemsInitialized(true);
    }

    public void initNaturalWeapon(boolean offhand) {

        if (offhand) {
            if (StringMaster.isEmpty(getProperty(PROPS.OFFHAND_NATURAL_WEAPON))) {
                return;
            }
        }
        PROPS prop = (offhand) ? PROPS.OFFHAND_NATURAL_WEAPON : PROPS.NATURAL_WEAPON;
        // if (game.isSimulation())
        // return;

        ObjType weaponType = DataManager.getType(getProperty(prop), DC_TYPE.WEAPONS);
        if (weaponType == null) {
            return;
        }
        DC_WeaponObj weapon = new DC_WeaponObj(weaponType, getEntity().getOwner(), getGame(), getRef(), !offhand);
        getEntity().setNaturalWeapon(offhand, weapon);

    }

    // TODO reqs: save() ; modify() ; resetSkillRanks() for (s s : skills)
    public void initFeatContainer(PROPERTY PROP, DC_TYPE TYPE, DequeImpl<DC_FeatObj> list) {
        // TODO make it dynamic and clean!
        List<String> feats = StringMaster.openContainer(getProperty(PROP));
        for (String feat : feats) {
            DC_FeatObj featObj = null;
            int rank = 0;
            // or special separator!
            if (StringMaster.isInteger(StringMaster.cropParenthesises(VariableManager
             .getVarPart(feat)))) {
                rank = StringMaster.getInteger(StringMaster.cropParenthesises(VariableManager
                 .getVarPart(feat)));
                feat = VariableManager.removeVarPart(feat);
            }
            ObjType featType = DataManager.getType(feat, TYPE);
            if (game.isSimulation()) {
                featObj = (DC_FeatObj) getGame().getSimulationObj(getEntity(), featType, PROP);
            }
            if (featObj == null) {
                try {
                    featObj = new DC_FeatObj(featType, getEntity().getOriginalOwner(), getGame(), getRef());
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if (game.isSimulation()) {
                    getGame().addSimulationObj(getEntity(), featType, featObj, PROP);
                }

            }
            if (rank != 0) {
                featObj.setParam(PARAMS.RANK, rank);
            }
            list.add(featObj);
        }
    }

    public boolean checkItemChanged(DC_HeroItemObj item, G_PROPS prop, DC_TYPE TYPE) {
        if (game.isSimulation()) {
            if (CoreEngine.isArcaneVault())// TODO prevent from piling up?
            {
                if (item != null) {
                    game.getState().removeObject(item.getId());
                    return true;
                }
            }

        }
        //
        String value = getProperty(prop);
        if (DataManager.isTypeName(value, TYPE)) {
            return true; // means it's not in id-form yet
        }
        if (item == null) {
            return StringMaster.isEmpty(value);
        }
        return !item.getId().toString().equals(value);
    }

    public DC_HeroItemObj initItem(DC_HeroItemObj item, G_PROPS prop, DC_TYPE TYPE) {
        if (game.isSimulation() || !getEntity().isItemsInitialized()) {
            if (checkItemChanged(item, prop, TYPE)) {
                String property = getProperty(prop);
                if (StringMaster.isEmpty(property)) {
                    return null;
                }
                ObjType type = DataManager.getType(property, TYPE);

                if (type != null) {
                    if (game.isSimulation()) {
                        item = (DC_HeroItemObj) getGame().getSimulationObj(getEntity(), type, prop);
                    }
                    if (item == null) {

                        item = (prop != G_PROPS.ARMOR_ITEM) ? (new DC_WeaponObj(type,
                         getEntity().getOriginalOwner(), getGame(), getRef(),

                         prop == G_PROPS.MAIN_HAND_ITEM)) : (new DC_ArmorObj(type,
                         getEntity().getOriginalOwner(), getGame(), getRef()));
                        if (game.isSimulation()) {
                            getGame().addSimulationObj(getEntity(), type, item, prop);
                        }
                    }
                    if (!game.isSimulation()) {
                        setProperty(prop, item.getId() + "");
                    }
                } else if (!game.isSimulation()) {
                    int itemId = StringMaster.toInt(property);
                    if (itemId != -1) {
                        item = ((DC_HeroItemObj) game.getObjectById(itemId));
                    }

                }
            }
        }

        return item;
    }


    public void initJewelry() {
        DequeImpl<? extends DC_HeroItemObj> items = initContainedItems(PROPS.JEWELRY, getEntity().getJewelry(),
         false);
        if (items == getEntity().getJewelry()) {
            return;
        }

        getEntity().getJewelry().clear();
        for (DC_HeroItemObj e : items) {
            if (e instanceof DC_JewelryObj) {
                getEntity().getJewelry().add((DC_JewelryObj) e);
            }
        }

    }

    public void initQuickItems() {

        // setQuickItems(new DequeImpl<DC_QuickItemObj>(

        DequeImpl<? extends DC_HeroItemObj> items = initContainedItems(PROPS.QUICK_ITEMS,
         getEntity().getQuickItems(), true)
         // )) TODO
         ;
        if (items == getEntity().getQuickItems()) {
            return;
        }
        getEntity().setQuickItems(new DequeImpl<>());
        for (DC_HeroItemObj e : items) {
            getEntity().getQuickItems().add((DC_QuickItemObj) e);
        }

    }


    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    public void initIntegrityAlignments() {
        Map<PRINCIPLES, Integer> map = new RandomWizard<PRINCIPLES>().constructWeightMap(
         getProperty(G_PROPS.PRINCIPLES), PRINCIPLES.class);
        for (PRINCIPLES principle : map.keySet()) {
            Integer amount = map.get(principle);
            if (amount == 0) {
                continue;
            }
            PARAMETER alignmentParam = DC_ContentManager.getAlignmentForPrinciple(principle);
            getEntity().modifyParameter(alignmentParam, amount);

        }
    }

}
