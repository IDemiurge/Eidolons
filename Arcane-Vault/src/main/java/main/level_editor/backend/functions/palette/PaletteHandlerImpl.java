package main.level_editor.backend.functions.palette;

import com.google.inject.internal.util.ImmutableList;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.core.Eidolons;
import eidolons.game.module.generator.GeneratorEnums;
import eidolons.game.module.generator.model.RoomTemplateMaster;
import eidolons.game.module.generator.tilemap.TileMapper;
import eidolons.libgdx.utils.GdxDialogMaster;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XML_Formatter;
import main.data.xml.XmlNodeMaster;
import main.data.xml.XmlStringBuilder;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.brush.LE_BrushType;
import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.panels.palette.tree.BlockTemplateTree;
import main.level_editor.gui.panels.palette.tree.PaletteTree;
import main.level_editor.gui.screen.LE_Screen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.threading.WaitMaster;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PaletteHandlerImpl extends LE_Handler implements IPaletteHandler {

    public static final String WALL_PLACEHOLDER = "Wall Placeholder";
    public static final String ALT_WALL_PLACEHOLDER = "Alt Wall Placeholder";
    private Map<String, List<ObjType>> workspaceTypeMap;

    public PaletteHandlerImpl(LE_Manager manager) {
        super(manager);
        initWorkspaceTypeMap();
    }

    @Override
    public void afterLoaded() {
        List<ObjType> types = DataManager.getTypesSubGroup(DC_TYPE.BF_OBJ, "Placeholder");
        createPalette(new LinkedHashSet<>(types), "Placeholders");

    }

    private void initWorkspaceTypeMap() {
        workspaceTypeMap = new LinkedHashMap<>();
        List<File> files = FileManager.getFilesFromDirectory(PathFinder.getEditorWorkspacePath(),
                false, false, false);
        for (File file : files) {
            String data = FileManager.readFile(file);
            List<ObjType> types = new ArrayList<>();
            try {
                if (data.contains("</")) {
                    if (data.contains("METADATA:")) {
                        data = data.split("METADATA:")[0];
                    }
                    Document doc = XML_Converter.getDoc(data);
                    for (Node n : XmlNodeMaster.getNodeListFromFirstChild(doc, true)) {
                        String s = XmlNodeMaster.getNodeList(n, true).stream().map(
                                node -> XML_Formatter.restoreXmlNodeName(node.getNodeName()
                                )).collect(Collectors.joining(";"));
                        types.addAll(DataManager.toTypeList(s, C_OBJ_TYPE.BF_OBJ_LE));
                    }
                } else {
                    types.addAll(DataManager.toTypeList(data, C_OBJ_TYPE.BF_OBJ_LE));
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            String fileName = StringMaster.cropFormat(file.getName());
            workspaceTypeMap.put(fileName, types);
        }
    }

    @Override
    public void fromBlock() {
        if (getModel().getLastSelectedStruct() != null) {
            createPaletteFromObjsOnCoordinates(getModel().getLastSelectedStruct().getCoordinatesSet());
        } else
            createPaletteFromObjsOnCoordinates(getSelectionHandler().getSelection().getCoordinates());
    }

    @Override
    public void fromZone() {
        if (getModel().getZone() == null) {
            return;
        }
        //TODO
    }

    @Override
    public void fromAll() {
        createPaletteFromObjsOnCoordinates(getGame().getCoordinates());
    }

    private void createPaletteFromObjsOnCoordinates(Set<Coordinates> coordinatesSet) {
        Set<BattleFieldObject> set = new LinkedHashSet<>();
        coordinatesSet =
                coordinatesSet.stream().sorted(SortMaster.getGridCoordSorter()).collect(
                        Collectors.toCollection(() -> new LinkedHashSet<>()));
        for (Coordinates coordinates : coordinatesSet) {
            set.addAll(getGame().getObjectsOnCoordinateAll(coordinates));
        }

        createPaletteFromObjs(set.stream().map(obj -> obj.getType()).collect(Collectors.toCollection(() -> new LinkedHashSet<>())));
    }

    private void createPaletteFromObjs(Set<ObjType> set) {
        String name = GdxDialogMaster.inputText(set.size() + " types", "workspace");
        if (StringMaster.isEmpty(name)) {
            return;
        }
        //        NameMaster.getUniqueVersionedFileName()
        createPalette(set, name);
        reload();
    }

    private void createPalette(Set<ObjType> set, String name) {
        String contents = buildWorkspace(set);
        FileManager.write(contents,
                PathFinder.getEditorWorkspacePath() + name + ".xml");
    }

    private String buildWorkspace(Set<ObjType> set) {
        XmlStringBuilder xmlStringBuilder = new XmlStringBuilder();
        xmlStringBuilder.open("Types");
        appendTypes(set, DC_TYPE.ENCOUNTERS, xmlStringBuilder);
        appendTypes(set, DC_TYPE.BF_OBJ, xmlStringBuilder);
        appendTypes(set, DC_TYPE.UNITS, xmlStringBuilder);

        xmlStringBuilder.close("Types");

        return xmlStringBuilder.toString();
    }

    void appendToPalette(String name, Set<ObjType> toAppend, boolean negative) {
        String path = PathFinder.getEditorWorkspacePath() + name + ".xml";
        Set<ObjType> objTypes = new LinkedHashSet<>(workspaceTypeMap.get(name));
        if (negative)
            objTypes.removeAll(toAppend);
        else {
            objTypes.addAll(toAppend);
        }

        String contents = buildWorkspace(objTypes);

        FileManager.write(contents, path);

        reload();
        WaitMaster.WAIT(400);
        selectPalette(name);
    }


    private void selectPalette(String name) {
        GuiEventManager.trigger(GuiEventType.LE_PALETTE_SELECTION, name);
    }

    private void selectBlockPalette(List<String> name) {
        GuiEventManager.trigger(GuiEventType.LE_BLOCK_PALETTE_SELECTION, name);
    }

    private void reload() {
        initWorkspaceTypeMap();
        Eidolons.onGdxThread(() -> LE_Screen.getInstance().getGuiStage().getPalettePanel().reload());
        GuiEventManager.trigger(GuiEventType.LE_PALETTE_RESELECT);
    }

    private void appendTypes(Set<ObjType> set, DC_TYPE type, XmlStringBuilder xmlStringBuilder) {
        Set<String> objs =
                set.stream().filter(obj -> obj.getType().getOBJ_TYPE_ENUM() == type)
                        .sorted(getTypeSorter(type)).map(obj -> obj.getType().getName()).collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
        if (objs.isEmpty()) {
            return;
        }
        xmlStringBuilder.open(type.name());
        for (String bfObj : objs) {
            xmlStringBuilder.appendNode("", bfObj);
        }
        xmlStringBuilder.close(type.name());
    }

    private Comparator<? super ObjType> getTypeSorter(DC_TYPE type) {
        return new Comparator<ObjType>() {
            @Override
            public int compare(ObjType o1, ObjType o2) {
                switch (type) {
                    case UNITS:
                        return SortMaster.compareValue(PARAMS.POWER, false, o1, o2);
                    case BF_OBJ:
                    case ENCOUNTERS:
                        return SortMaster.compareAlphabetically(o1.getGroup() + o1.getName()
                                , o2.getGroup() + o2.getName());
                }
                return 0;
            }
        };
    }


    @Override
    public void areaToBlock() {
        BlockData data = null;
        Set<Coordinates> coordinates = getModel().getSelection().getCoordinates();
        if (coordinates.size() < 9) {
            if (getModel().getBlock() != null) {
                data = getModel().getBlock().getData();
                coordinates = getModel().getBlock().getCoordinatesSet();
            } else return;
        }
        List<String> tiles = coordinates.stream()
                .sorted(SortMaster.getGridCoordSorter())
                .map(c -> getTileForCoordinate(c)).collect(Collectors.toList());

        int w = CoordinatesMaster.getWidth(coordinates);
        int h = CoordinatesMaster.getHeight(coordinates);
        String[][] tileMap = new String[w][h];
        int index = 0;
        for (int i = 0; i < w; i++) {
            tileMap[i] = new String[h];
            for (int j = 0; j < h; j++) {
                tileMap[i][j] = tiles.get(index++);
            }
        }
        //ask for types, for read palette selection
        //        LocationBuilder.ROOM_TYPE type=null ;
        //        GeneratorEnums.ROOM_TEMPLATE_GROUP group=null ;

        String group = BlockTemplateTree.room_group;
        String type = BlockTemplateTree.room_type;
        if (group == null) {
            GeneratorEnums.ROOM_TEMPLATE_GROUP e = getDialogHandler().chooseEnum(GeneratorEnums.ROOM_TEMPLATE_GROUP.class);
            if (e == null) {
                group = GeneratorEnums.ROOM_TEMPLATE_GROUP.VOID_MAZE.toString();
            } else {
                group = e.toString();
                BlockTemplateTree.room_group = group;
            }
        }
        if (type == null) {
            // LocationBuilder.ROOM_TYPE e = getDialogHandler().chooseEnum(LocationBuilder.ROOM_TYPE.class);
            // if (e == null) {
            type = LocationBuilder.ROOM_TYPE.THRONE_ROOM.toString();
            // } else
            //     type = e.toString();
        }
        String name = group + "/" + type;
        String filePath = PathFinder.getMapBlockFolderPath() + name + ".txt";
        StringBuilder contents = new StringBuilder();
        contents.append(FileManager.readFile(filePath));

        contents.append("\n" + TileMapper.createTileMap(tileMap).toString() + "\n" +
                RoomTemplateMaster.MODEL_SPLITTER);

        //ask to convert selection into a block!..
        //        LevelBlock block = getStructureManager().selectionToBlock();
        //        block.getData().setValue(LevelStructure.BLOCK_VALUE.room_type, type);
        //tilemap format vs raw -

        //reload to palette?
        FileManager.write(contents.toString(), filePath);

        selectBlockPalette(ImmutableList.of(group, type));
        reload();
    }

    private String getTileForCoordinate(Coordinates c) {
        Set<BattleFieldObject> objects = getGame().getObjectsOnCoordinateNoOverlaying(c);
        if (objects.isEmpty()) {
            if (getGame().getCellByCoordinate(c).isVOID()) {
                return GeneratorEnums.ROOM_CELL.VOID.symbol;
            }
            return GeneratorEnums.ROOM_CELL.FLOOR.symbol;
        }
        //art, doors, ...
        for (BattleFieldObject object : objects) {
            // if (object.isWall()) {
                return GeneratorEnums.ROOM_CELL.WALL.symbol;
            // }
            // for (GeneratorEnums.ROOM_CELL value : GeneratorEnums.ROOM_CELL.values()) {
            //     if (PlaceholderGenerator.getPlaceholderName(value).equalsIgnoreCase(object.getName())) {
            //         return value.getSymbol();
            //     }
            // }
        }
        return "?";
    }

    @Override
    public void createPalette() {
        //displayed as tree?
        //what is the data, txt files? yeah, but maybe with folder structure!

        //path via layered grouping...

        //        selectedPalettePath =  getModel().getPaletteSelection();
        //        if ( == null ){
        //            path = getDefaultPath() + name;
        //        }
        //        //paletteCreationDialog
        //
        //        FileManager.write(types, path);
    }

    @Override
    public void removePalette() {
        String filepath = PathFinder.getEditorWorkspacePath() + getPaletteName() + ".xml";
        Path path = FileManager.getPath(new File(filepath));
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reload();

    }


    @Override
    public void mergePalettes() {

    }

    @Override
    public void clonePalette() {

    }

    @Override
    public void addToPalette() {
        modifyPalette(false);
    }

    private void modifyPalette(boolean negative) {
        Set<ObjType> collect = getSelectionHandler().getSelection().getIds().stream().map(id ->
                getIdManager().getObjectById(id).getType()).collect(Collectors.toCollection(() -> new LinkedHashSet<>()));
        appendToPalette(getPaletteName(), collect, negative);
    }

    private String getPaletteName() {
        TreeX tree = LE_Screen.getInstance().getGuiStage().getPalettePanel().getCustomPalette().getTree();
        return ((PaletteTree) tree).getLastSelectedName();
    }

    @Override
    public void removeFromPalette() {
        modifyPalette(true);
    }

    public List<ObjType> getTypesForTreeNode(DC_TYPE TYPE, Object object) {
        List<ObjType> list = new ArrayList<>();
        if (TYPE == null) {
            return getWorkspaceTypeMap().get(object.toString());
        } else if (object instanceof DC_TYPE) {
            list = DataManager.getTypes(((DC_TYPE) object));
        } else {
            if (object instanceof String) {
                list = DataManager.getTypesSubGroup(TYPE, object.toString());
                if (list.isEmpty()) {
                    list = DataManager.getTypesGroup(TYPE, object.toString());
                }
            } else {

            }
        }
        return list;
    }

    public Map<String, List<ObjType>> getWorkspaceTypeMap() {
        return workspaceTypeMap;
    }

    public ObjType getFiller(LE_BrushType brushType) {
        switch (brushType) {
            case wall:
                return DataManager.getType(WALL_PLACEHOLDER);
            case alt_wall:
                return DataManager.getType(ALT_WALL_PLACEHOLDER);
        }
        return null;
    }

}


