package eidolons.libgdx.gui.panels.headquarters;

import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/25/2018.
 */
public class HqButtonPanel extends HqElement {
    @Override
    protected void update(float delta) {

    }
    private void levelUp() {
        HeroLevelManager.levelUp(dataSource.getEntity());
        modelChanged();
    }
    public HqButtonPanel() {
        if (!CoreEngine.isJar())
        add(new TextButtonX("Level Up", STD_BUTTON.GAME_MENU, () -> {
            levelUp();
        }));
        add(new TextButtonX("Undo All", STD_BUTTON.GAME_MENU, () -> {
            levelUp();
        }));
        add(new TextButtonX("Recommended", STD_BUTTON.GAME_MENU, () -> {
            levelUp();
        }));
    }
}
