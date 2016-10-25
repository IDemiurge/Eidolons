package main.swing.generic.misc;

import main.swing.generic.components.G_Panel;
import main.system.auxiliary.ColorManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class LogPane extends G_Panel {
    LogComponent textArea;
    JLabel bg;
    private String name;
    private JScrollPane scrollPane;

    public LogPane(String name) {
        super();
        this.name = name;
        textArea = new LogComponent();
        scrollPane = new JScrollPane(textArea);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        // gotta override paintComponent
        setBackground();
        // add(bg, "pos 0 0");
        // setComponentZOrder(bg, 1);
        // setComponentZOrder(scrollPane, 0);
        textArea.setForeground(Color.white);
        textArea.setBg(ImageManager.getIcon("UI//custom//GRID_BG_WIDE.png")
                .getImage());
        // scrollPane.setOpaque(false);
        ((JComponent) scrollPane.getParent()).setOpaque(false);
        scrollPane.setBackground(ColorManager.TRANSPARENT);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(ColorManager.TRANSPARENT);

    }

    /**
     * @return the scrollPane
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * @param scrollPane the scrollPane to set
     */
    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void log(String text) {
        textArea.setText(textArea.getText() + "\n" + text);

    }

    private void setBackground() {
        bg = new JLabel(
                ImageManager.getIcon("UI//custom//GRID_BG_WIDE - Copy.png"));
        textArea.setOpaque(false);
        textArea.setBackground(ColorManager.TRANSPARENT);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
