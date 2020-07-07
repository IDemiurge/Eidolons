package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Awakener;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleEnums;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.game.module.dungeoncrawl.objects.DungeonObj;
import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.game.netherflame.main.event.TipMessageMaster;
import eidolons.game.netherflame.main.pale.PaleAspect;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.sprite.CustomSpriteAnim;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.EncounterEnums;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
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
                    DC_Cell cell = DC_Game.game.getCellByCoordinate(Eidolons.getPlayerCoordinates());
                    cell.setOverlayRotation(cell.getOverlayRotation() + 90 * (int) (arg));
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
        // if (!isPaleReturn(puzzle,punishment))
        puzzle.failed();
        if (puzzle.isPale()) {
            PaleAspect.exitPale();
        }
    }

    private static Set<BattleFieldObject> getObjects(Puzzle puzzle) {
        Set<BattleFieldObject> set = new LinkedHashSet<>();
        for (Coordinates c : puzzle.getBlock().getCoordinatesSet()) {
            set.addAll(Eidolons.getGame().getObjectsOnCoordinateNoOverlaying(c));
        }
        return set;
        //                .stream().map(c-> Eidolons.getGame().getObjectsOnCoordinate(c)).reduce()
    }

    private static boolean isPaleReturn(Puzzle puzzle, PuzzleEnums.PUZZLE_PUNISHMENT punishment) {
        return true;
    }

    public static void resolution(PuzzleEnums.PUZZLE_RESOLUTION resolution, Puzzle puzzle, String s) {
        if (puzzle.isPale()) {
            PaleAspect.exitPale();
        }
        //TODO tips and so on
        switch (resolution) {
            case remove_wall:
                break;
            case unseal_door:
                LevelBlock block = puzzle.getBlock();
                for (Coordinates c : block.getCoordinatesSet()) {
                    for (BattleFieldObject object : Eidolons.getGame().getObjectsOnCoordinateNoOverlaying(c)) {
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
        LevelStruct struct = DC_Game.game.getDungeonMaster().getStructMaster().getLowestStruct(
                puzzle.getEntranceCoordinates());
        List<String> strings = ContainerUtils.openContainer(data);
        EncounterEnums.UNIT_GROUP_TYPE ai = EncounterEnums.UNIT_GROUP_TYPE.GUARDS;
        Awakener.awaken_type type = Awakener.awaken_type.valueOf(strings.get(0).toLowerCase());
        if (strings.size() > 1) {
            // ai
        }
        DC_Game.game.getDungeonMaster().getAwakener().awaken(struct, ai, type);
    }

    private static void teleport(Puzzle puzzle, String data) {
        if (data.isEmpty()) {
            data = puzzle.getEntranceCoordinates().toString();
        }
        Coordinates c = puzzle.getAbsoluteCoordinate((new AbstractCoordinates(true, data)));
        CustomSpriteAnim anim = AnimMaster.getInstance().spriteAnim(Sprites.PORTAL_CLOSE,
                Eidolons.getMainHero().getCoordinates());
        anim.onDone(p ->
                AnimMaster.getInstance().spriteAnim(Sprites.PORTAL_OPEN,
                        c), null);
        Eidolons.getGame().getMissionMaster().getScriptManager().execute(
                CombatScriptExecutor.COMBAT_SCRIPT_FUNCTION.REPOSITION,
                Ref.getSelfTargetingRefCopy(Eidolons.getMainHero()), c.toString());


    }
}
