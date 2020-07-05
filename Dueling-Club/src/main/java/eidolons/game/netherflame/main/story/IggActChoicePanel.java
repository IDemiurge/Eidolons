package eidolons.game.netherflame.main.story;

import eidolons.game.EidolonsGame;
import eidolons.game.netherflame.main.NF_Images;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartTextButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.SelectableItemDisplayer;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioInfoPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioListPanel;
import eidolons.libgdx.gui.menu.selection.scenario.ScenarioSelectionPanel;
import eidolons.libgdx.launch.MainLauncher;
import eidolons.libgdx.screens.menu.MainMenu;
import main.entity.Entity;
import main.system.launch.Flags;
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
        return "";
    }

    public void tryDone() {
        if (EidolonsGame.SELECT_SCENARIO) {
            return;
        }
//        if (CoreEngine.isIDE()) //TODO for A/B testing?
        if (isAutoDoneEnabled())
                if (!MainLauncher.presetNumbers.isEmpty()) {
                    listPanel.select(MainLauncher.presetNumbers.pop());
                } else if (isRandom()) {
                    listPanel.selectRandomItem();
                } else
                    return;
        if (listPanel.getCurrentItem() == null || listPanel.isBlocked(listPanel.getCurrentItem())) {
            return;
        }
        done();
    }

    @Override
    public void init() {
        super.init();
        listPanel.setPosition(
                GdxMaster.centerWidthScreen(listPanel),
                GdxMaster.centerHeightScreen(listPanel));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        MainMenu.getInstance().setVisible(false);
        listPanel.setPosition(
                GdxMaster.centerWidthScreen(listPanel),
                GdxMaster.centerHeightScreen(listPanel));
    }

    @Override

    protected SelectableItemDisplayer createInfoPanel() {
        return new ScenarioInfoPanel(null ){
            @Override
            protected void afterLayout() {
                super.afterLayout();
//                startButton.setPosition(getBtnX(),
//                        TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
//                IggActChoicePanel.this.add(fullsizePortrait);
//                fullsizePortrait.setPosition(
//                        GdxMaster.centerWidth(fullsizePortrait),
//                        GdxMaster.centerHeight(fullsizePortrait));

                IggActChoicePanel.this.add(startButton);
                startButton.setPosition(
                        GdxMaster.centerWidth(startButton),
                        TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
//                debugAll();
                setVisible(false);
            }

            public void initStartButton(String text, Runnable runnable) {
                addActor(startButton = new SmartTextButton(text, ButtonStyled.STD_BUTTON.MENU, () -> runnable.run()) {
                    @Override
                    public SoundMaster.BUTTON_SOUND_MAP getSoundMap() {
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
                if (Flags.isIDE()){
                    if (MainLauncher.presetNumbersOn) {
                       return false;
                    }
                }
                return !item.getName().equalsIgnoreCase("introduction");
//                return !item.getName().equalsIgnoreCase("act i");
            }
        };
    }

    @Override
    protected List<ItemListPanel.SelectableItemData> createListData() {
        List<ItemListPanel.SelectableItemData> list = super.createListData();
        int i =0;
        for (ItemListPanel.SelectableItemData selectableItemData : list) {
            selectableItemData.setFullsizeImagePath(NF_Images.BRIEF_ART.values()[i++].getPath());
            selectableItemData.setDescription("Some enemies are resistant to your main weapon’s damage type. It is easier to defeat them using a different one!\n" +
                    "\nUse G to swap your weapon set or adjust your weapons manually in the quick-inventory menu (be mindful of using it in combat, it will consume a lot of ATB and expose you to Attacks of Opportunity). \n" +
                    "You may want to try using your spell on the next enemy too. " + i);
        }
        return list;
    }
}
