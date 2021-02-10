package libgdx.gui.panels.headquarters.creation.selection.race;

import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import libgdx.texture.Images;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/3/2018.
 */
public class HcBackgroundPanel extends SelectionImageTable {
    public HcBackgroundPanel() {
        super(4, 4, 32);
    }

    @Override
    protected GuiEventType getEvent() {
        return GuiEventType.HC_BACKGROUND_CHOSEN;
    }

    public String getDisplayablePath(SelectableItemData data) {
        return data.getEmblem();
    }

    @Override
    protected void selected(SelectableItemData item) {
        super.selected(item);
        //apply type to hero?
        HeroCreationMaster.applyBackgroundType(item.getEntity());
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        ObjType type = (ObjType) getUserObject();

        List<SelectableItemData> filtered = new ArrayList<>();
        List<ObjType> types = DataManager.getFilteredTypes(
         DC_TYPE.CHARS,
         StringMaster.format(type.getProperty(G_PROPS.BACKGROUND)), G_PROPS.BACKGROUND);
        for (ObjType sub : types) {
            if (sub.getProperty(G_PROPS.GROUP).equalsIgnoreCase("background")) {
                SelectableItemData
                 item = new SelectableItemData(sub.getName(), sub);
                item.setSelectionUnderneath(true);
                try {
                    item.setFullsizeImagePath(Images.getSketch(
                     new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, sub.getProperty(G_PROPS.BACKGROUND))
                    ));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
                filtered.add(item);
                item.setBorderSelected(Images.WEAVE_LINK);
            }
        }
        return filtered.toArray(new SelectableItemData[0]);
    }

    @Override
    protected PROPERTY getProperty() {
        return null;
    }
}
