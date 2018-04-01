package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface EffectsAndAbilitiesSource {

    List<ValueContainer> getBuffs();

    List<ValueContainer> getAbilities();
}
