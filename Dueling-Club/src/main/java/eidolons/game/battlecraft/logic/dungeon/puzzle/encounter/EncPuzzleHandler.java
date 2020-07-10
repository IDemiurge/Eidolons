package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleStats;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterData;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.logic.event.Event;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.List;

/*
spawn per round OR <?>
 */
public class EncPuzzleHandler extends PuzzleHandler<EncounterPuzzle> {
    public EncPuzzleHandler(EncounterPuzzle encounterPuzzle) {
        super(encounterPuzzle);
    }

    @Override
    protected PuzzleSetup<EncounterPuzzle, Encounter> createSetup() {
        return new EncPuzzleSetup(puzzle, puzzle.getData());
    }

    @Override
    protected void entered() {
        ObjType encounterType = DataManager.getType(
                puzzle.getData().getValue(PuzzleData.PUZZLE_VALUE.ARG), DC_TYPE.ENCOUNTERS);
        Encounter encounter = new Encounter(encounterType, getCoordinates());
        puzzle.setEncounter(encounter);
        try {
            spawn(true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.entered();
    }

    @Override
    public void started() {
        super.started();
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getType() == Event.STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {
            puzzle.getQuest().increment();
            puzzle.getStats().addCount(PuzzleStats.PUZZLE_STAT.SCORE, event.getRef().getSourceObj().getIntParam(PARAMS.POWER));

            try {
                puzzle.getEncounter().getUnits().remove(event.getRef().getSourceObj());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    @Override
    public void customAction() {
        spawn(false);
    }

    private List<Coordinates> getSpawnCoords(List<ObjType> types, boolean first) {
        List<Coordinates> list = new ArrayList<>(types.size());
        int w = puzzle.getWidth();
        int h = puzzle.getHeight();

        boolean centerOrEdges = RandomWizard.random(); //check if hero is there
        int x = 0, y = 0;
        if (centerOrEdges) {
            x = w / 2 - 1;
            y = h / 2 - 1;
        }
        int i = 0;
        for (ObjType type : types) {
            if (centerOrEdges) {
                if (x > y) x++;
                else y++;
            } else {
                x = RandomWizard.getRandomIntBetween(1, w - 1);
                y = RandomWizard.getRandomIntBetween(1, h - 1);
                switch (DIRECTION.ORTHOGONAL[i++ % 4]) {
                    case UP:
                        y = 0;
                        break;
                    case DOWN:
                        y = h;
                        break;
                    case RIGHT:
                        x = w;
                        break;
                    case LEFT:
                        x = 0;
                        break;
                }
            }
            list.add(getCoordinates().getOffset(x, y));
        }
        return list;
    }

    private List<ObjType> getSpawnTypes(boolean first) {
        return puzzle.getEncounter().getTypes();
    }

    private void spawn(boolean first) {
        List<ObjType> types = getSpawnTypes(first);
        List<Coordinates> coords = getSpawnCoords(types, first);
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
            units = getGame().getDungeonMaster().getSpawner().spawn(data, getGame().getPlayer(false),
                    Spawner.SPAWN_MODE.WAVE);
        }
        puzzle.getEncounter().addMembers(units);
    }


    @Override
    protected void playerActionDone(DC_ActiveObj action) {
        super.playerActionDone(action);
        //check if need dynamic reinforcements
        if (puzzle.getEncounter().getUnits().size() < puzzle.getMinUnits()) {
            spawn(false);
        }
    }

    @Override
    public void ended() {
        super.ended();
        for (Unit unit : puzzle.getEncounter().getUnits()) {
            unit.kill();
        }
        puzzle.getEncounter().getUnits().clear(); //not really needed
    }


    @Override
    public void afterTipAction() {
        super.afterTipAction();
    }

    @Override
    public void glimpse() {
        super.glimpse();
    }

    @Override
    public void afterEndTip() {
        super.afterEndTip();
    }

    @Override
    public void failed() {
        super.failed();
    }

    @Override
    protected void beforeTip() {
        super.beforeTip();
    }
}
