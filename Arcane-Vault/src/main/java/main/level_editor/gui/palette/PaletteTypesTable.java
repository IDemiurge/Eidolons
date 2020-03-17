package main.level_editor.gui.palette;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectableImageItem;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.backend.metadata.options.LE_OptionsMaster;

import java.util.List;

import static main.level_editor.backend.metadata.options.LE_Options.EDITOR_OPTIONS.PALETTE_SCALE;

public class PaletteTypesTable extends SelectionImageTable {

    private  List<ObjType> types;

    protected boolean isTableFixedSize() {
        return false;
    }
    @Override
    protected SelectableItemData[] initDataArray() {
        if (types==null) {
            return data;
        }
        data = new SelectableItemData[size];
        int i = 0;
        for (ObjType type : types) {
            data[i] = new SelectableItemData(type);
            i++;
        }
        size = types.size();
        return data;
    }

    @Override
    public void setUserObject(Object userObject) {
        types = (List<ObjType>) userObject;
        size = types.size();
        super.setUserObject(userObject);

    }

    @Override
    public void updateAct(float delta) {
        if (types == null) {
            return;
        }
        super.updateAct(delta);
    }

    public PaletteTypesTable(int space) {
        super(getWrap(), 80, space);
    }

    private static int getWrap() {
        return
                Math.round(1000 / (LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE))+1); //sca
    }

    @Override
    protected int getDynamicWrap(int i) {
        return super.getDynamicWrap(i);
    }

    @Override
    protected SelectableImageItem createElement(SelectableItemData datum) {
        SelectableImageItem s = super.createElement(datum);
//        s.setScale(LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE));
        s.setSize(getElementSize().x, getElementSize().y);
        return s;
    }

    @Override
    protected void selected(SelectableItemData item) {
        LevelEditor.getCurrent().getManager().
                getModelManager().paletteSelection(item.getEntity());
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE)/100f,
                128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE)/100f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
