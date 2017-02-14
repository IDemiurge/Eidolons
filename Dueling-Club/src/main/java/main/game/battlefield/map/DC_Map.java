package main.game.battlefield.map;

import main.data.filesys.PathFinder;
import main.entity.obj.unit.DC_HeroObj;
import main.entity.type.ObjType;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.net.data.MapData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DC_Map {
    /**
     * Gotta refactor this - generator should have all those fields and methods
     */
    public static final String CORNERS = "0-0,8-0,8-5,0-5";
    public static final String SEMI_CORNERS = "1-0,7-0,7-5,1-5";
    public static final String EAST_FREE_ZONE = "1-1,1-2,1-3,2-1,2-2,2-3,1-4,2-4";
    public static final String WEST_FREE_ZONE = "7-1,7-2,7-3,6-1,6-2,6-3,6-4,7-4";
    public static final String CENTER_ZONE = "4-4,7-0,7-4,1-4";

    public static final String TOP_RIGHT_CORNER = "0-4";
    public static final String TOP_LEFT_CORNER = "0-0";
    public static final String BOTTOM_RIGHT_CORNER = "8-4";
    public static final String BOTTOM_LEFT_CORNER = "8-0";

    public static final String BOTTOM_CORNERS = "8-4,0-4";
    public static final String TOP_CORNERS = "0-0,8-0";
    public static final String MIDDLE = "4-2";

    public static final String MIDDLE_RIGHT = "5-2";
    public static final String MIDDLE_LEFT = "3-2";

    public static final String MIDDLE_RIGHT_2 = "6-2";
    public static final String MIDDLE_LEFT_2 = "2-2";

    public static final String LEFT_MIDDLE = "0-2";
    public static final String RIGHT_MIDDLE = "8-2";
    Map<Coordinates, BF_OBJ_OWNER> crystals = new HashMap<>();
    private DC_Game game;
    private Map<Coordinates, ObjType> objMap = new HashMap<>();
    private MapData mapData;
    private String background;
    private Map<Coordinates, BF_OBJ_OWNER> gateways = new HashMap<>();

    private boolean theme = true;

    private String name;

    // if null - fill with empty cell
    public DC_Map(DC_Game game, MAP_TEMPLATE template) {
        this.game = game;
        initMap(template);
        this.setName(template.toString());
    }

    public DC_Map(DC_Game game, MapData mapData) {
        this.mapData = mapData;
        setBackground(mapData.getBackground());
        objMap = mapData.getObjMap();
        this.setName(mapData.getMapName());
    }

    public DC_Map() {
    }

    private void initMap(MAP_TEMPLATE template) {
        constructFromTemplate(template);

        for (Coordinates c : getGateways().keySet()) {
            ObjType gateway = createGateway(getGateways().get(c));
            objMap.put(c, gateway);
        }
        for (Coordinates c : getCrystals().keySet()) {
            ObjType crystal = createCrystal(getCrystals().get(c));
            objMap.put(c, crystal);
        }
        if (isTheme()) {
            setBackground(initThemeBackground());
        } else {
            setBackground(initRandomBackground());
        }

        mapData = new MapData(objMap, getBackground());

    }

    private String initThemeBackground() {
        String key;
        if (game.getPlayer(true).isDefender()) {
            key = ((DC_HeroObj) game.getPlayer(true).getHeroObj()).getDeity()
                    .getName();
        } else {
            if (game.getPlayer(false).isDefender()) {
                key = ((DC_HeroObj) game.getPlayer(false).getHeroObj())
                        .getDeity().getName();

            } else {
                return initRandomBackground();
            }
        }
        return FileManager.getRandomFilePathVariant(
                PathFinder.getThemedBgPicsPath() + key, ".jpg"); // +number TODO
    }

    private String initRandomBackground() {
        File f = new File(ImageManager.getDefaultImageLocation()
                + PathFinder.getBgPicsPath());
        if (!f.isDirectory()) {
            LogMaster.log(4, "failed to init bg!");
            return "";
        }
        String[] array = f.list();
        String filename = f.list()[new Random().nextInt(array.length)];
        filename = PathFinder.getBgPicsPath() + filename;
        LogMaster.log(4, "init bg! - " + filename);
        return filename;

    }

    private ObjType createGateway(BF_OBJ_OWNER C_TYPE) {

        ObjType c = DC_MapGenerator.getGatewayObjType(C_TYPE);
        if (c == null) {

            LogMaster.log(1, "" + C_TYPE);
            throw new RuntimeException();
        }
        return c;
    }

    private ObjType createCrystal(BF_OBJ_OWNER C_TYPE) {
        ObjType c = DC_MapGenerator.getCrystalObjType(C_TYPE);
        if (c == null) {
            LogMaster.log(1, "" + C_TYPE);
            throw new RuntimeException();
        }
        return c;

    }

    public DC_Map constructFromTemplate(MAP_TEMPLATE template) {
        switch (template) {
            case DUEL:
                initMapGateways(BOTTOM_LEFT_CORNER, TOP_RIGHT_CORNER, "",
                        MIDDLE_RIGHT + "," + MIDDLE_LEFT, 0);
                initMapCrystals(TOP_LEFT_CORNER, BOTTOM_RIGHT_CORNER, MIDDLE,
                        ""
                        // MIDDLE_LEFT_2 + "," + MIDDLE_RIGHT_2
                        , 0);

        }
        return null;
    }

    private void initMapGateways(String enemygateways, String factiongateways,
                                 String neutralgateways, String randomgateways, int randomGateways) {
        BF_OBJ_OWNER TYPE;
        for (Coordinates c : Coordinates.getCoordinates(factiongateways)) {
            TYPE = BF_OBJ_OWNER.MY;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " gateway at " + c);
            gateways.put(c, BF_OBJ_OWNER.MY);
        }
        for (Coordinates c : Coordinates.getCoordinates(enemygateways)) {
            TYPE = BF_OBJ_OWNER.ENEMY;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " gateway at " + c);

            gateways.put(c, BF_OBJ_OWNER.ENEMY);
        }
        for (Coordinates c : Coordinates.getCoordinates(neutralgateways)) {
            TYPE = BF_OBJ_OWNER.NEUTRAL;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " gateway at " + c);

            gateways.put(c, BF_OBJ_OWNER.NEUTRAL);
        }
        for (Coordinates c : Coordinates.getCoordinates(randomgateways)) {
            TYPE = BF_OBJ_OWNER.RANDOM;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " gateway at " + c);
            gateways.put(c, TYPE);
        }

        for (int i = randomGateways; i > 0; i--) {
            Coordinates c = new Coordinates(100 + i, 0);
            gateways.put(c, BF_OBJ_OWNER.RANDOM);
        }

    }

    private void initMapCrystals(String enemycrystals, String factioncrystals,
                                 String neutralcrystals, String randomcrystals, int randomCrystals) {
        BF_OBJ_OWNER TYPE;

        for (Coordinates c : Coordinates.getCoordinates(factioncrystals)) {
            TYPE = BF_OBJ_OWNER.MY;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " crystal at " + c);

            crystals.put(c, BF_OBJ_OWNER.MY);
        }
        for (Coordinates c : Coordinates.getCoordinates(enemycrystals)) {
            TYPE = BF_OBJ_OWNER.ENEMY;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " crystal at " + c);
            crystals.put(c, BF_OBJ_OWNER.ENEMY);
        }
        for (Coordinates c : Coordinates.getCoordinates(neutralcrystals)) {
            TYPE = BF_OBJ_OWNER.NEUTRAL;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " crystal at " + c);
            crystals.put(c, BF_OBJ_OWNER.NEUTRAL);
        }
        for (Coordinates c : Coordinates.getCoordinates(randomcrystals)) {
            TYPE = BF_OBJ_OWNER.RANDOM;
            LogMaster.log(LogMaster.MAP_GENERATION_DEBUG,
                    TYPE + " crystal at " + c);
            crystals.put(c, BF_OBJ_OWNER.RANDOM);
        }

        for (int i = randomCrystals; i > 0; i--) {
            Coordinates c = new Coordinates(100 + i, 0);
            crystals.put(c, BF_OBJ_OWNER.RANDOM);
        }

    }

    public Map<Coordinates, ObjType> getMapObjects() {
        return objMap;
    }

    public MapData getMapData() {
        return mapData;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Map<Coordinates, BF_OBJ_OWNER> getGateways() {
        return gateways;
    }

    public void setGateways(Map<Coordinates, BF_OBJ_OWNER> gateways) {
        this.gateways = gateways;
    }

    public Map<Coordinates, BF_OBJ_OWNER> getCrystals() {
        return crystals;
    }

    public void setCrystals(Map<Coordinates, BF_OBJ_OWNER> crystals) {
        this.crystals = crystals;
    }

    public boolean isTheme() {
        return theme;
    }

    public void setTheme(boolean theme) {
        this.theme = theme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Coordinates, ObjType> getObjMap() {
        return objMap;
    }

    public void setObjMap(Map<Coordinates, ObjType> objMap) {
        this.objMap = objMap;
    }

    // + hero positions
    public enum MAP_TEMPLATE {
        ARENA(), DUEL(),;

    }

    public enum BF_OBJ_OWNER {
        MY, ENEMY, RANDOM, NEUTRAL;
    }
}
