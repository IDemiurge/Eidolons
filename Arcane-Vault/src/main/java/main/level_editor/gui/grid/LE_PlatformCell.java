package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.libgdx.bf.grid.moving.PlatformCell;
import main.game.bf.directions.DIRECTION;

public class LE_PlatformCell extends PlatformCell {
    public LE_PlatformCell(TextureRegion region, int gridX, int gridY, DIRECTION direction) {
        super(region, gridX, gridY, direction);
    }
    @Override
    protected EventListener createListener() {
        return new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return false;
            }
        };
    }
}
