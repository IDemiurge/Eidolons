package main.client.cc.gui.neo.points;

import main.client.cc.CharacterCreator;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.audio.DC_SoundMaster;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;

public class UnlockMaster {

    private static final int GOLD_FACTOR = 10;
    private static final int XP_FACTOR = 5;
    private static final double MAX_GOLD_COST = 4500;
    private static final double MAX_XP_COST = 1500;

    public static boolean promptUnlock(PARAMETER param, Entity entity) {

        int masteriesUnlocked = DC_MathManager.getMasteriesUnlocked(entity);
        masteriesUnlocked -= DC_MathManager.getMasteryDiscount(entity);
        // 25 75 150 250 400 500 [500]?

        Unit hero = CharacterCreator.getHeroManager().getHero((ObjType) entity);

        int xpCost = getXpCost(entity, masteriesUnlocked, hero);
        int goldCost = getGoldCost(entity, masteriesUnlocked, hero);

        // Intelligence point cost limit!

        if (!entity.checkParam(PARAMS.GOLD, goldCost + "")
         && !entity.checkParam(PARAMS.XP, xpCost + "")) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return false;
        }
        // gold/xp cost reduction?

        final UnlockDialog view = new UnlockDialog(entity, param, xpCost, goldCost);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    view.show();
                }
            });
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        // boolean result = (boolean) WaitMaster
        // .waitForInput(WAIT_OPERATIONS.CUSTOM_SELECT);
        //
        // return result;
        return true;
    }

    private static int getGoldCost(Entity entity, int masteriesUnlocked, Unit hero) {
        int goldCost = (int) Math.min(MAX_GOLD_COST, GOLD_FACTOR
         + (GOLD_FACTOR * Math.pow(2, masteriesUnlocked)));

        int goldFactor = -hero.getIntParam(PARAMS.GOLD_COST_REDUCTION)
         + hero.getIntParam(PARAMS.HERO_LEVEL) * hero.getIntParam(PARAMS.HERO_LEVEL);
        goldCost = MathMaster.addFactor(goldCost, goldFactor);
        if (!entity.checkParam(PARAMS.GOLD, goldCost + "")) {
            goldCost = -1;
        }
        return goldCost;
    }

    private static int getXpCost(Entity entity, int masteriesUnlocked, Unit hero) {
        int xpCost = (int) Math.min(MAX_XP_COST, XP_FACTOR
         + (XP_FACTOR * Math.pow(2, masteriesUnlocked)));
        int xpFactor = -hero.getIntParam(PARAMS.XP_COST_REDUCTION)
         + hero.getIntParam(PARAMS.HERO_LEVEL) * hero.getIntParam(PARAMS.HERO_LEVEL);
        xpCost = MathMaster.addFactor(xpCost, xpFactor);
        if (!entity.checkParam(PARAMS.XP, xpCost + "")) {
            xpCost = -1;
        }
        return xpCost;
    }

    public static void unlock(Entity entity, PARAMETER param, boolean gold) {
        int masteriesUnlocked = DC_MathManager.getMasteriesUnlocked(entity);
        masteriesUnlocked -= DC_MathManager.getMasteryDiscount(entity);
        // 25 75 150 250 400 500 [500]?
        Unit hero = CharacterCreator.getHeroManager().getHero((ObjType) entity);

        int cost = gold ? getGoldCost(entity, masteriesUnlocked, hero) : getXpCost(entity,
         masteriesUnlocked, hero);
        unlock(entity, param, gold, cost);
    }

    public static void unlock(Entity entity, PARAMETER param, boolean gold, int cost) {

        CharacterCreator.getHeroManager().saveType((ObjType) entity);

        Unit hero = CharacterCreator.getHeroManager().getHero((ObjType) entity);
        ObjType type = hero.getType();
        type.modifyParameter(param, 1);

        hero.modifyParameter((gold ? PARAMS.GOLD : PARAMS.XP), -cost);

        CharacterCreator.getHeroManager().update((ObjType) entity);

        if (gold) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__COINS);
        } else {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOOK_OPEN);
        }

    }

}
