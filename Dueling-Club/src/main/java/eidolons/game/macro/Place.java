package eidolons.game.macro;

import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;

import java.util.List;

/**
 * Created by Giskard on 6/8/2018.
 */
public class Place {

    private List<Dungeon> dungeons;
    private int nextDungeon;
    int x;
    int y;

    Place(List<Dungeon> dungeons){
        this.dungeons = dungeons;
        this.nextDungeon = 0;
    }

    public Dungeon enter(){
        return dungeons.get(0);
    }

    public Dungeon nextDungeon(){
        nextDungeon += 1;
        if (dungeons.size() > nextDungeon - 1) {
            return dungeons.get(nextDungeon);
        } else {
            return null;
        }
    }


}
