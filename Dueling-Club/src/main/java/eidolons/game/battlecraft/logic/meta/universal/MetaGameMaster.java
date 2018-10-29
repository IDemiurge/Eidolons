package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.Simulation;
import eidolons.game.battlecraft.logic.battle.universal.BattleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
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
import eidolons.libgdx.anims.AnimMaster;
import eidolons.macro.AdventureInitializer;
import eidolons.macro.global.persist.Loader;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
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
    protected DialogueFactory dialogueFactory;
    protected IntroFactory introFactory;

    protected E metaGame;
    protected DC_Game game;
    protected DialogueManager dialogueManager;
    protected DialogueActorMaster dialogueActorMaster;
    protected  TownMaster townMaster;


    public MetaGameMaster(String data) {
        this.data = data;
        initHandlers();
    }

    protected IntroFactory createIntroFactory() {
        return new IntroFactory();
    }

    protected DialogueFactory createDialogueFactory() {
        return new DialogueFactory();
    }

    protected abstract DC_Game createGame();

    protected abstract PartyManager<E> createPartyManager();

    protected abstract MetaDataManager<E> createMetaDataManager();

    public DialogueActorMaster getDialogueActorMaster() {
        return dialogueActorMaster;
    }

    protected abstract MetaInitializer<E> createMetaInitializer();

    public void initHandlers() {
        partyManager = createPartyManager();
        initializer = createMetaInitializer();
        metaDataManager = createMetaDataManager();

        dialogueFactory = createDialogueFactory();
        introFactory = createIntroFactory();
        dialogueManager = new DialogueManager(this);
        dialogueActorMaster = new DialogueActorMaster(this);

        townMaster = new TownMaster(this);// createTownMaster();
    }

    public void init() {
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
                    return;
                }
            } else if (isQuestsEnabled())
                if (!getQuestMaster().initQuests()) {
                    Eidolons.getMainGame().setAborted(true);
                    return;
                }
            if (!getBattleMaster().getOptionManager().chooseDifficulty(getMetaGame().isDifficultyReset()))
                Eidolons.getMainGame().setAborted(true);
        }

    }

    private boolean isTownEnabled() {
        if (CoreEngine.isMacro()) {
            return false;
        }
        if (!game.getMetaMaster().isRngDungeon())
            return false;
        return true;
    }
    private boolean isQuestsEnabled() {
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
        getBattleMaster().getOptionManager().selectDifficulty();
        //        getGame().getDataKeeper().setDungeonData(new DungeonData(getMetaGame()));

    }

    public void gameStarted() {
        partyManager.gameStarted();
        //   TODO remove lazy init hack?
        //        getDialogueFactory().init(this);
        //        getIntroFactory().init(this);
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
        return dialogueFactory;
    }

    public IntroFactory getIntroFactory() {
        return introFactory;
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
        try {
            GuiEventManager.cleanUp();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        AnimMaster.getInstance().cleanUp();
        Coordinates.clearCaches();

    }


    public boolean isRngDungeon() {
        ObjType type = DataManager.getType(getData(), DC_TYPE.SCENARIOS);
        if (type != null) {
            return
             type.getGroup().equalsIgnoreCase("Random");
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
}
