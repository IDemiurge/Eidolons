package main.swing.components.panels;

import main.client.dc.Launcher;
import main.entity.Entity;
import main.entity.obj.DC_UnitObj;
import main.game.Game;
import main.game.MicroGame;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.editors.ImageChooser;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;
import main.system.images.ImageManager.ALIGNMENT;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * name color active? me? portrait HP ...
 *
 * @author Regulus
 */
public class DC_PortraitPanel extends G_Panel implements MouseListener {
    JLabel portrait;
    private Entity entity;

    // mouselistener? character type?
    public DC_PortraitPanel(Game game, boolean info) {
        super(VISUALS.PORTRAIT_BORDER);

        // comp.setSize(size);
        portrait = new JLabel(((MicroGame) game).getPlayer(!info).getPortrait());
        add(portrait, "pos 5 17");
        addMouseListener(this);
    }

    private boolean isNonUnit() {
        return !(entity instanceof DC_UnitObj);
    }

    @Override
    public void refresh() {

        // if (info)
        // this.obj = game.getManager().getInfoObj();
        // else {
        // this.obj = game.getManager().getActiveObj();
        //
        // }
        ImageIcon icon = null;
        if (entity != null) {
            icon = entity.getIcon();
            if (icon == null) {
                icon = entity.getDefaultIcon();
            }
            if (isNonUnit()) {
                if (icon.getIconHeight() < GuiManager.getFullObjSize()) {
                    icon = ImageManager.getOffsetImage(icon.getImage(),
                            GuiManager.getFullObjSize(), ALIGNMENT.CENTER);
                }
            }
            if (icon != null) {
                portrait.setIcon(icon);
            } else {
                portrait.setIcon(ImageManager.getEmptyUnitIcon());
            }
        }

    }

    public JLabel getPortrait() {
        return portrait;
    }

    protected void selectNewPortrait() {
        String portrait = new ImageChooser().launch("Image", getEntity().getImagePath());
        if (portrait != null) {
            if (ImageManager.isImage(portrait)) {
                getEntity().setImage(portrait);
                getPortrait().setIcon(getEntity().getIcon());

            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (Launcher.DEV_MODE) {
            selectNewPortrait();
        }

    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity obj) {
        this.entity = obj;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}
