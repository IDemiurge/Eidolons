package libgdx.gui.panels.headquarters.creation.selection.misc;

import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.headquarters.creation.HcElement;
import libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import libgdx.gui.panels.headquarters.creation.selection.DescriptionScroll;
import eidolons.system.text.TextMaster;
import libgdx.gui.generic.btn.ButtonStyled;
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

    public HcIntro() {
        super();
        DescriptionScroll description;
        add(description = new DescriptionScroll()).row();
        description.setUserObject(new SelectableItemData("Welcome!", getDescription(),
         getPreviewOne(), getPreviewTwo()));
//        add(new TextButtonX("Select Preset", STD_BUTTON.MENU, () ->selectPreset()));
        add(new SmartTextButton("Random Preset", ButtonStyled.STD_BUTTON.MENU, this::randomPreset)).row();
        add(new SmartTextButton("Recommended", ButtonStyled.STD_BUTTON.MENU, this::recommended));


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
