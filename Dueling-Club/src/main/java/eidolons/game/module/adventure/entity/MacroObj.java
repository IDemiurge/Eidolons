package eidolons.game.module.adventure.entity;

import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.adventure.MacroRef;
import eidolons.game.module.adventure.faction.Faction;
import eidolons.game.module.adventure.map.MacroCoordinates;
import eidolons.game.module.adventure.map.Region;
import eidolons.game.module.adventure.utils.MacroContentManager;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.logic.battle.player.Player;

public class MacroObj extends Obj {

    protected int x;
    protected int y;
    protected Region region;
    protected MacroCoordinates coordinates;
    private Faction faction;

    public MacroObj(MacroGame game, ObjType type, Ref ref, Player owner) {
        super(type, owner, game, ref);
    }

    public MacroObj(MacroGame game, ObjType type, Ref ref) {
        super(type, Player.NEUTRAL, game, ref);
    }

    public MacroObj(ObjType type, MacroRef ref) {
        this(MacroGame.getGame(), type, ref);
    }

    public MacroObj(ObjType type) {
        this(MacroGame.getGame(), type, MacroGame.getGame().getRef());
    }

    public void newTurn() {

    }

    public Coordinates getCoordinates() {
        if (coordinates == null) {
            coordinates = new MacroCoordinates(getX(), getY());
        }
        return coordinates;
    }

    public void setCoordinates(MacroCoordinates coordinates) {
        this.coordinates = coordinates;
        setX(coordinates.getX());
        setY(coordinates.getY());
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        setParam(MACRO_PARAMS.MAP_POS_Y, y, true);
        getCoordinates().setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        setParam(MACRO_PARAMS.MAP_POS_X, x, true);
        getCoordinates().setX(x);
    }


    @Override
    public MacroRef getRef() {
        return (MacroRef) super.getRef();
    }

    @Override
    public MacroGame getGame() {
        return (MacroGame) super.getGame();
    }

    @Override
    public void init() {
        addToState();
        MacroContentManager.addDefaultValues(getType());
        toBase();
    }

    public Region getRegion() {
//        if (region == null) {
        return getRef().getRegion();
//        }
//        return region;
    }

	/*
     * Let's discover what true motivation is like, the true nature of this
	 * endeavor.
	 * 
	 * The depths of the RPG... A new foundation for old dreams
	 * 
	 * Writing I am willing to start building up real materials - for
	 * introductions, descriptions, faction dialogues
	 */

    @Override
    protected void addDynamicValues() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetPercentages() {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterEffects() {
        // TODO Auto-generated method stub

    }

    public String getSaveData() {
        return null;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
}
