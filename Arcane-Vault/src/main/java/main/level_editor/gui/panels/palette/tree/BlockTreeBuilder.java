package main.level_editor.gui.panels.palette.tree;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomTemplateMaster;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import main.data.filesys.PathFinder;
import main.level_editor.LevelEditor;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockTreeBuilder {

    public List<PaletteNode> build() {
        List<PaletteNode> list=     new ArrayList<>() ;
        PaletteNode templates = new PaletteNode("Templates");
        PaletteNode custom = new PaletteNode("Custom");
        list.add(custom);
        list.add(templates);
        Set<PaletteNode> topSet = new LinkedHashSet<>();
        templates.setChildrenSet(topSet);
        Set<PaletteNode> customSet = new LinkedHashSet<>();
        custom.setChildrenSet(customSet);

        for (ROOM_TEMPLATE_GROUP group :ROOM_TEMPLATE_GROUP.values()) {
            Set<PaletteNode> subNodes = new LinkedHashSet<>();
            for (LocationBuilder.ROOM_TYPE type : LocationBuilder.ROOM_TYPE.values()) {
                String contents = FileManager.readFile(
                        PathFinder.getMapBlockFolderPath() +
                                group + "/" + type+".txt");
                if (contents.isEmpty()) {
                    continue;
                }
                Set<RoomModel> models=     new LinkedHashSet<>();
                for (String room : contents.split(RoomTemplateMaster.MODEL_SPLITTER)) {
                    room = room.trim();
                    String[][] cells = TileMapper.toSymbolArray(room);
                    models.add(new RoomModel(cells, type, GeneratorEnums.EXIT_TEMPLATE.CROSSROAD));

                }
                subNodes.add(new PaletteNode(models){
                    @Override
                    public String toString() {
                        return type.toString();
                    }
                });
            }
            if (!subNodes.isEmpty()) {
                customSet.add(new PaletteNode(subNodes, group.toString()));
            }

        }

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

        return list;
    }
}
