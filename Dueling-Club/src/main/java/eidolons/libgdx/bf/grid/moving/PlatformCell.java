package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.cell.GenericGridView;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.FlipDrawable;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.LinkedHashMap;
import java.util.Map;

/*
    could it be slightly larger? or just decor?
    shading and overlays
    z-index

    portal on a platform?
    attached overlaying objects, like lanterns?

    IDEA: hang a chain along its path or draw aether line
     */
public class PlatformCell extends GridCellContainer {
    private final PLATFORM_TYPE type;
    private GroupX visuals;
    PlatformController controller;
    Map<Actor, Vector2> rotateMap = new LinkedHashMap<>();
    DIRECTION direction; //WHERE WE ENTER


    public PlatformCell(PLATFORM_TYPE type, int gridX, int gridY, DIRECTION direction) {
        super(type.getTexture(), gridX, gridY);
        this.type = type;
        this.direction = direction;
        initVisuals();
    }

    //TODO CANNOT BE VOID

    public void initVisuals() {
        // Boolean vert_hor_diag;
        addActor(visuals = new GroupX());
        visuals.setSize(128, 128);
        float angle = direction.getDegrees();
        switch (type) {
            case vessel:
                // addRotating(angle, Images.PLATFORM_HORN);

                GearCluster gears;
                visuals.addActor(gears = new GearCluster(1f));
                GdxMaster.center(gears);
                switch (direction) {
                    case RIGHT:
                    case LEFT:
                        GdxMaster.right(gears);
                        break;
                    case UP:
                    case DOWN:
                        GdxMaster.top(gears);
                        break;
                }
                break;
            case island:
                Image island;
                visuals.addActor(island = new Image(TextureCache.getOrCreateR(Images.PLATFORM_ISLAND)));
                island.pack();
                island.setPosition(64 - island.getWidth() / 2, 64 - island.getImageHeight());
                // addRotating();
                // addGears();
                // addLight(direction.flip())

                break;
        }
    }

    private void addRotating(float angle, String imagePath) {
        Image horn1;
        Image horn2;

        rotateMap.put(horn1 = new Image(
                        TextureCache.getOrCreateR(imagePath)),
                new Vector2(angle, angle - 90));
        visuals.addActor(horn1);
        rotateMap.put(horn2 = new Image(
                        new FlipDrawable(
                                TextureCache.getOrCreateTextureRegionDrawable(imagePath),
                                () -> true, () -> false)),
                new Vector2(-angle, -angle + 90));
        visuals.addActor(horn2);
        horn1.pack();
        horn2.pack();
        // horn1.setOrigin();
        horn1.setPosition(54 - horn1.getWidth(), 110);
        horn2.setPosition(74 - horn2.getWidth(), 110);
    }

    @Override
    public void resetZIndices() {
        super.resetZIndices();
        visuals.setZIndex(0);
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
                    Eidolons.onNonGdxThread(() -> {
                        DC_Game.game.getMovementManager().moved(unitView.getUserObject());
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

    public enum PLATFORM_TYPE {
        boat,
        island,
        vessel;

        private TextureRegion texture;

        public TextureRegion getTexture() {
            if (texture == null) {
                texture = TextureCache.getOrCreateR(PathFinder.getCellImagesPath() + "platform/" + name()
                        + ".png");
            }
            return texture;
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
