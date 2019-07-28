package eidolons.game.battlecraft.logic.meta.igg.soul;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.SoulMaster;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;

public class EidolonLord extends LightweightEntity {

    public static EidolonLord lord;

    HeroChain chain;

    public int getSoulforce() {
        return getIntParam(PARAMS.SOULFORCE );
    }
    public void soulsLost(Soul... souls) {

        for (Soul soul : souls) {
            if (soul == null) {
                continue;
            }
            removeProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
            getGame().getLogManager().log("A Soul is lost: "  + soul.getUnitType().getName());
        }
        SoulMaster.clear();

    }
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

    public void soulforceGained(int amount) {
        lord.addParam(PARAMS.SOULFORCE, amount);
    }
    public void soulforceLost(int amount) {
        lord.addParam(PARAMS.SOULFORCE, -amount);
    }
    public void soulsGained(Soul soul) {
        addProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
        getGame().getLogManager().log("A Soul is trapped: "  + soul.getUnitType().getName());
    }



    /**
     * from save
     * > soul list
     * > memories
     * and more...
     */


}
