package libgdx.gui.dungeon.panels.headquarters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.TablePanelX;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class ValueTable<D, A extends Actor> extends TablePanelX {
    protected int rows;
    protected int columns;
    protected D[] data;
    protected A[] actors;
    protected int wrap;
    protected int size;
    protected float space;

    public ValueTable(int wrap, int size) {
        this(wrap, size, 0);
    }

    public ValueTable(int wrap, int size, int space) {
        this.space = space;
        initSize(wrap, size);
    }

    protected void initSize(int wrap, int size) {
        this.wrap = wrap;
        this.size = size;
        if (isSquare()) {
            this.wrap = (int) Math.round(Math.sqrt(size));
        }
        columns = wrap;
        rows = size / wrap;
//        if (size % wrap > 0)
//            rows++;
        if (getElementSize() != null && isTableFixedSize()) {
            setFixedSize(true);
            setSize(columns * (space + getElementSize().x), Math.max(1, rows) * getElementSize().y);
        }
    }

    protected boolean isSquare() {
        return false;
    }

    protected boolean isTableFixedSize() {
        return true;
    }


    protected Vector2 getElementSize() {
        return null;
    }

    public float getSpace() {
        return space;
    }

    public void setSpace(float space) {
        this.space = space;
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
        clearChildren();
        init();
    }

    public D[] getData() {
        return data;
    }

    public A[] getActors() {
        return actors;
    }

    public void init() {
        data = initDataArray();
        size = data.length;
        actors = initActorArray();

        initSize(wrap, data.length);
        if (wrap == 0) {
            new HorizontalFlowGroup(getSpace());
            //needs fixed size
        }
        int j = 0, i = 0;
        int wrap = this.wrap + getDynamicWrap(i);
        if (getElementSize() != null) {
            float h = getElementSize().y;
            getContentTable().defaults().height(h).width(getElementSize().x);
        }
        for (D sub : data) {
            if (i >= actors.length)
                break;
            Cell cell = getContentTable().addElement(actors[i] = createElement(sub)).top().space(getSpace());
            if (getElementSize() != null) {
                cell.size(getElementSize(sub).x, getElementSize(sub).y);
            }
            j++;
            i++;
            if (j >= wrap) {
                getContentTable().row();
                j = 0;
                wrap = this.wrap + getDynamicWrap(i);
            }
        }
    }

    protected Vector2 getElementSize(D sub) {
        return getElementSize();
    }


    protected TablePanel getContentTable() {
        return this;
    }

    protected int getDynamicWrap(int i) {
        return 0;
    }

    protected abstract A createElement(D datum);

    protected abstract A[] initActorArray();

    protected abstract D[] initDataArray();


}
