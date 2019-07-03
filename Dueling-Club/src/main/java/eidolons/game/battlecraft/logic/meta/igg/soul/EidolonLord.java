package eidolons.game.battlecraft.logic.meta.igg.soul;

import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;

public class EidolonLord extends LightweightEntity {

    public static EidolonLord lord;

    HeroChain chain;

    public HeroChain getChain() {
        return chain;
    }

    public void setChain(HeroChain chain) {
        this.chain = chain;
    }

    public EidolonLord(ObjType type) {
        super(type);
        lord = this;
    }

    /**
     * from save
     * > soul list
     * > memories
     * and more...
     */


}
