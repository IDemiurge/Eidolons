package eidolons.entity.unit;

import eidolons.content.*;
import eidolons.content.values.ValuePages;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.Spell;
import eidolons.entity.active.spaces.Feat;
import eidolons.entity.active.spaces.FeatSpaces;
import eidolons.entity.handlers.bf.unit.*;
import eidolons.entity.item.trinket.JewelryItem;
import eidolons.netherflame.eidolon.heromake.model.DC_Attributes;
import eidolons.netherflame.eidolon.heromake.model.DC_Masteries;
import eidolons.entity.item.*;
import eidolons.entity.item.trinket.garment.Garment;
import eidolons.entity.item.trinket.garment.HeadGarment;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.handlers.KeyResolver;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.attach.ClassRank;
import eidolons.entity.unit.attach.Perk;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.PlayerManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.handlers.ExplorationMaster;
import eidolons.netherflame.lord.EidolonLord;
import eidolons.system.test.Debugger;
import eidolons.system.test.TestMasterContent;
import eidolons.system.utils.content.ContentGenerator;
import main.ability.AbilityObj;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.mode.STD_MODES;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.Player;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.*;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.math.MathMaster;

import java.util.*;
import java.util.stream.Collectors;

import static eidolons.content.consts.VisualEnums.CONTAINER;
import static eidolons.content.consts.VisualEnums.INTENT_ICON;

public class Unit extends UnitModel implements GridEntity {
    protected WeaponItem offhandNaturalWeapon;
    protected WeaponItem naturalWeapon;
    protected WeaponItem weapon;
    protected WeaponItem secondWeapon;
    protected WeaponItem reserveMainWeapon;
    protected WeaponItem reserveOffhandWeapon;

    protected ArmorItem armor;
    protected ArmorItem innerArmor;
    protected HeadGarment headGarment;
    protected Garment garment;

    protected DequeImpl<DC_PassiveObj> skills;
    protected DequeImpl<ClassRank> classes;
    protected DequeImpl<Perk> perks;
    protected DequeImpl<JewelryItem> jewelry;
    protected DequeImpl<HeroItem> inventory;

    protected DC_Masteries masteries;
    protected DC_Attributes attrs;

    protected FeatSpaces spellSpaces;
    protected FeatSpaces combatSpaces;

    protected boolean initialized;
    protected boolean itemsInitialized;
    protected boolean aiControlled;
    protected boolean usingStealth;
    private boolean actorLinked; //for dialogue
    protected Boolean mainHero;

    protected GENDER gender;
    protected FLIP flip;
    protected ObjType backgroundType;
    protected Unit engagementTarget;
    protected WeaponItem rangedWeapon;
    private DC_ActiveObj lastAction;

    public Unit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        if (isHero()) { //&& !(this instanceof HeroDataModel)
            Core.setMainHero(this);
            String message = this + " hero created " + getId();
            // if (ScreenLoader.isInitRunning()) {
            //     if (Eidolons.MAIN_HERO != null) {
            //         message += " SECOND TIME!...";
            //         Eidolons.MAIN_HERO.removeFromGame();
            //         addProperty(true, PROPS.INVENTORY, "Jade Key");
            //     }
            // }
            SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);

        }
        //TODO make it more apparent why and how this is done
        //cleanRef();
        ref.removeValue(KEYS.TARGET); //if summoned?
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
        this(type, 0, 0, PlayerManager.getDefaultPlayer(), game, new Ref(game));
    }


    @Override
    protected EntityMaster initMaster() {
        return new UnitMaster(this);
    }

    @Override
    public void init() {
        super.init();
        initEmblem();
        getInitializer().init();

    }

    @Override
    public String getDisplayedName() {
        return super.getDisplayedName();
    }

    @Override
    public UnitHandler getHandler() {
        return (UnitHandler) super.getHandler();
    }

    @Override
    public String getDescription() {
        if (isHero()) {
            return DescriptionMaster.getDescription(this);
        }
        return super.getDescription();
    }


    @Override
    public void reset() {
        super.reset();
        checkRemoveQuickItems();
    }

    @Override
    protected void putParameter(PARAMETER param, String value) {
        if (getName().contains("Leviathan")) {
            if (param == PARAMS.C_TOUGHNESS) {
                LogMaster.devLog("Leviathan set C_TOUGHNESS" + value);
            }
            if (param == PARAMS.C_ENDURANCE) {
                LogMaster.devLog("Leviathan set C_ENDURANCE" + value);
            }
        }

        if (Flags.isAutoFixOn())
            if (isPlayerCharacter()) {
                Integer integer = NumberUtils.getIntParse(value);
                if (param.getName().contains("Percentage")) {
                    if (integer < 0) {
                        return;
                    }
                    if (integer > MathMaster.MAX_REASONABLE_PERCENTAGE) {
                        return;
                    }
                }
                if (param.isDynamic()) {
                    if (Debugger.isImmortalityOn())
                        if (param == PARAMS.C_ENDURANCE || param == PARAMS.C_TOUGHNESS)
                            if (integer <= 0) {
                                return;
                            }
                }
            }
        if (isValidMapStored(param))
            if (!getGame().getState().getManager().isResetting())
                //this is gross!
                if (!isBeingReset()) {
                    getValidParams().put(param, NumberUtils.getIntParse(value));
                }

        super.putParameter(param, value);
    }

    public String getDynamicInfo() {
        StringBuilder info = new StringBuilder();
        for (VALUE V : ValuePages.UNIT_DYNAMIC_PARAMETERS) {
            info.append(V.getName()).append(" = ").append(getValue(V));
        }
        return info.toString();
    }

    public String getParamInfo() {
        StringBuilder info = new StringBuilder();
        for (VALUE V : ValuePages.UNIT_PARAMETERS) {
            info.append(V.getName()).append(" = ").append(getValue(V));
        }
        return info.toString();
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
        getEntity().setBeingReset(false);

        if (getName().contains("Leviathan")) {
            LogMaster.devLog("Leviathan C_TOUGHNESS " + getIntParam(PARAMS.C_TOUGHNESS));
            LogMaster.devLog("Leviathan C_ENDURANCE " + getIntParam(PARAMS.C_ENDURANCE));
        }
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
        for (QuickItem q : getQuickItems()) {
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

    public void setWeapon(WeaponItem weapon) {
        this.weapon = weapon;
        if (weapon != null) {
            weapon.setMainHand(true);
        } else {
            ref.setID(KEYS.WEAPON, null);
        }
        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (weapon != null) {
                id = weapon.getId() + "";
            }
            setProperty(G_PROPS.MAIN_HAND_ITEM, id);
        }
    }

    public DC_PassiveObj getFeat(ObjType type) {
        return getFeat(type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS, type);
    }

    public DC_PassiveObj getFeat(boolean skill, ObjType type) {
        for (DC_PassiveObj feat : getSkills()) {
            if (feat.getName().equalsIgnoreCase(type.getName())) {
                return feat;
            }
        }
        return null;// TODO
    }


    public void setNaturalWeapon(boolean offhand, WeaponItem weapon) {
        if (offhand) {
            offhandNaturalWeapon = weapon;
        } else {
            naturalWeapon = weapon;
        }
    }

    public WeaponItem getNaturalWeapon() {
        return getNaturalWeapon(false);
    }

    public WeaponItem getOffhandNaturalWeapon() {
        return getNaturalWeapon(true);
    }

    public WeaponItem getNaturalWeapon(boolean offhand) {
        WeaponItem weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        if (weaponObj == null) {
            initNaturalWeapon(offhand);
        }
        weaponObj = (!offhand) ? naturalWeapon : offhandNaturalWeapon;
        return weaponObj;
    }

    public boolean removeJewelryItem(HeroItem itemObj) {
        boolean result = getJewelry().remove(itemObj);
        if (getJewelry().isEmpty()) {
            setJewelry(null);
        }
        return result;
    }

    public void addQuickItem(QuickItem itemObj) {
        //TODO Spaces
    }

    public boolean removeQuickItem(HeroItem itemObj) {
        //TODO Spaces
        return false;
    }

    @Override
    public void newRound() {
        spellSpaces.newRound();
        // if (!itemsInitialized)
        // initItems();
        super.newRound();
    }

    public WeaponItem getMainWeapon() {
        return weapon;
    }

    public ArmorItem getArmor(boolean inner) {
        return inner ? innerArmor : armor;
    }

    public ArmorItem getArmor() {
        if (armor == null) {
            return innerArmor;
        }
        return armor;
    }

    public ArmorItem getInnerArmor() {
        return innerArmor;
    }

    public Garment getGarment() {
        return garment;
    }

    public HeadGarment getHeadwear() {
        return headGarment;
    }

    public void setArmor(ArmorItem armor) {
        this.armor = armor;
        if (armor == null) {
            ref.setID(KEYS.ARMOR, null);
        }
        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (armor != null) {
                id = armor.getId() + "";
            }
            setProperty(G_PROPS.ARMOR_ITEM, id);
        }
    }

    public List<Spell> getSpells() {
        //TODO Spaces
        return null;
    }

    public FeatSpaces getSpellSpaces() {
        return spellSpaces;
    }

    public void setSpellSpaces(FeatSpaces spellSpaces) {
        this.spellSpaces = spellSpaces;
    }

    public FeatSpaces getCombatSpaces() {
        return combatSpaces;
    }

    public void setCombatSpaces(FeatSpaces combatSpaces) {
        this.combatSpaces = combatSpaces;
    }

    public WeaponItem getOffhandWeapon() {
        return secondWeapon;
    }

    public void setSecondWeapon(WeaponItem secondWeapon) {
        this.secondWeapon = secondWeapon;
        if (secondWeapon != null) {
            secondWeapon.setMainHand(false);
        } else {
            ref.setID(KEYS.WEAPON, null);
        }

        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (secondWeapon != null) {
                id = secondWeapon.getId() + "";
            }
            setProperty(G_PROPS.OFF_HAND_ITEM, id);
        }
    }

    public void addFeat(DC_PassiveObj e) {
        if (e.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS) {
            getSkills().add(e);
        } else {
            getClasses().addCast(e);
        }
    }

    public DequeImpl<DC_PassiveObj> getSkills() {
        return skills;
    }

    public void setSkills(DequeImpl<DC_PassiveObj> skills) {
        this.skills = skills;
    }

    public boolean isInventoryFull() {
        if (getInventory() == null) {
            return false;
        }
        return getInventory().size() >= DC_CONSTS.MAX_INV_ITEMS;
    }

    public boolean isQuickSlotsFull() {
        //TODO Spaces
        return getRemainingQuickSlots() <= 0;
    }

    public int getQuickSlotsMax() {
        //TODO Spaces
        return 0;
    }

    public int getRemainingQuickSlots() {
        //TODO Spaces
        return 0;
    }

    public int getOccupiedQuickSlots() {
            return 0;
    }

    public DequeImpl<QuickItem> getQuickItems() {
        //TODO Spaces
        return null ;
    }

    public HeroItem getItemFromInventory(String name) {
        return new SearchMaster<HeroItem>().find(name, getInventory());
    }

    public QuickItem getQuickItem(String name) {
        return new SearchMaster<QuickItem>().find(name, getQuickItems());
    }

    public Entity getItem(String name) {
        // for (String generic: getInventory())
        List<Entity> list = new ArrayList<>();
        list.add(getWeapon(true));
        list.add(getWeapon(false));
        list.add(getNaturalWeapon(true));
        list.add(getNaturalWeapon(false));
        list.addAll(getInventory());
        list.addAll(getQuickItems());
        list.addAll(getJewelry());
        return new SearchMaster<Entity>().find(name, list);
    }

    public DequeImpl<HeroItem> getInventory() {
        //        if (!isItemsInitialized()) {
        if (inventory == null) {
            inventory = new DequeImpl<>();
        }
        return inventory;
    }

    public void setInventory(DequeImpl<HeroItem> inventory) {
        if (inventory == null) {
            LogMaster.log(1, "Inventory nullified  " + this);
        }
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
        return getBaseClassNumber() >= DC_Formulas.MAX_BASE_CLASSES;
    }

    protected int getBaseClassNumber() {
        return getContainerSize(PROPS.CLASSES);
    }

    protected int getContainerSize(PROPS prop) { // class-upgrade logic
        // deprecated?
        return ContainerUtils.openContainer(getProperty(prop)).size();
    }

    public DequeImpl<ClassRank> getClasses() {
        return classes;
    }

    public void setClasses(DequeImpl<ClassRank> classes) {
        this.classes = classes;
    }

    public WeaponItem getActiveWeapon(boolean offhand) {
        WeaponItem weapon = getWeapon(offhand);

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

    public WeaponItem getWeapon(boolean offhand) {
        return (offhand) ? getOffhandWeapon() : getMainWeapon();
    }

    public boolean equip(HeroItem item, ITEM_SLOT slot) {
        if (item == null) {
            return false;
        }
        HeroItem prevItem = getItem(slot);
        setItem(item, slot);
        item.equipped(ref);

        if (prevItem != null) {
            addItemToInventory(prevItem);
        }
        //TODO gdx events
        // if (game.isStarted())
        //     if (!CoreEngine.isGraphicsOff())
        //         if (!ExplorationMaster.isExplorationOn()) //only in combat!
        //             if (item instanceof DC_WeaponObj)
        //                 Atlases.preloadAtlas((DC_WeaponObj) item);
        // preCheck weight and prompt drop if too heavy?
        item.setContainer(CONTAINER.EQUIPPED);
        return true;
    }

    public boolean addItemToInventory(HeroItem item) {
        return addItemToInventory(item, false);
    }

    public boolean addItemToInventory(HeroItem item, boolean quiet) {
        if (isInventoryFull())
            return false;
        if (getInventory() == null) { //TODO EA check - INV system is loose?
            setProperty(PROPS.INVENTORY, getType().getProperty(getProperty(PROPS.INVENTORY)));
            itemsInitialized = false;
            inventory = (DequeImpl<HeroItem>) getInitializer().
                    initContainedItems(PROPS.INVENTORY, null, false);
        }
        try {
            getInventory().add(item);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
            return false;
        }
        item.setRef(getRef());
        item.setContainer(CONTAINER.INVENTORY);
        if (isPlayerCharacter())
            if (!quiet) {
                //            main.system.auxiliary.log.LogMaster.log(1," " );
                getGame().getLogManager().log(getName() + " receives item: " + item.getName());
                EUtils.showInfoText("Received: " + item.getName());
            }
        return true;
        // EVENT,
    }

    @Override
    public void applyType(ObjType type) {
        setItemsInitialized(false);
        //TODO need to copy all dynamic params!
        // or implement a real buffer copy
        type.copyValues(this,
                Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT).map(
                        ContentValsManager::getPercentageParam).
                        collect(Collectors.toList()));
        type.copyValues(this, Arrays.asList(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE_CURRENT));
        super.applyType(type);
    }

    public void setItem(HeroItem item, ITEM_SLOT slot) {
        if (item instanceof QuickItem) {
            if (((QuickItem) item).getWrappedWeapon() != null) {
                item = ((QuickItem) item).getWrappedWeapon();
            }
        }
        if (item != null) {
            if (inventory != null) //ToDo-Cleanup
                if (inventory.contains(item) ||
                        item.getContainer() == CONTAINER.INVENTORY) {
                    removeFromInventory(item);
                }
        }
        switch (slot) {
            case ARMOR:
                setArmor((ArmorItem) item);
                break;
            case MAIN_HAND:
                setWeapon((WeaponItem) item);
                break;
            case OFF_HAND:
                setSecondWeapon((WeaponItem) item);
                break;
            case RESERVE_MAIN_HAND:
                setReserveMainWeapon((WeaponItem) item);
                break;
            case RESERVE_OFF_HAND:
                setReserveOffhandWeapon((WeaponItem) item);
                break;
        }
        if (item != null) {
            if (slot == ITEM_SLOT.RESERVE_MAIN_HAND || slot == ITEM_SLOT.RESERVE_OFF_HAND) {
                //               TODO  item.equipReserve(ref);
            } else
                item.equipped(ref);
        }
    }

    public HeroItem getItem(ITEM_SLOT slot) {
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

    public boolean dropItemFromInventory(HeroItem item, Coordinates c) {
        removeFromInventory(item);
        if (!isSimulation()) //sim just remembers for real hero to drop via operation
        {
            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                    type.getName() + " is dropped down!");
            getGame().getDroppedItemManager().drop(item, c);
        }

        return true;
    }

    public boolean dropItemFromInventory(HeroItem item) {
        return dropItemFromInventory(item, getCoordinates());
    }

    public boolean removeFromInventory(HeroItem item) {
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
        for (HeroItem item : getQuickItems()) {
            game.getState().addObject(item);
        }
        for (HeroItem item : getInventory()) {
            game.getState().addObject(item);
        }
    }

    public BACKGROUND getBackground() {
        BACKGROUND background = new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class,
                getProperty(G_PROPS.BACKGROUND));
        if (getGender() != null) {
            return getGender() == GENDER.FEMALE
                    ? background.getFemale()
                    : background.getMale();
        }
        return background;
    }

    public DequeImpl<JewelryItem> getJewelry() {
        if (jewelry == null) {
            jewelry = new DequeImpl<>();
        }
        return jewelry;
    }

    public void setJewelry(DequeImpl<JewelryItem> jewelry) {
        this.jewelry = jewelry;
    }

    public void addJewelryItem(JewelryItem item) {
        getJewelry().add(item);
        item.setRef(ref);
    }

    public HeroItem unequip(ITEM_SLOT slot) {
        return unequip(slot, false);
    }

    public HeroItem unequip(ITEM_SLOT slot, Boolean drop) {
        HeroItem item = null;
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
            case RESERVE_MAIN_HAND:
                item = getReserveMainWeapon();
                setReserveMainWeapon(null);
                break;
            case RESERVE_OFF_HAND:
                item = getReserveOffhandWeapon();
                setReserveOffhandWeapon(null);
                break;
        }
        if (item instanceof WeaponItem) {
            if (((WeaponItem) item).isRanged()) {
                setRangedWeapon(null);
            }
        }
        if (item == null) {
            return item;
        }
        if (drop != null) {
            addItemToInventory(item);
            if (drop) {
                dropItemFromInventory(item);
            }
        }
        item.unequip();

        return item;
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

    public void unequip(HeroItem item, Boolean drop) {
        if (getWeapon(false) == item) {
            unequip(ITEM_SLOT.MAIN_HAND, drop);
            return;
        } else if (getWeapon(true) == item) {
            unequip(ITEM_SLOT.OFF_HAND, drop);
            return;
        } else if (getArmor() == item) {
            unequip(ITEM_SLOT.ARMOR, drop);
            return;
        } else if (getReserveOffhandWeapon() == item) {
            unequip(ITEM_SLOT.RESERVE_OFF_HAND, drop);
            return;
        } else if (getReserveMainWeapon() == item) {
            unequip(ITEM_SLOT.RESERVE_MAIN_HAND, drop);
            return;
        }
        boolean result = removeJewelryItem(item);
        if (!result)
            if (item instanceof QuickItem)
                result = removeQuickItem(item);
        if (result) {
            boolean full = !addItemToInventory(item, true);
            if (full || drop) {
                dropItemFromInventory(item);
            }
        }

    }

    private void checkRemoveQuickItems() {
        int n = getIntParam(PARAMS.QUICK_SLOTS);
        int size = getQuickItems().size();
        if (size <= n)
            return;
        List<QuickItem> toRemove = new ArrayList<>(getQuickItems()).subList(n - 1, size - 1);
        for (QuickItem q : toRemove) {
            unequip(q, false);
        }
    }

    @Override
    public void setDead(boolean dead) {
        super.setDead(dead);
    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
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
        return super.kill(killer, leaveCorpse, quietly);
    }

    public List<AbilityObj> getPassivesFiltered() {
        return null;
    }

    public List<HeroSlotItem> getReserveItems() {
        ListMaster<HeroSlotItem> listMaster = new ListMaster<>();
        return listMaster.removeNulls(listMaster.getList(reserveMainWeapon, reserveOffhandWeapon));
    }

    public List<HeroSlotItem> getSlotItems() {
        ListMaster<HeroSlotItem> listMaster = new ListMaster<>();
        return listMaster.removeNulls(listMaster.getList(weapon, armor, secondWeapon));
    }

    public boolean hasBroadReach() {
        return hasWeaponPassive(null, STANDARD_PASSIVES.BROAD_REACH);
    }

    public boolean hasHindReach() {
        return hasWeaponPassive(null, STANDARD_PASSIVES.HIND_REACH);
    }

    public boolean hasWeaponPassive(Boolean offhand, STANDARD_PASSIVES passive) {
        if (checkPassive(passive)) {
            return true;
        }
        if (offhand != null) {
            return (getActiveWeapon(offhand).checkPassive(passive));
        }
        WeaponItem activeWeapon = getActiveWeapon(true);
        if (activeWeapon != null) {
            if (activeWeapon.checkPassive(passive)) {
                return true;
            }
        }
        activeWeapon = getActiveWeapon(false);
        if (activeWeapon != null) {
            return activeWeapon.checkPassive(passive);
        }

        return false;
    }

    public boolean checkAiMod(AI_MODIFIERS aiMod) {
        return getChecker().checkAiMod(aiMod);

    }


    public Spell getSpell(String actionName) {
        for (Spell s : getSpells()) {
            if (StringMaster.compareByChar(s.getName(), actionName, true)) {
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

    public GENDER getGender() {
        if (gender == null || isSimulation()) {
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
        if (Core.getMainHero() == this)
            return true;
        if (mainHero == null)
            mainHero = owner.getHeroObj() == this;
        return mainHero;
    }

    public void setMainHero(boolean mainHero) {
        this.mainHero = mainHero;
    }

    public boolean isRevenant() {
        if (isPlayerCharacter()) {
            return Flags.isIggDemoRunning();
        }
        return false;
    }

    public boolean isPlayerCharacter() {
        return Core.getMainHero() == this;
    }


    public boolean isHostileTo(DC_Player player) {
        if (getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
            return true;
        }
        return isEnemyTo(player);
    }

    public HeroItem findItemAnywhere(String typeName) {
        HeroItem item = findItem(typeName, false);
        if (item == null) {
            item = findItem(typeName, true);
        }
        if (item == null) {
            item = findItem(typeName, null);
        }
        return item;
    }

    //TODO to Handler
    public boolean removeItemsFromAnywhere(String name, int n) {
        for (int i = 0; i < n; i++) {
            HeroItem item = findItemAnywhere(name);
            if (!removeFromInventory(item))
                if (!removeJewelryItem(item))
                    if (!removeQuickItem(item)) {
                        unequip(item, false);
                        if (!removeFromInventory(item))
                            return false;
                    }
        }
        return true;
    }

    public HeroItem findItem(String typeName, Boolean quick_inv_slot) {
        if (quick_inv_slot == null) {
            return new ListMaster<HeroSlotItem>().findType(typeName, new ArrayList<>(
                    getSlotItems()));
        }
        return !quick_inv_slot ? new ListMaster<HeroItem>().findType(typeName,
                new ArrayList<>(getInventory())) : new ListMaster<QuickItem>().findType(
                typeName, new ArrayList<>(getQuickItems()));
    }

    public void setCoordinates(Coordinates coordinates) {
        setCoordinates(coordinates, false);
    }

    public void setCoordinates(Coordinates coordinates, boolean force) {
        if (coordinates.isInvalid()) {
            coordinates = CoordinatesMaster.getClosestValid(coordinates);
            super.setCoordinates(coordinates);
            return;
        }
        // if (!force)
        //     if (isPlayerCharacter()) {
        //         if (!getCoordinates().equals(coordinates)) {
        //             if (AI_Manager.isRunning()) {
        //                 return;
        //             }
        //             if (getGame().getLoop().getActiveUnit() != this) {
        //                 return;
        //             }
        //         }
        //         //                if (getCoordinates().dst_(coordinates) >= 2) {
        //         //                    if (game.isStarted())
        //         //                        if (!originalCoordinates.equals(coordinates)) {
        //         //                            LogMaster.log(1, "Teleport bug? ");
        //         ////                            return;
        //         //                        }
        //         //                }
        //     }
        super.setCoordinates(coordinates);
    }

    public DC_ActiveObj getAttackAction(boolean offhand) {

        return getAction(offhand ? ActionEnums.OFFHAND_ATTACK :
                ActionEnums.ATTACK);
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

    public DequeImpl<JewelryItem> getRings() {
        DequeImpl<JewelryItem> list = new DequeImpl<>(getJewelry());
        for (JewelryItem j : getJewelry()) {
            if (j.isAmulet()) {
                list.remove(j);
            }
        }
        return list;
    }

    public JewelryItem getAmulet() {
        for (JewelryItem j : getJewelry()) {
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

    public WeaponItem getRangedWeapon() {
        return rangedWeapon;
    }

    public void setRangedWeapon(WeaponItem rangedWeapon) {
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
        return prefix + getName() + (Flags.isIDE() || AI_Manager.isRunning() ? " at " + getCoordinates()
                : "");
    }

    public AI_TYPE getAiType() {
        if (getAI() != null)
            return getAI().getType();
        AI_TYPE ai = new EnumMaster<AI_TYPE>().retrieveEnumConst(AI_TYPE.class,
                getProperty(PROPS.AI_TYPE));
        if (ai == null) {
            return AI_TYPE.NORMAL;
        }
        return ai;
    }

    public int getMaxVisionDistanceTowards(Coordinates c) {
        return getSightRange() * 2 + 1;
    }

    public int getSightRange() {
        int sight = getIntParam(PARAMS.SIGHT_RANGE);
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
        removeTempCoordinates();
        if (!CoreEngine.isLevelEditor())
            if (getAI().isOutsideCombat()) {
                return;
            }
        if (isMine()) {
            if (isHero()) {
                if (EidolonLord.lord != null) {
                    setParam(PARAMS.C_SOULFORCE, EidolonLord.lord.getSoulforce());
                }
            }
            if (Flags.isActiveTestMode()) {
                TestMasterContent.addVFX_TEST_Spells(getType(), ContentGenerator.getTestSpellFilter(getName()));

            }
            if (Flags.isAnimationTestMode()) {
                TestMasterContent.addANIM_TEST_Spells(getType());
            }
            if (Flags.isLogicTest())
                TestMasterContent.addTestGroupSpells(getType());

            if (Flags.isGuiTestMode()) {
                TestMasterContent.addGRAPHICS_TEST_Spells(getType());
            }
        }
        super.toBase();
    }


    public DC_ActiveObj getTurnAction(boolean clockwise) {
        return getAction(
                clockwise
                        ? ActionEnums.STD_ACTIONS.Turn_Clockwise.toString()
                        : ActionEnums.STD_ACTIONS.Turn_Anticlockwise.toString());
    }

    public DequeImpl<Perk> getPerks() {
        return perks;
    }

    public void setPerks(DequeImpl<Perk> perks) {
        this.perks = perks;
    }

    public List<DC_ActiveObj> getActions() {
        ArrayList<ActiveObj> list = new ArrayList<>(getActives());
        list.removeIf(activeObj -> !(activeObj instanceof DC_ActiveObj));
        return new ArrayList<>(new DequeImpl<DC_ActiveObj>().addAllCast(list));
    }

    public List<DC_ActiveObj> getMoveActions() {
        return getActions().stream().filter(a -> a.getActionGroup() == ACTION_TYPE_GROUPS.MOVE).collect(Collectors.toList());
    }

    public int getGold() {
        return getIntParam(PARAMS.GOLD);
    }

    public void applyBuffRules() {
        getResetter().applyBuffRules();
    }

    public WeaponItem getReserveMainWeapon() {
        return reserveMainWeapon;
    }

    public WeaponItem getReserveOffhandWeapon() {
        return reserveOffhandWeapon;
    }


    public void setReserveMainWeapon(WeaponItem reserveMainWeapon) {
        this.reserveMainWeapon = reserveMainWeapon;
        if (reserveMainWeapon != null) {
            reserveMainWeapon.setMainHand(true);
        } else {
            ref.setID(KEYS.RESERVE_WEAPON, null);
        }
        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (reserveMainWeapon != null) {
                id = reserveMainWeapon.getId() + "";
            }
            setProperty(G_PROPS.RESERVE_MAIN_HAND_ITEM, id);
        }
    }

    public void setReserveOffhandWeapon(WeaponItem reserveOffhandWeapon) {

        this.reserveOffhandWeapon = reserveOffhandWeapon;
        if (reserveOffhandWeapon != null) {
            reserveOffhandWeapon.setMainHand(false);
        } else {
            ref.setID(KEYS.RESERVE_OFFHAND_WEAPON, null);
        }
        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (reserveOffhandWeapon != null) {
                id = reserveOffhandWeapon.getId() + "";
            }
            setProperty(G_PROPS.RESERVE_OFF_HAND_ITEM, id);
        }
    }

    public HeroItem getReserveWeapon(boolean offhand) {
        return !offhand ? getReserveMainWeapon() : getReserveOffhandWeapon();
    }

    public DC_ActiveObj getLastAction() {
        return lastAction;
    }

    public void setLastAction(DC_ActiveObj lastAction) {
        this.lastAction = lastAction;
    }

    public boolean isNamedUnit() {
        return checkClassification(UnitEnums.CLASSIFICATIONS.UNIQUE) || checkBool(GenericEnums.STD_BOOLS.NAMED);
    }

    protected boolean isModifierMapOn() {
        return isPlayerCharacter();
    }

    //    public UnitEnums.UNIT_GROUP getUnitGroup() {
    //        return  new EnumMaster<UnitEnums.UNIT_GROUP>().retrieveEnumConst(UnitEnums.UNIT_GROUP.class, getProperty(G_PROPS.UNIT_GROUP));
    //    }
    public UnitEnums.UNIT_GROUPS getUnitGroup() {
        return new EnumMaster<UnitEnums.UNIT_GROUPS>().
                retrieveEnumConst(UnitEnums.UNIT_GROUPS.class, getProperty(G_PROPS.UNIT_GROUP));
    }

    public boolean isActorLinked() {
        return actorLinked;
    }

    public void setActorLinked(boolean actorLinked) {
        this.actorLinked = actorLinked;
    }

    public INTENT_ICON getIntentIcon() {
        if (getMode() != null && getMode() != STD_MODES.NORMAL) {
            return INTENT_ICON.getModeIcon(getMode());
        }
        if (isPlayerCharacter()) {
            return INTENT_ICON.WAIT;
        }
        if (isBoss()) {
            return INTENT_ICON.WHEEL;
        }

        return null;
    }

    public void setUnconscious(boolean b) {
        unconscious = b;
    }

    public void cleanReset() {
        removeAllBuffs();
        setMode(STD_MODES.NORMAL);
        reset();
        resetDynamicParam(PARAMS.C_TOUGHNESS);
        resetDynamicParam(PARAMS.C_FOCUS);
        resetDynamicParam(PARAMS.C_ESSENCE);
    }

    private void removeAllBuffs() {
        for (BuffObj buff : new LinkedList<>(getBuffs())) {
            buff.remove();
        }
    }

    public List<Feat> getActiveFeats() {
        List<Feat> feats = new ArrayList<>();
        feats.addAll(spellSpaces.getCurrent().getFeats());
        feats.addAll(combatSpaces.getCurrent().getFeats());
        return feats;
    }

    public boolean canInstantAttack() {
        return getChecker().canInstantAttack();
    }

}
