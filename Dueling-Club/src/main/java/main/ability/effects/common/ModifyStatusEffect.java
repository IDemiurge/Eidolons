package main.ability.effects.common;

import main.ability.effects.oneshot.common.ModifyPropertyEffect;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.properties.G_PROPS;

public class ModifyStatusEffect extends ModifyPropertyEffect {

    public ModifyStatusEffect(MOD_PROP_TYPE modtype, STATUS status) {
        super(G_PROPS.STATUS, modtype, status.name());
    }

}
