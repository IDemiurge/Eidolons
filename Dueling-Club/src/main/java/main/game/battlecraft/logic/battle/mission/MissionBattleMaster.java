package main.game.battlecraft.logic.battle.mission;

import main.game.battlecraft.logic.battle.*;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionBattleMaster extends BattleMaster<Mission> {


    public MissionBattleMaster(DC_Game game) {
        super(game);
    }

    @Override
    protected Mission createBattle() {
        return new Mission(this);
    }

    @Override
    protected PlayerManager<Mission> createPlayerManager() {
        return null;
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
