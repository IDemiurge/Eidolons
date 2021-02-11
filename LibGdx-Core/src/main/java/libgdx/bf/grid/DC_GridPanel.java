package libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.OutlineMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazePuzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid.DefVoidHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid.VoidHandler;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.InteractiveObj;
import eidolons.game.netherflame.boss.anims.generic.BossVisual;
import eidolons.game.netherflame.main.death.ShadowMaster;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMaster;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.bf.Borderable;
import libgdx.bf.GridMaster;
import libgdx.bf.TargetRunnable;
import libgdx.bf.decor.shard.ShardVisuals;
import libgdx.bf.grid.cell.*;
import libgdx.bf.grid.moving.PlatformCell;
import libgdx.bf.overlays.GridOverlaysManager;
import libgdx.gui.generic.GroupX;
import libgdx.gui.panels.headquarters.HqPanel;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.shaders.GrayscaleShader;
import libgdx.shaders.ShaderDrawer;
import eidolons.system.text.HelpMaster;
import libgdx.bf.grid.cell.*;
import libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static main.system.GuiEventType.*;

public class DC_GridPanel extends GridPanel {

    protected UnitGridView mainHeroView;
    protected AnimMaster animMaster;
    private final VoidHandler voidHandler;
    private VoidHandler customVoidHandler;

    private float resetTimer;

    private List<GroupX> commentSprites = new ArrayList<>(5);
    private final List<GroupX> activeCommentSprites = new ArrayList<>(3);

    private boolean updateRequired;
    private boolean firstUpdateDone;
    private boolean welcomeInfoShown;

    public DC_GridPanel(int paramCols, int paramRows, int cols, int rows) {
        super(paramCols, paramRows, cols, rows);
        voidHandler = new DefVoidHandler(this);
    }

    @Override
    public GridCellContainer getGridCell(int x, int y) {
        for (PlatformCell platform : platforms) {
            if (platform.getGridX() == x) {
                if (platform.getGridY() == y)
                    return platform;
            }
        }
        return super.getGridCell(x, y);
    }


    @Override
    public GridPanel initObjects(DequeImpl<BattleFieldObject> objects) {

        super.initObjects(objects);
        addActor(animMaster = AnimMaster.getInstance());
        animMaster.bindEvents();
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
            // if (isShowGridEmitters()) already done
            //     if (isDrawEmittersOnTop())
            //         drawEmitters(batch);
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
        if (!DC_Game.game.isPaused()) {
            getPlatformHandler().act(delta);
            if (customVoidHandler != null) {
                customVoidHandler.act(delta);
            } else {
                voidHandler.act(delta);
            }
        }
        if (updateRequired) {
            resetZIndices();
            update();
        }

        if (isAutoResetVisibleOn())
            if (DC_Game.game != null)
                if (DC_Game.game.getVisionMaster().getVisible() != null) {
                    if (resetTimer <= 0) {
                        float autoResetVisibleOnInterval = 0.5f;
                        resetTimer = autoResetVisibleOnInterval;
                        BattleFieldObject[] visible = DC_Game.game.getVisionMaster().getVisible();
                        for (int i = 0, visibleLength = visible.length; i < visibleLength; i++) {
                            BattleFieldObject sub = visible[i];
                            setVisible(viewMap.get(sub), true);
                            // if (!isCustomDraw())
                            GridMaster.validateVisibleUnitView(viewMap.get(sub));
                            if (sub.isPlayerCharacter()) {
                                if (ExplorationMaster.isExplorationOn()) {
                                    UnitGridView view = (UnitGridView) viewMap.get(sub);
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
            mainHeroView = (UnitGridView) view;
        }
        return view;
    }

    @Override
    public void unitMoved(BattleFieldObject object) {
        super.unitMoved(object);

        setUpdateRequired(true);
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
        for (Obj key : viewMap.keys()) {
            BattleFieldObject obj = (BattleFieldObject) key;
            if (!obj.isOverlaying())
                if (!obj.isMine())
                    if (!obj.isWall()) {
                        OUTLINE_TYPE outline = obj.getOutlineType();
                        UnitGridView uv = (UnitGridView) viewMap.get(obj);

                        TextureRegion texture;
                        if (outline != null) {
                            String path = Eidolons.game.getVisionMaster().getVisibilityMaster().getImagePath(outline, obj);
                            if (obj instanceof Unit) {
                                LogMaster.log(1, obj + " has OUTLINE: " + path);
                            }
                            texture = TextureCache.getRegionUV(path);
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
        if (ShardVisuals.TEST_MODE) {
            return true;
        }
        if (!CoreEngine.TEST_LAUNCH) {
            return true;
        }
        if (EidolonsGame.FOOTAGE) {
            return true;
        }
        return !Flags.isLiteLaunch();
    }


    private void initMaze(boolean hide, MazePuzzle.MazeData data) {
        Coordinates c;
        if (data.mazeTypeAlt != MazePuzzle.MazeType.NONE) {
            if (data.mazeMarksAlt != null)
                for (Coordinates coordinates : data.mazeMarksAlt) {
                    c = data.c.getOffset(coordinates);
                    GridCellContainer container = cells[c.getX()][(c.getY())];
                    container.setOverlayTexture(
                            hide ? null : TextureCache.getRegionUV(data.mazeTypeAlt.getImagePath()));
                }
        }
        for (Coordinates coordinates : data.mazeMarks) {
            c = data.c.getOffset(coordinates);
            GridCellContainer container = cells[c.getX()][(c.getY())];
            container.setOverlayTexture(
                    hide ? null : TextureCache.getRegionUV(data.mazeType.getImagePath()));
        }
        // gridViewAnimator.animate(container,
        //         GridViewAnimator.VIEW_ANIM.screen, new GraphicData("alpha:0.6f;dur:1f"));
    }

    protected void bindEvents() {
        super.bindEvents();


        GuiEventManager.bind(SHOW_MODE_ICON, obj -> {
            List list = (List) obj.get();
            UnitView view = getUnitView((BattleFieldObject) list.get(0));
            if (view == null) {
                main.system.auxiliary.log.LogMaster.verbose("show mode icons failed " + obj);
                return;
            }
            view.updateModeImage((String) list.get(1));
        });
        if (DC_Engine.isAtbMode()) {
            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Pair<Integer, Float>> p = (Pair<Unit, Pair<Integer, Float>>) obj.get();
                UnitGridView uv = (UnitGridView) viewMap.get(p.getLeft());
                QueueView initiativeQueueUnitView = null;
                if (uv == null) {
                    for (BossVisual bossVisual : bossVisuals) {
                        if (bossVisual.getUnit() == p.getLeft()) {
                            initiativeQueueUnitView = bossVisual.getQueueView();
                            break;
                        }
                    }
                    // addUnitView(p.getLeft());
                    // uv = (UnitGridView) viewMap.get(p.getLeft());
                } else {
                    initiativeQueueUnitView = uv.getInitiativeQueueUnitView();
                }

                initiativeQueueUnitView.setTimeTillTurn(p.getRight().getRight());
                initiativeQueueUnitView.updateInitiative(p.getRight().getLeft());
            });
        } else
            GuiEventManager.bind(INITIATIVE_CHANGED, obj -> {
                Pair<Unit, Integer> p = (Pair<Unit, Integer>) obj.get();
                UnitGridView uv = (UnitGridView) viewMap.get(p.getLeft());
                if (uv == null) {
                    addUnitView(p.getLeft());
                    uv = (UnitGridView) viewMap.get(p.getLeft());
                }
                if (uv != null)
                    uv.getInitiativeQueueUnitView().updateInitiative(p.getRight());
            });
        GuiEventManager.bind(VALUE_MOD, p -> {
            FloatingTextMaster.getInstance().
                    createAndShowParamModText(p.get());
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
            Pair<Set<BattleFieldObject>, TargetRunnable> p =
                    (Pair<Set<BattleFieldObject>, TargetRunnable>) obj.get();
            if (p.getLeft().isEmpty()) {
                FloatingTextMaster.getInstance().createFloatingText(FloatingTextMaster.TEXT_CASES.REQUIREMENT,
                        "No targets available!",
                        Eidolons.getGame().getManager().getControlledObj());
                GdxMaster.setDefaultCursor();
                EUtils.playSound(AudioEnums.STD_SOUNDS.NEW__CLICK_DISABLED);
                return;
            }
            EUtils.playSound(AudioEnums.STD_SOUNDS.NEW__TAB);
            ObjectMap<Borderable, Runnable> map = new ObjectMap<>();

            for (DC_Obj obj1 : p.getLeft()) {
                Borderable b = viewMap.get(obj1);
                if (b == null) {
                    b = cells[obj1.getX()][(obj1.getY())];
                }

                if (((Group) b).getUserObject() instanceof Unit) {
                    final UnitGridView gridView = (UnitGridView) b;
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
            if (DungeonScreen.getInstance().getCameraMan() == null) {
                return;
            }
            DungeonScreen.getInstance().getCameraMan().unitActive(hero);
            //gdx Review
            //            AnimConstructor.tryPreconstruct((Unit) hero);
            BaseView view = viewMap.get(hero);
            if (view == null) {
                System.out.println("No view for ACTIVE_UNIT_SELECTED!");
                return;
            }

            for (BaseView v : viewMap.values()) {
                v.setActive(false);
            }
            view.setActive(true);
            if (hero.isMine()) {
                GuiEventManager.trigger(SHOW_TEAM_COLOR_BORDER, view);
                GuiEventManager.trigger(ACTION_PANEL_UPDATE);
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
            // Tester check - when to welcome?
            if (HelpMaster.isDefaultTextOn())
                if (!welcomeInfoShown) {
                    new Thread(() -> {
                        WaitMaster.WAIT(2000);
                        GuiEventManager.trigger(SHOW_TEXT_CENTERED, HelpMaster.getWelcomeText());
                    }, " thread").start();
                    welcomeInfoShown = true;
                }
        });

        GuiEventManager.bind(GuiEventType.HP_BAR_UPDATE, p -> {
            updateHpBar((BattleFieldObject) p.get());
        });

    }


    private void updateHpBar(BattleFieldObject o) {
        HpBarView view = null;
        if (o != null) {
            if (viewMap.get(o) instanceof UnitGridView)
                view = (HpBarView) viewMap.get(o);
        } else if (o instanceof HpBarView)
            view = (HpBarView) (o);
        if (view != null)
            view.animateHpBarChange();
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
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
        updateTorches();
        updateRequired = false;
    }

    //Light revamp - this could do with box2d
    private void updateTorches() {
        for (Obj object : viewMap.keys()) {
            if (!(object instanceof Unit)) {
                continue;
            }
            float alpha = 0; //TODO
            GenericGridView view = (GenericGridView) viewMap.get(object);

            if (Math.abs(view.torch.getBaseAlpha() - alpha) > 0.1f)
                view.torch.setBaseAlpha(alpha);
        }
    }


    public VoidHandler getVoidHandler() {
        return voidHandler;
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

    public void setCustomVoidHandler(VoidHandler customVoidHandler) {
        this.customVoidHandler = customVoidHandler;
    }
}
