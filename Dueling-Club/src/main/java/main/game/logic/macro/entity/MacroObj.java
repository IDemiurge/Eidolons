package main.game.logic.macro.entity;

import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.logic.macro.MacroGame;
import main.game.logic.macro.MacroRef;
import main.game.logic.macro.map.Region;
import main.game.logic.macro.utils.MacroContentManager;
import main.game.logic.battle.player.Player;

public class MacroObj extends Obj {

    private Region region;

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
        add();
        MacroContentManager.addDefaultValues(getType());
        toBase();
    }

    public Region getRegion() {
        if (region == null) {
            region = getRef().getRegion();
        }
        return region;
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
}
