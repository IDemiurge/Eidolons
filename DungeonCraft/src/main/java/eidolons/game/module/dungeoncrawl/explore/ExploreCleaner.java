package eidolons.game.module.dungeoncrawl.explore;

import eidolons.ability.effects.oneshot.mechanic.ModeEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.StealthRule;
import eidolons.game.core.Core;
import eidolons.game.core.master.EffectMaster;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.SpellEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
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
            ModeEffect e = (ModeEffect) EffectMaster.getFirstEffectOfClass(activeObj, ModeEffect.class);
            if (e != null)
                if (e.getMode() == unit.getMode())
                    return;
        }
        removeMode(unit);
    }

    public void cleanUpAfterBattle() {
        for (Unit unit : new ArrayList<>(Core.getGame().getUnits())) {
            {
                if (unit.isSummoned()){
                    if (unit.getRef().getObj(Ref.KEYS.SPELL).checkProperty(G_PROPS.SPELL_TAGS,
                            SpellEnums.SPELL_TAGS.COMBAT_ONLY.toString())) {
                        unit.removeFromGame();
                    }
                }
                removeMode(unit);
                BuffObj buff = unit.getBuff(StealthRule.SPOTTED);
                if (buff != null)
                    buff.remove();
            }
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
