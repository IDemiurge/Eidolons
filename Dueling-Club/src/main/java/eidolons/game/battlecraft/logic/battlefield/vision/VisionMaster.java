package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.Obj;
import main.game.bf.GenericVisionManager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LOG_CHANNEL;
import main.system.auxiliary.log.LogMaster;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by JustMe on 2/22/2017.
 */
public class VisionMaster implements GenericVisionManager {

    private final VisionRule visionRule;
    ConcurrentLinkedDeque<BattleFieldObject> visibleList = new ConcurrentLinkedDeque<>();
    ConcurrentLinkedDeque<BattleFieldObject> invisibleList = new ConcurrentLinkedDeque<>();
    private SightMaster sightMaster;
    private DetectionMaster detectionMaster;
    private IlluminationMaster illuminationMaster;
    private VisibilityMaster visibilityMaster;
    private GammaMaster gammaMaster;
    private HintMaster hintMaster;
    private OutlineMaster outlineMaster;
    private DC_Game game;
    private boolean fastMode;
    private boolean firstResetDone;
    private VisionController visionController;
    private BattleFieldObject[] visible;
    private BattleFieldObject[] invisible;
    private boolean visionDebugMode;

    public VisionMaster(DC_Game game) {
        this.game = game;
        detectionMaster = new DetectionMaster(this);
        outlineMaster = new OutlineMaster(this);
        gammaMaster = new GammaMaster(this);
        visibilityMaster = new VisibilityMaster(this);
        illuminationMaster = new IlluminationMaster(this);
        sightMaster = new SightMaster(this);
        hintMaster = new HintMaster(this);
        visionController = new VisionController(this);
        visionRule = new VisionRule(this);

    }

    @Override
    public boolean isVisionTest() {
        return true;
    }
    public static boolean isNewVision() {
        return true;
    }

    public static boolean isLastSeenOn() {
        return true;
    }

    public void reinit() {
        visionController.init();
    }

    public void resetVisibilityStatuses() {
        Chronos.mark("VISIBILITY REFRESH");
        getIlluminationMaster().clearCache();
        getSightMaster().clearCaches();
        visionController.reset();

        if (getActiveUnit() == null) {
            LogMaster.log(1, "***********null active activeUnit for visibility!");
            return;
        }
//        resetForActiveUnit();

        if (ExplorationMaster.isExplorationOn() &&
                getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().isPeriodResetRunning()) {
            LogMaster.verbose("Vision reset skipped by period; time left: " +
                    getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().getVisibilityResetTimer());
        } else {
            getGame().getRules().getIlluminationRule().resetIllumination();
            getGame().getRules().getIlluminationRule().applyLightEmission();
        }

        visionRule.fullReset(getGame().getObjMaster().getUnitsArray());
        getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().resetVisibilityResetTimer();


        getActiveUnit().setUnitVisionStatus(UNIT_VISION.IN_PLAIN_SIGHT);
        getActiveUnit().setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);

        resetLastKnownCoordinates();
//        for (Unit sub : game.getUnits()) {
//            sightMaster.resetSightStatuses(sub);
//        }
        for (Object sub : getGame().getDungeonMaster().getPlayerManager().getPlayers()) {
            DC_Player player = (DC_Player) sub;
            resetPlayerVision(player);
        }
        triggerGuiEvents();

        firstResetDone = true;
//        visionController.log(getActiveUnit());

//    try{    getVisionController().logAll();}catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);}
//        getVisionController().log(getActiveUnit(), visibleList.toArray(new DC_Obj[visibleList.size()]));
        Chronos.logTimeElapsedForMark("VISIBILITY REFRESH", true);
    }


    private void resetPlayerVision(DC_Player player) {
        for (Obj sub : player.collectControlledUnits()) {
            Unit unit = (Unit) sub;
            if (player.isMe()) //enemy sees all objects always...?
                for (Structure object : game.getStructures()) {
                    PLAYER_VISION newVision = visionRule.playerVision(unit, object);
                    PLAYER_VISION oldVision = getVisionController().getPlayerVisionMapper().get(player, object);
                    if (newVision.isGreater(oldVision) || oldVision == null)
                        getVisionController().getPlayerVisionMapper().set(player, object, newVision);

                }

            //enemy units vision for the player
            for (Unit object : game.getUnits()) {
                if (!player.isMe())
                    if (!object.isOwnedBy(player))
                        getVisionController().getPlayerVisionMapper().set(player, object,
                                visionRule.playerVision(unit, object));
            }
        }
    }

    public VisionRule getVisionRule() {
        return visionRule;
    }

    private void resetLastKnownCoordinates() {
        //   TODO must communicate with GridPanel
//        for (Unit u : game.getUnits()) {
//            if (checkVisible(u)) {
//                u.setLastKnownCoordinates(u.getCoordinates());
//            }
//        }
    }

    public void triggerGuiEvents() {
        visibleList.clear();
        invisibleList.clear();
        for (BattleFieldObject sub : game.getBfObjects()) {
            if (sub.isDead())
                continue;
            if (isVisibleOnGrid(sub))
                visibleList.add(sub);
            else invisibleList.add(sub);
        }
//        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        if (LOG_CHANNEL.VISIBILITY_DEBUG.isOn()) {
            LogMaster.log(1, ">>>>>> visibleList  = " + visibleList);
            LogMaster.log(1, ">>>>>> invisibleList  = " + invisibleList);
            String string = "";
            for (BattleFieldObject sub : visibleList) {
                string += sub + ": \n";
                string += "getVisibilityLevelForPlayer= " + sub.getVisibilityLevelForPlayer() + "\n";
                string += "getVisibilityLevel= " + sub.getVisibilityLevel() + "\n";
                string += "getPlayerVisionStatus= " + sub.getPlayerVisionStatus(true) + "\n";
                string += "getGamma= " + sub.getGamma() + "\n";
            }

            LogMaster.log(1, "***********" +
                    "" + string);

        }
        visible = visibleList.toArray(new BattleFieldObject[0]);
        invisible = invisibleList.toArray(new BattleFieldObject[0]);
        GuiEventManager.trigger(GuiEventType.UNIT_VISIBLE_OFF, invisibleList);
        GuiEventManager.trigger(GuiEventType.UNIT_VISIBLE_ON, visibleList);
    }

    private boolean isVisibleOnGrid(BattleFieldObject object) {
        return visionRule.isDisplayedOnGrid(
                game.getManager().getMainHero(), object);
    }


    public UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, Unit activeUnit) {
        return sightMaster.getUnitVisibilityStatus(unit, activeUnit);
    }

    public void refresh() {
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Refreshing visibility...");
        sightMaster.clearCaches();
        resetVisibilityStatuses();
    }

    public VISIBILITY_LEVEL getVisibilityLevel(Unit source, DC_Obj target) {
        return getVisibilityMaster().getUnitVisibilityLevel(source, target);
    }

    public boolean checkDetectedEnemy(DC_Obj obj) {
        return getDetectionMaster().checkDetectedEnemy(obj);
    }

    @Override
    public boolean checkInvisible(Obj obj1) {
        DC_Obj obj = (DC_Obj) obj1;
        return obj.getPlayerVisionStatus() == PLAYER_VISION.INVISIBLE;
    }


    public boolean checkInvisible(DC_Obj obj, boolean active) {
        return obj.getPlayerVisionStatus(active) == PLAYER_VISION.INVISIBLE;
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
        return ExplorationMaster.isExplorationOn()
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
        Unit unit = Eidolons.getMainHero();
        if (unit == null) {
            unit = Eidolons.game.getManager().getActiveObj();
        }
        if (unit == null) {
            if (!Eidolons.game.getPlayer(true).collectControlledUnits().isEmpty())
                unit = (Unit) Eidolons.game.getPlayer(true).collectControlledUnits().iterator().next();
        }
        return unit;
    }

    public VisionController getVisionController() {
        return visionController;
    }

    public BattleFieldObject[] getVisible() {
        return visible;
    }

    public BattleFieldObject[] getInvisible() {
        return invisible;
    }


    public void overrideVisionOff(BattleFieldObject unit) {
        unit.setVisibilityFrozen(false);
        refresh();
    }

    public void overrideVision(BattleFieldObject unit, String s) {
        VISIBILITY_LEVEL vl = null;
        UNIT_VISION vs = null;
        switch (s) {
            case "off":
                vl = VISIBILITY_LEVEL.UNSEEN;
                vs = UNIT_VISION.BEYOND_SIGHT;
                unit.setPlayerVisionStatus(PLAYER_VISION.INVISIBLE);
                break;
            case "on":
                vl = VISIBILITY_LEVEL.CLEAR_SIGHT;
                vs = UNIT_VISION.IN_PLAIN_SIGHT;
                unit.setPlayerVisionStatus(PLAYER_VISION.DETECTED);
                break;
        }
        unit.setVisibilityLevel(vl);
        unit.setUnitVisionStatus(vs);
        unit.setVisibilityFrozen(true);
        refresh();

    }

    public boolean isVisionDebugMode() {
        return visionDebugMode;
    }

    public void setVisionDebugMode(boolean visionDebugMode) {
        this.visionDebugMode = visionDebugMode;
    }
}
