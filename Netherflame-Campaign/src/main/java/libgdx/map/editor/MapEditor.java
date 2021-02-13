package libgdx.map.editor;

import com.badlogic.gdx.Gdx;
import eidolons.content.consts.VisualEnums;
import libgdx.launch.GenericLauncher;
import eidolons.system.libgdx.datasource.ScreenData;
import main.data.xml.XML_Reader;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 2/10/2018.
 */
public class MapEditor extends GenericLauncher {

    private static final String DEFAULT = "Mistfall";
    private static final String microForMacro =
     "party;scenarios;dungeons;factions;chars;";

    public static void launch() {
        launch(DEFAULT);
    }

    public static void launch(String arg) {
        Flags.setMapEditor(true);
        CoreEngine.setSelectivelyReadTypes(microForMacro);
        XML_Reader.readTypes(false);
        //custom options?!
        new MapEditor().start();
//        MacroManager.newAdventureGame(arg);

        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        Gdx.app.postRunnable(() -> GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         new ScreenData(VisualEnums.SCREEN_TYPE.MAP, arg)));
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        XML_Reader.readTypes(true);
        if (args.length > 0)
            launch(args[0]);
        else launch(null);
    }

    @Override
    public String getOptionsPath() {
        return null;
    }

    //    protected void screenInit() {
//        //selection panel for campaign/scenario to edit!
//        ScreenData data = new ScreenData(ScreenType.MAP, "Editor");
//        screenSwitcher(new EventCallbackParam(data));
//    }

}
