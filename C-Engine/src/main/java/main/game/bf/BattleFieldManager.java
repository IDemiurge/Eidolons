package main.game.bf;

import main.game.bf.directions.DIRECTION;
import main.game.core.game.GameManager;
import main.game.core.game.GenericGame;
import main.game.core.state.MicroGameState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class BattleFieldManager {

    private static Map<Integer, BattleFieldManager> instances = new LinkedHashMap<>();
    public Coordinates[][] coordinates;
    private Map<Coordinates, Map<DIRECTION, Coordinates>> adjacenctDirectionMap = new HashMap<>();
    private Map<Coordinates, Set<Coordinates>> adjacenctMap = new HashMap<>();
    private Map<Coordinates, Set<Coordinates>> adjacenctMapNoDiags = new HashMap<>();
    private Map<Coordinates, Set<Coordinates>> adjacenctMapDiagsOnly = new HashMap<>();

    protected GenericGame game;
    protected GameManager mngr;
    protected MicroGameState state;

    private static BattleFieldManager instance;

    public BattleFieldManager(GenericGame game, Integer moduleId, int w, int h) {
        this.game = game;
        mngr = game.getManager();
        this.state = game.getState();
        instance = this;
        instances.put(moduleId, this);
//        coordinates= new Coordinates[w+5][h+5];
//        Coordinates.coordinates = coordinates;
        //any use of sub array of coords?
    }

    public static void entered(Integer moduleId) {
        instance = instances.get(moduleId);

    }

    public static BattleFieldManager getInstance() {
        return instance;
    }

    public abstract boolean isCellVisiblyFree(Coordinates c);


    public Map<Coordinates, Set<Coordinates>> getAdjacenctMap(Boolean diags) {
        if (diags != null)
            return diags ? adjacenctMap : adjacenctMapNoDiags;
        return adjacenctMapDiagsOnly;
    }

    public Map<Coordinates, Map<DIRECTION, Coordinates>> getAdjacenctDirectionMap() {
        return adjacenctDirectionMap;
    }

    public Map<Coordinates, Set<Coordinates>> getAdjacenctMap() {
        return adjacenctMap;
    }

    public Map<Coordinates, Set<Coordinates>> getAdjacenctMapNoDiags() {
        return adjacenctMapNoDiags;
    }

    public Map<Coordinates, Set<Coordinates>> getAdjacenctMapDiagsOnly() {
        return adjacenctMapDiagsOnly;
    }
}
