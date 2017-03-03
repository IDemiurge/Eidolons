package main.client.cc.gui.neo.choice;

import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.secondary.InfoMaster;

public class ScenarioChoiceView extends EntityChoiceView {
    private static final int MAX_SIZE = 12;
    private static final String SCENARIO = "Scenario";

    public ScenarioChoiceView() {
        super(null, null);
    }

    protected int getPageSize() {
        return Math.min(MAX_SIZE, data.size());
    }

    protected int getColumnsCount() {
        return 2;
    }

    @Override
    protected void initData() {
        data = DataManager.getTypesGroup(MACRO_OBJ_TYPES.MISSIONS, SCENARIO);
    }

    @Override
    public void itemSelected(ObjType i) {
        super.itemSelected(i);
        // setVisuals(new CompVisuals(GuiManager.DEF_DIMENSION,
        // getSelectedItem().getProperty(
        // PROPS.MAP_BACKGROUND)));
        // init();
        // refresh();
    }

    @Override
    protected void applyChoice() {
        // TODO
    }

    @Override
    public boolean checkBlocked(ObjType e) {
        // TODO
        return super.checkBlocked(e);
    }

    @Override
    protected void addControls() {
        // TODO Auto-generated method stub

        super.addControls();
    }

    protected boolean isReady() {
        return false;
    }

    protected void addInfoPanels() {
        return;
    }

    ;

    @Override
    protected PROPERTY getPROP() {
        return null;
    }

    @Override
    protected VALUE getFilterValue() {
        return G_PROPS.GROUP;
    }

    @Override
    protected OBJ_TYPE getTYPE() {
        return MACRO_OBJ_TYPES.MISSIONS;
    }

    @Override
    public String getInfo() {
        if (ListMaster.isNotEmpty(data)) {
            if (getSelectedIndex() >= 0) {
                return getSelectedItem().getName();
            }
        }
        return InfoMaster.CHOOSE_SCENARIO;
    }

}
