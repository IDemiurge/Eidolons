package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureManager;

import java.util.List;

public class EffectAndAbilitiesPanel extends TablePanel {

    public EffectAndAbilitiesPanel(List<TextureRegion> abilities, List<TextureRegion> effects) {
        TextureRegion textureRegion = new TextureRegion(TextureManager.getOrCreate("/UI/components/infopanel/effects_and_abilities_panel.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());

        IconGrid effectsGrid = new IconGrid(effects, 4, 3);
        addElement(new Container(effectsGrid).fill().left().bottom().pad(0, 30, 20, 0));

        addCol();

        IconGrid abilsGrid = new IconGrid(abilities, 4, 3);
        addElement(new Container(abilsGrid).fill().left().bottom().pad(0, 50, 20, 0));
    }
}
