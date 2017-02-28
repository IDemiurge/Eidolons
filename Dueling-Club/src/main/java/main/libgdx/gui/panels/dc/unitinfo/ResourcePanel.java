package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.texture.TextureCache;

import java.util.Iterator;
import java.util.List;

public class ResourcePanel extends TablePanel {

    public ResourcePanel(List<VerticalValueContainer> values) {
        TextureRegion textureRegion = new TextureRegion(TextureCache.getOrCreate("/UI/components/infopanel/main_resource_panel.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());

        Iterator<VerticalValueContainer> iter = values.iterator();
        for (int i = 0; i < 3; i++) {
            addCol();
            for (int j = 0; j < 2; j++) {
                if (iter.hasNext()) {
                    addElement(iter.next().fill().left().bottom().pad(0, 10, 10, 25));
                }
            }
        }
    }
}
