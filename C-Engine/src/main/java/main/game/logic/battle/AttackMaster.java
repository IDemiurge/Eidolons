package main.game.logic.battle;

import main.ability.effects.Effect;
import main.entity.Ref;

public interface AttackMaster {

    boolean attack(Ref ref, boolean free, boolean canCounter, Effect onHit, Effect onKill, boolean offhand, boolean counter);

}
