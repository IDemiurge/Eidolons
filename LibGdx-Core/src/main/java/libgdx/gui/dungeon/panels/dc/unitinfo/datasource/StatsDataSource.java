package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

import libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface StatsDataSource {
    List<List<ValueContainer>> getGeneralStats();

    List<List<ValueContainer>> getCombatStats();

    List<List<ValueContainer>> getMagicStats();

    List<List<ValueContainer>> getMiscStats();
}
