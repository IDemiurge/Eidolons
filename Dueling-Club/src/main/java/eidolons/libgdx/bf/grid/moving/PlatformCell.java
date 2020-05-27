package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.DC_Cell;
import eidolons.libgdx.bf.grid.cell.GenericGridView;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class PlatformCell extends GridCellContainer {
    /*
    could it be slightly larger? or just decor?
    shading and overlays
    z-index

    portal on a platform?
    attached overlaying objects, like lanterns?

    IDEA: hang a chain along its path or draw aether line
     */
    public PlatformCell(TextureRegion backTexture, int gridX, int gridY) {
        super(backTexture, gridX, gridY);
    }

    //TODO CANNOT BE VOID

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void setUserObject(Object userObject) {
        DC_Cell cell = (DC_Cell) userObject;
        gridX = cell.getX();
        gridY = cell.getY();
        Coordinates coordinates = Coordinates.get(gridX, gridY);
        boolean update = false;
        for (GenericGridView unitView : getUnitViews(false)) {
            if (!unitView.getUserObject().getCoordinates().equals(coordinates)) {
                unitView.getUserObject().setCoordinates(coordinates);
                if (unitView.isMainHero()) {
                    update = true;
                }
            }
        }
        super.setUserObject(userObject);
        if (update) {
            GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO);
        }
    }

    @Override
    protected boolean isGraveyardOn() {
        return false;
    }

    @Override
    protected boolean isViewCacheOn() {
        return false;
    }

    @Override
    protected boolean checkIgnored() {
        return false;
    }

    @Override
    public boolean isStackView() {
        return false;
    }

    @Override
    protected boolean isWithinCamera() {
        return true;
    }
}
