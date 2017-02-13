package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class TablePanel extends Container<Table> {

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

    public void addElement(Container el) {
        if (cols.size() == 0) {
            addCol();
            lastCol.setHeight(getPrefHeight());
        }
        el.fill();
        //el.setDebug(true);
        lastCol.add(el).fill();
        lastCol.row();
    }

    public void addCol() {
        lastCol = new Table();
        lastCol.setHeight(getPrefHeight());
        cols.add(lastCol);
        cols.forEach(el -> {
            el.setWidth(getPrefWidth() / cols.size());
        });
        inner.add(lastCol);
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
