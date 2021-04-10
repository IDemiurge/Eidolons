package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.module.dungeoncrawl.struct.DungeonLevel;
import eidolons.game.module.dungeoncrawl.struct.LevelZone;
import eidolons.dungeons.generator.LevelData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RestoredDungeonLevel extends DungeonLevel {
    private List<LevelZone> zones;
    private LevelData data;
    public RestoredDungeonLevel(List<LevelZone> zones, String name) {
        super(name);
        this.zones = zones;
    }
    public RestoredDungeonLevel(String name) {
        this(    new ArrayList<>() , name);
    }

    public LevelData getLevelData() {
        return data;
    }

    public void setData(LevelData data) {
        this.data = data;
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }
}
