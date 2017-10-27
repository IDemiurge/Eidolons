package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.*;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battle.universal.DC_Player;
import main.game.bf.GenericVisionManager;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.test.debug.DebugMaster;

import java.util.Collection;
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
        ClearShotCondition.clearCache();
        getGammaMaster().clearCache();
        getIlluminationMaster().clearCache();
//        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
//        setActiveUnit(
//         ExplorationMaster.isExplorationOn()
//          ? getSeeingUnit() :
//          game.getTurnManager().getActiveUnit(true));

        if (getActiveUnit() == null) {
            LogMaster.log(0, "null active activeUnit for visibility!");
            return;
        }
        boolean mine = getActiveUnit().getOwner().isMe();


        getActiveUnit().setPlainSightSpectrumCoordinates(new DequeImpl<>());
        getActiveUnit().setSightSpectrumCoordinates(new DequeImpl<>());


        setRelativeActiveUnitVisibility(getActiveUnit(), game.getStructures());
        setRelativeActiveUnitVisibility(getActiveUnit(), game.getPlayer(!mine).getControlledUnits());

        Set<Obj> cells = isFastMode() ? game.getBattleField().getGrid()
         .getCellsWithinVisionBounds() : game.getCells();
        setRelativeActiveUnitVisibility(getActiveUnit(), cells);

        setAlliedVisibility(game.getPlayer(mine).getControlledUnits());

        getVisibilityMaster().resetVisibilityLevels();

//        setRelativePlayerVisibility(game.getPlayer(mine), cells);
        //Chronos.logTimeElapsedForMark("PLAYER for terain cells VISIBILITY REFRESH");

        setRelativePlayerVisibility(game.getPlayer(mine), game.getStructures()); // DC_Player.NEUTRAL.getControlledUnits());

        setRelativePlayerVisibility(game.getPlayer(mine), game.getPlayer(!mine)
         .getControlledUnits());

        getActiveUnit().setUnitVisionStatus(VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
        getActiveUnit().setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);
//        resetLastKnownCoordinates();
        //Chronos.logTimeElapsedForMark("PLAYER VISIBILITY REFRESH");
    }

    private void setVisibilityLevel(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj target = (DC_Obj) obj;
            target.setVisibilityLevel(getVisibilityMaster().getUnitVisibilityLevel(
             getActiveUnit() ,  target ));
        }
    }

    private void setAlliedVisibility(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj unit = (DC_Obj) obj;
            UNIT_TO_UNIT_VISION status = getSightMaster().getUnitVisionStatusPrivate(unit,
             this.getActiveUnit());
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


    private void setRelativeActiveUnitVisibility(Unit activeUnit, Collection<? extends Obj> units) {
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
        return sightMaster.getUnitVisibilityStatus(unit, getGame().getManager().getActiveObj());
    }

    private void setRelativePlayerVisibility(DC_Player player, Collection<? extends Obj> units) {

        for (Obj obj : units) {
            DC_Obj target = (DC_Obj) obj;
            if (target instanceof Unit)
            {
                if (ExplorationMaster.isExplorationOn ()||   ((Unit) target).getAI().isOutsideCombat()) {
                    target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.UNKNOWN);
                }
            }
//            else
//                target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.UNKNOWN);
            if (target instanceof Structure)
            {
                if (!((Structure) target).isWall() || !target.isDetectedByPlayer()) {
                    target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.UNKNOWN);
                }
            }
            if (DebugMaster.isOmnivisionOn()) {
                if (player.isMe()) {
                    target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED);
                    continue;
                }
            }
            boolean result = false; // detected or not
            if (checkInvisible(target)) {
                if (target.getActivePlayerVisionStatus() != VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    player.getLastSeenCache().put(target, target.getCoordinates());
                }
                target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.INVISIBLE);
                continue;
            }

            for (Obj obj1 : player.getControlledUnits()) {
                if (obj1 != getActiveUnit()) {
                    continue; //TODO ideally, should check for all allies?
                }
                VISIBILITY_LEVEL status = target.getVisibilityLevel();
//                if (!ExplorationMaster.isExplorationOn())
//                    if (status==VISIBILITY_LEVEL.CONCEALED){
//                        result = false;
//                    }
                if (
                 status != VISIBILITY_LEVEL.BLOCKED
                  && status != VISIBILITY_LEVEL.UNSEEN

                 ) {

                    if (status == VISIBILITY_LEVEL.CONCEALED) {
                        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.CONCEALED);
                    } else {
                        target.setDetected(true);
                        if (player.isMe()) {
                            target.setDetectedByPlayer(true);
                        }
                    if (status == VISIBILITY_LEVEL.CLEAR_SIGHT) {
                        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.DETECTED);
                    }
                    else {
                        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.KNOWN);
                    }
                    }
                    result = true;
                    break; // TODO set VL to *max* => Identification Level
                }

            }
            if (result) {
                player.getLastSeenCache().put(target, target.getCoordinates());
            } else {
                if (target.isDetectedByPlayer()) {
                    if (obj instanceof DC_Cell) {
                        target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
                    } else if (target.getVisibilityLevel() != VISIBILITY_LEVEL.BLOCKED){
                        BattleFieldObject unit = (BattleFieldObject) obj;
                        if (unit.isWall() || unit.isLandscape()) {
                            target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
                        }
                    }

                }
//                if (target.getVisibilityLevel() != VISIBILITY_LEVEL.BLOCKED) {
//                    if (player.isMe()) {
//                        if (obj instanceof DC_Cell) {
//                            target.setDetectedByPlayer(true);
//                        }
//                    else {
//                        BattleFieldObject unit = (BattleFieldObject) obj;
//                        if (unit.isWall() || unit.isLandscape()) {
//                            target.setDetectedByPlayer(true);
//
//                        }
//                    }
//                    } else
                        {
                        //TODO  ???
//                        if (target.isDetected()
//                         ) {
//                            target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.KNOWN);
//                        } else {
//                            target.setPlayerVisionStatus(VisionEnums.UNIT_TO_PLAYER_VISION.UNKNOWN);
//                        }
                    }
                }


        }

    }


    public boolean checkVisible(DC_Obj obj) {
        boolean visible = checkVisible(obj, false);
//        if (!visible) {
//            obj.setDetected(false);
//        }
        return visible;
    }

    public boolean checkVisible(DC_Obj obj, boolean active) {
        Unit source = active ? getActiveUnit() : getSeeingUnit();
        if (source == null) {
            return false;
        }
        if (source.getZ() != obj.getZ() && !(obj instanceof Entrance)) // dungeon
        // sublevel
        {
            return false;
        }
        if (source.getOwner().isMe()) {
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
        //Chronos.mark("VISIBILITY REFRESH");
        sightMaster.clearCaches();
        resetVisibilityStatuses();
        //Chronos.logTimeElapsedForMark("VISIBILITY REFRESH");
    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target) {
        return getVisibilityMaster().getUnitVisibilityLevel(source, target);
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
        return !checkVisible(obj, true);
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
//        if (activeUnit == null)
//            return getGame().getManager().getActiveObj();
//        return activeUnit;
       return  ExplorationMaster.isExplorationOn()
         ? getSeeingUnit() :
         game.getTurnManager().getActiveUnit(true);
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

    public Unit getSeeingUnit() {
        Unit unit = Eidolons.game.getManager().getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        if (unit == null) {
            unit = (Unit) Eidolons.game.getPlayer(true).getControlledUnits().iterator().next();
        }
        return unit;
    }
}
