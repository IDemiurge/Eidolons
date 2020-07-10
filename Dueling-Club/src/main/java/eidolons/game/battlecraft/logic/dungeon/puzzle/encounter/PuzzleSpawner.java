package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterData;
import main.content.enums.EncounterEnums;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;

import static eidolons.game.core.Eidolons.getGame;

public class PuzzleSpawner {

    public static List<Coordinates> getSpawnCoords(EncounterPuzzle puzzle, List<ObjType> types, boolean first) {
        List<Coordinates> list = new ArrayList<>(types.size());

        int w = puzzle.getWidth();
        int h = puzzle.getHeight();
        boolean centerOrEdges = first; //check if hero is there
        int x = 0, y = 0;
        if (centerOrEdges) {
            x = w / 2 - 1;
            y = h / 2 - 1;
        }
        int i = 0;
        for (ObjType type : types) {
            if (centerOrEdges) {
                if (x > y) y++;
                else x++;
            } else {
                x = RandomWizard.getRandomIntBetween(-w / 2, w / 2);
                y = RandomWizard.getRandomIntBetween(-h / 2, h / 2);
                switch (DIRECTION.ORTHOGONAL[i++ % 4]) {
                    case UP:
                        y = -h / 2;
                        break;
                    case DOWN:
                        y = h / 2;
                        break;
                    case RIGHT:
                        x = w / 2;
                        break;
                    case LEFT:
                        x = -w / 2;
                        break;
                }
            }

            list.add(puzzle.getCoordinates().getOffset(x > 0 ? x+1 : x-1, y > 0 ? y+1 : y-1));
        }
        return list;
    }
    public static List<ObjType> getSpawnTypes(EncounterPuzzle puzzle, boolean first) {
        if (first) {
            return puzzle.getEncounter().getTypes();
        }
        return puzzle.getEncounter().getReinforcer().getReinforceTypes(RandomWizard.random()
                ? EncounterEnums.REINFORCEMENT_STRENGTH.normal
                : EncounterEnums.REINFORCEMENT_STRENGTH.low);
    }

    public static void spawn(EncounterPuzzle puzzle, boolean first) {
        List<ObjType> types = getSpawnTypes(puzzle, first);

        List<Coordinates> coords =PuzzleSpawner.getSpawnCoords(puzzle, types, first);
        UnitsData data = new UnitsData(coords, types);
        /*
        positioning around some 'nests'?
         */
        List<Unit> units = null;
        if (first) {
            puzzle.getEncounter().setTypes(types);
            puzzle.getEncounter().setAdjustmentProhibited(true);
            EncounterData eData = new EncounterData("");
            AiData aiData = new AiData("type:idlers");
            units = getGame().getMissionMaster().getEncounterSpawner().spawnEncounter(eData, aiData, puzzle.getEncounter(),
                    coords.toArray(new Coordinates[0]));
        } else {
            units =  getGame().getDungeonMaster().getSpawner().spawn(data, getGame().getPlayer(false),
                    Spawner.SPAWN_MODE.WAVE);
        }
        puzzle.getEncounter().addMembers(units);
        for (Unit unit : puzzle.getEncounter().getUnits()) {
            unit.getAI().setEngaged(true);
        }
        // getGame().getDungeonMaster().getExplorationMaster().getEngagementHandler().addEvent(
        //         EngageEvent.ENGAGE_EVENT.combat_start);
    }
}
