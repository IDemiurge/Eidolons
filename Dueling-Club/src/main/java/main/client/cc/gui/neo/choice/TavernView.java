package main.client.cc.gui.neo.choice;

import main.content.PARAMS;
import main.content.values.parameters.MACRO_PARAMS;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.meta.scenario.ScenarioPrecombatMaster;
import main.game.module.adventure.MacroManager;
import main.game.module.adventure.town.Tavern;
import main.game.module.adventure.town.TavernMaster;
import main.swing.components.buttons.CustomButton;
import main.swing.components.panels.page.info.element.TextCompDC;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.system.threading.Weaver;

import java.util.ArrayList;
import java.util.List;

public class TavernView extends HeroChoiceView<Unit> {

    private Tavern tavern;

    public TavernView(Tavern tavern, ChoiceSequence sequence) {
        super(sequence, MacroManager.getActiveParty().getLeader());
        this.tavern = tavern;
    }

    protected int getPageSize() {
        return 6;
        // tavern.getMaxHeroes()
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    public boolean checkBlocked(Unit e) {
        if (!TavernMaster.checkCanHire(MacroManager.getActiveParty(), hero, tavern, e)) {
            return false;
        }
        return hero.getIntParam(PARAMS.GOLD) > e.getIntParam(MACRO_PARAMS.HIRE_COST);
    }

    @Override
    protected String getPagePosY() {
        return "patrons.y2";
    }

    @Override
    protected String getInfoHeaderPosition() {
        // TODO
        return "id info, @pos center_x max_top";
    }

    @SuppressWarnings("serial")
    protected void addSelectionPages() {
        // TODO background, text panel,
        pages = createSelectionComponent();
        pages.setData(data);
        pages.refresh();
        String PAGE_POS = "id pages, pos " + getPagePosX() + " " + getPagePosY();
        TextCompDC patrons = new TextCompDC(VISUALS.PROP_BOX, tavern.getName() + "'s patrons", 18);
        add(patrons, "id patrons, pos " + getPagePosX() + " info.y2");
        CustomButton buyProvisions = new DialogueButton(VISUALS.BUTTON, "Buy Provisions",
                "How much supplies do you need?", "All you have", "All that "
                + MacroManager.getActiveParty().getSharedGold() + " can buy",
                "None, actually") {
            protected void processChoice(Boolean waitForInput) {
                if (waitForInput != null) {
                    tavern.buyProvisions(waitForInput);
                }
            }
        };
        add(buyProvisions, "id buyProvisions, pos 0 0");
        CustomButton rentRooms = new DialogueButton(VISUALS.BUTTON, "Rent Rooms",
                getRentRoomsInfoText(), "Just Tonight", "As needed", "Never mind") {
            protected void processChoice(Boolean waitForInput) {
                if (waitForInput != null) {
                    tavern.rentRooms(waitForInput);
                }
            }
        };
        add(rentRooms, "id rentRooms, pos 0 buyProvisions.y2");
        add(pages, PAGE_POS); // perhaps a plain list instead?
        // add Buy Provisions, Drinks and Rent Rooms
    }

    private String getRentRoomsInfoText() {
        return "We've got just the right accomodations for"
                + (MacroManager.getActiveParty().getMembers().size() > 1 ? (" the "
                + MacroManager.getActiveParty().getMembers().size() + " of") : "")
                + " you! How long will you be staying? We normally charge payment upfront...";
    }

    @Override
    protected float getInfoFontSize() {
        return 18;
    }

    @Override
    public String getInfo() {
        return "Welcome to " + tavern.getName() + "! Here for a drink, a meal or a good company? "
                + "We have excellent rooms as well if you are looking for accomodations...";
    }

    public void itemIndexSelected(int i) {
        this.setIndex(i);
        Unit e = data.get(getSelectedIndex());
        if (e instanceof Entity && infoPanel != null) {
            infoPanel.setEntity((Entity) e);
            infoPanel.refresh();
        }
        if (!isOkBlocked()) {
            okButton.setVisuals(VISUALS.FORWARD); // "hire"
        } else {
            okButton.setVisuals(VISUALS.FORWARD_BLOCKED);
        }

    }

    public void itemSelected(Unit i) {
        itemIndexSelected(data.indexOf(i));
        // init dialogue or set description text with Gold Share and other hire
        // infos
    }

    protected boolean isReady() {
        return false;
    }

    @Override
    protected void initData() {
        data = getData();
    }

    public List<Unit> getData() {
        if (tavern == null) {
            if (ScenarioPrecombatMaster.getScenario() != null) {

                return ScenarioPrecombatMaster.getHeroesForHire();
            }
            return new ArrayList<>();
        }
        return tavern.getHeroesForHire();
    }

    protected void addInfoPanels() {
        super.addInfoPanels();
    }

    protected void addControls() {
        // TODO
        super.addControls();
    }

    public enum TOWN_PLACE_FUNCTIONS {

    }

    abstract class DialogueButton extends CustomButton {
        private String info;
        private String TRUE;
        private String FALSE;
        private String NULL;

        public DialogueButton(VISUALS v, String text, String info, String TRUE, String FALSE,
                              String NULL) {
            super(v, text);
            this.info = info;
            this.FALSE = FALSE;
            this.TRUE = TRUE;
            this.NULL = NULL;
        }

        public void handleClick() {
            DialogMaster.ask(info, true, TRUE, FALSE, NULL);
            Weaver.inNewThread(new Runnable() {
                public void run() {
                    processChoice((Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION));

                }
            });
        }

        protected abstract void processChoice(Boolean waitForInput);
    }

}
