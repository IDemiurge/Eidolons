package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.OutlineMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.cell.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.GridObject;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObj;
import eidolons.game.netherflame.igg.death.ShadowMaster;
import eidolons.game.netherflame.igg.pale.PaleAspect;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.libgdx.bf.Borderable;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.TargetRunnable;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.grid.cell.*;
import eidolons.libgdx.bf.overlays.GridOverlaysManager;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.dc.actionpanel.datasource.PanelActionsDataSource;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.text.HelpMaster;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static main.system.GuiEventType.*;

public class DC_GridPanel extends GridPanel {

    private GridUnitView mainHeroViewShadow;
    private GridUnitView mainHeroViewPale;
    protected GridUnitView mainHeroView;
    protected AnimMaster animMaster;

    private float resetTimer;
    private float autoResetVisibleOnInterval = 0.5f;

    private List<GroupX> commentSprites = new ArrayList<>(5);
    private List<GroupX> activeCommentSprites = new ArrayList<>(3);

    private boolean updateRequired;
    private boolean firstUpdateDone;
    private boolean welcomeInfoShown;


    public DC_GridPanel(int paramCols, int paramRows, int cols, int rows) {
        super(paramCols, paramRows, cols, rows);
    }


    @Override
    public GridPanel initFullGrid() {
        return super.initFullGrid();
    }

    @Override
    public GridPanel initObjects(DequeImpl<BattleFieldObject> objects) {

        super.initObjects(objects);
        addActor(animMaster = AnimMaster.getInstance());
        animMaster.bindEvents();
        manager = new GridRenderHelper(this);
        addActor(overlayManager);

        return this;
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
            if (isShowGridEmitters())
                if (isDrawEmittersOnTop())
                    drawEmitters(batch);
            drawComments(batch);
            animMaster.setVisible(true);
            animMaster.draw(batch, 1f);
        } else
            ShaderDrawer.drawWithCustomShader(this, batch,
                    paused ? GrayscaleShader.getGrayscaleShader() : null, true);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (updateRequired) {
            resetZIndices();
            update();
        }

        if (isAutoResetVisibleOn())
            if (DC_Game.game != null)
                if (DC_Game.game.getVisionMaster().getVisible() != null) {
                    if (resetTimer <= 0) {
                        resetTimer = autoResetVisibleOnInterval;
                        BattleFieldObject[] visible = DC_Game.game.getVisionMaster().getVisible();
                        for (int i = 0, visibleLength = visible.length; i < visibleLength; i++) {
                            BattleFieldObject sub = visible[i];
                            setVisible(viewMap.get(sub), true);
                            GridMaster.validateVisibleUnitView(viewMap.get(sub));
                            if (sub.isPlayerCharacter()) {
                                if (ExplorationMaster.isExplorationOn()) {
                                    GridUnitView view = (GridUnitView) viewMap.get(sub);
                                    view.resetHpBar();
                                }
                            }
                        }
                        BattleFieldObject[] invisible = DC_Game.game.getVisionMaster().getInvisible();
                        for (int i = 0, invisibleLength = invisible.length; i < invisibleLength; i++) {
                            BattleFieldObject sub = invisible[i];
                            setVisible(viewMap.get(sub), false);
                        }
                    }
                    resetTimer -= delta;
                }
    }

    @Override
    protected BaseView createUnitView(BattleFieldObject battleFieldObjectbj) {
        BaseView view = super.createUnitView(battleFieldObjectbj);
        if (battleFieldObjectbj.isPlayerCharacter()) {
            if (PaleAspect.ON) {
                mainHeroViewPale = (GridUnitView) view;
            }
            if (ShadowMaster.isShadowAlive()) {
                mainHeroViewShadow = (GridUnitView) view;
            }
            mainHeroView = (GridUnitView) view;
        }
        return view;
    }

    @Override
    public void unitMoved(BattleFieldObject object) {
        super.unitMoved(object);

        setUpdateRequired(true);
    }

    @Override
    public void resetZIndices() {
        super.resetZIndices();
        animMaster.setZIndex(Integer.MAX_VALUE);
    }

    protected GridOverlaysManager createOverlays() {
        return new GridOverlaysManager(this);
    }

    private void drawComments(Batch batch) {
        for (GroupX commentSprite : getCommentSprites()) {
            commentSprite.setVisible(true);
            commentSprite.draw(batch, 1f);
        }
    }

    public void updateOutlines() {

        Object[] array = viewMap.keySet().toArray();
        for (int i = 0; i < array.length; i++) {
            BattleFieldObject obj = (BattleFieldObject) array[i];
            if (!obj.isOverlaying())
                if (!obj.isMine())
                    if (!obj.isWall()) {
                        OUTLINE_TYPE outline = obj.getOutlineType();
                        GridUnitView uv = (GridUnitView) viewMap.get(obj);

                        TextureRegion texture = null;
                        if (outline != null) {
                            String path = Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj);
                            if (obj instanceof Unit) {
                                LogMaster.log(1, obj + " has OUTLINE: " + path);
                            }
                            texture = TextureCache.getOrCreateR(path);
                            uv.setOutline(texture);
                        } else {
                            if (obj instanceof Unit) {
                                if (!obj.isOutsideCombat()) {
                                    LogMaster.log(1, obj + " has NO OUTLINE: ");
                                }
                            }
                            uv.setOutline(null);
                        }
                    }
        }
    }

    protected boolean isShardsOn() {
        if (CoreEngine.TEST_LAUNCH) {
            return false;
        }
        if (EidolonsGame.FOOTAGE) {
            return true;
        }
        if (EidolonsGame.BRIDGE) {
            return true;
        }
        if (EidolonsGame.BOSS_FIGHT) {
            return false;
        }
        return !CoreEngine.isLiteLaunch();

//        if (DC_Game.game.getDungeonMaster().getFloorWrapper() != null) {
//            switch (DC_Game.game.getDungeonMaster().getFloorWrapper().getLocationType().getGroup()) {
//                case NATURAL:
//                case AVERAGE:
//                case SURFACE:
//                    return true;
//            }
//        }
    }


    private void initMaze(boolean hide, MazePuzzle.MazeData data) {
        Coordinates c = null;
        for (Coordinates coordinates : data.mazeWalls) {
            c = data.c.getOffset(coordinates.negativeY());
            GridCellContainer container = cells[c.getX()][(c.getY())];
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

    protected void bindEvents() {
        super.bindEvents();

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
        GuiEventManager.bind(VALUE_MOD, p -> {
            FloatingTextMaster.getInstance().
                    createAndShowParamModText(p.get());
        });
        GuiEventManager.bind(ACTOR_SPEAKS, p -> {
            if (p.get() == null) {
                return;
            }
            BattleFieldObject unit = (BattleFieldObject) p.get();
            UnitView view = getUnitView(unit);
            main.system.auxiliary.log.LogMaster.dev("ACTOR_SPEAKS: " + unit);

            unit.getGame().getManager().setHighlightedObj(unit);

//            for (BaseView value : viewMap.values()) {
//                if (value==view) {

//                    GuiEventManager.trigger(GuiEventType.SCALE_UP_VIEW, view);
            view.highlight();
            GraphicData data = new GraphicData("alpha::0.8f");
            gridViewAnimator.animate(view, GridViewAnimator.VIEW_ANIM.screen, data);
            WaitMaster.doAfterWait(4000, () -> {
//                        if (!DialogueManager.isRunning())
                {
                    main.system.auxiliary.log.LogMaster.dev("hl off: " + unit);
                    view.highlightOff();
                    unit.getGame().getManager().setHighlightedObj(null);
                }
            });
//                } else
//                {
//                    value.highlightOff();
//                    GuiEventManager.trigger(GRID_OBJ_HOVER_OFF, view);
//                }
//            }
        });
        GuiEventManager.bind(GuiEventType.GRID_OBJ_ANIM, p -> {
            List list = (List) p.get();
            GraphicData data = (GraphicData) list.get(2);
            if (list.get(1) instanceof Coordinates) {
                String key = (String) list.get(0);
                Coordinates c = (Coordinates) list.get(1);
                GridObject gridObj = findGridObj(key, c);
                gridViewAnimator.animate(gridObj, data);
            } else {
                gridViewAnimator.animate(viewMap.get(list.get(1)),
                        (GridViewAnimator.VIEW_ANIM) list.get(0), data);
            }

        });

        GuiEventManager.bind(HIDE_MAZE, p -> {
            initMaze(true, (MazePuzzle.MazeData) p.get());
        });
        GuiEventManager.bind(SHOW_MAZE, p -> {
            initMaze(false, (MazePuzzle.MazeData) p.get());
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
        GuiEventManager.bind(UPDATE_GUI, obj -> {
            if (!VisionHelper.isVisionHacked())
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
                    b = cells[obj1.getX()][(obj1.getY())];
                }

                if (((Group) b).getUserObject() instanceof Unit) {
                    final GridUnitView gridView = (GridUnitView) b;
                    final UnitView unitView = gridView.getInitiativeQueueUnitView();
                    if (unitView != null) {
                        map.put(unitView, () -> p.getRight().run(obj1));
                    }
                }

                map.put(b, () -> p.getRight().run(obj1));
            }
            GuiEventManager.trigger(SHOW_TARGET_BORDERS, map);
        });

        GuiEventManager.bind(UPDATE_GRAVEYARD, obj -> {
            final Coordinates coordinates = (Coordinates) obj.get();
            cells[coordinates.getX()][(coordinates.getY())].updateGraveyard();
        });


        GuiEventManager.bind(ACTIVE_UNIT_SELECTED, obj -> {
            BattleFieldObject hero = (BattleFieldObject) obj.get();
            DungeonScreen.getInstance().getCameraMan().unitActive(hero);
            //dc refactor
//            AnimConstructor.tryPreconstruct((Unit) hero);
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
        HpBarView view = null;
        if (o instanceof BattleFieldObject) {
            if (viewMap.get(o) instanceof GridUnitView)
                view = (HpBarView) viewMap.get(o);
        } else if (o instanceof HpBarView)
            view = (HpBarView) (o);
        if (view != null)
            view.animateHpBarChange();
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }


    public boolean detachUnitView(Unit heroObj) {
        BaseView uv = viewMap.get(heroObj);
        if (!(uv.getParent() instanceof GridCellContainer))
            return false;
        if (!uv.isVisible())
            return false;

        GridCellContainer gridCellContainer = (GridCellContainer) uv.getParent();
        float x = uv.getX() + gridCellContainer.getX();
        float y = uv.getY() + gridCellContainer.getY();
        gridCellContainer.removeActor(uv);
        uv.clearActions();
        ActionMaster.addFadeOutAction(uv, 0.5f, false);
        addActor(uv);
        uv.setPosition(x, y);
        if (heroObj.isPlayerCharacter())
            main.system.auxiliary.log.LogMaster.verbose(heroObj + " detached; pos= " + uv.getX() + ":" + uv.getY());

        return true;
    }


    private boolean isAutoResetVisibleOn() {
        return true;
    }

    protected void update() {
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

    public GridUnitView getMainHeroView() {
        if (PaleAspect.ON) {
            return mainHeroViewPale;
        }
        if (ShadowMaster.isShadowAlive()) {
            return mainHeroViewShadow;
        }
        return mainHeroView;
    }

    public void clearSelection() {
        GuiEventManager.trigger(TARGET_SELECTION, null);
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
