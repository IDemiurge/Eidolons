package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface EffectsAndAbilitiesSource {

    List<ValueContainer> getBuffs();

    List<ValueContainer> getAbilities();
}
