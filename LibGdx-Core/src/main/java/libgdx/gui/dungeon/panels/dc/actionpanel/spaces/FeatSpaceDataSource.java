package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import eidolons.entity.feat.spaces.FeatSpace;
import main.content.enums.entity.NewRpgEnums;

public class FeatSpaceDataSource {

    FeatSpace space;

    public NewRpgEnums.FeatSpaceType getType() {
        return space.getType();
    }

    public NewRpgEnums.FeatSpaceMode getMode() {
        return space.getMode();
    }

    public boolean isLocked() {
        return space.isLocked();
    }

    public int getIndex() {
        return space.getIndex();
    }

    public String getName() {
        return space.getName();
    }

    public boolean isActive() {
        return space.isActive();
    }
}
