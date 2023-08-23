package combat;

import combat.battlefield.BattleField;
import combat.init.BattleSetup;
import combat.state.BattleData;
import combat.sub.BattleManager;
import combat.sub.skirmish.SkirmishManager;
import elements.content.enums.types.MiscTypes;
import framework.entity.Entity;
import framework.entity.field.Unit;

/**
 * Created by Alexander on 6/10/2023 Replay - set of states?
 */
public class Battle {
    public static Battle current;
    private BattleManager manager;
    //state - round, phase, global data (nf level, ...)
    // BattleAi battleAi;

    public Battle(BattleSetup battleSetup) {
        current = this;
        // init();

        // state = createState();
        if (battleSetup.getBattleType() == MiscTypes.BattleType.Skirmish) {
            //so if manager encapsulates handlers - how big of a diff should this make?
            // will this be somewhat like metaGameHandlers?!
            // well, if it is all centralized.... maybe it won't be such a disaster?
            manager = new SkirmishManager(battleSetup);
        }
    }

    public void end() {
        manager.battleEnds();
    }
    public void start() {
        manager.battleStarts();
        manager.newRound();
    }


    public BattleManager getManager() {
        return manager;
    }
}
