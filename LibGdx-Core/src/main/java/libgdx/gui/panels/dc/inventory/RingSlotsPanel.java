package libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.content.consts.VisualEnums;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanel;
import libgdx.gui.panels.dc.inventory.datasource.EquipDataSource;
import libgdx.texture.TextureCache;

import java.util.List;

import static libgdx.texture.TextureCache.getOrCreateR;

public class RingSlotsPanel extends TablePanel {
    boolean left;

    public RingSlotsPanel(boolean left) {
        this.left = left;
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
//        row();
//        addElement(new ValueContainer(getOrCreateR(
//         CELL_TYPE.RING.getSlotImagePath())));
//        addElement(new ValueContainer(getOrCreateR(CELL_TYPE.RING.getSlotImagePath())));
    }

//    @Override
//    public void clear() {
//
//    }

    @Override
    public void afterUpdateAct(float delta) {
        if (getUserObject() == null)
            return;
        clear();
        super.afterUpdateAct(delta);
        final List<InvItemActor> rings =
         ((EquipDataSource) getUserObject()).rings();

        int a = 0;
        for (int i = 0; i < 8; i++) {
            if (i % 2 == (left ? 1 : 0)) {
                continue;
            }
            a++;
            Actor valueContainer = rings.get(i);
            if (valueContainer == null) {
                valueContainer = new ValueContainer(TextureCache.getOrCreateR(VisualEnums.CELL_TYPE.RING.getSlotImagePath()));
            }
            add(valueContainer).expand(0, 0);
            if ((a) % 2 == 0) {
                row();
            }
        }
    }
}