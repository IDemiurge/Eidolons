package eidolons.entity.handlers.active.spell;

import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_SpellObj;
import main.entity.handlers.EntityMaster;
import eidolons.entity.handlers.active.ActiveChecker;

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
