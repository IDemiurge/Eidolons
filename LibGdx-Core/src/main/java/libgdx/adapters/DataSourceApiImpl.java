package libgdx.adapters;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.system.libgdx.api.DataSourceApi;
import libgdx.gui.panels.dc.inventory.container.ContainerDataSource;
import libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DataSourceApiImpl implements DataSourceApi {

    @Override
    public void showLootPanel(DC_Obj obj, Unit unit) {
        Pair<InventoryDataSource, ContainerDataSource> param =
                new ImmutablePair<>(new InventoryDataSource(unit), new ContainerDataSource(obj, unit));
        GuiEventManager.trigger(GuiEventType.SHOW_LOOT_PANEL, param);
    }
}
