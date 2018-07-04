package eidolons.libgdx.gui.panels.headquarters.creation.preview;

import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.hero.HqParamPanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;
import eidolons.libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.HqTabs;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import main.content.values.properties.G_PROPS;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 7/3/2018.
 */
public class HcGeneralTab extends eidolons.libgdx.gui.panels.headquarters.HqElement {

    HqParamPanel dynamicParamPanel;
    HqParamPanel paramPanel;
    FadeImageContainer portrait;
    HqAttributeTable attributeTable;
    HqScrolledValuePanel scrolledValuePanel;

    HqVerticalValueTable mainValues;
    HqVerticalValueTable deity;
    HqVerticalValueTable classes;
    HqVerticalValueTable identity;

    public HcGeneralTab() {
        TablePanelX  column1 = new TablePanelX<>();
        TablePanelX  column2 = new TablePanelX<>();

        column1.add(mainValues=new HqVerticalValueTable(G_PROPS.RACE, PARAMS.LEVEL)).row();
        column1.add(deity=new HqVerticalValueTable(G_PROPS.DEITY)).row();
        column1.add(classes=new HqVerticalValueTable(PROPS.CLASSES)).row();
//        column1.add(identity=new HqVerticalValueTable(G_PROPS.RACE, PARAMS.LEVEL)).row();

        column1.add(attributeTable = new HqAttributeTable()).row();


        column2.add(portrait = new FadeImageContainer());
        column2.add(dynamicParamPanel = new HqParamPanel(true));
        column2.add(paramPanel = new HqParamPanel());
        column2.add(scrolledValuePanel = new HqScrolledValuePanel());

        add(column1).width(HqTabs.WIDTH/2);
        add(column2).width(HqTabs.WIDTH/2);
    }

    @Override
    protected void update(float delta) {
        HqHeroDataSource dataSource = getUserObject();

        portrait.setImage(StringMaster.
         getAppendedImageFile(dataSource.getImagePath(), " full"));
        //the rest will self-update on setUserObject
    }

}
