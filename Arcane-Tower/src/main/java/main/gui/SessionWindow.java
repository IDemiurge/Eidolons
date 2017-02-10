package main.gui;

import main.ArcaneTower;
import main.enums.StatEnums.STATE;
import main.enums.StatEnums.WORK_DIRECTION;
import main.enums.StatEnums.WORK_STYLE;
import main.enums.StatEnums.WORK_TYPE;
import main.gui.sub.*;
import main.logic.AT_PARAMS;
import main.logic.Direction;
import main.session.Session;
import main.session.SessionMaster;
import main.swing.components.TextComp;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

public class SessionWindow implements ActionListener {
    private static int width;
    private static int height;
    JComboBox<WORK_STYLE> styleBox;
    JComboBox<STATE> stateBox;
    JComboBox<Direction> directionBox;
    JComboBox<VIEW_OPTION> viewBox;
    CustomButton lockButton;
    // JComboBox<Goal> goalBox;
    SessionTimer timer;
    private Class[] comboBoxes = {WORK_TYPE.class, WORK_DIRECTION.class, WORK_STYLE.class,};
    // CheckBoxes: timer, alt-mode,
    private int wrapComboBoxes = 4;
    private int wrapButtons = 8;
    private JFrame window;
    private G_Panel panel;

    // pinnedTasks; //combobox?
    private Dimension SIZE;
    private Session session;
    private DirectionHeader directionHeader;
    private SessionControlPanel sessionControlPanel;
    private InfoEditPanel infoPanel;
    private GoalPages goalsPanel;
    private G_Panel boxPanel;

    public SessionWindow(Session session) {
        width = GuiManager.getScreenWidthInt() * 2 / 3 - 255;
        height = GuiManager.getScreenHeightInt() * 3 / 4 + 72;
        SIZE = new Dimension(width, height);
        this.session = session;
        init();
        window = GuiManager.inNewWindow(false, panel, session.getName(), SIZE);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public void init() {
        panel = new G_Panel("flowy");

        sessionControlPanel = new SessionControlPanel(session);
        String pos = "pos 0 0, id sessionControlPanel";
        panel.add(sessionControlPanel, pos);

        timer = new SessionTimer(session, session.getIntParam(AT_PARAMS.SESSION_TIME));
        // pos = "pos @center_x directionHeader.y2, id timer";
        pos = "pos 0 sessionControlPanel.y2, id timer";
        panel.add(timer, pos);
        // timer.x2
        boxPanel = new G_Panel();
        pos = "pos @center_x-125 directionHeader.y2-30, id boxPanel";
        panel.add(boxPanel, pos);
        directionBox = initBox(getDirections(), "Direction");
        styleBox = initBox(WORK_STYLE.values(), "Style");
        stateBox = initBox(STATE.values(), "State");
        viewBox = initBox(VIEW_OPTION.values(), "Custom View");

        directionHeader = new DirectionHeader(session);
        pos = "pos 0 sessionControlPanel.y2, id directionHeader";
        panel.add(directionHeader, pos);

        goalsPanel = new GoalPages(session);
        pos = "pos 0 timer.y2, id goalsPanel";
        panel.add(goalsPanel, pos);

        infoPanel = new InfoEditPanel();
        pos = "pos goalsPanel.x2 directionHeader.y2+50, id infoPanel";
        panel.add(infoPanel.getPanel(), pos);

        lockButton = new CustomButton(VISUALS.LOCK) {
            @Override
            public VISUALS getVisuals() {
                return session.isLocked() ? VISUALS.LOCK : VISUALS.UNLOCK;
            }

            @Override
            public void handleClick() {
                session.setLocked(!session.isLocked());
                refresh();
            }
        };

        pos = "pos timer.x+85 timer.y2-50, id lockButton";
        panel.add(lockButton, pos);
        panel.refreshComponents();
    }

    private JComboBox initBox(Object[] items, String tooltip) {
        JComboBox<?> box = new JComboBox<>(items);
        box.setSelectedIndex(0);
        box.addActionListener(this);
        G_Panel wrapper = new G_Panel("flowy");
        wrapper.add(new TextComp(tooltip, Color.black));
        wrapper.add(box, "pos 0 20");
        boxPanel.add(wrapper);
        return box;
    }

    public void started() {
        timer.start();
    }

    public void refresh() {
        // panel.refreshComponents();
        // goalsPanel.getCurrentComponent().refresh();
        // panel.repaint();
        directionHeader.refresh();
        Object item = directionBox.getSelectedItem();
        directionBox.removeActionListener(this);
        Direction[] array = getDirections();
        directionBox.setModel(new DefaultComboBoxModel<>(array));
        directionBox.setSelectedItem(item);
        directionBox.addActionListener(this);

        viewBox.removeActionListener(this);
        viewBox.setSelectedItem(goalsPanel.getViewOption());
        viewBox.addActionListener(this);
        getGoalsPanel().refresh();
        getGoalsPanel().getSelectedPanel().refresh();

        SessionMaster.setActiveWindow(this);
    }

    private Direction[] getDirections() {
        List<Direction> directions = new LinkedList<>(ArcaneTower.getDirections());
        directions.remove(session.getDirection());
        directions.add(0, session.getDirection());
        Direction[] array = directions.toArray(new Direction[directions.size()]);
        return array;
    }

    public InfoEditPanel getInfoPanel() {
        return infoPanel;
    }

    private G_Panel initBoxPanel() {
        G_Panel boxPanel = new G_Panel("flowy");
        int i = 0;
        for (Class c : comboBoxes) {
            List<String> list = ListMaster.toStringList(true, null, c.getEnumConstants());
            JComboBox<String> box = new JComboBox<>(list.toArray(new String[list.size()]));
            // int x = 0, y = 0;
            // String pos = "pos " + x + " " + y;
            String pos = "";
            i++;
            if (i >= wrapComboBoxes) {
                i = 0;
                pos = "wrap";
            }
            boxPanel.add(box, pos);
        }
        return boxPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == styleBox) {
                WORK_STYLE style = (WORK_STYLE) styleBox.getSelectedItem();
                session.setStyle(style);
                return;
            }
            if (e.getSource() == stateBox) {
                STATE state = (STATE) stateBox.getSelectedItem();
                session.setState(state);
                return;
            }
            if (e.getSource() == viewBox) {
                goalsPanel.setViewOption((VIEW_OPTION) viewBox.getSelectedItem());

                return;
            }
            if (e.getSource() == directionBox) {
                // viewBox.setSelectedItem(VIEW_OPTION.OFF);
                goalsPanel.setViewOption(VIEW_OPTION.OFF);
                Direction direction = (Direction) directionBox.getSelectedItem();
                session.setDirection(direction);
                ArcaneTower.setSelectedEntity(direction);
                getInfoPanel().selectType(direction.getType());
                refresh();
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void show() {
        window.setVisible(true);

    }

    public void close() {
        window.setVisible(false);

    }

    public Class[] getComboBoxes() {
        return comboBoxes;
    }

    public JFrame getWindow() {
        return window;
    }

    public G_Panel getPanel() {
        return panel;
    }

    public Dimension getSIZE() {
        return SIZE;
    }

    public Session getSession() {
        return session;
    }

    public DirectionHeader getDirectionHeader() {
        return directionHeader;
    }

    public SessionControlPanel getSessionControlPanel() {
        return sessionControlPanel;
    }

    public GoalPages getGoalsPanel() {
        return goalsPanel;
    }

    public int getWrapComboBoxes() {
        return wrapComboBoxes;
    }

    public int getWrapButtons() {
        return wrapButtons;
    }

    public JComboBox<WORK_STYLE> getStyleBox() {
        return styleBox;
    }

    public JComboBox<STATE> getStateBox() {
        return stateBox;
    }

    public JComboBox<Direction> getDirectionBox() {
        return directionBox;
    }

    public JComboBox<VIEW_OPTION> getViewBox() {
        return viewBox;
    }

    public SessionTimer getTimer() {
        return timer;
    }

    public G_Panel getBoxPanel() {
        return boxPanel;
    }

    public enum VIEW_OPTION {
        ALL_TASKS, OFF, ACTIVE, PENDING, BLOCKED, GROUP_LAST, NEW_GROUP, CHOOSE_GROUP, PINNED,
    }

}
