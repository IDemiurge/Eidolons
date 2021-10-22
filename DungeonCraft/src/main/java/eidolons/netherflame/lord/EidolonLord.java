package eidolons.netherflame.lord;

import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.netherflame.eidolon.chain.EidolonChain;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class EidolonLord extends LightweightEntity {

    public static EidolonLord lord;
    public Unit trueForm;
    protected EidolonChain chain;
    // protected LordProgression progression;
    /*
    1) consider persistence - unlocks, account, runs that were started
    Should we split some of this stuff into Netherflame-Core project?
     */

    public EidolonLord(ObjType type) {
        super(type);
        lord = this;
        lord.setParam(PARAMS.C_SOULFORCE,   type.getIntParam(PARAMS.BASE_SOULFORCE));
        lord.setParam(PARAMS.SOULFORCE,   type.getIntParam(PARAMS.SOULFORCE));
        GuiEventManager.trigger(GuiEventType.SOULFORCE_GAINED, this);
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

    public EidolonChain getChain() {
        return chain;
    }
}
