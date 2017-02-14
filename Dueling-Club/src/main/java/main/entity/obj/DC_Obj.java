package main.entity.obj;

import main.ability.AddSpecialEffects;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.common.ModifyValueEffect;
import main.content.CONTENT_CONSTS.*;
import main.content.DC_ContentManager;
import main.content.PROPS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.obj.unit.DC_UnitObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.VisionManager;
import main.game.logic.battle.player.DC_Player;
import main.game.player.Player;
import main.rules.action.PerceptionRule.PERCEPTION_STATUS;
import main.rules.action.PerceptionRule.PERCEPTION_STATUS_PLAYER;
import main.rules.mechanics.ConcealmentRule.IDENTIFICATION_LEVEL;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.swing.components.obj.drawing.VisibilityMaster.OUTLINE_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.graphics.PhaseAnimation;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

public abstract class DC_Obj extends MicroObj {

    protected Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects;
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
            if ((this instanceof DC_Cell) || (this instanceof DC_HeroObj)) {
                playerVisionStatus = UNIT_TO_PLAYER_VISION.CONCEALED;
                outlineTypeForPlayer = (this instanceof DC_Cell) ? OUTLINE_TYPE.THICK_DARKNESS
                        : OUTLINE_TYPE.DARK_OUTLINE;
                visibilityLevelForPlayer = VISIBILITY_LEVEL.CONCEALED;
            }
        }
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

    public Integer getCounter(STD_COUNTERS c) {
        return getCounter(c.getName());
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
        return getUnitVisionStatus() == UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
    }

    public boolean checkInSightForUnit(DC_HeroObj unit) {
        return getGame().getVisionManager().getUnitVisibilityStatus(this, unit) == UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
    }

    @Override
    public void init() {
        super.init();
        addDefaultValues();
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
        main.system.auxiliary.LogMaster.log(0, getName() + " - CLICKED!");

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
        return checkPassive(STANDARD_PASSIVES.DEXTEROUS);
    }

    public boolean isFlying() {
        return checkPassive(STANDARD_PASSIVES.FLYING);
    }

    // StringMaster.getWellFormattedString

    public boolean hasDoubleCounter() {
        return checkPassive(STANDARD_PASSIVES.DOUBLE_RETALIATION);
    }

    public boolean hasBludgeoning() {
        return checkPassive(STANDARD_PASSIVES.BLUDGEONING);
    }

    public boolean hasNoRetaliation() {
        return checkPassive(STANDARD_PASSIVES.NO_RETALIATION);
    }

    public boolean hasFirstStrike() {
        return checkPassive(STANDARD_PASSIVES.FIRST_STRIKE);
    }

    public boolean hasNoMeleePenalty() {
        return checkPassive(STANDARD_PASSIVES.NO_MELEE_PENALTY);

    }

    public DAMAGE_TYPE getDamageType() {
        if (ref.getDamageType() != null) {
            return ref.getDamageType();
        }

        if (dmg_type == null) {
            String name = getProperty(PROPS.DAMAGE_TYPE);
            dmg_type = new EnumMaster<DAMAGE_TYPE>().retrieveEnumConst(DAMAGE_TYPE.class, name);
        }

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
            initSpecialEffects();
        }
        return specialEffects;
    }

    public void setSpecialEffects(Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects) {
        this.specialEffects = specialEffects;
    }

    public void initSpecialEffects() {
        specialEffects = new HashMap<>();

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
        if (this instanceof DC_HeroObj) {
            ref.setSource(getId());
        } else {
            ref.setID(KEYS.THIS, getId());
        }
        Effect effect = specialEffects.get(case_type);
        effect.apply(ref);
    }

    public void addSpecialEffect(SPECIAL_EFFECTS_CASE case_type, Effect effects) {
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
        if (this instanceof DC_HeroObj) {
            DC_HeroObj heroObj = (DC_HeroObj) this;

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
                    main.system.auxiliary.LogMaster.log(1, "outlineTypeForPlayer set to "
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
            return UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
        }
        if (activeUnitVisionStatus == null) {
            try {
                activeUnitVisionStatus = getGame().getVisionManager().getUnitVisibilityStatus(this);
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
            return UNIT_TO_PLAYER_VISION.DETECTED;
        }
        return activeVisionStatus;
    }

    public UNIT_TO_PLAYER_VISION getPlayerVisionStatus(boolean active) {
        if (VisionManager.isVisionHacked()) {
            return UNIT_TO_PLAYER_VISION.DETECTED;
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
