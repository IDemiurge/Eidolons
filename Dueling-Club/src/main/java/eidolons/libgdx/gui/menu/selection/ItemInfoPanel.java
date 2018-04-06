package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 11/29/2017.
 */
public class ItemInfoPanel extends TablePanel {


    protected DescriptionPanel description;
    protected Image preview;
    protected Image fullsizePortrait;
    protected Label title;
    protected SelectableItemData item;

    public ItemInfoPanel(SelectableItemData item) {
        //bg
        initBg();
        initSize();
        initComponents();
        TablePanel<Actor> header = new TablePanel<>();
        initHeader(header);

//        TablePanel<Actor> centered = new TablePanel<>();
//        centered.addNoGrow(header).  center().padLeft(100);//.height(128);

        addElement(header).left().padTop(35)
//         .maxWidth(700).maxHeight(700)
        ;
        row();
        if (description != null)
            addElement(description).left().padLeft(30);
        if (fullsizePortrait != null) {
            addNormalSize(fullsizePortrait).right().padBottom(75).padRight(35);
            fullsizePortrait.setZIndex(0);
        }
        if (item != null)
            setItem(item);
    }

    protected void initComponents() {
        description = new DescriptionPanel();
        description.setText(getDefaultText());
        title = new Label(getDefaultTitle(), StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 30));
        preview = new Image(TextureCache.getOrCreateR(getEmptyImagePath()));
        fullsizePortrait =
         new Image(TextureCache.getOrCreateR(getEmptyImagePathFullSize()));


    }

    protected void initSize() {
        if (GdxMaster.getFontSizeMod() != 1) {
            setSize(GdxMaster.adjustSize(1020), 900);
        }
    }

    protected void initBg() {
        if (isNinepatch())
            setBackground(new NinePatchDrawable(NinePatchFactory.getInfoPanel()));
        else
            setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));
//
    }

    protected boolean isNinepatch() {
        return false;
    }

    protected void initHeader(TablePanel<Actor> header) {
        header.addNormalSize(preview).left().padLeft(50).padTop(15);
        header.addElement(title).right();

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
         "UI",
         "components",
         "2017",
         "dialog",
         "log",
         "background.png");
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
        if (getUserObject() == null)
            return;
        super.updateAct(delta);
        description.setText(item.description);
        if (item.imagePath == null)
            preview.setDrawable(null);
        else
            preview.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(item.imagePath));

        if (item.previewImagePath == null)
            fullsizePortrait.setDrawable(null);
        else
            fullsizePortrait.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(
             item.previewImagePath));

        title.setText(getTitle());
        pack();
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

    protected String getTitle() {
        return item.name;
    }

    public SelectableItemData getItem() {
        return item;
    }

    public void setItem(SelectableItemData item) {
        this.item = item;
        updateRequired = true;
    }
}
