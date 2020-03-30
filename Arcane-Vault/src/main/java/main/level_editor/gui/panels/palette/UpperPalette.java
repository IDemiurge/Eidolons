package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.ScrollPaneX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.panels.palette.tree.BlockTemplateTree;
import main.level_editor.gui.panels.palette.tree.PaletteTree;


public class UpperPalette extends TablePanelX {
    private ScrollPane treeScroll;
    private TablePanelX table;
    private VisSplitPane split;

    public UpperPalette(HybridPalette.PALETTE value) {
        super(650, 800);
        initTable(value);
        initScrollPane(value);

        TablePanelX<Actor> container ;
        if (value == HybridPalette.PALETTE.blocks) {
            container = new TablePanelX<>(320, 800);
            container.add(table).top().left();

        } else {
            container = new TablePanelX<>(500, 800);
            container.add(table).top().left().height(500);
        }
        VisSplitPane.VisSplitPaneStyle style = new VisSplitPane.VisSplitPaneStyle(
                StyleHolder.getScrollStyle().vScroll,
                StyleHolder.getScrollStyle().vScroll);

        Group tableContainer= container;
        if (value == HybridPalette.PALETTE.blocks) {
            tableContainer = new ScrollPaneX(container);
        }
        add(split = new VisSplitPane(tableContainer, treeScroll, false, style)).fill().size(650, 800);
        container.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        split.setSplitAmount(0.75f);
        split.setFillParent(true);
        split.setSize(650, 800);


    }

    private void initTable(HybridPalette.PALETTE value) {
        if (value == HybridPalette.PALETTE.blocks) {
            table = new BlockTemplateChooser(true);
            table.setBackground((Drawable) null);
            return;
        }
        table = new PaletteTypesTable(0);
    }

    private void initScrollPane(HybridPalette.PALETTE value) {
        DC_TYPE arg = null;
        switch (value) {
            case obj:
                arg = DC_TYPE.BF_OBJ;
                break;
            case unit:
                arg = DC_TYPE.UNITS;
                break;
            case encounters:
                arg = DC_TYPE.ENCOUNTERS;
                break;
            case custom:
                treeScroll = new ScrollPaneX(new PaletteTree(null, table)
                         );
                return;
            case blocks:
                treeScroll = new ScrollPaneX(new BlockTemplateTree((BlockTemplateChooser) table)
                         );
                return;
        }
        if (arg != null) {
            treeScroll = new ScrollPaneX(new PaletteTree(arg, table)  );
        } else {

        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
