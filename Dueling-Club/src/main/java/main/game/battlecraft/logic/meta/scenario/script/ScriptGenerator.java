package main.game.battlecraft.logic.meta.scenario.script;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.battlecraft.DC_Engine;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import org.junit.Test;

import java.io.File;

/**
 * Created by JustMe on 6/1/2017.
 */
public class ScriptGenerator {

    public static final String SCRIPTS_FILE_NAME = "scripts.txt";
    private static final String TIPS_TEXT =
     "This is a script file to be parsed for this mission. " +
      "\n" +
      "Use this syntax to write scripts: Event>Condition>Action\n";

    // test scripts?
    @Test
    public void generateTemplatesForMissions() {
        DC_Engine.mainMenuInit();
        for (ObjType scenario : DataManager.getTypes(DC_TYPE.SCENARIOS)) {
            String root = StringMaster.buildPath(
             PathFinder.getScenariosPath(), scenario.getName());
            for (String sub : StringMaster.open(scenario.getProperty(PROPS.SCENARIO_MISSIONS))) {
                String path = StringMaster.buildPath(root, sub, SCRIPTS_FILE_NAME);
                File scriptFile = FileManager.getFile(PathFinder.getEnginePath() + path);
                if (scriptFile.exists()) {
                    //save scripts <?>
                    continue;
                }
                FileManager.write(getScriptsTemplate(), path);
            }
        }

    }

    private String getScriptsTemplate() {
        String tips = ScriptSyntax.COMMENT_OPEN;
        tips += TIPS_TEXT;
        tips += "PART_SEPARATOR:  " + ScriptSyntax.PART_SEPARATOR + "\n";
        tips += "SCRIPT_ARGS_SEPARATOR:  " + ScriptSyntax.SCRIPT_ARGS_SEPARATOR + "\n";
        tips += "SCRIPTS_SEPARATOR_ALT:  " + ScriptSyntax.SCRIPTS_SEPARATOR_ALT + "\n";
        tips += "SCRIPTS_SEPARATOR:  " + ScriptSyntax.SCRIPTS_SEPARATOR + "\n";
        tips += "EXAMPLE:  round(5)>string({name}, Gwyn)>SpellDamage(50, Shadow) | ..." +
         "\n";
        tips += ScriptSyntax.COMMENT_CLOSE;
        return tips;
    }
}
