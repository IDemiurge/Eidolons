package main.swing.generic.misc;

import javax.swing.*;
import java.awt.*;

public class LogComponent extends JTextArea {
    private Image bg;

    @Override
    protected void paintComponent(Graphics g) {

        g.drawImage(bg, 0, 0, null);
        // g.setColor(Color.BLACK);
        // g.drawString(getText(), 55, 55);
        super.paintComponent(g);
    }

    /**
     * @return the bg
     */
    public Image getBg() {
        return bg;
    }

    /**
     * @param bg the bg to set
     */
    public void setBg(Image bg) {
        this.bg = bg;
    }
}
