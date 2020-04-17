package main.level_editor.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import eidolons.game.module.generator.tilemap.TileMap;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster;

public class TileMapView extends TablePanelX {
    public TileMapView(TileMap tileMap) {
        this(tileMap.toString());
    }
    public TileMapView(String tileMap) {
        this(0, StringMaster.splitLines(tileMap));
    }
    public TileMapView(int size, String[] lines) {
        for (String s : lines) {
            Label label;
            add(label = new Label(s, VisUI.getSkin())).left().spaceTop(2);
            label.setStyle(StyleHolder.getSizedLabelStyle(
                    FontMaster.FONT.MONO_LARGE,
                    18
//                    (int) (18-Math.sqrt(size)/2)
            ));
//            label.setFontScaleY(0.8f);
            setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
            row();
        }
    }

}
