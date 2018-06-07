package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.system.text.TextWrapper;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class WeaponTooltip extends ValueTooltip {

    public WeaponTooltip() {
        setWidth(400);
    }

    public WeaponTooltip(DC_WeaponObj weapon) {
        this();
        setUserObject(new SlotItemToolTipDataSource(weapon));
    }

    @Override
    public void updateAct(float delta) {
        final SlotItemToolTipDataSource source = (SlotItemToolTipDataSource) getUserObject();

        ValueContainer container = new ValueContainer(source.getItem().getName());
        container.setStyle(StyleHolder.getHqLabelStyle(16));
        addElement(container);
        row();
        LabelStyle style = StyleHolder.getHqLabelStyle(15);
        String text = TextWrapper.processText(GdxMaster.getWidth() / 3, InventoryFactory.getTooltipsVals(source.getItem()), style);
        container = new ValueContainer(text);
        container.setStyle(style);
        addElement(container);
        row();

        addElement(initTableValues(source.getMainParams()));
        row();

        if (source.getBuffs().size() > 0) {
            TablePanel buffsTable = new TablePanel();

            source.getBuffs().forEach(el -> {
                el.overrideImageSize(32, 32);
                buffsTable.addElement(el);
            });
            addElement(buffsTable).padTop(5);
        }

    }

    private TablePanel initTableValues(List<ValueContainer> valueContainers) {
        TablePanel table = new TablePanel();
        final int size = valueContainers.size();
        int halfSize = size / 2;
        if (size % 2 != 0) {
            halfSize++;
        }

        for (int i = 0; i < halfSize; i++) {
            ValueContainer valueContainer = valueContainers.get(i);
            valueContainer.cropName();
            valueContainer.setNameAlignment(Align.left);
            valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            table.addElement(valueContainer);
            final int i1 = i + halfSize;
            if (i1 < valueContainers.size()) {
                valueContainer = valueContainers.get(i1);
                valueContainer.cropName();
                valueContainer.setNameAlignment(Align.left);
                valueContainer.setBorder(getOrCreateR("UI/components/infopanel/simple_value_border.png"));
                table.addElement(valueContainer);
            }

            table.row();
        }

        return table;
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
    }
}
