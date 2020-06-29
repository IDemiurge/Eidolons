package eidolons.libgdx.bf.overlays.map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager.STD_IMAGES;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by JustMe on 9/12/2017.
 */
public class WallMap extends OverlayMap {
    private static final String INDESTRUCTIBLE = " Indestructible";
    private static final String STRANGE_WALL = " Marked";
    public static boolean on;
    private ObjectMap<Coordinates, List<DIRECTION>> diagonalJoints;
    private ObjectMap<Coordinates, DOOR_STATE> doorMap;

    public WallMap() {
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.DOORS);
    }

    private static STD_IMAGES getWallImageFromSide(DIRECTION side) {
        switch (side) {
            case DOWN_LEFT:
                return STD_IMAGES.WALL_DIAGONAL_DOWN_LEFT;
            case DOWN_RIGHT:
                return STD_IMAGES.WALL_DIAGONAL_DOWN_RIGHT;
            case UP_LEFT:
                return STD_IMAGES.WALL_DIAGONAL_UP_LEFT;
            case UP_RIGHT:
                return STD_IMAGES.WALL_DIAGONAL_UP_RIGHT;
            case LEFT:
                return STD_IMAGES.WALL_HORIZONTAL_LEFT;
            case RIGHT:
                return STD_IMAGES.WALL_HORIZONTAL_RIGHT;
            case UP:
                return STD_IMAGES.WALL_VERTICAL_UP;
            case DOWN:
                return STD_IMAGES.WALL_VERTICAL_DOWN;
        }
        return null;
    }

    public static int getOffsetY() {
        // if (){
        //
        // } else
            return 40;
    }

    private TextureRegion get3dTexture(DIRECTION side, boolean skewed) {
        String name = side.toString().toLowerCase();
        if (!side.isDiagonal()) {
            if (!side.isVertical()) {
                side = side.flip();
            }
            if (!skewed) {
                name = side.isVertical() ? "vert" : "hor";
            }
        }
        return TextureCache.getOrCreateR("ui/cells/bf/3d/" + name + ".png");
    }

    protected TextureRegion getRegion(STD_IMAGES images, String suffix) {
        if (!StringMaster.isEmpty(suffix)) {
            String path = StringMaster.cropFormat(images.getPath());
            path += suffix + ".png";
            return TextureCache.getOrCreateR(path);
        }
        return TextureCache.getOrCreateR(images.getPath());
    }

    //saving space...
    public static String v(Boolean indestructible_nullForSecret) {
        if (indestructible_nullForSecret == null) {
            return WallMap.STRANGE_WALL;
        }
        return indestructible_nullForSecret ? WallMap.INDESTRUCTIBLE : "";
    }

    protected void bindEvents() {
        super.bindEvents();
        GuiEventManager.bind(GuiEventType.UPDATE_DOOR_MAP, p -> {
            doorMap = new ObjectMap<>(
                    (ObjectMap<Coordinates, DOOR_STATE>) p.get());
        });
        GuiEventManager.bind(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, p -> {
            diagonalJoints = new ObjectMap<>(
                    (ObjectMap<Coordinates, List<DIRECTION>>) p.get());
        });
    }

    @Override
    public boolean isTeamColorBorder() {
        return true;
    }

    @Override
    protected EventType getUpdateEvent() {
        return GuiEventType.UPDATE_WALL_MAP;
    }

    protected Vector2 getV(Coordinates coordinates, Object o) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false, true,
                ScreenMaster.getGrid());

        if (isCustomWall())
            v.set(v.x, v.y - 64);
        else {
            v.set(v.x, v.y - 128+getOffsetY());
        }
        return v;
    }

    @Override
    protected void fillDrawMap(List<Pair<Vector2, TextureRegion>> drawMap, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {

        boolean hasVertical = false;
        boolean hasHorizontal = false;
        boolean hasDiagonal = false;
        boolean drawCorner = true;
        boolean diagonalCorner = false;
        boolean round = false;
        boolean diamond = false;
        boolean mesh = false;

        // boolean darken = false;// obj.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT;
        // String suffix = darken ? "dark" : null;
        String suffix = null;
        if (list == null) {
            diamond = true;
        } else {
            for (DIRECTION side : list) {
                TextureRegion image = getRegion(getWallImageFromSide(side), suffix);

                if (side.isDiagonal()) {
                    if (list.size() == 1) {
                        round = true;
                    }

                    drawCorner = true;
                    if (diagonalCorner) // doens't do anything?
                    {
                        diagonalCorner = false;
                    } else if (hasDiagonal) {
                        diagonalCorner = true;
                    } else {
                        hasDiagonal = true;
                    }
                } else {
                    if (list.size() > 1) {
                        mesh = true;
                    }

                    if (side.isVertical()) {
                        if (hasVertical) {
                            drawCorner = false;
                        }
                        hasVertical = true;
                    } else {
                        if (hasHorizontal) {
                            drawCorner = false;
                        }
                        hasHorizontal = true;
                    }
                }

                this.drawMap.add(new ImmutablePair<>(new Vector2(v.x, v.y), image));

                // DOOR_STATE doorState = doorMap.get(coordinates);
                // if (doorState != null) {
                //     Color color = batch.getColor();
                //     //optimization
                //     batch.setColor(new Color(1, 1, 1, fluctuatingAlpha));
                //     batch.draw(image, v.x, v.y);
                //     batch.setColor(color);
                // }
            }
        }

        if (hasHorizontal && hasVertical) {
            drawCorner = true;
        }
        boolean skewed = false;
        if (diagonalCorner) {
            drawCorner = false;
            Boolean x = null;
            Boolean y = null;
            boolean vertical = false;
            for (DIRECTION side : list) {
                if (!side.isDiagonal()) {
                    drawCorner = true;
                    mesh = true;
                } else {
                    if (y != null) {
                        if (y == side.growY) {
                            vertical = true;
                            break;
                        } else {
                            if (x == side.growX) {
                                break;
                            }
                            mesh = true;
                            continue;
                            //TODO
                        }
                    }
                    y = side.growY;
                    x = side.growX;
                }
            }

            if (!mesh) {
                skewed = true; //TODO
                TextureRegion image = getRegion(
                        vertical ? STD_IMAGES.WALL_CORNER_ALMOND_V :
                                STD_IMAGES.WALL_CORNER_ALMOND_H, suffix);
                drawMapOver.put(new Vector2(v.x
                        + (128 - image.getRegionWidth()) / 2, v.y + (128 - image.getRegionHeight()) / 2  ), image);
            }
        }
        if (drawCorner) {
            skewed = true;
            TextureRegion image = getRegion(STD_IMAGES.WALL_CORNER, suffix);
            if (diamond) {
                image = getRegion(STD_IMAGES.WALL_CORNER_DIAMOND, suffix);
            } else if (mesh && hasDiagonal) {
                image = getRegion(STD_IMAGES.WALL_CORNER_MESH, suffix);
            } else if (round) {
                image = getRegion(STD_IMAGES.WALL_CORNER_ROUND, suffix);
            }
            //
            drawMapOver.put(new Vector2(v.x
                    + (128 - image.getRegionWidth()) / 2, v.y + (128 - image.getRegionHeight()) / 2), image);
            //TODO real 3d pillar!

        }
        if (isCustomWall())
            for (DIRECTION side : list) {
                TextureRegion image = get3dTexture(side, skewed);
                if (image != null) {
                    float offsetX = get3dOffsetX(side);
                    float offsetY = get3dOffsetY(side);

                    float adjX = 64;
                    float adjY = 64;
                    TextureRegion region = getRegion(getWallImageFromSide(side), "");
                    TextureRegion _3d = get3dTexture(side, true);

                    if (side == DIRECTION.DOWN)
                        adjY = 0;
                    else if (side.isGrowY() != null)
                        if (side.isGrowY() == true) {
                            int dif = (128 - 36) / 2;
                            adjY = dif;
                        }

                    if (side == DIRECTION.LEFT)
                        adjX = 0;
                    else if (side.isGrowX() != null)
                        if (side.isGrowX() == true) {
                            int dif = (128 - 36) / 2;
                            adjX = dif;
                        }

                    offsetX += adjX;
                    offsetY += adjY;
                    this.drawMap.add(0, new ImmutablePair<>(new Vector2(v.x + offsetX, v.y + offsetY), image));
                }
            }
    }

    private boolean isCustomWall() {
        return false;
    }

    public static final float height3d = 40;
    public static final float diagHeight3d = 45;
    // public static final float width3d=32;
    public static final float diagWidth3d = 25;

    // public static final float w = 36;

    private float get3dOffsetY(DIRECTION side) {
        switch (side) {
            case RIGHT:
            case LEFT:
                return -get3dTexture(side, true).getRegionHeight() / 2; //consider wall's own dim?
            case DOWN_LEFT:
            case UP_RIGHT:
            case DOWN_RIGHT:
            case UP_LEFT:
                return -get3dTexture(side, true).getRegionHeight() / 2;
        }
        return 0;
    }

    private float get3dOffsetX(DIRECTION side) {
        switch (side) {
            // case DOWN_LEFT:
            // case UP_RIGHT:
            //     return diagWidth3d;
            case UP:
            case DOWN:
                return get3dTexture(side, true).getRegionWidth() / 2; //consider wall's own dim?
            case DOWN_RIGHT:
            case UP_LEFT:
                return -get3dTexture(side, true).getRegionWidth() / 2;
        }
        return 0;
    }

    @Override
    protected boolean isCustomDraw() {
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    protected void fillOverlayMap(Coordinates c, ObjectMap<Vector2, TextureRegion> drawMapOver) {
        fillDiagonalJoint(c, drawMapOver);
    }

    private void fillDiagonalJoint(Coordinates c, ObjectMap<Vector2, TextureRegion> drawMapOver) {
        if (diagonalJoints == null)
            return;
        List<DIRECTION> list = diagonalJoints.get(c);
        if (!ListMaster.isNotEmpty(list))
            return;
        int h = GridMaster.CELL_H;
        int w = GridMaster.CELL_W;
        Vector2 v = GridMaster.getVectorForCoordinate(c, false, false, true,
                ScreenMaster.getGrid());
        for (DIRECTION side : list) {
            float x1 = v.x;
            float y1 = v.y+40;

            if (isCustomWall())
                y1 += 64;

            boolean flipped = false;
            if (side == DIRECTION.DOWN_LEFT) {
                y1 -= h;
            } else if (side == DIRECTION.DOWN_RIGHT) {
                x1 += w;
                y1 -= h;
                flipped = true;
            } else if (side == DIRECTION.UP_LEFT) {
                flipped = true;
            } else if (side == DIRECTION.UP_RIGHT) {
                x1 += w;
            }

            String suffix = flipped ? " flipped" : null;
            TextureRegion image = getRegion(STD_IMAGES.WALL_CORNER_ALMOND, suffix);
            //            if (flipped) {
            //            // TODO      image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
            //            }

            drawMapOver.put(new Vector2(x1 - image.getRegionWidth() / 2,
                    y1 - image.getRegionHeight() / 2), image);
        }
    }


    public enum WALL_STYLE {
        STONE,
        BRICK,
        WOOD_PLANKS,
        IRON_BARS,
        SKULLS,

    }

}
