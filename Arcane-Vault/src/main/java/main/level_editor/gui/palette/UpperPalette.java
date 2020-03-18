package main.level_editor.gui.palette;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.content.DC_TYPE;
import main.entity.type.ObjType;
import main.level_editor.LevelEditor;
import main.level_editor.gui.palette.tree.PaletteTree;

import java.util.List;


public class UpperPalette extends TablePanelX {
    private final PaletteTree tree;
    private final PaletteTypesTable table;

    public UpperPalette(DC_TYPE TYPE) {
        super(500, 800);
        tree = new PaletteTree(TYPE);
        table = new PaletteTypesTable(0);

        add(new VisSplitPane(tree, table, true)).fill();
        tree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Tree.Node node = tree.getSelection().first();
                Object object = node.getObject();
                List<ObjType> types = LevelEditor.getCurrent().getManager().getPaletteHandler().
                        getTypesForTreeNode(TYPE, object);
                table.setUserObject(types);
            }
        });
    }

}
