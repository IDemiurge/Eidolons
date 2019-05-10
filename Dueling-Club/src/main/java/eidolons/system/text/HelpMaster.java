package eidolons.system.text;

import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.ScenarioLauncher;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

/**
 * Created by JustMe on 11/17/2017.
 */
public class HelpMaster {
    public static String getHelpText() {
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "manual", "manual.txt"));
        return text;
    }

    public static String getHeroMainInfoText(String name) {
        return getHeroInfoText(name, " main");
    }

    public static String getHeroInfoText(String name, String suffix) {
        boolean demo=false;
        if (name.contains(" ")) {
            if (name.split(" ")[0].equalsIgnoreCase("demo")){
                name = name.split(" ")[1];
                demo = true;
            } else
                name = name.split(" ")[0];
        }
        String path=StrPathBuilder.build(PathFinder.getTextPath(),
         TextMaster.getLocale(), "info", "heroes");
        if (demo){
            path=StrPathBuilder.build(path, "demo");
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
        String name = Eidolons.getMainHero().getName().split(" ")[0];
        return getHeroInfoText(name, null);
    }

    public static boolean isDefaultTextOn() {
        if (ScenarioLauncher.missionIndex > 0)
            return false;
        return false;
    }

    public static String getScenarioInfoText(String name, String suffix) {
        String text = FileManager.readFile(
         StrPathBuilder.build(PathFinder.getTextPath(),
          TextMaster.getLocale(), "info", "scenarios",
          name + (suffix != null ? suffix : "") + ".txt"));
        if (text.isEmpty()) {
            text = "Sorry, no info on this scenario!..";
        }
        return text;
    }
}