package eidolons.entity.obj;

import eidolons.ability.AddSpecialEffects;
import eidolons.ability.effects.common.ModifyValueEffect;
import eidolons.content.DC_ContentValsManager;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.handlers.DC_ObjMaster;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.GammaMaster;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionController;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionHelper;
import eidolons.game.battlecraft.logic.battlefield.vision.mapper.*;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.battlecraft.logic.mission.universal.DC_Player;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.content.CONTENT_CONSTS;
import main.content.CONTENT_CONSTS.DYNAMIC_BOOLS;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
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
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class DC_Obj extends MicroObj {

    Module module;

    protected Map<SPECIAL_EFFECTS_CASE, Effect> specialEffects;
    protected Map<DAMAGE_CASE, List<Damage>> bonusDamage;
    protected DAMAGE_TYPE dmg_type;

    protected Integer gamma;
    private VisionController visionController;
    private boolean visibilityOverride;
    private boolean resetIgnored;
    private boolean visibilityFrozen;

    protected boolean pale;

    protected Set<ObjType> appliedTypes = new LinkedHashSet<>();
    protected ObjType originalType;

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
        if (getVisionController() == null) {
            return null;
        }
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

    public Integer getCounter(COUNTER counter) {
        return getCounter(counter.getName());
    }

    @Override
    public void init() {
        super.init();
        addDefaultValues();
    }

    protected void addDefaultValues() {
        if (Flags.isDefaultValuesAddedDynamically())
            DC_ContentValsManager.addDefaultValues(this);
    }

    public boolean checkInSight() {
        return getUnitVisionStatus() == UNIT_VISION.IN_PLAIN_SIGHT;
    }

    public boolean checkInSightForUnit(Unit unit) {
        return getGame().getVisionMaster().getUnitVisibilityStatus(this, unit) == UNIT_VISION.IN_PLAIN_SIGHT;
    }

    @Override
    public String getToolTip() {

        if (!VisionHelper.checkDetected(this)) {
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
        return checkPassive(STANDARD_PASSIVES.DEXTEROUS);
    }

    public boolean isFlying() {
        return checkPassive(STANDARD_PASSIVES.FLYING);
    }

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
        ref.setSource(getId());
        ref.setID(KEYS.THIS, getId());
        Effect effect = specialEffects.get(case_type);
        getGame().getLogManager().log(getName() + ": special effect " + case_type.getName());
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
        getOutlineMapper().set(this, outlineTypeForPlayer);
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
        getOutlineMapper().setForMe(this, outlineTypeForPlayer);
    }

    public VISIBILITY_LEVEL getVisibilityLevelForPlayer() {
        return getVisibilityLevelMapper().getForMe(this);
    }

    public void setVisibilityLevelForPlayer(VISIBILITY_LEVEL visibilityLevelForPlayer) {
        getVisibilityLevelMapper().setForMe(this, visibilityLevelForPlayer);
    }

    public boolean checkVisible() {

        return VisionHelper.checkVisible(this);
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
        if (getGammaMapper() == null) {
            return null;
        }
        return getGammaMapper().get(source, this);
    }

    public void setGamma(Unit source, Integer i) {
        if (getGammaMapper() != null) {
            getGammaMapper().set(source, this, i);
        }
        if (source.isPlayerCharacter()) {
            {
                setGamma(i);
                if (GammaMaster.DEBUG_MODE)
                    if (game.isDebugMode())
                        LogMaster.log(1, this + " gamma = " + i);
            }
        }
    }

    public void setVisibilityLevel(Unit source, VISIBILITY_LEVEL visibilityLevel) {
        getVisibilityLevelMapper().set(source, this, visibilityLevel);
    }

    public void setUnitVisionStatus(UNIT_VISION status, BattleFieldObject observer) {
        getUnitVisionMapper().set(observer, this, status);
    }

    public VisionController getVisionController() {
        if (visionController == null) {
            if (!isSimulation() || CoreEngine.isLevelEditor())
                visionController = getGame().getVisionMaster().getVisionController();
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

    public boolean isResetIgnored() {
        return resetIgnored;
    }

    @Override
    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public void setResetIgnored(boolean resetIgnored) {
        this.resetIgnored = resetIgnored;
    }

    public void setPale(boolean pale) {
        this.pale = pale;
    }

    public boolean isPale() {
        return pale;
    }

    public void log(String s) {
        getGame().getLogManager().log(getName() + ": " + s);
    }

    public boolean isVisibilityFrozen() {
        return visibilityFrozen;
    }

    public void setVisibilityFrozen(boolean visibilityFrozen) {
        this.visibilityFrozen = visibilityFrozen;
    }

    public CONTENT_CONSTS.COLOR_THEME getColorTheme() {
        if (!getProperty("COLOR_THEME").isEmpty()) {
            return new EnumMaster<CONTENT_CONSTS.COLOR_THEME>().retrieveEnumConst(CONTENT_CONSTS.COLOR_THEME.class,
                    getProperty("COLOR_THEME"));
        }
        return getGame().getDungeonMaster().getStructMaster().getLowestStruct(getCoordinates()).getColorTheme();
    }

    public Set<ObjType> getAppliedTypes() {
        return appliedTypes;
    }

    public ObjType getOriginalType() {
        return originalType;
    }

    public void setOriginalType(ObjType originalType) {
        this.originalType = originalType;
    }
}
