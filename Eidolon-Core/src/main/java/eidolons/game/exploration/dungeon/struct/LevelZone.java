package eidolons.game.exploration.dungeon.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.game.battlecraft.logic.dungeon.module.Module;
import eidolons.game.exploration.dungeon.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.exploration.dungeon.generator.GeneratorEnums.ZONE_TYPE;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphNode;
import eidolons.game.exploration.dungeon.generator.pregeneration.Pregenerator;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.enums.entity.UnitEnums.UNIT_GROUP;
import main.game.bf.Coordinates;
import main.system.datatypes.WeightMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelZone extends LevelStruct<LevelBlock, LevelBlock> {

    int id;
    List<LevelGraphNode> nodes = new ArrayList<>();
    private ZONE_TYPE type; //TODO   into zoneData "RNG PROPS"!!!
    private ROOM_TEMPLATE_GROUP templateGroup;
    private WeightMap<UNIT_GROUP> unitGroupWeightMap;
    private int nodeCount;
    private Module module;


    public LevelZone(ZONE_TYPE type, ROOM_TEMPLATE_GROUP templateGroup, DUNGEON_STYLE style, int id) {
        this.type = type;
        this.templateGroup = templateGroup;
        setStyle(style);
        this.id = id;

    }

    @Override
    public Set<Coordinates> getCoordinatesSet() {

        return super.getCoordinatesSet();
    }

    public LevelZone(int id) {
        this.id = id;
    }

    @Override
    public Module getParent() {
        return getModule();
    }

    public void addBlock(LevelBlock block) {
        getSubParts().add(block);
    }
    @Override
    public String toString() {
        return "Zone #" + id + ":" +
                type +
                " with style " + getStyle() + ", " +
                getSubParts().size() +
                "blocks";
    }

    public ZONE_TYPE getType() {
        return type;
    }

    public void setType(ZONE_TYPE type) {
        this.type = type;
    }

    public ROOM_TEMPLATE_GROUP getTemplateGroup() {
        return templateGroup;
    }

    public void setTemplateGroup(ROOM_TEMPLATE_GROUP templateGroup) {
        this.templateGroup = templateGroup;
    }

    public int getIndex() {
        return id;
    }

    public WeightMap<UNIT_GROUP> getUnitGroupWeightMap() {
        return unitGroupWeightMap;
    }

    public void setUnitGroupWeightMap(WeightMap<UNIT_GROUP> unitGroupWeightMap) {
        this.unitGroupWeightMap = unitGroupWeightMap;
    }

    public void nodeAdded(LevelGraphNode node) {
        if (node.getZoneIndex() == id)
            return;
        if (Pregenerator.TEST_MODE)
            nodes.add(node);
        nodeCount++;
        node.setZoneIndex(id);
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setData(ZoneData data) {
        this.data = data;
    }

    public ZoneData getData() {
        return (ZoneData) super.getData();
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
