package main.swing.components.obj.drawing;

import main.game.battlefield.XLine;

import java.awt.*;

public class Painter {
    public static void paintImagesInLine(Graphics g, XLine line, Image image, int interval) {
        float tangentX = new Float(line.p1.x) / line.p2.x;
        float tangentY = new Float(line.p1.y) / line.p2.y;
        int i = 0;
        int diff_x = line.p1.x - line.p2.x;
        int diff_y = line.p1.y - line.p2.y;
        int mod_y = diff_y >= 0 ? 1 : -1;
        int mod_x = diff_x >= 0 ? 1 : -1;
        while (true) {

            int offsetX = mod_x * Math.round(i * interval * tangentX);
            int x = line.p1.x + offsetX;
            int offsetY = mod_y * Math.round(i * interval * tangentY);
            int y = line.p1.y + offsetY;
            if (offsetX > Math.abs(diff_x) && offsetY > Math.abs(diff_y)) {
                return;
            }
            g.drawImage(image, x, y, null);
            i++;
        }
    }

}
