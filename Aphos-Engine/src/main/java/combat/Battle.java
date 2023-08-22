package combat;

import combat.battlefield.BattleField;
import combat.init.BattleSetup;
import combat.state.BattleData;
import combat.sub.BattleManager;
import combat.sub.skirmish.SkirmishManager;
import elements.content.enums.types.MiscTypes;
import framework.entity.Entity;

/**
 * Created by Alexander on 6/10/2023
 * Replay - set of states? 
 */
public class Battle {
    public static Battle current;
    private BattleManager manager;
    private BattleSetup battleSetup;
    //state - round, phase, global data (nf level, ...)
    // BattleAi battleAi;

    public Battle(BattleSetup battleSetup) {
        this.battleSetup = battleSetup;
        current = this;
        // init();

         // state = createState();
        if (battleSetup.getBattleType()== MiscTypes.BattleType.Skirmish) {
            //so if manager encapsulates handlers - how big of a diff should this make?
            // will this be somewhat like metaGameHandlers?!
            // well, if it is all centralized.... maybe it won't be such a disaster?
            manager = new SkirmishManager(battleSetup);
        }
    }

    public <T extends Entity> T getById(Integer id, Class<T> entityClass ) {
       return  manager.getData().getEntityById(id, entityClass);
    }

    public void start(){

    }


    public BattleManager getManager() {
        return manager;
    }
}
