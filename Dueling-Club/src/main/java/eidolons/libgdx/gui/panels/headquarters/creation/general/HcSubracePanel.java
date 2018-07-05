package eidolons.libgdx.gui.panels.headquarters.creation.general;

import eidolons.content.DC_ContentValsManager;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.RACE;
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
public class HcSubracePanel extends SelectionImageTable {
    public HcSubracePanel( ) {
        super(6, 6);
    }

    @Override
    protected GuiEventType getEvent() {
        return GuiEventType.HC_SUBRACE_CHOSEN;
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        RACE race = (RACE) getUserObject();
         List<SelectableItemData> filtered = new ArrayList<>();
//        List<ObjType> types = DataManager.getFilteredTypes(
//         StringMaster.getWellFormattedString(race.name()), DC_TYPE.CHARS, G_PROPS.RACE);
//        for (ObjType sub : types) {
//            if (sub.getProperty(G_PROPS.GROUP).equalsIgnoreCase("background"))
//                filtered.add(new SelectableItemData(sub.getName(), sub));
//        }

        BACKGROUND[] subraces = DC_ContentValsManager.getSubraces(race);
        for (BACKGROUND sub : subraces) {
            String typename=StringMaster.getWellFormattedString(sub.name());
            if (sub==BACKGROUND.MAN_OF_EAST_EMPIRE)
                typename = "Easterling";
            ObjType entity = DataManager.getType(typename, DC_TYPE.CHARS);
            String name = StringMaster.getWellFormattedString(sub.name().replace("MAN_OF", "")).trim();
            if (entity==null )
                continue;
            String imagePath = entity.getImagePath();
            String description = entity.getDescription();
            String previewImagePath = StringMaster.getAppendedImageFile(entity.getImagePath(), " full", true);
            String emblem = entity.getEmblemPath();
            SelectableItemData item = new SelectableItemData(name, description, previewImagePath, imagePath);
            item.setEmblem(emblem);
            item.setEntity(entity);
            filtered.add(item);

        }
        return filtered.toArray(new SelectableItemData[filtered.size()]);
    }

    @Override
    protected PROPERTY getProperty() {
        return null;
    }
}
