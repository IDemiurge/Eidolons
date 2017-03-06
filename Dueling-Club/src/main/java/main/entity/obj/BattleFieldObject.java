package main.entity.obj;

import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_UnitModel;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.DC_MovementManager;
import main.game.battlefield.FacingMaster;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.DC_Formulas;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.math.DC_MathManager;

/**
 * Created by JustMe on 2/15/2017.
 */
public class BattleFieldObject extends DC_Obj implements BfObj {

    protected FACING_DIRECTION facing;
    private DIRECTION direction;

    public BattleFieldObject(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public Boolean isLandscape() {
        return getType().checkProperty(G_PROPS.BF_OBJECT_TAGS, BfObjEnums.BF_OBJECT_TAGS.LANDSCAPE.toString());

    }

    public boolean isWall() {
        return getType().checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.WALL.toString());

    }

    @Override
    public boolean isObstructing(Obj obj) {
        return isObstructing(obj, null);

    }

    @Override
    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        if (isDead()) {
            return false;
        }
        boolean ignoreInterrupt = false;
        if (quietly == null) {
            ignoreInterrupt = true;
            quietly = false;

        }
        if (!ignoreInterrupt) {
            if (!quietly) {
                if (checkPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE)) {
                    preventDeath();
                    return false;
                }
            }
        }
        ref.setID(KEYS.KILLER, killer.getId());

        Ref REF = Ref.getCopy(killer.getRef());
        REF.setTarget(id);
        REF.setSource(killer.getId());

        if (!quietly) {
            if (!getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_KILLED, REF))) {
                if (!ignoreInterrupt) {
                    return false;
                }
            }
            ((BattleFieldObject) killer)
                    .applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_KILL, this, REF);

            applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH,
                    ((BattleFieldObject) killer), REF);
            if (!ignoreInterrupt) {
                if (ref.checkInterrupted()) {
                    return false;
                }
            }
        }
        setDead(true);

        getGame().getManager().unitDies(this, (Obj) killer, leaveCorpse, quietly);

        return true;
    }

    private void preventDeath() {
        LogMaster.log(1, "****preventDeath for " + this);
        setParam(PARAMS.C_ENDURANCE, Math.max(1, getIntParam(PARAMS.C_ENDURANCE)));
        setParam(PARAMS.C_TOUGHNESS, Math.max(1, getIntParam(PARAMS.C_TOUGHNESS)));

    }

    public boolean isObstructing(Obj obj, DC_Obj target) {

        if (target == null) {
            return false;
        }
        if (obj == null) {
            return false;
        }
        if (isBfObj()) {
            if (isWall()) {
                // if (WindowRule.checkWindowOpening(this, obj, target))
                // return false;
            }
        }
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        // boolean targetTall = false;
        // boolean targetShort = false;
        // if (target instanceof DC_HeroObj) {
        // targetTall = (((DC_HeroObj) target).isTall());
        // targetShort = (((DC_HeroObj) target).isShort());
        // }
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.NON_OBSTRUCTING)) {
            return false;
        }
        if (obj instanceof BattleFieldObject) {
            int height = getIntParam(PARAMS.HEIGHT);
            if (height > 200) {
                height = getIntParam(PARAMS.HEIGHT);
            }
            int source_height = obj.getIntParam(PARAMS.HEIGHT);
            int target_height = target.getIntParam(PARAMS.HEIGHT);

            BattleFieldObject source = (BattleFieldObject) obj;
            if (target_height > height) {
                return false;
            }
            if (source.isAgile() && !isHuge()) {
                return false;
            }
            if (source_height < height)
            // if (!source.isFlying()) //add height TODO
            {
                return true;
            }

            // if (isShort())
            // if (!(source.isShort() && !targetShort))
            // return false;
            //
            // if (source.isAgile() && !isHuge())
            // return false;
            //
            // if (!isTall())
            // if (source.isFlying() || source.isTall() || targetTall)
            // return false;
        }

        return false;

    }

    public boolean isSmall() {
        if (checkProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.STANDARD_PASSIVES.SMALL)) {
            return true;
        }
        return checkProperty(G_PROPS.CLASSIFICATIONS, "" + UnitEnums.CLASSIFICATIONS.SMALL);
    }

    public boolean isShort() {
        return checkClassification(UnitEnums.CLASSIFICATIONS.SHORT) || checkPassive(UnitEnums.STANDARD_PASSIVES.SHORT);
    }

    public boolean isTall() {
        return checkClassification(UnitEnums.CLASSIFICATIONS.TALL) || checkPassive(UnitEnums.STANDARD_PASSIVES.TALL);
    }

    protected void addBfObjDefaults() {
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.INDESTRUCTIBLE)) {
            type.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.INDESTRUCTIBLE.toString());
        }
        if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" + BfObjEnums.BF_OBJECT_TAGS.PASSABLE)) {
            type.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.PASSABLE.toString());
        }

        type.addProperty(G_PROPS.STD_BOOLS, GenericEnums.STD_BOOLS.LEAVES_NO_CORPSE.toString());

        setParam(PARAMS.C_MORALE, 0);
        setParam(PARAMS.C_STAMINA, 0);
        setParam(PARAMS.C_FOCUS, 0);
        setParam(PARAMS.C_ESSENCE, 0);
        // type.addProperty(G_PROPS.STANDARD_PASSIVES,
        // STANDARD_PASSIVES.SNEAK_IMMUNE.toString());

    }

    protected void addDefaultFacing() {
        facing = DC_MovementManager.getDefaultFacingDirection(owner.isMe());
        resetFacing();
    }

    @Override
    public void addDynamicValues() {
        setParam(G_PARAMS.POS_X, x, true);
        setParam(G_PARAMS.POS_Y, y, true);
        setParam(PARAMS.C_MORALE, getIntParam(PARAMS.SPIRIT) * DC_Formulas.MORALE_PER_SPIRIT, true);

        setParam(PARAMS.C_ENDURANCE, getIntParam(PARAMS.ENDURANCE), true);
        setParam(PARAMS.C_TOUGHNESS, getIntParam(PARAMS.TOUGHNESS), true);
        setParam(PARAMS.C_N_OF_ACTIONS, getIntParam(PARAMS.N_OF_ACTIONS), true);
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS), true);
        setParam(PARAMS.C_ENERGY, "0", true);

        setParam(PARAMS.C_FOCUS, DC_MathManager.getStartingFocus(this), true);
        setParam(PARAMS.C_ESSENCE, getGame().getMathManager().getStartingEssence(this), true);
        setParam(PARAMS.C_STAMINA, getIntParam(PARAMS.STAMINA), true);


    }

    public void resetPercentages() {
        resetPercentage(PARAMS.TOUGHNESS);
        resetPercentage(PARAMS.MORALE);
        resetPercentage(PARAMS.ENDURANCE);
        resetPercentage(PARAMS.ESSENCE);
        resetPercentage(PARAMS.FOCUS);
        resetPercentage(PARAMS.STAMINA);
        resetPercentage(PARAMS.N_OF_COUNTERS);
        resetPercentage(PARAMS.N_OF_ACTIONS);
        resetPercentage(PARAMS.INITIATIVE);

    }

    public void resetCurrentValues() {
        resetCurrentValue(PARAMS.TOUGHNESS);
        resetCurrentValue(PARAMS.ENDURANCE);
        resetCurrentValue(PARAMS.MORALE);
        resetCurrentValue(PARAMS.ESSENCE);
        resetCurrentValue(PARAMS.FOCUS);
        resetCurrentValue(PARAMS.STAMINA);
        resetCurrentValue(PARAMS.N_OF_COUNTERS);
        resetCurrentValue(PARAMS.N_OF_ACTIONS);
        resetCurrentValue(PARAMS.INITIATIVE);

    }



    public boolean removeStatus(STATUS status) {
        return removeProperty(G_PROPS.STATUS, status.name());

    }

    @Override
    public void addPassive(String abilName) {
        if (DataManager.isTypeName(abilName)) {
            super.addPassive(abilName);
        } else {
            addProperty(G_PROPS.STANDARD_PASSIVES, abilName);
        }
    }

    public boolean isDone() {
        if (isDead()) {
            return true;
        }
        return getIntParam(PARAMS.C_N_OF_ACTIONS) <= 0;
    }

    public void regen() {
        if (isFull()) {
            return;
        }
        regen(PARAMS.ENDURANCE);
        regen(PARAMS.FOCUS);
        regen(PARAMS.ESSENCE);
        regen(PARAMS.STAMINA);

        regen(PARAMS.ENERGY);

    }

    protected void regen(PARAMETER p) {
        if (isFull(p)) {
            return;
        }
        Integer regen = getIntParam(ContentManager.getRegenParam(p));
        if (regen != 0) {
            modifyParameter(ContentManager.getCurrentParam(p), regen, getIntParam(p));
        }

    }

    public boolean isUnmoved() {
        return getIntParam(PARAMS.C_N_OF_ACTIONS) >= getIntParam(PARAMS.N_OF_ACTIONS);

    }

    public boolean isFull() {
        if (getIntParam(PARAMS.C_ENDURANCE) < getIntParam(PARAMS.ENDURANCE)) {
            return false;
        }
        if (getIntParam(PARAMS.C_TOUGHNESS) < getIntParam(PARAMS.TOUGHNESS)) {
            return false;
        }
        if (getIntParam(PARAMS.C_ESSENCE) < getIntParam(PARAMS.ESSENCE)) {
            return false;
        }
        if (getIntParam(PARAMS.C_FOCUS) < getIntParam(PARAMS.FOCUS)) {
            return false;
        }
        if (getIntParam(PARAMS.C_STAMINA) < getIntParam(PARAMS.STAMINA)) {
            return false;
        }
        if (getIntParam(PARAMS.C_ESSENCE) < getIntParam(PARAMS.ESSENCE)) {
            return false;
        }
        return getIntParam(PARAMS.C_ENERGY) >= getIntParam(PARAMS.ENERGY);
    }

    public void resetFacing() {
        if (facing != null) {
            setProperty(PROPS.FACING_DIRECTION, facing.getName());
        } else {
            String name = getProperty(PROPS.FACING_DIRECTION);
            facing = (new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
                    name));
            if (facing == null) {
                if (getDirection() != null) {
                    FacingMaster.getFacingFromDirection(getDirection());
                } else if (ref.getObj(KEYS.SUMMONER) != null) {
                    facing = ((DC_UnitModel) ref.getObj(KEYS.SUMMONER)).getFacing();
                } else {
                    facing = FacingMaster.getRandomFacing();
                }
            }

        }
    }


    public DIRECTION getDirection() {
        if (direction == null) {
            direction = new EnumMaster<DIRECTION>().retrieveEnumConst(DIRECTION.class,
                    getProperty(PROPS.DIRECTION));
        }
        // if (direction == null)
        // if (!isOverlaying())
        // direction = facing.getDirection();
        return direction;
        //
        // TODO perhaps for stacked as well...
        // return
    }

    public void setDirection(DIRECTION d) {
        this.direction = d;

    }

    public boolean isBfObj() {
        // TODO would be cool to make petrified/frozen units appear as bfObj
        return getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ;
    }

    public boolean isHuge() {
        if (checkProperty(G_PROPS.STANDARD_PASSIVES, "" + UnitEnums.STANDARD_PASSIVES.HUGE)) {
            return true;
        }
        return checkProperty(G_PROPS.CLASSIFICATIONS, "" + UnitEnums.CLASSIFICATIONS.HUGE);
    }

    public boolean isTurnable() {
        return true;
    }

    public FACING_DIRECTION getFacing() {
        if (facing == null) {
            resetFacing();
        }
        return facing;
    }

    public void setFacing(FACING_DIRECTION direction) {
        this.facing = direction;
        resetFacing();
    }
}
