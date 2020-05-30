package eidolons.libgdx.bf.grid.moving;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.gui.generic.GearCluster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitGroup;
import eidolons.libgdx.gui.generic.btn.FlipDrawable;
import eidolons.libgdx.particles.EmitterActor;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.RandomWizard;
import main.system.launch.CoreEngine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PlatformDecor extends NoHitGroup {

    private final PlatformCell.PLATFORM_TYPE type;
    private GroupX container;
    private DIRECTION direction;
    private GearCluster gears;
    private GearCluster gears2;
    Map<Actor, Vector2> rotateMap = new LinkedHashMap<>();
    List<PlatformCell> cells;
    private EmitterActor trail;
    private SpriteX light;

    public PlatformDecor(PlatformCell.PLATFORM_TYPE type, List<PlatformCell> cells) {
        this.type = type;
        this.cells = cells;
        init();
    }

    public void init() {
        /*
        advanced: specify each cell's img/type ?
         */

        Set<Coordinates> coordinatesSet = cells.stream().map(cell
                -> cell.getUserObject().getCoordinates()).collect(Collectors.toSet());
        int w = CoordinatesMaster.getWidth(coordinatesSet);
        int h = CoordinatesMaster.getHeight(coordinatesSet);
        setSize(w * 128, h * 128);

        //always push to the TIP, so...? and tip is OPPOSITE of enter direction...

        addActor(container = new GroupX());
        container.setSize(w * 128, h * 128);
        if (direction == null) {
            direction = DIRECTION.UP;
        }
        DIRECTION tipDirection = direction.flip();
        int offsetX = w - 1;
        int offsetY = h - 1;


        if (direction.growY == null) {
            //center
        } else {
            container.setY(tipDirection.growY
                    ? -(offsetY) * 128
                    : (offsetY) * 128);
        }
        if (direction.growX == null) {
            //center
        } else {
            container.setX(tipDirection.growX
                    ? -(offsetX) * 128
                    : (offsetX) * 128);
        }
        String img = Images.PLATFORM_ROCKS;

        switch (type) {
            case vessel:
                // addRotating(angle, Images.PLATFORM_HORN);
                int n = 1;
                int align = getAlignment(n, tipDirection);
                container.addActor(gears = new GearCluster(1f), align);
                align = getAlignment(n, tipDirection);
                container.addActor(gears2 = new GearCluster(1f), align);
                gears.setSpeed(0);
                gears2.setSpeed(0);
                break;
            case boat:
                break;
            case island:
                img=Images.PLATFORM_ISLAND;
            case rock:
                Image island;
                container.addActor(island = new Image(TextureCache.getOrCreateR(img)));
                island.pack();
                GdxMaster.center(island);
                island.setY(h * 80 - island.getImageHeight());
                break;
        }
        container.addActor(trail = new EmitterActor(GenericEnums.VFX.CINDERS3));
        // container.addActor(light = new SpriteX(Sprites.FIRE_LIGHT));
        // light.getSprite().setAlpha(0.5f);
        // light.setBlending(GenericEnums.BLENDING.SCREEN);
        // light.setRotation(90);
        // GdxMaster.center(light);        // light.setPosition( );
        GdxMaster.center(trail);
        if (CoreEngine.TEST_LAUNCH) {
            debugAll();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private int getAlignment(int n, DIRECTION tipDirection) {
        boolean clockwise = RandomWizard.random();
        for (int i = 0; i < n; i++) {
            tipDirection = tipDirection.rotate45(clockwise);
        }
        switch (tipDirection) {
            case UP:
                return Align.top;
            case DOWN:
                return Align.bottom;
            case LEFT:
                return Align.left;
            case RIGHT:
                return Align.right;
            case UP_LEFT:
                return Align.topLeft;
            case UP_RIGHT:
                return Align.topRight;
            case DOWN_RIGHT:
                return Align.bottomRight;
            case DOWN_LEFT:
                return Align.bottomLeft;
        }
        return n;
    }

    public void left() {
        trail.reset();
    }

    public void entered() {
        trail.start();
    }

    public void arrived() {
        toggleGears();
    }

    public void resumed() {
        toggleGears();
    }

    private void toggleGears() {
        if (gears != null) {
            gears.toggle();
            gears2.toggle();
            gears.reverse();
            gears2.reverse();
        }
    }

    private void addRotating(float angle, String imagePath) {
        Image horn1;
        Image horn2;

        rotateMap.put(horn1 = new Image(
                        TextureCache.getOrCreateR(imagePath)),
                new Vector2(angle, angle - 90));
        container.addActor(horn1);
        rotateMap.put(horn2 = new Image(
                        new FlipDrawable(
                                TextureCache.getOrCreateTextureRegionDrawable(imagePath),
                                () -> true, () -> false)),
                new Vector2(-angle, -angle + 90));
        container.addActor(horn2);
        horn1.pack();
        horn2.pack();
        // horn1.setOrigin();
        horn1.setPosition(54 - horn1.getWidth(), 110);
        horn2.setPosition(74 - horn2.getWidth(), 110);
    }

}
