package eidolons.game.battlecraft.rules.combat.attack.accuracy;

import eidolons.game.battlecraft.logic.meta.universal.event.ChoiceEventOption;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import main.content.enums.entity.NewRpgEnums;

public class Deadeye extends ChoiceEventOption {
    Attack attack;
    NewRpgEnums.DeadeyeType type;

    public Deadeye(Attack attack, NewRpgEnums.DeadeyeType type) {
        this.attack = attack;
        this.type = type;
    }

    @Override
    public String getHoverDescription() {
        return null;
    }

    @Override
    public String getFlavorDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public boolean isBlocked() {
        return false;
    }
}
