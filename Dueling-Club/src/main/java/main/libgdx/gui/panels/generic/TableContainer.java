package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class TableContainer extends Container {
    protected int rows;
    protected int columns;
    protected int horSpace;
    protected int vertSpace;

    protected boolean horizontal = true;
    protected List<Comp> cellComps;
    protected Supplier<List<Comp>> supplier;
//TODO generate texture for row*column

    public TableContainer( int rows, int columns,
                          Supplier<List<Comp>> supplier) {
        this("",   rows,   columns,  supplier);
    }
     public TableContainer(String imagePath, int rows, int columns, Supplier<List<Comp>> supplier) {
            super(imagePath);
        this.columns = columns;
        this.rows = rows;
        this.supplier = supplier;
    }

    @Override
    public void update() {
        cellComps = supplier.get();
        List<Comp> list = new LinkedList<>();
        int n = horizontal ? columns : rows;
        int n1 = !horizontal ? columns : rows;
        if (cellComps.size() > rows * columns) {
            //what if not enough cells? Default expand direction?
        }
        for (Comp c : cellComps) {
            n--;
            if (n <= 0) {
                n1--;
                n = horizontal ? columns : rows;
                list.add(new Wrap(horizontal));
            }

            list.add(c);

        }
        setComps(list.toArray(new Comp[list.size()]));
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }
}
