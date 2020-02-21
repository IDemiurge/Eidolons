package main.level_editor.gui.menus;

import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisWindow;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import main.level_editor.LevelEditor;

public class DataTable extends VisWindow {

    public DataTable(String title) {
        super(title, LevelEditor.getWindowStyle()        );

        setVisible(false);
//        setSize(GdxMaster.adjustSize(getDefaultWidth()), GdxMaster.adjustSize(getDefaultHeight()));
        pad(GdxMaster.adjustSize(12));
        closeOnEscape();
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().pack();
        getTitleLabel().setY(getTitleLabel().getY()-getTitleLabel().getHeight()/2);
    }
public void init(){

}
    public void open(){

    }
    public void closed(){

    }
}
