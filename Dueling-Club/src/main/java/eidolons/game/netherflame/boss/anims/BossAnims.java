package eidolons.game.netherflame.boss.anims;

public class BossAnims {
    /*

     */

    //for all parts at once
    public enum BOSS_ANIM_COMMON {
        hit,
        hit_large,
        appear,
        death, idle, attack,


    }

    public enum BOSS_ANIM_TYPE {
        attack,
        spell,
        hit,
        death,
        ultimate,

    }

    public interface BOSS_ANIM {

        int getVariations();

        String getSuffix();

        boolean isSymmetric();

        boolean isAllParts();

        BOSS_ANIM_TYPE getType();
    }
}
