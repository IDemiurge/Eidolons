package main.level_editor.struct.campaign;

import main.entity.LightweightEntity;
import main.entity.type.ObjType;
import main.level_editor.struct.adventure.AdventureLocation;
import main.level_editor.struct.boss.BossDungeon;
import main.level_editor.struct.level.Floor;

import java.util.Set;

public class Campaign extends LightweightEntity {
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
}
