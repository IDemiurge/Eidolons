package eidolons.client.cc.gui.neo.header;

import eidolons.client.cc.CharacterCreator;
import eidolons.entity.obj.unit.Unit;
import eidolons.swing.components.panels.page.log.WrappedTextComp;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class HeroTextComp extends WrappedTextComp implements MouseListener {

    private static final VALUE[] VALUES = {G_PROPS.NAME, G_PROPS.BACKGROUND};
    private Unit hero;

    public HeroTextComp() {
        super(null);
        panelSize = new Dimension(200, 100);

    }

    public HeroTextComp(Unit hero) {
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
        List<String> lines = new ArrayList<>();
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