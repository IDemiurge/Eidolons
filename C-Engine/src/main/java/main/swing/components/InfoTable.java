package main.swing.components;

import main.content.VALUE;
import main.entity.obj.Obj;
import main.swing.generic.components.G_Panel;

import java.util.ArrayList;
import java.util.List;

public abstract class InfoTable extends G_Panel {
    protected int rows;
    protected int compWidth;
    protected int compHeight;

    protected VALUE[][] valueTable;
    protected Obj obj;
    protected float size;
    private List<PropertyComponent> components = new ArrayList<>();

    public InfoTable(Obj entity) {
        this.obj = entity;
        init();
    }

    @Override
    public void refresh() {
        for (PropertyComponent comp : components) {
            comp.refresh();
        }
    }

    protected void init() {
        initSize();
        initValues();
        initComponents();
    }

    public void scrollDown() {
        // TODO
    }

    protected void initComponents() {
        for (int y = 0; y < rows; y++) {
            int x = 0;
            for (VALUE value : valueTable[y]) {

                boolean special = false;
                PropertyComponent comp = new PropertyComponent(value, obj,
                        special);
                components.add(comp);

                String pos = "sg comps, wrap, " + "pos 0 " + y
                        * comp.getPanelSize().getHeight() * y;
                add(comp, pos);
            }
            x++;
        }
    }

    protected abstract void initValues();

    public abstract void initSize();

}
