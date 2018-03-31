package main.gui.sub;

import main.ArcaneTower;
import main.content.values.properties.G_PROPS;
import main.enums.StatEnums.TASK_STATUS;
import main.gui.sub.TaskComponent.TASK_COMMAND;
import main.io.AT_EntityMouseListener;
import main.io.PromptMaster;
import main.logic.AT_PARAMS;
import main.logic.Task;
import eidolons.swing.components.buttons.ActionButtonEnum;
import eidolons.swing.components.buttons.CustomButton;
import eidolons.swing.components.panels.page.info.element.IconValueComp;
import eidolons.swing.components.panels.page.info.element.TextCompDC;
import eidolons.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.ImageChooser;
import main.swing.generic.components.misc.GraphicComponent;
import eidolons.swing.generic.services.dialog.DialogMaster;
import main.swing.generic.services.listener.ClickListenerEnum;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;
import main.system.images.ImageManager.STD_IMAGES;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskComponent extends G_Panel implements ClickListenerEnum<TASK_COMMAND>,
        ActionListener {

    G_Panel buttonPanel;
    Map<Rectangle, TASK_COMMAND> mouseMap = new HashMap<>();
    private Task task;
    private TextCompDC headerComp;
    private WrappedTextComp descrPanel;
    private WrappedTextComp tipPanel;
    private GraphicComponent imageComp;
    private IconValueComp gloryComp;
    private JComboBox<TASK_STATUS> comboBox;

    public TaskComponent(final Task task) {
        this.task = task;
        headerComp = new TextCompDC(null, null, getFontSize(), FONT.MAIN, getTextColor()) {
            @Override
            protected String getText() {
                String prefix = "";// task.getTaskType() != null ?
                // task.getTaskType().prefix : "";
                String string = prefix + task.getName();
                return string;
            }
        };
        add(headerComp, "pos " + GoalPanel.offsetX + " 0, id header");
        headerComp.setDefaultSize(new Dimension(getPanelWidth(), 2 * FontMaster
                .getFontHeight(getHeaderFont())));
        headerComp.addMouseListener(new AT_EntityMouseListener(task));

        String pos = "pos 0 img.y2";
        gloryComp = new IconValueComp(14, FONT.NYALA, AT_PARAMS.GLORY, task);
        add(gloryComp, pos);

        pos = "id img, pos 0 center_y@";
        imageComp = new GraphicComponent(task.getImage());
        add(imageComp, pos);

        comboBox = new JComboBox<>(TASK_STATUS.values());
        pos = "id box, @pos max_x max_y@";
        resetStatusComboBox();
        comboBox.addActionListener(this);
        add(comboBox, pos);

        descrPanel = new WrappedTextComp(null, true, 5, Color.black, getDescrFont(), false) {
            @Override
            protected String getText() {
                if (task.getProperty(G_PROPS.DESCRIPTION).isEmpty()) {
                    return task.getName() + " Description";
                }
                return task.getProperty(G_PROPS.DESCRIPTION);
            }

        };
        add(descrPanel, "pos header.x header.y2, id descrPanel");
        descrPanel.setDefaultSize(new Dimension(GoalPanel.getWIDTH() - getButtonPanelWidth(), 100)); // unfortunately
        descrPanel.refresh();
        descrPanel.setDefaultSize(new Dimension(GoalPanel.getWIDTH() - getButtonPanelWidth(),
                getPanelHeight()));

        tipPanel = new WrappedTextComp(null, true, 5, Color.black, getDescrFont(), false) {
            @Override
            protected String getText() {
                return task.getProperty(G_PROPS.DEV_NOTES);
            }

        };
        add(tipPanel, "pos descrPanel.x2 box.y2, id tipPanel");
        tipPanel.setDefaultSize(new Dimension(getButtonPanelWidth(), getPanelHeight() - 40));
        tipPanel.refresh();

        buttonPanel = new G_Panel("flowy");
        buttonPanel.setPanelSize(new Dimension(getButtonPanelWidth(), getPanelHeight()));

        MouseClickListener listener = new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                handleButtonMouseClick(arg0);
            }
        };
        buttonPanel.addMouseListener(listener);
        headerComp.addMouseListener(listener);
        descrPanel.addMouseListener(listener);
        imageComp.addMouseListener(listener);
        gloryComp.addMouseListener(listener);
        add(buttonPanel, "pos descrPanel.x2 header.y2, id buttonPanel");

    }

    protected void handleButtonMouseClick(MouseEvent arg0) {
        boolean alt = arg0.isAltDown();

        if (arg0.getSource() == imageComp) {
            String newPath = new ImageChooser().launch(task.getImagePath(), task.getImagePath());
            task.setImage(newPath);
            refresh();
        } else if (arg0.getSource() == gloryComp) {
            Integer i = DialogMaster.inputInt("Set Glory reward...", task
                    .getIntParam(AT_PARAMS.GLORY));
            task.setParam(AT_PARAMS.GLORY, i);
        } else if (arg0.getSource() == descrPanel) {
            if (!SwingUtilities.isRightMouseButton(arg0)) {
                if (arg0.getClickCount() == 1) {
                    return;
                }
            }
            String descr = DialogMaster.inputText("", task.getProperty(G_PROPS.DESCRIPTION));
            task.setProperty(G_PROPS.DESCRIPTION, descr);
        } else if (arg0.getSource() == headerComp) {
            if (SwingUtilities.isRightMouseButton(arg0))
            // handleClick(getRightClickCommand(), alt);
            {
                PromptMaster.fillOut(task, !alt);
            } else if (arg0.getClickCount() > 1) {
                handleClick(getDoubleClickCommand(), alt);
            }
        } else if (arg0.getSource() == buttonPanel) {
            for (Rectangle r : mouseMap.keySet()) {
                if (r.contains(arg0.getPoint())) {
                    handleClick(mouseMap.get(r), alt);
                    return;
                }
            }
        }
    }

    private TASK_COMMAND getRightClickCommand() {
        return null;
    }

    private TASK_COMMAND getDoubleClickCommand() {
        return TASK_COMMAND.TOGGLE;
    }

    private int getButtonPanelWidth() {
        return 152;
    }

    private Font getDescrFont() {
        return FontMaster.getFont(FONT.NYALA, 14, Font.PLAIN);
    }

    @Override
    public int getPanelWidth() {
        return GoalPanel.getWIDTH();
    }

    @Override
    public int getPanelHeight() {
        int fontHeight = FontMaster.getFontHeight(getHeaderFont());
        int fontHeight2 = FontMaster.getFontHeight(getDescrFont()) - 2;
        List<String> textLines = descrPanel.getTextLines();
        return 40 + fontHeight + fontHeight2 * textLines.size();
    }

    private Font getHeaderFont() {
        return FontMaster.getFont(FONT.MAIN, getFontSize(), Font.PLAIN);
    }

    protected void drawRect(Graphics g, int offset) {
        g.drawRect(offset + GoalPanel.offsetX, offset, getPanelWidth() - 1 - offset,
                getPanelHeight() - 1 - offset);
    }

    @Override
    public void refresh() {
        buttonPanel.removeAll();
        mouseMap.clear();
        if (isSelected()) {
            // addButtons();
            // what buttons will I need if status is changed via box?
        }
        buttonPanel.revalidate();
        descrPanel.refresh();
        tipPanel.refresh();
        headerComp.refresh();
        if (task.getStatusEnum() != comboBox.getSelectedItem()) {
            comboBox.removeActionListener(this);
            resetStatusComboBox();
            comboBox.addActionListener(this);
        }
        gloryComp.refresh();
        imageComp.setImg(task.getImage());
        super.refresh();
        panelSize = new Dimension(getPanelWidth(), getPanelHeight());

    }

    private void resetStatusComboBox() {
        comboBox.setSelectedItem(task.getStatusEnum());
    }

    private void addButtons() {
        int i = 0;
        int wrap = getPanelHeight() / getButtonMaxHeight();
        String pos = "";
        int x = 0;
        int y = 0;
        int width = getButtonMaxWidth();
        int height = getButtonMaxHeight();
        for (TASK_COMMAND cmd : TASK_COMMAND.values()) {
            if (!task.checkCommandShown(cmd)) {
                continue;
            }
            Image image = cmd.getImage();
            if (image == null) {
                image = ImageManager.getEmptyItemIcon(true).getImage();
            }
            CustomButton btn = new ActionButtonEnum<>(null, image, cmd, this);
            i++;
            y += height;
            if (i >= wrap) {
                y = 0;
                x += width;
                pos = "pos " + x + " " + y;
            }
            mouseMap.put(new Rectangle(x, y, image.getWidth(null), image.getHeight(null)), cmd);
            buttonPanel.add(btn, pos);

        }
    }

    private int getButtonMaxWidth() {
        return 40;
    }

    private int getButtonMaxHeight() {
        return 40;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!ArcaneTower.isSwingGraphicsMode()) {
            return;
        }

        if (isBlocked()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.2f));
            g.fillRect(0, 0, getPanelWidth(), getPanelHeight());
        }
    }

    @Override
    public int getBorderWidth() {
        if (isSelected()) {
            return 2;
        }
        return 1;
    }

    @Override
    public Color getBorderColor() {
        if (isSelected()) {
            return (ColorManager.ESSENCE);
        }
        return super.getBorderColor();
    }

    private boolean isBlocked() {
        return task.isBlocked();
    }

    private boolean isSelected() {
        return ArcaneTower.getSelectedEntity() == task;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        task.setStatus((TASK_STATUS) comboBox.getSelectedItem());
        ArcaneTower.getSessionWindow().refresh();

    }

    @Override
    public void handleClick(TASK_COMMAND command, boolean alt) {
        if (!(ArcaneTower.getSelectedEntity() instanceof Task)) {
            return;
        }
        Task task = (Task) ArcaneTower.getSelectedEntity();
        switch (command) {
            case TOGGLE:
                task.toggle();
                break;
            case BLOCK:
                task.block();
                break;
            case DONE:
                task.done();
                break;
            case REMOVE:
                task.remove();
                break;
        }
        ArcaneTower.getSessionWindow().refresh();
    }

    private Color getTextColor() {
        return Color.black;
    }

    private int getFontSize() {
        return 20;
    }

    public enum TASK_COMMAND {
        TOGGLE(STD_IMAGES.ACTIONS.getImage()),
        BLOCK(VISUALS.CANCEL.getImage()),
        DONE(STD_IMAGES.BAG.getImage()),
        REMOVE(STD_IMAGES.DEATH.getImage()),
        // INSPECT(STD_IMAGES.waSEARCH.getEmitterPath()),
        // RESET(STD_IMAGES.FOOT.getEmitterPath())
        ;
        private Image image;

        TASK_COMMAND(Image image) {
            this.image = image;
        }

        public Image getImage() {

            return image;
        }
    }

}
