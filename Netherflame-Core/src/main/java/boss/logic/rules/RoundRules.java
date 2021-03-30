package boss.logic.rules;

import boss.BossHandler;
import boss.BossManager;
import boss.BossModel;

public class RoundRules<T extends BossModel> extends BossHandler<T> {
    public RoundRules(BossManager<T> manager) {
        super(manager);
    }
     /*
    create ultimate clock and trigger
    handle transit in and out
    alter rules?
BossRound round;
could be used
     */

    public void loadRound() {
/*
new coordinates to teleport to - self and boss
in LE: marker for boss and hero spawn coords
 */

    }

    public void roundEnds() {

    }

    public void roundStarts() {
        for (BossHandler<T> handler : getHandlers()) {
            handler.roundStarts();
        }
    }

    public void bossDeath() {

    }

    public void ultimate() {

    }

}
