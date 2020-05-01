package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.mapper.*;
import eidolons.game.core.game.DC_Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/28/2018.
 * <p>
 * Algorithm
 * <p>
 * game-reset
 * outlines reset
 * visibility reset
 * <p>
 * wallmap reset
 * <p>
 * 1.  STATUS FOR UNIT- (CLEAR) IN SIGHT/ BEYOND SIGHT ...
 * 2.  OUTLINE TYPE - based on gamma or clearshot, determines next
 * 3.  VISIBILITY LEVEL - based on outline+status,
 * CLEAR_SIGHT(no outline)
 * OUTLINE(outline with hints)
 * CONCEALED(just something, no hints, except "last seen here" )
 * BLOCKED(no clearshot) //must be unseen?
 * UNSEEN;
 * <p>
 * 4.  * STATUS FOR PLAYER - DETECTED, KNOWN, UNKNOWN,
 * INVISIBLE - if was seen at least once, but not now (stealth/block...)
 * <p>
 * <p>
 * RULES
 * 1) If a unit is beyond the range of [sight], it is always
 * 2)
 */
public class VisionController {
    DC_Game game;
    VisionMaster master;

    OutlineMapper outlineMapper;
    PlayerVisionMapper playerVisionMapper;
    UnitVisionMapper unitVisionMapper;
    DetectionMapper detectionMapper;
    VisibilityLevelMapper visibilityLevelMapper;
    ClearshotMapper clearshotMapper;
    WallObstructionMapper wallObstructionMapper;
    GammaMapper gammaMapper;
    LastSeenMapper lastSeenMapper;
    SeenMapper seenMapper;
    List<GenericMapper> mappers;
    private StealthMapper stealthMapper;
    private DiagObstructMapper diagObstructMapper;


    public VisionController(VisionMaster visionMaster) {
        this.master = visionMaster;
        game = visionMaster.getGame();
        init();
    }

    public void init() {
        mappers = new ArrayList<>();
        mappers.add(stealthMapper = new StealthMapper());
        mappers.add(lastSeenMapper = new LastSeenMapper());
        mappers.add(gammaMapper = new GammaMapper());
        mappers.add(clearshotMapper = new ClearshotMapper());
        mappers.add(unitVisionMapper = new UnitVisionMapper());
        mappers.add(visibilityLevelMapper = new VisibilityLevelMapper());
        mappers.add(outlineMapper = new OutlineMapper());
        mappers.add(playerVisionMapper = new PlayerVisionMapper());
        mappers.add(detectionMapper = new DetectionMapper());
        mappers.add(wallObstructionMapper = new WallObstructionMapper());
        mappers.add(seenMapper = new SeenMapper());
    }

    public SeenMapper getSeenMapper() {
        return seenMapper;
    }

    public LastSeenMapper getLastSeenMapper() {
        return lastSeenMapper;
    }

    public WallObstructionMapper getWallObstructionMapper() {
        return wallObstructionMapper;
    }

    public GammaMapper getGammaMapper() {
        return gammaMapper;
    }

    public DC_Game getGame() {
        return game;
    }

    public VisibilityLevelMapper getVisibilityLevelMapper() {
        return visibilityLevelMapper;
    }

    public VisionMaster getMaster() {
        return master;
    }

    public OutlineMapper getOutlineMapper() {
        return outlineMapper;
    }

    public PlayerVisionMapper getPlayerVisionMapper() {
        return playerVisionMapper;
    }

    public UnitVisionMapper getUnitVisionMapper() {
        return unitVisionMapper;
    }

    public DetectionMapper getDetectionMapper() {
        return detectionMapper;
    }

    public ClearshotMapper getClearshotMapper() {
        return clearshotMapper;
    }

    public void reset() {
        mappers.forEach(mapper -> mapper.reset());
    }

    public void afterCheck() {

        checkOutlines();
        checkVisibilityLevels();
        checkGridVisible();
        checkPlayerVisionStatus();

        //for debug/logging, which is best practice here?
//        List<Structure> list = game.getMaster().getWalls();

    }

    private void checkPlayerVisionStatus() {
    }

    private void checkGridVisible() {
    }

    private void checkVisibilityLevels() {
    }

    private void checkOutlines() {
    }

    public void logAll() {
        for (GenericMapper sub : mappers) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> "
             + sub);
            sub.log();
        }
    }

    public void log(Unit unit) {
        for (GenericMapper sub : mappers) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> "
             + sub);
            try {
                sub.log(unit);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    public void logFor(DC_Obj unit) {
        main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> FOR VALUE"
         + unit);
        for (GenericMapper sub : mappers) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> "
             + sub);
            sub.logForValue(unit);
        }
    }

    public void log(Unit unit, DC_Obj... objs) {
        for (GenericMapper sub : mappers) {
            main.system.auxiliary.log.LogMaster.log(1, ">>>>>>>> "
             + sub);
            if (sub instanceof PlayerMapper)
                sub.log(unit.getOwner(), objs);
            else sub.log(unit, objs);
        }
    }

    public StealthMapper getStealthMapper() {
        return stealthMapper;
    }

    public DiagObstructMapper getDiagObstructMapper() {
        return diagObstructMapper;
    }

    public void setDiagObstructMapper(DiagObstructMapper diagObstructMapper) {
        this.diagObstructMapper = diagObstructMapper;
    }


    public enum VISIBILITY_CHECK_OBJ_CASE {
        WALL, UNIT, STRUCTURE
    }

}
