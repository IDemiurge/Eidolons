package main.swing.components.obj.drawing;

import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.DirectionMaster;
import main.rules.mechanics.ConcealmentRule;
import main.rules.mechanics.WatchRule;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.GuiManager;
import main.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.test.debug.DebugMaster;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 03.11.2016
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class DrawMasterStatic {
    public static boolean GRAPHICS_TEST_MODE = false;
    public static boolean FULL_GRAPHICS_TEST_MODE = false;
    protected static Map<Obj, BufferedImage> objImageCache = new HashMap<>();
    private static boolean sightVisualsOn = true;

    public static boolean isEditorMode() {
        return CoreEngine.isLevelEditor();
    }

    public static boolean checkWallTresspassed(Coordinates origin, Coordinates destination) {
        // how did we do that ClearShotCondition?
        // it just allows walking on the walled cells as long as you walk
        // *alongside* it
        // totally two-sided, so we just have Boolean3, yet there are also
        // CORNERS...
        // if contains null! then need to check the SIDES of other adjacent
        // WALLS to know
        // what if an alternative path is there via a broken section?
        return false;

    }

    public static void drawWatchInfo(int zoom, Graphics g) {
        DC_Obj info = DC_Game.game.getManager().getInfoObj();
        if (info instanceof DC_HeroObj) {
            java.util.List<DC_Obj> watchedObjects = WatchRule.getWatchersMap().get(info);
            if (watchedObjects != null) {
                drawWatchVisuals(watchedObjects, (DC_HeroObj) info, g, zoom, watchedObjects, false,
                        true);
                drawWatchVisuals(watchedObjects, (DC_HeroObj) info, g, zoom, watchedObjects, false,
                        false);
            }
        } else {
            DC_HeroObj active = DC_Game.game.getManager().getActiveObj();
            if (active.isMine()) {
                java.util.List<DC_Obj> watchedObjects = WatchRule.getWatchersMap().get(active);
                if (watchedObjects != null) {
                    drawWatchVisuals(watchedObjects, active, g, zoom, watchedObjects, true, true);
                    drawWatchVisuals(watchedObjects, active, g, zoom, watchedObjects, true, false);
                }
            }
        }
    }

    private static void drawWatchVisuals(java.util.List<DC_Obj> watchedObjects, DC_HeroObj watcher,
                                         Graphics g, int zoom, java.util.List<DC_Obj> list, boolean active, boolean lines_images) {
        int w = ImageManager.STD_IMAGES.WATCHER.getWidth() * zoom / 100;
        int h = ImageManager.STD_IMAGES.WATCHER.getHeight() * zoom / 100;
        Point p = getPointFromCoordinates(lines_images, w, h, watcher.getCoordinates(), watcher
                .getFacing().getDirection(), zoom);
        for (DC_Obj watched : watchedObjects) {
            Point p2 = getPointFromCoordinates(lines_images, w, h, watched.getCoordinates(),
                    DirectionMaster.rotate180(DirectionMaster
                            .getRelativeDirection(watcher, watched)), zoom);
            if (lines_images) {
                g.setColor(watcher.isMine() ? ColorManager.CYAN : ColorManager.CRIMSON);
                int offsetX = 0;
                int offsetY = 0;
                if (!watcher.getFacing().isVertical()) {
                    offsetX = !watcher.getFacing().isCloserToZero() ? 3 : -3;
                } else {
                    offsetY = !watcher.getFacing().isCloserToZero() ? 3 : -3;
                }
                g.drawLine(p.x + offsetX, p.y + offsetY, p2.x, p2.y); // TODO
                // punctir...
                // visualize clear shot too?
                // Painter.paintImagesInLine(g, new XLine(p, p2),
                // STD_IMAGES.EYE.getImage(),
                // 40 * zoom / 100);
            } else {
                Image image = ImageManager.STD_IMAGES.WATCHER.getImage();
                drawImage(g, image, p.x, p.y);
                image = ImageManager.STD_IMAGES.WATCHED.getImage();
                drawImage(g, image, p2.x, p2.y);
            }
        }

    }

    private static Point getPointFromCoordinates(boolean lines_images, int w, int h,
                                                 Coordinates coordinates, Coordinates.DIRECTION d, int zoom) {
        Point p = DC_Game.game.getBattleField().getGrid().getGridComp().mapToPoint(coordinates);
        int cellWidth = GuiManager.getCellWidth() * zoom / 100;
        int cellHeight = GuiManager.getCellHeight() * zoom / 100;
        int xOffset = cellWidth / 2 - (lines_images ? 0 : w / 2);
        int yOffset = cellHeight / 2 - (lines_images ? 0 : h / 2);

        if (d.isGrowX() != null) {
            xOffset = (d.isGrowX()) ? cellWidth - w : 0;
        }
        if (d.isGrowY() != null) {
            yOffset = (d.isGrowY()) ? cellHeight - h : 0;
        }

        return new Point(p.x + xOffset, p.y + yOffset);
    }

    public static void drawDiagonalJoints(int zoom, Graphics g, int offsetX, int offsetY, int w,
                                          int h, Map<Coordinates, java.util.List<Coordinates.DIRECTION>> diagonalJoints) {
        for (Coordinates c : diagonalJoints.keySet()) {
            boolean darken = false;
            DC_HeroObj obj = DC_Game.game.getUnitByCoordinate(c);
            // TODO CHECK WALL
            if (obj == null) {
                obj = DC_Game.game.getUnitByCoordinate(c);
                continue;
            }
            if (!CoreEngine.isLevelEditor()) {
                if (!obj.isDetected()) {
                    if (!DebugMaster.isOmnivisionOn())
                    // obj.getPlayerVisionStatus(false) !=
                    // UNIT_TO_PLAYER_VISION.DETECTED
                    {
                        continue;
                    }
                }
            }
            if (obj != null) {
                darken = obj.getVisibilityLevel() != ConcealmentRule.VISIBILITY_LEVEL.CLEAR_SIGHT;
            }
            String prefix = darken ? "dark" : "";
            int x = w * (c.x - offsetX);
            int y = h * (c.y - offsetY);
            if (x < 0) {
                continue;
            }
            if (x > GuiManager.getBattleFieldWidth()) {
                continue;
            }

            if (y < 0) {
                continue;
            }
            if (y > GuiManager.getBattleFieldHeight()) {
                continue;
            }
            java.util.List<Coordinates.DIRECTION> list = diagonalJoints.get(c);

            for (Coordinates.DIRECTION side : list) {
                int x1 = x;
                int y1 = y;
                boolean flipped = false;
                if (side == Coordinates.DIRECTION.DOWN_LEFT) {
                    y1 += h;
                } else if (side == Coordinates.DIRECTION.DOWN_RIGHT) {
                    x1 += w;
                    y1 += h;
                    flipped = true;
                } else if (side == Coordinates.DIRECTION.UP_LEFT) {
                    flipped = true;
                } else if (side == Coordinates.DIRECTION.UP_RIGHT) {
                    x1 += w;
                }
                // if (list.indexOf(side) > 0) {
                // flipped = !flipped;
                // }
                // getOrCreate the corner for this side
                // add offset if necessary

                Image image = ImageManager.STD_IMAGES.WALL_CORNER_ALMOND.getPathPrefixedImage(prefix);

                if (zoom != 100) {
                    image = ImageManager.getSizedVersion(image, zoom, true);
                }
                if (flipped) {
                    image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
                }
                // TODO scale them!
                drawImage(g, image, x1 - image.getWidth(null) / 2, y1 - image.getHeight(null) / 2);

            }

        }
    }

    public static void drawImageCentered(Graphics g, Image image, int w, int h) {
        int y = (h - image.getHeight(null)) / 2;
        int x = (w - image.getWidth(null)) / 2;
        drawImage(g, image, x, y);
    }

    public static void drawImage(Graphics g, Image image, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public static boolean isSightVisualsOn() {
        if (isEditorMode()) {
            return false;
        }
        return false;
        // return sightVisualsOn;
    }

    public static void setSightVisualsOn(boolean sightVisualsOn) {
        DrawMasterStatic.sightVisualsOn = sightVisualsOn;
    }

    public static Map<Obj, BufferedImage> getObjImageCache() {
        return objImageCache;
    }

    public static void setObjImageCache(Map<Obj, BufferedImage> objImageCache) {
        DrawMasterStatic.objImageCache = objImageCache;
    }
}
