package main.entity.obj;

import com.graphbuilder.math.ExpressionParseException;
import main.ability.AbilityObj;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.client.cc.CharacterCreator;
import main.client.cc.logic.party.PartyObj;
import main.client.cc.logic.spells.LibraryManager;
import main.client.cc.logic.spells.SpellUpgradeMaster;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.*;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.CONTENT_CONSTS2.SPELL_UPGRADE;
import main.content.*;
import main.content.DC_ContentManager.ATTRIBUTE;
import main.content.enums.STD_MODES;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ItemActiveObj;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.DC_AttackMaster;
import main.game.battlefield.ResistMaster;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.macro.MacroManager;
import main.game.logic.macro.entity.MacroActionManager.MACRO_MODES;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.rules.DC_ActionManager;
import main.rules.mechanics.EngagedRule;
import main.rules.rpg.IntegrityRule;
import main.system.DC_Constants;
import main.system.DC_Formulas;
import main.system.ai.tools.ParamAnalyzer;
import main.system.auxiliary.*;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.debug.GameLauncher;

import java.util.*;

public class DC_HeroObj extends DC_UnitObj {
    private DC_WeaponObj weapon;
    private DC_WeaponObj secondWeapon;
    // private Footwear boots;
    // private Helmet helmet;
    // private Gloves gloves;
    // private Cloak cloak;
    private DC_ArmorObj armor;
    private DequeImpl<DC_FeatObj> skills;
    private DequeImpl<DC_FeatObj> classes;
    private DequeImpl<DC_QuickItemObj> quickItems;
    private DequeImpl<DC_HeroItemObj> jewelry;
    private DequeImpl<DC_HeroItemObj> inventory;

    private DC_Masteries masteries;
    private DC_Attributes attrs;
    private boolean dynamicValuesReady = false;

    private List<DC_SpellObj> spells;
    private boolean initialized;
    private List<DC_SpellObj> spellbook;
    private boolean itemsInitialized;
    private boolean aiControlled;
    private MACRO_MODES macroMode;
    private GENDER gender;
    private boolean hidden;
    private Dungeon dungeon;
    private DC_HeroObj engagementTarget;
    private boolean mainHero;
    private boolean leader;
    private FLIP flip;
    private ObjType backgroundType;
    private Map<DC_ActiveObj, String> actionModeMap;
    private boolean animated;

    public DC_HeroObj(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        setDungeon(getGame().getDungeonMaster().getDungeon());
        if (getGame().getDungeonMaster().getZ() != null)
            setZ(getGame().getDungeonMaster().getZ());
        else
            setZ(0);

        // getGame().getTestMaster().getTestSpells(); TODO add!
    }

    public DC_HeroObj(DC_HeroObj hero) {
        this(new ObjType(hero.getType(), true), hero.getX(), hero.getY(), hero.getOriginalOwner(),
                hero.getGame(), hero.getRef().getCopy());
        // transfer all buffs and other dynamic stuff?
    }

    public DC_HeroObj(ObjType type) {
        this(type, DC_Game.game);
    }

    public DC_HeroObj(ObjType type, DC_Game game) {
        this(type, 0, 0, DC_Player.NEUTRAL, game, new Ref(game));
    }

    public void addDynamicValues() {
        super.addDynamicValues();
        if (MacroManager.isMacroGame()) {
            // macro params? or maybe just out of 100
        }
    }

    @Override
    public void init() {
        try {
            super.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            initDeity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            initEmblem();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            initIntegrityAlignments();
        } catch (Exception e) {
            e.printStackTrace();
        }

        WaitMaster.receiveInput(WAIT_OPERATIONS.UNIT_OBJ_INIT, true);
    }

    private void initIntegrityAlignments() {
        Map<PRINCIPLES, Integer> map = new RandomWizard<PRINCIPLES>().constructWeightMap(
                getProperty(G_PROPS.PRINCIPLES), PRINCIPLES.class);
        for (PRINCIPLES principle : map.keySet()) {
            Integer amount = map.get(principle);
            if (amount == 0)
                continue;
            PARAMETER alignmentParam = DC_ContentManager.getAlignmentForPrinciple(principle);
            modifyParameter(alignmentParam, amount);

        }
    }

    public void saveRanks(boolean skills) {
        saveRanks(skills ? getSkills() : getClasses(), skills ? PROPS.SKILLS : PROPS.CLASSES);
    }

    public void saveRanks(DequeImpl<DC_FeatObj> container, PROPERTY property) {
        String value = "";
        for (DC_FeatObj featObj : container) {
            value += featObj.getName();
            if (featObj.getIntParam(PARAMS.RANK) > 0)
                value += StringMaster.wrapInParenthesis(featObj.getParam(PARAMS.RANK));
            value += ";";
        }
        setProperty(property, value, true);
    }

    public boolean incrementFeatRank(boolean skill, ObjType type) {
        DC_FeatObj featObj = getFeat(skill, type);
        return incrementFeatRank(skill, featObj);
    }

    public boolean incrementFeatRank(boolean skill, DC_FeatObj featObj) {
        if (featObj.getIntParam(PARAMS.RANK) == featObj.getIntParam(PARAMS.RANK_MAX))
            return false;
        featObj.setParam(PARAMS.RANK, featObj.getIntParam(PARAMS.RANK) + 1);
        return true;
    }

    public void setFeatRank(boolean skill, int rank, ObjType type) {
        DC_FeatObj featObj = getFeat(skill, type);
        featObj.setParam(PARAMS.RANK, rank);
        // reset
    }

    public DC_FeatObj getFeat(ObjType type) {
        return getFeat(type.getOBJ_TYPE_ENUM() == OBJ_TYPES.SKILLS, type);
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        if (game.isSimulation())
            return (DC_FeatObj) getGame().getSimulationObj(this, type,
                    skill ? PROPS.SKILLS : PROPS.CLASSES);
        return null;// TODO
    }

    public void resetRanks(DequeImpl<DC_FeatObj> container, PROPERTY property) {
        List<DC_FeatObj> list = new LinkedList<>(container);
        for (String feat : StringMaster.openContainer(getProperty(property))) {
            Integer rank = StringMaster.getInteger(VariableManager.getVarPart(feat));
            if (rank == 0)
                continue;
            feat = (VariableManager.removeVarPart(feat));
            for (DC_FeatObj featObj : container) {
                if (!featObj.getName().equals(feat))
                    continue;
                featObj.setParam(PARAMS.RANK, rank);
                list.remove(featObj);
            }
        }
        for (DC_FeatObj featObj : list) {
            featObj.setParam(PARAMS.RANK, 0);
        }
    }

    // TODO reqs: save() ; modify() ; resetSkillRanks() for (s s : skills)
    private void initFeatContainer(PROPERTY PROP, OBJ_TYPES TYPE, DequeImpl<DC_FeatObj> list) {
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
            if (game.isSimulation())
                featObj = (DC_FeatObj) getGame().getSimulationObj(this, featType, PROP);
            if (featObj == null) {
                featObj = new DC_FeatObj(featType, getOriginalOwner(), getGame(), ref);
                if (game.isSimulation())
                    getGame().addSimulationObj(this, featType, featObj, PROP);

            }
            if (rank != 0) {
                featObj.setParam(PARAMS.RANK, rank);
            }
            list.add(featObj);
        }
    }

    public void initClasses() {
        this.setClasses(new DequeImpl<DC_FeatObj>());
        initFeatContainer(PROPS.CLASSES, OBJ_TYPES.CLASSES, getClasses());
    }

    public void initSkills() {
        this.setSkills(new DequeImpl<DC_FeatObj>());
        initFeatContainer(PROPS.SKILLS, OBJ_TYPES.SKILLS, getSkills());
    }

    private void initAttributes() {
        this.setAttrs(new DC_Attributes(this));

    }

    private void initMasteries() {
        this.setMasteries(new DC_Masteries(this));

    }

    private boolean checkItemChanged(DC_HeroItemObj item, G_PROPS prop, OBJ_TYPES TYPE) {
        if (game.isSimulation()) {
            if (CoreEngine.isArcaneVault())// TODO prevent from piling up?
                if (item != null) {
                    game.getState().removeObject(item.getId());
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

    private DC_HeroItemObj initItem(DC_HeroItemObj item, G_PROPS prop, OBJ_TYPES TYPE) {
        if (game.isSimulation() || !isItemsInitialized())
            if (checkItemChanged(item, prop, TYPE)) {
                String property = getProperty(prop);
                if (StringMaster.isEmpty(property))
                    return null;
                ObjType type = DataManager.getType(property, TYPE);

                if (type != null) {
                    if (game.isSimulation())
                        item = (DC_HeroItemObj) getGame().getSimulationObj(this, type, prop);
                    if (item == null) {

                        item = (prop != G_PROPS.ARMOR_ITEM) ? (new DC_WeaponObj(type,
                                getOriginalOwner(), getGame(), ref,

                                prop == G_PROPS.MAIN_HAND_ITEM)) : (new DC_ArmorObj(type,
                                getOriginalOwner(), getGame(), ref));
                        if (game.isSimulation())
                            getGame().addSimulationObj(this, type, item, prop);
                    }
                    if (!game.isSimulation())
                        setProperty(prop, item.getId() + "");
                } else if (!game.isSimulation()) {
                    int itemId = StringMaster.toInt(property);
                    if (itemId != -1)
                        item = ((DC_HeroItemObj) game.getObjectById(itemId));

                }
            }

        return item;
    }

    private void initItems() {
        try {
            initInventory();
        } catch (ExpressionParseException e) {
            main.system.auxiliary.LogMaster.log(1, "failed to parse for initQuickItems "
                    + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            main.system.auxiliary.LogMaster.log(1, "failed to initInventory");
        }
        try {
            initJewelry();
        } catch (ExpressionParseException e) {
            main.system.auxiliary.LogMaster.log(1, "failed to parse for initQuickItems "
                    + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            main.system.auxiliary.LogMaster.log(1, "failed to initJewelry");
        }
        try {
            initQuickItems();
        } catch (ExpressionParseException e) {
            main.system.auxiliary.LogMaster.log(1, "failed to parse for initQuickItems "
                    + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            main.system.auxiliary.LogMaster.log(1, "failed to initQuickItems");
        }

        setWeapon((DC_WeaponObj) initItem(weapon, G_PROPS.MAIN_HAND_ITEM, OBJ_TYPES.WEAPONS));
        if (getNaturalWeapon(false) == null)
            initNaturalWeapon(false);
        else if (!getNaturalWeapon(false).getType().getName().equalsIgnoreCase(
                getProperty(PROPS.NATURAL_WEAPON)))
            initNaturalWeapon(false);

        setSecondWeapon((DC_WeaponObj) initItem(secondWeapon, G_PROPS.OFF_HAND_ITEM,
                OBJ_TYPES.WEAPONS));

        if (getNaturalWeapon(true) == null)
            initNaturalWeapon(true);
        else if (!getNaturalWeapon(true).getType().getName().equalsIgnoreCase(
                getProperty(PROPS.OFFHAND_NATURAL_WEAPON)))
            initNaturalWeapon(true);
        setArmor((DC_ArmorObj) initItem(armor, G_PROPS.ARMOR_ITEM, OBJ_TYPES.ARMOR));

        setItemsInitialized(true);
    }

    private void initNaturalWeapon(boolean offhand) {
        if (getWeapon(offhand) != null)
            return;
        if (offhand) {
            if (StringMaster.isEmpty(getProperty(PROPS.OFFHAND_NATURAL_WEAPON)))
                return;
        }
        PROPS prop = (offhand) ? PROPS.OFFHAND_NATURAL_WEAPON : PROPS.NATURAL_WEAPON;
        // if (game.isSimulation())
        // return;

        ObjType weaponType = DataManager.getType(getProperty(prop), OBJ_TYPES.WEAPONS);
        if (weaponType == null) {
            return;
        }
        DC_WeaponObj weapon = new DC_WeaponObj(weaponType, owner, getGame(), ref, !offhand);
        setNaturalWeapon(offhand, weapon);

    }

    private void setNaturalWeapon(boolean offhand, DC_WeaponObj weapon) {
        if (offhand)
            offhandNaturalWeapon = weapon;
        else
            naturalWeapon = weapon;
    }

    public DC_WeaponObj getNaturalWeapon(boolean offhand) {
        DC_WeaponObj weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        if (weaponObj == null)
            initNaturalWeapon(offhand);
        weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        return weaponObj;
    }

    private void initJewelry() {
        DequeImpl<? extends DC_HeroItemObj> items = initContainedItems(PROPS.JEWELRY, getJewelry(),
                false);
        if (items == getJewelry())
            return;

        getJewelry().clear();
        for (DC_HeroItemObj e : items) {
            getJewelry().add(e);
        }

    }

    private void initQuickItems() {

        // setQuickItems(new DequeImpl<DC_QuickItemObj>(

        DequeImpl<? extends DC_HeroItemObj> items = initContainedItems(PROPS.QUICK_ITEMS,
                getQuickItems(), true)
                // )) TODO
                ;
        if (items == getQuickItems())
            return;
        setQuickItems(new DequeImpl<DC_QuickItemObj>());
        for (DC_HeroItemObj e : items) {
            getQuickItems().add((DC_QuickItemObj) e);
        }

    }

    public void resetQuickSlotsNumber() {
        int size = 0;
        if (getQuickItems() != null)
            size = getQuickItems().size();
        int slotsRemaining = getIntParam(PARAMS.QUICK_SLOTS) - size;
        setParam(PARAMS.C_QUICK_SLOTS, slotsRemaining);
    }

    public void removeJewelryItem(DC_HeroItemObj itemObj) {
        getJewelry().remove(itemObj);
        if (getJewelry().isEmpty())
            setJewelry(null);
    }

    public void addQuickItem(DC_QuickItemObj itemObj) {
        getQuickItems().add(itemObj);
        itemObj.setRef(ref);
        resetQuickSlotsNumber();
    }

    public void removeQuickItem(DC_QuickItemObj itemObj) {
        if (getQuickItems().remove(itemObj))
            resetQuickSlotsNumber();
        // if (game.isArcade()
        if (CharacterCreator.isArcadeMode()) {
            type.removeProperty(PROPS.QUICK_ITEMS, itemObj.getName(), true);
            removeProperty(PROPS.QUICK_ITEMS, "" + itemObj.getId(), true);
            // setProperty(PROPS.QUICK_ITEMS, value, true);
        }
        if (getQuickItems().isEmpty())
            setQuickItems(null);
    }

    private void initInventory() {
        setInventory(new DequeImpl<>(initContainedItems(PROPS.INVENTORY, getInventory(), false)));
    }

    public void resetObjectContainers(boolean fromValues) {
        if (fromValues) {
            setItemsInitialized(false);
        }
    }

    private DequeImpl<? extends DC_HeroItemObj> initContainedItems(PROPS prop,
                                                                   DequeImpl<? extends DC_HeroItemObj> list, boolean quick) {
        if (StringMaster.isEmpty(getProperty(prop))) {
            if (list == null)
                return new DequeImpl<>();
            if (list.isEmpty() || game.isSimulation())
                return new DequeImpl<>();
        }
        if (list == null || (!game.isSimulation() && isItemsInitialized())) {
            setProperty(prop, StringMaster.constructContainer(StringMaster.convertToIdList(list)));

        } else {
            List<String> idList = new LinkedList<>();
            Collection<DC_HeroItemObj> items = new LinkedList<DC_HeroItemObj>();
            for (String subString : StringMaster.openContainer(getProperty(prop))) {
                ObjType type = DataManager.getType(subString, C_OBJ_TYPE.ITEMS);

                DC_HeroItemObj item = null;
                if (game.isSimulation())
                    item = (DC_HeroItemObj) getGame().getSimulationObj(this, type, prop);
                if (item == null) {
                    if (type == null)
                        item = (DC_HeroItemObj) game.getObjectById(StringMaster
                                .getInteger(subString));
                    else
                        item = ItemFactory.createItemObj(type, getOriginalOwner(), getGame(), ref,
                                quick);
                    if (item != null)
                        if (!game.isSimulation())
                            idList.add(item.getId() + "");
                        else {
                            getGame().addSimulationObj(this, type, item, prop);
                        }
                }
                if (item == null) {
                    main.system.auxiliary.LogMaster.log(1, getName()
                            + " has null items in item container " + prop);
                } else
                    items.add(item);

            }
            list = new DequeImpl<>(items);
            if (!game.isSimulation())

                setProperty(prop, StringMaster.constructContainer(idList));
        }
        if (list == null)
            return new DequeImpl<>();
        return list;

    }

    public String getDynamicInfo() {
        String info = "";
        for (VALUE V : ValuePages.UNIT_DYNAMIC_PARAMETERS) {
            info += V.getName() + " = " + getValue(V);
        }
        return info;
    }

    public String getParamInfo() {
        String info = "";
        for (VALUE V : ValuePages.UNIT_PARAMETERS) {
            info += V.getName() + " = " + getValue(V);
        }
        return info;
    }

    @Override
    public void toBase() {
        this.mode = STD_MODES.NORMAL;
        // Chronos.mark(toString() + "to base (values)");
        super.toBase();
        // Chronos.logTimeElapsedForMark(toString() + "to base (values)");

        // Chronos.mark(toString() + "to base (init objects)");
        if (!GameLauncher.getInstance().SUPER_FAST_MODE)
        initHeroObjects();
        // Chronos.logTimeElapsedForMark(toString() + "to base (init objects)");
        // if (mainHero)
        if (!CoreEngine.isArcaneVault())
            if (game.isSimulation()) {
                resetObjects();
                resetQuickSlotsNumber();
                String value = "";
                for (DC_SpellObj s : getSpells()) {
                    if (!s.getProperty(PROPS.SPELL_UPGRADES).isEmpty())
                        value += s.getName()
                                + StringMaster.wrapInParenthesis(s
                                .getProperty(PROPS.SPELL_UPGRADES).replace(";", ",")) + ";";
                }
                if (!value.isEmpty())
                    setProperty(PROPS.SPELL_UPGRADES, value, true);
            }

        if (!isBfObj())
            if (!isNeutral())
                if (game.isDummyMode()) {
                    if (getGame().isDummyPlus()) {
                        resetParam(PARAMS.C_N_OF_COUNTERS);
                        resetParam(PARAMS.C_STAMINA);
                        resetParam(PARAMS.C_FOCUS);
                        resetParam(PARAMS.C_ESSENCE);
                        resetParam(PARAMS.C_FOCUS);
                    }
                    if (!getOwner().isMe()) {
                        setParam(PARAMS.INITIATIVE_MODIFIER, 1);
                    }
                    if (equals(getOwner().getHeroObj()))
                    addPassive(STANDARD_PASSIVES.INDESTRUCTIBLE);
                }
    }

    public void addPassive(STANDARD_PASSIVES passive) {
        addPassive(passive.getName());
    }

    @Override
    public void removed() {
        for (Obj obj : getSkills()) {
            getGame().remove(obj);
        }
    }

    public boolean checkDualWielding() {
        if (getSecondWeapon() == null || getMainWeapon() == null)
            return false;
        if (getMainWeapon().isRanged() || getMainWeapon().isMagical())
            return false;
        if (getSecondWeapon().isRanged() || getSecondWeapon().isMagical())
            return false;
        return (getSecondWeapon().isWeapon());

    }

    public void addAction(String string) {
        ActiveObj action = game.getActionManager().getAction(string, this);
        if (action != null)
            actives.add(action);
    }

    private void applyBackground() {
        if (backgroundType == null) {
            backgroundType = DataManager.getType(getProperty(G_PROPS.BACKGROUND_TYPE),
                    getOBJ_TYPE_ENUM());
            if (backgroundType == null)
                backgroundType = DataManager.getType(getProperty(G_PROPS.BASE_TYPE),
                        getOBJ_TYPE_ENUM());
        }
        if (backgroundType == null)
            return;
        for (PARAMETER param : DC_ContentManager.getBackgroundDynamicParams()) {
            Integer amount = backgroundType.getIntParam(param);
            modifyParameter(param, amount);
        }

    }

    @Override
    public void resetObjects() {
        Chronos.mark(toString() + " OBJECTS APPLY");

        applyBackground();
        resetAttributes();
        resetMasteryScores();
        if (getSkills() != null) {

            resetRanks(getSkills(), PROPS.SKILLS);
            for (DC_FeatObj feat : getSkills())
                feat.apply();
        }
        if (getClasses() != null) {
            resetRanks(getClasses(), PROPS.CLASSES);
            for (DC_FeatObj feat : getClasses())
                feat.apply();

        }
        if (weapon != null)
            weapon.apply();
        else if (naturalWeapon != null)
            naturalWeapon.apply();

        if (secondWeapon != null) {
            secondWeapon.apply();
            // if (checkDualWielding())
            // DC_Formulas.MAIN_HAND_DUAL_ATTACK_MOD
        } else if (offhandNaturalWeapon != null)
            offhandNaturalWeapon.apply();

        if (armor != null) {
            armor.apply();
        }
        resetQuickSlotsNumber();
        for (DC_HeroItemObj item : getQuickItems()) {
            item.apply();
        }
        for (DC_HeroItemObj item : getJewelry()) {
            item.apply();
        }
        // Chronos.logTimeElapsedForMark(toString() + " OBJECTS APPLY");

        Chronos.mark(toString() + " activate PASSIVES");
        initSpells(game.isSimulation());
        activatePassives();

        // Chronos.logTimeElapsedForMark(toString() + " activate PASSIVES");

        setDirty(false);
        if (game.isSimulation() || type.isModel()) {
            // initSpellbook(); //in afterEffect()
            return;
        }
        Chronos.mark(toString() + " init ACTIVES");
        initActives();
        // Chronos.logTimeElapsedForMark(toString() + " init ACTIVES");

        resetFacing();

    }

    public void initSpells(boolean reset) {
        boolean initUpgrades = false;
        if (game.isSimulation())
            if (!ListMaster.isNotEmpty(spells)) {
                initUpgrades = true;
            }
        spells = getGame().getManager().getSpells(this, reset);
        // TODO support spellbook changes!
        if (initUpgrades)
            SpellUpgradeMaster.initSpellUpgrades(this);
    }

    public Integer calculateDamage(boolean offhand) {
        int dmg = DC_AttackMaster.getUnitAttackDamage(this, offhand);
        Integer mod = 0;
        mod = getIntParam((offhand) ? PARAMS.OFFHAND_DAMAGE_MOD : PARAMS.DAMAGE_MOD);
        if (mod != 0)
            dmg = dmg * mod / 100;
        PARAMS minDamage = (offhand) ? PARAMS.OFF_HAND_MIN_DAMAGE : PARAMS.MIN_DAMAGE;
        setParam(minDamage, dmg);
        PARAMS damage = (offhand) ? PARAMS.OFF_HAND_DAMAGE : PARAMS.DAMAGE;
        DC_WeaponObj weapon = getWeapon(offhand);
        if (weapon == null)
            weapon = getNaturalWeapon(offhand);
        Integer dieSize = (weapon == null) ? getIntParam(PARAMS.DIE_SIZE) : weapon
                .getIntParam(PARAMS.DIE_SIZE);

        if (mod != 0)
            dieSize = dieSize * mod / 100;

        setParam(damage, MathMaster.getAverage(dmg, dmg + dieSize));
        PARAMS maxDamage = (offhand) ? PARAMS.OFF_HAND_MAX_DAMAGE : PARAMS.MAX_DAMAGE;
        setParam(maxDamage, dmg + dieSize);
        return MathMaster.getAverage(dmg, dmg + dieSize);
    }

    public void initSpellbook() {
        LibraryManager.initSpellbook(this);
        // init objects for all the known spells as well!
    }

    public int calculateRemainingMemory() {
        int memory = getIntParam(PARAMS.MEMORIZATION_CAP) - calculateMemorizationPool();
        setParam(PARAMS.MEMORY_REMAINING, memory);
        return memory;
    }

    public int calculateMemorizationPool() {
        int memory = 0;
        for (ObjType type : DataManager.toTypeList(StringMaster
                .openContainer(getProperty(PROPS.MEMORIZED_SPELLS)), OBJ_TYPES.SPELLS)) {
            memory += type.getIntParam(PARAMS.SPELL_DIFFICULTY);
        }
        return memory;
    }

    // @Deprecated
    public int calculatePower() {
        if (isBfObj())
            return 0; // TODO into new class!

        int power = DC_MathManager.getUnitPower(this);
        if (!isHero()) {
            if (power != 0)
                return 0;
        }
        setParam(PARAMS.POWER, power);
        // GetParam(PARAMS.UNIT_LEVEL, power);
        return power;
    }

    public int calculateWeight() {
        int weight = initCarryingWeight();
        setParam(PARAMS.C_CARRYING_WEIGHT, weight);
        int result = getIntParam(PARAMS.CARRYING_CAPACITY) - weight;
        weight += getIntParam(PARAMS.WEIGHT);
        setParam(PARAMS.TOTAL_WEIGHT, weight);
        return result;

    }

    // Java 8 collections methods?
    public int initCarryingWeight() {
        int weight = 0;
        if (getMainWeapon() != null)
            weight += getMainWeapon().getIntParam(PARAMS.WEIGHT);
        if (getSecondWeapon() != null)
            weight += getSecondWeapon().getIntParam(PARAMS.WEIGHT);
        if (getArmor() != null)
            weight += getArmor().getIntParam(PARAMS.WEIGHT);

        if (game.isSimulation()) {
            for (ObjType type : DataManager.toTypeList(StringMaster
                    .openContainer(getProperty(PROPS.INVENTORY)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
            for (ObjType type : DataManager.toTypeList(StringMaster
                    .openContainer(getProperty(PROPS.QUICK_ITEMS)), C_OBJ_TYPE.ITEMS)) {
                weight += type.getIntParam(PARAMS.WEIGHT);
            }
        } else {

            for (DC_HeroItemObj item : getInventory()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
            for (DC_HeroItemObj item : getQuickItems()) {
                weight += item.getIntParam(PARAMS.WEIGHT);
            }
        }
        return weight;

    }

    private void resetMasteryScores() {
        for (PARAMS mastery : DC_ContentManager.getMasteryParams()) {
            PARAMETER score = ContentManager.getMasteryScore(mastery);
            type.setParam(score, getIntParam(mastery));
            setParam(score, getIntParam(mastery));
        }
    }

    private void resetAttributes() {
        for (ATTRIBUTE attr : DC_ContentManager.getAttributeEnums()) {
            resetAttr(attr);
        }

    }

    private void resetDefaultAttr(ATTRIBUTE attr) {
        type.setParam(DC_ContentManager.getDefaultAttr(attr.getParameter()),
                getIntParam(DC_ContentManager.getBaseAttr(attr.getParameter())));
    }

    /**
     * only from Arcane Vault!
     */
    public void resetDefaultAttrs() {
        for (ATTRIBUTE attr : DC_ContentManager.getAttributeEnums()) {
            resetDefaultAttr(attr);
        }
    }

    private void resetAttr(ATTRIBUTE attr) {
        PARAMETER baseAttr = DC_ContentManager.getBaseAttr(attr);
        type.setParam(attr.getParameter(), getIntParam(baseAttr));
        setParam(attr.getParameter(), getIntParam(baseAttr));

    }

    private void initHeroObjects() {
        if ((isHero() || checkPassive(STANDARD_PASSIVES.FAVORED)) && getDeity() != null)
            addProperty(G_PROPS.PASSIVES, getDeity().getProperty(G_PROPS.PASSIVES));

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

    private void resetSpells() {
        if (spells != null)
            for (DC_SpellObj spell : spells) {
                spell.toBase();
            }
    }

    protected void applyMods() {
        // if (getMainWeapon() != null) //
        // getMainWeapon().applyMods();
        // if (getArmor() != null)
        // getArmor().applyMods();
        // if (getSecondWeapon() != null)
        // getSecondWeapon().applyMods();

        if (engagementTarget != null)
            EngagedRule.applyMods(this);
        else
            removeStatus(STATUS.ENGAGED);
        int mod = getIntParam(PARAMS.ATTACK_MOD);
        multiplyParamByPercent(PARAMS.ATTACK, mod, false);
        mod = getIntParam(PARAMS.OFFHAND_ATTACK_MOD);
        multiplyParamByPercent(PARAMS.OFF_HAND_ATTACK, mod, false);
        mod = getIntParam(PARAMS.DEFENSE_MOD);
        multiplyParamByPercent(PARAMS.DEFENSE, mod, false);

        ResistMaster.initUnitResistances(this);
    }

    public void resetIntegrity() {
        IntegrityRule.resetIntegrity(this);

    }

    public void applyIntegrity() {
        IntegrityRule.applyIntegrity(this);

    }

    @Override
    public void afterEffects() {
        resetHeroValues();
        if (game.isSimulation()) {
            initSpellbook();
        }

        resetMorale();
        if (!dynamicValuesReady && !game.isSimulation()) {
            addDynamicValues();
            dynamicValuesReady = true; // TODO recalc by percentage in wc3 style
            resetPercentages();
        }

        calculatePower();
        calculateWeight();
        calculateRemainingMemory();

        if (!game.isSimulation()) { // TODO perhaps I should apply and display
            // them!
            if (!getGame().getRules().getStaminaRule().apply(this))
                setInfiniteValue(PARAMS.STAMINA, 0.2f);
            if (!getGame().getRules().getFocusRule().apply(this))
                setInfiniteValue(PARAMS.FOCUS, 1);
            if (!getGame().getRules().getMoraleRule().apply(this))
                setInfiniteValue(PARAMS.MORALE, 0.5f);
            if (!getGame().getRules().getWeightRule().apply(this))
                setInfiniteValue(PARAMS.CARRYING_CAPACITY, 2);
            getGame().getRules().getWatchRule().updateWatchStatus(this);
            getGame().getRules().getWoundsRule().apply(this);

            recalculateInitiative();
        } else
            afterBuffRuleEffects();

        if (game.isSimulation()) {
            resetSpells();
            return;
        }
        if (getGame().getInventoryManager() != null)
            if (getGame().getInventoryManager().isActive())
                return;
        resetSpells();
        resetQuickItemActives();
        resetActives();
    }

    private void resetQuickItemActives() {
        for (DC_QuickItemObj q : getQuickItems())
            q.afterEffects();
    }

    public List<DC_ItemActiveObj> getQuickItemActives() {
        if (!ListMaster.isNotEmpty(getQuickItems()))
            return new LinkedList<>();
        List<DC_ItemActiveObj> qia = new LinkedList<>();
        for (DC_QuickItemObj q : getQuickItems()) {
            if (!q.isConstructed())
                q.construct();
            DC_ItemActiveObj active = q.getActive();
            if (active != null)
                qia.add(active);
        }
        return qia;

    }

    public void afterBuffRuleEffects() {
        if (secondWeapon != null) {
            setParam(PARAMS.OFF_HAND_ATTACK, getIntParam(PARAMS.ATTACK));
            secondWeapon.applyMasteryBonus();

        } else if (getNaturalWeapon(true) != null) {
            setParam(PARAMS.OFF_HAND_ATTACK, getIntParam(PARAMS.ATTACK));
            getNaturalWeapon(true).applyUnarmedMasteryBonus();
        }
        calculateDamage(true);
        if (weapon != null)
            weapon.applyMasteryBonus();
        else if (getNaturalWeapon(false) != null) {
            getNaturalWeapon(false).applyUnarmedMasteryBonus();
        }
        calculateDamage(false);

        applyMods();
    }

    public void resetMorale() {
        if (ParamAnalyzer.isMoraleIgnore(this))
            return;
        if (getIntParam(PARAMS.BATTLE_SPIRIT) == 0) {
            if (getRef().getObj(KEYS.PARTY) == null) {
                setParam(PARAMS.BATTLE_SPIRIT, 100);
            }
        }
        setParam(PARAMS.MORALE, getIntParam(PARAMS.SPIRIT) * DC_Formulas.MORALE_PER_SPIRIT
                * getIntParam(PARAMS.BATTLE_SPIRIT) / 100);
        // the C_ value cannot be changed, but the PERCENTAGE
        setParam(PARAMS.C_MORALE, getIntParam(PARAMS.C_MORALE), true);

    }

    private void setInfiniteValue(PARAMS param, float mod) {
        setParam(param, (int) (DC_Formulas.INFINITE_VALUE * mod));
        setParam(ContentManager.getCurrentParam(param), getIntParam(param));
    }

    @Override
    public String getParamRounded(PARAMETER param, boolean base) {
        if (base) {
            if (param.isAttribute()) {
                return getParam(DC_ContentManager.getBaseAttr(param));
            }
        }
        return super.getParamRounded(param, base);
    }

    private void resetHeroValues() {
        if (isHero()) {
            resetIntegrity();
            applyIntegrity();
        }
        getMasteries().apply();
        getAttrs().apply();
    }

    private void initActives() {
        // if (!isActivesReady()) {

        AbilityConstructor.constructActives(this);
        // }
        setActivesReady(true);
    }

    public boolean canUseItems() {
        return canUseWeapons() || canUseArmor();
    }

    public boolean canUseArmor() {
        return checkContainerProp(G_PROPS.CLASSIFICATIONS, CLASSIFICATIONS.HUMANOID.toString());
    }

    public boolean canUseWeapons() {
        return TYPE_ENUM == OBJ_TYPES.CHARS;
        // return checkContainerProp(G_PROPS.CLASSIFICATIONS,
        // CLASSIFICATIONS.HUMANOID
        // .toString());
    }

    public boolean hasArmorProficiency(ARMOR_TYPE type) {
        return false;
    }

    public boolean hasWeaponProficiency(WEAPON_SIZE size) {
        return false;
    }

    // isPassivesReady
    @Override
    public void activatePassives() {
        if (!isPassivesReady() || game.isSimulation())
            AbilityConstructor.constructPassives(this);
        super.activatePassives();
    }

    public void setWeapon(DC_WeaponObj weapon) {
        this.weapon = weapon;
        if (weapon != null)
            weapon.setMainHand(true);
        else
            ref.setID(KEYS.WEAPON, null);
        if (!game.isSimulation()) {
            String id = "";
            if (weapon != null)
                id = weapon.getId() + "";
            setProperty(G_PROPS.MAIN_HAND_ITEM, id);
        }
    }

    @Override
    public void newRound() {
        // if (!itemsInitialized)
        // initItems();
        super.newRound();
    }

    public DC_WeaponObj getMainWeapon() {
        return weapon;
    }

    public DC_ArmorObj getArmor() {
        return armor;
    }

    public void setArmor(DC_ArmorObj armor) {
        this.armor = armor;
        if (armor == null)
            ref.setID(KEYS.ARMOR, null);
        if (!game.isSimulation()) {
            String id = "";
            if (armor != null)
                id = armor.getId() + "";
            setProperty(G_PROPS.ARMOR_ITEM, id);
        }
    }

    public List<DC_SpellObj> getSpells() {

        if (spells == null) {
            spells = new LinkedList<>();
        }
        return spells;
    }

    public void setSpells(List<DC_SpellObj> spells) {
        this.spells = spells;
    }

    public DC_WeaponObj getSecondWeapon() {
        return secondWeapon;
    }

    public void setSecondWeapon(DC_WeaponObj secondWeapon) {
        this.secondWeapon = secondWeapon;
        if (secondWeapon != null)
            secondWeapon.setMainHand(false);
        else
            ref.setID(KEYS.WEAPON, null);

        if (!game.isSimulation()) {
            String id = "";
            if (secondWeapon != null)
                id = secondWeapon.getId() + "";
            setProperty(G_PROPS.OFF_HAND_ITEM, id);
        }
    }

    /**
     * mastery group (spell/skill),
     *
     * @param potential    has or can have
     * @param TYPE
     * @param dividingProp spellgroup/mastery group/...
     * @param prop         spellbook/verbatim/skills/etc
     * @return
     */

    public boolean checkItemGroup(PROPERTY prop, PROPERTY dividingProp, String name,
                                  boolean potential, OBJ_TYPE TYPE) {
        // at least one item with NAME as PROP

        for (String item : StringMaster.openContainer(getProperty(prop))) {

            ObjType type = DataManager.getType(item, TYPE);
            if (type == null)
                continue;
            if (!potential)
                return type.checkSingleProp(dividingProp, name);

            return game.getRequirementsManager().check(this, type) == null;

        }

        return false;

    }

    public List<DC_SpellObj> getSpellbook() {
        return spellbook;
    }

    public void setSpellbook(List<DC_SpellObj> spellbook) {
        this.spellbook = spellbook;
    }

    public void addFeat(DC_FeatObj e) {
        if (e.getOBJ_TYPE_ENUM() == OBJ_TYPES.SKILLS)
            getSkills().add(e);
        else
            getClasses().add(e);
    }

    public DequeImpl<DC_FeatObj> getSkills() {
        return skills;
    }

    public void setSkills(DequeImpl<DC_FeatObj> skills) {
        this.skills = skills;
    }

    public boolean isQuickSlotsFull() {
        if (game.isSimulation()) {
            return getIntParam(PARAMS.QUICK_SLOTS) <= StringMaster.openContainer(
                    getProperty(PROPS.QUICK_ITEMS)).size();
        }
        if (quickItems == null)
            return false;
        return getRemainingQuickSlots() <= 0;
    }

    public int getRemainingQuickSlots() {
        return getIntParam(PARAMS.QUICK_SLOTS) - quickItems.size();
    }

    public DequeImpl<DC_QuickItemObj> getQuickItems() {
        if (!isItemsInitialized())
            if (quickItems == null)
                quickItems = new DequeImpl<>();
        return quickItems;
    }

    public void setQuickItems(DequeImpl<DC_QuickItemObj> quickItems) {
        this.quickItems = quickItems;
    }

    public Entity getItem(String name) {
        // for (String sub: getInventory())
        return null;
    }

    public DequeImpl<DC_HeroItemObj> getInventory() {
        if (!isItemsInitialized())
            if (inventory == null)
                inventory = new DequeImpl<>();
        return inventory;
    }

    public void setInventory(DequeImpl<DC_HeroItemObj> inventory) {
        this.inventory = inventory;
    }

    public DC_Masteries getMasteries() {
        if (masteries == null)
            initMasteries();
        return masteries;
    }

    public void setMasteries(DC_Masteries masteries) {
        this.masteries = masteries;
    }

    public DC_Attributes getAttrs() {
        if (attrs == null)
            initAttributes();
        return attrs;
    }

    public void setAttrs(DC_Attributes attrs) {
        this.attrs = attrs;
    }

    public boolean isMaxClassNumber() {
        return getBaseClassNumber() >= DC_Constants.MAX_BASE_CLASSES;
    }

    private int getBaseClassNumber() {
        return getContainerSize(PROPS.CLASSES);
    }

    private int getContainerSize(PROPS prop) { // class-upgrade logic
        // deprecated?
        return StringMaster.openContainer(getProperty(prop)).size();
    }

    public DequeImpl<DC_FeatObj> getClasses() {
        return classes;
    }

    public void setClasses(DequeImpl<DC_FeatObj> classes) {
        this.classes = classes;
    }

    public DC_WeaponObj getActiveWeapon(boolean offhand) {
        DC_WeaponObj weapon = getWeapon(offhand);

        if (weapon == null)
            weapon = getNaturalWeapon(offhand);
        if (weapon == null)
            if (!offhand)
                weapon = DC_ContentManager.getDefaultWeapon(this);
        return weapon;
    }

    public DC_WeaponObj getWeapon(boolean offhand) {
        return (offhand) ? getSecondWeapon() : getMainWeapon();
    }

    public boolean equip(DC_HeroItemObj item, ITEM_SLOT slot) {
        DC_HeroItemObj prevItem = getItem(slot);
        setItem(item, slot);
        item.setRef(ref);
        if (prevItem != null)
            addItemToInventory(prevItem);
        // check weight and prompt drop if too heavy?
        return true;
    }

    public void addItemToInventory(DC_HeroItemObj item) {
        inventory.add(item);
        item.setRef(ref);
        // EVENT,
    }

    public void setItem(DC_HeroItemObj item, ITEM_SLOT slot) {
        if (item instanceof DC_QuickItemObj) {
            if (((DC_QuickItemObj) item).getWrappedWeapon() != null)
                item = ((DC_QuickItemObj) item).getWrappedWeapon();
        }
        switch (slot) {
            case ARMOR:
                setArmor((DC_ArmorObj) item);
                break;
            case MAIN_HAND:
                setWeapon((DC_WeaponObj) item);
                break;
            case OFF_HAND:
                setSecondWeapon((DC_WeaponObj) item);
                break;
        }
        if (item != null)
            item.equipped(ref);
    }

    public DC_HeroItemObj getItem(ITEM_SLOT slot) {
        switch (slot) {
            case ARMOR:
                return getArmor();
            case MAIN_HAND:
                return getMainWeapon();
            case OFF_HAND:
                return getSecondWeapon();
        }
        return null;
    }

    public boolean isItemsInitialized() {
        return itemsInitialized;
    }

    public void setItemsInitialized(boolean itemsInitialized) {
        this.itemsInitialized = itemsInitialized;
    }

    public boolean dropItemFromInventory(DC_HeroItemObj item) {
        removeFromInventory(item);
        // add to some container in the gamestate!

        getGame().getDroppedItemManager().drop(item, this);

        return true;
    }

    public void removeFromInventory(DC_HeroItemObj item) {
        getInventory().remove(item);
        if (getInventory().isEmpty())
            setInventory(null);
    }

    public void fullReset(DC_Game newGame) {
        setDead(false);
        setGame(newGame);
        game.getState().addObject(this);
        toBase();
        if (!game.isSimulation())
            resetObjects();
        afterEffects();
        addDynamicValues(); // initial perc?
        resetPercentages();
        getCustomParamMap().clear();
        getCustomPropMap().clear();
        game.getState().addObject(secondWeapon);
        game.getState().addObject(weapon);
        game.getState().addObject(armor);
        for (DC_HeroItemObj item : getQuickItems()) {
            game.getState().addObject(item);
        }
        for (DC_HeroItemObj item : getInventory()) {
            game.getState().addObject(item);
        }
    }

    public boolean canDivine() {
        if (deity == null)
            return false;
        if (!checkParam(PARAMS.DIVINATION_MASTERY))
            return false;
        return !deity.getName().equals(STD_BUFF_NAMES.Faithless.name());

    }

    public BACKGROUND getBackground() {
        return new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class,
                getProperty(G_PROPS.BACKGROUND));
    }

    public DequeImpl<DC_HeroItemObj> getJewelry() {
        if (jewelry == null)
            jewelry = new DequeImpl<>();
        return jewelry;
    }

    public void setJewelry(DequeImpl<DC_HeroItemObj> jewelry) {
        this.jewelry = jewelry;
    }

    public void addJewelryItem(DC_HeroItemObj item) {
        getJewelry().add(item);
        item.setRef(ref);
    }

    public void unequip(ITEM_SLOT slot) {
        unequip(slot, false);
    }

    public void unequip(ITEM_SLOT slot, Boolean drop) {
        DC_HeroItemObj item = null;
        switch (slot) {
            case ARMOR:
                item = getArmor();
                setArmor(null);
                break;
            case MAIN_HAND:
                item = getMainWeapon();
                setWeapon(null);
                break;
            case OFF_HAND:
                item = getSecondWeapon();
                setSecondWeapon(null);
                break;
        }
        if (item == null)
            return;
        if (drop != null) {
            addItemToInventory(item);
            if (drop)
                dropItemFromInventory(item);
        }
        item.unequip();
    }

    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, DC_UnitObj target, Ref REF,
                                    boolean offhand) {
        DC_Obj weapon = (DC_Obj) ref.getObj(offhand ? KEYS.OFFHAND : KEYS.WEAPON);
        if (weapon == null)
            weapon = getWeapon(offhand);
        if (weapon == null)
            weapon = getNaturalWeapon(offhand);
        if (weapon != null)
            weapon.applySpecialEffects(case_type, target, REF);
        super.applySpecialEffects(case_type, target, REF);
    }

    public void unequip(DC_HeroItemObj item, Boolean drop) {
        if (getWeapon(false) == item)
            unequip(ITEM_SLOT.MAIN_HAND, drop);
        else if (getWeapon(true) == item)
            unequip(ITEM_SLOT.OFF_HAND, drop);
        else if (getArmor() == item)
            unequip(ITEM_SLOT.ARMOR, drop);

    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        boolean result = super.kill(killer, leaveCorpse, quietly);
        if (!CoreEngine.isLevelEditor())
            if (result) {
                for (DC_FeatObj s : getSkills())
                    s.apply();
                for (AbilityObj p : passives)
                    p.activate();
                // TODO could filter by some boolean set via GOME itself! so
                // much
                // for a small thing...
            }
        return result;
    }

    public List<DC_HeroSlotItem> getSlotItems() {
        ListMaster<DC_HeroSlotItem> listMaster = new ListMaster<DC_HeroSlotItem>();
        return listMaster.removeNulls(listMaster.getList(weapon, armor, secondWeapon));
    }

    public boolean hasBroadReach() {
        return hasWeaponPassive(null, STANDARD_PASSIVES.BROAD_REACH);
    }

    public boolean hasHindReach() {
        return hasWeaponPassive(null, STANDARD_PASSIVES.HIND_REACH);
    }

    public boolean hasWeaponPassive(Boolean offhand, STANDARD_PASSIVES passive) {
        if (checkPassive(passive))
            return true;
        if (offhand != null) {
            return (getActiveWeapon(offhand).checkPassive(passive));
        }
        DC_WeaponObj activeWeapon = getActiveWeapon(true);
        if (activeWeapon != null)
            if (activeWeapon.checkPassive(passive))
                return true;
        activeWeapon = getActiveWeapon(false);
        if (activeWeapon != null)
            if (activeWeapon.checkPassive(passive))
                return true;

        return false;
    }

    public boolean checkAiMod(AI_MODIFIERS trueBrute) {
        if (Launcher.BRUTE_AI_MODE) {
            if (trueBrute == AI_MODIFIERS.TRUE_BRUTE) {
                if (getUnitAI() == null) {
                    return true;
                }
                if (getUnitAI().getType() != AI_TYPE.SNEAK)
                    if (getUnitAI().getType() != AI_TYPE.CASTER)
                        if (getUnitAI().getType() != AI_TYPE.ARCHER) {
                            if (getSpells().isEmpty())
                                return true;
                        }
            }
        }
        return checkProperty(PROPS.AI_MODIFIERS, trueBrute.toString());
    }

    public DC_SpellObj getSpell(String actionName) {
        for (DC_SpellObj s : getSpells())
            if (s.getName().equalsIgnoreCase(actionName))
                return s;

        return null;
    }

    public boolean isAiControlled() {
        if (aiControlled)
            return true;

        if (owner.isAi())
            if (!checkBool(DYNAMIC_BOOLS.PLAYER_CONTROLLED))
                return true;
        if (checkBool(DYNAMIC_BOOLS.AI_CONTROLLED))
            return true;

        return getBehaviorMode() != null;
    }

    public void setAiControlled(boolean aiControlled) {
        this.aiControlled = aiControlled;
    }

    public MACRO_MODES getMacroMode() {
        if (macroMode == null)
            macroMode = new EnumMaster<MACRO_MODES>().retrieveEnumConst(MACRO_MODES.class,
                    getProperty(MACRO_PROPS.MACRO_MODE));
        return macroMode;
    }

    public void setMacroMode(MACRO_MODES mode) {
        if (mode == null)
            removeProperty(MACRO_PROPS.MACRO_MODE);
        else
            setProperty(MACRO_PROPS.MACRO_MODE, mode.toString());
        this.macroMode = mode;
    }

    public GENDER getGender() {
        if (gender == null)
            gender = new EnumMaster<GENDER>().retrieveEnumConst(GENDER.class,
                    getProperty(G_PROPS.GENDER));
        return gender;
    }

    public boolean isLeader() {
        Obj obj = ref.getObj(KEYS.PARTY);
        if (obj instanceof PartyObj) {
            PartyObj partyObj = (PartyObj) obj;
            return partyObj.getLeader().equals(this);
        }
        return false;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean b) {
        hidden = b;
    }

    public Dungeon getDungeon() {
        if (dungeon == null)
            return getGame().getDungeonMaster().getRootDungeon();
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        if (dungeon != null)
            setZ(dungeon.getZ());
    }

    public FLIP getFlip() {
        return flip;
    }

    public void setFlip(FLIP flip) {
        this.flip = flip;
    }

    public boolean isMainHero() {
        return mainHero;
    }

    public void setMainHero(boolean mainHero) {
        this.mainHero = mainHero;
    }

    public boolean toggleActionMode(DC_ActiveObj action, String mode) {
        String previous = getActionModeMap().put(action, mode);
        if (previous != null)
            if (mode.equals(previous)) {
                getActionModeMap().remove(action);
                return false;
            }
        return true;
    }

    public String getActionMode(DC_ActiveObj activeObj) {
        if (StringMaster.isEmpty(getActionModeMap().get(activeObj)))
            return null;
        return getActionModeMap().get(activeObj);
    }

    public boolean isAnimated() {
        if (animation == null)
            return false;
        return animation.isDrawReady();
    }

    public void initToolTip() {
        getGame().getToolTipMaster().initUnitTooltip(this, false);

    }

    public boolean isHostileTo(DC_Player player) {
        if (getBehaviorMode() == BEHAVIOR_MODE.BERSERK)
            return true;
        return getOwner().isHostileTo(player);
    }

    public void spellUpgradeToggled(Entity spell, SPELL_UPGRADE ug) {
        // getSpell(spell.getName());
        // // spellUpgradeMap.getOrCreate();
        // upgrades
        // value += spell +
        // StringMaster.wrapInParenthesis(StringMaster.constructStringContainer(upgrades));
        // setProperty(PROPS.SPELL_UPGRADES, value);

    }

    public DC_HeroItemObj findItem(String typeName, Boolean quick_inv_slot) {
        if (quick_inv_slot == null) {
            return new ListMaster<DC_HeroSlotItem>().findType(typeName, new LinkedList<>(
                    getSlotItems()));
        }
        DC_HeroItemObj item = quick_inv_slot ? new ListMaster<DC_HeroItemObj>().findType(typeName,
                new LinkedList<>(getInventory())) : new ListMaster<DC_QuickItemObj>().findType(
                typeName, new LinkedList<>(getQuickItems()));
        return item;
    }

    public void setCoordinates(main.game.battlefield.Coordinates coordinates) {
        if (coordinates.isInvalid()) {
            coordinates = CoordinatesMaster.getClosestValid(coordinates);
            super.setCoordinates(coordinates);
            return;
        }
        super.setCoordinates(coordinates);
    }

    public DC_ActiveObj getAttackAction(boolean offhand) {
        return getAction(offhand ? DC_ActionManager.OFFHAND_ATTACK : DC_ActionManager.ATTACK);
    }

    public DC_HeroObj getEngagementTarget() {
        return engagementTarget;
    }

    public void setEngagementTarget(DC_HeroObj engaged) {
        engagementTarget = engaged;
    }

    public boolean isSelected() {
        return isInfoSelected() || isActiveSelected();
    }

    public ObjType getBackgroundType() {
        return backgroundType;
    }

    public Map<DC_ActiveObj, String> getActionModeMap() {
        if (actionModeMap == null)
            actionModeMap = new HashMap<DC_ActiveObj, String>();
        return actionModeMap;
    }

    public boolean isPlayerControlled() {
        if (isAiControlled())
            return false;

        return !getOwner().isAi();
    }

    public boolean isEngagedWith(DC_HeroObj attacker) {
        // TODO Auto-generated method stub
        return false;
    }

}
