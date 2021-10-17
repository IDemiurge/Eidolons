package eidolons.entity.active.spaces;

import eidolons.entity.active.DC_ActiveObj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class FeatSpaceHqHandler {
    /*operations?     */

    FeatSpaceInitializer initializer;
    Map<FeatSpace, FeatSpaceData> dataMap;
    FeatSpaces spaces;

    public void save() {

    }

    public void upgrade(DC_ActiveObj active) {
        updateGui();
    }

    private void updateGui() {
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI); //TODO
    }

    public boolean move(DC_ActiveObj active, FeatSpace space, int newIndex) {
        FeatSpaceData data = dataMap.get(space);
        String prev = data.getActive(newIndex);
        if (!StringMaster.isEmpty(prev)) {
            int   oldIndex= data.indexOf(active.getName());
            data.set(oldIndex, prev);
        }
        data.set(newIndex, active.getName());
        initializer.update(space, data);
        return true;
    }

    public boolean add(DC_ActiveObj active, FeatSpace space) {

        return false;
    }

    public void remove(DC_ActiveObj active, FeatSpace space) {

    }

}














