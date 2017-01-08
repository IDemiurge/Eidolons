package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 1/7/2017.
 */
public class TableContainer extends Container {
    private int rows;
    private int columns;
    private int horSpace;
    private int vertSpace;

    boolean horizontal = true;
    List<Comp> cellComps;
    Supplier<List<Comp>> supplier;

    public TableContainer(int rows, int columns, Supplier<List<Comp>> supplier) {
        super("");
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
