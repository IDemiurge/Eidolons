package main.libgdx.screens.map.editor;

import com.badlogic.gdx.Gdx;
import main.data.xml.XML_Reader;
import main.game.battlecraft.DC_Engine;
import main.libgdx.launch.GenericLauncher;
import main.libgdx.screens.ScreenData;
import main.libgdx.screens.ScreenType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

/**
 * Created by JustMe on 2/10/2018.
 */
public class MapEditor extends GenericLauncher {

    private static final String DEFAULT = "Mistfall";
    private static final String microForMacro =
     "party;scenarios;dungeons;factions;";

    public static void launch() {
        launch(DEFAULT);
    }

    public static void launch(String arg) {
        CoreEngine.setMapEditor(true);
        CoreEngine.setSelectivelyReadTypes(microForMacro);
        XML_Reader.readTypes(false);
        //custom options?!
        new MapEditor().start();
//        MacroManager.newGame(arg);

        WaitMaster.waitForInput(WAIT_OPERATIONS.GDX_READY);
        Gdx.app.postRunnable(() -> GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         new ScreenData(ScreenType.MAP, arg)));
    }

    public static void main(String[] args) {
        CoreEngine.systemInit();
        XML_Reader.readTypes(true);
        if (args.length > 0)
            launch(args[0]);
        else launch(null);
    }

    //    protected void screenInit() {
//        //selection panel for campaign/scenario to edit!
//        ScreenData data = new ScreenData(ScreenType.MAP, "Editor");
//        screenSwitcher(new EventCallbackParam(data));
//    }
    @Override
    protected void engineInit() {
        super.engineInit();
        DC_Engine.dataInit(true);
    }

}
