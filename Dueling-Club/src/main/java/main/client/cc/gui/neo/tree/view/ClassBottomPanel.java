package main.client.cc.gui.neo.tree.view;

import main.client.cc.gui.neo.points.HC_PointComp;
import main.client.cc.gui.neo.tree.HC_Tree;
import main.content.CONTENT_CONSTS.CLASS_GROUP;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.entity.obj.DC_HeroObj;
import main.entity.type.ObjType;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.secondary.InfoMaster;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ClassBottomPanel extends HT_BottomPanel {

    ObjType lastSelectedType;
    G_Panel masteryCompsPanel;
    private CLASS_GROUP group;
    private List<PARAMETER> displayedMasteries = new LinkedList<>();

    public ClassBottomPanel(CLASS_GROUP group, DC_HeroObj hero, HC_Tree tree) {
        super(group, hero, tree);
        int x = 460;
        int y = 160;
        panelSize = new Dimension(x, y);
        this.group = group;
        this.hero = hero;
        this.tree = tree;
        init();
    }

    private void init() {

    }

    public void setGroup(CLASS_GROUP group) {
        this.group = group;

    }

    @Override
    public void refresh() {
        super.refresh();
        if (lastSelectedType != selectedType) {
            // selectedType.getProperty(PROPS.REQUIREMENTS)
            displayedMasteries.clear();
            for (String req : hero.getGame().getRequirementsManager().getRequirements(selectedType,
                    0).getReqMap().keySet()) {
                String[] parts = req.split(InfoMaster.PARAM_REASON_STRING);
                if (parts.length < 2) {
                    continue;
                }
                PARAMETER param = ContentManager.getMastery(parts[0]);
                if (param != null) {
                    if (param.isMastery()) {
                        displayedMasteries.add(param);
                    }
                }
            }
            main.system.auxiliary.LogMaster.log(1, "displayedMasteries = " + displayedMasteries);

            // masteryCompsPanel.removeAll();
            for (PARAMETER p : displayedMasteries) {
                HC_PointComp comp = generateMasteryComp(p);
                // // wrap
                Object pos = "";
                // i++;
                masteryCompsPanel.add(comp, pos);
            }
        }

        super.refresh();
    }

    private HC_PointComp generateMasteryComp(PARAMETER param) {
        HC_PointComp c = new HC_PointComp(true, hero, getBuffer(), param, PARAMS.MASTERY_POINTS,
                VISUALS.BUTTON_NEW_TINY) {

        };
        return c;
    }

    @Override
    public void setSelectedType(ObjType selectedType) {
        lastSelectedType = this.selectedType;
        super.setSelectedType(selectedType);
    }

    @Override
    protected void addSpecial() {
        // TODO MASTERY_COMP_ID

    }

}
