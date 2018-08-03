package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import main.data.xml.XML_Converter;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelZone extends LevelLayer<LevelBlock>{

    private  ZONE_TYPE type;
    private ROOM_TEMPLATE_GROUP templateGroup ;
    private DUNGEON_STYLE style;
    int id;

    public LevelZone(ZONE_TYPE type, ROOM_TEMPLATE_GROUP templateGroup, DUNGEON_STYLE style, int id) {
        this.type = type;
        this.templateGroup = templateGroup;
        this.style = style;
        this.id = id;
    }

    @Override
    public String toXml() {
        String xml = "";
        //props
        for (LevelBlock block : getSubParts()) {
            xml += block.toXml();
        }
        xml = XML_Converter.wrap("Blocks", xml);
        xml = XML_Converter.wrap("Zone_"+id, xml);
        return xml;
    }

    public ZONE_TYPE getType() {
        return type;
    }

    public ROOM_TEMPLATE_GROUP getTemplateGroup() {
        return templateGroup;
    }

    public DUNGEON_STYLE getStyle() {
        return style;
    }

    public void setStyle(DUNGEON_STYLE style) {
        this.style = style;
    }

    public void setTemplateGroup(ROOM_TEMPLATE_GROUP templateGroup) {
        this.templateGroup = templateGroup;
    }

    public int getIndex() {
        return id;
    }
}
