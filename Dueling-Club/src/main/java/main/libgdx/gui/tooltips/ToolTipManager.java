package main.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.entity.Entity;
import main.entity.active.DC_UnitAction;
import main.libgdx.GdxMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridUnitView;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.stage.GuiStage;
import main.system.GuiEventManager;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private final  GuiStage guiStage;
    private Cell actorCell;

    public ToolTipManager( GuiStage battleGuiStage) {
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
            if (DungeonScreen.getInstance().isBlocked())
                return ;
            BaseView object = (BaseView) event.get();
//            if (object.getScaleX()==getDefaultScale(object))
//                if (object.getScaleX()==getDefaultScale(object))

            float scaleX = getDefaultScale(object);
            if (object.getScaleX() == getDefaultScale(object))
                scaleX = getZoomScale(object);
            float scaleY = getDefaultScale(object);
            if (object.getScaleY() == getDefaultScale(object))
                scaleY = getZoomScale(object);

            ActorMaster.
             addScaleActionIfNoActions(object, scaleX, scaleY, 0.35f);
            if (object instanceof GridUnitView) {
                if (scaleX == getDefaultScale(object))
                    scaleX = getZoomScale(object);
                if (scaleY == getDefaultScale(object))
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

    public void entityHover(Entity entity) {
        if (entity instanceof DC_UnitAction) {
            if (((DC_UnitAction) entity).isAttackAny())
                AnimMaster3d.initHover((DC_UnitAction) entity);
            return;
        }

        guiStage.getRadial().hover(entity);
        //differentiate radial from bottom panel? no matter really ... sync :)

//        guiStage.getBottomPanel().getSpellPanel().getCells();

    }

    public void entityHoverOff(Entity entity) {
        if (entity instanceof DC_UnitAction) {
            AnimMaster3d.hoverOff((DC_UnitAction) entity);
        }
        guiStage.getRadial().hoverOff(entity);
    }

    private float getZoomScale(BaseView object) {
//        if (object instanceof OverlayView){
//            return 0.61f;
//        } gridPanel handles this by setBounds()!
        return 1.12f;
    }

    private float getDefaultScale(BaseView object) {
//        if (object instanceof OverlayView){
//            return OverlayView.SCALE;
//        } gridPanel handles this by setBounds()!
        return 1;
    }


    private void init(ToolTip toolTip) {
        toolTip.setManager(this);

        toolTip.invalidate();
        actorCell.setActor(toolTip);

        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x + 10, v2.y);

        if (toolTip.getEntity() != null)
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
            float y = (v2.y - toolTip.getPrefHeight() - getPreferredPadding());
            if (y < 0) {
                actorCell.bottom();
                actorCell.padBottom(
                 Math.max(-y / 2 - getPreferredPadding(), 64));

            }

            y = v2.y + toolTip.getPrefHeight() + getPreferredPadding();
            if (y > GdxMaster.getHeight()) {
                actorCell.top();
                actorCell.padTop((y - GdxMaster.getHeight()) / 2 - getPreferredPadding());
            }

            float x = v2.x - toolTip.getPrefWidth() - getPreferredPadding();
            if (x < 0) {
                actorCell.left();
                actorCell.padLeft((-x) / 2 - getPreferredPadding());
            }
            x = v2.x + toolTip.getPrefWidth() + getPreferredPadding();
            if (x > GdxMaster.getWidth()) {
                actorCell.right();
                actorCell.padRight((x - GdxMaster.getWidth()) / 2 - getPreferredPadding());
            }

        }
    }

    private float getPreferredPadding() {
        return 65;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (actorCell.getActor()!=null ){
                if ( actorCell.getActor().isTouchable()) {
                    return super.hit(x, y, touchable);

            }
        }
        return null;

        //this is untouchable element
    }

}
