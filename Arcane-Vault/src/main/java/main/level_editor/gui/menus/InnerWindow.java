package main.level_editor.gui.menus;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisWindow;
import eidolons.libgdx.GdxMaster;
import main.level_editor.LevelEditor;

public class InnerWindow extends VisWindow {

    public InnerWindow(String title) {
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
