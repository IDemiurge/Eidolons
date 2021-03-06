package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.content.PROPS;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.module.ModuleMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Floor;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.game.netherflame.main.death.ShadowMaster;
import eidolons.game.eidolon.event.GameEventHandler;
import eidolons.system.libgdx.GdxBeans;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.ExceptionMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.Flags;

/**
 * Created by JustMe on 5/7/2017.
 * <p>
 * Does everything TO the *DC_Game It kind of "owns" the Game
 */
public abstract class MetaGameMaster<E extends MetaGame> {

    private static final String SOLO_LEVEL = "crawl/a flight[new].xml"; //review this
    protected String data;
    protected PartyManager partyManager;
    protected MetaInitializer<E> initializer;
    protected MetaDataManager<E> metaDataManager;

    protected E metaGame;
    protected DC_Game game;
    protected DialogueManager dialogueManager;
    protected DefeatHandler defeatHandler;
    protected LootMaster<E> lootMaster;

    protected GameEventHandler eventHandler;

    ShadowMaster shadowMaster = new ShadowMaster(this);
    private GdxBeans gdxBeans;
    private QuestMaster questMaster;

    public ShadowMaster getShadowMaster() {
        return shadowMaster;
    }

    public MetaGameMaster(String data) {
        this.data = data;
        initHandlers();
    }


    protected abstract DC_Game createGame();

    protected abstract PartyManager createPartyManager();

    protected abstract MetaDataManager createMetaDataManager();

    public DialogueActorMaster getDialogueActorMaster() {
        return dialogueManager.getDialogueActorMaster();
    }

    protected abstract MetaInitializer createMetaInitializer();

    public void initHandlers() {
        partyManager = createPartyManager();
        initializer = createMetaInitializer();
        metaDataManager = createMetaDataManager();
        // VisualChoiceHandler choiceHandler = new VisualChoiceHandler(this);

        if (Flags.isCombatGame()) {
            lootMaster = createLootMaster();
            questMaster = createQuestMaster();
            eventHandler = createEventHandler( );
            defeatHandler = createDefeatHandler();
            dialogueManager = new DialogueManager(this);
            if (dialogueManager.isPreloadDialogues()) {
                getDialogueFactory().init(this);
            }
        }
    }
    protected QuestMaster createQuestMaster() {
        return new QuestMaster(this);
    }
    protected GameEventHandler createEventHandler() {
        return null;
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
            // Simulation.init(false, this);
            game = createGame();
            // Simulation.setRealGame(game);
        }
        game.setMetaMaster(this);
        gdxBeans = Eidolons.getGdxBeansProvider().get();
        metaGame = initializer.initMetaGame(data);
        preStart();

        if (partyManager.initPlayerParty() != null) {
            if (isTownEnabled()) {
                getMetaDataManager().initData();
                getMissionMaster().getConstructor().init();
            } else if (isRngQuestsEnabled() || isCustomQuestsEnabled())
                if (!getQuestMaster().initQuests()) {
                    Eidolons.getMainGame().setAborted(true);
                    return game;
                }
            if (!getMissionMaster().getOptionManager().chooseDifficulty(
                    getMetaGame().isDifficultyReset()))
                Eidolons.getMainGame().setAborted(true);
        }
        return game;
    }

    public boolean isCustomQuestsEnabled() {
        return false;
    }

    protected boolean isTownEnabled() {
        if (EidolonsGame.TOWN)
            return true;
        if (Flags.isFullFastMode()) {
            return false;
        }
        if (Flags.isMacro()) {
            return false;
        }
        return game.getMetaMaster().isRngDungeon() || !Flags.isSafeMode();
    }

    public boolean isRngQuestsEnabled() {
        if (Flags.isFullFastMode()) {
            return false;
        }
        if (!QuestMaster.ON)
            return false;
        if (!isRngDungeon())
            return false;
        return !Flags.isMacro();
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

    public LootMaster getLootMaster() {
        return lootMaster;
    }

    public DungeonMaster getDungeonMaster() {
        return game.getDungeonMaster();
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public MissionMaster getMissionMaster() {
        return game.getMissionMaster();
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

    public MetaGame getMetaGame() {
        return metaGame;
    }

    public DC_Game getGame() {
        return game;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public MetaInitializer getInitializer() {
        return initializer;
    }

    public MetaDataManager getMetaDataManager() {
        return metaDataManager;
    }

    public void next(Boolean outcome) {

        String message = (outcome != null) ? "next level!" : "game restarted!";
        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN, message);

        gameExited();
        game.reinit(outcome == null);

    }

    public void gameExited() {
        PartyManager.setSelectedHero(null);
        ShadowMaster.reset();
        try {
            GuiEventManager.cleanUp();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        if (game.isStarted())
            //TODO gdx sync
            // try {
            //     AnimMaster.getInstance().getDrawer().cleanUp();
            // } catch (Exception e) {
            //     main.system.ExceptionMaster.printStackTrace(e);
            // }

        FileLogManager.writeStatInfo(game.getMissionMaster().getStatManager().getStats().toString());
    }


    public boolean isRngDungeon() {
        ObjType type = DataManager.getType(getData(), DC_TYPE.SCENARIOS);
        if (type != null) {
            if (type.getGroup().equalsIgnoreCase("Demo")) {
                if (type.checkProperty(PROPS.SCENARIO_TYPE, "Custom")) {
                    return false;
                }
                return !type.checkProperty(PROPS.SCENARIO_TYPE, "Boss");
            }

            return
                    type.getGroup().equalsIgnoreCase("Random");
        }
        type = DataManager.getType(getData(), DC_TYPE.FLOORS);

        if (type != null) {
            if (type.getName().toLowerCase().contains("boss")) {
                return false;
            }
            return !type.getGroup().equalsIgnoreCase("Tutorial");
        }
        //        getMetaGame().isRestarted()
        return false;
    }

    public String getDungeonInfo() {
        return getScenarioInfo();
    }

    public DefeatHandler getDefeatHandler() {
        return defeatHandler;
    }

    protected String getScenarioInfo() {
        return "No info!";
    }

    public QuestMaster getQuestMaster() {
        return questMaster ;
    }

    public void reinit() {
        getQuestMaster().startQuests();
    }

    public GameEventHandler getEventHandler() {
        return eventHandler;
    }

    public boolean isAlliesSupported() {
        return false;
        //!OptionsMaster.getGameplayOptions().getBooleanValue(GameplayOptions.GAMEPLAY_OPTION.MANUAL_CONTROL);
    }

    public ModuleMaster getModuleMaster() {
        return null;
    }

    public Floor getFloor() {
        return getMissionMaster().getFloor();
    }

    public boolean isSoloLevel() {
        return  data.equalsIgnoreCase(SOLO_LEVEL);
    }

    public GdxBeans getGdxBeans() {
        if (gdxBeans == null) {
            gdxBeans = Eidolons.getGdxBeansProvider().get();
        }
        return gdxBeans;
    }

    public void setGdxBeans(GdxBeans gdxBeans) {
        this.gdxBeans = gdxBeans;
    }
}
