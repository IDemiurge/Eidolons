package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileConverter.DUNGEON_STYLE;
import eidolons.system.audio.MusicMaster.AMBIENCE;
import main.content.CONTENT_CONSTS.COLOR_THEME;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.data.xml.XML_Converter;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelZone extends LevelLayer<LevelBlock>{

    private  ZONE_TYPE type;
    private ROOM_TEMPLATE_GROUP templateGroup ;
    private DUNGEON_STYLE style;
    int id;
    private WeightMap<UNIT_GROUP> unitGroupWeightMap;

    public LevelZone(DUNGEON_STYLE style, AMBIENCE ambience,
                     COLOR_THEME colorTheme, int globalIllumination, int id) {
        super(null , ambience, colorTheme, globalIllumination);
        this.style = style;
        this.id = id;
    }

    public LevelZone(ZONE_TYPE type, ROOM_TEMPLATE_GROUP templateGroup, DUNGEON_STYLE style, int id) {
        this.type = type;
        this.templateGroup = templateGroup;
        this.style = style;
        this.id = id;

    }

    public LevelZone(int id) {
        this.id = id;
    }

    @Override
    public String toXml() {
        String xml = "";
        //props
        int n=0;
        for (LevelBlock block : getSubParts()) {
            xml +=XML_Converter.wrap("Block"+n++, block.toXml());
        }
        xml = XML_Converter.wrap("Blocks", xml);
        String values="";
        values+=XML_Converter.wrap(RngXmlMaster.ZONE_STYLE_NODE, style.name());
        values+=XML_Converter.wrap(RngXmlMaster.ZONE_TYPE_NODE, type.name());
        values+=XML_Converter.wrap(RngXmlMaster.ZONE_TEMPLATE_GROUP_NODE, templateGroup.name());
        xml+= XML_Converter.wrap(RngXmlMaster.VALUES_NODE, values);
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

    public void setUnitGroupWeightMap(WeightMap<UNIT_GROUP> unitGroupWeightMap) {
        this.unitGroupWeightMap = unitGroupWeightMap;
    }

    public WeightMap<UNIT_GROUP> getUnitGroupWeightMap() {
        return unitGroupWeightMap;
    }

    public void setType(ZONE_TYPE type) {
        this.type = type;
    }
}
