package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RestoredDungeonLevel extends DungeonLevel {
    private List<LevelZone> zones = new ArrayList<>();
    private LevelData data;

    public RestoredDungeonLevel(TileMap tileMap, LevelModel model, SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        super(  model, type, locationType);
    }

    public RestoredDungeonLevel(List<LevelZone> zones) {
        super(  null, null, null);
        this.zones = zones;
    }

    public RestoredDungeonLevel() {
        this(new ArrayList<>());
    }

    public LevelData getData() {
        return data;
    }

    public void setData(LevelData data) {
        this.data = data;
    }

    @Override
    public List<LevelZone> getSubParts() {
        return zones;
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }
}
