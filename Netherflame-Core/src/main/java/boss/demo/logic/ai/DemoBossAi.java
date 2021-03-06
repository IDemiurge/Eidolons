package boss.demo.logic.ai;

import boss.logic.action.BOSS_ACTION;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import boss.BossManager;
import boss.ai.BossAi;
import boss.demo.logic.DemoBossActionManager;
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
        ref.setTarget(Eidolons.getMainHero().getId());
        return new Action(active, ref);
    }

    private DC_Cell getTargetCell(DC_ActiveObj active) {
       return getGame().getCellByCoordinate(Eidolons.getMainHero().getCoordinates());
    }

    private BOSS_ACTION chooseSpellKey() {
        return DemoBossActionManager.KNIGHT_ACTION.FIRE_STORM;
    }
}
