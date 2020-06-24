package eidolons.libgdx.bf.overlays;

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


    private static TextureRegion getRegion(STD_IMAGES images, String suffix) {
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


    @Override
    protected void fillDrawMap(Batch batch, Coordinates coordinates, List<DIRECTION> list, Vector2 v) {

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

                drawMap.add(new ImmutablePair<>(new Vector2(v.x,
                        v.y), image));
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
                TextureRegion image = getRegion(
                        vertical ? STD_IMAGES.WALL_CORNER_ALMOND_V :
                                STD_IMAGES.WALL_CORNER_ALMOND_H, suffix);
                drawMapOver.put(new Vector2(v.x
                        + (128 - image.getRegionWidth()) / 2,v.y+ (128 - image.getRegionHeight()) / 2), image);
            }
        }
        if (drawCorner) {
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
                    + (128 - image.getRegionWidth()) / 2, v.y+ (128 - image.getRegionHeight()) / 2), image);

        }
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
            float y1 = v.y;
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
