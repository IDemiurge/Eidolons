package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import eidolons.entity.active.spaces.FeatSpace;
import main.content.enums.entity.NewRpgEnums;

public class FeatSpaceDataSource {

    FeatSpace space;

    public NewRpgEnums.FEAT_SPACE_TYPE getType() {
        return space.getType();
    }

    public NewRpgEnums.FEAT_SPACE_MODE getMode() {
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
