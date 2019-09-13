package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.LastSeenMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.dungeon.puzzle.cell.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.LinkedGridObject;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Manipulator;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObj;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.FadeOutAction;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.TargetRunnable;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.decor.Pillars;
import eidolons.libgdx.bf.decor.ShardVisuals;
import eidolons.libgdx.bf.light.ShadowMap;
import eidolons.libgdx.bf.mouse.BattleClickListener;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import eidolons.libgdx.bf.overlays.WallMap;
import eidolons.libgdx.gui.generic.GroupWithEmitters;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.texture.TextureManager;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.HelpMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

import static main.system.GuiEventType.*;

public class GridPanel extends Group {

    private final int square;
    protected GridCellContainer[][] cells;
    private int cols;
    private int rows;
    private Map<BattleFieldObject, BaseView> viewMap;
    private List<OverlayView> overlays = new ArrayList<>();

    private GridManager manager;

    private AnimMaster animMaster;
    private GridOverlaysManager overlayManager;
    private ShadowMap shadowMap;
    private WallMap wallMap;
    private ShardVisuals shards;
    private Pillars pillars;

    private GridUnitView hoverObj;
    private GridUnitView mainHeroView;
    private GridUnitView mainHeroViewShadow;
    private GridUnitView mainHeroViewPale;

    private float resetTimer;
    private boolean resetVisibleRequired;
    private boolean updateRequired;
    private boolean firstUpdateDone;
    private boolean welcomeInfoShown;
    private float autoResetVisibleOnInterval = 0.5f;

    protected List<Manipulator> manipulators = new ArrayList<>();
    protected List<GridObject> gridObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjects = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsTop = new ArrayList<>();
    protected List<GroupX> customOverlayingObjectsUnder = new ArrayList<>(100);
    protected List<GroupWithEmitters> emitterGroups = new ArrayList<>(125);
    private List<GroupX> commentSprites = new ArrayList<>(5);
    private List<GroupX> activeCommentSprites = new ArrayList<>(3);
    GridViewAnimator gridViewAnimator = new GridViewAnimator(this);


    public GridPanel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.square = rows * cols;
    }

    public void reactivateModule() {
        bindEvents();

    }

    public GridPanel init(DequeImpl<BattleFieldObject> objects) {
        objects.removeIf(unit ->
                {
                    if (unit.getCoordinates().x < 0)
                        return true;
                    if (unit.getCoordinates().y < 0)
                        return true;
                    if (unit.getCoordinates().x >= cols)
                        return true;
                    if (unit.getCoordinates().y >= rows)
                        return true;
                    return false;
                }
        );
        this.viewMap = new HashMap<>();
        manager = new GridManager(this);
        cells = new GridCellContainer[cols][rows];

        int rows1 = rows - 1;
        boolean hasVoid = false;
        TextureRegion emptyImage = TextureCache.getOrCreateR(getCellImagePath());
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                DC_Cell cell = DC_Game.game.getCellByCoordinate(Coordinates.get(x, rows1 - y));
                if (cell == null) {
                    hasVoid = true;
                    continue;
                }
                emptyImage = TextureCache.getOrCreateR(cell.getImagePath());
                cells[x][y] = new GridCellContainer(emptyImage, x, rows1 - y);
                cells[x][y].setX(x * GridMaster.CELL_W);
                cells[x][y].setY(y * GridMaster.CELL_H);
                addActor(cells[x][y].init());
                cells[x][y].setUserObject(cell);
            }
        }
        if (!hasVoid) {
            for (int x = 0; x < cols; x++) {
                for (int y = 0; y < rows; y++) {
                    checkAddBorder(x, y);
                }
            }
        }
        if (hasVoid) {
            if (isShardsOn())
                addActor(shards = new ShardVisuals(this));
            if (isPillarsOn())
                addActor(pillars = new Pillars(this));
        }
        if (OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.SPRITE_CACHE_ON))
            TextureManager.addCellsToCache(cols, rows);

        addActor(new CellBorderManager());

        bindEvents();

        createUnitsViews(objects);

        setHeight(GridMaster.CELL_W * rows);
        setWidth(GridMaster.CELL_H * cols);

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
        addActor(overlayManager = new GridOverlaysManager(this));
        addActor(animMaster = AnimMaster.getInstance());
        animMaster.bindEvents();

        //        if (AnimConstructor.isPreconstructAllOnGameInit())
        //            units.forEach(unit ->
        //            {
        //                if (unit instanceof Unit)
        //                    animMaster.getConstructor().preconstructAll((Unit) unit);
        //            });


        return this;
    }

    private boolean isPillarsOn() {
        return true;
    }

    private boolean isShardsOn() {
        if (EidolonsGame.BRIDGE) {
            return true;
        }
        if (CoreEngine.isLiteLaunch()) {
            return false;
        }
        if (EidolonsGame.BOSS_FIGHT) {
            return false;
        }

        if (DC_Game.game.getDungeonMaster().getDungeonLevel() == null) {
            switch (DC_Game.game.getDungeonMaster().getDungeonLevel().getLocationType().getGroup()) {
                case NATURAL:
                case AVERAGE:
                case SURFACE:
                    return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        boolean paused = false;
        if (HqPanel.getActiveInstance() != null) {
            if (HqPanel.getActiveInstance().getColor().a == 1)
                return;
        }
        if (Eidolons.game != null)
            if (Eidolons.game.getLoop() != null)
                paused = Eidolons.game.getLoop().isPaused();
        if (ShadowMaster.isShadowAlive() && Eidolons.game.getLoop().getActiveUnit() != ShadowMaster.getShadowUnit()) {
            paused = false;
        }
        if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
            for (GroupX groupX : getCommentSprites()) {
                groupX.setVisible(false);
            }
            animMaster.setVisible(false);

            super.draw(batch, 1);
            if (isDrawEmittersOnTop())
                drawEmitters(batch);
            drawComments(batch);
            animMaster.setVisible(true);
            animMaster.draw(batch, 1f);
        } else
            ShaderDrawer.drawWithCustomShader(this, batch,
                    paused ? GrayscaleShader.getGrayscaleShader() : null, true);
    }

    public static final  boolean isDrawEmittersOnTop() {
        return true;
    }

    private void drawComments(Batch batch) {
        for (GroupX commentSprite : getCommentSprites()) {
            commentSprite.setVisible(true);
            commentSprite.draw(batch, 1f);
        }
    }

    private void drawEmitters(Batch batch) {
        for (int i = 0; i <emitterGroups.size() ; i++) {
            emitterGroups.get(i). draw(batch, 1f, true);
        }
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        if (actor instanceof GroupWithEmitters) {
            emitterGroups.add((GroupWithEmitters) actor);
        }
    }

    @Override
    public void act(float delta) {
        if (HqPanel.getActiveInstance() != null) {
            if (HqPanel.getActiveInstance().getColor().a == 1)
                return;
        }
        if (resetVisibleRequired) {
            resetVisible();
        }
        super.act(delta);
        if (updateRequired) {
            resetZIndices();
            update();
        }
//     moving units will be hidden!
//     if (animMaster != null) {
//            animMaster.setVisible(!CoreEngine.isFootageMode());
//        }

        if (isAutoResetVisibleOn())
            if (DC_Game.game != null)
                if (DC_Game.game.getVisionMaster().getVisible() != null) {
                    if (resetTimer <= 0) {
                        resetTimer = autoResetVisibleOnInterval;
                        for (BattleFieldObject sub : DC_Game.game.getVisionMaster().getVisible()) {
                            setVisible(viewMap.get(sub), true);
                            GridMaster.validateVisibleUnitView(viewMap.get(sub));
                            if (sub.isPlayerCharacter()) {
                                if (ExplorationMaster.isExplorationOn()) {
                                    GridUnitView view = (GridUnitView) viewMap.get(sub);
                                    view.resetHpBar();
                                }
                            }
                        }
                        for (BattleFieldObject sub : DC_Game.game.getVisionMaster().getInvisible()) {
                            setVisible(viewMap.get(sub), false);
                            //                            Debugger.validateInvisibleUnitView(viewMap.getVar(sub));
                        }
                        //                        for (int x = 0; x < cols; x++) {
                        //                            for (int y = 0; y < rows; y++) {
                        //                                GridCellContainer cell = cells[x][y];
                    }
                    resetTimer -= delta;
                }
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
                            "ui", "cells", "bf", "gridBorder " +
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
                    "ui", "cells", "bf", "gridBorder " +
                            suffix +
                            ".png")));
            addActor(image);
            image.setPosition(posX //+ i * 35
                    , posY
                            + i * GridMaster.CELL_H + (20 - 20 * i));//+40
        }
        TextureRegion cornerRegion = TextureCache.getOrCreateR(GridMaster.gridCornerElementPath);
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


    private String getCellImagePath() {
        if (Eidolons.getGame().getDungeon() != null) {
            if (Eidolons.getGame().getDungeon().isSurface())
                return GridMaster.emptyCellPath;
        }
        return GridMaster.emptyCellPathFloor;
    }

    private UnitView getUnitView(BattleFieldObject battleFieldObject) {
        return (UnitView) viewMap.get(battleFieldObject);
    }


    private void initMaze(boolean hide, MazePuzzle.MazeData data) {
        Coordinates c = null;
        for (Coordinates coordinates : data.mazeWalls) {
            c = data.c.getOffset(coordinates.negativeY());
            GridCellContainer container = cells[c.getX()][getGdxY(c.getY())];
            if (container == null) {
                main.system.auxiliary.log.LogMaster.warn("Void cell in maze puzzle!" + c);
                continue;

            }
            container.setOverlayTexture(
                    hide ? null :
                            TextureCache.getOrCreateR(data.mazeType.getImagePath()));
            //TODO VFX or sprite?
        }
    }

    private void bindEvents() {
        GuiEventManager.bind(ACTOR_SPEAKS, p -> {
            if (p.get() == null) {
                return;
            }
            BattleFieldObject unit = (BattleFieldObject) p.get();
            UnitView view = getUnitView(unit);
            main.system.auxiliary.log.LogMaster.dev("ACTOR_SPEAKS: " +unit);

            unit.getGame().getManager().setHighlightedObj(unit);

            for (BaseView value : viewMap.values()) {
                if (value==view) {

//                    GuiEventManager.trigger(GuiEventType.SCALE_UP_VIEW, view);
                    view.highlight();
                    GraphicData data= new GraphicData("alpha::0.8f");
                    gridViewAnimator.animate(view ,  GridViewAnimator.VIEW_ANIM.screen , data);
                    WaitMaster.doAfterWait(4000, ()-> {
                        if (!DialogueManager.isRunning())
                        {
                            main.system.auxiliary.log.LogMaster.dev("hl off: " +unit);
                            view.highlightOff();
                            unit.getGame().getManager().setHighlightedObj(null);
                        }
                    });
                } else
                {
                    value.highlightOff();
                    GuiEventManager.trigger(GRID_OBJ_HOVER_OFF, view);
                }
            }

        });
        GuiEventManager.bind(REMOVE_GRID_OBJ, p -> {
            List list = (List) p.get();
            String key = (String) list.get(0);
            Coordinates c = (Coordinates) list.get(1);
            GridObject gridObj = findGridObj(key, c);
            if (gridObj == null) {
                main.system.auxiliary.log.LogMaster.dev("No grid obj to remove: " + key + c);
            }
            gridObj.fadeOut(true);
            customOverlayingObjectsUnder.remove(gridObj);
            customOverlayingObjects.remove(gridObj);
            customOverlayingObjectsTop.remove(gridObj);
            gridObjects.remove(gridObj);
        });
        GuiEventManager.bind(GuiEventType.GRID_OBJ_ANIM, p -> {
            List list = (List) p.get();
            String key = (String) list.get(0);
            Coordinates c = (Coordinates) list.get(1);
            GraphicData data = (GraphicData) list.get(2);
            GridObject gridObj = findGridObj(key, c);
            gridViewAnimator.animate(gridObj, data);
        });

        GuiEventManager.bind(GuiEventType.ADD_GRID_OBJ, p -> {
            GridObject object = (GridObject) p.get();
            addActor(object);

            gridObjects.add(object);
            Boolean under = object.getUnder();
            if (under != null) {
                (under ? customOverlayingObjectsUnder : customOverlayingObjectsTop).add(object);
                return;
            }
            if (object instanceof LinkedGridObject) {
                if (((LinkedGridObject) object).getLinked() instanceof OverlayView) {
                    customOverlayingObjectsTop.add(object);
                    return;
                } else {
                    if (((LinkedGridObject) object).getLinked().getUserObject() instanceof Structure) {
                        if (((Structure) ((LinkedGridObject) object).getLinked().getUserObject()).isWater()) {
                            customOverlayingObjectsUnder.add(object);
                            return;
                        }
                    }
                }
            }
            getCustomOverlayingObjects().add(object);
        });
        GuiEventManager.bind(HIDE_MAZE, p -> {
            initMaze(true, (MazePuzzle.MazeData) p.get());
        });
        GuiEventManager.bind(SHOW_MAZE, p -> {
            initMaze(false, (MazePuzzle.MazeData) p.get());
        });
        GuiEventManager.bind(INIT_MANIPULATOR, (obj) -> {
            Manipulator manipulator = (Manipulator) obj.get();
            addActor(manipulator);
            manipulators.add(manipulator);
            Coordinates c = manipulator.getCoordinates();
            manipulator.setPosition(c.x * 128,
                    (getGdxY(c.y)));
        });
        GuiEventManager.bind(INIT_CELL_OVERLAY, (obj) -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][rows - 1 - cell.getY()];
            String overlayData = cell.getOverlayData();

            String path = VariableManager.removeVarPart(overlayData);
            Coordinates c = new Coordinates(VariableManager.getVars(overlayData));
            TextureRegion region = new TextureRegion(TextureCache.getOrCreateR(path),
                    c.x * 128, c.y * 128, 128, 128);
            container.setOverlayTexture(region);
            container.setOverlayRotation(cell.getOverlayRotation());


            container.getBackImage().setDrawable(new TextureRegionDrawable(region));
        });
        GuiEventManager.bind(INTERACTIVE_OBJ_RESET, (p) -> {
            InteractiveObj obj = (InteractiveObj) p.get();
            if (obj.isOff()) {
                //TODO find and disble  light emitter
                GuiEventManager.trigger(RESET_LIGHT_EMITTER, obj);
            }
        });
        GuiEventManager.bind(GuiEventType.ANIMATION_QUEUE_FINISHED, (p) -> {
            resetVisible();
        });
        GuiEventManager.bind(CELL_RESET, obj -> {
            DC_Cell cell = (DC_Cell) obj.get();
            GridCellContainer container = cells[cell.getX()][getGdxY(cell.getY())];
            //overlays
            container.getBackImage().setDrawable(
                    new TextureRegionDrawable(TextureCache.getOrCreateR(cell.getImagePath())));

            container.setOverlayRotation(cell.getOverlayRotation());

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
        GuiEventManager.bind(UNIT_FADE_OUT_AND_BACK, obj -> {
            UnitView unitView = getUnitView((BattleFieldObject) obj.get());
            if (unitView != null) {
                unitView.fadeOut();
            }
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

        GuiEventManager.bind(UNIT_MOVED, obj -> {
            unitMoved((BattleFieldObject) obj.get());
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
                GdxMaster.setDefaultCursor();
                EUtils.playSound(STD_SOUNDS.NEW__CLICK_DISABLED);
                return;
            }
            EUtils.playSound(STD_SOUNDS.NEW__TAB);
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
            GuiEventManager.trigger(SHOW_TARGET_BORDERS, map);
        });

        GuiEventManager.bind(DESTROY_UNIT_MODEL, param -> {
            BattleFieldObject unit = (BattleFieldObject) param.get();
            removeUnitView(unit);
        });


        GuiEventManager.bind(UPDATE_GRAVEYARD, obj -> {
            final Coordinates coordinates = (Coordinates) obj.get();
            cells[coordinates.getX()][rows - 1 - coordinates.getY()].updateGraveyard();
        });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            BattleFieldObject hero = (BattleFieldObject) obj.get();
            DungeonScreen.getInstance().getCameraMan().unitActive(hero);
            AnimConstructor.tryPreconstruct((Unit) hero);
            BaseView view = viewMap.get(hero);
            if (view == null) {
                System.out.println("viewMap not initiatilized at ACTIVE_UNIT_SELECTED!");
                return;
            }

            if (view.getParent() instanceof GridCellContainer) {
                //                ((GridCellContainer) view.getParent()).popupUnitView((GridUnitView) view);
            }

            viewMap.values().stream().forEach(v -> v.setActive(false));
            view.setActive(true);
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(ACTION_PANEL_UPDATE, new PanelActionsDataSource((Unit) hero));
            } else {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(ACTION_PANEL_UPDATE, null);
            }
            if (!firstUpdateDone) {
                DC_Game.game.getVisionMaster().triggerGuiEvents();
                GuiEventManager.trigger(UPDATE_GUI, null);
                GuiEventManager.trigger(UPDATE_SHADOW_MAP);
//                GuiEventManager.trigger(BLACKOUT_OUT, 5f);
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
        GuiEventManager.bind(REMOVE_UNIT_VIEW, p -> {

            getUnitView((BattleFieldObject) p.get()).setVisible(false);
            getUnitView((BattleFieldObject) p.get()).remove();
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

    private GridObject findGridObj(String key, Coordinates c) {
        for (GridObject gridObject : gridObjects) {
            if (gridObject.getKey().equalsIgnoreCase(key)) {
                if (c == null || gridObject.getCoordinates().equals(c)) {
                    return gridObject;
                }
            }
        }
        return null;
    }


    public int getGdxY(int y) {
        return getRows() - 1 - y;
    }

    private void updateHpBar(Object o) {
        HpBarView view = null;
        if (o instanceof BattleFieldObject) {
            if (viewMap.get(o) instanceof GridUnitView)
                view = (HpBarView) viewMap.get(o);
        } else if (o instanceof HpBarView)
            view = (HpBarView) (o);
        if (view != null)
            view.animateHpBarChange();
    }

    private void setVisible(BattleFieldObject sub, boolean visible) {
        BaseView view = viewMap.get(sub);
        //TODO refactor this quick fix!
        if (view == null)
            return;
        if (sub.isWall()) {
            if (!visible) {
                if (view.isVisible()) {
                    return;
                }
            }
        }
        if (view.getParent() instanceof GridCellContainer) {
            ((GridCellContainer) view.getParent()).setDirty(true);

        }

        setVisible(view, visible);
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
            if (view.getColor().a > 0 && view.getColor().a < 1)//            if (view.getActionsOfClass(FadeOutAction.class, false).size > 0)
                return;
        }
        if (!isFadeAnimForUnitsOn()) {
            view.setVisible(visible);
        } else {
            if (visible) {
                view.getColor().a = 0;
                view.setVisible(true);
                ActionMaster.addFadeInAction(view, getFadeDuration(view));
            } else {
//                ActionMaster.checkHasAction(view, AlphaAction.class).if
                if (view.getActionsOfClass(FadeOutAction.class).size > 0) {
                    return;
                }
                view.clearActions();
                ActionMaster.addFadeOutAction(view, getFadeDuration(view));
                ActionMaster.addSetVisibleAfter(view, false);

                if (overlayManager != null)
                    overlayManager.clearTooltip(view.getUserObject());
            }
            if (view instanceof GridUnitView)
                if (((GridUnitView) view).getLastSeenView() != null) {
                    BattleFieldObject obj = getObjectForView(view);

                    LastSeenMaster.resetLastSeen((GridUnitView) view,
                            obj, !visible);
                    if (obj.getLastSeenOutline() == null)
                        (((GridUnitView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> null);
                    else
                        (((GridUnitView) view).getLastSeenView()).setOutlinePathSupplier(
                                () -> (obj.getLastSeenOutline().getImagePath())
                        );

                }
        }
    }

    private float getFadeDuration(BaseView view) {
        if (Cinematics.ON) {
            return 2f;
        }
        return 0.33f;
    }

    private boolean isFadeAnimForUnitsOn() {
        return true;
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }


    private void createUnitsViews(DequeImpl<BattleFieldObject> units) {

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

            final GridCellContainer gridCellContainer = cells[coordinates.getX()]
                    [rows - 1 - coordinates.getY()];
            if (gridCellContainer == null) {
                continue;
            }
            views.forEach(gridCellContainer::addActor);
        }

        shadowMap = new ShadowMap(this);
        addActor(shadowMap);
        wallMap = new WallMap();
        addActor(wallMap);

        GuiEventManager.bind(MOVE_OVERLAYING, obj -> {
            for (OverlayView overlay : overlays) {
                if (overlay.getUserObject() == obj.get()) {
                    Vector2 v = OverlayingMaster.getOffset(overlay.getDirection(),
                            overlay.getUserObject().getDirection());

                    overlay.setDirection(overlay.getUserObject().getDirection());

                    MoveToAction a = ActionMaster.addMoveByAction(overlay, v.x, v.y, 0.5f);
                    a.setInterpolation(Interpolation.circleOut);
                }
            }

        });
        GuiEventManager.bind(SHOW_MODE_ICON, obj -> {
            List list = (List) obj.get();
            UnitView view = (UnitView) getViewMap().get(list.get(0));
            if (view == null) {
                main.system.auxiliary.log.LogMaster.verbose("show mode icons failed " + obj);
                return;
            }
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
                    uv.getInitiativeQueueUnitView().updateInitiative(p.getRight());
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

    public void unitViewMoved(BaseView view) {
        unitMoved(getObjectForView(view));
    }

    public void unitMoved(BattleFieldObject object) {
        int rows1 = rows - 1;
        GridUnitView uv = (GridUnitView) viewMap.get(object);
        if (uv == null) {
            return;
        }
        Coordinates c = object.getCoordinates();
        //        if (!(object instanceof Entrance))
        //            if (c.equals(Eidolons.getMainHero().getCoordinates())) {
        //                if (object != Eidolons.getMainHero()) {
        //                    uv = uv;// it's a trap!!
        //                }
        //            }
        //        uv.setVisible(true);
        try {
            cells[c.x][rows1 - c.y].addActor(uv);
            GuiEventManager.trigger(GuiEventType.UNIT_VIEW_MOVED, uv);
            if (uv.getLastSeenView() != null) {
                if (LastSeenMaster.isUpdateRequired(object))
                    cells[c.x][rows1 - c.y].addActor(uv.getLastSeenView());
            }
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.dev("No cell for view: " + object.getNameAndCoordinate());
        }
        setUpdateRequired(true);
        if (overlayManager != null)
            overlayManager.clearTooltip(object);
    }

    private BaseView createUnitView(BattleFieldObject battleFieldObjectbj) {
        if (viewMap.get(battleFieldObjectbj) != null) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>> UnitView already created!!! " + battleFieldObjectbj);
            return viewMap.get(battleFieldObjectbj);
        }
        GridUnitView view = UnitViewFactory.create(battleFieldObjectbj);
        viewMap.put(battleFieldObjectbj, view);
        if (battleFieldObjectbj.isPlayerCharacter()) {
            if (PaleAspect.ON) {
                mainHeroViewPale = view;
            }
            if (ShadowMaster.isShadowAlive()) {
                mainHeroViewShadow = view;
            }
            mainHeroView = view;
        }

        unitMoved(battleFieldObjectbj);
        if (!

                isVisibleByDefault(battleFieldObjectbj))
            view.setVisible(false);
        GuiEventManager.trigger(GuiEventType.UNIT_VIEW_CREATED, view);
        return view;
    }

    private void addUnitView(BattleFieldObject heroObj) {
        BaseView uv = createUnitView(heroObj);
        unitMoved(heroObj);
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
        if (heroObj.isPlayerCharacter())
            main.system.auxiliary.log.LogMaster.verbose(heroObj + " detached; pos= " + uv.getX() + ":" + uv.getY());

        return true;
    }

    protected BaseView removeUnitView(BattleFieldObject obj) {
        BaseView uv = viewMap.get(obj);
        if (uv == null) {
            LogMaster.log(1, obj + " IS NOT ON UNIT MAP!");
            return null;
        }
//        gridCellContainer.removeActor(uv);
        uv.remove(); //if it was detached...
        uv.setVisible(false);

        if (overlayManager != null)
            overlayManager.clearTooltip(obj);
        LogMaster.log(1, obj + " unit view REMOVED!");
        return uv;
    }


    private boolean isAutoResetVisibleOn() {
        return true;
    }

    private void update() {
        shadowMap.update();

        //update torches?
        updateTorches();
        updateRequired = false;

    }

    private void updateTorches() {
        for (BattleFieldObject object : viewMap.keySet()) {
            if (!(object instanceof Unit)) {
                continue;
            }
            float alpha = DC_Game.game.getVisionMaster().
                    getGammaMaster().getLightEmitterAlpha(object, 0);

            GenericGridView view = (GenericGridView) viewMap.get(object);

            if (Math.abs(view.torch.getBaseAlpha() - alpha) > 0.1f)
                view.torch.setBaseAlpha(alpha);
        }
    }

    private void resetVisible() {
        if (DeathAnim.isOn() && animMaster.getDrawer().isDrawing()) {
            return;
        }

        for (BattleFieldObject sub : viewMap.keySet()) {
            BaseView view = viewMap.get(sub);
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
        List<GridCellContainer> topCells = new ArrayList<>();
        loop:
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                GridCellContainer cell = cells[x][y];
                if (cell == null) {
                    continue;
                }
                cell.setHovered(false);
                List<GenericGridView> views = cell.getUnitViewsVisible();
                if (views.isEmpty()) {
                    cell.setZIndex(0);
                } else {
                    if (!cell.getUserObject().isPlayerHasSeen()) {
                        cell.setZIndex(x + y);
                    } else {
                        cell.setZIndex(square + x + y);
                    }
                    //                TODO     cell.recalcUnitViewBounds();
                    for (GenericGridView sub : views) {
                        if (sub.isHovered() && sub instanceof GridUnitView
                        ) {
                            setHoverObj((GridUnitView) sub);
                            topCells.add(cell);
                            cell.setHovered(true);
                        } else if (
                                sub.getUserObject().isBoss() ||
                                        sub.getUserObject().isPlayerCharacter() || sub.isStackView()) {
                            topCells.add(cell);
                        }
                    }
                }
                //                cell.resetZIndices();
            }
        }
        customOverlayingObjectsUnder.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });

        wallMap.setVisible(WallMap.isOn());
        //        boolean ctrl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT);
        //        shards.setZIndex(Integer.MAX_VALUE);
        wallMap.setZIndex(Integer.MAX_VALUE);


        shadowMap.setZIndex(Integer.MAX_VALUE);
        for (GridCellContainer cell : topCells) {
            cell.setZIndex(Integer.MAX_VALUE);
        }
        customOverlayingObjects.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });
        customOverlayingObjectsTop.forEach(obj -> {
            obj.setZIndex(Integer.MAX_VALUE);
        });
        manipulators.forEach(manipulator -> {
            manipulator.setZIndex(Integer.MAX_VALUE);
        });

//        if (mainHeroViewPale!=null) {
//            mainHeroViewPale.getParent().setZIndex(Integer.MAX_VALUE);
//        }
//        if (mainHeroViewShadow!=null) {
//            mainHeroViewPale.getParent().setZIndex(Integer.MAX_VALUE);
//        }
//        if (mainHeroView!=null) {
//            mainHeroView.getParent().setZIndex(Integer.MAX_VALUE);
//        }
        overlays.forEach(overlayView -> overlayView.setZIndex(Integer.MAX_VALUE));

        overlayManager.setZIndex(Integer.MAX_VALUE);


        animMaster.setZIndex(Integer.MAX_VALUE);
    }

    public void addOverlay(OverlayView view) {
        int width = (int) (GridMaster.CELL_W * view.getScale());
        int height = (int) (GridMaster.CELL_H * view.getScale());
        Dimension dimension = OverlayingMaster.getOffsetsForOverlaying(view.getDirection(), width, height, view);
        float calcXOffset = view.getX();
        float calcYOffset = view.getY();

        view.setBounds((float) dimension.getWidth() + calcXOffset
                , (float) dimension.getHeight() + calcYOffset
                , width, height);
        view.setOffsetX(dimension.getWidth());
        view.setOffsetY(dimension.getHeight());
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

    public GridUnitView getMainHeroView() {
        if (PaleAspect.ON) {
            return mainHeroViewPale;
        }
        if (ShadowMaster.isShadowAlive()) {
            return mainHeroViewShadow;
        }
        return mainHeroView;
    }

    public BattleFieldObject getObjectForView(BaseView source) {
        return (BattleFieldObject) source.getUserObject(); //TODO igg demo fix
//        return new MapMaster<BattleFieldObject, BaseView>()
//         .getKeyForValue(viewMap, source);
    }

    public void clearSelection() {
        GuiEventManager.trigger(TARGET_SELECTION, null);
    }

    public List<GroupX> getCustomOverlayingObjects() {
        return customOverlayingObjects;
    }

    public GridManager getGridManager() {
        return manager;

    }

    public DC_Cell getCell(int i, int i1) {
        try {
            return cells[i][i1].getUserObject();
        } catch (Exception e) {
        }
        return null;
    }

    public List<GroupX> getActiveCommentSprites() {
        return activeCommentSprites;
    }

    public List<GroupX> getCommentSprites() {
        return commentSprites;
    }

    public void setCommentSprites(List<GroupX> commentSprites) {
        this.commentSprites = commentSprites;
    }
}
