package main.swing.generic.components;

import main.swing.generic.services.layout.LayoutInfo;
import main.system.auxiliary.LogMaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public abstract class Builder implements GameGUI {
    protected static Dimension screenSize;

    protected boolean initialized;
    protected Builder[] builderArray = new Builder[0];

    protected String[] infoArray = new String[0];

    protected G_Component[] compArray = new G_Component[0];

    protected String[] cInfoArray = new String[0];

    // protected G_Component[] compHolderArray;
    //
    // protected String[] cHinfoArray;

    protected Map<Builder, LayoutInfo> builders;

    protected JComponent comp;

    protected boolean ready = false;
    protected HashMap<JComponent, LayoutInfo> comps;

    private int N = 0;

    private KeyListener keyListener;

    private boolean dirty = true;

    public static void setScreenSize(Dimension size) {
        screenSize = size;
    }

    // G_Panel
    public JComponent build() {
        N = 0;
        if (!initialized)
            init();
        if (comp == null) {
            comp = new G_Panel();
        }

        if (builders != null)
            if (builderArray == null)
                return getComp();
            else {
                initMap();
                buildBuilders();
            }

        if (compArray != null) {
            initCompMap();
            addComponents();

        }
        if (getComp() == null)
            throw new RuntimeException();
        return getComp();

    }

    private void addComponents() {
        for (JComponent c : compArray) {
            String info = comps.get(c).getMiGString();
            main.system.auxiliary.LogMaster.log(0, "adding " + c.getClass().getSimpleName()
                    + " in " + getClass().getSimpleName() + " at " + info);
            add(c, info);
            getComp().setComponentZOrder(c, N);
            N++;
        }
    }

    private void buildBuilders() {

        for (Builder builder : builderArray) {
            String info = builders.get(builder).getMiGString();

            main.system.auxiliary.LogMaster.log(LogMaster.GUI_DEBUG,

                    "building " + builder.getClass().getSimpleName() + " for " + getClass().getSimpleName()
                            + " at " + info);

            if (keyListener != null)
                builder.setKeyListener(keyListener);
            JComponent newComp = null;
            try {
                newComp = builder.build();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            add(newComp, info);

            try {
                getComp().setComponentZOrder(newComp, N);
                N++;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void add(JComponent newComp, String info) {
        getComp().add(newComp, info);
        if (getKeyListener() != null) {
            if (newComp instanceof G_Component) {
                ((G_Component) newComp).setKeyManager(keyListener);
            } else
                newComp.addKeyListener(getKeyListener());
        }
    }

    public void initCompMap() {

        comps = new HashMap<JComponent, LayoutInfo>();
        int i = 0;
        for (JComponent c : compArray) {
            comps.put(c, new LayoutInfo(cInfoArray[i]));
            i++;
        }
    }

    public void initMap() {

        builders = new HashMap<Builder, LayoutInfo>();
        int i = 0;
        for (Builder b : builderArray) {
            builders.put(b, new LayoutInfo(infoArray[i]));
            i++;
        }
    }

    public abstract void init();

    public Builder[] getBuilderArray() {
        return builderArray;
    }

    public void setBuilderArray(Builder[] builderArray) {
        this.builderArray = builderArray;
    }

    public String[] getInfoArray() {
        return infoArray;
    }

    public void setInfoArray(String[] infoArray) {
        this.infoArray = infoArray;
    }

    public Map<Builder, LayoutInfo> getBuilders() {
        return builders;
    }

    public void setBuilders(Map<Builder, LayoutInfo> builders) {
        this.builders = builders;
    }

    public JComponent getComp() {
        return comp;
    }

    @Override
    public void reload() {

    }

    @Override
    public void refresh() {

        if (!isDirty())
            return;
        for (Refreshable r : compArray) {
            try {
                r.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Refreshable r : builderArray) {
            try {
                r.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void dataChanged() {
        for (Builder b : builderArray)
            b.dataChanged();
        for (G_Component c : compArray)
            c.dataChanged();
    }

    public KeyListener getKeyListener() {
        return keyListener;
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }

}
