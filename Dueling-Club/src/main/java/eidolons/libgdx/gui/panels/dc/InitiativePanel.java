package eidolons.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.UnitView;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.generic.SuperContainer;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import eidolons.libgdx.gui.panels.dc.clock.ClockActor;
import eidolons.libgdx.gui.panels.dc.clock.GearCluster;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.texture.TextureCache;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class InitiativePanel extends Group {
    public final static int imageSize = 104;
    private final int maxSize = 25;
    private final int visualSize = 10;
    private final int offset = -12;
    private final GearCluster gears;
    Label timeLabel;
    private QueueView[] queue;
    private WidgetGroup queueGroup;
    private ValueContainer panelImage;
    private Container<WidgetGroup> container;
    private float queueOffsetY = 15;
    private SuperContainer light;
    private Actor clock;
    private boolean checkPositionsRequired;
    private float maxMoveAnimDuration = 0;
    private float timePassedSincePosCheck = Integer.MAX_VALUE;
    private ImageContainer previewActor;
    private boolean animatedClock = true;

    public InitiativePanel() {
        init();
        registerCallback();
        resetZIndices();
        addActor(gears = new GearCluster(3, 0.8f));
        gears.setPosition(80, -25);
    }

    public static boolean isLeftToRight() {
        return true;
    }

    private Actor createAnimatedClock() {
        return new ClockActor();
    }

    private void registerCallback() {

        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            if (!isRealTime()) {
                UnitView p = (UnitView) obj.get();
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
                if (!(obj.get() instanceof UnitView))
                    return;
                UnitView p = (UnitView) obj.get();
                QueueView view = getIfExists(p.getCurId());
                if (view != null)
                    view.setZIndex(Integer.MAX_VALUE - 1);
            }
            DungeonScreen.getInstance().getGridPanel().resetZIndices();
        });
        GuiEventManager.bind(GuiEventType.GRID_OBJ_HOVER_OFF, obj -> {
            if (!isRealTime()) {
                resetZIndices();
            }
            DungeonScreen.getInstance().getGridPanel().resetZIndices();

        });
        GuiEventManager.bind(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, obj -> {
//            if (!isRealTime()) {
            UnitView p = (UnitView) obj.get();
            removeView(p);
            resetZIndices();
//                checkPositionsRequired=true;
//            }
        });
        if (DC_Engine.isAtbMode()) {

            GuiEventManager.bind(GuiEventType.PREVIEW_ATB_READINESS, obj -> {
                List<Integer> list = (List<Integer>) obj.get();
                previewAtbReadiness(list);
            });
            GuiEventManager.bind(GuiEventType.ATB_POS_PREVIEW, obj -> {
                if (obj.get() == null) {
                    removePreviewAtbPos();
                    previewAtbReadiness(null);
                } else {
                    previewAtbPos((int) obj.get());
                }

            });
        }
    }

    private void previewAtbReadiness(List<Integer> list) {
        int i = 0;
        for (int j = queue.length - 1; j > 0; j--) {
            QueueView sub = queue[j];
            if (sub == null)
                break;
            UnitView actor = (UnitView) sub.getActor();
            String text = (list == null ? sub.initiative : list.get(i++)) + "";
            actor.setInitiativeLabelText(text);
        }
    }

    private void init() {

        queue = new QueueView[maxSize];
        queueGroup = new WidgetGroup();
        queueGroup.setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        container = new Container<>(queueGroup);
        container.setBounds(imageSize - offset, queueOffsetY, imageSize * visualSize +
         (offset - 1) * visualSize, imageSize);
        container.left().bottom();
        if (ExplorationMaster.isExplorationOn()) {
            container.setY(imageSize);
            container.setVisible(false);
        }

        addActor(container);

        final TextureRegion textureRegion = getOrCreateR(StrPathBuilder.build("UI",
         "components", "2017", "panels", "initiativepanel",
         "initiativePanel plain.png"));
        panelImage = new ValueContainer(textureRegion);
//
        panelImage.setPosition(50, 25 + queueOffsetY);
//        image.align(Align.bottomLeft);
//        image.overrideImageSize(imageSize, imageSize);
//        image.setSize(imageSize, imageSize);
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer("Good time to die!", "")));
        panelImage.addListener(tooltip.getController());
        addActor(panelImage);
//        panelImage.setPosition(0, -textureRegion.getRegionHeight());

        setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize,
//         textureRegion.getRegionHeight()
         imageSize + queueOffsetY);

        light = new SuperContainer(
         new Image(TextureCache.getOrCreateR(SHADE_LIGHT.LIGHT_EMITTER.getTexturePath())), true);
        clock =
         animatedClock
          ? createAnimatedClock()
          :
          new SuperContainer(
           new Image(TextureCache.getOrCreateR(StrPathBuilder.build("UI",
            "components", "2017", "panels", "initiativepanel",
            "clock.png"))), true) {
              @Override
              protected float getAlphaFluctuationMin() {
                  return 0.6f;
              }

              @Override
              protected float getAlphaFluctuationPerDelta() {
                  return super.getAlphaFluctuationPerDelta() / 3;
              }
          };
        clock.addListener(getClockListener());

        addActor(light);
        light.setPosition(-15, -14);
        addActor(clock);
        clock.setPosition(-23, -31);


        timeLabel = new Label("Time", StyleHolder.getSizedLabelStyle(FONT.NYALA, 22));
        addActor(timeLabel);
        timeLabel.setPosition(15, 100);
    }

    private EventListener getClockListener() {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                DC_Game.game.getDungeonMaster().
                 getExplorationMaster().
                 getTimeMaster().playerWaits();
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    private void resetPositions() {
        container.setBounds(imageSize + offset, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        panelImage.setPosition(0, -panelImage.getHeight());
        timeLabel.setPosition(50, 0);
    }

    private void resetZIndices() {
        panelImage.setZIndex(0);
        for (QueueView view : queue) {
            if (view != null) {
                view.setZIndex(Integer.MAX_VALUE);
            }
        }
        light.setZIndex(Integer.MAX_VALUE);
        clock.setZIndex(Integer.MAX_VALUE);
    }

    private void removeView(UnitView p) {
        removeView(p.getCurId());
    }

    private void removeView(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                queueGroup.removeActor(queue[i]);
                queue[i] = null;
                sort();
                break;
            }
        }
    }

    public boolean isCleanUpOn() {
        return true;
    }

    private void checkPositions() {
        int n = 0;
        boolean moveApplied = false;
        for (int i = queue.length - 1; i >= 0; i--) {
            QueueView sub = queue[isLeftToRight() ? i : queue.length - 1 - i];
            if (sub == null)
                continue;
//            if (sub.getActions().size != 0)
//                continue;
            float x = relToPixPos(n);
            n++;
            if (sub.getX() == x)
                continue;
            if (sub.mainHero) {
                gears.setClockwise(sub.getX() > x);
            }
            moveApplied = true;
            sub.getActions().clear();
            AfterAction a = new AfterAction();
            a.setAction(ActorMaster.getMoveToAction(x, sub.getY(), getViewMoveDuration()));
            sub.addAction(a);
            a.setTarget(sub);
//            sub.setX(relToPixPos(n));
//            queue[i].getActions().clear();
        }
        timePassedSincePosCheck = 0;
        checkPositionsRequired = false;
        if (moveApplied) {
            gears.activeWork(getViewMoveDuration() / 2, getViewMoveDuration() / 2);
        }
    }

    private float getViewMoveDuration() {
        return 1;
    }

    private void cleanUp() {
        Map<Integer, UnitView> views = new XLinkedMap<>();
        DC_Game.game.getTurnManager().getDisplayedUnitQueue().stream().forEach(unit -> {
            UnitView view = (UnitView) DungeonScreen.getInstance().getGridPanel().getViewMap().get(unit);
            views.put(view.getCurId(), view);
        });
        /*
        if unit is unknown/unseen?
        >>
        if unit is unable to act?

        proper cleanup is not done on death(), right?
         */
        for (QueueView sub : queue) {
            if (sub == null)
                continue;
            if (!views.containsKey(sub.id)) {
                QueueView view = getIfExists(sub.id);
                if (view == null)
                    continue;
                if (view.getActor() == null)
                    continue;
                removeView((sub.id));
            }
        }
        previewAtbReadiness(null); // to ensure proper values are displayed

    }

    private void addOrUpdate(UnitView unitView) {
        updateTime();
        QueueView container = getIfExists(unitView.getCurId());
        if (container == null) {
            container = new QueueView(unitView);
            container.id = unitView.getCurId();
            container.size(imageSize, imageSize);
            container.left().bottom();
            int lastSlot = getLastEmptySlot();
            queue[lastSlot] = container;
            queueGroup.addActor(container);
        }

        container.queuePriority = 1f/unitView.getTimeTillTurn();
        container.initiative = unitView.getInitiativeIntVal();
        container.mobilityState = unitView.getMobilityState();
        sort();
    }

    private int getLastEmptySlot() {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null) {
                return i;
            }
        }
        extendQueue();
        return 0;
    }

    private void extendQueue() {
        QueueView[] newc = new QueueView[queue.length + 3];
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

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isRealTime()) {
            updateTime();
            if (container.isVisible())
                if (ActorMaster.getActionsOfClass(container, MoveToAction.class).size() == 0) {
                    toggleQueue(false);
                }
        } else {
            if (!container.isVisible())
                if (ActorMaster.getActionsOfClass(container, MoveToAction.class).size() == 0)
                    toggleQueue(true);

            timePassedSincePosCheck += delta;
            if (DC_Game.game.isDebugMode() ||
             timePassedSincePosCheck > 2 ||
             (checkPositionsRequired && timePassedSincePosCheck >= maxMoveAnimDuration))
                checkPositions();
            else {
//                for (QueueView sub : queue)
//                    if (sub != null)
//                        if (sub.getActions().size > 0)
//                            return;
//
//                gears.work();
            }
        }
    }

    private void toggleQueue(boolean visible) {

        cleanUp();
        float x = container.getX();
        float y = !visible ? container.getHeight() : queueOffsetY;
        ActorMaster.addMoveToAction(container, x, y, 1);
        gears.activeWork(0.5f, 1);
        if (visible)
            container.setVisible(visible);
        else
            ActorMaster.addHideAfter(container);
    }

    private boolean isRealTime() {
        return ExplorationMaster.isExplorationOn();
    }

    private void updateTime() {
        String time = "";
        if (isRealTime()) {
            time = Eidolons.game.getDungeonMaster().getExplorationMaster().getTimeMaster().getDisplayedTime();
        } else {
            time =
             DC_Engine.isAtbMode() ?
              Eidolons.game.getTurnManager().getTimeString()
              :
              String.valueOf(Eidolons.game.getRules().getTimeRule().getTimeRemaining());
        }
        timeLabel.setText(time);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    private void sort() {
        for (int i = 0; i < queue.length - 1; i++) {
            final int ip1 = i + 1;
            QueueView cur = queue[i];

            if (cur != null) {
                QueueView next = queue[ip1];
                if (next == null) {
                    queue[i] = null;
                    queue[ip1] = cur;
                } else {
                    boolean result =
                     isSortByTimeTillTurn() ? cur.queuePriority > next.queuePriority || (cur.id > next.id && cur.queuePriority == next.queuePriority)
                      : cur.initiative > next.initiative || (cur.id > next.id && cur.initiative == next.initiative);
                    if (result) {
                        queue[ip1] = cur;
                        queue[i] = next;
                        for (int y = i; y > 0; y--) {
                            if (queue[y - 1] == null) {
                                break;
                            }
                            if (queue[y].initiative >= queue[y - 1].initiative) {
                                break;
                            }
                            QueueView buff = queue[y - 1];
                            queue[y - 1] = queue[y];
                            queue[y] = buff;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < queue.length; i++) {
            QueueView cur = queue[i];
            if (cur != null && !cur.mobilityState) {
                for (int y = i; y > 0; y--) {
                    if (queue[y - 1] == null) {
                        break;
                    }
                    if (!queue[y - 1].mobilityState) {
                        break;
                    }
                    QueueView buff = queue[y - 1];
                    queue[y - 1] = queue[y];
                    queue[y] = buff;
                }
            }
        }
//        applyImageMove();
    }

    private boolean isSortByTimeTillTurn() {
        return true;
    }


    private void removePreviewAtbPos() {
        if (previewActor != null) {
            previewActor.setVisible(false);
        }
    }

    private void previewAtbPos(int i) {
        if (i > maxSize) {
            //TODO
        }
        if (previewActor == null) {
            addActor(previewActor = new ImageContainer(getPreviewPath()));
            previewActor.setAlphaTemplate(ALPHA_TEMPLATE.ATB_POS);
        }
        previewActor.setVisible(true);
        previewActor.setScale(1.25f);
        previewActor.clearActions();
        previewActor.setY(-30);
        previewActor.setX(container.getX() + (i + 1) * (imageSize + offset) - offset / 2);
        ActorMaster.addScaleActionCentered(previewActor, 0, 1, 8);
//        ActorMaster.addMoveToAction(previewActor, previewActor.getX()+previewActor.getWidth()/2,
//         previewActor.getY(), 5);
    }

    private String getPreviewPath() {
        return StrPathBuilder.build(PathFinder.getComponentsPath()
         , "2018", "atb pos preview.png"
        ) + StringMaster.getPathSeparator();
    }

    private float relToPixPos(int pos) {
        return pos * imageSize + pos * offset;
    }

    private QueueView getIfExists(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                return queue[i];
            }
        }
        return null;
    }


    private class QueueView extends Container<UnitView> {
        public int initiative;
        public int precalcInitiative;
        public float queuePriority;
        public float precalcQueuePriority;
        public int id;
        public boolean mobilityState;
        public boolean mainHero;
        public boolean immobilized;

        public QueueView(UnitView actor) {
            super(actor);
            mainHero = actor.isMainHero();
            if (actor == null) {
                return;
            }
//            Image shadow = new Image(TextureCache.getOrCreateR(
//             StrPathBuilder.build("UI",
//              "components", "2017", "panels",
//              "initiativepanel",
//              "initiativepanel unitview shadow.png")));
//            shadow.setY(-48);
//            actor.addActor(shadow);

        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            ShaderProgram shader = batch.getShader();
            if (immobilized) {
                batch.setShader(DarkShader.getShader());
            }
            super.draw(batch, parentAlpha);
            batch.setShader(shader);
        }
    }
}
