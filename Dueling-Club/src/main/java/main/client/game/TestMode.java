package main.client.game;

import main.client.game.logic.PrecombatData;
import main.client.game.logic.PrecombatData.PRECOMBAT_VALUES;
import main.game.logic.battle.player.Player;
import main.system.net.user.User;

public class TestMode {

    private static final User CLIENT_USER = new User(false);
    private static final User HOST_USER = new User(true);
    protected static NetGame hostGame;
    protected static NetGame clientGame;

    public static NetGame launchThis(boolean host_client) {
        if (host_client) {
            hostGame = new NetGame(true);
        } else {
            clientGame = new NetGame(false);
        }

        if (host_client) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    hostGame.init();

                }
            }, "HOST").start();
        } else {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    clientGame.init();
                }
            }, "CLIENT").start();
        }

        return host_client ? hostGame : clientGame;
    }

    public static NetGame launch(Boolean host_client_both) {
        if (host_client_both == null) {
            launchThis(true);
            launchThis(false);
            return hostGame;
        } else {
            if (host_client_both) {
                return launchThis(host_client_both);
            }

            clientGame = new NetGame(false);
            clientGame.init();
            return clientGame;
        }

        //
    }

    public static String getTestData(Player player) {
        PrecombatData data = new PrecombatData("");
        // Obj hero = player.getHeroObj();
        data.setValue(PRECOMBAT_VALUES.MEMORIZED_SPELLS, "Shadow Bolt");
        // getMemorizedSpells(hero));

        return data.getData();
    }

    public static User getClientUser() {
        return CLIENT_USER;
    }

    public static User getHostUser() {
        return HOST_USER;
    }

    public static NetGame getHostGame() {
        return hostGame;
    }

    public static NetGame getClientGame() {
        return clientGame;
    }
}
