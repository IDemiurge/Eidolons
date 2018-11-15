package eidolons.game.module.dungeoncrawl.quest.tutorial;

import eidolons.game.module.dungeoncrawl.quest.DungeonQuest;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 11/13/2018.
 *
 * combine with normal ones?
 * custom events
 * ++ highlight target always
 *
 */
public class TutorialQuests {

    public enum TUTORIAL_QUEST {
        DANCE("Show me how you move. I want you to break some sweat, jump and whirl, understood?"),
        OPEN_CHEST("Get yourself a weapon, it's in the chest"),
        EQUIP("Put it all on, it's not much, but some armor is better than none. We don't do sword blunting here, after all."),
        KILL_,
        UP_MASTERIES("I'm sure you have learned something this day, haven't you? ",
         "Use [F1] to open the Character Screen", "Click on the plus button next to a mastery to boost its score"),
        //         (TO GAIN
        //        RANKS)
        LEARN_NEW_SKILL (""),
        RESTORE_FOCUS_BEFORE_BATTLE,
        LEARN_NEW_MASTERY,
        ACQUIRE_A_CLASS ,
        USE_FOOD_TO_CAMP,
        USE_POTION ,
        THROW_KNIFE ,;

        TUTORIAL_QUEST(String... text) {
        this.text = text;
        }

        String[] text;
        }


    public static void generateQuestTypes(){
        for (TUTORIAL_QUEST tutorial_quest : TUTORIAL_QUEST.values()) {
            ObjType type = new ObjType();
type.setProperty(MACRO_PROPS.QUEST_TYPE, "Tutorial");
//args?
type.setProperty(G_PROPS.DESCRIPTION, tutorial_quest.text[0]);
            new DungeonQuest(type);
        }
    }




}
