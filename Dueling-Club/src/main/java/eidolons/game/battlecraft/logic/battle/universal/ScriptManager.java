package eidolons.game.battlecraft.logic.battle.universal;

import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.elements.triggers.Trigger;
import main.system.auxiliary.ContainerUtils;

/**
 * Created by JustMe on 6/2/2017.
 */
public abstract class ScriptManager<T extends Battle, E> extends BattleHandler<T> implements ScriptExecutor<E> {
    public ScriptManager(BattleMaster<T> master) {
        super(master);
    }

    public abstract void init();

    protected String readScriptsFile() {
        return "";
    }

    public void parseScripts(String scripts) {

        //syntax: new_round->equals({amount}, 2)->spawn(Vampires,5-5);
        for (String script : ContainerUtils.open(scripts,
         ScriptSyntax.SCRIPTS_SEPARATOR)) {
            try {
                addTrigger(ScriptParser.parseScript(script, getMaster().getGame(), this,
                 getFunctionClass()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        }
    }

    protected abstract Class<E> getFunctionClass();

    protected void addTrigger(Trigger trigger) {
        if (trigger == null)
            return;
        getMaster().getGame().getManager().addTrigger(trigger);
//        scriptTriggers.add(trigger);
    }
}
