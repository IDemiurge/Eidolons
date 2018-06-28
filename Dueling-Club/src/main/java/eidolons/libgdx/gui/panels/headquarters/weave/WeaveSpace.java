package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveModelBuilder;
import eidolons.libgdx.stage.StageX;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 6/4/2018.
 */
public class WeaveSpace extends StageX {
    WEAVE_VIEW_MODE viewMode;
    Map<Object, GroupX> cache = new HashMap<>();
    private GroupX currentView;
    boolean skills;

    public WeaveSpace() {
        super(new ScreenViewport(new OrthographicCamera()));
        refresh();
    }

    public void refresh() {
        clear();
        addActor(currentView = getView(viewMode));
    }

    private GroupX getView(WEAVE_VIEW_MODE viewMode) {
        GroupX group;
        if (viewMode == WEAVE_VIEW_MODE.HERO)
            group = cache.get(getHero());
        else
            group = cache.get(viewMode);

        if (group != null)
            return group;
        if (viewMode == WEAVE_VIEW_MODE.ALL) {
            group = initFullView();
        } else {
            group = new GroupX();
            Weave graph = WeaveModelBuilder.buildHeroGraph(getHero(), skills);
            group.addActor(graph);
        }
        //events? refresh?
        return group;
    }

    private GroupX initFullView() {
        GroupX group = new GroupX();
        List<Weave> graphs = WeaveModelBuilder.buildAllGraphs(skills);
        for (Weave graph : graphs) group.addActor(graph);
        return group;
    }

    private Unit getHero() {
        return Eidolons.getMainHero();
    }

    public enum WEAVE_VIEW_MODE {
        ALL, HERO,
    }
}
