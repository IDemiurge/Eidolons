package eidolons.macro.entity.shop;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.game.core.EUtils;

/**
 * Created by JustMe on 11/10/2018.
 */
public class ShopTransactions {
    static  boolean  confirmOff;
    public static boolean confirm(int debt, int max, DC_HeroItemObj item, Shop shop, boolean heroBuys) {
        if (confirmOff)
            return true;
        String text="";
        if (heroBuys) {
            text ="It seems you don't have enough coin, but I will let you have " + item +
              " in credit. You'll owe me " +
             debt +", and I will collect your debt next time you are in town. I'd let you go as high as " +
            max + ", but no higher.";
        } else {
            text ="I don't really have enough coin right now to buy this " +
             item +
             ", but I will soon, so how about a little credit? It's just " +
            debt +", after all."+
             "I will pay you back next time you are in town. I would consider going as far as " +
             max + " in debt, but no more.";
        }
        return EUtils.onConfirm(true, text, true, null, false);

    }
}
