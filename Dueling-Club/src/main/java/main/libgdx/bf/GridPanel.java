package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.mode.STD_MODES;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.AI_Manager;
import main.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.logic.event.Event;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.AnimationConstructor;
import main.libgdx.anims.particles.lighting.LightingManager;
import main.libgdx.anims.std.DeathAnim;
import main.libgdx.anims.std.MoveAnimation;
import main.libgdx.anims.text.FloatingTextMaster;
import main.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import main.libgdx.bf.light.ShadowMap;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.bf.overlays.HpBar;
import main.libgdx.bf.overlays.WallMap;
import main.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.libgdx.texture.TextureManager;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import main.system.text.HelpMaster;
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
    private static final String backgroundPath = "UI/custom/grid/GRID_BG_WIDE.png";
    private static final String emptyCellPath = "UI/cells/Empty Cell v3.png";
    private static final String emptyCellPathFloor = "UI/cells/Floor.png";
    private static final String hiddenCellPath = "UI/cells/Hidden Cell v2.png";
    private static final String highlightCellPath = "UI/cells/Highlight Green Cell v3.png";
    private static final String unknownCellPath = "UI/cells/Unknown Cell v2.png";
    private static final String cellBorderPath = "UI\\CELL for 96.png";
    private static final String gridCornerElementPath = StrPathBuilder.build(
     "UI", "bf", "gridCorner.png");

    protected TextureRegion emptyImage;
    protected TextureRegion hiddenImage;
    protected TextureRegion highlightImage;
    protected TextureRegion unknownImage;
    protected TextureRegion cellBorderTexture;
    protected GridCellContainer[][] cells;
    private Map<BattleFieldObject, BaseView> unitMap;
    private int cols;
    private int rows;
    private LightingManager lightingManager;
    private AnimMaster animMaster;
    private ShadowMap shadowMap;
    private Label fpsLabel;
    private boolean fpsDebug = false;
    private TextureRegion cornerRegion;
    private WallMap wallMap;
    private List<OverlayView> overlays = new LinkedList<>();
    private GridUnitView hoverObj;
    private boolean resetVisibleRequired;
    private boolean updateRequired;
    private boolean firstUpdateDone;
    private boolean welcomeInfoShown;

    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    public static boolean isHpBarsOnTop() {
        return true;
    }

    public void updateOutlines() {

        unitMap.keySet().forEach(obj -> {
            if (!obj.isOverlaying())
                if (!obj.isMine())
                    if (!obj.isWall()) {
                        OUTLINE_TYPE outline = obj.getOutlineType();
                        GridUnitView uv = (GridUnitView) unitMap.get(obj);

                        TextureRegion texture = null;
                        if (outline != null) {
                            texture = TextureCache.getOrCreateR(
                             Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj));
                            uv.setOutline(texture);
                        } else {
                            uv.setOutline(null);
//                    uv.setAltPortrait();
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

        float posX = x * GridConst.CELL_W;
        float posY = y * GridConst.CELL_H;
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
            image.setPosition(posX + i * GridConst.CELL_W + (20 - 20 * i)//+40
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
              + i * GridConst.CELL_H + (20 - 20 * i));//+40
        }

        if (hor != null)
            if (vert != null) {
                int i = vert ? 1 : -1;
                Image image = new Image(cornerRegion);
                image.setPosition(posX + i * 40 + i * GridConst.CELL_W + i * -77, posY
                 + i * 40 + i * GridConst.CELL_H + i * -77);

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
        this.unitMap = new HashMap<>();
        emptyImage = TextureCache.getOrCreateR(getCellImagePath());
        hiddenImage = TextureCache.getOrCreateR(hiddenCellPath);
        highlightImage = TextureCache.getOrCreateR(highlightCellPath);
        unknownImage = TextureCache.getOrCreateR(unknownCellPath);
        cellBorderTexture = TextureCache.getOrCreateR(cellBorderPath);
        cornerRegion = TextureCache.getOrCreateR(gridCornerElementPath);
        cells = new GridCellContainer[cols][rows];


        int rows1 = rows - 1;
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cells[x][y] = new GridCellContainer(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridConst.CELL_W);
                cells[x][y].setY(y * GridConst.CELL_H);
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

        animMaster = AnimMaster.getInstance();
        animMaster.bindEvents();
        addActor(animMaster);

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
        return (UnitView) unitMap.get(battleFieldObject);
    }

    private void bindEvents() {

        GuiEventManager.bind(GuiEventType.ANIMATION_QUEUE_FINISHED, (p) -> {
            resetVisible();
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


//        GuiEventManager.bind(GRID_OBJ_HOVER_ON, (event) -> {
//            resetZIndices();
//        });
//        GuiEventManager.bind(GRID_OBJ_HOVER_OFF, (event) -> {
//            resetZIndices();
//        });
        GuiEventManager.bind(UNIT_MOVED, obj -> {
            moveUnitView((BattleFieldObject) obj.get());
        });
        GuiEventManager.bind(UPDATE_GUI, obj -> {
            if (!VisionManager.isVisionHacked())
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
                Borderable b = unitMap.get(obj1);
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
            BaseView view = unitMap.get(hero);
            if (view == null) {
                System.out.println("unitMap not initiatilized at ACTIVE_UNIT_SELECTED!");
                return;
            }

            if (view.getParent() instanceof GridCellContainer) {
                ((GridCellContainer) view.getParent()).popupUnitView(view);
            }

            unitMap.values().stream().forEach(v -> v.setActive(false));
            view.setActive(true);
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(UPDATE_QUICK_SLOT_PANEL, new PanelActionsDataSource((Unit) hero));
            } else {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(UPDATE_QUICK_SLOT_PANEL, null);
            }
            if (!firstUpdateDone) {
                DC_Game.game.getVisionMaster().triggerGuiEvents();
                GuiEventManager.trigger(UPDATE_GUI, null);

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

//        GuiEventManager.bind(UPDATE_UNIT_VISIBLE, obj -> {
//            final Pair<Unit, Boolean> pair = (Pair<Unit, Boolean>) obj.get();
//            final BaseView baseView = unitMap.get(pair.getLeft());
//            if (baseView instanceof GridUnitView) {
//                final Boolean isVisible = pair.getRight();
//                //TODO ???
//                ((GridUnitView) baseView).setVisibleVal(isVisible ? 100 : 50);
//            }
//        });
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
            final BaseView baseView = unitMap.get(pair.getLeft());
            if (baseView instanceof GridUnitView) {
                final boolean mobilityState = pair.getRight();
                ((GridUnitView) baseView).setMobilityState(mobilityState);
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
            view = (GridUnitView) unitMap.get(o);
        else
            view = (GridUnitView) (o);
        view.animateHpBarChange();
    }

    private void setVisible(BattleFieldObject sub, boolean b) {
        unitMap.get(sub).setVisible(b);
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
//                if (hero.isMainHero()) TODO this insane feature...
//                    if (hero.isMine()) {
//                        turnField(event.getType());
//                    }
                BaseView view = unitMap.get(hero);
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
//                else //TODO make it work instead of onFinishEvents!
//                AnimMaster.getInstance(). onDone(event,portrait ->
//                GuiEventManager.trigger(DESTROY_UNIT_MODEL,
//                 new EventCallbackParam(r.getTargetObj())
//                )                ,  new EventCallbackParam(r.getTargetObj()));

                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(ref.getSourceObj(), unitMap.get(ref.getSourceObj())))
                    removeUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(ref.getSourceObj(),
                 unitMap.get(ref.getSourceObj())))
                    moveUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            }
//            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_SUMMONED) {
//                GuiEventManager.trigger(UNIT_CREATED, new EventCallbackParam(ref.getObj(KEYS.SUMMONED)));
//                caught = true; now in ObjCreator
//            }

            else if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
                /*switch (event.getType().getArg()) {
                    case "Spellpower":
                        int a = 10;
                        break;
                }*/
            } else if (event.getType().name().startsWith("PROP_")) {
                caught = true;
            } else if (event.getType().name().startsWith("ABILITY_")) {
                caught = true;
            } else if (event.getType().name().startsWith("EFFECT_")) {
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                if (GuiEventManager.isParamEventAlwaysFired(event.getType().getArg())) {
                    UnitView view = (UnitView) getUnitMap().get(
                     event.getRef().getSourceObj());
                    if (view != null)
                        if (view.isVisible())
                            if (view.getHpBar() != null)
//                                if (view.getHpBar( ).getDataSource().canHpBarBeVisible())
                                view.resetHpBar(new ResourceSourceImpl((BattleFieldObject) event.getRef().getSourceObj()));
                }
//                case "Illumination":
//                        if (lightingManager != null) {
//                            Obj o = event.getRef().getTargetObj();
//                            if (o instanceof Unit) {
//                                lightingManager.updateObject((BattleFieldObject) event.getRef().getTargetObj());
//                            }
//                        }
//                        caught = true;
//                        break;
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

    private void turnField(int i) {
        ActorMaster.addRotateByAction(this, getRotation(), getRotation() + i);
//        for (int x = 0; x < cols; x++) {
//            for (int y = 0; y < rows; y++) {
//                GridCellContainer view = cells[x][y];
//                ActorMaster.addRotateByAction(view, view.getRotation(), view.getRotation() - i);
//            }
//        }
        unitMap.values().forEach(view -> {
            ActorMaster.addRotateByAction(view, view.getRotation(), view.getRotation() - i);
        });
//        cells

    }

    private void createUnitsViews(DequeImpl<BattleFieldObject> units) {
        lightingManager = new LightingManager(units, rows, cols);

        Map<Coordinates, List<BattleFieldObject>> map = new HashMap<>();
        for (BattleFieldObject object : units) {
            Coordinates c = object.getCoordinates();
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
                    unitMap.put(object, overlay);
                    Vector2 v = GridMaster.getVectorForCoordinate(
                     object.getCoordinates(), false, false, this);
                    overlay.setPosition(v.x, v.y - GridConst.CELL_H);
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
            Unit unit = (Unit) obj.get();
            if (unit.isDead()) return;
            UnitView view = (UnitView) unitMap.get(unit);
            if (view != null) {
                if (unit.getModeFinal() == null || unit.getModeFinal() == STD_MODES.NORMAL) {
                    view.updateModeImage(null);
                } else {
                    if (unit.getModeFinal() != null)
                        if (unit.getBuff(unit.getModeFinal().getBuffName()) != null)
                            try {
                                view.updateModeImage(unit.getBuff(unit.getModeFinal().getBuffName()).getImagePath());
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
                }
            }
        });

        GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
            Pair<Unit, Integer> p = (Pair<Unit, Integer>) obj.get();
            GridUnitView uv = (GridUnitView) unitMap.get(p.getLeft());
            if (uv == null) {
                addUnitView(p.getLeft());
                uv = (GridUnitView) unitMap.get(p.getLeft());
            }
            if (uv != null)
                uv.updateInitiative(p.getRight());
        });
        GuiEventManager.bind(UNIT_CREATED, p -> {
            addUnitView((BattleFieldObject) p.get());
        });


        WaitMaster.receiveInput(WAIT_OPERATIONS.GUI_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GUI_READY);
    }

    private boolean isVisibleByDefault(BattleFieldObject battleFieldObjectbj) {
        if (battleFieldObjectbj.isMine())
            return true;
        if (battleFieldObjectbj instanceof Entrance)
            return true;
        return false;
    }

    private void moveUnitView(BattleFieldObject heroObj) {
        int rows1 = rows - 1;
        BaseView uv = unitMap.get(heroObj);
        if (uv == null) {
            return;
        }
        Coordinates c = heroObj.getCoordinates();

//        uv.setVisible(true);
        cells[c.x][rows1 - c.y].addActor(uv);

        if (lightingManager != null) {
            lightingManager.updatePos(heroObj);
            lightingManager.updateAll();
        }
    }

    private BaseView createUnitView(BattleFieldObject battleFieldObjectbj) {
        BaseView uv = UnitViewFactory.create(battleFieldObjectbj);
        unitMap.put(battleFieldObjectbj, uv);
        moveUnitView(battleFieldObjectbj);
        if (!isVisibleByDefault(battleFieldObjectbj))
            uv.setVisible(false);
        return uv;
    }

    private void addUnitView(BattleFieldObject heroObj) {
        BaseView uv = createUnitView(heroObj);
        moveUnitView(heroObj);
        if (!isVisibleByDefault(heroObj))
            uv.setVisible(false);
    }

    public void detachUnitView(BattleFieldObject heroObj) {
        BaseView uv = unitMap.get(heroObj);
        if (!(uv.getParent() instanceof GridCellContainer))
            return;
        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        float x = uv.getX() + gridCellContainer.getX();
        float y = uv.getY() + gridCellContainer.getY();
        gridCellContainer.removeActor(uv);
        addActor(uv);
        uv.setPosition(x, y);
    }

    private BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = unitMap.get(obj);
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
        if (lightingManager != null) {
            lightingManager.updateLight();
        }

        if (isHpBarsOnTop())
            for (BattleFieldObject obj : unitMap.keySet()) {
                BaseView sub = unitMap.get(obj);
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
                                            } else if (!sub.isHovered())
                                                continue;
                                            else {
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
                                hpBar.scaleBy(scale, (1 + scale) / 2);
                                hpBar.act(Gdx.graphics.getDeltaTime());
                                hpBar.drawAt(batch, x, y);


                            }

                    }
            }
//        int w = GdxMaster.getWidth();
//        int h = GdxMaster.getHeight();
        // draw all caches (could be optimized to cull caches outside the camera)
//TODO TODO
//        for (SpriteCache cache : this.cacheMap) {
//            // setup camera to zoom in and out
//            cache.getProjectionMatrix().setToOrtho2D(64*32*8 - w*zoom*0.5f,-h*zoom*0.5f,w*zoom,h*zoom);
//            cache.begin();
//            cache.draw(0); // the cacheId is 0, we know that ahead for this sample
//            cache.end();
//        }
    }

    @Override
    public void act(float delta) {
        if (resetVisibleRequired) {
            resetVisible();
        }
        super.act(delta);
//        if (checkResetZRequired()) {

//        }
        if (updateRequired) {
            resetZIndices();
            update();
        }
    }

    private void update() {
        shadowMap.update();
//        for (int x = 0; x < cols; x++) {
//            for (int y = 0; y < rows; y++) {
//                GridCellContainer cell = cells[x][y];
//                cell.recalcUnitViewBounds();
//            }
//        }
        updateRequired = false;

    }

    private void resetVisible() {
        for (BattleFieldObject sub : unitMap.keySet()) {
            BaseView view = unitMap.get(sub);
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
//
//                    }
//                }
//                units.put(((GridUnitView) view), stealth);
//            }
//            for (GridUnitView v : units.keySet())
//                ((UnitView) view).setFlickering(units.get(v));


            if (view.getActions().size == 0) {
                if (sub.isDead())
                    view.setVisible(false);
                if (!AI_Manager.isRunning())
                    if (!sub.isOverlaying()) {
                        GridCellContainer cellContainer =
                         cells[sub.getCoordinates().x][rows - sub.getCoordinates().y - 1];
                        {
                            if (view.getParent() != cellContainer)
                                view.remove();
                            cellContainer.addActor(view);
                        }
                    }
            }
        }
        resetVisibleRequired = false;
//sub instanceof Unit && !((Unit)sub).getAI().isOutsideCombat()
//            if (!sub.isMine()) {
//                if (sub.getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.UNKNOWN)
//                    view.setVisible(false);
//                else if (sub.getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.INVISIBLE)
//                    view.setVisible(false);
//                else if (!sub.isWall()) {
//                    if (sub.getOutlineTypeForPlayer() == OUTLINE_TYPE.BLOCKED_OUTLINE)
//                        view.setVisible(false);
//                    else {
//                        if (sub.getVisibilityLevelForPlayer() == VISIBILITY_LEVEL.UNSEEN)
//                            view.setVisible(false);
//                        else if (view instanceof UnitView) {
//                            if (((UnitView) view).getOutline() != null)
//                                if (((UnitView) view).getOutline().getTexture().getTextureData().toString().contains("locked")) {
//                                    view.setVisible(false);
//                                }
//                        }
//                    }
//                }
//            }
//        }
//        resetVisibleRequired = false;
    }

    private boolean checkResetZRequired() {
        if (hoverObj == null) return true;
        return !hoverObj.isHovered();
    }

    public void resetZIndices() {
        loop:
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                GridCellContainer cell = cells[x][y];
                List<GridUnitView> views = cell.getUnitViews();
//                if (views.size()>1)

                for (GridUnitView sub : views) {
                    if (sub.isHovered()) {
                        hoverObj = sub;
                        cell.setZIndex(Integer.MAX_VALUE);
                        break;
                    }
//                    else
//                        cell.setZIndex(100+x*rows+y);
                }
            }
        }
        wallMap.setVisible(WallMap.isOn());
        wallMap.setZIndex(Integer.MAX_VALUE);
//        if (ShadowMap.isOn())
//            shadowMap.setZtoMax(SHADE_LIGHT.LIGHT_EMITTER);
        overlays.forEach(overlayView -> overlayView.setZIndex(Integer.MAX_VALUE));
        if (ShadowMap.isOn())
            for (SHADE_LIGHT sub : shadowMap.getCells().keySet()) {
//            if (sub!=SHADE_LIGHT.LIGHT_EMITTER)
                shadowMap.setZtoMax(sub);
            }


        animMaster.setZIndex(Integer.MAX_VALUE);
        if (fpsLabel != null)
            fpsLabel.setZIndex(Integer.MAX_VALUE);
    }

    public void addOverlay(OverlayView view) {
        int width = (int) (GridConst.CELL_W * OverlayView.SCALE);
        int height = (int) (GridConst.CELL_H * OverlayView.SCALE);
        Dimension dimension = GridMaster.getOffsetsForOverlaying(view.getDirection(), width, height);
        float calcXOffset = view.getX();
        float calcYOffset = view.getY();

        view.setBounds((float) dimension.getWidth() + calcXOffset
         , (float) dimension.getHeight() + calcYOffset
         , width, height);
        addActor(view);
        overlays.add(view);
    }

    public Map<BattleFieldObject, BaseView> getUnitMap() {
        return unitMap;
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
}
