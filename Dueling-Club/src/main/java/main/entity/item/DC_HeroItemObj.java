package main.entity.item;

import main.content.PARAMS;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.HeroItem;
import main.entity.obj.attach.DC_HeroAttachedObj;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.math.DC_MathManager;

public abstract class DC_HeroItemObj extends DC_HeroAttachedObj implements HeroItem {

    protected boolean equipped;
    private PARAMETER[] params;
    private PROPERTY[] props = {G_PROPS.STD_BOOLS};

    public DC_HeroItemObj(ObjType type, Player owner, MicroGame game, Ref ref, PARAMETER[] params
                          // , PROPERTY[] props
    ) {
        super(type, owner, game, ref);
        this.params = params;
        // this.props = props;
    }

    @Override
    public void addBuff(BuffObj buff) {
        super.addBuff(buff);
        // if (getHero() != null)
        // getHero().addBuff(buff);
    }

    public void applyMods() {
    }

    @Override
    public void clicked() {
        super.clicked();

    }

    protected int getWeight() {
        return getIntParam(PARAMS.WEIGHT);
    }

    public void equipped(Ref ref) {
        this.equipped = true;
    }

    @Override
    protected void initHero() {
        super.initHero();
        if (!equipped) {
            equipped(ref);
        }
    }

    @Override
    public void apply() {

        super.apply();

        applyDurability();

        for (PROPERTY prop : props) {
            initProp(prop);
        }
        for (PARAMETER param : params) {
            initParam(param);
        }

        // hero.modifyParameter(PARAMS.CARRYING_WEIGHT, getWeight());

        getHero().setDirty(true);
    }

    private void applyDurability() {
        resetPercentages();
        Integer durability = getIntParam(PARAMS.DURABILITY_PERCENTAGE);
        multiplyParamByPercent(getDurabilityParam(), durability, false);
    }

    @Override
    protected void addDynamicValues() {
        super.addDynamicValues();
        setParam(PARAMS.C_DURABILITY, getIntParam(PARAMS.DURABILITY));
    }

    @Override
    public void resetPercentages() {
        resetPercentage(PARAMS.DURABILITY);
    }

    protected abstract PARAMETER getDurabilityParam();

    public int reduceDurability(int amount) {
        return reduceDurability(amount, false);
    }

    public int reduceDurability(int amount, boolean simulation) {
        if (amount <= 0) {
            return 0;
        }
        amount = Math.min(amount, getIntParam(PARAMS.C_DURABILITY));
        if (simulation) {
            return amount;
        }
        modifyParameter(PARAMS.C_DURABILITY, -amount, 0);

        if (getIntParam(PARAMS.C_DURABILITY) <= 0) {
            setProperty(G_PROPS.STATUS, UnitEnums.STATUS.BROKEN.toString());
            getHero().unequip(this, false);
            game.getLogManager()
                    .log(StringMaster.MESSAGE_PREFIX_ALERT + getHero().getName() + "'s " + getName() + " is broken!");
        } else {
            game.getLogManager().log(StringMaster.MESSAGE_PREFIX_INFO + getName() + " loses " + amount + " durability, "
                    + getIntParam(PARAMS.C_DURABILITY) + " left");
        }
        return amount;
    }

    public int reduceDurabilityForDamage(int damage, int armor, int mod, boolean simulation) {
        int amount = DC_MathManager.getDurabilityForDamage(damage, armor, getOBJ_TYPE_ENUM());
        if (amount == 0) {
            return 0;
        }
        Integer integer = getIntParam(PARAMS.DURABILITY_SELF_DAMAGE_MOD);
        if (integer != 0) {
            mod = mod * integer / 100;
        }

        amount = amount * mod / 100;
        return reduceDurability(amount, simulation);

    }

    public void unequip() {
        equipped = false;

    }

    public void broken() {

        kill();

    }

}
