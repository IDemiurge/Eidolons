package main.game.logic.dungeon.editor;

import main.content.DC_TYPE;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import main.data.DataManager;
import main.data.XList;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.Location;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.BLOCK_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.DUNGEON_TEMPLATES;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.battlecraft.logic.dungeon.location.building.BuildHelper;
import eidolons.game.battlecraft.logic.dungeon.location.building.BuildHelper.BUILD_PARAMS;
import eidolons.game.battlecraft.logic.dungeon.location.building.BuildHelper.BuildParameters;
import eidolons.game.battlecraft.logic.dungeon.location.building.DungeonPlan;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapZone;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.game.bf.Coordinates;
import main.game.bf.DirectionMaster;
import main.swing.generic.components.editors.FileChooser;
import main.swing.generic.components.editors.TextEditor;
import main.swing.generic.components.editors.lists.ListChooser;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;
import eidolons.system.math.DC_PositionMaster;
import main.system.math.PositionMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.w3c.dom.Document;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LE_MapMaster {

    private MapBlock block;
    private MapBlock blockBuffer;
    private FileChooser blockChooser;

    public static void generateNew(Mission mission, Level level, boolean alt) {
        DUNGEON_TEMPLATES template = new EnumMaster<DUNGEON_TEMPLATES>().retrieveEnumConst(
                DUNGEON_TEMPLATES.class, ListChooser.chooseEnum(DUNGEON_TEMPLATES.class));
        if (template == null) {
            if (!DialogMaster.confirm("Generate by standard template?")) {
                return;
            }
        }
        Dungeon dungeon = level.getDungeon();

//        dungeon.setPlan(null);
        // ++ size, fills, zone prefs ++ BUILD PARAMS
        dungeon.setTemplate(template);

        initBuildParams(alt, level.getLocation());
//        new DungeonMapGenerator()
//        new Location()

        DungeonMaster master = level.getDungeonMaster();

        LevelEditor.getSimulation().getUnits().clear();
        level.init();
        level.getMinimap().init();
        level.getMinimap().getGrid().refresh();
    }

    public static void transform() {
        TRANSFORM transform = new EnumMaster<TRANSFORM>().retrieveEnumConst(TRANSFORM.class,
                ListChooser.chooseEnum(TRANSFORM.class));
        if (transform == null) {
            return;
        }
        Location dungeon = LevelEditor.getCurrentLevel().getLocation();
        DungeonPlan plan = dungeon.getPlan();
        switch (transform) {
            case FLIP_X:
                plan.setFlippedX(!plan.isFlippedX());
                break;
            case FLIP_Y:
                plan.setFlippedY(!plan.isFlippedY());
                break;
            case ROTATE:
                plan.setRotated(!plan.isRotated());
                break;
        }
        dungeon.setPlan(plan);
        plan = new LocationBuilder().transformDungeonPlan(plan);
        dungeon.setPlan(plan);
        LevelEditor.getSimulation().getUnits().clear();
        LevelEditor.getCurrentLevel().init();
        LevelEditor.getCurrentLevel().getMinimap().init();
        LevelEditor.getCurrentLevel().getMinimap().getGrid().refresh();
    }

    public static BuildParameters initBuildParams(boolean empty, Location location) {
        BuildParameters params = new BuildHelper(location).new BuildParameters(empty);
        params.setValue(BUILD_PARAMS.WIDTH, "" + location.getCellsX());
        params.setValue(BUILD_PARAMS.HEIGHT, "" + location.getCellsY());
        if (location.getPlan() != null) {
            params.setValue(BUILD_PARAMS.WIDTH_MOD, "" + location.getPlan().getWidthMod());
            params.setValue(BUILD_PARAMS.HEIGHT_MOD, "" + location.getPlan().getHeightMod());
            params.setValue(BUILD_PARAMS.SIZE_MOD, "" + location.getPlan().getSizeMod());
        }

        String data = params.getDataMapFormat();
        data = TextEditor.inputTextLargeField("Alter build parameters...", data);
        if (data != null) {

            params = new BuildHelper(location).new BuildParameters(data);
            location.setParam(PARAMS.BF_WIDTH, params.getIntValue(BUILD_PARAMS.WIDTH));
            location.setParam(PARAMS.BF_HEIGHT, params.getIntValue(BUILD_PARAMS.HEIGHT));
            location.setProperty(PROPS.FILLER_TYPE, params.getValue(BUILD_PARAMS.FILLER_TYPE));
            location.getType().setParam(PARAMS.BF_WIDTH, params.getIntValue(BUILD_PARAMS.WIDTH));
            location.getType().setParam(PARAMS.BF_HEIGHT, params.getIntValue(BUILD_PARAMS.HEIGHT));
            location.getType().setProperty(PROPS.FILLER_TYPE,
                    params.getValue(BUILD_PARAMS.FILLER_TYPE));
            location.setBuildParams(params);
        }
        GuiManager.setCurrentLevelCellsX(location.getCellsX());
        GuiManager.setCurrentLevelCellsY(location.getCellsY());

        return params;
    }

    public static Coordinates pickCoordinate() {
        return LevelEditor.getMouseMaster().pickCoordinate();
    }

    public static List<Coordinates> pickCoordinates() {
        return pickCoordinates(false);
    }

    public static List<Coordinates> pickCoordinates(boolean diagonal) {
        Coordinates c = pickCoordinate();
        if (c == null) {
            return null;
        }
        Coordinates c1 = pickCoordinate();
        if (c1 == null) {
            return null;
        }

        int x1 = Math.min(c.x, c1.x) - 1;
        int x2 = Math.max(c.x, c1.x);
        int y1 = Math.min(c.y, c1.y) - 1;
        int y2 = Math.max(c.y, c1.y);

        if (diagonal) {
            if (PositionMaster.inLineDiagonally(c, c1)) {
                return DC_PositionMaster.getLine(false,
                        DirectionMaster.getRelativeDirection(c, c1), c, PositionMaster.getDistance(
                                c, c1));
            }
        }

        return new ArrayList<>(CoordinatesMaster.getCoordinatesWithin(x1, x2, y1, y2));

    }

    public void alterSize(boolean x, String newValue) {
        Dungeon dungeon = LevelEditor.getCurrentLevel().getDungeon();
        int newSize = StringMaster.getInteger(newValue);
        Integer size = x ? dungeon.getCellsX() : dungeon.getCellsY();
        boolean reduce = newSize < size;
        String TRUE = x ? "RIGHT" : "DOWN";
        String FALSE = x ? "LEFT" : "UP";
        DialogMaster.ask("Where to " + (reduce ? "crop" : "expand") + "?", true, TRUE, FALSE,
                "BOTH");
        // save-load onto new map of diff size with offset?
        Boolean plus_negative_both_sides = (Boolean) WaitMaster
                .waitForInput(WAIT_OPERATIONS.OPTION_DIALOG);

		/*
         * expand zone
		 *
		 * introduce offset
		 *
		 */

        LevelEditor.getCurrentLevel().getMinimap().init();
        LevelEditor.getCurrentLevel().getMinimap().getGrid().refresh();
    }

    public void addZone() {
        // choose c1 and c2
    }

    public void removeZone(MapZone activeZone) {
        // brutal - all blocks are deleted; normally one would edit it ...
        SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
    }

    public void moveBlock(MapBlock block, int offsetX, int offsetY) {
        for (Obj obj : block.getObjects()) {
            // moveObj(obj, offsetX, offsetY);
        }
        List<Coordinates> coordinates = new ArrayList<>();
        for (Coordinates c : block.getCoordinates()) {
            coordinates.add(new Coordinates(c.x + offsetX, c.y + offsetY));
        }
        block.setCoordinates(coordinates);

    }

    public void moveBlock(MapBlock selectedBlock) {
        SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
    }

    public void moveZone(MapZone zone) {
        SoundMaster.playStandardSound(STD_SOUNDS.FAIL);
        Coordinates c = pickCoordinate();
        int offsetX = c.x - zone.getX1();
        int offsetY = c.y - zone.getY1();
        for (MapBlock b : zone.getBlocks()) {
            moveBlock(b, offsetX, offsetY);
        }
    }

    public void newRoom(List<Coordinates> coordinates) {
        ROOM_TYPE type = chooseRoomType();
        if (type == null) {
            return;
        }
        if (coordinates == null) {
            coordinates = pickCoordinates();
        }
        MapZone zone = pickZone(coordinates);
        MapBlock b = new MapBlock(getPlan().getBlocks().size(), BLOCK_TYPE.ROOM, zone, getPlan(),
                coordinates);
        b.setRoomType(type);
        addBlock(b);

    }

    public void newRoom() {
        newRoom(null);
    }

    public MapBlock getBlock() {
        return block;
    }

    public void setBlock(MapBlock block) {
        this.block = block;
    }

    private MapZone pickZone(List<Coordinates> coordinates) {
        zLoop:
        for (MapZone z : getPlan().getZones()) {
            for (Coordinates c : coordinates) {
                if (!CoordinatesMaster
                        .isWithinBounds(c, z.getX1(), z.getX2(), z.getY1(), z.getY2())) {
                    continue zLoop;
                }
                // else zones.add(z);
            }
        }
        return null; // TODO choose
    }

    public void clearArea(List<Coordinates> coordinates) {
        clearArea(coordinates, false);

    }

    public void clearArea(List<Coordinates> coordinates, boolean ignoreBlockChoice) {
        if (!ignoreBlockChoice) {
            ArrayList<MapBlock> blocks = new ArrayList<>();
            for (Coordinates c : CoordinatesMaster.getCornerCoordinates(coordinates)) {
                MapBlock b = getLevel().getBlockForCoordinate(c, true, blocks);
                if (b != null) {
                    if (!blocks.contains(b)) {
                        blocks.add(b);
                    }
                }
            }
            if (blocks.size() > 1) {
                XList<Object> list = new XList<>(blocks.toArray());
                list.add(0, "New");
                list.add("Fit for each");
                int i = DialogMaster.optionChoice(list.toArray(),
                        "Which block to add coordinates to?");
                if (i > 0 && i != list.size() - 1) {
                    setBlock(blocks.get(i - 1));
                } else {
                    if (i == 0) {
                        if (DialogMaster.confirm("Create new block?")) {
                            newRoom(coordinates);
                        }
                    }

                }
            }
        }

        try {
            LE_ObjMaster.removeObjects(coordinates);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            setBlock(null);
        }
    }

    public void addBlock(MapBlock block) {
        clearArea(block.getCoordinates(), true);
        Set<MapZone> zones = new HashSet<>();
        // Set<MapBlock> blocks = new HashSet<>();
        // remove from other blocks?
        for (Coordinates c : block.getCoordinates()) {
            MapBlock b = getPlan().getBlockByCoordinate(c);
            if (b != null) {
                b.getCoordinates().remove(c);
                zones.add(b.getZone());
                // blocks.add(b);
                b.getCoordinates().remove(c); // hard to Undo...
                // perhaps I can keep a copy of the *PLAN* or even *LEVEL* to
                // return to?
            }
        }
        if (zones.isEmpty()) {
            zones.addAll(getPlan().getZones());
        }
        MapZone zone = (MapZone) zones.toArray()[0];
        if (zones.size() > 1) {
            int index = DialogMaster.optionChoice(zones.toArray(), "Choose zone");
            if (index != -1) {
                zone = (MapZone) zones.toArray()[index];
            }
        }
        zone.addBlock(block);
        block.setZone(zone);
        LevelEditor.getMainPanel().getPlanPanel().getTreePanel().blockAdded(block);
        getPlan().getBlocks().add(block);

        // for (MapBlock b: blocks){
        // b.getCoordinates().removeAll(block.getCoordinates());
        // }

    }

    public void removeBlock(MapBlock block) {
        // fill
        LevelEditor.getMainPanel().getPlanPanel().getTreePanel().blockRemoved(block);
        LE_ObjMaster.removeObjects(block.getCoordinates());

        LE_ObjMaster.fill(block.getCoordinates(), DataManager.getType(block.getZone()
                .getFillerType(), DC_TYPE.BF_OBJ));
        for (MapBlock b : getPlan().getBlocks()) {
            b.getConnectedBlocks().remove(block);// connected
        }
        getPlan().getBlocks().remove(block);
    }

    private ROOM_TYPE chooseRoomType() {
        return new EnumMaster<ROOM_TYPE>().retrieveEnumConst(ROOM_TYPE.class, ListChooser
                .chooseEnum(ROOM_TYPE.class));
    }

    public Level getLevel() {
        return LevelEditor.getCurrentLevel();
    }

    public DungeonPlan getPlan() {
        return getLevel().getLocation().getPlan();
    }

    public void newCorridor() {
        // TODO Auto-generated method stub
    }

    public boolean clearArea() {
        List<Coordinates> coordinates = pickCoordinates();
        if (coordinates == null) {
            return false;
        }
        clearArea(coordinates);
        return true;

    }

    public void replace(String prevFiller, String filler, List<Coordinates> coordinates) {
        LevelEditor.getCurrentLevel().removeObj(prevFiller,
                coordinates.toArray(new Coordinates[coordinates.size()]));

        ObjType type = DataManager.getType(filler);
        for (Coordinates c : coordinates) {
            // LevelEditor.getObjMaster().removeObj(c);
            LevelEditor.getObjMaster().addObj(type, c);
        }

    }

    public void copyBlock(MapBlock b) {
        blockBuffer = b;
    }

    public void pasteBlock() {
        if (blockBuffer == null) {
            return;
        }
        loadBlock(blockBuffer);
    }

    public void loadBlock() {
        if (blockChooser == null) {
            blockChooser = new FileChooser(PathFinder.getMapBlockFolderPath());
        }
        String filePath = blockChooser.launch(PathFinder.getMapBlockFolderPath(), "");
        if (filePath == null) {
            return;
        }
        Document node = XML_Converter.getDoc(FileManager.readFile(filePath));
        Dungeon dungeon = getLevel().getDungeon();
        LocationBuilder.constructBlock(node, getPlan().getBlocks().size(), null, getPlan(), dungeon);
    }

    public void loadBlock(MapBlock block) {
        if (block == null) {
            return;
        }
        Coordinates c = pickCoordinate();
        int offsetX = -CoordinatesMaster.getMinX(block.getCoordinates()) + c.x;
        int offsetY = -CoordinatesMaster.getMinY(block.getCoordinates()) + c.y;
        List<Coordinates> coordinates = CoordinatesMaster.getCoordinatesWithOffset(block
                .getCoordinates(), offsetX, offsetY);
        // coordinates=CoordinatesMaster.getCoordinatesWithin(c.x, , c.y, y1);

        this.block = new MapBlock(getPlan().getBlocks().size(), block.getType(), getPlan()
                .getZones().get(0), getPlan(), coordinates);
        clearArea(coordinates); // should add to this.block
        // ++ ZONE INIT

        for (Obj obj : block.getObjects()) {
            // beware deadlock
            LevelEditor.getCurrentLevel().setInitialized(false);
            try {
                Coordinates newCoordinates = new Coordinates(obj.getCoordinates().x + offsetX, obj
                        .getCoordinates().y
                        + offsetY);
                obj.setCoordinates(newCoordinates);
                LevelEditor.getObjMaster().addObj(obj.getType(), obj.getCoordinates());
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                LevelEditor.getCurrentLevel().setInitialized(true);
            }
        }

    }

    public enum FILL_GROUPS {
        FOREST,

    }

    public enum TRANSFORM {
        FLIP_X, FLIP_Y, ROTATE
    }

}
