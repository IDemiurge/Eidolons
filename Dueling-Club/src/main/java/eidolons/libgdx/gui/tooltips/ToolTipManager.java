package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.bf.grid.LastSeenView;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.GuiStage;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private final GuiStage guiStage;
    private Cell actorCell;

    public ToolTipManager(GuiStage battleGuiStage) {
        guiStage = battleGuiStage;
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {

            Object object = event.get();
            if (object == null) {
                if (isRemoveImmediately(actorCell.getActor())) {
                    actorCell.setActor(null);
//                    immediate removal
                } else {
                    if (actorCell.getActor() != null)
                        ActorMaster.addFadeOutAction(actorCell.getActor(), 0.35f);
                }
            } else {
                init((Tooltip) object);
            }

        });

        GuiEventManager.bind(GRID_OBJ_HOVER_ON, (event) -> {
            if (DungeonScreen.getInstance().isBlocked())
                return;
            BaseView object = (BaseView) event.get();
            if (object instanceof LastSeenView)
                return;
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
            DungeonScreen.getInstance().getGridPanel().setUpdateRequired(true);
        });
        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();
            if (object instanceof LastSeenView) {
                return;
            }

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
            DungeonScreen.getInstance().getGridPanel().setUpdateRequired(true);

        });
        actorCell = addElement(null);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderMaster.SUPER_DRAW ||
         ConfirmationPanel.getInstance().isVisible())
            super.draw(batch, 1);
        else
            ShaderMaster.drawWithCustomShader(this, batch, null, false, false);
    }

    private boolean isRemoveImmediately(Actor actor) {

        return actor instanceof UnitViewTooltip;

    }

    public void entityHover(Entity entity) {
        if (entity instanceof DC_ActiveObj) {
            GuiEventManager.trigger(ACTION_HOVERED, entity);
        }
        if (entity instanceof DC_UnitAction) {
            if (((DC_UnitAction) entity).isAttackAny())
                AnimMaster3d.initHover((DC_UnitAction) entity);
            return;
        }

        guiStage.getRadial().hover(entity);
        //differentiate radial from bottom panel? no matter really ... sync :)

//        guiStage.getBottomPanel().getSpellPanel().getCellsSet();

    }

    public void entityHoverOff(Entity entity) {
        if (entity instanceof DC_ActiveObj) {
            GuiEventManager.trigger(ACTION_HOVERED_OFF, entity);
            if (DC_Engine.isAtbMode())
                if (!ExplorationMaster.isExplorationOn())
                    GuiEventManager.trigger(GuiEventType.ATB_POS_PREVIEW, null);
        }
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


    private void init(Tooltip tooltip) {
        tooltip.setManager(this);

        tooltip.invalidate();
        actorCell.setActor(tooltip);

        Vector2 v2 = GdxMaster.getCursorPosition(this);
        setPosition(v2.x + 10, v2.y);

        if (isRemoveImmediately(tooltip)) {

        } else {
            tooltip.getColor().a = 0;
            ActorMaster.addFadeInAction(tooltip, 0.5f);
        }
        if (tooltip.getEntity() != null)
            entityHover(tooltip.getEntity());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            setVisible(false);
        } else setVisible(true);
        if (actorCell.getActor() != null) {
            final Tooltip tooltip = (Tooltip) actorCell.getActor();
            if (tooltip.getColor().a == 0) {
                actorCell.setActor(null);
                return;
            }
            Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            v2 = getStage().screenToStageCoordinates(v2);

            actorCell.left().top();
            float y = (v2.y - tooltip.getPrefHeight() - getPreferredPadding());
            boolean bot = false;
            if (y < 0) {
                actorCell.bottom();
                actorCell.padBottom(
                 Math.max(-y / 2 - getPreferredPadding(), 64));
                bot = true;
            }

            y = v2.y + tooltip.getPrefHeight() + getPreferredPadding();
            if (y > GdxMaster.getHeight()) {
                if (bot) {
                    actorCell.center();
                } else
                    actorCell.top();
                actorCell.padTop((y - GdxMaster.getHeight()) / 2 - getPreferredPadding());
            }
            float x = v2.x - tooltip.getPrefWidth() - getPreferredPadding();
            if (x < 0) {
                actorCell.left();
                actorCell.padLeft((-x) / 2 - getPreferredPadding());
            }
            x = v2.x + tooltip.getPrefWidth() + getPreferredPadding();
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
        if (actorCell.getActor() != null) {
            if (actorCell.getActor().isTouchable()) {
                return super.hit(x, y, touchable);

            }
        }
        return null;

        //this is untouchable element
    }

}
