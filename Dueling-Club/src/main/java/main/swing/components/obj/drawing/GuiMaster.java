package main.swing.components.obj.drawing;

import main.game.core.game.DC_Game;
import main.swing.components.buttons.DynamicButton;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

public class GuiMaster {
    List<DynamicButton> dynamicButtons = new LinkedList<>();
    private Object mouseMap;

    public GuiMaster(DC_Game game) {
    }

    public boolean buttonClicked(MouseEvent e, DYNAMIC_BUTTON type, Object arg) {
        switch (type) {
            case SIGHT_INFO:
                DrawMasterStatic.setSightVisualsOn(!DrawMasterStatic.isSightVisualsOn());
                return true;
        }
        return false;

    }

    public void addButton(Point p, DYNAMIC_BUTTON type, Object arg) {
        Dimension d = getDimension(type);
//		DynamicButton btn = new DynamicButton(new Rectangle(p, d), type, arg);
//		dynamicButtons.add(btn);

    }

    public Rectangle drawButton(Graphics g, DynamicButton d) {
        g.drawImage(getImage(d.getType()), d.getRectangle().x, d.getRectangle().y, null);
        return d.getRectangle();
    }

    private Image getImage(DYNAMIC_BUTTON type) {
        switch (type) {
            case SIGHT_INFO:
                return STD_IMAGES.EYE.getImage();
        }
        return null;
    }

    private Dimension getDimension(DYNAMIC_BUTTON type) {
        return new Dimension(getImage(type).getWidth(null), getImage(type).getHeight(null));
    }

//    public void drawDynamicButtons(Graphics g) {
//        for (DynamicButton d : dynamicButtons) {
//            Rectangle rect = drawButton(g, d);
//            mouseMap.put(rect, d);
//        }
//    }

    public List<DynamicButton> getDynamicButtons() {
        return dynamicButtons;
    }

    public enum DYNAMIC_BUTTON {
        SIGHT_INFO, HELP, QUICK_ATTACK, QUICK_MOVE,
    }

}
