package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.gui.LabelX;
import main.level_editor.LevelEditor;
import main.system.graphics.FontMaster;

import java.util.HashMap;
import java.util.Map;

public class LE_GridCell extends GridCellContainer {

    //colorOverlay
    Map<Color, FadeImageContainer> colorOverlays = new HashMap<>();

    LabelX scriptsLabel;
    LabelX aiLabel;

    public LE_GridCell(TextureRegion backTexture, int gridX, int gridY) {
        super(backTexture, gridX, gridY);
        scriptsLabel = new LabelX("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 12));
        aiLabel = new LabelX("", StyleHolder.getSizedLabelStyle(FontMaster.FONT.NYALA, 12));
    }

    @Override
    protected boolean isCoordinatesShown() {
        return
                LevelEditor.getModel().getDisplayMode().isShowCoordinates();
    }

    public void displayModeUpdated() {
        for (Color color : colorOverlays.keySet()) {
            //check?
            if (LevelEditor.getModel().getDisplayMode().isUseColors())
                colorOverlays.get(color).fadeIn();
            else
                colorOverlays.get(color).fadeOut();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public int getUnitViewCountEffective() {
        return super.getUnitViewCountEffective()+1;
    }

    @Override
    protected EventListener createListener() {
        return new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LevelEditor.getCurrent().getManager().getMouseHandler().handleCellClick(event, getTapCount(), getGridX(), getGridY());
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
//                if (button==0)
//                    return false;
                return true;
            }
        };
    }

    protected boolean checkIgnored() {
        if (!isVisible())
            return true;
        if (Eidolons.game == null)
            return true;

        return !isWithinCamera();
    }

    @Override
    protected boolean isWithinCamera() {
        return super.isWithinCamera();
    }


    protected boolean isShadersSupported() {
        return false;
    }
}
