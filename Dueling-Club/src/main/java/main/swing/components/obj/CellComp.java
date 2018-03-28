package main.swing.components.obj;

import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.swing.components.obj.drawing.DrawMaster;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.text.SmartText;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class CellComp {
    private G_Panel panel;
    private List<Unit> objects;// visible!
    private BufferedImage bufferImage;
    private BufferedImage paintImage;
    private DC_Cell terrainObj;
    private int width;
    private int height;
    private int sizeFactor = 100;
    private DC_Game game;
    private Image centerImage;
    private Coordinates coordinates;
    private Map<SmartText, Point> animOverlayingStrings;
    private Map<Image, Point> animOverlayingImages;
    private Map<Rectangle, Object> mouseMap;
    private List<Unit> overlayingObjects;
    private BfGridComp grid;

    public CellComp(DC_Game game, Coordinates coordinates, BfGridComp bfGridComp) {
        this.coordinates = coordinates;
        this.game = game;
        if (!CoreEngine.isLevelEditor()) {
            setTerrainObj(new DC_Cell(coordinates.x, coordinates.y, game, new Ref(), game
             .getDungeon()));
        }
        grid = bfGridComp;
        initPanel();
    }

    public BfGridComp getGrid() {
        return grid;
    }

    private void resetSize() {
        width = grid.getCellWidth();
        height = grid.getCellHeight();
        panel.setPanelSize(new Dimension(width, height));
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    private void initPanel() {
        panel = new G_Panel() {
            protected void paintComponent(Graphics g) {
                if (bufferImage != null) {
                    g.drawImage(bufferImage, 0, 0, null);
                }
                String text = getToolTipText();
                if (text != null) {
                    LogMaster.log(1, toString() + " has tooltip: " + text);
                }
            }
        };
        resetSize();
        panel.setIgnoreRepaint(true);
    }

    @Override
    public String toString() {
        if (isTerrain()) {
            return "comp " + getTerrainObj().getNameAndCoordinate();
        }
        return "comp " + getTopObj().getNameAndCoordinate();
    }

    public void removeAnimation() {
        getAnimOverlayingImages().clear();
        getAnimOverlayingStrings().clear();
        centerImage = null;

    }

    public void refresh() {
        // getAnimOverlayingImages().clear();
        // getAnimOverlayingStrings().clear();
        // centerImage = null;
        resetSize();
        bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        final Graphics g = bufferImage.getGraphics();
        // Chronos.mark("drawing " + this);
        final CellComp cell = this;

        Runnable drawJob = new Runnable() {
            @Override
            public void run() {
                try {
                    new DrawMaster().draw(cell, g, sizeFactor, game.isSimulation());
                    g.dispose();
                    setPaintImage(bufferImage);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                    LogMaster.log(1, toString() + " failed to draw!");
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            drawJob.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(drawJob);
            } catch (InvocationTargetException | InterruptedException e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

        // g.drawImage(img, x, y, observer);

    }

    public DC_Obj getTopObjOrCell() {
        if (getObjects().isEmpty()) {
            return getTerrainObj();
        }
        // return objects.getOrCreate(0);
        return getObjects().get(getObjects().size() - 1);
    }

    public Unit getTopObj() {
        if (getObjects().isEmpty()) {
            return null;
        }
        // return objects.getOrCreate(0);
        return getObjects().get(getObjects().size() - 1); // changed?
    }

    public Unit getLandscapeObj() {
        for (Unit o : getObjects()) {
            if (o.isLandscape()) {
                return o;
            }
        }
        return null;
    }

    public Unit getWallObj() {
        for (Unit o : getObjects()) {
            if (o.isWall()) {
                return o;
            }
        }
        return null;
    }

    public boolean isWall() {
        return getWallObj() != null;
    }

    public boolean isLandscape() {
        return getLandscapeObj() != null;
    }

    public Boolean isMiniCellFrame() {
        return isMiniCellFrame(true);
    }

    public Boolean isMiniCellFrame(boolean stacked) {
        if (stacked) {
            // if (getObjects().size() > 3) //'no frame'
            // return null;
            if (getObjects().size() > 1) {
                return true;
            }
        }
        if (isSingleObj()) {
            if (getObjects().get(0).isBfObj()) {
                return true;
                // return getObjects().getOrCreate(0).isLandscape();
            }
            return getObjects().get(0).isHuge();
        }
        return false;
    }

    public boolean isMultiObj() {
        return getObjects().size() > 1;
    }

    public boolean isSingleObj() {
        return getObjects().size() == 1;
    }

    public boolean isTerrain() {
        return getObjects().isEmpty();
    }

    public Image getCenterOverlayingImage() {
        return centerImage;
    }

    public void setCenterOverlayingImage(Image img) {
        this.centerImage = img;

    }

    public boolean isBfObj(Obj obj) {
        if (obj instanceof Unit) {
            Unit her0 = (Unit) obj;
            return (her0.isBfObj());
        }
        return false;
    }

    public boolean isTop(Obj obj) {
        if (objects.isEmpty()) {
            return false;
        }
        return getTopObjOrCell() == obj;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public List<Unit> getObjects() {
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return objects;
    }

    public void setObjects(List<Unit> objects) {
        if (objects != null) {
            if (objects.size() > 1) {

                // main.system.auxiliary.LogMaster.log(1, objects +
                // " before sort ");

                Collections.sort(objects, new Comparator<Unit>() {
                    @Override
                    public int compare(Unit o1, Unit o2) {
                        if (o1.isInfoSelected()) {
                            if (!o2.isInfoSelected()) {
                                return 1;
                            }
                        }
                        if (o2.isInfoSelected()) {
                            if (!o1.isInfoSelected()) {
                                return -1;
                            }
                        }
                        if (o1.isTargetHighlighted()) {
                            if (!o2.isTargetHighlighted()) {
                                return 1;
                            }
                        }
                        if (o2.isTargetHighlighted()) {
                            if (!o1.isTargetHighlighted()) {
                                return -1;
                            }
                        }

                        if (o1.isActiveSelected()) {
                            return 1;
                        }
                        if (o2.isActiveSelected()) {
                            return -1;
                        }
                        if (o1.getId() > o2.getId()) {
                            return 1;
                        }
                        if (o1.getId() < o2.getId()) {
                            return -1;
                        }

                        return 0;
                    }
                });

                LogMaster.log(LogMaster.GUI_DEBUG, objects + " after sort ");
            }
        }
        // if (objects.equals(this.objects)){

        this.objects = objects;
    }

    public BufferedImage getPaintImage() {
        return paintImage;
    }

    private void setPaintImage(BufferedImage paintImage) {
        this.paintImage = paintImage;
    }

    public List<Unit> getOverlayingObjects() {
        return overlayingObjects;
    }

    public void setOverlayingObjects(List<Unit> objects) {
        this.overlayingObjects = objects;

    }

    public DC_Cell getTerrainObj() {
        if (CoreEngine.isLevelEditor()) {
            return game.getCellByCoordinate(getCoordinates());
        }
        return terrainObj;
    }

    private void setTerrainObj(DC_Cell terrainObj) {
        this.terrainObj = terrainObj;
    }

    public void addAnimOverlayingString(Point p, SmartText text) {
        getAnimOverlayingStrings().put(text, p);

    }

    public void addAnimOverlayingImage(Point p, Image img) {
        getAnimOverlayingImages().put(img, p);

    }

    public boolean isInfoSelected() {
        if (CoreEngine.isLevelEditor()) {
            if (game.getManager().getInfoUnit() == null) {
                return false;
            }
            return game.getManager().getInfoUnit().getCoordinates().equals(getCoordinates());
        }
        for (Unit o : getObjects()) {
            if (o.isInfoSelected()) {
                return true;
            }
        }
        return getTopObjOrCell().isInfoSelected();
    }

    public boolean isAnimated() {
        for (Unit o : getObjects()) {
            if (o.isAnimated()) {
                return true;
            }
        }

        return false;
    }

    public Map<SmartText, Point> getAnimOverlayingStrings() {
        if (animOverlayingStrings == null) {
            animOverlayingStrings = (new HashMap<>());
        }
        return animOverlayingStrings;
    }

    public Map<Image, Point> getAnimOverlayingImages() {
        if (animOverlayingImages == null) {
            animOverlayingImages = (new HashMap<>());
        }
        return animOverlayingImages;
    }

    public Map<Rectangle, Object> getMouseMap() {
        return mouseMap;
    }

    public void setMouseMap(Map<Rectangle, Object> mouseMap) {
        this.mouseMap = mouseMap;

    }

    public void setSizeFactor(int sizeFactor) {
        this.sizeFactor = sizeFactor;
    }

    public DC_Game getGame() {
        return game;
    }

    private int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
