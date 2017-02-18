package main.client.battle.gui;

import main.entity.obj.unit.Unit;
import main.game.core.game.DC_Game;
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
                     Collection<Unit> dequeImpl) {

    }

    public class SlainPagedListPanel extends G_PagedListPanel<Unit> {

        public SlainPagedListPanel(int pageSize, boolean vertical, int version) {
            super(pageSize, vertical, version);
        }

        @Override
        protected G_Component createPageComponent(List<Unit> list) {
            return null;
        }

        @Override
        protected List<List<Unit>> getPageData() {
            return null;
        }
    }

    public class SlainComponent extends G_Panel {

        public SlainComponent(Unit unit) {

        }

    }
}
