package combat;

import combat.init.BattleSetup;
import combat.sub.BattleManager;
import combat.sub.skirmish.SkirmishManager;
import combat.turns.CombatLoop;
import elements.content.enums.types.MiscTypes;

/**
 * Created by Alexander on 6/10/2023 Replay - set of states?
 */
public class Battle {
    public static Battle current;
    private BattleManager manager;
    CombatLoop combatLoop;
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
        combatLoop = new CombatLoop(manager);
    }

    public void end() {
        manager.battleEnds();
    }

    public void start() {
        manager.battleStarts();
        combatLoop.start();
        manager.newRound();
    }


    public BattleManager getManager() {
        return manager;
    }
}
