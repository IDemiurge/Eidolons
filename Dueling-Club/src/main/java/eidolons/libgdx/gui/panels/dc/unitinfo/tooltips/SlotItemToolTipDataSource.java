package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_HeroSlotItem;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.content.values.properties.G_PROPS;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.content.UNIT_INFO_PARAMS.WEAPON_DC_INFO_PARAMS;

public class SlotItemToolTipDataSource {
    DC_HeroSlotItem item;
    List<ValueContainer> list = new ArrayList<>();

    public SlotItemToolTipDataSource(DC_HeroSlotItem item) {
        this.item = item;
        if (item != null)
            for (int i = 0; i < WEAPON_DC_INFO_PARAMS.length; i++) {
                PARAMS p = WEAPON_DC_INFO_PARAMS[i];
                String value = String.valueOf(item.getIntParam(p));
                String name = p.getName();
                final ValueContainer tooltipContainer = new ValueContainer(name, value);
                tooltipContainer.pad(10);
                list.add(tooltipContainer);
            }
    }

    public DC_HeroSlotItem getItem() {
        return item;
    }

    public List<ValueContainer> getMainParams() {
        return list;
    }

    public List<ValueContainer> getBuffs() {
        return item.getBuffs().stream()
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

}
