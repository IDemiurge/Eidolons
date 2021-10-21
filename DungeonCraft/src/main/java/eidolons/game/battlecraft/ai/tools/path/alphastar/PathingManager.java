package eidolons.game.battlecraft.ai.tools.path.alphastar;

import eidolons.ability.conditions.special.ClearShotCondition;
import main.entity.Entity;
import main.game.bf.BattleFieldGrid;
import main.game.bf.Coordinates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.core.Core.getGame;

/**
 * not thread safe!
 *
 * @author JustMe
 */
public class PathingManager {
    public static final double NO_PATH = -1;
    A_StarAlgorithm alg;
    private PathNode[][] nodeGrid;
    private final List<PathNode> nodeList = new LinkedList<>();
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

    public PathNode getPathNode(Coordinates c) {
        return new PathNode(c);
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

    public boolean isDiagonallyBlocked(Coordinates c, Coordinates c2) {
        //TODO wall block must be considered anyway
        if (!(c.x != c2.x && c.y != c2.y)) {
            return false;
        }
        return !new ClearShotCondition().check(c, c2);
    }


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

    public BattleFieldGrid getGrid() {
        return getGame().getGrid();
    }

    public List<PathNode> getNodeList() {
        return nodeList;
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
        return Arrays.stream(c.getAdjacent()).map(c1 -> nodeGrid[c1.x][c1.y]).collect(Collectors.toList());
        // List<PathNode> nodeList = new LinkedList<>();
        //
        // checkNode(nodeList, c.x + 1, c.y - 1);
        // checkNode(nodeList, c.x, c.y - 1);
        // checkNode(nodeList, c.x - 1, c.y - 1);
        //
        // checkNode(nodeList, c.x + 1, c.y + 1);
        // checkNode(nodeList, c.x, c.y + 1);
        // checkNode(nodeList, c.x - 1, c.y + 1);
        //
        // checkNode(nodeList, c.x - 1, c.y);
        // checkNode(nodeList, c.x + 1, c.y);
        //
        // return nodeList;
    }

}
