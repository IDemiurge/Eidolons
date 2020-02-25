package eidolons.game.battlecraft.logic.dungeon.location.layer;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.data.xml.XML_Converter;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.ListMaster;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Set;

public class LayerManager extends DungeonHandler {

    Set<Layer> layers;
    private Layer current;

    public LayerManager(DungeonMaster master) {
        super(master);
    }

    public void initLayers(Node main) {
        for (Node node : XML_Converter.getNodeList(main)) {
            String name = node.getNodeName();
            Set<Integer> ids = toIdSet(node.getTextContent());
            Layer layer = null;
            layers.add(layer = new Layer(name, ids));
            if (current == null) {
                current = layer;
            }
            //script vfx
        }

    }

    private Set<Integer> toIdSet(String textContent) {
        List<String> ids = ContainerUtils.openContainer(textContent, ",");
        return NumberUtils.toIntegers(ids);
    }


    public boolean isLayerOn(String name) {
        Layer layer = getLayer(name);
        return layer.isOn();
    }

    public void toggleLayer(boolean on, String name) {

        Layer layer = getLayer(name);
        if (layer.isOn() == on) {
            return;
        }
        layer.setOn(on);
        for (Integer id : layer.getIds()) {
            //translate to real objects?

        }
    }

    public void activateLayer(String name) {
        toggleLayer(true, name);
    }
    public void deactivateLayer(String name) {
        toggleLayer(false, name);
    }

    private Layer getLayer(String name) {
        for (Layer layer : layers) {
            if (layer.getName().equalsIgnoreCase(name)) {
                return layer;
            }
        }
        return null;
    }

    public void addToCurrent(Integer id, MicroObj obj) {
        current.getIds().add(id);
//        current.getObjMap().put(id, obj);
    }

    public void removeFromCurrent(Integer id, Obj obj) {
    }
}
