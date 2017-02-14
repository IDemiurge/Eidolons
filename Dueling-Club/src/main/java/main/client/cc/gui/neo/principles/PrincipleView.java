package main.client.cc.gui.neo.principles;

import main.client.cc.gui.MainPanel;
import main.client.cc.gui.neo.HeroPanel;
import main.content.CONTENT_CONSTS.PRINCIPLES;
import main.entity.obj.unit.DC_HeroObj;
import main.swing.generic.components.G_Panel;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.GuiManager;

import java.awt.*;

public class PrincipleView extends G_Panel {

    private static Font defaultFont;
    PrinciplePanel panel;
    PrincipleInfoPanel infoPanel;
    PrincipleMiddlePanel middlePanel;
    PrincipleItemPanel itemPanel;
    private PRINCIPLES principle;
    private DC_HeroObj hero;
    private PrincipleDescriptionPanel descriptionPanel;

    public PrincipleView(DC_HeroObj hero) {
        this.setHero(hero);
    }

    public static Font getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = FontMaster.getFont(FONT.AVQ, 22, Font.PLAIN);
        }
        return defaultFont;
    }

    public void reset() {
        panel.reset();

    }

    public void init() {
        panel = new PrinciplePanel(getHero(), this);
        descriptionPanel = new PrincipleDescriptionPanel();
        infoPanel = new PrincipleInfoPanel(getHero());
        middlePanel = new PrincipleMiddlePanel(getHero());
        itemPanel = new PrincipleItemPanel(getHero());
        panel.init();
        infoPanel.init();
        middlePanel.init();
        descriptionPanel.init();
        itemPanel.init();
        add(panel, "id panel, pos 0 0");
        add(descriptionPanel, "id descriptionPanel, pos panel.x2 0");
        add(middlePanel, "id middlePanel, pos panel.x2 descriptionPanel.y2");
        add(itemPanel, "id itemPanel, pos middlePanel.x2 0");
        add(infoPanel, "id infoPanel, pos panel.x2 middlePanel.y2");
        panelSize = new Dimension(GuiManager.getScreenWidthInt()
                - HeroPanel.HERO_PANEL_FRAME.getWidth(), MainPanel.MAIN_PANEL_HEIGHT);
    }

    public void principleSelected(PRINCIPLES principle) {
        this.setPrinciple(principle);
        panel.setPrinciple(principle);
        infoPanel.setPrinciple(principle);
        descriptionPanel.setPrinciple(principle);
        itemPanel.setPrinciple(principle);
        refresh();
    }

    public void refresh() {
        panel.refresh();
        infoPanel.refresh();
        middlePanel.refresh();
        itemPanel.refresh();
    }

    public PRINCIPLES getPrinciple() {
        return principle;
    }

    public void setPrinciple(PRINCIPLES principle) {
        this.principle = principle;
    }

    public DC_HeroObj getHero() {
        return hero;
    }

    public void setHero(DC_HeroObj hero) {
        this.hero = hero;
    }

    public PrinciplePanel getPanel() {
        return panel;
    }

    public PrincipleInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public PrincipleMiddlePanel getMiddlePanel() {
        return middlePanel;
    }

    public PrincipleItemPanel getItemPanel() {
        return itemPanel;
    }

    public PrincipleDescriptionPanel getDescriptionPanel() {
        return descriptionPanel;
    }

}
