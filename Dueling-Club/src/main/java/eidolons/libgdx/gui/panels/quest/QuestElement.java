package eidolons.libgdx.gui.panels.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import static eidolons.libgdx.gui.panels.quest.QuestProgressPanel.WIDTH;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestElement extends TablePanelX{
    private final LabelX title;
    private final LabelX progress;
    Quest quest;
    private boolean done;

    public QuestElement(Quest quest) {
        this.quest = quest;
        pad(5);
        float w = GdxMaster.adjustWidth(WIDTH);
        //TODO icon
        setWidth(w);
        add(title = new LabelX(quest.getTitle(), StyleHolder.getHqLabelStyle(18))).left().width(w).row();

        add(progress = new LabelX(quest.getProgressText(), StyleHolder
         .getSizedLabelStyle(FONT.NYALA, 16))).padLeft(GdxMaster.adjustSize(10)).left().width(w);

//        if (quest.getTimeLeft()!=null ){
//        }
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        if (done)
        {
            if (quest.isRewardTaken()) {
                remove();
            }
            return;
        }
        //italic?

        progress.setText(quest.getProgressText());
        if (quest.isComplete()) {
            title.setText(title.getText() + StringMaster.wrapInParenthesis("complete"));
            title.getColor().a=0.6f;
            progress.getColor().a=0.6f;
            done = true;
//            fadeOut();
//            ActorMaster.addRemoveAfter(this);
        }

    }
}
