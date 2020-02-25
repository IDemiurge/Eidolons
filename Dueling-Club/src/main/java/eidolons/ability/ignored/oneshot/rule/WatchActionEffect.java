package eidolons.ability.ignored.oneshot.rule;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.OneshotEffect;
import main.entity.obj.Obj;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.ArrayList;
import java.util.List;

public class WatchActionEffect extends DC_Effect implements OneshotEffect {

    private Boolean alert = false;

    public WatchActionEffect() {
    }

    public WatchActionEffect(Boolean alert) {
        this.alert = alert;
    }

    @Override
    public boolean applyThis() {
        Obj sourceObj = ref.getSourceObj();
        List<DC_Obj> list = WatchRule.getWatchersMap().get(sourceObj);
        if (list == null) {
            list = new ArrayList<>();
            WatchRule.getWatchersMap().put((Unit) sourceObj, list);
        } else if (!alert) {
            if (list.contains(ref.getTargetObj())) {
                list.remove(ref.getTargetObj());
                if (RandomWizard.random()) {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.BACK);
                } else {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.CLOSE);
                }
                LogMaster.log(1, sourceObj.getName() + " no longer watching "
                 + ref.getTargetObj().getNameIfKnown());
                return true;
            } else if (!list.isEmpty()) {
                if (RandomWizard.random()) {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BLOCKED);
                } else {
                    DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                }
                // if (maxUnitsWatched<=1)
                return false;
            }
        }
        list.add((DC_Obj) ref.getTargetObj());
        if (!alert) {
            if (RandomWizard.random()) // (sourceObj.isMine())
            {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__KNIFE);
            } else {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.NOTE);
            }
        }
        return true;

    }

}
