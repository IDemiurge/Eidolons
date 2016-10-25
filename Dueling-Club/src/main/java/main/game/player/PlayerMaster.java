package main.game.player;

import main.client.net.HostClientConnection;
import main.game.DC_Game;
import main.system.auxiliary.ColorManager.FLAG_COLOR;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.RandomWizard;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlayerMaster {
    public static final FLAG_COLOR[] playerColors = {FLAG_COLOR.BLUE, FLAG_COLOR.RED,
            FLAG_COLOR.CYAN, FLAG_COLOR.PURPLE};
    public static final List<FLAG_COLOR> playerColorsList = new ListMaster<FLAG_COLOR>()
            .getList(playerColors);
    public static List<FLAG_COLOR> unusedPlayerColorsList = new ListMaster<FLAG_COLOR>()
            .getList(playerColors);
    DC_Player neutral;
    DC_Player me;
    List<DC_Player> players = new LinkedList<>();
    private DC_Game game;
    private FLAG_COLOR NEUTRAL_COLOR = FLAG_COLOR.BROWN;
    private boolean randomFlags = true;

    public PlayerMaster(DC_Game game, DC_Player... players) {
        this.game = game;
        this.players = new LinkedList<>(Arrays.asList(players));
    }

    public void initPlayerFlag(DC_Player player) {
        if (randomFlags)
            player.setFlagColor(getRandomColorFlag());
        else {
            // choose or get from data or sequential next()
        }
    }

    public void initPlayerFlags() {
        for (DC_Player player : players)
            initPlayerFlag(player);
        // neutral.setFlagColor(NEUTRAL_COLOR);
    }

    private FLAG_COLOR getRandomColorFlag() {
        int index = RandomWizard.getRandomListIndex(unusedPlayerColorsList);
        return unusedPlayerColorsList.remove(index);
    }

    // public void addPlayer(String data) {
    // new DC_Player(name, false, party);
    // }

    public DC_Player initPlayer(HostClientConnection hcc, String data) {
        String name = null;
        Color color = null;
        DC_Player player = new DC_Player(name, color, false, false, data);
        // from data? choose color?
        initPlayerFlag(player);
        return player;
    }

    public DC_Player getPlayer(boolean me) {
        if (me)
            return players.get(0);
        return players.get(1);
    }

}
