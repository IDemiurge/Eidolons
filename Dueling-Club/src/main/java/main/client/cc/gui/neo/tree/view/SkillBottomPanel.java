package main.client.cc.gui.neo.tree.view;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.misc.PoolComp;
import main.client.cc.gui.neo.points.HC_PointComp;
import main.client.cc.gui.neo.tree.HC_Tree;
import main.content.ContentManager;
import main.content.PARAMS;
import main.content.values.parameters.PARAMETER;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.system.math.DC_MathManager;

import java.awt.event.MouseEvent;

public class SkillBottomPanel extends HT_BottomPanel {

    HC_PointComp masteryComp;

    // TextComp pointCost;
    // TextComp rank;
    public SkillBottomPanel(final PARAMS mastery, final Unit hero, final HC_Tree tree) {
        super(mastery, hero, tree);
    }

    public ObjType getBuffer() {
        return CharacterCreator.getHeroPanel().getMiddlePanel().getScc().getBufferType();
    }

    protected void addSpecial() {
        freePoints = new TextCompDC(VISUALS.SPACE_SMALL) {
            protected String getText() {
                return "Skill Points: "
                        + DC_MathManager.getFreeMasteryPoints(hero, (PARAMETER) arg);
            }

            ;
        };
        masteryScore = new TextCompDC(VISUALS.SPACE_SMALL) {
            protected String getText() {
                return "Final Score: "
                        + hero.getIntParam(ContentManager.getMasteryScore((PARAMETER) arg));
            }

            ;
        };

        masteryPoints = new PoolComp(hero, PARAMS.MASTERY_POINTS, "Mastery Points", false);

        masteryComp = new HC_PointComp(true, hero, bufferType, (PARAMETER) arg,
                PARAMS.MASTERY_POINTS) {
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == upArrow) {

                    upClick(); // sound
                    tree.refresh();
                    refresh();
                } else if (e.getSource() == downArrow) {
                    downClick(); // sound
                    // CharacterCreator.refreshGUI();
                    tree.refresh();
                } else if (e.getSource() == lock) {
                    lockClick(e);
                }
            }

        };
        add(freePoints, "id points, pos @center_x-" + VISUALS.SPACE_SMALL.getWidth() / 2 + " 0"); // @center_x
        add(masteryScore, "id masteryScore, pos points.x2+1 points.y");
        add(masteryComp, "id masteryComp, pos 0 points.y2");
    }

    @Override
    public void refresh() {
        super.refresh();
        freePoints.refresh();
        masteryScore.refresh();
        masteryComp.setEntity(bufferType);
        masteryComp.refresh();

    }
}
