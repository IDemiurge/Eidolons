package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureManager;

import java.util.Iterator;
import java.util.List;

public class MainParamPanel extends TablePanel {
    public MainParamPanel(List<ValueContainer> values) {
        TextureRegion textureRegion = TextureManager.getOrCreateR("/UI/components/infopanel/main_param_panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());

        Iterator<ValueContainer> iter = values.iterator();
        for (int i = 0; i < 2; i++) {
            addCol();
            for (int j = 0; j < 5; j++) {
                if (iter.hasNext()) {
                    addElement(iter.next().fill().left().bottom().pad(0, 10, 10, 25));
                }
            }
        }
    }
}
