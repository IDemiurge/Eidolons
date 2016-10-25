package main.swing.components.panels.page.info.element;

import main.content.VALUE;
import main.entity.Entity;
import main.game.battlefield.Coordinates.DIRECTION;
import main.system.auxiliary.FontMaster.FONT;
import main.system.images.ImageManager;

import java.awt.*;

public class IconValueComp extends IconTextComp {

    private VALUE value;
    private Entity entity;

    public IconValueComp(int fontSize, FONT type, VALUE value, Entity entity) {
        super(null, fontSize, type);
        this.value = value;
        this.entity = entity;
        image = ImageManager.getValueIcon(value);
        init();
    }

    public IconValueComp(DIRECTION textDirection, int textOffset, int fontSize, FONT type,
                         Color textColor, VALUE value) {
        super(textDirection, textOffset, fontSize, type, textColor, null);
        this.value = value;
        image = ImageManager.getValueIcon(value);
        init();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    protected TextCompDC createTextComp() {
        return new ValueTextComp(entity, null, value, fontSize, type, textColor);
    }

    @Override
    public void refresh() {
        textComp.refresh();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }
}
