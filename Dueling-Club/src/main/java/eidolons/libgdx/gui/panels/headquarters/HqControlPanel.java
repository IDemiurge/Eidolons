package eidolons.libgdx.gui.panels.headquarters;

import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqControlPanel extends HqElement{

    public HqControlPanel() {
//        setFixedSize(true);
        add(new TextButtonX( STD_BUTTON.OK, () -> {
            save();
        }));
        add(new TextButtonX("Level Up", STD_BUTTON.GAME_MENU, () -> {
            levelUp();
        }));
        add(new TextButtonX(  STD_BUTTON.CANCEL, () -> {
            close();
        }));
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

    private void levelUp() {
        HeroLevelManager.levelUp(dataSource.getEntity());
        modelChanged();
    }

    @Override
    protected void update(float delta) {

    }
}
