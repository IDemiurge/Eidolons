package eidolons.libgdx.gui.panels.headquarters.creation.selection.misc;

import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import eidolons.libgdx.gui.panels.headquarters.creation.selection.SelectionImageTable;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
import main.system.images.ImageManager;

import java.util.stream.Collectors;

/**
 * Created by JustMe on 6/25/2018.
 */
public class HcPortraitPanel extends SelectionImageTable {
    public static final boolean FULL_PORTRAITS = true;
    private static final int SIZE = 24;

    public HcPortraitPanel() {
        super(4, SIZE, 0);
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        if (HeroCreationMaster.getModel().
         getBackground() == null) {
            return new SelectableItemData[0];
        }
        return ImageManager.getPortraitsForBackground(HeroCreationMaster.getModel().
         getBackground().toString(), false).stream().filter(this::checkPortrait).map(portrait ->
         new SelectableItemData(portrait, portrait))
         .limit(SIZE).
          collect(Collectors.toList()).toArray(new SelectableItemData[1]);
    }

    private boolean checkPortrait(String portrait) {
        return
         (HeroCreationMaster.getModel().getGender() == GENDER.FEMALE)
          == portrait.toLowerCase().contains("w_");
    }

    @Override
    protected GuiEventType getEvent() {
        return GuiEventType.HC_PORTRAIT_CHOSEN;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.IMAGE;
    }
}
