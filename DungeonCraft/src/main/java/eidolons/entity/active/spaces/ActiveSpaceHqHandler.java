package eidolons.entity.active.spaces;

import eidolons.entity.active.DC_ActiveObj;
import main.content.enums.entity.NewRpgEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class ActiveSpaceHqHandler {
    /*operations?     */

    ActiveSpaceInitializer initializer;
    Map<ActiveSpace, ActiveSpaceData> dataMap;
    UnitActiveSpaces spaces;

    public void save() {

    }

    public void upgrade(DC_ActiveObj active) {
        updateGui();
    }

    private void updateGui() {
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI); //TODO
    }

    public boolean move(DC_ActiveObj active, ActiveSpace space, int newIndex) {
        ActiveSpaceData data = dataMap.get(space);
        String prev = data.getActive(newIndex);
        if (!StringMaster.isEmpty(prev)) {
            int   oldIndex= data.indexOf(active.getName());
            data.set(oldIndex, prev);
        }
        data.set(newIndex, active.getName());
        initializer.update(space, data);
        return true;
    }

    public boolean add(DC_ActiveObj active, ActiveSpace space) {

        return false;
    }

    public void remove(DC_ActiveObj active, ActiveSpace space) {

    }

}














