package main.libgdx.gui.panels.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Arrays;
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
    protected List<Actor> cellComps;
    protected Supplier<List<Actor>> supplier;
//TODO generate texture for row*column

    public TableContainer(int rows, int columns,
                          Supplier<List<Actor>> supplier) {
        this("", rows, columns, supplier);
    }

    public TableContainer(String imagePath, int rows, int columns,
                          Supplier<List<Actor>> supplier) {
        super(imagePath);
        this.columns = columns;
        this.rows = rows;
        this.supplier = supplier;
    }

    public TableContainer(String imagePath, int rows, int columns,
                          Actor... actors) {
        super(imagePath);
        this.columns = columns;
        this.rows = rows;
        this.cellComps = new LinkedList<>(Arrays.asList(actors));
    }

    @Override
    public void update() {
        initComps();
        List<Actor> list = new LinkedList<>();
        int n = horizontal ? columns : rows;
        if (cellComps.size() > rows * columns) {
            //what if not enough cells? Default expand direction?
        }
        for (Actor c : cellComps) {

            if (n <= 0) {
                n = horizontal ? columns : rows;
                list.add(new Wrap(horizontal));
            } else
                n--;
            list.add(c);

        }
        setComps(list.toArray(new Comp[list.size()]));
        super.update();
    }

    @Override
    public void initComps() {
        if (supplier != null)
            cellComps = supplier.get();
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
    }
}
