package libgdx.gui.dungeon.panels.headquarters.datasource;

import eidolons.ability.InventoryTransactionManager;
import eidolons.content.ContentConsts;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.feat.active.Spell;
import eidolons.netherflame.eidolon.heromake.NF_ProgressionMaster;
import eidolons.entity.item.HeroItem;
import eidolons.entity.item.trinket.JewelryItem;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.DroppedItemManager;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.entity.item.vendor.GoldMaster;
import eidolons.netherflame.eidolon.heromake.model.PointMaster;
import eidolons.netherflame.eidolon.heromake.passives.SkillMaster;
import eidolons.system.libgdx.datasource.HeroDataModel;
import eidolons.system.libgdx.datasource.HeroOperation;
import libgdx.gui.dungeon.panels.headquarters.HqMaster;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.panels.headquarters.tabs.spell.HqSpellMaster;
import libgdx.gui.dungeon.panels.headquarters.town.TownPanel;
import eidolons.game.battlecraft.logic.meta.universal.shop.Shop;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.math.DC_MathManager;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqDataMaster {
    private static Map<Unit, HqDataMaster> map = new HashMap<>();
    protected boolean dirty;
    protected Stack<List<HeroOperation>> undoStack = new Stack();
    Unit hero;
    HeroDataModel heroModel;
    Stack<Pair<ParamMap, PropMap>> stack;

    public HqDataMaster(Unit hero) {
        this.hero = hero;
        try {
            heroModel = createHeroDataModel(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            if (getMap().get(hero) != null)
                heroModel = getMap().get(hero).getHeroModel();
        }
        stack = new Stack<>();
    }

    public static HeroDataModel getHeroModel(Unit hero) {
        return getMap().get(hero).getHeroModel();
    }

    public static HqHeroDataSource getHeroDataSource(Unit hero) {
        if (getMap().get(hero) == null) {
            createAndSaveInstance(hero);
        }
        return new HqHeroDataSource(getMap().get(hero).getHeroModel());
    }

    public static void saveAll() {
        for (Unit sub : getMap().keySet()) {
            getMap().get(sub).save();
        }
    }

    public static void saveHero(HeroDataModel model) {
        saveHero(model, false, false);
    }

    public static void saveHero(HeroDataModel model, boolean type, boolean asNew) {
        getMap().get(model.getHero()).save();
        EUtils.showInfoText(model.getName() + " saved");
        if (type) {
            if (asNew)
                model.setName(getNewSaveHeroName(model));

            updateType(model);
            DataManager.addType(model.getType());
            XML_Writer.writeXML_ForType(model.getType(), DC_TYPE.CHARS, model.getGroupingKey());
        }
    }

    private static String getNewSaveHeroName(HeroDataModel model) {
        if (Flags.isIggDemo()) {
            return model.getName()+" lvl " + model.getIntParam(PARAMS.HERO_LEVEL);
        }
        return NameMaster.getUniqueVersionedName(model.getName(), DC_TYPE.CHARS);
    }

    public static void updateType(HeroDataModel model) {
        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {
            String val = model.getProperty(sub);
            StringBuilder newValBuilder = new StringBuilder();
            for (String substring : ContainerUtils.openContainer(val)) {
                if (!NumberUtils.isInteger(substring))
                    continue;
                Integer id = NumberUtils.getIntParse(substring);
                if (id != 0) {
                    Obj obj = model.getGame().getObjectById(id);
                    if (obj == null)
                        continue;
                    newValBuilder.append(obj.getName()).append(";");
                }
            }
            String newVal = newValBuilder.toString();
            if (newVal.isEmpty())
                continue;
            main.system.auxiliary.log.LogMaster.log(1, model + " updates type with " +
                    sub + "==" + newVal);
            model.getType().setProperty(sub, newVal.substring(0,
                    newVal.length() - 1));
        }

    }

    public static void operation(HqHeroDataSource dataSource,
                                 HeroEnums.HERO_OPERATION operation,
                                 Object... args) {
        operation(dataSource.getEntity(), operation, args);
    }

    public static void operation(HeroDataModel model,
                                 HeroEnums.HERO_OPERATION operation,
                                 Object... args) {
        //        new Thread(() -> {
        Core.onThisOrNonGdxThread(() -> {
            HqDataMaster master = getInstance(model.getHero());
            master.applyOperation(model,
                    operation, args);
            model.modified(operation, args);
            master.reset();
        });
        //        }, operation+ " hq operation thread").start();

    }

    public static Map<Unit, HqDataMaster> getMap() {
        return map;
    }

    public static void setMap(Map<Unit, HqDataMaster> map) {
        HqDataMaster.map = map;
    }

    protected static HeroItem getItem(Unit hero, Object arg) {
        HeroItem item = (HeroItem) arg;
        if (hero instanceof HeroDataModel) {
            if (item.isSimulation())
                return item;
            else {
                //create sim item
                HeroItem simItem = (HeroItem) HqMaster.getSimCache().getSim(item);
                if (simItem == null) {
                    return item;
                }
                return simItem;
            }
        } else {
            if (item.isSimulation())
            //                return (DC_HeroItemObj) hero.getGame().getObjectById(item.getId());
            {
                HeroItem realItem = (HeroItem) HqMaster.getSimCache().getReal(item);
                if (realItem == null) {
                    return item;
                }
                return realItem;
            } else
                return item;

        }
    }

    public static void modelChanged(HeroDataModel entity) {
//        getMap().getVar(entity.getHero()).reset(); TODO causes double reset; why? and what is it for?
    }

    public static HqDataMaster createInstance(Unit unit) {
        if (isSimulationOff()) {
            return new HqDataMasterDirect(unit);
        }
        return new HqDataMaster(unit);
    }

    public static boolean isSimulationOff() {
        return true;
    }

    public static HqDataMaster createAndSaveInstance(Unit unit) {
        HqDataMaster instance = createInstance(unit);
        getMap().put(unit, instance);
        return instance;
    }

    public static HqDataMaster getInstance() {
        return getInstance(Core.getMainHero());
    }

    public static HqDataMaster getInstance(Unit unit) {
        HqDataMaster instance = getMap().get(unit);
        if (instance == null) {
            instance = createInstance(unit);
            getMap().put(unit, instance);
        }
        return instance;
    }

    public static void exit() {
        getMap().clear();
    }

    public static void undoAll(HeroDataModel entity) {
        getMap().get(entity.getHero()).undoAll_();
    }

    public static void undo() {
        undo(HqMaster.getActiveHero());
    }

    public static void undo(Unit hero) {
        getMap().get(hero).undo_();
    }

    public static void redo(Unit hero) {
        getMap().get(hero).redo_();
    }

    public static HqDataMaster getOrCreateInstance(Unit unit) {
        HqDataMaster dataMaster;
        if (HqPanel.getActiveInstance() == null
                && TownPanel.getActiveInstance() == null)
            dataMaster = HqDataMaster.createAndSaveInstance(unit);
        else
            dataMaster = HqDataMaster.getInstance(unit);
        return dataMaster;
    }

    public void operation(HeroEnums.HERO_OPERATION operation,
                          Object... args) {
        applyOperation(heroModel,
                operation, args);
        heroModel.modified(operation, args);
        heroModel.reset();
    }

    public void save() {
        applyModifications();

    }

    public void undoAll_() {
        undo_(true);
    }

    public void undo_() {
        undo_(false);
    }

    public void undo_(boolean all) {
        if (heroModel.getModificationList().isEmpty())
            return;
        List<HeroOperation> list = new ArrayList<>(heroModel.getModificationList());

        if (all)
            list.clear();
        else
            list.remove(list.size() - 1);

        undo_(list);
    }

    public void undo_(List<HeroOperation> list) {
        undoStack.add(heroModel.getModificationList());
        setModificationList(list);
    }

    public void setModificationList(List<HeroOperation> list) {
        heroModel = createHeroDataModel(hero);
        heroModel.setModificationList(list);
        applyModifications(true);
        heroModel.reset();
        if (HqPanel.getActiveInstance() != null) {
            HqPanel.getActiveInstance().setUserObject(new HqHeroDataSource(heroModel));
        }
    }

    protected void redo_() {
        List<HeroOperation> list = undoStack.pop();
        setModificationList(list);
    }

    protected HeroDataModel createHeroDataModel(Unit hero) {
        return new HeroDataModel(hero);
    }

    public void applyModifications() {
        applyModifications(false);
    }

    public void applyModifications(boolean self) {
        Unit hero = self ? heroModel : heroModel.getHero();
        //QUIET MODE ETC , LOCK THINGS!
        try {
            for (HeroOperation operation : heroModel.getModificationList()) {
                applyOperation(hero, operation.getOperation(), operation.getArg());
            }
            if (!self) {
                heroModel.getModificationList().clear();
                //           TODO why?     this.hero.resetObjectContainers(false);
                this.hero.reset();
                dirty = false;
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public void applyItemOperation(Unit hero, HeroEnums.HERO_OPERATION operation, Object... args) {
        HeroItem item = getItem(hero, args[0]);
        switch (operation) {
            //TODO macro update
            // case STASH:
            //     item = (DC_HeroItemObj) args[0];
            //     if (hero.removeFromInventory(item))
            //         Eidolons.getTown().addToStash(item);
            //     break;
            // case UNSTASH:
            //     item = (DC_HeroItemObj) args[0];
            //     if (Eidolons.getTown().removeFromStash(item))
            //         hero.addItemToInventory(item, true);
            //     break;
            case SELL:
            case BUY:
                item = (HeroItem) args[0]; //TODO fix?
                Shop shop = (Shop) args[1];
                if (operation == HeroEnums.HERO_OPERATION.SELL) {
                    // if (!hero.removeFromInventory(item)) {
                    //     if (!Eidolons.getTown().removeFromStash(item))
                    //         return;
                    // }
                    shop.sellItemTo(item, hero);
//                    hero.modifyParameter(PARAMS.GOLD, price); all gold is handled by ShopItemManager!
                } else {
                    Integer price = shop.buyItemFrom(item, hero);
                    if (price == null)
                        return;
                    hero.addItemToInventory(item);
//                    hero.modifyParameter(PARAMS.GOLD, -price); all gold is handled by ShopItemManager!
                }
                DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__GOLD);
                break;
            case PICK_UP:
                item = (HeroItem) args[0];
                if (GoldMaster.checkGoldPack(item, hero)) {
                    DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__GOLD);
                } else {
                    hero.addItemToInventory(item); //TODO fix pickup!
                    DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__HOVER);
                }
                break;
            case DROP:
                if (!DroppedItemManager.canDropItem(item)) {
                    EUtils.showInfoText("Cannot drop this");
                    return;
                }
                HeroItem finalItem = item;
                GuiEventManager.trigger(GuiEventType.CONFIRM,
                        new ImmutableTriple<String, Runnable, Runnable>("Drop " + item.getName() + "?"
                                , () -> {
                        }, () ->
                                hero.dropItemFromInventory(finalItem))

                );
//                boolean result = (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.CONFIRM);
//                if (result)
//                {
//                    hero.dropItemFromInventory(item);
//                }
                break;
            case UNEQUIP_JEWELRY:
                hero.removeJewelryItem(item);
                hero.addItemToInventory(item, true);
                break;
            case UNEQUIP:
                hero.unequip(item, false);
                break;
            case UNEQUIP_QUICK_SLOT:
                hero.removeQuickItem(item);
                hero.addItemToInventory(item, true);
                break;
            case EQUIP:
            case EQUIP_RESERVE:
                hero.removeFromInventory(item);
                if (item instanceof JewelryItem) {
                    hero.addJewelryItem((JewelryItem) item);
                } else
                    hero.equip(item, (ITEM_SLOT) args[1]);
                break;
            case EQUIP_QUICK_SLOT:
                hero.unequip(item, false);
                hero.removeFromInventory(item);
                if (item instanceof WeaponItem) {
                    hero.addQuickItem(new QuickItem(((WeaponItem) item)));

                } else
                    hero.addQuickItem((QuickItem) item);
                break;
        }
    }

    public void applyOperation(Unit hero, HeroEnums.HERO_OPERATION operation, Object... args) {

        switch (operation) {
            case ADD_PARAMETER:
                hero.modifyParameter((PARAMETER) args[0],
                        Integer.parseInt(args[1].toString()));
                break;

            case SET_PARAMETER:
                hero.setParameter((PARAMETER) args[0], Integer.parseInt(args[1].toString()));
                break;
            case APPLY_TYPE:
                String imagePath = hero.getImagePath();
                hero.applyType((ObjType) args[0]);
                for (PARAMETER item : DC_ContentValsManager.getAttributes()) {
                    hero.getType().modifyParameter((item), 5, null, true);
                }
                for (PARAMETER item : ContentConsts.DYNAMIC_PARAMETERS) {
                    hero.setParameter(DC_ContentValsManager.getPercentageParam(item),
                            DC_MathManager.PERCENTAGE);
                }
                hero.setImage(imagePath);
                hero.setGroup("Custom", true);
                break;
            case SET_PROPERTY:
                hero.setProperty((PROPERTY) args[0], args[1].toString(), true);
                break;
            case BUY:
            case SELL:

            case PICK_UP:
            case DROP:
            case UNEQUIP_JEWELRY:
            case UNEQUIP:
            case UNEQUIP_QUICK_SLOT:
            case EQUIP:
            case EQUIP_RESERVE:
            case EQUIP_QUICK_SLOT:
            case STASH:
            case UNSTASH:
                applyItemOperation(hero, operation, args);
                break;
            case ATTRIBUTE_INCREMENT:
                PointMaster.increaseAttribute((PARAMETER) args[0], hero, 1);
                break;
            case MASTERY_INCREMENT:
                PointMaster.increaseMastery((PARAMETER) args[0], hero, 1);
                break;
            case NEW_MASTERY:
                hero.getType().addProperty(PROPS.UNLOCKED_MASTERIES,
                        ((PARAMETER) args[0]).getName(), true);
                hero.addParam((PARAMETER) args[0], "1", true);
                break;
            case NEW_CLASS:
                SkillMaster.newClass(hero, (ObjType) args[0], (Integer) args[1], (Integer) args[2]);
                break;
            case NEW_PERK:
                SkillMaster.newPerk(hero, (ObjType) args[0], (Integer) args[1], (Integer) args[2]);
                break;
            case NEW_SKILL:
                SkillMaster.newSkill(hero, (ObjType) args[0]
                        , (Integer) args[1], (Integer) args[2]);
                break;
            case SKILL_RANK:
            case CLASS_RANK:
                break;
            case SPELL_LEARNED:
                applySpellOperation(hero, operation, args);
                break;
            case LEVEL_UP:
                int i = 1;
                if (args.length > 0)
                    i = (int) args[0];
                for (int j = 0; j < i; j++) {
                    NF_ProgressionMaster.levelUp(hero);
                }
                break;
        }
    }

    protected void applySpellOperation(Unit hero, HeroEnums.HERO_OPERATION operation, Object... args) {
        Spell spell = (Spell) args[0];
        switch (operation) {
            case SPELL_LEARNED:
                HqSpellMaster.learnSpell(hero, spell);
                break;
        }
        reset();
    }

    protected void reset() {
        dirty = true;
        heroModel.reset();
        if (HqPanel.getActiveInstance() != null) {
            HqPanel.getActiveInstance().modelChanged();
        }
    }

    public HeroDataModel getHeroModel() {
        return heroModel;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
