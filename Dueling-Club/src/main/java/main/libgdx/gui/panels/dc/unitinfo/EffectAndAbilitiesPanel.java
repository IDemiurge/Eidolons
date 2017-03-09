package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;

import java.util.List;
import java.util.stream.Collectors;

public class EffectAndAbilitiesPanel extends TablePanel {

    public EffectAndAbilitiesPanel() {
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/effects_and_abilities_panel.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            clear();

            EffectsAndAbilitiesSource source = (EffectsAndAbilitiesSource) getUserObject();

            List<ValueContainer> effects = source.getEffects().stream()
                    .map(textureStringPair -> new ValueContainer(textureStringPair.getLeft())) //skip text value for now, its for tooltip description
                    .collect(Collectors.toList());

            IconGrid effectsGrid = new IconGrid(effects, 4, 3);
            addElement(effectsGrid.fill().left().bottom().pad(0, 30, 20, 0));

            addCol();

            List<ValueContainer> abils = source.getAbilities().stream()
                    .map(textureStringPair -> new ValueContainer(textureStringPair.getLeft())) //skip text value for now, its for tooltip description
                    .collect(Collectors.toList());

            IconGrid abilsGrid = new IconGrid(abils, 4, 3);
            addElement(abilsGrid.fill().left().bottom().pad(0, 50, 20, 0));

            updatePanel = false;
        }
    }
}
