package eidolons.libgdx.gui.panels.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestElement extends TablePanelX{
    private final LabelX title;
    private final LabelX progress;
    DungeonQuest quest;

    public QuestElement(DungeonQuest quest) {
        this.quest = quest;
        add(title = new LabelX(quest.getTitle(), StyleHolder.getHqLabelStyle(18)));
        add(progress = new LabelX(quest.getProgressText(), StyleHolder.getSizedLabelStyle(FONT.NYALA, 16)));
        if (quest.getTimeLeft()!=null ){

        }
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        //italic?
        if (quest.isComplete()) {
            fadeOut();
            ActorMaster.addRemoveAfter(this);
        } else {
            progress.setText(quest.getProgressText());
        }
    }
}
