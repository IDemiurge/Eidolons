package main.swing.generic.services.dialog;

import main.ability.effects.oneshot.attack.AttackEffect;
import main.ability.effects.Effect;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.ai.tools.future.FutureBuilder;
import main.game.ai.tools.target.EffectFinder;
import main.game.logic.combat.attack.AttackCalculator;

import java.util.List;

public class AttackChoiceDialog extends EntityChoiceDialog<DC_ActiveObj> {

    private Unit target;

    public AttackChoiceDialog(List<DC_ActiveObj> data, Unit target) {
        super(data);
        this.target = target;
    }

    @Override
    protected int getObjSize() {
        return 50;
    }

    @Override
    public void close() {
        target.getGame().getToolTipMaster().removeToolTips();
        super.close();
    }

    @Override
    protected ChoicePanel<DC_ActiveObj> getChoicePanel() {
        return null;
//        return new AttackChoicePanel(this, data);
    }

    @Override
    protected void initTooltip(DC_ActiveObj active) {
        FutureBuilder.precalculateDamage(active, target, true);
        List<Effect> effect = EffectFinder.getEffectsOfClass(active, AttackEffect.class);
        if (effect.size() > 0) {
            AttackCalculator attackCalculator = new AttackCalculator(((AttackEffect) effect.get(0))
                    .getAttack(), true);
            int chance = attackCalculator.getCritOrDodgeChance();
            // attackCalculator.getCritChance();
            // attackCalculator.getCritChance();
        }
        active.getGame().getToolTipMaster();
        super.initTooltip(active);
    }

    public Unit getTarget() {
        return target;
    }

}
