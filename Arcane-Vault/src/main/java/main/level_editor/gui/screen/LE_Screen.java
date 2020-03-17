package main.level_editor.gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL30;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.screens.dungeon.GenericDungeonScreen;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.StageX;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.grid.LE_BfGrid;
import main.level_editor.gui.stage.LE_GuiStage;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LE_Screen extends GenericDungeonScreen {

    private static Map<Floor, Supplier<ScreenWithLoader>> cached = new HashMap();
    static LE_Screen instance;
    Floor floor;
    private LE_InputProcessor processor;
    private InputMultiplexer multiplexer;

    public static Supplier<ScreenWithLoader> getScreen(Floor parameter) {
        Supplier<ScreenWithLoader> supplier = cached.get(parameter);
        if (supplier != null) {
            return supplier;
        }
        ScreenWithLoader screen = new LE_Screen();
        cached.put(parameter, supplier = () -> screen);
        return supplier;
    }

    public static LE_Screen getInstance() {
        return instance;
    }


    @Override
    protected void preLoad() {
        instance = this;
        WaitMaster.unmarkAsComplete(WaitMaster.WAIT_OPERATIONS.GUI_READY);
        floor = (Floor) data.getParameter();

        gridStage = new StageX(viewPort, getBatch()){
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
    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    public void updateInputController() {
        if (guiStage == null) {
            main.system.auxiliary.log.LogMaster.log(1, "NULL GUI! ");
        } else
            main.system.auxiliary.log.LogMaster.log(1, "updateInputController for GUI! ");
        GdxMaster.setInputProcessor(getMultiplexer());
    }

    @Override
    protected GridPanel createGrid(BFDataCreatedEvent param) {
        return new LE_BfGrid(param.getCols(), param.getRows());
    }

    @Override
    protected InputProcessor createInputController() {
        if (processor == null) {
            processor = new LE_InputProcessor(getCamera(), floor);
        }
        return processor;
    }

    @Override
    protected void afterLoad() {
        super.afterLoad();
        particleManager = new ParticleManager();
        gridStage.addActor(particleManager);

        controller = (InputController) createInputController();
    }

    @Override
    public void render(float delta) {
//        super.render(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        cameraMan.act(delta);
        gridStage.act(delta);
        guiStage.act(delta);
        drawBg(delta);
        gridStage.draw();
        guiStage.draw();
        Gdx.input.setInputProcessor(getMultiplexer());
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
