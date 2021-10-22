package libgdx.anims.main;

import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import libgdx.anims.construct.AnimConstructor;
import libgdx.anims.std.BuffAnim;
import main.data.ConcurrentMap;
import main.entity.obj.BuffObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 11/15/2018.
 */
public class BuffAnimMaster {

    ConcurrentMap<BuffObj, BuffAnim> continuousAnims = new ConcurrentMap<>();
    private boolean continuousAnimsOn;
    private Integer showBuffAnimsOnNewRoundLength = 2;
    private Integer showBuffAnimsOnHoverLength = 3; //continuous

    public BuffAnimMaster() {
        continuousAnimsOn =
         false;
    }

    private void updateContinuousAnims() {
        if (!continuousAnimsOn) {
            return;
        }
        final List<BuffObj> toRemove = new ArrayList<>();
        continuousAnims.keySet().forEach(buff -> {
            if (buff.isDead()) {
                continuousAnims.get(buff).finished();
                toRemove.add(buff);
            }
        });
        toRemove.forEach(buff -> {
            continuousAnims.remove(buff);
        });
        DC_Game.game.getUnits().forEach(unit -> {
            unit.getBuffs().forEach(buff -> {
                if (!continuousAnims.containsKey(buff)) //TODO or full reset always?
                {
                    if (buff.isVisible()) {
                        BuffAnim anim = AnimConstructor.getBuffAnim(buff);
                        //TODO cache!
                        if (anim != null) {
                            continuousAnims.put(buff, anim);
                            try {
                                anim.start();
                            } catch (Exception e) {
                                main.system.ExceptionMaster.printStackTrace(e);
                            }
                        }
                    }
                }
            });
        });
    }
    protected void mouseHover(Unit unit) {

        if (showBuffAnimsOnHoverLength == null) {
            return;
        }
        unit.getBuffs().forEach(buff -> {
            BuffAnim anim = continuousAnims.get(buff);
            if (anim != null) {
                anim.reset();
                anim.start();
                anim.setDuration(showBuffAnimsOnHoverLength);
            }
        });
    }
    //        GuiEventManager.bind(GuiEventType.UPDATE_BUFFS, p -> {
    //            updateContinuousAnims();
    //        });
    //        GuiEventManager.bind(GuiEventType.ABILITY_RESOLVES, p -> {
    //            if (!isOn()) {
    //                return;
    //            }
    //            Ability ability = (Ability) p.getVar();
    //what about triggers?
    //            getParentAnim(ability.getRef().getActive()).addAbilityAnims(ability);
    //        });
}
