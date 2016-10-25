package main.client.cc.gui.neo.tree.t3;

import main.client.cc.CharacterCreator;
import main.client.cc.gui.misc.PoolComp;
import main.content.PARAMS;
import main.entity.Entity;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;
import main.swing.generic.components.G_Panel;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class T3InfoPanel extends G_Panel {

    DC_PagedInfoPanel infoPanel;
    G_Panel controlPanel;
    PoolComp xpPool;
    PoolComp costPool;
    JLabel label;
    private boolean flipped;

    public T3InfoPanel(boolean flipped) {
        this.flipped = flipped;

    }

    public void init() {
        Entity skill = null;
        try {
            skill = CharacterCreator.getHero().getSkills().get(flipped ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        infoPanel = new DC_PagedInfoPanel(skill);
        ImageIcon icon = ImageManager.getEmptyItemIcon(flipped);
        if (skill != null)
            icon = skill.getIcon();
        label = new JLabel(icon);
        controlPanel = new G_Panel();
        controlPanel.setPanelSize(new Dimension(122, getPanelSize().height));
        costPool = new PoolComp(skill, PARAMS.XP_COST, "Experience cost", false);

        xpPool = new PoolComp(CharacterCreator.getHero(), PARAMS.XP, "Experience points", true) {
            // public Entity getEntity() {
            // // TODO buffer?
            // return super.getEntity();
            // }
        };
        addComps();
    }

    public void addComps() {
        Object pos = null;
        String x = null;
        String y = null;
        if (!flipped) {
            x = "label.x2";
            y = "0";
            pos = "id info, pos " + x + "+2 " + y;
            add(infoPanel, pos);

            x = "info.x2";
            y = "0";
            pos = "id cp, pos " + x + " " + y;
            add(controlPanel, pos);

            x = getPanelWidth() + "-" + VISUALS.POOL.getWidth() + "-"
                    + (VISUALS.INFO_PANEL.getWidth() + controlPanel.getPanelWidth());
            y = "64";
            pos = "id costPool, pos " + x + " " + y;
            add(costPool, pos);

            y = "label.y2+32";
            pos = "id xpPool, pos " + x + " " + y;
            add(xpPool, pos);

            x = x + "+12";
            y = "costPool.y2+32";
            pos = "id label, pos " + x + " " + y;
            add(label, pos);
        } else {
            x = "cp.x2";
            y = "0";
            pos = "id info, pos " + x + " " + y;
            add(infoPanel, pos);

            x = "0";
            y = "0";
            pos = "id cp, pos " + x + " " + y;
            add(controlPanel, pos);

            x = "info.x2";
            y = "64";
            pos = "id costPool, pos " + x + " " + y;
            add(costPool, pos);

            y = "label.y2+12";
            pos = "id xpPool, pos " + x + " " + y;
            add(xpPool, pos);

            x = "info.x2";
            y = "costPool.y2+12";
            pos = "id label, pos " + x + "+12 " + y;
            add(label, pos);

        }

    }

    public void select(Entity value) {
        infoPanel.select(value);
        label.setIcon(value.getIcon());
    }

    enum T3_INFO_CONTROLS {
        INV, HERO_INFO, SB,;
    }

	/*
     * ought to be special... maybe double-paged?
	 * 
	 * normal IP + icon/pool placement 
	 * 
	 * ++ Controls - inv, hero info, sb, ...
	 * 
	 * 
	 * 
	 * 
	 */
}
