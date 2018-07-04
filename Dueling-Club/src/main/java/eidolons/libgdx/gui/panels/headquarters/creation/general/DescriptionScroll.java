package eidolons.libgdx.gui.panels.headquarters.creation.general;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 7/3/2018.
 */
public class DescriptionScroll extends TablePanelX{

    private static final String SCROLL = Images.HC_SCROLL_BACKGROUND;

    DescriptionPanel description;
    FadeImageContainer preview;
    FadeImageContainer preview2;

    public DescriptionScroll() {
        super();
         setBackground(new TextureRegionDrawable(TextureCache.getOrCreateR(SCROLL)));
        TablePanelX previews = new TablePanelX( );
        previews.add(preview= new FadeImageContainer()).top().left().row();
        previews.add(preview2= new FadeImageContainer()).bottom().left() ;
        add(previews).growY();

        add(description = new DescriptionPanel());
    }

    @Override
    public void updateAct(float delta) {
        SelectableItemData data = (SelectableItemData) getUserObject();

        description.setText(data.getDescription());
        preview.setImage(data.getImagePath());
        preview2.setImage(data.getPreviewImagePath());
        super.updateAct(delta);
    }
}
