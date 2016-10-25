package main.elements.conditions.standard;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref.KEYS;
import main.game.player.Player;

public class OwnershipCondition extends ConditionImpl {

    private String string;
    private String string2;
    private boolean neutral = false;
    private Player player;
    private boolean enemy = false;

    public OwnershipCondition(KEYS key, KEYS key2) {
        this.string = key.name();
        this.string2 = key2.name();
    }

    public OwnershipCondition(String string, String string2) {
        this.string = string;
        this.string2 = string2;
    }

    public OwnershipCondition(Boolean enemy, String string, String string2) {
        this.string = string;
        this.string2 = string2;
        this.enemy = enemy;
    }

    public OwnershipCondition(String string, Boolean neutral) {
        this.string = string;
        this.neutral = neutral;
    }

    public OwnershipCondition(String string, Player player) {
        this.string = string;
        this.player = player;
    }

    @Override
    public boolean check() {
        if (player != null)
            return ref.getObj(string).getOwner() == player;
        if (neutral)
            return ref.getObj(string).getOwner() == Player.NEUTRAL;

        if (enemy)
            return ref.getObj(string).getOwner() != Player.NEUTRAL
                    && ref.getObj(string).getOwner() != ref.getObj(string2)
                    .getOwner();
        else
            return ref.getObj(string).getOwner() == ref.getObj(string2)
                    .getOwner();
    }
}
