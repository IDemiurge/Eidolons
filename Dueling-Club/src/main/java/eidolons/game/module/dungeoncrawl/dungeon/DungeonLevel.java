package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.xml.XML_Converter;

/**
 * Created by JustMe on 7/20/2018.
 */
public class DungeonLevel extends LevelLayer<LevelZone> {
    TileMap tileMap;
    LevelModel model;
    SUBLEVEL_TYPE type;
    LOCATION_TYPE locationType;

    public DungeonLevel(TileMap tileMap, LevelModel model, SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        this.tileMap = tileMap;
        this.model = model;
        this.type = type;
        this.locationType = locationType;
    }

    @Override
    public String toXml() {
        String xml = "";
        //props
        for (LevelZone levelZone : getSubParts()) {
            xml += levelZone.toXml();
        }
        xml = XML_Converter.wrap(LocationBuilder.ZONES_NODE, xml);
        xml = XML_Converter.wrap("Level", xml);
        return xml;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public LevelModel getModel() {
        return model;
    }

    public SUBLEVEL_TYPE getType() {
        return type;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }
}
