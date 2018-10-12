package eidolons.libgdx.gui.panels.headquarters;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.UnitDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HERO_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/25/2018.
 */
public class HqButtonPanel extends HqElement {
    @Override
    protected void update(float delta) {

    }
    private void saveAndExit() {
        HqDataMaster.saveHero(dataSource.getEntity());
        HqMaster.closeHqPanel();
    }
    private void saveType() {
        HqDataMaster.saveHero(dataSource.getEntity(), true, false);
    }
    private void saveTypeNew() {
        HqDataMaster.saveHero(dataSource.getEntity(), true, true);
    }

    private void viewInfo() {
        GuiEventManager.trigger(GuiEventType.SHOW_UNIT_INFO_PANEL, new UnitDataSource(dataSource.getEntity()));
    }
    private void undoAll() {
        HqDataMaster.undoAll(dataSource.getEntity());
    }
    private void levelUp() {
        HqDataMaster.operation(dataSource, HERO_OPERATION.LEVEL_UP);
    }
    public HqButtonPanel() {
        if (!CoreEngine.isJar())
        {
            add(new SmartButton("Level Up", STD_BUTTON.MENU, () -> {  levelUp();}));
            add(new SmartButton("Save Type", STD_BUTTON.MENU, () -> { saveType();}));
            add(new SmartButton("Save as New", STD_BUTTON.MENU, () -> { saveTypeNew();}));
        } else
            {
            add(new SmartButton("View Info", STD_BUTTON.MENU, () -> {
                viewInfo();
            }));
        }
        add(new SmartButton("Undo All", STD_BUTTON.MENU, () -> {
            undoAll();
        }));
        add(new SmartButton("Save and Exit", STD_BUTTON.MENU, () -> {
            saveAndExit();
        }));
    }

}
