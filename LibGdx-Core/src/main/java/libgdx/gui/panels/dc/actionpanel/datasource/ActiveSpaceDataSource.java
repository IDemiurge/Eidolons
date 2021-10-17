package libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.entity.active.spaces.FeatSpace;
import libgdx.gui.generic.ValueContainer;

import java.util.List;
import java.util.Map;

public interface ActiveSpaceDataSource {
    List<ValueContainer> getActives();

    Map<FeatSpace.ActiveSpaceMeta, List<ValueContainer>> getActiveSpacesExpanded();
}
