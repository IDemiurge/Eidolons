package gdx.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import logic.functions.GameController;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class AKeyListener extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE){
            GuiEventManager.trigger(GuiEventType.RESET_CAMERA);
            return super.keyDown(keycode);
        }
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            move(2, false);
        } else
        if (keycode == Input.Keys.Q){
            move(1, false);
        } else
        if (keycode == Input.Keys.R || keycode == Input.Keys.D){
            move(2, true);
        } else
        if (keycode == Input.Keys.E){
            move(1, true);
        }

        return super.keyDown(keycode);
    }

    private void move(int length, boolean direction) {
        GameController.heroMove(length, direction);
    }

    private void showAttackMenu() {

    }
}
