package main.game.battlecraft.logic.dungeon.location;

import main.game.battlecraft.logic.dungeon.universal.Dungeon;

import java.util.List;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationTraverser {
    /*
    manages dungeon to dungeon travels during a Crawl
     */
    private Integer z;
    private List<Dungeon> dungeons;
    private Dungeon rootDungeon;

//    public static void goToDungeon(Dungeon newDungeon) {
//        game.getMainHero().setDungeon(newDungeon);
//        // coordinates? the dungeon's entrance..
//        Coordinates coordinates = game.getMainHero().getCoordinates();
//        if (newDungeon.getMainEntrance() != null) {
//            coordinates = newDungeon.getMainEntrance().getCoordinates();
//        }
//        // exit?
//        List<Unit> units = new ArrayList<>();
//        if (game.getParty() != null) {
//            units.addAll(game.getParty().getMembers());
//        } else {
//
//        }
//        game.getArenaManager().getSpawnManager().spawnUnitsAt(units, coordinates);
//        game.getBattleField().refresh();
//    }
//
//    public Integer getZ() {
//        if (z == null) {
//            if (dungeon != null) {
//                return dungeon.getZ();
//            }
//        }
//        return z;
//    }

}
