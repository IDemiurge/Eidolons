package eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator.Awakener;
import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy.grid.VoidHandler;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import eidolons.libgdx.bf.grid.DC_GridPanel;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.EncounterEnums;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;

import java.util.List;

public class ExecutorHelper {
    SpeechExecutor executor;
    private VoidHandler voidHandler;

    public ExecutorHelper(SpeechExecutor executor) {
        this.executor = executor;

    }

    public void execute(SpeechScript.SCRIPT speechAction, String value, List<String> vars) {
        if (voidHandler == null)
                voidHandler = ((DC_GridPanel) ScreenMaster.getGrid()).getVoidHandler();
        boolean bool = false;
        switch (speechAction) {
            case RAISE:
               bool = true;
            case COLLAPSE:
                List<Coordinates> list = CoordinatesMaster.getCoordinatesFromString(value);
                Coordinates origin = executor.getCoordinate(vars.get(0));
                Float speed=1f;
                if (vars.size()>1) {
                    speed = NumberUtils.getFloat(vars.get(1));
                }
                voidHandler.toggle(bool, origin , list, speed);
                break;

            case AUTO_RAISE_ON:
                voidHandler.toggleAuto();
                break;
            case AWAKEN:
                LevelStruct struct = executor.master.getDungeonMaster().getStructMaster().
                        findBlockByName(vars.get(0));
                EncounterEnums.UNIT_GROUP_TYPE
                        ai = new EnumMaster<EncounterEnums.UNIT_GROUP_TYPE>().
                        retrieveEnumConst(EncounterEnums.UNIT_GROUP_TYPE.class, (vars.get(1)));

                Awakener.awaken_type type = new EnumMaster<Awakener.awaken_type>().retrieveEnumConst(
                        Awakener.awaken_type.class, (value));

                executor.master.getDungeonMaster().getAwakener().awaken(struct, ai, type);
                break;
        }
    }
}
