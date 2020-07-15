package eidolons.game.battlecraft.ai.tools.path.alphastar;

import main.game.bf.Coordinates;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;
import main.system.text.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

public class A_StarAlgorithm {

    private PathNode dest;
    private PathNode orig;
    private PathNode cur;
    private Path PATH;

    private List<PathNode> openList;
    private List<PathNode> closedList;

    private boolean agile;
    private boolean flying;
    private final PathingManager mngr;
    private final List<PathNode> nodeList;
    private int N;
    private boolean success;
    private LinkedList<Coordinates> targets;

    public A_StarAlgorithm(PathingManager mngr) {
        this.mngr = mngr;
        nodeList = mngr.getNodeList();
    }

    public Path getPath(boolean flying, boolean agile, Coordinates c1, Coordinates... targets) {
        this.targets = new LinkedList<>(Arrays.asList(targets));
        log(LogMaster.PATHING_DEBUG, "A* - Building path from " + c1
                + " to " + this.targets);
        N = 0;
        dest = getPathNode(this.targets.get(0));
        orig = getPathNode(c1);
        cur = orig;
        this.flying = flying;
        this.agile = agile;

        refresh();
        closedList.add(orig);

        while (true) {
            if (step()) {
                success = true;
                break;
            }
            if (openList.isEmpty()) {
                success = false;
                break;
            }
        }

        if (!success) {
            log(LogMaster.PATHING_DEBUG, "A* - Failed to Build path from " + c1
                    + " to " + this.targets + "\n" + closedList);
            return null;
        }
        constructPath();

        log(LogMaster.PATHING_DEBUG, "A* - Done with path from " + c1
                + " to " + this.targets + "\n" + PATH);
        return PATH;
    }

    private void refresh() {
        openList = new LinkedList<>();
        closedList = new LinkedList<>();
        for (PathNode node : nodeList) {
            node.reset();
        }
    }

    private void constructPath() {
        PATH = new Path(orig, dest);
        PathNode node = cur; // .getParent()
        if (node == null) {
            PATH.setAgile(agile);
            PATH.setFlying(flying);
            PATH.setCost(PathingManager.NO_PATH);
            return;
        }
        double cost = 0;
        while (true) {
            if (node == null) {
                break;
            }
            PATH.add(node);
            cost += node.getCost();

            node = node.getParent();
            if (node == orig) {
                break;
            }

        }

        PATH.setAgile(agile);
        PATH.setFlying(flying);
        PATH.setCost(cost);
    }

    private PathNode getPathNode(Coordinates c) {
        return mngr.getPathNode(c);
    }

    public boolean checkBetterPath(PathNode node) {
        return node.getG() < cur.getG();

    }

    public boolean step() {

        openList.clear();
        for (PathNode node : mngr.getAdjacentNodes(cur.getCoordinates())) {
            if (!isPassable(flying, agile, node)) {
                continue;
            }
            if (closedList.contains(node)) {
                continue;
            }

            if (openList.contains(node)) {
                if (checkBetterPath(node)) {
                    cur.setParent(node);
                }
            } else {
                openList.add(node);
            }
            setFGH(node);
        }
        if (openList.isEmpty()) {
            return false;
        }

        PathNode nextNode = getNodeWithLowestF();

        nextNode.setParent(cur);
        cur = nextNode;
        closedList.add(cur);
        cur.setCost(getCost(agile, flying, cur));

        if (targets.contains(cur.getCoordinates())) {
            return true;
        }
/*
must proceed until a goal node has lowest F among openList?
 */

        if (Log.check(Log.LOG_CASE.astar_pathing))
            log(LogMaster.PATHING_DEBUG, "A* Step #"
                    + N + " to " + cur + " with closed list " + closedList

            );
        N++;
        return false;
    }

    private double getCost(boolean agile, boolean flying, PathNode node) {
        PathNode parent = node.getParent();
        if (node.getX() != parent.getX() && node.getY() != parent.getY()) {
            if (agile) {
                return Math.sqrt(2);
            } else {
                return 2;
            }
        }
        return 1;
    }

    private void setFGH(PathNode node) {
        double g = heuristic(flying, false, orig.getCoordinates(), node.getCoordinates());
        double h = heuristic(flying, true, dest.getCoordinates(), node.getCoordinates());
        double f = g + h;
        node.setG(g);
        node.setH(h);
        node.setF(f);
        if (Log.check(Log.LOG_CASE.astar_pathing))
            log(LogMaster.PATHING_DEBUG, node
                    + " has: g = " + g + " h = " +
                    h + " f = " + f);
    }

    private PathNode getNodeWithLowestF() {

        double F = Integer.MAX_VALUE;
        PathNode NODE = null;
        for (PathNode node : openList) {

            if (node.getF() < F) {
                NODE = node;
                F = node.getF();
            }
        }
        return NODE;
    }

    public boolean isDone() {
        return openList.isEmpty() || closedList.contains(dest);
    }

    public double heuristic(boolean flying, boolean toDestination, Coordinates c1, Coordinates c2) {
        // euclidian distance for flying;
        // manhattan for non-aglie
        // for agile...?? never overestimate
        // TODO We could cache REAL distances as we do moving around obstacles...
        if (!toDestination)
            return  Math.sqrt(PositionMaster.getExactDistance(c1, c2));
        return PositionMaster.getExactDistance(c1, c2);
    }

    public boolean isPassable(boolean flying, boolean agile, PathNode node) {
        if (isFullyBlocked(node)) { //TODO what's that, some faster pre-check?
            return false;
        }
        if (!flying && isGroundBlocked(node)) {
            return false;
        }
        return !isDiagonallyBlocked(cur, node);
    }

    private boolean isFullyBlocked(PathNode node) {
        return mngr.isFullyBlocked(node);

    }

    private boolean isDiagonallyBlocked(PathNode node, PathNode node2) {
        return mngr.isDiagonallyBlocked(node, node2);

    }

    private boolean isGroundBlocked(PathNode node) {
        return mngr.isGroundBlocked(node);

    }
}

// private boolean isBlocked() {
// Coordinates c;
// for (Obj cell : manager.getAdjacentObjs(C, true)) {
// c = cell.getCoordinates();
// if (!prevList.contains(c))
// if (!agile) {
// if (!manager.isDiagonalBlocked(c, C))
// return false;
// }
// return false;
// }
// return true;
// }
// public int plotPath() {
// if (isBlocked())
// return -1;
//
// // preCheck if finished
// if (manager.isAdjacent(C, dest)) {
// return -1;
// }
// // count the path
// if (PositionMaster.inLine(C, prev)) {
// path++;
// }
// if (agile) {
// path++;
// } else if (manager.isDiagonalBlocked(C, dest)) {
// return -1;
//
// } else
// path = path + 2;
//
// // new path => prevList.clear();
// prev = C;
// prevList.add(C);
// for (Obj cell : getAdjacentCells()) {
//
// C = cell.getCoordinates();
// prevList.add(C);
// while (true) {
// int PATH = plotPath();
// if (PATH == -1)
// break;
// path += PATH;
// }
//
// }
// return path;
// }

// public Path getOptimalGroundPath(boolean agile, Coordinates c1, Coordinates
// c2) {
// this.dest = c2;
// this.orig = c1;
// this.prev = orig;
// this.agile = agile;
// this.result = Integer.MAX_VALUE;
// prevList = new LinkedList<Coordinates>();
// paths = new LinkedList<Integer>();
//
// List<Obj> adjacentCells = manager.getAdjacentObjs(c1, true);
// if (adjacentCells.isEmpty())
// return -1;
//
// for (Obj cell : adjacentCells) {
// C = cell.getCoordinates();
//
// if (isBlocked())
// continue;
// path = 0;
// this.prev = orig;
// prevList.clear();
// plotPath();
// if (path < result)
// result = path;
// }
//
// return PATH;
// }
//
/**
 * foreach adjacent cell plotPath(agile, c1, c2) depth first: keep building to c2 or blocked if (blocked(agile)) return
 * -1;
 * <p>
 * <?> how to know when to break from plotting - when all paths are tried or blocked? use foreach instead
 * <p>
 * <p>
 * newPath = plotPath(agile, c3, c2); path = min (path, newPath);
 * <p>
 * <p>
 * <p>
 * recursion: foreach getCells(): plotpath
 * <p>
 * use a field to keep track of the Path; reset it when the path is finished and stored in Paths[]
 */
