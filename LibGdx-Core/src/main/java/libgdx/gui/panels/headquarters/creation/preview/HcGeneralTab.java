package libgdx.gui.panels.headquarters.creation.preview;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.content.PARAMS;
import libgdx.StyleHolder;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.panels.TablePanelX;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.panels.headquarters.creation.HeroCreationWorkspace;
import libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.panels.headquarters.hero.HqParamPanel;
import libgdx.gui.panels.headquarters.hero.HqScrolledValuePanel;
import libgdx.gui.panels.headquarters.hero.HqVerticalValueTable;
import libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import main.content.values.properties.G_PROPS;

/**
 * Created by JustMe on 7/3/2018.
 */
public class HcGeneralTab extends HqElement {

    ValueContainer name;
    FadeImageContainer portrait;
    HqVerticalValueTable mainValues;
    HqParamPanel dynamicParamPanel;
    HqParamPanel paramPanel;
    HqAttributeTable attributeTable;
    HqScrolledValuePanel scrolledValuePanel;

    HqVerticalValueTable deity;
    HqVerticalValueTable classes;
    HqVerticalValueTable identity;

    public HcGeneralTab() {
        TablePanelX column1 = new TablePanelX<>();
        TablePanelX column2 = new TablePanelX<>();

        //        column1.add(classes=new HqVerticalValueTable(PROPS.CLASSES)).row();
        //        column1.add(identity=new HqVerticalValueTable(G_PROPS.RACE, PARAMS.LEVEL)).row();

        //        column1.add(attributeTable = new HqAttributeTable()).row();


        column1.add(portrait = new FadeImageContainer()).row();
        column1.add(name = new ValueContainer(StyleHolder.getHqLabelStyle(20), "", "")).row();
        column1.add(mainValues = new HqVerticalValueTable(G_PROPS.BACKGROUND, PARAMS.LEVEL){
            @Override
            protected Drawable getDefaultBackground() {
                return null;
            }
        }).row();
        column1.add(dynamicParamPanel = new HqParamPanel(true)).row();
        column1.add(paramPanel = new HqParamPanel(false)).row();
        column1.add(deity = new HqVerticalValueTable(G_PROPS.DEITY)).row();
        deity.setColor(new Color(1, 1, 1, 0));
        column1.add(scrolledValuePanel = new HqScrolledValuePanel());

        add(column1).width(HeroCreationWorkspace.PREVIEW_WIDTH).height(HeroCreationWorkspace.PREVIEW_HEIGHT);
        //        add(column2).width(HqTabs.WIDTH/2);

        mainValues.setDisplayPropNames(false);
        mainValues.setDisplayColumn(false);
    }

    @Override
    protected void update(float delta) {
        HqHeroDataSource dataSource = getUserObject();
        portrait.setImage(dataSource.getImagePath());
        name.setNameText(dataSource.getName());
        if (dataSource.getEntity().getDeity() != null)
            deity.fadeIn();
        else {
            deity.setColor(new Color(1, 1, 1, 0));
        }
        //        if ( StringMaster.
        //         getAppendedImageFile(dataSource.getImagePath(), " m")!=null ){
        //            portrait.setImage(StringMaster.
        //             getAppendedImageFile(dataSource.getImagePath(), " m"));
        //            return;
        //        }
        //        Texture texture = GdxImageMaster.size(StringMaster.
        //         getAppendedImageFile(dataSource.getImagePath(), " full"), 250, 350, false);
        //        portrait.setImage(
        //         new Image(texture));
        //the rest will self-update on setUserObject
    }

}
