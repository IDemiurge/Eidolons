package main.swing.generic.components.panels;

import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.services.listener.MouseClickListener;
import main.system.auxiliary.RandomWizard;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import java.util.List;

public abstract class G_ScrolledPanel<E> extends G_Panel implements MouseWheelListener {
    protected List<E> data;
    protected List<G_Panel> comps = new LinkedList<>();
    protected int offset;
    protected boolean vertical;
    protected int spaceTaken;
    protected int arrowVersion;

    public G_ScrolledPanel(boolean vertical, int arrowVersion, Dimension size) {
        super(vertical ? "flowy" : "");
        this.vertical = vertical;
        this.arrowVersion = arrowVersion;
        panelSize = size;
        addMouseWheelListener(this);
    }

    boolean isArrowBlocked(boolean forward) {
        if (!forward)
            return (offset == 0);
        return (comps.get(comps.size() - 1).getParent() == this);
    }

    protected void arrowPressed(boolean forward) {
        if (isArrowBlocked(forward)) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return;
        }
        if (RandomWizard.random())
            SoundMaster.playStandardSound(STD_SOUNDS.SCROLL);
        else
            SoundMaster.playStandardSound(STD_SOUNDS.SCROLL2);
        int n = forward ? 1 : -1;
        offset += n;
        refresh();
    }

    protected void resetComps() {
        comps.clear();
        for (E d : getData()) {
            comps.add(createComponent(d));
        }
    }

    protected abstract G_Panel createComponent(E d);

    protected void addArrows(final boolean forward) {
        String y = "";
        String x = "";
        // if (forward) {
        y = vertical ? "" + spaceTaken : "center_y@";
        x = !vertical ? "" + spaceTaken : "center_x@";
        // } else {
        // y = vertical ? "0" : "center_y@";
        // x = !vertical ? "0" : "center_x@";
        // }
        String pos = "pos " + x + " " + y;
        GraphicComponent arrow = new GraphicComponent(getArrowImage(forward));
        arrow.addMouseListener(new MouseClickListener() {
            public void mouseClicked(MouseEvent arg0) {
                arrowPressed(forward);
            }
        });
        add(arrow, pos);
        spaceTaken += (vertical ? getArrowImage(true).getHeight(null) : getArrowImage(true)
                .getWidth(null));
    }

    private Image getArrowImage(final boolean forward) {
        // TODO
        return ImageManager.getArrowImage(vertical, !forward, arrowVersion);
    }

    @Override
    public void refresh() {
        removeAll();
        resetComps();
        spaceTaken = 0;
        if (isArrowShown(false))
            addArrows(false);
        for (int i = offset; i < comps.size(); i++) {
            G_Panel component = comps.get(i);
            int spaceRequired = vertical ? component.getPanelHeight() : component.getPanelWidth();
            if (spaceRequired > getSpace() - spaceTaken)
                break;
            String y = vertical ? "" + spaceTaken : isCentering() ? "@center_y" : "0";
            String x = !vertical ? "" + spaceTaken : isCentering() ? "@center_x" : "0";
            spaceTaken += spaceRequired;
            add(component, "pos " + x + " " + y);
        }
        if (isArrowShown(true))
            addArrows(true);

        refreshComponents();
    }

    protected boolean isCentering() {
        return false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {

        arrowPressed(arg0.getWheelRotation() > 0);

    }

    private boolean isArrowShown(boolean b) {
        // if (isArrowAlwaysShown())
        // return true;
        // if (isArrowBlocked(b))
        // if (comps.get(comps.size() - 1).getParent() == this)
        // return false;
        // return b;
        return true;
    }

    private boolean isArrowAlwaysShown() {
        // TODO Auto-generated method stub
        return false;
    }

    protected int getSpace() {
        if (vertical)
            return getPanelHeight();
        return getPanelWidth();
    }

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

}
