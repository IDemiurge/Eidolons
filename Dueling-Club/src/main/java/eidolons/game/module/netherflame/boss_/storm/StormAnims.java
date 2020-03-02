package eidolons.game.module.netherflame.boss_.storm;

import eidolons.game.module.netherflame.boss_.anims.BossAnims;

public enum StormAnims implements BossAnims.BOSS_ANIM {
    spikes_fly,

    pearl_cast,

    ult_crush,
;

    private BossAnims.BOSS_ANIM_TYPE type;
    private boolean allParts;
    private boolean symmetric;
    private String sfx;
    private int variations;

    @Override
    public int getVariations() {
        return variations;
    }

    @Override
    public String getSuffix() {
        return sfx;
    }

    @Override
    public boolean isSymmetric() {
        return symmetric;
    }

    @Override
    public boolean isAllParts() {
        return allParts;
    }

    @Override
    public BossAnims.BOSS_ANIM_TYPE getType() {
        return type;
    }
}
