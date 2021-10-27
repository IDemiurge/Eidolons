package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Awakener;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.game.exploration.dungeon.objects.Door;
import eidolons.game.exploration.dungeon.objects.DungeonObj;
import eidolons.game.exploration.dungeon.generator.model.AbstractCoordinates;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.text.tips.TipMessageMaster;
import main.content.enums.EncounterEnums;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.sound.AudioEnums;
import main.system.threading.WaitMaster;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PuzzleActions extends PuzzleElement {

    public PuzzleActions(Puzzle puzzle) {
        super(puzzle);
    }

    public static Runnable action(PuzzleEnums.PUZZLE_ACTION rotateMosaicCell) {
        return () -> {
            Object arg = -1;
            switch (rotateMosaicCell) {
                case ROTATE_MOSAIC_CELL_ANTICLOCKWISE:
                    arg = 1;
                case ROTATE_MOSAIC_CELL_CLOCKWISE:
                    DC_Cell cell = DC_Game.game.getCell(Core.getPlayerCoordinates());
                    cell.setOverlayRotation(cell.getOverlayRotation() + 90 * (int) (arg));

                    if (cell.getOverlayRotation() % 360 == 0) {
                        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.CLICK_ACTIVATE);
                    }
                    GuiEventManager.trigger(GuiEventType.CELL_RESET, cell);
                    break;
            }
        };
    }

    public Runnable create(PuzzleEnums.PUZZLE_ACTION_BASE template) {
        return null;
    }

    public static void punishment(Puzzle puzzle, PuzzleEnums.PUZZLE_PUNISHMENT punishment, String data) {
        WaitMaster.WAIT(puzzle.getWaitTimeBeforeEndMsg(true));
        try {
            applyPunishment(puzzle, punishment, data);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        // if (!isPaleReturn(puzzle,punishment))
        puzzle.failed();
    }

    private static void applyPunishment(Puzzle puzzle, PuzzleEnums.PUZZLE_PUNISHMENT punishment, String data) {
        switch (punishment) {
            case battle:
            case death:
            case spell:
                break;
            case teleport:
                teleport(puzzle, data);
                break;
            case tip:
                TipMessageMaster.tip(data);
                break;
            case awaken:
                awaken(puzzle, data);
                break;
        }
    }

    private static Set<BattleFieldObject> getObjects(Puzzle puzzle) {
        Set<BattleFieldObject> set = new LinkedHashSet<>();
        for (Coordinates c : puzzle.getBlock().getCoordinatesSet()) {
            set.addAll(Core.getGame().getObjectsOnCoordinateNoOverlaying(c));
        }
        return set;
        //                .stream().map(c-> Eidolons.getGame().getObjectsOnCoordinate(c)).reduce()
    }

    private static boolean isPaleReturn(Puzzle puzzle, PuzzleEnums.PUZZLE_PUNISHMENT punishment) {
        return true;
    }

    public static void resolution(PuzzleEnums.PUZZLE_RESOLUTION resolution, Puzzle puzzle, String s) {
        //TODO tips and so on
        switch (resolution) {
            case remove_wall:
                break;
            case unseal_door:
                LevelBlock block = puzzle.getBlock();
                for (Coordinates c : block.getCoordinatesSet()) {
                    for (BattleFieldObject object : Core.getGame().getObjectsOnCoordinateNoOverlaying(c)) {
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
            case awaken:
                awaken(puzzle, s);
                break;
        }
    }

    private static void awaken(Puzzle puzzle, String data) {
        Awakener.awaken_type default_type = Awakener.awaken_type.animate_stone;
        LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(
                puzzle.getEntranceCoordinates());
        List<String> strings = ContainerUtils.openContainer(data);
        EncounterEnums.UNIT_GROUP_TYPE ai = EncounterEnums.UNIT_GROUP_TYPE.GUARDS;
        Awakener.awaken_type type =
                strings.isEmpty() ? default_type :
                        Awakener.awaken_type.valueOf(strings.get(0).toLowerCase());
        if (strings.size() > 1) {
            // ai
        }
        DC_Game.game.getDungeonMaster().getAwakener().awaken(struct, ai, type);
    }

    private static void teleport(Puzzle puzzle, String data) {

        if (data.isEmpty()) {
            data = puzzle.getEntranceCoordinates().toString();
        }
        Coordinates c = (new AbstractCoordinates(true, data));
        //TODO gdx sync
        // GdxAdapter.getAnims()
        // AnimMaster.getInstance().spriteAnim(Sprites.PORTAL_CLOSE,
        //         Eidolons.getMainHero().getCoordinates(), p ->
        //                 AnimMaster.getInstance().spriteAnim(Sprites.PORTAL_OPEN,
        //                         c), null);
        Core.getGame().getMissionMaster().getScriptManager().execute(
                CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION.REPOSITION,
                Ref.getSelfTargetingRefCopy(Core.getMainHero()), c.toString());


    }
}
