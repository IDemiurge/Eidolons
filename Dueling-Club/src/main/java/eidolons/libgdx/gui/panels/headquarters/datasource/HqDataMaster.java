package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.ability.InventoryTransactionManager;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PROPS;
import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.creation.HcHeroModel;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HeroOperation;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.HqSpellMaster;
import eidolons.system.math.DC_MathManager;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PROPERTY;
import main.content.values.properties.PropMap;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqDataMaster {
    static Map<Unit, HqDataMaster> map = new HashMap<>();
    Unit hero;
    HeroDataModel heroModel;
    Stack<Pair<ParamMap, PropMap>> stack;
    private boolean dirty;
    private Stack<List<HeroOperation>> undoStack=new Stack();

    public HqDataMaster(Unit hero) {
        this.hero = hero;
        try {
            heroModel = createHeroDataModel(hero);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            if (map.get(hero) != null)
                heroModel = map.get(hero).getHeroModel();
        }
        stack = new Stack<>();
    }

    public static HeroDataModel getHeroModel(Unit hero) {
        return map.get(hero).getHeroModel();
    }

    public static HqHeroDataSource getHeroDataSource(Unit hero) {
        if (map.get(hero) == null) {
            createAndSaveInstance(hero);
        }
        return new HqHeroDataSource(map.get(hero).getHeroModel());
    }

    public static void saveAll() {
        for (Unit sub : map.keySet()) {
            map.get(sub).save();
        }
    }

    public static void saveHero(HeroDataModel model) {
        saveHero(model, false, false);
    }

    public static void saveHero(HeroDataModel model, boolean type, boolean asNew) {
        map.get(model.getHero()).save();
        EUtils.showInfoText(model.getName() + " saved");
        if (type) {
            if (asNew)
                model.setName(NameMaster.getUniqueVersionedName(model.getName(), DC_TYPE.CHARS));

            updateType(model);
            DataManager.addType(model.getType());
            XML_Writer.writeXML_ForType(model.getType(), DC_TYPE.CHARS, model.getGroupingKey());
        }
    }

    public static void updateType(HeroDataModel model) {
        for (PROPERTY sub : InventoryTransactionManager.INV_PROPS) {
            String val = model.getProperty(sub);
            String newVal = "";
            for (String substring : ContainerUtils.openContainer(val)) {
                if (!NumberUtils.isInteger(substring))
                    continue;
                Integer id = NumberUtils.getInteger(substring);
                if (id != 0) {
                    Obj obj = model.getGame().getObjectById(id);
                    if (obj == null)
                        continue;
                    newVal += obj.getName() + ";";
                }
            }
            if (newVal.isEmpty())
                continue;
            main.system.auxiliary.log.LogMaster.log(1, model + " updates type with " +
             sub + "==" + newVal);
            model.getType().setProperty(sub, newVal.substring(0,
             newVal.length() - 1));
        }

    }

    public static void operation(HqHeroDataSource dataSource,
                                 HERO_OPERATION operation,
                                 Object... args) {
        operation(dataSource.getEntity(), operation, args);
    }

    public static void operation(HeroDataModel model,
                                 HERO_OPERATION operation,
                                 Object... args) {
        //        new Thread(() -> {
        HqDataMaster master = getInstance(model.getHero());
        master.applyOperation(model,
         operation, args);
        model.modified(operation, args);
        master.reset();
        //        }, operation+ " hq operation thread").start();

    }

    private static DC_HeroItemObj getItem(Unit hero, Object arg) {
        DC_HeroItemObj item = (DC_HeroItemObj) arg;
        if (hero instanceof HeroDataModel) {
            if (item.isSimulation())
                return item;
            else {
                //create sim item
                return (DC_HeroItemObj) HqMaster.getSimCache().getSim(item);
            }
        } else {
            if (item.isSimulation())
                //                return (DC_HeroItemObj) hero.getGame().getObjectById(item.getId());
                return (DC_HeroItemObj) HqMaster.getSimCache().getReal(item);
            else
                return item;

        }
    }

    public static void modelChanged(HeroDataModel entity) {
        map.get(entity.getHero()).reset();
    }

    public static HqDataMaster createInstance(Unit unit) {
        return new HqDataMaster(unit);
    }

    public static HqDataMaster createAndSaveInstance(Unit unit) {
        HqDataMaster instance = new HqDataMaster(unit);
        map.put(unit, instance);
        return instance;
    }

    public static HqDataMaster getInstance(Unit unit) {
        HqDataMaster instance = map.get(unit);
        if (instance == null) {
            instance = new HqDataMaster(unit);
            map.put(unit, instance);
        }
        return instance;
    }

    public static void exit() {
        map.clear();
    }

    public static void undoAll(HeroDataModel entity) {
        map.get(entity.getHero()).undoAll_();
    }

    public static void undo() {
        undo(HqMaster.getActiveHero());
    }

    public static void undo(Unit hero) {
        map.get(hero).undo_();
    }
    public static void redo(Unit hero) {
        map.get(hero).redo_();
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
        List<HeroOperation> list =new ArrayList<>( heroModel.getModificationList());

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
        } else {
            if (HeroCreationMaster.isHeroCreationInProgress())
                HeroCreationPanel.getInstance().setUserObject(new HqHeroDataSource(heroModel));
        }
    }
    private void redo_() {
        List<HeroOperation> list = undoStack.pop();
        setModificationList(list);
    }


    private HeroDataModel createHeroDataModel(Unit hero) {
        if (HeroCreationMaster.isHeroCreationInProgress())
        {
            return new HcHeroModel(hero);
        }
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

    public void applyItemOperation(Unit hero, HERO_OPERATION operation, Object... args) {
        DC_HeroItemObj item = getItem(hero, args[0]);
        switch (operation) {
            case PICK_UP:
                hero.addItemToInventory((DC_HeroItemObj) args[0]); //TODO fix pickup!
                break;
            case DROP:
                hero.dropItemFromInventory(item);
                break;
            case UNEQUIP_JEWELRY:
                hero.removeJewelryItem(item);
                hero.addItemToInventory(item);
                break;
            case UNEQUIP:
                hero.unequip(item, false);
                break;
            case UNEQUIP_QUICK_SLOT:
                hero.removeQuickItem((DC_QuickItemObj) item);
                hero.addItemToInventory(item);
                break;
            case EQUIP:
                hero.removeFromInventory(item);
                if (item instanceof DC_JewelryObj) {
                    hero.addJewelryItem((DC_JewelryObj) item);
                } else
                    hero.equip(item, (ITEM_SLOT) args[1]);
                break;
            case EQUIP_QUICK_SLOT:
                hero.unequip(item, false);
                hero.removeFromInventory(item);
                if (item instanceof DC_WeaponObj) {
                    hero.addQuickItem(new DC_QuickItemObj(((DC_WeaponObj) item)));

                } else
                    hero.addQuickItem((DC_QuickItemObj) item);
                break;
        }
    }

    public void applyOperation(Unit hero, HERO_OPERATION operation, Object... args) {

        switch (operation) {
            case APPLY_TYPE:
                String imagePath = hero.getImagePath();
                hero.applyType((ObjType) args[0]);
                for (PARAMETER item : DC_ContentValsManager.getAttributes()) {
                    hero.getType(). modifyParameter((item), 5, null , true );
                }
                for (PARAMETER item : DC_ContentValsManager.DYNAMIC_PARAMETERS ) {
                    hero.setParameter(DC_ContentValsManager.getPercentageParam(item),
                     DC_MathManager.PERCENTAGE);
                }
                hero.setImage(imagePath);
                hero.setGroup("Custom", true);
                break;
            case SET_PROPERTY:
                hero.setProperty((PROPERTY) args[0], args[1].toString(), true);
                break;
            case PICK_UP:
            case DROP:
            case UNEQUIP_JEWELRY:
            case UNEQUIP:
            case UNEQUIP_QUICK_SLOT:
            case EQUIP:
            case EQUIP_QUICK_SLOT:
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
                SkillMaster.newClass(hero, (ObjType) args[0]);
                break;
            case NEW_PERK:
                SkillMaster.newPerk(hero, (ObjType) args[0]);
                break;
            case NEW_SKILL:
                SkillMaster.newSkill(hero, (ObjType) args[0]);
                break;
            case SKILL_RANK:
                break;
            case CLASS_RANK:
                break;
            case SPELL_LEARNED:
            case SPELL_MEMORIZED:
            case SPELL_EN_VERBATIM:
            case SPELL_UNMEMORIZED:
                applySpellOperation(hero, operation, args);
                break;
            case LEVEL_UP:
                int i =1;
                if (args.length>0)
                    i = (int) args[0];
                for (int j = 0; j < i; j++) {
                    HeroLevelManager.levelUp(hero);
                }
                break;
        }
    }

    private void applySpellOperation(Unit hero, HERO_OPERATION operation, Object... args) {
        DC_SpellObj spell = (DC_SpellObj) args[0];
        switch (operation) {
            case SPELL_LEARNED:
                HqSpellMaster.learnSpell(hero, spell);
                break;
            case SPELL_MEMORIZED:
                HqSpellMaster.memorizeSpell(hero, spell);
                break;
            case SPELL_EN_VERBATIM:
                HqSpellMaster.learnSpellEnVerbatim(hero, spell);
                break;
            case SPELL_UNMEMORIZED:
                HqSpellMaster.unmemorizeSpell(hero, spell);
                //                CharacterCreator.getHeroManager().removeContainerItem(hero, spell);
                break;
        }
    }

    private void reset() {
        dirty = true;
        heroModel.reset();
        if (HqPanel.getActiveInstance() != null) {
            HqPanel.getActiveInstance().modelChanged();
        } else {
            if (HeroCreationMaster.isHeroCreationInProgress()) {
                HeroCreationPanel.getInstance().modelChanged();
            }
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
