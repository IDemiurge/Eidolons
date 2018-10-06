package eidolons.entity.item;

import eidolons.content.PARAMS;
import eidolons.entity.ChangeableType;
import eidolons.entity.obj.attach.DC_HeroAttachedObj;
import eidolons.game.battlecraft.rules.mechanics.DurabilityRule;
import eidolons.game.core.EUtils;
import eidolons.system.math.DC_MathManager;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Ref;
import main.entity.obj.BuffObj;
import main.entity.obj.HeroItem;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.StringMaster;

public abstract class DC_HeroItemObj extends DC_HeroAttachedObj implements HeroItem, ChangeableType {

    private final ObjType originalType;
    protected boolean equipped;
    private PARAMETER[] params;
    private PROPERTY[] props = {G_PROPS.STD_BOOLS};

    public DC_HeroItemObj(ObjType type, Player owner, GenericGame game, Ref ref, PARAMETER[] params
                          // , PROPERTY[] props
    ) {
        super(new ObjType(type), owner, game, ref);
        addDynamicValues();
        this.params = params;
        this.originalType = type;
        // this.props = props;
    }

    public ObjType getOriginalType() {
        return originalType;
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

    protected void applyDurability() {
        resetPercentages();
        Integer durability = getIntParam(PARAMS.DURABILITY_PERCENTAGE);
        if (durability <= 0) {
            broken();
        } else
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

        getRef().setAmount(amount);
        getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.DURABILITY_LOST, getRef()));
        if (getIntParam(PARAMS.C_DURABILITY) <= 0) {
            broken();
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
//     TODO    mod = mod * getGame().getBattleMaster().getOptionManager().getDifficulty().getDurabilityDamageMod() / 100;
        amount = amount * mod / 100;
        return reduceDurability(amount, simulation);

    }

    public void unequip() {
        equipped = false;

    }

    public void broken() {
        if (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.ITEM_BROKEN, getRef()))) {
            return;
        }
        setProperty(G_PROPS.STATUS, UnitEnums.STATUS.BROKEN.toString());
        if (DurabilityRule.isSaveBrokenItem())
         getHero().unequip(this, false);
        else {
            getHero().unequip(this, null );
        }
        kill();
        if (isMine()) {
        String msg = StringMaster.MESSAGE_PREFIX_ALERT + getHero().getName() + "'s " + getName() + " is broken!";
        game.getLogManager().log(msg);
        EUtils.showInfoText(msg);
        }

    }

}
