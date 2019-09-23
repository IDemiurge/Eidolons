package eidolons.ability.conditions.shortcut;

import eidolons.ability.conditions.DC_Condition;
import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.BfObjEnums;
import main.content.values.properties.G_PROPS;
import main.entity.EntityCheckMaster;
import main.entity.Ref;

public class PushableCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {

        if (EntityCheckMaster.isOverlaying(ref.getMatchObj())) {
            if (!new ClearShotCondition().check(ref.getSourceObj(), ref.getMatchObj())) {
                return false;
            }
        }

        if (ref.getMatchObj() instanceof Structure) {
            Structure structure = (Structure) ref.getMatchObj();
            return isPushable(structure , (Unit) ref.getSourceObj());

        }
        return false;
    }

    public static boolean isPushable(Structure structure, Unit unit) {
        if (unit.isImmaterial()) {
            return false;
        }
        if (structure.isWall()) {
            return false;
        }
        if (structure.isLandscape()) {
            return false;
        }
        if (structure.checkProperty(G_PROPS.BF_OBJECT_TAGS, BfObjEnums.BF_OBJECT_TAGS.PUSHABLE.toString())) {
            return true;
        }
        switch (structure.getBfObjGroup()) {
            case WALL:
            case COLUMNS:
            case CONSTRUCT:
            case GATEWAY:
            case WINDOWS:
            case INTERIOR:
            case STATUES:
            case LOCK:
            case ENTRANCE:
            case TRAP:
            case DOOR:
            case WATER:
            case TREES:
            case RUINS:
            case GRAVES:
                return false;

            case MAGICAL:
            case HANGING:
            case LIGHT_EMITTER:
            case CONTAINER:
            case TREASURE:
            case DUNGEON:
            case ROCKS:
            case VEGETATION:
            case REMAINS:
            case CRYSTAL:
            case CONJURATE:
                return true;
        }
        return false;
    }
}
