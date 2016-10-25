package main.system.net;

import main.system.net.socket.ServerConnector.NetCode;

public class Codes {

    public enum CHAT_CODES implements NetCode {
        ASSIGN_HANDLER,
        USER_NAME_REQUEST,
        MESSAGE,;

        @Override
        public boolean isInputIrrelevant() {
            return false;
        }

    }
}
