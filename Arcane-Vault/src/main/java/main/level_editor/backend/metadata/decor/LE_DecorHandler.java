package main.level_editor.backend.metadata.decor;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorLoader;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.boss.demo.knight.AriusVessel;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.datasource.GraphicData.GRAPHIC_VALUE;
import eidolons.libgdx.bf.decor.DecorData;
import eidolons.libgdx.screens.ScreenMaster;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.metadata.script.CellDataHandler;
import main.level_editor.gui.dialog.struct.DecorEditor;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.FileManager;
import main.system.threading.WaitMaster;

import java.io.File;
import java.util.*;

import static eidolons.libgdx.bf.decor.DecorData.DECOR_LEVEL;

public class LE_DecorHandler extends CellDataHandler<DecorData> implements IDecorHandler {

    public static Map<DECOR, List<GraphicData>> decorPalette = new LinkedHashMap<>();

    @Override
    public void afterLoaded() {
        super.afterLoaded();
        // if (!decorPalette.isEmpty()) {
        //     return;
        // }
        for (DECOR value : DECOR.values()) {
            List<GraphicData> items = new ArrayList<>();
            List<File> files = null;
            switch (value) {
                case texture:
                    files = FileManager.getFilesFromDirectory(
                            PathFinder.getTexturesPath(), false, true, false);
                    items.addAll(createDecorList(FileManager.getFilePaths(files), false));
                    break;
                case image:
                    // items.addAll( createDecorList(Images.getFieldsAsPaths(), false));
                    break;
                case boss:
                    files = FileManager.getSpriteFilesFromDirectory("boss");
                case sprites:
                    if (files == null) {
                        files = FileManager.getSpriteFilesFromDirectory("cells");
                        files.addAll(FileManager.getSpriteFilesFromDirectory("unit"));
                    }
                    items.addAll(createDecorList(FileManager.getFilePaths(files), true));
                    break;
                case vfx:
                    ////TODO need append-decor mode!
                    items.addAll(createVfxData());
                    break;
            }
            decorPalette.put(value, items); //gonna be used by script-helper too
        }

    }

    private Collection<GraphicData> createVfxData() {
        Collection<GraphicData> list = new ArrayList<>();
        for (GenericEnums.VFX vfx : GenericEnums.VFX.values()) {
            GraphicData data = new GraphicData("");
            data.setValue(GRAPHIC_VALUE.vfx, vfx);
            list.add(data);
        }
        return list;
    }

    private List<GraphicData> createDecorList(List<String> fileNames, boolean sprite) {
        List<GraphicData> items = new ArrayList<>();
        for (String file : fileNames) {
            String path = GdxImageMaster.cropImagePath(file);
            //just use name?
            GraphicData data = new GraphicData("");
            data.setValue(sprite ? GRAPHIC_VALUE.sprite : GRAPHIC_VALUE.texture, path);
            items.add(data);
            items.addAll(
                    createTransformed(data, sprite));
        }
        return items;
    }

    private Collection<GraphicData> createTransformed(GraphicData data, boolean sprite) {
        Collection<GraphicData> list = new ArrayList<>();
        if (sprite) {
        } else {
        }
        list.add(data.clone().setValue(GRAPHIC_VALUE.flipX, "true"));
        list.add(data.clone().setValue(GRAPHIC_VALUE.flipY, "true"));
        return list;
    }


    public enum DECOR {
        texture,
        image,
        sprites,
        vfx, boss,
    }

    public LE_DecorHandler(LE_Manager manager) {
        super(manager);
    }

    public void fromPalette(Coordinates c) {
        GraphicData decor = getModel().getPaletteSelection().getDecorData();
        ////TODO could have operations there too like flips / ..
        DecorData data = createData("");
        DECOR_LEVEL level = getEditHandler().chooseEnum(DECOR_LEVEL.class);
        data.setValue(level.name(), decor.getData());
        setData(c, data.getData());


    }


    @Override
    public void clear() {
        for (Coordinates c : getModel().getSelection().getCoordinates()) {
            clear(c);
        }
    }

    @Override
    public void copy() {
        copyData(false);
    }

    @Override
    public void paste() {
        pasteData();
    }

    @Override
    public void cut() {
        copyData(true);
    }

    @Override
    public void edit() {
        editData(getModel().getSelection().getLastCoordinates());
    }


    public void offset(DIRECTION d) {
        int n = 5;
        switch (d) {
            case UP:
                offset(0, n);
                break;
            case DOWN:
                offset(0, -n);
                break;
            case LEFT:
                offset(-n, 0);
                break;
            case RIGHT:
                offset(n, 0);
                break;
            case UP_LEFT:
                offset(-n, n);
                break;
            case UP_RIGHT:
                offset(n, n);
                break;
            case DOWN_RIGHT:
                offset(n, -n);
                break;
            case DOWN_LEFT:
                offset(-n, -n);
                break;
        }
    }

    public void offset(int x, int y) {
        for (Coordinates coordinate : getCoordinates()) {
            addIntValue(coordinate, GRAPHIC_VALUE.x, x);
            addIntValue(coordinate, GRAPHIC_VALUE.y, y);
        }
    }

    public void rotate(boolean clockwise) {
        for (Coordinates coordinate : getCoordinates()) {
            int n = clockwise ? -5 : 5;
            addIntValue(coordinate, GRAPHIC_VALUE.rotation, n);
        }
    }

    private void addIntValue(Coordinates c, GRAPHIC_VALUE value, int n) {
        DecorData decorData = new DecorData(getData(c).getData());
        for (DECOR_LEVEL decor_level : DECOR_LEVEL.values()) {
            String data = decorData.getValue(decor_level);
            if (data.isEmpty()) {
                continue;
            }
            GraphicData graphicData = new GraphicData(data);
            int intValue = graphicData.getIntValue(value);
            intValue += n;
            decorData.setValue(decor_level, graphicData.setValue(value, intValue).getData());
        }
        setData(c, decorData.getData());
    }

    private void append(Coordinates coordinate, DecorData data) {
        DecorData orig = getData(coordinate);
        for (String s : data.getValues().keySet()) {
            orig.addValue(s, data.getValue(s));
        }
    }

    @Override
    protected DecorData append(DecorData previous, DecorData data) {
        for (DECOR_LEVEL level : DECOR_LEVEL.values()) {
            if (!data.getValue(level).isEmpty() &&
                    !previous.getValue(level).isEmpty())
            {
                DECOR_LEVEL next = new EnumMaster<DECOR_LEVEL>().getNextEnumConst(DECOR_LEVEL.class, level);
                if (next== DECOR_LEVEL.BOTTOM) {
                    if (!previous.getValue(next).isEmpty()) {
                        continue;
                        //confirm
                    }
                }
                data.setValue(next, data.getValue(level));
            }
        }
        return data;
    }

    @Override
    public void move() {
        DIRECTION direction = getEditHandler().chooseEnum(DIRECTION.class);
        move(direction);
    }

    public void move(DIRECTION d) {
        for (Coordinates coordinate : getCoordinates()) {
            DecorData data = getData(coordinate);
            clear(coordinate);
            coordinate = coordinate.getAdjacentCoordinate(d);
            if (coordinate != null) {
                append(coordinate, data);
            }
        }
    }

    @Override
    protected void init() {
        //now via floorloader

        if (ScreenMaster.getGrid() != null)
            if (ScreenMaster.getGrid().isDecorInitialized()) {
                return;
            }
        for (Coordinates coordinates : getMap().keySet()) {
            DecorData decorData = getMap().get(coordinates);
            if (decorData != null) {
                GuiEventManager.triggerWithParams(GuiEventType.CELL_DECOR_RESET, coordinates, decorData);
            }
        }
    }


    @Override
    public void boss() {
        Eidolons.onGdxThread(() -> {
            AriusVessel ariusVessel = new AriusVessel(null) {
                @Override
                public Coordinates getCoordinates() {
                    return getSelectionHandler().getSelection().getLastCoordinates();
                }
            };
            GuiEventManager.triggerWithParams(GuiEventType.ADD_BOSS_VIEW, ariusVessel);
            // AriusBodyActive ariusBodyActive = new AriusBodyActive(null);
            // GuiEventManager.triggerWithParams(GuiEventType.ADD_BOSS_VIEW, ariusVessel);
        });
    }

    @Override
    protected String getXmlNodeName() {
        return FloorLoader.DECOR_DATA;
    }

    @Override
    protected DecorData getData(Coordinates c) {
        return getMap().get(c);
    }

    @Override
    protected Map<Coordinates, DecorData> getMap() {
        return getGame().getDungeonMaster().getFloorWrapper().getDecorMap();
    }

    @Override
    protected Operation.LE_OPERATION getOperation() {
        return Operation.LE_OPERATION.CELL_DECOR_CHANGE;
    }

    @Override
    protected WaitMaster.WAIT_OPERATIONS getEditOperation() {
        return DecorEditor.OPERATION;
    }

    @Override
    protected DecorData createData(String s) {
        return new DecorData(s);
    }
}
