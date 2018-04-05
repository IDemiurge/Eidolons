package eidolons.libgdx.gui.menu.selection.scenario;

import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.system.text.HelpMaster;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/30/2017.
 */
public class ScenarioListPanel extends ItemListPanel {


    @Override
    public List<SelectableItemData> toDataList(List<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            SelectableItemData item = new SelectableItemData(sub);
            list.add(item);
            item.setDescription(HelpMaster.getScenarioInfoText(item.getName(), null));
            item.setPreviewImagePath(item.getEntity().getProperty(G_PROPS.FULLSIZE_IMAGE));
        }

        return list;
    }
}
