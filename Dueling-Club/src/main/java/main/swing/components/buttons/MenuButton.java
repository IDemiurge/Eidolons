package main.swing.components.buttons;

import main.client.dc.Launcher;
import main.game.DC_Game;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.sound.SoundMaster;
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
                game.getDebugMaster().toggleDebugGui();
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
        SoundMaster.playStandardSound(STD_SOUNDS.TURN);
    }

    public void run() {
        Boolean result = DialogMaster
                .askAndWait("Need a break?", true, "Yep", "Nope", "I give up.");
        if (result == null) {
            game.getBattleManager().surrender();
            return;
        }
        if (result) {
            Launcher.getMainManager().exitToMainMenu();
        } else {
            DialogMaster.inform("Then back to work!");
        }
    }

}
