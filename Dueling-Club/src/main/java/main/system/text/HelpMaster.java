package main.system.text;

import main.data.filesys.PathFinder;
import main.game.core.Eidolons;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.libgdx.launch.ScenarioLauncher;

/**
 * Created by JustMe on 11/17/2017.
 */
public class HelpMaster {
    public static String getHelpText() {
//         TextMaster.getLocale()
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          "russian", "info", "manual.txt"));
        return text;
    }

    public static String getHeroMainInfoText(String name) {
        return getHeroInfoText(name, " main");
    }
    public static String getHeroInfoText(String name, String suffix) {
        if (name.contains(" ")) {
            name = name.split(" ")[0];
        }
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "info","heroes",
          name+ (suffix!=null ? suffix : "") +".txt"));
        if (text.isEmpty()) {
            text = "Sorry, no info on this hero!..";
        }
        return text;
    }
        public static String getWelcomeText() {
            String name = Eidolons.getMainHero().getName().split(" ")[0];
       return getHeroInfoText(name, null );
    }

    public static boolean isDefaultTextOn() {
        if (ScenarioLauncher.missionIndex>0)
            return false;
        return false;
    }

}
