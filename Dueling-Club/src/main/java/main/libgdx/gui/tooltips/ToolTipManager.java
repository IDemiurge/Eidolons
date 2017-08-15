package main.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridUnitView;
import main.libgdx.bf.UnitView;
import main.libgdx.gui.panels.dc.TablePanel;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

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

        GuiEventManager.bind(UNIT_VIEW_HOVER_ON, (event) -> {
            UnitView object = (UnitView) event.get();
//            if (object.getScaleX()== 1)
//                if (object.getScaleX()== 1)

            float scaleX = 1;
            if (object.getScaleX() == 1)
                scaleX = 1.12f;
            float scaleY = 1;
            if (object.getScaleY() == 1)
                scaleY = 1.12f;

            ActorMaster.
             addScaleActionIfNoActions(object, scaleX, scaleY, 0.35f);
            if (object instanceof GridUnitView) {
                if (scaleX == 1)
                    scaleX = 1.12f;
                if (scaleY == 1)
                    scaleY = 1.12f;
                ActorMaster.
                 addScaleAction(((GridUnitView) object).getInitiativeQueueUnitView()
                  , scaleX, scaleY, 0.35f);
            } else {

            }
//           DungeonScreen.getInstance().getGridPanel().getbo
            object.setHovered(true);
        });
        GuiEventManager.bind(UNIT_VIEW_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();

            float scaleX;
            float scaleY;

            if (object instanceof GridUnitView) {
                scaleX = object.getScaledWidth();
                scaleY = object.getScaledHeight();
                ActorMaster.
                 addScaleAction(object, scaleX, scaleY, 0.35f);
                scaleX = 1.0f;
                scaleY = 1.0f;
                ActorMaster.
                 addScaleAction(((GridUnitView) object).getInitiativeQueueUnitView()
                  , scaleX, scaleY, 0.35f);
            } else {
                scaleX = 1.0f;
                scaleY = 1.0f;
                ActorMaster.
                 addScaleAction(object, scaleX, scaleY, 0.35f);
            }

            object.setHovered(false);

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
