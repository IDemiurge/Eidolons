package eidolons.entity.active.spaces;

import eidolons.entity.active.ActiveObj;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.Map;

public class FeatSpaceHqHandler {
    /* For the Feat Space TAB where we put 'em together  */

    FeatSpaceInitializer initializer;
    Map<FeatSpace, FeatSpaceData> dataMap;
    FeatSpaces spaces;

    public void save() {

    }

    public void upgrade(ActiveObj active) {
        updateGui();
    }

    private void updateGui() {
        GuiEventManager.trigger(GuiEventType.UPDATE_GUI); //TODO
    }

    public boolean move(ActiveObj active, FeatSpace space, int newIndex) {
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

    public boolean add(ActiveObj active, FeatSpace space) {

        return false;
    }

    public void remove(ActiveObj active, FeatSpace space) {

    }

}














