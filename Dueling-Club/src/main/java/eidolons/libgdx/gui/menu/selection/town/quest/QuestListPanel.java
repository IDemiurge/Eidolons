package eidolons.libgdx.gui.menu.selection.town.quest;

import eidolons.libgdx.gui.menu.selection.town.TownPlaceListPanel;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestListPanel extends TownPlaceListPanel {
    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    BUTTON_SOUND_MAP.SELECTION;
    }
}
