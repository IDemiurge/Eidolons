package eidolons.game.battlecraft.logic.battle.mission;

import eidolons.game.battlecraft.logic.battle.universal.*;
import eidolons.game.battlecraft.logic.battle.universal.stats.BattleStatManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class MissionBattleMaster extends BattleMaster<QuestMission> {


    public MissionBattleMaster(DC_Game game) {
        super(game);
    }

    public String getMissionResourceFolderPath() {
        return getBattle().getMission().getMissionResourceFolderPath();

    }

    @Override
    public CombatScriptExecutor getScriptManager() {
        return (CombatScriptExecutor) super.getScriptManager();
    }

    @Override
    public MetaGameMaster getMetaMaster() {
        return  super.getMetaMaster();
    }

    protected CombatScriptExecutor createScriptManager() {
        return new CombatScriptExecutor(this);
    }


    @Override
    protected QuestMission createBattle() {
        return new QuestMission(this);
    }

    @Override
    protected PlayerManager<QuestMission> createPlayerManager() {
        return new MissionPlayerManager(this);
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
