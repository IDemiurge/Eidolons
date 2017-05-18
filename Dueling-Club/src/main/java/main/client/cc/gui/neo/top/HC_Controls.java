package main.client.cc.gui.neo.top;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.neo.choice.ChoiceSequence;
import main.client.cc.gui.neo.choice.HeroChoiceSequence;
import main.client.cc.gui.neo.choice.PrincipleChoiceView;
import main.client.cc.logic.HeroLevelManager;
import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.client.dc.SequenceManager;
import main.content.DC_TYPE;
import main.data.XList;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Writer;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.battlecraft.demo.DemoManager;
import main.game.battlecraft.logic.meta.universal.PartyHelper;
import main.game.battlecraft.logic.meta.PartyManager;
import main.game.battlecraft.logic.meta.arcade.ArcadeManager.ARCADE_STATUS;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.game.battlecraft.logic.meta.scenario.ScenarioMaster;
import main.game.battlecraft.logic.meta.scenario.ScenarioMaster.SCENARIO_MODES;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.town.Tavern;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.services.dialog.DialogMaster;
import main.swing.listeners.ButtonHandler;
import main.system.auxiliary.data.FileManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.NameMaster;

import java.awt.*;
//reset could be used for preset parties 
// "New Party" from in Arcade or Custom Skirmish; "Open Party" too
// new/open should be done via menu perhaps

public class HC_Controls extends G_Panel implements SequenceManager, ButtonHandler {
    private static final String SAVE = "Save";
    private static final String BACK = "Undo";
    private static final String LEVEL_UP = "Level Up";
    private static final String FIGHT = "Fight!";
    private static final String FIGHT_2 = "To Glory!";
    private static final String FIGHT_3 = "Start";
    private static final String FIGHT_ARCADE = "Ready";
    private static final String ADD = "Add";
    private static final String ADD_ANY = "Open";
    private static final String CLOSE = "Close";
    private static final String REMOVE = "Remove";
    private static final String NEW_BRANCH = "New Branch";
    private static final String EXPORT_HERO = "Export hero";
    private static final String EXPORT_PARTY = "Export Party";
    private static final String NEW_HERO = "New Hero";
    private static final String MAP = "Map";
    private static final String TAVERNS = "Taverns";
    private static final String TOWNHALL = "Town Hall";
    private static final String TOWNSQUARE = "Town Square";

    private static final String FACTION = "Faction";

    // perhaps better to set some condition on each buttoN? LVL ON DEBUG
    // how will HC screen be used in Macro?
    private static final String[] macro_controls = {SAVE, EXPORT_HERO, BACK, MAP, TAVERNS,
            TOWNHALL, TOWNSQUARE};
    private static final String[] prearcade_controls = {SAVE, EXPORT_HERO, EXPORT_PARTY, NEW_HERO,
            ADD, REMOVE, BACK, LEVEL_UP, FIGHT_3,};

    private static final String[] arcade_controls = {SAVE, NEW_BRANCH, EXPORT_PARTY, EXPORT_HERO,
            BACK, FIGHT_ARCADE,};

    private static final String[] skirmish_controls = {NEW_HERO, EXPORT_PARTY, EXPORT_HERO,
            LEVEL_UP, ADD, REMOVE, BACK, FIGHT,};

    private static final String[] free_controls = {SAVE, EXPORT_HERO, LEVEL_UP, ADD_ANY, CLOSE,
            BACK,}; // ++delete!
    // party
    // mode?
    private static final String ID = "btn";
    HC_ControlButton buttons;
    private Unit hero;
    private Unit newHero;
    private boolean processing;
    private HC_MODE mode;

    public HC_Controls(Unit hero) {

        super("fillx");
        this.hero = hero;
        initButtons();
    }

    public void refresh() {
        // setLayout(new MigLayout("fillx"));
        initButtons();
    }

    private void initButtons() {
        removeAll();
        mode = HC_MODE.ARCADE;
        if (Launcher.getMainManager().isMacroMode()) {
            mode = HC_MODE.MACRO;
        } else if (CharacterCreator.isArcadeMode()) {
            mode = PartyHelper.getParty().getArcadeStatus() != ARCADE_STATUS.PRESTART

                    ? HC_MODE.ARCADE : HC_MODE.PRE_ARCADE;
        } else {
            mode = CharacterCreator.isPartyMode() ? HC_MODE.SKIRMISH : HC_MODE.FREE;
        }
        int i = 0;
        XList<String> controls = new XList<>(mode.getControls());

        if (DC_Game.game.getGameType() == GAME_TYPE.SCENARIO) {
            controls.remove(SAVE);
//            if (ScenarioPrecombatMaster.getScenario().getMode() != SCENARIO_MODES.FREE_MODE) {
//                controls.remove(ADD);
//                controls.remove(NEW_HERO);
//                if (ScenarioPrecombatMaster.getScenario().getMode() == SCENARIO_MODES.RPG_MODE) {
//                    controls.add(3, TAVERNS);
//                }
//            }
        }

        for (String command : controls) {
            String pos = "growx 100";
            add(getButton(command), pos);
            i++;
            if (i == 2) {
                i++;
            }
        }
        revalidate();

    }

    private String getId(int i) {
        return (i > 0) ? ID + i + ".y2" : "0";
    }

    private Component getButton(String command) {
        HC_ControlButton button = new HC_ControlButton(command, this) {
            protected void playClickSound() {
            }

            ;
        };
        button.activateMouseListener();
        return button;
    }

    public void handleClick(String command) {
        handleClick(command, false);
    }

    public void handleClick(String command, final boolean alt) {
        if (processing) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return;
        }
        processing = true;
        try {

            switch (command) {
                // TODO open/close

                // case GROUP:
                // UnitGroupMaster.
                // break;

                case MAP:
                    Launcher.setView(MacroManager.getMacroViewComponent(), VIEWS.MAP);
                    MacroManager.refreshGui();
                    break;
                case TAVERNS:
                    Tavern tavern = MacroManager.getActiveParty().getTown().selectTavern();
                    if (tavern != null) {
                        tavern.openView();
                    }
                    break;
                case ADD:
                    SoundMaster.playStandardSound(STD_SOUNDS.MOVE);

                    if (PartyHelper.checkPartySize() || Launcher.DEV_MODE) {
                        Launcher.getMainManager().getSequenceMaster().chooseNewMember(
                                PartyHelper.getParty());
                    } else {
                        DialogMaster.error("Maximum party size reached!");
                    }
                    break;
                case REMOVE:
                    PartyHelper.remove(hero);// ...
                    break;
                case FIGHT:
                case FIGHT_2:
                case FIGHT_3:
                case FIGHT_ARCADE:
                    new Thread(() -> fight(), " thread").start();
//                            if (Launcher.DEV_MODE) {
//                                if (!alt && isNameGenTest()) {
//                                    testFightLaunch();
//
//                                    // testNameGen();
//                                } else
//                                // DC_Game.game.getDungeonMaster().initDungeonLevelChoice();
//                                {

//                                }
//                            }
                    break;
                case EXPORT_HERO: {
                    SoundMaster.playStandardSound(STD_SOUNDS.DONE);
                    new Thread(new Runnable() {
                        public void run() {
                            CharacterCreator.saveAs(
                                    CharacterCreator.getSelectedHeroType(), !alt);
                            // CharacterCreator.savePreset(hero.getType());
                            SoundMaster.playStandardSound(STD_SOUNDS.OK);
                        }
                    }).start();
                    break;
                }
                case EXPORT_PARTY: {
                    new Thread(new Runnable() {
                        public void run() {

                            PartyHelper.savePartyAs(false);
                        }
                    }).start();
                    break;
                }
                case NEW_BRANCH: {
                    new Thread(new Runnable() {
                        public void run() {

                            PartyHelper.savePartyAs(true);
                        }
                    }).start();

                    break;
                }

                case NEW_HERO: {
                    newHero = CharacterCreator.getNewHero();
                    Launcher.setView(null, VIEWS.CHOICE);
                    final HeroChoiceSequence sequence = new HeroChoiceSequence(newHero);
                    sequence.setManager(this);
                    new Thread(new Runnable() {
                        public void run() {
                            sequence.start();
                        }
                    }).start();
                    break;
                }

                case SAVE:
                    SoundMaster.playStandardSound(STD_SOUNDS.DONE);
                    if (CharacterCreator.isPartyMode()) {
                        PartyHelper.saveParty();
                    } else {
                        CharacterCreator.save(CharacterCreator.getSelectedHeroType());

                    }

                    SoundMaster.playStandardSound(STD_SOUNDS.OK);
                    break;

                case BACK:
                    SoundMaster.playStandardSound(STD_SOUNDS.ACTION_CANCELLED);
                    CharacterCreator.getHeroManager().stepBack(hero);
                    CharacterCreator.refreshGUI();
                    break;
                case LEVEL_UP:
                    // always ready?
                    if (!CharacterCreator.isLevelUpEnabled(hero)) {
                        SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                        break;
                    }

                    HeroLevelManager.levelUp(hero);
                    SoundMaster.playStandardSound(STD_SOUNDS.LEVEL_UP);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            processing = false;

            // CharacterCreator.refreshGUI();
        }
    }

    public void fight() {

        if (Launcher.getMainManager().getSequenceMaster().mainHeroChoiceSequence(hero)) {
            DemoManager.battleEntered();
            Launcher.launchDC();
        }

//        if (hero.getGame().getGameType() == GAME_TYPE.ARCADE){
////            ArcadeManager.
//            DemoManager.init();
//            Launcher.launchDC();
//        }
//          if (hero.getGame().getGameType() == GAME_TYPE.SKIRMISH) {
//                SkirmishMaster.preLaunch();
//            } else if (hero.getGame().getGameType() == GAME_TYPE.SCENARIO) {
//                ScenarioMaster.preLaunch();
//            }
//            Launcher.launchDC();
//
//            if (hero.getGame().getGameType() == GAME_TYPE.SKIRMISH
//                    || hero.getGame().getGameType() == GAME_TYPE.SCENARIO) {
//                ScenarioMaster.afterLaunch();
//            }
//        }
    }

    private void testFightLaunch() {
        ChoiceSequence choiceSequence = new ChoiceSequence();
        choiceSequence.addView(new PrincipleChoiceView(choiceSequence, hero));

        choiceSequence.setManager(new SequenceManager() {

            @Override
            public void doneSelection() {
                Launcher.resetView(VIEWS.HC);
            }

            @Override
            public void cancelSelection() {
                Launcher.resetView(VIEWS.HC);

            }
        });
        choiceSequence.start();
        // choose a dungeon freely?
        // test profiles , enemy presets
        // DC_Game.game.getDungeonMaster().initDungeonLevelChoice();
    }

    private boolean isNameGenTest() {
        return false;
    }

    private void testNameGen() {
        String content = FileManager.readFile(PathFinder.getXML_PATH() + "names\\" + "names.xml");
        while (true) {
            ObjType type = ListChooser.chooseTypeObj_(DC_TYPE.CHARS, "Background");
            if (type == null) {
                break;
            }
            String name;
            while (true) {
                name = CharacterCreator.getHeroName(type);
                if (name == null) {
                    break;
                }
                if (name.equals(NameMaster.NO_NAME)) {
                    break;
                }
                content += name + ";";
            }

        }
        if (!content.isEmpty()) {
            XML_Writer.write(content, PathFinder.getXML_PATH() + "names\\", "names.xml");
        }
    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public void doneSelection() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    CharacterCreator.initNewHero(newHero, false);
                    // if (mode == HC_MODE.PRE_ARCADE) {
                    // DialogMaster.inform("You can add " + newHero.getName()
                    // + " to your party via 'Add' button "
                    // +
                    // "(must match your party leader's principles or deity)");
                    newHero.toBase();
                    // } else
                    PartyHelper.addMember(newHero);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Launcher.resetView(VIEWS.HC);
                }
            }
        }, " thread").start();
    }

    @Override
    public void cancelSelection() {
        Launcher.resetView(VIEWS.HC);

    }

    public enum HC_MODE {
        MACRO(macro_controls),
        PRE_ARCADE(prearcade_controls),
        ARCADE(arcade_controls),
        SKIRMISH(skirmish_controls),
        FREE(free_controls);
        private String[] controls;

        HC_MODE(String[] controls) {
            this.controls = (controls);
        }

        public String[] getControls() {
            return controls;
        }
    }

}
