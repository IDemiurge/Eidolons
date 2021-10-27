package libgdx.gui.dungeon.panels.headquarters;

import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.panels.dc.unitinfo.datasource.UnitDataSource;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.generic.btn.ButtonStyled;
import eidolons.system.libgdx.datasource.HeroDataModel;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.Flags;

/**
 * Created by JustMe on 4/25/2018.
 */
public class HqButtonPanel extends HqElement {
    public HqButtonPanel() {
        if (!Flags.isJar()) {
            add(new SmartTextButton("Level Up", ButtonStyled.STD_BUTTON.MENU, this::levelUp));
            add(new SmartTextButton("Save Type", ButtonStyled.STD_BUTTON.MENU, this::saveType));
            add(new SmartTextButton("Save as New", ButtonStyled.STD_BUTTON.MENU, this::saveTypeNew));
        }

            add(new SmartTextButton("View Info", ButtonStyled.STD_BUTTON.MENU, this::viewInfo));


        if (!HqDataMaster.isSimulationOff())
        add(new SmartTextButton("Undo All", ButtonStyled.STD_BUTTON.MENU, this::undoAll));


        add(new SmartTextButton("Done", ButtonStyled.STD_BUTTON.MENU, this::saveAndExit));
    }

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
        HqDataMaster.operation(dataSource, HeroDataModel.HERO_OPERATION.LEVEL_UP);
    }

}
