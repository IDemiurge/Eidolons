package main.game.logic.dungeon.scenario;

import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.ScenarioChoiceView;
import main.client.cc.gui.neo.choice.ScenarioModeChoiceView;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.SequenceManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.elements.conditions.NumericCondition;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.Location;
import main.game.logic.generic.PartyManager;
import main.game.meta.skirmish.SkirmishMaster;
import main.system.entity.ConditionMaster;
import main.system.auxiliary.StringMaster;

import java.util.LinkedList;
import java.util.List;

public class ScenarioMaster {
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
                        heroes = new LinkedList<>();
                        String leaderName = prop.split(";")[0];
                        Unit leader = Launcher.getMainManager().initSelectedHero(leaderName);
                        // prop = prop.replace(leaderName, "");
                        // for (String s : StringMaster.openContainer(prop)) {
                        // }
                        heroes.add(leader);
                        PartyManager.newParty(leader);
                        Launcher.getMainManager().launchHC();
                        return;
                    } else {
                        heroes = PartyManager.loadParty(prop);
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
        DC_Game.game.getDungeonMaster().setDungeon(dungeon);
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
        ScenarioMaster.scenario = scenario;
    }

    public static List<Unit> getHeroesForHire() {
        return scenario.getHeroesForHire();
    }

    @Deprecated
    // levels instead!
    public void initScenarioResources() {
        Integer gold = scenario.getIntParam(PARAMS.GOLD);
        Integer xp = scenario.getIntParam(PARAMS.XP);
        for (Unit member : PartyManager.getParty().getMembers()) {
            member.setParam(PARAMS.GOLD, gold);
        }
    }

    public void scenarioWon() {

    }

    public enum HERO_ELEMENT {
        BACKGROUND, PORTRAIT, NAME, DEITY, PRINCIPLES,

    }

    public enum STATS {
        LOOT, SECRETS, OBJECTIVES, KILLS, COMPANIONS, TIME, TRAPS, STEALTH,

    }

    public enum MODS {
        SHORT,

    }

    public enum SCENARIO_MODES {
        STORY_MODE, RPG_MODE, FREE_MODE,
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
