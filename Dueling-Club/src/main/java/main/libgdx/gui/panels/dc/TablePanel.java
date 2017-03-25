package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
    protected boolean updatePanel;
    private Table inner;
    private Table lastCol;
    private List<Table> cols;

    public TablePanel() {
        cols = new ArrayList<>();
        inner = new Table();
        inner.left().bottom();
        fill().left().bottom();
        super.setActor(inner);
    }

    @Override
    public void clear() {
        inner.clear();
        cols.clear();
    }

    public void addEmptyCol(int val) {
        Actor a = new Actor();
        a.setWidth(val);
        addElement(new Container<>(a).left().bottom());

        addCol();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        updatePanel = true;
    }

    public Cell<Container> addElement(Container el) {
        if (cols.size() == 0) {
            createNewCol();
        }
        el.fill();
        final Cell<Container> cell = lastCol.add(el).expand();
        cell.fill();
        //cell.pad(el.getTop(), el.getPadLeft(), el.getPadBottom(), el.getPadRight());
        //el.pad(0);
        if (rowDirection == TOP_DOWN) {
            lastCol.row();
        }
        return cell; //fuck incapsulation
    }

    private void createNewCol() {
        lastCol = new Table();
        //lastCol.setHeight(getPrefHeight());
        cols.add(lastCol);
        inner.add(lastCol);
    }

    public void addCol() {
        if (cols.size() == 0) {
            createNewCol();
        }

        if (rowDirection == TOP_DOWN) {
            createNewCol();
        }

        if (rowDirection == TOP_RIGHT || rowDirection == TOP_LEFT) {
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

    @Override
    public Container<Table> background(Drawable background) {
        if (background instanceof TextureRegionDrawable) {
            final TextureRegionDrawable drawable = ((TextureRegionDrawable) background);
            width(drawable.getRegion().getRegionWidth());
            height(drawable.getRegion().getRegionHeight());
        }
        return super.background(background);
    }
}
