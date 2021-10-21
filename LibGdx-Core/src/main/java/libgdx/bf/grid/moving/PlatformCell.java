package libgdx.bf.grid.moving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.Core;
import eidolons.game.core.game.DC_Game;
import libgdx.bf.grid.cell.GenericGridView;
import libgdx.bf.grid.cell.GridCellContainer;
import libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

import java.util.function.Function;

/*
    could it be slightly larger? or just decor?
    shading and overlays
    z-index

    portal on a platform?
    attached overlaying objects, like lanterns?

    IDEA: hang a chain along its path or draw aether line
     */
public class PlatformCell extends GridCellContainer {
    PlatformController controller;
    DIRECTION direction; //WHERE WE ENTER
    private final int originalX;
    private final int originalY;


    public PlatformCell(TextureRegion region, int gridX, int gridY, DIRECTION direction ,
                        Function<Coordinates, Color> colorFunction) {
        super(region , gridX, gridY, colorFunction);
        originalX = gridX;
        originalY = gridY;
        this.direction = direction;
        if (CoreEngine.TEST_LAUNCH) {
            addListener(createDebugListener());
        }
    }

    protected EventListener createDebugListener() {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getTapCount()>1) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        controller.toggle();
                    }
                }
            }
        };
    }


    @Override
    public float getCellImgAlpha() {
        return 1f;
    }


    @Override
    public void resetZIndices() {
        super.resetZIndices();
    }

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
                    Core.onNonGdxThread(() -> {
                        DC_Game.game.getMovementManager().moved(unitView.getUserObject(), cell, false);
                        DC_Game.game.getManager().reset();
                    });
                }
            }
        }
        super.setUserObject(userObject);
        if (update) {

            GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO);
        }
    }

    public void setController(PlatformController platformController) {
        controller = platformController;
    }

    public int getOriginalX() {
        return originalX;
    }
    public int getOriginalY() {
        return originalY;
    }


    public enum PLATFORM_TYPE {
        boat,
        island,
        vessel, rock;

        private TextureRegion texture;

        public TextureRegion getTexture() {
            if (texture == null) {
                texture = TextureCache.getRegionUV(PathFinder.getCellImagesPath() + "platform/" + name()
                        + ".png");
            }
            return texture;
        }
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
    public boolean isWithinCamera() {
        return true;
    }
}
