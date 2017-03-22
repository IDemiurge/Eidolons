package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;

import java.util.Iterator;
import java.util.List;

public class IconGrid extends TablePanel {
    private static String cellBackImage = "/UI/empty32.jpg";

    public IconGrid(List<ValueContainer> images, int w, int h) {
        rowDirection = TOP_RIGHT;

        if (rowDirection != TOP_DOWN || rowDirection != DOWN_TOP) {
            int b = w;
            w = h;
            h = b;
        }

        TextureRegion emptyCell = new TextureRegion(TextureCache.getOrCreate(cellBackImage));

        Iterator<ValueContainer> iter = images.iterator();
        for (int y = 0; y < w; y++) {
            addCol();
            for (int x = 0; x < h; x++) {
                if (iter.hasNext()) {
                    addElement(new Container<>(iter.next()).left().bottom().width(32).height(32));
                } else {
                    addElement(new Container<>(new Image(emptyCell)).left().bottom().width(32).height(32));
                }
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
