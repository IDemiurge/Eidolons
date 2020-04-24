package eidolons.game.netherflame.boss.anims;

import eidolons.game.netherflame.boss.BOSS_PART;
import eidolons.game.netherflame.boss.anims.view.BossPart;
import main.game.logic.event.Event;

public interface BossAnimHandler {
    void animate(BossPart part, BossAnims.BOSS_ANIM animType);

    void handleEvent(Event event);

    default PartAnim createAnim(BossAnims.BOSS_ANIM_COMMON idle, BOSS_PART type){

        return null;
    }
}
