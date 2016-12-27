package main.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.elements.Filter;
import main.elements.targeting.SelectiveTargeting;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.Game;
import main.system.EventCallbackParam;
import main.system.TempEventManager;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadialMenu extends Group {
    private TextureCache textureCache;
    private MenuNode curentNode;
    private Image closeImage;

    public RadialMenu(Texture closeTex, TextureCache textureCache) {
        this.textureCache = textureCache;
        closeImage = new Image(closeTex);

/*        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor a = event.getTarget();
                if (a != null && a instanceof RadialMenu.MenuNode) {
                    RadialMenu.MenuNode node = (RadialMenu.MenuNode) a;
                    node.action.run();
                    return true;
                }

*//*
                if (radialMenu != null) {
                    Vector2 v = new Vector2(x, y);
                    v = parentToLocalCoordinates(v);
//                    a = radialMenu.hit(v.x - radialMenu.getX(), v.y - radialMenu.getY(), true);
                    a = radialMenu.hit(x, y, true);
                    if (a != null && a instanceof RadialMenu.MenuNode) {
                        RadialMenu.MenuNode node = (RadialMenu.MenuNode) a;
                        node.action.run();
                        return true;
                    }
                }
*//*
                return false;
            }
        });*/
    }

    private void init(List<CreatorNode> nodes) {
        curentNode = new MenuNode(closeImage, "Close");
        curentNode.childs = createChilds(curentNode, nodes);
        setBounds(
                Gdx.input.getX() - getWidth() / 2,
                Gdx.graphics.getHeight() - Gdx.input.getY() - getHeight() / 2,
                curentNode.getWidth(),
                curentNode.getHeight()
        );

        final RadialMenu menu = this;
        curentNode.action = () -> menu.setVisible(false);

        updateCallbacks();
        curentNode.drawChilds = true;
        setVisible(true);
        //addActor(curentNode);
    }

    @Override
    protected void positionChanged() {
        curentNode.setX(getX());
        curentNode.setY(getY());
        updatePosition();
    }

    private void setCurentNode(MenuNode node) {
        removeActor(curentNode);
        curentNode.drawChilds = false;
        curentNode = node;
        updatePosition();
        updateCallbacks();
        curentNode.drawChilds = true;
        // addActor(curentNode);
    }

    private void updatePosition() {

        int step = 360 / curentNode.childs.size();
        int pos;
        int r = (int) (curentNode.getWidth() * 1.5);

        for (int i = 0; i < curentNode.childs.size(); i++) {
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos)));
            int x = (int) (r * Math.cos(Math.toRadians(pos)));
            curentNode.childs.get(i).setX(x + curentNode.getX());
            curentNode.childs.get(i).setY(y + curentNode.getY());
        }
    }

    private void updateCallbacks() {
        if (curentNode.parent == null) {
            curentNode.action = () -> setVisible(false);
        } else {
            curentNode.action = () -> setCurentNode(curentNode.parent);
        }
        for (final MenuNode child : curentNode.childs) {
            if (child.childs.size() > 0) {
                child.action = () -> setCurentNode(child);
            }
        }
    }

    private List<MenuNode> createChilds(final MenuNode parent, final List<CreatorNode> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (final CreatorNode node : creatorNodes) {
            final MenuNode menuNode = new MenuNode(new Image(node.texture), node.name);
            menuNode.parent = parent;
            if (node.action != null) {
                final Runnable r = node.action;
                final RadialMenu menu = this;
                menuNode.action = () -> {
                    r.run();
                    menu.setVisible(false);
                };
            } else {
                menuNode.setChilds(createChilds(menuNode, node.childNodes));
            }
            menuNodes.add(menuNode);
            //parent.addActor(menuNode);
        }
        return menuNodes;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!isVisible() || curentNode == null) return null;
        if (!Gdx.input.isTouched()) return null;
        int xx = Gdx.input.getX();
        int yy = Gdx.graphics.getHeight() - Gdx.input.getY();
        return curentNode.hit(xx, yy, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible() || curentNode == null) return;
        curentNode.draw(batch, parentAlpha);
    }


    public class MenuNode extends Group {
        public MenuNode parent = null;
        private List<MenuNode> childs = new ArrayList<>();
        public Runnable action = null;
        private boolean drawChilds = false;
        private Image image;
        private Label text = null;

        public MenuNode(Image image, String text) {
//            addActor(image);
            if (text != null && text.length() > 0) {
                this.text = new Label(text, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            }
            this.image = image;
            setHeight(image.getHeight());
            setWidth(image.getWidth());

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    action.run();
                    return true;
                }
            });
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            image.setX(x);
            if (text != null) {
                text.setX(x);
            }
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            image.setY(y);
            if (text != null) {
                text.setY(y);
            }
        }

        public void setChilds(List<MenuNode> childs) {
            this.childs = childs;
            for (MenuNode child : childs) {
                addActor(child);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            image.draw(batch, parentAlpha);
            if (text != null) {
                text.draw(batch, parentAlpha);
            }
            if (drawChilds) {
                for (MenuNode child : childs) {
                    child.draw(batch, parentAlpha);
                }
            }
        }

        @Override
        public Actor hit(float x, float y, boolean touchable) {
            Actor a = image.hit(x - image.getX(), y - image.getY(), touchable);
            if (a != null) a = this;
            if (a == null && drawChilds) {
                for (MenuNode child : childs) {
                    a = child.hit(x, y, touchable);
                    if (a != null) {
                        a = child;
                        break;
                    }
                }
            }
            return a;
        }
    }

    public static class CreatorNode {
        public Texture texture;
        public String name;
        public List<CreatorNode> childNodes;
        public Runnable action;
    }


    public void createNew(float x, float y, DC_Obj target) {
        MicroObj activeObj = (MicroObj) Game.game.getManager().getActiveObj();
        List<ActiveObj> activeObjs = activeObj.getActives();

        List<Triple<Runnable, Texture, String>> moves = new ArrayList<>();
        List<Triple<Runnable, Texture, String>> turns = new ArrayList<>();
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();

        for (ActiveObj obj : activeObjs) {
            if (obj.isMove()) {
                if (obj.getTargeting() instanceof SelectiveTargeting) {
                    moves.add(new ImmutableTriple<>(() -> {
                        Ref ref = obj.getRef();
                        ref.setTarget(target.getId());
                        obj.activate(ref);
                    }, textureCache.getOrCreate(((Entity) obj).getImagePath()), obj.getName()));
                } else {
                    moves.add(new ImmutableTriple<>(
                            ((Entity) obj)::invokeClicked,
                            textureCache.getOrCreate(((Entity) obj).getImagePath()),
                            obj.getName()
                    ));
                }

                //add this filter later
                //obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            }
            if (obj.isTurn()) {
                turns.add(new ImmutableTriple<>(
                        ((Entity) obj)::invokeClicked,
                        textureCache.getOrCreate(((Entity) obj).getImagePath()),
                        obj.getName()
                ));
            }

            if (obj.isAttack()) {
                DC_ActiveObj dcActiveObj = (DC_ActiveObj) obj;
                if (obj.getRef().getSourceObj() == target) {
                    Ref ref1 = dcActiveObj.getRef();
                    ref1.setMatch(target.getId());
                    Filter<Obj> filter = dcActiveObj.getTargeting().getFilter();
                    filter.setRef(ref1);
                    TempEventManager.trigger("select-multi-objects", new EventCallbackParam(filter.getObjects()));
                }

                if (obj.getTargeting() instanceof SelectiveTargeting) {
                    RadialMenu.CreatorNode inn1 = new CreatorNode();
                    inn1.texture = textureCache.getOrCreate(((Entity) obj).getImagePath());
                    inn1.name = obj.getName();
                    inn1.action = null;
                    List<RadialMenu.CreatorNode> list = new ArrayList<>();
                    for (DC_ActiveObj dc_activeObj : dcActiveObj.getSubActions()) {
                        RadialMenu.CreatorNode innn = new CreatorNode();
                        innn.name = dc_activeObj.getName();
                        innn.texture = textureCache.getOrCreate(dc_activeObj.getImagePath());
                        innn.action = () -> {
                            Ref ref = dc_activeObj.getRef();
                            ref.setTarget(target.getId());
                            dc_activeObj.activate(ref);
                        };
                        list.add(innn);
                    }
                    inn1.childNodes = list;
                    nn1.add(inn1);
                }
            }

        }


        Texture moveAction = textureCache.getOrCreate("\\UI\\actions\\Move gold.jpg");
        Texture turnAction = textureCache.getOrCreate("\\UI\\actions\\turn anticlockwise quick2 - Copy.jpg");
        Texture attackAction = textureCache.getOrCreate("\\mini\\actions\\New folder\\Achievement_Arena_2v2_2.jpg");
        Texture yellow = new Texture(GridPanel.class.getResource("/data/marble_yellow.png").getPath());
        Texture red = new Texture(GridPanel.class.getResource("/data/marble_red.png").getPath());
        Texture green = new Texture(GridPanel.class.getResource("/data/marble_green.png").getPath());

        RadialMenu.CreatorNode n1 = new RadialMenu.CreatorNode();
        n1.texture = moveAction;
        n1.childNodes = creatorNodes(moves);

        RadialMenu.CreatorNode n2 = new RadialMenu.CreatorNode();
        n2.texture = turnAction;
        n2.childNodes = creatorNodes(turns);

        RadialMenu.CreatorNode n3 = new RadialMenu.CreatorNode();
        n3.texture = attackAction;
        n3.childNodes = nn1;

        RadialMenu.CreatorNode n4 = new RadialMenu.CreatorNode();
        n4.texture = yellow;
        n4.action = () -> {
//                activeObj.invokeClicked();
        };
        n4.childNodes = creatorNodes("nn4:", red);

        init(Arrays.asList(n1, n2, n3, n4));
    }

    private static List<RadialMenu.CreatorNode> creatorNodes(List<Triple<Runnable, Texture, String>> pairs) {
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (final Triple<Runnable, Texture, String> pair : pairs) {
            RadialMenu.CreatorNode inn1 = new RadialMenu.CreatorNode();
            inn1.texture = pair.getMiddle();
            inn1.action = pair.getLeft();
            inn1.name = pair.getRight();
            nn1.add(inn1);
        }
        return nn1;
    }

    private static List<RadialMenu.CreatorNode> creatorNodes(final String name, Texture t) {
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            RadialMenu.CreatorNode inn1 = new RadialMenu.CreatorNode();
            inn1.texture = t;
            final int finalI = i;
            inn1.action = () -> System.out.println(name + finalI);
            nn1.add(inn1);
        }
        return nn1;
    }
}
