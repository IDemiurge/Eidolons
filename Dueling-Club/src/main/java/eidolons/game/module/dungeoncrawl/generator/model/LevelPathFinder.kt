package eidolons.game.module.dungeoncrawl.generator.model

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums
import eidolons.game.module.dungeoncrawl.generator.tilemap.TilesMaster
import main.game.bf.Coordinates
import main.game.bf.directions.FACING_DIRECTION
import main.system.auxiliary.data.MapMaster
import java.util.*

/**
 * Created by JustMe on 7/30/2018.
 */
class LevelPathFinder {

    internal var stepList: ArrayList<Coordinates> = ArrayList<Coordinates>()

    fun findPath(c: Coordinates, c2: Coordinates, cells: Array<Array<
            GeneratorEnums.ROOM_CELL>>): Boolean {
        var current = c;
        var tried = mapOf<Coordinates, List<FACING_DIRECTION>>()
        loop@ while (true) {

            while (true) {
                var toTry = listOf<FACING_DIRECTION>(FACING_DIRECTION.NORTH, FACING_DIRECTION.WEST, FACING_DIRECTION.EAST, FACING_DIRECTION.SOUTH)
                Collections.shuffle(toTry)

                for (direction in toTry) {
                    toTry.minus(direction)
                    var next = current.getAdjacentCoordinate(direction.direction)
                    var cell: GeneratorEnums.ROOM_CELL

                        cell=  cells[next.x][next.y]

//                    if (toTry.isEmpty())
//                        break
                    if (!TilesMaster.isPassable(cell)) {
                        continue
                    }
                    MapMaster.addToListMap(tried, current, direction)
                    stepList.add(next)
                    current = next
                    if (current.equals(c2)) {
                        return true
                    }
                }
                if (stepList.size == 0)
                    return false
                current = stepList.removeAt(stepList.size - 1)
                while (tried.get(current)?.size!! > 3 && stepList.size > 0)
                    current = stepList.removeAt(stepList.size - 1)


            }
        }
        return false
    }

}