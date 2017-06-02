package main.game.battlecraft.logic.meta.scenario.script;

import main.data.ability.construct.VariableManager;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/31/2017.
 */
public class ScriptMaster {
    public static final String scripts_path = PathFinder.getTextPath() + "scripts\\";
    public static final String generated_scripts_path =
     scripts_path + "generated_scripts.xml";
    public static final String dev_scripts_path =
     scripts_path + "dev_scripts.xml";

    public static String getScriptByName(String name) {
        String vars = VariableManager.getVars(name);
        if (vars.isEmpty())
            return getScriptByName(name, false);
        return getScriptByName(name, true, vars.split(StringMaster.SEPARATOR));
    }

    public static  String getScriptByName(String name, boolean variables, String... vars) {
        //TODO  use some official data format already!!!
//        String text = null;
//        FileManager.readFile(generated_scripts_path);
//        VariableManager.getVarText(text, true, true, vars);
        return name;
    }

    public void generateScripts() {
        //from missions? use ^VARs?
    }



}
