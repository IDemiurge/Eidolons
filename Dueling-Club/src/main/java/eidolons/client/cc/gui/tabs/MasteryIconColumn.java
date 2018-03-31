package eidolons.client.cc.gui.tabs;

import eidolons.entity.obj.unit.Unit;
import eidolons.client.cc.HC_Master;
import eidolons.client.cc.gui.tabs.SkillTabNew.SKILL_DISPLAY_GROUPS;
import main.content.values.parameters.PARAMETER;
import main.entity.Entity;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import eidolons.system.math.DC_MathManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class MasteryIconColumn extends G_Panel implements MouseListener {
    SKILL_DISPLAY_GROUPS group;
    List<PARAMETER> masteries;
    int offset = 2;
    private int fitSize = 34;
    private int fullSize = 40;
    private int wrap = 2;
    private int rows = 5;
    private Entity hero;

    public MasteryIconColumn(Unit hero, SKILL_DISPLAY_GROUPS group) {
        masteries = new ArrayList<>();
        List<PARAMETER> unlockedList = DC_MathManager.getUnlockedMasteries(hero);
        for (PARAMETER p : group.getMasteries()) {
            if (unlockedList.contains(p)) {
                masteries.add(p);
            }
        }
        this.hero = hero;
        this.group = group;
        panelSize = new Dimension((fullSize) * wrap - offset * wrap, (fullSize) * rows - offset
         * rows);

    }

    private void updateMasteries() {
        List<PARAMETER> unlockedList = DC_MathManager.getUnlockedMasteries(hero);
        for (PARAMETER p : group.getMasteries()) {
            if (masteries.contains(p)) {
                continue;
            }
            // preserve order, only *add*!
            if (unlockedList.contains(p)) {
                masteries.add(p);
            }
        }

    }

    public Component getMasteryIconComp(PARAMETER mastery) {
        // boolean locked = DC_MathManager.isMasteryUnlocked(hero, mastery);
        Image img = HC_Master.generateValueIcon(mastery, false);
        GraphicComponent comp = new GraphicComponent(img);
        comp.addMouseListener(this);
        comp.setDataObject(mastery);
        return comp;
    }

    @Override
    public void refresh() {
        updateMasteries();
        removeAll();
        int i = 0;
        for (PARAMETER mastery : masteries) {
            Component icon = getMasteryIconComp(mastery);

            int x = i % 2 == 0 ? -offset : fitSize - offset;
            int y = i / 2 * fitSize;
            add(icon, "pos " + x + " " + y); // 40x40 override? overlap,
            // that is... selected
            // on top... custom zorder
            i++;
            // page controls needed? perhaps not!
        }
        // setComponentZOrder(comp, index);
        revalidate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        PARAMETER param = (PARAMETER) ((GraphicComponent) e.getSource()).getDataObject();
        HC_Master.setLastClickedMastery(param);

        if (e.getClickCount() > 1 || e.isAltDown()) {
            HC_Master.goToSkillTree(param);
        }
        refresh();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
