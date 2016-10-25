package main.swing.generic.components.panels;

import main.data.XLinkedMap;
import main.swing.generic.components.G_Panel;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class WrappedContainer<E> extends G_Panel {

    protected boolean vertical;
    protected List<E> data;
    protected List<G_Panel> comps = new LinkedList<>();
    protected Map<Integer, G_Panel> customComps = new XLinkedMap<>();
    protected int spaceTaken;
    protected int wrapped;
    protected int estimatedFullSize;
    private Map<E, G_Panel> compMap = new HashMap<E, G_Panel>();
    private int wrap = 2;

    public WrappedContainer(boolean vertical) {
        this.vertical = vertical;
        setMigLayout(vertical ? "flowy" : "");
    }

    public void addCustomComp(G_Panel comp, Integer index) {
        customComps.put(index, comp);
    }

    protected void addCustomComps() {
        int i = 0;
        for (E sub : getData()) {
            i++;
            if (checkCustomCompRequired(sub)) {
                customComps.put(i, getCustomComp(sub));
            }
        }

    }

    protected int getWrap() {
        return wrap;
    }

    protected abstract boolean checkCustomCompRequired(E sub);

    protected abstract G_Panel getCustomComp(E sub);

    @Override
    public void refresh() {
        clearCustomComps();
        removeAll();
        resetComps();
        addCustomComps();
        spaceTaken = 0;
        int i = 0;
        estimateSizes();

        for (G_Panel sub : comps) {
            G_Panel comp = customComps.get(i);
            if (comp != null)
                spaceTaken = addComp(comp, true);

            spaceTaken = addComp(sub, false);

            i++;
        }
        // panelSize = new Dimension(width, height);
        refreshComponents();
    }

    protected void sortData() {

    }

    @Override
    public boolean isAutoSizingOn() {
        return true;
    }

    @Override
    public Dimension getPanelSize() {
        return super.getPreferredSize();
    }

    protected G_Panel getComp(E e) {
        return compMap.get(e);
    }

    protected void estimateSizes() {
        estimatedFullSize = 0;
        for (G_Panel sub : comps) {
            estimatedFullSize += sub.getDimension(vertical);
        }
        for (G_Panel sub : customComps.values()) {
            estimatedFullSize += sub.getDimension(vertical);
        }
    }

    protected int addComp(G_Panel panel, boolean noWrap) {
        String pos = "";
        if (!noWrap)
            if (checkWrap(panel)) // horizontal?
            {
                pos = "wrap";
                spaceTaken = 0;
                wrapped++;
            }
        add(panel, pos);
        spaceTaken += panel.getDimension(vertical);
        return spaceTaken;
    }

    protected boolean checkWrap(G_Panel panel) {
        return panel.getDimension(vertical) + spaceTaken >= estimatedFullSize / getWrap();
    }

    protected void clearCustomComps() {
        customComps.clear();

    }

    protected void resetComps() {
        comps.clear();
        compMap.clear();
        for (E d : getData()) {
            G_Panel comp = createComp(d);
            compMap.put(d, comp);
            comp.refresh();
        }
        sortData();

        for (E d : getData())
            comps.add(getComp(d));
    }

    public abstract G_Panel createComp(E e);

    public boolean isVertical() {
        return vertical;
    }

    public List<E> getData() {
        data = new LinkedList<>(initData());
        return data;
    }

    public abstract List<E> initData();

    public List<G_Panel> getComps() {
        return comps;
    }

    public Map<Integer, G_Panel> getCustomComps() {
        return customComps;
    }

    public int getSpaceTaken() {
        return spaceTaken;
    }

    public int getWrapped() {
        return wrapped;
    }

    public int getEstimatedSize() {
        return estimatedFullSize;
    }
}
