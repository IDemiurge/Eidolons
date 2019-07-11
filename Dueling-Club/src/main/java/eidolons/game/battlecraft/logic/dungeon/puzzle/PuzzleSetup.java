package eidolons.game.battlecraft.logic.dungeon.puzzle;

import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleElement;
import eidolons.game.core.Eidolons;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

public class PuzzleSetup extends PuzzleElement {

    private static final String SEPARATOR = "=";
    private static final String ARGS_SEPARATOR = ",";
    public String arg;
    String data;

    public PuzzleSetup(Puzzle puzzle, String data) {
        super(puzzle);
        this.data = data;
    }

    public void init() {

        Coordinates c = puzzle.getCoordinates();

        for (int i = 0; i < puzzle.getWidth(); i++) {
            for (int j = puzzle.getHeight()-1; j >= 0; j--) {
                float rotation = 90 * RandomWizard.getRandomIntBetween(0, 4);
                Coordinates root = c.getOffsetByY(-j).getOffsetByX(i);
                DC_Cell cell = Eidolons.getGame().getCellByCoordinate(
                        root);
                cell.setOverlayRotation(rotation);
                cell.setOverlayData(arg+ StringMaster.wrapInParenthesis(Coordinates.get(i, j).toString()));

                GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY, cell);
            }

        }
//        for (String substring : ContainerUtils.openContainer(data)) {
//            DC_Cell cell = Eidolons.getGame().getCellByCoordinate(
//                    root);
//            c = cell.getCoordinates().getOffset(c);
//            String args = data.split(SEPARATOR)[1];
//
//            cell.setOverlayRotation(Float.valueOf(args.split(ARGS_SEPARATOR)[0]));
//            cell.setOverlayData(Float.valueOf(args.split(ARGS_SEPARATOR)[1])
//                    + StringMaster.wrapInParenthesis(c.toString()));
//
//            GuiEventManager.trigger(GuiEventType.INIT_CELL_OVERLAY, cell);
//        }
    }
}
