package main.libgdx.bf.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.module.dungeoncrawl.objects.DoorMaster.DOOR_STATE;
import main.libgdx.bf.GridConst;
import main.libgdx.bf.GridMaster;
import main.libgdx.bf.SuperActor;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.images.ImageManager.STD_IMAGES;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 9/12/2017.
 */
public class WallMap extends SuperActor {
    private static boolean on = true;
    private Map<Coordinates, List<DIRECTION>> wallMap;
    private Map<Coordinates, List<DIRECTION>> diagonalJoints;
    private boolean updateRequired;
    private Map<Coordinates, DOOR_STATE> doorMap;

    public WallMap() {
        //doors? probably as separate actors
        bindEvents();
        //idea - just statically modify wall actors?
        // flipping, darkening, ...

        // main challenge - sometimes wall is "full", sometimes "fractured"

        /*
        new in this version:
        > My aim might be to transition into "overlays only" wall mode
         >
        > slight alpha fluctuation?

         */

    }

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        WallMap.on = on;
    }

    private static STD_IMAGES getWallImageFromSide(DIRECTION side, String suffix) {
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

    private static String getPath(DIRECTION directions) {
        return StrPathBuilder.build("ui", "bf", "");
    }

    private static TextureRegion getRegion(STD_IMAGES images, String suffix) {
//        images.getPathsuffixedImage(j).g
        if (!StringMaster.isEmpty(suffix)) {
            String path = StringMaster.cropFormat(images.getPath());
            path += suffix + ".png";
            return TextureCache.getOrCreateR(path);
        }
        return TextureCache.getOrCreateR(images.getPath());
    }

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_DOOR_MAP, p -> {
            doorMap = new XLinkedMap<>(
             (Map<Coordinates, DOOR_STATE>) p.get());
            updateRequired = true;
        });
        GuiEventManager.bind(GuiEventType.UPDATE_WALL_MAP, p -> {
            wallMap = new XLinkedMap<>(
             (Map<Coordinates, List<DIRECTION>>) p.get());
            updateRequired = true;
        });
        GuiEventManager.bind(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, p -> {
            diagonalJoints = new XLinkedMap<>(
             (Map<Coordinates, List<DIRECTION>>) p.get());
            updateRequired = true;
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(new Color(1, 1, 1, 1));
        if (wallMap == null)
            return;
        Set<Coordinates> set = wallMap.keySet();
        for (Coordinates coordinates : set) {
            if (checkCoordinateIgnored(coordinates))
                continue;

            List<DIRECTION> list = wallMap.get(coordinates);
            boolean hasVertical = false;
            boolean hasHorizontal = false;
            boolean hasDiagonal = false;
            boolean drawCorner = true;
            boolean diagonalCorner = false;
            boolean round = false;
            boolean diamond = false;
            boolean mesh = false;

            Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false);
            v.set(v.x, v.y - 128);
            boolean darken = false;// obj.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT;
            String suffix = darken ? "dark" : null;
            if (list == null) {
                diamond = true;
            } else {
                for (DIRECTION side : list) {
                    TextureRegion image = getRegion(getWallImageFromSide(side, suffix), suffix);

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

                    DOOR_STATE doorState = doorMap.get(coordinates);
                    if (doorState != null) {
                        Color color = batch.getColor();
                        batch.setColor(new Color(1, 1, 1, fluctuatingAlpha));
                        batch.draw(image, v.x, v.y);
                        batch.setColor(color);
                    } else {
                        batch.draw(image, v.x, v.y);
                    }
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
                            if (y == side.isGrowY()) {
                                vertical = true;
                                break;
                            } else {
                                if (x == side.isGrowX()) {
                                    break;
                                }
                                mesh = true;
                                continue;
                                //TODO
                            }
                        }
                        y = side.isGrowY();
                        x = side.isGrowX();
                    }
                }

                if (!mesh) {
                    TextureRegion image = getRegion(
                     vertical ? STD_IMAGES.WALL_CORNER_ALMOND_V :
                      STD_IMAGES.WALL_CORNER_ALMOND_H, suffix);
                    batch.draw(image, v.x
                     + (128 - image.getRegionWidth()) / 2, v.y
                     + (128 - image.getRegionHeight()) / 2);
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
                batch.draw(image, v.x
                 + (128 - image.getRegionWidth()) / 2, v.y
                 + (128 - image.getRegionHeight()) / 2);

            }
        }
        drawDiagonalJoints(batch, set);
        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean isAlphaFluctuationOn() {
        return super.isAlphaFluctuationOn();
    }

    @Override
    protected float getAlphaFluctuationPerDelta() {
        return super.getAlphaFluctuationPerDelta();
    }

    @Override
    protected float getAlphaFluctuationMin() {
        return super.getAlphaFluctuationMin();
    }

    @Override
    protected float getAlphaFluctuationMax() {
        return super.getAlphaFluctuationMax();
    }

    private void drawDiagonalJoints(Batch batch, Set<Coordinates> set) {
        if (diagonalJoints == null)
            return;
        for (Coordinates c : set) {
            List<Coordinates.DIRECTION> list = diagonalJoints.get(c);
            if (!ListMaster.isNotEmpty(list))
                continue;
            if (checkCoordinateIgnored(c))
                continue;
            int h = GridConst.CELL_H;
            int w = GridConst.CELL_W;
            for (Coordinates.DIRECTION side : list) {

                Vector2 v = GridMaster.getVectorForCoordinate(c, false, false);

                float x1 = v.x;
                float y1 = v.y;
                boolean flipped = false;
                if (side == Coordinates.DIRECTION.DOWN_LEFT) {
                    y1 -= h;
                } else if (side == Coordinates.DIRECTION.DOWN_RIGHT) {
                    x1 += w;
                    y1 -= h;
                    flipped = true;
                } else if (side == Coordinates.DIRECTION.UP_LEFT) {
                    flipped = true;
                } else if (side == Coordinates.DIRECTION.UP_RIGHT) {
                    x1 += w;
                }

                String suffix = flipped ? " flipped" : null;
                TextureRegion image = getRegion(STD_IMAGES.WALL_CORNER_ALMOND, suffix);
//            if (flipped) {
//            // TODO      image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
//            }


                batch.draw(image, x1 - image.getRegionWidth() / 2, y1 - image.getRegionHeight() / 2);
            }
        }
    }


    private boolean checkCoordinateIgnored(Coordinates coordinates) {
        Vector2 v = GridMaster.getVectorForCoordinate(coordinates, false, false);
        v.set(v.x, v.y - 128);
        float offsetX = v.x;
        float offsetY = v.y;
        if (!DungeonScreen.getInstance().getController().
         isWithinCamera(getX() + offsetX, getY() + offsetY, 128, 128)) {
            return true;
        }
        return false;
    }

    public enum WALL_STYLE {
        STONE,
        BRICK,
        WOOD_PLANKS,
        IRON_BARS,
        SKULLS,

    }

}
