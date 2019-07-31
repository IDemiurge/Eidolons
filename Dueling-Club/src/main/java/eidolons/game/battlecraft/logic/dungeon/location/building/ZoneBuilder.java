package eidolons.game.battlecraft.logic.dungeon.location.building;

import main.data.xml.XML_Converter;
import main.system.auxiliary.StringMaster;
import org.w3c.dom.Node;

import static eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.constructBlock;

public class ZoneBuilder {


    public static MapZone buildZone(DungeonPlan plan, Node zoneNode) {

        String name = StringMaster.getWellFormattedString(zoneNode.getNodeName());
        MapZone zone=null ;
        try {
            zone = createZone(plan, name);
        } catch (Exception e) {
            e.printStackTrace();
//                zone = new MapZone(plan.getDungeon(), name );
        } // ++ add coord exceptions
        for (Node node : XML_Converter.getNodeList(zoneNode)) {
            switch (node.getNodeName().toLowerCase()){
                case "bounds":
//                    zone.setBounds(node.getTextContent());
                    break;
                case "blocks":
                    int id=0;
                    for (Node n : XML_Converter.getNodeList(node)) {
                        // if (node.getNodeName().equalsIgnoreCase(BLOCKS_NODE))
                        // blocks = initBlocks(XML_Converter.getStringFromXML(node));

                        MapBlock block = constructBlock(n, id, zone, plan, plan.getDungeon());
                        id++;
                        zone.addBlock(block);
                        plan.getBlocks().add(block);
                    }
                    break;
            }
        }
        return zone;
    }

    private static MapZone createZone(DungeonPlan plan, String name) {
        MapZone zone = new MapZone(name, plan.getDungeon());
        return zone;
    }
}
