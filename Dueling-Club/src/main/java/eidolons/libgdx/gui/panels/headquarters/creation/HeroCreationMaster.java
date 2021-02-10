package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationSequence.HERO_CREATION_ITEM;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.Stack;

import static main.system.threading.WaitMaster.waitForInput;

/**
 * Created by JustMe on 7/3/2018.
 * <p>
 * use hqDataMaster?
 * <p>
 * use
 */
public class HeroCreationMaster {


    public static final boolean NEW_ON_LAUNCH = false;
    public static final boolean TEST_MODE = false;
    public static final boolean FAST_MODE = false;
    private static final int INITIAL_LEVEL = 2;
    public static final boolean HUMAN_ONLY = true;
    static HcHeroModel model;
    private static HERO_CREATION_ITEM currentItem = HERO_CREATION_ITEM.values()[0];
    private static HERO_CREATION_ITEM previousItem;
    private static boolean heroCreationInProgress;
    private static Stack<HERO_CREATION_ITEM> screens = new Stack<>();
    private static Stack<HERO_CREATION_ITEM> backScreens = new Stack<>();

    // what if a single STAGE contains 2 property changes? and we do the 2nd... 1st will be lost?!
    // or we can overwrite ? that will pile up operations...
    // how will it work in stats?
    public static void modified(PROPERTY property, String value) {
        if (checkRevert()) {
            if (confirmRollback())
                model.rollback(currentItem);
            else return;
        }
        HqDataMaster.operation(model,
                HERO_OPERATION.SET_PROPERTY, property, value);

        screens.add(currentItem);
        backScreens.clear();
        if (FAST_MODE) {
            checkNext();
        }

    }

    private static void checkNext() {

    }

    private static boolean confirmRollback() {
        return true;
    }

    public static boolean checkRevert() {
        if (!isRollbackRequired(currentItem))
            return false;
        if (previousItem != null)
            return EnumMaster.getEnumConstIndex(HERO_CREATION_ITEM.class,
                    previousItem) > EnumMaster.getEnumConstIndex(HERO_CREATION_ITEM.class,
                    currentItem);
        return false;
    }

    private static boolean isRollbackRequired(HERO_CREATION_ITEM currentItem) {
        switch (currentItem) {
            case SKILLSET:
            case DEITY:
            case RACE:
                return true;
        }
        return false;
    }

    public static void modified(PARAMETER parameter, String value) {
        HqDataMaster.operation(model,
                HERO_OPERATION.SET_PARAMETER, parameter, value);
    }

    public static void export() {
        HqDataMaster.saveHero(model, true, true);
    }

    public static void cancel() {
        heroCreationInProgress = false;
        WaitMaster.receiveInputIfWaiting(WAIT_OPERATIONS.HC_DONE, null);

    }

    public static void done() {
        heroCreationInProgress = false;
        HqDataMaster.getInstance(model.getHero()).applyModifications();
        WaitMaster.receiveInputIfWaiting(WAIT_OPERATIONS.HC_DONE, model.getHero());
    }

    public static Unit newHero() {
        screens = new Stack<>();
        backScreens = new Stack<>();
        //        ObjType baseType = DataManager.getType("base hero type", DC_TYPE.CHARS);
        Unit hero = HeroCreator.getInstance().newHero();
        Eidolons.setMainHero(hero);
        EUtils.event(GuiEventType.HC_SHOW, hero);
        hero = (Unit) waitForInput(WAIT_OPERATIONS.HC_DONE);
        return hero;

    }

    public static HcHeroModel getModel() {
        return model;
    }

    public static HERO_CREATION_ITEM getCurrentItem() {
        return currentItem;
    }

    public static void setCurrentItem(HERO_CREATION_ITEM currentItem) {
        HeroCreationMaster.previousItem = HeroCreationMaster.currentItem;
        HeroCreationMaster.currentItem = currentItem;
    }

    public static boolean checkItemIsDone(HERO_CREATION_ITEM currentItem) {
        switch (currentItem) {
            case RACE:
                return model.checkProperty(G_PROPS.RACE) //TODO remove race from base type!
                        && checkProperty(G_PROPS.BACKGROUND);
            case GENDER:
                return checkProperty(G_PROPS.GENDER);
            case PORTRAIT:
                return checkProperty(G_PROPS.IMAGE);
            case PERSONALITY:
                return checkProperty(G_PROPS.SOUNDSET)
                        //        TODO fix King's!         &&!model.checkProperty(G_PROPS.SOUNDSET,
                        //                  DataManager.getType(model.getBackground().toString(),
                        //                   DC_TYPE.CHARS).getProperty(G_PROPS.SOUNDSET))
                        ;
            case DEITY:
                return checkProperty(G_PROPS.DEITY)
                        &&
                        !model.getProperty(G_PROPS.DEITY).contains(";");
            case INTRODUCTION:
                return true;

        }
        return checkItemIsDone(
                HERO_CREATION_ITEM.values()[
                        EnumMaster.getEnumConstIndex(HERO_CREATION_ITEM.class, currentItem) - 1]);
    }

    private static boolean checkProperty(G_PROPS prop) {
        if (model.checkProperty(prop))
            return !model.checkProperty(prop,
                    HeroCreator.ROOT_TYPE.getProperty(prop));
        return false;
    }

    public static boolean isCurrentItemDone() {
        return checkItemIsDone(getCurrentItem());
    }

    public static boolean isHeroCreationInProgress() {
        return heroCreationInProgress;
    }

    public static void setHeroCreationInProgress(boolean heroCreationInProgress) {
        HeroCreationMaster.heroCreationInProgress = heroCreationInProgress;
    }

    public static void setModel(HcHeroModel model) {
        HeroCreationMaster.model = model;
    }

    public static void rename() {

    }

    public static void applyPresetType(ObjType type) {
        Unit hero = HeroCreator.createHeroObj(type);
        HqDataMaster.createAndSaveInstance(hero);
        //updates the model etc
        HeroCreationPanel.getInstance().setUserObject(new HqHeroDataSource(model));
        //just create a new model base on this! 
        //        HqDataMaster.operation(model, HERO_OPERATION.APPLY_TYPE, entity);
    }

    public static void applyBackgroundType(Entity entity) {
        HqDataMaster.operation(model, HERO_OPERATION.APPLY_TYPE, entity);
        HqDataMaster.operation(model, HERO_OPERATION.LEVEL_UP, INITIAL_LEVEL);
    }

    public static void undo() {
        if (screens.size() < 2)
            return;
        HqDataMaster.undo(model.getHero());
        screens.pop();
        backScreens.add(currentItem = screens.peek());
        HeroCreationPanel.getInstance().setView(getCurrentItem(), true);
    }

    public static void redo() {
        if (backScreens.size() < 2)
            return;
        HqDataMaster.redo(model.getHero());
        backScreens.pop();
        HeroCreationPanel.getInstance().setView(backScreens.peek(), false);
        screens.add(currentItem);
    }
}
