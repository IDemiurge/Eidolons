package eidolons.game.battlecraft.logic.dungeon.universal;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battlefield.vision.IlluminationMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.decor.CellData;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.DUNGEON_TAGS;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.LightweightEntity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

public class Floor extends LightweightEntity {
    private String levelFilePath;
    private LOCATION_TYPE dungeonSubtype;
    private Location location;

    public Floor(ObjType type) {
        super(type);
        setRef(new Ref());
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

    private boolean isDaytime() {
        return !isNight();
        //        return getGame().getState().getRound()/roundsPerCycle%2==0;
    }

    public String getLevelFilePath() {
        return levelFilePath;
    }

    public void setLevelFilePath(String levelFilePath) {
        this.levelFilePath = levelFilePath;
    }

    public DungeonEnums.CELL_SET getCellType(int i, int j) {
        CellData cellScriptData = location.getCellMap().get(Coordinates.get(i, j));
        if (cellScriptData != null) {
            String value = cellScriptData.getValue(CellData.CELL_VALUE.cell_set);
            if (!value.isEmpty()) {
                return DungeonEnums.CELL_SET.valueOf(value.toLowerCase());
            }
        }
        return getGame().getDungeonMaster().getStructMaster().getCellType(i, j);
    }


    public Coordinates getCoordinateByName(String value) {
        for (Coordinates s : location.getTextDataMap().keySet()) {
            CellScriptData data = location.getTextDataMap().get(s);

            String val = data.getValue(CellScriptData.CELL_SCRIPT_VALUE.named_point).trim();
            if (VariableManager.removeVarPart(val).equalsIgnoreCase(value)) {
                return Coordinates.get(VariableManager.getVar(val));
            }
        }
        return null;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public CONTENT_CONSTS.COLOR_THEME getColorTheme() {
        return location.getColorTheme();
    }

    public CONTENT_CONSTS.COLOR_THEME getAltColorTheme() {
        return location.getAltColorTheme();
    }

    public String getMapBackground() {
        return type.getProperty(PROPS.MAP_BACKGROUND);
    }

    public LOCATION_TYPE getDungeonSubtype() {
        if (dungeonSubtype == null) {
            dungeonSubtype = new EnumMaster<LOCATION_TYPE>().retrieveEnumConst(LOCATION_TYPE.class,
                    getProperty(PROPS.SUBDUNGEON_TYPE));
        }
        return dungeonSubtype;
    }

    public boolean isBoss() {
        return false;
    }

    public Integer getCellsX() {
        return getWidth();
    }

    public Integer getWidth() {
        return getGame().getDungeonMaster().getFloorWrapper().getWidth();
    }

    public Integer getCellsY() {
        return getHeight();
    }

    public Integer getHeight() {
        return getGame().getDungeonMaster().getFloorWrapper().getHeight();
    }


    public DC_Game getGame() {
        return (DC_Game) super.getGame();
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


}
