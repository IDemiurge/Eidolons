package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface StatsDataSource {
      List<List<ValueContainer>> getGeneralStats();

      List<List<ValueContainer>> getCombatStats();

      List<List<ValueContainer>> getMagicStats();

      List<List<ValueContainer>> getMiscStats();
}
