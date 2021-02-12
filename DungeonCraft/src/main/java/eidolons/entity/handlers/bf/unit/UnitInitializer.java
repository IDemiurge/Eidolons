package eidolons.entity.handlers.bf.unit;

import com.graphbuilder.math.ExpressionParseException;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.SimCache;
import eidolons.entity.active.Spell;
import eidolons.entity.handlers.bf.BfObjInitializer;
import eidolons.entity.item.*;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.hero.DC_Attributes;
import eidolons.entity.obj.hero.DC_Masteries;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.core.game.SimulationGame;
import eidolons.game.module.dungeoncrawl.objects.ContainerMaster;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj.DUNGEON_OBJ_TYPE;
import eidolons.game.module.herocreator.logic.items.ItemGenerator;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import main.content.DC_TYPE;
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
import main.data.ability.construct.VariableManager;
import main.entity.handlers.EntityMaster;
import main.entity.type.ObjType;
import main.system.auxiliary.*;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static main.content.DC_TYPE.PERKS;

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
            initPerks();

            initAttributes();
            initMasteries();

        } else if (game.isSimulation()) {
            initClasses();
            initSkills();
        }
        initialized = true;
    }

    public void initActives() {
        master.getActionManager().resetActions(getEntity());
    }

    public void initSpells(boolean reset) {
        getEntity().setSpells(
                getGame().getManager().getSpellMaster().getSpells(getEntity(), reset));
    }

    public void initSpellbook() {
        SpellMaster.initSpellbook(getEntity());
        List<Spell> spellbook =
                new ArrayList<>(getEntity().getSpells());
        spellbook.addAll(getGame().getManager().getSpellMaster().
                initSpellpool(getEntity(), PROPS.SPELLBOOK));
        getEntity().setSpellbook(spellbook);
        // init objects for all the known spells as well!
    }

    public void initPerks() {
        getEntity().setPerks(new DequeImpl<>());
        initFeatContainer(PROPS.PERKS, PERKS,
                getEntity().getPerks());
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
        if (!getEntity().isMine())
            if (ContainerMaster.isGenerateItemsForUnits())
                if (ContainerMaster.isPregenerateItems(getEntity()))
                    if (!ListMaster.isNotEmpty(getEntity().getInventory())) {
                        ContainerMaster master = (ContainerMaster) getGame().getDungeonMaster().getDungeonObjMaster(DUNGEON_OBJ_TYPE.CONTAINER);
                        try {
                            master.initContents(getEntity());
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                    }

        getEntity().setInventory(
                new DequeImpl<>(initContainedItems(PROPS.INVENTORY,
                        getEntity().getInventory(), false)));
    }


    public void initItems() {
        if (Flags.isItemGenerationOff()) {
            main.system.auxiliary.log.LogMaster.log(1, "NO ITEMS! - Item Generation Off!");
            return;
        }
        try {
            initInventory();
        } catch (ExpressionParseException e) {
            LogMaster.log(1, "failed to parse for initQuickItems "
                    + e.getMessage());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "failed to initInventory");
        }
        if (ItemGenerator.isJewelryOn())
            try {
                initJewelry();
            } catch (ExpressionParseException e) {
                LogMaster.log(1, "failed to parse for initQuickItems "
                        + e.getMessage());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                LogMaster.log(1, "failed to initJewelry");
            }
        try {
            initQuickItems();
        } catch (ExpressionParseException e) {
            LogMaster.log(1, "failed to parse for initQuickItems "
                    + e.getMessage());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            LogMaster.log(1, "failed to initQuickItems");
        }

        getEntity().setWeapon((DC_WeaponObj) initItem(getEntity().getMainWeapon(), G_PROPS.MAIN_HAND_ITEM, DC_TYPE.WEAPONS));
        if (getEntity().getNaturalWeapon(false) == null) {
            initNaturalWeapon(false);
        } else if (!getEntity().getNaturalWeapon(false).getType().getName().equalsIgnoreCase(
                getProperty(PROPS.NATURAL_WEAPON))) {
            initNaturalWeapon(false);
        }

        getEntity().setSecondWeapon((DC_WeaponObj) initItem(getEntity().getOffhandWeapon(), G_PROPS.OFF_HAND_ITEM,
                DC_TYPE.WEAPONS));

        if (getEntity().getNaturalWeapon(true) == null) {
            initNaturalWeapon(true);
        } else if (!getEntity().getNaturalWeapon(true).getType().getName().equalsIgnoreCase(
                getProperty(PROPS.OFFHAND_NATURAL_WEAPON))) {
            initNaturalWeapon(true);
        }

        getEntity().setReserveMainWeapon((DC_WeaponObj) initItem(getEntity().getReserveMainWeapon(),
                G_PROPS.RESERVE_MAIN_HAND_ITEM, DC_TYPE.WEAPONS));
        getEntity().setReserveOffhandWeapon((DC_WeaponObj) initItem(getEntity().getReserveOffhandWeapon(),
                G_PROPS.RESERVE_OFF_HAND_ITEM, DC_TYPE.WEAPONS));


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
    public void initFeatContainer(PROPERTY PROP, DC_TYPE TYPE,
                                  DequeImpl<? extends DC_FeatObj> list) {
        // TODO make it dynamic and clean!
        List<String> feats = ContainerUtils.openContainer(getProperty(PROP));
        for (String feat : feats) {
            DC_FeatObj featObj = null;
            int rank = 0;
            // or special separator!
            if (NumberUtils.isInteger(StringMaster.cropParenthesises(VariableManager
                    .getVarPart(feat)))) {
                rank = NumberUtils.getIntParse(StringMaster.cropParenthesises(VariableManager
                        .getVarPart(feat)));
                feat = VariableManager.removeVarPart(feat);
            }
            ObjType featType = DataManager.getType(feat, TYPE);
            if (featType == null) {
                continue;
            }
            if (featObj == null) {
                featObj = createFeatObj(featType);
            }
            if (rank != 0) {
                featObj.setParam(PARAMS.RANK, rank);
            }
            list.addCast(featObj);
        }
    }

    private DC_FeatObj createFeatObj(ObjType featType) {
        return SkillMaster.createFeatObj(featType, getRef());
    }

    public boolean checkItemChanged(DC_HeroItemObj item, G_PROPS prop, DC_TYPE TYPE) {
        if (game.isSimulation()) {
            if (CoreEngine.isArcaneVault())// TODO prevent from piling up?
            {
                if (item != null) {
                    game.getState().manager.removeObject(item.getId(), item.getOBJ_TYPE_ENUM());
                    return true;
                }
            } else {
                return true;
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
        if (getEntity().isLoaded()) {
            return getLoadedItem(prop);
        }
        if (game.isSimulation() || !getEntity().isItemsInitialized()) {
            if (checkItemChanged(item, prop, TYPE)) {
                String property = getProperty(prop);
                if (game.isSimulation()) {
                    if (initialized)
                        if (item == null)
                            return null;


                    if (game.isSimulation()) {
                        if (item == null || !item.isSimulation()) {
                            if (item == null) {
                                item = (DC_HeroItemObj) ((SimulationGame) game).getRealGame().getObjectById(StringMaster.toInt(property));
                            }
                            if (item != null) {
                                DC_HeroItemObj simItem = createItem(prop, item.getType());
                                 SimCache.getInstance().addSim(item, simItem);
                                Integer durability = item.getIntParam(PARAMS.C_DURABILITY);
                                simItem.setParam(PARAMS.C_DURABILITY, durability);
                                item = simItem;
                            }
                        }
                    }
                } else if (initialized) {
                    int itemId = StringMaster.toInt(property);
                    if (itemId != -1) {
                        item = ((DC_HeroItemObj) game.getObjectById(itemId));
                    }
                } else {
                    if (StringMaster.isEmpty(property)) {
                        return null;
                    }
                    ObjType type = DataManager.getType(property, TYPE);
                    if (type != null) {
                        item = createItem(prop, type);
                    }
                    setProperty(prop, item.getId() + "");
                }


            }
        }

        return item;
    }

    private DC_HeroItemObj getLoadedItem(G_PROPS prop) {
        //TODO macro Review
        // return Loader.getLoadedItem(getEntity(), prop);
        return null;
    }

    private DC_HeroItemObj createItem(PROPERTY prop, ObjType type) {
        return (prop != G_PROPS.ARMOR_ITEM) ? (new DC_WeaponObj(type,
                getEntity().getOriginalOwner(), getGame(), getRef(),

                prop == G_PROPS.MAIN_HAND_ITEM)) : (new DC_ArmorObj(type,
                getEntity().getOriginalOwner(), getGame(), getRef()));
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


    public void initIntegrityAlignments() {
        if (!RuleKeeper.isRuleOn(RuleEnums.RULE.INTEGRITY)) {
            return ;
        }
        Map<PRINCIPLES, Integer> map = new RandomWizard<PRINCIPLES>().constructWeightMap(
                getProperty(G_PROPS.PRINCIPLES), PRINCIPLES.class);
        for (PRINCIPLES principle : map.keySet()) {
            Integer amount = map.get(principle);
            if (amount == 0) {
                continue;
            }
            PARAMETER alignmentParam = DC_ContentValsManager.getAlignmentForPrinciple(principle);
            getEntity().modifyParameter(alignmentParam, amount);

        }
    }

    //    public boolean isItemsInitialized() {
    //        return getEntity().isItemsInitialized();
    //    }
}
