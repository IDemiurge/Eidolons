package main.game.battlecraft.logic.battle;

import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.ColorManager.FLAG_COLOR;
import main.system.net.data.DataUnit;

import java.util.LinkedList;
import java.util.List;

public class PlayerManager<E extends Battle> extends BattleHandler<E> {

    public final FLAG_COLOR[] playerColors = {FLAG_COLOR.BLUE, FLAG_COLOR.RED,

            FLAG_COLOR.CYAN, FLAG_COLOR.PURPLE};
    public List<FLAG_COLOR> unusedPlayerColorsList;
    List<DC_Player> players = new LinkedList<>();
    private String data;

    public PlayerManager(BattleMaster<E> master) {
        super(master);


    }

    public void initializePlayers() {
        if (data == null) {
            data = generateDefaultPlayerData();
        }
        unusedPlayerColorsList = new ListMaster<FLAG_COLOR>()

                .getList(playerColors);
        for (String substring : StringMaster.openContainer(data)) {
            DC_Player player = initPlayerFromString(substring);
            if (player.getAllegiance()==ALLEGIENCE.NEUTRAL)
                Player.NEUTRAL =player;
            else
            players.add(player);

        }
        if (Player.NEUTRAL==null ){
            Player.NEUTRAL =new DC_Player("Neutral", FLAG_COLOR.BROWN,   "", "", ALLEGIENCE.NEUTRAL);
            DC_Player.NEUTRAL = (DC_Player) Player.NEUTRAL;
        }
    }



    private String generateDefaultPlayerData() {
        String data = "";
//        emblem = ImageManager.getEmptyEmblem()
            data += "You,Blue, , ,Player;";
            data += "Enemy,Red, , ,Enemy;";
        return data;
    }

    public DC_Player initPlayerFromString(String data) {
        DataUnit<PLAYER_VALUE> dataUnit = new DataUnit<PLAYER_VALUE>(data) {
            public Boolean getFormat() {
                return false; //',' separator!
            }
        };
        ALLEGIENCE allegience =

                new EnumMaster<ALLEGIENCE>().retrieveEnumConst(ALLEGIENCE.class,
                        dataUnit.getValue(PLAYER_VALUE.ALLEGIENCE));
        if (allegience == null) {
            allegience = ALLEGIENCE.NEUTRAL;
        }
        FLAG_COLOR color = new EnumMaster<FLAG_COLOR>().retrieveEnumConst(FLAG_COLOR.class,

                dataUnit.getValue(PLAYER_VALUE.COLOR));
        if (color == null) {
            color = getRandomColorFlag();
        }
        return new DC_Player(dataUnit.getValue(PLAYER_VALUE.NAME), color,

                dataUnit.getValue(PLAYER_VALUE.EMBLEM), dataUnit.getValue(PLAYER_VALUE.PORTRAIT), allegience);

    }


    public void setData(String data) {
        this.data = data;
    }


    private FLAG_COLOR getRandomColorFlag() {
        int index = RandomWizard.getRandomListIndex(unusedPlayerColorsList);
        return unusedPlayerColorsList.remove(index);
    }

    public DC_Player getPlayer(String name) {
        for (DC_Player player: players){
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }


    public DC_Player getPlayer(boolean me) {
        for (DC_Player player : players) {
            if (player.isMe())
                if (me)
                    return player;
            if (player.isEnemy())
                if (!me)
                    return player;

        }
        if (me) {
            return players.get(0);
        }
        return players.get(1);
    }

    public List<DC_Player> getPlayers() {
        return players;
    }

    public enum ALLEGIENCE {
        PLAYER, ALLY, ENEMY, NEUTRAL, PASSIVE;

        public boolean isNeutral() {
            switch (this) {
                case PASSIVE:
                case NEUTRAL:
                    return true;
            }
            return false;
        }

        public boolean isAi() {
            if (isMe())
                return false;
            return true;
        }

        public boolean isMe() {
            switch (this) {
                case PLAYER:
                    return true;
            }
            return false;
        }
    }

    // public void addPlayer(String data) {
    // new DC_Player(name, false, party);
    // }

    public enum PLAYER_VALUE {
        NAME, COLOR, EMBLEM, PORTRAIT, ALLEGIENCE,
    }

}
