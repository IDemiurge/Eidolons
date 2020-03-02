package eidolons.game.module.netherflame.boss.logic;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.AiActionFactory;
import eidolons.game.core.Eidolons;
import eidolons.game.module.netherflame.boss.entity.BossActionMaster;
import main.entity.Ref;

public class BossAi {
    private static final boolean TEST_MODE = true;
    UnitAI ai;
    Action lastAction;
    Action lastPlayerAction;
    Integer presetTargetId;

    public BossAi(UnitAI ai) {
        this.ai = ai;
    }

    public int getPriority(BossActionMaster.BOSS_ACTION_REAPER action, Integer targetId) {
        return 0;
    }

    public Action getAction() {
        String name = null;
        if (!TEST_MODE){
        name = checkChanneling();
        if (name == null) {
            name = checkInstant();
        }
        if (name == null) {
            name = checkAttack();
        }
        }
        if (name == null)
          name = getDefault(); // by priority?
        Action a = AiActionFactory.newAction(name, ai);
        if (lastAction != null)
        if (lastAction.getActive() == a.getActive()) {
            //same action!
        }
        Ref ref = getTargetingRef(a);
        a.setRef(ref);
        if (lastAction != null)
            if (lastAction.getTarget() == a.getTarget()) {
            //same target!
        }
        return a;
    }

    private String checkChanneling() {
//        if (BossAnalyzer.isEnoughMinions())
//            return null;
        return BossActionMaster.BOSS_ACTION_REAPER.NETHER_CALL.getName();
    }

    private String checkInstant() {
//        if (BossAnalyzer.isLowHp())
//            return null;
        return BossActionMaster.BOSS_ACTION_REAPER.MORTAL_SCREAM.getName();
    }

    private String checkAttack() {
//        if (RandomWizard.random()) {
//            return BossActionMaster.BOSS_ACTION_REAPER.DEATH_RAZOR__PURIFY.getName();
//        }
        return BossActionMaster.BOSS_ACTION_REAPER.SEVER.getName();
    }

    private String getDefault() {
//        if (RandomWizard.random()) {
//            return BossActionMaster.BOSS_ACTION_REAPER.SEVER.getName();
//        }
//        if (RandomWizard.random()) {
//            return BossActionMaster.BOSS_ACTION_REAPER.DEATH_RAZOR__PURIFY.getName();
//        }
        return BossActionMaster.BOSS_ACTION_REAPER.SOUL_RIP.getName();
    }

    private Ref getTargetingRef(Action a) {
        Ref ref = new Ref(ai.getUnit());
        if (presetTargetId != null) {
            ref.setTarget(presetTargetId);
            presetTargetId = null;
        } else {
        ref.setTarget(Eidolons.getMainHero().getId());
        }

        return ref;
    }


}
