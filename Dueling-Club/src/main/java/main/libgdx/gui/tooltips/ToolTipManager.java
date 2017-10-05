package main.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.entity.Entity;
import main.entity.active.DC_UnitAction;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridUnitView;
import main.libgdx.bf.OverlayView;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.stage.BattleGuiStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private final BattleGuiStage guiStage;
    private Cell actorCell;

    public   void entityHover(Entity entity) {
        if (entity instanceof DC_UnitAction) {
            if (((DC_UnitAction) entity).isAttackAny())
            AnimMaster3d.initHover((DC_UnitAction) entity);
            return;
        }

        guiStage.getRadial().hover(entity);
        //differentiate radial from bottom panel? no matter really ... sync :)

//        guiStage.getBottomPanel().getSpellPanel().getCells();

    }
    public   void entityHoverOff(Entity entity) {
        if (entity instanceof DC_UnitAction) {
            AnimMaster3d.hoverOff((DC_UnitAction) entity);
        }
        guiStage.getRadial().hoverOff(entity);
    }
    public ToolTipManager(BattleGuiStage battleGuiStage) {
        guiStage = battleGuiStage;
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {

            Object object = event.get();
            if (object == null) {
                actorCell.setActor(null);
            } else {
                init((ToolTip) object);
            }

        });

        GuiEventManager.bind(GRID_OBJ_HOVER_ON, (event) -> {
            BaseView object = (BaseView) event.get();
//            if (object.getScaleX()==getDefaultScale(object))
//                if (object.getScaleX()==getDefaultScale(object))

            float scaleX = getDefaultScale(object);
            if (object.getScaleX() ==getDefaultScale(object))
                scaleX = getZoomScale(object);
            float scaleY =getDefaultScale(object);
            if (object.getScaleY() ==getDefaultScale(object))
                scaleY =getZoomScale(object);

            ActorMaster.
             addScaleActionIfNoActions(object, scaleX, scaleY, 0.35f);
            if (object instanceof GridUnitView) {
                if (scaleX ==getDefaultScale(object))
                    scaleX = getZoomScale(object);
                if (scaleY ==getDefaultScale(object))
                    scaleY = getZoomScale(object);
                ActorMaster.
                 addScaleAction(((GridUnitView) object).getInitiativeQueueUnitView()
                  , scaleX, scaleY, 0.35f);
            } else {

            }
//           DungeonScreen.getInstance().getGridPanel().getbo
            object.setHovered(true);
        });
        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();

            float scaleX;
            float scaleY;

            if (object instanceof GridUnitView) {
                scaleX = object.getScaledWidth();
                scaleY = object.getScaledHeight();
                ActorMaster.
                 addScaleAction(object, scaleX, scaleY, 0.35f);
                scaleX = getDefaultScale(object);
                scaleY = getDefaultScale(object);
                ActorMaster.
                 addScaleAction(((GridUnitView) object).getInitiativeQueueUnitView()
                  , scaleX, scaleY, 0.35f);
            } else {
                scaleX = getDefaultScale(object);
                scaleY = getDefaultScale(object);
                ActorMaster.
                 addScaleAction(object, scaleX, scaleY, 0.35f);
            }

            object.setHovered(false);

        });
        actorCell = addElement(null);
    }

    private float getZoomScale(BaseView object) {
        if (object instanceof OverlayView){
            return 0.61f;
        }
        return 1.12f;
    }

    private float getDefaultScale(BaseView object) {
        if (object instanceof OverlayView){
            return OverlayView.SCALE;
        }
        return 1;
    }


    private void init(ToolTip toolTip) {
        toolTip.setManager(this);

        toolTip.invalidate();
        actorCell.setActor(toolTip);

        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x + 10, v2.y);

        if (toolTip.getEntity()!=null )
            entityHover(toolTip.getEntity());
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
