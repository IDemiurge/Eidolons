package libgdx.gui.panels.dc.unitinfo.datasource;

import libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface EffectsAndAbilitiesSource {

    List<ValueContainer> getBuffs(boolean body);

    List<ValueContainer> getAbilities(boolean body);

}
