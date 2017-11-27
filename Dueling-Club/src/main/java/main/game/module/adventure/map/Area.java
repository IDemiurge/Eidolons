package main.game.module.adventure.map;

import main.content.values.properties.MACRO_PROPS;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroObj;
import main.game.module.adventure.travel.MacroCoordinates;
import main.game.module.adventure.travel.MacroGroup;
import main.system.auxiliary.StringMaster;

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
        return StringMaster
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
            for (String boundary : StringMaster
                    .openContainer(getProperty(MACRO_PROPS.AREA_BOUNDARIES))) {
                boundaries.add(new MacroCoordinates(boundary));
            }
        }
        return boundaries;
    }
}
