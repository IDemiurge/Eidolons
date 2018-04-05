package eidolons.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.gui.panels.dc.TablePanel;
import eidolons.libgdx.gui.panels.dc.ValueContainer;
import eidolons.libgdx.gui.panels.dc.VerticalValueContainer;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSource;
import eidolons.libgdx.texture.TextureCache;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class ResourcePanel extends TablePanel {

    private final ValueContainer toughness;
    private final ValueContainer endurance;
    private final ValueContainer stamina;

    private final ValueContainer morale;
    private final ValueContainer essence;
    private final ValueContainer focus;

    public ResourcePanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/main_resource_panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        pad(16, 20, 16, 20);

        toughness = new VerticalValueContainer(getOrCreateR("UI/value icons/toughness.jpg"), "Toughness", "");
        addElement(toughness).grow();

        endurance = new VerticalValueContainer(getOrCreateR("UI/value icons/endurance.png"), "Endurance", "");
        addElement(endurance).grow();

        stamina = new VerticalValueContainer(getOrCreateR("UI/value icons/stamina.png"), "Stamina", "");
        addElement(stamina).grow();

        row().padTop(18);

        morale = new VerticalValueContainer(getOrCreateR("UI/value icons/morale.png"), "Morale", "");
        addElement(morale).grow();

        essence = new VerticalValueContainer(getOrCreateR("UI/value icons/essence.png"), "Essence", "");
        addElement(essence).grow();

        focus = new VerticalValueContainer(getOrCreateR("UI/value icons/focus.png"), "Focus", "");
        addElement(focus).grow();
    }

    @Override
    public void updateAct(float delta) {
        ResourceSource source = (ResourceSource) getUserObject();

        toughness.updateValue(source.getToughness());
        endurance.updateValue(source.getEndurance());
        stamina.updateValue(source.getStamina());

        morale.updateValue(source.getMorale());
        essence.updateValue(source.getEssence());
        focus.updateValue(source.getFocus());
    }
}
