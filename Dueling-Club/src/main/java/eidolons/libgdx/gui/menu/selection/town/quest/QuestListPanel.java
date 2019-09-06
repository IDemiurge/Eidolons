package eidolons.libgdx.gui.menu.selection.town.quest;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.libgdx.gui.menu.selection.town.TownPlaceListPanel;
import main.system.sound.SoundMaster.BUTTON_SOUND_MAP;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestListPanel extends TownPlaceListPanel {
    private   TextButton chosen;

    public QuestListPanel() {
//        GuiEventManager.bind(GuiEventType.QUEST_TAKEN, p -> {
//            chosen=  buttons.getVar(getIndex());
//            setDisabled(true);
//        });
//        GuiEventManager.bind(GuiEventType.QUEST_CANCELLED, p -> {
//            chosen = null;
//            setDisabled(false);
//        });
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (chosen != null) {
            chosen.setChecked(true);
        }
    }

    protected BUTTON_SOUND_MAP getButtonSoundMap() {
        return    BUTTON_SOUND_MAP.SELECTION;
    }
}
