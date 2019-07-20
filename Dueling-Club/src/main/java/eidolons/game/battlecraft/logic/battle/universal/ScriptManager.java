package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.elements.triggers.Trigger;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 6/2/2017.
 */
public abstract class ScriptManager<T extends Battle, E> extends BattleHandler<T> implements ScriptExecutor<E> {
    private static final String DUNGEON_SCRIPT_SEPARATOR = "::";

    public ScriptManager(BattleMaster<T> master) {
        super(master);
    }

    public abstract void init();

    protected String readScriptsFile() {
        return "";
    }

    public void parseDungeonScripts(Dungeon dungeon) {
        List<String> scriptList = new ArrayList<>();
        for (String s : dungeon.getCustomDataMap().keySet()) {
            String dungeonScript = checkDungeonScript(s, getGame().getDungeon().getCustomDataMap().get(s));
            if (dungeonScript != null) {
                scriptList.add(dungeonScript);
            }
        }
        parseScripts(scriptList);
    }

    public void parseScripts(String scripts) {
        List<String> scriptList = ContainerUtils.openContainer(scripts,
                ScriptSyntax.SCRIPTS_SEPARATOR);
        parseScripts(scriptList);
    }

    public void parseScripts(List<String> scriptList) {
        //syntax: new_round->equals({amount}, 2)->spawn(Vampires,5-5);
        for (String script : scriptList) {
            try {
                addTrigger(ScriptParser.parseScript(script, getMaster().getGame(), this,
                        getFunctionClass()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    private String checkDungeonScript(String key, String val) {

        Coordinates c = new Coordinates(key);

        if (val.contains(DUNGEON_SCRIPT_SEPARATOR)) {
            String type = val.split(DUNGEON_SCRIPT_SEPARATOR)[0].toLowerCase();
            String arg = val.split(DUNGEON_SCRIPT_SEPARATOR)[1].toLowerCase();
            switch (type) {
                case "tip":
                    return "pos(" + c.toString() + ")>mainHero()>tip(" +
                            arg + ")";
                case "tip_dialogue ":
                case "dialogue_tip":
                case "dialogue":
                    return "pos(" + c.toString() + ")>mainHero()>" +
                            type +
                            "(" +
                            arg + "," + arg + ")";
                case "quest":
                    return "pos(" + c.toString() + ")>mainHero()>tip_quest(" +
                            arg + "," + arg + ")";
            }
        }

        return null;
    }

    protected abstract Class<E> getFunctionClass();

    protected void addTrigger(Trigger trigger) {
        if (trigger == null)
            return;
        getMaster().getGame().getManager().addTrigger(trigger);
//        scriptTriggers.add(trigger);
    }
}
