package main.swing.components.panels.page.info.element;

import main.game.bf.Coordinates.DIRECTION;
import main.swing.generic.components.CompVisuals;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster.FONT;

import java.awt.*;

public class IconTextComp extends G_Panel {
    protected FONT type;
    protected Color textColor;
    protected String text;
    protected DIRECTION textDirection;
    protected int textOffset;
    protected int fontSize;
    protected Image image;
    protected String tooltip;
    protected TextCompDC textComp;
    private GraphicComponent graphicComp;

    public IconTextComp(String text, int fontSize, FONT type) {
        this(DIRECTION.DOWN, 12, fontSize, type, ColorManager.GOLDEN_WHITE, text);
    }

    public IconTextComp(DIRECTION textDirection, int textOffset, int fontSize, FONT type,
                        Color textColor, String text) {
        this.textOffset = textOffset;
        this.textDirection = textDirection;
        this.type = type;
        this.fontSize = fontSize;
        this.textColor = textColor;
        this.text = text;
        if (isInitialized()) {
            init();
        }
    }

    protected GraphicComponent createGraphicComponent() {
        return new GraphicComponent(image, tooltip);
    }

    protected TextCompDC createTextComp() {
        return new TextCompDC(null, text, fontSize, type, textColor);
    }

    public void init() {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        setVisuals(new CompVisuals(image));
        graphicComp = createGraphicComponent();
        add(graphicComp, "pos 0 0");
        String relativeX = "@centered_x";
        String relativeY = "@centered_y";
        int x = 0;
        int y = 0;
        if (BooleanMaster.isTrue(textDirection.isGrowX())) {
            relativeX = "icon.x2";
            x = textOffset;
        }
        if (BooleanMaster.isFalse(textDirection.isGrowX())) {
            relativeX = "";
            x = -textOffset;
        }
        if (BooleanMaster.isTrue(textDirection.isGrowY())) {

            relativeY = "icon.y2";
            y = textOffset;
        }
        if (BooleanMaster.isFalse(textDirection.isGrowY())) {

            relativeY = "";
            y = -textOffset;
        }
        String pos = "pos " + relativeX + "+" + x + " " + relativeY + "+" + y;
        textComp = createTextComp();
        add(textComp, pos);
        setPanelSize(new Dimension(width, height));
    }

    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);
    }
}
