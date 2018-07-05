package eidolons.libgdx.gui.panels.headquarters.creation.general;

import eidolons.game.core.EUtils;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.creation.general.HcDeitySelectionPanel.HcDeityElement;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.GuiEventType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 7/4/2018.
 */
public class HcDeitySelectionPanel extends SelectionTable<HcDeityElement> {
    public HcDeitySelectionPanel( ) {
        super(4, 4);
        EUtils.bind(GuiEventType.HC_DEITY_ASPECT_CHOSEN, p -> {
            setUserObject(p.get());
        });
    }

    @Override
    protected GuiEventType getEvent() {
        return null;
    }

    @Override
    protected PROPERTY getProperty() {
        return G_PROPS.DEITY;
    }

    @Override
    protected SelectableItemData[] initDataArray() {
        List<ObjType> types = DataManager.getFilteredTypes(DC_TYPE.DEITIES, getUserObject().toString(), G_PROPS.ASPECT);
        return types.stream().map(type ->  new SelectableItemData(type.getName(), type))
         .collect(Collectors.toList()).toArray(new SelectableItemData[types.size()]);
    }

    @Override
    protected HcDeityElement createElement(SelectableItemData datum) {
        return new HcDeityElement(datum.getEntity());
    }

    @Override
    protected HcDeityElement[] initActorArray() {
        return new HcDeityElement[size];
    }

    public class HcDeityElement extends TablePanelX{
        private final Entity deity;

        public HcDeityElement(Entity entity) {
            deity = entity;
            add(new ImageContainer(entity.getEmblemPath()));

            add(new TextButtonX(entity.getName(),
             StyleHolder.getHqTextButtonStyle(20)));

            add(new ImageContainer(entity.getEmblemPath()));
        }
    }
}
