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
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private static final float DEFAULT_WAIT_TIME = 1.3f;
    private static final float TOOLTIP_HIDE_DISTANCE = 60;
    private final GuiStage guiStage;
    float tooltipTimer;
    private Tooltip tooltip;
    private Cell actorCell;
    private float toWait;
    private Vector2 originalPosition;

    public ToolTipManager(GuiStage battleGuiStage) {
        guiStage = battleGuiStage;
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {
            Object object = event.get();
            requestedShow(object);
        });

        GuiEventManager.bind(GRID_OBJ_HOVER_ON, (event) -> {
            if (DungeonScreen.getInstance().isBlocked())
                return;
            BaseView object = (BaseView) event.get();
            hovered(object);
            if (object instanceof LastSeenView)
                return;
        });

        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();
            hoverOff(object);
        });

        actorCell = addElement(null);
    }


    private void requestedShow(Object object) {
        if (tooltip == object) {
            if (isLogged()) LogMaster.log(1, "Update ignored " );
            return;
        }
        if (isRemoveImmediately(actorCell.getActor())) {
            actorCell.setActor(null);
            //                    immediate removal
        } else {
            if (actorCell.getActor() != null)
                ActorMaster.addFadeOutAction(actorCell.getActor(), 0.35f);
        }
        tooltip = (Tooltip) object;
        toWait = getTimeToWaitForTooltip(tooltip);
        if (toWait == 0) {
            if (isLogged()) LogMaster.log(1, "Immediate show: " );
            show();
        } else if (isLogged()) LogMaster.log(1, "Wait for tooltip: " );

        initTooltipPosition();
    }

    private void show() {
        init(tooltip);
    }

    private float getTimeToWaitForTooltip(Tooltip tooltip) {
        if (tooltip == null) {
            return -1;
        }
        if (!tooltip.isBattlefield()) {
            return 0;
        }
        return DEFAULT_WAIT_TIME / OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED) / 100;
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
        return false;
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


    private void init(Tooltip tooltip) {

        if (isLogged()) LogMaster.log(1, "showing tooltips" + tooltip);
        tooltip.setManager(this);

        tooltip.invalidate();
        actorCell.setActor(tooltip);

        originalPosition = GdxMaster.getCursorPosition(this);
        setPosition(originalPosition.x + 10, originalPosition.y);

        tooltip.getColor().a = 0;
        tooltip.clearActions();
        ActorMaster.addFadeInAction(tooltip, 0.5f);
        if (tooltip.getEntity() != null)
            entityHover(tooltip.getEntity());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (toWait > 0)
            if (tooltip != null) {
                if (tooltip.isMouseHasMoved()) {
                    tooltipTimer = 0;
                    if (isLogged()) LogMaster.log(1, "MouseHasMoved for tooltip ");
                } else {
                    tooltipTimer += delta;
                    if (isLogged()) LogMaster.log(1, "tooltipTimer: " + tooltipTimer);
                    if (tooltipTimer >= toWait) {
                        if (isLogged()) LogMaster.log(1, "tooltipTimer out!" + tooltipTimer);
                        tooltipTimer = 0;
                        toWait = 0;
                        if (tooltip.showing)
                            show();
                    }
                }
            }

        float dst = 0;
        if (originalPosition != null)
            dst = originalPosition.dst(GdxMaster.getCursorPosition(this));
        if (dst > TOOLTIP_HIDE_DISTANCE * GdxMaster.getFontSizeModSquareRoot()
                || Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            setVisible(false);
        } else {
            setVisible(true);
        }

    }

    private void initTooltipPosition() {
        final Tooltip tooltip = (Tooltip) actorCell.getActor();
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
        boolean right=true ;
        if (x > GdxMaster.getWidth()) {
            actorCell.right();
            actorCell.padRight((x - GdxMaster.getWidth()) / 2 - getPreferredPadding());
            right = true;
        }
        Vector2 offset = tooltip.getDefaultOffset();
        if (offset != null) {
            int difX = right? -2 : 1;
            int difY = bot? -2 : 1;
            setPosition(getX() + offset.x*difX, getY() + offset.y*difY);
            if (right){
                setX(getX()-getWidth());
            }
        }

    }

    private boolean isLogged() {
        return true;
    }

    private float getPreferredPadding() {
        return 65 * GdxMaster.getFontSizeMod();
    }



    private void hovered(BaseView object) {
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
        }
        object.setHovered(true);
        DungeonScreen.getInstance().getGridPanel().setUpdateRequired(true);

    }

    private void hoverOff(BaseView object) {
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

}
