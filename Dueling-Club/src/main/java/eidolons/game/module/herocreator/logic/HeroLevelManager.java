package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.anims.text.FloatingTextMaster.TEXT_CASES;
import eidolons.system.DC_Formulas;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.logic.event.Event;
import main.system.sound.AudioEnums;

import java.util.HashMap;
import java.util.Map;

public class HeroLevelManager {
    public static final VALUE[] LEVEL_RELEVANT_VALUES = {PARAMS.XP_LEVEL_MOD,};
    private static final Map<VALUE, String> buffer = new HashMap<>();

    public static void levelUp(Unit hero) {
        levelUp(hero, false);
    }

    public static void levelUpByXp(Unit hero) {
        levelUp(hero, null);
        if (hero.isDead())
            return; //for ChainParty
        FloatingTextMaster.getInstance().createFloatingText(
                TEXT_CASES.LEVEL_UP, "Level Up!", hero);
        EUtils.showInfoText(hero.getName() + "is now Level " + hero.getLevel());
        EUtils.playSound(AudioEnums.STD_SOUNDS.LEVEL_UP);

        hero.getGame().fireEvent(new Event(Event.STANDARD_EVENT_TYPE.HERO_LEVEL_UP, hero.getRef().getCopy()));
    }

    public static void levelUp(Unit hero, Boolean dc_hc_macro) {
        boolean auto = false;
        if (dc_hc_macro == null) {
            auto = true;
            dc_hc_macro = true;
        }
        if (!dc_hc_macro && hero.getGame().isSimulation()) {
            CharacterCreator.getHeroManager().saveHero(hero);
            CharacterCreator.getHeroManager().update(hero);
        }
        ObjType type = hero.getType();

        copyLevelValues(hero, type);

        hero.modifyParameter(PARAMS.MASTERY_POINTS, DC_Formulas.getMasteryFromIntelligence(hero
         .getIntParam(PARAMS.KNOWLEDGE)));

        modifyValues(hero, auto);
        if (!dc_hc_macro) {
            CharacterCreator.getHeroManager().update(hero);
        } else {
            hero.reset();
        }
        resetLevelValues(type);
    }

    private static void resetLevelValues(ObjType type) {
        for (VALUE v : LEVEL_RELEVANT_VALUES) {
            type.setValue(v, buffer.get(v));
        }
    }

    private static void copyLevelValues(Unit hero, ObjType type) {
        for (VALUE v : LEVEL_RELEVANT_VALUES) {
            buffer.put(v, type.getValue(v));
            type.setValue(v, hero.getValue(v));
        }
    }

    public static void addLevels(Entity hero, int levels) {
        for (int i = 0; i < levels; i++) {
            modifyValues(hero);
        }
    }

    private static void modifyValues(Entity hero) {
        modifyValues(hero, true);
    }

    private static void modifyValues(Entity hero, boolean auto) {
        hero.getType().modifyParameter(PARAMS.HERO_LEVEL, 1);
        hero.getType().modifyParameter(PARAMS.LEVEL, 1);
        if (!auto) {
            int level = hero.getType().getIntParam(PARAMS.HERO_LEVEL);
            int xpAdded = DC_Formulas.getXpForLevel(level);
            int identityAdded = DC_Formulas.getIdentityPointsForLevel(level);
            int goldAdded = DC_Formulas.getGoldForLevel(level);
            goldAdded += hero.getIntParam(PARAMS.GOLD_PER_LEVEL);
            // gold per level from Craftsman?
            int mod = hero.getIntParam(PARAMS.XP_LEVEL_MOD);
            if (mod != 0) {
                xpAdded = xpAdded * mod / 100;
            }
            mod = hero.getIntParam(PARAMS.GOLD_MOD);
            if (mod != 0) {
                goldAdded = goldAdded * mod / 100;
            }
            hero.modifyParameter(PARAMS.GOLD, goldAdded);
            hero.getType().modifyParameter(PARAMS.TOTAL_XP, xpAdded);
            hero.modifyParameter(PARAMS.XP, xpAdded);
            hero.modifyParameter(PARAMS.IDENTITY_POINTS, identityAdded);
        }

        int p = hero.getIntParam(PARAMS.ATTR_POINTS_PER_LEVEL);
        hero.modifyParameter(PARAMS.ATTR_POINTS, p);
        hero.getType().modifyParameter((PARAMS.ATTR_POINTS_PER_LEVEL),
         DC_Formulas.ATTR_POINTS_PER_LEVEL_BONUS);

        p = hero.getIntParam(PARAMS.MASTERY_POINTS_PER_LEVEL);
        hero.modifyParameter(PARAMS.MASTERY_POINTS, p);
        hero.getType().modifyParameter((PARAMS.MASTERY_POINTS_PER_LEVEL),
         DC_Formulas.MASTERY_POINTS_PER_LEVEL_BONUS);

    }

    private static void upMasteries(Entity hero) {
        for (PARAMETER p : ContentValsManager.getMasteries()) {
            if (hero.getIntParam(p) > 0) {
                int amount = hero.getIntParam(ContentValsManager.getPerLevelValue(p.toString()));
                if (amount > 0) {
                    hero.modifyParameter(p, amount);
                }
            }
        }
    }

    @Deprecated
    private static void upAttrs(Entity hero) {

        // for (ATTRIBUTE attr : ATTRIBUTE.values()) {
        // int amount = hero.getIntParam(ContentManager.getPerLevelValue(attr
        // .getParameter().toString()));
        // if (amount > 0)
        // hero.modifyParameter(ContentManager.getBaseAttribute(attr
        // .getParameter()), amount);
        // }
    }

    public static void checkLevelUp(Unit hero) {

        int level = hero.getLevel();
        if (level < DC_Formulas.getLevelForXp(hero.getIntParam(PARAMS.TOTAL_XP))) {
            levelUpByXp(hero);
        }
    }

    public static void addGold(Unit hero, int gold) {
        hero.modifyParameter(PARAMS.GOLD, gold);
        FloatingTextMaster.getInstance().createFloatingText(
         TEXT_CASES.GOLD, gold + " gold", hero);
        EUtils.showInfoText("Gold gained: " + gold);
    }

    public static void addXp(Unit hero, int xp) {
        hero.xpGained(xp);
        FloatingTextMaster.getInstance().createFloatingText(
         TEXT_CASES.XP, xp + " xp", hero);

        //      TODO   EUtils.showVFX(new EnumMaster<EMITTER_PRESET>().getRandomEnumConst(EMITTER_PRESET.class),
        //         GridMaster.getCenteredPos(hero.getCoordinates())
        //        );
        EUtils.showInfoText("Experience gained: " + xp);
    }

    public static void addXpForKill(Unit unit, Unit killer) {
        int divider = Math.max(1, killer.getLevel() - unit.getLevel());
        int xp = (int) (Math.round((unit.getIntParam(PARAMS.POWER) / 10 +
         (Math.sqrt(unit.getIntParam(PARAMS.POWER)) *
          unit.getIntParam(PARAMS.POWER)) / 50) / divider));

        addXp(killer, xp);
    }
}
