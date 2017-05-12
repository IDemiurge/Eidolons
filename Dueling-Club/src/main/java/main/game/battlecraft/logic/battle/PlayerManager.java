package main.game.battlecraft.logic.battle;

import main.game.battlecraft.logic.dungeon.UnitData;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.data.DataUnitFactory;
import main.system.data.PlayerData;
import main.system.data.PlayerData.ALLEGIENCE;
import main.system.data.PlayerData.PLAYER_VALUE;
import main.system.graphics.ColorManager.FLAG_COLOR;

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
        if (getMaster().getGame().getDataKeeper().getPlayerData() != null) {
            // TODO init data from preset
        }
        if (data == null) {
            data = generateDefaultPlayerData();
        }
        unusedPlayerColorsList = new ListMaster<FLAG_COLOR>()

                .getList(playerColors);
        int i = 0;
        for (String substring : StringMaster.openContainer(data)) {
            DC_Player player = initPlayerFromString(substring);
            if (player.getAllegiance() == ALLEGIENCE.NEUTRAL)
                Player.NEUTRAL = player;
//            else
                players.add(player);
if (player.isEnemy())
    player.setAi(true);

            UnitData[] unitData = getMaster().getGame().getDataKeeper().getUnitData();
            if (unitData != null)
                if (unitData.length > i) {
                    player.setUnitData(unitData[i]);
                }
                i++;
        }
        if (Player.NEUTRAL == null) {
            Player.NEUTRAL = new DC_Player("Neutral", FLAG_COLOR.BROWN, "", "", ALLEGIENCE.NEUTRAL);
            DC_Player.NEUTRAL = (DC_Player) Player.NEUTRAL;
            players.add(DC_Player.NEUTRAL);
        }
    }


        private String generateDefaultPlayerData() {
        String data = "";
//        emblem = ImageManager.getEmptyEmblem()
        DataUnitFactory<PlayerData> factory = new DataUnitFactory<>(PlayerData.FORMAT);
        PLAYER_VALUE[] default_values = {
         PLAYER_VALUE.NAME, PLAYER_VALUE.COLOR, PLAYER_VALUE.EMBLEM, PLAYER_VALUE.PORTRAIT, PLAYER_VALUE.ALLEGIENCE
        };
        factory.setValueNames(default_values);
        factory.setValues("You", "Blue", " ", " ", "Player");
        data += factory.constructDataString() + StringMaster.SEPARATOR;

        factory.setValueNames(default_values);
        factory.setValues("Enemy", "Red", " ", " ", "Enemy");
        data += factory.constructDataString();

        return data;
    }

    public DC_Player initPlayerFromString(String data) {
        PlayerData dataUnit = new PlayerData(data);
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
        DC_Player player = new DC_Player(dataUnit.getValue(PLAYER_VALUE.NAME), color,

                dataUnit.getValue(PLAYER_VALUE.EMBLEM), dataUnit.getValue(PLAYER_VALUE.PORTRAIT), allegience);


        player.setMainHeroName(dataUnit.getValue(PLAYER_VALUE.MAIN_HERO) );

        return player;
    }


    public void setData(String data) {
        this.data = data;
    }


    private FLAG_COLOR getRandomColorFlag() {
        int index = RandomWizard.getRandomListIndex(unusedPlayerColorsList);
        return unusedPlayerColorsList.remove(index);
    }

    public DC_Player getPlayer(String name) {
        for (DC_Player player : players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
        public DC_Player getPlayer(boolean me) {
        for (DC_Player player: players){
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


    // public void addPlayer(String data) {
    // new DC_Player(name, false, party);
    // }


}
