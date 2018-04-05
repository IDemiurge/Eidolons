package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.client.cc.gui.neo.choice.ChoiceSequence;
import eidolons.client.cc.gui.neo.choice.ScenarioChoiceView;
import eidolons.client.cc.gui.neo.choice.ScenarioModeChoiceView;
import eidolons.client.dc.Launcher;
import eidolons.client.dc.Launcher.VIEWS;
import eidolons.client.dc.SequenceManager;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.meta.skirmish.SkirmishMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.game.module.dungeoncrawl.dungeon.Location;
import main.content.DC_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
import main.system.entity.ConditionMaster;

import java.util.ArrayList;
import java.util.List;

public class ScenarioPrecombatMaster {
    /*
     * manage 'party's' data, much like with an Arcade
	 * 
	 *  provide info 
	 *  
	 *  win/lose
	 *  after-mission
	 *  
	 *  proto-macro
	 *  time factor? 
	 */

    private static Scenario scenario;
    private static Location location;


    public static void newScenario() {
        final ScenarioChoiceView scv = new ScenarioChoiceView();
        final ChoiceSequence choiceSequence = new ChoiceSequence(scv);
        final ScenarioModeChoiceView smcv = new ScenarioModeChoiceView(choiceSequence,
         SCENARIO_MODES.class);
        choiceSequence.addView(smcv); // TODO only if there are choices!
        choiceSequence.start();
        choiceSequence.setManager(new SequenceManager() {

            @Override
            public void doneSelection() {
                ObjType type = scv.getSelectedItem();
                scenario = new Scenario(type);
                SCENARIO_MODES mode = smcv.getSelectedItem();
                // scenario.setMode(mode);

                if (mode == SCENARIO_MODES.STORY_MODE) {
                    String prop = scenario.getProperty(MACRO_PROPS.MISSION_PARTY);
                    List<Unit> heroes;
                    if (!DataManager.isTypeName(prop, DC_TYPE.PARTY)) {
                        prop = scenario.getProperty(MACRO_PROPS.MISSION_CUSTOM_PARTY);
                        heroes = new ArrayList<>();
                        String leaderName = prop.split(";")[0];
                        Unit leader = Launcher.getMainManager().initSelectedHero(leaderName);
                        // prop = prop.replace(leaderName, "");
                        // for (String s : StringMaster.open(prop)) {
                        // }
                        heroes.add(leader);
                        PartyHelper.newParty(leader);
                        Launcher.getMainManager().launchHC();
                        return;
                    } else {
                        heroes = PartyHelper.loadParty(prop);
                    }
                    Launcher.getMainManager().launchHC(heroes);
                    return;
                }
                Launcher.resetView(VIEWS.MENU);
            }

            @Override
            public void cancelSelection() {
                Launcher.getMainManager().exitToMainMenu();

            }
        });

    }

    public static boolean isPresetHero() {
        return false;
    }

    private static void generateLocation() {
        location = new Location(getScenario());
        Dungeon dungeon = location.construct();
//        DC_Game.game.getDungeonMaster().setDungeon(dungeon);
    }

    // launch
    public static void preLaunch() {
        generateLocation();
    }

    public static void afterLaunch() {
        ObjectiveMaster.initObjectives(scenario.getProperty(MACRO_PROPS.OBJECTIVE_TYPES), scenario
         .getProperty(MACRO_PROPS.OBJECTIVE_DATA), location);
        // scenario.getIntParam(PARAMS.DIFFICULTY_MOD);
    }

    public static Condition getSelectHeroConditions() {
        // generic - level!
        Conditions conditions = new Conditions(ConditionMaster.toConditions(scenario
         .getProperty(MACRO_PROPS.HERO_SELECTION_FILTER_CONDITIONS)));

        String level = scenario.getParam(PARAMS.LEVEL);
        if (!level.isEmpty()) {
            conditions.add(new NumericCondition("{MATCH_LEVEL}", level)); // extract
        }
        return conditions;
    }

    public static Condition getShopFilterConditions() {
        scenario.getProperty(MACRO_PROPS.SHOP_DATA);
        return null;
        // for single-scenarios, do I allow to 'gear up' beforehand? Aye!
        // GOLD_VALUE !
    }

    public static void loadScenario() {
        // TODO Auto-generated method stub

    }

    public static Scenario getScenario() {
        if (scenario == null) {
            return SkirmishMaster.getSkirmish();
        }
        return scenario;
    }

    public static void setScenario(Scenario scenario) {
        ScenarioPrecombatMaster.scenario = scenario;
    }

    public static List<Unit> getHeroesForHire() {
        return new ArrayList<>();
    }

    @Deprecated
    // levels instead!
    public void initScenarioResources() {
        Integer gold = scenario.getIntParam(PARAMS.GOLD);
        Integer xp = scenario.getIntParam(PARAMS.XP);
        for (Unit member : PartyHelper.getParty().getMembers()) {
            member.setParam(PARAMS.GOLD, gold);
        }
    }

    public void scenarioWon() {

    }

    public enum HERO_ELEMENT {
        BACKGROUND, PORTRAIT, NAME, DEITY, PRINCIPLES,

    }

    public enum MODS {
        SHORT,

    }

    public enum SCENARIO_MODES {
        STORY_MODE, RPG_MODE, FREE_MODE,
    }

    public enum STATS {
        LOOT, SECRETS, OBJECTIVES, KILLS, COMPANIONS, TIME, TRAPS, STEALTH,

    }

    public class ScenarioRequirements {
        public boolean isChosen(HERO_ELEMENT element) {
            if (StringMaster.contains(scenario.getProperty(MACRO_PROPS.HERO_CREATION_CHOICE_DATA),
             element.toString())) {
                if (!StringMaster.contains(scenario
                 .getProperty(MACRO_PROPS.HERO_CREATION_CHOICE_DATA), StringMaster.OR)) {
                    return false;
                }
            }
            return true;
        }

    }

}
