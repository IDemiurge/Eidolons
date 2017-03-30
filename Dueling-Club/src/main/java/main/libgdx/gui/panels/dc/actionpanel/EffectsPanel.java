package main.libgdx.gui.panels.dc.actionpanel;

import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;

public class EffectsPanel extends TablePanel {
    public EffectsPanel() {
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final EffectsAndAbilitiesSource source = (EffectsAndAbilitiesSource) getUserObject();

        source.getEffects().forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });
        row();

        source.getAbilities().forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });
    }
}
