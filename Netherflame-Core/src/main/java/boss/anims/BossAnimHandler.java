package boss.anims;

import boss.BOSS_PART;
import boss.anims.old.BossPart;
import boss.anims.old.PartAnim;
import boss.logic.BossCycle;
import main.game.logic.event.Event;

public interface BossAnimHandler {
    void animate(BossPart part, BossAnims.BOSS_ANIM animType);

    void handleEvent(Event event);

    default PartAnim createAnim(BossAnims.BOSS_ANIM_COMMON idle, BOSS_PART type){

        return null;
    }

    void toggleActive(BossCycle.BOSS_TYPE type, boolean active);
}
