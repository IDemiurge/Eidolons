package main.game.bf.pathing;

import main.entity.obj.MicroObj;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

public class Path {
    DequeImpl<PathNode> nodes;
    boolean agile;
    double cost;
    PathNode origin, destination;
    private boolean flying;

    public Path(PathNode origin, PathNode destination) {
        nodes = new DequeImpl<>();
        this.origin = origin;
        this.destination = destination;
    }

    public Path(DequeImpl<PathNode> nodes, double cost) {
        this.nodes = nodes;
        this.cost = cost;
        origin = nodes.get(0);
        destination = nodes.get(nodes.size() - 1);
    }

    // nodes?
    @Override
    public String toString() {

        return ((agile) ? " agile " : "")
                + ((flying) ? " flying path" : " path: ") + nodes.toString()
                + " cost = " + cost;
    }

    public void add(PathNode node) {
        nodes.add(node);

    }

    public double traverse(MicroObj obj) {
        // TODO highlight origin!!!!
        obj.getGame().getManager().clearLastTraversedPath();
        double result = 0;
        origin.setGame(obj.getGame());
//		origin.highlightOrigin();
        // for (PathNode node : nodes) {
        PathNode node = null;
        boolean interrupted = false;
        for (int i = nodes.size() - 1; i >= 0; i--) {

            node = nodes.get(i);
//			double cost = node.traverse(obj, this);
            if (cost == -1) {
                LogMaster
                        .log(LogMaster.COMBAT_DEBUG, "Path traversal stopped at "
                                + node.getCoordinates());
                interrupted = true;
                break;
            }
            result += cost;

        }
        if (interrupted) {
            DequeImpl<PathNode> NODES = nodes;
            for (int i = nodes.size() - 1; i >= 0; i--) {
                PathNode NODE = nodes.get(i);
                NODES.add(NODE);
                if (NODE == node) {
                    break;
                }
            }
            LogMaster
                    .log(LogMaster.COMBAT_DEBUG, "Path traversal interrupted "
                            + this);
            obj.getGame().getManager()
                    .setLastTraversedPath(new Path(NODES, result));
        } else {
            obj.getGame().getManager().setLastTraversedPath(this);
        }
        return result;
    }

    public void clearGraphics() {
//		origin.clearGraphics();
        for (PathNode node : nodes) {
            try {
//				node.clearGraphics();
            } catch (Exception e) {
                // main.system.auxiliary.LogMaster.log(LogMaster
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    public DequeImpl<PathNode> getNodes() {
        return nodes;
    }

    public void setNodes(DequeImpl<PathNode> nodes) {
        this.nodes = nodes;
    }

    public boolean isAgile() {
        return agile;
    }

    public void setAgile(boolean agile) {
        this.agile = agile;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost2) {
        this.cost = cost2;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public int getIntegerCost() {
        if (cost > 1 && cost < 2) {
            return 2;
        }

        return (int) Math.round(cost);

    }

}
