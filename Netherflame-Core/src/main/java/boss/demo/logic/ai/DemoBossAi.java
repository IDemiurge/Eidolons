package boss.demo.logic.ai;

import boss.logic.action.BOSS_ACTION;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.GridCell;
import eidolons.game.battlecraft.ai.elements.actions.AiAction;
import boss.BossManager;
import boss.ai.BossAi;
import boss.demo.logic.DemoBossActionManager;
import main.entity.Ref;

public class  DemoBossAi extends BossAi {

    public DemoBossAi(BossManager manager) {
        super(manager);
    }

    protected AiAction getSpell() {

        BOSS_ACTION key=chooseSpellKey();
        DC_ActiveObj active=getActionMaster().getAction(key) ;
        //can we re-use some of old ai? build possible sequences and decide?
        Ref ref=new Ref(getGame());
        // DC_Cell cell = getTargetCell(active);
        // ref.setTarget(cell.getId());
        ref.setTarget(Eidolons.getMainHero().getId());
        return new AiAction(active, ref);
    }

    private GridCell getTargetCell(DC_ActiveObj active) {
       return getGame().getCell(Eidolons.getMainHero().getCoordinates());
    }

    private BOSS_ACTION chooseSpellKey() {
        return DemoBossActionManager.KNIGHT_ACTION.FIRE_STORM;
    }
}
