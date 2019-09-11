package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PROPS;
import eidolons.game.Simulation;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.igg.death.ShadowMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.GameEventHandler;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.global.persist.Loader;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;

import static main.system.auxiliary.StringMaster.getWellFormattedString;
import static main.system.auxiliary.StringMaster.wrapInParenthesis;

/**
 * Created by JustMe on 5/7/2017.
 * <p>
 * Does everything TO the *DC_Game
 * It kind of "owns" the Game
 */
public abstract class MetaGameMaster<E extends MetaGame> {

    protected String data;
    protected PartyManager<E> partyManager;
    protected MetaInitializer<E> initializer;
    protected MetaDataManager<E> metaDataManager;

    protected E metaGame;
    protected DC_Game game;
    protected DialogueManager dialogueManager;
    protected TownMaster townMaster;
    protected DefeatHandler defeatHandler;
    protected LootMaster<E> lootMaster;

    protected GameEventHandler eventHandler;
    protected ModuleMaster moduleMaster;

    public MetaGameMaster(String data) {
        this.data = data;
        initHandlers();
    }


    protected abstract DC_Game createGame();

    protected abstract PartyManager<E> createPartyManager();

    protected abstract MetaDataManager<E> createMetaDataManager();

    public DialogueActorMaster getDialogueActorMaster() {
        return dialogueManager.getDialogueActorMaster();
    }

    protected abstract MetaInitializer<E> createMetaInitializer();

    public void initHandlers() {
        moduleMaster = new ModuleMaster(this);
        eventHandler = new GameEventHandler(this);
        defeatHandler = createDefeatHandler();
        partyManager = createPartyManager();
        lootMaster = createLootMaster();
        initializer = createMetaInitializer();
        metaDataManager = createMetaDataManager();


        townMaster = createTownMaster();
        dialogueManager = new DialogueManager(this);
        if (dialogueManager.isPreloadDialogues()) {
            getDialogueFactory().init(this);
        }
    }

    protected TownMaster createTownMaster() {
        return new TownMaster(this);
    }

    protected LootMaster<E> createLootMaster() {
        return new LootMaster<>(this);
    }

    protected DefeatHandler createDefeatHandler() {
        return new DefeatHandler(this);
    }

    public DC_Game init() {
        game = Eidolons.game;
        if (game == null) {
            Simulation.init(false, this);
            game = createGame();
            Simulation.setRealGame(game);
        } else {
            game.setMetaMaster(this);
        }

        metaGame = initializer.initMetaGame(data);
        preStart();

        if (AdventureInitializer.isLoad())
            Loader.loadCharacters();
        if (partyManager.initPlayerParty() != null) {
            if (isTownEnabled()) {
                if (!getTownMaster().initTownPhase()) {
                    Eidolons.getMainGame().setAborted(true);
                    return game;
                }
            } else if (isRngQuestsEnabled() || isCustomQuestsEnabled())
                if (!getQuestMaster().initQuests()) {
                    Eidolons.getMainGame().setAborted(true);
                    return game;
                }
            if (!getBattleMaster().getOptionManager().chooseDifficulty(
                    getMetaGame().isDifficultyReset()))
                Eidolons.getMainGame().setAborted(true);
        }
        return game;
    }

    public boolean isCustomQuestsEnabled() {
        return false;
    }

    protected boolean isTownEnabled() {
        if (CoreEngine.isFullFastMode()) {
            return false;
        }
        if (CoreEngine.isMacro()) {
            return false;
        }
        if (!game.getMetaMaster().isRngDungeon() && CoreEngine.isSafeMode())
            return false;
        return true;
    }

    public boolean isRngQuestsEnabled() {
        if (CoreEngine.isFullFastMode()) {
            return false;
        }
        if (!QuestMaster.ON)
            return false;
        if (!isRngDungeon())
            return false;
        if (CoreEngine.isMacro()) {
            return false;
        }
        return true;
    }

    public void preStart() {
        partyManager.preStart();
        //        getBattleMaster().getOptionManager().selectDifficulty();
        //        getGame().getDataKeeper().setDungeonData(new DungeonData(getMetaGame()));

    }

    public void gameStarted() {
        partyManager.gameStarted();
        //   TODO remove lazy init hack?
        //        getDialogueFactory().init(this);
        //        getIntroFactory().init(this);
    }

    public LootMaster<E> getLootMaster() {
        return lootMaster;
    }

    public DungeonMaster getDungeonMaster() {
        return game.getDungeonMaster();
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public BattleMaster getBattleMaster() {
        return game.getBattleMaster();
    }

    public DialogueFactory getDialogueFactory() {
        return dialogueManager.getDialogueFactory();
    }

    public IntroFactory getIntroFactory() {
        return dialogueManager.getIntroFactory();
    }

    public String getData() {
        return data;
    }

    public E getMetaGame() {
        return metaGame;
    }

    public DC_Game getGame() {
        return game;
    }

    public PartyManager<E> getPartyManager() {
        return partyManager;
    }

    public MetaInitializer<E> getInitializer() {
        return initializer;
    }

    public MetaDataManager<E> getMetaDataManager() {
        return metaDataManager;
    }

    public void next(Boolean outcome) {

        String message = (outcome != null) ? "next level!" : "game restarted!";
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);

        gameExited();
        game.reinit(outcome == null);

    }

    public void gameExited() {
        PartyManager.setSelectedHero(null);
        ShadowMaster.reset();
        try {
            GuiEventManager.cleanUp();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (game.isStarted())
            try {
                AnimMaster.getInstance().getDrawer().cleanUp();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        Coordinates.clearCaches();

        FileLogManager.writeStatInfo(game.getBattleMaster().getStatManager().getStats().toString());
    }


    public boolean isRngDungeon() {
        ObjType type = DataManager.getType(getData(), DC_TYPE.SCENARIOS);
        if (type != null) {
            if (type.getGroup().equalsIgnoreCase("Demo")) {
                if (type.checkProperty(PROPS.SCENARIO_TYPE, "Custom")) {
                    return false;
                }
                if (type.checkProperty(PROPS.SCENARIO_TYPE, "Boss")) {
                    return false;
                }
                return true;
            }

            return
                    type.getGroup().equalsIgnoreCase("Random");
        }
        type = DataManager.getType(getData(), DC_TYPE.MISSIONS);

        if (type != null) {
            if (type.getName().toLowerCase().contains("boss")) {
                return false;
            }
            if (type.getGroup().equalsIgnoreCase("Tutorial")) {
                return false;
            }
            return true;
        }
        //        getMetaGame().isRestarted()
        return false;
    }

    public String getDungeonInfo() {
        if (getDungeonMaster().getDungeonLevel() != null) {
            StringBuilder info = new StringBuilder(200);
            DungeonLevel level = getDungeonMaster().getDungeonLevel();
            info.append("Randomly generated ");
            info.append(getWellFormattedString(level.getLocationType().toString()) +
                    " " + wrapInParenthesis(getWellFormattedString(level.getSublevelType().toString())) + "\n");

            LevelBlock block = level.getBlockForCoordinate(Eidolons.getMainHero().getCoordinates());
            LevelZone zone = block.getZone();

            info.append(getWellFormattedString(block.getRoomType().toString())
                    + " " + wrapInParenthesis(getWellFormattedString(zone.getStyle().toString())) + "\n");

            // objective?
            // units left?
            // secrets uncovered?
            //level of illumination, time of day,
            return info.toString();
        } else {
            return getScenarioInfo();
        }


    }

    public DefeatHandler getDefeatHandler() {
        return defeatHandler;
    }

    protected String getScenarioInfo() {
        return "No info!";
    }

    public QuestMaster getQuestMaster() {
        return townMaster.getQuestMaster();
    }

    public void reinit() {
        getQuestMaster().startQuests();
    }

    public TownMaster getTownMaster() {
        return townMaster;
    }

    public GameEventHandler getEventHandler() {
        return eventHandler;
    }

    public boolean isAlliesSupported() {
        return true;
        //!OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.MANUAL_CONTROL);
    }

    public ModuleMaster getModuleMaster() {
        return moduleMaster;
    }
}
