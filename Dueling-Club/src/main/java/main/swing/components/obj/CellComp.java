package main.swing.components.obj;

import main.entity.Ref;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.swing.components.obj.drawing.DrawMaster;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.LogMaster;
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
    private List<DC_HeroObj> objects;// visible!
    private BufferedImage bufferImage;
    private BufferedImage paintImage;
    private DC_Cell terrainObj;
    private int width;
    private int height;
    private int sizeFactor = 100;
    private DC_BattleFieldGrid holder;
    private DC_Game game;
    private Image centerImage;
    private Coordinates coordinates;
    private Map<SmartText, Point> animOverlayingStrings;
    private Map<Image, Point> animOverlayingImages;
    private Map<Rectangle, Object> mouseMap;
    private List<DC_HeroObj> overlayingObjects;
    private BfGridComp grid;

    public CellComp(DC_BattleFieldGrid holder, Coordinates coordinates) {
        this(holder.getDungeon().getGame(), coordinates, holder.getGridComp());
        this.holder = holder;
    }

    public CellComp(DC_Game game, Coordinates coordinates, BfGridComp bfGridComp) {
        this.coordinates = coordinates;
        this.game = game;
        if (!CoreEngine.isLevelEditor())
            setTerrainObj(new DC_Cell(coordinates.x, coordinates.y, game, new Ref(), game
                    .getDungeon()));
        initPanel();
        resetSize();
        grid = bfGridComp;
    }

    public BfGridComp getGrid() {
        return grid;
    }

    private void resetSize() {
        // if (width == 0)
        if (holder != null)
            width = holder.getGridComp().getCellWidth();
        // if (height == 0)
        if (holder != null)
            height = holder.getGridComp().getCellHeight();
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
                    main.system.auxiliary.LogMaster.log(1, toString() + " has tooltip: " + text);
                }
            }
        };
        panel.setPanelSize(new Dimension(width, height));
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
                    e.printStackTrace();
                    main.system.auxiliary.LogMaster.log(1, toString() + " failed to draw!");
                }
            }
        };

        if (SwingUtilities.isEventDispatchThread()
            // || CoreEngine.isLevelEditor()
                ) {
            drawJob.run();
        } else
            try {
                SwingUtilities.invokeAndWait(drawJob);
            } catch (InvocationTargetException | InterruptedException e) {
                e.printStackTrace();
            }

        // g.drawImage(img, x, y, observer);

    }

    public DC_Obj getTopObjOrCell() {
        if (getObjects().isEmpty())
            return getTerrainObj();
        // return objects.get(0);
        return getObjects().get(getObjects().size() - 1);
    }

    public DC_HeroObj getTopObj() {
        if (getObjects().isEmpty())
            return null;
        // return objects.get(0);
        return getObjects().get(getObjects().size() - 1); // changed?
    }

    public DC_HeroObj getLandscapeObj() {
        for (DC_HeroObj o : getObjects()) {
            if (o.isLandscape())
                return o;
        }
        return null;
    }

    public DC_HeroObj getWallObj() {
        for (DC_HeroObj o : getObjects()) {
            if (o.isWall())
                return o;
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
            if (getObjects().size() > 1)
                return true;
        }
        if (isSingleObj()) {
            if (getObjects().get(0).isBfObj()) {
                return true;
                // return getObjects().get(0).isLandscape();
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
        if (obj instanceof DC_HeroObj) {
            DC_HeroObj her0 = (DC_HeroObj) obj;
            return (her0.isBfObj());
        }
        return false;
    }

    public boolean isTop(Obj obj) {
        if (objects.isEmpty())
            return false;
        return getTopObjOrCell() == obj;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public List<DC_HeroObj> getObjects() {
        if (objects == null)
            objects = new LinkedList<>();
        return objects;
    }

    public void setObjects(List<DC_HeroObj> objects) {
        if (objects != null)
            if (objects.size() > 1) {

                // main.system.auxiliary.LogMaster.log(1, objects +
                // " before sort ");

                Collections.sort(objects, new Comparator<DC_HeroObj>() {
                    @Override
                    public int compare(DC_HeroObj o1, DC_HeroObj o2) {
                        if (o1.isInfoSelected())
                            if (!o2.isInfoSelected())
                                return 1;
                        if (o2.isInfoSelected())
                            if (!o1.isInfoSelected())
                                return -1;
                        if (o1.isTargetHighlighted())
                            if (!o2.isTargetHighlighted())
                                return 1;
                        if (o2.isTargetHighlighted())
                            if (!o1.isTargetHighlighted())
                                return -1;

                        if (o1.isActiveSelected())
                            return 1;
                        if (o2.isActiveSelected())
                            return -1;
                        if (o1.getId() > o2.getId())
                            return 1;
                        if (o1.getId() < o2.getId())
                            return -1;

                        return 0;
                    }
                });

                main.system.auxiliary.LogMaster.log(LogMaster.GUI_DEBUG, objects + " after sort ");
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

    public List<DC_HeroObj> getOverlayingObjects() {
        return overlayingObjects;
    }

    public void setOverlayingObjects(List<DC_HeroObj> objects) {
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
            if (game.getManager().getInfoUnit() == null)
                return false;
            return game.getManager().getInfoUnit().getCoordinates().equals(getCoordinates());
        }
        for (DC_HeroObj o : getObjects()) {
            if (o.isInfoSelected())
                return true;
        }
        return getTopObjOrCell().isInfoSelected();
    }

    public boolean isAnimated() {
        for (DC_HeroObj o : getObjects())
            if (o.isAnimated())
                return true;

        return false;
    }

    public Map<SmartText, Point> getAnimOverlayingStrings() {
        if (animOverlayingStrings == null)
            animOverlayingStrings = (new HashMap<>());
        return animOverlayingStrings;
    }

    public Map<Image, Point> getAnimOverlayingImages() {
        if (animOverlayingImages == null)
            animOverlayingImages = (new HashMap<>());
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
