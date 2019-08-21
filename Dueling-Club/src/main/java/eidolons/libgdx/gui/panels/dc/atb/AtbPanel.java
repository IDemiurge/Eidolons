package eidolons.libgdx.gui.panels.dc.atb;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.Fluctuating.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.libgdx.bf.grid.QueueView;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_CELL;
import eidolons.libgdx.gui.HideButton;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.SpeedControlPanel;
import eidolons.libgdx.gui.panels.dc.clock.ClockActor;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.libgdx.screens.CustomSpriteBatch;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class AtbPanel extends GroupX {
    public final static int imageSize = 104;
    private final int maxSize = 25;
    private final int visualSize = 10;
    private final int offset = -12;
    Label timeLabel;
    private QueueViewContainer[] queue;
    private WidgetGroup queueGroup;
    private ValueContainer panelImage;
    private Container<WidgetGroup> container;
    private float queueOffsetY = 15;
    private boolean checkPositionsRequired;
    private float maxMoveAnimDuration = 0;
    private float timePassedSincePosCheck = Integer.MAX_VALUE;
    private ImageContainer previewActor;

    AtbViewManager manager;

    private GearCluster gears;
    private ClockActor clock;
    private FadeImageContainer light;

    SpeedControlPanel speedControlPanel;
    private HideButton hideButton;

    public AtbPanel() {
        manager = new AtbViewManager(this);
        init();
        bindEvents();
        resetZIndices();
        gears.setPosition(80, -33);

        initResolutionScaling();
    }

    public static boolean isLeftToRight() {
        return true;
    }

    private void init() {

        addActor(gears = new GearCluster(true, 3, 1.2f, true));
        queue = new QueueViewContainer[maxSize];
        queueGroup = new WidgetGroup();
        addActor(
//                RollDecorator.decorate(
                speedControlPanel = new SpeedControlPanel());
        addActor(container = new Container<>(queueGroup));
        addActor(hideButton = new HideButton(speedControlPanel));
        speedControlPanel.setPosition(0, -150);
        hideButton.setPosition(130, -100);

        final TextureRegion textureRegion = getOrCreateR(StrPathBuilder.build("ui",
                "components", "dc", "atb",
                "atb background.png"));
        DynamicTooltip tooltip = new DynamicTooltip(() -> "Time:" + NumberUtils.getFloatWithDigitsAfterPeriod(DC_Game.game.getLoop().getTime(), 1));


        addActor(panelImage = new ValueContainer(textureRegion));
        panelImage.addListener(tooltip.getController());

//        addActor(
        light = new FadeImageContainer(SHADE_CELL.LIGHT_EMITTER.getTexturePath());

        addActor(clock = new ClockActor());
        clock.addListener(getClockListener());

        timeLabel = new Label("Time", StyleHolder.getSizedLabelStyle(FONT.AVQ, 20));
        addActor(timeLabel);


        resetPositions();

    }


    private void bindEvents() {

        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            if (!isRealTime()) {
                QueueView p = (QueueView) obj.get();
                addOrUpdate(p);

                if (isCleanUpOn())
                    cleanUp();
                resetZIndices();
            }
        });
        GuiEventManager.bind(GuiEventType.PUZZLE_STARTED, p -> {
            rollComponent(speedControlPanel, false);
        });
        GuiEventManager.bind(GuiEventType.PUZZLE_FINISHED, p -> {
            rollComponent(speedControlPanel, true);
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
                QueueViewContainer view = getIfExists(p.getCurId());
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
            QueueView p = (QueueView) obj.get();
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

    private EventListener getClockListener() {

        return new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!GdxMaster.isWithin(event.getTarget(),
                        new Vector2(event.getStageX(), event.getStageY()), true))
                    return;
                if (button == 0) {
                    DC_Game.game.getLoop().togglePaused();
                }

                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 1) {
                    if (!DC_Game.game.getLoop().isPaused())
                        DC_Game.game.getDungeonMaster().
                                getExplorationMaster().
                                getTimeMaster().playerWaits();
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        };
    }

    private void resetPositions() {
        if (ExplorationMaster.isExplorationOn()) {
            container.setY(imageSize);
            container.setVisible(false);
        }
        setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize,
                imageSize + queueOffsetY);
        queueGroup.setBounds(imageSize * 2, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        light.setPosition(-2, -14);
        clock.setPosition(-5, -31);
        panelImage.setPosition(50, 25 + queueOffsetY);
        container.setBounds(imageSize * 2 //imageSize - offset
                , queueOffsetY, imageSize * visualSize +
                        (offset - 1) * visualSize, imageSize);
        container.left().bottom();
    }


    private void previewAtbReadiness(List<Integer> list) {
        int i = 0;
        for (int j = queue.length - 1; j > 0; j--) {
            QueueViewContainer sub = queue[j];
            if (sub == null)
                break;
            QueueView actor = (QueueView) sub.getActor();
            String text = (list == null ? sub.initiative : list.get(i++)) + "";
            if (actor == null) {
                continue;
            }
            actor.setInitiativeLabelText(text);
        }
    }

    private void resetZIndices() {
        panelImage.setZIndex(0);
        for (QueueViewContainer view : queue) {
            if (view != null) {
                view.setZIndex(Integer.MAX_VALUE);
            }
        }
        light.setZIndex(Integer.MAX_VALUE);
        clock.setZIndex(Integer.MAX_VALUE);
    }

    private void removeView(QueueView p) {
        removeView(p.getCurId());
    }

    private void removeView(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                ActionMaster.addFadeInAndOutAction(queue[i], 0.35f, true);
//                queueGroup.removeActor(queue[i]);
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
            QueueViewContainer sub = queue[isLeftToRight() ? i : queue.length - 1 - i];
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
            a.setAction(ActionMaster.getMoveToAction(x, sub.getY(), getViewMoveDuration()));
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
        Map<Integer, QueueView> views = new XLinkedMap<>();
        DC_Game.game.getTurnManager().getDisplayedUnitQueue().stream().forEach(unit -> {
            Object o = DungeonScreen.getInstance().getGridPanel().getViewMap().get(unit);
            if (o instanceof GridUnitView) {
                GridUnitView view = ((GridUnitView) o);
                views.put(view.getCurId(), view.getInitiativeQueueUnitView());
            }

        });

        for (QueueViewContainer sub : queue) {
            if (sub == null)
                continue;
            if (!views.containsKey(sub.id)) {
                QueueViewContainer view = getIfExists(sub.id);
                if (view == null)
                    continue;
                if (view.getActor() == null)
                    continue;
                removeView((sub.id));
            }
        }
        previewAtbReadiness(null); // to ensure proper values are displayed

    }

    private void addOrUpdate(QueueView unitView) {
        updateTime();
        QueueViewContainer container = getIfExists(unitView.getCurId());
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
        sort();
    }

    private void updateTimedEvents() {

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

    @Override
    public void act(float delta) {
        boolean altBg = EidolonsGame.isAltControlPanel();
        hideButton.setVisible(!altBg);

        timeLabel.setPosition(19, 120 - timeLabel.getPrefHeight());
        timeLabel.setZIndex(Integer.MAX_VALUE);
        if (altBg) {
            if (speedControlPanel.getColor().a == 1)
                speedControlPanel.fadeOut();
        } else {
            if (speedControlPanel.getColor().a == 0)
                speedControlPanel.fadeIn();
        }
        hideButton.setPosition(160, -100);
        speedControlPanel.setPosition(0, -300);
        super.act(delta);
        if (isRealTime()) {
            updateTime();
            if (container.isVisible())
                if (ActionMaster.getActionsOfClass(container, MoveToAction.class).size() == 0) {
                    toggleQueue(false);
                }
        } else {
            if (!container.isVisible())
                if (ActionMaster.getActionsOfClass(container, MoveToAction.class).size() == 0)
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
        rollComponent(container, visible);
//        rollComponent(speedControlPanel, visible);
    }

    private void rollComponent(Actor container, boolean visible) {
        float x = container.getX();
        float y = !visible ? container.getHeight() : queueOffsetY;
        ActionMaster.addMoveToAction(container, x, y, 1);
        gears.activeWork(0.5f, 1);
        if (visible)
            container.setVisible(true);
        else
            ActionMaster.addHideAfter(container);
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
            QueueViewContainer cur = queue[i];

            if (cur != null) {
                QueueViewContainer next = queue[ip1];
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
                            QueueViewContainer buff = queue[y - 1];
                            queue[y - 1] = queue[y];
                            queue[y] = buff;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < queue.length; i++) {
            QueueViewContainer cur = queue[i];
            if (cur != null && !cur.mobilityState) {
                for (int y = i; y > 0; y--) {
                    if (queue[y - 1] == null) {
                        break;
                    }
                    if (!queue[y - 1].mobilityState) {
                        break;
                    }
                    QueueViewContainer buff = queue[y - 1];
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
        ActionMaster.addScaleActionCentered(previewActor, 0, 1, 8);
//        ActorMaster.addMoveToAction(previewActor, previewActor.getX()+previewActor.getWidth()/2,
//         previewActor.getY(), 5);
    }

    private String getPreviewPath() {
        return StrPathBuilder.build(PathFinder.getComponentsPath()
                , "dc", "atb pos preview.png"
        ) + PathUtils.getPathSeparator();
    }

    private float relToPixPos(int pos) {
        return pos * imageSize + pos * offset;
    }

    private QueueViewContainer getIfExists(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                return queue[i];
            }
        }
        return null;
    }

    public enum INTENT_ICON {
        ATTACK,
        MOVE,
        OTHER,
        SPELL,
        DEBUFF,
        BUFF,
        HOSTILE_SPELL,
        UNKNOWN,

        PREPARE,
        WAIT,
        DEFEND, SEARCH;

        public String getPath() {
            switch (this) {
                case SPELL:
                case BUFF:
                case DEBUFF:
                case HOSTILE_SPELL:
                    return "ui/content/intent icons/" +
                            "eye.txt";
                case ATTACK:
                case MOVE:
                case PREPARE:
                case WAIT:
                case SEARCH:
                    return "ui/content/intent icons/" + name() + ".txt";
            }
            return "ui/content/intent icons/" +
                    "attack.txt";
        }

    }

    private class QueueViewContainer extends Container<QueueView> {
        public int initiative;
        public int precalcInitiative;
        public float queuePriority;
        public float precalcQueuePriority;
        public int id;
        public boolean mobilityState;
        public boolean mainHero;
        public boolean immobilized;

        SpriteX intentIconSprite;
        Map<INTENT_ICON, SpriteAnimation> iconMap = new HashMap<>();
        private INTENT_ICON intentIcon;

        public QueueViewContainer(QueueView actor) {
            super(actor);
            mainHero = actor.isMainHero();
            if (actor == null) {
                return;
            }
            Image shadow = new Image(TextureCache.getOrCreateR(
                    StrPathBuilder.build("ui",
                            "components", "dc", "atb",
                            "initiativepanel unitview shadow.png")));
            shadow.setY(-48);
            actor.addActor(shadow);
            shadow.setZIndex(0);
            actor.addActor(intentIconSprite = new SpriteX());
            intentIconSprite.setX(getWidth() / 2 - 14);
            intentIconSprite.setFps(15);
//            shadow.addListener(new DynamicTooltip(() -> getIntentTooltip(actor.getUserObject())));
        }

        @Override
        public void act(float delta) {
            super.act(delta);
//            IntentIconMaster
            if (isIntentIconsOn())
                if (getActor().getUserObject() instanceof Unit) {
                    intentIcon = ((Unit) getActor().getUserObject()).getAI().getCombatAI().getIntentIcon();
                    SpriteAnimation sprite = iconMap.get(intentIcon);
                    if (intentIcon != null)
                        if (sprite == null) {
                            iconMap.put(intentIcon,
                                    sprite =
                                            SpriteAnimationFactory.getSpriteAnimation(intentIcon.getPath()));
                        }
                    intentIconSprite.setSprite(sprite);
                    intentIconSprite.setY(-24);
                    intentIconSprite.setX(getPrefWidth() / 2);
                    intentIconSprite.setFps(15);
                    intentIconSprite.setZIndex(Integer.MAX_VALUE);
                }

        }

        private boolean isIntentIconsOn() {
            return true;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            ShaderProgram shader = batch.getShader();
            if (immobilized) {
                if (batch instanceof CustomSpriteBatch) {
                    ((CustomSpriteBatch) batch).setFluctuatingShader(DarkShader.getInstance());
                }
            }
//            if (intentIconSprite != null) {
//                intentIconSprite.centerOnParent(getActor());
//                intentIconSprite.setOffsetY(intentIconSprite.getOffsetY()-500);
//                intentIconSprite.setOffsetX(intentIconSprite.getOffsetX());
//                intentIconSprite.draw(batch);
//            }
            super.draw(batch, parentAlpha);
            batch.setShader(shader);
        }
    }
}
