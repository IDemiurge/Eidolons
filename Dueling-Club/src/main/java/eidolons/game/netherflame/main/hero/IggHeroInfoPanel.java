package eidolons.game.netherflame.main.hero;

import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.menu.selection.ItemListPanel;
import eidolons.libgdx.gui.menu.selection.hero.HeroInfoPanel;
import main.system.sound.SoundMaster;

public class IggHeroInfoPanel extends HeroInfoPanel  {
    public IggHeroInfoPanel(ItemListPanel.SelectableItemData item) {
        super(item);
    }

    @Override
    public void layout() {
        super.layout();
    }

    @Override
    public void initStartButton(String text, Runnable runnable) {
        addActor(startButton = new SmartButton(text, ButtonStyled.STD_BUTTON.MENU, () -> runnable.run()) {
            @Override
            protected SoundMaster.BUTTON_SOUND_MAP getSoundMap() {
                return SoundMaster.BUTTON_SOUND_MAP.ENTER;
            }
        });
    }
}
