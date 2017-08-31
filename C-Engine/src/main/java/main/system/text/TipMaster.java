package main.system.text;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.text.Tips.*;

import java.util.LinkedList;
import java.util.List;

/*
 * Perhaps I should also make some use of these in-game while in TUTORIAL
 * mode!
 *
 * 
 */
public class TipMaster {
    static boolean no_repeat;
    static Boolean basicOrAdvanced = true;
    private static List displayedTips = new LinkedList<>();

    public static String getTip() {
        return getTip(0);
    }

    public static String getTip(int combat_hero_misc) {
        Class<? extends TIP> tipClass = null;
        switch (combat_hero_misc) {
            case 0:
                tipClass = basicOrAdvanced
                 ? BASIC_COMBAT_TIPS.class
                 : ADVANCED_COMBAT_TIPS.class;
                break;
            case 1:
                tipClass = basicOrAdvanced
                 ? BASIC_HERO_TIPS.class
                 : ADVANCED_HERO_TIPS.class;
                break;
            case 2:
                tipClass = basicOrAdvanced
                 ? BASIC_USABILITY_TIPS.class
                 : BASIC_USABILITY_TIPS.class;
                break;
        }
        String tip = "";
        Loop loop = new Loop(200);
        while (loop.continues()) {
//            int i = RandomWizard.getRandomInt(tipClass.getEnumConstants().length);
            List<TIP> list = new EnumMaster<TIP>().getEnumList((Class<TIP>) tipClass);
            TIP tipp = new RandomWizard<TIP>().getRandomListItem(list);
            if (displayedTips.contains(tipp)) {
                continue;
            }
            tip = tipp.getText();
            if (tip.isEmpty())
                continue;
            displayedTips.add(tipp);
            return tip;
        }
        displayedTips.clear();
        return getTip();
    }

    public static String getText(TIP tip) {
        return "No text for tip: " + tip.toString();
    }

    public static String getText(BASIC_COMBAT_TIPS tip) {
        switch (tip) {
            case ARMOR:
                return "Armor will reduce damage";
            case ATTACK_AND_DEFENSE:
                return  StringMaster.getWellFormattedString(tip.name())
                 +" determine what percentage of damage is dealt. " +
                 "Greater difference creates chance to miss or make a critical strike";
            case DYNAMIC_ROUNDS:
                return
                 "Units make actions when they become 1st in the Initiative Queue on the top. \n" +
                  "";
            case GAME_START:
               return
                "Normally, you can only choose one hero to control in your party.";
            case DEFAULT_ACTIONS:
                return "To make a default action on a unit or cell, just left-click it." +
                 "Your hero will try to either move or attack accordingly.";
            case RESISTANCE:
                return "";
            case VISION:
                return "";
            default:
                return "";

        }

    }

    public static String getText(COMBAT_TIPS tip) {
        switch (tip) {
            case BF_OBJECTS:
                return "";
            case PARAMETER_RULES:
                return "Every dynamic parameter unit has might additional effect if it goes high or drops low, for instance Focus will affect Attack/Defense while Morale"
                 + " - Resistance, Initiative and Spirit";
            case STEALTH:
                return "Unit with Stealth may not appear at all on the battlefield until they are Spotted. A Stealth vs Detection roll is made for each unit that has them within Sight Range whenever the hidden or the spotting unit moves.";
            case SUMMONING_SICKNESS:
                return "If a unit is summoned its Initiative in this round will be reduced by the amount of Time that has passed";
            case TIME_RULE:
                return "At the start of each new round, the Time value (below the Initiative Queue) "
                 + "is set to the greatest Initiative among units in play and active at the moment."
                 + " During the round, this value will decrease based on the greatest amount of "
                 + "Initiative spent by any unit. If it reaches zero, units only have 1 more free"
                 + " action to make, after which may be removed from Initiative Queue. ";
            case UPKEEP:
                return "";
            case WOUNDS_AND_BLEEDING:
                return "";
            default:
                return "";

        }

    }

    public static String getText(ADVANCED_COMBAT_TIPS tip) {
        switch (tip) {
            case ALERT_MODE:
                return "";
            case SMART_ACTION_SEQUENCES:
                return "";
            case USE_INVENTORY:
                return "";
            case USING_TIME_RULE:
                return "";
            case WAIT_ACTION:
                return "";
            default:
                return "";

        }

    }

    public static String getText(ADVANCED_HERO_TIPS tip) {
        switch (tip) {
            case GRADUAL_SPELL_LEARNING:
                return "High-Intelligence heroes may take advantage of gradual spell learning - Memorizing them first and learning En Verbatim the most essential ones as they accumulate Experience";
            case ITEM_ENCHANTMENT:
                return "";
            case MULTICLASSING:
                return "";
            case SPELL_UPGRADES:
                return "Hero can Memorize only one version of a spell and will also have maximum of one learned En Verbatim (base version will be upgraded, after which other upgrades can only be Memorized)";
            default:
                return "";

        }

    }

    public static String getText(BASIC_HERO_TIPS tip) {
        switch (tip) {
            case ARMOR_ITEMS:
                return "";
            case HERO_BACKGROUNDS:
                return "";
            case PRINCIPLES:
                return "Principles are required to acquire Classes and certain skills. They also determine the compatibility of characters in your party - in order to take a character into his party, the leader must have at least 1 common Principle with them, making their choice a strategically crucial one";
            case SPELL_LEARNING:
                return "A hero may learn spell by amassing Knowledge or investing in Intelligence. "
                 + "The former will automatically make all standard spells Known if hero either has "
                 + "both Knowledge and Spell Mastery equal to the Spell's Difficulty (SD) or twice "
                 + "the amount of Knowledge and at least 1 point of Mastery. With Intelligence,"
                 + " hero can manually choose a spell to Learn if the sum of his Intelligence "
                 + "and Spell Mastery is equal or greater than twice the SD.";

            // Even if you intend to increase your Knowledge and spell
            // school Mastery, learning spell manually ahead of time might
            // be a
            // good option, as it will be promoted to Verbatim once you meet
            // the aforementioned requirements.

            case WEAPON_ITEMS:
                return "A character has two weapon slots. Putting a non-weapon item into your offhand slot (Shield, Orb, etc) will still incur a penalty on your main hand Attack";
        }
        return null;

    }

    public static String getText(HERO_TIPS tip) {
        switch (tip) {
            case DIVINATION:
                return "Heroes following a Deity may use Divination Mode to receive semi-random spells from their "
                 + "Deity's Favored Spell Schools, based on the number of actions spent, Charisma and Divination Mastery";
            case JEWELRY:
                return "";
            case QUICK_ITEMS:
                return "";
            case SKILL_POINTS:
                return "The number of Skills from each particular Mastery Group is limited by the number of Mastery Points you invested in this Mastery. Each Skill has its own Point Cost, proportional to the XP cost.";
            case WEIGHT:
                return "";
        }
        return null;

    }

    public static EventListener getListener(Label tip) {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                tip.setText(getTip());
                return true;
            }
        };
    }


}
