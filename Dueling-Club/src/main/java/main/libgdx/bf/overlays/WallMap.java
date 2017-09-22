package main.libgdx.bf.overlays;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.libgdx.bf.GridMaster;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.images.ImageManager.STD_IMAGES;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 9/12/2017.
 */
public class WallMap extends Group {
    private Map<Coordinates, List<DIRECTION>> wallMap = new XLinkedMap<>();
    private Map<Coordinates, List<DIRECTION>> diagonalJoints = new XLinkedMap<>();
    private boolean updateRequired;

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

    private void bindEvents() {
        GuiEventManager.bind(GuiEventType.UPDATE_WALL_MAP, p -> {
            wallMap = (Map<Coordinates, List<DIRECTION>>) p.get();
            updateRequired = true;
        });
        GuiEventManager.bind(GuiEventType.UPDATE_DIAGONAL_WALL_MAP, p -> {
            diagonalJoints = (Map<Coordinates, List<DIRECTION>>) p.get();
            updateRequired = true;
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
    private STD_IMAGES getWallImageFromSide(DIRECTION side, String prefix) {
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
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Set<Coordinates> set = new LinkedHashSet<>(wallMap.keySet());
        set.forEach(coordinates -> {
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
            boolean darken =false;// obj.getVisibilityLevel() != VISIBILITY_LEVEL.CLEAR_SIGHT;
            String prefix = darken ? "dark" : "";
            if (list == null) {
                diamond = true;
            } else {
                for (DIRECTION side : list) {
                    TextureRegion image = getRegion(getWallImageFromSide(side, prefix));

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

                    
                    batch.draw(image, v.x,v.y);
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
                                return;
                            }
                        }
                        y = side.isGrowY();
                        x = side.isGrowX();
                    }
                }
                // return;
                if (!mesh) {
                   TextureRegion image = getRegion(STD_IMAGES.WALL_CORNER_ALMOND_H );
                    if (vertical) {
//                        image = STD_IMAGES.WALL_CORNER_ALMOND_V.getPath()
                    }
                    batch.draw(image, v.x
                     +(128-image.getRegionWidth())/2, v.y
                     +(128-image.getRegionHeight())/2);
                }
            }
            if (drawCorner) {
               TextureRegion image = getRegion(STD_IMAGES.WALL_CORNER,prefix);
                if (diamond) {
                    image =getRegion(STD_IMAGES.WALL_CORNER_DIAMOND, prefix);
                } else if (mesh && hasDiagonal) {
                    image = getRegion(STD_IMAGES.WALL_CORNER_MESH,prefix);
                } else if (round) {
                    image = getRegion(STD_IMAGES.WALL_CORNER_ROUND,prefix);
                }
                //
                batch.draw(image, v.x
                  +(128-image.getRegionWidth())/2, v.y
                  +(128-image.getRegionHeight())/2);

            }
        });
//        diagonalJoints.keySet().forEach(coordinates -> {
//            x=getX(coordinates);
//            y=getY(coordinates);
//            for (DIRECTION direction : diagonalJoints.get(coordinates)) {
//                batch.draw(getDiagonalRegion(direction), x, y);
//            }
//        });
        super.draw(batch, parentAlpha);
    }


    private TextureRegion getRegion(STD_IMAGES wallCornerDiamond, String... prefix) {
        return TextureCache.getOrCreateR(wallCornerDiamond.getPath());
    }

    private TextureRegion getCornerRegion(DIRECTION direction) {

        return null;
    }
        private TextureRegion getDiagonalRegion(DIRECTION direction) {
        return null;
    }

    private TextureRegion getRegion(DIRECTION directions) {
        return TextureCache.getOrCreateR(getPath(directions));
    }

    private String getPath(DIRECTION directions) {
        return StrPathBuilder.build("ui", "bf", "");
    }

}
