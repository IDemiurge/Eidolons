package eidolons.game.module.herocreator;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.Simulation;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_Game.GAME_TYPE;
import eidolons.game.module.herocreator.logic.HeroCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.swing.generic.services.dialog.DialogMaster;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.CUSTOM_HERO_GROUP;
import main.content.enums.system.MetaEnums;
import main.content.enums.system.MetaEnums.WORKSPACE_GROUP;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.threading.Weaver;

import javax.swing.*;
import java.awt.*;

public class CharacterCreator {

    private static final String TITLE = "Hero Creator";
    public static int STD_COLUMN_NUMBER = 6;
    private static DC_Game game;
    private static HeroManager heroManager;
    private static HeroCreator heroCreator;
    private static Dimension heroPanelSize;
    private static ObjType selectedHeroType;
    private static boolean initialized;

    private static boolean partyMode;
    private static Party party;
    private static boolean arcadeMode;
    private static boolean AV;
    private static HeroManager dc_HeroManager;
    private static Unit hero;

    public static void init() {
        setHeroCreator(HeroCreator.getInstance() );
        setHeroManager(new HeroManager(getGame()));
    }

    // heroes;

    public static void setSelectedHeroType(ObjType selectedHeroType) {
        CharacterCreator.selectedHeroType = selectedHeroType;
    }

    public static void open(boolean unit) {

    }

    public static void open(ObjType type) {
        // no additional levels; support renaming; save
    }

    public static void saveAs(ObjType type) {
        saveAs(type, false);
    }

    public static void saveAs(ObjType type, boolean preset) {
        // choose new name

        boolean newVersion = false;
        // if (DataManager.isTypeName(name)) {
        new Thread(new Runnable() {
            public void run() {
                DialogMaster.ask("Save as...", // This name has already been
                 // used, what would you like to
                 // do?
                 true, "Overwrite", "New Hero", "New Version");
            }
        }).start();

        Boolean result = (Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.OPTION_DIALOG);
        String name = type.getName();
        if (result == null) {
            name = NameMaster.appendVersionToName(name);
            newVersion = true;
        } else if (!result) {
            name = getHeroName(getHero()); // TODO what if it's not the one?
            if (name == null) {
                return;
            }
            // if (!preset)
            // if (checkNameBlocked(name)) {
            // DialogMaster.error("Sorry, this name is blocked.");
            // return;
            // }
            // saveAs(type, preset);
            return;
        }
        // }
        ObjType newType = new ObjType(type);
        newType.setProperty(G_PROPS.NAME, name);
        Simulation.getGame().initType(newType);

        if (!partyMode) {
            addHero(newType);
        }
        if (preset && !newVersion) {
            newType.setGroup(StringMaster.PRESET, false);
            String value = ListChooser.chooseEnum(CUSTOM_HERO_GROUP.class);
            if (value != null) {
                newType.setProperty(G_PROPS.CUSTOM_HERO_GROUP, value);
            }
            WORKSPACE_GROUP ws = getDefaultWorkspaceGroup();
            String string = ListChooser.chooseEnum(WORKSPACE_GROUP.class);
            if (string != null) {
                ws = new EnumMaster<WORKSPACE_GROUP>().retrieveEnumConst(WORKSPACE_GROUP.class,
                 string);
            }
            newType.setWorkspaceGroup(ws);
        } else {
            newType.setGroup(getFilterGroup(), false);
            newType.setProperty(G_PROPS.CUSTOM_HERO_GROUP, StringMaster
             .getWellFormattedString(getDefaultSpecGroup().name()));
            newType.setWorkspaceGroup(getDoneWorkspaceGroup());
            // automatic ?
        }

        save(newType);
        DataManager.addType(newType);
        if (newVersion) {
            heroManager.applyChangedType(heroManager.getHero(type), newType);
        }
    }

    public static String getFilterGroup() {
        return StringMaster.BATTLE_READY;
    }

    public static WORKSPACE_GROUP getDoneWorkspaceGroup() {
        return MetaEnums.WORKSPACE_GROUP.COMPLETE;
    }

    public static CUSTOM_HERO_GROUP getDefaultSpecGroup() {
        return CUSTOM_HERO_GROUP.PLAYTEST;
    }

    public static WORKSPACE_GROUP getDefaultWorkspaceGroup() {
        return WORKSPACE_GROUP.TEST;
    }

    private static boolean checkNameBlocked(String name) {
        if (!DataManager.isTypeName(name)) {
            return false;
        }
        ObjType type = DataManager.getType(name, DC_TYPE.CHARS);
        return !type.getGroup().equals(StringMaster.CUSTOM);
    }

    public static void save(ObjType type) {
        type.setProperty(G_PROPS.VERSION, CoreEngine.VERSION);
        XML_Writer.writeXML_ForType(type, type.getOBJ_TYPE_ENUM(), type.getGroupingKey());
    }

    public static boolean checkHeroName(String newName) {

        return !(newName == null || !DataManager.checkTypeName(newName));

    }

    private static void addHero(ObjType newType) {
        addHero(HeroCreator.createHeroObj(newType));

    }
@Deprecated
    public static void refreshGUI() {

    }

    public static void addHero(final Unit hero) {
        addHero(hero, false);
    }

    public static void addHero(final Unit hero, boolean initial) {

        getHeroManager().addHero(hero);
        setSelectedHeroType(hero.getType());
        setInitialized(true);
        saveLastPartyData();
    }

    public static Unit getNewHero() {
        return getHeroCreator().newHero();
    }

    public static void addNewHero() {
        Unit hero = getHeroCreator().newHero();

        if (hero != null) {
            addHero(hero);

        }
    }

    public static DC_Game getGame() {
        if (game == null) {
            return DC_Game.game;
        }
        return game;
    }

    public static void setGame(DC_Game game) {
        CharacterCreator.game = game;
    }


    public static Unit getHero() {
        return hero;
    }

    public static void setHero(Unit hero) {
        CharacterCreator.hero = hero;
    }


    public static HeroManager getHeroManager() {
        return getHeroManager(getGame().isSimulation());

    }

    public static void setHeroManager(HeroManager heroManager) {
        CharacterCreator.heroManager = heroManager;
    }

    public static HeroManager getHeroManager(boolean simulation) {
        return simulation ? heroManager : dc_HeroManager;
    }

    public static HeroCreator getHeroCreator() {
        if (heroCreator == null) {
            heroCreator = HeroCreator.getInstance();
        }
        return heroCreator;
    }

    public static void setHeroCreator(HeroCreator heroCreator) {
        CharacterCreator.heroCreator = heroCreator;
    }

    public static boolean isPartyMode() {
        return partyMode;
    }

    public static void setPartyMode(boolean partyMode) {
        CharacterCreator.partyMode = partyMode;
    }

    public static Dimension getHeroPanelSize() {
        return heroPanelSize;
    }

    public static void setHeroPanelSize(Dimension heroPanelSize) {
        CharacterCreator.heroPanelSize = heroPanelSize;
    }

    public static boolean isAV() {
        return AV;
    }

    public static void setAV(boolean aV) {
        AV = aV;
    }

    public static Party getParty() {
        return party;
    }

    public static void setParty(Party party) {
        CharacterCreator.party = party;
    }

    public static boolean isArcadeMode() {
        return arcadeMode;
    }

    public static void setArcadeMode(boolean arcadeMode) {
        CharacterCreator.arcadeMode = arcadeMode;
    }

    public static void partyMemberAdded(Unit hero) {
        addHero(hero);
        saveLastPartyData();
    }

    public static void saveLastPartyData() {
//        FileManager.write(PartyHelper.getParty().getMemberString(), Launcher.getLastPresetPath());
    }

    public static void partyMemberRemoved(Unit hero) {
        saveLastPartyData();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initialized) {
        CharacterCreator.initialized = initialized;
    }

    public static void savePreset(ObjType heroType) {
//        saveAs(heroType, CoreEngine.DEV_MODE);

    }

    public static void setDC_HeroManager(HeroManager heroManager) {
        dc_HeroManager = heroManager;

    }

    public static void initNewHero(final Unit hero) {
        initNewHero(hero, true);
    }

    public static void initNewHero(final Unit hero, boolean newThread) {
        if (newThread) {
            Weaver.inNewThread(new Runnable() {
                public void run() {
                    WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, initHero(hero));
                }
            });
        } else {
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, initHero(hero));
        }
    }

    public static String getHeroName(Entity hero) {
        String name;
        NameMaster.clearNames();

        Boolean result;
        if (SwingUtilities.isEventDispatchThread()) {
            DialogMaster.ask("What about the hero's name?", true, "Random", "Input", "Back");
            result = (Boolean) WaitMaster.waitForInput(DialogMaster.ASK_WAIT);
        } else {
            result = DialogMaster.askAndWait("What about the hero's name?", true, "Random",
             "Input", "Back");
        }
        if (result == null) {
            // TODO
            return null;
        }
        while (true) {
            if (!result) {
                name = DialogMaster.inputText(InfoMaster.INPUT_HERO_NAME, NameMaster
                 .generateName(hero));
                if (name == null) {
                    return null;
                }
                if (CharacterCreator.checkHeroName(name)) {
                    break;
                }
            }
            name = NameMaster.generateName(hero);
            result = DialogMaster.askAndWait(RandomWizard.random() ? "Perhaps '" + name
              + "' will do?" : "How about '" + name + "'?", true,
             RandomWizard.random() ? "Keep going" : "Try Again",
             RandomWizard.random() ? "Sure" : "Great",
             RandomWizard.random() ? "I'd like to..." : "Let me...");

            if (result == null) {
                result = DialogMaster.askAndWait("Do what, exactly?", "Pick", "Set Group", "Edit");
                // set background and continue...
                // view/pick names
                if (result == null) {

                    name = DialogMaster.inputText(RandomWizard.random() ? "Got a better idea?"
                     : "You're the boss...", NameMaster.generateName(hero));
                    // if (name == null)
                    // {
                    // return null;
                    // }
                    if (!CharacterCreator.checkHeroName(name)) {
                        result = true;
                        continue;
                    }
                    break;
                }
                if (result) {
                    name = NameMaster.pickName(hero);
                    if (name == null) {
                        continue;
                    }
                } else {
                    String bg = ListChooser.chooseEnum(BACKGROUND.class, SELECTION_MODE.SINGLE);

                    hero.setProperty(G_PROPS.BACKGROUND, bg);
                    result = true;
                    continue;

                }
            }
            if (!result) {
                break;
            }
        }
        hero.setProperty(G_PROPS.BACKGROUND, hero.getType().getProperty(G_PROPS.BACKGROUND));
        return name;
    }

    public static boolean initHero(Unit hero) {
        String name = getHeroName(hero);
        if (name == null) {
            return false;
        }

        hero.setName(name);
        // if (CharacterCreator.isArcadeMode()) {
        hero.setGroup(StringMaster.CUSTOM, true);

        hero.getDeity().applyHeroBonuses(hero);

//        BattleCraft.chooseMasteryGroups(hero);TODO

        DataManager.addType(hero.getType());
        // if (
        // CharacterCreator.save(hero.getType());

        return true;
    }

    public static boolean isLevelUpEnabled(Unit hero) {
        if (hero.getGame().getGameType() == GAME_TYPE.SKIRMISH
         || hero.getGame().getGameType() == GAME_TYPE.SCENARIO) {
//            if (hero.getLevel() >= ScenarioPrecombatMaster.getScenario().getMaxHeroLevel()) {
//                return false;
//            }
        }
        return true;
    }

    public static ObjType selectHero() {
        return getHeroCreator().chooseBaseType();
    }


    public enum HC_MODE {

    }

}
