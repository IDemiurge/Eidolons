package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.core.game.DC_Game;
import eidolons.system.text.Texts;

import java.util.HashMap;
import java.util.Map;

public class ScriptLib {
    public static final Map<String, String> scriptMap = new HashMap<>();
    //parameterized?
    public static String get(SCRIPT script) {
        return get(script.name().replace("_", " ").trim().toLowerCase());
    }
        public static String get(String scriptKey) {
        return Texts.getScript(scriptKey);
        // return scriptMap.get(scriptKey.toLowerCase());
    }
    /*
    what else can it do?
     */
    public static void init(){
        // FileManager.readFile("");
        // for(String substring: ContainerUtils.openContainer( string )){ }
    }

    public static void execute(String key) {
        String script=get(key);
        DC_Game.game.getMetaMaster().getDialogueManager().getSpeechExecutor().execute(script);
    }

    public enum SCRIPT{
        black_hole,
        mini_explosion,
        white_shake,
        gate_flash,

    }
}
