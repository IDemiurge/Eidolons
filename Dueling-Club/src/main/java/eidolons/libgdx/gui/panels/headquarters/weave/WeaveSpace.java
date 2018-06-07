package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.libgdx.stage.StageX;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveSpace extends StageX {

    //Graph

    public enum WEAVE_VIEW_MODE{
        ALL, HERO,
    }

    public WeaveSpace() {
        super(new ScreenViewport(new OrthographicCamera()));

//        graph = new WeaveGraph();
    }
}
