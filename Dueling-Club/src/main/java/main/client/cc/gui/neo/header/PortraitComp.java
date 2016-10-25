package main.client.cc.gui.neo.header;

import main.client.dc.Launcher;
import main.entity.obj.DC_HeroObj;
import main.swing.generic.components.G_CompHolder;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.G_Panel.VISUALS;
import main.swing.generic.components.editors.ImageChooser;
import main.system.auxiliary.GuiManager;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PortraitComp extends G_CompHolder implements MouseListener {
    public static final VISUALS border = VISUALS.PORTRAIT_BORDER;
    private DC_HeroObj hero;
    private JLabel label;
    private Dimension size = new Dimension(GuiManager.getFullObjSize(), GuiManager.getFullObjSize());

    public PortraitComp(DC_HeroObj hero) {
        this(null, hero);
    }

    public PortraitComp(HeroHeader header, DC_HeroObj hero) {
        this.hero = hero;
        initComp();
    }

    @Override
    public void refresh() {
        label.setIcon(hero.getIcon());
        super.refresh();
    }

    private void initComp() {
        comp = new G_Panel(border);
        Image img = ImageManager.getSizedVersion(hero.getIcon().getImage(), size);
        label = new JLabel(new ImageIcon(img));
        comp.add(label, "pos 5 17");
        comp.addMouseListener(this);
    }

    protected void handleMouseClick() {
        if (Launcher.DEV_MODE) {
            String portrait = new ImageChooser().launch("Image", hero.getImagePath());
            if (portrait != null)
                if (ImageManager.isImage(portrait)) {
                    hero.setImage(portrait);
                    // hero.setProperty(G_PROPS.IMAGE, portrait, true);
                    label.setIcon(hero.getIcon());
                }

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseClick();

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
