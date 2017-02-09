package main.entity.obj;

import main.content.CONTENT_CONSTS;
import main.content.parameters.G_PARAMS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.MicroGame;
import main.game.battlefield.Coordinates;
import main.game.player.Player;

public class MicroObj extends Obj {

    protected int x;
    protected int y;
    protected Coordinates coordinates;
    protected Boolean overlaying;
    private Coordinates lastKnownCoordinates;
    private int z;

    public MicroObj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public MicroGame getGame() {
        return (MicroGame) game;
    }

    public void init() {
        super.init();
        this.game = getGenericGame();
        cloneMaps(type);
        addDynamicValues();
        setParam(G_PARAMS.TURN_CREATED, game.getState().getRound());

    }

    public boolean canMove() {
        return false;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
        setParam(G_PARAMS.POS_Z, z, true);

    }

    public Coordinates getLastKnownCoordinates() {
        return lastKnownCoordinates;
    }

    public void setLastKnownCoordinates(Coordinates lastKnownCoordinates) {
        this.lastKnownCoordinates = lastKnownCoordinates;
    }

    public Coordinates getCoordinates() {
        if (coordinates == null) {
            coordinates = new Coordinates(getX(), getY());
        }
        coordinates.setX(getX());
        coordinates.setY(getY());
        // coordinates.setZ(getZ()); better use separately
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {

        this.coordinates = coordinates;
        setX(coordinates.getX());
        setY(coordinates.getY());
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        setParam(G_PARAMS.POS_Y, y, true);

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        setParam(G_PARAMS.POS_X, x, true);
    }

    public boolean isOverlaying() {
        if (overlaying == null) {
            overlaying = checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + CONTENT_CONSTS.BF_OBJECT_TAGS.OVERLAYING)
                    || checkProperty(G_PROPS.CLASSIFICATIONS, "" + CONTENT_CONSTS.CLASSIFICATIONS.ATTACHED);
        }
        return overlaying;
    }

    @Override
    protected void addDynamicValues() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterEffects() {
        // TODO Auto-generated method stub

    }

    @Override
    public void newRound() {

    }

    @Override
    public void clicked() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetPercentages() {
        // TODO Auto-generated method stub

    }

    public boolean isTurnable() {
        return false;
    }

}
