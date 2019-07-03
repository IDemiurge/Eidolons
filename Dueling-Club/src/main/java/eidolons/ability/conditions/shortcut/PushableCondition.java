package eidolons.ability.conditions.shortcut;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.Structure;
import main.content.enums.entity.BfObjEnums;
import main.entity.Ref;

public class PushableCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {
        if (ref.getMatchObj() instanceof Structure) {
            if (((Structure) ref.getMatchObj()).isWall()) {
                return false;
            }
            if (((Structure) ref.getMatchObj()).isLandscape()) {
                return false;
            }
            switch (((Structure) ref.getMatchObj()).getBfObjGroup()) {
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
                    return true;
            }

        }
        return false;
    }
}
