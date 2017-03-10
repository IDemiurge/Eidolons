package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import main.libgdx.texture.TextureCache;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class ResourcePanel extends TablePanel {

    private final ValueContainer toughness;
    private final ValueContainer endurance;
    private final ValueContainer stamina;

    private final ValueContainer morale;
    private final ValueContainer essence;
    private final ValueContainer focus;

    public ResourcePanel() {
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/main_resource_panel.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());

        toughness = new VerticalValueContainer(getOrCreateR("UI/value icons/toughness.png"), "Toughness", "");
        addElement(toughness.fill().left().bottom().pad(0, 10, 10, 25));

        morale = new VerticalValueContainer(getOrCreateR("UI/value icons/morale.png"), "Morale", "");
        addElement(morale.fill().left().bottom().pad(0, 10, 10, 25));

        addCol();

        endurance = new VerticalValueContainer(getOrCreateR("UI/value icons/endurance.png"), "Endurance", "");
        addElement(endurance.fill().left().bottom().pad(0, 10, 10, 25));

        essence = new VerticalValueContainer(getOrCreateR("UI/value icons/essence.png"), "Essence", "");
        addElement(essence.fill().left().bottom().pad(0, 10, 10, 25));

        addCol();

        stamina = new VerticalValueContainer(getOrCreateR("UI/value icons/stamina.png"), "Stamina", "");
        addElement(stamina.fill().left().bottom().pad(0, 10, 10, 25));

        focus = new VerticalValueContainer(getOrCreateR("UI/value icons/focus.png"), "Focus", "");
        addElement(focus.fill().left().bottom().pad(0, 10, 10, 25));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePanel) {

            ResourceSource source = (ResourceSource) getUserObject();

            toughness.updateValue(source.getToughness());
            endurance.updateValue(source.getEndurance());
            stamina.updateValue(source.getStamina());

            morale.updateValue(source.getMorale());
            essence.updateValue(source.getEssence());
            focus.updateValue(source.getFocus());

            updatePanel = false;
        }
    }
}
