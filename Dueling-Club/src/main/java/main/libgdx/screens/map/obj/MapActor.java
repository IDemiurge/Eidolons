package main.libgdx.screens.map.obj;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.module.adventure.map.MapVisionMaster.MAP_OBJ_INFO_LEVEL;
import main.libgdx.bf.SuperActor;
import main.libgdx.bf.generic.SuperContainer;
import main.libgdx.bf.light.ShadowMap.SHADE_LIGHT;
import main.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 2/7/2018.
 */
public class MapActor extends SuperActor {
    protected final TextureRegion originalTexture;
    protected SuperContainer highlight;
    protected Image portrait;
    MAP_OBJ_INFO_LEVEL infoLevel;

    public MapActor(TextureRegion portraitTexture) {
        addActor(portrait = new Image(portraitTexture));
        originalTexture = portraitTexture;
    }
    public void init(){
        highlight = new SuperContainer(new Image(TextureCache.getOrCreateR(
         SHADE_LIGHT.LIGHT_EMITTER.getTexturePath())), true);
        highlight.setTouchable(Touchable.disabled);
        highlight.setAlphaTemplate(ALPHA_TEMPLATE.HIGHLIGHT);
        highlight.setColor(getTeamColor());
//        highlight.setPosition(highlight.getWidth()/2, highlight.getHeight()/2);
        addActor(highlight);
//        highlight.setSize(getWidth()*1.2f,getWidth()*1.2f);
}


    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void move(Vector2 destination) {

    }

    @Override
    public float getWidth() {
        return portrait.getWidth();
    }

    @Override
    public float getHeight() {
        return portrait.getHeight();
    }

    public Image getPortrait() {
        return portrait;
    }
}
