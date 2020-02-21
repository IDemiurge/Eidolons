package main.level_editor.struct.campaign;

import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.level_editor.struct.adventure.AdventureLocation;
import main.level_editor.struct.boss.BossDungeon;
import main.system.auxiliary.ContainerUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public class CampaignInitializer {
    public static void init(Campaign campaign) {
        Set<BossDungeon> dungeons=new LinkedHashSet<>();
        Set<AdventureLocation> locations=new LinkedHashSet<>();
        for (String substring : ContainerUtils.openContainer(
                campaign.getProperty(MACRO_PROPS.CAMPAIGN_PARTY))) {
            //read list of floors and other data WHERE
            ObjType type = DataManager.getType(substring, MACRO_OBJ_TYPES.PLACE);
            dungeons.add(initBossDungeon( new BossDungeon(type)));
        }
        campaign.setDungeons(dungeons);
        campaign.setLocations(locations);
    }

    private static BossDungeon initBossDungeon(BossDungeon bossDungeon) {
        //initial floor
//bossDungeon.addFloor()

        return bossDungeon;
    }
}
