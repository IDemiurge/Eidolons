package main.client.cc.gui.neo.header;

import main.client.cc.CharacterCreator;
import main.content.VALUE;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.swing.components.panels.page.log.WrappedTextComp;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

public class HeroTextComp extends WrappedTextComp implements MouseListener {

    private static final VALUE[] VALUES = {G_PROPS.NAME, G_PROPS.BACKGROUND};
    private DC_HeroObj hero;

    public HeroTextComp() {
        super(null);
        panelSize = new Dimension(200, 100);

    }

    public HeroTextComp(DC_HeroObj hero) {
        super(null);
        this.hero = hero;
        refresh();

    }

    @Override
    public void refresh() {
        if (getMouseListeners().length == 0) {
            addMouseListener(this);
        }
        if (hero == null) {
            return;
        }
        List<String> lines = new LinkedList<>();
        for (VALUE V : VALUES) {
            String value = hero.getValue(V);
            if (V instanceof PARAMETER) {
                value = V.getName() + " " + value;
            }
            lines.add(value);
        }
        setTextLines(lines);
    }

    protected int getDefaultFontSize() {
        return 17;
    }

    protected boolean isCentering() {
        return true;
    }

    @Override
    public Dimension initSizeFromText(String text) {
        return new Dimension(VISUALS.PORTRAIT_BORDER.getWidth(), super.getDefaultY()
                * VALUES.length);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        new Thread(new Runnable() {
            public void run() {
                String name = CharacterCreator.getHeroName(hero);
                if (name == null) {
                    return;
                }
                hero.setName(name);
                refresh();
            }
        }, " thread").start();

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