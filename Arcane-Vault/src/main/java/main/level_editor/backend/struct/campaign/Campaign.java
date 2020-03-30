package main.level_editor.backend.struct.campaign;

import main.data.tree.LayeredData;
import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.level_editor.backend.struct.adventure.AdventureLocation;
import main.level_editor.backend.struct.boss.BossDungeon;

import java.util.Set;

public class Campaign extends LightweightEntity implements LayeredData<BossDungeon> {
    //game campaign obj?
    // edit either in CAMPAIGN mode or in scenario - where all is separate and independent
    Set<AdventureLocation> locations;
    Set<BossDungeon> dungeons;
    private BossDungeon currentDungeon;

    public Campaign(ObjType type) {
        super(type);
        CampaignInitializer.init(this);
    }
    //full structure - ??

    //associate map obj with each floor


    public Set<AdventureLocation> getLocations() {
        return locations;
    }

    public void setLocations(Set<AdventureLocation> locations) {
        this.locations = locations;
    }

    public Set<BossDungeon> getDungeons() {
        return dungeons;
    }

    public void setDungeons(Set<BossDungeon> dungeons) {
        this.dungeons = dungeons;
    }

    public BossDungeon getCurrentDungeon() {
        return currentDungeon;
    }

    public void setCurrentDungeon(BossDungeon currentDungeon) {
        this.currentDungeon = currentDungeon;
    }

    @Override
    public Set<BossDungeon> getChildren() {
        return getDungeons();
    }
}
