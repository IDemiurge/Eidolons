package eidolons.client.cc.gui.neo.principles;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.misc.PoolComp;
import eidolons.client.cc.gui.neo.points.HC_PointComp;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.images.ImageManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class PrinciplePanel extends G_Panel implements MouseListener {

    private Unit hero;
    private PRINCIPLES principle;
    private PrincipleView view;
    private List<PrinciplePointComp> pointComps = new ArrayList<>();
    private PrincipleTable table;
    private PoolComp pool;
    private GraphicComponent principlesIcon;
    private GraphicComponent helpIcon;

    public PrinciplePanel(Unit hero, PrincipleView view) {
        super(VISUALS.PRINCIPLE_PANEL_FRAME);
        this.hero = hero;
        this.view = view;
        principlesIcon = new GraphicComponent(ImageManager.getPrincipleImage(null));
        helpIcon = new GraphicComponent(STD_COMP_IMAGES.QUESTION);
    }

    public void init() {
        principlesIcon.addMouseListener(this);
        helpIcon.addMouseListener(this);
        pool = new PoolComp(getBuffer(), PARAMS.IDENTITY_POINTS, "", null) {
            @Override
            protected int getOffsetY() {
                return 6;
            }

            @Override
            protected Font getDefaultFont() {
                return PrincipleView.getDefaultFont();
            }
        };
        table = new PrincipleTable(hero, true);
        int x = (VISUALS.PRINCIPLE_VALUE_BOX.getWidth() + 24 - 50) / 2;

        add(principlesIcon, "id principlesIcon, pos " + x + " " + getFrameBorderWidth());

        x = VISUALS.PRINCIPLE_VALUE_BOX.getWidth() - VISUALS.POOL_MECH.getWidth() + 44;
        add(pool, "id pool, pos " + x + " 12");
        add(table, "pos pool.x2 pool.y");
        int height = VISUALS.PRINCIPLE_VALUE_BOX.getHeight();
        int y = 0;
        for (PRINCIPLES p : HeroEnums.PRINCIPLES.values()) {
            PrinciplePointComp pointComp = new PrinciplePointComp(this, p, hero, this, getBuffer());
            pointComps.add(pointComp);
            y += height;
            add(pointComp, "pos " + getFrameBorderWidth() + " " + y); // TODO
            // buffer
        }

    }

    private int getFrameBorderWidth() {
        return 16;
    }

    public void reset() {
        for (HC_PointComp comp : pointComps) {
            comp.getModel().reset();
        }
    }

    @Override
    public void refresh() {
        CharacterCreator.getPanel().getMiddlePanel().getScc().resetBuffer();
        for (HC_PointComp comp : pointComps) {
            comp.setEntity(getBuffer());
            comp.refresh();
        }
        pool.setEntity(getBuffer());
        pool.refresh();
        table.refresh();
    }

    private ObjType getBuffer() {
        return CharacterCreator.getPanel().getMiddlePanel().getScc().getBufferType();
    }

    public PRINCIPLES getPrinciple() {
        return principle;
    }

    public void setPrinciple(PRINCIPLES principle) {
        this.principle = principle;
        refresh();
    }

    public PrincipleView getView() {
        return view;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == principlesIcon) {
            view.principleSelected(null);
        } else if (e.getSource() == helpIcon) {
            view.principleSelected(null);
        }

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
