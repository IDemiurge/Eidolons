package main.client.battle.gui;

import main.entity.obj.DC_HeroObj;
import main.game.DC_Game;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.panels.G_PagedListPanel;

import java.util.Collection;
import java.util.List;

//should it be a full-scale VIEW? or an inner frame on top of Battlefield? 

//display: Glory stats (monsters killed - perhaps a visual list), rounds played, time played, 
// damage  taken/dealt (for each hero),  
// hall of glory tab perhaps with best battles "recorded" 
// controls - Continue to Hero Quarters, Exit to Main Menu

public class EndScreen {
    boolean victory;
    G_Panel mainPanel;
    CustomButton continueButton;
    CustomButton exitButton;

    public EndScreen(DC_Game game, boolean victory, int glory,
                     Collection<DC_HeroObj> dequeImpl) {

    }

    public class SlainPagedListPanel extends G_PagedListPanel<DC_HeroObj> {

        public SlainPagedListPanel(int pageSize, boolean vertical, int version) {
            super(pageSize, vertical, version);
        }

        @Override
        protected G_Component createPageComponent(List<DC_HeroObj> list) {
            return null;
        }

        @Override
        protected List<List<DC_HeroObj>> getPageData() {
            return null;
        }
    }

    public class SlainComponent extends G_Panel {

        public SlainComponent(DC_HeroObj unit) {

        }

    }
}
