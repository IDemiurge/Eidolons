package eidolons.game.module.herocreator;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.DC_Formulas;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Core;
import eidolons.game.core.EUtils;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effect;
import main.content.C_OBJ_TYPE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.enums.entity.ItemEnums.WEAPON_CLASS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.elements.conditions.RequirementsManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Attachment;
import main.entity.obj.BuffObj;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.Bools;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

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

    public static Integer getIntCost(Entity type, Entity hero) {
        return
                new Formula(getCost(type, hero, type.getOBJ_TYPE_ENUM(), null)).getInt(hero.getRef());
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
                                 boolean buying, boolean parsed) {
        if (TYPE.getParam() == null) {
            return "";
        }
        PARAMETER costParam = ContentValsManager.getCostParam(TYPE.getParam());
        PARAMETER discountParam = DC_ContentValsManager.getCostReductionParam(costParam, PROP);
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

        discountParam = DC_ContentValsManager.getSpecialCostReductionParam(costParam, PROP);
        if (discountParam != null) {
            mod = hero.getIntParam(discountParam);
            if (mod == 100) {
            } else {
                costFormula.applyFactor((!buying ? "" : "-")
                        + StringMaster.getValueRef(KEYS.SOURCE, discountParam));
            }
        }
        if (parsed) {
            return "" + costFormula.getInt(hero.getRef());
        }
        return "(" + costFormula + ")";
    }

    public static String getCost(Entity type, Entity hero, OBJ_TYPE TYPE, PROPERTY PROP,
                                 boolean buying) {
        return getCost(type, hero, TYPE, PROP, buying, true);
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
            return type.checkSingleProp(G_PROPS.WEAPON_SIZE, ItemEnums.WEAPON_SIZE.SMALL + "")
                    || type.checkSingleProp(G_PROPS.WEAPON_SIZE, ItemEnums.WEAPON_SIZE.TINY + "")
                    || type.checkSingleProp(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO + "");
        }
        return false;
    }

    public static boolean isQuickSlotOnly(Entity type) {
        if (type instanceof DC_QuickItemObj)
            return true;
        return type.checkSingleProp(G_PROPS.WEAPON_TYPE, ItemEnums.WEAPON_TYPE.AMMO + "");
    }

    public static boolean canEquip(Unit hero, Entity item, ITEM_SLOT slot) {
        ITEM_SLOT possible_slot = getItemSlot(hero, item, false);
        if (possible_slot != null)
            if (possible_slot == slot || possible_slot.getReserve() == slot)
                return true;
        possible_slot = getItemSlot(hero, item, false, true);
        if (possible_slot != null)
            return possible_slot == slot || possible_slot.getReserve() == slot;
        return false;
    }

    public static ITEM_SLOT getItemSlot(Unit hero, Entity type) {
        return getItemSlot(hero, type, false);
    }

    public static ITEM_SLOT getItemSlot(Unit hero, Entity type, boolean askSwap) {
        return getItemSlot(hero, type, askSwap, false);
    }

    public static ITEM_SLOT getItemSlot(Unit hero, Entity type, boolean askSwap, boolean alt) {
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.ITEMS) {
            return null;
        }
        if (type.getOBJ_TYPE_ENUM() == DC_TYPE.WEAPONS) {
            WEAPON_CLASS CLASS = getWeaponClass(type);
            switch (CLASS) {
                case OFF_HAND:
                    if (hero.getMainWeapon() == null || (!askSwap && !alt)) {
                        return ItemEnums.ITEM_SLOT.MAIN_HAND;
                    }
                    if (askSwap)
                        if (promptItemSwap(hero.getMainWeapon(),
                                hero, type)) {
                            return ItemEnums.ITEM_SLOT.MAIN_HAND;
                        }
                    if (hero.getMainWeapon().getWeaponClass() == ItemEnums.WEAPON_CLASS.TWO_HANDED
                            || hero.getMainWeapon().getWeaponClass() == ItemEnums.WEAPON_CLASS.DOUBLE) {
                        return null;
                    }
                    if (hero.getOffhandWeapon() == null || (!askSwap && alt)) {
                        return ItemEnums.ITEM_SLOT.OFF_HAND;
                    }
                    if (askSwap)
                        if (promptItemSwap(hero.getOffhandWeapon(),
                                hero, type)) {
                            return ItemEnums.ITEM_SLOT.OFF_HAND;
                        }
                    return null;
                case MAIN_HAND_ONLY:
                    if (hero.getMainWeapon() != null) {
                        if (askSwap)
                            if (!promptItemSwap(
                                    hero.getMainWeapon(), hero, type)) {
                                return null;
                            }
                    }
                    return ItemEnums.ITEM_SLOT.MAIN_HAND;

                case OFF_HAND_ONLY:
                    if (hero.getOffhandWeapon() != null) {
                        if (askSwap)
                            if (!promptItemSwap(
                                    hero.getOffhandWeapon(), hero, type)) {
                                return null;
                            }
                    }
                    if (hero.getMainWeapon() != null) {
                        if (hero.getMainWeapon().getWeaponClass() == ItemEnums.WEAPON_CLASS.TWO_HANDED) {
                            return null;
                        }
                    }
                    return ItemEnums.ITEM_SLOT.OFF_HAND;
                case DOUBLE:
                case TWO_HANDED:
                    if (hero.getOffhandWeapon() != null) {
                        return null;
                    }
                    if (hero.getMainWeapon() != null) {
                        if (askSwap)
                            if (!promptItemSwap(
                                    hero.getMainWeapon(), hero, type)) {
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
        if (!StringMaster.isEmpty(hero.getProperty(G_PROPS.ARMOR_ITEM))
                && hero.getArmor() != null) {
            if (!promptItemSwap(hero.getArmor(), hero, type)) {
                return null;
            }
        }
        return ItemEnums.ITEM_SLOT.ARMOR;
    }

    private static boolean promptItemSwap(Entity item, Unit hero, Entity newItem) {
        EUtils.onConfirm("Do you wish to swap " +
                item.getName() +
                " for " +
                newItem.getName() + "?", true, null);
        return
                Bools.isTrue(
                        (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.CONFIRM));
    }

    public static WEAPON_CLASS getWeaponClass(String type) {
        ObjType objType = DataManager.getType(type, DC_TYPE.WEAPONS);
        if (objType == null) {
            objType = Core.game.getObjectById(NumberUtils.getIntParse(type)).getType();
        }
        return getWeaponClass(objType);
    }

    public static WEAPON_CLASS getWeaponClass(Entity type) {
        return new EnumMaster<WEAPON_CLASS>().retrieveEnumConst(WEAPON_CLASS.class, type
                .getProperty(G_PROPS.WEAPON_CLASS));
    }

    public void removeHero(Unit hero) {
        typeStacks.remove(hero);
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
            EffectMaster.applyAttachmentEffects(hero.getMainWeapon(), null);
            EffectMaster.applyAttachmentEffects(hero.getOffhandWeapon(), null);
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
                main.system.ExceptionMaster.printStackTrace(e);
            }
            return;
        }
        if (refresh) {
            refresh(hero);
        }
    }

    private void refresh(Unit hero) {
    }

    protected void refreshInvWindow() {
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
                if (!promptItemSwap(hero.getAmulet(), hero, type))
                // makes me wonder once again why HC has to deal with types
                // and not objects, would it be hard to write thru to types?
                // Easier than have objects initiatilized like that
                {
                    return 0;
                } else {
                    removeJewelryItem(hero, sortedJewelryData.get(JewelryMaster.AMULET_INDEX));
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
        return size < JewelryMaster.LIST_SIZE;
    }

    private boolean chechHasNoAmulet(Unit hero) {
        return getSortedJewelry(hero).get(JewelryMaster.AMULET_INDEX) == null;
    }

    private List<ObjType> getSortedJewelry(Unit hero) {
        return JewelryMaster
                .getSortedJewelryData(new ListMaster<DC_JewelryObj>().convertToTypeList(
                        hero
                                .getJewelry()));
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

        if (update) {
            saveHero(hero);
        }
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
        if (update) {
            update(hero);
        }

        return true;
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

    public String checkRequirements(Unit hero, Entity type, int mode) {
        return hero.getGame().getRequirementsManager().check(hero, type, mode);
    }

    protected boolean checkNoDuplicates(OBJ_TYPE TYPE) {
        if (TYPE instanceof DC_TYPE) {
            switch ((DC_TYPE) TYPE) {
                case SKILLS:
                case CLASSES:
                case SPELLS:
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    public int subtractCost(Unit hero, Entity type, OBJ_TYPE TYPE, PROPERTY p) {
        //        update(hero);
        return modifyCostParam(hero, type, TYPE, false, p);
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

        if (update) {
            update(hero);
        }
    }


    public boolean isTrainer() {
        return trainer;
    }

    public void setTrainer(boolean trainer) {
        this.trainer = trainer;
    }


}
