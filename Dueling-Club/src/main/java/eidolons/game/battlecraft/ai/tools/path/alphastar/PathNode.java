package eidolons.game.battlecraft.ai.tools.path.alphastar;

import eidolons.game.core.game.DC_Game;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

import java.util.List;

public class PathNode {
    Coordinates coordinates;
    PathNode parent;
    List<PathNode> children;
    Obj cell;
    double G;
    double F;
    double H;
    private double cost;
    private DC_Game game;
    private int TRAVERSAL_DELAY = 100;

    public PathNode(Coordinates c, PathNode parent) {

        this.coordinates = c;
        this.parent = parent;
    }

    public PathNode(Coordinates c) {
        this.coordinates = c;
    }

    @Override
    public String toString() {
        return "node " + coordinates.toString()
                // + ((parent != null) ? " parent: " + parent.getCoordinates()
                // : "" )
                ;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public List<PathNode> getChildren() {
        return children;
    }

    public void setChildren(List<PathNode> children) {
        this.children = children;
    }

    public int getY() {
        return coordinates.y;
    }

    public int getX() {
        return coordinates.x;
    }

    public void reset() {
        setParent(null);
        setCost(0);
        setG(0);
        setH(0);
        setF(0);
    }

    public double getG() {
        return G;
    }

    public void setG(double g) {
        G = g;
    }

    public double getF() {
        return F;
    }

    public void setF(double f) {
        F = f;
    }

    public double getH() {
        return H;
    }

    public void setH(double h) {
        H = h;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }


    public int getTRAVERSAL_DELAY() {
        return TRAVERSAL_DELAY;
    }

    public void setTRAVERSAL_DELAY(int tRAVERSAL_DELAY) {
        TRAVERSAL_DELAY = tRAVERSAL_DELAY;
    }

}
