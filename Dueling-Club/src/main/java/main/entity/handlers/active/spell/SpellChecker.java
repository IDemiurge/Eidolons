package main.entity.handlers.active.spell;

import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.active.ActiveChecker;

/**
 * Created by JustMe on 4/29/2017.
 */
public class SpellChecker extends ActiveChecker {
    public SpellChecker(DC_SpellObj entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    protected PROPERTY getTagProp() {
        return G_PROPS.SPELL_TAGS;
    }
}
