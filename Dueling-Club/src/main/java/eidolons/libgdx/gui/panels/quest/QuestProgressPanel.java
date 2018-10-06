package eidolons.libgdx.gui.panels.quest;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestProgressPanel extends TablePanelX {
    public QuestProgressPanel() {
        super(GdxMaster.adjustWidth(450), GdxMaster.adjustHeight(800));
        debugAll();
        GuiEventManager.bind(GuiEventType.QUEST_UPDATE,
         p -> {
             updateRequired = true;
         });

        GuiEventManager.bind(GuiEventType.QUEST_ENDED,
         p -> {
             updateRequired = true;
         });
        GuiEventManager.bind(GuiEventType.QUEST_STARTED,
         p -> {
             DungeonQuest quest = (DungeonQuest) p.get();
             QuestElement element = new QuestElement(quest);
             add(element);
             row();
             element.fadeIn();
             element.debugAll();
         });
        GuiEventManager.bind(GuiEventType.GAME_STARTED,
         p -> {
             GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
         });
    }

    @Override
    public void update() {
        GuiEventManager.trigger(GuiEventType.QUESTS_UPDATE_REQUIRED);
    }
}
