package main.game.logic.macro.gui.map.obj;

import main.game.logic.macro.travel.MacroParty;
import main.system.images.ImageManager.BORDER;

import java.awt.*;

public class PartyComp extends MapObjComp {

    private static final int DEFAULT_SIZE = 50;

    public PartyComp(MacroParty p) {
        super(p);
    }

    @Override
    protected BORDER getBorder() {
        return super.getBorder();
    }

    @Override
    public Dimension getSize() {
        if (isSymbolRepresentation()) {
            return getSymbolImageSize().getSize();
        }
        if (isInfoSelected()) {
            return new Dimension(DEFAULT_SIZE + 10, DEFAULT_SIZE + 10);
        }
        return new Dimension(DEFAULT_SIZE, DEFAULT_SIZE);
    }
}
