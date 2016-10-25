package main.swing.components.panels.page.info;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.spells.SpellUpgradeMaster;
import main.content.CONTENT_CONSTS2.SPELL_UPGRADE;
import main.entity.Entity;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.swing.generic.components.misc.GraphicComponent;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.ListMaster;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellUpgradePage extends InfoPage implements MouseListener {

    SPELL_UPGRADE selected;
    Map<SPELL_UPGRADE, SpellUpgradeComp> comps = new HashMap<>();
    int wrap = 4;
    private WrappedTextComp textComp;

    public SpellUpgradePage(Entity entity) {
        // TODO Auto-generated constructor stub
        super(VISUALS.INFO_PANEL);
        this.entity = entity;
        // new PropertyPage(list, entity)
        List<SPELL_UPGRADE> upgrades = SpellUpgradeMaster.getAvailableUpgradesFromSpell(entity);
        if (upgrades.isEmpty())
            upgrades = new ListMaster<SPELL_UPGRADE>().getList(SPELL_UPGRADE.APHOTIC,
                    SPELL_UPGRADE.DRAINING, SPELL_UPGRADE.VAMPIRIC, SPELL_UPGRADE.PUTRID,
                    SPELL_UPGRADE.TWILIGHT, SPELL_UPGRADE.GHOSTLY, SPELL_UPGRADE.VINDICATION,

                    // SPELL_UPGRADES.PSIONIC, SPELL_UPGRADES.ACIDIC,
                    // SPELL_UPGRADES.PUTRID,
                    // SPELL_UPGRADES.GHOSTLY, SPELL_UPGRADES.ASTRAL,
                    // SPELL_UPGRADES.VINDICATION,
                    // SPELL_UPGRADES.PALE,
                    SPELL_UPGRADE.BLIGHT

            );
        textComp = new WrappedTextComp(null, true);
        textComp.setDefaultSize(new Dimension(getVisuals().getWidth() - 40, getVisuals()
                .getHeight()
                - 40 - GuiManager.getSmallObjSize() * 2
                // (1 + upgrades.size() / wrap)
        ));
        add(textComp, "id textComp, pos 20 20" + "" + "" + "");
        int n = 0;
        String y = ", y textComp.y2";
        for (SPELL_UPGRADE su : upgrades) {
            if (n == 0)
                selected = su;
            if (n >= wrap) {
                n = 0;
                y += "+" + 72;
            }
            String c = "x 20+" + ((60) * n) + y;
            n++;

            SpellUpgradeComp comp = new SpellUpgradeComp(su);
            comp.addMouseListener(this);
            comps.put(su, comp);
            add(comp, c);
        }
    }

    @Override
    public void refresh() {
        for (SPELL_UPGRADE su : comps.keySet()) {
            comps.get(su).setSelected(su == selected);
            Image glyphImage = null;
            if (SpellUpgradeMaster.getActiveUpgradesFromSpell(entity).contains(su))
                glyphImage = su.getGlyphImageActive(); // (su == selected) ?
            else
                glyphImage = (su == selected) ? su.getGlyphImageSelected() : su.getGlyphImage();
            comps.get(su).setImg(glyphImage);
            comps.get(su).repaint();
        }
        String string = selected.getDescription();
        if (string == null)
            string = selected.getName();
        textComp.setText(selected.getName() + ": "
                // entity.getType().getName() + " Upgrades: \n"
                + string

        );
        textComp.refresh();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        SPELL_UPGRADE upgrade = ((SpellUpgradeComp) e.getSource()).getUpgrade();
        selected = upgrade;
        if (e.getClickCount() > 1)
            CharacterCreator.getHeroManager().spellUpgradeToggle(selected, entity);
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

    public class SpellUpgradeComp extends GraphicComponent {
        boolean selected;
        private SPELL_UPGRADE upgrade;

        public SpellUpgradeComp(SPELL_UPGRADE su) {
            super(su.getGlyphImage());
            this.upgrade = su;

        }

        // public ComponentVisuals getGenericVisuals() {
        // if (selected)
        // return new CompVisuals(null, upgrade.getGlyphImageActive());
        // return new CompVisuals(null, upgrade.getGlyphImage());
        // }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public SPELL_UPGRADE getUpgrade() {
            return upgrade;
        }

    }
}
