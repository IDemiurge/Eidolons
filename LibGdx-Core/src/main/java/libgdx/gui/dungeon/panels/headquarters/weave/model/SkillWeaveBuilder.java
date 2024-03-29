package libgdx.gui.dungeon.panels.headquarters.weave.model;

import com.badlogic.gdx.math.Vector2;
import eidolons.entity.unit.Unit;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.dungeon.panels.headquarters.weave.Weave;
import libgdx.gui.dungeon.panels.headquarters.weave.WeaveSpace.WEAVE_VIEW_FILTER;
import libgdx.gui.dungeon.panels.headquarters.weave.actor.WeaveActorBuilder;
import libgdx.gui.dungeon.panels.headquarters.weave.model.skills.SkillWeave;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return Arrays.stream(SKILL_GROUP.values()).filter(this::checkFilter).map
                (group -> StringMaster.format(group.name())).toArray(String[]::new);
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
