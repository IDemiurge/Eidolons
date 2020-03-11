package eidolons.game.netherflame.boss;

import static eidolons.game.netherflame.boss.BossDataMaster.*;

public class BossData {
    SHARD_CORE_TYPE coreType;
    SHARD_FORCE_TYPE forceType;
    SHARD_OVERLAY_TYPE overlayType;
    SHARD_LIMB[] limbs;

    public BossData() {
//        BossDataMaster.randomInit(this);
    }

    public BossData(SHARD_CORE_TYPE coreType, SHARD_FORCE_TYPE forceType, SHARD_OVERLAY_TYPE overlayType, SHARD_LIMB... limbs) {
        this.coreType = coreType;
        this.forceType = forceType;
        this.overlayType = overlayType;
        this.limbs = limbs;
    }

    @Override
    public String toString() {
        String c="Boss: ";
//        с += "\nCore: "+ coreType;
//        с += "\nforce: "+ forceType;
//        с += "\noverlay: "+ overlayType;
//        с += "\nlimbs: "+ limbs;
        return c;
    }

    public SHARD_CORE_TYPE getCoreType() {
        return coreType;
    }

    public void setCoreType(SHARD_CORE_TYPE coreType) {
        this.coreType = coreType;
    }

    public SHARD_FORCE_TYPE getForceType() {
        return forceType;
    }

    public void setForceType(SHARD_FORCE_TYPE forceType) {
        this.forceType = forceType;
    }

    public SHARD_OVERLAY_TYPE getOverlayType() {
        return overlayType;
    }

    public void setOverlayType(SHARD_OVERLAY_TYPE overlayType) {
        this.overlayType = overlayType;
    }

    public SHARD_LIMB[] getLimbs() {
        return limbs;
    }

    public void setLimbs(SHARD_LIMB[] limbs) {
        this.limbs = limbs;
    }
}
