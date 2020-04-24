package main.level_editor.backend.handlers.structure.layer;

import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.location.layer.LayerManager;
import main.elements.triggers.Trigger;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;

import java.util.LinkedHashSet;
import java.util.Set;

public class LayerHandlerImpl extends LE_Handler implements ILayerHandler {

    public LayerHandlerImpl(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void cloneLayer() {
        Layer layer = getSelectedLayer();
//layer.getIds()
    }

    private Layer getDefaultLayer() {
        return getLayerManager().getBaseLayer();
    }

    private LayerManager getLayerManager() {
        return getGame().getDungeonMaster().getLayerManager();
    }

    private Layer getSelectedLayer() {
        return getModel().getLayer();
    }

    @Override
    public void toggleOn() {

    }

    @Override
    public void toggleVisible() {
        getSelectedLayer().setHidden(getSelectedLayer().isHidden());
    }

    @Override
    public void setTrigger() {
        String text = getDialogHandler().textInput("Input trigger script", getSelectedLayer().
                getTriggerText());
        Trigger trigger = getGame().getBattleMaster().getScriptManager().parseTrigger(text);
        getSelectedLayer().setTriggerText(text);
        getSelectedLayer().setTrigger(trigger);
    }

    @Override
    public void edit() {
//trigger, flags, color, name,
    }

    @Override
    public void add() {
        String name = getDialogHandler().textInput("Layer name", "Layer");
        new Layer(name, new LinkedHashSet<>());

    }

    @Override
    public void remove() {
//kill all units? or merge into default layer?
        remove(getSelectedLayer());
    }

    private void remove(Layer layer) {
        for (Integer id : layer.getIds()) {
            operation(Operation.LE_OPERATION.REMOVE_OBJ, getIdManager().getObjectById(id));
//SCRIPTS?!
        }
//        getLayerManager().remove(layer);

    }
    @Override
    public void mergeWithDefault() {
        merge(getSelectedLayer(), getDefaultLayer());
    }

    private void merge(Layer selectedLayer, Layer defaultLayer) {
        Set<Integer> ids=new LinkedHashSet<>();
        ids.addAll(selectedLayer.getIds());
        ids.addAll(defaultLayer.getIds());
        Layer merged = new Layer("", ids);
        remove(selectedLayer);
        remove(defaultLayer);
    }


    @Override
    public void mergeWith() {
//choose layer
    }
    @Override
    public void setDefault() {
        getLayerManager().setBaseLayer(getSelectedLayer());
    }

    @Override
    public void removeTrigger() {

    }

    public Layer getLayer(String layerName) {
        return getLayerManager().getLayer(layerName);
    }
}
