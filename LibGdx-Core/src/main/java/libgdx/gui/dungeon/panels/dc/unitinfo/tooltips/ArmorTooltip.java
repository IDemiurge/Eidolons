package libgdx.gui.dungeon.panels.dc.unitinfo.tooltips;

import eidolons.entity.item.ArmorItem;
import eidolons.entity.item.HeroSlotItem;
import libgdx.gui.generic.ValueContainer;
import main.content.values.properties.G_PROPS;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.content.values.UNIT_INFO_PARAMS.ARMOR_TOOLTIP;

public class ArmorTooltip extends SlotItemTooltip {

    public ArmorTooltip(ArmorItem armor) {
        setUserObject(new SlotItemToolTipDataSource(null) {
            @Override
            public HeroSlotItem getItem() {
                return armor;
            }

            @Override
            public List<ValueContainer> getMainParams() {
                return Arrays.stream(ARMOR_TOOLTIP)
                        .map(el -> new ValueContainer(el.getName(), armor.getStrParam(el)).pad(10))
                        .collect(Collectors.toList());
            }

            @Override
            public List<ValueContainer> getBuffs() {
                return armor.getBuffs().stream()
                        .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
                        .map(AttackTooltipFactory.getObjValueContainerMapper())
                        .collect(Collectors.toList());
            }
        });
    }
}
