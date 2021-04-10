package eidolons.ability.conditions.shortcut;

import eidolons.ability.conditions.DC_Condition;
import eidolons.game.core.Eidolons;
import main.entity.Ref;

public class MainHeroCondition extends DC_Condition {
    @Override
    public boolean check(Ref ref) {
        return ref.getSourceObj() == Eidolons.getMainHero();
    }
}
