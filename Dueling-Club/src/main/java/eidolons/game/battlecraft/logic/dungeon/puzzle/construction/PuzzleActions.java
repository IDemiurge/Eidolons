package eidolons.game.battlecraft.logic.dungeon.puzzle.construction;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.battle.mission.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.PuzzleResolution;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.igg.pale.PaleAspect;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.data.DataManager;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashSet;
import java.util.Set;

public class PuzzleActions extends PuzzleElement {

    public PuzzleActions(Puzzle puzzle) {
        super(puzzle);
    }

    public static Runnable action(PuzzleMaster.PUZZLE_ACTION rotateMosaicCell) {
        return () -> {
            Object arg = -1;
            switch (rotateMosaicCell) {

                case ROTATE_MOSAIC_CELL_ANTICLOCKWISE:
                    arg= 1;
                case ROTATE_MOSAIC_CELL_CLOCKWISE:
                    DC_Cell cell = DC_Game.game.getCellByCoordinate(Eidolons.getMainHero().getCoordinates());
                    cell.setOverlayRotation(cell.getOverlayRotation() + 90 * (int) (arg));
                    GuiEventManager.trigger(GuiEventType.CELL_RESET, cell);
                    break;
            }
        };
    }

    public Runnable create(PuzzleMaster.PUZZLE_ACTION_BASE template) {
        return null;
    }

    public static void punishment(Puzzle puzzle, PuzzleResolution.PUZZLE_PUNISHMENT punishment, String data) {

        switch (punishment) {
            case battle:
                break;
            case spell:
                break;
            case teleport:
                teleport(puzzle, data);
                break;
            case tip:
                TipMessageMaster.tip(data);
                break;
            case death:
                break;
            case animate_enemies:
                for (BattleFieldObject object : getObjects(puzzle)) {
                    if (object.checkBool(GenericEnums.STD_BOOLS.LIVING_STATUE)) {
                        Unit unit = (Unit) object.getGame().createUnit(DataManager.getType("Living " + object.getName(), DC_TYPE.UNITS),
                                object.getCoordinates(), object.getGame().getPlayer(false));
                        object.kill();
                        unit.getAI().setEngaged(true);
                        unit.getAI().setEngagedOverride(true);

//                        enemies.add(unit);
                    }

                }

                break;
        }
        if (!isPaleReturn(puzzle,punishment))
            puzzle.failed();
        if (puzzle.isPale()) {
            PaleAspect.exitPale();
        }
    }

    private static Set<BattleFieldObject> getObjects(Puzzle puzzle) {
        Set<BattleFieldObject> set = new LinkedHashSet<>();
        for (Coordinates c : puzzle.getBlock().getCoordinatesList()) {
            set.addAll(Eidolons.getGame().getObjectsOnCoordinate(c));
        }
        return set;
//                .stream().map(c-> Eidolons.getGame().getObjectsOnCoordinate(c)).reduce()
    }

    private static boolean isPaleReturn(Puzzle puzzle, PuzzleResolution.PUZZLE_PUNISHMENT punishment) {
        if (EidolonsGame.BRIDGE)
            return false;
        return true;
    }

    public static void resolution(PuzzleResolution.PUZZLE_RESOLUTION resolution, Puzzle puzzle, String s) {
        if (puzzle.isPale()) {
            PaleAspect.exitPale();
        }
        //TODO tips and so on
        switch (resolution) {
            case remove_wall:
                break;
            case unseal_door:
                LevelBlock block = puzzle.getBlock();
                for (Coordinates c : block.getCoordinatesList()) {
                    for (BattleFieldObject object : Eidolons.getGame().getObjectsOnCoordinate(c)) {
                        if (object instanceof Door) {
//                            ((Door) object).setState(DoorMaster.DOOR_STATE.OPEN);
                            ((Door) object).getDM().open((DungeonObj) object, new Ref());
                        }
                    }

                }
                break;
            case teleport:
                teleport(puzzle, s);
                break;
            case tip:
                TipMessageMaster.tip(s);
                break;
        }
    }

    private static void teleport(Puzzle puzzle, String data) {
        if (data.isEmpty()) {
            data = puzzle.getEntranceCoordinates().toString();
        }
        Coordinates c = puzzle.getAbsoluteCoordinate((new AbstractCoordinates(true, data)));
        Eidolons.getGame().getBattleMaster().getScriptManager().execute(CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION.REPOSITION,
                Ref.getSelfTargetingRefCopy(Eidolons.getMainHero()), c.toString());
    }
}
