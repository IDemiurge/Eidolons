package eidolons.game.battlecraft.logic.dungeon.puzzle.maze.voidy;

import eidolons.game.battlecraft.logic.dungeon.puzzle.maze.MazeSetup;
import eidolons.game.core.Eidolons;
import eidolons.game.module.generator.model.RoomModel;
import main.content.CONTENT_CONSTS;
import main.game.bf.Coordinates;
import main.system.auxiliary.RandomWizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VoidMazeSetup extends MazeSetup<VoidMaze> {

    private RoomModel template;

    public VoidMazeSetup(VoidMaze puzzle) {
        super(puzzle );
    }

    @Override
    public void ended() {
        super.ended();
        template = null;
    }

    public RoomModel reset () {
        if ( template != null) {
            return template;
        }
        template = super.reset();
        if (puzzle.isTransform()) {
            transformMaze();
        }
        puzzle. falseExits = new ArrayList<>();
        for (int i = 0; i < template.getCells().length; i++) {
            for (int j = 0; j < template.getCells()[0].length; j++) {
                if (template.getCells()[i][j].equalsIgnoreCase(puzzle.getExitSymbol().symbol)) {
                    Coordinates c = Coordinates.get(i, j);
                    if (!isExitToConvert(c))
                        puzzle. falseExits.add(c);
                  puzzle. getHandler(). markedCells.add(c);
                }
            }
        }
        for (Coordinates markedCell : puzzle. getHandler().markedCells) {
            Eidolons.getGame().getCellByCoordinate(getAbsoluteCoordinate(markedCell)).
                    getMarks().add(CONTENT_CONSTS.MARK.togglable);
        }
        if (puzzle.isMarkAroundEntrance()){
            for (Coordinates c : getEntranceCoordinates().getAdjacent()) {
                Eidolons.getGame().getCellByCoordinate(c).
                        getMarks().add(CONTENT_CONSTS.MARK.togglable);
            }
        }
        puzzle. getHandler(). markedCells.removeAll(puzzle.falseExits);
        puzzle.falseExits= filterExits(puzzle.falseExits);
        int i = RandomWizard.getRandomIndex(puzzle.falseExits);
        puzzle. realExit = puzzle.falseExits.remove(i);
        return template;
    }

    private boolean isExitToConvert(Coordinates exit) {
        Coordinates c = getEntranceCoordinates();
        return getAbsoluteCoordinate(exit).dst(c) < 3;
    }

    private List<Coordinates> filterExits(List<Coordinates> falseExits) {
        Coordinates c = getEntranceCoordinates();
        falseExits.removeIf(exit -> getAbsoluteCoordinate(exit).dst(c) < puzzle.getMinExitDst());
        Collections.shuffle(falseExits);
        falseExits = falseExits.stream().limit(puzzle.getMaxExits()).collect(Collectors.toList());
        return falseExits; 
    }

    private void transformMaze() {
        if (RandomWizard.random()) {
            template.setRotations(RandomWizard.random(),
                    RandomWizard.random(),
                    RandomWizard.random());
        }
        template.setFlip(RandomWizard.random(), RandomWizard.random());
    }




}
