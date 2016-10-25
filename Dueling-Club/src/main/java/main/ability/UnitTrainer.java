package main.ability;

import main.client.cc.HeroManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.ValuePages;
import main.content.parameters.PARAMETER;
import main.data.DataManager;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.WeightMap;
import main.system.auxiliary.secondary.WorkspaceMaster;

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

    private static DC_HeroObj unit;
    private static WeightMap<ObjType> pool;
    private static HeroManager heroManager;

    public static void train(DC_HeroObj trainee) {
        unit = trainee;
        initXpItemPool();
        Loop.startLoop(100);
        while (!Loop.loopEnded()) { // TODO it could also add Classes for
            // HEROES!
            ObjType newSkill = getSkill();
            if (newSkill != null)
                learn(newSkill);
            initXpItemPool();
            if (pool.isEmpty())
                break;
        }
        unit.initSkills();
        getHeroManager().update(unit);

    }

    public static boolean isDeterministicMode(DC_HeroObj trainee) {
        return trainee.getGame().isDummyMode();

    }

    private static ObjType getSkill() {
        if (isDeterministicMode(unit))
            return pool.getGreatest();
        return new RandomWizard<ObjType>().getObjectByWeight(pool);
    }

    private static void generateSkillPlan() {
		/*
		 * weights per mastery level and skill difficulty TODO
		 */
        String plan = getPlan().replace(StringMaster.BASE_CHAR, "");
        if (!plan.isEmpty())
            if (!plan.endsWith(";"))
                plan += ";"; // ++ syntax for cancelling [mastery] skills...
        for (PARAMETER mastery : ValuePages.MASTERIES) {
            Integer score = unit.getIntParam(mastery);
            if (score <= 0)
                continue;
            List<ObjType> types = DataManager.toTypeList(DataManager.getTypesSubGroupNames(
                    OBJ_TYPES.SKILLS, mastery.getName()), OBJ_TYPES.SKILLS);
            for (ObjType t : types) {
                if (plan.contains(t.getName()))
                    continue;
                if (!WorkspaceMaster.checkTypeIsReadyForUse(t))
                    continue;
                int weight = Math.max(1, score - t.getIntParam(PARAMS.SKILL_DIFFICULTY));
                plan += t.getName() + StringMaster.wrapInParenthesis("" + weight)
                        + StringMaster.CONTAINER_SEPARATOR;
            }
        }

        unit.setProperty(PROPS.XP_PLAN, plan, true);
    }

    private static void learn(ObjType newSkill) {
        if (getHeroManager().addItem(unit, newSkill, OBJ_TYPES.SKILLS, PROPS.SKILLS))
            main.system.auxiliary.LogMaster.log(1, "SKILL TRAINING: " + unit.getName() + " learns "
                    + newSkill.getName() + ", remaining xp: " + unit.getIntParam(PARAMS.XP));
        // getHeroManager().update(unit); ??
    }

    private static HeroManager getHeroManager() {
        if (heroManager == null)
            heroManager = new HeroManager(DC_Game.game);
        heroManager.setTrainer(true);
        return heroManager;
    }

    private static void initXpItemPool() {
        if (StringMaster.isEmpty(getPlan()) || getPlan().contains(StringMaster.BASE_CHAR)) {
            generateSkillPlan();
        }
        pool = new WeightMap<ObjType>();
        Map<ObjType, Integer> map = new RandomWizard<ObjType>().constructWeightMap(getPlan(),
                ObjType.class, OBJ_TYPES.SKILLS);

        for (ObjType type : map.keySet()) {
            if (unit.checkProperty(PROPS.SKILLS, type.getName()))
                continue; // TODO ++ exceptions
            String reason = unit.getGame().getRequirementsManager().check(unit, type);
            if (reason != null)
                continue;
            pool.put(type, map.get(type));
            // we really can't have weights here - must be more or less
            // sequential, since it'll be skill trees!
            // and i dont wanna override reqs

        }

        // filter affordable
        // random-pick

    }

    private static String getPlan() {
        return unit.getProperty(PROPS.XP_PLAN);
    }

}
