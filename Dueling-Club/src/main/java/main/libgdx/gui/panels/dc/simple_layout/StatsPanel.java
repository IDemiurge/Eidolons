package main.libgdx.gui.panels.dc.simple_layout;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static main.libgdx.texture.TextureCache.getOrCreateR;

public class StatsPanel extends TablePanel {
    public StatsPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();

        List<List<ValueContainer>> valueContainers = ((Supplier<List<List<ValueContainer>>>) getUserObject()).get();
        for (int i = 0; i < valueContainers.size(); i++) {
            List<ValueContainer> valueContainerList = valueContainers.get(i);
            Iterator<ValueContainer> iter = valueContainerList.iterator();

            int rows = valueContainerList.size() / 2;
            if (valueContainerList.size() % 2 != 0) {
                rows++;
            }

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < 2; x++) {
                    if (iter.hasNext()) {
                        ValueContainer next = iter.next();
                        next.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"), false);
                        next.cropName();
                        next.setNameAlignment(Align.left);
                        next.setValueAlignment(Align.right);
                        next.pad(0, 5, 0, 5);
                        addElement(next);
                    }
                }
                row();
            }

            addElement(null).pad(0, 0, 10, 0);
            row();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
