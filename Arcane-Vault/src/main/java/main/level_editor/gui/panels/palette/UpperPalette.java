package main.level_editor.gui.panels.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.level_editor.gui.panels.palette.tree.PaletteTree;


public class UpperPalette extends TablePanelX {
    private final PaletteTree tree;
    private final PaletteTypesTable table;
    private final VisSplitPane split;

    public UpperPalette(DC_TYPE TYPE) {
        super(500, 800);
        table = new PaletteTypesTable(0);
        tree = new PaletteTree(TYPE, table);
        TablePanelX<Actor> container = new TablePanelX<>();
        container.add(table).top().right();
        add(split=new VisSplitPane(container, tree,  false)).fill().size(500, 800).bottom().left();
        split.setSplitAmount(0.7f);
        split.setFillParent(true);
        split.setSize(500, 800);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
