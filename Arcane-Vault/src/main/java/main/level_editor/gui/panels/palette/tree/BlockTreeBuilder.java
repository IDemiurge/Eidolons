package main.level_editor.gui.panels.palette.tree;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import main.level_editor.LevelEditor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockTreeBuilder {

    public PaletteNode build() {

        PaletteNode root = new PaletteNode("Templates");
        Set<PaletteNode> topSet = new LinkedHashSet<>();
        root.setChildrenSet(topSet);
        RoomTemplateMaster manager = LevelEditor.getManager().getStructureManager().
                getRoomTemplateManager();

        for (ROOM_TEMPLATE_GROUP group : manager.getModels().keySet()) {
            PaletteNode groupNode;
            topSet.add(groupNode = new PaletteNode(group.toString()));
            Set<PaletteNode> subNodes = new LinkedHashSet<>();
            groupNode.setChildrenSet(subNodes);
            Set<RoomModel> roomModels = manager.getModels().get(group);
            if (roomModels == null) {
                continue;
            }
            roomModels.removeIf(model -> model == null);
            for (LocationBuilder.ROOM_TYPE value : LocationBuilder.ROOM_TYPE.values()) {
                Set<RoomModel> models = roomModels.stream().filter(roomModel -> roomModel.getType() == value)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                if (models.isEmpty()) {
                    continue;
                }
                subNodes.add(new PaletteNode(models){
                    @Override
                    public String toString() {
                        return value.toString();
                    }
                });
            }
        }

        return root;
    }
}
