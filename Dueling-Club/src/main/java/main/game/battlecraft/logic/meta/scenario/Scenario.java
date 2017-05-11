package main.game.battlecraft.logic.meta.scenario;

import main.content.PARAMS;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.scenario.ScenarioMaster.SCENARIO_MODES;
import main.game.core.game.DC_Game;
import main.game.battlecraft.logic.battle.DC_Player;

import java.util.List;

public class Scenario extends Entity {

    private SCENARIO_MODES mode;
    private List<Unit> heroesForHire;

    public Scenario(ObjType type) {
        super(type, DC_Player.NEUTRAL, DC_Game.game, new Ref());

    }

    private void initHeroesForHire() {
//		getProperty(MACRO_PROPS.MISSION_HEROES_FOR_HIRE);
//
//		if (mode == SCENARIO_MODES.RPG_MODE) {
//			getProperty(prop);
//
//			DataManager.getTypesSubGroup(OBJ_TYPES.CHARS, "Scenario");
//		}

    }

    public void init() {
        toBase();
    }

    public int getMaxHeroLevel() {
        return getIntParam(PARAMS.MAX_LEVEL);
    }

    public int getMinHeroLevel() {
        return getIntParam(PARAMS.MIN_LEVEL);
    }

    public SCENARIO_MODES getMode() {
        return mode;
    }

    public void setMode(SCENARIO_MODES mode) {
        this.mode = mode;
    }

    public List<Unit> getHeroesForHire() {
        if (heroesForHire == null) {
            initHeroesForHire();
        }
        return heroesForHire;
    }

}
