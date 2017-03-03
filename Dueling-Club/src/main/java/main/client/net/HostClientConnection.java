package main.client.net;

import main.client.DuelingClub;
import main.client.game.TestMode;
import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.game.logic.arena.UnitGroupMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.Err;
import main.system.net.Communicator.COMMAND;
import main.system.net.RefresherImpl;
import main.system.net.RefresherImpl.REFRESHER_TYPE;
import main.system.net.Viewer;
import main.system.net.WaitingThread;
import main.system.net.socket.GenericConnection;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.NetCode;
import main.system.net.user.User;
import main.test.PresetMaster;
import main.test.frontend.FAST_DC;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

public class HostClientConnection extends GenericConnection {
    DocumentBuilder builder;
    private boolean isHost;
    private User user;
    private GameConnector gameConnector;
    private Viewer viewer;

    // depending on whether you are host or client, system must provide
    // user info for you (REQUEST USER DATA)
    public HostClientConnection(Socket socket, GameConnector gameConnector) {
        super(socket, HOST_CLIENT_CODES.class);
        if (DuelingClub.TEST_MODE) {
            setUser(TestMode.getClientUser());
        }

        this.gameConnector = gameConnector;
        gameConnector.setConnection(this);
        isHost = false;
    }

    public HostClientConnection(boolean isHost, Socket socket, GameConnector gameConnector) {
        super(socket, HOST_CLIENT_CODES.class);
        if (DuelingClub.TEST_MODE) {
            setUser(TestMode.getHostUser());
        }
        this.isHost = isHost;
        this.gameConnector = gameConnector;
        gameConnector.setConnection(this);
        send(HOST_CLIENT_CODES.NEW_USER_DATA_REQUEST);
        if (new WaitingThread(HOST_CLIENT_CODES.NEW_USER_DATA_REQUEST).waitForInput()) {
            setUser(new User(WaitingThread.getINPUT(HOST_CLIENT_CODES.NEW_USER_DATA_REQUEST)));

            send(HOST_CLIENT_CODES.GAME_JOINED);
        } else {
            Err.error("DIDN'T RECEIVE USER DATA FROM CONNECTED CLIENT!!!");

            send(HOST_CLIENT_CODES.GAME_JOIN_FAILED);
        }
    }

    @Override
    public void handleInput(String input) {
        if (viewer != null) {
            viewer.input(input);
        }
        CONCURRENT_INPUT(input);

    }

    public String sendAndWaitForResponse(HOST_CLIENT_CODES code, String data) {
        send(code, data);
        WaitingThread waitingThread = new WaitingThread(code);
        if (!waitingThread.waitForInput()) {
            return null;
        }
        return waitingThread.getInput();
    }

    @Override
    public void handleInputCode(NetCode code) {
        if (viewer != null) {
            viewer.input("Code: " + code.name());
        }

        if (new EnumMaster<COMMAND>().retrieveEnumConst(COMMAND.class, code.name()) != null) {
            gameConnector.awaitCommand(code);
            return;
        }
        switch ((HOST_CLIENT_CODES) code) {
            // case COMMAND: {
            // gameConnector.awaitCommand();
            // break;
            // }
            case FACING_MAP:
                if (isHost) {
                    gameConnector.getConnection().send(
                            HOST_CLIENT_CODES.FACING_MAP,
                            MapMaster.getNetStringForMap(gameConnector.getGame().getArenaManager()
                                    .getSpawnManager().getMultiplayerFacingMap()));
                }
                break;
            case POWER_LEVEL: {
                if (isHost) {
                    if (UnitGroupMaster.isFactionMode()) {
                        Integer powerLevel = UnitGroupMaster.getPowerLevel();
                        if (UnitGroupMaster.isFactionLeaderRequired()) {
                            powerLevel += UnitGroupMaster.LEADER_REQUIRED;
                        }
                        send(code, "" + powerLevel);
                        return;
                    } else {
                        send(code, "-1");
                    }
                } else {
                    // send(code, "" + UnitGroupMaster.getMyGroup());
                }
                break;
            }
            case GAME_DATA_REQUEST:
                if (isHost) {
                    if (PresetMaster.getPreset() == null) {
                        if (FAST_DC.SUPER_FAST_MODE) {
                            send(code, "1");
                        } else if (FAST_DC.FAST_MODE) {
                            send(code, "2");
                        } else {
                            // TODO
                        }
                    } else {
                        send(code, PresetMaster.getPreset().getData());
                    }
                }
                break;

            case CLIENT_READY: {
                setLastReceivedCode(code);
                wakeUp(code);
                return;
            }
            case HOST_READY: {
                if (!gameConnector.isHost()) {
                    gameConnector.getHostedGame().getLobby().getGameStarter().clientInit();
                }
                break;
            }
            case GAME_STARTED: {
                if (!gameConnector.isHost()) {
                    gameConnector.getHostedGame().getLobby().getGameStarter().gameStarted();
                }
                break;
            }
            case HOST_PARTY_DATA: {
                if (!gameConnector.isHost()) {
                    break;
                }
                if (gameConnector.isReady()) // send error if not
                {
                    gameConnector.sendPartyData();
                }
                break;
            }

            case MAP_DATA_REQUEST: {
                if (!gameConnector.isHost()) {
                    break;
                }
                gameConnector.sendMapData();
                break;
            }
            case CLIENT_PARTY_DATA_REQUEST: {
                if (gameConnector.isHost()) {
                    break;
                }
                if (gameConnector.isReady()) // send ABORT error if not
                {
                    gameConnector.sendPartyData();
                }
                break;
            }
            case GAME_JOINED: {
                if (gameConnector.isHost()) {
                    break;
                }
                gameConnector.getClient().connect();
            }
            case NEW_USER_DATA_REQUEST: {
                if (gameConnector.isHost()) {
                    break;
                }
                // send(HOST_CLIENT_CODES.NEW_USER_DATA_REQUEST);
                send(HOST_CLIENT_CODES.NEW_USER_DATA_REQUEST + StringMaster.NETCODE_SEPARATOR
                        + getUser().getRelevantData());
                break;

            }
            case CHECK_READY: {
                if (!gameConnector.isHost()) {
                    gameConnector.checkReady(); // request host's game data
                }
                break;
            }

            case GAME_OPTIONS_REQUEST: {
                if (gameConnector.isHost()) {
                    send(code);
                    sendGameOptions();
                }

                break;
            }
            case HOST_USER_DATA_REQUEST: {
                if (gameConnector.isHost()) {
                    send(code);
                    sendMyUserData();
                }

                break;
            }
            case GAME_OPTIONS_CHANGED: {
                RefresherImpl.getSwitchers().put(REFRESHER_TYPE.GAME_OPTIONS, true);
                break;
            }
            case GAME_USERS_LIST_CHANGED: {
                RefresherImpl.getSwitchers().put(REFRESHER_TYPE.USERLIST_GAME, true);
                break;
            }
            // ??????
            default:
                break;
        }
        setLastReceivedCode(code);
    }

    private void wakeUp(NetCode code) {

        new Thread(new Runnable() {
            public void run() {
                registerCode();
            }
        }, " thread").start();
    }

    public void initViewer() {
        viewer = new Viewer(isHost ? "host" : "client", HOST_CLIENT_CODES.class, this);
        WaitingThread.setViewer(viewer);

        ServerConnector.setViewer(viewer);

    }

    @Override
    public void initIO() {
        if (!DuelingClub.TEST_MODE) {
            initViewer();
            super.initIO();
            viewer.addMessagePanel(out);
        } else {

            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        initViewer();
                    }
                });
                super.initIO();
                viewer.addMessagePanel(out);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void send(Object o) {
        super.send(o);
        monostring:
        if (viewer != null) {
            viewer.output(String.valueOf(o));
        }

    }

    private void sendMyUserData() {
        send(getUser().getRelevantData());
    }

    private void sendGameUsersList() {
        send(gameConnector.getPlayerData());
    }

    private void sendGameOptions() {
        // if (!DuelingClub.TEST_MODE)
        send(gameConnector.getHostedGame().getOptions().getData());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameConnector getGameConnector() {
        return gameConnector;
    }

    public void setGameConnector(GameConnector gameConnector) {
        this.gameConnector = gameConnector;
    }

}
