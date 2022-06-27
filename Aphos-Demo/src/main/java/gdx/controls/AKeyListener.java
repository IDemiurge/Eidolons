package gdx.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class AKeyListener extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A){
            move(2, false);
        } else
        if (keycode == Input.Keys.Q){
            move(1, false);
        }

        return super.keyDown(keycode);
    }

    private void move(int i, boolean b) {

    }
    private void showAttackMenu() {

    }
}
