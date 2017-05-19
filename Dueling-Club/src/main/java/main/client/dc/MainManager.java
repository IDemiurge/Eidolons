package main.client.dc;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.HeroChoiceSequence;
import main.client.cc.logic.HeroCreator;
import main.client.dc.Launcher.VIEWS;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.elements.conditions.Condition;
import main.elements.conditions.NumericCondition;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.game.battlecraft.demo.DemoManager;
import main.game.battlecraft.logic.meta.arcade.ArenaArcadeMaster;
import main.game.battlecraft.logic.dungeon.test.UnitGroupMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioPrecombatMaster;
import main.game.module.adventure.MacroManager;
import main.game.battlecraft.logic.meta.skirmish.SkirmishMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.hotkey.GlobalKeys;
import main.system.hotkey.HC_KeyManager;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.threading.Weaver;

import java.util.Arrays;
import java.util.List;

public class MainManager implements SequenceManager {
    public static final MAIN_MENU_ITEMS[] default_items = {
//     MAIN_MENU_ITEMS.NEW_ADVENTURE,
//            MAIN_MENU_ITEMS.SCENARIO,
//      MAIN_MENU_ITEMS.NEW_ARCADE,
//      MAIN_MENU_ITEMS.NEW_SKIRMISH,
     MAIN_MENU_ITEMS.NEW_DEMO,
     MAIN_MENU_ITEMS.CONTINUE_DEMO,
     MAIN_MENU_ITEMS.HERO_CREATOR,
//     MAIN_MENU_ITEMS.CONTINUE_ARCADE,
//      MAIN_MENU_ITEMS.OPTIONS,
//      MAIN_MENU_ITEMS.EDITOR,
     MAIN_MENU_ITEMS.EXIT};
    MAIN_MENU_ITEMS currentItem = MAIN_MENU_ITEMS.MAIN;
    MAIN_MENU_ITEMS previousItem;
    MainMenu menuComp;
    HC_KeyManager keyManager;
    private Unit hero;
    private ChoiceSequence sequence;
    private HC_SequenceMaster sequenceMaster;
    private GlobalKeys globalKeys;
    private boolean macroMode;
    private DC_Game game;

    public MainManager(MainMenu menuComp) {
        game = DC_Game.game;
        this.menuComp = menuComp;
        sequenceMaster = new HC_SequenceMaster();
        keyManager = new HC_KeyManager();
        this.globalKeys = new GlobalKeys();
        if (Launcher.ITS_ME) {
            globalKeys.initMenuGlobalKeys();
        }

    }

    public static String getPresetGroup() {
        if (Launcher.isSuperFastMode()) {
            return StringMaster.BATTLE_READY;
        }

        return StringMaster.BATTLE_READY + StringMaster.OR + StringMaster.PRESET + StringMaster.OR
         + StringMaster.PLAYTEST;
    }

    public void refresh() {
        // if (!Launcher.isInMenu()) {
        // return;
        // }
        MAIN_MENU_ITEMS[] items = default_items;
        if (currentItem != null && currentItem != MAIN_MENU_ITEMS.MAIN) {
            items = Arrays.copyOf(currentItem.getItems(), currentItem.getItems().length + 1);
            items[currentItem.getItems().length] = MAIN_MENU_ITEMS.BACK;
        }
        menuComp.setItems(items);
    }

    public void itemClicked(MAIN_MENU_ITEMS item) {
        itemClicked(item, false);
    }

    public void itemClicked(MAIN_MENU_ITEMS item, boolean alt) {
        if (item != MAIN_MENU_ITEMS.BACK) {
            previousItem = currentItem;
            currentItem = item;
        }
        // COPY_PRESET,
        // CREATE_PRESET(NEW_HERO, SELECT_LEADER, COPY_PRESET),
        // FACTION(CREATE_PRESET, SELECT_LEADER),
        // EDITOR(NEW_HERO, FACTION),
        switch (item) {

            case COPY_PRESET: {
                new Thread(new Runnable() {
                    public void run() {
                        UnitGroupMaster.copyUnitGroup();
                    }
                }).start();
                break;
            }
            case NEW_PRESET: {
                new Thread(new Runnable() {
                    public void run() {
                        UnitGroupMaster.createUnitGroup(hero);
                    }
                }).start();
                break;
            }
            case NEW: {
                ScenarioPrecombatMaster.newScenario();
                if (ScenarioPrecombatMaster.isPresetHero()) {
                    List<Unit> heroes = null;
                    // init party
                    launchHC(heroes);
                }
                break;
            }
            case LOAD: {
                ScenarioPrecombatMaster.loadScenario();
                break;
            }
            case SCENARIO: {
                DC_Game.game.setGameType(GAME_TYPE.SCENARIO);
                break;
            }

            case NEW_DEMO:

                launchHC(PartyHelper.loadParty(DemoManager.PARTY_NAME, game, false));
                DemoManager.hqEntered();
                break;
            case CONTINUE_DEMO:
            case CONTINUE_LAST:
                if ( game.getGameType() == GAME_TYPE.SCENARIO) {
                    // TODO
                } else {
                    List<Unit> party = PartyHelper.loadParty(PartyHelper
                     .readLastPartyType());
                    if (DC_Game.game.getGameMode() == GAME_MODES.ARENA_ARCADE) {
//                        DC_Game.game.getArenaArcadeMaster().continueArcade(PartyManager.getParty());
                        break;
                    }
//                    DC_Game.game.getArcadeManager().initializeArcade(PartyManager.getParty());
                    launchHC(party);
                }
                break;

            case NEW_ADVENTURE: {
                setMacroMode(true);
                break;
            }
            case LOAD_ADVENTURE: {
                setMacroMode(true);
                launchSelection(DC_TYPE.CHARS, StringMaster.ADVENTURE, InfoMaster.CHOOSE_HERO);
                break;
            }

            case EDIT:
                launchHC();
                break;
            case PLAY:
                launchDC(sequence.getValue());
                break;
            case NEW_LEADER:
                newHero(true);
                break;
            case NEW_HERO:
                newHero(false);
                break;

            case SELECT_PRESET_PARTY:
                launchSelection(DC_TYPE.PARTY, getPresetGroup(), InfoMaster.CHOOSE_PARTY);
                break;

            case SELECT_ARCADE:
                launchSelection(DC_TYPE.PARTY, StringMaster.ARCADE, InfoMaster.CHOOSE_ARCADE);
//                DC_Game.game.getArcadeManager().initializeArcade(PartyManager.getParty());
                break;
            case PRESET_HERO:
                launchSelection(DC_TYPE.CHARS, getPresetGroup(), InfoMaster.CHOOSE_HERO);
                break;
            case MY_HERO:
                launchSelection(DC_TYPE.CHARS, StringMaster.CUSTOM, InfoMaster.CHOOSE_HERO);
                break;
            case SELECT_PARTY:
                break;

            case SELECT_MY_PARTY:
                launchSelection(DC_TYPE.PARTY, StringMaster.CUSTOM, InfoMaster.CHOOSE_PARTY);
                break;
            case NEW_ARCADE:
                CharacterCreator.setArcadeMode(true);
                if (alt) {
                    launchHC(new ListMaster<Unit>().getList(getDefaultEmptyHero()));
                } else if (ArenaArcadeMaster.isTestMode()) {
                    DC_Game.game.setGameMode(GAME_MODES.ARENA_ARCADE);
                    DC_Game.game.setGameType(GAME_TYPE.ARCADE);
                }

                break;
            case CONTINUE_ARCADE: {
                CharacterCreator.setArcadeMode(true);
                break;
            }

            case NEW_SKIRMISH:
                CharacterCreator.setArcadeMode(false);
                break;
            case CUSTOM_SKIRMISH:
                SkirmishMaster.newCustomSkirmish();
                break;
            case CHOOSE_SKIRMISH:
                SkirmishMaster.chooseSkirmish();
                break;
            case HERO_CREATOR:
                break;
            case OPTIONS:
                break;
            case BACK:
                if (currentItem == previousItem) {
                    currentItem = MAIN_MENU_ITEMS.MAIN;
                } else {
                    currentItem = previousItem;
                }
                break;
            case EXIT:
                Launcher.exit();
                break;

        }
        if (currentItem == MAIN_MENU_ITEMS.MAIN) {
            DC_Game.game.setGameType(null);
        } else if (currentItem == MAIN_MENU_ITEMS.NEW_ARCADE) {
            DC_Game.game.setGameType(GAME_TYPE.ARCADE);
        }
        refresh();
    }

    private Unit getDefaultEmptyHero() {

        return null;
    }

    private void back() {
        // TODO more graceful?
        if (isMacroMode()) {

            exitToMainMenu();
            return;
        }

        if (currentItem == previousItem) {
            currentItem = MAIN_MENU_ITEMS.MAIN;
        } else {
            currentItem = previousItem;
        }

        refresh();
        setMenuView();
    }

    private void setMenuView() {
        Launcher.setView(menuComp, VIEWS.MENU);
    }

    public void launchSelection(final DC_TYPE t, final String group, String info) {
        Condition c = null;
        if (t == DC_TYPE.CHARS) {
            if (CharacterCreator.isArcadeMode()) {
                c = new NumericCondition("1", "{match_hero_level}");
            } else {
                if (DC_Game.game.getGameType() == GAME_TYPE.SCENARIO) {
                    if (ScenarioPrecombatMaster.getScenario() != null) {
                        c = ScenarioPrecombatMaster.getSelectHeroConditions();
                    }
                }
            }
        }
        sequenceMaster.launchEntitySelection(t, group, c, hero, info);
        sequenceMaster.getSequence().setManager(this);
        this.sequence = sequenceMaster.getSequence();
    }

    public void newHero(boolean arcadeMode) {
        hero = CharacterCreator.getNewHero();
        Launcher.setView(null, VIEWS.CHOICE);
        // TODO
        if (isMacroMode()) {
            // add filters
        }
        sequence = new HeroChoiceSequence(hero);
        sequence.setManager(this);

        sequence.start();
    }

    private void initSelectedHero() {
        hero = initSelectedHero(sequence.getValue());
    }

    public Unit initSelectedHero(String value) {
        hero = HeroCreator.initHero(value);
        return hero;
    }

    public void doneSelection() {
        switch (currentItem) {
            case PRESET_HERO:
            case MY_HERO:
                initSelectedHero();
                newParty();
                launchHC();
                break;
            case NEW_HERO:
                initNewHero();
                break;
            case NEW_LEADER:
                initNewHero();
                break;
            case SELECT_PRESET_PARTY:
                PartyHelper.loadParty(sequence.getValue());
                launchDC(sequence.getValue()); // "arg"
                break;
            case SELECT_ARCADE:
                launchHC(PartyHelper.loadParty(sequence.getValue()));
                break;
            case SELECT_MY_PARTY:
                launchHC(PartyHelper.loadParty(sequence.getValue()));
                break;
        }

    }

    private void newParty() {
        PartyHelper.newParty(hero);
        PartyHelper.saveParty();
        if (CharacterCreator.isArcadeMode()) {
//            DC_Game.game.getArcadeManager().initializeArcade(PartyManager.getParty());
        } else {
//            DC_Game.game.getArcadeManager().initializeSkirmish(PartyManager.getParty());
        }
    }

    private void initNewHero() {
        Weaver.inNewThread(new Runnable() {
            public void run() {
                CharacterCreator.initNewHero(hero);
                boolean result = (boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION);
                if (!result) {
                    return;
                }
                newParty();
                launchHC();
            }
        });

    }

    public void exitToMainMenu() {

        try {
            globalKeys.disable();
            globalKeys.initMenuGlobalKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Launcher.resetView(VIEWS.MENU);
        currentItem = MAIN_MENU_ITEMS.MAIN;
        refresh();
        CharacterCreator.setInitialized(false);
        DataManager.reloadOverwrittenTypes();
        if (PartyHelper.getParty() != null) {
            PartyHelper.getParty().getMembers().clear();
            PartyHelper.setParty(null);
        }
    }

    private void launchDC(String value) {
        // TODO
        globalKeys.initDC_GlobalKeys();
        Launcher.launchDC(value);
    }

    public void launchHC(List<Unit> heroes) {

        Launcher.launchHC(true, heroes.toArray(new Unit[heroes.size()]));
    }

    public void launchHC() {
        if (isMacroMode()) {
            MacroManager.newGame();
        }
        Launcher.launchHC(true, hero); // false?
        hero.toBase();
        hero.afterEffects();

    }

    public void cancelSelection() {
        back();
    }

    public HC_SequenceMaster getSequenceMaster() {
        return sequenceMaster;
    }

    public ChoiceSequence getSequence() {
        return sequence;
    }

    public MAIN_MENU_ITEMS getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(MAIN_MENU_ITEMS currentItem) {
        this.currentItem = currentItem;
    }

    public void escape() {

        if (!Launcher.isInMenu()) {
            exitToMainMenu();
        } else {
            if (currentItem == MAIN_MENU_ITEMS.MAIN) {
                Launcher.exit();
            } else {
                back();
            }
        }
    }

    public void enter() {

    }

    public void space() { // scroll down? init page panel into here and flip
        // it...
        if (sequence != null) {
            if (sequence.isActive()) {
                sequence.tryNext();
            }
        }
    }

    public boolean isMacroMode() {
        return macroMode;
    }

    public void setMacroMode(boolean macroMode) {
        this.macroMode = macroMode;
    }

    public enum MAIN_MENU_ITEMS {
        MAIN(default_items),
        PRESET_HERO,
        MY_HERO,
        PLAY,
        EDIT,
        SELECT_PRESET_PARTY(PLAY, EDIT),
        SELECT_MY_PARTY(PLAY, EDIT),
        NEW_LEADER,
        SELECT_LEADER(PRESET_HERO, MY_HERO),

        NEW_PARTY(NEW_LEADER, SELECT_LEADER),
        SELECT_PARTY(SELECT_PRESET_PARTY, SELECT_MY_PARTY),
        NEW_ARCADE(NEW_PARTY, SELECT_PARTY),
        CHOOSE_SKIRMISH(NEW_PARTY, SELECT_PARTY),
        CUSTOM_SKIRMISH(NEW_PARTY, SELECT_PARTY),
        NEW_SKIRMISH(CHOOSE_SKIRMISH, CUSTOM_SKIRMISH),

        CONTINUE_LAST(PLAY, EDIT),

        SELECT_ARCADE(PLAY, EDIT),
        CONTINUE_ARCADE(CONTINUE_LAST, SELECT_ARCADE),
        OPTIONS,
        EXIT,
        BACK,
        NEW_HERO,
        HERO_CREATOR(NEW_HERO ),
        NEW_ADVENTURE(NEW_LEADER, SELECT_LEADER),
        LOAD_ADVENTURE,
        ADVENTURE(NEW_ADVENTURE, LOAD_ADVENTURE, CONTINUE_LAST),
        LOAD,
        NEW(NEW_HERO, SELECT_LEADER),
        SCENARIO(NEW, CONTINUE_LAST, LOAD),

        COPY_PRESET,
        NEW_PRESET,
        CREATE_PRESET(NEW_PRESET, NEW_HERO, SELECT_LEADER, COPY_PRESET),
        FACTION(CREATE_PRESET, SELECT_LEADER),
        EDITOR(NEW_HERO, FACTION), NEW_DEMO(), CONTINUE_DEMO(),  ;

        private MAIN_MENU_ITEMS[] items;

        private MAIN_MENU_ITEMS(MAIN_MENU_ITEMS... items) {
            this.setItems(items);
        }

        public MAIN_MENU_ITEMS[] getItems() {
            return items;
        }

        public void setItems(MAIN_MENU_ITEMS[] items) {
            this.items = items;
        }

        public String getText() {
            return StringMaster.getWellFormattedString(name());
        }
    }

}
