package eidolons.entity.obj;

import eidolons.ability.AddSpecialEffects;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.handlers.DC_ObjMaster;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionController;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;
import eidolons.game.battlecraft.logic.battlefield.vision.mapper.*;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.COUNTER;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.properties.G_PROPS;
import main.data.XLinkedMap;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.handlers.EntityMaster;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

import java.util.List;
import java.util.Map;

public abstract class DC_Obj extends MicroObj {

    protected Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects;
    protected Map<DAMAGE_CASE, List<Damage>> bonusDamage;
    protected DAMAGE_TYPE dmg_type;
    //    protected UNIT_TO_PLAYER_VISION activeVisionStatus;
//    protected UNIT_TO_UNIT_VISION activeUnitVisionStatus;
//    protected boolean detected;
//protected boolean detectedByPlayer;
//    protected OUTLINE_TYPE outlineTypeForPlayer;
//    protected VISIBILITY_LEVEL visibilityLevelForPlayer = VISIBILITY_LEVEL.UNSEEN;
//    protected UNIT_TO_PLAYER_VISION playerVisionStatus = UNIT_TO_PLAYER_VISION.UNKNOWN;
//    protected VISIBILITY_LEVEL visibilityLevel;
//    protected OUTLINE_TYPE outlineType;
    protected Integer gamma;
    //    protected PERCEPTION_STATUS_PLAYER playerPerceptionStatus;
//    protected PERCEPTION_STATUS perceptionStatus;
//    protected IDENTIFICATION_LEVEL identificationLevel;
    protected DIRECTION blockingWallDirection;
    protected boolean blockingDiagonalSide;
    protected Coordinates blockingWallCoordinate;
    Coordinates blockingCoordinate;
    private VisionController visionController;
    private boolean visibilityOverride;

    public DC_Obj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    public OutlineMapper getOutlineMapper() {
        return getVisionController().getOutlineMapper();
    }

    public PlayerVisionMapper getPlayerVisionMapper() {
        return getVisionController().getPlayerVisionMapper();
    }

    public VisibilityLevelMapper getVisibilityLevelMapper() {
        return getVisionController().getVisibilityLevelMapper();
    }

    public UnitVisionMapper getUnitVisionMapper() {
        return getVisionController().getUnitVisionMapper();
    }

    public DetectionMapper getDetectionMapper() {
        return getVisionController().getDetectionMapper();
    }
    public GammaMapper getGammaMapper() {
        return getVisionController().getGammaMapper();
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
        if (CoreEngine.isDefaultValuesAddedDynamically())
            DC_ContentValsManager.addDefaultValues(this);
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
        return getUnitVisionStatus() == UNIT_VISION.IN_PLAIN_SIGHT;
    }

    public boolean checkInSightForUnit(Unit unit) {
        return getGame().getVisionMaster().getUnitVisibilityStatus(this, unit) == UNIT_VISION.IN_PLAIN_SIGHT;
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

    // StringMaster.getWellFormattedString

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

        if (dmg_type == null) dmg_type = DAMAGE_TYPE.PHYSICAL;
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

    public void setSpecialEffects(Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects) {
        this.specialEffects = specialEffects;
    }

    public void addBonusDamage(DAMAGE_CASE c, Damage d) {
        MapMaster.addToListMap(getBonusDamage(), c, d);
    }

    public Map<DAMAGE_CASE, List<Damage>> getBonusDamage() {
        if (bonusDamage == null) {
            bonusDamage = new XLinkedMap<>();
        }
        return bonusDamage;
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



    public PLAYER_VISION getActiveVisionStatus() {
        return getPlayerVisionMapper().get(this);
    }

    public void setActiveVisionStatus(PLAYER_VISION activeVisionStatus) {
        getPlayerVisionMapper().set(this, activeVisionStatus);
    }

    public PLAYER_VISION getPlayerVisionStatus() {
        return getPlayerVisionMapper().getForMe(this);
    }

    public void setPlayerVisionStatus(PLAYER_VISION playerVisionStatus) {
        getPlayerVisionMapper().setForMe(this, playerVisionStatus);
    }



    public UNIT_VISION getActiveUnitVisionStatus() {
        return getUnitVisionMapper().get(this);
    }

    public void setActiveUnitVisionStatus(UNIT_VISION activeUnitVisionStatus) {
        getUnitVisionMapper().set(this, activeUnitVisionStatus);
    }
    public UNIT_VISION getUnitVisionStatus() {
        return getUnitVisionMapper().getForMe(this);
    }

    public void setUnitVisionStatus(UNIT_VISION activeUnitVisionStatus) {
        getUnitVisionMapper().setForMe(this, activeUnitVisionStatus);
    }



    public boolean isDetected() {
        return getDetectionMapper().get(this);
    }

    public void setDetected(boolean detected) {
        getDetectionMapper().set(this, detected);
    }

    public boolean isDetectedByPlayer() {
        return getDetectionMapper().getForMe(this);
    }

    public void setDetectedByPlayer(boolean detectedByPlayer) {
          getDetectionMapper().setForMe(this, detectedByPlayer);
    }



    public OUTLINE_TYPE getOutlineType() {
        return getOutlineMapper().get(this);
    }

    public void setOutlineType(OUTLINE_TYPE outlineTypeForPlayer) {
        getOutlineMapper().set(this,outlineTypeForPlayer);
    }

    public VISIBILITY_LEVEL getVisibilityLevel() {
        return getVisibilityLevelMapper().get(this);
    }

    public void setVisibilityLevel(VISIBILITY_LEVEL visibilityLevelForPlayer) {
        getVisibilityLevelMapper().set(this, visibilityLevelForPlayer);
    }



    public OUTLINE_TYPE getOutlineTypeForPlayer() {
        return getOutlineMapper().getForMe(this);
    }

    public void setOutlineTypeForPlayer(OUTLINE_TYPE outlineTypeForPlayer) {
        getOutlineMapper().setForMe(this,outlineTypeForPlayer);
    }

    public VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
        return getVisibilityLevelMapper().getForMe(this);
    }

    public void setVisibilityLevelForPlayer(VISIBILITY_LEVEL visibilityLevelForPlayer) {
        getVisibilityLevelMapper().setForMe(this, visibilityLevelForPlayer);
    }




//           TODO OLD                                <><><><><>
    //    public VISIBILITY_LEVEL getActiveVisibilityLevel() {
//        return getVisibilityLevel(true);
//    }
//    public VISIBILITY_LEVEL getVisibilityLevel() {
//        return getVisibilityLevel(false);
//    }
//
//    public void setVisibilityLevel(VISIBILITY_LEVEL visibilityLevel) {
//        Unit seeingUnit = getGame().getVisionMaster().getSeeingUnit();
//        if (seeingUnit != null) {
//            if (seeingUnit.isMine())
//                if (seeingUnit.isMainHero()) {
//                    setVisibilityLevelForPlayer(visibilityLevel);
//                }
//        }
//
//        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG,
//         this + " setVisibilityLevel " + visibilityLevel);
//        if (this instanceof Unit) {
//            if (activeUnitVisionStatus == UNIT_TO_UNIT_VISION.IN_SIGHT ||
//             activeUnitVisionStatus == UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT) {
//                if (visibilityLevel == VISIBILITY_LEVEL.UNSEEN || visibilityLevel == VISIBILITY_LEVEL.BLOCKED)
//                    main.system.auxiliary.log.LogMaster.log(1,
//                     this + " gotcha " + visibilityLevel);
//            } else {
//                if (activeUnitVisionStatus == UNIT_TO_UNIT_VISION.BEYOND_SIGHT)
//                    if (visibilityLevel == VISIBILITY_LEVEL.CLEAR_SIGHT
//                     ) {
//                        main.system.auxiliary.log.LogMaster.log(1,
//                         this + " gotcha " + visibilityLevel);
//                    }
//
//            }
//        }
//        this.visibilityLevel = visibilityLevel;
//    }
//
//    public VISIBILITY_LEVEL getVisibilityLevel(boolean active) {
//        if (VisionManager.isVisionHacked()) {
//            return VISIBILITY_LEVEL.CLEAR_SIGHT;
//        }
//        if (!active) {
//            if (!game.isDebugMode()) {
//                return getVisibilityLevelForPlayer();
//            }
//        }
//        return visibilityLevel;
//    }
//
//    public VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
//        if (visibilityLevelForPlayer == null) {
//            if (game.isDebugMode())
//                return VISIBILITY_LEVEL.CLEAR_SIGHT;
//            return VISIBILITY_LEVEL.UNSEEN;
//        }
//        if (isDisplayEnemyVisibility()) {
//            return visibilityLevel;
//        }
//        return visibilityLevelForPlayer;
//    }
//
//    private void setVisibilityLevelForPlayer(VISIBILITY_LEVEL visibilityLevelForPlayer) {
//        this.visibilityLevelForPlayer = visibilityLevelForPlayer;
//    }
//
//
//
//
//    public OUTLINE_TYPE getOutlineType() {
//        if (getGame().isSimulation()) {
//            return null;
//        }
//        if (VisionManager.isVisionHacked()) {
//            return null;
//        }
//        if (!game.isDebugMode()) {
//            return getOutlineTypeForPlayer();
//        }
//        return outlineType;
//    }
//
//    public void setOutlineType(OUTLINE_TYPE outlineType) {
//        if (getGame().getManager().getActiveObj() != null) {
//            if (getGame().getManager().getActiveObj().isMine())
//            // TODO MAIN HERO ONLY?
//            {
//                setOutlineTypeForPlayer(outlineType);
//            }
//        }
//        else {
//            setOutlineTypeForPlayer(outlineType);
//        }
//        if (outlineType != null)
//            if (isMine()) {
//                return;
//            }
//        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG,
//         this + "   setOutlineType " + outlineType);
//        this.outlineType = outlineType;
//    }
//
//    public OUTLINE_TYPE getOutlineTypeForPlayer() {
//        if (isDisplayEnemyVisibility()) {
//            return outlineType;
//        }
//        return outlineTypeForPlayer;
//    }
//
//    public void setOutlineTypeForPlayer(OUTLINE_TYPE outlineTypeForPlayer) {
//
//        this.outlineTypeForPlayer = outlineTypeForPlayer;
//        if (outlineTypeForPlayer == null) {
//            if (getGame().getManager().getActiveObj() != null) {
//                if (!getGame().getManager().getActiveObj().isMine()) {
//                    main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG, "outlineTypeForPlayer set to "
//                     + outlineTypeForPlayer);
//                }
//            }
//        }
//
//        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG,
//         this + "   setOutlineTypeForPlayer " + outlineTypeForPlayer);
//        // main.system.auxiliary.getGame().getLogManager().appendSpecialLog(SPECIAL_LOG.VISIBILITY, "outlineTypeForPlayer set to "
//        // + outlineTypeForPlayer);
//    }
//
//    protected boolean isDisplayEnemyVisibility() {
//        return false;
//    }
//
//    public UNIT_TO_UNIT_VISION getUnitVisionStatus() {
//        if (VisionManager.isVisionHacked()) {
//            return VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT;
//        }
//        if (activeUnitVisionStatus == null) {
//            try {
//                activeUnitVisionStatus = getGame().getVisionMaster()
//                 .getUnitVisibilityStatus(this);
//            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
//            }
//        }
//        return activeUnitVisionStatus;
//    }
//
//    public void setUnitVisionStatus(UNIT_TO_UNIT_VISION unitVisionStatus) {
//        main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG, "setUnitVisionStatus " + getNameAndCoordinate() +
//         " from " +
//         this.activeUnitVisionStatus +
//         " to "
//         + unitVisionStatus.toString());
//
//        this.activeUnitVisionStatus = unitVisionStatus;
//        if (unitVisionStatus != null) {
//            setProperty(PROPS.VISIBILITY_STATUS, unitVisionStatus.toString());
//        }
//    }
//
//    public UNIT_TO_PLAYER_VISION getActivePlayerVisionStatus() {
//        if (VisionManager.isVisionHacked()) {
//            return VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
//        }
//        return activeVisionStatus;
//    }
//
//    public UNIT_TO_PLAYER_VISION getPlayerVisionStatus(boolean active) {
//        if (VisionManager.isVisionHacked()) {
//            return VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
//        }
//
//        if (active) {
//            return activeVisionStatus;
//        }
//        return playerVisionStatus;
//    }
//
//    public void setPlayerVisionStatus(UNIT_TO_PLAYER_VISION playerVisionStatus) {
//        if (getGame().getManager().getActiveObj() != null) {
//            if (getGame().getManager().getActiveObj().isMine()) {
////                if (this instanceof Unit)
////              main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG, "set PlayerVisionStatus " + getNameAndCoordinate()   +
////                 " from " +
////                 this.playerVisionStatus +
////                 " to "
////                 + playerVisionStatus.toString());
//                this.playerVisionStatus = playerVisionStatus;
//            }
//        }
////        if (this instanceof Unit)
////      main.system.auxiliary.log.LogMaster.log(LOG_CHANNEL.VISIBILITY_DEBUG, "set activeVisionStatus " + getNameAndCoordinate()   +
////         " from " +
////         this.activeVisionStatus +
////         " to "
////         + playerVisionStatus.toString());
//        this.activeVisionStatus = playerVisionStatus;
//        if (playerVisionStatus != null) {
//            setProperty(PROPS.DETECTION_STATUS, playerVisionStatus.toString());
//        }
//
//    }
//
//
//    public boolean isDetected() {
//        if (VisionManager.isVisionHacked()) {
//            return true;
//        }
//        if (getGame().getManager().getActiveObj() != null) {
//            if (getGame().getManager().getActiveObj().isMine()) {
//                return isDetectedByPlayer();
//            }
//        }
//        return detected;
//    }
//
//    public void setDetected(boolean b) {
//        if (!b) {
//            this.detected = b;
//        }
//        this.detected = b;
//        if (getGame().getManager().getActiveObj() != null) {
//            if (getGame().getManager().getActiveObj().isMine()) {
//                setDetectedByPlayer(b);
//            }
//        }
//    }
//
//    public boolean isDetectedByPlayer() {
//        if (VisionManager.isVisionHacked()) {
//            return true;
//        }
//        if (isMine()) return true;
//        return detectedByPlayer;
//    }
//
//    public void setDetectedByPlayer(boolean detectedByPlayer) {
//        if (!detectedByPlayer) {
//            this.detectedByPlayer = detectedByPlayer;
//        }
//        this.detectedByPlayer = detectedByPlayer;
//    }
    public boolean checkVisible() {

        return VisionManager.checkVisible(this);
    }

    public boolean isFlippedImage() {
        if (this instanceof Unit) {
            Unit heroObj = (Unit) this;

            checkBool(DYNAMIC_BOOLS.FLIPPED);

            return heroObj.getDirection() == DirectionMaster.FLIP_DIRECTION;
        }
        return false;
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


    public void outsideCombatReset() {
        setParam(PARAMS.ILLUMINATION, 0);
        setDirty(false);
    }

    public PLAYER_VISION getActivePlayerVisionStatus() {
        return getPlayerVisionMapper().get(this);
    }

    public PLAYER_VISION getPlayerVisionStatus(boolean active) {
        if (active)
            return getActivePlayerVisionStatus();
        return getPlayerVisionStatus();
    }

    public VISIBILITY_LEVEL getVisibilityLevel(boolean active) {
        if (active)
            return getVisibilityLevelMapper().get(this);
        return getVisibilityLevelMapper().getForMe(this);
    }

    public UNIT_VISION getUnitVisionStatus(BattleFieldObject object) {
        return getUnitVisionMapper().get(object, this);
    }

    public boolean isDetected(DC_Player owner) {
        return getDetectionMapper().get(owner, this);
    }

    public Integer getGamma(Unit source) {
        return getGammaMapper().get(source, this);
    }
    public void setGamma(Unit source, Integer i) {
          getGammaMapper().set(source, this, i);
        if (source.isPlayerCharacter()) {
            {
                setGamma(i);
                if (GammaMaster.DEBUG_MODE)
                if (game.isDebugMode())
                    main.system.auxiliary.log.LogMaster.log(1,this + " gamma = " +i);
            }
        }
    }

    public void setVisibilityLevel(Unit source, VISIBILITY_LEVEL visibilityLevel) {
        getVisibilityLevelMapper().set(source, this, visibilityLevel);
    }

    public void setUnitVisionStatus(UNIT_VISION status, BattleFieldObject observer) {
        getUnitVisionMapper() .set(observer, this, status);
    }

    public VisionController getVisionController() {
        if (visionController == null) {
            if (!isSimulation())
                visionController=getGame().getVisionMaster().getVisionController();
            else {
                return null;
            }
        }
        return visionController;
    }

    public boolean isVisibilityOverride() {
        return visibilityOverride;
    }

    public void setVisibilityOverride(boolean visibilityOverride) {
        this.visibilityOverride = visibilityOverride;
    }

    public boolean isObstructing(Obj source, DC_Obj target) {
        return false;
    }

    public DIRECTION getDirection() {
        return null;
    }
}
