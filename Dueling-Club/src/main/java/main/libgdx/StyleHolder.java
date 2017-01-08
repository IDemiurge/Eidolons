package main.libgdx;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster.FONT;

public class StyleHolder {
    private static Label.LabelStyle defaultLabelStyle;
    private static Label.LabelStyle avqLabelStyle;
    private static Color defaultColor=
     ColorManager.getGdxColor( ColorManager.GOLDEN_WHITE);

    public static Label.LabelStyle getDefaultLabelStyle() {
        if (defaultLabelStyle == null) {
            defaultLabelStyle = new Label.LabelStyle(new BitmapFont(),
             defaultColor);
        }

//        new FileHandle(PathFinder.getFontPath()+ FONT.MAIN.path )
        return defaultLabelStyle;
    }

    public static Label.LabelStyle getAVQLabelStyle() {
        if (avqLabelStyle == null) {
            avqLabelStyle = new Label.LabelStyle(new BitmapFont(
             new FileHandle(PathFinder.getFontPath()+ FONT.AVQ.path )),
             defaultColor);
        }
        return avqLabelStyle;
    }
}
