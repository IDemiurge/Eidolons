package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class InitiativePanel extends Group {
    private ImageContainer[] queue;
    private int maxSize = 25;
    private int visualSize = 8;
    private int imageSize = 50;
    private int offset = 2;


    public InitiativePanel() {
        init();
        registerCallback();
    }

    private void registerCallback() {
        GuiEventManager.bind(GuiEventType.ADD_OR_UPDATE_INITIATIVE, obj -> {
            InitiativePanelParam p = (InitiativePanelParam) obj.get();
            addOrUpdate(p);
        });
        GuiEventManager.bind(GuiEventType.REMOVE_FROM_INITIATIVE_PANEL, obj -> {
            InitiativePanelParam p = (InitiativePanelParam) obj.get();
            for (int i = 0; i < queue.length; i++) {
                if (queue[i] != null && queue[i].id == p.getId()) {
                    queue[i].image.clear();
                    queue[i] = null;
                    sort();
                    break;
                }
            }
        });
    }

    private void init() {
        queue = new ImageContainer[maxSize];
    }

    private void addOrUpdate(InitiativePanelParam panelParam) {
        ImageContainer container = getIfExists(panelParam.getId());
        if (container == null) {
            container = new ImageContainer();
            container.pos = 0;
            container.initiative = panelParam.getVal();
            container.id = panelParam.getId();
            int lastSlot = getLastEmptySlot();
            container.image = newImage(lastSlot, panelParam);
            queue[lastSlot] = container;
        } else {
            container.initiative = panelParam.getVal();
            container.image = newImage(container.image, panelParam);
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
            if (queue[i] == null) return i;
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
    public void act(float delta) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null) queue[i].image.act(delta);
        }
    }

    @Override
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

    }

    private void sort() {
        for (int i = 0; i < queue.length - 1; i++) {
            final int ip1 = i + 1;
            ImageContainer cur = queue[i];

            if (cur != null) {
                ImageContainer next = queue[ip1];
                if (next == null) {
                    cur.pos++;
                    queue[i] = null;
                    queue[ip1] = cur;
                } else {
                    if (cur.initiative > next.initiative) {
                        cur.pos++;
                        queue[ip1] = cur;
                        next.pos--;
                        queue[i] = next;
                        for (int y = i; y > 0; y--) {
                            if (queue[y] == null) break;
                            if (queue[y].initiative >= queue[y - 1].initiative) break;
                            ImageContainer buff = queue[y - 1];
                            queue[y - 1] = queue[y];
                            queue[y].pos--;
                            queue[y] = buff;
                            buff.pos++;
                        }
                    }
                }
            }
        }
        applyImageMove();
    }

    private void applyImageMove() {
        for (int i = queue.length - 1; i >= 0; i--) {
            ImageContainer container = queue[i];
            if (container != null && container.pos != 0) {
                MoveByAction a = new MoveByAction();
                a.setAmountX(relToPixPos(container.pos));
                container.image.addAction(a);
            }
        }
    }

    private int relToPixPos(int pos) {
        return pos * imageSize + (pos - 1) * offset;
    }

    private ImageContainer getIfExists(int id) {
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] == null && queue[i].id == id) return queue[i];
        }
        return null;
    }


    private class ImageContainer {
        public Image image;
        public int initiative;
        public int id;
        public int pos = 0;
        public boolean isAnimated;
    }
}
