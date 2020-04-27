package eidolons.game.module.dungeoncrawl.explore;

import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.target.EffectFinder;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.StealthRule;
import eidolons.game.core.Eidolons;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.ActiveObj;
import main.entity.obj.BuffObj;

import java.util.ArrayList;

/**
 * Created by JustMe on 9/11/2017.
 */
public class ExploreCleaner extends ExplorationHandler {

    public ExploreCleaner(ExplorationMaster master) {
        super(master);
    }

    public void cleanUpAfterAction(DC_ActiveObj activeObj, Unit unit) {
        if (unit == null) {
            return;
        }
        if (activeObj.getActionGroup() == ACTION_TYPE_GROUPS.MODE) {
            ModeEffect e = (ModeEffect) EffectFinder.getFirstEffectOfClass(activeObj, ModeEffect.class);
            if (e != null)
                if (e.getMode() == unit.getMode())
                    return;
        }
        removeMode(unit);
    }

    public void cleanUpAfterBattle() {
        for (Unit unit : new ArrayList<>(Eidolons.getGame().getUnits())) {
            {
                if (unit.isScion()) {
                    unit.kill(); //TODO lazy..
                    return;
                }
                if (unit.isSummoned()){
                    if (unit.getRef().getObj(Ref.KEYS.SPELL).checkProperty(G_PROPS.SPELL_TAGS,
                            SpellEnums.SPELL_TAGS.COMBAT_ONLY.toString())) {
                        unit.removeFromGame();
                    }
                }
                if (unit.isPlayerCharacter()) {
                    if (unit.getIntParam(PARAMS.FOCUS_FATIGUE)>0) {
                        unit.  getGame().getLogManager().log(unit+ "'s focus fatigue is reset "+
                                unit.getIntParam(PARAMS.FOCUS_FATIGUE) );
                    }
                }
                unit.setParam(PARAMS.FOCUS_FATIGUE, 0);
                unit.resetDynamicParam(PARAMS.C_N_OF_ACTIONS);
                removeMode(unit);
                BuffObj buff = unit.getBuff(StealthRule.SPOTTED);
                if (buff != null)
                    buff.remove();
                cleanUpActions(unit);
            }
        }
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
