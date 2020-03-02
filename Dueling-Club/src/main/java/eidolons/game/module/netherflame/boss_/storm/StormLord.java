package eidolons.game.module.netherflame.boss_.storm;

import eidolons.game.module.netherflame.boss_.BOSS_PART;
import eidolons.game.module.netherflame.boss_.BossModel;

public class StormLord extends BossModel {


    public enum STORM_PARTS implements BOSS_PART {
    upper_core,
    lower_core,
    upper_hand,
    upper_hand_2,
    lower_hand,
    lower_hand_2,
    ;
    String[] anims;

    STORM_PARTS(String... anims) {
        this.anims = anims;
    }

    @Override
    public String[] getAnims() {
        return anims;
    }
}

}
