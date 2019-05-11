package eidolons.entity.obj;

import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.ValuePages;
import eidolons.entity.ChangeableType;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_ArmorObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.atb.AtbController;
import eidolons.game.module.dungeoncrawl.objects.Door;
import eidolons.libgdx.bf.grid.GridUnitView;
import eidolons.system.DC_Formulas;
import eidolons.system.math.DC_MathManager;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.content.values.parameters.G_PARAMS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.BfObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.core.game.Game;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.Player;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 2/15/2017.
 */
public class BattleFieldObject extends DC_Obj implements BfObj, ChangeableType {

    protected FACING_DIRECTION facing;
    protected int maxVisionDistance;
    private DIRECTION direction;
    private Coordinates bufferedCoordinates;
    private boolean sneaking;
    private Float lastSeenTime;
    private FACING_DIRECTION lastSeenFacing;
    private Coordinates lastCoordinates;
    private OUTLINE_TYPE lastSeenOutline;
    private ObjType originalType;
    private boolean summoned;

    public BattleFieldObject(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public Boolean isLandscape() {
        return false;

    }

    @Override
    public void toBase() {
        maxVisionDistance = 0;
        super.toBase();
    }

    public boolean isWall() {
        return false;
    }

    @Override
    public boolean isObstructing(Obj obj) {
        return isObstructing(obj, null);

    }

    @Override
    public String getToolTip() {
        if (OutlineMaster.isOutlinesOn()) {
            if (getOutlineTypeForPlayer() != null)
                return getOutlineTypeForPlayer().getName();
            //         if (!isDetected())
            if (!getGame().getVisionMaster().getDetectionMaster().checkKnownForPlayer(this)) {
                return "Unknown";
            }
        }
        String prefix = "";

        if (isMine()) {
            if (isMainHero())
                prefix = "(You) ";
            else
                prefix = "Ally ";
        } else if (!getOwner().isNeutral())
            prefix = "Enemy ";

        return prefix + getDisplayedName();
    }

    @Override
    public String toString() {
        if (getGame().isDebugMode())
            return super.toString();
        return getName();
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
        if (!quietly)
        if (CoreEngine.isIggDemoRunning())
            if (isPlayerCharacter()) {
                if (!ShadowMaster.isShadowAlive()) {
                    preventDeath();
                    return false;
                }
                if (ShadowMaster.checkCheatDeath()) {
                    preventDeath();
                    Eidolons.getGame().getLogManager().log(getName()+
                            " cheats Death! The trick can only work once... ");
                    return false;
                }
            }

        if ((game.isDebugMode() && isMine()) || (!ignoreInterrupt && !quietly)) {
            if ((game.isDebugMode() && isMine()) || checkPassive(UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE)) {
                preventDeath();
                return false;
            }
        }
        if (killer == null)
            killer = this;
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


        getGame().getManager().unitDies(this, (Obj) killer, leaveCorpse, quietly);

        return true;
    }

    public boolean canCounter() {
        return false;
    }

    public void preventDeath() {
        LogMaster.log(1, "****preventDeath for " + this);
        setParam(PARAMS.C_ENDURANCE, Math.max(1, getIntParam(PARAMS.C_ENDURANCE)));
        setParam(PARAMS.C_TOUGHNESS, Math.max(1, getIntParam(PARAMS.C_TOUGHNESS)));

    }

    public boolean isEnemyTo(DC_Player player) {
        return getOwner().isHostileTo(player);
    }

    public boolean isAlliedTo(DC_Player player) {
        return !getOwner().isHostileTo(player);
        //if (getOwner().equals(player))
        //    return true;
        //if (player.getGame().getBattleMaster().getPlayerManager().)
        //        return false;
    }

    @Override
    public boolean isObstructing() {
        if (isDead())
            return false;
        if (checkPassive(UnitEnums.STANDARD_PASSIVES.NON_OBSTRUCTING)) {
            return false;
        }
        return !isOverlaying();
    }

    public boolean isObstructing(Obj watcher, DC_Obj target) {

        if (target == null) {
            return false;
        }
        if (watcher == null) {
            return false;
        }
        //        if (isBfObj()) {
        //            if (isWall()) {
        // if (WindowRule.checkWindowOpening(this, obj, target))
        // return false;
        //            }
        //        }
        if (!isObstructing()) {
            return false;
        }

        if (checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            return false;
        }
        if (isWall())
            return true;
        if (this instanceof Door) {
            return true;
        }
        //        double distance = PositionMaster.getExactDistance(this, target);
        //        Integer girth = getIntParam(PARAMS.GIRTH);
        //        if (girth/100 < distance  ){
        //            return false;
        //        }
        //1000 space per cell

        if (watcher instanceof BattleFieldObject) {
            int height = getIntParam(PARAMS.HEIGHT);
            int source_height = watcher.getIntParam(PARAMS.HEIGHT);
            int target_height = target.getIntParam(PARAMS.HEIGHT);
            //            if (height-source_height  > target_height-height)
            //            {
            //                return true;
            //            }
            if (target_height > height) {
                return false;
            }
            //TODO        lvl80!     if (source.isAgile() && !isHuge()) {
            //                return false;
            //            }

            if (source_height < height)
            // if (!source.isFlying()) //add height TODO
            {
                return true;
            }

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


    @Override
    public void addDynamicValues() {
        setParam(G_PARAMS.POS_X, x, true);
        setParam(G_PARAMS.POS_Y, y, true);
        setParam(PARAMS.C_MORALE, getIntParam(PARAMS.SPIRIT) * DC_Formulas.MORALE_PER_SPIRIT, true);

        setParam(PARAMS.C_ENDURANCE, getIntParam(PARAMS.ENDURANCE), true);
        setParam(PARAMS.C_TOUGHNESS, getIntParam(PARAMS.TOUGHNESS), true);
        if (!DC_Engine.isAtbMode())
            setParam(PARAMS.C_N_OF_ACTIONS, getIntParam(PARAMS.N_OF_ACTIONS), true);
        setParam(PARAMS.C_N_OF_COUNTERS, getIntParam(PARAMS.N_OF_COUNTERS), true);
        setParam(PARAMS.C_ENERGY, "0", true);

        setParam(PARAMS.C_FOCUS, DC_MathManager.getStartingFocus(this), true);
        setParam(PARAMS.C_ESSENCE, getGame().getMathManager().getStartingEssence(this), true);
        setParam(PARAMS.C_STAMINA, getIntParam(PARAMS.STAMINA), true);


    }

    @Override
    protected void putParameter(PARAMETER param, String value) {
        if (param == PARAMS.C_TOUGHNESS) {
            if (NumberUtils.getInteger(value) >
                    getIntParam(PARAMS.TOUGHNESS)) {
                LogMaster.log(1, "gotcha dwarf " + this + value);
            //igg demo hack!
                value = getParam(PARAMS.TOUGHNESS);
            }
        }
        if (param == PARAMS.C_N_OF_ACTIONS) {
            Integer prev = getIntParam(param);
            int diff = NumberUtils.getInteger(value) - prev;
            if (diff == 0)
                return;
            int mod = AtbController.ATB_READINESS_PER_AP * diff;
            //            main.system.auxiliary.log.LogMaster.log
            //             (1,this+"'s INITIATIVE modified by " +mod);
            modifyParameter(PARAMS.C_INITIATIVE, mod);
        } else if (param == PARAMS.INITIATIVE_MODIFIER) {
            //            Integer prev = getIntParam(param);
            //            int diff = StringMaster.getInteger(value) - prev;
            //            modifyParameter(PARAMS.N_OF_ACTIONS, diff);
        } else if (param == PARAMS.C_INITIATIVE_BONUS) {
            Integer prev = getIntParam(param);
            int diff = NumberUtils.getInteger(value) - prev;
            modifyParameter(PARAMS.C_INITIATIVE, diff);
        } else if (param == PARAMS.INITIATIVE_BONUS) {
            super.putParameter(param, value);
        } else if (param == PARAMS.C_INITIATIVE) {
            Integer val = NumberUtils.getInteger(value);
            val = MathMaster.getMinMax(val, 0, 100);
            super.putParameter(param, val + "");
        } else
            super.putParameter(param, value);
    }

    public void resetPercentages() {
        Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE).forEach(p -> resetPercentage(p));
    }

    public void resetCurrentValues() {
        Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE).forEach(p -> {
            if (p == PARAMS.N_OF_ACTIONS)
                if (DC_Engine.isAtbMode())
                    return;
            resetCurrentValue(p);
        });
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

        Arrays.stream(DC_ContentValsManager.REGENERATED_PARAMS).forEach(parameter -> {
            regen(parameter);
        });

    }

    protected void regen(PARAMETER p) {
        if (isFull(p)) {
            return;
        }
        Integer regen = getIntParam(ContentValsManager.getRegenParam(p));
        if (regen != 0) {
            //TODO igg demo hack
            if (p == PARAMS.STAMINA) {
                regen = MathMaster.getMinMax(regen, 5, getIntParam("stamina")/2);
            }
            modifyParameter(ContentValsManager.getCurrentParam(p), regen, getIntParam(p));
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

    }

    @Override
    public void setDirty(boolean dirty) {
        if (!dirty) {
            setBufferedCoordinates(getCoordinates());
        }
        super.setDirty(dirty);
    }

    public DIRECTION getDirection() {
        if (direction == null) {
            direction = new EnumMaster<DIRECTION>().retrieveEnumConst(DIRECTION.class,
                    getProperty(PROPS.DIRECTION));
        }
        return direction;
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

    public void setFacing(FACING_DIRECTION facing) {
        if (facing == null) {
            return;
        }
        this.facing = facing;
    }

    public boolean canCounter(DC_ActiveObj action, boolean sneak) {
        return false;
    }

    public boolean isMainHero() {
        return false;
    }

    public boolean isPlayerCharacter() {
        return false;
    }

    public boolean isItemsInitialized() {
        return false;
    }

    @Override
    public boolean isPlayerDetected() {
        return isDetectedByPlayer();
    }

    public Coordinates getBufferedCoordinates() {
        return bufferedCoordinates;
    }

    public void setBufferedCoordinates(Coordinates bufferedCoordinates) {
        this.bufferedCoordinates = bufferedCoordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates.equals(this.coordinates)) {
            lastCoordinates = (this.coordinates);
        }
        super.setCoordinates(coordinates);
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        if (sneaking)
            addStatus(STATUS.SNEAKING);
        else
            removeStatus(STATUS.SNEAKING);
        this.sneaking = sneaking;
    }

    public boolean isValidMapStored(PARAMETER p) {
        if (p == PARAMS.ENDURANCE)
            return true;
        if (p == PARAMS.TOUGHNESS)
            return true;
        return false;
    }

    public int getMaxVisionDistance() {
        if (maxVisionDistance == 0)
            maxVisionDistance = getIntParam(PARAMS.SIGHT_RANGE) * 2 + 1;
        return maxVisionDistance;
    }

    public boolean isSpotted() {
        return checkStatus(STATUS.SPOTTED);
    }

    public Float getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(Float lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public FACING_DIRECTION getLastSeenFacing() {
        return lastSeenFacing;
    }

    public void setLastSeenFacing(FACING_DIRECTION lastSeenFacing) {
        this.lastSeenFacing = lastSeenFacing;
    }


    public Coordinates getLastCoordinates() {
        return lastCoordinates;
    }

    public OUTLINE_TYPE getLastSeenOutline() {
        return lastSeenOutline;
    }

    public void setLastSeenOutline(OUTLINE_TYPE lastSeenOutline) {
        this.lastSeenOutline = lastSeenOutline;
    }

    public ObjType getOriginalType() {
        return originalType;
    }

    public void setOriginalType(ObjType originalType) {
        this.originalType = originalType;
    }

    public boolean isIndestructible() {
        if (getGame().isDebugMode())
            return false;
        if (checkStatus( STATUS.UNDYING ))
            return true;
        return checkProperty(G_PROPS.STD_BOOLS, STD_BOOLS.INDESTRUCTIBLE.name());
    }

    public boolean isInvulnerable() {
        return checkProperty(G_PROPS.STD_BOOLS, STD_BOOLS.INVULNERABLE.name());
    }

    public VISION_MODE getVisionMode() {
        return VISION_MODE.NORMAL_VISION;
    }

    public boolean isAiControlled() {
        return false;
    }

    public Obj getLinkedObj(IdKey idKey) {
        return null;
    }

    @Override
    public void setRef(Ref ref) {
        ref.setSource(id);
        super.setRef(ref);
    }

    public DC_WeaponObj getActiveWeapon(boolean offhand) {
        return null;
    }

    public DC_ArmorObj getArmor() {
        return null;
    }

    public int getPower() {
        return getIntParam(PARAMS.POWER);
    }

    public boolean isDisabled() {
        return false;
    }

    public void setParam(PARAMETER param, int i, boolean quietly, boolean base) {
        super.setParam(param, i, quietly, base);
        if (base && !customType) {
            if (!getType().isGenerated())
                cloneType();
        }
    }

    public void setProperty(PROPERTY name, String value, boolean base) {
        super.setProperty(name, value, base);
        if (base && !customType) {
            if (!getType().isGenerated())
                cloneType();
        }
    }


    public boolean isBoss() {
        return false;
    }

    public String getInfo() {
        return getNameAndCoordinate() + " ";
    }

    public void removeFromGame() {
        if (!isDead())
            kill(this, false, true);
        getGame().softRemove(this);
        getVisionController().reset();
        GuiEventManager.trigger(GuiEventType.DESTROY_UNIT_MODEL, this);
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
    }
}
