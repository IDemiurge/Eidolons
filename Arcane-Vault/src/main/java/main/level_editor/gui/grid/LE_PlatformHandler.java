package main.level_editor.gui.grid;

import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.bf.grid.moving.PlatformCell;
import eidolons.libgdx.bf.grid.moving.PlatformData;
import eidolons.libgdx.bf.grid.moving.PlatformHandler;
import main.game.bf.Coordinates;

public class LE_PlatformHandler extends PlatformHandler {
    public LE_PlatformHandler(GridPanel grid) {
        super(grid);
    }

    @Override
    protected PlatformCell create(Coordinates c, PlatformData data) {
        return new LE_PlatformCell(data.getType().getTexture(), c.x, c.y, data.getDirection());
    }
}
