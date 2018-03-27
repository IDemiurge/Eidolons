package main.swing.generic.services.dialog;

import main.data.XLinkedMap;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
import main.swing.builders.DC_Builder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.audio.DC_SoundMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.GuiManager;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster.STD_SOUNDS;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

public class DialogPanel extends G_Panel {
    protected static final Image IMG = VISUALS.OK.getImage();
    protected boolean visible;
    protected Map<Component, Point> compMap = new XLinkedMap<>();
    DC_Obj target;
    Rectangle ok;
    Rectangle cancel;
    private DC_Game game;

    public DialogPanel() {
        this(null);
    }

    public DialogPanel(Unit target) {
        setLayout(new MigLayout(getLayoutArgs()));
        this.target = target;
        if (target != null) {
            game = target.getGame();
        }
        initSize();
        initLocation();
    }

    protected String getLayoutArgs() {
        return "";
    }

    protected void initLocation() {
        Point p;
        if (isCentered()) {
            p = GuiManager.getCenterPoint(getSize());
        } else {
            // target
            p = getTargetLocation();

            p.setLocation(p.x - (getPanelWidth() - GuiManager.getCellWidth()) / 2, p.y
                    + GuiManager.getCellHeight());

        }
        if (isBfDialog()) {
            p.setLocation(p.x + DC_Builder.getBfGridPosX(), p.y + DC_Builder.getBfGridPosY());
        }
        setLocation(p);

    }

    protected Point getTargetLocation() {
        return getGame().getBattleField().getGrid().getPointForCoordinateWithOffset(
                target.getCoordinates());
    }

    protected boolean isCentered() {
        return false;
    }

    protected boolean isBfDialog() {
        return true;
    }

    protected void initButtonAreas() {
        int w = IMG.getWidth(null);
        int h = IMG.getHeight(null);
        ok = new Rectangle(getLocation().x + getPanelWidth() - w, getLocation().y, w, h);
        cancel = new Rectangle(getLocation().x + getPanelWidth() - w, getLocation().y
                + getPanelHeight() - h, w, h);

    }

    public boolean checkClick(MouseEvent e) {
        Point point = e.getLocationOnScreen();
        if (!new Rectangle(getLocation(), getPanelSize()).contains(point)) {
            return false;
        }
        mouseClicked(e);
        refresh();
        return true;
    }

    public void mouseClicked(MouseEvent e) {
        if (ok.contains(e.getLocationOnScreen())) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.OK_STONE);
            ok();
        } else if (cancel.contains(e.getLocationOnScreen())) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.BACK);
            close();
        }

    }

    public boolean isInfoClickAllowed() {
        return true;

    }

    public boolean isActionClickAllowed() {
        return false;

    }

    public void paint(Graphics g) {
        initLocation();
        initButtonAreas();
        BufferedImage image = ImageManager.getNewBufferedImage(getPanelWidth(), getPanelHeight());
        super.paint(image.getGraphics());

        for (Component c : compMap.keySet()) {
            if (c instanceof GraphicComponent) {
                GraphicComponent graphicComponent = (GraphicComponent) c;
                Point p = compMap.get(c);
                image.getGraphics().drawImage(graphicComponent.getImg(), p.x, p.y, null);
            }
        }
        if (isDrawBackground()) {
            g.setColor(getBackgroundColor());
            g.fillRect(getBackgroundX(), getBackgroundY(), getBackgroundWidth(),
                    getBackgroundHeight());
        }
        g.drawImage(image, getLocation().x, getLocation().y, null);

        g.drawImage(IMG, ok.x, ok.y, null);
        g.drawImage(VISUALS.CANCEL.getImage(), cancel.x, cancel.y, null);
        // CustomButton okButton = new CustomButton(VISUALS.OK) {
        // CustomButton closeButton = new CustomButton(VISUALS.CANCEL) {
        // comp.add(okButton, "id ok, pos " + x + " 0");
        // comp.add(closeButton, "pos ok.x @max_bottom, id close");
    }

    protected Color getBackgroundColor() {
        return ColorManager.BACKGROUND;
    }

    protected boolean isDrawBackground() {
        return false;
    }

    protected int getBackgroundHeight() {
        return 0;
    }

    protected int getBackgroundWidth() {
        return 0;
    }

    protected int getBackgroundY() {
        return 0;
    }

    protected int getBackgroundX() {
        return 0;
    }

    protected void ok() {

    }

    public void close() {
        visible = false;

    }

    public DC_Game getGame() {
        if (game == null) {
            game = DC_Game.game;
        }
        return game;
    }

    @Override
    public void refresh() {
        // target.getGame().getBattleField().refresh();
    }

    public void show() {
        visible = true;
        getGame().getBattleField().getBuilder().refresh();
    }

    public boolean isVisible() {
        return visible;
    }

}
