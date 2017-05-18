package main.game.battlecraft.logic.battle.mission;

import main.game.battlecraft.logic.battle.universal.*;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionBattleMaster extends BattleMaster<MissionBattle> {

    MissionScriptManager scriptManager;

    public MissionBattleMaster(DC_Game game) {
        super(game);
        scriptManager= createScriptManager();

    }

    private MissionScriptManager createScriptManager() {
        return new MissionScriptManager(this);
    }

    public MissionScriptManager getScriptManager() {
        return scriptManager;
    }

    @Override
    protected MissionBattle createBattle() {
        return new MissionBattle(this);
    }

    @Override
    protected PlayerManager<MissionBattle> createPlayerManager() {
        return new PlayerManager<>(this);
    }

    @Override
    protected BattleOutcomeManager createOutcomeManager() {
        return new MissionOutcomeManager(this);
    }

    @Override
    protected BattleConstructor createConstructor() {
        return new MissionConstructor(this);
    }

    @Override
    protected BattleStatManager createStatManager() {
        return new MissionStatManager(this);
    }

    @Override
    protected BattleOptionManager createOptionManager() {
        return new MissionOptionManager(this);
    }
}
