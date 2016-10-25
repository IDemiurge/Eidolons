package main.system.net;

import main.client.net.GameConnector.HOST_CLIENT_CODES;
import main.game.DC_Game;
import main.system.auxiliary.StringMaster;

import java.util.Random;

public class NetRandom extends Random {

    HOST_CLIENT_CODES code = HOST_CLIENT_CODES.RANDOM;
    private DC_Game game;

    public NetRandom(DC_Game game) {
        this.game = game;

    }

    @Override
    public int nextInt(int n) {
        if (game.getManager().getActiveObj() != null) {
            if (!game.getManager().getActiveObj().isMine()) {
                new WaitingThread(code).waitForInput();
                String input = WaitingThread.getINPUT(code);
                return StringMaster.getInteger(input);
            }
        }
        int nextInt = super.nextInt(n);
        game.getConnection().send(HOST_CLIENT_CODES.RANDOM, nextInt + "");
        return nextInt;
    }
}
