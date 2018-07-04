package eidolons.libgdx.gui.panels.headquarters.weave;

import com.badlogic.gdx.graphics.Camera;
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
    WEAVE_VIEW_MODE viewMode = WEAVE_VIEW_MODE.DEFAULT;
    WEAVE_VIEW_FILTER filter = WEAVE_VIEW_FILTER.NONE;
    Map<Object, GroupX> cache = new HashMap<>();
    boolean skills = false; //toggle between class/skill view
    private GroupX currentView;

    public WeaveSpace(Camera cam) {
        super(new ScreenViewport(cam));
        //emitters!
//        addActor(ambience =new WeaveAmbience());
        refresh();
    }

    public void toggle() {
        skills = !skills;
    }

    public void refresh() {
        if (currentView != null) {
            currentView.remove();
        }
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
        //        group.pack();
        //        group.layout();
        group.setPosition((group).getWidth() / 2, (group).getHeight() / 2);
        //events? refresh?
        return group;
    }

    private GroupX initFullView() {
        GroupX group = new GroupX();
        List<Weave> graphs = WeaveModelBuilder.buildAllGraphs(filter, getHero(), skills);
        for (Weave graph : graphs) group.addActor(graph);
        return group;
    }

    private Unit getHero() {
        return Eidolons.getMainHero();
    }

    public void setViewMode(WEAVE_VIEW_MODE viewMode) {
        this.viewMode = viewMode;
    }

    public enum WEAVE_VIEW_FILTER {
        NONE,
        MAGIC,
        NON_MAGIC,


    }

    public enum WEAVE_VIEW_MODE {
        ALL, HERO,;
        public static WEAVE_VIEW_MODE DEFAULT = ALL;

    }
}
