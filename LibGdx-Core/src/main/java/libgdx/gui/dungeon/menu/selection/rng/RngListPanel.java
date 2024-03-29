package libgdx.gui.dungeon.menu.selection.rng;

import libgdx.gui.dungeon.menu.selection.ItemListPanel;
import main.content.values.properties.G_PROPS;
import main.entity.Entity;
import main.system.sound.AudioEnums;
import main.system.sound.AudioEnums.BUTTON_SOUND_MAP;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 10/11/2018.
 */
public class RngListPanel extends ItemListPanel {
    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    AudioEnums.BUTTON_SOUND_MAP.SELECTION_SHARP;
    }
    @Override
    public List<SelectableItemData> toDataList(Collection<? extends Entity> objTypes) {
        List<SelectableItemData> list = new LinkedList<>();
        for (Entity sub : objTypes) {
            SelectableItemData item = new SelectableItemData(sub);
            list.add(item);
            if (sub.getDescription().isEmpty())
                item.setDescription("Deep, treacherous dungeon brought to you by Machine's Imagination");
            item.setFullsizeImagePath(item.getEntity().getProperty(G_PROPS.FULLSIZE_IMAGE));
        }

        return list;
    }
}
