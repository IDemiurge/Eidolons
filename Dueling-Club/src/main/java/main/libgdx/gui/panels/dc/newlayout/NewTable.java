package main.libgdx.gui.panels.dc.newlayout;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import java.util.LinkedList;
import java.util.List;

public class NewTable extends BasePanel {
    private int rowCount;
    private AlignW alignW;
    private AlignH alignH;
    private TableCell[][] table;
    private float[] fixedRowH;
    private float[] colSizes;
    private boolean scrollable;
    private boolean isVertical;

    public NewTable() {
    }

    public NewTable(float[] colSizes, int rowCount, AlignW alignW, AlignH alignH) {
        init(colSizes, rowCount, alignW, alignH);
    }

    protected void init(float[] colSizes, int rowCount, AlignW alignW, AlignH alignH) {
        this.rowCount = rowCount;
        this.fixedRowH = new float[rowCount];

        for (int i = 0; i < fixedRowH.length; i++) {
            fixedRowH[i] = -1;
        }

        this.alignW = alignW;
        this.alignH = alignH;
        table = new TableCell[colSizes.length][rowCount];

        for (int x = 0; x < table.length; x++) {
            for (int y = 0; y < table[x].length; y++) {
                table[x][y] = new TableCell();
                super.addActor(table[x][y]);
            }
        }

        int sum = 0;
        for (int i = 0; i < colSizes.length; i++) {
            sum += colSizes[i];
        }

        if (sum != 100) {
            float midSize = 100f / (float) colSizes.length;
            for (int i = 0; i < colSizes.length; i++) {
                colSizes[i] = midSize;
            }
        }

        for (int i = 0; i < colSizes.length; i++) {
            float size = colSizes[i];
            colSizes[i] = Math.max(Math.min(size, 100), 0);
        }

        this.colSizes = colSizes;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    @Deprecated
    public void addActor(Actor actor) {
        throw new UnsupportedOperationException("direct add operation not supported");
    }

    @Override
    @Deprecated
    public void addActorAt(int index, Actor actor) {
        throw new UnsupportedOperationException("direct add operation not supported");
    }

/*    public void addValueContainer(ValueContainer[] valueContainers) {
        for (int r = 0; r < rowCount; r++) {
            if (table[0][r].getActor() == null) {
                int i = 0;
                for (ValueContainer container : valueContainers) {
                    if (container.getImageContainer().getActor() != null) {
                        table[i++][r].setActor(container.getImageContainer());
                    }

                    if (container.getNameContainer().getActor() != null) {
                        table[i++][r].setActor(container.getNameContainer());
                    }

                    if (container instanceof MultiValueContainer) {
                        final List<Container> values = ((MultiValueContainer) container).getValues();
                        for (Container value : values) {
                            if (value.getActor() != null) {
                                table[i++][r].setActor(value.getActor());
                            }
                        }
                    } else {
                        if (container.getValueContainer().getActor() != null) {
                            table[i++][r].setActor(container.getValueContainer());
                        }
                    }
                }
                sizeChanged();
                break;
            }
        }
    }*/

/*    public void addValueContainer(ValueContainer valueContainer) {
        for (int r = 0; r < rowCount; r++) {
            if (table[0][r].getActor() == null) {
                int i = 0;
                if (valueContainer.getImageContainer().getActor() != null) {
                    table[i++][r].setActor(valueContainer.getImageContainer());
                }

                if (valueContainer.getNameContainer().getActor() != null) {
                    table[i++][r].setActor(valueContainer.getNameContainer());
                }

                if (valueContainer instanceof MultiValueContainer) {
                    final List<Container> values = ((MultiValueContainer) valueContainer).getValues();
                    for (Container value : values) {
                        if (value.getActor() != null) {
                            table[i++][r].setActor(value.getActor());
                        }
                    }
                } else {
                    if (valueContainer.getValueContainer().getActor() != null) {
                        table[i++][r].setActor(valueContainer.getValueContainer());
                    }
                }
                sizeChanged();
                break;
            }
        }
    }*/

    @Override
    @Deprecated
    public void addActorBefore(Actor actorBefore, Actor actor) {
        throw new UnsupportedOperationException("direct add operation not supported");
    }

    @Override
    @Deprecated
    public void addActorAfter(Actor actorAfter, Actor actor) {
        throw new UnsupportedOperationException("direct add operation not supported");
    }

    @Override
    @Deprecated
    public boolean removeActor(Actor actor) {
        throw new UnsupportedOperationException("remove operation not supported");
    }

    @Override
    @Deprecated
    public boolean removeActor(Actor actor, boolean unfocus) {
        throw new UnsupportedOperationException("remove operation not supported");
    }

    public <T extends Actor> CellParams addAt(int x, int y, T actor) {
        table[x][y].setActor(actor);
        update();
        table[x][y].update();
        return new CellParams() {
            @Override
            public CellParams setH(float h) {
                table[x][y].setHeight(h);
                fixedRowH[y] = h;
                update();
                table[x][y].update();
                return this;
            }
        };
    }

    protected void setBackground(Drawable drawable) {

    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (table == null) return;//not initialized
        super.draw(batch, parentAlpha);
    }

    public void update() {
        float relOne = getWidth() / 100f;
        for (int col = 0; col < colSizes.length; col++) {
            for (int row = 0; row < table[col].length; row++) {
                float colH;
                final float cellW = colSizes[col] * relOne;
                if (fixedRowH[row] > 0) {
                    final float height = fixedRowH[row];
                    for (int i = 0; i < table.length; i++) {
                        table[i][row].setHeight(height);
                    }
                    colH = fixedRowH[row];
                } else {
                    float notFixedH = getHeight();
                    List<TableCell> cells = new LinkedList<>();
                    for (int i = 0; i < table[col].length; i++) {
                        if (fixedRowH[i] > 0) {
                            notFixedH += table[col][i].getHeight();
                        } else {
                            cells.add(table[col][i]);
                        }
                    }
                    final float notFixedSingleCellH = notFixedH / cells.size();
                    for (TableCell cell : cells) {
                        cell.setHeight(notFixedSingleCellH);
                    }
                    colH = notFixedSingleCellH;
                }

                table[col][row].setWidth(cellW);

                float x = 0;
                for (int i = col - 1; i >= 0; i--) {
                    x += colSizes[i] * relOne;
                }

                table[col][row].setPosition(x, row * colH);
            }
        }
    }

    public enum AlignW {LEFT, CENTER, RIGHT}

    public enum AlignH {TOP, CENTER, BOTTOM}

    private static class TableCell extends Group {
        private Actor actor;

        public Actor getActor() {
            return actor;
        }

        public void setActor(Actor actor) {
            if (this.actor != null) {
                removeActor(this.actor);
            }
            this.actor = actor;
            addActor(actor);
            update();
        }

        private void update() {
            final AlignH alignH = ((NewTable) getParent()).alignH;
            final AlignW alignW = ((NewTable) getParent()).alignW;

            if (actor != null) {
                final float actorHeight = actor.getHeight();
                final float actorWidth = actor.getWidth();
                final float parentH = getHeight();
                final float parentW = getWidth();

                if (actorHeight > parentH) {
                    actor.setHeight(parentH);
                }
                if (actorWidth > parentW) {
                    actor.setWidth(parentW);
                }

                if (actorWidth == 0) {
                    actor.setWidth(parentW);
                }

                if (actorHeight == 0) {
                    actor.setHeight(parentH);
                }

                switch (alignW) {
                    case LEFT:
                        actor.setX(0);
                        break;
                    case CENTER:
                        final float halfW = actor.getWidth() / 2f;
                        final float parentHalfW = parentW / 2f;
                        actor.setX(parentHalfW - halfW);
                        break;
                    case RIGHT:
                        actor.setX(parentW - actor.getWidth());
                        break;
                }

                switch (alignH) {
                    case TOP:
                        actor.setY(parentH - actor.getHeight());
                        break;
                    case CENTER:
                        final float halfH = actor.getHeight() / 2f;
                        final float parentHalfH = parentH / 2f;
                        actor.setY(parentHalfH - halfH);
                        break;
                    case BOTTOM:
                        actor.setY(0);
                        break;
                }
            }
        }
    }

}
