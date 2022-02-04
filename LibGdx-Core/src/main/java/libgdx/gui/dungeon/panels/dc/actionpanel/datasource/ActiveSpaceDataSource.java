package libgdx.gui.dungeon.panels.dc.actionpanel.datasource;

import eidolons.entity.feat.spaces.FeatSpace;
import libgdx.gui.generic.ValueContainer;

import java.util.List;
import java.util.Map;

public interface ActiveSpaceDataSource {
    List<ValueContainer> getActives();

    Map<FeatSpace.ActiveSpaceMeta, List<ValueContainer>> getActiveSpacesExpanded();
}
