package main.test.frontend;

import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.data.filesys.PathFinder;
import main.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import main.game.core.launch.PresetLauncher;
import main.swing.generic.components.editors.FileChooser;
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
    public static final String[] missions_showcase =
     "Prison Break;The Demon Shrine;The Escape;Into the Woods;On a Pirate Ship;Ledwraith Castle".split(";");
    public static final String launchDataPath = PathFinder.getXML_PATH() + "showcase last.txt";
    public static final String[] launch_options = {
     "Mission", "Last Custom", "Custom",
//     "Tutorial",
     "Test", "Showcase",
     "Hero Creator",
    };
    public static String launchData = "";
    private static boolean running;

    public static void main(String[] args) {
        running = true;
        boolean preset = false;
        FontMaster.init();
        GuiManager.init();
        int index = -1;
        if (args != null) {
            preset = true;
            index = Integer.valueOf(args[0]);
        } else
            index = DialogMaster.optionChoice(launch_options,
             "Choose the type of Eidolons game you want to launch...");

        if (index == 5) {
            Launcher.main(null);
            return;
        }
        if (index == 1) {
            String data = FileManager.readFile(launchDataPath);
            List<String> parts = StringMaster.openContainer(data);
            index = 2;
//            index = StringMaster.getInteger(parts.get(0));
//            if (parts.size()>0)
            FAST_DC.DEFAULT_DUNGEON = parts.get(0);
            FAST_DC.PLAYER_PARTY = parts.get(1);
            FAST_DC.ENEMY_PARTY = parts.get(2);

        }
        if (index == 4 || index == 0) {
            boolean showcase = index == 4;
            String[] options = !showcase ? missions : missions_showcase;
            if (preset) index = 0;
            else
                index = DialogMaster.optionChoice(options, "Choose mission to launch");
            if (index == -1)
                return;
            if (index > 2) {
                ItemGenerator.setBasicMode(false);
            }
            launchData += index + ";";
            String[] args1 = {
             showcase ? "Crawl Demo"
              //"Showcase"
              : null, index + ""
            };
            ScenarioLauncher.main(args1);
        } else if (index == 2) {
            String d = new FileChooser(PathFinder.getDungeonLevelFolder() + "showcase")
             .launch("", "");
            if (d == null) {
                d = DungeonInitializer.RANDOM_DUNGEON;
            }
            FAST_DC.DEFAULT_DUNGEON = d;
            launchData += d + ";";

//            FAST_DC.PLAYER_PARTY= TestLauncher.c
//            FAST_DC.ENEMY_PARTY= parts.get(2);
            FAST_DC.main(new String[]{String.valueOf(PresetLauncher.OPTION_NEW)}
            );
        } else {
            FAST_DC.main(new String[]{
            });
        }
        new Showcase().write();

    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Showcase.running = running;
    }

    protected void write() {
        if (!StringMaster.isEmpty(launchData)) {
            FileManager.write(launchData, launchDataPath);

        }
    }
}
