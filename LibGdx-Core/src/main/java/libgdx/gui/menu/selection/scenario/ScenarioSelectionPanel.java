package libgdx.gui.menu.selection.scenario;

import eidolons.content.consts.VisualEnums;
import eidolons.game.netherflame.main.IntroLauncher;
import libgdx.gui.menu.selection.ItemListPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.menu.selection.SelectableItemDisplayer;
import libgdx.gui.menu.selection.SelectionPanel;
import eidolons.system.libgdx.datasource.ScreenData;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 11/30/2017.
 */
public class ScenarioSelectionPanel extends SelectionPanel {
    Supplier<List<? extends Entity>> dataSupplier;


    public ScenarioSelectionPanel(Supplier<List<? extends Entity>> dataSupplier) {
        super();
        this.dataSupplier = dataSupplier;
        init();
    }

    @Override
    public void tryDone() {
        super.tryDone();
    }

    @Override
    public void init() {
        super.init();
    }

    protected String getDoneText() {
        return "Next";
    }

    protected String getTitle() {
        return "Select a Scenario";
    }

    @Override
    public void closed(Object selection) {
        fadeOut();
        if (selection == null) {
            return;
        }

        scenarioChosen(DataManager.getType(selection.toString(), DC_TYPE.SCENARIOS));
        listPanel.deselect();
    }

    protected void scenarioChosen(ObjType type) {
        main.system.auxiliary.log.LogMaster.log(1,"*** Scenario chosen: " +type);
        ScreenData screenData = new ScreenData(VisualEnums.SCREEN_TYPE.DUNGEON, type.getName());
        if (IntroLauncher.isDemo(type))
            screenData.setParam(new EventCallbackParam(type));

//        CustomLaunch customLaunch = new CustomLaunch("");
//        customLaunch.setValue(CustomLaunch.CustomLaunchValue.xml_path,
//                type.getProperty(PROPS.SCENARIO_PATHS));
//        MainLauncher.setCustomLaunch(customLaunch);

        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                screenData);
    }

    @Override
    protected SelectableItemDisplayer createInfoPanel() {
        return new ScenarioInfoPanel(null);
    }

    protected boolean isReadyToBeInitialized() {
        return false;
    }

    @Override
    protected List<SelectableItemData> createListData() {
        return listPanel.toDataList(dataSupplier.get());
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ScenarioListPanel();
    }
}
