package libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.Core;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.screens.handlers.ScreenMaster;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;

/**
 * Created by JustMe on 8/31/2017.
 */
public class BattleClickListener extends ClickListener {

    private static Boolean altDefault;

    public BattleClickListener() {
    }

    public BattleClickListener(int button) {
        super(button);
    }

    public static Boolean getAltDefault() {
        if (altDefault==null )
            altDefault = OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.ALT_MODE_ON);
        return altDefault;
    }

    public static void setAltDefault(Boolean altDefault) {
        BattleClickListener.altDefault = altDefault;
    }

    public boolean isShift() {
        return Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
         || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
    }

    public boolean isControl() {
        return Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
         || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
    }

    public boolean isAlt() {
        if (getAltDefault())
            return !(Gdx.input.isKeyPressed(Keys.ALT_RIGHT)
             || Gdx.input.isKeyPressed(Keys.ALT_LEFT));
        return Gdx.input.isKeyPressed(Keys.ALT_RIGHT)
         || Gdx.input.isKeyPressed(Keys.ALT_LEFT);
    }

    @Override
    public boolean handle(Event e) {
        if (Core.game == null)
            return true;
        if (!Core.game.isBattleInit())
            return true;
        if (HqPanel.getActiveInstance()!=null )
            return true;
        return super.handle(e);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        ScreenMaster.getScreen().getController().mouseInput();
    }
}
