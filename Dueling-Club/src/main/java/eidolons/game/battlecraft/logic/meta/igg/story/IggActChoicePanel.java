package eidolons.game.battlecraft.logic.meta.igg.story;

import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioInfoPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioListPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import main.entity.Entity;
import main.system.sound.SoundMaster;

import java.util.List;
import java.util.function.Supplier;

public class IggActChoicePanel extends ScenarioSelectionPanel {
    public IggActChoicePanel(Supplier<List<? extends Entity>> dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected String getDoneText() {
        return "Onward!";
    }

    @Override
    protected String getTitle() {
        return "The Journey begins";
    }

    @Override

    protected SelectableItemDisplayer createInfoPanel() {
        return new ScenarioInfoPanel(null ){
            @Override
            protected void afterLayout() {
                super.afterLayout();
                startButton.setPosition(getBtnX(),
                        TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
            }

            public void initStartButton(String text, Runnable runnable) {
                addActor(startButton = new SmartButton(text, ButtonStyled.STD_BUTTON.MENU, () -> runnable.run()) {
                    @Override
                    protected SoundMaster.BUTTON_SOUND_MAP getSoundMap() {
                        return SoundMaster.BUTTON_SOUND_MAP.ENTER;
                    }
                });
                startButton.setPosition(getBtnX(),
                        TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
            }
        };




    }

    private float getBtnX() {
        return getWidth()/2;
    }

    @Override
    protected ItemListPanel createListPanel() {
        return new ScenarioListPanel(){
            @Override
            public boolean isBlocked(SelectableItemData item) {
                return !item.getName().equalsIgnoreCase("act i");
            }
        };
    }

    @Override
    protected List<ItemListPanel.SelectableItemData> createListData() {
        return super.createListData();
    }
}
