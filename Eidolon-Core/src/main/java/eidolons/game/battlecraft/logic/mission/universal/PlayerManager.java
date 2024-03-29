package eidolons.game.battlecraft.logic.mission.universal;

import eidolons.game.battlecraft.logic.dungeon.universal.UnitsData;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.Strings;
import main.system.auxiliary.data.ListMaster;
import main.system.data.DataUnitFactory;
import main.system.data.PlayerData;
import main.system.data.PlayerData.ALLEGIENCE;
import main.system.data.PlayerData.PLAYER_VALUE;
import main.system.graphics.ColorManager.FLAG_COLOR;
import main.system.launch.Flags;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerManager<E extends DungeonSequence> extends MissionHandler<E> {

    private static final DC_Player DEFAULT_PLAYER = new DC_Player("Default Player", Color.red, true) ;
    public final FLAG_COLOR[] playerColors = {
//            FLAG_COLOR.BLUE, FLAG_COLOR.RED,
//     FLAG_COLOR.CYAN,
            FLAG_COLOR.PURPLE};
    public final FLAG_COLOR defaultPlayerColor =
            FLAG_COLOR.PURPLE;
    public final FLAG_COLOR defaultPlayerColorAlt =
            FLAG_COLOR.CYAN;
    public final FLAG_COLOR defaultEnemyColor =
            FLAG_COLOR.RED;
    public final FLAG_COLOR defaultEnemyColorAlt=
            FLAG_COLOR.NETHER;

    public final FLAG_COLOR[] enemyColors = {FLAG_COLOR.RED, FLAG_COLOR.ORANGE, FLAG_COLOR.CRIMSON,};
    public final FLAG_COLOR[] allyColors = {FLAG_COLOR.BLUE,
     FLAG_COLOR.CYAN, FLAG_COLOR.PURPLE};
    public List<FLAG_COLOR> unusedPlayerColorsList;
    List<DC_Player> players = new ArrayList<>();
    private String data;
    private DC_Player PC;

    public PlayerManager(MissionMaster<E> master) {
        super(master);


    }

    public static DC_Player getDefaultPlayer() {
        return DEFAULT_PLAYER;
    }
    public void gameStarted() {
//        DC_Player player = getPlayer(true);
//        player.getControlledUnits()
//        player..setHeroObj(hero);
    }

    public void initializePlayers() {
        players = new ArrayList<>();
        if (getMaster().getGame().getDataKeeper().getPlayerData() != null) {
            // TODO init data from preset
        }
        if (data == null) {
            data = generateDefaultPlayerData();
        }
        unusedPlayerColorsList = new ListMaster<FLAG_COLOR>()
         .getList(playerColors);
        int i = 0;
        for (String substring : ContainerUtils.open(data)) {
            DC_Player player = initPlayerFromString(substring);
            if (player.getAllegiance() == ALLEGIENCE.NEUTRAL)
                Player.NEUTRAL = player;
//            else
            players.add(player);
            if (player.isEnemy())
                player.setAi(true);
            initUnitData(player, i);

            FLAG_COLOR color = getColorFlag(player.isEnemy());
            player.setFlagColor(color);
            FLAG_COLOR colorAlt = getColorFlag(player.isEnemy(), true);
            player.setFlagColorAlt(colorAlt);

            i++;
        }
        if (Player.NEUTRAL == null) {
            Player.NEUTRAL = new DC_Player("Neutral", FLAG_COLOR.BROWN, "", "", ALLEGIENCE.NEUTRAL);
            DC_Player.NEUTRAL = (DC_Player) Player.NEUTRAL;
            players.add(DC_Player.NEUTRAL);
        }
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
            color = getColorFlag();
        }
        DC_Player player = new DC_Player(dataUnit.getValue(PLAYER_VALUE.NAME), color,
                dataUnit.getValue(PLAYER_VALUE.EMBLEM), dataUnit.getValue(PLAYER_VALUE.PORTRAIT), allegience);

        FLAG_COLOR colorAlt = getColorFlag(player.isEnemy(), true);
        player.setFlagColorAlt(colorAlt);

        player.setMainHeroName(dataUnit.getValue(PLAYER_VALUE.MAIN_HERO));

        return player;
    }

    protected void initUnitData(DC_Player player, int i) {
        UnitsData[] unitData = getMaster().getGame().getDataKeeper().getUnitData();
        if (unitData != null)
            if (unitData.length > i) {
                player.setUnitData(unitData[i]);
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
        data += factory.constructDataString() + Strings.SEPARATOR;

        factory.setValueNames(default_values);
        factory.setValues("Enemy", "Red", " ", " ", "Enemy");
        data += factory.constructDataString();

        return data;
    }


    public void setData(String data) {
        this.data = data;
    }

    private FLAG_COLOR getColorFlag(boolean enemy ) {
        return getColorFlag(enemy, false);
    }
    private FLAG_COLOR getColorFlag(boolean enemy, boolean alt) {
        if (Flags.isIggDemo()){
            if (enemy) {
                return alt ? defaultEnemyColorAlt : defaultEnemyColor;
            }
            return alt ? defaultPlayerColorAlt : defaultPlayerColor;
        }
        List<FLAG_COLOR> list = new ArrayList<>(Arrays.asList(enemy ? enemyColors : allyColors));
        int index = RandomWizard.getRandomIndex(list);
        return list.get(index);
    }


    private FLAG_COLOR getColorFlag() {
        int index = RandomWizard.getRandomIndex(unusedPlayerColorsList);
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
        if (players == null) {
            return null;
        }
        if (me)
            if (PC!=null)
                return PC;
        for (DC_Player player : players) {
            if (player.isMe())
                if (me)
                    return PC = player;
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
