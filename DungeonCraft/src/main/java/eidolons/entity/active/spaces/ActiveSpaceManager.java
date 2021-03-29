package eidolons.entity.active.spaces;

import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.NewRpgEnums;
import main.system.auxiliary.ContainerUtils;

import java.util.ArrayList;
import java.util.List;

public class ActiveSpaceManager implements IActiveSpaceManager {

    public static final PROPS[] activeSpaceProps = {
            PROPS.MEMORIZED_SPACES,
            PROPS.VERBATIM_SPACES,
            PROPS.ACTIVE_SPACES,
    };

    @Override
    public UnitActiveSpaces createActiveSpaces(Unit unit) {
        UnitActiveSpaces spaces = new UnitActiveSpaces(unit);
        int index = 0;
        //TODO what about standard ones - memorized/verbatim?
        for (PROPS prop : activeSpaceProps)
            for (String string : ContainerUtils.openContainer(
                    unit.getProperty(prop), ActiveSpaceData.getInstanceSeparator())) {
                ActiveSpaceData data = new ActiveSpaceData(string);
                ActiveSpace space = createSpace(index, unit, spaces, data);
                spaces.add(space);
            }

        return spaces;
    }

    private ActiveSpace createSpace(int i, Unit unit, UnitActiveSpaces spaces, ActiveSpaceData data) {
        NewRpgEnums.ACTIVE_SPACE_TYPE type = data.getType();
        // mods = getGlobalMods(unit, type); //TODO
        List<DC_ActiveObj> actives = createActives(unit, data.getActives());
        ActiveSpace space = new ActiveSpace(i, data.getName(), type, data.getMode(), actives);
        return space;

    }

    private List<DC_ActiveObj> createActives(Unit unit, String value) {
        List<DC_ActiveObj> actions = new ArrayList<>();
        for (String name : ContainerUtils.openContainer(value)) {
            DC_UnitAction action = unit.getGame().getActionManager().getOrCreateAction(name, unit);
            actions.add(action);
        }
        return actions;
    }


    public ActiveSpace.ActiveSpaceMeta createMeta(ActiveSpace  space) {
        ActiveSpace.ActiveSpaceMeta meta= new ActiveSpace.ActiveSpaceMeta(space.getName(), NewRpgEnums.ACTIVE_SPACE_SKIN.lite, false, false);
        return meta;
    }
}
