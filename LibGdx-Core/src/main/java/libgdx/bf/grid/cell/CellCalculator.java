package libgdx.bf.grid.cell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.entity.obj.Structure;
import libgdx.bf.GridMaster;

public class CellCalculator {
    private final GridCellContainer cell;
    public int offsetX;
    public int offsetY;
    protected int z = 2;
    
    public CellCalculator(GridCellContainer cell) {
        this.cell = cell;
    }

    public final float getViewX(UnitGridView view) {
        int i = 0;
        if (cell.visibleViews != null) {
            i = cell.visibleViews.indexOf(view);
        }
        return getViewX(i);
    }

    public final float getViewY(UnitGridView view) {
        int i = 0;
        if (cell.visibleViews != null) {
            i = cell.visibleViews.indexOf(view);
        }
        return getViewY_(i, cell.getUnitViewCount());
    }

    public final float getViewX(int i) {
        return getViewX(getUnitViewOffset(), i);
    }

    public final float getViewY_(int i, int n) {
        return getViewY_(getUnitViewOffset(), i, n);
    }

    public final float getViewY(float perImageOffsetY, int i, int n) {
        return getViewY_(perImageOffsetY, i, n);
    }

    public final float getViewX(float perImageOffsetX, int i) {
        return perImageOffsetX * i+ offsetX;
    }

    public final float getViewY_(float perImageOffsetY, int i, int n) {
        if ( isTopToBottom())
            return (n - 1) * perImageOffsetY - perImageOffsetY * (n - i - 1) + offsetY;
        else
            return (n - 1) * perImageOffsetY - perImageOffsetY * i+ offsetY;
    }
    protected boolean isTopToBottom() {
        return true;
    }

    public float getUnitViewSize(BaseView actor) {
        if (actor instanceof UnitViewSprite) {
            return 128 - getUnitViewOffset() * (cell.getUnitViewCount() - 1);
        }
        return actor.getPortrait().getWidth() - getUnitViewOffset() * Math.max(0, cell.getUnitViewCount() - 1);
    }

    public float getObjScale(BaseView actor) {
        return getUnitViewSize(actor) / GridMaster.CELL_W;
    }

    protected Integer getZIndexForView(GenericGridView actor) {
        if (actor.isCellBackground())
            return 1;
        if (cell.getIndexMap().containsKey(actor)) {
            return cell.getIndexMap().get(actor);
        }
        if (actor.getUserObject() instanceof Structure || !actor.isHpBarVisible()) {
            return z=z + 1 ;
        } else
            return z++ + 1 ;
    }

    protected boolean isStaticZindex() {
        return !Gdx.input.isKeyPressed(Input.Keys.TAB)
                && !Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)
                && !Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
        //        if (staticZindexAlways)
        //            return true;
    }

    public int getUnitViewOffset() {
        return Math.round(cell.getWidth() /
                Math.max(1,  (getSizeFactorPerView() * cell.getUnitViewCountEffective())));
    }

    public float getSizeFactorPerView() {
        if (cell.hasBackground)
            return 6.0f;
        return 5.0f;
}
}