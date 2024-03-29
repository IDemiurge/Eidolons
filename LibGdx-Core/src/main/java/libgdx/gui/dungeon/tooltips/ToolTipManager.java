package libgdx.gui.dungeon.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.feat.active.UnitAction;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.story.cinematic.Cinematics;
import eidolons.game.exploration.handlers.ExplorationMaster;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.assets.AnimMaster3d;
import libgdx.bf.Hoverable;
import libgdx.bf.grid.cell.BaseView;
import libgdx.bf.grid.cell.LastSeenView;
import libgdx.bf.grid.cell.QueueView;
import libgdx.bf.grid.cell.UnitGridView;
import libgdx.bf.mouse.InputController;
import libgdx.gui.dungeon.controls.StackViewMaster;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.gui.dungeon.panels.headquarters.HqTooltipPanel;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.ConfirmationPanel;
import libgdx.stage.GenericGuiStage;
import libgdx.stage.GuiStage;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;
import main.system.threading.WaitMaster;

import static main.system.GuiEventType.*;

public class ToolTipManager extends TablePanel {

    private static final float DEFAULT_WAIT_TIME = 1.0f;
    private static final float TOOLTIP_HIDE_DISTANCE = 80;
    private final GenericGuiStage guiStage;
    private float tooltipTimer;
    private Tooltip tooltip;
    private Cell actorCell;
    private float toWait;
    private Vector2 originalPosition;
    private Boolean bottom;
    private Boolean right;
    private final StackViewMaster stackMaster = new StackViewMaster();
    private static Vector2 presetTooltipPos;
    private static HqTooltipPanel tooltipPanel;
    private static boolean hoverOff;

    private static final float HOVER_CHECK_PERIOD = 2.0f;
    private float hoverCheck = 0;
    Hoverable hovered;
    DequeImpl<BaseView> hoveredList = new DequeImpl<>();
    private boolean hidden;

    public static void setTooltipPanel(HqTooltipPanel tooltipPanel) {
        ToolTipManager.tooltipPanel = tooltipPanel;
    }

    public static void setPresetTooltipPos(Vector2 presetTooltipPos) {
        ToolTipManager.presetTooltipPos = presetTooltipPos;
    }

    public ToolTipManager(GenericGuiStage battleGuiStage) {
        guiStage = battleGuiStage;
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {
            if (ScreenMaster.getScreen().getController().isLeftPressed()) {
                return;
            }
            Object object = event.get();
            requestedShow(object);
        });

        GuiEventManager.bind(GRID_OBJ_HOVER_ON, (event) -> {
            if (ScreenMaster.getScreen() instanceof DungeonScreen){
                if (DungeonScreen.getInstance().isBlocked())
                    return;
            }
            if (Cinematics.ON) {
                return;
            }
            if (ScreenMaster.getScreen().getController().isLeftPressed()) {
                return;
            }
            BaseView object = (BaseView) event.get();
            hovered(object);
            if (object instanceof LastSeenView) {
            }
        });

        GuiEventManager.bind(SCALE_UP_VIEW, (event) -> {
            BaseView object = (BaseView) event.get();
            scaleUp(object);
            WaitMaster.doAfterWait(3500, () -> scaleDown(object));
        });

        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
            BaseView object = (BaseView) event.get();
            hoverOff(object);
        });

        actorCell = addElement(null);
    }

    public static boolean isHoverOff() {
        return hoverOff;
    }

    public static void setHoverOff(boolean hoverOff) {
        ToolTipManager.hoverOff = hoverOff;
    }

    private void requestedShow(Object object) {
        if (object != null)
            if (tooltip == object) {
                //            TODO dangerous?     originalPosition = GdxMaster.getCursorPosition(this);
                tooltip.fadeIn();
                if (isLogged()) LogMaster.log(1, "Update ignored ");
                return;
            }
        if (tooltip != null) {
            if (object == null)
                if (tooltipPanel != null) {
                    if (HqPanel.getActiveInstance() != null)
                        if (GdxMaster.isVisibleEffectively(tooltipPanel)) {
                            return;
                        }
                }
            ActionMasterGdx.addFadeOutAction(tooltip, 0.35f);
        }
        if (object == null) {
            actorCell.setActor(tooltip = null);
            originalPosition = null;
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

        if (tooltipPanel != null) {
            if (tooltip.isBattlefield()) {
                return;
            }
            if (HqPanel.getActiveInstance() != null)
                if (GdxMaster.isVisibleEffectively(tooltipPanel)) {
                    tooltipPanel.init(tooltip);
                    return;
                }
        }
        init(tooltip);
    }

    private float getTimeToWaitForTooltip(Tooltip tooltip) {
        if (tooltip == null) {
            return -1;
        }
        if (!tooltip.isBattlefield()) {
            return 0;
        }
        return DEFAULT_WAIT_TIME;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (hidden)
            return;
        if (parentAlpha == ShaderDrawer.SUPER_DRAW ||
                ConfirmationPanel.getInstance().isVisible())
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null, false, false);
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

        //        presetTooltipPos= null;
        updatePosition();

        tooltip.getColor().a = 0;
        tooltip.clearActions();
        ActionMasterGdx.addFadeInAction(tooltip, 0.5f);
        if (tooltip.getEntity() != null)
            entityHover(tooltip.getEntity());
    }

    protected boolean isHidden() {
        if (DialogueManager.isRunning()) {
            return true;
        }
        if (DC_Game.game.getDungeonMaster().getPuzzleMaster() != null) {
            if (DC_Game.game.getDungeonMaster().getPuzzleMaster().isUiMinimized()) {
                return true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) != CoreEngine.isLevelEditor()) {
            if (tooltip instanceof UnitViewTooltip)
                return true;
        }
        if (DungeonScreen.getInstance() != null)
            if (DungeonScreen.getInstance().getGridPanel() != null)
                return DungeonScreen.getInstance().getGridPanel().getActiveCommentSprites().size() > 0;
        return false;
    }

    @Override
    public void act(float delta) {
        if (hidden = isHidden())
            return;
        // if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
        //  //TODO    retained = tooltip;
        // }
        super.act(delta);
        hoverCheck -= delta;
        if (hoverCheck <= 0) {
            hoverCheck = HOVER_CHECK_PERIOD;
            resetHovered();
        }


        stackMaster.act(delta);
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


        if (tooltip != null) {
            if (tooltip.isBattlefield()) {
                if (guiStage.isBlocked() ||
                        originalPosition != null && originalPosition.dst(GdxMaster.getCursorPosition(this))
                                > 300 * GdxMaster.getFontSizeModSquareRoot()) {
                    //                    if (guiStage.isBlocked())
                    //                        main.system.auxiliary.log.LogMaster.log(1, tooltip + " Blocked");
                    //                    else
                    //                        main.system.auxiliary.log.LogMaster.log(1, tooltip + " Too far!" + originalPosition + "vs " + GdxMaster.getCursorPosition(this));
                    requestedShow(null);
                    //                        TODO ? ? if (isStackHoverOn())
                    //                            waitToHideStack = 2;
                }
            }
        }
        if (tooltip == null) {
            return;
        }
        updatePosition();
    }


    private void resetHovered() {
        for (BaseView hoverable : hoveredList) {
            if (hoverable != hovered) {
                hoverOff(hoverable);
            }
        }
    }


    private void updatePosition() {
        if (presetTooltipPos != null) {
            Vector2 pos = new Vector2(presetTooltipPos);
            pos.lerp(new Vector2(
                    Gdx.input.getX(), Gdx.input.getY(
            )), 0.3f);
            setPosition(presetTooltipPos.x - tooltip.getWidth() + 400,
                    presetTooltipPos.y - tooltip.getHeight() - 150);
            //set align too
            return;
        }
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

        float offsetY = 32;
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

        float offsetX = 32;
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
        if (presetTooltipPos != null)
            return;
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
        if (entity instanceof ActiveObj) {
            GuiEventManager.trigger(ACTION_HOVERED_OFF, entity);
            if (DC_Engine.isAtbMode())
                if (!ExplorationMaster.isExplorationOn())
                    GuiEventManager.trigger(GuiEventType.ATB_POS_PREVIEW, null);
        }
        if (entity instanceof UnitAction) {
            AnimMaster3d.hoverOff((UnitAction) entity);
        }
        if (guiStage instanceof GuiStage) {
            ((GuiStage) guiStage).getRadial().hoverOff(entity);
        }
    }

    public void entityHover(Entity entity) {
        if (entity instanceof ActiveObj) {
            GuiEventManager.trigger(ACTION_HOVERED, entity);
        }
        if (entity instanceof UnitAction) {
            if (((UnitAction) entity).isAttackAny())
                AnimMaster3d.initHover((UnitAction) entity);
            return;
        }

        if (guiStage instanceof GuiStage) {
            ((GuiStage) guiStage).getRadial().hover(entity);
        }
        //differentiate radial from bottom panel? no matter really ... sync :)
        //        guiStage.getBottomPanel().getSpellPanel().getCellsSet();

    }


    private void hovered(BaseView object) {

        CursorDecorator.getInstance().hovered(object.getUserObject());
        if (object.getUserObject() instanceof Structure) {
            if (((Structure) object.getUserObject()).isLandscape()) {
                return;
            }
        }
        object.setHovered(true);
        if (object instanceof QueueView) {
            ((QueueView) object).hoverOn();
        } else {
            if (object instanceof UnitGridView) {
                ((UnitGridView) object).getInitiativeQueueUnitView().hoverOn();
            }
            scaleUp(object);
        }

        hoverOff = false;
        stackMaster.checkShowStack(object);


        ScreenMaster.getGrid().setUpdateRequired(true);
        hoveredList.add(object);
        hovered = object;

    }

    private void scaleUp(BaseView object) {
        scale(object, getZoomScale(object), getZoomScale(object));
    }

    private void scaleDown(BaseView object) {
        scale(object, object.getScaledWidth(), getDefaultScale(object));

    }


    private void scale(BaseView object, float scale, float scaleQueue) {
        //        float scaleX = getDefaultScale(object);
        //        if (object.getScaleX() == getDefaultScale(object))
        //            scaleX = getZoomScale(object);
        //        float scaleY = getDefaultScale(object);
        //        if (object.getScaleY() == getDefaultScale(object))
        //            scaleY = getZoomScale(object);

        ActionMasterGdx.
                addScaleActionIfNoActions(object, scale, scale, 0.35f);

    }

    private void hoverOff(BaseView object) {
        CursorDecorator.getInstance().hoverOff();
        if (object instanceof LastSeenView) {
            return;
        }
        if (object.getUserObject() instanceof Structure) {
            if (((Structure) object.getUserObject()).isLandscape()) {
                return;
            }
        }
        scaleDown(object);
        ActionMasterGdx.screenOff(object);
        float scaleX;
        float scaleY;
        object.setHovered(false);
        if (object instanceof UnitGridView) {
            scaleX = object.getScaledWidth();
            scaleY = object.getScaledHeight();
            ActionMasterGdx.
                    addScaleAction(object, scaleX, scaleY, 0.35f);
            ((UnitGridView) object).getInitiativeQueueUnitView().hoverOff();
        } else if (object instanceof QueueView) {
            ((QueueView) object).hoverOff();
        }

        hoverOff = true;
        stackMaster.checkStackOff(object);
        ScreenMaster.getGrid().setUpdateRequired(true);
        hoveredList.remove(object);

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
        return 1.08f;
    }

    private float getDefaultScale(BaseView object) {
        //        if (object instanceof OverlayView){
        //            return OverlayView.SCALE;
        //        } gridPanel handles this by setBounds()!
        return 1;
    }

    public GenericGuiStage getGuiStage() {
        return guiStage;
    }

    public StackViewMaster getStackMaster() {
        return stackMaster;
    }
}
