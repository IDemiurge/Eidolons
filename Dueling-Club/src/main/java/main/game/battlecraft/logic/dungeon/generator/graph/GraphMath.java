package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;

/**
 * Created by JustMe on 2/13/2018.
 */
public class GraphMath {

    public float getPriority(ROOM_TYPE type, ROOM_TYPE type2) {
        switch (type) {
            case TREASURE_ROOM:
                switch (type2) {
                    case TREASURE_ROOM:
                        break;
                    case THRONE_ROOM:
                        break;
                    case DEATH_ROOM:
                        break;
                    case GUARD_ROOM:
                        break;
                    case COMMON_ROOM:
                        break;
                    case ENTRANCE_ROOM:
                        break;
                    case EXIT_ROOM:
                        break;
                    case SECRET_ROOM:
                        break;
                }
            case THRONE_ROOM:
                break;
            case DEATH_ROOM:
                break;
            case GUARD_ROOM:
                break;
            case COMMON_ROOM:
                break;
            case ENTRANCE_ROOM:
                break;
            case EXIT_ROOM:
                break;
            case SECRET_ROOM:
                break;
        }
        return 0;
    }
}
