package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums;

/**
 * Created by JustMe on 4/1/2018.
 * <p>
 * special tooltip
 * never in outline
 * emblem?
 */
public class LastSeenView extends GenericGridView {

    private GridUnitView parentView;

    public LastSeenView(UnitViewOptions o, GridUnitView view) {
        super(view.getUserObject(), o);
        greyedOut = true;
        setParentView(view);
    }

    @Override
    public BattleFieldObject getUserObject() {
        return parentView.getUserObject();
    }

    @Override
    protected TextureRegion getDefaultTexture() {
        return TextureCache.getOrCreateR(VisionEnums.OUTLINE_TYPE.UNKNOWN.getImagePath());
    }

    @Override
    protected void setDefaultTexture() {
    }

    @Override
    protected void checkResetOutline(float delta) {
        super.checkResetOutline(delta);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getColor().a != 1) {
            return null;
        }
        return super.hit(x, y, touchable);
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible)
            if (this.isVisible() != visible) {
                super.setVisible(visible);
            }
        super.setVisible(visible);
        //        if (visible) {
        //            arrow.setRotation(getParentView().arrow.getRotation());
        //        }
    }

    @Override
    protected void updateVisible() {
        if (emblemImage != null)
            emblemImage.setVisible(false);
        if (modeImage != null)
            modeImage.setVisible(false);
        if (arrow != null)
            arrow.setVisible(false);
        if (getHpBar() != null)
            getHpBar().setVisible(false);
    }

    protected void init(TextureRegion arrowTexture, int arrowRotation,  TextureRegion emblem) {
        super.init(arrowTexture, arrowRotation,   emblem);
        if (arrow != null)
            arrow.setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //        setVisible(!getParentView().isVisible());
        if (getUserObject().isPlayerCharacter())
            return;

        checkResetOutline(Gdx.graphics.getDeltaTime());
        super.draw(batch, parentAlpha);
    }


    @Override
    public boolean isHpBarVisible() {
        return false;
    }

    @Override
    public String toString() {
        return isVisible() + " LSV for " + getParentView() + getColor().a +
                getActionsOfClass(AlphaAction.class);
    }

    @Override
    public void addAction(Action action) {
        if (getUserObject().isPlayerCharacter())
            return;
        super.addAction(action);
        //        main.system.auxiliary.log.LogMaster.log(1,this+" action: " +action);
    }

    public GridUnitView getParentView() {
        return parentView;
    }

    public void setParentView(GridUnitView parentView) {
        this.parentView = parentView;
    }
}
