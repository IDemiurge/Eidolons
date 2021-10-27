package libgdx.gui.dungeon.panels.dc.actionpanel;

import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;

public class BuffPanelSimple extends TablePanel {
    private final boolean body;

    public BuffPanelSimple(boolean body) {
        this.body = body;
        left().bottom();
    }

    @Override
    public void updateAct(float delta) {
        clear();

        final EffectsAndAbilitiesSource source =
         (EffectsAndAbilitiesSource) getUserObject();

        source.getAbilities(body).forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });
        source.getBuffs(body).forEach(el -> {
            el.overrideImageSize(32, 32);
            add(el).left().bottom();
        });
    }

}
