package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.IlluminationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import eidolons.game.battlecraft.logic.meta.scenario.script.ScriptSyntax;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_TAGS;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.xml.XML_Converter;
import main.entity.LightweightEntity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.*;
import main.system.data.DataUnitFactory;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.launch.TypeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Dungeon extends LightweightEntity {
    Integer z;
    private COLOR_THEME colorTheme;
    private DUNGEON_TYPE dungeonType;
    private DUNGEON_TEMPLATES template;
    private String levelFilePath;
    private LOCATION_TYPE dungeonSubtype;
    private Collection<Coordinates> voidCoordinates;

    /*
     * Encounters Levels Rewards Loot
     * Atmo and background
     */
    public Dungeon(ObjType type) {
        this(type, false);
    }

    public Dungeon(ObjType type, boolean sublevel) {
        super(type);
        setRef(new Ref());
    }

    /*
        dimensions
        dungeon type (non-changeable)
        custom params
        or
        level xml path
         */
    public Dungeon(String typeName, boolean sublevel) {
        this(DataManager.getType(typeName, DC_TYPE.DUNGEONS), sublevel);
    }

    public String toXml() {
        String xml = XML_Converter.wrap("Name", getType().getName());
        //        xml += XML_Converter.wrap(PARAMS.BF_WIDTH.getName(), getCellsX() + "");
//        xml += XML_Converter.wrap(PARAMS.BF_HEIGHT.getName(), getCellsY() + "");
        xml +=
                TypeBuilder.getAlteredValuesXml(this, getType());

        if (levelFilePath != null)
            xml += XML_Converter.wrap("LevelFilePath", levelFilePath);
        return XML_Converter.wrap("Dungeon", xml);
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

    public LOCATION_TYPE getDungeonSubtype() {
        if (dungeonSubtype == null) {
            dungeonSubtype = new EnumMaster<LOCATION_TYPE>().retrieveEnumConst(LOCATION_TYPE.class,
                    getProperty(PROPS.SUBDUNGEON_TYPE));
        }
        return dungeonSubtype;
    }

    public boolean isBoss() {
        return getDungeonType() == DUNGEON_TYPE.BOSS;
    }

    public Integer getCellsX() {
        return getWidth();
    }

    public Integer getWidth() {

        if (getIntParam(PARAMS.BF_WIDTH) == 0)
            setParam(PARAMS.BF_WIDTH, getGame().getDungeonMaster().getBuilder().getDefaultWidth());
        return getIntParam(PARAMS.BF_WIDTH);
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        if (getIntParam(PARAMS.BF_HEIGHT) == 0)
            setParam(PARAMS.BF_HEIGHT, getGame().getDungeonMaster().getBuilder().
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
            if (getDungeonType() == DUNGEON_TYPE.BOSS) {
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
        return checkProperty(PROPS.DUNGEON_TAGS, DUNGEON_TAGS.SURFACE + "");
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
                if (isPermanentDusk())
                    return (IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_DAY
                            + IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_NIGHT) / 2;
                if (isDaytime())
                    return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_DAY;
                return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_NIGHT;
            }
        }

        return IlluminationMaster.DEFAULT_GLOBAL_ILLUMINATION_UNDERGROUND;

    }

    public Coordinates getPoint(Integer index) {
        return getPoint("" + (index + 1));
    }

    public Coordinates getPoint(String arg) {
        Coordinates c = null;
        if (arg.contains(ScriptSyntax.SPAWN_POINT) || NumberUtils.isInteger(arg)) {
            arg = arg.replace(ScriptSyntax.SPAWN_POINT, "");
            Integer i = NumberUtils.getInteger(arg) - 1;
            List<String> spawnPoints = ContainerUtils.openContainer(
                    getProperty(PROPS.COORDINATE_POINTS));
            c = Coordinates.get(spawnPoints.get(i));
        } else {
            Map<String, String> map = new DataUnitFactory(true).
                    deconstructDataString(getProperty(PROPS.NAMED_COORDINATE_POINTS));
            String string = map.get(arg);
            if (string == null) {
                //find
                Object key = new SearchMaster<>().findClosest(arg, map.keySet());
                string = map.get(key);
            }
            return Coordinates.get(string);
        }
        return c;
//        getProperty(PROPS.ENCOUNTER_SPAWN_POINTS)
    }

    private boolean isDaytime() {
        return !isNight();
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

    public DUNGEON_STYLE getStyle() {
        return ZoneCreator.getStyle(ZONE_TYPE.OUTSKIRTS, getDungeonSubtype());
    }

    public String getCellImagePath(int i, int j) {
        if (getGame().getDungeonMaster().getDungeonLevel() == null) {
            if (isSurface()) {

            }
        } else {
            return getGame().getDungeonMaster().getDungeonLevel().getCellImgPath(i, j);

        }
        return ImageManager.getEmptyCellPath(GuiManager.getBfCellsVersion());
    }

    public Collection<Coordinates> getVoidCoordinates() {
        if (voidCoordinates == null) {
            voidCoordinates = new ArrayList<>();
        }
        return voidCoordinates;
    }

    public enum POINTS {
        CENTER_SPAWN,
        REAR_SPAWN,
        SCOUTS_SPAWN,

    }

}
