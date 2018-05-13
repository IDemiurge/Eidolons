package eidolons.libgdx.gui.panels.headquarters.datasource;

import eidolons.entity.active.DC_SpellObj;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.DC_JewelryObj;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.game.module.herocreator.logic.skills.SkillMaster;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HqOperation;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.tabs.spell.HqSpellMaster;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.PropMap;
import main.entity.type.ObjType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by JustMe on 4/15/2018.
 */
public class HqDataMaster {
    static Map<Unit, HqDataMaster> map = new HashMap<>();
    Unit hero;
    HeroDataModel heroModel;
    Stack<Pair<ParamMap, PropMap>> stack;
    private boolean dirty;

    public HqDataMaster(Unit hero) {
        this.hero = hero;
        heroModel = createHeroDataModel(hero);
        stack = new Stack<>();
        map.put(hero, this);
    }

    public static HeroDataModel getHeroModel(Unit hero) {
        return map.get(hero).getHeroModel();
    }

    public static HqHeroDataSource getHeroDataSource(Unit hero) {
        return new HqHeroDataSource(map.get(hero).getHeroModel());
    }

    public static void saveHero(HeroDataModel model) {
        map.get(model.getHero()).save();
    }

    public static void operation(HqHeroDataSource dataSource,
                                 HQ_OPERATION operation,
                                 Object... args) {
        operation(dataSource.getEntity(), operation, args);
    }

    public static void operation(HeroDataModel model,
                                 HQ_OPERATION operation,
                                 Object... args) {
        HqDataMaster master = getInstance(model.getHero());

        master.applyOperation(model,
         operation, args);
        model.modified(operation, args);
        master.reset();
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

    public static HqDataMaster getInstance(Unit unit) {
        HqDataMaster instance = map.get(unit);
        if (instance == null) {
            instance = new HqDataMaster(unit);
        }
        return instance;
    }

    public static void exit() {
        map.clear();
    }

    public void save() {
        applyModifications();

    }

    public static void undoAll(HeroDataModel entity) {
        map.get(entity.getHero()).undoAll_();
    }
    public void undoAll_() {
        undo_(true);
    }
    public static void undo() {
        undo(HqMaster.getActiveHero());
    }
        public static void undo(Unit hero) {
        map.get(hero).undo_();
    }
    public void undo_( ) {
        undo_(false);
    }
        public void undo_(boolean all) {
        if (heroModel.getModificationList().isEmpty())
            return;
        List<HqOperation> list = heroModel.getModificationList();
        heroModel = createHeroDataModel(hero);
        if (all)
            list.clear();
        else
            list.remove(list.size() - 1);
        heroModel.setModificationList(list);
        applyModifications(true);
        heroModel.reset();
        if (HqPanel.getActiveInstance()!=null )
            HqPanel.getActiveInstance().setUserObject(new HqHeroDataSource(heroModel));
//       reset();
    }

    private HeroDataModel createHeroDataModel(Unit hero) {
        return new HeroDataModel(hero) {
            @Override
            public Unit getHero() {
                return hero;
            }
        };
    }

    public void applyModifications() {
        applyModifications(false);
    }

    public void applyModifications(boolean self) {
        Unit hero = self ? heroModel : heroModel.getHero();
        //QUIET MODE ETC , LOCK THINGS!
        try {
            for (HqOperation operation : heroModel.getModificationList()) {
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

    public void applyItemOperation(Unit hero, HQ_OPERATION operation, Object... args) {
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
                hero.removeFromInventory(item);
                hero.addQuickItem((DC_QuickItemObj) item);
                break;
        }
    }

    public void applyOperation(Unit hero, HQ_OPERATION operation, Object... args) {

        switch (operation) {
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
                hero.modifyParameter((PARAMETER) args[0], 1, true);
                PointMaster.increased((PARAMETER) args[0],   hero);
                break;
            case MASTERY_INCREMENT:
                hero.modifyParameter((PARAMETER) args[0], 1, true);
                PointMaster.increased((PARAMETER) args[0],   hero);
                SkillMaster.masteryIncreased(hero, (PARAMETER) args[0]);
                break;
            case NEW_MASTERY:
                break;
            case NEW_CLASS:
                SkillMaster.newClass(hero,(ObjType) args[0]);
                break;
            case NEW_PERK:
                SkillMaster.newPerk(hero,(ObjType) args[0]);
                break;
            case NEW_SKILL:
                SkillMaster.newSkill(hero,(ObjType) args[0]);
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
                HeroLevelManager.levelUp(hero);
                break;
        }
    }

    private void applySpellOperation(Unit hero, HQ_OPERATION operation, Object... args) {
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
