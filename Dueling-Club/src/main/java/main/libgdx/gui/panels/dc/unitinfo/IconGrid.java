package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;

import java.util.Iterator;
import java.util.List;

public class IconGrid extends TablePanel {
    private static String cellBackImage = "/UI/empty32.jpg";

    public IconGrid(List<ValueContainer> images, int w, int h) {

        TextureRegion emptyCell = new TextureRegion(TextureCache.getOrCreate(cellBackImage));

        Iterator<ValueContainer> iter = images.iterator();
        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                if (iter.hasNext()) {
                    addElement(iter.next());
                } else {
                    addElement(new Image(emptyCell));
                }
            }
            row();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
