package main.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.GuiEventManager;

import static main.system.GuiEventType.SHOW_TOOLTIP;

public class ToolTipManager extends TablePanel {

    private Cell actorCell;

    public ToolTipManager() {
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {
            Object object = event.get();
            if (object == null) {
                actorCell.setActor(null);
            } else {
                init((ToolTip) object);
            }
        });
        actorCell = addElement(null);
    }

    private void init(ToolTip toolTip) {

        toolTip.invalidate();
        actorCell.setActor(toolTip);

        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x + 10, v2.y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (actorCell.getActor() != null) {
            final ToolTip toolTip = (ToolTip) actorCell.getActor();
            Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            v2 = getStage().screenToStageCoordinates(v2);

            actorCell.left().top();

            if ((v2.y - toolTip.getPrefHeight()) < 0) {
                actorCell.bottom();
            }
            if (v2.y + toolTip.getPrefHeight() > Gdx.graphics.getHeight()) {
                actorCell.top();
            }

            if (v2.x - toolTip.getPrefWidth() < 0) {
                actorCell.left();
            }

            if (v2.x + toolTip.getPrefWidth() > Gdx.graphics.getWidth()) {
                actorCell.right();
            }

        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;//this is untouchable element
    }
}
