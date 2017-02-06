package main.game.logic.dungeon.minimap;

import main.content.CONTENT_CONSTS.UNIT_TO_PLAYER_VISION;
import main.content.CONTENT_CONSTS.UNIT_TO_UNIT_VISION;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.game.logic.dungeon.Dungeon;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.rules.mechanics.PerceptionRule.PERCEPTION_STATUS;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.Refreshable;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.datatypes.DequeImpl;
import main.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.images.ImageManager.HIGHLIGHT;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MiniObjComp implements Refreshable {
    static Map<Image, Map<HIGHLIGHT, BufferedImage>> highlightedImageCacheMap = new HashMap<>();
    protected G_Panel comp;
    protected DC_Obj obj;
    protected Dimension size;
    protected Image image;
    // protected Image buffer;
    protected Dungeon dungeon;
    private int width;
    private int height;
    private HIGHLIGHT hl;
    private Minimap map;
    private boolean overlaying;
    private DequeImpl<DC_Obj> objects;

    public MiniObjComp(boolean overlaying, DC_Obj obj, Minimap map) {
        initComp();
        this.overlaying = overlaying;
        this.obj = obj;
        this.map = map;
        dungeon = obj.getGame().getDungeonMaster().getDungeon();
        initSize(map.getSize());
        refresh();
    }

    public MiniObjComp(DC_Obj obj, Minimap map) {
        this(false, obj, map);
    }

    public void initSize(Dimension mapSize) {
        width = isOverlaying() ? map.getGrid().getOverlayingObjWidth() : map.getGrid()
                .getCellWidth();
        height = width;
        // (int) (mapSize.getWidth() / dungeon.getCellsX());
        // height = (int) (mapSize.getHeight() / dungeon.getCellsY());
        // maximum possible size for square dimensions
        // width = Math.min(width, height);
        // height = Math.min(width, height);
        size = new Dimension(getWidth(), getHeight());
        if (comp != null) {
            comp.setPanelSize(size);
        }
    }

    public void addObj(DC_Obj obj) {
        getObjects().add(obj);
        if (this.obj == null) {
            this.obj = obj;
        }
    }

    public DequeImpl<DC_Obj> getObjects() {
        if (objects == null) {
            objects = new DequeImpl<>();
        }
        if (obj instanceof DC_HeroObj) {
            if (!objects.contains(obj)) {
                objects.add(obj);
            }
        }
        return objects;
    }

    public void setObjects(DequeImpl<DC_Obj> objects) {
        this.objects = objects;
    }

    private void applyVisibility() {
        // TODO inter-dependence of the four...?
        UNIT_TO_PLAYER_VISION detection = obj.getActivePlayerVisionStatus();
        UNIT_TO_UNIT_VISION vision = obj.getUnitVisionStatus();
        VISIBILITY_LEVEL visibility = obj.getVisibilityLevel();
        PERCEPTION_STATUS perception = obj.getPerceptionStatus();
        // ++ TARGETING HL
        boolean hidden = false; // draw image / info icons
        if (detection == UNIT_TO_PLAYER_VISION.UNKNOWN) {
            image = (ImageManager.getHiddenCellIcon()).getImage();
            obj.setImage("UI//cells//Hidden Cell v" + 1 + ".png");
            return;
        }
        // ImageManager.getOutlineImage(obj);
        // ImageTransformer.getTransparent(image, trasparency);
        // // ImageManager.STD_IMAGES.THICK_DARKNESS
        // PerceptionRule.getHints(obj, perception, visibility);
        // TO BE USED WHEN UNIT ACTS -
        // "something huge/monstrous/humanoid/mechanical/ghostly/demonic..." etc

        // if (vision == UNIT_TO_UNIT_VISION.IN_SIGHT) -> negate other effects?
        // which status has priority? -> VISIBILITY!

		/*
         * perception:
		 *
		 *
		 *
		 */

        boolean terrain = (obj instanceof DC_Cell);

        if (vision != UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT) {
            if (detection == UNIT_TO_PLAYER_VISION.DETECTED) {
                image = !terrain ? ImageManager
                        .applyBorder(image, ImageManager.BORDER_BEYOND_SIGHT) : (ImageManager
                        .getHiddenCellIcon()).getImage();
            } else {
                image = !terrain ? ImageManager.applyBorder(image, ImageManager.BORDER_UNKNOWN)
                        : (ImageManager.getUnknownCellIcon()).getImage();
            }
        } else {
            // if (visibility == VISIBILITY_LEVEL.THICK_DARKNESS) {
            //
            // }
        }

    }

    public void setHighlight(HIGHLIGHT hl) {
        this.hl = hl;

    }

    private void applyHighlights() {
        if (hl != null) {
            Map<HIGHLIGHT, BufferedImage> highlightedImageCache = highlightedImageCacheMap
                    .get(image);
            if (highlightedImageCache == null) {
                highlightedImageCache = new HashMap<HIGHLIGHT, BufferedImage>();
                highlightedImageCacheMap.put(image, highlightedImageCache);
            }
            BufferedImage buffer = highlightedImageCache.get(hl);
            if (buffer == null) {
                buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                buffer.getGraphics().drawImage(image, 0, 0, width, height, null);
                Image sizedVersion = ImageManager.getSizedIcon(hl.getBorder().getImagePath(),
                        size).getImage();
                buffer.getGraphics().drawImage(sizedVersion, 0, 0, null);
                highlightedImageCache.put(hl, buffer);
            }
            image = buffer;
        }
        boolean terrain = (obj instanceof DC_Cell);
        if (terrain) {
            // green?
        } else if (obj.isInfoSelected()) {
            image = ImageManager.applyImageNew(image, ImageManager.BORDER_INFO_SELECTION_HIGHLIGHT
                    .getImage());
        }
        // image = ImageManager.applyBorder(image,
        // ImageManager.BORDER_INFO_SELECTION_HIGHLIGHT);

    }

    protected void drawImage(Graphics g) { // borderX
        g.drawImage(image, 0, 0, (int) size.getWidth(), (int) size.getHeight(), null);
    }

    public Dimension getCellSize() {
        return size;
    }

    // protected void applyUnknown() {
    // if (terrain) {
    // setPic(ImageManager.getUnknownCellIcon());
    // } else {
    // setPic(ImageManager.STD_IMAGES.UNKNOWN_UNIT.getIcon());
    // }
    // }
    //
    // protected void applyBeyondSight() {
    // if (terrain) {
    // if (detection == UNIT_TO_PLAYER_VISION.DETECTED) {
    // setPic(ImageManager.getHiddenCellIcon());
    // } else
    // setPic(ImageManager.getUnknownCellIcon());
    // } else {
    // if (detection == UNIT_TO_PLAYER_VISION.DETECTED) {
    // setPic(new ImageIcon(ImageManager.applyBorder(getPic()
    // .getImage(), BORDER.CONCEALED)));
    // } else
    // setPic(new ImageIcon(ImageManager.applyBorder(getPic()
    // .getImage(), BORDER.HIDDEN)));
    //
    // }
    // }

    protected void drawInfoIcons(Graphics g) {
        if (getObjects().size() > 1) {
            Image img = STD_IMAGES.GUARD.getImage();
            // if (img.getWidth(null) > getWidth() / 3)
            // img = ImageManager.getSizedVersion(img, new Dimension(getWidth()
            // / 3,
            // getHeight() / 3));
            g.drawImage(img, 0, 0, getWidth() / 3, getHeight() / 3, null);
        }

    }

    private void initComp() {
        this.comp = new G_Panel() {
            public void paint(Graphics g) {
                drawImage(g);
                drawInfoIcons(g);
                super.paint(g);
            }

            // public Dimension getSize() {
            // return getCellSize();
            // }
        }; // MinimapCompMouseListener
        if (!MiniGrid.isMouseDragOffsetModeOn()) {
            comp.addMouseListener(getMouseListener()); // revamp!
        }
        comp.setPanelSize(size);
    }

    private MouseListener getMouseListener() {
        return new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                // TODO ALT_CLICK = SHOW ALL OBJECTS
                clicked(e.getPoint());

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

        };
    }

    public void clicked(Point point) {
        if (getObjects().size() < 2) {
            obj.getGame().getManager().objClicked(obj);
            return;
        }

        // if ( SwingUtilities.isRightMouseButton(e)) {
        // obj.getGame().getManager().objClicked(obj);
        // }

        if (point.x < getWidth() / 3) {
            if (point.y < getHeight() / 3) {
                // CYCLE THRU OBJs
                Obj choice = DialogMaster.objChoice("Which object?", getObjects().toArray(
                        new Obj[getObjects().size()]));
                if (choice != null) {
                    obj = (DC_Obj) choice;
                    obj.getGame().getManager().objClicked(obj);
                }
                return;
            }
        }

        obj.getGame().getManager().objClicked(obj);
    }

    public DC_Obj getTopObj() {
        // topObj
        return obj;
    }

    public boolean isOverlaying() {
        return overlaying;
    }

    public void refresh() {
        image = ImageManager.getImage(obj.getImagePath());
        if (obj.isFlippedImage()) {
            image = ImageTransformer.flipHorizontally(ImageManager.getBufferedImage(image));
        }
        applyVisibility();
        applyHighlights();
        comp.setToolTipText(obj.getToolTip());
        comp.repaint();
        comp.revalidate();
        if (getComp().getMouseListeners() != null) {
            if (getComp().getMouseListeners().length > 0) {
                main.system.auxiliary.LogMaster.log(1, getComp().getMouseListeners() + " on "
                        + getComp().toString());
            }
        }
    }

    private int getHeight() {
        return height;
    }

    private int getWidth() {
        return width;
    }

    public G_Panel getComp() {
        return comp;
    }

    public DC_Obj getObj() {
        return obj;
    }

    public void setObj(DC_Obj obj) {
        this.obj = obj;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public enum VISIBILITY_RENDER {
        TRANSPARENCY, DARKEN, IMAGE_UNKNOWN, IMAGE_OUTLINE,
    }

    public enum IDENTIFICATION_STATUS {

    }

}
