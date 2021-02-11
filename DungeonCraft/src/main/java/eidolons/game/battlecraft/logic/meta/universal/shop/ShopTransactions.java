package eidolons.game.battlecraft.logic.meta.universal.shop;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.core.EUtils;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 11/10/2018.
 */
public class ShopTransactions {
    static boolean confirmOff;

    public static boolean confirm(int debt, int max, DC_HeroItemObj item, Shop shop, boolean heroBuys) {
        if (confirmOff)
            return true;
        String text;
        if (heroBuys) {
            text = "It seems you don't have enough coin, but I will let you have this " + item.getName() +
             " in credit. You'll owe me " +
             debt + " gold pieces, and I will collect your debt next time you are in town. Of course, there will be a small interest on the sum, but then, we're not charity. I'd let you go as high as " +
             max + ", but no higher.";
        } else {
            text = "I don't really have enough coin right now to buy this " +
             item.getName() +
             ", but I will soon, so how about a little credit? It's just " +
             debt + ", after all." +
             "I will pay you back next time you are in town. I would consider going as far as " +
             max + " in debt, but no more.";
        }
        return EUtils.onConfirm(true, text, true, null, false);

    }

    public static String getDebtHandleMessage(Shop shop, boolean ok, boolean gives,
                                              int transferred, int playerBalance) {
        StringBuilder message = new StringBuilder();
        message.append(getReturnMessage(gives, shop));
        if (gives) {
            message.append(getGivesMessage(transferred, shop, ok, playerBalance));
        } else {
            message.append(getTakesMessage(transferred, shop, ok, playerBalance));
        }
        //        if (ok) {
        //            message.append(getDebtDoneMessage(gives, shop));
        //        } else {
        //            message.append(getDebtRemainsMessage(gives, shop));
        //        }

        return message.toString();
    }

    private static String getTakesMessage(int transferred, Shop shop, boolean ok, int playerBalance) {
        return ok
         ? new WeightMap<String>()
         .chain("I'll be collecting that debt then, shan't I? ", 5)
         .chain("Let's settle our debt then, shan't we? ", 5)
         .chain("Why don't we settle our debt then?  ", 5)
         .getRandomByWeight()
         : new WeightMap<String>()
         .chain("I'll be collecting that debt then, shan't I? ", 5)
         .chain("Let's settle our debt then, shan't we? ", 5)
         .chain("Why don't we settle our debt then? " +
          transferred +
          " well and paid, " +
          playerBalance +
          " to go, then. Mind the interest, friend, I'm not a charity. Doesn't do your reputation " +
          "any good either, that debt of yours ", 5)
         .getRandomByWeight();
    }

    private static String getGivesMessage(int transferred, Shop shop, boolean ok, int playerBalance) {
        return ok
         ? new WeightMap<String>()
         .chain("I've just finished with my books, there is your gold, " +
          transferred + " pieces, down to a coin. Now, why don't you buy yourself something useful with that? Let me show you...", 5)
         .chain("I haven't forgotten our agreement - here is the gold, " +
          transferred + " pieces, no less, no more... " +
          "Any more things to sell? I certainly have, take a look!", 5)
         .getRandomByWeight()
         : new WeightMap<String>()
         .chain("Well, it seems that I will need a bit more time to collect the coin, but there are some " +
          transferred + " pieces for you, the rest will have to wait, sorry about that. You can buy something any time though, and we will write it off for the debt, how about that?", 5)
         .getRandomByWeight();
    }

    private static String getReturnMessage(boolean gives, Shop shop) {
        return new WeightMap<String>()
         .chain("So, you have returned... ", gives ? 15 : 2)
         .chain("I see the Gods have been merciful, you are back... ", gives ? 15 : 2)

         .chain("Ah, just the one I've been looking for! ", 5)
         .chain("There you are, I've been looking for you... ", 5)
         .chain("Welcome back, friend. ", 5)

         .chain("There-there, just who I've been looking for... ", gives ? 0 : 15)
         .chain("At last, the one I've been waiting for... ", gives ? 0 : 15)
         .chain("So, you are back. ", gives ? 0 : 5)
         .getRandomByWeight();
    }
    private static String getDebtDoneMessage(boolean gives, Shop shop) {
        return "";
    }
    private static String getDebtRemainsMessage(boolean gives, Shop shop) {
        //"Don't let see you slipping out of town! " +
        //         "Wouldn't be surprised if other merchants gave you a bit of a friendly discount after this.";
        //         "gold pieces, wasn't it? Still don't have it? Well, the interest's just doubled, friend. " +
        //          //         " Nice doing business with you!");

        return "";
    }

}
