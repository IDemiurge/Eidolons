package eidolons.entity.obj;

import eidolons.content.ContentConsts;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePages;
import eidolons.entity.ChangeableType;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.ArmorItem;
import eidolons.entity.item.WeaponItem;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.OutlineMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.mapper.SeenMapper;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.game.exploration.dungeon.objects.Door;
import eidolons.system.math.DC_MathManager;
import main.ability.AbilityObj;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums.STD_BOOLS;
import main.content.enums.entity.BfObjEnums;
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
import main.game.core.game.Game;
import main.game.logic.action.context.Context.IdKey;
import main.game.logic.battle.player.Player;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.LogMaster;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JustMe on 2/15/2017.
 */
public class BattleFieldObject extends DC_Obj implements BfObj, ChangeableType {

    protected int maxVisionDistance;
    protected boolean hidden;
    private DIRECTION direction;
    private Coordinates bufferedCoordinates;
    private boolean sneaking;
    private Float lastSeenTime;
    private Coordinates lastCoordinates;
    private OUTLINE_TYPE lastSeenOutline;
    private ObjType originalType;
    private boolean summoned;
    private boolean revealed;
    private boolean moduleBorder;
    private CONTENT_CONSTS.FLIP flip;

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

    @Override
    public void setPassives(List<AbilityObj> passives) {
        super.setPassives(passives);
        passivesReady = true;
        //        activatePassives();
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
        if (!isMine())
            if (OutlineMaster.isOutlinesOn()) {
                if (getOutlineTypeForPlayer() != null)
                    return getOutlineTypeForPlayer().getName();
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
        return getNameAndCoordinate();
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

            // if (!source.isFlying()) //add height TODO
            return source_height < height;

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

        setParam(PARAMS.C_ENDURANCE, getIntParam(PARAMS.ENDURANCE), true);
        setParam(PARAMS.C_TOUGHNESS, getIntParam(PARAMS.TOUGHNESS), true);
        setParam(PARAMS.C_ENERGY, "0", true);

        setParam(PARAMS.C_FOCUS, DC_MathManager.getStartingFocus(this), true);
        setParam(PARAMS.C_ESSENCE, getGame().getMathManager().getStartingEssence(this), true);


    }

    @Override
    protected void putParameter(PARAMETER param, String value) {
        if (param == PARAMS.C_TOUGHNESS) {
            if (NumberUtils.getIntParse(value) >
                    getIntParam(PARAMS.TOUGHNESS)) {
                LogMaster.log(1, "BUG: toughness >100%! " + this + value);
                //TODO debug
                value = getParam(PARAMS.TOUGHNESS);
            }
        }
        super.putParameter(param, value);
    }

    public void resetPercentages() {
        Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE).forEach(this::resetPercentage);
    }

    public void resetCurrentValues() {
        Arrays.stream(ValuePages.UNIT_DYNAMIC_PARAMETERS_CORE).forEach(p -> {
            if (p == PARAMS.INITIATIVE)
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

    public void regen() {

        Arrays.stream(ContentConsts.REGENERATED_PARAMS).forEach(this::regen);

    }

    protected void regen(PARAMETER p) {
        if (isFull(p)) {
            return;
        }
        Integer regen = getIntParam(ContentValsManager.getRegenParam(p));
        if (regen != 0) {
            modifyParameter(ContentValsManager.getCurrentParam(p), regen, getIntParam(p));
        }

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

    public boolean canCounter(ActiveObj action, boolean sneak) {
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
        if (!coordinates.equals(this.coordinates)) {
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
        return p == PARAMS.TOUGHNESS;
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
        if (checkStatus(STATUS.UNDYING))
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

    public WeaponItem getActiveWeapon(boolean offhand) {
        return null;
    }

    public ArmorItem getArmor() {
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

    public boolean isLightEmitter() {
        return checkProperty(G_PROPS.BF_OBJECT_GROUP, BfObjEnums.BF_OBJECT_GROUP.LIGHT_EMITTER.toString());
    }

    public boolean isWater() {
        return false;
    }

    public boolean isImmaterial() {
        if (checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return true;
        }
        return checkProperty(G_PROPS.STANDARD_PASSIVES, UnitEnums.STANDARD_PASSIVES.IMMATERIAL.getName());
    }

    public void setHidden(boolean b) {
        hidden = b;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isImpassable() {
        return checkBool(STD_BOOLS.IMPASSABLE);
    }

    public boolean isModuleBorder() {
        return moduleBorder;
    }

    public void setModuleBorder(boolean moduleBorder) {
        this.moduleBorder = moduleBorder;
    }


    public String getVisionInfo() {
        return "[" +
                "gamma=" + gamma +
                "; " + getVisibilityLevel() +
                "/" + getUnitVisionStatus() +
                "/" + getPlayerVisionStatus() +
                ']';
    }

    public SeenMapper getSeenMapper() {
        return getVisionController().getSeenMapper();
    }

    public void setFlip(CONTENT_CONSTS.FLIP flip) {
        this.flip = flip;
    }

    public CONTENT_CONSTS.FLIP getFlip() {
        return flip;
    }

    public LevelStruct getStruct() {
        return getGame().getDungeonMaster().getStructMaster().getLowestStruct(getCoordinates());
    }

    public boolean checkCanDoFreeMove(ActiveObj entity) {
        return false;
    }
}
