package eidolons.system.text.tips;

import eidolons.content.consts.Images;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor;
import eidolons.game.core.Core;
import eidolons.netherflame.campaign.assets.NF_Images;
import main.data.DataManager;
import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.MESSAGE_RESPONSE;

public enum TIP implements TextEvent {

    //BRIDGE
    welcome_1(true, Images.DEMIURGE, ""),
    welcome_2(true, DataManager.getObjImage("INSCRIPTION"), "") {
        @Override
        public void run() {
            //            Eidolons.getGame().getLoop().activateMainHeroAction("Move");
            //            Eidolons.getGame().getLoop().activateMainHeroAction("Deathstorm");
        }
    },

    first_battle_over(false, NF_Images.BRIEF_ART.SENTRIES.getPath(), "") {

    },
    cols(true, DataManager.getObjImage("Column"), ""),
    Container(true, DataManager.getObjImage("Enchanted Ash Urn"), ""),

    Black_waters(false, NF_Images.BRIEF_ART.BLACK_WATERS.getPath(), ""),

    fractured_soul_harvester_comment(false, NF_Images.BRIEF_ART.HARVESTER.getPath(), ""),
    HARVESTER(false, NF_Images.BRIEF_ART.HARVESTER.getPath(), ""),

    first_maze_before(false, NF_Images.BRIEF_ART.HARVESTER.getPath(), ""),

    first_art_befor2(false, NF_Images.BRIEF_ART.HARVESTER.getPath(), ""),
    first_art_before(false, NF_Images.BRIEF_ART.HARVESTER.getPath(), ""),
    //        first_art_before_2(false, IGG_Images.BRIEF_ART.HARVESTER.getPath(), ""),

    // PLOT
    Stone_Warden(false, NF_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),

    warden2(false, NF_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
    warden3(false, NF_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
    crypt_maid(false, NF_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),
    crypt_angel(false, NF_Images.BRIEF_ART.STONE_WARDEN.getPath(), ""),

    rune_priest(true, ""),
    //self-chain constructor?i

    // TIPS
    Strategy(true, "", ""),

    //EVENTS

    First_shadow(false, "", ""),
    First_death(false, "", ""),
    HERO_LEVEL_UP(false, "", ""),

    //TUTORIAL
    TUTORIAL_START(false, NF_Images.BRIEF_ART.APHOLON_SMALL.getPath(),
            "The Transformation has taken a heavy toll on me. Without Apholon and its thousand trapped souls, it is my own soul that must be bled to keep Eidolons from disintegrating. \n" +
                    "Fortunately, this place is filled with fragments of the Esoterica - albeit writ in crude runes of the dwarves. Still, each one will give me back a bit of my former Mastery."),

    Gateway_Glyphs(true, Images.GATEWAY_GLYPH, ""),

    //ui
    // combat
    IMMORTAL("Soulforce is effectively infinite, but the negative value you end up with will show you how much better you should do next time to win fairly. "),

    UNCONSCIOUS(false,
            // "My avatar is failing. But it is not over yet, there is life still in this shell!
            // All I need is time... !"),
            Images.DEMIURGE,
            "This shell is waning. Time to put off the mask. The pain will be legendary... " +
                    "I will have to be quick, kill every foe before my avatar crumbles. "

    ),
    SHADE_RESTORE("Their pain will be enough to restore my avatar. Let these ashes rise anew..."),
    DEATH_SHADE_FINAL(false, "This torment is all but in vain, and cruel is the fate. " +
            "Better to sleep now and harken to the song of the stars. Father, I am coming home..."),

    DEATH_SHADE_TIME(false,
            "Late now is the vengeance. " +
                    //                "Out of time. ")
                    "This avatar is lost for now, but my grip is still strong. This quest will continue."),
    DEATH_SHADE(false, "I do not fear death. But have I overestimated myself? Still, there is an escape yet, another chance. " +
            "I better make it count."),

    NEXT_LEVEL(false, Images.VICTORY, ""),

    VICTORY(false, Images.VICTORY, ""),

    DEATH(false, Images.DEFEAT, "My avatar crumbles. The sweet cold of death engulfs me, tempts me. Yet I am not done yet. " +
            "From its ashes I can rise again...\n"),
    DEATH_FINAL(false, "And now comes the darkness. My hand is empty - is this the final hour?"),
    //TODO multiple random ones?!

    //demo
    WELCOME("Greetings! Alexander here, " + //my own pic?!
            "very glad to have you on board! " +
            "I will be guiding your journey through the stormy seas of this Demo. I am sorry to inflict this form of tutorial on you, but with only a few weeks to make the demo, I had little choice! " +
            "If you feel you no longer need aid, be sure to disable these with the checkbox. " +
            "See the Manual via main menu, "),
    WELCOME_2("This Demo consists of " +
            "In this first location, you can choose a path that is relatively safe and has some optional tips to read and space" +
            " to practice your sword. "),
    WELCOME_3("Good luck!"),

    DEMO_END(false, NF_Images.BRIEF_ART.APHOLON.getPath(), ""),

    //        ranged
    //        required reloading with ammo, which you carry in your Quick Slots alongside potions
    //        main\bf\prop\statues\angel statue.png
    /*

     */
/*
TODO DISPLAY TOOLTIP TITLE?
 when things turn ill, you can attempt to escape into shadows again by using Stealth Mode in combination with Shadow Cloak.
use Fire Breath to 'breathe out' and reduce Rage, or just for good AoE damage
be careful not to let the Rage Counters reach 100 - or Gorr will lose control over himself!
another trick is to set yourself on fire and gain Rage each time the burning hurts you



 */
    //dev

    LIBRARY(false, "", "");
    private boolean optional = true;
    private boolean once = true;
    private boolean done;
    private String img;
    private String message;
    public WaitMaster.WAIT_OPERATIONS messageChannel = MESSAGE_RESPONSE;

    TIP(String message) {
        this.message = message;
        this.once = true;
    }

    //to txt of course
    TIP(boolean optional, String message) {
        this.optional = optional;
        this.message = message;


    }

    TIP(boolean optional, String img, String message) {
        this.optional = optional;
        this.img = img;
        this.message = message;
    }

    public void run() {
        //            runnable!=null
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean isOnce() {
        return once;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String getImg() {
        return img;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public WaitMaster.WAIT_OPERATIONS getMessageChannel() {
        return messageChannel;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageChannel(WaitMaster.WAIT_OPERATIONS messageChannel) {
        this.messageChannel = messageChannel;
    }
}
