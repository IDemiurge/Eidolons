package eidolons.libgdx.gui.panels.dc.actionpanel;

import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;

public class BuffPanelSimple extends TablePanel {
    public BuffPanelSimple() {
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final EffectsAndAbilitiesSource source =
         (EffectsAndAbilitiesSource) getUserObject();

        source.getBuffs().forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });

        source.getAbilities().forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });
    }
}
