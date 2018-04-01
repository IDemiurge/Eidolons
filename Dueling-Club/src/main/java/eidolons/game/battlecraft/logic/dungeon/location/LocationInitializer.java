package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonData.DUNGEON_VALUE;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonInitializer;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.module.adventure.travel.Encounter;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import eidolons.game.module.adventure.map.Place;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 5/8/2017.
 */
public class LocationInitializer extends DungeonInitializer<Location> {
    public LocationInitializer(DungeonMaster master) {
        super(master);
    }

    @Override
    public Location initDungeon() {
        setDungeonPath(game.getDataKeeper().getDungeonData()
         .getContainerValue(DUNGEON_VALUE.PATH, 0));
        //or take mission directly?

        return (Location) getBuilder().buildDungeon(getDungeonPath());
    }

    @Override
    public Location createDungeon(ObjType type) {
        return new Location((LocationMaster) getMaster(), new Dungeon(type, false));
    }


    public boolean initDungeon(String typeName, Place place) {
        // set Dungeon obj here so that launch() will be proper
        // set some other parameters perhaps...

//        ObjType type = DataManager.getType(typeName, DC_TYPE.DUNGEONS);
//        if (type == null) {
//            type = getDungeonTypeFromPlace(place);
//        }
//        this.dungeon = new Dungeon(type);
//        getDungeons().add(dungeon);
        //

        return true;

    }

    public void initSublevel(Dungeon subLevel) {
//        setDungeon(subLevel);
////        game.getBattleField().getBuilder().newDungeon(subLevel);
//        if (!getDungeons().contains(subLevel)) {
//            getDungeons().add(subLevel);
//        }
    }

    //TODO this is macro!
    public void initEncounterDungeon(Encounter e) {
        ObjType type = new ObjType(e.getRoute().getName(), DC_TYPE.DUNGEONS);
        type.initType();
        String value = e.getRoute().getProperty(PROPS.MAP_BACKGROUND);
        if (!ImageManager.isImage(value)) {
            value = e.getRoute().getArea().getProperty(PROPS.MAP_BACKGROUND);
        }
        type.setProperty(PROPS.MAP_BACKGROUND, value);
//        for (VALUE p : encounterDungeonValues) {
//            type.copyValue(p, e.getRoute());
//            if (type.checkValue(p)) {
//                type.copyValue(p, e.getRoute().getArea());
//            }
//        }
//        // 'rewards' ? encounters ?
//        setDungeon(new Dungeon(type));
//        getDungeons().add(dungeon);
    }

    private ObjType getDungeonTypeFromPlace(Place place) {
        // place.getProperty(prop)
        ObjType type = DataManager.getType(place.getName(), DC_TYPE.DUNGEONS);
        if (type == null) {
            type = DataManager.findType(place.getName(), DC_TYPE.DUNGEONS);
        }
        return type;
    }


}
