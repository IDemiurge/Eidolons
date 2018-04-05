package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by JustMe on 4/1/2018.
 *
 * special tooltip
 * never in outline
 * emblem?
 *
 */
public class LastSeenView extends  GridUnitView{

    public LastSeenView(UnitViewOptions o, GridUnitView view) {
        super(o);
        greyedOut=true;
       setParentView(view);
        debug();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
//        if (visible) {
//            arrow.setRotation(getParentView().arrow.getRotation());
//        }
    }

    @Override
    protected void updateVisible() {
        super.updateVisible();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setVisible(!getParentView().isVisible());
        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean isHpBarVisible() {
        return false;
    }
}
