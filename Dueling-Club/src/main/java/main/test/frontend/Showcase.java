package main.test.frontend;

import main.data.filesys.PathFinder;
import main.game.core.launch.PresetLauncher;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.FontMaster;
import main.system.graphics.GuiManager;

import java.util.List;

/**
 * Created by JustMe on 8/2/2017.
 */
public class Showcase {

    public static final String[] missions = {
     "Road Ambush",
     "A Walk among Tombstones",
     "The Ravenguard",
     "In Spider's Den",
     "The Tunnel",
     "Bone Temple",
    };
    public static   String launchData = "";
    public static final  String launchDataPath = PathFinder.getXML_PATH()+"last.txt";
    public static final String[] launch_options = {
     "Last Custom", "Mission", "Custom", "Tutorial", "Test",
    };
    private static boolean running;

    public static void main(String[] args) {
        running = true;
        FontMaster.init();
        GuiManager.init();
        int index = DialogMaster.optionChoice(launch_options,
         "Choose the type of Eidolons game you want to launch...");

        if (index==0){
            String data = FileManager.readFile(launchDataPath);
            List<String> parts = StringMaster.openContainer(data);
            index = 2;
//            index = StringMaster.getInteger(parts.get(0));
//            if (parts.size()>0)
            FAST_DC.PLAYER_PARTY= parts.get(0);
            FAST_DC.ENEMY_PARTY= parts.get(1);

        }
        if (index == 1) {
            index = DialogMaster.optionChoice(missions, "Choose mission to launch");
            if (index==-1)
                return ;

            launchData+=index+";";
            String[] args1 = {
             null, index + ""
            };
            ScenarioLauncher.main(args1);
        }
        else
        if (index==2){
            FAST_DC.main(new String[]{String.valueOf(PresetLauncher.OPTION_NEW)}
            );
        }
        else
        if (index==2){

        }
        if (!StringMaster.isEmpty(launchData) ) {
            FileManager.write(launchData, launchDataPath);

        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Showcase.running = running;
    }
}
