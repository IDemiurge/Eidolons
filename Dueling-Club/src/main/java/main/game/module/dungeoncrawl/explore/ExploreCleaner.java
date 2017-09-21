package main.game.module.dungeoncrawl.explore;

import main.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.PARAMS;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.target.EffectFinder;
import main.game.core.Eidolons;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExploreCleaner extends ExplorationHandler {

    public ExploreCleaner(ExplorationMaster master) {
        super(master);
    }

    public void cleanUpAfterAction(DC_ActiveObj activeObj, Unit unit) {
        if (activeObj.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {
            ModeEffect e = (ModeEffect) EffectFinder.getFirstEffectOfClass(activeObj, ModeEffect.class);
            if (e!= null )
                if (e.getMode()== unit.getMode())
                    return ;
        }
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
                unit.setProperty(G_PROPS.MODE, "");
                unit.getMode(); //to reset
//             for (BuffObj buff : unit.getBuffs()) {
//                 if (checkBuffRemoved(buff))
//                     buff.remove();
//             }
    }


}
