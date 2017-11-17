package main.system.text;

import main.data.filesys.PathFinder;
import main.game.core.Eidolons;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

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

    public static String getWelcomeText() {
        String name = Eidolons.getMainHero().getName().split(" ")[0];
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "info","heroes",name+ ".txt"));
        if (text.isEmpty()) {
            text = "Sorry, not info on this hero!..";
        }
        return text;
    }

    public static boolean isDefaultTextOn() {
        return true;
    }
}
