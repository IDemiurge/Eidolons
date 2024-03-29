package libgdx.gui.dungeon.menu.selection;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by JustMe on 11/30/2017.
 */
public class SelectionInputListener extends InputListener {
    SelectionPanel selectionPanel;

    public SelectionInputListener(SelectionPanel selectionPanel) {
        this.selectionPanel = selectionPanel;
    }

    public void next() {
        selectionPanel.next();
    }

    public void previous() {
        selectionPanel.previous();

    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        switch (keycode) {
            case Keys.UP:
            case Keys.W:
            case Keys.LEFT:
            case Keys.A:
                previous();
                break;
            case Keys.DOWN:
            case Keys.S:
            case Keys.RIGHT:
            case Keys.D:
                next();
                break;
            case Keys.ENTER:
            case Keys.SPACE:
                selectionPanel.tryDone();
                return false;
            case Keys.ESCAPE:
                if (selectionPanel.isBackSupported())
                    selectionPanel.cancel();
                return false;

        }
        return super.keyUp(event, keycode);
    }


    @Override
    public boolean keyTyped(InputEvent event, char character) {
        return super.keyTyped(event, character);
    }

}
