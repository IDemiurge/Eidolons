package main.level_editor.gui.panels.palette.table;

import com.badlogic.gdx.math.Vector2;
import eidolons.content.consts.GraphicData;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectableImageItem;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import main.level_editor.LevelEditor;
import main.level_editor.backend.metadata.options.LE_OptionsMaster;

import java.util.List;

import static main.level_editor.backend.metadata.options.LE_Options.EDITOR_OPTIONS.PALETTE_SCALE;

public class DecorChooser extends SelectionImageTable {
    List<GraphicData> files;

    public DecorChooser() {
        super(1, 0, 5);
    }

    @Override
    public void setUserObject(Object userObject) {
        files = (List<GraphicData>) userObject;
        size = files.size();
        initSize(5, size);
        super.setUserObject(userObject);
        setFixedSize(true);
        if (getParent() instanceof TablePanel) {
            TablePanel parent = ((TablePanel) getParent());
            remove();
            parent.add(this);
            parent.setSize(getPrefWidth()+90, getPrefHeight()+290);
        }
    }

    private Tooltip createTooltip(ItemListPanel.SelectableItemData datum) {
        return new ValueTooltip(datum.getDescription());
    }

    @Override
    protected SelectableImageItem createElement(ItemListPanel.SelectableItemData datum) {
        SelectableImageItem s = super.createElement(datum);
        s.setSize(getElementSize().x, getElementSize().y);
        Tooltip tooltip = createTooltip(datum);
        if (tooltip != null) {
            s.addListener(tooltip.getController());
        }
        s.setFlipX(new GraphicData(datum.getDescription()).getBooleanValue(GraphicData.GRAPHIC_VALUE.flipX));
        s.setFlipY(new GraphicData(datum.getDescription()).getBooleanValue(GraphicData.GRAPHIC_VALUE.flipY));
        return s;
    }

    @Override
    protected ItemListPanel.SelectableItemData[] initDataArray() {
        if (files == null) {
            return data;
        }
        data = new ItemListPanel.SelectableItemData[size];
        int i = 0;
        for (GraphicData type : files) {
            data[i] = new ItemListPanel.SelectableItemData(
                    type.getName(), type.getData(),
                    null, type.getImage());
            i++;
        }
        size = files.size();
        return data;
    }

    @Override
    protected void selected(ItemListPanel.SelectableItemData item) {
        LevelEditor.getCurrent().getManager().
                getModelManager().paletteDecorSelection(item.getDescription());
    }


    @Override
    protected Vector2 getElementSize() {
        return new Vector2(128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE) / 100f,
                128 * LE_OptionsMaster.getOptions_().getFloatValue(PALETTE_SCALE) / 100f);
    }
}
