package main.swing.components;

import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_UnitObj;
import main.game.battlefield.VisionManager;
import main.swing.components.panels.page.DC_PagedPriorityPanel;
import main.swing.generic.components.G_Panel;
import main.swing.renderers.SmartTextManager;
import main.system.auxiliary.ColorManager;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.GuiManager;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.math.MathMaster;
import main.system.text.SmartText;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriorityListItem extends G_Panel {

    private static final int X = 6;
    private static final int Y = 10;
    private static final Dimension AP_IMG_DIMENSION = new Dimension(17, 19);
    protected Map<Point, Image> specialOverlayingImages = new ConcurrentHashMap<Point, Image>();
    protected Map<Point, SmartText> specialOverlayingStrings = new ConcurrentHashMap<Point, SmartText>();
    SmartTextManager smartMaster = new SmartTextManager();
    boolean clock;
    private DC_UnitObj unit;
    private JLabel lbl;
    private Image img;
    private SmartText apText;
    private Point p2;
    private Point p1;
    private SmartText initText;
    private String emptyIcon = ImageManager.getAltEmptyListIcon();

    public PriorityListItem(DC_UnitObj unit) {
        this.unit = unit;
        if (unit != null) {
            if (unit.getName().equals(DC_PagedPriorityPanel.CLOCK_UNIT)) {
                clock = true;
                unit.setProperty(G_PROPS.IMAGE, "UI\\custom\\Time2.JPG");
            }
        }
        init();
    }

    private void init() {
        if (unit == null) {
            initEmptyLabel();
            return;
        }

        initialized = true;
        initLabel();
        if (clock) {
            initClock();
        } else {
            initOverlays();
        }

    }

    private void initEmptyLabel() {
        lbl = new JLabel(ImageManager.getIcon(getEmptyIcon()));
        add(lbl, "pos 0 0 ");
    }

    private void initLabel() {
        this.img = ImageManager.getSizedVersion(unit.getIcon().getImage(), new Dimension(GuiManager
                .getSmallObjSize(), GuiManager.getSmallObjSize()));
        lbl = new JLabel(new ImageIcon(img));
        add(lbl, "pos 0 0 ");
    }

    @Override
    public void refresh() {
        if (unit == null) {
            lbl.setIcon(ImageManager.getIcon(getEmptyIcon()));
            return;
        }
        if (!initialized) {
            init();
        }
        if (clock) {
            initClock();
        } else {
            resetSmartText();
        }
        lbl.setIcon(new ImageIcon(img));
        if (unit.isInfoSelected()) {
            lbl.setIcon(new ImageIcon(ImageManager.applyBorder(img, BORDER.HIGHLIGHTED_96)));
        } else if (unit.isTargetHighlighted()) {

            lbl.setIcon(new ImageIcon(ImageManager.applyBorder(img,
                    unit.isMine() ? BORDER.HIGHLIGHTED_BLUE : BORDER.HIGHLIGHTED_RED)));

        } else if (unit.isActiveSelected()) {
            lbl.setIcon(new ImageIcon(ImageManager.applyBorder(img,
                    unit.isMine() ? BORDER.HIGHLIGHTED_GREEN
                            : BORDER.NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT_SQUARE_64)));

        }

    }

    private void initClock() {
        String value = "" + unit.getGame().getRules().getTimeRule().getTimeRemaining();
        int perc = MathMaster.getFractionValueCentimal(unit.getGame().getRules()
                .getTimeRule().getBaseTime(), unit.getGame().getRules().getTimeRule()
                .getTimeRemaining());
        Color color = ColorManager.GOLDEN_WHITE;
        // SmartTextManager.getValueCase(perc).getColor();

        SmartText text = new SmartText(value, color);
        text.setFont(getTimeFont());

        int x = MigMaster.getCenteredTextPosition(value, getTimeFont(), GuiManager
                .getSmallObjSize());
        int y = MigMaster.getCenteredTextPositionY(getTimeFont(), GuiManager.getSmallObjSize()) + 5;
        p1 = new Point(x, y);
        addSpecialOverlayingString(p1, text);

    }

    private Font getTimeFont() {
        return FontMaster.getFont(FONT.AVQ, 18, Font.BOLD);
    }

    private void initOverlays() {
        // ++ emblem?
        int x = X;
        int y = Y;
        p1 = new Point(X, Y);
        resetSmartText();

        addSpecialOverlayingString(p1, initText);

        x = GuiManager.getSmallObjSize() - X * 2;
        y = GuiManager.getSmallObjSize() - Y;

        p2 = new Point(x, y);
        addSpecialOverlayingString(p2, apText);

    }

    private void resetSmartText() {
        String value = null;
        Color color = null;

        if (VisionManager.checkDetectedEnemy(unit)) {
            value = unit.getParam(PARAMS.C_N_OF_ACTIONS);
            color = SmartTextManager.getParamCase(PARAMS.C_N_OF_ACTIONS, unit).getColor();
        } else {
            value = "?";
            color = ColorManager.getAspectColor(unit.getType());
        }
        apText = new SmartText(value, color);

        if (VisionManager.checkDetectedEnemy(unit)) {
            value = unit.getParam(PARAMS.C_INITIATIVE);
            color = SmartTextManager.getParamCase(PARAMS.C_INITIATIVE, unit).getColor();
        } else {
            value = "?";
            color = ColorManager.getAspectColor(unit.getType());
        }
        initText = new SmartText(value, color);
    }

    public void addSpecialOverlayingImage(Point c, Image img) {
        specialOverlayingImages.put(c, img);
    }

    public void addSpecialOverlayingString(Point c, SmartText text) {
        specialOverlayingStrings.put(c, text);
    }

    @Override
    public void paint(Graphics g) {
        refresh();
        super.paint(g);

        for (Point c : specialOverlayingImages.keySet()) {
            Image img = specialOverlayingImages.get(c);
            g.drawImage(img, c.x, c.y, null);
        }
        for (Point c : specialOverlayingStrings.keySet()) {
            SmartText text = specialOverlayingStrings.get(c);
            g.setColor(text.getColor());
            g.setFont(text.getFont());
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawString(text.getText(), c.x, c.y);

        }
    }

    public String getEmptyIcon() {
        return emptyIcon;
    }

    public void setEmptyIcon(String emptyIcon) {
        this.emptyIcon = emptyIcon;
    }
}
