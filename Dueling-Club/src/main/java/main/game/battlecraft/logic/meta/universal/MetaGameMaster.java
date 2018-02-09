package main.game.battlecraft.logic.meta.universal;

import main.entity.DataModel;
import main.game.battlecraft.logic.battle.universal.BattleMaster;
import main.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueActorMaster;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueManager;
import main.game.battlecraft.logic.meta.scenario.dialogue.intro.IntroFactory;
import main.game.bf.Coordinates;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.libgdx.anims.AnimMaster;
import main.system.GuiEventManager;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;

/**
 * Created by JustMe on 5/7/2017.
 */
public abstract class MetaGameMaster<E extends MetaGame> {

    protected String data;
    protected PartyManager<E> partyManager;
    protected MetaInitializer<E> initializer;
    protected ShopManager<E> shopManager;
    protected MetaDataManager<E> metaDataManager;
    protected DialogueFactory dialogueFactory;
    protected IntroFactory introFactory;

    protected E metaGame;
    protected DC_Game game; //<? extends DC_Game>
    DialogueManager dialogueManager;
    DialogueActorMaster dialogueActorMaster;
    private DataModel entity;
    //    PrecombatManager<E> precombatManager;
//    AfterCombatManager<E> afterCombatManager;

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


    //from data? if save
    protected abstract DC_Game createGame();

    protected abstract PartyManager<E> createPartyManager();

    protected abstract MetaDataManager<E> createMetaDataManager();

    protected abstract ShopManager<E> createShopManager();

    public DialogueActorMaster getDialogueActorMaster() {
        return dialogueActorMaster;
    }

    protected abstract MetaInitializer<E> createMetaInitializer();

    public void initHandlers() {
        partyManager = createPartyManager();
        initializer = createMetaInitializer();
        shopManager = createShopManager();
        metaDataManager = createMetaDataManager();
        dialogueFactory = createDialogueFactory();
        introFactory = createIntroFactory();
        dialogueManager = new DialogueManager(this);
        dialogueActorMaster = new DialogueActorMaster(this);
    }

    public void init() {
//        shopManager.init();
//        metaDataManager.init();
        game = Eidolons.game;
        if (game == null)
            game = createGame();
        else
            game.setMetaMaster(this);
        metaGame = initializer.initMetaGame(data);
        preStart();
        if (partyManager.initPlayerParty() != null) {
            if (getBattleMaster().getOptionManager().chooseDifficulty())
                return;
        }
        Eidolons.getMainGame().setAborted(true);

    }

    public void preStart() {
        partyManager.preStart();
        getBattleMaster().getOptionManager().selectDifficulty();
//        getGame().getDataKeeper().setDungeonData(new DungeonData(getMetaGame()));

    }

    public void gameStarted() {
        partyManager.gameStarted();
//   TODO remove lazy init hack?
//     getDialogueFactory().init(this);
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

    public ShopManager<E> getShopManager() {
        return shopManager;
    }

    public MetaDataManager<E> getMetaDataManager() {
        return metaDataManager;
    }

    public DataModel getEntity() {
        return entity;
    }

    public void next(Boolean outcome) {

        String
         message = (outcome != null) ? "next level!" : "game restarted!";
        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN, message);


        gameExited();
        game.reinit(outcome == null);
        //or selective clear() - removeIf() ...
//        for (Unit hero : getPartyManager().getParty().getMembers()) {
//            for (ActiveObj activeObj : hero.getActives()) {
//                game.getState().addObject((Obj) activeObj);
//            }
//            for (ActiveObj activeObj : hero.getstattaiteActives()) {
//            }
//        }

    }

    public void gameExited() {

//        try {
//            DungeonScreen.getInstance().dispose();
//        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
        //TODO !!!

//        game.getMaster().clear();
//        game.getStateManager().clear();
        try {
            GuiEventManager.cleanUp();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        AnimMaster.getInstance().cleanUp();
        Coordinates.clearCaches();

    }
}
