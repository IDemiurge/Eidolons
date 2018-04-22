package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.gui.panels.TablePanel;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class ValueTable<D, A extends Actor> extends TablePanel {
    protected   int rows;
    protected   int columns;
    protected D[] data;
    protected A[] actors;
    protected int wrap;
    protected int size;

    public ValueTable(int wrap, int size) {
        this.wrap = wrap;
        this.size = size;
        columns = wrap;
        rows = size/wrap;
        if (size%wrap>0)
            rows++;
        if (getElementSize() != null) {
            setFixedSize(true);
            setSize(columns * getElementSize().x, rows * getElementSize().y);
        }
    }

    protected Vector2 getElementSize() {
        return null;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public void updateAct(float delta) {
        clear();
        init();
    }

    public void init() {
        data = initDataArray();
        actors = initActorArray();

        int j = 0, i = 0;
        for (D sub : data) {
            addElement(actors[i] = createElement(sub)).top();
            j++;
            i++;
            if (j >= wrap) {
                row();
                j = 0;
            }
        }
}

    protected abstract A createElement(D datum);

    protected abstract A[] initActorArray();

    protected abstract D[] initDataArray();


}
