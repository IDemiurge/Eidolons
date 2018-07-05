package eidolons.libgdx.gui.panels.headquarters.creation.misc;

import eidolons.game.core.EUtils;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.headquarters.creation.general.SelectionTable;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.system.text.NameMaster;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.type.ObjType;
import main.system.GuiEventType;

/**
 * Created by JustMe on 6/25/2018.
 */
public class HcNamePanel extends SelectionTable<TextButtonX> {

    public HcNamePanel() {
        super(3, 36);
        add(new TextButtonX("Randomize", STD_BUTTON.MENU, () -> randomize())).colspan(3);
        EUtils.bind(GuiEventType.HC_GENDER_CHOSEN, p -> {
             randomize();
        });
    }

    private void randomize() {
        updateAct(0);
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        data = new SelectableItemData[size];
        HqHeroDataSource dataSource = (HqHeroDataSource) getUserObject();
        ObjType type = dataSource.getEntity().getType();
        for (int i = 0; i < size; i++) {
            data[i] = new SelectableItemData(
             NameMaster.generateNewHeroName(type), type);
        }
        return data;
    }

    @Override
    protected TextButtonX createElement(SelectableItemData datum) {
        return new TextButtonX(datum.getName(), STD_BUTTON.MENU, () -> selected(datum));
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.NAME;
    }


    @Override
    protected TextButtonX[] initActorArray() {
        return new TextButtonX[size];
    }
}
