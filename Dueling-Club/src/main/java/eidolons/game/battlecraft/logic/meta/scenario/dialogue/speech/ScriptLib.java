package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.core.game.DC_Game;
import eidolons.system.text.Texts;

import java.util.HashMap;
import java.util.Map;

public class ScriptLib {
    public static final Map<String, String> scriptMap = new HashMap<>();

    //parameterized?
    public static String get(STD_SCRIPT script) {
        return get(script.name().replace("_", " ").trim().toLowerCase());
    }

    public static String get(String scriptKey) {
        String script = Texts.getScript(scriptKey);
        if (script == null) {
            return scriptKey;
        }
        return script;
        // return scriptMap.get(scriptKey.toLowerCase());
    }

    public static void execute(String key) {
        String script = get(key);
        main.system.auxiliary.log.LogMaster.log(1, "Script by key: " + key);
        DC_Game.game.getMetaMaster().getDialogueManager().getSpeechExecutor().execute(script);
    }

    public static void execute(STD_SCRIPT script) {
        execute(get(script));
    }

    public enum STD_SCRIPT {
        black_hole,
        mini_explosion,
        white_shake,
        gate_flash,

    }
}
