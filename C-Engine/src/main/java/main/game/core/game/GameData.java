package main.game.core.game;

import main.game.logic.battle.player.Player;

public class GameData {

    private String objData;
    private Player player2;
    private Player player1;
    private String name;
    private String objData2;

    public GameData(String name, String objData, String objData2, Player p1, Player p2) {
        this.objData = objData;
        this.objData2 = objData2;
        player2 = p2;
        player1 = p1;
        this.name = name;
    }

    public String getObjData() {
        return objData;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public String getName() {
        return name;
    }

    public String getObjData2() {
        return objData2;
    }

}
