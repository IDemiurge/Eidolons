package macro.map.area;

import eidolons.content.PROPS;
import macro.map.MacroCoordinates;
import macro.map.Region;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;

import java.util.List;

public class MacroGroup { // macro obj type?
    private String groupName;
    private ObjType encounterType;

    private Boolean northOrSouth;
    private Boolean westOrEast;
    private MacroCoordinates coordinates;

    private float travelSpeed;
    private boolean areaBound;

    private Region region;
    private Area area;
    private boolean ambushing;

    public MacroGroup(String name, Area area) {
        this.groupName = name;
        encounterType = DataManager.getType(name, DC_TYPE.ENCOUNTERS);
        this.area = area;
        this.region = area.getRegion();
        initParams();
        // should the object be initialized? In case some dynamic data is
        // needed, e.g. if half the group is dead...
        // create from Encounter?
    }

    private void initParams() {
        initTravelSpeed();

    }

    @Override
    public String toString() {
        return groupName
         // + " encounter"
         ; // ++ ambushing?: ; coordinates
    }

    private void initTravelSpeed() {
        travelSpeed = 0;
        List<String> types = ContainerUtils.openContainer(encounterType
         .getProperty(PROPS.PRESET_GROUP));
        int size = types.size();
        for (String s : types) { // PROPS.UNIT_TYPES - all possible types?
            ObjType type = DataManager.getType(s, C_OBJ_TYPE.UNITS_CHARS);
            if (type != null) {
//                travelSpeed += TravelMasterOld.getTravelSpeed(type);
            } else {
                size--;
            }
            // TODO average // min?
        }
        if (!types.isEmpty()) {
            travelSpeed /= size;
        }

    }

    public void wander() {
        if (RandomWizard.random() || northOrSouth == null) {
            if (northOrSouth == null) {
                northOrSouth = RandomWizard.random();
            } else {
                northOrSouth = null;
            }
        }
        if (RandomWizard.random() || westOrEast == null) {
            if (westOrEast == null) {
                westOrEast = RandomWizard.random();
            } else {
                westOrEast = null;
            }
        }

        // useful for visual scouting too - can actually update enemies on the map
        // rather realistically... although in some cases it might look weird on
        // mountains etc ;)
        boolean diagonal = northOrSouth != null && westOrEast != null;
        int y_offset = 0;
        int x_offset = 0;
        if (diagonal) {
            y_offset = (int) Math.round(travelSpeed / region.getMilePerPixel()
             * Math.sqrt(2) / 2);
            x_offset = y_offset;
        } else if (westOrEast != null) {
            x_offset = Math.round(travelSpeed / region.getMilePerPixel());
            if (westOrEast) {
                x_offset = -x_offset;
            }
        } else if (northOrSouth != null) {
            y_offset = Math.round(travelSpeed / region.getMilePerPixel());
            if (northOrSouth) {
                y_offset = -y_offset;
            }
        }
        // preCheck out of bounds
        if (coordinates == null) {
            setCoordinates(AreaManager.getRandomCoordinateWithinArea(getArea()));
        }
        int x = coordinates.x;
        int y = coordinates.y;

        setCoordinates(new MacroCoordinates(x + x_offset, y + y_offset));
    }

    public boolean checkSetAmbush() {
        // TODO getCunning()
        return false;
    }

    public boolean isAmbushing() {
        return ambushing;
    }

    public void setAmbushing(boolean ambushing) {
        this.ambushing = ambushing;
    }

    public Boolean getNorthOrSouth() {
        return northOrSouth;
    }

    public void setNorthOrSouth(Boolean northOrSouth) {
        this.northOrSouth = northOrSouth;
    }

    public Boolean getWestOrEast() {
        return westOrEast;
    }

    public void setWestOrEast(Boolean westOrEast) {
        this.westOrEast = westOrEast;
    }

    public MacroCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(MacroCoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isAreaBound() {
        return areaBound;
    }

    public void setAreaBound(boolean areaBound) {
        this.areaBound = areaBound;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getGroupName() {
        return groupName;
    }

    public ObjType getEncounterType() {
        return encounterType;
    }

    public float getTravelSpeed() {
        return travelSpeed;
    }

    public enum Macro_group_params {
        AGGRESSION, RESTLESSNESS, CUNNING, // AMBUSHES
        STEALTH,
        DETECTION,

    }

}
