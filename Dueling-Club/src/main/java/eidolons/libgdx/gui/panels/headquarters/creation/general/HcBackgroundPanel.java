package eidolons.libgdx.gui.panels.headquarters.creation.general;

import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/3/2018.
 */
public class HcBackgroundPanel extends SelectionImageTable {
    public HcBackgroundPanel( ) {
        super(4, 4);
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        BACKGROUND subrace = (BACKGROUND) getUserObject();
        List<SelectableItemData> filtered = new ArrayList<>();
        List<ObjType> types = DataManager.getFilteredTypes(
         StringMaster.getWellFormattedString(subrace.name()), DC_TYPE.CHARS, G_PROPS.BACKGROUND);
        for (ObjType sub : types) {
            if (sub.getProperty(G_PROPS.GROUP).equalsIgnoreCase("background"))
                filtered.add(new SelectableItemData(sub.getName(), sub));
        }
        return filtered.toArray(new SelectableItemData[filtered.size()]);
    }
    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.BACKGROUND;
    }
}
