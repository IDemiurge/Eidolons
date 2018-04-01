package eidolons.libgdx.gui.menu.selection.hero;

import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.system.text.HelpMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroListPanel extends ItemListPanel {

    @Override
    public boolean isBlocked(SelectableItemData item) {
        return
         !item.getEntity().getProperty(G_PROPS.WORKSPACE_GROUP).equalsIgnoreCase(WORKSPACE_GROUP
          .COMPLETE.toString());
    }

    @Override
    public List<SelectableItemData> toDataList(List<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            SelectableItemData item = new SelectableItemData(sub);
            list.add(item);
            item.setDescription(HelpMaster.getHeroInfoText(item.getName(), null));
        }

        return list;
    }
}
