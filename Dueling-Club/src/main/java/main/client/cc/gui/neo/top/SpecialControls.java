package main.client.cc.gui.neo.top;

import main.client.cc.HC_Master;
import main.client.cc.gui.MainViewPanel;
import main.client.cc.gui.MainViewPanel.HERO_VIEWS;
import main.entity.obj.DC_HeroObj;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Panel;

import java.awt.*;

public class SpecialControls extends G_Panel {

    CustomButton toggleTreeViewButton;
    private DC_HeroObj hero;
    private MainViewPanel mvp;
    private CustomButton toggleAltModeButton;
    private CustomButton toggleT3ViewButton;

    public SpecialControls(DC_HeroObj hero, final MainViewPanel mvp) {
        this.hero = hero;
        this.mvp = mvp;
        panelSize = new Dimension(180, 32);
        toggleTreeViewButton = new CustomButton(VISUALS.GEARS) {
            public void handleClick() {
                mvp.toggleTreeView();
            }
        };
        toggleAltModeButton = new CustomButton(VISUALS.MAP_VIEW_BUTTON) {
            public void handleClick() {
                mvp.toggleAltMode();
            }
        };
        toggleT3ViewButton = new CustomButton(VISUALS.CHAR_BUTTON) {
            public void handleClick() {
                HC_Master.toggleT3View();
            }
        };
    }

    public void refresh() {
        removeAll();
        add(toggleT3ViewButton, "id toggleT3ViewButton,pos 0 0");
        if (mvp.getCurrentView() == HERO_VIEWS.SKILLS || mvp.getCurrentView() == HERO_VIEWS.CLASSES) {
            add(toggleTreeViewButton, "id treeView, pos toggleT3ViewButton.x2+5 0 ");
            add(toggleAltModeButton, "id toggleAltModeButton,pos treeView.x2+5 0");
        }
        revalidate();
    }
}
