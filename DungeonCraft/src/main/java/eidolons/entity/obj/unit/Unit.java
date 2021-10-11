package eidolons.entity.obj.unit;

import eidolons.content.*;
import eidolons.content.values.ValuePages;
import eidolons.entity.Deity;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.active.Spell;
import eidolons.entity.active.spaces.UnitActiveSpaces;
import eidolons.entity.handlers.bf.unit.*;
import eidolons.entity.item.*;
import eidolons.entity.item.garment.Garment;
import eidolons.entity.item.garment.HeadGarment;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.KeyResolver;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.attach.HeroClass;
import eidolons.entity.obj.attach.Perk;
import eidolons.entity.hero.DC_Attributes;
import eidolons.entity.hero.DC_Masteries;
import eidolons.game.battlecraft.ai.AI_Manager;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.logic.mission.universal.PlayerManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.game.netherflame.lord.EidolonLord;
import eidolons.content.DC_Formulas;
import eidolons.system.utils.content.ContentGenerator;
import eidolons.system.test.Debugger;
import eidolons.system.test.TestMasterContent;
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
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.AI_TYPE;
import main.content.enums.system.MetaEnums;
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
import main.game.bf.directions.FACING_DIRECTION;
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

import static eidolons.content.consts.VisualEnums.*;

public class Unit extends DC_UnitModel implements FacingEntity {
    protected DC_WeaponObj offhandNaturalWeapon;
    protected DC_WeaponObj naturalWeapon;
    protected DC_WeaponObj weapon;
    protected DC_WeaponObj secondWeapon;
    protected DC_WeaponObj reserveMainWeapon;
    protected DC_WeaponObj reserveOffhandWeapon;

    protected DC_ArmorObj armor;
    protected DC_ArmorObj innerArmor;
    protected HeadGarment headGarment;
    protected Garment garment;

    protected DequeImpl<DC_FeatObj> skills;
    protected DequeImpl<HeroClass> classes;
    protected DequeImpl<Perk> perks;
    protected DequeImpl<DC_QuickItemObj> quickItems;
    protected DequeImpl<DC_JewelryObj> jewelry;
    protected DequeImpl<DC_HeroItemObj> inventory;

    protected DC_Masteries masteries;
    protected DC_Attributes attrs;

    protected List<Spell> spells;
    protected UnitActiveSpaces activeSpaces;
    protected boolean initialized;
    protected List<Spell> spellbook;
    protected boolean itemsInitialized;
    protected boolean aiControlled;
    protected Boolean mainHero;
    protected GENDER gender;
    protected FLIP flip;
    protected ObjType backgroundType;
    protected Map<DC_ActiveObj, String> actionModeMap;
    protected Unit engagementTarget;
    protected DC_WeaponObj rangedWeapon;
    protected boolean leader;
    protected boolean usingStealth;
    private DC_ActiveObj lastAction;
    private boolean shadow;
    private boolean actorLinked; //for dialogue
    private boolean movePointsOn = true;
    private boolean extraAtkPointsOn;
    private int freeMovesDone;
    private List<Deity> nemesis;
    private List<Deity> patron;

    public Unit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        if (isHero()) { //&& !(this instanceof HeroDataModel)
            String message = this + " hero created " + getId();
            // if (ScreenLoader.isInitRunning()) {
            //     if (Eidolons.MAIN_HERO != null) {
            //         message += " SECOND TIME!...";
            //         Eidolons.MAIN_HERO.removeFromGame();
            //         addProperty(true, PROPS.INVENTORY, "Jade Key");
            //     }
            // }
            SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);

        } else {
            try {
                setFacing(FacingMaster.getRandomFacing());
            } catch (Exception e) {
                ExceptionMaster.printStackTrace(e);
            }
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
        initDeity();
        initEmblem();
        initIntegrityAlignments();
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
                    if (Debugger.isImmortalityOn() )
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
        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (weapon != null) {
                id = weapon.getId() + "";
            }
            setProperty(G_PROPS.MAIN_HAND_ITEM, id);
        }
    }

    public DC_FeatObj getFeat(ObjType type) {
        return getFeat(type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS, type);
    }

    public DC_FeatObj getFeat(boolean skill, ObjType type) {
        for (DC_FeatObj feat : getSkills()) {
            if (feat.getName().equalsIgnoreCase(type.getName())) {
                return feat;
            }
        }
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
        itemObj.setContainer(CONTAINER.QUICK_SLOTS);
    }

    public boolean removeQuickItem(DC_HeroItemObj itemObj) {
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
    public void newRound() {
        activeSpaces.newRound();
        // if (!itemsInitialized)
        // initItems();
        super.newRound();
    }

    public DC_WeaponObj getMainWeapon() {
        return weapon;
    }

    public DC_ArmorObj getArmor(boolean inner) {
        return inner ? innerArmor : armor;
    }

    public DC_ArmorObj getArmor() {
        if (armor == null) {
            return innerArmor;
        }
        return armor;
    }

    public DC_ArmorObj getInnerArmor() {
        return innerArmor;
    }

    public Garment getGarment() {
        return garment;
    }

    public HeadGarment getHeadwear() {
        return headGarment;
    }

    public void setArmor(DC_ArmorObj armor) {
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

        if (spells == null) {
            spells = new ArrayList<>();
        }
        return spells;
    }

    public UnitActiveSpaces getActiveSpaces() {
        return activeSpaces;
    }

    public void setActiveSpaces(UnitActiveSpaces activeSpaces) {
        this.activeSpaces = activeSpaces;
    }

    public void setSpells(List<Spell> spells) {
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

        if (!game.isSimulation() && !isLoaded()) {
            String id = "";
            if (secondWeapon != null) {
                id = secondWeapon.getId() + "";
            }
            setProperty(G_PROPS.OFF_HAND_ITEM, id);
        }
    }


    public List<Spell> getSpellbook() {
        return spellbook;
    }

    public void setSpellbook(List<Spell> spellbook) {
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
        if (getInventory() == null) {
            return false;
        }
        return getInventory().size() >= DC_CONSTS.MAX_INV_ITEMS;
    }

    public boolean isQuickSlotsFull() {
        if (game.isSimulation()) {
            return getIntParam(PARAMS.QUICK_SLOTS) <= ContainerUtils.openContainer(
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
        //        if (!isItemsInitialized()) {
        if (quickItems == null) {
            quickItems = new DequeImpl<>();
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
        list.addAll(getInventory());
        list.addAll(getQuickItems());
        list.addAll(getJewelry());
        return new SearchMaster<Entity>().find(name, list);
    }

    public DequeImpl<DC_HeroItemObj> getInventory() {
        //        if (!isItemsInitialized()) {
        if (inventory == null) {
            inventory = new DequeImpl<>();
        }
        return inventory;
    }

    public void setInventory(DequeImpl<DC_HeroItemObj> inventory) {
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
        if (item == null) {
            return false;
        }
        DC_HeroItemObj prevItem = getItem(slot);
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

    public boolean addItemToInventory(DC_HeroItemObj item) {
        return addItemToInventory(item, false);
    }

    public boolean addItemToInventory(DC_HeroItemObj item, boolean quiet) {
        if (isInventoryFull())
            return false;
        if (getInventory() == null) { //TODO EA check - INV system is loose?
            setProperty(PROPS.INVENTORY, getType().getProperty(getProperty(PROPS.INVENTORY)));
            itemsInitialized = false;
            inventory = (DequeImpl<DC_HeroItemObj>) getInitializer().
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

    public void setItem(DC_HeroItemObj item, ITEM_SLOT slot) {
        if (item instanceof DC_QuickItemObj) {
            if (((DC_QuickItemObj) item).getWrappedWeapon() != null) {
                item = ((DC_QuickItemObj) item).getWrappedWeapon();
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
                setArmor((DC_ArmorObj) item);
                break;
            case MAIN_HAND:
                setWeapon((DC_WeaponObj) item);
                break;
            case OFF_HAND:
                setSecondWeapon((DC_WeaponObj) item);
                break;
            case RESERVE_MAIN_HAND:
                setReserveMainWeapon((DC_WeaponObj) item);
                break;
            case RESERVE_OFF_HAND:
                setReserveOffhandWeapon((DC_WeaponObj) item);
                break;
        }
        if (item != null) {
            if (slot == ITEM_SLOT.RESERVE_MAIN_HAND || slot == ITEM_SLOT.RESERVE_OFF_HAND) {
                //               TODO  item.equipReserve(ref);
            } else
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
        if (!isSimulation()) //sim just remembers for real hero to drop via operation
        {
            GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
                    type.getName() + " is dropped down!");
            getGame().getDroppedItemManager().drop(item, c);
        }

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
        return !deity.getName().equals(MetaEnums.STD_BUFF_NAME.Faithless.name());

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

    public DC_HeroItemObj unequip(ITEM_SLOT slot) {
        return unequip(slot, false);
    }

    public DC_HeroItemObj unequip(ITEM_SLOT slot, Boolean drop) {
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
            case RESERVE_MAIN_HAND:
                item = getReserveMainWeapon();
                setReserveMainWeapon(null);
                break;
            case RESERVE_OFF_HAND:
                item = getReserveOffhandWeapon();
                setReserveOffhandWeapon(null);
                break;
        }
        if (item instanceof DC_WeaponObj) {
            if (((DC_WeaponObj) item).isRanged()) {
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

    public void unequip(DC_HeroItemObj item, Boolean drop) {
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
            if (item instanceof DC_QuickItemObj)
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
        List<DC_QuickItemObj> toRemove = new ArrayList<>(getQuickItems()).subList(n - 1, size - 1);
        for (DC_QuickItemObj q : toRemove) {
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

    public List<DC_HeroSlotItem> getReserveItems() {
        ListMaster<DC_HeroSlotItem> listMaster = new ListMaster<>();
        return listMaster.removeNulls(listMaster.getList(reserveMainWeapon, reserveOffhandWeapon));
    }

    public List<DC_HeroSlotItem> getSlotItems() {
        ListMaster<DC_HeroSlotItem> listMaster = new ListMaster<>();
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
        DC_WeaponObj activeWeapon = getActiveWeapon(true);
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
                        if (!isMainHero() && !isShadow()) {
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
        if (Eidolons.getMainHero() == this)
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
        return Eidolons.getMainHero() == this;
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

    public boolean hasItems(String typeName, int n) {
        for (DC_QuickItemObj item : getQuickItems())
            if (item.getName().equalsIgnoreCase(typeName))
                n--;
        for (Obj item : getInventory())
            if (item.getName().equalsIgnoreCase(typeName))
                n--;
        for (Obj item : getSlotItems())
            if (item.getName().equalsIgnoreCase(typeName))
                n--;
        return n <= 0;
    }

    public DC_HeroItemObj findItemAnywhere(String typeName) {
        DC_HeroItemObj item = findItem(typeName, false);
        if (item == null) {
            item = findItem(typeName, true);
        }
        if (item == null) {
            item = findItem(typeName, null);
        }
        return item;
    }

    public boolean removeItemsFromAnywhere(String name, int n) {
        for (int i = 0; i < n; i++) {
            DC_HeroItemObj item = findItemAnywhere(name);
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

    public DC_HeroItemObj findItem(String typeName, Boolean quick_inv_slot) {
        if (quick_inv_slot == null) {
            return new ListMaster<DC_HeroSlotItem>().findType(typeName, new ArrayList<>(
                    getSlotItems()));
        }
        return !quick_inv_slot ? new ListMaster<DC_HeroItemObj>().findType(typeName,
                new ArrayList<>(getInventory())) : new ListMaster<DC_QuickItemObj>().findType(
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
        if (!force)
            if (isPlayerCharacter()) {
                if (!getCoordinates().equals(coordinates)) {
                    if (AI_Manager.isRunning()) {
                        return;
                    }
                    if (getGame().getLoop().getActiveUnit() != this) {
                        return;
                    }
                }
                //                if (getCoordinates().dst_(coordinates) >= 2) {
                //                    if (game.isStarted())
                //                        if (!originalCoordinates.equals(coordinates)) {
                //                            LogMaster.log(1, "Teleport bug? ");
                ////                            return;
                //                        }
                //                }
            }
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

    public List<DC_UnitAction> getAttacks(boolean offhand) {
        return getAction(offhand ? ActionEnums.OFFHAND_ATTACK : ActionEnums.ATTACK).getSubActions();
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

    @Override
    public void setFacing(FACING_DIRECTION facing) {
        super.setFacing(facing);
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
        return getSightRangeTowards(c) * 2 + 1;
    }

    public int getSightRangeTowards(DC_Obj target) {
        return getSightRangeTowards(target.getCoordinates());
    }

    public int getSightRangeTowards(Coordinates coordinates) {
        int sight = getIntParam(PARAMS.SIGHT_RANGE);
        FACING_SINGLE singleFacing = FacingMaster.getSingleFacing(this.getFacing(), this.getCoordinates(),
                coordinates);
        if (singleFacing == FACING_SINGLE.BEHIND) {
            sight = getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
        } else if (singleFacing == FACING_SINGLE.TO_THE_SIDE) {
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
        removeTempFacing();
        removeTempCoordinates();
        if (!CoreEngine.isLevelEditor())
            if (getAI().isOutsideCombat()) {
                return;
            }
        if (isMine()) {
            if (isShadow() || isHero()) {
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

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void applyBuffRules() {
        getResetter().applyBuffRules();
    }

    public DC_WeaponObj getReserveMainWeapon() {
        return reserveMainWeapon;
    }

    public DC_WeaponObj getReserveOffhandWeapon() {
        return reserveOffhandWeapon;
    }


    public void setReserveMainWeapon(DC_WeaponObj reserveMainWeapon) {
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

    public void setReserveOffhandWeapon(DC_WeaponObj reserveOffhandWeapon) {

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

    public DC_HeroItemObj getReserveWeapon(boolean offhand) {
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

    public boolean canInstantAttack() {
        return getChecker().canInstantAttack();
    }

    public boolean isMovePointsOn() {
        return movePointsOn;
    }

    public void setMovePointsOn(boolean movePointsOn) {
        this.movePointsOn = movePointsOn;
    }

    public void setExtraAtkPointsOn(boolean extraAtkPointsOn) {
        this.extraAtkPointsOn = extraAtkPointsOn;
    }

    public boolean getExtraAtkPointsOn() {
        return extraAtkPointsOn;
    }

    public int getFreeMovesDone() {
        return freeMovesDone;
    }

    public void freeMoveDone() {
        this.freeMovesDone++;
    }

    public void setFreeMovesDone(int freeMovesDone) {
        this.freeMovesDone = freeMovesDone;
    }

    public void setNemesis(List<Deity> nemesis) {
        this.nemesis = nemesis;
    }

    public List<Deity> getNemesis() {
        return nemesis;
    }

    public void setPatron(List<Deity> patron) {
        this.patron = patron;
    }

    public List<Deity> getPatron() {
        return patron;
    }
}
