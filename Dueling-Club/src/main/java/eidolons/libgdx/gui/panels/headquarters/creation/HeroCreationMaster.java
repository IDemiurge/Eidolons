package eidolons.libgdx.gui.panels.headquarters.creation;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroCreator;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import static main.system.threading.WaitMaster.waitForInput;

/**
 * Created by JustMe on 7/3/2018.
 *
 * use hqDataMaster?
 *
 * use
 */
public class HeroCreationMaster {


    public static void modified(PROPERTY property, String name){}
    public static void revertToCached(){}
    public static void rollback(){}
    public static void export(){}
    public static void done(){}

    public static Unit newHero() {
//        ObjType baseType = DataManager.getType("base hero type", DC_TYPE.CHARS);
        Unit hero= HeroCreator.getInstance() .newHero();
        EUtils.event(GuiEventType.HC_SHOW, hero);
         hero = (Unit) waitForInput(WAIT_OPERATIONS.HC_DONE);
        return hero;

    }
}
