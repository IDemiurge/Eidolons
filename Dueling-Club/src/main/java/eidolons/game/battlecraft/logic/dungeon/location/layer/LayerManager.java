package eidolons.game.battlecraft.logic.dungeon.location.layer;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.data.xml.XML_Converter;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LayerManager extends DungeonHandler {

    Set<Layer> layers;
    private Layer current;
    private Layer baseLayer;

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
        if (current == null) {
            return;
        }
        current.getIds().add(id);
//        current.getObjMap().put(id, obj);
    }
    public Layer getCurrent() {
        if (current == null) {
            return getBaseLayer();
        }
        return current;
    }

    public Set<Layer> getLayers() {
        return layers;
    }

    public Layer getBaseLayer() {
        if (baseLayer == null) {
            baseLayer= initBaseLayer();
        }
        return baseLayer;
    }

    private Layer initBaseLayer() {
        Set<Integer> ids = new LinkedHashSet<>(getMaster().getObjIdMap().keySet());
        baseLayer = new Layer("Base", ids);
        return baseLayer;
    }

    public void removeFromCurrent(Integer id, Obj obj) {
    }
}
