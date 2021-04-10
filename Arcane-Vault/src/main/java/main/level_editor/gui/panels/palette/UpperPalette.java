package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.ScrollPaneX;
import libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.components.TreeX;
import main.level_editor.gui.dialog.BlockTemplateChooser;
import main.level_editor.gui.panels.palette.table.DecorChooser;
import main.level_editor.gui.panels.palette.tree.BlockTemplateTree;
import main.level_editor.gui.panels.palette.tree.DecorTree;
import main.level_editor.gui.panels.palette.tree.PaletteTree;


public class UpperPalette extends TablePanelX {
    private ScrollPane treeScroll;
    private TablePanelX table;
    private final VisSplitPane split;
    private TreeX tree;
    //for easy auto-select

    public UpperPalette(HybridPalette.PALETTE value) {
        super(650, 750);
        initTable(value);
        initScrollPane(value);

        TablePanelX<Actor> container ;

        if (isScrolled(value)) {
        // if (value == HybridPalette.PALETTE.blocks) {
//            container = new TablePanelX<>(300, 750);
            container = new TablePanelX<>();
            container.add(table).top().left();
        } else {
            container = new TablePanelX<>(500, 750);
            container.add(table).top().left().maxHeight(800);
        }
        VisSplitPane.VisSplitPaneStyle style = new VisSplitPane.VisSplitPaneStyle(
                StyleHolder.getScrollStyle().vScroll,
                StyleHolder.getScrollStyle().vScroll);

        Group tableContainer= container;
        if (isScrolled(value)) {
            tableContainer = new ScrollPaneX(container);
        }
        add(split = new VisSplitPane(tableContainer, treeScroll, false, style)).fill().size(650, 750);
        container.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        split.setSplitAmount(0.5f);
        split.setFillParent(true);
        split.setSize(650, 750);

        split.setLayoutEnabled(true);
    }

    private boolean isScrolled(HybridPalette.PALETTE value) {
        if (value== HybridPalette.PALETTE.blocks) return true;
        if (value== HybridPalette.PALETTE.custom) return true;
        if (value== HybridPalette.PALETTE.decor) return true;
        return value == HybridPalette.PALETTE.obj;
    }

    private void initTable(HybridPalette.PALETTE value) {
        if (value == HybridPalette.PALETTE.blocks) {
            table =  new BlockTemplateChooser(true);
            table.setBackground((Drawable) null);
            return;
        }
        if (value == HybridPalette.PALETTE.decor) {
            table =  new DecorChooser( );
            table.setBackground((Drawable) null);
            return;
        }
        table = new PaletteTypesTable(0);
        table.setBackground(NinePatchFactory.getLightPanelFilledDrawable());

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
            case decor:
                treeScroll = new ScrollPaneX(tree =new DecorTree((DecorChooser) table)
                );
                return;
            case custom:
                treeScroll = new ScrollPaneX(tree =new PaletteTree(null, table)
                         );
                return;
            case blocks:
                treeScroll = new ScrollPaneX(tree =new BlockTemplateTree((BlockTemplateChooser) table)
                         );
                return;
        }
        if (arg != null) {
            treeScroll = new ScrollPaneX(tree =new PaletteTree(arg, table)  );
        } else {

        }
        // if (isScrolled(value)) {
        //     treeScroll = new ScrollPaneX(tree);
        // }
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public TreeX getTree() {
        return tree;
    }
}
