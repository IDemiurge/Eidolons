package main.game.battlecraft.logic.battle.mission;

import main.data.filesys.PathFinder;
import main.game.battlecraft.logic.battle.universal.*;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.game.DC_Game;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionBattleMaster extends BattleMaster<MissionBattle> {

    MissionScriptManager scriptManager;

    public MissionBattleMaster(DC_Game game) {
        super(game);
        scriptManager = createScriptManager();

    }

    public String getMissionResourceFolderPath() {
        return StringMaster.buildPath(PathFinder.getScenariosPath()  ,
         getMetaMaster().getMetaGame().getScenario().getName(),
         getBattle().getMission().getName());

    }

    @Override
    public ScenarioMetaMaster getMetaMaster() {
        return (ScenarioMetaMaster) super.getMetaMaster();
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
