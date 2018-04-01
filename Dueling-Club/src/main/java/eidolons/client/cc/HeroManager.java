package eidolons.client.cc;

import eidolons.content.DC_ContentManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.system.DC_Formulas;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import eidolons.client.cc.gui.MainPanel;
import eidolons.client.cc.gui.tabs.lists.JewelrySlots;
import eidolons.client.cc.gui.views.ClassView;
import eidolons.client.cc.gui.views.HeroItemView;
import eidolons.client.cc.logic.spells.LibraryManager;
import eidolons.client.cc.logic.spells.SpellUpgradeMaster;
import eidolons.client.dc.Launcher;
import main.content.*;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.ItemEnums.WEAPON_CLASS;
import main.content.enums.entity.SpellEnums;
import main.content.enums.entity.SpellEnums.SPELL_UPGRADE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.elements.conditions.RequirementsManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.item.DC_HeroSlotItem;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import main.entity.type.ObjType;
import eidolons.game.core.game.DC_Game;
import main.game.logic.event.MessageManager;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.town.Shop;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.*;

public class HeroManager {
    // protected static final String DUMMY_ITEM = "Two-handed item";
    protected Map<Unit, Stack<ObjType>> typeStacks = new HashMap<>();
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

    public static boolean isQuickItem(Entity type) {
        // what about small weapons?!
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
            return true;
        }
        return isQuickSlotWeapon(type);
    }

    public static boolean isQuickSlotWeapon(Entity type) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) {
            if (type.checkSingleProp(G_PROPS.WEAPON_SIZE, ItemEnums.WEAPON_SIZE.SMALL + "")
             || type.checkSingleProp(G_PROPS.WEAPON_SIZE, ItemEnums.WEAPON_SIZE.TINY + "")
             || type.checkSingleProp(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO + "")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isQuickSlotOnly(Entity type) {
        return type.checkSingleProp(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO + "");
    }

    public void removeHero(Unit hero) {
        typeStacks.remove(hero);
    }

    public void afterDefeatRewind() {
        game.setSimulation(true);
        for (Unit hero : PartyHelper.getParty().getMembers()) {
            stepBack(hero);
        }
    }

    public void prebattleCleanSave() {
        clearStacks();
        for (Unit hero : PartyHelper.getParty().getMembers()) {
            saveHero(hero);
        }
    }

    public void clearStacks() {
        typeStacks.clear();
    }

    public void addHero(Unit hero) {
        Stack<ObjType> stack = new Stack<>();
        typeStacks.put(hero, stack);
    }

    // for DC
    public boolean undo(Unit hero) {
        Stack<ObjType> stack = typeStacks.get(hero);
        if (ListMaster.isNotEmpty(stack)) {
            Entity type = stack.pop();
            applyChangedType(true, hero, type);
            return true;
        }
        return false;
    }

    // for HC
    public void stepBack(Unit hero) {
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

    public void update(Unit hero) {
        update(hero, true);
    }

    public void update(Unit hero, boolean refresh) {
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

            List<Effect> secondLayerEffects = new ArrayList<>();
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
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                }
            }

            for (Effect e : secondLayerEffects) {
                e.apply(Ref.getSelfTargetingRefCopy(hero));
            }
            EffectFinder.applyAttachmentEffects(hero.getMainWeapon(), null);
            EffectFinder.applyAttachmentEffects(hero.getOffhandWeapon(), null);
            EffectFinder.applyAttachmentEffects(hero.getArmor(), null);

        }
        hero.afterEffects();
        hero.setDirty(true);

        if (!hero.getGame().isSimulation()) {
            try {
                hero.resetObjects();
                hero.resetQuickSlotsNumber();
                refreshInvWindow();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            return;
        }
        if (refresh) {
            refresh(hero);
        }
    }

    private void refresh(Unit hero) {
        if (CharacterCreator.isInitialized()) {
            if (CharacterCreator.getHeroPanel(hero) != null) {
                CharacterCreator.getHeroPanel(hero).refresh();
            }
        }
    }

    protected void refreshInvWindow() {
        if (game.getInventoryTransactionManager().getWindow() != null) {
            game.getInventoryTransactionManager().getWindow().refresh();
        }
    }

    protected ITEM_SLOT getItemSlot(Unit hero, Entity type) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
            return null;
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) {
            WEAPON_CLASS CLASS = getWeaponClass(type);
            switch (CLASS) {
                case OFF_HAND:
                    if (StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        return ItemEnums.ITEM_SLOT.MAIN_HAND;
                    }
                    if (MessageManager.promptItemSwap(hero.getProperty(G_PROPS.MAIN_HAND_ITEM),
                     hero, type)) {
                        return ItemEnums.ITEM_SLOT.MAIN_HAND;
                    }
                    if (getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == ItemEnums.WEAPON_CLASS.TWO_HANDED
                     || getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == ItemEnums.WEAPON_CLASS.DOUBLE) {
                        return null;
                    }
                    if (StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM))) {
                        return ItemEnums.ITEM_SLOT.OFF_HAND;
                    }
                    if (MessageManager.promptItemSwap(hero.getProperty(G_PROPS.OFF_HAND_ITEM),
                     hero, type)) {
                        return ItemEnums.ITEM_SLOT.OFF_HAND;
                    }
                    return null;
                case MAIN_HAND_ONLY:
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        if (!MessageManager.promptItemSwap(
                         hero.getProperty(G_PROPS.MAIN_HAND_ITEM), hero, type)) {
                            return null;
                        }
                    }
                    return ItemEnums.ITEM_SLOT.MAIN_HAND;

                case OFF_HAND_ONLY:
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.OFF_HAND_ITEM))) {
                        return null;
                    }
                    if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.MAIN_HAND_ITEM))) {
                        if (getWeaponClass(hero.getProperty(G_PROPS.MAIN_HAND_ITEM)) == ItemEnums.WEAPON_CLASS.TWO_HANDED) {
                            return null;
                        }
                    }
                    return ItemEnums.ITEM_SLOT.OFF_HAND;
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
                    return ItemEnums.ITEM_SLOT.MAIN_HAND;
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
        return ItemEnums.ITEM_SLOT.ARMOR;
    }

    protected WEAPON_CLASS getWeaponClass(String type) {
        ObjType objType = DataManager.getType(type, DC_TYPE.WEAPONS);
        if (objType == null) {
            objType = game.getObjectById(StringMaster.getInteger(type)).getType();
        }
        return getWeaponClass(objType);
    }

    protected WEAPON_CLASS getWeaponClass(Entity type) {
        return new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class, type
         .getProperty(G_PROPS.WEAPON_CLASS));
    }

    public int addSlotItem(Unit hero, Entity type, boolean alt) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.JEWELRY) {
            return addJewelryItem(hero, type);
        }

        ITEM_SLOT slot = null;
        if (!alt) {
            slot = getItemSlot(hero, type);
        }
        if (slot == null) {
            if (!alt) {
                if (isQuickSlotWeapon(type)) {
                    return 0;
                }
            }
            if (isQuickItem(type)) {
                // type.getProp(G_PROPS.CUSTOM_SOUNDSET)
                return addQuickItem(hero, type);
            }

            return 0;
        }
        return setHeroItem(hero, slot, type);

    }

    protected int addJewelryItem(Unit hero, Entity type) {
        boolean amulet = type.checkProperty(G_PROPS.JEWELRY_TYPE, "" + ItemEnums.JEWELRY_TYPE.AMULET);
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

    public boolean checkCanEquipJewelry(Unit hero, ObjType itemType) {
        boolean amulet = itemType.checkProperty(G_PROPS.JEWELRY_TYPE, "" + ItemEnums.JEWELRY_TYPE.AMULET);
        if (amulet) {
            return chechHasNoAmulet(hero);
        } else {
            return checkCanEquipJewelry(hero);
        }
    }

    public boolean checkCanEquipJewelry(Unit hero) {
        boolean hasNoAmulet = chechHasNoAmulet(hero);
        int size = hero.getJewelry().size();
        if (!hasNoAmulet) {
            size++;
        }
        return size < JewelrySlots.LIST_SIZE;
    }

    private boolean chechHasNoAmulet(Unit hero) {
        return getSortedJewelry(hero).get(JewelrySlots.AMULET_INDEX) == null;
    }

    private List<ObjType> getSortedJewelry(Unit hero) {
        List<ObjType> sortedJewelryData = JewelrySlots
         .getSortedJewelryData(new ListMaster<DC_JewelryObj>().convertToTypeList(
          hero
           .getJewelry()));
        return sortedJewelryData;
    }

    public int addQuickItem(Unit hero, Entity type) {
        if (hero.isQuickSlotsFull()) {
            return 0;
        }
        saveHero(hero);
        addItem(hero, type, C_OBJ_TYPE.ITEMS, PROPS.QUICK_ITEMS, true, false);
        removeItem(hero, type, PROPS.INVENTORY, C_OBJ_TYPE.ITEMS, true, false);
        update(hero);
        return 1;

    }

    public boolean addItem(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP) {
        return addItem(hero, type, TYPE, PROP, false);

    }

    public boolean addItem(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP, boolean free) {
        return addItem(hero, type, TYPE, PROP, free, !trainer);
    }

    public boolean tryIncrementRank(Unit hero, ObjType type) {
        boolean skill = type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS;
        DC_FeatObj feat = hero.getFeat(skill, type);
        if (!checkCanIncrementRank(type, feat, hero)) {
            return false;
        }
        if (!hero.incrementFeatRank(skill, type)) {
            return false;
        }
        saveHero(hero);
        hero.saveRanks(skill);
        // DC_SoundMaster.playSkillAddSound(STD_SOUNDS.ButtonUp);
        int xpCost = feat.getIntParam(PARAMS.XP_COST) * feat.getIntParam(PARAMS.RANK_XP_MOD) / 100;
        hero.modifyParameter(PARAMS.XP, -xpCost);
        update(hero);
        return true;
    }

    protected boolean checkCanIncrementRank(ObjType type, DC_FeatObj feat, Unit hero) {
        if (feat == null) {
            return false;
        }
        boolean skill = type.getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS;
        if (skill) {
            return feat.getGame().getRequirementsManager().check(hero, feat,
             RequirementsManager.RANK_MODE) == null;
        }
        List<String> reasons = feat.getGame().getRequirementsManager().checkRankReqs(feat);
        return reasons.isEmpty();
    }

    public boolean addItem(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY PROP,
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

        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.CLASSES) {
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

        if (TYPE.equals(DC_TYPE.SPELLS)) {
            if (PROP.equals(PROPS.VERBATIM_SPELLS)) {
                SpellUpgradeMaster.removeUpgrades(hero, type);
            }
        }
        // TODO preCheck if hero now has the prop!
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
        if (T == DC_TYPE.SPELLS) {
            if (p == PROPS.VERBATIM_SPELLS) {
                return RequirementsManager.VERBATIM_MODE;
            }
        }
        return RequirementsManager.NORMAL_MODE;
    }

    private void classAdded(Unit hero, Entity class_type) {
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

    public boolean addSpellUpgrade(Unit hero, Entity type, PROPERTY prop) {
        saveHero(hero);
        subtractCost(hero, type, DC_TYPE.SPELLS, prop);
        boolean result = LibraryManager.replaceSpellVersion(hero, type, PROPS.VERBATIM_SPELLS);
        update(hero);
        return result;

    }

    public String checkRequirements(Unit hero, Entity type, int mode) {
        return hero.getGame().getRequirementsManager().check(hero, type, mode);
    }

    protected boolean checkNoDuplicates(OBJ_TYPE TYPE) {
        if (TYPE instanceof DC_TYPE) {
            switch ((DC_TYPE) TYPE) {
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

    protected MainPanel getPanel(Unit hero) {
        return CharacterCreator.getHeroPanel(hero);
    }

    public int subtractCost(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY p) {
        int cost = modifyCostParam(hero, type, TYPE, false, p);
//        update(hero);
        return cost;
    }

    protected Integer modifyCostParam(Unit hero, Entity type, OBJ_TYPE TYPE, boolean selling,
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

    protected boolean checkCost(Unit hero, Entity type, OBJ_TYPE t, PROPERTY PROP) {
        if (CoreEngine.isArcaneVault()) {
            return true;
        }
        return hero.checkParam(t.getParam(), ""
         + new Formula(getCost(type, hero, t, PROP)).getInt(hero.getRef()));
    }

    public Unit getHero(ObjType type) {
        for (Unit hero : typeStacks.keySet()) {
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

    public void saveHero(Unit hero) {
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

    public void modifyHeroParam(Unit hero, PARAMETER param, int amount) {
        hero.modifyParameter(param, amount);
        hero.getType().modifyParameter(param, amount);
        getPanel(hero).refresh();
    }

    public void applyChangedType(Unit hero, Entity type) {
        applyChangedType(false, hero, type);
    }

    public void applyChangedType(boolean dont_save, Unit hero, Entity type) {
        if (!dont_save) {
            saveHero(hero);
        }
        game.initType((ObjType) type);
        // hero.applyType(type);
        hero.setType((ObjType) type);
        hero.cloneMaps(type);
        update(hero);

    }

    public int setHeroItem(Unit hero, ITEM_SLOT slot, Entity type) {

        saveHero(hero);
        int result = 1;
        if (!StringMaster.isEmpty(hero.getProperty(slot.getProp()))) {
            removeSlotItem(hero, slot, false);
            result++;
        }
        hero.getType().setProperty(slot.getProp(), type.getNameOrId());
        if (getWeaponClass(type) == ItemEnums.WEAPON_CLASS.TWO_HANDED
         || getWeaponClass(type) == ItemEnums.WEAPON_CLASS.DOUBLE) {

        }

        hero.getType().removeProperty(PROPS.INVENTORY, type.getNameOrId());
        update(hero, false);
        if (hero.getGame().isSimulation()) {
            AbilityConstructor.constructActives(hero);
        }
        refresh(hero);

        return result;
    }

    public void removeSlotItem(Unit hero, ITEM_SLOT slot) {
        removeSlotItem(hero, slot, true);
    }

    public void removeSlotItem(Unit hero, ITEM_SLOT slot, boolean save) {
        if (save) {
            saveHero(hero);
        }
        if (save) {
            if (slot == ItemEnums.ITEM_SLOT.MAIN_HAND) {
                if (hero.getOffhandWeapon() != null) {
                    if (hero.getOffhandWeapon().isWeapon()) {
                        if (!hero.getOffhandWeapon().checkSingleProp(G_PROPS.WEAPON_CLASS,
                         ItemEnums.WEAPON_CLASS.OFF_HAND_ONLY + "")) {
                            removeSlotItem(hero, ItemEnums.ITEM_SLOT.OFF_HAND, false);
                            setHeroItem(hero, slot, hero.getOffhandWeapon().getType());
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
        // game.getInventoryTransactionManager().getInvListManager().operationDone();
    }

    public void removeQuickSlotItem(Unit hero, Entity type) {
        removeContainerItem(hero, type, PROPS.QUICK_ITEMS);
    }

    public void removeContainerItem(Unit hero, Entity type, PROPS p) {
        removeContainerItem(hero, type, p, true);
    }

    public void removeContainerItem(Unit hero, Entity type, PROPS p, boolean update) {
        saveHero(hero);

        hero.getType().removeProperty(p, type.getNameOrId());
        hero.getType().addProperty(PROPS.INVENTORY, type.getNameOrId(), false);
        update(hero);
    }

    public void removeJewelryItem(Unit hero, Entity type) {
        removeContainerItem(hero, type, PROPS.JEWELRY);

    }

    public void removeItem(Unit hero, Entity type, PROPERTY prop, OBJ_TYPE TYPE, boolean free) {
        removeItem(hero, type, prop, TYPE, free, true);
    }

    public void removeItem(Unit hero, Entity type, PROPERTY prop, OBJ_TYPE TYPE,
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

    public boolean addMemorizedSpell(Unit hero, Entity type) {

        if (hero.calculateRemainingMemory() >= type.getIntParam(PARAMS.SPELL_DIFFICULTY)) {
            if (type.isUpgrade()) {
                if (LibraryManager.hasSpellVersion(hero, type, PROPS.MEMORIZED_SPELLS)) {
                    return false;
                }
            }
            return addItem(hero, type, DC_TYPE.SPELLS, PROPS.MEMORIZED_SPELLS, !trainer
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
        Unit hero = CharacterCreator.getHero();
        DC_SpellObj spell = hero.getSpell(entity.getName());
        boolean verbatim = spell.getSpellPool() == SpellEnums.SPELL_POOL.VERBATIM;
        if (!SpellUpgradeMaster.checkUpgrade(verbatim, hero, spell, selected)) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
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
            DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_UPGRADE_LEARNED);
        } else {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.SPELL_UPGRADE_UNLEARNED);
        }
        if (!verbatim) {
            update(hero);
        }
    }

}
