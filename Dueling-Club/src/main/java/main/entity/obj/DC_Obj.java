package main.entity.obj;

import main.ability.AddSpecialEffects;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.ability.effects.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.DC_ContentManager;
import main.content.PROPS;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.*;
import main.content.values.properties.G_PROPS;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.entity.tools.DC_ObjMaster;
import main.entity.tools.EntityMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.DirectionMaster;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;
import main.game.core.game.DC_Game;
import main.game.core.game.Game;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.logic.battle.player.Player;
import main.game.battlecraft.rules.combat.damage.Damage;
import main.game.battlecraft.rules.action.PerceptionRule.PERCEPTION_STATUS;
import main.game.battlecraft.rules.action.PerceptionRule.PERCEPTION_STATUS_PLAYER;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.PhaseAnimation;
import main.system.launch.CoreEngine;

import java.util.List;
import java.util.Map;

public abstract class DC_Obj extends MicroObj {

    protected Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects;
    protected Map<DAMAGE_CASE,  List<Damage>> bonusDamage;
    protected UNIT_TO_PLAYER_VISION activeVisionStatus;
    protected UNIT_TO_UNIT_VISION activeUnitVisionStatus;
    protected PERCEPTION_STATUS_PLAYER playerPerceptionStatus;
    protected PERCEPTION_STATUS perceptionStatus;
    protected boolean detected;
    protected DAMAGE_TYPE dmg_type;
    protected VISIBILITY_LEVEL visibilityLevel;
    protected IDENTIFICATION_LEVEL identificationLevel;
    protected OUTLINE_TYPE outlineType;
    protected Integer gamma;
    protected OUTLINE_TYPE outlineTypeForPlayer;
    protected VISIBILITY_LEVEL visibilityLevelForPlayer;
    protected UNIT_TO_PLAYER_VISION playerVisionStatus;
    protected DIRECTION blockingWallDirection;
    protected boolean blockingDiagonalSide;
    protected Coordinates blockingWallCoordinate;
    protected boolean detectedByPlayer;
    protected PhaseAnimation animation;
    Coordinates blockingCoordinate;

    public DC_Obj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
        // [QUICK FIX] - ought to have "NO_OUTLINE" const
        if (!CoreEngine.isLevelEditor()) {
            if ((this instanceof DC_Cell) || (this instanceof Unit)) {
                playerVisionStatus = VisionEnums.UNIT_TO_PLAYER_VISION.CONCEALED;
                outlineTypeForPlayer = (this instanceof DC_Cell) ? VisionEnums.OUTLINE_TYPE.THICK_DARKNESS
                        : VisionEnums.OUTLINE_TYPE.DARK_OUTLINE;
                visibilityLevelForPlayer = VISIBILITY_LEVEL.CONCEALED;
            }
        }
    }

    @Override
    protected EntityMaster initMaster() {
        return new DC_ObjMaster(this);
    }

    @Override
    public String getDisplayedName() {
        if (!isMine()) {
            if (getOutlineType() != null)
            // if (getOutlineType()==)
            // getToolTip();
            {
                return StringMaster.getWellFormattedString(getOutlineType().toString());
            }
        }

        return super.getDisplayedName();
    }

    @Override
    public DC_Player getOwner() {
        return (DC_Player) super.getOwner();
    }

    public Integer getCounter(COUNTER c) {
        return getCounter(c.getName());
    }
    @Override
    public void init() {
        super.init();
        addDefaultValues();
    }
    protected void addDefaultValues() {
        DC_ContentManager.addDefaultValues(this);
        // for (String value : DC_ContentManager
        // .getInfoPanelValueList(getOBJ_TYPE())) {
        // VALUE VAL = ContentManager.getValue(value);
        // if (StringMaster.isEmpty(getValue(VAL))
        // && !StringMaster.isEmpty(VAL.getDefaultValue())) {
        // getType().setValue(VAL, VAL.getDefaultValue());
        // setValue(VAL, VAL.getDefaultValue());
        // }
        // }
    }

    public boolean checkInSight() {
        return getUnitVisionStatus() == VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
    }

    public boolean checkInSightForUnit(Unit unit) {
        return getGame().getVisionMaster().getUnitVisibilityStatus(this, unit) == VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
    }



    @Override
    public String getToolTip() {

        if (!VisionManager.checkDetected(this)) {
            return "?";
        }
        return super.getToolTip();
    }

    @Override
    public void clicked() {
        getGame().getManager().objClicked(this);
        LogMaster.log(0, getName() + " - CLICKED!");

    }

    public DC_Game getGame() {
        return (DC_Game) game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public boolean checkClassification(CLASSIFICATIONS PROP) {
        return checkProperty(G_PROPS.CLASSIFICATIONS, PROP.toString());

    }

    public boolean checkPassive(STANDARD_PASSIVES PROP) {
        return checkProperty(G_PROPS.STANDARD_PASSIVES, PROP.getName());

    }

    // boolean map instead?
    public boolean isAgile() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.DEXTEROUS);
    }

    public boolean isFlying() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.FLYING);
    }

    // StringMaster.getWellFormattedString

    public boolean hasDoubleCounter() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.DOUBLE_RETALIATION);
    }

    public boolean hasBludgeoning() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.BLUDGEONING);
    }

    public boolean hasNoRetaliation() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.NO_RETALIATION);
    }

    public boolean hasFirstStrike() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.FIRST_STRIKE);
    }

    public boolean hasNoMeleePenalty() {
        return checkPassive(UnitEnums.STANDARD_PASSIVES.NO_MELEE_PENALTY);

    }

    public DAMAGE_TYPE getDamageType() {
        if (ref.getDamageType() != null) {
            return ref.getDamageType();
        }

        if (dmg_type == null) {
            String name = getProperty(PROPS.DAMAGE_TYPE);
            dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, name);
        }

        if (dmg_type == null) dmg_type=DAMAGE_TYPE.PHYSICAL;
        return dmg_type;
    }

    public boolean checkSelectHighlighted() {
        try {
            return getGame().getManager().getSelectingSet().contains(this);
        } catch (Exception e) {

        }
        return false;
    }

    public Map<SPECIAL_EFFECTS_CASE, Effect> getSpecialEffects() {
        if (specialEffects == null) {
            specialEffects = new XLinkedMap<>();
        }
        return specialEffects;
    }
    public  void addBonusDamage(DAMAGE_CASE c, Damage d) {
        MapMaster.addToListMap(getBonusDamage(), c, d);
    }
        public Map<DAMAGE_CASE, List<Damage>> getBonusDamage() {
        if (bonusDamage == null) {
            bonusDamage = new XLinkedMap<>();
        }
        return bonusDamage;
    }

    public void setSpecialEffects(Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects) {
        this.specialEffects = specialEffects;
    }


    public void applySpecialEffects(SPECIAL_EFFECTS_CASE case_type,
                                    BattleFieldObject target, Ref REF) {
        if (specialEffects == null) {
            return;
        }
        if (specialEffects.get(case_type) == null) {
            return;
        }
        Ref ref = Ref.getCopy(REF);
        ref.setTarget(target.getId());
        if (this instanceof Unit) {
            ref.setSource(getId());
        } else {
            ref.setID(KEYS.THIS, getId());
        }
        Effect effect = specialEffects.get(case_type);
        effect.apply(ref);
    }

    public void addSpecialEffect(SPECIAL_EFFECTS_CASE case_type,
                                 Effect effects) {
        if (effects instanceof Effects) {
            Effects effects_ = (Effects) effects;
            for (Effect e : effects_.getEffects()) {
                addSpecialEffect(case_type, e);
            }
            return;
        }
        if (effects instanceof AddSpecialEffects) {
            AddSpecialEffects addSpecialEffects = (AddSpecialEffects) effects;
            effects = addSpecialEffects.getEffects();
        }
        if (getSpecialEffects().get(case_type) != null) {
            getSpecialEffects().put(case_type,
                    new Effects(getSpecialEffects().get(case_type), effects));
        } else {
            getSpecialEffects().put(case_type, effects);
        }

    }

    public void modified(ModifyValueEffect modifyValueEffect) {

    }

    public VISIBILITY_LEVEL getActiveVisibilityLevel() {
        return getVisibilityLevel(true);
    }

    public VISIBILITY_LEVEL getVisibilityLevel() {
        return getVisibilityLevel(false);
    }

    public void setVisibilityLevel(VISIBILITY_LEVEL visibilityLevel) {
        if (getGame().getManager().getActiveObj() != null) {
            if (getGame().getManager().getActiveObj().isMine())
            // TODO MAIN HERO ONLY?
            {
                setVisibilityLevelForPlayer(visibilityLevel);
            }
        }
        this.visibilityLevel = visibilityLevel;
    }

    public VISIBILITY_LEVEL getVisibilityLevel(boolean active) {
        if (VisionManager.isVisionHacked()) {
            return VISIBILITY_LEVEL.CLEAR_SIGHT;
        }
        if (!active) {
            if (!game.isDebugMode()) {
                return getVisibilityLevelForPlayer();
            }
        }
        return visibilityLevel;
    }

    public VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
        if (visibilityLevelForPlayer == null) {
            return VISIBILITY_LEVEL.CONCEALED;
        }
        if (isDisplayEnemyVisibility()) {
            return visibilityLevel;
        }
        return visibilityLevelForPlayer;
    }

    public void setVisibilityLevelForPlayer(VISIBILITY_LEVEL visibilityLevelForPlayer) {
        this.visibilityLevelForPlayer = visibilityLevelForPlayer;
    }

    public IDENTIFICATION_LEVEL getIdentificationLevel() {
        if (VisionManager.isVisionHacked()) {
            return IDENTIFICATION_LEVEL.UNIT;
        }
        return identificationLevel;
    }

    public void setPerceptionStatus(PERCEPTION_STATUS_PLAYER perceptionStatus) {
        this.playerPerceptionStatus = perceptionStatus;
    }

    public PERCEPTION_STATUS_PLAYER getPlayerPerceptionStatus() {
        return playerPerceptionStatus;
    }

    public void setPlayerPerceptionStatus(PERCEPTION_STATUS_PLAYER playerPerceptionStatus) {
        this.playerPerceptionStatus = playerPerceptionStatus;
    }

    public PERCEPTION_STATUS getPerceptionStatus() {
        return perceptionStatus;
    }

    public void setPerceptionStatus(PERCEPTION_STATUS perceptionStatus) {
        this.perceptionStatus = perceptionStatus;
    }

    public boolean isFlippedImage() {
        if (this instanceof Unit) {
            Unit heroObj = (Unit) this;

            checkBool(DYNAMIC_BOOLS.FLIPPED);

            return heroObj.getDirection() == DirectionMaster.FLIP_DIRECTION;
        }
        return false;
    }

    public OUTLINE_TYPE getOutlineType() {
        if (getGame().isSimulation()) {
            return null;
        }
        if (VisionManager.isVisionHacked()) {
            return null;
        }
        if (!game.isDebugMode()) {
            return getOutlineTypeForPlayer();
        }
        return outlineType;
    }

    public void setOutlineType(OUTLINE_TYPE outlineType) {
        if (getGame().getManager().getActiveObj() != null) {
            if (getGame().getManager().getActiveObj().isMine())
            // TODO MAIN HERO ONLY?
            {
                setOutlineTypeForPlayer(outlineType);
            }
        }
        this.outlineType = outlineType;
    }

    public OUTLINE_TYPE getOutlineTypeForPlayer() {
        if (isDisplayEnemyVisibility()) {
            return outlineType;
        }
        return outlineTypeForPlayer;
    }

    public void setOutlineTypeForPlayer(OUTLINE_TYPE outlineTypeForPlayer) {

        this.outlineTypeForPlayer = outlineTypeForPlayer;
        if (outlineTypeForPlayer == null) {
            if (getGame().getManager().getActiveObj() != null) {
                if (!getGame().getManager().getActiveObj().isMine()) {
                    LogMaster.log(1, "outlineTypeForPlayer set to "
                            + outlineTypeForPlayer);
                }
            }
        }
        // main.system.auxiliary.LogMaster.log(1, "outlineTypeForPlayer set to "
        // + outlineTypeForPlayer);
    }

    protected boolean isDisplayEnemyVisibility() {
        return false;
    }

    public UNIT_TO_UNIT_VISION getUnitVisionStatus() {
        if (VisionManager.isVisionHacked()) {
            return VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
        }
        if (activeUnitVisionStatus == null) {
            try {
                activeUnitVisionStatus = getGame().getVisionMaster()
                        .getUnitVisibilityStatus(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return activeUnitVisionStatus;
    }

    public void setUnitVisionStatus(UNIT_TO_UNIT_VISION unitVisionStatus) {

        this.activeUnitVisionStatus = unitVisionStatus;
        if (unitVisionStatus != null) {
            setProperty(PROPS.VISIBILITY_STATUS, unitVisionStatus.toString());
        }
    }

    public UNIT_TO_PLAYER_VISION getActivePlayerVisionStatus() {
        if (VisionManager.isVisionHacked()) {
            return VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
        }
        return activeVisionStatus;
    }

    public UNIT_TO_PLAYER_VISION getPlayerVisionStatus(boolean active) {
        if (VisionManager.isVisionHacked()) {
            return VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
        }

        if (active) {
            return activeVisionStatus;
        }
        return playerVisionStatus;
    }

    public void setPlayerVisionStatus(UNIT_TO_PLAYER_VISION playerVisionStatus) {
        if (getGame().getManager().getActiveObj() != null) {
            if (getGame().getManager().getActiveObj().isMine()) {
                this.playerVisionStatus = playerVisionStatus;
            }
        }

        this.activeVisionStatus = playerVisionStatus;
        if (playerVisionStatus != null) {
            setProperty(PROPS.DETECTION_STATUS, playerVisionStatus.toString());
        }

    }

    public boolean checkVisible() {

        return VisionManager.checkVisible(this);
    }

    public boolean isDetected() {
        if (VisionManager.isVisionHacked()) {
            return true;
        }
        if (getGame().getManager().getActiveObj() != null) {
            if (getGame().getManager().getActiveObj().isMine()) {
                return isDetectedByPlayer();
            }
        }
        return detected;
    }

    public void setDetected(boolean b) {
        if (!b) {
            this.detected = b;
        }
        this.detected = b;
        if (getGame().getManager().getActiveObj() != null) {
            if (getGame().getManager().getActiveObj().isMine()) {
                setDetectedByPlayer(b);
            }
        }
    }

    public boolean isDetectedByPlayer() {
        if (VisionManager.isVisionHacked()) {
            return true;
        }
        return detectedByPlayer;
    }

    public void setDetectedByPlayer(boolean detectedByPlayer) {
        if (!detectedByPlayer) {
            this.detectedByPlayer = detectedByPlayer;
        }
        this.detectedByPlayer = detectedByPlayer;
    }

    public boolean checkStatus(STATUS STATUS) {
        return checkProperty(G_PROPS.STATUS, (STATUS.name()));
    }

    public Integer getGamma() {
        return gamma;
    }

    public void setGamma(Integer gamma) {
        this.gamma = gamma;
    }

    public Coordinates getBlockingCoordinate() {
        return blockingCoordinate;
    }

    public void setBlockingCoordinate(Coordinates blockingCoordinate) {
        this.blockingCoordinate = blockingCoordinate;
    }

    public DIRECTION getBlockingWallDirection() {
        return blockingWallDirection;
    }

    public void setBlockingWallDirection(DIRECTION d) {
        blockingWallDirection = d;
    }

    public boolean isBlockingDiagonalSide() {
        return blockingDiagonalSide;
    }

    public void setBlockingDiagonalSide(boolean left) {
        blockingDiagonalSide = left;

    }

    public Coordinates getBlockingWallCoordinate() {
        return blockingWallCoordinate;
    }

    public void setBlockingWallCoordinate(Coordinates c) {
        blockingWallCoordinate = c;
    }

    public void initAnimation() {
    }

    public PhaseAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(PhaseAnimation animation) {
        this.animation = animation;
    }


}
