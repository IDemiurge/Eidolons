package main.client.game;

import main.client.game.logic.PrecombatData;
import main.client.game.logic.PrecombatData.PRECOMBAT_VALUES;
import main.content.PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.Obj;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;

@Deprecated
public class PrecombatManager {
    public static final String ABORTED = "ABORTED";

    Obj heroObj;
    private DC_Game game;
    private Player player;
    private PrecombatData precombatData;

    public PrecombatManager(DC_Game game) {
        this.game = game;
        this.player = game.getPlayer(true);
        heroObj = player.getHeroObj();
        this.precombatData = new PrecombatData("");
    }

    private void createAndShowGUI() {

    }

    public void launch() {
        createAndShowGUI();
    }

    public String getResults() {
        if (precombatData.isAborted()) {
            return ABORTED;
        }
        return precombatData.getData();
    }

    public void initMyPrecombatData() {
        initPrecombatData(precombatData, game.getPlayer(true));
    }

    public void initPrecombatData(String precombatData, Player player) {
        initPrecombatData(new PrecombatData(precombatData), player);

    }

    public void initPrecombatData(PrecombatData precombatData, Player player) {
        // TODO ++ Items
        initHeroProp(PROPS.DIVINED_SPELLS, precombatData.getValue(PRECOMBAT_VALUES.DIVINED_SPELLS), player);
        // mercs with preselected positioning!
        initHeroProp(PROPS.HIRED_MERCENARIES, precombatData.getValue(PRECOMBAT_VALUES.HIRED_MERCENARIES), player);
        initHeroProp(PROPS.MEMORIZED_SPELLS, precombatData.getValue(PRECOMBAT_VALUES.MEMORIZED_SPELLS), player);
        initHeroProp(PROPS.PREPARED_SPELLS, precombatData.getValue(PRECOMBAT_VALUES.PREPARED_SPELLS), player);
    }

    private void initHeroProp(PROPERTY prop, String value, Player player) {
        player.getHeroObj().setProperty(prop, value);

    }

    public Obj getHeroObj() {
        return heroObj;
    }

    public void setHeroObj(Obj heroObj) {
        this.heroObj = heroObj;
    }

    public DC_Game getGame() {
        return game;
    }

    public void setGame(DC_Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public PrecombatData getPrecombatData() {
        return precombatData;
    }

    public void setPrecombatData(PrecombatData precombatData) {
        this.precombatData = precombatData;
    }
}
