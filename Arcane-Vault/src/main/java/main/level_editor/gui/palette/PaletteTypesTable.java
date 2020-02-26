package main.level_editor.gui.palette;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.ValueTable;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionTable;
import main.content.DC_TYPE;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.system.GuiEventType;

import java.util.List;

public class PaletteTypesTable extends SelectionImageTable  {

    private final List<ObjType> types;

    @Override
    protected SelectableItemData[] initDataArray() {
        data= new SelectableItemData[size];
        for (int i = 0; i < size; i++) {
            data[i] = new SelectableItemData(types.get(i));
        }
        return data;
    }

    public PaletteTypesTable(int wrap, List<ObjType> types, int space) {
        super(wrap, types.size(), space);
        this.types = types;
    }

    @Override
    protected void selected(SelectableItemData item) {
        LevelEditor.getCurrent().getManager().
                getModelManager().paletteSelection(item.getEntity());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
