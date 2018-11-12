package eidolons.libgdx.gui.panels.dc.unitinfo.old;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;

import java.util.Iterator;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class StatsPanel extends TablePanel {
    public StatsPanel() {
    }

    @Override
    public void updateAct(float delta) {
        clear();

        List<List<ValueContainer>> valueContainers = (List<List<ValueContainer>>) getUserObject();
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
                        next.setBorder(getOrCreateR("ui/components/ninepatch/std/background_3px_border.png"), false);
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
