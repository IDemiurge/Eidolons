package eidolons.game.battlecraft.logic.dungeon.puzzle.encounter;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleHandler;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleSetup;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleStats;
import eidolons.game.battlecraft.logic.dungeon.universal.utils.Summoner;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.battlecraft.logic.mission.encounter.Encounter;
import main.content.CONTENT_CONSTS;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;

import java.util.HashSet;
import java.util.Set;

/*
spawn per round OR <?>
 */
public class EncPuzzleHandler extends PuzzleHandler<EncounterPuzzle> {
    private final Set<BattleFieldObject> blocks = new HashSet<>();

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
        blockExit();
    }

    private void blockExit() {
        String name = "Indestructible Force Field";
        for (Coordinates c : puzzle.getData().getBlockData().keySet()) {
            if ( puzzle.getData().getBlockData().get(c).getValue(CellScriptData.CELL_SCRIPT_VALUE.marks)
                    .contains(CONTENT_CONSTS.MARK.block.name()))
                blocks.add(Summoner.bfObj(c, name));
        }
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

    private void spawn(boolean first) {
        PuzzleSpawner.spawn(puzzle, first);
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
        for (BattleFieldObject block : blocks) {
            block.kill();
        }
        blocks.clear();
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
