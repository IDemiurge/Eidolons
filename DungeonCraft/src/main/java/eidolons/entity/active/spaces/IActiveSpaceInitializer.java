package eidolons.entity.active.spaces;

import eidolons.entity.obj.unit.Unit;

public interface IActiveSpaceInitializer {

    //default actions - in AS form or not?
    // perhaps they can follow the old logic - just assemble a list... if >6, we'll use Expand button.

    UnitActiveSpaces createActiveSpaces(Unit unit);

    ActiveSpace.ActiveSpaceMeta createMeta(ActiveSpace  space);
}
