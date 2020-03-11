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

    public static final String VFX_NODE = "VFX";
    public static final String ID_NODE = "OBJS";
    public static final String SCRIPT_NODE = "SCRIPTS";
    Set<Layer> layers;
    private Layer current;
    private Layer baseLayer;

    public LayerManager(DungeonMaster master) {
        super(master);
    }

    public void initLayers(Node main) {
        Set<Integer> ids = null ;
        getMaster().getObjIdMap();
        getMaster().getIdTypeMap();
        for (Node node : XML_Converter.getNodeList(main)) {
            for (Node sub : XML_Converter.getNodeList(node)) {
                switch (sub.getNodeName().toUpperCase()) {
                    case VFX_NODE:
                        break;
                    case SCRIPT_NODE:
                        break;
                    case ID_NODE:
                        ids = toIdSet(node.getTextContent());
                        break;

                }
            }
            String name = node.getNodeName();
            ids = toIdSet(node.getTextContent());
            boolean active = getMaster().isLayerActive(name);
//            boolean trigger = getMaster().isLayerActive(name);
            Layer layer = null;
            layers.add(layer = new Layer(name, ids));
            if (current == null) {
                current = layer;
            }
            if (layer.isActive()){
                getMaster();
                LayerInitializer.initLayer(layer);
                if (layer.isTrigger()){
                    LayerInitializer.cacheLayer(layer); //prepare trigger conditions? or should only happen via global script func
                }
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
