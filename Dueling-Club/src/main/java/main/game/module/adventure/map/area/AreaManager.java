package main.game.module.adventure.map.area;

import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.enums.EncounterEnums.ENCOUNTER_SUBGROUP;
import main.content.values.parameters.MACRO_PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.global.TimeMaster;
import main.game.module.adventure.gui.MacroGuiManager;
import main.game.module.adventure.map.Area;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.map.Region;
import main.game.module.adventure.travel.EncounterMaster;
import main.game.module.adventure.map.MacroCoordinates;
import main.game.module.adventure.travel.MacroGroup;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.entity.FilterMaster;

import java.util.List;

public class AreaManager {
    private static final Integer MINIMUM_TOTAL_POWER_MOD = 400;
    private static final Integer MAX_TOTAL_POWER_MOD = 1500;
    private static final int MAX_GROUPS_IN_AREA = 25;

    // how to figure out which groups got killed in a skirmish?
    public static void newTurn() {
        // danger_level -> newGroup
        for (Region region : MacroManager.getRegions()) {
            for (Area area : region.getAreas()) {
                area.modifyParameter(
                        MACRO_PARAMS.AREA_CREEP_POWER_TOTAL,
                        TimeMaster.getHoursPerTurn()
                                * area.getIntParam(MACRO_PARAMS.CREEP_POWER_PER_HOUR));
                checkAddGroups(area);
                for (MacroGroup group : area.getGroups()) {
                    if (group.isAmbushing()) {
                        // preCheck continue;
                    } else {
                        if (!group.checkSetAmbush())

                        {
                            group.wander();
                        }
                    }
                }
            }

        }
    }

    public static void hourPassed() {

    }

    private static void checkAddGroups(Area area) {
        int power = area.getIntParam(MACRO_PARAMS.AREA_CREEP_POWER_TOTAL);
        if (power > EncounterMaster.getMinCreepWavePower()) { // area's mod
            addRandomGroup(area);
        }

    }

    public static void initRegionAreas(Region region) {
        for (Area area : region.getAreas()) {
            initWanderingGroups(area);
        }
    }

    public static void assignPlacesToAreas(Region region) {
        places:
        for (Place p : region.getPlaces()) {
            Coordinates coordinates = p.getCoordinates();
            for (Area area : region.getAreas()) {
                if (checkWithinAreaBoundaries(area, coordinates)) {
                    p.setArea(area);
                    continue places;
                }
            }
            Area defaultArea = region.getArea(region
                    .getProperty(MACRO_PROPS.AREA));
            p.setArea(defaultArea);

        }

    }

    public static Area getAreaForCoordinate(Coordinates c) {
        for (Area area : MacroManager.getActiveParty().getRegion().getAreas()) {
            if (checkWithinAreaBoundaries(area, c)) {
                return area;
            }
        }
        return null;
    }

    public static boolean checkWithinAreaBoundaries(Area area,
                                                    Coordinates coordinates) {
        MacroCoordinates prevBoundary = null;
        if (area.getBoundaries().isEmpty()) {
            return false;
        }
        for (MacroCoordinates boundary : area.getBoundaries()) {
            if (prevBoundary == null) {
                prevBoundary = (boundary);
                continue;
            }
            // at least one min/max fit with each line?
            if (!checkLine(coordinates, boundary, prevBoundary)) {
                return false;
            }
            prevBoundary = (boundary);
        }
        return true;
    }

    private static boolean checkLine(Coordinates coordinates, Coordinates c1,
                                     Coordinates c2) {
        int max_x = Math.max(c1.x, c2.x);
        int max_y = Math.max(c1.y, c2.y);
        int min_x = Math.min(c1.x, c2.x);
        int min_y = Math.min(c1.y, c2.y);

        if (coordinates.x > max_x && coordinates.y > max_y) {
            return false;
        }
        return !(coordinates.x < min_x && coordinates.y < min_y);
    }

    public static void assignWanderingGroup(MacroGroup group) {
        // objType instead?
        // groupWanders();
        // region.getAreas();
        // coordinates
        // translate to routes... multiple perhaps TODO

        // route coordinates defined as 'beteween x and y'
        // or perhaps we should

        // so currently the coordinates don't mean much, but perhaps they
        // could...
        //

    }

    public static void initWanderingGroups(Area area) {
        // by number limit?
        int totalPower = getTotalPower(area);
        Loop.startLoop(MAX_GROUPS_IN_AREA);
        while (!Loop.loopEnded()) {
            if (area.checkParameter(MACRO_PARAMS.AREA_CREEP_POWER_TOTAL,
                    totalPower)) {
                break;
            }
            addRandomGroup(area);
        }
        // ENCOUNTER_GROUP

    }

    private static void addRandomGroup(Area area) {
        MacroGroup group = getRandomCreepGroup(area);
        if (group == null) {
            return;
        }
        addGroup(area, group);
        Boolean min_max_normal = null;
        area.modifyParameter(MACRO_PARAMS.AREA_CREEP_POWER_TOTAL,
                EncounterMaster.getPower(group.getEncounterType(),
                        min_max_normal));
    }

    private static void addGroup(Area area, MacroGroup group) {
        group.setCoordinates(getRandomCoordinateWithinArea(area));
        area.addGroup(group);
        // total power for area - danger level,
        // special wave type generation - separate map-prop
        // perhaps one could use actual square metrics?
        area.modifyParameter(MACRO_PARAMS.AREA_CREEP_POWER_TOTAL, group
                .getEncounterType().getIntParam(PARAMS.POWER_BASE));

    }

    public static MacroCoordinates getRandomCoordinateWithinArea(Area area) {
        double x = MacroGuiManager.getMapHeight();
        double y = MacroGuiManager.getMapWidth();
        Loop.startLoop(10000);
        while (!Loop.loopEnded()) {
            MacroCoordinates c = new MacroCoordinates(
                    RandomWizard.getRandomInt((int) x),
                    RandomWizard.getRandomInt((int) y));
            if (checkWithinAreaBoundaries(area, c)) {
                return c;
            }
        }
        return null;
    }

    public static int getTotalPower(Area area) {
        Integer mod = area.getIntParam(MACRO_PARAMS.DANGER_MOD);
        if (mod == 0) {
            mod = MINIMUM_TOTAL_POWER_MOD;
        }
        return EncounterMaster.getMinCreepWavePower() * mod / 100;
    }

    // TODO pass MIN_MAX !
    private static MacroGroup getRandomCreepGroup(Area area) {
        Loop.startLoop(10);
        while (!Loop.loopEnded()) {
            ENCOUNTER_SUBGROUP group = new RandomWizard<ENCOUNTER_SUBGROUP>()
                    .getObjectByWeight(
                            area.getProperty(MACRO_PROPS.ENCOUNTER_SUBGROUPS),
                            ENCOUNTER_SUBGROUP.class);// TODO
            List<ObjType> pool;
            if (group != null) {
                pool = DataManager.getTypesSubGroup(DC_TYPE.ENCOUNTERS,
                        group.toString());
            } else
            // TODO
            {
                pool = DataManager.getTypes(DC_TYPE.ENCOUNTERS);
            }

            FilterMaster.filterByParam(pool, PARAMS.POWER_MINIMUM,
                    EncounterMaster.getMaxCreepWavePower(),
                    DC_TYPE.ENCOUNTERS, false);
            FilterMaster.filterByParam(pool, PARAMS.POWER_MAXIMUM,
                    EncounterMaster.getMinCreepWavePower(),
                    DC_TYPE.ENCOUNTERS, true);
            // more filter! By TYPE? TODO
            if (pool.isEmpty()) {
                continue;
            }
            // higher probability for non-bosses?
            String waveName = new RandomWizard<ObjType>().getRandomListItem(
                    pool).getName();
            MacroGroup macroGroup = new MacroGroup(waveName, area);
            return macroGroup;
        }
        return null;
    }

}
