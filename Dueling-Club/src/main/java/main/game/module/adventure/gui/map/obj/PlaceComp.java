package main.game.module.adventure.gui.map.obj;

import main.game.module.adventure.map.Place;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;

import java.awt.*;
import java.awt.event.MouseListener;

public class PlaceComp extends MapObjComp implements MouseListener {

    public static final int DEFAULT_SIZE = GuiManager.getObjSize() - 18;

    public PlaceComp(Place p) {
        super(p);
    }

    public Place getObj() {
        return (Place) super.getObj();
    }

    public Dimension getSize() {
        if (isSymbolRepresentation()) {
            return getSymbolImageSize().getSize();
        }
        if (isInfoSelected()) {
            return new Dimension(DEFAULT_SIZE + 10, DEFAULT_SIZE + 10);
        }
        return new Dimension(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    protected boolean isSymbolRepresentation() {
        // if (getObj().isAvailable())
        // return false;
        return !isInfoSelected();
    }

    protected String getSymbolImagePath() {
        return ImageManager.STD_IMAGES.MAP_PLACE.getPath();
    }

    protected String getSymbolHighlightedImagePath() {
        return ImageManager.STD_IMAGES.MAP_PLACE_HIGHLIGHTED.getPath();
    }

}
