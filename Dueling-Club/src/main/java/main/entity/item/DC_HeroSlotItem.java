package main.entity.item;

import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.MATERIAL;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BuffObj;
import main.entity.obj.unit.DC_UnitObj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.player.Player;
import main.system.auxiliary.EnumMaster;

public abstract class DC_HeroSlotItem extends DC_HeroItemObj {

    public DC_HeroSlotItem(ObjType type, Player owner, MicroGame game, Ref ref, PARAMETER[] params
                           // , PROPERTY[] props
    ) {
        super(type, owner, game, ref, params
                // , props
        );
    }

    protected abstract void applyPenaltyReductions();

    @Override
    public void apply() {
        applyBuffEffects();
        applyPenaltyReductions();
        super.apply();
        applyMods();
    }

    private void applyBuffEffects() {
        for (BuffObj b : getBuffs()) {
            b.getEffects().apply(Ref.getSelfTargetingRefCopy(this));
        }
    }

    public MATERIAL getMaterial() {
        return new EnumMaster<MATERIAL>().retrieveEnumConst(MATERIAL.class,
                getProperty(G_PROPS.MATERIAL));
    }

    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type, DC_UnitObj target, Ref REF) {
        if (specialEffects == null) {
            return;
        }
        if (specialEffects.get(case_type) == null) {
            return;
        }
        Ref ref = Ref.getCopy(REF);
        ref.setTarget(target.getId());
        ref.setSource(getOwnerObj().getId());
        Effect effect = specialEffects.get(case_type);
        effect.apply(ref);
    }

    protected boolean isActivatePassives() {
        return true;
    }

    @Override
    public String getToolTip() {
        return super.getToolTip();
    }

    @Override
    public void toBase() {
        getSpecialEffects().clear();
        super.toBase();
    }

    @Override
    public boolean checkSelectHighlighted() {
        return super.checkSelectHighlighted();
    }

    @Override
    public void applyMods() {
        super.applyMods();
    }

    protected KEYS getKey() {
        return KEYS.SLOT_ITEM;
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
    }
}
