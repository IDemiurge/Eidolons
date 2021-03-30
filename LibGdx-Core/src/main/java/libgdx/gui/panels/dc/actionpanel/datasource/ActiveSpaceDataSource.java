package libgdx.gui.panels.dc.actionpanel.datasource;

import eidolons.entity.active.spaces.ActiveSpace;
import libgdx.gui.generic.ValueContainer;

import java.util.List;
import java.util.Map;

public interface ActiveSpaceDataSource {
    List<ValueContainer> getActives();

    Map<ActiveSpace.ActiveSpaceMeta, List<ValueContainer>> getActiveSpacesExpanded();
}
