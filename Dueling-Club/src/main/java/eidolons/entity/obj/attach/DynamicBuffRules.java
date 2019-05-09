package eidolons.entity.obj.attach;

import eidolons.ability.effects.attachment.AddBuffEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.BattleHandler;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.core.game.DC_Game;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.obj.BuffObj;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;

public class DynamicBuffRules  {
    DC_Game game;

    public DynamicBuffRules(DC_Game game) {
        this.game = game;
    }

    public void checkBuffs(Unit unit) {
//on top of buffRules?
        for (BuffObj buff :     new ArrayList<>(unit.getBuffs())) {
            if (buff.isDynamic()) {
                buff.remove();
            }
        }
        if (unit.isScion()) {
            if (game.getMetaMaster() instanceof IGG_MetaMaster) {
                int sfx = ((IGG_MetaMaster) game.getMetaMaster()).getShadowMaster().getTimeLeft();
                addDynamicBuff("Shadow of Death", unit, ": " + sfx);
            }
        }



        if (unit.checkDualWielding()) {
//            new AddBuffEffect(type, fx, true);
            addDynamicBuff("Dual Wielding", unit, "(" +
                    (unit.getIntParam(PARAMS.DUAL_WIELDING_MASTERY) * 100 / 20) +
                    "% mastery)");

        }
        if (unit.getIntParam(PARAMS.FOCUS_FATIGUE) > 0) {
            addDynamicBuff("Focus Fatigue", unit, StringMaster.wrapInParenthesis(unit.getIntParam(PARAMS.FOCUS_FATIGUE) + ""));
        }

    }

    private void addDynamicBuff(String name, Unit unit, String suffx) {

        BuffObj buff = new DC_BuffObj(name, unit, 0);
        if (!StringMaster.isEmpty(suffx)) {
            buff.setName(name + " " + suffx);
            String descr = buff.getDescription();
            descr = VariableManager.substitute(descr, suffx);
            buff.setProperty(G_PROPS.DESCRIPTION, descr);
        }
        buff.setTransient(true);
        buff.setDynamic(true);
        unit.getGame().getManager().buffCreated(buff, unit);
//        unit.addBuff(buff);
    }

}
