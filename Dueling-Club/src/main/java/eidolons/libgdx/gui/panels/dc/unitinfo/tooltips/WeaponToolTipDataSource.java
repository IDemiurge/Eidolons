package eidolons.libgdx.gui.panels.dc.unitinfo.tooltips;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.content.values.properties.G_PROPS;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.content.UNIT_INFO_PARAMS.WEAPON_DC_INFO_PARAMS;

public   class WeaponToolTipDataSource {
    DC_WeaponObj weapon;
    List<ValueContainer> list = new ArrayList<>();

    public WeaponToolTipDataSource(DC_WeaponObj weapon) {
        this.weapon = weapon;
if (weapon!=null )
        for (int i = 0; i < WEAPON_DC_INFO_PARAMS.length; i++) {
            PARAMS p = WEAPON_DC_INFO_PARAMS[i];
            String value = String.valueOf(weapon.getIntParam(p));
            String name = p.getName();
            final ValueContainer tooltipContainer = new ValueContainer(name, value);
            tooltipContainer.pad(10);
            list.add(tooltipContainer);
        }
    }

    public List<ValueContainer> getMainParams() {
        return list;
    }

    public List<ValueContainer> getBuffs() {
        return weapon.getBuffs().stream()
         .filter(obj -> StringUtils.isNoneEmpty(obj.getType().getProperty(G_PROPS.IMAGE)))
         .map(AttackTooltipFactory.getObjValueContainerMapper())
         .collect(Collectors.toList());
    }

}
