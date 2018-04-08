package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;

/**
 * Created by JustMe on 4/1/2018.
 *
 * special tooltip
 * never in outline
 * emblem?
 *
 */
public class LastSeenView extends  GenericGridView{

    private GridUnitView parentView;

    public LastSeenView(UnitViewOptions o, GridUnitView view) {
        super(o);
        greyedOut = true;
        setParentView(view);
        debug();
    }

    @Override
    protected void checkResetOutline(float delta) {
        super.checkResetOutline(delta);
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible)
            if (this.isVisible()!=visible){
                super.setVisible(visible);
            }
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
//        setVisible(!getParentView().isVisible());
        super.draw(batch, parentAlpha);
    }


    @Override
    public boolean isHpBarVisible() {
        return false;
    }

    @Override
    public String toString() {
        return isVisible()+ " LSV for " +getParentView() + getColor().a +
         getActionsOfClass(AlphaAction.class);
    }

    @Override
    public void addAction(Action action) {
        super.addAction(action);
//        main.system.auxiliary.log.LogMaster.log(1,this+" action: " +action);
    }

    public void setParentView(GridUnitView parentView) {
        this.parentView = parentView;
    }

    public GridUnitView getParentView() {
        return parentView;
    }
}
