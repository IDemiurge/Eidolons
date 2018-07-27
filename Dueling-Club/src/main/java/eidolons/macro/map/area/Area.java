package eidolons.macro.map.area;

import eidolons.macro.MacroGame;
import eidolons.macro.entity.MacroObj;
import eidolons.macro.map.MacroCoordinates;
import main.content.values.properties.MACRO_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

public class Area extends MacroObj {
    private List<MacroGroup> groups = new ArrayList<>();
    private List<MacroCoordinates> boundaries;

    public Area(MacroGame game, ObjType type, Ref ref) {
        super(game, type, ref);
    }

    public Area(ObjType type) {
        super(type);
    }

    public List<String> getWanderingGroups() {
        return ContainerUtils
         .openContainer(getProperty(MACRO_PROPS.WANDERING_GROUPS));
    }

    public List<MacroGroup> getGroups() {
        return groups;
    }

    public void addGroup(MacroGroup group) {
        groups.add(group);
    }

    public List<MacroCoordinates> getBoundaries() {
        if (boundaries == null) {
            boundaries = new ArrayList<>();
            for (String boundary : ContainerUtils
             .openContainer(getProperty(MACRO_PROPS.AREA_BOUNDARIES))) {
                boundaries.add(new MacroCoordinates(boundary));
            }
        }
        return boundaries;
    }
}
