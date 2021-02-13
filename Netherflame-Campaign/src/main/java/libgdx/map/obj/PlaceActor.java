package libgdx.map.obj;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMaster;
import eidolons.macro.map.Place;
import main.content.enums.GenericEnums;

/**
 * Created by JustMe on 2/9/2018.
 */
public class PlaceActor extends MapActor {
    private Place place;
    private final float scaleMod;

//    Image preview;

    public PlaceActor(PlaceActorFactory.PlaceActorParameters parameters) {
        super(parameters.getMainIcon());
        scaleMod = Math.max(portrait.getWidth() / portrait.getHeight(),
         portrait.getHeight() / portrait.getWidth());
        init(parameters);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getColor().a == 0)
            return null;
        return super.hit(x, y, touchable);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getColor().a == 0)
            setTouchable(Touchable.disabled);
        else
            setTouchable(Touchable.enabled);
    }

    public void hover() {
        ActionMaster.addScaleAction(this, getHoveredScale(), 0.5f);
        ActionMaster.addFadeInAction(border, 0.5f);
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT);
    }

    private float getHoveredScale() {
        return getHoveredSize()
         / originalTexture.getRegionHeight();
    }

    private float getDefaultScale() {
        return
         (getDefaultSize() / originalTexture.getRegionHeight());
    }

    private float getDefaultSize() {
        return GdxMaster.adjustSizeBySquareRoot(80, 0.1f) * getScaleMod();
    }

    private float getScaleMod() {
        return scaleMod;
    }

    public Place getPlace() {
        return place;
    }

    private float getHoveredSize() {
        return GdxMaster.adjustSizeBySquareRoot(120, 0.1f ) * getScaleMod();
    }

    public void minimize() {
        ActionMaster.addScaleAction(this, getDefaultScale(), 0.5f);
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.TOP_LAYER);
    }

    private void init(PlaceActorFactory.PlaceActorParameters parameters) {
//        preview = new Image(parameters.preview);
        this.place = parameters.getPlace();
        border = new Image(parameters.getBorder());
        addActor(border);
        if (isHighlightUnder()) {
            border.setZIndex(0);
        }
        setPosition(parameters.getPosition().x - portrait.getImageWidth() / 2,
         parameters.getPosition().y - portrait.getImageHeight() / 2);
        minimize();
    }

    private boolean isHighlightUnder() {
        return true;
    }


}
