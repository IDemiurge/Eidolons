package eidolons.game.module.dungeoncrawl.quest;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestPreparer {

    public void prepareForQuest(DungeonQuest quest) {

        switch (quest.getType()) {
            case BOSS:
                /*
                rng should know in advance?
                 */
                break;
            case HUNT:
                break;
            case COMMON_ITEMS:
                break;
            case ESCAPE:
                break;
        }

    }
}
