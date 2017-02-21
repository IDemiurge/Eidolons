package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class TablePanel extends Container<Table> {

    protected static final int TOP_RIGHT = 0;
    protected static final int TOP_LEFT = 1;
    protected static final int TOP_DOWN = 2;
    protected static final int DOWN_TOP = 3;
    protected static final int DOWN_LEFT = 4;
    protected static final int DOWN_RIGHT = 5;
    protected int rowDirection = TOP_DOWN;
    private Table inner;
    private Table lastCol;
    private List<Table> cols;

    public TablePanel() {
        cols = new ArrayList<>();
        inner = new Table();
        inner.setFillParent(true);
        inner.left().bottom();
        fill();
        left().bottom();
        super.setActor(inner);
    }

    public void addEmptyCol(int val) {
        Actor a = new Actor();
        a.setWidth(val);
        addElement(new Container(a).fill().left().bottom());

        addCol();
    }
    public void addElement(Container el) {
        if (cols.size() == 0) {
            createNewCol();
        }
        el.fill();
        //el.setDebug(true);
        lastCol.add(el).fill();
        if (rowDirection == TOP_DOWN) {
            lastCol.row();
        }
    }

    private void createNewCol() {
        lastCol = new Table();
        lastCol.setHeight(getPrefHeight());
        cols.add(lastCol);
        cols.forEach(el -> {
            el.setWidth(getPrefWidth() / cols.size());
        });
        inner.add(lastCol);
    }

    public void addCol() {
        if (rowDirection == TOP_DOWN || cols.size() == 0) {
            createNewCol();
        }
        if (rowDirection == TOP_RIGHT) {
            lastCol.row();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    @Deprecated
    public Table getActor() {
        throw new UnsupportedOperationException("Do not use this!");
    }

    @Override
    @Deprecated
    public void setActor(Table actor) {
        throw new UnsupportedOperationException("Use TablePanel#addElement.");
    }
}
