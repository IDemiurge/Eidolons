package main.entity.obj;

import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;

public class MicroObj extends Obj {

    protected int x;
    protected int y;
    protected Coordinates coordinates;
    protected boolean overlaying;
    protected int z;
    protected Boolean overlayingInitialized;
    protected Coordinates originalCoordinates;
    private boolean invalidCoordinates;

    public MicroObj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public GenericGame getGame() {
        return (GenericGame) game;
    }

    public void init() {
        super.init();
        this.game = getGenericGame();


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


    public Coordinates getCoordinates() {
        if (coordinates == null || invalidCoordinates) {
            coordinates = Coordinates.get(getX(), getY());
            invalidCoordinates = false;
        }
        return coordinates;
    }

    public int getY() {
        return y;
    }


    public void setCoordinates(Coordinates c) {
        if (this.coordinates == null) {
            if (c.x == 0)
                if (c.y == 0) {
                    return;
                }
            originalCoordinates = c;
        }
        this.coordinates = c;
        this.y = c.y;
        setParam(G_PARAMS.POS_Y, y, true);
        this.x = c.x;
        setParam(G_PARAMS.POS_X, x, true);
    }

    public void setY(int y) {
        if (this.y == y) {
            return;
        }
        setCoordinates(Coordinates.get(getCoordinates().x, y));
    }

    public void setX(int x) {
        if (this.x == x) {
            return;
        }
        setCoordinates(Coordinates.get(x, getCoordinates().y));
    }
    // public void setCoordinates(Coordinates coordinates) {
    //
    //     this.coordinates = coordinates;
    //     setX(coordinates.getX());
    //     setY(coordinates.getY());
    // }
    //
    //
    // public void setY(int y) {
    //     if ( this.y == y){
    //         return;
    //     }
    //     invalidCoordinates= true;
    //     this.y = y;
    //     setParam(G_PARAMS.POS_Y, y, true);
    // }
    //
    // public void setX(int x) {
    //     if ( this.x == x){
    //         return;
    //     }
    //     invalidCoordinates= true;
    //     this.x = x;
    //     setParam(G_PARAMS.POS_X, x, true);
    // }

    public int getX() {
        return x;
    }

    @Override
    public void toBase() {
        coordinates = null;
        super.toBase();
        int x = getIntParam(G_PARAMS.POS_X);
        if (this.x != x)
            setX(x);
        int y = getIntParam(G_PARAMS.POS_Y);
        if (this.y != y)
            setY(y);
    }

    public boolean isOverlaying() {
        if (overlayingInitialized == null) {
            overlaying = checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.OVERLAYING)
                    || checkProperty(G_PROPS.CLASSIFICATIONS, "" + UnitEnums.CLASSIFICATIONS.ATTACHED);
            overlayingInitialized = true;
        }
        return overlaying;
    }

    @Override
    protected void addDynamicValues() {
    }

    @Override
    public void afterEffects() {
    }

    @Override
    public void newRound() {

    }

    @Override
    public void clicked() {
    }

    @Override
    public void resetPercentages() {
        getResetter().resetPercentages();

    }

    public boolean isTurnable() {
        return false;
    }

    public Coordinates getOriginalCoordinates() {
        return originalCoordinates;
    }

}
