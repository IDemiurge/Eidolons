package main.client.net;

import main.system.net.RefresherImpl;
import main.system.net.RefresherImpl.REFRESHER_TYPE;
import main.system.net.socket.ServerConnection;
import main.system.net.socket.ServerConnector;
import main.system.net.socket.ServerConnector.CODES;
import main.system.net.socket.ServerConnector.NetCode;

import java.net.Socket;

public class DC_ServerConnectionHandler extends ServerConnection {

    public DC_ServerConnectionHandler(Socket socket1) {
        super(socket1);

    }

    public DC_ServerConnectionHandler() {

    }

    @Override
    public void handleInput(String input) {
        System.out.println(input + getLastReceivedCode().name());
        if (getLastReceivedCode() != null)
            switch ((CODES) getLastReceivedCode()) {
                case USER_VALIDATION: {

                    ServerConnector.handleUserCheckResult(input);
                    break;
                }

                case USER_DATA_REQUEST: {
                    CONCURRENT_INPUT(input);
                    break;
                }
                case USER_ACTIVATION_CODE: {

                    CONCURRENT_INPUT(input);
                    break;
                }
                case REFRESH_GAME_LIST: {
                    CONCURRENT_INPUT(input);
                    break;
                }
                case ONLINE_USERS_LIST: {
                    CONCURRENT_INPUT(input);
                    break;
                }
                case NEW_GAME: {
                    // game data from server?
                    CONCURRENT_INPUT(input);

                    break;
                }

                default:
                    break;
            }

    }

    @Override
    public void handleInputCode(NetCode code) {
        switch ((CODES) code) {

            case PING: {
                send(CODES.PING);
                this.pinged = true;
                break;
            }
            case GAME_LIST_CHANGED: {

                RefresherImpl.getSwitchers().put(REFRESHER_TYPE.GAMELIST, true);
                break;
            }
            case USER_REGISTRATION_SUCCESS: {
                // Menu.welcomeNewUser();
                break;
            }
            case USER_REGISTRATION_FAILURE: {
                // Menu.userRegistrationFailed();
                break;
            }
            case USER_LIST_CHANGED: {
                RefresherImpl.getSwitchers().put(REFRESHER_TYPE.USERLIST, true);
                break;
            }
            case NEW_GAME: {
                // waiting ends

                break;
            }
            default:
                break;
        }
    }

}
