package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.EffectsAndAbilitiesSource;
import main.libgdx.texture.TextureCache;

import java.util.List;
import java.util.stream.Collectors;

public class EffectAndAbilitiesPanel extends TablePanel {

    public EffectAndAbilitiesPanel() {
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/effects_and_abilities_panel.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);
    }

    @Override
    public void updateAct(float delta) {
        clear();

        EffectsAndAbilitiesSource source = (EffectsAndAbilitiesSource) getUserObject();

        List<ValueContainer> effects = source.getEffects().stream()
                .map(textureStringPair -> {
                    final ValueContainer valueContainer = new ValueContainer(textureStringPair.getLeft());
                    return valueContainer;
                }).collect(Collectors.toList());

        final int h = 3;
        final int w = 4;
        IconGrid effectsGrid = new IconGrid(effects, w, h);
        addElement(effectsGrid).size(32 * w, 32 * h);

        List<ValueContainer> abils = source.getAbilities().stream()
                .map(textureStringPair -> {
                    final ValueContainer valueContainer = new ValueContainer(textureStringPair.getLeft());
                    return valueContainer;
                }).collect(Collectors.toList());

        IconGrid abilsGrid = new IconGrid(abils, w, h);
        addElement(abilsGrid).size(32 * w, 32 * h);
    }
}
