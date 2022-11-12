package eidolons.entity.unit.trainers;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.content.values.ValuePages;
import eidolons.entity.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.netherflame.eidolon.heromake.handlers.HeroManager;
import main.content.DC_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.WorkspaceMaster;
import main.system.datatypes.WeightMap;

import java.util.List;
import java.util.Map;

public class UnitTrainer {
    /*
     * learn skills as per mastery will requirements work fine? managing xp
	 * prioritizing skills mastery priority vs "xp/skill plan" monsters should
	 * have some other means of progression... perhaps var-passives like
	 * Feral/Rage, but also level-blocked Actives! In xp plan then, and with xp
	 * cost, so it is learned here if possible...
	 */

    private static HeroManager heroManager;

    public static void train(Unit trainee) {
        WeightMap<ObjType> pool = initXpItemPool(trainee);

        for (int i = 0; i < 100; i++) {
            ObjType newSkill = getSkill(trainee, pool);

            if (newSkill != null) {
                learn(newSkill, trainee);
            }

            pool = initXpItemPool(trainee);
            if (pool.isEmpty()) {
                break;
            }
        }

        trainee.initSkills();
        getHeroManager().update(trainee);
    }

    private static boolean isDeterministicMode(Unit trainee) {
        return trainee.getGame().isDummyMode();

    }

    private static ObjType getSkill(Unit trainee, WeightMap<ObjType> pool) {
        if (isDeterministicMode(trainee)) {
            return pool.getGreatest();
        }
        return new RandomWizard<ObjType>().getObjectByWeight(pool);
    }

    private static void generateSkillPlan(Unit trainee) {
        /*
         * weights per mastery level and skill difficulty TODO
		 */
        String plan = getPlan(trainee).replace(Strings.BASE_CHAR, "");
        if (!plan.isEmpty()) {
            if (!plan.endsWith(";")) {
                plan += ";"; // ++ syntax for cancelling [mastery] skills...
            }
        }
        StringBuilder planBuilder = new StringBuilder(plan);
        for (PARAMETER mastery : ValuePages.MASTERIES) {
            Integer score = trainee.getIntParam(mastery);
            if (score <= 0) {
                continue;
            }
            List<ObjType> types = DataManager.toTypeList(DataManager.getTypesSubGroupNames(
             DC_TYPE.SKILLS, mastery.getName()), DC_TYPE.SKILLS);
            for (ObjType t : types) {
                if (planBuilder.toString().contains(t.getName())) {
                    continue;
                }
                if (!WorkspaceMaster.checkTypeIsReadyForUse(t)) {
                    continue;
                }
                //TODO progression Review
                // int weight = Math.max(1, score - t.getIntParam(PARAMS.SKILL_DIFFICULTY));
                // planBuilder.append(t.getName()).append(StringMaster.wrapInParenthesis("" + weight)).append(Strings.CONTAINER_SEPARATOR);
            }
        }
        plan = planBuilder.toString();

        trainee.setProperty(PROPS.LVL_PLAN, plan, true);
    }

    private static void learn(ObjType newSkill, Unit trainee) {
        if (getHeroManager().addItem(trainee, newSkill, DC_TYPE.SKILLS, PROPS.SKILLS)) {
            LogMaster.devLog(
             "SKILL TRAINING: " + trainee.getName() + " learns " + newSkill.getName()
              + ", remaining pts: " + trainee.getIntParam(PARAMS.SKILL_POINTS_UNSPENT));
        }
        // getHeroManager().update(unit); ??
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null) {
            heroManager = new HeroManager(DC_Game.game);
        }
        heroManager.setTrainer(true);
        return heroManager;
    }

    private static WeightMap<ObjType> initXpItemPool(Unit trainee) {
        if (StringMaster.isEmpty(getPlan(trainee)) || getPlan(trainee).contains(Strings.BASE_CHAR)) {
            generateSkillPlan(trainee);
        }
        WeightMap<ObjType> pool = new WeightMap<>();
        Map<ObjType, Integer> map = new RandomWizard<ObjType>().constructWeightMap(getPlan(trainee),
         ObjType.class, DC_TYPE.SKILLS);

        for (ObjType type : map.keySet()) {
            if (type == null) continue;
            if (trainee.checkProperty(PROPS.SKILLS, type.getName())) {
                continue; // TODO ++ exceptions
            }

            String reason = trainee.getGame().getRequirementsManager().check(trainee, type);

            if (reason != null) {
                continue;
            }

            pool.put(type, map.get(type));
            // we really can't have weights here - must be more or less
            // sequential, since it'll be skill trees!
            // and i dont wanna override reqs

        }

        // filter affordable
        // random-pick
        return pool;
    }

    private static String getPlan(Unit trainee) {
        return trainee.getProperty(PROPS.LVL_PLAN);
    }

}
