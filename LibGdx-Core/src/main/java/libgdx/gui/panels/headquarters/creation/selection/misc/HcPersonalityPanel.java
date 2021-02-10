package libgdx.gui.panels.headquarters.creation.selection.misc;

import libgdx.StyleHolder;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.headquarters.creation.HeroCreationMaster;
import libgdx.gui.panels.headquarters.creation.selection.SelectionTable;
import eidolons.system.audio.DC_SoundMaster;
import libgdx.gui.generic.btn.ButtonStyled;
import main.content.enums.entity.HeroEnums.GENDER;
import main.content.enums.entity.HeroEnums.HERO_SOUNDSET;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;

import java.util.Arrays;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcPersonalityPanel extends SelectionTable<SmartTextButton> {


    public HcPersonalityPanel() {
        super(2, 10);
    }

    @Override
    protected GuiEventType getEvent() {
        return null; //sound
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        return Arrays.stream(HERO_SOUNDSET.values()).
                filter(soundset -> soundset.isFemale() == (
                        HeroCreationMaster.getModel().getGender() == GENDER.FEMALE))
                .map(type -> new SelectableItemData(type.getName(), type.getName())).toArray(SelectableItemData[]::new);

    }

    @Override
    public void init() {
        super.init();
        if (selectedData!=null )
        for (SmartTextButton actor : actors) {
            if (selectedData.getName().equalsIgnoreCase((actor.getText().toString()))) {
                actor.setChecked(true);
            }
        }
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.SOUNDSET;
    }

    @Override
    protected SmartTextButton createElement(SelectableItemData datum) {
        return new SmartTextButton(datum.getName(), StyleHolder.getHqTextButtonStyle(ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT, 24),
         () -> {

             selectedData=datum;
             selected(datum);
             DC_SoundMaster.playRandomSound(
              new EnumMaster<HERO_SOUNDSET>().retrieveEnumConst(HERO_SOUNDSET.class, datum.getName()));
         }, ButtonStyled.STD_BUTTON.MENU);
    }

    @Override
    protected SmartTextButton[] initActorArray() {
        return new SmartTextButton[size];
    }
}
