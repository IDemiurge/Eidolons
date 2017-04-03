package main.game.logic.battle.player;

import main.client.net.HostClientConnection;
import main.content.enums.system.AiEnums;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.PlayerAI;
import main.game.battlefield.Coordinates;
import main.game.core.game.DC_Game;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Regulus
 */
public class DC_Player extends Player {

    public static final DC_Player NEUTRAL = new DC_Player("Neutral", Color.GRAY, false, "");

    static {
        Player.NEUTRAL = NEUTRAL;
    }

    HostClientConnection connection;
    boolean host;
    private Map<DC_Obj, Coordinates> detectionCache = new HashMap<>();
    private PlayerAI playerAI;
    private String data;
    private String partyDataString;

    public DC_Player(String name, Color color, boolean me) {
        super(name, color, me, null);
    }

    public DC_Player(String name, Color color, boolean me, String portrait) {
        super(name, color, me, portrait);
    }

    public DC_Player(String name, Color color, boolean me, boolean host, String data) {
        super(name, color, me, null);
        this.data = data;
        this.host = host;
        if (data != null) {
            initData(data);
        }
    }

    @Override
    public boolean isAi() {
        // preCheck connection?
        return super.isAi();
    }

    public void disconnected() {
        // prompt, pause...
        // TODO if ping thread fails?
        setAi(true);
    }

    private void initData(String data) {
        // String[] array = data.split(StringMaster.NET_DATA_SEPARATOR);
        // name = array[0];
        // partyData = new PartyData(array[1]);
        // setPartyDataString((array[1]));
        setPartyDataString(data);
        // color, user data, team
    }

    public DC_Game getGame() {
        if (game == null) {
            game = DC_Game.game;
        }
        return (DC_Game) super.getGame();
    }

    public Set<Obj> getControlledUnits() {

        Set<Obj> units = new HashSet<>();
        for (Unit unit : (getGame().getUnits())) {
            if (unit.getOwner() == this) {
                units.add(unit);
            }
        }
        return units;

    }

    public Map<DC_Obj, Coordinates> getLastSeenCache() {
        return detectionCache;
    }

    public PlayerAI getAI() {
        return getPlayerAI();
    }

    public PlayerAI getPlayerAI() {
        if (playerAI == null) {
            playerAI = new PlayerAI(AiEnums.PLAYER_AI_TYPE.NORMAL);
        }
        return playerAI;
    }

    public void setPlayerAI(PlayerAI playerAI) {
        this.playerAI = playerAI;
    }

    public HostClientConnection getConnection() {
        return connection;
    }

    public boolean isHost() {
        return host;
    }

    public String getData() {
        return data;
    }

    public String getPartyDataString() {
        return partyDataString;
    }

    public void setPartyDataString(String partyDataString) {
        this.partyDataString = partyDataString;
    }

}
