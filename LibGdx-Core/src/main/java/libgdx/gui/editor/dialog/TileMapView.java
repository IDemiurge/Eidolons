package libgdx.gui.editor.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMap;
import libgdx.StyleHolder;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.dungeon.panels.TablePanelX;
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
            s= s.replace("O", ".").replace("0", ".");
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
