package eidolons.game.module.netherflame.boss;

public class BossDataMaster {
/*
syncing the animations

 */
    public static void main(String[] a) {
        String name = new BossData().toString();
        main.system.auxiliary.log.LogMaster.log(1," " +name);
    }
    public enum SHARD_CORE_TYPE{

    }
    //FX on all parts? I could make AE duplicates and underlay them with microdelay == TRAIL!
    public enum SHARD_FORCE_TYPE{
FLAME, LIGHTNING, INK,
    }
    public enum SHARD_OVERLAY_TYPE{
MIST, FIRE, WATER, SWARM, DEBRIS, LAVA, SMOKE, SNOW, ASH, POISON,
    }
    public enum SHARD_LIMB{

    }
}
