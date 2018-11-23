package eidolons.entity.handlers.active.spell;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.Spell;
import eidolons.entity.handlers.active.ActiveChecker;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.handlers.EntityMaster;

/**
 * Created by JustMe on 4/29/2017.
 */
public class SpellChecker extends ActiveChecker {
    public SpellChecker(Spell entity, EntityMaster<DC_ActiveObj> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    protected PROPERTY getTagProp() {
        return G_PROPS.SPELL_TAGS;
    }
}
