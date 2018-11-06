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
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.bf.grid.*;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.ShaderMaster;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.GuiStage;
import eidolons.system.options.AnimationOptions.ANIMATION_OPTION;
import eidolons.system.options.ControlOptions.CONTROL_OPTION;
import eidolons.system.options.OptionsMaster;
import main.entity.Entity;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private static final float DEFAULT_WAIT_TIME = 1.5f;
    private static final float TOOLTIP_HIDE_DISTANCE = 80;
    private final GuiStage guiStage;
    private float tooltipTimer;
    private float stackTimer;
    private float waitToHideStack;
    private Map<GenericGridView, Vector2> posMap = new HashMap<>();
    private Map<GenericGridView, Float> scaleMap = new HashMap<>();
    private Tooltip tooltip;
    private Cell actorCell;
    private float toWait;
    private Vector2 originalPosition;
    private int minStackSize = 2;
    private float offsetX = 32;
    private float offsetY = 32;
    private Boolean bottom;
    private Boolean right;

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
            boolean canShowStack = true;
            if (object instanceof GenericGridView) {
                if (((GenericGridView) object).isStackView()) {
                    canShowStack = false;
                }
            }
            hovered(object, canShowStack);
            if (object instanceof LastSeenView)
                return;
        });

        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();
            boolean canShowStack = true;
            if (object instanceof GenericGridView) {
                if (((GenericGridView) object).isStackView()) {
                    canShowStack = false;
                }
            }
            hoverOff(object, canShowStack);
        });

        actorCell = addElement(null);
    }


    private void requestedShow(Object object) {
        if (object != null)
            if (tooltip == object) {
                //            TODO dangerous?     originalPosition = GdxMaster.getCursorPosition(this);
                tooltip.fadeIn();
                if (isLogged()) LogMaster.log(1, "Update ignored ");
                return;
            }
        if (tooltip != null)
            ActorMaster.addFadeOutAction(tooltip, 0.35f);
        if (object == null) {
            actorCell.setActor(tooltip = null);
            originalPosition= null;
            return;
        }
        tooltip = (Tooltip) object;

        toWait = getTimeToWaitForTooltip(tooltip);
        if (toWait == 0) {
            if (isLogged()) LogMaster.log(1, "Immediate show: ");
            show();
        } else if (isLogged()) LogMaster.log(1, "Wait for tooltip: ");

        if (!isUseTable())
            if (tooltip instanceof UnitViewTooltip) {
                Vector2 v = new Vector2(
                 ((UnitViewTooltip) tooltip).getView().getX() + 200,
                 ((UnitViewTooltip) tooltip).getView().getY());
                addActor(tooltip);
                tooltip.setPosition(v.x, v.y);
                return;
            }
        initTooltipPosition();
    }

    private boolean isUseTable() {
        return true;
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
        return DEFAULT_WAIT_TIME / (OptionsMaster.getAnimOptions().getIntValue(ANIMATION_OPTION.SPEED) / 100);
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


    private void init(Tooltip tooltip) {

        if (isLogged()) LogMaster.log(1, "showing tooltips" + tooltip);
        tooltip.setManager(this);

        tooltip.invalidate();
        if (actorCell.getTable() != this) {
            actorCell = addElement(tooltip);
        } else
            actorCell.setActor(tooltip);

        originalPosition = GdxMaster.getCursorPosition(this);
        bottom = null;
        right = null;
        updatePosition();

        tooltip.getColor().a = 0;
        tooltip.clearActions();
        ActorMaster.addFadeInAction(tooltip, 0.5f);
        if (tooltip.getEntity() != null)
            entityHover(tooltip.getEntity());
    }

    @Override
    public void act(float delta) {
        setVisible(true);
        super.act(delta);
        if (waitToHideStack > 0) {
            stackTimer += delta;
            if (stackTimer >= waitToHideStack) {
                stackOff();
            }
        }
        if (tooltip != null) {
            if (tooltip.getColor().a == 0) {
                tooltip.fadeIn();
            }
        }
        if (toWait > 0)
            if (tooltip != null) {
                if (tooltip.isMouseHasMoved()) {
                    tooltipTimer = 0;
                    tooltip.exited();
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


        if (tooltip != null)
            if (tooltip.isBattlefield())
                if (originalPosition != null)
                    if (originalPosition.dst(GdxMaster.getCursorPosition(this))
                     > 300 * GdxMaster.getFontSizeModSquareRoot()) {
                        main.system.auxiliary.log.LogMaster.log(1, tooltip + " Too far!" + originalPosition + "vs " + GdxMaster.getCursorPosition(this));
                        requestedShow(null);
                        if (isStackHoverOn())
                            waitToHideStack = 2;
                    }
        //         ||
        //
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            setVisible(false);
        } else {
            setVisible(true);
        }
        if (tooltip == null) {
            return;
        }
        updatePosition();
    }


    private void updatePosition() {
        float x = GdxMaster.getCursorPosition(this).x;
        float y = GdxMaster.getCursorPosition(this).y;

        //        if (isSimplePositioning()) {
        //            setPosition(x + offsetX, y + offsetY);
        //            return;
        //        }
        boolean setBottom = y > InputController.getHalfHeight() + tooltip.getHeight();
        boolean setTop = y < InputController.getHalfHeight() - tooltip.getHeight();
        boolean setLeft = x > InputController.getHalfWidth() + tooltip.getHeight();
        boolean setRight = x < InputController.getHalfWidth() - tooltip.getHeight();

        if (setBottom)
            bottom = true;
        if (setTop)
            bottom = false;
        if (setRight)
            right = true;
        if (setLeft)
            right = false;

        if (bottom == null)
            y += offsetY;
        else if (bottom) {
            y -= +offsetY;
            if (!isSimplePositioning())
                y -= tooltip.getHeight();
        } else {
            y += offsetY;
            if (!isSimplePositioning())
                y += tooltip.getHeight();
        }

        if (right == null)
            x += offsetX;
        else if (right) {
            x += offsetX;
            if (!isSimplePositioning())
                x += tooltip.getWidth();
        } else {
            x -= offsetX;
            if (!isSimplePositioning())
                x -= tooltip.getWidth();
        }
        //        tooltip.
        setPosition(x, y);
    }

    private boolean isSimplePositioning() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    private void initTooltipPosition() {
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);

        actorCell.left().top();

        actorCell.pad(0);
        if (tooltip == null) {
            return;
        }
        float y = (v2.y - tooltip.getPrefHeight() - getPreferredPadding());
        boolean bottom = false;
        if (y < 0) {
            actorCell.bottom();
            actorCell.padBottom(
             Math.max(-y / 2 - getPreferredPadding(), 64));
            bottom = true;
        }

        y = v2.y + tooltip.getPrefHeight() + getPreferredPadding();
        if (y > GdxMaster.getHeight()) {
            if (bottom) {
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
        boolean right = true;
        if (x > GdxMaster.getWidth()) {
            actorCell.right();
            actorCell.padRight((x - GdxMaster.getWidth()) / 2 - getPreferredPadding());
            right = true;
        }

        Vector2 offset = tooltip.getDefaultOffset();
        if (offset != null) {
            int difX = right ? -3 : 2;
            //            int difY = bot? -3 : 2;
            if (right) {
                actorCell.padRight(actorCell.getPadRight() + offset.x * difX + actorCell.getActorWidth());
            } else {
                actorCell.padLeft(actorCell.getPadLeft() + offset.x * difX + actorCell.getActorWidth());
            }
            //            if (right){
            //                actorCell.setActorX(actorCell.getActorX()-actorCell.getActorWidth()*2);
            //            }
            float offsetY = offset.y;
            if (bottom) {
                offsetY = -offsetY;
            }
            if (isSimplePositioning()) {
                actorCell.setActorY(MathMaster.minMax(actorCell.getActorY() + offsetY,
                 GDX.height(-200 - offset.y), GDX.height(200 + offset.y)));
            }
        }

        //        actorCell.setActorY(MathMaster.minMax(actorCell.getActorX(),
        //         GDX.height(-200), GDX.height(200)));
    }

    private boolean isLogged() {
        return false;
    }

    private float getPreferredPadding() {
        return 65 * GdxMaster.getFontSizeMod();
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


    private void hovered(BaseView object, boolean canShowStack) {
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

        if (isStackHoverOn()) {
            if (canShowStack) {
                Coordinates c = object.getUserObject().getCoordinates();
                GridCellContainer cell = DungeonScreen.getInstance().getGridPanel().getCells()[c.x][
                 PositionMaster.getLogicalY(c.y)];
                if (cell.isStackView()) {
                    waitToHideStack = 0;
                } else
                    stackOn(cell);
            }
        }

        DungeonScreen.getInstance().getGridPanel().setUpdateRequired(true);

    }

    private void stackOn(GridCellContainer cell) {
        //        showingStack = true;
        List<GenericGridView> views = cell.getUnitViews(true);

        if (views.size() < minStackSize) {
            return;
        }
        cell.setStackView(true);
        int size = 128;
        boolean horizontal = bottom != null;
        int x = horizontal ? -size * views.size() / 2 + size / 2 : 0;
        int y = !horizontal ? -size * views.size() / 2 + size / 2 : 0;
        for (GenericGridView view : views) {
            //sorted?
            ActorMaster.addMoveByAction(view, x, y, 0.9f);
            posMap.put(view, new Vector2(-x, -y));
            if (horizontal) {
                x += 140;
            } else {
                y += 140;
            }
            view.setStackView(true);
            view.setHovered(true);

            ActorMaster.addScaleAction(view, 1, 1.2f);
            //                DungeonScreen.getInstance().getGridPanel().getCells()[c.x][c.y];
            //stackView(true);
            scaleMap.put(view, view.getScaleX());

            //scaling on?

            //highlight by color ally

        }
        DungeonScreen.getInstance().setStackView(cell);
        //ESC to cancel
        main.system.auxiliary.log.LogMaster.log(1, "Stack on! \n" + cell + "\n" + posMap + "\n" + scaleMap);
    }

    public void stackOff() {
        main.system.auxiliary.log.LogMaster.log(1, "Stack off!\n " + posMap + "\n" + scaleMap);
        for (GenericGridView view : posMap.keySet()) {
            Vector2 v = posMap.get(view);
            view.setHovered(false);
            view.setStackView(false);
            ActorMaster.addMoveByAction(view, v.x, v.y, 1.2f);
        }
        for (GenericGridView view : scaleMap.keySet()) {
            ActorMaster.addScaleAction(view, scaleMap.get(view), 1.4f);
        }
        posMap.clear();
        scaleMap.clear();
        waitToHideStack = 0;
        stackTimer = 0;
        if (DungeonScreen.getInstance().getStackView() == null) {
            return;
        }
        DungeonScreen.getInstance().getStackView().setStackView(false);
        DungeonScreen.getInstance().setStackView(null);
    }

    private boolean isStackHoverOn() {
        return OptionsMaster.getControlOptions().getBooleanValue(CONTROL_OPTION.SPLIT_OBJECT_STACKS_ON_HOVER);
    }

    private void hoverOff(BaseView object, boolean canShowStack) {
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

        if (isStackHoverOn()) {
            if (canShowStack) {
                stackOff();
            } else {
                //                waitToHideStack = 2;
            }
        }
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
