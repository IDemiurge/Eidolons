package main.game.logic.dungeon;

import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.values.parameters.G_PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.map.DC_Map;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.game.logic.dungeon.DungeonLevelMaster.ENTRANCE_POINT_TEMPLATE;
import main.game.logic.dungeon.building.BuildHelper.BuildParameters;
import main.game.logic.dungeon.building.DungeonBuilder.DUNGEON_TEMPLATES;
import main.game.logic.dungeon.building.DungeonPlan;
import main.game.logic.dungeon.minimap.Minimap;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;

import java.util.LinkedList;
import java.util.List;

public class Dungeon extends Entity {
    private static final Integer DEFAULT_GLOBAL_ILLUMINATION = 75;
    private COLOR_THEME colorTheme;
    private DUNGEON_TYPE dungeonType;

    private List<Dungeon> subLevels;
    private boolean sublevel;
    private Dungeon parent;
    private boolean currentLevel;
    private int z;
    private List<Entrance> entrances;
    private String entranceData;
    private DungeonPlan plan;
    private DUNGEON_TEMPLATES template;
    private Entrance mainEntrance;
    private Entrance mainExit;
    private boolean rotated;
    private boolean flippedY;
    private boolean flippedX;
    private Minimap minimap;
    private DC_Map map;
    private BuildParameters params;
    private Integer nextZ;
    private String levelFilePath;

    /*
     * Encounters Levels Rewards Loot
     * Atmo and background
     */
    public Dungeon(ObjType type) {
        this(type, false);
    }

    public Dungeon(ObjType type, boolean sublevel) {
        super(type, Player.NEUTRAL, DC_Game.game, new Ref());
        z = getIntParam(G_PARAMS.Z_LEVEL);
        this.sublevel = sublevel;
        if (!sublevel) {
            generateSublevels();
        }
        initEntrances();
    }

    public Dungeon(String typeName, boolean sublevel) {
        this(DataManager.getType(typeName, DC_TYPE.DUNGEONS), sublevel);
    }

    public boolean isCurrentLevel() {
        return currentLevel;
    }

    public void init() {
        toBase();
    }

    public Minimap getMinimap() {
        if (minimap == null) {
            minimap = new Minimap(CoreEngine.isLevelEditor(), this);
            minimap.setViewMode(true);
            minimap.init();
        }
        return minimap;
    }

    public String getMapBackground() {
        return type.getProperty(PROPS.MAP_BACKGROUND);
    }

    public COLOR_THEME getColorTheme() {
        if (colorTheme == null) {
            setColorTheme(new EnumMaster<COLOR_THEME>().retrieveEnumConst(COLOR_THEME.class,
                    getProperty(PROPS.COLOR_THEME)));
        }
        return colorTheme;
    }

    public void setColorTheme(COLOR_THEME colorTheme) {
        this.colorTheme = colorTheme;
    }

    public DUNGEON_TYPE getDungeonType() {
        if (dungeonType == null) {
            dungeonType = new EnumMaster<DUNGEON_TYPE>().retrieveEnumConst(DUNGEON_TYPE.class,
                    getProperty(G_PROPS.DUNGEON_TYPE));
        }
        return dungeonType;
    }

    public boolean isBoss() {
        return getDungeonType() == DungeonEnums.DUNGEON_TYPE.BOSS;
    }

    public List<Entrance> getEntrances() {
        if (entrances == null) {
            entrances = new LinkedList<>();
        }
        return entrances;
    }

    public String getEntranceData() {
        return entranceData;
    }

    public void setEntranceData(String entranceData) {
        this.entranceData = entranceData;
    }

    public ENTRANCE_POINT_TEMPLATE getEntranceTemplate() {
        return null;
    }

    public void generateSublevels() {
        entrances = new LinkedList<>();
        if (DungeonLevelMaster.isSublevelTestOn() && !sublevel) {
            setProperty(PROPS.SUBLEVELS, DungeonLevelMaster.TEST_ENTRANCE_DATA, true);
        }
        subLevels = new LinkedList<>();
        for (String sublevel : StringMaster.openContainer(getProperty(PROPS.SUBLEVELS))) {
            Dungeon dungeon = new Dungeon(VariableManager.removeVarPart(sublevel), true);
            DungeonMaster.getDungeons().add(dungeon);
            addSublevel(sublevel, dungeon);
        }

        // by default?same bf types, smaller size down
        // in most cases, some levels at least should be set...
        // other dungeon types should be eligible as sublevels for
        // multi-dungeons
        // new spawning std - big level with 2R1E, medium level with E and small
        // with B.
    }

    public int getNextZ() {
        if (nextZ == null) {
            nextZ = getZ() + DungeonLevelMaster.getNextZ(true);
        }
        return nextZ;
    }

    public void addSublevel(String sublevel, Dungeon dungeon) {
        subLevels.add(dungeon); // mark if can go
        // deeper
        dungeon.setEntranceData(StringMaster
                .cropParenthesises(VariableManager.getVarPart(sublevel)));
        if (!dungeon.getEntranceData().contains(StringMaster.VAR_SEPARATOR)) {
            DungeonLevelMaster.generateEntranceData(dungeon);
        }
        int z = dungeon.getIntParam(G_PARAMS.Z_LEVEL);
        if (z == 0) {
            z = getNextZ();
        }
        dungeon.setZ(z);
        nextZ = null;
    }

    public boolean isExtendedBattlefield() {
        if (getCellsY() != 0) {
            return true; // TODO WHAT IF IT IS 'SHRUNKEN'?
        }
        return getCellsX() != 0;
    }

    public Integer getCellsX() {
        return getWidth();
    }

    public Integer getWidth() {
        if (getIntParam(PARAMS.BF_WIDTH) == 0) {
            return DungeonMaster.BASE_WIDTH;
        }
        return getIntParam(PARAMS.BF_WIDTH);
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        if (getIntParam(PARAMS.BF_HEIGHT) == 0) {
            return DungeonMaster.BASE_HEIGHT;
        }
        return getIntParam(PARAMS.BF_HEIGHT);
    }

    public FACING_DIRECTION getSpawningSide() {

        return null;
    }

    public List<Dungeon> getSubLevels() {
        if (subLevels == null) {
            subLevels = new LinkedList<>();
        }
        return subLevels;
    }

    public void setSubLevels(List<Dungeon> subLevels) {
        this.subLevels = subLevels;
    }

    public boolean isSublevel() {
        return sublevel;
    }

    public void setSublevel(boolean sublevel) {
        this.sublevel = sublevel;
    }

    public Dungeon getParent() {
        return parent;
    }

    public void setParent(Dungeon parent) {
        this.parent = parent;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int i) {
        z = i;
    }

    private void initEntrances() {
        if (StringMaster.isEmpty(entranceData)) {
            entranceData = getProperty(PROPS.DUNGEON_MAIN_ENTRANCES);
        }
        if (StringMaster.isEmpty(entranceData)) {
            return;
        }
        String enterData = entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR)[0];
        String name = VariableManager.removeVarPart(enterData);
        Coordinates c = new Coordinates(VariableManager.getVarPart(enterData));

        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainEntrance(e);
                }
            }
        }
        if (entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR).length < 2) {
            return;
        }
        String exitData = entranceData.split(DungeonLevelMaster.ENTRANCE_SEPARATOR)[1];
        name = VariableManager.removeVarPart(exitData);
        c = new Coordinates(VariableManager.getVarPart(exitData));
        for (Entrance e : getEntrances()) {
            if (e.getCoordinates().equals(c)) {
                if (e.getName().equals(name)) {
                    setMainExit(e);
                }
            }
        }
    }

    public boolean isUnderground() {
        return checkProperty(G_PROPS.DUNGEON_TYPE, DungeonEnums.DUNGEON_TYPE.UNDERGROUND + "");
    }

    public DungeonPlan getPlan() {
        return plan;
    }

    public void setPlan(DungeonPlan plan) {
        this.plan = plan;
    }

    public DUNGEON_TEMPLATES getTemplate() {
        if (template == null) {
            initTemplate();
        }
        return template;
    }

    public void setTemplate(DUNGEON_TEMPLATES template) {
        this.template = template;
    }

    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

    private void initTemplate() {
        template = new RandomWizard<DUNGEON_TEMPLATES>().getObjectByWeight(
                getProperty(PROPS.DUNGEON_TEMPLATES), DUNGEON_TEMPLATES.class);
        if (template == null)
        // if (getProperty(PROPS.DUNGEON_TEMPLATES).isEmpty())
        {
            if (getDungeonType() == DungeonEnums.DUNGEON_TYPE.BOSS) {
                // to be set upon sublevel generation?
                template = DUNGEON_TEMPLATES.GREAT_ROOM;
            }
            // if (getSublevelType() == SUBLEVEL_TYPE.PRE_BOSS) {
            //
            // }
            // random getSublevelType().getTemplates();
            // getDungeonLevel
        }

    }

    public Entrance getMainEntrance() {
        return mainEntrance;
    }

    public void setMainEntrance(Entrance mainEntrance) {
        this.mainEntrance = mainEntrance;
    }

    public Entrance getMainExit() {
        return mainExit;
    }

    public void setMainExit(Entrance mainExit) {
        this.mainExit = mainExit;
    }

    public boolean isRotated() {
        return rotated;
    }

    public void setRotated(boolean rotated) {
        this.rotated = rotated;
    }

    public boolean isFlippedY() {
        return flippedY;
    }

    public void setFlippedY(boolean flippedY) {
        this.flippedY = flippedY;
    }

    public boolean isFlippedX() {
        return flippedX;
    }

    public void setFlippedX(boolean flippedX) {
        this.flippedX = flippedX;
    }

    public boolean isSurface() {
        // return true;
        return checkProperty(PROPS.DUNGEON_TAGS, DungeonEnums.DUNGEON_TAGS.SURFACE + "");
    }

    public int getSquare() {
        return getCellsX() * getCellsY();
    }

    @Override
    public void setName(String name) {
        this.name = name;
        setProperty(G_PROPS.NAME, name, true);
        name = StringMaster.formatDisplayedName(name);
        setProperty(G_PROPS.DISPLAYED_NAME, name, true);
    }

    public Coordinates getPlayerSpawnCoordinates() {
        String prop = getProperty(PROPS.PARTY_SPAWN_COORDINATES);
        if (prop.isEmpty()) {
            return Coordinates.getMiddleCoordinate(FACING_DIRECTION.NONE);
        }
        return new Coordinates(prop);
    }

    public Coordinates getEnemySpawningCoordinates() {
        String prop = getProperty(PROPS.ENEMY_SPAWN_COORDINATES);
        if (prop.isEmpty()) {
            return getDefaultEnemyCoordinates();
        }
        return new Coordinates(prop);
    }

    public Coordinates getDefaultEnemyCoordinates() {
        // TODO encounter?
        // default - getOrCreate a random point in some range from player start
        Coordinates playerC = getPlayerSpawnCoordinates();
        Loop.startLoop(100);
        int n = getDefaultDistanceToEnemy();
        while (Loop.loopContinues()) {
            int x = playerC.x;
            int y = playerC.y + MathMaster.getPlusMinusRandom(getOffsetEnemyMode(), n);

            if (y > GuiManager.getBattleFieldHeight() - 1 || y < 0) {
                // TODO adjust offset if static!
                continue;
            }
            if (isOffsetEnemyByX()) {
                x = x + RandomWizard.getRandomIntBetween(-n, n);
                if (x > GuiManager.getBattleFieldWidth() - 1) {
                    continue;
                }
                if (x < 0) {
                    continue;
                }
            }
            return new Coordinates(x, y);
        }
        return null;
    }

    private Boolean getOffsetEnemyMode() {
        return false;
    }

    private boolean isOffsetEnemyByX() {
        return false;
    }

    private int getDefaultDistanceToEnemy() {
        return 4;
    }

    public void setMap(DC_Map map) {
        this.map = map;
    }

    public BuildParameters getBuildParams() {
        return params;
    }

    public void setBuildParams(BuildParameters params) {
        this.params = params;
    }

    public Integer getGlobalIllumination() {
        if (isSurface()) {
            if (checkParam(PARAMS.GLOBAL_ILLUMINATION)) {
                return getIntParam(PARAMS.GLOBAL_ILLUMINATION);
            }// day/night
            else {
                return DEFAULT_GLOBAL_ILLUMINATION;
            }
        }

        return 0;

    }

    public boolean isRandomized() {
        // TODO
        return false;
    }

    public String getLevelFilePath() {
        return levelFilePath;
    }

    public void setLevelFilePath(String levelFilePath) {
        this.levelFilePath = levelFilePath;
    }

}
