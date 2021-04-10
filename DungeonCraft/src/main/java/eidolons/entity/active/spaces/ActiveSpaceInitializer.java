package eidolons.entity.active.spaces;

import eidolons.content.PROPS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.NewRpgEnums;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActiveSpaceInitializer implements IActiveSpaceInitializer {
    public static final PROPS[] activeSpaceProps = {
            PROPS.MEMORIZED_SPACES,
            PROPS.VERBATIM_SPACES,
            PROPS.ACTIVE_SPACES,
    };
    public static final int MAX_SLOTS = 6;

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
        Map<Integer, DC_ActiveObj> actives = createActives(unit, data);
        ActiveSpace space = new ActiveSpace(i, data.getName(), unit, type, data.getMode(), actives);
        return space;
    }

    private Map<Integer, DC_ActiveObj> createActives(Unit unit, ActiveSpaceData value) {
        Map<Integer, DC_ActiveObj> actions = new LinkedHashMap<>();
        for (int i = 0; i < MAX_SLOTS; i++) {
            String name = value.getActive(i);
            if (StringMaster.isEmpty(name)) {
                continue;
            }
            DC_ActiveObj action = unit.getGame().getActionManager().getOrCreateActionOrSpell(name, unit);
            actions.put(i, action);
        }
        return actions;
    }

    public void update(ActiveSpace space, ActiveSpaceData data) {
        space.setActivesMap(createActives(space.getOwner(), data));
    }

    public ActiveSpace.ActiveSpaceMeta createMeta(ActiveSpace space) {
        ActiveSpace.ActiveSpaceMeta meta = new ActiveSpace.ActiveSpaceMeta(space.getName(), NewRpgEnums.ACTIVE_SPACE_SKIN.lite, false, false);
        return meta;
    }
}
