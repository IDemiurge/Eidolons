package eidolons.game.module.dungeoncrawl.explore;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.core.Eidolons;
import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.values.properties.G_PROPS;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;
import eidolons.game.battlecraft.logic.battlefield.vision.StealthRule;

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
            if (e != null)
                if (e.getMode() == unit.getMode())
                    return;
        }
        removeMode(unit);
    }

    public void cleanUpAfterBattle() {
        Eidolons.getGame().getUnits().forEach(
         unit -> {
             unit.resetDynamicParam(PARAMS.C_N_OF_ACTIONS);
             removeMode(unit);
             BuffObj buff = unit.getBuff(StealthRule.SPOTTED);
             if (buff != null)
                 buff.remove();
             cleanUpActions(unit);
         });
    }

    private void cleanUpActions(Unit unit) {
        for (ActiveObj activeObj : unit.getActives()) {
            activeObj.setParam(PARAMS.C_COOLDOWN, activeObj.getIntParam(PARAMS.COOLDOWN, false));
        }
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
