package gdx.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import gdx.general.AScreen;
import logic.core.Aphos;
import logic.functions.GameController;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import content.AphosEvent;

public class AKeyListener extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER){
            Aphos.game.getRoundHandler().setForceEndRound(true);
            return super.keyDown(keycode);
        }
        if (keycode == Input.Keys.SPACE){
            if (!Aphos.started){
                Aphos.start();
                return true;
            }
            GuiEventManager.trigger(AphosEvent.RESET_CAMERA);
            GuiEventManager.trigger(AphosEvent.RESET_ZOOM);
            return super.keyDown(keycode);
        }
        if (keycode == Input.Keys.A){
            move(2, false);
        } else
        if (keycode == Input.Keys.Q){
            move(1, false);
        } else
        if (keycode == Input.Keys.D){
            move(2, true);
        } else
        if (keycode == Input.Keys.E){
            move(1, true);
        }

        if (keycode == Input.Keys.LEFT) {
            AScreen.instance.getCameraMan().arrowMove(3, DIRECTION.LEFT);
            return true;
        }
        if (keycode == Input.Keys.UP) {
            AScreen.instance.getCameraMan().arrowMove(3, DIRECTION.UP);
            return true;
        }
        if (keycode == Input.Keys.DOWN) {
            AScreen.instance.getCameraMan().arrowMove(3, DIRECTION.DOWN);
            return true;
        }
        if (keycode == Input.Keys.RIGHT) {
            AScreen.instance.getCameraMan().arrowMove(3, DIRECTION.RIGHT);
            return true;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        AScreen.instance.getCameraMan().stopMove();
//        if (keycode == Input.Keys.LEFT) {
//            AScreen.instance.getCameraMan().arrowMove(DIRECTION.LEFT);
//        }
//        if (keycode == Input.Keys.UP) {
//            AScreen.instance.getCameraMan().arrowMove(DIRECTION.UP);
//        }
//        if (keycode == Input.Keys.DOWN) {
//            AScreen.instance.getCameraMan().arrowMove(DIRECTION.DOWN);
//        }
//        if (keycode == Input.Keys.RIGHT) {
//            AScreen.instance.getCameraMan().arrowMove(DIRECTION.RIGHT);
//        }
        return super.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {

        return super.keyTyped(character);
    }

    private void move(int length, boolean direction) {
        GuiEventManager.triggerWithParams(AphosEvent.INPUT_MOVE, length, direction );
    }

    private void showAttackMenu() {

    }
}
