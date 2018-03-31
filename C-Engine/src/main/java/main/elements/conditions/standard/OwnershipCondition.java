package main.elements.conditions.standard;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.logic.battle.player.Player;

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
    public boolean check(Ref ref) {
        Obj obj = ref.getObj(string);
        Obj obj2 = ref.getObj(string2);
//        if (obj==null )
//            return  false;
        if (player != null) {
            return obj.getOwner() == player;
        }
        if (neutral) {
            return obj.getOwner() == Player.NEUTRAL;
        }

        if (enemy) {
            return obj.getOwner() != Player.NEUTRAL
             && obj.getOwner() != obj2
             .getOwner();
        } else {
            return obj.getOwner() == obj2
             .getOwner();
        }
    }
}
