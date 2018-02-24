package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.module.adventure.map.Route;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/19/2018.
 alpha fluctuation
 mouse responsive

 special component perhaps
 */
public class RouteActor extends ImageContainer {
    private static final String HIGHLIGHT = " hl";
    private final Image overlay;
    boolean highlighted;

    public RouteActor(Route sub) {
        super(  new Image(TextureCache.getOrCreateR(
         getHighlightImgPath(sub))));
      overlay =   new Image(TextureCache.getOrCreateR(sub.getRouteImage()));
      addActor(overlay);

//addListener()
    }

    private static String getHighlightImgPath(Route sub) {
        return  StringMaster.cropLastPathSegment(sub.getRouteImage())+ StringMaster.getPathSeparator()+
         HIGHLIGHT.trim()
         + StringMaster.getPathSeparator()+
         StringMaster.getLastPathSegment(
          StringMaster.cropFormat(sub.getRouteImage()))+HIGHLIGHT+".png";
    }

    @Override
    protected float getAlphaFluctuationMin() {
        if (!highlighted)
            return 0.0f;
        return super.getAlphaFluctuationMin();
    }
    @Override
    protected float getAlphaFluctuationMax() {
        if (!highlighted)
            return 0.1f;
        return super.getAlphaFluctuationMax();
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
