package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.data.XLinkedMap;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.libgdx.StyleHolder;
import main.libgdx.bf.UnitView;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;

import java.util.Arrays;
import java.util.Map;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class InitiativePanel extends Group {
    private final int maxSize = 25;
    private final int visualSize = 10;
    private final int imageSize = 104;
    private final int offset = -12;
    Label timeLabel;
    private ImageContainer[] queue;
    private WidgetGroup queueGroup;
    private boolean cleanUpOn = false;
    private ValueContainer clockImage;

    public InitiativePanel() {
        init();
        registerCallback();
        resetZIndices();
    }

    private void registerCallback() {

        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            if (!isRealTime()) {
                UnitView p = (UnitView) obj.get();
                addOrUpdate(p);
                resetZIndices();

            }
        });

        GuiEventManager.bind(GuiEventType.UPDATE_GUI, obj -> {
            if (!isRealTime()) {
                cleanUp();
                resetZIndices();
            }
        });
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_HOVER_ON, obj -> {
            if (!isRealTime()) {
                UnitView p = (UnitView) obj.get();
                ImageContainer view = getIfExists(p.getCurId());
                if (view != null)
                    view.setZIndex(Integer.MAX_VALUE - 1);
            }
        });
        GuiEventManager.bind(GuiEventType.UNIT_VIEW_HOVER_OFF, obj -> {
            if (!isRealTime()) {
                resetZIndices();
            }

        });
        GuiEventManager.bind(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, obj -> {
            if (!isRealTime()) {
                UnitView p = (UnitView) obj.get();
                removeView(p);
                resetZIndices();
            }
        });
    }

    private void resetZIndices() {
        clockImage.setZIndex(0);
        for (ImageContainer view : queue) {
            if (view != null) {
                view.setZIndex(Integer.MAX_VALUE);
            }
        }
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

    private void cleanUp() {
        if (!isCleanUpOn())
            return;
        Map<Integer, UnitView> views = new XLinkedMap<>();
        DC_Game.game.getTurnManager().getDisplayedUnitQueue().stream().forEach(unit -> {
            UnitView view = (UnitView) DungeonScreen.getInstance().getGridPanel().getUnitMap().get(unit);
            views.put(view.getCurId(), view);
        });
        for (ImageContainer sub : queue) {
            if (sub == null)
                continue;
            if (!views.containsKey(sub.id))
                removeView((sub.id));
        }
    }

    private void init() {


        queue = new ImageContainer[maxSize];
        queueGroup = new WidgetGroup();
        queueGroup.setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        Container<WidgetGroup> container = new Container<>(queueGroup);
        container.setBounds(imageSize + offset, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        container.left().bottom();
        addActor(container);

        final TextureRegion textureRegion = getOrCreateR(StrPathBuilder.build("UI",
         "components", "2017", "panels", "initiativePanel.png"));
        clockImage = new ValueContainer(textureRegion);
        clockImage.setPosition(50, 25);
//        image.align(Align.bottomLeft);
//        image.overrideImageSize(imageSize, imageSize);
//        image.setSize(imageSize, imageSize);
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer("Good time to die!", "")));
        clockImage.addListener(tooltip.getController());
        addActor(clockImage);
        clockImage.setPosition(0, 15);

        setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize, clockImage.getHeight() + 15);

        timeLabel = new Label("Time", StyleHolder.getDefaultLabelStyle());
        addActor(timeLabel);
        timeLabel.setPosition(50, 0);

    }

    private void addOrUpdate(UnitView unitView) {
        updateTime();
        ImageContainer container = getIfExists(unitView.getCurId());
        if (container == null) {
            container = new ImageContainer();
            container.id = unitView.getCurId();
            container.size(imageSize, imageSize);
            container.left().bottom();
            int lastSlot = getLastEmptySlot();
            queue[lastSlot] = container;
            queueGroup.addActor(container);
        }

        container.setActor(unitView);
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
        ImageContainer[] newc = new ImageContainer[queue.length + 3];
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
        if (isRealTime()) {
            updateTime();
            queueGroup.setVisible(false);
        } else {
            queueGroup.setVisible(true);
        }
        super.act(delta);
    }

    private boolean isRealTime() {
        return ExplorationMaster.isExplorationOn();
    }

    private void updateTime() {
        String time = "";
        if (isRealTime()) {
            time = Eidolons.game.getDungeonMaster().getExplorationMaster().getTimeMaster().getDisplayedTime();
        } else {
            time = String.valueOf(Eidolons.game.getRules().getTimeRule().getTimeRemaining());
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
            ImageContainer cur = queue[i];

            if (cur != null) {
                ImageContainer next = queue[ip1];
                if (next == null) {
                    queue[i] = null;
                    queue[ip1] = cur;
                } else {
                    if (cur.initiative > next.initiative) {
                        queue[ip1] = cur;
                        queue[i] = next;
                        for (int y = i; y > 0; y--) {
                            if (queue[y - 1] == null) {
                                break;
                            }
                            if (queue[y].initiative >= queue[y - 1].initiative) {
                                break;
                            }
                            ImageContainer buff = queue[y - 1];
                            queue[y - 1] = queue[y];
                            queue[y] = buff;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < queue.length; i++) {
            ImageContainer cur = queue[i];
            if (cur != null && !cur.mobilityState) {
                for (int y = i; y > 0; y--) {
                    if (queue[y - 1] == null) {
                        break;
                    }
                    if (!queue[y - 1].mobilityState) {
                        break;
                    }
                    ImageContainer buff = queue[y - 1];
                    queue[y - 1] = queue[y];
                    queue[y] = buff;
                }
            }
        }
        applyImageMove();
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
        for (int i = from; i <= to; i++) {
            ImageContainer container = queue[i];
            int pixPos = relToPixPos(rpos);
            if (container != null) {
                if (container.getX() != pixPos) {
                    MoveToAction a = new MoveToAction();
                    a.setX(pixPos);
                    a.setDuration(1.5f);
                    a.setTarget(container);
                    container.addAction(a);
                }
                rpos++;
            }
        }
    }

    private int relToPixPos(int pos) {
        return pos * imageSize + pos * offset;
    }

    private ImageContainer getIfExists(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null && queue[i].id == id) {
                return queue[i];
            }
        }
        return null;
    }

    public boolean isCleanUpOn() {
        return cleanUpOn;
    }

    public void setCleanUpOn(boolean cleanUpOn) {
        this.cleanUpOn = cleanUpOn;
    }


    private class ImageContainer extends Container<UnitView> {
        public int initiative;
        public int id;
        public boolean mobilityState;

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }
    }
}
