package main.level_editor.gui.screen;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.mouse.DungeonInputController;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.particles.EmitterPools;
import eidolons.libgdx.particles.ambi.ParticleManager;
import eidolons.libgdx.screens.GenericDungeonScreen;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.StageX;
import main.level_editor.gui.grid.LE_BfGrid;
import main.level_editor.gui.stage.LE_GuiStage;
import main.level_editor.struct.level.Floor;
import main.system.threading.WaitMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LE_Screen extends GenericDungeonScreen {

    private static Map<Floor, Supplier<ScreenWithLoader>> cached = new HashMap();
    static LE_Screen instance;
    Floor floor;
    private LE_InputProcessor processor;

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

        gridStage = new StageX(viewPort, getBatch());
        guiStage = createGuiStage(); //separate batch for PP

        initGl();
        preBindEvent();

        EmitterPools.preloadDefaultEmitters();
    }

    @Override
    protected GenericGuiStage createGuiStage() {
        return new LE_GuiStage(null, null);
    }

    @Override
    protected boolean isWaitForInput() {
        return false;
    }

    @Override
    public void updateInputController() {
        GdxMaster.setInputProcessor(
                new InputMultiplexer( createInputController(), gridStage, guiStage ));
    }

    @Override
    protected GridPanel createGrid(BFDataCreatedEvent param) {
        return new LE_BfGrid(param.getCols(), param.getRows());
    }

    @Override
    protected InputProcessor createInputController() {
        if (processor==null) {
            processor= new LE_InputProcessor(getCamera(), floor);
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
        super.render(delta);
        cameraMan.act(delta);
        gridStage.act(delta);
        guiStage.act(delta);
        batch.begin();
        drawBg(delta);
        batch.end();
        gridStage.draw();
//        guiStage.draw();
    }

    @Override
    protected void doBlackout() {
    }

    @Override
    public GenericGuiStage getGuiStage() {
        return guiStage;
    }

    @Override
    public GridPanel getGridPanel() {
        return gridPanel;
    }
}
