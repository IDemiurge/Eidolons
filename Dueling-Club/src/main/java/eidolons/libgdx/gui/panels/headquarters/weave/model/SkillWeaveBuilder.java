package eidolons.libgdx.gui.panels.headquarters.weave.model;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.weave.Weave;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveSpace.WEAVE_VIEW_FILTER;
import eidolons.libgdx.gui.panels.headquarters.weave.actor.WeaveActorBuilder;
import eidolons.libgdx.gui.panels.headquarters.weave.model.skills.SkillWeave;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/28/2018.
 */
public class SkillWeaveBuilder extends WeaveModelBuilder {
    private final WEAVE_VIEW_FILTER filter;

    public SkillWeaveBuilder(WEAVE_VIEW_FILTER filter) {
        super();
        this.filter = filter;
    }

    public List<Weave> buildAll(Unit hero) {
        List<Weave> graphs = new ArrayList<>();
        int n = 0;
        for (String sub : getWeaveGroups()) {

            WeaveDataNode root = createGroupNode(sub);
            Weave graph = new SkillWeave(root, true);
            graph.setUserObject(HqDataMaster.getHeroDataSource(hero));
            graph.init();
            WeaveActorBuilder.build(graph);

            Vector2 v = getWeavePosition(n++);
            graph.setPosition(v.x, v.y);
            graphs.add(graph);
        }
        return graphs;
    }


    @Override
    protected WeaveDataNode createGroupNode(String sub) {
        String img = ImageManager.getMasteryGroupPath(sub);
        String descr = "";
        Object arg = new EnumMaster<SKILL_GROUP>().retrieveEnumConst(SKILL_GROUP.class, sub);
        return new WeaveDataNode(img, descr, arg);
    }

    @Override
    protected String[] getWeaveGroups() {
        List<String> list = Arrays.stream(SKILL_GROUP.values()).filter(group -> checkFilter(group)).map
         (group -> StringMaster.format(group.name())).
         collect(Collectors.toList());
        return list.toArray(new String[0]);
    }

    private boolean checkFilter(SKILL_GROUP group) {
        if (filter == WEAVE_VIEW_FILTER.NONE || filter==null )
            return true;

        if (filter == WEAVE_VIEW_FILTER.NON_MAGIC) {
            switch (group) {
                case SPELLCASTING:
                case PRIME_ARTS:
                case ARCANE_ARTS:
                case LIFE_ARTS:
                case DARK_ARTS:
                case CHAOS_ARTS:
                case HOLY_ARTS:
                case DEATH_ARTS:
                    return false;
            }
            return true;
        }
        if (filter == WEAVE_VIEW_FILTER.MAGIC) {
            switch (group) {
                case SPELLCASTING:
                case PRIME_ARTS:
                case ARCANE_ARTS:
                case LIFE_ARTS:
                case DARK_ARTS:
                case CHAOS_ARTS:
                case HOLY_ARTS:
                case DEATH_ARTS:
                    return true;
            }
        }
        return false;
    }


}
