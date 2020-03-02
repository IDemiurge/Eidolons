package main.level_editor.gui.palette;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectableImageItem;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.metadata.settings.LE_OptionsMaster;

import java.util.List;

import static main.level_editor.metadata.settings.LE_Options.EDITOR_OPTIONS.PALETTE_SCALE;

public class PaletteTypesTable extends SelectionImageTable {

    private final List<ObjType> types;

    @Override
    protected SelectableItemData[] initDataArray() {
        data = new SelectableItemData[size];
        for (int i = 0; i < size; i++) {
            data[i] = new SelectableItemData(types.get(i));
        }
        return data;
    }

    public PaletteTypesTable(List<ObjType> types, int space) {
        super(getWrap(), types.size(), space);
        this.types = types;
    }

    private static int getWrap() {
        return 40; //sca
    }

    @Override
    protected int getDynamicWrap(int i) {
        return super.getDynamicWrap(i);
    }

    @Override
    protected SelectableImageItem createElement(SelectableItemData datum) {
        SelectableImageItem s = super.createElement(datum);
        s.setScale(LE_OptionsMaster.getOptions().getFloatValue(PALETTE_SCALE));
        return s;
    }

    @Override
    protected void selected(SelectableItemData item) {
        LevelEditor.getCurrent().getManager().
                getModelManager().paletteSelection(item.getEntity());
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(128 * LE_OptionsMaster.getOptions().getFloatValue(PALETTE_SCALE),
                128 * LE_OptionsMaster.getOptions().getFloatValue(PALETTE_SCALE));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
