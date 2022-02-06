package libgdx.bf.grid.cell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.OutlineMaster;
import libgdx.assets.texture.TextureCache;
import main.content.enums.rules.VisionEnums;

/**
 * Created by JustMe on 4/1/2018.
 * <p>
 * special tooltip
 * never in outline
 * emblem?
 */
public class LastSeenView extends GenericGridView {

    private UnitGridView parentView;

    public LastSeenView(UnitViewOptions o, UnitGridView view) {
        super(view.getUserObject(), o);
        greyedOut = true;
        setParentView(view);
    }

    @Override
    public BattleFieldObject getUserObject() {
        if (parentView == null) {
            return null;
        }
        return parentView.getUserObject();
    }

    @Override
    public TextureRegion getDefaultTexture() {
        if (defaultTexture == null) {
            defaultTexture = TextureCache.getRegionUV(VisionEnums.OUTLINE_TYPE.UNKNOWN.getImagePath());
        }
        return defaultTexture;
    }

    protected boolean isResetOutlineOnHide() {
        return false;
    }
    @Override
    protected void setDefaultTexture() {
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
        if (getHpBar() != null)
            getHpBar().setVisible(false);
    }

    protected void init(TextureRegion arrowTexture, int arrowRotation, TextureRegion emblem) {
        super.init( emblem);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //        setVisible(!getParentView().isVisible());
        if (getUserObject().isPlayerCharacter())
            return;

        if ( OutlineMaster.isOutlinesOn())
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

    public UnitGridView getParentView() {
        return parentView;
    }

    public void setParentView(UnitGridView parentView) {
        this.parentView = parentView;
    }
}
