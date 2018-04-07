package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.LastSeenMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.anims.AnimMaster;
import eidolons.libgdx.anims.AnimationConstructor;
import eidolons.libgdx.anims.actions.FadeOutAction;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.MoveAnimation;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.*;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.bf.overlays.OverlaysManager;
import eidolons.libgdx.bf.overlays.WallMap;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.HelpMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

import static main.system.GuiEventType.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 26.10.2016
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public class GridPanel extends Group {
    private static final String emptyCellPath = StrPathBuilder.build(
     "UI", "cells", "Empty Cell v3.png");
    private static final String emptyCellPathFloor = StrPathBuilder.build(
     "UI", "cells", "Floor.png");
    private static final String gridCornerElementPath = StrPathBuilder.build(
     "UI", "bf", "gridCorner.png");

    protected TextureRegion emptyImage;
    protected GridCellContainer[][] cells;
    private Map<BattleFieldObject, BaseView> viewMap;
    private int cols;
    private int rows;
    private AnimMaster animMaster;
    private ShadowMap shadowMap;
    private Label fpsLabel;
    private boolean fpsDebug = false;
    private TextureRegion cornerRegion;
    private WallMap wallMap;
    private List<OverlayView> overlays = new ArrayList<>();
    private GridUnitView hoverObj;
    private boolean resetVisibleRequired;
    private boolean updateRequired;
    private boolean firstUpdateDone;
    private boolean welcomeInfoShown;
    private OverlaysManager overlayManager;
    private GridUnitView mainHeroView;
    private float resetTimer;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public static boolean isHpBarsOnTop() {
        return true;
    }

    public void updateOutlines() {

        viewMap.keySet().forEach(obj -> {
            if (!obj.isOverlaying())
                if (!obj.isMine())
                    if (!obj.isWall()) {
                        OUTLINE_TYPE outline = obj.getOutlineType();
                        GridUnitView uv = (GridUnitView) viewMap.get(obj);

                        TextureRegion texture = null;
                        if (outline != null) {
                            String path = Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj);
                            if (obj instanceof Unit) {
                                main.system.auxiliary.log.LogMaster.log(1, obj + " has OUTLINE: " + path);
                            }
                            texture = TextureCache.getOrCreateR(path);
                            uv.setOutline(texture);
                        } else {
                            if (obj instanceof Unit) {
                                if (!obj.isOutsideCombat()) {
                                    main.system.auxiliary.log.LogMaster.log(1, obj + " has NO OUTLINE: ");
                                }
                            }
                            uv.setOutline(null);
                        }
                    }
        });
    }

    private void checkAddBorder(int x, int y) {
        Boolean hor = null;
        Boolean vert = null;
        if (x + 1 == cols)
            hor = true;
        if (x == 0)
            hor = false;
        if (y + 1 == rows)
            vert = true;
        if (y == 0)
            vert = false;

        float posX = x * GridMaster.CELL_W;
        float posY = y * GridMaster.CELL_H;
        String suffix = null;
        if (hor != null) {
            int i = hor ? 1 : -1;
            suffix = hor ? "right" : "left";
            Image image = new Image(TextureCache.getOrCreateR(
             StrPathBuilder.build(
              "UI", "bf", "gridBorder " +
               suffix +
               ".png")));
            addActor(image);
            image.setPosition(posX + i * GridMaster.CELL_W + (20 - 20 * i)//+40
             , posY
             //+ i * 35
            );
        }
        if (vert != null) {
            int i = vert ? 1 : -1;
            suffix = vert ? "up" : "down";
            Image image = new Image(TextureCache.getOrCreateR(StrPathBuilder.build(
             "UI", "bf", "gridBorder " +
              suffix +
              ".png")));
            addActor(image);
            image.setPosition(posX //+ i * 35
             , posY
              + i * GridMaster.CELL_H + (20 - 20 * i));//+40
        }

        if (hor != null)
            if (vert != null) {
                int i = vert ? 1 : -1;
                Image image = new Image(cornerRegion);
                image.setPosition(posX + i * 40 + i * GridMaster.CELL_W + i * -77, posY
                 + i * 40 + i * GridMaster.CELL_H + i * -77);

                if (!vert && hor) {
                    image.setX(image.getX() + 170);
                    image.setY(image.getY() + 12);
                }
                if (vert && !hor) {
                    image.setX(image.getX() - 180);
                    image.setY(image.getY() - 25);
                }
                if (vert && hor) {
                    image.setX(image.getX() - 15);
                    image.setY(image.getY() - 15);
                }
                addActor(image);
            }
    }

    public GridPanel init(DequeImpl<BattleFieldObject> units) {
        this.viewMap = new HashMap<>();
        emptyImage = TextureCache.getOrCreateR(getCellImagePath());
        cornerRegion = TextureCache.getOrCreateR(gridCornerElementPath);
        cells = new GridCellContainer[cols][rows];


        int rows1 = rows - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCellContainer(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridMaster.CELL_W);
                cells[x][y].setY(y * GridMaster.CELL_H);
                addActor(cells[x][y].init());
                checkAddBorder(x, y);

            }
        }
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON))
            TextureManager.addCellsToCache(cols, rows);

        addActor(new CellBorderManager());

        bindEvents();

        createUnitsViews(units);

        setHeight(cells[0][0].getHeight() * rows);
        setWidth(cells[0][0].getWidth() * cols);

        addListener(new BattleClickListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                GridPanel.this.getStage().setScrollFocus(GridPanel.this);
                return false;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
//                return PhaseAnimator.getInstance().checkAnimClicked(x, y, pointer, button);
                return false;
            }
        });
        addActor(overlayManager = new OverlaysManager(this));
        addActor(animMaster = AnimMaster.getInstance());
        animMaster.bindEvents();

        if (AnimationConstructor.isPreconstructAllOnGameInit())
            units.forEach(unit ->
            {
                if (unit instanceof Unit)
                    animMaster.getConstructor().preconstructAll((Unit) unit);
            });

        if (fpsDebug) {
            fpsLabel = new Label("0", StyleHolder.getDefaultLabelStyle());
            addActor(fpsLabel);
            fpsLabel.setAlignment(Align.topLeft);
        }

        return this;
    }

    private String getCellImagePath() {
        if (Eidolons.getGame().getDungeon() != null) {
            if (Eidolons.getGame().getDungeon().isSurface())
                return emptyCellPath;
        }
        return emptyCellPathFloor;
    }

    private UnitView getUnitView(BattleFieldObject battleFieldObject) {
        return (UnitView) viewMap.get(battleFieldObject);
    }

    private void bindEvents() {

        GuiEventManager.bind(GuiEventType.ANIMATION_QUEUE_FINISHED, (p) -> {
            resetVisible();
        });
        GuiEventManager.bind(GuiEventType.UPDATE_LAST_SEEN_VIEWS, p -> {
            List<BattleFieldObject> list = (List<BattleFieldObject>) p.get();

            for (BattleFieldObject sub : viewMap.keySet()) {
                GridUnitView view = (GridUnitView) viewMap.get(sub);
                if (list.contains(sub)) {
                    setVisible(view.getLastSeenView(), true);
                } else {
                    setVisible(view.getLastSeenView(), false);
                }
            }

        });
        GuiEventManager.bind(UNIT_GREYED_OUT_ON, obj -> {
            BattleFieldObject bfObj = (BattleFieldObject) obj.get();
            if (bfObj.isOverlaying())
                return;
            UnitView unitView = getUnitView(bfObj);
            unitView.setFlickering(true);
            unitView.setGreyedOut(true);
//            unitView.setVisible(true);
        });
        GuiEventManager.bind(UNIT_GREYED_OUT_OFF, obj -> {
            BattleFieldObject bfObj = (BattleFieldObject) obj.get();
            if (bfObj.isOverlaying())
                return;
            UnitView unitView = getUnitView(bfObj);
            unitView.setGreyedOut(false);
            unitView.setFlickering(false);
//            ActorMaster.getActionsOfClass(unitView, AlphaAction.class);
            unitView.getActions().clear();
//            unitView.setVisible(true);
        });

        GuiEventManager.bind(UNIT_STARTS_MOVING, obj -> {
            detachUnitView((BattleFieldObject) obj.get());
        });
        GuiEventManager.bind(UNIT_MOVED, obj -> {
            moveUnitView((BattleFieldObject) obj.get());
        });
        GuiEventManager.bind(UPDATE_GUI, obj -> {
            if (!VisionManager.isVisionHacked())
                if (OutlineMaster.isAutoOutlinesOff())
                    if (OutlineMaster.isOutlinesOn()) {
                        updateOutlines();
                    }

            firstUpdateDone = true;
            resetVisibleRequired = true;
            updateRequired = true;

            DungeonScreen.getInstance().updateGui();

        });

        GuiEventManager.bind(SELECT_MULTI_OBJECTS, obj -> {
            Pair<Set<DC_Obj>, TargetRunnable> p = (Pair<Set<DC_Obj>, TargetRunnable>) obj.get();
            if (p.getLeft().isEmpty()) {
                FloatingTextMaster.getInstance().createFloatingText(TEXT_CASES.REQUIREMENT,
                 "No targets available!",
                 Eidolons.getGame().getManager().getControlledObj());
                return;
            }
            Map<Borderable, Runnable> map = new HashMap<>();

            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = viewMap.get(obj1);
                if (b == null) {
                    b = cells[obj1.getX()][rows - 1 - obj1.getY()];
                }

                if (b instanceof GridUnitView) {
                    final GridUnitView gridView = (GridUnitView) b;
                    final UnitView unitView = gridView.getInitiativeQueueUnitView();
                    map.put(unitView, () -> p.getRight().run(obj1));
                }

                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_BLUE_BORDERS, map);
        });

        GuiEventManager.bind(DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            removeUnitView(unit);
        });

        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, onIngameEvent());

        GuiEventManager.bind(UPDATE_GRAVEYARD, obj -> {
            final Coordinates coordinates = (Coordinates) obj.get();
            cells[coordinates.getX()][rows - 1 - coordinates.getY()].updateGraveyard();
        });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            BattleFieldObject hero = (BattleFieldObject) obj.get();
            DungeonScreen.getInstance().activeUnitSelected(hero);
            if (hero instanceof Unit)
                animMaster.getConstructor().tryPreconstruct((Unit) hero);
            BaseView view = viewMap.get(hero);
            if (view == null) {
                System.out.println("viewMap not initiatilized at ACTIVE_UNIT_SELECTED!");
                return;
            }

            if (view.getParent() instanceof GridCellContainer) {
                ((GridCellContainer) view.getParent()).popupUnitView((GridUnitView) view);
            }

            viewMap.values().stream().forEach(v -> v.setActive(false));
            view.setActive(true);
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(BOTTOM_PANEL_UPDATE, new PanelActionsDataSource((Unit) hero));
            } else {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(BOTTOM_PANEL_UPDATE, null);
            }
            if (!firstUpdateDone) {
                DC_Game.game.getVisionMaster().triggerGuiEvents();
                GuiEventManager.trigger(UPDATE_GUI, null);
                GuiEventManager.trigger(UPDATE_LIGHT);
            }
            if (HelpMaster.isDefaultTextOn())
                if (!welcomeInfoShown) {
                    new Thread(() -> {
                        WaitMaster.WAIT(2000);
                        GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getWelcomeText());
                    }, " thread").start();
                    welcomeInfoShown = true;
                }
        });

        GuiEventManager.bind(UNIT_VISIBLE_ON, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, true);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), true);
        });
        GuiEventManager.bind(UNIT_VISIBLE_OFF, p -> {
            if (p.get() instanceof Collection) {
                for (Object sub : ((Collection) p.get())) {
                    setVisible((BattleFieldObject) sub, false);
                }
                return;
            }
            setVisible((BattleFieldObject) p.get(), false);
        });
        GuiEventManager.bind(UPDATE_UNIT_ACT_STATE, obj -> {
            final Pair<Unit, Boolean> pair = (Pair<Unit, Boolean>) obj.get();
            final BaseView baseView = viewMap.get(pair.getLeft());
            if (baseView instanceof GridUnitView) {
                final boolean mobilityState = pair.getRight();
                ((GridUnitView) baseView).getInitiativeQueueUnitView().setQueueMoving(mobilityState);
            }
        });

        GuiEventManager.bind(GuiEventType.HP_BAR_UPDATE_MANY, p -> {
            List list = (List) p.get();
            list.forEach(o -> updateHpBar(o));
        });
        GuiEventManager.bind(GuiEventType.HP_BAR_UPDATE, p -> {
            updateHpBar(p.get());
        });

    }

    private void updateHpBar(Object o) {
        GridUnitView view = null;
        if (o instanceof BattleFieldObject)
            view = (GridUnitView) viewMap.get(o);
        else if (o instanceof GridUnitView)
            view = (GridUnitView) (o);
        if (view != null)
            view.animateHpBarChange();
    }

    private void setVisible(BattleFieldObject sub, boolean b) {
        setVisible(viewMap.get(sub), b);
    }

    private void setVisible(BaseView view, boolean visible) {

        if (view == null)
            return;
        if (view.getParent() == null)
            addActor(view);
        if (visible) {
            if (view.isVisible())
                return;
        } else {
            if (!view.isVisible())
                return;
            if (view.getActionsOfClass(FadeOutAction.class, false).size > 0)
                return;
        }
        if (isFadeAnimForUnitsOn()) {
            if (visible) {
                view.getColor().a = 0;
                view.setVisible(true);
                ActorMaster.addFadeInAction(view, 0.25f);
            } else {
                ActorMaster.addFadeOutAction(view, 0.25f);
                ActorMaster.addSetVisibleAfter(view, false);
            }
            if (view instanceof GridUnitView) {
                BattleFieldObject obj = getObjectForView(view);
                LastSeenMaster.resetLastSeen((GridUnitView) view,
                 obj, !visible);

                (((GridUnitView) view).getLastSeenView()).setOutlinePathSupplier(
                 () -> StringMaster.toStringOrNull(obj.getLastSeenOutline())
                );

            }
        } else view.setVisible(visible);
    }

    private boolean isFadeAnimForUnitsOn() {
        return true;
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    private EventCallback onIngameEvent() {
        return param -> {
            Event event = (Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;

            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED) {
                GuiEventManager.trigger(GuiEventType.EFFECT_APPLIED, event.getRef());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING
             || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
             || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE) {
                BattleFieldObject hero = (BattleFieldObject) ref.getObj(KEYS.TARGET);
//                if (hero.isMainHero()) TODO this is an experiment (insane) feature...
//                    if (hero.isMine()) {
//                        turnField(event.getType());
//                    }
                BaseView view = viewMap.get(hero);
                if (view != null && view instanceof GridUnitView) {
                    GridUnitView unitView = ((GridUnitView) view);
                    unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
//                    SoundController.getCustomEventSound(SOUND_EVENT.UNIT_TURNS, );
                    if (hero instanceof Unit)
                        DC_SoundMaster.playTurnSound((Unit) hero);
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS
             ) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_ON, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL, ref.getTargetObj());
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                if (!MoveAnimation.isOn()) //|| AnimMaster.isAnimationOffFor(ref.getSourceObj(), viewMap.get(ref.getSourceObj())))
                    removeUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(ref.getSourceObj(),
                 viewMap.get(ref.getSourceObj())))
                    moveUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
            } else if (event.getType().name().startsWith("PROP_")) {
                caught = true;
            } else if (event.getType().name().startsWith("ABILITY_")) {
                caught = true;
            } else if (event.getType().name().startsWith("EFFECT_")) {
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                if (GuiEventManager.isParamEventAlwaysFired(event.getType().getArg())) {
                    UnitView view = (UnitView) getViewMap().get(
                     event.getRef().getSourceObj());
                    if (view != null)
                        if (view.isVisible())
                            if (view.getHpBar() != null)
//                                if (view.getHpBar( ).getDataSource().canHpBarBeVisible())
                                view.resetHpBar(new ResourceSourceImpl((BattleFieldObject) event.getRef().getSourceObj()));
                }
                caught = true;
            }

            if (!caught) {
             /*      System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
           */
            }
        };
    }

    private void turnField(EVENT_TYPE type) {
        if (type == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE) {
            turnField(90);
        } else if (type == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE) {
            turnField(-90);
        }
    }

    //Maybe as a disorienting effect from a spell?..
    private void turnField(int i) {
        ActorMaster.addRotateByAction(this, getRotation(), getRotation() + i);
//        for (int x = 0; x < cols; x++) {
//            for (int y = 0; y < rows; y++) {
//                GridCellContainer view = cells[x][y];
//                ActorMaster.addRotateByAction(view, view.getRotation(), view.getRotation() - i);
//            }
//        }
        viewMap.values().forEach(view -> {
            ActorMaster.addRotateByAction(view, view.getRotation(), view.getRotation() - i);
        });
    }

    private void createUnitsViews(DequeImpl<BattleFieldObject> units) {
//        lightingManager = new LightingManager(units, rows, cols);

        Map<Coordinates, List<BattleFieldObject>> map = new HashMap<>();
        for (BattleFieldObject object : units) {
            Coordinates c = object.getCoordinates();
            if (c == null)
                continue;
            if (!map.containsKey(c)) {
                map.put(c, new ArrayList<>());
            }
            List<BattleFieldObject> list = map.get(c);
            list.add(object);
        }

        for (Coordinates coordinates : map.keySet()) {
            List<BaseView> views = new ArrayList<>();
            List<OverlayView> overlays = new ArrayList<>();

            if (map.get(coordinates) == null) {
                continue;
            }
            for (BattleFieldObject object : map.get(coordinates)) {
                if (!object.isOverlaying()) {
                    final BaseView baseView = createUnitView(object);
                    views.add(baseView);
                } else {
                    final OverlayView overlay = UnitViewFactory.createOverlay(object);
                    if (!isVisibleByDefault(object))
                        overlay.setVisible(false);
                    viewMap.put(object, overlay);
                    Vector2 v = GridMaster.getVectorForCoordinate(
                     object.getCoordinates(), false, false, this);
                    overlay.setPosition(v.x, v.y - GridMaster.CELL_H);
                    addOverlay(overlay);
                }
            }

            final GridCellContainer gridCellContainer = cells[coordinates.getX()][rows - 1 - coordinates.getY()];
            views.forEach(gridCellContainer::addActor);
        }

        shadowMap = new ShadowMap(this);
        wallMap = new WallMap();
        addActor(wallMap);

        GuiEventManager.bind(SHOW_MODE_ICON, obj -> {
            List list = (List) obj.get();
            UnitView view = (UnitView) getViewMap().get(list.get(0));
            view.updateModeImage((String) list.get(1));
        });
        if (DC_Engine.isAtbMode()) {
            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Pair<Integer, Float>> p = (Pair<Unit, Pair<Integer, Float>>) obj.get();
                GridUnitView uv = (GridUnitView) viewMap.get(p.getLeft());
                if (uv == null) {
                    addUnitView(p.getLeft());
                    uv = (GridUnitView) viewMap.get(p.getLeft());
                }
                if (uv != null) {
                    uv.getInitiativeQueueUnitView().setTimeTillTurn(p.getRight().getRight());
                    uv.getInitiativeQueueUnitView().updateInitiative(p.getRight().getLeft());
                }
            });
        } else
            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Integer> p = (Pair<Unit, Integer>) obj.get();
                GridUnitView uv = (GridUnitView) viewMap.get(p.getLeft());
                if (uv == null) {
                    addUnitView(p.getLeft());
                    uv = (GridUnitView) viewMap.get(p.getLeft());
                }
                if (uv != null)
                    uv.getInitiativeQueueUnitView(). updateInitiative(p.getRight());
            });
        GuiEventManager.bind(UNIT_CREATED, p -> {
            addUnitView((BattleFieldObject) p.get());
        });
        GuiEventManager.bind(VALUE_MOD, p -> {
            FloatingTextMaster.getInstance().
             createAndShowParamModText(p.get());
        });


        WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
    }

    private boolean isVisibleByDefault(BattleFieldObject battleFieldObjectbj) {
        if (battleFieldObjectbj.isMine())
            return true;
        return battleFieldObjectbj instanceof Entrance;
    }

    private void moveUnitView(BattleFieldObject object) {
        int rows1 = rows - 1;
        GridUnitView uv = (GridUnitView) viewMap.get(object);
        if (uv == null) {
            return;
        }
        Coordinates c = object.getCoordinates();

//        uv.setVisible(true);

        try {
            cells[c.x][rows1 - c.y].addActor(uv);
            if (uv.getLastSeenView() != null) {
                if (LastSeenMaster.isUpdateRequired(object))
                    cells[c.x][rows1 - c.y].addActor(uv.getLastSeenView());
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        setUpdateRequired(true);
    }

    private BaseView createUnitView(BattleFieldObject battleFieldObjectbj) {
        GridUnitView view = UnitViewFactory.create(battleFieldObjectbj);
        viewMap.put(battleFieldObjectbj, view);
        if (battleFieldObjectbj.isPlayerCharacter()) {
            mainHeroView = view;
        }
        moveUnitView(battleFieldObjectbj);
        if (!isVisibleByDefault(battleFieldObjectbj))
            view.setVisible(false);
        return view;
    }

    private void addUnitView(BattleFieldObject heroObj) {
        BaseView uv = createUnitView(heroObj);
        moveUnitView(heroObj);
        if (!isVisibleByDefault(heroObj))
            uv.setVisible(false);
    }

    public boolean detachUnitView(BattleFieldObject heroObj) {
        BaseView uv = viewMap.get(heroObj);
        if (!(uv.getParent() instanceof GridCellContainer))
            return false;
        if (!uv.isVisible())
            return false;

        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        float x = uv.getX() + gridCellContainer.getX();
        float y = uv.getY() + gridCellContainer.getY();
        gridCellContainer.removeActor(uv);
        addActor(uv);
        uv.setPosition(x, y);
        return true;
    }

    private BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = viewMap.get(obj);
        if (uv == null) {
            LogMaster.log(1, obj + " IS NOT ON UNIT MAP!");
            return null;
        }
        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        if (gridCellContainer == null) {
            LogMaster.log(1, obj + " IS ALREADY REMOVED!");
            return uv;
        } else
            LogMaster.log(1, obj + " unit view REMOVED!");
        gridCellContainer.removeActor(uv);
        uv.setVisible(false);


        return uv;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);

        if (isHpBarsOnTop() && !GdxMaster.isHpBarAttached())
            for (BattleFieldObject obj : viewMap.keySet()) {
                BaseView sub = viewMap.get(obj);
                if (sub.isVisible())
                    if (sub instanceof GridUnitView) {
                        if (((GridUnitView) sub).getHpBar() != null)
                            if (((UnitView) sub).isHpBarVisible()) {
                                float x = sub.getX();
                                float y = sub.getY();

//                                if (x == 0 && y == 0  )
                                float scale = 1f;
                                if (sub.getParent() != this) //detached and moving!
                                {
                                    if (sub.getParent() instanceof GridCellContainer) {
                                        GridCellContainer parent = (GridCellContainer) sub.getParent();
                                        if (parent.
                                         getUnitViewCount() > 1) {
                                            if (parent.
                                             getUnitViewCountEffective() == 1) {
                                                scale = parent.getObjScale();
                                            } else if (!sub.isHovered()) {
                                                if (parent.getTopUnitView() != sub)
                                                    continue;
                                            } else {
                                                //offset? scale?
                                            }
                                        }

                                    }
                                    Vector2 v = GridMaster.getVectorForCoordinate(obj.getBufferedCoordinates(),
                                     false, false, true, this);

                                    x = v.x;
                                    y = v.y;
                                }
                                HpBar hpBar = ((UnitView) sub).getHpBar();
                                hpBar.setScale(scale, (1 + scale) / 2);
                                hpBar.act(Gdx.graphics.getDeltaTime());
                                hpBar.drawAt(batch, x, y);


                            }

                    }
            }
    }

    @Override
    public void act(float delta) {
        if (resetVisibleRequired) {
            resetVisible();
        }
        super.act(delta);
        if (updateRequired) {
            resetZIndices();
            update();
        }
        if (isAutoResetVisibleOn()) {
            if (resetTimer <= 0) {
                resetTimer = 0.5f;
                for (BattleFieldObject sub : DC_Game.game.getVisionMaster().getVisible()) {
                    setVisible(viewMap.get(sub), true);
                }
                for (BattleFieldObject sub : DC_Game.game.getVisionMaster().getInvisible()) {
                    setVisible(viewMap.get(sub), false);
                }

            }
            resetTimer -= delta;
        }
    }

    private boolean isAutoResetVisibleOn() {
        return true;
    }

    private void update() {
        shadowMap.update();
        updateRequired = false;

    }

    private void resetVisible() {
        for (BattleFieldObject sub : viewMap.keySet()) {
            BaseView view = viewMap.get(sub);
            //TODO STEALTH VISUALS
//            Map<GridUnitView, Boolean> units = new HashMap<>();
//            if (view instanceof GridUnitView) {
//                boolean stealth = false;
//                if (sub.isMine()) {
//                    if (sub.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.INVISIBLE) {
//                        if (sub instanceof Unit) {
//                            if (((Unit) sub).isUsingStealth()) {
//                                stealth = true;
////                            ((UnitView) view).getmsetmFlickering(true);
//                            }
//                        }
//                    }
//                }
//                units.put(((GridUnitView) view), stealth);
//            }
//            for (GridUnitView v : units.keySet())
//                ((UnitView) view).setFlickering(units.get(v));
            if (view.getActions().size == 0) {
                if (sub.isDead()) {
                    view.setVisible(false);
                    view.remove();
                }
            }
        }
        resetVisibleRequired = false;
    }

    public GridUnitView getHoverObj() {
        return hoverObj;
    }

    public void setHoverObj(GridUnitView hoverObj) {
        this.hoverObj = hoverObj;
    }

    public void resetZIndices() {
        loop:
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                GridCellContainer cell = cells[x][y];
                List<GenericGridView> views = cell.getUnitViewsVisible();
                if (views.isEmpty()) {
                    cell.setZIndex(0);
                } else {
                    cell.setZIndex(y);

                    for (GenericGridView sub : views) {
                        if (sub.isHovered() && sub instanceof  GridUnitView) {
                            setHoverObj((GridUnitView) sub);
                            cell.setZIndex(Integer.MAX_VALUE);
                            cell.setTopUnitView(sub);

                        }
                    }
                }
            }
        }
        wallMap.setVisible(WallMap.isOn());
        boolean ctrl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
        if (ctrl) {
            if (ShadowMap.isOn())
                for (SHADE_LIGHT sub : shadowMap.getCells().keySet()) {
                    shadowMap.setZtoMax(sub);
                }
        }
        wallMap.setZIndex(Integer.MAX_VALUE);
        overlays.forEach(overlayView -> overlayView.setZIndex(Integer.MAX_VALUE));

        if (!ctrl) {
            if (ShadowMap.isOn())
                for (SHADE_LIGHT sub : shadowMap.getCells().keySet()) {
                    shadowMap.setZtoMax(sub);
                }
        }

        overlayManager.setZIndex(Integer.MAX_VALUE);

        animMaster.setZIndex(Integer.MAX_VALUE);
        if (fpsLabel != null)
            fpsLabel.setZIndex(Integer.MAX_VALUE);
    }

    public void addOverlay(OverlayView view) {
        int width = (int) (GridMaster.CELL_W * OverlayView.SCALE);
        int height = (int) (GridMaster.CELL_H * OverlayView.SCALE);
        Dimension dimension = GridMaster.getOffsetsForOverlaying(view.getDirection(), width, height);
        float calcXOffset = view.getX();
        float calcYOffset = view.getY();

        view.setBounds((float) dimension.getWidth() + calcXOffset
         , (float) dimension.getHeight() + calcYOffset
         , width, height);
        addActor(view);
        overlays.add(view);
    }

    public Map<BattleFieldObject, BaseView> getViewMap() {
        return viewMap;
    }

    public int getCols() {
        return cols;
    }

    public GridCellContainer[][] getCells() {
        return cells;
    }

    public int getRows() {
        return rows;
    }

    public Label getFpsLabel() {
        return fpsLabel;
    }

    public void setFpsLabel(Label fpsLabel) {
        this.fpsLabel = fpsLabel;
    }

    public GridUnitView getMainHeroView() {
        return mainHeroView;
    }

    public BattleFieldObject getObjectForView(BaseView source) {
        return new MapMaster<BattleFieldObject, BaseView>()
         .getKeyForValue(viewMap, source);
    }
}
