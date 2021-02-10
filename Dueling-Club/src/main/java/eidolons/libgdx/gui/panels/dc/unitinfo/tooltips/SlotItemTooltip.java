package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryFactory;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.entity.CounterMaster;
import main.system.graphics.FontMaster;
import main.system.text.TextWrapper;

import java.util.List;

public class SlotItemTooltip extends ValueTooltip {

    public SlotItemTooltip() {
    }

    @Override
    public SlotItemToolTipDataSource getUserObject() {
        Object obj = super.getUserObject();
        if (obj instanceof DC_HeroSlotItem) {
            return  new SlotItemToolTipDataSource((DC_HeroSlotItem) obj);
        }
        return (SlotItemToolTipDataSource) obj;
    }


    @Override
    public void updateAct(float delta) {
        clearChildren();
        final SlotItemToolTipDataSource source =  getUserObject();

        ValueContainer container = new ValueContainer(source.getItem().getName());
        container.setStyle(StyleHolder.getHqLabelStyle(13));
        addElement(container);
        row();
        LabelStyle style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 17);
        float w = getWidth();
        if (w<=200) {
            w = GdxMaster.getWidth() / 3;
        }
        String text = TextWrapper.processText(
                (int) (w*1.1f),
         InventoryFactory.getTooltipsVals(source.getItem()), style);
        container = new ValueContainer(text);
        container.setStyle(style);
        addElement(container).left();
        row();

        addElement(initTableValues(source, source.getMainParams())).left();
        row();

        if (source.getBuffs().size() > 0) {
            TablePanel buffsTable = new TablePanel();

            source.getBuffs().forEach(el -> {
                el.overrideImageSize(32, 32);
                buffsTable.addElement(el);
            });
            addElement(buffsTable).padTop(5).left();
        }

    }

    private TablePanel initTableValues(SlotItemToolTipDataSource source, List<ValueContainer> valueContainers) {
        TablePanel table = new TablePanel();
        if (source!=null )
        if (source.item!=null )
        if (source.item.getCustomParamMap() != null) {
            source.item.getCustomParamMap().keys().forEach(counter -> {
                final String name = StringMaster.format(counter);
                String img = CounterMaster.getImagePath(counter);
                if (img != null) {

                    TextureRegion texture = TextureCache.getOrCreateR(
                            img);
                    String val = source.item.getCustomParamMap().get(counter);
                    final ValueContainer valueContainer = (texture == null)
                            ? new ValueContainer(name, val)
                            : new ValueContainer(texture, name, source.item.getCustomParamMap().get(counter));
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    valueContainer.setStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 19));
                    table.add(valueContainer).row();
                }
            });
        }
        final int size = valueContainers.size();
        int halfSize = size / 2;
        if (size % 2 != 0) {
            halfSize++;
        }

        for (int i = 0; i < halfSize; i++) {
            ValueContainer valueContainer = valueContainers.get(i);
            valueContainer.cropName();
            valueContainer.setNameAlignment(Align.left);
            valueContainer.background(NinePatchFactory.
             getLightPanelFilledSmallDrawable());
            table.addElement(valueContainer);
            final int i1 = i + halfSize;


            if (i1 < valueContainers.size()) {
                valueContainer = valueContainers.get(i1);
                valueContainer.cropName();
                valueContainer.setNameAlignment(Align.left);
                valueContainer.background(NinePatchFactory.
                 getLightPanelFilledSmallDrawable());
//                valueContainer.setBorder(getOrCreateR(
//                 "ui/components/infopanel/simple_value_border.png"));
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
