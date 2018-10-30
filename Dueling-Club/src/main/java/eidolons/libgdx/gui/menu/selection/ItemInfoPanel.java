package eidolons.libgdx.gui.menu.selection;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/29/2017.
 */
public class ItemInfoPanel extends TablePanelX implements SelectableItemDisplayer{


    public static final int WIDTH = 1020;
    public static final int HEIGHT = 850;
    protected DescriptionPanel description;
    protected FadeImageContainer preview;
    protected FadeImageContainer fullsizePortrait;
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

        addElement(header).left().padTop(GDX.size(40))
//         .maxWidth(700).maxHeight(700)
        ;
        row();
        if (description != null)
            addElement(description).left().padLeft(30);
        if (fullsizePortrait != null) {
            addNormalSize(fullsizePortrait).right().padBottom(GDX.size(110)).padRight(GDX.size(42));
            fullsizePortrait.setZIndex(0);
        }
        if (item != null)
            setItem(item);
    }

    protected void initComponents() {
        description = new DescriptionPanel();
        description.setText(getDefaultText());
        title = new Label(getDefaultTitle(), StyleHolder.getSizedLabelStyle(FONT.METAMORPH, 30));
        preview = new FadeImageContainer( (getEmptyImagePath()), 1.4f);
        fullsizePortrait =
         new FadeImageContainer(getEmptyImagePathFullSize(), 1.4f);

    }

    protected void initSize() {
        if (GdxMaster.getFontSizeMod() != 1) {
            setSize(GdxMaster.adjustWidth(WIDTH),  GdxMaster.adjustHeight(HEIGHT));
        }
    }

    protected void initBg() {
        if (isNinepatch())
//            setBackground(new NinePatchDrawable(NinePatchFactory.getInfoPanel()));
            setBackground(new TextureRegionDrawable(new TextureRegion(
             TiledNinePatchGenerator.getOrCreateNinePatch(NINE_PATCH.SAURON,
             BACKGROUND_NINE_PATCH.PATTERN,
              (int) GdxMaster.adjustWidth(WIDTH+30)
              , (int) GdxMaster.adjustHeight(HEIGHT)))));
        else {
            setBackground(TextureCache.getOrCreateTextureRegionDrawable(getBackgroundPath()));
        }
//
    }

    protected boolean isNinepatch() {
        return true;
    }

    protected void initHeader(TablePanel<Actor> header) {
        header.addNormalSize(preview).left().padLeft(GDX.size(70)).padTop(GDX.size(100));
        header.addElement(title).right().padTop(65);

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
         "dc",
         "dialog",
         "log",
         "background.png");
//        return VISUALS.MAIN.getImgPath();
    }


    protected String getEmptyImagePath() {
//        return ImageManager.getEmptyUnitIconPath();
        return "";
    }

    protected String getEmptyImagePathFullSize() {
        return "";
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.updateAct(delta);
        description.setText(item.description);
        if (StringMaster.isEmpty(item.imagePath))
            preview.setImage(getEmptyImagePathFullSize());
        else
            preview.setImage( (item.imagePath));
        getCell(preview).size(preview.getWidth(), preview.getHeight());

        if (item.fullsizeImagePath == null)
            fullsizePortrait.fadeOut();
        else
        {
            if (!TextureCache.isImage(item.fullsizeImagePath))
                fullsizePortrait.setImage(getEmptyImagePathFullSize());
            else
                fullsizePortrait.setImage((item.fullsizeImagePath));
        }
        getCell(fullsizePortrait).size(fullsizePortrait.getWidth(), fullsizePortrait.getHeight());

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
        setUserObject(item);
    }

    @Override
    public Actor getActor() {
        return this;
    }
}
