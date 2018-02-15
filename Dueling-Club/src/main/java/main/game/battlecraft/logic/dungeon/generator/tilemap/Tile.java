package main.game.battlecraft.logic.dungeon.generator.tilemap;

import main.content.OBJ_TYPE;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by JustMe on 2/15/2018.
 */
public class Tile {
    Pair<String, OBJ_TYPE>[] data;

    public Tile(Pair<String, OBJ_TYPE>... data) {
        this.data = data;
    }
}
