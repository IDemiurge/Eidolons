package main.elements.costs.old;

import main.ability.Ability;
import main.elements.conditions.Conditions;
import main.elements.targeting.Targeting;
import main.entity.Ref;

public class CostImpl implements Cost {
    protected Ability pay_ability;
    protected boolean paid = false;
    protected Conditions canPayConditions;

    // protected String payment_type;
    protected String[] formulas;
    protected String payee = "SOURCE";

    protected Targeting targeting; // who's
    // gonna
    // pay?! ;)

    /*
     * <SPELL CLASS=INSTANT NAME=X> <COSTS> <COST OPTIONAL=TRUE
     * PAYEE=MINION/ANY/ (you if empty) > [DEFAULT COSTS] 0;0;3;2;6;4;2; ...
     * (MANAS, STA AND INIT) </COST>
     *
     *
     * <ALTCOST TYPE=SACRIFICE_UNIT OPTIONAL=?> <TARGETING TYPE= CONDITION
     *
     * </COSTS>
     */
    private Ref ref;

    // private final static UNIT_PARAMETERS[] cost_types = {
    // UNIT_PARAMETERS.MANA_SACRED,
    // UNIT_PARAMETERS.MANA_INFERNAL,
    // UNIT_PARAMETERS.MANA_ARCANE,
    // UNIT_PARAMETERS.MANA_APHOTHIC,
    //
    // UNIT_PARAMETERS.MANA_CELESTIAL,
    // UNIT_PARAMETERS.MANA_IGNEOUS,
    // UNIT_PARAMETERS.MANA_TERRESTRIAL,
    // UNIT_PARAMETERS.MANA_AQUATIC,
    //
    // UNIT_PARAMETERS.IN_CUR, UNIT_PARAMETERS.STA_CUR };

    public CostImpl(String s) {
        /*
         * IF DYNAMIC COST - formulas=s.split(" "); payment_type=tempArray[1];
		 * =tempArray[0]; if (s.contains("#")) {
		 * target=s.substring(s.indexOf("#")+1, s.lastIndexOf("#")-1);
		 *
		 * s.replace(target, ""); s.replaceAll("#", ""); if
		 * (s.contains("CHOOSE_")){ target.replace("CHOOSE_", ""); targeting=new
		 * AutoTargeting
		 * (Keywords.CONDITION_KEYWORD.valueOf(target).getConditions()); } }
		 * targeting=new FixedTargeting(target);
		 *
		 * canPayConditions = new Condition
		 */
    }

    @Override
    public boolean isPaid() {
        return paid;

    }

    @Override
    public boolean pay(SoEObj payee, Ref ref) {
        setRef(ref);
        if (!(targeting == null)) {
            targeting.select(this.ref);
        } else {
            this.ref.setTarget(ref.getSource());
        }
        int i = 0;
        for (String s : formulas) {
            if (!s.equals("0")) {
                // for future: maybe each 's' could also optionally provide a
                // MOD type? some abils might require you to spend ALL mana of X
                // type... fun, isnt it?

				/*Effect effect = new ModifyValueEffect(cost_types[i], MOD.CONST,
                        new Formula(s));
				effect.apply(this.ref);
				i++;
				pay_ability = new ActiveAbility(targeting, effect);*/
            }
        }


        ref.setSource(payee.getId());
        if (pay_ability.activate(ref)) {
            paid = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean canBePaid(Ref ref) {
        // flexibility issue:
        // "You can pay with your summoned minions stamina for this spell" ???
        return targeting == null;
    }

    @Override
    public Ref getRef() {
        // TODO Auto-generated method stub
        return ref;
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = (Ref) ref.clone();
    }

    // WHT ABOUT ITEM CHARGE COST?
    public enum PAYEE {
        MINION,
    }

}
