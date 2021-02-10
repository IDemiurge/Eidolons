package eidolons.game.netherflame.main.lord;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.core.EUtils;
import eidolons.game.netherflame.main.death.HeroChain;
import eidolons.game.netherflame.main.soul.eidola.Soul;
import eidolons.game.netherflame.main.soul.eidola.SoulMaster;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class EidolonLord extends LightweightEntity {

    public static EidolonLord lord;
    HeroChain chain;

    public EidolonLord(ObjType type) {
        super(type);
        lord = this;
        lord.setParam(PARAMS.C_SOULFORCE,   type.getIntParam(PARAMS.BASE_SOULFORCE));
        lord.setParam(PARAMS.SOULFORCE,   type.getIntParam(PARAMS.SOULFORCE));
        GuiEventManager.trigger(GuiEventType.SOULFORCE_GAINED, this);
    }

    public void useArts() {
    }

    public void soulsLost(Soul... souls) {
        for (Soul soul : souls) {
            if (soul == null) {
                continue;
            }
            removeProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
            getGame().getLogManager().log("A Soul is lost: " + soul.getUnitType().getName());
        }
        SoulMaster.clear();

    }

    public void soulforceGained(int amount) {
        lord.addParam(PARAMS.C_SOULFORCE, amount);
        EUtils.showInfoText(true, "Soulforce gained: " + amount);
        GuiEventManager.trigger(GuiEventType.SOULFORCE_GAINED, this);
    }

    public void soulforceLost(int amount) {
        lord.addParam(PARAMS.C_SOULFORCE, -amount);
        EUtils.showInfoText(true, "Soulforce lost: " + amount);
        GuiEventManager.trigger(GuiEventType.SOULFORCE_LOST, this);

    }

    public Integer getSoulforce() {
        return super.getIntParam(PARAMS.C_SOULFORCE);
    }

    public Integer getSoulforceMax() {
        return super.getIntParam(PARAMS.SOULFORCE);
    }

    public void soulsGained(Soul soul) {
        addProperty(PROPS.LORD_SOULS, soul.getUnitType().getName());
        getGame().getLogManager().log("A Soul is trapped: " + soul.getUnitType().getName());

//        SpeechExecutor.run("sprite=soul grip(me)");
//        WaitMaster.waitForCondition();

        GuiEventManager.trigger(GuiEventType.SOULS_CLAIMED, soul);
    }

    public HeroChain getChain() {
        return chain;
    }

    public void setChain(HeroChain chain) {
        this.chain = chain;
    }


    /**
     * from save
     * > soul list
     * > memories
     * and more...
     */


}
