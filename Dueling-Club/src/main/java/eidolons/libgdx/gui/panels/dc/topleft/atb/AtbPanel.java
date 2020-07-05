package eidolons.libgdx.gui.panels.dc.topleft.atb;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.boss.anims.view.BossQueueView;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.grid.cell.QueueView;
import eidolons.libgdx.bf.grid.cell.UnitGridView;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.topleft.ClockPanel;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

public class AtbPanel extends GroupX {
    public final static int imageSize = 96;
    private static final float POS_CHECK_PERIOD = 1f;
    protected final int maxSize = 25;
    protected final int visualSize = 10;
    protected final int DST_BETWEEN_VIEWS = 12;

    protected int viewsShown;
    protected final int maxSizeStacked = 3;
    int viewsToShow = maxSizeStacked;

    AtbViewManager manager;
    protected QueueViewContainer[] queue;
    protected WidgetGroup queueGroup;
    protected TablePanelX container;

    protected float queueOffsetY = 22;
    protected float combatQueueOffsetY = -42; //depend on soulbar vis?
    protected boolean checkPositionsRequired;
    protected float maxMoveAnimDuration = 0;
    protected float timePassedSincePosCheck = Integer.MAX_VALUE;
    protected ImageContainer previewActor;

    ClockPanel clock;

    QueueStackPanel stackPanel;
    SmartTextButton stackButton; //rotate it?!
    boolean stacked;
    private ImageContainer horizontal;
    private Image background;

    public AtbPanel(ClockPanel clock) {
        this.clock = clock;
        manager = new AtbViewManager(this);
        init();
        bindEvents();
        resetZIndices();

    }

    public static float getSpeedFactor() {
        return 0.78f;
    }

    protected boolean isRealTime() {
        return ExplorationMaster.isExplorationOn();
    }

    public boolean isCleanUpOn() {
        return true;
    }

    public static boolean isLeftToRight() {
        return true;
    }

    protected void init() {
        //        addActor(background = new FadeImageContainer(StrPathBuilder.build("ui",
        //                "components", "dc", "atb", "atb background black.png")));

        queue = new QueueViewContainer[maxSize];
        queueGroup = new WidgetGroup();
        addActor(background = new Image(new TextureRegionDrawable(TextureCache.getOrCreateR(StrPathBuilder.build("ui",
                "components", "dc", "atb", "atb background black.png")))));
        background.setPosition(getInactiveX(), 24);
        addActor(horizontal = new ImageContainer(Images.SEPARATOR_METAL));

        addActor(container = new TablePanelX());
        container.add(queueGroup);
        horizontal.setY(13);
        horizontal.setX(-125);
        resetPositions();

    }


    protected void bindEvents() {

        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            if (!isRealTime()) {
                QueueView p = (QueueView) obj.get();
                addOrUpdate(p);

                if (isCleanUpOn())
                    cleanUp();
                resetZIndices();
            }
        });

        GuiEventManager.bind(GuiEventType.ACTIVE_UNIT_SELECTED, obj -> {
            if (!isRealTime())
                checkPositions();
        });
        GuiEventManager.bind(GuiEventType.UPDATE_GUI, obj -> {
            if (!isRealTime()) {
                //                cleanUp();
                resetZIndices();
            }
            checkPositionsRequired = true;
        });
        GuiEventManager.bind(GuiEventType.GRID_OBJ_HOVER_ON, obj -> {
            if (!isRealTime()) {
                if (!(obj.get() instanceof QueueView))
                    return;
                QueueView p = (QueueView) obj.get();
                QueueViewContainer view = manager.getIfExists(p.getCurId());
                if (view != null)
                    view.setZIndex(Integer.MAX_VALUE - 1);
            }
            ScreenMaster.getGrid().resetZIndices();
        });
        GuiEventManager.bind(GuiEventType.GRID_OBJ_HOVER_OFF, obj -> {
            if (!isRealTime()) {
                resetZIndices();
            }
            ScreenMaster.getGrid().resetZIndices();

        });
        GuiEventManager.bind(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, obj -> {
            //            if (!isRealTime()) {
            QueueView p = (QueueView) obj.get();
            removeView(p);
            resetZIndices();
            //                checkPositionsRequired=true;
            //            }
        });

    }


    protected void resetPositions() {
        if (ExplorationMaster.isExplorationOn()) {
            container.setY(imageSize);
            container.setVisible(false);
        }
        setBounds(0, 0, imageSize * visualSize + (DST_BETWEEN_VIEWS - 1) * visualSize,
                imageSize + queueOffsetY);
        queueGroup.setBounds(imageSize * 2, 0, imageSize * visualSize + (DST_BETWEEN_VIEWS - 1) * visualSize, imageSize);
        //        background.setPosition(50, 25 + queueOffsetY);
        container.setBounds(imageSize * 2 //imageSize - offset
                , queueOffsetY, imageSize * visualSize +
                        (DST_BETWEEN_VIEWS - 1) * visualSize, imageSize);
        container.left().bottom();
    }


    protected void resetZIndices() {
        for (QueueViewContainer view : queue) {
            if (view != null) {
                view.setZIndex(Integer.MAX_VALUE);
            }
        }
    }

    protected void removeView(QueueView p) {
        removeView(p.getCurId());
    }

    protected void removeView(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                // ActionMaster.addFadeInAndOutAction(queue[i], 0.35f, true);
                queueGroup.removeActor(queue[i]);
                queue[i] = null;
                manager.sort();
                break;
            }
        }
    }


    protected void checkPositions() {
        int n = 0;
        boolean moveApplied = false;
        for (int i = queue.length - 1; i >= 0; i--) {
            QueueViewContainer sub = queue[isLeftToRight() ? i : queue.length - 1 - i];
            if (sub == null)
                continue;
            //            if (sub.getActions().size != 0)
            //                continue;
            float x = manager.relToPixPos(n);
            n++;
            if (sub.getX() == x)
                continue;
            if (sub.mainHero) {
                getGears().setClockwise(sub.getX() > x);
            }
            moveApplied = true;
            sub.getActions().clear();
            AfterAction a = new AfterAction();
            a.setAction(ActionMaster.getMoveToAction(x, sub.getY(), getViewMoveDuration()));
            sub.addAction(a);
            a.setTarget(sub);
            //            sub.setX(relToPixPos(n));
            //            queue[i].getActions().clear();
        }
        timePassedSincePosCheck = 0;
        checkPositionsRequired = false;
        if (moveApplied) {
            getGears().activeWork(getViewMoveDuration() / 2, getViewMoveDuration() / 2);
        }
    }

    private GearCluster getGears() {
        return clock.getGears();
    }

    protected float getViewMoveDuration() {
        return 1;
    }

    protected void cleanUp() {
        List<QueueView> views = new ArrayList<>();
        DequeImpl<Unit> displayedUnitQueue = DC_Game.game.getTurnManager().getDisplayedUnitQueue();
        viewsShown = DC_Game.game.getTurnManager().getDisplayedUnitQueue().size();
        displayedUnitQueue.stream().forEach(unit -> {
            Object o = ScreenMaster.getGrid().getViewMap().get(unit);
            if (o instanceof UnitGridView) {
                UnitGridView view = ((UnitGridView) o);
                views.add(view.getInitiativeQueueUnitView());
            }
        });

        for (QueueViewContainer sub : queue) {
            if (sub == null)
                continue;
            if (!views.contains(sub.getActor())) {
                QueueViewContainer view = manager.getIfExists(sub.getActor());
                if (view == null)
                    continue;
                if (view.getActor() == null)
                    continue;
                if (view.getActor() instanceof BossQueueView)
                    continue;
                removeView((sub.id));
            }
        }
        manager.previewAtbReadiness(null); // to ensure proper values are displayed

    }

    protected void addOrUpdate(QueueView unitView) {
        clock.updateTime();
        QueueViewContainer container = manager.getIfExists(unitView.getCurId());
        if (container == null) {
            container = new QueueViewContainer(unitView);
            container.id = unitView.getCurId();
            container.size(imageSize, imageSize);
            container.left().bottom();
            int lastSlot = getLastEmptySlot();
            queue[lastSlot] = container;
            queueGroup.addActor(container);
        }

        container.queuePriority = 1f / unitView.getTimeTillTurn();
        container.initiative = unitView.getInitiativeIntVal();
        container.mobilityState = unitView.isQueueMoving();
        manager.sort();
    }

    protected int getLastEmptySlot() {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null) {
                return i;
            }
        }
        extendQueue();
        return 0;
    }

    protected void extendQueue() {
        QueueViewContainer[] newc = new QueueViewContainer[queue.length + 3];
        System.arraycopy(queue, 0, newc, 3, queue.length);
        queue = newc;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == this) {
            actor = null;
        }
        return actor;
    }


    public void toggleQueue(boolean visible) {
        cleanUp();
        // rollComponent(container, visible);

        float x = !visible ? getInactiveX() : getActiveX();
        float y = background.getY();
        ActionMaster.addMoveToAction(background, x, y, 2f);
        container.setVisible(true);
        ActionMaster.addAlphaAction(container, 2f, !visible);

        checkPositionsRequired = true;
        getGears().activeWork(0.5f, 1);
    }

    private float getInactiveX() {
        return -background.getWidth();
    }

    private float getActiveX() {
        if (viewsShown>=8) {
            return   Math.min(200, -background.getWidth() + (950 + (imageSize + 12) * viewsShown));
        }
        return -background.getWidth() + (950 + (imageSize+12) * 2);
    }

    protected void rollComponent(Actor container, boolean visible) {
        float x = container.getX();
        float y = !visible ? container.getHeight() - combatQueueOffsetY : queueOffsetY;
        ActionMaster.addMoveToAction(container, x, y, 1);
        getGears().activeWork(0.5f, 1);
        if (visible)
            container.setVisible(true);
        else
            ActionMaster.addHideAfter(container);
    }


    @Override
    public void act(float delta) {
        container.setX(184);
        horizontal.setY(8);
        super.act(delta);
        if (isRealTime()) {
            // if (container.isVisible())
            //     if (ActionMaster.getActionsOfClass(container, MoveToAction.class).size() == 0) {
            //         toggleQueue(false);
            //     }
        } else {
            // if (!container.isVisible())
            //     if (ActionMaster.getActionsOfClass(container, MoveToAction.class).size() == 0)
            //         toggleQueue(true);

            timePassedSincePosCheck += delta;
            if (DC_Game.game.isDebugMode() ||
                    timePassedSincePosCheck > POS_CHECK_PERIOD ||
                    (checkPositionsRequired && timePassedSincePosCheck >= maxMoveAnimDuration))
                checkPositions();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
