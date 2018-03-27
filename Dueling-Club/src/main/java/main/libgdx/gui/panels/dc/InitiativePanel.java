package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import main.libgdx.bf.UnitView;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.Arrays;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InitiativePanel extends Group {
    public final static int imageSize = 104;
    private final int maxSize = 25;
    private final int visualSize = 10;
    private final int offset = -12;
    Label timeLabel;
    private QueueView[] queue;
    private WidgetGroup queueGroup;
    private ValueContainer panelImage;
    private Container<WidgetGroup> container;
    private float queueOffsetY = 15;
    private SuperContainer light;
    private SuperContainer clock;
    private boolean checkPositionsRequired;
    private float maxMoveAnimDuration = 0;
    private float timePassedSincePosCheck = Integer.MAX_VALUE;
    private ImageContainer previewActor;

    public InitiativePanel() {
        init();
        registerCallback();
        resetZIndices();
    }

    public static boolean isLeftToRight() {
        return true;
    }

    private void registerCallback() {
        if (DC_Engine.isAtbMode()) {
            GuiEventManager.bind(GuiEventType.ATB_POS_PREVIEW, obj -> {
                if (obj.get() == null)
                    removePreviewAtbPos();
                previewAtbPos((int) obj.get());
            });
        }
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
        clock = new SuperContainer(
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
            sub.getActions().clear();
            AfterAction a = new AfterAction();
            a.setAction(ActorMaster.getMoveToAction(x, sub.getY(), 1));
            sub.addAction(a);
            a.setTarget(sub);
//            sub.setX(relToPixPos(n));
//            queue[i].getActions().clear();
        }
        timePassedSincePosCheck = 0;
        checkPositionsRequired = false;
    }

    private void cleanUp() {
        Map<Integer, UnitView> views = new XLinkedMap<>();
        DC_Game.game.getTurnManager().getDisplayedUnitQueue().stream().forEach(unit -> {
            UnitView view = (UnitView) DungeonScreen.getInstance().getGridPanel().getUnitMap().get(unit);
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
        previewActor.setScale(1);
        previewActor.clearActions();
        previewActor.setY(-30);
        previewActor.setX(container.getX() + i * (imageSize + offset) - offset);
//        ActorMaster.addScaleAction(previewActor, 0, 0, 5);
    }

    private String getPreviewPath() {
        return StrPathBuilder.build(PathFinder.getComponentsPath()
         , "2018", "atb pos preview.png"
        ) + StringMaster.getPathSeparator();
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

        container.queuePriority = 1f / unitView.getTimeTillTurn();
        container.initiative = unitView.getInitiativeIntVal();
        container.mobilityState = unitView.getMobilityState();
        sort();
    }


    private Image newImage(int pos, InitiativePanelParam panelParam) {
        Image i = new Image(panelParam.getTextureRegion());
        i.setBounds(relToPixPos(pos), 0, imageSize, imageSize);
        return i;
    }

    private Image newImage(Image old, InitiativePanelParam panelParam) {
        Image image = new Image(panelParam.getTextureRegion());
        image.setBounds(old.getX(), 0, old.getWidth(), old.getHeight());
        old.clear();
        return image;
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
        }
    }

    private void toggleQueue(boolean visible) {

        cleanUp();
        float x = container.getX();
        float y = !visible ? container.getHeight() : queueOffsetY;
        ActorMaster.addMoveToAction(container, x, y, 1);
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
                    if (cur.initiative > next.initiative || (cur.id > next.id && cur.initiative == next.initiative)) {
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

    private void applyImageMove() {
        int from = queue.length - visualSize;
        int to = queue.length - 1;
        int rpos = 0;
        setupAnim(from, to, rpos);

        to = from;
        from = 0;
        rpos = queue.length - visualSize * -1;
        setupAnim(from, to, rpos);
    }

    private void setupAnim(int from, int to, int rpos) {
        if (from <= to)
            return;
        maxMoveAnimDuration = 0;
        timePassedSincePosCheck = 0;
        for (int i = from; i <= to; i++) {
            QueueView container = queue[i];
            float pixPos = relToPixPos(rpos);
            if (container != null) {
                if (container.getX() != pixPos) {
                    MoveToAction a = new MoveToAction();
                    a.setX(pixPos);
                    float dur = Math.abs(container.getX() - pixPos) / 400;
                    if (dur > maxMoveAnimDuration)
                        maxMoveAnimDuration = dur;
                    a.setDuration(dur);
                    a.setTarget(container);
                    container.addAction(a);
                }
                rpos++;
            }
        }
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


    private class QueueView extends Container<Actor> {
        public int initiative;
        public float queuePriority;
        public int id;
        public boolean mobilityState;

        public QueueView(UnitView actor) {
            super(actor);
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
        public void setActor(Actor actor) {
            super.setActor(actor);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }
    }
}
