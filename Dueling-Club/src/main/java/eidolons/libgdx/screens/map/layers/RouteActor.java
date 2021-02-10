package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.texture.TextureCache;
import eidolons.macro.map.Route;
import main.system.PathUtils;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 2/19/2018.
 * alpha fluctuation
 * mouse responsive
 * <p>
 * special component perhaps
 */
public class RouteActor extends ImageContainer {
    private static final String HIGHLIGHT = " hl";
    private final Route route;
    boolean highlighted;

    public RouteActor(Route sub) {
        super(new Image(TextureCache.getOrCreateR(
         getHighlightImgPath(sub))));
        Image overlay = new Image(TextureCache.getOrCreateR(sub.getRouteImage()));
        addActor(overlay);
        this.route = sub;

//addListener()
    }

    private static String getHighlightImgPath(Route sub) {
        try {
            return PathUtils.cropLastPathSegment(sub.getRouteImage()) + PathUtils.getPathSeparator() +
             HIGHLIGHT.trim()
             + PathUtils.getPathSeparator() +
             PathUtils.getLastPathSegment(
              StringMaster.cropFormat(sub.getRouteImage())) + HIGHLIGHT + ".png";
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return "";
    }

    public Route getRoute() {
        return route;
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
