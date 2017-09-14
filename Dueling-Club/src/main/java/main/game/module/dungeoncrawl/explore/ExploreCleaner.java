package main.game.module.dungeoncrawl.explore;

import main.content.PARAMS;
import main.entity.obj.unit.Unit;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExploreCleaner extends ExplorationHandler {

    public ExploreCleaner(ExplorationMaster master) {
        super(master);
    }

    public void cleanUpAfterAction(Unit unit) {
        removeMode(unit);
    }

    public void cleanUpAfterBattle() {
        Eidolons.getGame().getUnits().forEach(
         unit -> {
             unit.resetDynamicParam(PARAMS.C_N_OF_ACTIONS);
             removeMode(unit);

         });
    }

    private void removeMode(Unit unit) {
        if (unit.getMode() != null)
            if (unit.getBuff(unit.getMode().getBuffName()) != null)
                unit.getBuff(unit.getMode().getBuffName()).remove();
                unit.getMode(); //to reset
//             for (BuffObj buff : unit.getBuffs()) {
//                 if (checkBuffRemoved(buff))
//                     buff.remove();
//             }
    }


}
