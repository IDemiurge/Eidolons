package eidolons.game.module.herocreator.logic;

import eidolons.content.PARAMS;
import eidolons.game.eidolon.heromake.NF_ProgressionMaster;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.content.DC_Formulas;
import main.content.VALUE;
import main.entity.Entity;
import main.entity.type.ObjType;

import java.util.HashMap;
import java.util.Map;

public class HeroLevelManager {
    private static final Map<VALUE, String> buffer = new HashMap<>();

    public static void levelUp(Unit hero) {
        levelUp(hero, false);
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

        modifyValues(hero, auto);
        if (!dc_hc_macro) {
            CharacterCreator.getHeroManager().update(hero);
        } else {
            hero.reset();
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
            int identityAdded = DC_Formulas.getIdentityPointsForLevel(level);
            int goldAdded = DC_Formulas.getGoldForLevel(level);
            goldAdded += hero.getIntParam(PARAMS.GOLD_PER_LEVEL);
            // gold per level from Craftsman?
            int mod = hero.getIntParam(PARAMS.GOLD_MOD);
            if (mod != 0) {
                goldAdded = goldAdded * mod / 100;
            }
            hero.modifyParameter(PARAMS.GOLD, goldAdded);
            hero.modifyParameter(PARAMS.IDENTITY_POINTS, identityAdded);
        }

        int p = hero.getIntParam(PARAMS.ATTR_POINTS_PER_LEVEL);
        hero.modifyParameter(PARAMS.ATTR_POINTS, p);
        hero.getType().modifyParameter((PARAMS.ATTR_POINTS_PER_LEVEL),
         DC_Formulas.ATTR_POINTS_PER_LEVEL_BONUS);

        p = NF_ProgressionMaster.getMasteryRanksForLevelUp(hero);
        hero.modifyParameter(PARAMS.MASTERY_RANKS_UNSPENT, p);
        hero.modifyParameter(PARAMS.MASTERY_RANKS, p);
        hero.getType().modifyParameter((PARAMS.MASTERY_RANKS_PER_LEVEL),
         DC_Formulas.MASTERY_RANKS_PER_LEVEL_BONUS);

    }


}
