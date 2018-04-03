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
    private final GridUnitView source;

    public LastSeenView(UnitViewOptions o, GridUnitView view) {
        super(o);
        greyedOut=true;
        source=view;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            arrow.setRotation(source.arrow.getRotation());
        }
    }

    @Override
    protected void updateVisible() {
        super.updateVisible();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);
    }
}
