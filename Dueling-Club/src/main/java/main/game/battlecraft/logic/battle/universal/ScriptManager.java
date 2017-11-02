package main.game.battlecraft.logic.battle.universal;

import main.elements.triggers.Trigger;
import main.game.battlecraft.logic.meta.scenario.script.ScriptExecutor;
import main.game.battlecraft.logic.meta.scenario.script.ScriptParser;
import main.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import main.system.auxiliary.StringMaster;

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
        for (String script : StringMaster.open(scripts,
         ScriptSyntax.SCRIPTS_SEPARATOR)) {
            addTrigger(ScriptParser.parseScript(script, getMaster().getGame(), this,
             getFunctionClass()));
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
