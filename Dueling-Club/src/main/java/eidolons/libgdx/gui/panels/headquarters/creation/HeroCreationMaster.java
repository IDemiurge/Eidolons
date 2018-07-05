package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationSequence.HERO_CREATION_ITEM;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static main.system.threading.WaitMaster.waitForInput;

/**
 * Created by JustMe on 7/3/2018.
 * <p>
 * use hqDataMaster?
 * <p>
 * use
 */
public class HeroCreationMaster {


    public static final boolean TEST_MODE = true;
    static HcHeroModel model;
    private static HERO_CREATION_ITEM currentItem;
    private static HERO_CREATION_ITEM previousItem;
    private static boolean currentItemDone;
    private static boolean heroCreationInProgress;

// what if a single STAGE contains 2 property changes? and we do the 2nd... 1st will be lost?!
    // or we can overwrite ? that will pile up operations...
    // how will it work in stats?
    public static void modified(PROPERTY property, String value) {
        if (checkRevert()) {
            model.rollback(currentItem);
        }
        HqDataMaster.operation(model,
         HERO_OPERATION.SET_PROPERTY, property, value);
    }

    public static boolean checkRevert() {
        if (previousItem != null)
            return EnumMaster.getEnumConstIndex(HERO_CREATION_ITEM.class,
             previousItem) > EnumMaster.getEnumConstIndex(HERO_CREATION_ITEM.class,
             currentItem);
        return false;
    }

    public static void modified(PARAMETER parameter, String value) {
        HqDataMaster.operation(model,
         HERO_OPERATION.SET_PARAMETER, parameter, value);
    }

    public static void export() {
    }

    public static void done() {
    }

    public static Unit newHero() {
        //        ObjType baseType = DataManager.getType("base hero type", DC_TYPE.CHARS);
        Unit hero = HeroCreator.getInstance().newHero();
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

        setCurrentItemDone(checkItemIsDone(currentItem));
    }

    private static boolean checkItemIsDone(HERO_CREATION_ITEM currentItem) {
        switch (currentItem) {
            case RACE:
                return model.checkProperty(G_PROPS.RACE)
                 &&model.checkProperty(G_PROPS.BACKGROUND);
            case GENDER:
                return model.checkProperty(G_PROPS.GENDER);
            case PORTRAIT:
                return !model.checkProperty(G_PROPS.DEITY, HeroCreator. ROOT_TYPE.getImagePath());
            case PERSONALITY:
                return model.checkProperty(G_PROPS.SOUNDSET);
            case DEITY:
                return model.checkProperty(G_PROPS.DEITY);
            case FINALIZE:
                break;
        }
        return true;
    }

    public static boolean isCurrentItemDone() {
        return currentItemDone;
    }

    public static void setCurrentItemDone(boolean currentItemDone) {
        HeroCreationMaster.currentItemDone = currentItemDone;
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
}
