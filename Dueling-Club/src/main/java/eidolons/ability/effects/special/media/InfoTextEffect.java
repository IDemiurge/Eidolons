package eidolons.ability.effects.special.media;

import eidolons.ability.effects.DC_Effect;
import eidolons.game.core.EUtils;
import main.entity.Ref;

public class InfoTextEffect extends DC_Effect {
    Ref.KEYS key;
    String text;
    boolean log;

    public InfoTextEffect(Ref.KEYS key, String text, boolean log) {
        this.key = key;
        this.text = text;
        this.log = log;
    }

    @Override
    public boolean applyThis() {
        EUtils.showInfoText(ref.getObj(key).getName() + " " + text);
        if (log) {
            getGame().getLogManager().log(ref.getObj(key).getName() + " " + text);
        }
        return true;
    }
}
