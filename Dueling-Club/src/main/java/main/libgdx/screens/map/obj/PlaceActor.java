package main.libgdx.screens.map.obj;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.module.adventure.map.Place;
import main.libgdx.GdxMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.screens.map.obj.PlaceActorFactory.PlaceActorParameters;

/**
 * Created by JustMe on 2/9/2018.
 */
public class PlaceActor extends MapActor {
    private Place place;

//    Image preview;

    public PlaceActor(PlaceActorParameters parameters) {
        super(parameters.mainIcon);
        init(parameters);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (getColor().a==0)
            return null;
        return super.hit(x, y, touchable);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getColor().a==0)
            setTouchable(Touchable.disabled);
        else
            setTouchable(Touchable.enabled);
    }

    public void hover() {
        ActorMaster.addScaleAction(this, getHoveredScale(), 0.5f);
        ActorMaster.addFadeInAction(border, 0.5f);
        setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);
    }

    private float getHoveredScale() {
        return getHoveredSize() / originalTexture.getRegionHeight();
    }

    private float getDefaultScale() {
        return (getDefaultSize() / originalTexture.getRegionHeight());
    }

    private float getDefaultSize() {
        return GdxMaster.adjustSize(96);
    }

    public Place getPlace() {
        return place;
    }

    private float getHoveredSize() {
        return GdxMaster.adjustSize(128);
    }

    public void minimize() {
        ActorMaster.addScaleAction(this, getDefaultScale(), 0.5f);
        setAlphaTemplate(ALPHA_TEMPLATE.TOP_LAYER);
    }

    private void init(PlaceActorParameters parameters) {
//        preview = new Image(parameters.preview);
        this.place = parameters.place;
        border = new Image(parameters.border);
        addActor(border);
        if (isHighlightUnder()){
            border.setZIndex(0);
        }
        setPosition(parameters.position.x - portrait.getImageWidth() / 2,
         parameters.position.y - portrait.getImageHeight() / 2);
        minimize();
    }

    private boolean isHighlightUnder() {
        return true;
    }


}
