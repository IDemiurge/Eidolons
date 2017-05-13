package main.game.battlecraft.logic.battlefield.vision;

import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.DC_Player;
import main.game.battlecraft.rules.action.StealthRule;
import main.game.bf.GenericVisionManager;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.debug.DebugMaster;

import java.util.Set;

/**
 * Created by JustMe on 2/22/2017.
 */
public class VisionMaster implements GenericVisionManager {


    private SightMaster sightMaster;
    private DetectionMaster detectionMaster;
    private IlluminationMaster illuminationMaster;
    private VisibilityMaster visibilityMaster;
    private GammaMaster gammaMaster;
    private HintMaster hintMaster;
    private OutlineMaster outlineMaster;


    private DC_Game game;
    private Unit activeUnit;
    private boolean fastMode;

    public VisionMaster(DC_Game game) {
        this.game = game;
        detectionMaster = new DetectionMaster(this);
        outlineMaster = new OutlineMaster(this);
        gammaMaster = new GammaMaster(this);
        visibilityMaster = new VisibilityMaster(this);
        illuminationMaster = new IlluminationMaster(this);
        sightMaster = new SightMaster(this);
        hintMaster = new HintMaster(this);

    }

    private void resetLastKnownCoordinates() {
        for (Unit u : game.getUnits()) {
            if (checkVisible(u)) {
                u.setLastKnownCoordinates(u.getCoordinates());
            }
        }

    }


    public void resetVisibilityStatuses() {
        getGammaMaster().clearCache();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        activeUnit = game.getTurnManager().getActiveUnit(true);

        if (activeUnit == null) {
            LogMaster.log(0, "null active activeUnit for visibility!");
            return;
        }
        boolean mine = activeUnit.getOwner().isMe();

        activeUnit = activeUnit;
        activeUnit.setPlainSightSpectrumCoordinates(new DequeImpl<>());
        activeUnit.setSightSpectrumCoordinates(new DequeImpl<>());

        Chronos.mark("UNIT VISIBILITY REFRESH");

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for neutral units...");
        try {
            setRelativeActiveUnitVisibility(activeUnit, Player.NEUTRAL.getControlledUnits());
        } catch (Exception e) {

        }
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for enemy units...");
        setRelativeActiveUnitVisibility(activeUnit, game.getPlayer(!mine).getControlledUnits());

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for terrain cells...");
        Set<Obj> cells = isFastMode() ? game.getBattleField().getGrid()
                .getCellsWithinVisionBounds() : game.getCells();
        setRelativeActiveUnitVisibility(activeUnit, cells);

        Chronos.logTimeElapsedForMark("UNIT VISIBILITY REFRESH");

        Chronos.mark("PLAYER VISIBILITY REFRESH");

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for allied units...");
        setAlliedVisibility(game.getPlayer(mine).getControlledUnits());

        getVisibilityMaster().resetVisibilityLevels();

        Chronos.mark("PLAYER for terrain cells VISIBILITY REFRESH");
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for terain cells...");
        setRelativePlayerVisibility(game.getPlayer(mine), cells);
        Chronos.logTimeElapsedForMark("PLAYER for terain cells VISIBILITY REFRESH");

        LogMaster
                .log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for neutral units...");
        setRelativePlayerVisibility(game.getPlayer(mine), Player.NEUTRAL.getControlledUnits());

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for enemy units...");
        setRelativePlayerVisibility(game.getPlayer(mine), game.getPlayer(!mine)
                .getControlledUnits());

        // setVisibilityLevel(game.getPlayer(!mine).getControlledUnits());
        // setVisibilityLevel(game.getPlayer(mine).getControlledUnits());
        // setVisibilityLevel(Player.NEUTRAL.getControlledUnits());
        // setVisibilityLevel(cells);

        activeUnit.setUnitVisionStatus(VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
        activeUnit.setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);
        resetLastKnownCoordinates();
        Chronos.logTimeElapsedForMark("PLAYER VISIBILITY REFRESH");
    }

    private void setVisibilityLevel(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj target = (DC_Obj) obj;
            target.setVisibilityLevel(getVisibilityMaster().getUnitVisibilityLevel(target, activeUnit));
        }
    }

    private void setAlliedVisibility(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj unit = (DC_Obj) obj;
            UNIT_TO_UNIT_VISION status = getSightMaster().getUnitVisionStatusPrivate(unit,
                    this.activeUnit);
            // UNIT_TO_PLAYER_VISION detectionStatus =
            // UNIT_TO_PLAYER_VISION.DETECTED;

            if (!unit.isMine()) {
                if (unit.getActivePlayerVisionStatus() == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    if (status == VisionEnums.UNIT_TO_UNIT_VISION.CONCEALED) {
                        status = VisionEnums.UNIT_TO_UNIT_VISION.BEYOND_SIGHT;
                    }
                    // detectionStatus = UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY;
                }
            }
            // unit.setPlayerVisionStatus(detectionStatus); //TODO NEW - enemy
            // exclusive
            unit.setUnitVisionStatus(status);
        }
    }


    private void setRelativeActiveUnitVisibility(Unit activeUnit, Set<Obj> units) {
        for (Obj obj : units) {
            DC_Obj unit = (DC_Obj) obj;

            UNIT_TO_UNIT_VISION status = getSightMaster().getUnitVisionStatusPrivate(unit, activeUnit);

            LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Setting visibility for " + activeUnit + " to "
                    + status.toString());

            unit.setUnitVisionStatus(status);
        }
    }


    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, Unit activeUnit) {
        return sightMaster.getUnitVisibilityStatus(unit, activeUnit);
    }

    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit) {
        return sightMaster.getUnitVisibilityStatus(unit, activeUnit);
    }

    private void setRelativePlayerVisibility(DC_Player player, Set<Obj> units) {

        for (Obj obj : units) {
            DC_Obj target = (DC_Obj) obj;
            if (DebugMaster.isOmnivisionOn()) {
                if (player.isMe()) {
                    target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED);
                    return;
                }
            }
            // Chronos.logTimeElapsedForMark("setRelativePlayerVisibility");

            boolean result = false; // detected or not

            Chronos.mark("checkInvisible " + target);
            if (checkInvisible(target)) {
                if (target.getActivePlayerVisionStatus() != VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    player.getLastSeenCache().put(target, target.getCoordinates());
                }
                target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE);
                if (Chronos.getTimeElapsedForMark("checkInvisible " + target) > 5) {
                    Chronos.logTimeElapsedForMark("checkInvisible " + target);
                }
                continue;
            }
            if (Chronos.getTimeElapsedForMark("checkInvisible " + target) > 5) {
                Chronos.logTimeElapsedForMark("checkInvisible " + target);
            }

            // if (target.getUnitVisionStatus() == UNIT_TO_UNIT_VISION.IN_SIGHT)
            // {
            // target.setDetected(true);
            // result = true;
            // } else { //sight range will influence VL

            for (Obj obj1 : player.getControlledUnits()) {
                // if (fastMode) //TODO
                if (obj1 != activeUnit) {
                    continue;
                }
                Unit source = (Unit) obj1;
                Chronos.mark("getUnitVisibilityLevel " + target);
                VISIBILITY_LEVEL status = getVisibilityMaster().getUnitVisibilityLevel(target, source);
                if (Chronos.getTimeElapsedForMark("getUnitVisibilityLevel " + target) > 5) {
                    Chronos.logTimeElapsedForMark("getUnitVisibilityLevel " + target);
                }

                // target.setIdentificationLevel(status);
                // if any activeUnit has him in sight, he's detected
                if (status == VISIBILITY_LEVEL.CLEAR_SIGHT) {
                    // TODO IF AN OUTLINE IS SEEN - 'KNOWN'?
                    LogMaster.log(LogMaster.VISIBILITY_DEBUG, ">>>> " + target + " SPOTTED BY "
                            + source);
                    target.setDetected(true);
                    result = true;
                    break; // TODO set VL to *max* => Identification Level
                }

            }
            if (result) {
                target.setDetected(true);
                if (player.isMe()) {
                    target.setDetectedByPlayer(true);
                }
                target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED);
                player.getLastSeenCache().put(target, target.getCoordinates());
            } else if (!visibilityMaster.isZeroVisibility(target)) {
                if (player.isMe()) {
                    if (obj instanceof DC_Cell) {
                        target.setDetectedByPlayer(true);
                    } else {
                        Unit unit = (Unit) obj;
                        if (unit.isWall() || unit.isLandscape()) {
                            target.setDetectedByPlayer(true);
                        }
                    }
                } else {
                    if (target.isDetected()

                        // TODO only walls? || (obj.getOwner() == Player.NEUTRAL &&
                        // obj.getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ)
                            ) {
                        target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
                    } else {
                        target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN);
                    }
                    // //Chronos.logTimeElapsedForMark("checkInvisible " + obj);
                }
            }

        }

    }


    public boolean checkVisible(DC_Obj obj) {
        boolean visible = checkVisible(obj, false);
        if (!visible) {
            obj.setDetected(false);
        }
        return visible;
    }

    public boolean checkVisible(DC_Obj obj, boolean active) {
        if (activeUnit == null) {
            return true;
        }
        if (activeUnit.getZ() != obj.getZ() && !(obj instanceof Entrance)) // dungeon
        // sublevel
        {
            return false;
        }
        if (activeUnit.getOwner().isMe()) {
            if (obj.getOwner().isMe()) {
                return true;
            }
        }
        // if (obj.getPerceptionStatus(active) ==
        // PERCEPTION_STATUS.KNOWN_TO_BE_THERE)
        // return true;

        if (visibilityMaster.isZeroVisibility(obj, active)) {
            if (obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN) {
                return false;
            }
        }

        if (obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE
                || obj.getPlayerVisionStatus(active) == VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY) {
            return false;
        }

        // if (obj.getVisibilityLevel()==VISIBILITY_LEVEL.CLEAR)
        // return true; // ILLUMINATION SHOULD ADD TO SIGHTED-COORDINATES
        // VISIBILITY TODO
        return true;

    }

    /**
     * only invoke after checking if DETECTED
     */
    private boolean checkInvisible(DC_Obj unit) {
        return StealthRule.checkInvisible(unit);
    }

    public void refresh() {
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Refreshing visibility...");
        Chronos.mark("VISIBILITY REFRESH");
        sightMaster.clearCaches();
        resetVisibilityStatuses();
        Chronos.logTimeElapsedForMark("VISIBILITY REFRESH");
    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target) {
        return getVisibilityMaster().getVisibilityLevel(source, target);
    }

    public String getDisplayImagePathForUnit(DC_Obj obj) {
        return getVisibilityMaster().getDisplayImagePathForUnit(obj);
    }

    public boolean checkDetectedForPlayer(DC_Obj obj) {
        return getDetectionMaster().checkDetectedForPlayer(obj);
    }

    public boolean checkDetectedEnemy(DC_Obj obj) {
        return getDetectionMaster().checkDetectedEnemy(obj);
    }

    public boolean checkDetected(DC_Obj obj) {
        return getDetectionMaster().checkDetected(obj);
    }

    public boolean checkDetected(DC_Obj obj, boolean enemy) {
        return getDetectionMaster().checkDetected(obj, enemy);
    }

    @Override
    public boolean checkInvisible(Obj obj1) {
        DC_Obj obj = (DC_Obj) obj1;
        return !checkVisible(obj);
    }

    public boolean isFastMode() {
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    public DetectionMaster getDetectionMaster() {
        return detectionMaster;
    }

    public IlluminationMaster getIlluminationMaster() {
        return illuminationMaster;
    }

    public VisibilityMaster getVisibilityMaster() {
        return visibilityMaster;
    }

    public GammaMaster getGammaMaster() {
        return gammaMaster;
    }

    public SightMaster getSightMaster() {
        return sightMaster;
    }

    public HintMaster getHintMaster() {
        return hintMaster;
    }

    public Unit getActiveUnit() {
        return activeUnit;
    }

    public OutlineMaster getOutlineMaster() {
        return outlineMaster;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }
}
