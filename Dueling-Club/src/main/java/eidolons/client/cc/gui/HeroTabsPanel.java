package eidolons.client.cc.gui;

import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.gui.neo.HeroPanel;
import eidolons.client.cc.gui.neo.tabs.HC_TabPanel;
import eidolons.client.cc.gui.neo.tabs.TabChangeListener;
import eidolons.client.cc.logic.party.Party;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.universal.PartyHelper;
import eidolons.swing.components.panels.DC_TopPanel;
import eidolons.swing.components.panels.page.info.element.TextCompDC;
import eidolons.swing.components.panels.page.info.element.ValueTextComp;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.GraphicComponent;
import main.swing.generic.components.misc.GraphicComponent.STD_COMP_IMAGES;
import main.system.graphics.ColorManager;
import main.system.images.ImageManager.STD_IMAGES;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeroTabsPanel extends G_Panel implements TabChangeListener {
    HC_TabPanel tabs;
    DC_TopPanel top;
    private Dimension iconSize = new Dimension(32, 32);
    private Map<Unit, MainPanel> panels = new LinkedHashMap<>();
    private List<MainPanel> panelList = new ArrayList<>();
    private PartyHeader header;

    public HeroTabsPanel(Unit... heroes) {
        super();
        tabs = new HC_TabPanel(new ArrayList<>());
        tabs.setChangeListener(this);
        top = new DC_TopPanel(CharacterCreator.getGame());

        if (heroes.length > 0) {
            for (Unit hero : heroes) {
                addHero(hero, true);
            }
            tabs.refresh();
        }
        add(top, "pos container.x2/2-" + VISUALS.BUTTON.getWidth() / 3 + " 0");

        if (CharacterCreator.isPartyMode()) {
            header = new PartyHeader(CharacterCreator.getParty());

            // panelSize = new Dimension(
            // (int) GuiManager.DEF_DIMENSION.getWidth(),
            // (int) (CharacterCreator.getHeroPanelSize().getHeight() - header
            // .getSize().getHeight()));

            add(header, "id header, pos 0 0");
            // add(tabs, "@id tabs, pos 0 " + MigMaster.MAX_BOTTOM);
            add(tabs, "id tabs, pos 0 header.y2");
        } else {
            add(tabs, "id tabs, pos 0 0");
        }

        top.setBackground(ColorManager.BACKGROUND);
        setBackground(ColorManager.BACKGROUND);
        setOpaque(true);
        // int i = 0;
        // setComponentZOrder(tabs, i);
        // i++;
        // if (header != null) {
        // setComponentZOrder(header, i);
        // i++;
        // }
        // setComponentZOrder(top, i);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void refresh() {
        if (PartyHelper.getParty() != null) {
            PartyHelper.getParty().toBase();
        }
        if (header != null) {
            header.refresh();
        }
        tabs.refresh();
        super.refresh();
    }

    public void addHero(Unit hero) {
        addHero(hero, false);
    }

    public void removeHero(Unit hero) {
        MainPanel o = panels.get(hero);
        panels.remove(hero);
        int i = panelList.indexOf(o);
        panelList.remove(o);
        tabs.removeTab(i);
        header.refresh();
    }

    public void addHero(Unit hero, boolean quiet) {
        MainPanel panel = new MainPanel(hero);

        panels.put(hero, panel);
        panel.initSelection();
        panelList.add(panel);
        // ImageIcon icon = new ImageIcon(ImageManager.getSizedVersion(hero
        // .getIcon().getEmitterPath(), iconSize));
        // HC_Tab tab = new HC_Tab(hero.getImagePath(),
        // panel.getComp());
        tabs.addTab(hero.getImagePath(), panel.getComp());
        if (!quiet) {
            tabs.selectLast();
        } else {
            tabs.select(0);
        }
        // tabs.getTabs().setSelectedComponent(panel.getComp());
    }

    public MainPanel getCurrentPanel() {
        if (tabs.getIndex() == -1) {
            return null;
        }
        if (panelList.size() <= tabs.getIndex()) {
            return null;
        }
        return panelList.get(tabs.getIndex());
    }

    public synchronized Dimension getIconSize() {
        return iconSize;
    }

    public synchronized void setIconSize(Dimension iconSize) {
        this.iconSize = iconSize;
    }

    public synchronized Map<Unit, MainPanel> getPanels() {
        return panels;
    }

    public synchronized void setPanels(Map<Unit, MainPanel> panels) {
        this.panels = panels;
    }

    public synchronized List<MainPanel> getPanelList() {
        return panelList;
    }

    public synchronized void setPanelList(List<MainPanel> panelList) {
        this.panelList = panelList;
    }

    public PartyHeader getPartyHeader() {
        return header;
    }

    @Override
    public void tabSelected(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void tabSelected(String name) {
        // TODO Auto-generated method stub

    }

    public static class PartyHeader extends G_Panel {

        private static final VISUALS V = VISUALS.VALUE_BOX_BIG;
        private Party party;
        private ValueTextComp gloryComp;
        private ValueTextComp orgComp;
        private ValueTextComp bsComp;
        private TextCompDC nameComp;

        // ++ rank/level
        public PartyHeader(Party party) {
            super();
            this.party = party;
            init();
            refresh();
            setBackground(ColorManager.BACKGROUND);
            setOpaque(true);
            panelSize = new Dimension(HeroPanel.HERO_PANEL_FRAME.getWidth(), V.getHeight());
        }

        private void init() {
            resetParty();
            nameComp = new TextCompDC(V, party.getName()) {
                protected int getDefaultFontSize() {
                    return 18;
                }
            };
            add(nameComp, "id text, pos 0 0");
            gloryComp = new ValueTextComp(PARAMS.GLORY, null) {
                protected int getDefaultFontSize() {
                    return 17;
                }

                public String getToolTipText() {
                    return "Glory";
                }
            };
            gloryComp.setEntity(party);
            // TODO CustomPanel(compInfo...[comp, string])
            add(new GraphicComponent(STD_COMP_IMAGES.GLORY) {
                public String getToolTipText() {
                    return "Glory";
                }
            }, "@id ig, pos text.x2+3 0");
            add(gloryComp, "id g, pos ig.x-4 ig.y2-6");

            add(new GraphicComponent(STD_IMAGES.MOVES) {
                public String getToolTipText() {
                    return "Organization";
                }
            }, "@id io, pos ig.x2+15 0");
            orgComp = new ValueTextComp(PARAMS.ORGANIZATION, null) {
                public String getToolTipText() {
                    return "Organization";
                }

                protected int getDefaultFontSize() {
                    return 17;
                }
            };
            orgComp.setEntity(party);
            add(orgComp, "id o, pos io.x+8 io.y2-6");

            add(new GraphicComponent(STD_IMAGES.WING) {
                public String getToolTipText() {
                    return "Battle Spirit";
                }
            }, "@id ibs, pos io.x2+15 0");
            bsComp = new ValueTextComp(PARAMS.BATTLE_SPIRIT, null) {
                public String getToolTipText() {
                    return "Battle Spirit";
                }

                protected int getDefaultFontSize() {
                    return 17;
                }
            };
            bsComp.setEntity(party);
            add(bsComp, "id bs, pos ibs.x+8 ibs.y2-6");
        }

        @Override
        public void refresh() {
            resetParty();
            nameComp.setText(party.getName());
            gloryComp.setEntity(party);
            gloryComp.refresh();
            orgComp.setEntity(party);
            orgComp.refresh();
            bsComp.setEntity(party);
            bsComp.refresh();
            revalidate();
            repaint();
        }

        public void resetParty() {
            this.party = PartyHelper.getParty();

        }

    }

}
