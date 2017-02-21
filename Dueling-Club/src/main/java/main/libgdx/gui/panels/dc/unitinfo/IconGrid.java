package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureManager;

import java.util.Iterator;
import java.util.List;

public class IconGrid extends TablePanel {
    private static String cellBackImage = "/UI/empty32.jpg";

    public IconGrid(List<TextureRegion> images, int w, int h) {
        rowDirection = TOP_RIGHT;

        if (rowDirection != TOP_DOWN || rowDirection != DOWN_TOP) {
            int b = w;
            w = h;
            h = b;
        }

        TextureRegion emptyCell = new TextureRegion(TextureManager.getOrCreate(cellBackImage));

        Iterator<TextureRegion> iter = images.iterator();
        for (int y = 0; y < w; y++) {
            addCol();
            for (int x = 0; x < h; x++) {
                TextureRegion region;
                if (iter.hasNext()) {
                    region = iter.next();
                } else {
                    region = emptyCell;
                }
                addElement(new Container(new Image(region)).fill().left().bottom());
            }
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
