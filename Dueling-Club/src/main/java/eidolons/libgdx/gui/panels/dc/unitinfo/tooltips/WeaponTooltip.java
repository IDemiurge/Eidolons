package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.tooltips.ValueTooltip;

import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class WeaponTooltip extends ValueTooltip {

    public WeaponTooltip() {
        setWidth(400);
    }

    public WeaponTooltip(DC_WeaponObj weapon) {
        this();
        setUserObject(new WeaponToolTipDataSource(weapon));
    }
    @Override
    public void updateAct(float delta) {
        final WeaponToolTipDataSource source = (WeaponToolTipDataSource) getUserObject();
        String durability= "Durability:" +
         source.getWeapon().getIntParam(PARAMS.C_DURABILITY)+
         "/" +
         source.getWeapon().getIntParam(PARAMS.DURABILITY)  ;
        ValueContainer container = new ValueContainer(source.getWeapon().getName(),
         durability);
        container.setStyle(StyleHolder.getHqLabelStyle(16));
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
        setBackground(new NinePatchDrawable(NinePatchFactory.getTooltip()));
    }
}
