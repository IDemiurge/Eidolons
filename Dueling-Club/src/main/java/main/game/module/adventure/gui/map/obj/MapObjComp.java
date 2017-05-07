package main.game.module.adventure.gui.map.obj;

import main.game.module.adventure.MacroGame;
import main.game.module.adventure.entity.MacroObj;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ImageTransformer;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MapObjComp implements MouseListener {
    protected Dimension d;
    protected MacroObj obj;
    G_Panel comp; // no custom painting?
    JLabel label;
    JLabel borderLabel;
    boolean highlighted;
    private Image image;
    private BORDER border;

    public MapObjComp(MacroObj p) {
        this.setObj(p);
        comp = new G_Panel() {
            public void paint(java.awt.Graphics g) {
                super.paint(g);
                if (isSymbolRepresentation()) {
                    return;
                }
                BORDER border_ = MapObjComp.this.getBorder();
                if (border_ != null) {
                    g.drawImage(border_.getImage(), 0, 0, (int) getSize().getWidth(),
                            (int) getSize().getHeight(), null);
                }

            }

            ;
        };
        initComp();
    }

    protected void initComp() {
        d = getDimension();
        Image image = getImage();
        label = new JLabel(new ImageIcon(image));
        G_Panel imgPanel = new G_Panel(label);
        comp.add(imgPanel); // TODO pos for symbol vs image
        comp.setPanelSize(d);
        comp.addMouseListener(this);
    }

    public void refresh() {
        d = getDimension();
        comp.setPanelSize(d);
        checkInfoHighlight();
    }

    protected BORDER getBorder() {
        if (border != null) {
            return border;
        }
        if (isInfoSelected() || highlighted) {
            return BORDER.CIRCLE_HIGHLIGHT_96;
        }
        return BORDER.CIRCLE_DEFAULT;
    }

    protected Image getImage() {
        image = null;
        boolean highlighted = isInfoSelected() || this.highlighted;

        if (isSymbolRepresentation()) {
            image = ImageManager.getImage(highlighted ? getSymbolHighlightedImagePath()
                    : getSymbolImagePath());
            Dimension size = getSymbolImageSize();
            if (size != null) {
                image = ImageManager.getSizedVersion(image, size);
            }
            return image;
        }
        // green
        // for
        // available
        // (this.highlighted)
        image = ImageManager.getSizedIcon(getObj().getImagePath(), d).getImage();
        image = ImageTransformer.getCircleCroppedImage(image
                // , null, getDimension() //TODO
        );
        // if (highlighted)
        // image = ImageManager.applyBorder(image, BORDER.CIRCLE_HIGHLIGHT_96,
        // null, getDimension());
        return image;
    }

    protected boolean isInfoSelected() {
        return MacroGame.getGame().getManager().getInfoObj() == getObj();
    }

    protected boolean isSymbolRepresentation() {
        return false;
    }

    protected String getSymbolImagePath() {
        return null;
    }

    protected Dimension getSymbolImageSize() {
        return ImageManager.getImageSize(ImageManager.getImage(getSymbolImagePath()));
    }

    protected Dimension getDimension() {
        if (isSymbolRepresentation()) {
            if (image != null) {
                return new Dimension(image.getWidth(null), image.getHeight(null));
            }
        }
        return getSize();
    }

    public Dimension getSize() {
        if (isSymbolRepresentation()) {
            return getSymbolImageSize().getSize();
        }
        if (isInfoSelected()) {
            return new Dimension(getDefaultSize() * 6 / 5, getDefaultSize() * 6 / 5);
        }
        return new Dimension(getDefaultSize(), getDefaultSize());
    }

    public int getDefaultSize() {
        return 0;
    }

    protected String getSymbolHighlightedImagePath() {
        return null;
    }

    protected void checkInfoHighlight() {
        label.setIcon(new ImageIcon(getImage()));
    }

    public void setHighlighted(boolean b) {
        highlighted = b;
    }

    public G_Panel getComp() {
        return comp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MacroGame.getGame().getManager().objClicked(getObj());

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public MacroObj getObj() {
        return obj;
    }

    public void setObj(MacroObj obj) {
        this.obj = obj;
    }

}
