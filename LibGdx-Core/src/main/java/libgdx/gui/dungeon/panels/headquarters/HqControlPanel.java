package libgdx.gui.dungeon.panels.headquarters;

import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqControlPanel extends HqElement {

    public HqControlPanel() {
        add(new SymbolButton(ButtonStyled.STD_BUTTON.UNDO, HqDataMaster::undo));
        add(new SymbolButton(ButtonStyled.STD_BUTTON.OK, () -> {
            // if (!HqDataMaster.isSimulationOff())
            //     save();
            // else
            close();
        }));
        add(new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL, this::close));
    }

    private void save() {
        if (!HqDataMaster.getInstance(dataSource.getEntity().getHero()).isDirty())
            close();
        else
            HqDataMaster.saveHero(dataSource.getEntity());
    }

    private void close() {
        HqMaster.closeHqPanel();
        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO, dataSource.getEntity().getHero());
    }


    @Override
    protected void update(float delta) {

    }
}
