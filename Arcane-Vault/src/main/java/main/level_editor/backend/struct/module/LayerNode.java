package main.level_editor.backend.struct.module;

import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import main.level_editor.LevelEditor;
import main.level_editor.gui.tree.data.LayeredData;

import java.util.Set;
import java.util.stream.Collectors;

public class LayerNode implements LayeredData<LayeredData> {
    private final Set<LayeredData> objs;

    public LayerNode(Layer layer) {
        objs =
        layer.getIds().stream().map(id ->
                new ObjNode(LevelEditor.getCurrent().getManager().getIdManager().getObjectById(id))).
                collect(Collectors.toSet());


//        layer.getScripts().stream().map(id ->
//                new ObjNode(LevelEditor.getCurrent().getManager().getIdManager().getObjectById(id))).
//                collect(Collectors.toSet());
    }

    @Override
    public Set<LayeredData> getChildren() {
        return objs;
    }
}
