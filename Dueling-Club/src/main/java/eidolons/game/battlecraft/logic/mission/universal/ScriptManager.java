package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.meta.scenario.script.*;
import main.data.ability.construct.VariableManager;
import main.elements.triggers.Trigger;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 6/2/2017.
 */
public abstract class ScriptManager<T extends DungeonSequence, E> extends MissionHandler<T> implements ScriptExecutor<E> {
    private static final String DUNGEON_SCRIPT_SEPARATOR = "::";
    private Trigger lastTrigger;

    public ScriptManager(MissionMaster<T> master) {
        super(master);
    }

    public abstract void init();

    protected String readScriptsFile() {
        return "";
    }

    public void parseDungeonScripts(Map<Coordinates, CellScriptData> map) {
        List<String> scriptList = new ArrayList<>();

        for (Coordinates s : map.keySet()) {
            String dungeonScript = checkDungeonScript(s,map.get(s).
                    getValue(CellScriptData.CELL_SCRIPT_VALUE.script));
            if (dungeonScript != null) {
                scriptList.add(dungeonScript);
            }

              dungeonScript = checkFlightScript(s,map.get(s).
                    getValue(CellScriptData.CELL_SCRIPT_VALUE.flight));
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

    private String checkFlightScript(Coordinates s, String value) {
        if (StringMaster.isEmpty(value)) {
            return null;
        }
        return "pos" +StringMaster.wrapInParenthesis(s.toString())+
                ">main_hero()" +
                ">flight"+StringMaster.wrapInParenthesis(value);
    }
    private String checkDungeonScript(Coordinates c , String val) {
        if (StringMaster.isEmpty(val)) {
            return null;
        }

        if (val.contains(DUNGEON_SCRIPT_SEPARATOR)) {
            String type = val.split(DUNGEON_SCRIPT_SEPARATOR)[0].toLowerCase();
            String arg = val.split(DUNGEON_SCRIPT_SEPARATOR)[1].toLowerCase();
            String vars = VariableManager.getVars(arg);
            arg = VariableManager.removeVarPart(arg);
            switch (type) {

                case "comment":
                    return "pos(" + c.toString() + ")>mainHero()>" +
                            type + "(" + vars + "," + arg +
                            ")";
                case "tip":
                case "esoterica":
                    return "pos(" + c.toString() + ")>mainHero()>" +
                            type + "(" + arg + ")";
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
//        trigger.
        if (trigger instanceof ScriptTrigger) {
            if (!((ScriptTrigger) trigger).isTutorial()) {
                lastTrigger = trigger;
            }
        }
        getMaster().getGame().getManager().addTrigger(trigger);
//        map.put(name, trigger);
//        scriptTriggers.add(trigger);
    }

    public void removeLast() {
        lastTrigger.remove();
//        lastTrigger = null;
    }

    public Trigger parseTrigger(String text) {
        return ScriptParser.parseScript(text, getMaster().getGame(), this,
                getFunctionClass());
    }
}
