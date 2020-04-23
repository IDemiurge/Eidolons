package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import eidolons.content.PROPS;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectableImageItem;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.backend.metadata.options.LE_OptionsMaster;
import main.system.auxiliary.StringMaster;

import java.util.List;

import static main.level_editor.backend.metadata.options.LE_Options.EDITOR_OPTIONS.PALETTE_SCALE;

public class PaletteTypesTable extends SelectionImageTable {

    private List<ObjType> types;

    protected boolean isTableFixedSize() {
        return true;
    }

    @Override
    public float getMinHeight() {
        return super.getMinHeight();
    }

    @Override
    public float getMaxHeight() {
        return super.getMaxHeight();
    }

    @Override
    public float getMaxWidth() {
        return super.getMaxWidth();
    }

    @Override
    public void layout() {
        super.layout();
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        if (types == null) {
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
        return Math.round(200 / (LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE)) + 1); //sca
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

        Tooltip tooltip = createTooltip(datum);
        if (tooltip != null) {
            s.addListener(tooltip.getController());
        }
        return s;
    }

    private Tooltip createTooltip(SelectableItemData datum) {
        String text = datum.getEntity().getName();
        if (datum.getEntity().getOBJ_TYPE_ENUM() == DC_TYPE.ENCOUNTERS) {
            text= StringMaster.getValueTooltip(datum.getEntity(),
                    G_PROPS.NAME,
                    PROPS.PRESET_GROUP,
                    PROPS.FILLER_TYPES);
//
        }
        return new ValueTooltip(text);
    }

    @Override
    protected void selected(SelectableItemData item) {
        LevelEditor.getCurrent().getManager().
                getModelManager().paletteSelection((ObjType) item.getEntity());
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE) / 100f,
                128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE) / 100f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

}
