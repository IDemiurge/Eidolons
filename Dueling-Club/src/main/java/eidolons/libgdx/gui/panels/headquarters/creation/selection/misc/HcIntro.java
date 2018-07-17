package eidolons.libgdx.gui.panels.headquarters.creation.selection.misc;

import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.HcElement;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.DescriptionScroll;
import eidolons.system.text.TextMaster;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;

/**
 * Created by JustMe on 7/2/2018.
 *
 * recommended
 * random
 * choose preset
 */
public class HcIntro extends HcElement {
    private static final String RECOMMENDED = "Demo Fighter";

    private final DescriptionScroll description;

    public HcIntro() {
        super();
        add(description= new DescriptionScroll()).row();
        description.setUserObject(new SelectableItemData("Welcome!", getDescription(),
         getPreviewOne(), getPreviewTwo()));
//        add(new TextButtonX("Select Preset", STD_BUTTON.MENU, () ->selectPreset()));
        add(new TextButtonX("Random Preset", STD_BUTTON.MENU, () ->randomPreset())).row();
        add(new TextButtonX("Recommended", STD_BUTTON.MENU, () ->recommended()));


    }
public enum  SKETCHES{
        RAVEN,
    WOLF,
    BATS,
    DRAGONCREST,
    GRIFF,
    EAGLE,
    SHIP,
    CASTLE,
    }
    private String getPreviewOne() {
        return PathFinder.getSketchPath() + SKETCHES.RAVEN.name() + ".png";
    }
    private String getPreviewTwo() {
        return PathFinder.getSketchPath() + SKETCHES.BATS.name() + ".png";
    }

    private void recommended() {
        presetSelected(RECOMMENDED);
    }

    private void presetSelected(String recommended) {
        HeroCreationMaster.applyPresetType(DataManager.getType(recommended, DC_TYPE.CHARS));

    }

    private void randomPreset() {
        presetSelected(DataManager.getRandomType(DC_TYPE.CHARS, "Preset").getName());
    }

    private String getDescription() {
        return TextMaster.readResource("manual", "hc intro.txt");
    }

    private void selectPreset() {
//        presetChoicePanel.fadeIn();
    }

    @Override
    protected void update(float delta) {

    }
}
