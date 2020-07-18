package main.level_editor.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.GridCreateData;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.screens.dungeon.GenericDungeonScreen;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.GridStage;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.struct.level.LE_Floor;
import main.level_editor.gui.grid.LE_BfGrid;
import main.level_editor.gui.grid.LE_GridCell;
import main.level_editor.gui.stage.LE_GuiStage;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LE_Screen extends GenericDungeonScreen {

    private static final Map<LE_Floor, Supplier<ScreenWithLoader>> cached = new HashMap();
    private static LE_Screen instance;
    private LE_Floor floor;
    private LE_InputController processor;
    private InputMultiplexer multiplexer;
    private String floorName;

    public static Supplier<ScreenWithLoader> getScreen(LE_Floor parameter) {
        Supplier<ScreenWithLoader> supplier = cached.get(parameter);
        if (supplier != null) {
            return supplier;
        }
        ScreenWithLoader screen = new LE_Screen();
        cached.put(parameter, supplier = () -> screen);
        return supplier;
    }

    @Override
    public void moduleEntered(Module module, DequeImpl<BattleFieldObject> objects) {
        super.moduleEntered(module, objects);
    }

    public static LE_Screen getInstance() {
        return instance;
    }

    public static Map<LE_Floor, Supplier<ScreenWithLoader>> getCache() {
        return cached;
    }

    @Override
    protected void preLoad() {
        instance = this;
        if (floor != null) {
            return;
        }
        floor = (LE_Floor) data.getParameter();

        GuiEventManager.bind(GuiEventType.LE_FLOORS_TABS, p -> {
            LE_GridCell.hoveredCell=null; //TODO refactor?

            main.system.auxiliary.log.LogMaster.log(1,"Tabs for " +floorName);
                ((LE_GuiStage) guiStage).getFloorTabs().removeAll();
                List<LE_Floor> floors = (List<LE_Floor>) p.get();
                for (LE_Floor le_floor : floors) {
                    ((LE_GuiStage) guiStage).getFloorTabs().addTab(le_floor);
//                ((LE_GuiStage) guiStage).getFloorTabs().setActiveTab((LE_Floor) data.getParameter());
                }
        });
        WaitMaster.unmarkAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);

        gridStage = new GridStage(viewPort, getBatch()) {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return super.touchUp(screenX, screenY, pointer, button);
            }
        };
        guiStage = createGuiStage(); //separate batch for PP

        initGl();
        preBindEvent();

        EmitterPools.preloadDefaultEmitters();
    }

    @Override
    protected GenericGuiStage createGuiStage() {
        return new LE_GuiStage(null, getBatch());
    }

    @Override
    protected boolean isWaitForInputSupported() {
        return false;
    }

    @Override
    public void updateInputController() {
        if (getCam() != null) {
            GdxMaster.setInputProcessor(getMultiplexer());
        }
    }

    @Override
    protected GridPanel createGrid(GridCreateData param) {
        return new LE_BfGrid(param.getCols(), param.getRows(), param.getModuleWidth(),
                param.getModuleHeight());
    }

    @Override
    protected InputProcessor createInputController() {
        if (processor == null) {
            processor = new LE_InputController(getCamera(), floor);
        }
        return processor;
    }

    @Override
    protected void afterLoad() {
        super.afterLoad();
        if (floorName == null) {
            final GridCreateData param = ((GridCreateData) data.getParams().get());
            createAndInitModuleGrid(param);
            particleManager = new ParticleManager();
            gridStage.addActor(particleManager);
            this.floorName = param.getName();
            controller = (InputController) createInputController();
            GuiEventManager.trigger(GuiEventType.LE_FLOOR_LOADED, param.getName());
        }

        GuiEventManager.trigger(GuiEventType.LE_FLOORS_TABS , FloorManager.getFloors());
    }

    @Override
    public void render(float delta) {
        if (floor.getGame()!= DC_Game.game) {
            return; //new floor being loaded
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        if (cameraMan == null) {
            drawBg(delta);
            return;
        }
        cameraMan.act(delta);
        gridStage.act(delta);
        guiStage.act(delta);
//        getBatch().setColor(1,1,1,0.5f);
        drawBg(delta);
//        getBatch().drawBlack(0.5f, false);
//        getBatch().draw(TextureCache.getOrCreateR());
//        getBatch().setColor(1,1,1,1f);
        gridStage.draw();
        guiStage.draw();
        Gdx.input.setInputProcessor(getMultiplexer());
        renderMeta();
    }

    public InputMultiplexer getMultiplexer() {
        if (multiplexer == null)
            multiplexer = new InputMultiplexer(createInputController(), guiStage, gridStage);
        return multiplexer;
    }

    @Override
    protected void doBlackout() {
    }

    @Override
    public LE_GuiStage getGuiStage() {
        return (LE_GuiStage) guiStage;
    }

    @Override
    public GridPanel getGridPanel() {
        return gridPanel;
    }
}
