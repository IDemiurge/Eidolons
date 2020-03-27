package eidolons.game.battlecraft.logic.dungeon.location.layer;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.data.xml.XML_Converter;
import main.data.xml.XmlNodeMaster;
import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.data.DataUnit;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LayerManager extends DungeonHandler {

    public static final String VFX_NODE = "VFX";
    public static final String ID_NODE = "OBJS";
    public static final String SCRIPT_NODE = "SCRIPTS";
    public static final String META_NODE = "META";
    Set<Layer> layers;
    private Layer current;
    private Layer baseLayer;

    public LayerManager(DungeonMaster master) {
        super(master);
    }

    public void initLayers(String data) {
        initLayers(XML_Converter.getDoc(data));
    }
        public void initLayers(Node main) {

        for (Node node : XmlNodeMaster.getNodeList(main)) {
            String name = node.getNodeName();
            Layer layer = addLayer(name);

            for (Node sub : XmlNodeMaster.getNodeList(node)) {
                initLayerNode(layer, sub.getNodeName().toUpperCase(), sub.getTextContent());
            }

            initLayer(layer);
        }

    }

    private void initLayer(Layer layer) {
//        addObjects etc
    }

    private Layer addLayer(String name) {
        Layer layer = null;
        layers.add(layer = new Layer(name, new LinkedHashSet<>()));
        if (current == null) {
            current = layer;
        }
        return layer;
    }
    public enum LAYER_VALUE{
        active,
        trigger,
        name,
        hidden,

    }
public class LayerData extends DataUnit<LAYER_VALUE>{
    public LayerData(String text) {
        super(text);
    }
}
    private void initLayerNode(Layer layer, String type, String textContent) {
//        XML_Converter.getDoc(textContent)
        switch (type) {
            case META_NODE:
                LayerData data = new LayerData(textContent);
                layer.setActive(data.getBooleanValue(LAYER_VALUE.active));
                layer.setTriggerText(data.getValue(LAYER_VALUE.trigger));
                break;
            case VFX_NODE:
                break;
            case SCRIPT_NODE:
                break;
            case ID_NODE:
                layer.setIds(toIdSet(textContent));
                break;

        }
//            boolean active = getMaster().isLayerActive(name);
//            boolean trigger = getMaster().isLayerActive(name);

        if(layer.isActive())

    {
//                getMaster();
//                LayerInitializer.initLayer(layer);
//                if (layer.isTrigger()){
//                    LayerInitializer.cacheLayer(layer); //prepare trigger conditions? or should only happen via global script func
//                }
    }

}

    private Set<Integer> toIdSet(String textContent) {
        List<String> ids = ContainerUtils.openContainer(textContent, ",");
        return NumberUtils.toIntegers(ids);
    }


    public boolean isLayerOn(String name) {
        Layer layer = getLayer(name);
        return layer.isActive();
    }

    public void toggleLayer(boolean on, String name) {

        Layer layer = getLayer(name);
        if (layer.isActive() == on) {
            return;
        }
        layer.setActive(on);
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

    public Layer getLayer(String name) {
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
            baseLayer = initBaseLayer();
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

    public void setBaseLayer(Layer selectedLayer) {
        baseLayer = selectedLayer;
    }
}
