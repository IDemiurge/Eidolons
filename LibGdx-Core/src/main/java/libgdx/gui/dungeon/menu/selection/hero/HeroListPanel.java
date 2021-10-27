package libgdx.gui.dungeon.menu.selection.hero;

import libgdx.gui.dungeon.menu.selection.ItemListPanel;
import eidolons.system.text.HelpMaster;
import main.entity.Entity;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroListPanel extends ItemListPanel {

    @Override
    public boolean isBlocked(SelectableItemData item) {
            return false;
        // return
        //  !item.getEntity().getProperty(G_PROPS.WORKSPACE_GROUP).equalsIgnoreCase(WORKSPACE_GROUP
        //   .COMPLETE.toString());
    }
    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    AudioEnums.BUTTON_SOUND_MAP.SELECTION_SHARP;
    }
    @Override
    public List<SelectableItemData> toDataList(Collection<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            SelectableItemData item = new SelectableItemData(sub);
            list.add(item);
            item.setDescription(HelpMaster.getHeroInfoText(item.getName(), null));
        }

        return list;
    }
}
