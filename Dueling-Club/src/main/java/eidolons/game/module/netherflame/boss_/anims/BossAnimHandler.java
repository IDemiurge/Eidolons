package eidolons.game.module.netherflame.boss_.anims;

import eidolons.game.module.netherflame.boss_.BOSS_PART;
import main.game.logic.event.Event;

public interface BossAnimHandler {
    void animate(BossPart part, BossAnims.BOSS_ANIM animType);

    void handleEvent(Event event);

    default PartAnim createAnim(BossAnims.BOSS_ANIM_COMMON idle, BOSS_PART type){

        return null;
    }
}
