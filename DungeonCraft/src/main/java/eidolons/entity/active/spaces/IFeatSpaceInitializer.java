package eidolons.entity.active.spaces;

import eidolons.entity.unit.Unit;

public interface IFeatSpaceInitializer {

    //default actions - in AS form or not?
    // perhaps they can follow the old logic - just assemble a list... if >6, we'll use Expand button.

    FeatSpaces createFeatSpaces(Unit unit, boolean spellSpaces);

    FeatSpace.ActiveSpaceMeta createMeta(FeatSpace space);
}
