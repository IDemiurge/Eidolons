package eidolons.game.module.dungeoncrawl.generator.graph;


import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;

/**
 * Created by JustMe on 2/13/2018.
 */
public class GraphMath {

    public float getPriority(ROOM_TYPE type, ROOM_TYPE type2) {
        switch (type) {
            case TREASURE_ROOM:
                switch (type2) {
                    case TREASURE_ROOM:
                    case SECRET_ROOM:
                    case EXIT_ROOM:
                    case ENTRANCE_ROOM:
                    case COMMON_ROOM:
                    case GUARD_ROOM:
                    case DEATH_ROOM:
                    case THRONE_ROOM:
                        break;
                }
            case THRONE_ROOM:
            case SECRET_ROOM:
            case EXIT_ROOM:
            case ENTRANCE_ROOM:
            case COMMON_ROOM:
            case GUARD_ROOM:
            case DEATH_ROOM:
                break;
        }
        return 0;
    }
}
