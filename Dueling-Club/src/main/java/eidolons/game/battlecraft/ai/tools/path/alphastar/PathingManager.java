package eidolons.game.battlecraft.ai.tools.path.alphastar;

import eidolons.ability.conditions.special.ClearShotCondition;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

import static eidolons.game.core.Eidolons.getGame;

/**
 * not thread safe!
 *
 * @author JustMe
 */
public class PathingManager {
    public static final double NO_PATH = -1;
    A_StarAlgorithm alg;
    private PathNode[][] nodeGrid;
    private List<PathNode> nodeList = new LinkedList<>();
    IPathHandler handler;

    public PathingManager(  IPathHandler handler) {
        this.handler = handler;
    }

    public Path getPath(boolean flying, boolean agile, Coordinates c1, Coordinates... targets) {
        if (nodeGrid == null) {
            initNodeGrid();
        }
        if (alg == null) {
            alg = new A_StarAlgorithm(this);
        }
        return alg.getPath(flying, agile, c1, targets);

    }

    private void initNodeGrid() {
        //TODO gdx Review - re-init smaller grid?
        nodeGrid = new PathNode[handler.getWidth()+4][handler.getHeight()+4];
        for (Coordinates c : getGrid().getCoordinatesList()) {
            PathNode node = new PathNode(c);
            nodeGrid[c.x][c.y] = node;
            getNodeList().add(node);
        }

    }

    public PathNode getPathNode(Coordinates c) {
        return new PathNode(c);
    }
    public boolean checkPassable(boolean agile, Coordinates c1, Coordinates c2) {
        return (PositionMaster.inLine(c1, c2) || agile);
    }

    public boolean isDiagonallyBlocked(Coordinates c, Coordinates c2) {
        //TODO wall block must be considered anyway
        if (!(c.x != c2.x && c.y != c2.y)) {
            return false;
        }
        return !new ClearShotCondition().check(c, c2);
    }

    // public boolean isOccupied(Coordinates c) {
    //     return handler.isOccupied(c);
    // }

    public boolean isGroundPassable(Entity obj, Coordinates c) {
        return handler.canMoveOnto(obj, c);
    }

    public boolean isGroundBlocked(PathNode node) {
        return !isGroundPassable(handler.getUnit(), node.getCoordinates());
    }

    public boolean isDiagonallyBlocked(PathNode node, PathNode node2) {
        return isDiagonallyBlocked(node.getCoordinates(), node2.getCoordinates());
    }

    public boolean isFullyBlocked(PathNode node) {
        return false;
    }

    public void checkCoordinatesForObj(List<Obj> list, Coordinates c, int i, int j, boolean cell) {
        Coordinates c1 = new Coordinates(c.x + i, c.y + j);
        Obj obj = null;
        try {
            obj = (cell) ? getCell(c1) : getObj(c1);
        } catch (Exception e) {

        }
        if (obj != null) {
            list.add(obj);
        }
    }

    public List<Obj> getAdjacentObjs(Coordinates coordinates, boolean cell) {

        List<Obj> list = new LinkedList<>();
        // for (xXx:MathMaster.permutations(bla!)){
        checkCoordinatesForObj(list, coordinates, 0, 1, cell);
        checkCoordinatesForObj(list, coordinates, 1, 0, cell);
        checkCoordinatesForObj(list, coordinates, 1, 1, cell);
        checkCoordinatesForObj(list, coordinates, -1, -1, cell);
        checkCoordinatesForObj(list, coordinates, 1, -1, cell);
        checkCoordinatesForObj(list, coordinates, -1, 1, cell);
        checkCoordinatesForObj(list, coordinates, 0, -1, cell);
        checkCoordinatesForObj(list, coordinates, -1, 0, cell);

        return list;
    }

    public boolean isAdjacent(Coordinates c1, Coordinates c2) {
        return (PositionMaster.getX_Diff(c1, c2) <= 1 && PositionMaster.getY_Diff(c1, c2) <= 1);
    }

    public boolean isAdjacent(Obj obj1, Obj obj2) {
        return (PositionMaster.getX_Diff(obj1, obj2) <= 1 && PositionMaster.getY_Diff(obj1, obj2) <= 1);
    }

    public Obj getObj(Coordinates c1) {
        return handler.getObj(c1);
    }

    public Obj getCell(Coordinates c1) {
        return getGrid().getCell(c1);
    }

    public BattleFieldGrid getGrid() {
        return getGame().getGrid();
    }

    public PathNode[][] getNodeGrid() {
        return nodeGrid;
    }

    public void setNodeGrid(PathNode[][] nodeGrid) {
        this.nodeGrid = nodeGrid;
    }

    public List<PathNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<PathNode> nodeList) {
        this.nodeList = nodeList;
    }

    private void checkNode(List<PathNode> nodeList, int x, int y) {
        PathNode node;
        try {
            node = nodeGrid[x][y];
        } catch (Exception e) {
            node = null;
        }
        if (node != null) {
            nodeList.add(node);
        }
    }

    public List<PathNode> getAdjacentNodes(Coordinates c) {
        List<PathNode> nodeList = new LinkedList<>();

        checkNode(nodeList, c.x + 1, c.y - 1);
        checkNode(nodeList, c.x, c.y - 1);
        checkNode(nodeList, c.x - 1, c.y - 1);

        checkNode(nodeList, c.x + 1, c.y + 1);
        checkNode(nodeList, c.x, c.y + 1);
        checkNode(nodeList, c.x - 1, c.y + 1);

        checkNode(nodeList, c.x - 1, c.y);
        checkNode(nodeList, c.x + 1, c.y);

        return nodeList;
    }

}
