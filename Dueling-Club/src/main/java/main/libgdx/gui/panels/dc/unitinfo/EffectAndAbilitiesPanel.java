package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;
import main.libgdx.texture.TextureCache;

import java.util.List;

public class EffectAndAbilitiesPanel extends TablePanel {

    public EffectAndAbilitiesPanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/effects_and_abilities_panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        EffectsAndAbilitiesSource source = (EffectsAndAbilitiesSource) getUserObject();

        final int h = 3;
        final int w = 4;

        List<ValueContainer> effects = source.getBuffs();
        IconGrid effectsGrid = new IconGrid(effects, w, h);
        addElement(effectsGrid).size(32 * w, 32 * h);

        List<ValueContainer> abils = source.getAbilities();
        IconGrid abilsGrid = new IconGrid(abils, w, h);
        addElement(abilsGrid).size(32 * w, 32 * h);
    }
}
