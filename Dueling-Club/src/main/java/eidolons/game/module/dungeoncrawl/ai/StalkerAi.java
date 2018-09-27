package eidolons.game.module.dungeoncrawl.ai;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.core.Eidolons;

/**
 * Created by EiDemiurge on 9/21/2018.
 *
 * TODO simple variant - added to aggro group if close by when combat starts
 */
public class StalkerAi extends AiBehavior {
    @Override
    public ActionSequence getOrders(UnitAI ai) {
        Unit hero = Eidolons.getMainHero();

        if (checkAttackable(hero, ai)){

        }

        return null;
    }

    private boolean checkAttackable(Unit hero, UnitAI ai) {
        GroupAI group = ai.getGroup();

        return false;
    }
}
