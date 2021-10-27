package libgdx.gui.dungeon.panels.dc.actionpanel.spaces;

import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.entity.active.spaces.Feat;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.dungeon.panels.TablePanel;

import java.util.Map;
import java.util.Set;

public class FeatSpaceRow extends TablePanel {

    HorizontalFlowGroup slots;
    String name;
    FadeImageContainer icon;
    boolean active;
    Set<FeatContainer> feats;

    public FeatSpaceRow(String name) {
        this.name = name;
        left().bottom();
        slots = new HorizontalFlowGroup();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        FeatSpaceDataSource ds;

        Map<Feat, FeatContainer> cache;


        /*
        do we support dynamic changes in F.S. slots?
         */
        slots.clear();

        feats.forEach(feat-> {
            // feat.setActive(active);
        });
    }
}
