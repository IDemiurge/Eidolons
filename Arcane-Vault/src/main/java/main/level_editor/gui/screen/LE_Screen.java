package main.level_editor.gui.screen;

import com.badlogic.gdx.InputProcessor;
import eidolons.libgdx.bf.BFDataCreatedEvent;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.screens.GenericDungeonScreen;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.stage.GenericGuiStage;
import eidolons.libgdx.stage.GuiStage;
import main.content.ValueMap;
import main.level_editor.gui.grid.LE_BfGrid;
import main.level_editor.gui.stage.LE_GuiStage;
import main.level_editor.struct.level.Floor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LE_Screen extends GenericDungeonScreen {

    private static Map<Floor, Supplier<ScreenWithLoader>> cached = new HashMap();
    LE_Screen instance;
    Floor floor;

    public static Supplier<ScreenWithLoader> getScreen(Floor parameter) {
        Supplier<ScreenWithLoader> supplier = cached.get(parameter);
        if (supplier != null) {
            return supplier;
        }
        ScreenWithLoader screen= new LE_Screen();
        cached.put(parameter, supplier=()-> screen);
        return supplier;
    }

    @Override
    protected void preLoad() {
        floor = (Floor) data.getParameter();
        super.preLoad();
    }

    @Override
    protected GenericGuiStage createGuiStage() {
        return new LE_GuiStage(null, null);
    }

    @Override
    protected GridPanel createGrid(BFDataCreatedEvent param) {
        return new LE_BfGrid(param.getGridW(), param.getGridH());
    }

    @Override
    protected InputProcessor createInputController() {
        return new LE_InputProcessor(floor.getManager());
    }
}
