package main.gui.sub;

import main.enums.StatEnums.SESSION_STATUS;
import main.gui.SessionWindow;
import main.io.AT_EntityMouseListener;
import main.logic.AT_PROPS;
import main.logic.Direction;
import main.session.Session;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.components.G_Panel;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster.FONT;

import java.awt.*;

public class DirectionHeader extends G_Panel {
    Direction direction;
    private Session session;

    public DirectionHeader(Session session) {
        this.session = session;
        panelSize = new Dimension(SessionWindow.getWidth(), 96);
        refresh();
        addText();
        addMouseListener(new AT_EntityMouseListener(direction));
        // addBackground();
        // addArrows();
    }

    @Override
    public void refresh() {
        this.direction = session.getDirection();
        super.refresh();
    }

    private void addText() {
        TextCompDC textComp = new TextCompDC(null, session.getName(), getFontSize(), FONT.AVQ,
                getTextColor());
        textComp.setDefaultSize(panelSize);
        add(textComp, "pos @centered_x centered_y");

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (session.checkProperty(AT_PROPS.SESSION_STATUS, SESSION_STATUS.ACTIVE.toString())) {
            g.setColor(ColorManager.GREEN);
            g.drawRect(0, 0, getPanelWidth(), getPanelHeight());

        }

        // URL url = new URL("<URL to your Animated GIF>");
        // Icon icon = new ImageIcon(url);
        // JLabel label = new JLabel(icon);
    }

    private Color getTextColor() {
        return Color.black;
    }

    private int getFontSize() {
        return 40;
    }
}
