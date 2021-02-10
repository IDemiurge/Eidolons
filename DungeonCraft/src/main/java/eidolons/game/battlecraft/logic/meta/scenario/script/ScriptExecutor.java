package eidolons.game.battlecraft.logic.meta.scenario.script;

import main.entity.Ref;

/**
 * Created by JustMe on 5/19/2017.
 */
public interface ScriptExecutor<T> {
    boolean execute(T function, Ref ref, Object... args);

    String getSeparator(T func);

}
