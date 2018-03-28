package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.UiMaster;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.datasource.AttributesDataSource;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class MainParamPanel extends TablePanel {

    List<ValueContainer> containers = new LinkedList<>();

    String[] attributes = new String[]{
     "strength", "vitality",
     "agility", "dexterity",
     "spellpower", "willpower",
     "intelligence", "knowledge",
     "charisma", "wisdom"
    };

    public MainParamPanel() {
        TextureRegion textureRegion = getOrCreateR("/UI/components/infopanel/main_param_panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        pad(20, 10, 20, 10);
        int i = 0;
        for (String sub : attributes) {
            addAttributeContainer(sub);
            i++;
            if (i > 1) {
                i = 0;
                row();
            }
        }

    }

    private void addAttributeContainer(String sub) {
        ValueContainer container = new ValueContainer(
         getOrCreateR("UI/value icons/attributes/" + sub +
          ".png"), StringMaster.getWellFormattedString(sub), "");
        container.overrideImageSize(
         UiMaster.getSmallIconSize(), UiMaster.getSmallIconSize());
        containers.add(container);
        addElement(container);
    }

    @Override
    public void updateAct(float delta) {
        AttributesDataSource source = (AttributesDataSource) getUserObject();
        containers.forEach(c -> c.updateValue(source.getAttribute(c.getName())));

    }
}
