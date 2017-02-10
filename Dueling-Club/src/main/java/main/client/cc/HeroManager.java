package main.client.cc;

import main.ability.effects.Effect;
import main.client.battle.arcade.PartyManager;
import main.client.cc.gui.MainPanel;
import main.client.cc.gui.tabs.lists.JewelrySlots;
import main.client.cc.gui.views.ClassView;
import main.client.cc.gui.views.HeroItemView;
import main.client.cc.logic.spells.LibraryManager;
import main.client.cc.logic.spells.SpellUpgradeMaster;
import main.client.dc.Launcher;
import main.content.CONTENT_CONSTS.*;
import main.content.CONTENT_CONSTS2.SPELL_UPGRADE;
import main.content.*;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.elements.conditions.RequirementsManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.event.MessageManager;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.town.Shop;
import main.system.DC_Formulas;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.*;

public class HeroManager {
    // protected static final String DUMMY_ITEM = "Two-handed item";
    protected Map<DC_HeroObj, Stack<ObjType>> typeStacks = new HashMap<>();
    protected DC_Game game;
    private boolean trainer;

    // dtFlags
    public HeroManager(DC_Game game) {
        this.game = game;
    }

    public static String getCost(Entity type, Entity hero) {
        return getCost(type, hero, type.getOBJ_TYPE_ENUM(), null);
    }

    public static String getCost(Entity type, Entity hero, OBJ_TYPE TYPE, PROPERTY PROP) {
        return getCost(type, hero, TYPE, PROP, true);
    }

    public static String getCost(boolean formula, Entity type, Entity hero, OBJ_TYPE TYPE,
                                 PROPERTY PROP, boolean buying) {
        return null;

    }

    public static String getCost(Entity type, Entity hero, OBJ_TYPE TYPE, PROPERTY PROP,
                                 boolean buying) {
        if (TYPE.getParam() == null) {
            return "";
        }
        PARAMETER costParam = ContentManager.getCostParam(TYPE.getParam());
        PARAMETER discountParam = DC_ContentManager.getCostReductionParam(costParam, PROP);
        Integer value = type.getIntParam(costParam);
        Integer mod = hero.getIntParam(discountParam);
        Formula costFormula = new Formula("" + value);
        if (discountParam != null) {
            costFormula.applyFactor((!buying ? "" : "-")
                    + StringMaster.getValueRef(KEYS.SOURCE, discountParam));
        }
        if (!buying) {
            costFormula.applyFactor(-DC_Formulas.getSellingPriceReduction());
        }

        discountParam = DC_ContentManager.getSpecialCostReductionParam(costParam, PROP);
        if (discountParam == null) {
            return "" + costFormula;
        }

        mod = hero.getIntParam(discountParam);
        if (mod == 100) {
            value = 0;
        } else {
            costFormula.applyFactor((!buying ? "" : "-")
                    + StringMaster.getValueRef(KEYS.SOURCE, discountParam));
        }

        return "(" + costFormula + ")";
    }

    public static void applyChangedTypeStatic(Entity hero, Entity type) {
        hero.getGame().initType((ObjType) type);
        hero.setType((ObjType) type);
        hero.cloneMaps(type);
    }

    public void removeHero(DC_HeroObj hero) {
        typeStacks.remove(hero);
    }

    public void afterDefeatRewind() {
        game.setSimulation(true);
        for (DC_HeroObj hero : PartyManager.getParty().getMembers()) {
            stepBack(hero);
        }
    }

    public void prebattleCleanSave() {
        clearStacks();
        for (DC_HeroObj hero : PartyManager.getParty().getMembers()) {
            saveHero(hero);
        }
    }

    public void clearStacks() {
        typeStacks.clear();
    }

    public void addHero(DC_HeroObj hero) {
        Stack<ObjType> stack = new Stack<>();
        typeStacks.put(hero, stack);
    }

    public void stepBack(DC_HeroObj hero) {
        if (!game.isSimulation()) {
            return;
        }
        Stack<ObjType> stack = typeStacks.get(hero);
        if (stack == null) {
            return;
        }

        Entity type = stack.pop();
        applyChangedType(true, hero, type);
        CharacterCreator.setSelectedHeroType(hero.getType());
        if (CharacterCreator.getPanel().isPrincipleView()) {
            CharacterCreator.getPanel().getPrincipleViewComp().reset();
        }
    }

    public void update(DC_HeroObj hero) {
        update(hero, true);
    }

    public void update(DC_HeroObj hero, boolean refresh) {
        // hero.setItemsInitialized(false);
        if (hero == null) {
            return;
        }
        for (DC_HeroSlotItem item : hero.getSlotItems()) {
            if (item != null) {
                if (item.getAttachments() != null) {
                    item.getAttachments().clear();
                    for (BuffObj buff : item.getBuffs()) {
                        buff.kill();
                    }
                }
            }
        }
        hero.toBase();
        if (game.isSimulation()) {

            List<Attachment> attachments = hero.getAttachments();

            List<Effect> secondLayerEffects = new LinkedList<>();
            if (attachments != null) {
                for (Attachment a : attachments) {
                    try {
                        for (Effect e : a.getEffects()) {
                            if (e.getLayer() != Effect.SECOND_LAYER) {
                                e.apply(Ref.getSelfTargetingRefCopy(hero));
                            } else {
                                secondLayerEffects.add(e);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            for (Effect e : secondLayerEffects) {
                e.apply(Ref.getSelfTargetingRefCopy(hero));
            }
            EffectMaster.applyAttachmentEffects(hero.getMainWeapon(), null);
            EffectMaster.applyAttachmentEffects(hero.getSecondWeapon(), null);
            EffectMaster.applyAttachmentEffects(hero.getArmor(), null);

        }
        hero.afterEffects();
        hero.setDirty(true);

        if (!hero.getGame().isSimulation()) {
            try {
                hero.resetObjects();
                hero.resetQuickSlotsNumber();
                refreshInvWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (refresh) {
            refresh(hero);
        }
    }

    private void refresh(DC_HeroObj hero) {
        if (CharacterCreator.isInitialized()) {
            if (CharacterCreator.getHeroPanel(hero) != null) {
                CharacterCreator.getHeroPanel(hero).refresh();
            }
        }
    }

    protected void refreshInvWindow() {
        if (game.getInventoryManager().getWindow() != null) {
            game.getInventoryManager().getWindow().refresh();
        }
    }

    protected ITEM_SLOT getItemSlot(DC_HeroObj hero, Entity type) {
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.ITEMS) {
            return null;
        }
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS) {
            WEAPON_CLASS CLASS = getWeaponClass(type);
            switch (CLASS) {
                case OFF_HAND:
                    if (StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        return ITEM_SLOT.MAIN_HAND;
                    }
                    if (MessageManager.promptItemSwap(hero.getProperty(G_PROPS.MAIN_HAND_ITEM),
                            hero, type)) {
                        return ITEM_SLOT.MAIN_HAND;
                    }
                    if (getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == WEAPON_CLASS.TWO_HANDED
                            || getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == WEAPON_CLASS.DOUBLE) {
                        return null;
                    }
                    if (StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM))) {
                        return ITEM_SLOT.OFF_HAND;
                    }
                    if (MessageManager.promptItemSwap(hero.getProperty(G_PROPS.OFF_HAND_ITEM),
                            hero, type)) {
                        return ITEM_SLOT.OFF_HAND;
                    }
                    return null;
                case MAIN_HAND_ONLY:
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        if (!MessageManager.promptItemSwap(
                                hero.getProperty(G_PROPS.MAIN_HAND_ITEM), hero, type)) {
                            return null;
                        }
                    }
                    return ITEM_SLOT.MAIN_HAND;

                case OFF_HAND_ONLY:
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM))) {
                        return null;
                    }
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        if (getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == WEAPON_CLASS.TWO_HANDED) {
                            return null;
                        }
                    }
                    return ITEM_SLOT.OFF_HAND;
                case DOUBLE:
                case TWO_HANDED:
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM))) {
                        return null;
                    }
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        if (!MessageManager.promptItemSwap(
                                hero.getProperty(G_PROPS.MAIN_HAND_ITEM), hero, type)) {
                            return null;
                        }
                    }
                    return ITEM_SLOT.MAIN_HAND;
                default:
                    return null;

            }

            // if (!type
            // .checkProperty((G_PROPS.WEAPON_CLASS), WEAPON_CLASS.OFF_HAND_ONLY
            // .toString())) {
            // if (!StringMaster.isEmpty(hero
            // .getProperty(G_PROPS.MAIN_HAND_ITEM)))
            // if (!promptItemSwap(G_PROPS.MAIN_HAND_ITEM, hero, type))
            // return null;
            // return ITEM_SLOT.MAIN_HAND;
            // }
            //
            // if (type.checkProperty((G_PROPS.WEAPON_CLASS),
            // WEAPON_CLASS.MAIN_HAND_ONLY
            // .toString())
            // )
            // if (!StringMaster.isEmpty(hero
            // .getProperty(G_PROPS.MAIN_HAND_ITEM)))
            // return null;
            //
            // if (type.checkProperty((G_PROPS.WEAPON_CLASS),
            // WEAPON_CLASS.TWO_HANDED
            // .toString())
            //
            // )
            // // this item can only be used with main hand!
            // return null;
            //
            // if
            // (!StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM)))
            // if (!promptItemSwap(G_PROPS.OFF_HAND_ITEM, hero, type))
            // return null;
            // return ITEM_SLOT.OFF_HAND;

        }
        if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.ARMOR_ITEM))) {
            if (!MessageManager.promptItemSwap(hero.getProperty(G_PROPS.ARMOR_ITEM), hero, type)) {
                return null;
            }
        }
        return ITEM_SLOT.ARMOR;
    }

    protected WEAPON_CLASS getWeaponClass(String type) {
        ObjType objType = DataManager.getType(type, OBJ_TYPES.WEAPONS);
        if (objType == null) {
            objType = game.getObjectById(StringMaster.getInteger(type)).getType();
        }
        return getWeaponClass(objType);
    }

    protected WEAPON_CLASS getWeaponClass(Entity type) {
        return new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class, type
                .getProperty(G_PROPS.WEAPON_CLASS));
    }

    public int addSlotItem(DC_HeroObj hero, Entity type, boolean alt) {
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.JEWELRY) {
            return addJewelryItem(hero, type);
        }

        ITEM_SLOT slot = null;
        if (!alt) {
            slot = getItemSlot(hero, type);
        }
        if (slot == null) {
            if (isQuickItem(type)) {
                // type.getProp(G_PROPS.CUSTOM_SOUNDSET)
                return addQuickItem(hero, type);
            }

            return 0;
        }
        return setHeroItem(hero, slot, type);

    }

    protected int addJewelryItem(DC_HeroObj hero, Entity type) {
        boolean amulet = type.checkProperty(G_PROPS.JEWELRY_TYPE, "" + JEWELRY_TYPE.AMULET);
        boolean hasNoAmulet = chechHasNoAmulet(hero);
        List<ObjType> sortedJewelryData = getSortedJewelry(hero);
        if (amulet) {
            if (!hasNoAmulet) {
                if (!MessageManager.promptItemSwap(sortedJewelryData.get(JewelrySlots.AMULET_INDEX)
                        .getName(), hero, type))
                // makes me wonder once again why HC has to deal with types
                // and not objects, would it be hard to write thru to types?
                // Easier than have objects initiatilized like that
                {
                    return 0;
                } else {
                    removeJewelryItem(hero, sortedJewelryData.get(JewelrySlots.AMULET_INDEX));
                }
            }

        } else {

            if (!checkCanEquipJewelry(hero)) {
                return 0;
            }
        }
        saveHero(hero);
        addItem(hero, type, C_OBJ_TYPE.ITEMS, PROPS.JEWELRY, true, false);
        removeItem(hero, type, PROPS.INVENTORY, C_OBJ_TYPE.ITEMS, true, false);
        update(hero);
        return 1;
    }

    public boolean checkCanEquipJewelry(DC_HeroObj hero, ObjType itemType) {
        boolean amulet = itemType.checkProperty(G_PROPS.JEWELRY_TYPE, "" + JEWELRY_TYPE.AMULET);
        if (amulet) {
            return chechHasNoAmulet(hero);
        } else {
            return checkCanEquipJewelry(hero);
        }
    }

    public boolean checkCanEquipJewelry(DC_HeroObj hero) {
        boolean hasNoAmulet = chechHasNoAmulet(hero);
        int size = hero.getJewelry().size();
        if (!hasNoAmulet) {
            size++;
        }
        return size < JewelrySlots.LIST_SIZE;
    }

    private boolean chechHasNoAmulet(DC_HeroObj hero) {
        return getSortedJewelry(hero).get(JewelrySlots.AMULET_INDEX) == null;
    }

    private List<ObjType> getSortedJewelry(DC_HeroObj hero) {
        List<ObjType> sortedJewelryData = JewelrySlots
                .getSortedJewelryData(new ListMaster<DC_HeroItemObj>().convertToTypeList(hero
                        .getJewelry()));
        return sortedJewelryData;
    }

    public int addQuickItem(DC_HeroObj hero, Entity type) {
        if (hero.isQuickSlotsFull()) {
            return 0;
        }
        saveHero(hero);
        addItem(hero, type, C_OBJ_TYPE.ITEMS, PROPS.QUICK_ITEMS, true, false);
        removeItem(hero, type, PROPS.INVENTORY, C_OBJ_TYPE.ITEMS, true, false);
        update(hero);
        return 1;

    }

    protected boolean isQuickItem(Entity type) {
        // what about small weapons?!
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.ITEMS) {
            return true;
        }
        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.WEAPONS) {
            if (type.checkSingleProp(G_PROPS.WEAPON_SIZE, WEAPON_SIZE.SMALL + "")
                    || type.checkSingleProp(G_PROPS.WEAPON_SIZE, WEAPON_SIZE.TINY + "")
                    || type.checkSingleProp(G_PROPS.WEAPON_TYPE, WEAPON_TYPE.AMMO + "")) {
                return true;
            }
        }
        return false;
    }

    public boolean addItem(DC_HeroObj hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP) {
        return addItem(hero, type, TYPE, PROP, false);

    }

    public boolean addItem(DC_HeroObj hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP, boolean free) {
        return addItem(hero, type, TYPE, PROP, free, true);
    }

    public boolean tryIncrementRank(DC_HeroObj hero, ObjType type) {
        boolean skill = type.getOBJ_TYPE_ENUM() == OBJ_TYPES.SKILLS;
        DC_FeatObj feat = hero.getFeat(skill, type);
        if (!checkCanIncrementRank(type, feat, hero)) {
            return false;
        }
        if (!hero.incrementFeatRank(skill, type)) {
            return false;
        }
        saveHero(hero);
        hero.saveRanks(skill);
        // SoundMaster.playSkillAddSound(STD_SOUNDS.ButtonUp);
        int xpCost = feat.getIntParam(PARAMS.XP_COST) * feat.getIntParam(PARAMS.RANK_XP_MOD) / 100;
        hero.modifyParameter(PARAMS.XP, -xpCost);
        update(hero);
        return true;
    }

    protected boolean checkCanIncrementRank(ObjType type, DC_FeatObj feat, DC_HeroObj hero) {
        if (feat == null) {
            return false;
        }
        boolean skill = type.getOBJ_TYPE_ENUM() == OBJ_TYPES.SKILLS;
        if (skill) {
            return feat.getGame().getRequirementsManager().check(hero, feat,
                    RequirementsManager.RANK_MODE) == null;
        }
        List<String> reasons = feat.getGame().getRequirementsManager().checkRankReqs(feat);
        return reasons.isEmpty();
    }

    public boolean addItem(DC_HeroObj hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP,
                           boolean free, boolean update) {

        if (!free && !CoreEngine.isArcaneVault()) {
            if (!checkCost(hero, type, TYPE, PROP)) {
                return false;
            }
            String s = checkRequirements(hero, type, getMode(TYPE, PROP));
            if (s != null) {
                // alarm(s);
                return false;
            }
        }

        // DC_SpellObj base = LibraryManager.getVerbatimSpellVersion(hero,
        // type);
        // if (base != null) {
        // if (isTrainer())
        // return addSpellUpgrade(hero, type, PROP);
        // if (!MessageManager.promptSpellReplace(hero, type, base.getType()))
        // return false;
        // else
        // return addSpellUpgrade(hero, type, PROP);
        // }
        // }

        if (update) {
            saveHero(hero);
        }
        // if (type.isUpgrade()) {
        // if (!addUpgrade(hero, type, PROP))
        // return false; TODO DEPRECATED!
        // }
        if (!hero.getType().addProperty(PROP,
                ((game.isSimulation() || trainer) ? type.getName() : type.getId() + ""),
                checkNoDuplicates(TYPE)))
            // undoSave(hero);
        {
            return false;
        }

        if (type.getOBJ_TYPE_ENUM() == OBJ_TYPES.CLASSES) {
            classAdded(hero, type);
        }
        int cost;
        if (!free) {
            cost = subtractCost(hero, type, TYPE, PROP);
            checkShop(type, TYPE, PROP, cost, true);
        } else {
            if (update) {
                update(hero);
            }

        }

        if (TYPE.equals(OBJ_TYPES.SPELLS)) {
            if (PROP.equals(PROPS.VERBATIM_SPELLS)) {
                SpellUpgradeMaster.removeUpgrades(hero, type);
            }
        }
        // TODO check if hero now has the prop!
        return true;
    }

    private void checkShop(Entity type, OBJ_TYPE TYPE, PROPERTY PROP, int cost, boolean sold) {
        if (C_OBJ_TYPE.ITEMS.equals(TYPE)) {
            if (PROP == PROPS.INVENTORY) {
                if (Launcher.getMainManager().isMacroMode()) {
                    String shopName = ((HeroItemView) CharacterCreator.getHeroPanel().getMvp()
                            .getCurrentViewComp()).getVendorPanel().getSelectedTabName();
                    Shop shop = MacroGame.getGame().getPlayerParty().getTown().getShop(shopName);
                    if (sold) {
                        shop.sellItem((ObjType) type, cost);
                    } else {
                        shop.buyItem((ObjType) type, cost);
                    }
                }
            }
        }
    }

    private int getMode(OBJ_TYPE T, PROPERTY p) {
        if (T == OBJ_TYPES.SPELLS) {
            if (p == PROPS.VERBATIM_SPELLS) {
                return RequirementsManager.VERBATIM_MODE;
            }
        }
        return RequirementsManager.NORMAL_MODE;
    }

    private void classAdded(DC_HeroObj hero, Entity class_type) {
        if (ClassView.isMulticlass(class_type)) {
            if (!class_type.checkProperty(G_PROPS.BASE_TYPE)) { // prime
                // multiclass
                hero.setProperty(PROPS.MULTICLASSES, hero.getType().getProperty(PROPS.CLASSES),
                        true);
                hero.setProperty(PROPS.FIRST_CLASS, ClassView.MULTICLASS + " "
                        + StringMaster.wrapInParenthesis(class_type.getName()), true);
                hero.setProperty(PROPS.SECOND_CLASS, "", true);
            } else {
                hero.getType().addProperty(PROPS.MULTICLASSES, class_type.getName());
            }

            return;
        }
        PROPS property = PROPS.FIRST_CLASS;
        String value = hero.getProperty(property);
        String group = class_type.getProperty(G_PROPS.CLASS_GROUP);
        if (StringMaster.isEmpty(value)) {
            hero.getType().setProperty(property, group);
            return;
        }
        if (StringMaster.compare(hero.getProperty(property), group, true)) {
            return;
        }

        property = PROPS.SECOND_CLASS;
        value = hero.getProperty(property);
        if (StringMaster.isEmpty(value)) {
            hero.getType().setProperty(property, group);
        }

    }

    public boolean addSpellUpgrade(DC_HeroObj hero, Entity type, PROPERTY prop) {
        saveHero(hero);
        subtractCost(hero, type, OBJ_TYPES.SPELLS, prop);
        boolean result = LibraryManager.replaceSpellVersion(hero, type, PROPS.VERBATIM_SPELLS);
        update(hero);
        return result;

    }

    public String checkRequirements(DC_HeroObj hero, Entity type, int mode) {
        return hero.getGame().getRequirementsManager().check(hero, type, mode);
    }

    protected boolean checkNoDuplicates(OBJ_TYPE TYPE) {
        if (TYPE instanceof OBJ_TYPES) {
            switch ((OBJ_TYPES) TYPE) {
                case SKILLS:
                    return true;
                case SPELLS:
                    return true;
                case CLASSES:
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    protected MainPanel getPanel(DC_HeroObj hero) {
        return CharacterCreator.getHeroPanel(hero);
    }

    public int subtractCost(DC_HeroObj hero, Entity type, OBJ_TYPE TYPE, PROPERTY p) {
        int cost = modifyCostParam(hero, type, TYPE, false, p);
        update(hero);
        return cost;
    }

    protected Integer modifyCostParam(DC_HeroObj hero, Entity type, OBJ_TYPE TYPE, boolean selling,
                                      PROPERTY p) {
        String cost = getCost(type, hero, TYPE, p, !selling);
        Integer amount = new Formula(cost).getInt(hero.getRef());
        if (!selling) {
            amount *= -1;
        }
        hero.modifyParameter(TYPE.getParam(), amount);
        if (!selling) {
            amount *= -1;
        }
        return amount;
        // update(hero); TODO
    }

    protected boolean checkCost(DC_HeroObj hero, Entity type, OBJ_TYPE t, PROPERTY PROP) {
        if (CoreEngine.isArcaneVault()) {
            return true;
        }
        return hero.checkParam(t.getParam(), ""
                + new Formula(getCost(type, hero, t, PROP)).getInt(hero.getRef()));
    }

    public DC_HeroObj getHero(ObjType type) {
        for (DC_HeroObj hero : typeStacks.keySet()) {
            if (hero.getType().getName().equals(type.getName())) {
                return (hero);
            }
        }
        return null;
    }

    public void saveType(ObjType type) {
        saveHero(getHero(type));
    }

    public void update(ObjType type) {
        update(getHero(type));
    }

    public void saveHero(DC_HeroObj hero) {
        if (hero == null) {
            return;
        }
        final ObjType type = hero.getType();
        if (game.isSimulation()) {
            if (typeStacks.get(hero) == null) {
                addHero(hero);
            }
            typeStacks.get(hero).push(new ObjType(type));
        }

    }

    public void modifyHeroParam(DC_HeroObj hero, PARAMETER param, int amount) {
        hero.modifyParameter(param, amount);
        hero.getType().modifyParameter(param, amount);
        getPanel(hero).refresh();
    }

    public void applyChangedType(DC_HeroObj hero, Entity type) {
        applyChangedType(false, hero, type);
    }

    public void applyChangedType(boolean dont_save, DC_HeroObj hero, Entity type) {
        if (!dont_save) {
            saveHero(hero);
        }
        game.initType((ObjType) type);
        // hero.applyType(type);
        hero.setType((ObjType) type);
        hero.cloneMaps(type);
        update(hero);

    }

    public int setHeroItem(DC_HeroObj hero, ITEM_SLOT slot, Entity type) {

        saveHero(hero);
        int result = 1;
        if (!StringMaster.isEmpty(hero.getProperty(slot.getProp()))) {
            removeSlotItem(hero, slot, false);
            result++;
        }
        hero.getType().setProperty(slot.getProp(), type.getNameOrId());
        if (getWeaponClass(type) == WEAPON_CLASS.TWO_HANDED
                || getWeaponClass(type) == WEAPON_CLASS.DOUBLE) {

        }

        hero.getType().removeProperty(PROPS.INVENTORY, type.getNameOrId());
        update(hero, false);
        if (hero.getGame().isSimulation()) {
            AbilityConstructor.constructActives(hero);
        }
        refresh(hero);

        return result;
    }

    public void removeSlotItem(DC_HeroObj hero, ITEM_SLOT slot) {
        removeSlotItem(hero, slot, true);
    }

    public void removeSlotItem(DC_HeroObj hero, ITEM_SLOT slot, boolean save) {
        if (save) {
            saveHero(hero);
        }
        if (save) {
            if (slot == ITEM_SLOT.MAIN_HAND) {
                if (hero.getSecondWeapon() != null) {
                    if (hero.getSecondWeapon().isWeapon()) {
                        if (!hero.getSecondWeapon().checkSingleProp(G_PROPS.WEAPON_CLASS,
                                WEAPON_CLASS.OFF_HAND_ONLY + "")) {
                            removeSlotItem(hero, ITEM_SLOT.OFF_HAND, false);
                            setHeroItem(hero, slot, hero.getSecondWeapon().getType());
                            return;
                        }
                    }
                }
            }
        }

        String prevItem = hero.getProperty(slot.getProp());
        // if (prevItem.equals(DUMMY_ITEM)) {
        // prevItem = hero.getProperty(ITEM_SLOT.MAIN_HAND.toString());
        // }
        hero.getType().addProperty(PROPS.INVENTORY, prevItem, false);

        hero.getType().setProperty(slot.getProp(), "");

        if (save) {
            update(hero, false);
        }
        if (hero.getGame().isSimulation()) {
            AbilityConstructor.constructActives(hero);
        }
        refresh(hero);
        // game.getInventoryManager().getInvListManager().operationDone();
    }

    public void removeQuickSlotItem(DC_HeroObj hero, Entity type) {
        removeContainerItem(hero, type, PROPS.QUICK_ITEMS);
    }

    public void removeContainerItem(DC_HeroObj hero, Entity type, PROPS p) {
        removeContainerItem(hero, type, p, true);
    }

    public void removeContainerItem(DC_HeroObj hero, Entity type, PROPS p, boolean update) {
        saveHero(hero);

        hero.getType().removeProperty(p, type.getNameOrId());
        hero.getType().addProperty(PROPS.INVENTORY, type.getNameOrId(), false);
        update(hero);
    }

    public void removeJewelryItem(DC_HeroObj hero, Entity type) {
        removeContainerItem(hero, type, PROPS.JEWELRY);

    }

    public void removeItem(DC_HeroObj hero, Entity type, PROPERTY prop, OBJ_TYPE TYPE, boolean free) {
        removeItem(hero, type, prop, TYPE, free, true);
    }

    public void removeItem(DC_HeroObj hero, Entity type, PROPERTY prop, OBJ_TYPE TYPE,
                           boolean free, boolean update) {
        if (update) {
            saveHero(hero);
        }
        if (!hero.getType().removeProperty(prop, type.getNameOrId())) {
            return;
        }

        int cost;
        if (!free) {
            cost = modifyCostParam(hero, type, TYPE, true, prop);
            checkShop(type, TYPE, prop, cost, false);
        }
        if (update) {
            update(hero);
        }

    }

    public boolean addMemorizedSpell(DC_HeroObj hero, Entity type) {

        if (hero.calculateRemainingMemory() >= type.getIntParam(PARAMS.SPELL_DIFFICULTY)) {
            if (type.isUpgrade()) {
                if (LibraryManager.hasSpellVersion(hero, type, PROPS.MEMORIZED_SPELLS)) {
                    return false;
                }
            }
            return addItem(hero, type, OBJ_TYPES.SPELLS, PROPS.MEMORIZED_SPELLS, true
                    // , game.isSimulation()
            );
        } else {
            // TODO alarm
            return false;
        }
    }

    public boolean isTrainer() {
        return trainer;
    }

    public void setTrainer(boolean trainer) {
        this.trainer = trainer;
    }

    public void spellUpgradeToggle(SPELL_UPGRADE selected, Entity entity) {
        DC_HeroObj hero = CharacterCreator.getHero();
        DC_SpellObj spell = hero.getSpell(entity.getName());
        boolean verbatim = spell.getSpellPool() == SPELL_POOL.VERBATIM;
        if (!SpellUpgradeMaster.checkUpgrade(verbatim, hero, spell, selected)) {
            SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
            return;
        }
        // result = SpellUpgradeMaster.isUpgraded(spell);
        boolean result = SpellUpgradeMaster.toggleUpgrade(hero, spell, selected);

        if (result) {
            saveHero(hero);
            if (verbatim) {
                hero.modifyParameter(PARAMS.XP, -SpellUpgradeMaster.getXpCost(entity, hero,
                        selected));
            }
            SoundMaster.playStandardSound(STD_SOUNDS.SPELL_UPGRADE_LEARNED);
        } else {
            SoundMaster.playStandardSound(STD_SOUNDS.SPELL_UPGRADE_UNLEARNED);
        }
        if (!verbatim) {
            update(hero);
        }
    }

}
