package eidolons.swing.components.buttons;

import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.client.dc.Launcher;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class MenuButton extends CustomButton implements Runnable {

    private DC_Game game;

    public MenuButton(DC_Game game) {
        super(VISUALS.MENU_BUTTON);
        this.game = game;
    }

    public void handleAltClick() {
        if (game.isDebugMode() || Launcher.DEV_MODE) {
            if (game.isSimulation()) {
                game.getDebugMaster().showDebugWindow();
            } else {
            }
        }
    }

    @Override
    public void handleClick() {
        if (game.isSimulation()) {
            Launcher.getMainManager().exitToMainMenu();
            return;
        }
        // getMenuDialog().update();
        if (DialogMaster.confirm("Exit to Main Menu?")) {
            Launcher.getMainManager().exitToMainMenu();
        }

        // if (game.isDebugMode()) {
        // game.getDebugMaster().showDebugWindow();
        // } else {
        // new Thread(this, "menu").start();
        // }

    }


    protected void playClickSound() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.TURN);
    }

    public void run() {
        Boolean result = DialogMaster
         .askAndWait("Need a break?", true, "Yep", "Nope", "I give up.");
        if (result == null) {
//            game.getBattleManager().surrender();
            return;
        }
        if (result) {
            Launcher.getMainManager().exitToMainMenu();
        } else {
            DialogMaster.inform("Then back to work!");
        }
    }

}