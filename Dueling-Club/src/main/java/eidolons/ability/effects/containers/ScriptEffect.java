package eidolons.ability.effects.containers;

import eidolons.ability.effects.DC_Effect;

public class ScriptEffect extends DC_Effect {
    String script;

    public ScriptEffect(String script) {
        this.script = script;
        //alt - get script from property of the spell/... ?
    }

    @Override
    public boolean applyThis() {
        getGame().getMetaMaster().getDialogueManager().getSpeechExecutor().execute(script);
        return true;
    }
}
