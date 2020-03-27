package main.level_editor;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.utils.FileChooserX;
import main.content.DC_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.model.LE_DataModel;
import main.level_editor.backend.handlers.structure.FloorManager;
import main.level_editor.backend.metadata.options.LE_OptionsMaster;
import main.level_editor.backend.sim.LE_MetaMaster;
import main.level_editor.backend.struct.campaign.Campaign;
import main.level_editor.backend.struct.level.Floor;
import main.level_editor.gui.screen.LE_WaitingScreen;
import main.system.launch.CoreEngine;
import main.system.launch.TypeBuilder;
import main.system.threading.WaitMaster;

import java.util.Arrays;

public class LevelEditor {
    public static final WaitMaster.WAIT_OPERATIONS SELECTION_EVENT = WaitMaster.WAIT_OPERATIONS.SELECTION;
    public static Window.WindowStyle windowStyle;
    private static boolean campaignMode;
    public  static final boolean TEST_MODE = true;

    public static void main(String[] args) {
        CoreEngine.setLevelEditor(true);

        CoreEngine.setSelectivelyReadTypes("terrain;dungeons;bf obj;units;encounters;");
        TypeBuilder.typeBuildOverride.addAll( Arrays.asList(DC_TYPE.BF_OBJ, DC_TYPE.UNITS, DC_TYPE.ENCOUNTERS));
        Assets.setON(true);
//        DC_Engine.systemInit(false);
        new EditorApp(args).start();
        LE_OptionsMaster.init();

    }


    public static Integer getId(BattleFieldObject bfObj) {
        return getCurrent().getGame().getId(bfObj);
    }

    public static LE_DataModel getModel() {
        return getCurrent().getManager().getModelManager().getModel();
    }

    public static Window.WindowStyle getWindowStyle() {
        if (windowStyle == null) {
            windowStyle = new Window.WindowStyle(StyleHolder.getHqLabelStyle(
                    GdxMaster.adjustFontSize(20)).font
                    , StyleHolder.getDefaultLabelStyle().fontColor,
                    new NinePatchDrawable(NinePatchFactory.getLightDecorPanelFilledDrawable()));
        }
        return windowStyle;
    }

    public static Floor getCurrent() {
        return FloorManager.current;
    }
    public static LE_Manager getManager() {
        return FloorManager.current.getManager();
    }


    public static void welcome(
            String toOpen) {
        //welcome screen?..
//        editorSettings = FileManager.readFile(getSettingsPath());
//        toOpen = getOpenDefault();
        if (toOpen == null) {
            toOpen = FileChooserX.chooseFile(getDefaultOpenPath(), "xml",
                    LE_WaitingScreen.getInstance().getStage());
        }
        LE_MetaMaster meta;
        if (toOpen.contains("campaigns")) {
            campaignMode = true;
            ObjType type = DataManager.getType(toOpen, MACRO_OBJ_TYPES.CAMPAIGN);
            FloorManager.campaign = new Campaign(type);
            FloorManager.dungeon = FloorManager.campaign.getCurrentDungeon();
            meta = new LE_MetaMaster(FloorManager.campaign);
            //use data soruce / update() paradigm?     VS cached LE_Screens, eh?
        } else {
            meta = new LE_MetaMaster(toOpen);
        }
        FloorManager.initFloor(meta);
    }


    private static String getDefaultOpenPath() {
        return PathFinder.getDungeonLevelFolder();
    }
}
