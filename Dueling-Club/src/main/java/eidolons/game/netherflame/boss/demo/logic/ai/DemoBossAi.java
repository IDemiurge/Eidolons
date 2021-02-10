package eidolons.game.netherflame.boss.demo.logic.ai;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.ai.BossAi;
import eidolons.game.netherflame.boss.demo.logic.DemoBossActionManager;
import eidolons.game.netherflame.boss.logic.action.BOSS_ACTION;
import main.entity.Ref;

import static eidolons.game.core.Eidolons.getMainHero;

public class  DemoBossAi extends BossAi {

    public DemoBossAi(BossManager manager) {
        super(manager);
    }

    protected Action getSpell() {

        BOSS_ACTION key=chooseSpellKey();
        DC_ActiveObj active=getActionMaster().getAction(key) ;
        //can we re-use some of old ai? build possible sequences and decide?
        Ref ref=new Ref(getGame());
        // DC_Cell cell = getTargetCell(active);
        // ref.setTarget(cell.getId());
        ref.setTarget(getMainHero().getId());
        return new Action(active, ref);
    }

    private DC_Cell getTargetCell(DC_ActiveObj active) {
       return getGame().getCellByCoordinate(getMainHero().getCoordinates());
    }

    private BOSS_ACTION chooseSpellKey() {
        return DemoBossActionManager.KNIGHT_ACTION.FIRE_STORM;
    }
}
