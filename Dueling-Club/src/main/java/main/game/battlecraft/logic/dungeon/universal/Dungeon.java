package main.game.battlecraft.logic.dungeon.universal;

import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_TAGS;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.dungeon.minimap.Minimap;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

public class Dungeon extends LightweightEntity {
    private static final Integer DEFAULT_GLOBAL_ILLUMINATION_NIGHT = 15;
    private static final Integer DEFAULT_GLOBAL_ILLUMINATION_DAY = 75;
    Integer z;
    private COLOR_THEME colorTheme;
    private DUNGEON_TYPE dungeonType;
    private DUNGEON_TEMPLATES template;
    private Minimap minimap;
    private String levelFilePath;

    /*
     * Encounters Levels Rewards Loot
     * Atmo and background
     */
    public Dungeon(ObjType type) {
        this(type, false);
    }

    public Dungeon(ObjType type, boolean sublevel) {
        super(type );

    }

    public Dungeon(String typeName, boolean sublevel) {
        this(DataManager.getType(typeName, DC_TYPE.DUNGEONS), sublevel);
    }

//    public void init() {
//        toBase();
//    }


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

    public Integer getCellsX() {
        return getWidth();
    }

    public Integer getWidth() {

        if (getIntParam(PARAMS.BF_WIDTH) == 0)
            setParam(PARAMS.BF_WIDTH,   getGame().getDungeonMaster().getBuilder().getDefaultWidth());
        return getIntParam(PARAMS.BF_WIDTH);
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        if (getIntParam(PARAMS.BF_HEIGHT) == 0)
            setParam(PARAMS.BF_HEIGHT,   getGame().getDungeonMaster().getBuilder().
             getDefaultHeight());

        return getIntParam(PARAMS.BF_HEIGHT);
    }


    public Integer getZ() {
        if (z == null)
            return 0;
        return z;
    }

    public void setZ(int i) {
        z = i;
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

    public boolean isSurface() {
        return checkProperty(PROPS.DUNGEON_TAGS, DungeonEnums.DUNGEON_TAGS.SURFACE + "");
    }
    public boolean isNight() {
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.NIGHT + "");
    }
    public boolean isPermanentDusk() {
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.PERMA_DUSK + "");
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


    public Integer getGlobalIllumination() {
        if (isSurface()) {
            if (checkParam(PARAMS.GLOBAL_ILLUMINATION)) {
                return getIntParam(PARAMS.GLOBAL_ILLUMINATION);
            }// day/night
            else {
                if ( isPermanentDusk())
                    return (DEFAULT_GLOBAL_ILLUMINATION_DAY+DEFAULT_GLOBAL_ILLUMINATION_NIGHT)/2;
                    if ( isDaytime())
                    return DEFAULT_GLOBAL_ILLUMINATION_DAY;
                return DEFAULT_GLOBAL_ILLUMINATION_NIGHT;
            }
        }

        return 0;

    }

    private boolean isDaytime() {
        if (isNight()) return false;
        return true;
//        return getGame().getState().getRound()/roundsPerCycle%2==0;
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
