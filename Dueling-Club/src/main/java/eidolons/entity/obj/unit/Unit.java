package eidolons.entity.obj.unit;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.ValuePages;
import eidolons.entity.active.DC_ActionManager;
import eidolons.entity.active.DC_ActionManager.STD_ACTIONS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.handlers.bf.unit.*;
import eidolons.entity.item.*;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.KeyResolver;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.obj.hero.DC_Attributes;
import eidolons.entity.obj.hero.DC_Masteries;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.adventure.entity.MacroActionManager.MACRO_MODES;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.gui.panels.dc.inventory.InventorySlotsPanel;
import eidolons.system.DC_Constants;
import eidolons.system.DC_Formulas;
import eidolons.system.test.Debugger;
import main.ability.AbilityObj;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.MetaEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.SearchMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;

import java.util.*;
import java.util.stream.Collectors;

public class Unit extends DC_UnitModel {
    protected DC_WeaponObj offhandNaturalWeapon;
    protected DC_WeaponObj naturalWeapon;
    protected DC_WeaponObj weapon;
    protected DC_WeaponObj secondWeapon;
    // protected Footwear boots;
    // protected Helmet helmet;
    // protected Gloves gloves;
    // protected Cloak cloak;]
    protected DC_ArmorObj armor;
    protected DequeImpl<DC_FeatObj> skills;
    protected DequeImpl<HeroClass> classes;
    protected DequeImpl<Perk> perks;
    protected DequeImpl<DC_QuickItemObj> quickItems;
    protected DequeImpl<DC_JewelryObj> jewelry;
    protected DequeImpl<DC_HeroItemObj> inventory;

    protected DC_Masteries masteries;
    protected DC_Attributes attrs;

    protected List<DC_SpellObj> spells;
    protected boolean initialized;
    protected List<DC_SpellObj> spellbook;
    protected boolean itemsInitialized;
    protected boolean aiControlled;
    protected MACRO_MODES macroMode;
    protected GENDER gender;
    protected Boolean mainHero;
    protected FLIP flip;
    protected ObjType backgroundType;
    protected Map<DC_ActiveObj, String> actionModeMap;
    protected Unit engagementTarget;
    protected DC_WeaponObj rangedWeapon;
    protected boolean leader;
    protected boolean usingStealth;

    public Unit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        if (isHero()) {
//            main.system.auxiliary.log.LogMaster.log(1,this + " hero created " +getId());
            String message = this + " hero created " + getId();
            SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);

        }

        // getGame().getTestMaster().getTestSpells(); TODO add!
    }

    public Unit(Unit hero) {
        this(new ObjType(hero.getType(), true), hero.getX(), hero.getY(), hero.getOriginalOwner(),
         hero.getGame(), hero.getRef().getCopy());
        // transfer all buffs and other dynamic stuff?
    }

    public Unit(ObjType type) {
        this(type, DC_Game.game);
    }

    public Unit(ObjType type, DC_Game game) {
        this(type, 0, 0, DC_Player.NEUTRAL, game, new Ref(game));
    }


    @Override
    protected EntityMaster initMaster() {
        return new UnitMaster(this);
    }

    @Override
    public void init() {
        super.init();
        try {
            initDeity();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            initEmblem();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            initIntegrityAlignments();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

//        WaitMaster.receiveInput(WAIT_OPERATIONS.UNIT_OBJ_INIT, true);
    }

    @Override
    public UnitHandler getHandler() {
        return (UnitHandler) super.getHandler();
    }

    public void saveRanks(boolean skills) {
        saveRanks(skills ? getSkills() : getClasses(), skills ? PROPS.SKILLS : PROPS.CLASSES);
    }

    public void saveRanks(DequeImpl<? extends DC_FeatObj> container, PROPERTY property) {
        String value = "";
        for (DC_FeatObj featObj : container) {
            value += featObj.getName();
            if (featObj.getIntParam(PARAMS.RANK) > 0) {
                value += StringMaster.wrapInParenthesis(featObj.getParam(PARAMS.RANK));
            }
            value += ";";
        }
        setProperty(property, value, true);
    }

    public boolean incrementFeatRank(boolean skill, ObjType type) {
        DC_FeatObj featObj = getFeat(skill, type);
        return incrementFeatRank(skill, featObj);
    }

    public boolean incrementFeatRank(boolean skill, DC_FeatObj featObj) {
        if (featObj.getIntParam(PARAMS.RANK) == featObj.getIntParam(PARAMS.RANK_MAX)) {
            return false;
        }
        featObj.setParam(PARAMS.RANK, featObj.getIntParam(PARAMS.RANK) + 1);
        return true;
    }

    public void setFeatRank(boolean skill, int rank, ObjType type) {
        DC_FeatObj featObj = getFeat(skill, type);
        featObj.setParam(PARAMS.RANK, rank);
        // reset
    }

    public DC_FeatObj getFeat(ObjType type) {
        return getFeat(type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS, type);
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        return null;// TODO
    }


    public void setNaturalWeapon(boolean offhand, DC_WeaponObj weapon) {
        if (offhand) {
            offhandNaturalWeapon = weapon;
        } else {
            naturalWeapon = weapon;
        }
    }

    public DC_WeaponObj getNaturalWeapon() {
        return getNaturalWeapon(false);
    }

    public DC_WeaponObj getOffhandNaturalWeapon() {
        return getNaturalWeapon(true);
    }

    public DC_WeaponObj getNaturalWeapon(boolean offhand) {
        DC_WeaponObj weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        if (weaponObj == null) {
            initNaturalWeapon(offhand);
        }
        weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        return weaponObj;
    }

    public boolean removeJewelryItem(DC_HeroItemObj itemObj) {
        boolean result = getJewelry().remove(itemObj);
        if (getJewelry().isEmpty()) {
            setJewelry(null);
        }
        return result;
    }

    public void addQuickItem(DC_QuickItemObj itemObj) {
        if (getQuickItems() == null)
            setQuickItems(new DequeImpl<>());
        getQuickItems().add(itemObj);
        itemObj.setRef(ref);
        getResetter().resetQuickSlotsNumber();
    }

    public boolean removeQuickItem(DC_QuickItemObj itemObj) {
        if (getQuickItems() == null)
            return false;
        if (getQuickItems().remove(itemObj)) {
            getResetter().resetQuickSlotsNumber();

            type.removeProperty(PROPS.QUICK_ITEMS, itemObj.getName(), false);

            removeProperty(PROPS.QUICK_ITEMS, "" + itemObj.getId(), true);

            if (getQuickItems().isEmpty()) {
                setQuickItems(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void putParameter(PARAMETER param, String value) {
        if (isPlayerCharacter()) {
            Integer integer = StringMaster.getInteger(value);
            if (param.getName().contains("Percentage")) {
                if (integer < 0) {
                    return;
                }
                if (integer > MathMaster.MAX_REASONABLE_PERCENTAGE) {
                    return;
                }
            }
            if (param.isDynamic()) {
                if (integer < 0) {
                    if (param == PARAMS.C_ENDURANCE || param == PARAMS.C_TOUGHNESS)
                        if (!CoreEngine.isJar() ||
                         Debugger.isImmortalityOn())
                            return;
                }
            }
        }
        if (isValidMapStored(param))
            if (!getGame().getState().getManager().isResetting())
            //this is gross!
            if (!isBeingReset()){
                    getValidParams().put(param, StringMaster.getInteger(value));
                }

        super.putParameter(param, value);
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


    public void addPassive(STANDARD_PASSIVES passive) {
        addPassive(passive.getName());
    }

    @Override
    public void removed() {
        for (Obj obj : getSkills()) {
            getGame().remove(obj);
        }
    }


    public void addAction(String string) {
        ActiveObj action = game.getActionManager().getAction(string, this);
        if (action != null) {
            actives.add(action);
        }
    }


    @Override
    public void afterEffects() {
        if (ExplorationMaster.isExplorationOn()) {
            if (!isDirty()) {
                return;
            }
        }
        getResetter().afterEffectsApplied();

    }

    @Override
    public boolean isOutsideCombat() {
        return getAI().isOutsideCombat();
    }

    public List<DC_QuickItemAction> getQuickItemActives() {
        if (!ListMaster.isNotEmpty(getQuickItems())) {
            return new ArrayList<>();
        }
        List<DC_QuickItemAction> qia = new ArrayList<>();
        for (DC_QuickItemObj q : getQuickItems()) {
            if (!q.isConstructed()) {
                q.construct();
            }
            DC_QuickItemAction active = q.getActive();
            if (active != null) {
                qia.add(active);
            }
        }
        return qia;

    }

    public void afterBuffRuleEffects() {
        getResetter().afterBuffRuleEffects();
    }

    public void setInfiniteValue(PARAMS param, float mod) {
        setParam(param, (int) (DC_Formulas.INFINITE_VALUE * mod));
        setParam(ContentValsManager.getCurrentParam(param), getIntParam(param));
    }

    @Override
    public String getParamRounded(PARAMETER param, boolean base) {
        if (base) {
            if (param.isAttribute()) {
                return getParam(DC_ContentValsManager.getBaseAttr(param));
            }
        }
        return super.getParamRounded(param, base);
    }


    // isPassivesReady
    @Override
    public void activatePassives() {
        if (!isPassivesReady() || game.isSimulation()) {
            AbilityConstructor.constructPassives(this);
        }
        super.activatePassives();
    }

    public void setWeapon(DC_WeaponObj weapon) {
        this.weapon = weapon;
        if (weapon != null) {
            weapon.setMainHand(true);
        } else {
            ref.setID(KEYS.WEAPON, null);
        }
        if (!game.isSimulation()) {
            String id = "";
            if (weapon != null) {
                id = weapon.getId() + "";
            }
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
        if (armor == null) {
            ref.setID(KEYS.ARMOR, null);
        }
        if (!game.isSimulation()) {
            String id = "";
            if (armor != null) {
                id = armor.getId() + "";
            }
            setProperty(G_PROPS.ARMOR_ITEM, id);
        }
    }

    public List<DC_SpellObj> getSpells() {

        if (spells == null) {
            spells = new ArrayList<>();
        }
        return spells;
    }

    public void setSpells(List<DC_SpellObj> spells) {
        this.spells = spells;
    }

    public DC_WeaponObj getOffhandWeapon() {
        return secondWeapon;
    }

    public void setSecondWeapon(DC_WeaponObj secondWeapon) {
        this.secondWeapon = secondWeapon;
        if (secondWeapon != null) {
            secondWeapon.setMainHand(false);
        } else {
            ref.setID(KEYS.WEAPON, null);
        }

        if (!game.isSimulation()) {
            String id = "";
            if (secondWeapon != null) {
                id = secondWeapon.getId() + "";
            }
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

        for (String item : StringMaster.open(getProperty(prop))) {

            ObjType type = DataManager.getType(item, TYPE);
            if (type == null) {
                continue;
            }
            if (!potential) {
                return type.checkSingleProp(dividingProp, name);
            }

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
        if (e.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS) {
            getSkills().add(e);
        } else {
            getClasses().addCast(e);
        }
    }

    public DequeImpl<DC_FeatObj> getSkills() {
        return skills;
    }

    public void setSkills(DequeImpl<DC_FeatObj> skills) {
        this.skills = skills;
    }

    public boolean isInventoryFull() {
        return getInventory().size() >= InventorySlotsPanel.COLUMNS * InventorySlotsPanel.ROWS;
    }

    public boolean isQuickSlotsFull() {
        if (game.isSimulation()) {
            return getIntParam(PARAMS.QUICK_SLOTS) <= StringMaster.openContainer(
             getProperty(PROPS.QUICK_ITEMS)).size();
        }
        if (quickItems == null) {
            return false;
        }
        return getRemainingQuickSlots() <= 0;
    }

    public int getQuickSlotsMax() {
        return getIntParam(PARAMS.QUICK_SLOTS);
    }

    public int getRemainingQuickSlots() {
        if (quickItems == null) {
            return getQuickSlotsMax();
        }
        return getIntParam(PARAMS.QUICK_SLOTS) - quickItems.size();
    }

    public int getOccupiedQuickSlots() {
        if (quickItems == null) {
            return 0;
        }
        return quickItems.size();
    }

    public DequeImpl<DC_QuickItemObj> getQuickItems() {
        if (!isItemsInitialized()) {
            if (quickItems == null) {
                quickItems = new DequeImpl<>();
            }
        }
        return quickItems;
    }

    public void setQuickItems(DequeImpl<DC_QuickItemObj> quickItems) {
        this.quickItems = quickItems;
    }

    public DC_HeroItemObj getItemFromInventory(String name) {
        return new SearchMaster<DC_HeroItemObj>().find(name, getInventory());
    }

    public DC_QuickItemObj getQuickItem(String name) {
        return new SearchMaster<DC_QuickItemObj>().find(name, getQuickItems());
    }

    public Entity getItem(String name) {
        // for (String generic: getInventory())
        List<Entity> list = new ArrayList<>();
        list.add(getWeapon(true));
        list.add(getWeapon(false));
        list.add(getNaturalWeapon(true));
        list.add(getNaturalWeapon(false));
        getInventory().forEach(item -> list.add(item));
        getQuickItems().forEach(item -> list.add(item));
        getJewelry().forEach(item -> list.add(item));
        return new SearchMaster<Entity>().find(name, list);
    }

    public DequeImpl<DC_HeroItemObj> getInventory() {
        if (!isItemsInitialized()) {
            if (inventory == null) {
                inventory = new DequeImpl<>();
            }
        }
        return inventory;
    }

    public void setInventory(DequeImpl<DC_HeroItemObj> inventory) {
        this.inventory = inventory;
    }

    public DC_Masteries getMasteries() {
        if (masteries == null) {
            initMasteries();
        }
        return masteries;
    }

    public void setMasteries(DC_Masteries masteries) {
        this.masteries = masteries;
    }

    public DC_Attributes getAttrs() {
        if (attrs == null) {
            initAttributes();
        }
        return attrs;
    }

    public void setAttrs(DC_Attributes attrs) {
        this.attrs = attrs;
    }

    public boolean isMaxClassNumber() {
        return getBaseClassNumber() >= DC_Constants.MAX_BASE_CLASSES;
    }

    protected int getBaseClassNumber() {
        return getContainerSize(PROPS.CLASSES);
    }

    protected int getContainerSize(PROPS prop) { // class-upgrade logic
        // deprecated?
        return StringMaster.openContainer(getProperty(prop)).size();
    }

    public DequeImpl<HeroClass> getClasses() {
        return classes;
    }

    public void setClasses(DequeImpl<HeroClass> classes) {
        this.classes = classes;
    }

    public DC_WeaponObj getActiveWeapon(boolean offhand) {
        DC_WeaponObj weapon = getWeapon(offhand);

        if (weapon == null) {
            weapon = getNaturalWeapon(offhand);
        }
        if (weapon == null) {
            if (!offhand) {
                weapon = DC_ContentValsManager.getDefaultWeapon(this);
            }
        }
        return weapon;
    }

    public DC_WeaponObj getWeapon(boolean offhand) {
        return (offhand) ? getOffhandWeapon() : getMainWeapon();
    }

    public boolean equip(DC_HeroItemObj item, ITEM_SLOT slot) {
        DC_HeroItemObj prevItem = getItem(slot);
        setItem(item, slot);
        item.setRef(ref);
        if (prevItem != null) {
            addItemToInventory(prevItem);
        }
        if (game.isStarted())
            if (!CoreEngine.isGraphicsOff())
                if (!ExplorationMaster.isExplorationOn()) //only in combat!
                    if (item instanceof DC_WeaponObj)
                        AnimMaster3d.preloadAtlas((DC_WeaponObj) item);
        // preCheck weight and prompt drop if too heavy?
        return true;
    }

    public void addItemToInventory(DC_HeroItemObj item) {
        inventory.add(item);
        item.setRef(ref);
        // EVENT,
    }

    @Override
    public void applyType(ObjType type) {
        setItemsInitialized(false);
        //TODO need to copy all dynamic params!
        // or implement a real buffer copy
        type.copyValues(this,
         Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT).map(
          (PARAMETER p) -> ContentValsManager.getPercentageParam(p)).
          collect(Collectors.toList()));
        type.copyValues(this, Arrays.asList(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT));
        super.applyType(type);
    }

    public void setItem(DC_HeroItemObj item, ITEM_SLOT slot) {
        if (item instanceof DC_QuickItemObj) {
            if (((DC_QuickItemObj) item).getWrappedWeapon() != null) {
                item = ((DC_QuickItemObj) item).getWrappedWeapon();
            }
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
        if (item != null) {
            item.equipped(ref);
        }
    }

    public DC_HeroItemObj getItem(ITEM_SLOT slot) {
        switch (slot) {
            case ARMOR:
                return getArmor();
            case MAIN_HAND:
                return getMainWeapon();
            case OFF_HAND:
                return getOffhandWeapon();
        }
        return null;
    }

    public boolean isItemsInitialized() {
        return itemsInitialized;
    }

    public void setItemsInitialized(boolean itemsInitialized) {
        this.itemsInitialized = itemsInitialized;
    }

    public boolean dropItemFromInventory(DC_HeroItemObj item, Coordinates c) {
        removeFromInventory(item);
        if (!isSimulation()) //sim just remembers for real hero to drop
            getGame().getDroppedItemManager().drop(item, c);

        return true;
    }

    public boolean dropItemFromInventory(DC_HeroItemObj item) {
        return dropItemFromInventory(item, getCoordinates());
    }

    public boolean removeFromInventory(DC_HeroItemObj item) {
        if (!getInventory().remove(item))
            return false;
        if (getInventory().isEmpty()) {
            setInventory(null);
        }
        return true;
    }

    public void fullReset(DC_Game newGame) {
        setDead(false);
        setGame(newGame);
        game.getState().addObject(this);
        toBase();
        if (!game.isSimulation()) {
            resetObjects();
        }
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
        if (deity == null) {
            return false;
        }
        if (!checkParam(PARAMS.DIVINATION_MASTERY)) {
            return false;
        }
        return !deity.getName().equals(MetaEnums.STD_BUFF_NAMES.Faithless.name());

    }

    public BACKGROUND getBackground() {
        return new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class,
         getProperty(G_PROPS.BACKGROUND));
    }

    public DequeImpl<DC_JewelryObj> getJewelry() {
        if (jewelry == null) {
            jewelry = new DequeImpl<>();
        }
        return jewelry;
    }

    public void setJewelry(DequeImpl<DC_JewelryObj> jewelry) {
        this.jewelry = jewelry;
    }

    public void addJewelryItem(DC_JewelryObj item) {
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
                item = getOffhandWeapon();
                setSecondWeapon(null);
                break;
        }
        if (item instanceof DC_WeaponObj) {
            if (((DC_WeaponObj) item).isRanged()) {
                setRangedWeapon(null);
            }
        }
        if (item == null) {
            return;
        }
        if (drop != null) {
            addItemToInventory(item);
            if (drop) {
                dropItemFromInventory(item);
            }
        }
        item.unequip();
    }

    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, BattleFieldObject target, Ref REF,
                                    boolean offhand) {
        DC_Obj weapon = (DC_Obj) ref.getObj(offhand ? KEYS.OFFHAND : KEYS.WEAPON);
        if (weapon == null) {
            weapon = getWeapon(offhand);
        }
        if (weapon == null) {
            weapon = getNaturalWeapon(offhand);
        }
        if (weapon != null) {
            weapon.applySpecialEffects(case_type, target, REF);
        }
        DC_Obj action = (DC_Obj) ref.getActive();
        if (action != null) {
            action.applySpecialEffects(case_type, target, REF);
        }
        super.applySpecialEffects(case_type, target, REF);
    }

    public void unequip(DC_HeroItemObj item, Boolean drop) {
        if (getWeapon(false) == item) {
            unequip(ItemEnums.ITEM_SLOT.MAIN_HAND, drop);
            return;
        } else if (getWeapon(true) == item) {
            unequip(ItemEnums.ITEM_SLOT.OFF_HAND, drop);
            return;
        } else if (getArmor() == item) {
            unequip(ItemEnums.ITEM_SLOT.ARMOR, drop);
            return;
        }
        boolean result = removeJewelryItem(item);
        if (!result)
            if (item instanceof DC_QuickItemObj)
                result = removeQuickItem((DC_QuickItemObj) item);
        if (result) {
            addItemToInventory(item);
            if (drop) {
                dropItemFromInventory(item);
            }
        }

    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        boolean result = super.kill(killer, leaveCorpse, quietly);
//        if (!CoreEngine.isLevelEditor()) {
//            if (result) {
//                for (DC_FeatObj s : getSkills()) {
//                    s.apply();
//                }
//                for (AbilityObj p : passives) {
//                    p.activate();
//                }
        // TODO could filter by some boolean set via GOME itself! so
        // much
        // for a small thing...
//            }
//        }
        return result;
    }

    public List<AbilityObj> getPassivesFiltered() {
        return null;
    }

    public List<DC_HeroSlotItem> getSlotItems() {
        ListMaster<DC_HeroSlotItem> listMaster = new ListMaster<>();
        return listMaster.removeNulls(listMaster.getList(weapon, armor, secondWeapon));
    }

    public boolean hasBroadReach() {
        return hasWeaponPassive(null, UnitEnums.STANDARD_PASSIVES.BROAD_REACH);
    }

    public boolean hasHindReach() {
        return hasWeaponPassive(null, UnitEnums.STANDARD_PASSIVES.HIND_REACH);
    }

    public boolean hasWeaponPassive(Boolean offhand, STANDARD_PASSIVES passive) {
        if (checkPassive(passive)) {
            return true;
        }
        if (offhand != null) {
            return (getActiveWeapon(offhand).checkPassive(passive));
        }
        DC_WeaponObj activeWeapon = getActiveWeapon(true);
        if (activeWeapon != null) {
            if (activeWeapon.checkPassive(passive)) {
                return true;
            }
        }
        activeWeapon = getActiveWeapon(false);
        if (activeWeapon != null) {
            if (activeWeapon.checkPassive(passive)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkAiMod(AI_MODIFIERS trueBrute) {
        if (AI_Manager.BRUTE_AI_MODE) {
            if (trueBrute == AI_MODIFIERS.TRUE_BRUTE) {
                if (getUnitAI() == null) {
                    return true;
                }
                if (getUnitAI().getType() != AiEnums.AI_TYPE.SNEAK) {
                    if (getUnitAI().getType() != AiEnums.AI_TYPE.CASTER) {
                        if (getUnitAI().getType() != AiEnums.AI_TYPE.ARCHER) {
                            if (getSpells().isEmpty()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return checkProperty(PROPS.AI_MODIFIERS, trueBrute.toString());
    }


    public DC_SpellObj getSpell(String actionName) {
        for (DC_SpellObj s : getSpells()) {
            if (s.getName().equalsIgnoreCase(actionName)) {
                return s;
            }
        }

        return null;
    }

    public boolean isAiControlled() {
        if (!getAI().getForcedActions().isEmpty()) {
            return true;
        }
        if (aiControlled || checkBool(DYNAMIC_BOOLS.AI_CONTROLLED)) {
            return !AI_Manager.isOff();
        }
        if (owner.isAi()) {
            if (!checkBool(DYNAMIC_BOOLS.PLAYER_CONTROLLED)) {
                return !AI_Manager.isOff();
            }
        } else {
            if (!getGame().isDebugMode()) {
                if (!getGame().getCombatMaster().isFullManualControl()) {
                    if (owner.getHeroObj() != null) {
                        if (!isMainHero()) {
                            return !AI_Manager.isOff();
                        }
                    }
                }
            }
        }

        return getBehaviorMode() != null;
    }

    public void setAiControlled(boolean aiControlled) {
        this.aiControlled = aiControlled;
    }

    public UnitAI getAI() {
        return getUnitAI();
    }

    public UnitAI getUnitAI() {
        if (unitAI == null) {
            unitAI = new UnitAI(this);
        }
        return unitAI;
    }

    public MACRO_MODES getMacroMode() {
        if (macroMode == null) {
            macroMode = new EnumMaster<MACRO_MODES>().retrieveEnumConst(MACRO_MODES.class,
             getProperty(MACRO_PROPS.MACRO_MODE));
        }
        return macroMode;
    }

    public void setMacroMode(MACRO_MODES mode) {
        if (mode == null) {
            removeProperty(MACRO_PROPS.MACRO_MODE);
        } else {
            setProperty(MACRO_PROPS.MACRO_MODE, mode.toString());
        }
        this.macroMode = mode;
    }

    public GENDER getGender() {
        if (gender == null) {
            gender = new EnumMaster<GENDER>().retrieveEnumConst(GENDER.class,
             getProperty(G_PROPS.GENDER));
        }
        return gender;
    }


    public FLIP getFlip() {
        return flip;
    }

    public void setFlip(FLIP flip) {
        this.flip = flip;
    }

    public boolean isMainHero() {
        if (mainHero == null)
            mainHero = owner.getHeroObj() == this;
        return mainHero;
    }

    public void setMainHero(boolean mainHero) {
        this.mainHero = mainHero;
    }

    public boolean isPlayerCharacter() {

        return isMainHero() && isMine();
    }

    public boolean toggleActionMode(DC_ActiveObj action, String mode) {
        String previous = getActionModeMap().put(action, mode);
        if (previous != null) {
            if (mode.equals(previous)) {
                getActionModeMap().remove(action);
                return false;
            }
        }
        return true;
    }

    public String getActionMode(DC_ActiveObj activeObj) {
        if (StringMaster.isEmpty(getActionModeMap().get(activeObj))) {
            return null;
        }
        return getActionModeMap().get(activeObj);
    }

    @Deprecated
    public void initToolTip() {

    }

    public boolean isLeader() {
        Obj obj = ref.getObj(KEYS.PARTY);
        if (obj instanceof Party) {
            Party party = (Party) obj;
            return party.getLeader().equals(this);
        }
        return false;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public boolean isHostileTo(DC_Player player) {
        if (getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
            return true;
        }
        return isEnemyTo(player);
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
            return new ListMaster<DC_HeroSlotItem>().findType(typeName, new ArrayList<>(
             getSlotItems()));
        }
        DC_HeroItemObj item = !quick_inv_slot ? new ListMaster<DC_HeroItemObj>().findType(typeName,
         new ArrayList<>(getInventory())) : new ListMaster<DC_QuickItemObj>().findType(
         typeName, new ArrayList<>(getQuickItems()));
        return item;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates.isInvalid()) {
            coordinates = CoordinatesMaster.getClosestValid(coordinates);
            super.setCoordinates(coordinates);
            return;
        }
        super.setCoordinates(coordinates);
    }

    public DC_ActiveObj getAttackAction(boolean offhand) {

        return getAction(offhand ? DC_ActionManager.OFFHAND_ATTACK :
         DC_ActionManager.ATTACK);
    }

    public Unit getEngagementTarget() {
        return engagementTarget;
    }

    public void setEngagementTarget(Unit engaged) {
        engagementTarget = engaged;
    }


    public ObjType getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(ObjType backgroundType) {
        this.backgroundType = backgroundType;
    }

    public Map<DC_ActiveObj, String> getActionModeMap() {
        if (actionModeMap == null) {
            actionModeMap = new HashMap<>();
        }
        return actionModeMap;
    }

    public boolean isPlayerControlled() {
        if (isAiControlled()) {
            return false;
        }

        return !getOwner().isAi();
    }

    public boolean isEngagedWith(Unit attacker) {
        // TODO Auto-generated method stub
        return false;
    }

    public List<DC_ActiveObj> getAttacks(boolean offhand) {
        return getAction(offhand ? DC_ActionManager.OFFHAND_ATTACK : DC_ActionManager.ATTACK).getSubActions();
    }

    public void resetQuickSlotsNumber() {
        getResetter().resetQuickSlotsNumber();
    }

    public void resetObjectContainers(boolean fromValues) {
//        if (!fromValues)
        setItemsInitialized(false);
    }


    public void resetDefaultAttrs() {
        getResetter().resetDefaultAttrs();
    }


    public void resetMorale() {
        getResetter().resetMorale();
    }

    @Override
    public UnitLogger getLogger() {
        return (UnitLogger) super.getLogger();
    }

    @Override
    public UnitCalculator getCalculator() {
        return super.getCalculator();
    }

    public int calculateRemainingMemory() {
        return getCalculator().calculateRemainingMemory();
    }

    public int calculateUsedMemory() {
        return getCalculator().calculateUsedMemory();
    }

    public int calculatePower() {
        return getCalculator().calculatePower();
    }

    public int calculateWeight() {
        return getCalculator().calculateWeight();
    }

    public Integer calculateAndSetDamage(boolean offhand) {
        return getCalculator().calculateAndSetDamage(offhand);
    }

    public Integer calculateDamage(boolean offhand) {
        return getCalculator().calculateDamage(offhand);
    }

    public Integer calculateDamage(boolean offhand, boolean set) {
        return getCalculator().calculateDamage(offhand, set);
    }

    public int calculateCarryingWeight() {
        return getCalculator().calculateCarryingWeight();
    }

    @Override
    public UnitInitializer getInitializer() {
        return super.getInitializer();
    }

    public void initSpells(boolean reset) {
        getInitializer().initSpells(reset);
    }

    public void initSpellbook() {
        getInitializer().initSpellbook();
    }


    public void initSkills() {
        getInitializer().initSkills();
    }

    public void initAttributes() {
        getInitializer().initAttributes();
    }

    public void initMasteries() {
        getInitializer().initMasteries();
    }


    public void initNaturalWeapon(boolean offhand) {
        getInitializer().initNaturalWeapon(offhand);
    }


    public void initIntegrityAlignments() {
        getInitializer().initIntegrityAlignments();
    }


    public boolean canUseItems() {
        return getChecker().canUseItems();
    }

    public Unit getEntity() {
        return getChecker().getEntity();
    }

    public boolean canUseArmor() {
        return getChecker().canUseArmor();
    }

    public boolean canUseWeapons() {
        return getChecker().canUseWeapons();
    }

    public boolean checkDualWielding() {
        return getChecker().checkDualWielding();
    }

    public boolean isImmortalityOn() {
        return getChecker().isImmortalityOn();
    }

    public boolean isConstructAlways() {
        return true;
    }

    @Override
    public void resetFacing() {
//   TODO bugged? used to work
//     getResetter().resetFacing();
    }

    public DequeImpl<DC_JewelryObj> getRings() {
        DequeImpl<DC_JewelryObj> list = new DequeImpl<>(getJewelry());
        for (DC_JewelryObj j : getJewelry()) {
            if (j.isAmulet()) {
                list.remove(j);
            }
        }
        return list;
    }

    public DC_JewelryObj getAmulet() {
        for (DC_JewelryObj j : getJewelry()) {
            if (j.isAmulet()) {
                return j;
            }
        }
        return null;
    }

    @Override
    public void setConstructed(boolean b) {
        super.setConstructed(b); //TODO set for all?
    }

    public DC_Obj getLinkedObj(IdKey key) {
        return new KeyResolver().getObj(key, this);
    }

    public DC_WeaponObj getRangedWeapon() {
        return rangedWeapon;
    }

    public void setRangedWeapon(DC_WeaponObj rangedWeapon) {
        this.rangedWeapon = rangedWeapon;
    }

    @Override
    public String toString() {
        String prefix = "";
        if (getOwner() != DC_Player.NEUTRAL) {
            if (isAiControlled())
                prefix = isMine() ? "Allied " : "Enemy ";
        }
        if (isDead()) {
            prefix += "(Dead) ";
        }
        return prefix + getName() + (game.isDebugMode() ? " at " + getCoordinates()
         : "");
    }

    public AI_TYPE getAiType() {
        if (getAI() != null)
            return getAI().getType();
        AI_TYPE ai = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class,
         getProperty(PROPS.AI_TYPE));
        if (ai == null) {
            return AiEnums.AI_TYPE.NORMAL;
        }
        return ai;
    }

    public int getSightRangeTowards(DC_Obj target) {
        return getSightRangeTowards(target.getCoordinates());
    }

    public int getSightRangeTowards(Coordinates coordinates) {
        int sight = getIntParam(PARAMS.SIGHT_RANGE);
        FACING_SINGLE singleFacing = FacingMaster.getSingleFacing(this.getFacing(), this.getCoordinates(),
         coordinates);
        if (singleFacing == UnitEnums.FACING_SINGLE.BEHIND) {
            sight = getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (singleFacing == UnitEnums.FACING_SINGLE.TO_THE_SIDE) {
            sight -= getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
        }
        return sight;
    }

    public boolean isUsingStealth() {
        return usingStealth;
    }

    public void setUsingStealth(boolean usingStealth) {
        this.usingStealth = usingStealth;
    }

    @Override
    public void toBase() {
        if (getAI().isOutsideCombat()) {
            return;
        }
        super.toBase();
    }


    public DC_ActiveObj getTurnAction(boolean clockwise) {
        return getAction(
         clockwise
          ? STD_ACTIONS.Turn_Clockwise.toString()
          : STD_ACTIONS.Turn_Anticlockwise.toString());
    }

    public DequeImpl<Perk> getPerks() {
        return perks;
    }

    public void setPerks(DequeImpl<Perk> perks) {
        this.perks = perks;
    }
}
