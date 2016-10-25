package main.client.game;

import main.client.DC_Engine;
import main.client.DuelingClub;
import main.client.game.gui.DC_GameGUI;
import main.client.game.options.GAME_OPTIONS;
import main.client.game.options.GameOptions;
import main.client.net.DC_ServerConnector;
import main.client.net.GameConnector;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_UnitObj;
import main.game.DC_Game;
import main.game.DC_GameData;
import main.game.HostedGame;
import main.game.battlefield.map.DC_Map;
import main.game.battlefield.map.DC_Map.MAP_TEMPLATE;
import main.game.event.MessageManager;
import main.game.player.Player;
import main.swing.builders.DC_Builder;
import main.swing.generic.components.editors.lists.ListChooser;
import main.system.auxiliary.StringMaster;
import main.system.net.WaitingThread;
import main.system.net.data.MapData;
import main.system.net.data.PartyData;
import main.system.net.data.PartyData.PARTY_VALUES;
import main.system.net.data.PlayerData;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.List;

@Deprecated

public class GameStarter {

    public static final int HERO_X1 = 4;
    public static final int HERO_Y1 = 4;

    public static final int HERO_Y2 = 0;
    public static final int HERO_X2 = 4;

    public static final int PRESET_HERO = 400;
    public static final int CUSTOM_HERO = 777;
    public static final int HERO_SELECTION_CANCELLED = -666;
    public static final String CUSTOM_HERO_IDENTIFIER = "CUSTOM_HERO";
    public static final String READY = "rdy";
    public static final int NOT_READY = -777;
    private static final String PLAYERS_NOT_READY = "PLAYERS_NOT_READY!";
    private static final String START_FAILED = "START_FAILED!";
    private static final long PRECOMBAT_TIME = 120000;
    String enemy_partydata = "";
    private DC_Game game;
    private Player player2;
    private DC_GameData gameData;
    private HostedGame hostedGame;
    private DC_GameGUI gui;
    private GameOptions options;
    private String myHeroData = "";
    private PartyData myPartyData = new PartyData();
    private PlayerData myPlayerData;
    private Player player1;
    private PlayerData playerData;
    private MapData mapData;
    private int n = 0;
    private PartyData enemyPartyData;
    private GameConnector connector;
    private PrecombatManager precombatManager;
    private boolean fullscreen = true;

    // public GameStarter(HostedGame hostedGame, String playerdata,
    // String partydata) {
    // // players should actually choose what to take into this round...
    // DC_Launcher.gameInit(true);
    // this.hostedGame = hostedGame;
    // this.setOptions(hostedGame.getOptions());
    // initGameData(playerdata, partydata);
    //
    // }

    public GameStarter(HostedGame hostedGame, GameConnector connector) {
        DC_Engine.gameInit();
        this.hostedGame = hostedGame;
        this.connector = connector;
        this.setOptions(hostedGame.getOptions());
        if (!hostedGame.isHost())
            setPlayerData(new PlayerData(hostedGame.getHost().getRelevantData()));
        if (DC_ServerConnector.getUser() != null)
            myPlayerData = new PlayerData(DC_ServerConnector.getUser().getRelevantData());
    }

    public static String selectHero(String res_level) {
        List<String> listData = DataManager.getHeroList(res_level);
        ListChooser lc = new ListChooser(listData, false, OBJ_TYPES.CHARS);
        lc.setColumns(3);
        return lc.getString();
    }

    public void init() {

        player1 = initPlayer(myPlayerData, myPartyData);
        player1.setMe(true);

    }

    public void finalInit(String partydata) {
        main.system.auxiliary.LogMaster.log(4, "initializing game data: " + partydata);
        enemyPartyData = new PartyData(partydata, true);

        player2 = initPlayer(getPlayerData(), enemyPartyData);
        if (player1 == null)
            player1 = initPlayer(myPlayerData, myPartyData);

        gameData = new DC_GameData(hostedGame.getTitle(), getMyPartyData(), enemyPartyData,
                getMyPlayer(), player2);

        initDC_Game();
    }

    private Player initPlayer(PlayerData playerData, PartyData partyData) {
        Player player = getNewPlayer(playerData);
        player.setHero_type(partyData.getHeroObjType());
        player.setAllegiance(player.getHeroObjType().getProperty(G_PROPS.ASPECT));
        player.setPartyData(partyData);
        return player;
    }

    public PartyData getMyPartyData() {
        if (myPartyData == null)
            myPartyData = new PartyData(getMyHeroData());
        return myPartyData;
    }

    private Player getNewPlayer(PlayerData playerdata) {

        Player player = new Player(playerdata);

        return player;
    }

    public Player getMyPlayer() {
        return player1;
    }

    public int chooseHero(GameOptions options) {
        // if (DuelingClub.TEST_MODE)
        this.myHeroData = selectHero(options.getValue(GAME_OPTIONS.RES_LEVEL));

        myPartyData.setValue(PARTY_VALUES.HERO_TYPE, myHeroData);
        if (myHeroData == null)
            return HERO_SELECTION_CANCELLED;
        if (myHeroData.equals(""))
            return HERO_SELECTION_CANCELLED;
        if (myHeroData.contains(CUSTOM_HERO_IDENTIFIER))
            return CUSTOM_HERO;
        return PRESET_HERO;
    }

    public void start() {
        startDC_Game();
        this.gui = new DC_GameGUI(game, fullscreen);
        gui.initGUI();
        game.getBattleField().refresh();
        connector.setCommunicator(game.getCommunicator());

    }

    private void startDC_Game() {
        game.start(hostedGame.isHost());
        hostedGame.setStarted(true);

    }

    public void initDC_Game() {

        game = new DC_Game(gameData.getPlayer1(), gameData.getPlayer2(), connector, hostedGame,
                myPartyData, enemyPartyData);

        // initParties();
        initHeroes(); // to battlefield?
        game.init();
        initMap();

    }

    private void initHeroes() {
        if (hostedGame.isHost()) {
            player1.setHeroObj(createHeroUnit(player1));
            player1.setPortrait(player1.getHeroObj().getIcon());
            player2.setHeroObj(createHeroUnit(player2));
            player2.setPortrait(player2.getHeroObj().getIcon());
        } else {
            player2.setHeroObj(createHeroUnit(player2));
            player2.setPortrait(player2.getHeroObj().getIcon());
            player1.setHeroObj(createHeroUnit(player1));
            player1.setPortrait(player1.getHeroObj().getIcon());

        }

    }

    private DC_UnitObj createHeroUnit(Player player) {
        return new DC_HeroObj(player.getHeroObjType(), (player == player1) ? HERO_X1 : HERO_X2,
                (player == player1) ? HERO_Y1 : HERO_Y2, player, game, new Ref());

    }

    private void initMap() {
        DC_Map map = null;
        if (getMapData() != null) {
            map = new DC_Map(game, getMapData());
        } else {
            map = new DC_Map(game, MAP_TEMPLATE.DUEL);
            setMapData(map.getMapData());
        }
        ((DC_Builder) game.getBattleField().getBuilder()).getGrid().setMap(map);
    }

    public String getMyHeroData() {
        if (myHeroData == null)
            myHeroData = DataManager.getRandomType(OBJ_TYPES.CHARS, null).getName();
        return myHeroData;
    }

    public GameOptions getOptions() {
        return options;
    }

    public void setOptions(GameOptions options) {
        this.options = options;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setMapData(MapData mapData) {
        this.mapData = mapData;
        main.system.auxiliary.LogMaster.log(4, "MAP_DATA: " + getMapData().getData());

    }


    public void initCommunicator(GameConnector gameConnector) {
        // TODO Auto-generated method stub

    }

    public String launchPrecombatPhase() {
        this.setPrecombatManager(new PrecombatManager(game));
        if (DuelingClub.PRESENTATION_MODE || DuelingClub.TEST_MODE)
            return TestMode.getTestData(player1);
        getPrecombatManager().launch();
        return WaitMaster.waitForInput(WAIT_OPERATIONS.PRECOMBAT).toString();
    }

    public void hostPrecombatMenuInit() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                setPrecombatManager(new PrecombatManager(game));
                getPrecombatManager().launch();
            }
        }).start();

    }

    public boolean hostPrecombatPhase() {
        if (!(DuelingClub.PRESENTATION_MODE || DuelingClub.TEST_MODE))
            hostPrecombatMenuInit();

        String clientPrecombatData = null;
        if (new WaitingThread(HOST_CLIENT_CODES.PRECOMBAT_DATA, PRECOMBAT_TIME).waitForInput())
            clientPrecombatData = WaitingThread.getINPUT(HOST_CLIENT_CODES.PRECOMBAT_DATA);

        if (clientPrecombatData.equals(PrecombatManager.ABORTED)) {
            MessageManager.alert(PLAYERS_NOT_READY);
            return false;
        }
        initEnemyPrecombatData(clientPrecombatData);
        String precombatData = TestMode.getTestData(getMyPlayer());
        if (!(DuelingClub.PRESENTATION_MODE || DuelingClub.TEST_MODE))
            precombatData = WaitMaster.waitForInput(WAIT_OPERATIONS.PRECOMBAT).toString();

        initMyPrecombatData(precombatData);
        connector.send(HOST_CLIENT_CODES.PRECOMBAT_DATA, precombatData);

        if (precombatData.equals(PrecombatManager.ABORTED))
            return false;
        return false;

    }

    public void clientInit() {

        init();

        connector.send(HOST_CLIENT_CODES.CLIENT_READY + StringMaster.NETCODE_SEPARATOR + READY);

        if (new WaitingThread(HOST_CLIENT_CODES.HOST_PARTY_DATA).waitForInput())
            enemy_partydata = WaitingThread.getINPUT(HOST_CLIENT_CODES.HOST_PARTY_DATA);

        connector.send(HOST_CLIENT_CODES.CLIENT_READY + StringMaster.NETCODE_SEPARATOR + READY);

        MapData mapData = null;
        if (new WaitingThread(HOST_CLIENT_CODES.MAP_DATA_REQUEST).waitForInput())
            mapData = new MapData(WaitingThread.getINPUT(HOST_CLIENT_CODES.MAP_DATA_REQUEST));

        setMapData(mapData);

        finalInit(enemy_partydata);
        if (!(DuelingClub.PRESENTATION_MODE || DuelingClub.TEST_MODE))
            clientPrecombatPhase();

        connector.send(HOST_CLIENT_CODES.CLIENT_READY, READY);
    }

    private void clientPrecombatPhase() {
        String precombatData = launchPrecombatPhase();
        connector.send(HOST_CLIENT_CODES.PRECOMBAT_DATA, precombatData);
        if (precombatData.equals(PrecombatManager.ABORTED)) {
            return;
        }
        initMyPrecombatData();
        connector.send(HOST_CLIENT_CODES.PRECOMBAT_DATA, precombatData);
        String hostPrecombatData = "";
        if (new WaitingThread(HOST_CLIENT_CODES.PRECOMBAT_DATA, PRECOMBAT_TIME).waitForInput())
            hostPrecombatData = WaitingThread.getINPUT(HOST_CLIENT_CODES.PRECOMBAT_DATA);

        if (hostPrecombatData.equals(PrecombatManager.ABORTED)) {
            MessageManager.alert("Host aborted!");
            return;
        }
        initEnemyPrecombatData(hostPrecombatData);
    }

    public void startButtonClicked() {
        if (!connector.isReady())
            if (!connector.promptReady())
                return;

        if (hostInit()) {
            gameStarted();
        } else {
            MessageManager.alert(START_FAILED);
        }
    }

    public void gameStarted() {
        if (connector.isHost())
            connector.send(HOST_CLIENT_CODES.GAME_STARTED);
        main.system.auxiliary.LogMaster.log(2, "GAME STARTED!");
        // Weaver.inNewThread(getGameStarter(), "start", null);
        start();
        initCommunicator(connector);
        hostedGame.getLobby().closeLobby();
    }

    public boolean hostInit() {
        connector.send(HOST_CLIENT_CODES.CHECK_READY);
        String result = "";
        if (new WaitingThread(HOST_CLIENT_CODES.CHECK_READY).waitForInput())
            result = WaitingThread.getINPUT(HOST_CLIENT_CODES.CHECK_READY);
        if (!result.equals(READY)) {
            MessageManager.alert(PLAYERS_NOT_READY);
            return false;
        }

        // add some space for selecting mercs and spells
        init();
        connector.send(HOST_CLIENT_CODES.HOST_READY);

        if (new WaitingThread(HOST_CLIENT_CODES.CLIENT_READY).waitForInput())
            result = WaitingThread.getINPUT(HOST_CLIENT_CODES.CLIENT_READY);
        if (!result.equals(READY)) {
            MessageManager.alert(PLAYERS_NOT_READY);
            return false;
        }
        connector.send(HOST_CLIENT_CODES.CLIENT_PARTY_DATA_REQUEST);
        if (new WaitingThread(HOST_CLIENT_CODES.CLIENT_PARTY_DATA_REQUEST).waitForInput())
            enemy_partydata = WaitingThread.getINPUT(HOST_CLIENT_CODES.CLIENT_PARTY_DATA_REQUEST);
        else {
            return false;
        }

        finalInit(enemy_partydata);

        connector.sendPartyData();

        if (new WaitingThread(HOST_CLIENT_CODES.CLIENT_READY).waitForInput())
            result = WaitingThread.getINPUT(HOST_CLIENT_CODES.CLIENT_READY);
        if (!result.equals(READY)) {
            MessageManager.alert(PLAYERS_NOT_READY);
            return false;
        }

        connector.sendMapData();
        if (!(DuelingClub.PRESENTATION_MODE || DuelingClub.TEST_MODE))
            hostPrecombatPhase();

        // "loading screen?" Waiting for the opponent?
        if (new WaitingThread(HOST_CLIENT_CODES.CLIENT_READY).waitForInput())
            result = WaitingThread.getINPUT(HOST_CLIENT_CODES.CLIENT_READY);
        if (!result.equals(READY)) {
            MessageManager.alert(PLAYERS_NOT_READY);
            return false;
        }

        return true;
    }

    public PrecombatManager getPrecombatManager() {
        return precombatManager;
    }

    public void setPrecombatManager(PrecombatManager precombatManager) {
        this.precombatManager = precombatManager;
    }

    public void initEnemyPrecombatData(String precombatData) {
        precombatManager.initPrecombatData(precombatData, player2);

    }

    public void initMyPrecombatData() {
        precombatManager.initMyPrecombatData();
    }

    public void initMyPrecombatData(String precombatData) {
        precombatManager.initPrecombatData(precombatData, player1);

    }
}
