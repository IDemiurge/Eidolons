package main.client.cc.gui.neo.choice;

import main.game.logic.dungeon.scenario.ScenarioMaster.SCENARIO_MODES;

import javax.swing.*;

public class ScenarioModeChoiceView extends EnumChoiceView<SCENARIO_MODES> implements
        ListCellRenderer<SCENARIO_MODES> {
    public ScenarioModeChoiceView(ChoiceSequence sequence, Class<SCENARIO_MODES> CLASS) {
        super(sequence, null, CLASS);
    }

    @Override
    public String getInfo() {
        return "Choose Mode for Scenario";
    }

    protected void applyChoice() {

    }

    @Override
    protected int getColumnsCount() {
        return 1;
    }

    public boolean checkBlocked(SCENARIO_MODES value) {
        //
        return false;

    }

}
