package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.libgdx.bf.UnitView;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class InitiativePanel extends Group {
    private final int maxSize = 25;
    private final int visualSize = 8;
    private final int imageSize = 100;
    private final int offset = 2;
    private ImageContainer[] queue;
    private WidgetGroup queueGroup;


    public InitiativePanel() {
        init();
        registerCallback();
    }

    private void registerCallback() {

        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            UnitView p = (UnitView) obj.get();
            addOrUpdate(p);
        });

        GuiEventManager.bind(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, obj -> {
            UnitView p = (UnitView) obj.get();
            for (int i = 0; i < queue.length; i++) {
                if (queue[i] != null && queue[i].id == p.getCurId()) {
                    queueGroup.removeActor(queue[i]);
                    queue[i] = null;
                    sort();
                    break;
                }
            }
        });
    }

    private void init() {
        queue = new ImageContainer[maxSize];
        queueGroup = new WidgetGroup();
        queueGroup.setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        //queueGroup.debug();
        Container<WidgetGroup> container = new Container<>(queueGroup);
        container.setBounds(imageSize, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        container.size(imageSize * visualSize + (offset - 1) * visualSize, imageSize);
        container.left().bottom();
        //container.setClip(true);
        //container.debug();
        addActor(container);
        setBounds(0, 0, imageSize * visualSize + (offset - 1) * visualSize, imageSize);
    }

    private void addOrUpdate(UnitView unitView) {
        ImageContainer container = getIfExists(unitView.getCurId());
        if (container == null) {
            container = new ImageContainer();
            container.initiative = unitView.getInitiativeIntVal();
            container.id = unitView.getCurId();
            container.size(imageSize, imageSize);
            container.left().bottom();
            int lastSlot = getLastEmptySlot();
            container.setActor(unitView);
            queue[lastSlot] = container;
            queueGroup.addActor(container);
        } else {
            container.initiative = unitView.getInitiativeIntVal();
            container.setActor(unitView);
        }
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

/*    @Override
    public void act(float delta) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null) queue[i].image.act(delta);
        }
    }*/

/*    @Override
    public void draw(Batch batch, float parentAlpha) {
        int startPos = queue.length - visualSize - 1;
        for (int i = startPos; i < queue.length; i++) {
            if (queue[i] != null) {
                Image im = queue[i].image;
                Vector2 v2 = new Vector2(im.getX(), im.getY());
                Vector2 vv2 = new Vector2(v2);
                v2 = localToParentCoordinates(v2);
                im.setPosition(v2.x, v2.y);
                im.draw(batch, parentAlpha);
                im.setPosition(vv2.x, vv2.y);
            }
        }
    }*/

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == this) {
            actor = null;
        }
        return actor;
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


    private class ImageContainer extends Container<UnitView> {
        public int initiative;
        public int id;
    }
}
