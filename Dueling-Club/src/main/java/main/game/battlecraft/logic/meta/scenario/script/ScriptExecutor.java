package main.game.battlecraft.logic.meta.scenario.script;

import main.entity.Ref;
import main.game.battlecraft.logic.battle.mission.MissionScriptManager.MISSION_SCRIPT_FUNCTION;

/**
 * Created by JustMe on 5/19/2017.
 */
public interface ScriptExecutor<T> {
    boolean execute(MISSION_SCRIPT_FUNCTION function, Ref ref, String... args);
}
