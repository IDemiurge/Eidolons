package tests.macro;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.screens.menu.MainMenu.MAIN_MENU_ITEM;
import eidolons.macro.global.persist.Loader;
import eidolons.macro.global.persist.Saver;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 6/10/2018.
 *
 * enter game, save, exit, load, check all is in tact
 */
public class JUnitMacroSaveLoad extends JUnitMacroInit{

    private Unit originalHero;
    private String saveName;

    @Test
    public void testSaveLoadCycle(){

        super.init();
        originalHero = Eidolons.getMainHero();
        applyChanges();
        save();
        exit();
        load();
        testAllLoaded();
    }

    private void applyChanges() {
        //remove an item, change param,
        originalHero.getInventory().remove();
        originalHero.getQuickItems().remove();
        originalHero.getMainWeapon().modifyParameter(PARAMS.C_DURABILITY, -1);
    }

    private void load() {
        Loader.load(saveName);
    }

    private void exit() {
    }

    private void save() {
       saveName= Saver.save();
    }

    public void testAllLoaded(){
        Unit loadedHero = Eidolons.getMainHero();

        assertTrue(loadedHero.getInventory().equals(originalHero.getInventory()));

        assertTrue(loadedHero.getMainWeapon().getIntParam(PARAMS.C_DURABILITY).
         equals(originalHero.getMainWeapon().getIntParam(PARAMS.C_DURABILITY)));

    }

    @Override
    protected boolean isManual() {
        return super.isManual();
    }

    @Override
    protected String getLaunchArgString() {
        return MAIN_MENU_ITEM.LOAD.toString();
    }
}
