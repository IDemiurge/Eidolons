package main.entity.obj.unit;

import main.ability.AbilityObj;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.client.cc.CharacterCreator;
import main.client.cc.logic.party.PartyObj;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.CONTENT_CONSTS.FLIP;
import main.content.CONTENT_CONSTS2.AI_MODIFIERS;
import main.content.*;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.system.AiEnums;
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
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_QuickItemAction;
import main.entity.active.DC_SpellObj;
import main.entity.item.*;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.KeyResolver;
import main.entity.obj.Obj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.tools.EntityMaster;
import main.entity.tools.bf.unit.*;
import main.entity.type.ObjType;
import main.game.ai.UnitAI;
import main.game.battlefield.CoordinatesMaster;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.battle.player.Player;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.generic.DC_ActionManager;
import main.game.logic.generic.hero.DC_Attributes;
import main.game.logic.generic.hero.DC_Masteries;
import main.game.logic.macro.entity.MacroActionManager.MACRO_MODES;
import main.system.DC_Constants;
import main.system.DC_Formulas;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;

import java.util.*;
import java.util.stream.Collectors;

public class Unit extends DC_UnitModel {
    protected DC_WeaponObj offhandNaturalWeapon;
    protected DC_WeaponObj naturalWeapon;
    private DC_WeaponObj weapon;
    private DC_WeaponObj secondWeapon;
    // private Footwear boots;
    // private Helmet helmet;
    // private Gloves gloves;
    // private Cloak cloak;]
    private DC_ArmorObj armor;
    private DequeImpl<DC_FeatObj> skills;
    private DequeImpl<DC_FeatObj> classes;
    private DequeImpl<DC_QuickItemObj> quickItems;
    private DequeImpl<DC_JewelryObj> jewelry;
    private DequeImpl<DC_HeroItemObj> inventory;

    private DC_Masteries masteries;
    private DC_Attributes attrs;

    private List<DC_SpellObj> spells;
    private boolean initialized;
    private List<DC_SpellObj> spellbook;
    private boolean itemsInitialized;
    private boolean aiControlled;
    private MACRO_MODES macroMode;
    private GENDER gender;
    private Dungeon dungeon;
    private boolean mainHero;
    private boolean leader;
    private FLIP flip;
    private ObjType backgroundType;
    private Map<DC_ActiveObj, String> actionModeMap;
    private boolean animated;
    private Unit engagementTarget;
    private DC_WeaponObj rangedWeapon;

    public Unit(ObjType type, int x, int y, Player owner, DC_Game game, Ref ref) {
        super(type, x, y, owner, game, ref);
        setDungeon(getGame().getDungeonMaster().getDungeon());
        if (getGame().getDungeonMaster().getZ() != null) {
            setZ(getGame().getDungeonMaster().getZ());
        } else {
            setZ(0);
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

//        WaitMaster.receiveInput(WAIT_OPERATIONS.UNIT_OBJ_INIT, true);
    }

    @Override
    public UnitHandler getHandler() {
        return (UnitHandler) super.getHandler();
    }

    public void saveRanks(boolean skills) {
        saveRanks(skills ? getSkills() : getClasses(), skills ? PROPS.SKILLS : PROPS.CLASSES);
    }

    public void saveRanks(DequeImpl<DC_FeatObj> container, PROPERTY property) {
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
        if (game.isSimulation()) {
            return (DC_FeatObj) getGame().getSimulationObj(this, type,
             skill ? PROPS.SKILLS : PROPS.CLASSES);
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

    public void removeJewelryItem(DC_HeroItemObj itemObj) {
        getJewelry().remove(itemObj);
        if (getJewelry().isEmpty()) {
            setJewelry(null);
        }
    }

    public void addQuickItem(DC_QuickItemObj itemObj) {
        getQuickItems().add(itemObj);
        itemObj.setRef(ref);
        getResetter().resetQuickSlotsNumber();
    }

    public void removeQuickItem(DC_QuickItemObj itemObj) {
        if (getQuickItems().remove(itemObj)) {
            getResetter().resetQuickSlotsNumber();
        }
        // if (game.isArcade()
        if (CharacterCreator.isArcadeMode()) {
            type.removeProperty(PROPS.QUICK_ITEMS, itemObj.getName(), true);
            removeProperty(PROPS.QUICK_ITEMS, "" + itemObj.getId(), true);
            // setProperty(PROPS.QUICK_ITEMS, value, true);
        }
        if (getQuickItems().isEmpty()) {
            setQuickItems(null);
        }
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
        getResetter().afterEffectsApplied();

    }

    public List<DC_QuickItemAction> getQuickItemActives() {
        if (!ListMaster.isNotEmpty(getQuickItems())) {
            return new LinkedList<>();
        }
        List<DC_QuickItemAction> qia = new LinkedList<>();
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

        for (String item : StringMaster.openContainer(getProperty(prop))) {

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
            getClasses().add(e);
        }
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
        if (quickItems == null) {
            return false;
        }
        return getRemainingQuickSlots() <= 0;
    }

    public int getRemainingQuickSlots() {
        return getIntParam(PARAMS.QUICK_SLOTS) - quickItems.size();
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

    public Entity getItem(String name) {
        // for (String generic: getInventory())
        return null;
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

        if (weapon == null) {
            weapon = getNaturalWeapon(offhand);
        }
        if (weapon == null) {
            if (!offhand) {
                weapon = DC_ContentManager.getDefaultWeapon(this);
            }
        }
        return weapon;
    }

    public DC_WeaponObj getWeapon(boolean offhand) {
        return (offhand) ? getSecondWeapon() : getMainWeapon();
    }

    public boolean equip(DC_HeroItemObj item, ITEM_SLOT slot) {
        DC_HeroItemObj prevItem = getItem(slot);
        setItem(item, slot);
        item.setRef(ref);
        if (prevItem != null) {
            addItemToInventory(prevItem);
        }
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
          (PARAMETER p) -> ContentManager.getPercentageParam(p)).
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
        if (getInventory().isEmpty()) {
            setInventory(null);
        }
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
                item = getSecondWeapon();
                setSecondWeapon(null);
                break;
        }
        if (item instanceof DC_WeaponObj){
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

    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, DC_UnitModel target, Ref REF,
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

        if (getQuickItems().contains(item)) {
            removeQuickItem((DC_QuickItemObj) item);
            addItemToInventory(item);
        } else if (getJewelry().contains(item)) {
            removeJewelryItem(item);
            addItemToInventory(item);

        }
        if (drop) {
            dropItemFromInventory(item);
        }
    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        boolean result = super.kill(killer, leaveCorpse, quietly);
        if (!CoreEngine.isLevelEditor()) {
            if (result) {
                for (DC_FeatObj s : getSkills()) {
                    s.apply();
                }
                for (AbilityObj p : passives) {
                    p.activate();
                }
                // TODO could filter by some boolean set via GOME itself! so
                // much
                // for a small thing...
            }
        }
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
        if (Launcher.BRUTE_AI_MODE) {
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
        if (aiControlled) {
            return true;
        }
        if (!getGame().isDebugMode()) {
            if (getGame().getGameMode() == GAME_MODES.ARENA) {
                if (owner.getHeroObj() != null) {
                    if (owner.getHeroObj() != this) {
                        return true;
                    }
                }
            }
        }
        if (owner.isAi()) {
            if (!checkBool(DYNAMIC_BOOLS.PLAYER_CONTROLLED)) {
                return true;
            }
        }
        if (checkBool(DYNAMIC_BOOLS.AI_CONTROLLED)) {
            return true;
        }
        if (! getAI().getForcedActions().isEmpty()) {
            return true;
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

    public Dungeon getDungeon() {
        if (dungeon == null) {
            return getGame().getDungeonMaster().getRootDungeon();
        }
        return dungeon;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        if (dungeon != null) {
            setZ(dungeon.getZ());
        }
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

    public boolean isAnimated() {
        if (animation == null) {
            return false;
        }
        return animation.isDrawReady();
    }
@Deprecated
    public void initToolTip() {

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

    public boolean isHostileTo(DC_Player player) {
        if (getBehaviorMode() == AiEnums.BEHAVIOR_MODE.BERSERK) {
            return true;
        }
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
        DC_HeroItemObj item = !quick_inv_slot ? new ListMaster<DC_HeroItemObj>().findType(typeName,
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

        return getAction(offhand ? DC_ActionManager.OFFHAND_ATTACK :
         DC_ActionManager.ATTACK);
    }

    public Unit getEngagementTarget() {
        return engagementTarget;
    }

    public void setEngagementTarget(Unit engaged) {
        engagementTarget = engaged;
    }

    public boolean isSelected() {
        return isInfoSelected() || isActiveSelected();
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
        return getAction(offhand ? DC_ActionManager.ATTACK : DC_ActionManager.OFFHAND_ATTACK).getSubActions();
    }

    public void resetQuickSlotsNumber() {
        getResetter().resetQuickSlotsNumber();
    }

    public void resetObjectContainers(boolean fromValues) {
        getResetter().resetObjectContainers(fromValues);
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
        return (UnitCalculator) super.getCalculator();
    }

    public int calculateRemainingMemory() {
        return getCalculator().calculateRemainingMemory();
    }

    public int calculateMemorizationPool() {
        return getCalculator().calculateMemorizationPool();
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
        return (UnitInitializer) super.getInitializer();
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

    public DC_Obj getLinkedObj(IdKey key) {
        return  new KeyResolver().getObj(key, this);
    }

    public DC_WeaponObj getRangedWeapon() {
        return rangedWeapon;
    }

    public void setRangedWeapon(DC_WeaponObj rangedWeapon) {
        this.rangedWeapon = rangedWeapon;
    }
}
