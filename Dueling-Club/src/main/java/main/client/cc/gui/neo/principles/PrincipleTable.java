package main.client.cc.gui.neo.principles;

import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.content.PARAMS;
import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.rules.rpg.IntegrityRule;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.util.List;

public class PrincipleTable extends G_Panel {

    private List<Entity> items;
    private boolean principleData;
    private DC_HeroObj hero;
    private Font defaultFont;
    private PRINCIPLES selectedPrinciple;
    private boolean largeItems;

    public PrincipleTable(DC_HeroObj hero, boolean principleData) {
        this.principleData = principleData;
        this.hero = hero;
        panelSize = new Dimension(215, 729);
    }

    public void init() {

    }

    public void setData(List<Entity> list) {
        this.items = list;
        refresh();
    }

    @Override
    public void refresh() {
        repaint();
    }

    private void drawValueIcons(Graphics g) {
        int x = getOffsetX();
        int y = 16;

        Image img = VISUALS.CHAR_BUTTON.getImage();
        g.drawImage(img, x, y, null);
        x += img.getWidth(null) + getColumnGap(0) / 2;
        img = STD_IMAGES.ALIGNMENT.getImage();
        g.drawImage(img, x, y, null);
        x += img.getWidth(null) + getColumnGap(0) / 2;
        img = ImageManager.getValueIcon(PARAMS.INTEGRITY);
        g.drawImage(img, x, y, null);
    }

    @Override
    public void paint(Graphics g) {
        drawValueIcons(g);
        int i = 0;
        if (principleData) {
            for (PRINCIPLES p : PRINCIPLES.values()) {
                Integer[] values = IntegrityRule.getValues(p, null, hero);

                drawRow(i, values, g);
                i++;
                // drawRow(identity, alignment, integrity, true);
            }
        } else {
            if (items != null) {
                for (Entity item : items) {
                    if (item == null) {
                        continue;
                    }
                    if (selectedPrinciple == null) {
                        int n = 0;
                        List<PRINCIPLES> principles = IntegrityRule.getAffectingPrinciples(item,
                                hero);
                        for (PRINCIPLES principle : principles) {
                            Integer[] values = IntegrityRule.getValues(principle, item, hero);
                            drawRow(principle, i, values, g, principles.size(), n);
                            i++;
                            n++;
                        }
                        continue;
                    }
                    Integer[] values = IntegrityRule.getValues(selectedPrinciple, item, hero);
                    drawRow(i, values, g);
                    i++;
                }
            }
        }
    }

    private void drawRow(int i, Integer[] values, Graphics g) {

        drawRow(null, i, values, g, 0, 0);
    }

    private void drawRow(PRINCIPLES principle, int i, Integer[] values, Graphics g, int rows,
                         int rowNumber) {

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(PrincipleView.getDefaultFont());
        g.setColor(ColorManager.getHC_DefaultColor());
        int column = -1;
        int x = getOffsetX();
        int y = getOffsetY() + getY(i + 1);

        if (principle != null) {
            // TODO draw second table on X !
            Image img = ImageManager.getPrincipleImage(principle);
            if (rows > 1) {
                int percentage = 100 / rows + 15 + Math.min(20, rows * 3);
                img = ImageManager.getSizedVersion(img, percentage);
                g.setFont(PrincipleView.getDefaultFont().deriveFont(
                        new Float(PrincipleView.getDefaultFont().getSize() / rows + 6 + rows)));
            }
            y += getRowHeight() / rows * rowNumber;
            x -= img.getWidth(null) / 2;
            g.drawImage(img, x, y, null);
            x += img.getWidth(null);
            x = drawValues(x, y, g, column, values, true);
        } else {
            x = drawValues(x, y, g, column, values, false);
        }
    }

    private int drawValues(int x, int y, Graphics g, int column, Integer[] values,
                           boolean multiplePrinciples) {
        for (Integer v : values) {
            column++;
            if (v == 0) {
                x = x + getColumnGap(column);
                continue;
            }
            String str = v + "";
            if (v > 0) {
                str = "+" + v;
            }
            g.drawString(str, x, y);
            x = x + getColumnGap(column);
        }
        return x;
    }

    private int getOffsetY() {
        return getRowHeight() * 3 / 4 - 18;
    }

    private int getOffsetX() {
        return 8;
    }

    private int getColumnGap(int column) {
        return 38 + column * 16;
    }

    private int getY(int i) {
        return getRowHeight() * i;
    }

    private int getRowHeight() {
        if (principleData) {
            return VISUALS.PRINCIPLE_VALUE_BOX.getHeight();
        }
        if (largeItems) {
            return GuiManager.getFullObjSize();
        }
        return GuiManager.getSmallObjSize();
    }

    public PRINCIPLES getSelectedPrinciple() {
        return selectedPrinciple;
    }

    public void setSelectedPrinciple(PRINCIPLES selectedPrinciple) {
        this.selectedPrinciple = selectedPrinciple;
    }

}
