package main.game.core.game;

import main.entity.type.ObjType;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.battle.player.Player;
import main.system.net.data.PartyData;

import java.util.LinkedList;
import java.util.List;

public class DC_GameData {
List<Player> players;


    private static int i = 0;
    List<ObjType> playerParty = new LinkedList<>();
    List<ObjType> enemyParty = new LinkedList<>();
    private String playerUnitData;
    private String player2UnitData;
    private Player player2;
    private Player player1;
    private String name;
    private boolean simulation;

    public DC_GameData(String name, PartyData partyData, PartyData partyData2, Player p1, Player p2) {
        this.playerUnitData = partyData.getObjData();
        this.player2UnitData = partyData2.getObjData();
        player2 = p2;
        player1 = p1;
        this.name = name;
    }

    public DC_GameData(String name, String objData, String objData2, Player p1, Player p2) {
        this.playerUnitData = objData;
        this.player2UnitData = objData2;
        player2 = p2;
        player1 = p1;
        this.name = name;
    }

    public DC_GameData(String data) {
        simulation = true;
        name = "Simulation" + i;
        i++;
        playerUnitData = data;
        player2UnitData = data;
        player1 = new DC_Player("P1", null, true, "");
        player2 = new DC_Player("P2", null, false, "");
        Player.I = player1;
        Player.ENEMY = player2;
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

    public void addType(ObjType type, boolean me) {
        if (me) {
            playerParty.add(type);
        } else {
            enemyParty.add(type);
        }

    }

    public List<ObjType> getPlayerParty() {
        return playerParty;
    }

    public List<ObjType> getEnemyParty() {
        return enemyParty;
    }

    public String getPlayerUnitData() {
        return playerUnitData;
    }

    public void setPlayerUnitData(String playerUnitData) {
        this.playerUnitData = playerUnitData;
    }

    public String getPlayer2UnitData() {
        return player2UnitData;
    }

    public void setPlayer2UnitData(String player2UnitData) {
        this.player2UnitData = player2UnitData;
    }

}
