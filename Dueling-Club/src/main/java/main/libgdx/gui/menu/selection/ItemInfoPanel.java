package main.libgdx.gui.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 11/29/2017.
 */
public class ItemInfoPanel extends TablePanel{

    protected  DescriptionPanel description;
    protected  Image preview;
    protected  Image fullsizePortrait;
    protected Label title;
    protected SelectableItemData item;

    public ItemInfoPanel(SelectableItemData item) {
        //bg
        setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));
//        setSize(700, 700);
        description = new DescriptionPanel();
        description.setText(getDefaultText());
        title = new Label(getDefaultTitle(), StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 30));
        preview = new Image(TextureCache.getOrCreateR(getEmptyImagePath()));
        fullsizePortrait =
         new Image(TextureCache.getOrCreateR(getEmptyImagePathFullSize()));

        TablePanel<Actor> header = new TablePanel<>();
        initHeader(header);
        
//        TablePanel<Actor> centered = new TablePanel<>();
//        centered.addNoGrow(header).  center().padLeft(100);//.height(128);

        addElement(header).  left().padTop(30). maxWidth(700).maxHeight(700);
        row();

       

        addElement(description).left().padLeft(30) ;
        addNormalSize(fullsizePortrait).right().padBottom(70).padRight(25) ;

        if (item!=null )
        setItem(item);
    }

    protected void initHeader(TablePanel<Actor> header) {
        header. addNormalSize(preview).left().padLeft(40) ;
        header.addElement(title). right() ; 
    
    }

    protected boolean isRandomDefault() {
        return false;
    }
        protected String getDefaultTitle() {
        if (isRandomDefault())
            return "Random";
        return "No item selected";
    }
    protected String getDefaultText() {
        if (isRandomDefault())
            return "How much do you trust in your luck?";
        return "Select an item";
    }

    protected String getBackgroundPath() {
        return StrPathBuilder.build(
         "UI" ,
          "components" ,
          "2017" ,
          "dialog" ,
          "log" ,
          "background.png" );
//        return VISUALS.MAIN.getImgPath();
    }


    protected String getEmptyImagePath() {
        return ImageManager.getEmptyUnitIconPath();
    }

    protected String getEmptyImagePathFullSize() {
        return ImageManager.getEmptyUnitIconFullSizePath();
    }
    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        description.setText(item.description);
        if (item.imagePath==null )
            preview.setDrawable(null);
        else
        preview.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(item.imagePath));

        if (item.previewImagePath==null )
            fullsizePortrait.setDrawable(null);
        else
            fullsizePortrait.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(
         item.previewImagePath));

        title.setText(getTitle());
        pack();
    }

    protected String getTitle() {
        return item.name;
    }

    public void setItem(SelectableItemData item) {
        this.item = item;
        updateRequired = true;
    }

    public SelectableItemData getItem() {
        return item;
    }
}
