package main.swing.generic.components;

import main.swing.SwingMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.beans.Transient;

public class G_Panel extends G_Component implements VisualComponent {
    protected static Dimension tsize;
    protected static int ID = 0;
    protected Dimension panelSize;
    protected ComponentVisuals visuals;
    protected JLabel background;
    protected boolean initialized = false;
    protected Image visualsImage;
    protected boolean backgroundVisuals = true;
    protected int id;
    private int borderWidth;

    public G_Panel() {
        this("");
    }

    public G_Panel(String constraints) {
        setMigLayout("insets 0 0 0 0," +

         (GuiManager.isGuiDebug() ? "debug," : "")

         + constraints);// "debug,"
        // +
        setBorder(null);
        setOpaque(false);
        setFocusable(true);
        setIgnoreRepaint(GuiManager.isFullScreen());
        id = ID;
        ID++;
    }

    public G_Panel(ComponentVisuals v) {
        this();
        setVisuals(v);
    }

    public G_Panel(Component... comps) {
        this();
        for (Component c : comps) {
            add(c);
        }
    }

    public G_Panel(boolean special) {
        this();
    }

    @Override
    public String toString() {
        String string = getClass().getSimpleName();
        string += StringMaster.wrapInBraces("" + id);

        string += "; Width=" + getWidth();
        string += ", Height=" + getHeight();
        string += "; x=" + getX();
        string += ", y=" + getY();
        // TODO original contraints? x/y?
        if (getParent() != null) {
            string += "; parent=" + getParent().getClass().getSimpleName();
        }
        if (getComponentCount() != 0) {
            string += "; comp count=" + getComponentCount();
        }

        return string;
    }

    @Override
    public void add(Component comp, Object constraints) {
        if (constraints instanceof String) {
            if (getKeyManager() == null) {
                setKeyManager(GuiManager.getKeyListener());
            }
            if (getKeyManager() != null) {
                if (comp instanceof G_Panel) {
                    ((G_Panel) comp).setKeyManager(getKeyManager()); // doesn't
                } else {
                    comp.addKeyListener(getKeyManager());
                    if (comp instanceof Container) {
                        Container container = (Container) comp;
                        for (Component c : container.getComponents()) {
                            c.addKeyListener(getKeyManager());
                        }

                    }
                }
            }
            // work...
            if (((String) constraints).contains(MigMaster.PROCESS_CHAR)) {
                constraints = MigMaster.processConstraints(this, comp, (String) constraints);
            }
        }
        super.add(comp, constraints);
        if (SwingMaster.DEBUG_ON) {
            LogMaster.log(isSizeLogged() ? 1 : 0, comp.getWidth()
             + " width, " + comp.getHeight() + " height comp at " + comp.getX() + "-"
             + comp.getY());
        }
        if (isAutoZOrder()) {
            setComponentZOrder(comp, getComponentCount() - 1);
        }
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {
        if (panelSize != null) {
            return panelSize;
        }
        LogMaster.log(isSizeLogged() ? 1 : 0, "getMaximumSize no panel size!"
         + toString());
        if (!isAutoSizingOn() || !isValid() || super.getMaximumSize().width <= 0
         || super.getMaximumSize().height <= 0) {
            return super.getMaximumSize();
        }
        panelSize = super.getMaximumSize();
        return panelSize;
    }

    private boolean isSizeLogged() {
        return false;
    }

    public boolean isAutoSizingOn() {
        return false;
    }

    @Override
    @Transient
    public Dimension getMinimumSize() {
        // return getPreferredSize();
        if (panelSize != null) {
            return panelSize;
        }
        LogMaster.log(isSizeLogged() ? 1 : 0, "getMinimumSize no panel size!"
         + toString());
        if (!isAutoSizingOn() || !isValid() || super.getMinimumSize().width <= 0
         || super.getMinimumSize().height <= 0) {
            try {
                return super.getMinimumSize();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        panelSize = super.getMinimumSize();
        return panelSize;
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        if (panelSize != null) {
            return panelSize;
        }
        LogMaster.log(isSizeLogged() ? 1 : 0,
         "getPreferredSize no panel size!" + toString());
        if (!isAutoSizingOn() || !isValid() || super.getPreferredSize().width <= 0
         || super.getPreferredSize().height <= 0) {
            try {
                return super.getPreferredSize();
            } catch (Exception e) {
                try { // pagePanel controls
                    panelSize = new Dimension(getPanelWidth(), getPanelHeight());
                    return panelSize;
                } catch (Exception e2) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }

            }
        }

        panelSize = super.getPreferredSize();
        return panelSize;
    }

    @Override
    public Dimension getSize() {
        if (panelSize != null) {
            return panelSize;
        }
        LogMaster.log(isSizeLogged() ? 1 : 0, "getSize no panel size!"
         + toString());
        if (!isAutoSizingOn() || !isValid() || super.getSize().width <= 0
         || super.getSize().height <= 0) {
            return super.getSize();
        }
        panelSize = super.getSize();
        return panelSize;
    }

    public void setMigLayout(String constraints) {
        MigLayout mgr = new MigLayout(constraints);
        setLayout(mgr);
    }

    @Override
    public void refresh() {
        repaint();
    }

    protected void drawRect(Graphics g, int offset) {
        g.drawRect(offset, offset, getPanelWidth() - 1 - offset, getPanelHeight() - 1 - offset);
    }

    @Override
    public void paint(Graphics g) {
        if (background != null) {
            if (background.getParent() == this) {
                setComponentZOrder(background, getComponentCount() - 1);
            }
        }

        if (isBackgroundVisuals()) {
            paintVisuals(g);
        }
        try {
            super.paint(g);
        } catch (java.lang.ClassCastException e) {

        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);

        }
        if (!isBackgroundVisuals()) {
            paintVisuals(g);
        }

        int i = getBorderWidth();
        while (i > 0) {
            g.setColor(getBorderColor());
            try {
                drawRect(g, i);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                break;
            }
            i--;
        }

        // if (v != null) {
        // Image img = ImageManager.getSizedVersion(v.getImg(), tsize);
        // g.drawImage(v.getImg(), 0, 0, null);
        // }
    }

    public Color getBorderColor() {
        return Color.black;
    }

    public int getBorderWidth() {
        if (borderWidth != 0) {
            return borderWidth;
        }
        return 0;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    protected void paintVisuals(Graphics g) {
        if (getGenericVisuals() != null) {
            if (panelSize == null) {
                panelSize = visuals.getSize();
            }

            // if (visualsImage == null) {
            if (getGenericVisuals().getImage() != null) {
                visualsImage = ImageManager.getSizedVersion(getGenericVisuals().getImage(),
                 panelSize);
            } else {
                visualsImage = ImageManager.getSizedIcon(getGenericVisuals().getImgPath(),
                 panelSize).getImage();
            }

            // }
            if (visualsImage.getWidth(null) < 1) {
                g.drawImage(getGenericVisuals().getImage(), 0, 0, panelSize.width,
                 panelSize.height, null);
            } else {
                g.drawImage(visualsImage, 0, 0, null);
            }
        }
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    public ComponentVisuals getGenericVisuals() {
        return visuals;
    }

    @Override
    public VISUALS getVisuals() {
        if (visuals instanceof VISUALS) {
            return (VISUALS) visuals;
        }
        return null;
    }

    @Override
    public void setVisuals(ComponentVisuals visuals) {
        this.visuals = visuals;
        if (visuals == null) {
            return;
        }
        panelSize = visuals.getSize();

        if (isBackGroundLabelRequired()) {
            background = visuals.getLabel();
            removeAll();
            add(background, "pos 0 0 container.x2 container.y2");
            repaint();
        }
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        if (isBackgroundMouseListener()) {
            if (background != null) {
                background.addMouseListener(l);
            }
        }
        super.addMouseListener(l);
    }

    protected boolean isBackgroundMouseListener() {
        return false;
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        if (isBackgroundMouseListener()) {
            if (background != null) {
                background.removeMouseListener(l);
            }
        }
        super.removeMouseListener(l);
    }

    public int getDimension(boolean vertical) {
        return vertical ? getPanelHeight() : getPanelWidth();
    }

    protected boolean isBackGroundLabelRequired() {
        return true;
    }

    public boolean isBackgroundVisuals() {
        return backgroundVisuals;
    }

    public void setBackgroundVisuals(boolean backgroundVisuals) {
        this.backgroundVisuals = backgroundVisuals;
    }

    public int getPanelWidth() {
        if (getPanelSize() == null) {
            initSize();
        }
        return getPanelSize().width;
    }

    public int getPanelHeight() {
        if (getPanelSize() == null) {
            initSize();
        }
        return getPanelSize().height;
    }

    public Dimension getPanelSize() {
        if (panelSize == null) {
            initSize();
        }
        return panelSize;
    }

    public void setPanelSize(Dimension size) {
        this.panelSize = size;
        // setPreferredSize(size);
    }

    public void initSize() {
        // TODO may not be necessary
        // for (Component c : getComponents()) {
        // // maxSize = c.get
        // if (c instanceof G_Panel) {
        // G_Panel panel = (G_Panel) c;
        // // if (SwingMaster)
        // if (panel.getPanelSize() != null) {
        // panelSize = panel.getPanelSize();
        // return;
        // }
        // // maxSize = panel.getPanelSize();
        // }
        // panelSize = new Dimension(100, 100);
        // }
    }

    public enum VISUALS implements ComponentVisuals {
        MAIN("UI\\custom\\" + "Frame.png"),
        CELL("UI\\" + "CELL for 96.png"),
        CIRCLE("UI\\Borders\\" + "CIRCLE.png"),
        BF_GRID("UI\\custom\\" + "GRID_BG_WIDE.png"),
        VALUE_BAR("UI\\components\\new\\bars\\" + "bar comp.png"),
        VALUE_BAR_UPSIDEDOWN("UI\\components\\new\\bars\\" + "bar comp upside.png"),
        BAR_COMP("UI\\components\\" + "SPEC_VALUE_COMP.png"),
        MENU_ITEM("UI\\components\\" + "INFO_COMP_WIDE.png"),
        TOOLTIP_PANEL_HIGHLIGHTED("UI\\components\\neo\\" + "tooltip frame x.png"),
        TOOLTIP_PANEL("UI\\components\\neo\\" + "tooltip frame x highlight.png"),
        VALUE_BOX("UI\\components\\" + "VALUE_BOX.png"),
        VALUE_BOX_BIG("UI\\components\\" + "VALUE_BOX_BIG.png"),
        VALUE_BOX_SMALL("UI\\components\\" + "VALUE_BOX3.png"),
        VALUE_BOX_TINY("UI\\components\\" + "box.jpg"),
        INFO_PANEL_WIDE("UI\\components\\info panel wide.png"),
        INFO_PANEL_LARGE("UI\\components\\info panel large.png"),
        INFO_PANEL_HC("UI\\components\\info panel hc.png"),
        INFO_PANEL_TEXT_SMALL("UI\\components\\info panel text small.png"),
        INFO_PANEL_DESCRIPTION("UI\\components\\info panel description.png"),

        INFO_PANEL("UI\\components\\info panel.png"),
        INFO_PANEL_TEXT("UI\\components\\info panel text.png"),
        HEADER("UI\\components\\INFO_COMP.png"),
        PROP_BOX("UI\\components\\INFO_COMP_NEW.png"),
        TOP("UI\\components\\top.png"),
        TOP_HC("UI\\components\\top hc.png"),
        MENU_BUTTON("UI\\components\\menu.png"),
        QUESTION("UI\\components\\small\\question.png"),
        GEARS("UI\\components\\small\\gears.png"),

        FRAME("UI\\components\\Frame.png"),
        FULL_CHARACTER_FRAME("UI\\components\\Full Character Frame.png"),
        FRAME_BIG_FILLED("UI\\components\\Frame big filled.png"),
        FRAME_FILLED("UI\\components\\Frame filled.png"),
        TAB("UI\\components\\small\\tab.png"),
        TAB_SELECTED("UI\\components\\small\\tab_blocked.png"),
        TAB_SMALL("UI\\components\\small\\tab small.png"),
        TAB_SMALL_SELECTED("UI\\components\\small\\tab small blocked.png"),

        BUTTON("UI\\components\\button.png"),
        DEITY("UI\\components\\small\\deity comp.png"),

        POOL("UI\\components\\small\\pool.png"),
        POOL_C("UI\\components\\small\\pool c.png"),
        POOL_MECH("UI\\components\\neo\\border mech.png"),

        SPACE("UI\\components\\small\\space.png"),
        SPACE_SMALL("UI\\components\\small\\space small.png"),
        OPTION_COMP("UI\\components\\small\\space.png"),

        ADD_REMOVE("UI\\components\\small\\add_remove.png"),
        ADD("UI\\components\\small\\add.png"),
        REMOVE("UI\\components\\small\\remove.png"),
        ADD_BLOCKED("UI\\components\\small\\add_blocked.png"), // BLOCKED?
        REMOVE_BLOCKED("UI\\components\\small\\remove_blocked.png"),

        PORTRAIT_BORDER("UI\\components\\" + "Border New.png"),
        H_LIST_1_5("UI\\components\\lists\\H_LIST_1_5.png"),
        H_LIST_1_5_NARROW("UI\\components\\lists\\H_LIST_1_5_NARROW.png"),
        H_LIST_1_6("UI\\components\\lists\\H_LIST_1_6.png"),
        H_LIST_1_6_NARROW("UI\\components\\lists\\H_LIST_1_6_NARROW.png"),
        H_LIST_2_5_NARROW("UI\\components\\lists\\H_LIST_2_5 narrow.png"),
        H_LIST_2_8("UI\\components\\lists\\H_LIST_2_8.png"),
        H_LIST_2_6("UI\\components\\lists\\H_LIST_2_6.png"),
        V_LIST_2_4("UI\\components\\lists\\V_LIST_2_4.png"),

        V_CHOICE_LIST_8_12("UI\\components\\lists\\choice v 2_3.png"),

        OK("UI\\components\\small\\ok.png"),
        CANCEL("UI\\components\\small\\no.png"),
        GOLD("UI\\components\\small\\crowns2.png"),
        XP("UI\\components\\small\\xp.png"),
        BLOCKED("UI\\components\\small\\blocked.png"),
        LOCK("UI\\components\\small\\locked.png"),
        UNLOCK("UI\\components\\small\\unlocked.png"),
        ROOT_FRAME("UI\\components\\small\\locked.png"),

        BACK("UI\\components\\small\\back.png"),
        FORWARD("UI\\components\\small\\forward.png"),
        FORWARD_BLOCKED("UI\\components\\small\\forward_blocked.png"),
        DICE("ui\\components\\small\\dice.png"),
        DIVINATION("ui\\components\\small\\divine.png"),
        ENUM_CHOICE_COMP_SELECTED("UI\\components\\hc\\principle comp selected.png"),
        ENUM_CHOICE_COMP("UI\\components\\hc\\principle comp.png"),

        END_PANEL("UI\\components\\end panel.png"),
        INV_PANEL("UI\\components\\inv panel.png"),
        INFO_COMP_HEADER("UI\\components\\INFO_COMP_HEADER.png"),
        PARTY_HEADER("UI\\components\\party header.png"),
        HAMMER("UI\\components\\new\\hammer.jpg"),
        MAP_VIEW_BUTTON("UI\\components\\new\\map view.png"),
        MINIMAP_GRID_BUTTON("UI\\components\\new\\map grid.png"),
        CHAR_BUTTON("UI\\components\\new\\char.png"),
        LOG_BUTTON("UI\\components\\new\\log.png"),
        OPTIONS_BUTTON("UI\\components\\new\\gears.png"),
        CONTROL_PANEL_HORIZONTAL("UI\\components\\new\\control panel.png"),
        VALUE_ORB("UI\\components\\new\\orb.png"),
        VALUE_ORB_FILLER("UI\\components\\new\\orb filler 68.png"),
        VALUE_ORB_64("UI\\components\\new\\orb 64.png"),
        VALUE_ORB_FILLER_64("UI\\components\\new\\orb filler 64.png"),
        PLAN_PANEL_FRAME("UI\\components\\level editor\\plan panel frame.jpg"),
        DRAGON_DIVIDER_SMALL("UI\\components\\new\\dragon small2.png"),
        DRAGON_DIVIDER("UI\\components\\new\\dragon.png"),
        BUTTON_NEW("UI\\components\\new\\button new.png"),
        BUTTON_NEW_SMALL("UI\\components\\new\\button new small.png"),
        BUTTON_NEW_THIN("UI\\components\\new\\button new thin.png"),
        BUTTON_NEW_TINY("UI\\components\\new\\button new tiny.png"),
        TREE_VIEW("UI\\components\\skill tree.png"),
        CHOICE_VIEW_BG("UI\\components\\lists\\choice v 2_3.png"),

        GRAVEYARD("UI\\components\\new\\GRAVEYARD.png"),
        DROPPED_ITEMS("UI\\components\\new\\ITEMS.png"),
        SIGHT_CELL_IMAGE("UI\\bf\\eye cell image.png"),
        BF_NAME_COMP("UI\\bf\\name comp.png"),

        SPELLBOOK("UI\\bf\\spellbook.png"),
        INV("UI\\bf\\inv.jpg"),
        HAND("UI\\bf\\hand.jpg"),
        PRINCIPLE_VALUE_BOX("UI\\components\\hc\\principle value box.png"),
        PRINCIPLE_VALUE_BOX_SELECTED("UI\\components\\hc\\principle value box glow y.png"),
        PRINCIPLE_PANEL_FRAME("UI\\components\\hc\\principle panel frame.png"),;

        public Image img;
        protected String s;
        protected Dimension size;

        VISUALS(String s) {
            this.s = s;
        }

        public int getWidth() {
            return getImage().getWidth(null);
        }

        public int getHeight() {
            return getImage().getHeight(null);
        }

        public String getImgPath() {
            return s;
        }

        public Image getImage() {
            if (img == null) {
                img = ImageManager.getImage(s);
                if (img == null) {
                    img = ImageManager.getNewBufferedImage(0, 0);
                }
            }
            return img;
        }

        public JLabel getLabel() {
            if (img == null) {
                img = ImageManager.getIcon(s).getImage();
            }
            return new JLabel(new ImageIcon(img));
        }

        public Dimension getSize() {
            if (size == null) {
                size = new Dimension(getWidth(), getHeight());
            }
            return size;
        }
    }

	/*
     * Need a max size frame for all major proportions, then I can size it down
	 * and paint Windowed mode is a liablity...
	 */
}
