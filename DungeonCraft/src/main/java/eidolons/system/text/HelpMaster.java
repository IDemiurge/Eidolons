package eidolons.system.text;

import eidolons.game.core.Core;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.Flags;

/**
 * Created by JustMe on 11/17/2017.
 */
public class HelpMaster {
    public static String getHelpText() {
        return FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "manual", "manual.txt"));
    }

    public static String getHeroMainInfoText(String name) {
        return getHeroInfoText(name, " main");
    }

    public static String getHeroInfoText(String name, String suffix) {
        String prefix = null ;
        if (Flags.isIggDemo()) {
            prefix = "igg";
        } else
        if (name.contains(" ")) {
            if (name.split(" ")[0].equalsIgnoreCase("demo")){
                name = name.split(" ")[1];
                prefix = "demo";
            } else
                name = name.split(" ")[0];
        }
        String path=StrPathBuilder.build(PathFinder.getTextPath(),
         TextMaster.getLocale(), "descriptions", "heroes");
        if (prefix!=null ){
            path=StrPathBuilder.build(path, prefix);
        }
        String text = FileManager.readFile(
         StrPathBuilder.build(path,
          name + (suffix != null ? suffix : "") + ".txt"));
        if (text.isEmpty()) {
            text = "Sorry, no info on this hero!..";
        }
        return text;
    }

    public static String getWelcomeText() {
        String name = Core.getMainHero().getName().split(" ")[0];
        return getHeroInfoText(name, null);
    }

    public static boolean isDefaultTextOn() {
        return false;
    }

    public static String getScenarioInfoText(String name, String suffix) {
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "descriptions", "scenarios",
          name + (suffix != null ? suffix : "") + ".txt"));
        if (text.isEmpty()) {
            text = "Sorry, no info on this scenario!..";
        }
        return text;
    }
}
