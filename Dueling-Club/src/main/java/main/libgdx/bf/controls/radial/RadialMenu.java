package main.libgdx.bf.controls.radial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
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
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_QuickItemObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.libgdx.bf.GridPanel;
import main.libgdx.bf.TargetRunnable;
import main.libgdx.texture.TextureCache;
import main.system.EventCallbackParam;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GreyscaleUtils;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static main.system.GuiEventManager.trigger;
import static main.system.GuiEventType.SELECT_MULTI_OBJECTS;

public class RadialMenu extends Group {
    private Texture closeTex;
    private MenuNode currentNode;
    private Image closeImage;

    public RadialMenu() {
        final Texture t = new Texture(RadialMenu.class.getResource("/data/marble_green.png").getPath());
        closeImage = new Image(t);
        this.closeTex = t;
    }

    private static List<RadialMenu.CreatorNode> createNodes(List<Triple<Runnable, Texture, String>> pairs) {
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

    private static List<RadialMenu.CreatorNode> createNodes(final String name, Texture t) {
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

    public void init(List<CreatorNode> nodes) {
        currentNode = new MenuNode(closeImage, "Close");
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setBounds(
                v2.x - currentNode.getWidth() / 2,
                v2.y - currentNode.getHeight() / 2,
                currentNode.getWidth(),
                currentNode.getHeight()
        );

        currentNode.setX(getX());
        currentNode.setY(getY());

        currentNode.childNodes = createChildren(currentNode, nodes);

        final RadialMenu menu = this;
        currentNode.action = () -> menu.setVisible(false);

        updateCallbacks();
        updatePosition();
        currentNode.drawChildren = true;
        setVisible(true);
        //addActor(currentNode);
    }

    private void setCurrentNode(MenuNode node) {
        removeActor(currentNode);
        currentNode.drawChildren = false;
        currentNode = node;
        updatePosition();
        updateCallbacks();
        currentNode.drawChildren = true;
        // addActor(currentNode);
    }

    private void updatePosition() {

        int step = 360 / currentNode.childNodes.size();
        int pos;
        int r = (int) (currentNode.getWidth() * 1.5);

        for (int i = 0; i < currentNode.childNodes.size(); i++) {
            pos = i * step;
            int y = (int) (r * Math.sin(Math.toRadians(pos)));
            int x = (int) (r * Math.cos(Math.toRadians(pos)));
            currentNode.childNodes.get(i).setX(x + currentNode.getX());
            currentNode.childNodes.get(i).setY(y + currentNode.getY());
        }
    }

    private void updateCallbacks() {
        if (currentNode.parent == null) {
            currentNode.action = () -> setVisible(false);
        } else {
            currentNode.action = () -> setCurrentNode(currentNode.parent);
        }
        for (final MenuNode child : currentNode.childNodes) {
            if (child.childNodes.size() > 0) {
                child.action = () -> setCurrentNode(child);
            }
        }
    }

    private List<MenuNode> createChildren(final MenuNode parent, final List<CreatorNode> creatorNodes) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (final CreatorNode node : creatorNodes) {
            final MenuNode menuNode = new MenuNode(
                    node.texture == null ?
                            new Image(closeTex)
                            : new Image(node.texture), node.name,
                    node.w, node.h
            );
            menuNode.parent = parent;
            if (node.action != null) {
                final Runnable r = node.action;
                final RadialMenu menu = this;
                menuNode.action = () -> {
                    r.run();
                    menu.setVisible(false);
                };
            } else {
                menuNode.setChildren(createChildren(menuNode, node.childNodes));
            }
            menuNodes.add(menuNode);
            //parent.addActor(menuNode);
        }
        return menuNodes;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (!isVisible() || currentNode == null) {
            return null;
        }
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        return currentNode.hit(v2.x, v2.y, touchable);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isVisible() || currentNode == null) {
            return;
        }
        currentNode.draw(batch, parentAlpha);

    }

    private Texture getTextureForActive(ActiveObj obj, Ref ref) {
        Texture t = TextureCache.getOrCreate(((Entity) obj).getImagePath());
        if (obj.canBeActivated(ref)) {

        }

        if (!t.getTextureData().isPrepared()) {
            t.getTextureData().prepare();
        }
        Pixmap pixmap2 = new Pixmap(t.getWidth(), t.getHeight(), t.getTextureData().getFormat());
        Pixmap pixmap = t.getTextureData().consumePixmap();

        for (int i = 0; i < t.getWidth(); i++) {
            for (int j = 0; j < t.getHeight(); j++) {
                int c = pixmap.getPixel(i, j);
                pixmap2.drawPixel(i, j, GreyscaleUtils.lightness(c));
            }
        }

        Texture result = new Texture(pixmap2);
        return result;
    }

    public void createNew(DC_Obj target) {
        Unit source = (Unit) Game.game.getManager().getActiveObj();
        if (source == null) {
            return; //fix logic to avoid null value between turns!
        }
        List<ActiveObj> activeObjs = source.getActives();

        List<Triple<Runnable, Texture, String>> moves = new ArrayList<>();
        List<Triple<Runnable, Texture, String>> turns = new ArrayList<>();
        List<RadialMenu.CreatorNode> nn1 = new ArrayList<>();

        Set<ActiveObj> actives = new HashSet<>();

        for (ActiveObj obj : activeObjs) {
            if (actives.contains(obj)) {
                continue;
            }
            if (obj.getTargeting() == null) {
                continue;
            }
            if (!(obj instanceof DC_ActiveObj)) {
                continue;
            }
            DC_ActiveObj active = ((DC_ActiveObj) obj);
            actives.add(active);
            ImmutableTriple<Runnable, Texture, String> triple = new ImmutableTriple<>(
                    ((Entity) obj)::invokeClicked,
                    getTextureForActive(obj,
                            obj.getOwnerObj().getRef().getTargetingRef(target)),
                    obj.getName()
            );
            if (obj.isMove()) {
                if (obj.getTargeting() instanceof SelectiveTargeting) {
                    moves.add(new ImmutableTriple<>(() -> {
                        active.activateOn(target);
                    },
                            TextureCache.getOrCreate(((Entity) obj).getImagePath()),
                            obj.getName()));
                } else {
                    moves.add(triple);
                }

                //add this filter later
                //obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            }
            if (obj.isTurn()) {
                turns.add(triple);
            }

            if (obj.isAttack()) {
                RadialMenu.CreatorNode inn1 = new CreatorNode();
                try {
                    inn1.texture = TextureCache.getOrCreate(((Entity) obj).getImagePath());
                } catch (Exception e) {
                    e.printStackTrace();

                }

                inn1.name = obj.getName();
                inn1.action = null;
                List<RadialMenu.CreatorNode> list = new ArrayList<>();
                if (obj.getRef().getSourceObj() == target) {
                    for (DC_ActiveObj active1 : active.getSubActions()) {
                        Ref ref1 = active.getRef();
                        ref1.setMatch(target.getId());
                        Filter<Obj> filter = active.getTargeting().getFilter();
                        filter.setRef(ref1);
                        Set<Obj> objects = new HashSet<>();
                        if (CoreEngine.isActionTargetingFiltersOff()) {
                            for (Unit o : DC_Game.game.getUnits()) {
                                objects.add(o);
                            }
                        } else {
                            objects = filter.getObjects();
                        }

                        if (objects.size() > 0) {
                            Pair<Set<Obj>, TargetRunnable> p = new ImmutablePair<>(objects, (t) -> {
                                active1.activateOn(t);

                            });
                            RadialMenu.CreatorNode innn = new CreatorNode();
                            innn.name = active1.getName();
                            innn.texture = TextureCache.getOrCreate(active1.getImagePath());
                            innn.action = () -> {
                                trigger(SELECT_MULTI_OBJECTS, new EventCallbackParam(p));
                            };
                            list.add(innn);
                        } else {
                            int debug = 10;
                            //STD_SOUNDS.CLICK_ERROR.getPath()
                        }
                    }


                    inn1.childNodes = list;
                    nn1.add(inn1);
                } else if (obj.getTargeting() instanceof SelectiveTargeting) {
                    for (DC_ActiveObj dc_activeObj : active.getSubActions()) {
                        RadialMenu.CreatorNode innn = new CreatorNode();
                        innn.name = dc_activeObj.getName();
                        innn.texture = TextureCache.getOrCreate(dc_activeObj.getImagePath());
                        innn.action = () -> {
                            dc_activeObj.activateOn(target);
                        };
                        list.add(innn);
                    }
                    inn1.childNodes = list;
                    nn1.add(inn1);
                }
                if (active.getActiveWeapon() != null) {
                    if (active.getActiveWeapon().isRanged()) {
                        if (active.getRef().getObj(KEYS.AMMO) == null) {
                            for (DC_QuickItemObj ammo : active.getOwnerObj().getQuickItems()) {
                                CreatorNode innn = new CreatorNode();
                                innn.name = "Reload with " + ammo.getName();
                                innn.texture = TextureCache.getOrCreate(ammo.getImagePath());
                                innn.action = () -> {
                                    ammo.invokeClicked();
                                };
                                list.add(innn);
                            }
                        }
                    }
                }
            }

        }


        Texture moveAction = TextureCache.getOrCreate("\\UI\\actions\\Move gold.jpg");
        Texture turnAction = TextureCache.getOrCreate("\\UI\\actions\\turn anticlockwise quick2 - Copy.jpg");
        Texture attackAction = TextureCache.getOrCreate("\\mini\\actions\\New folder\\Achievement_Arena_2v2_2.jpg");
        Texture yellow = new Texture(GridPanel.class.getResource("/data/marble_yellow.png").getPath());
        Texture red = new Texture(GridPanel.class.getResource("/data/marble_red.png").getPath());
        Texture green = new Texture(GridPanel.class.getResource("/data/marble_green.png").getPath());


        List<RadialMenu.CreatorNode> list = new LinkedList<>();


        RadialMenu.CreatorNode attM = new RadialMenu.CreatorNode();
        attM.texture = nn1.get(0).texture;
        attM.childNodes = nn1.get(0).childNodes;
        attM.name = nn1.get(0).name;
        list.add(attM);

        RadialMenu.CreatorNode movesN1 = new RadialMenu.CreatorNode();
        movesN1.texture = moveAction;
        movesN1.childNodes = createNodes(moves);
        list.add(movesN1);

        if (nn1.size() > 1) {
            RadialMenu.CreatorNode attO = new RadialMenu.CreatorNode();
            attO.texture = nn1.get(1).texture;
            attO.childNodes = nn1.get(1).childNodes;
            attO.name = nn1.get(1).name;
            list.add(attO);

        }
        if (getDebug() || !source.getSpells().isEmpty()) {
            RadialMenu.CreatorNode spellNode = new RadialMenu.CreatorNode();
            spellNode.texture = TextureCache.getOrCreate(ImageManager.getRadialSpellIconPath());
            spellNode.childNodes = SpellRadialManager.getSpellNodes(source, target);
            spellNode.name = "Spells";
            if (spellNode.childNodes.size() == 0) {
                spellNode.action = () -> {
                };//spell manager may return empty spell list, close menu
                System.out.println("Error: SpellRadialManager.getSpellNodes() return empty list");
            }
            list.add(spellNode);
        }
        RadialMenu.CreatorNode turnsN1 = new RadialMenu.CreatorNode();
        turnsN1.texture = turnAction;
        turnsN1.childNodes = createNodes(turns);
        list.add(turnsN1);

/*        RadialMenu.CreatorNode n3 = new RadialMenu.CreatorNode();
        n3.texture = attackAction;
        n3.childNodes = nn1;*/

        RadialMenu.CreatorNode n4 = new RadialMenu.CreatorNode();
        n4.texture = yellow;
        n4.action = () -> {
//                activeObj.invokeClicked();
        };
        n4.childNodes = createNodes("nn4:", red);
        init(list);
    }


    public static class CreatorNode {
        public Texture texture;
        public String name;
        public List<CreatorNode> childNodes;
        public Runnable action;
        public float w;
        public float h;
    }

    public class MenuNode extends Group {
        public MenuNode parent = null;
        public Runnable action = null;
        private List<MenuNode> childNodes = new ArrayList<>();
        private boolean drawChildren = false;
        private Image image;
        private Label text = null;

        public MenuNode(Image image, String text) {
            this(image, text, image.getWidth(), image.getHeight());
        }

        public MenuNode(Image image, String text, float w, float h) {
//            addActor(image);
            if (text != null && text.length() > 0) {
                this.text = new Label(text, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            }
            this.image = image;
            if (w != 0) {
                image.setWidth(w);
            }
            if (h != 0) {
                image.setHeight(h);
            }
            setHeight(image.getHeight());
            setWidth(image.getWidth());

            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (action == null) {
                        LogMaster.log(1, "action == null");
                    } else {

                        action.run();
                    }
                    event.stop();
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

        public void setChildren(List<MenuNode> childs) {
            this.childNodes = childs;
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
            if (drawChildren) {
                List<MenuNode> renderList = new LinkedList<>(childNodes);
                Collections.reverse(renderList); //to sync with click listener; TODO rework!
                for (MenuNode child : renderList) {
                    child.draw(batch, parentAlpha);
                }
            }
        }

        @Override
        public Actor hit(float x, float y, boolean touchable) {
            Actor a = image.hit(x - image.getX(), y - image.getY(), touchable);
            if (a != null) {
                a = this;
            }
            if (a == null && drawChildren) {
                for (MenuNode child : childNodes) {
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
}
