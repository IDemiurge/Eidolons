package main.libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 8/31/2017.
 */
public class BattleClickListener extends ClickListener {

    public boolean isShift() {
        return Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
         || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
    }

    public boolean isControl() {
        return Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
         || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
    }

    public boolean isAlt() {
        return Gdx.input.isKeyPressed(Keys.ALT_RIGHT)
         || Gdx.input.isKeyPressed(Keys.ALT_LEFT);
    }

    @Override
    public boolean handle(Event e) {
        if (Eidolons.game == null)
            return true;
        if (!Eidolons.game.isBattleInit())
            return true;
        return super.handle(e);
    }
}
