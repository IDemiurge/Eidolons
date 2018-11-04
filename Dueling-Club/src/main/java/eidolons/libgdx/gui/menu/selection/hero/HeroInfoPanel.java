package eidolons.libgdx.gui.menu.selection.hero;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH_PADDING;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.menu.selection.ItemInfoPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.text.HelpMaster;
import main.content.values.properties.G_PROPS;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroInfoPanel extends ItemInfoPanel {
    LabelX mainInfo;
    Image emblem; //TODO implement choice/ color change
    TablePanel stats; // class, power,

    //++ view inv, spellbook, info panel
    public HeroInfoPanel(SelectableItemData item) {
        super(item);


    }

    protected void afterLayout() {
        super.afterLayout();
        Cell c = getCell(fullsizePortrait);
        c.setActorY(c.getActorY()+ NINE_PATCH_PADDING.SAURON.bottom);
        description.setY(description.getY()+GDX.height(20));
        fullsizePortrait.setY(NINE_PATCH_PADDING.SAURON.bottom);
    }
    @Override
    protected float getDescriptionHeight() {
        return super.getDescriptionHeight()*3/4;
    }

    @Override
    protected String getEmptyImagePath() {
        return "";
    }

    @Override
    protected String getEmptyImagePathFullSize() {
        return "";
    }

    @Override
    protected String getTitle() {
        return super.getTitle() + StringMaster.NEW_LINE + item.
         getEntity().getProperty(G_PROPS.DESCRIPTION);
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.updateAct(delta);
        emblem.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(item.getEntity().getProperty(G_PROPS.EMBLEM)));
        mainInfo.setText(HelpMaster.getHeroMainInfoText(item.getName()));

    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {
        super.initHeader(header);
        emblem = new Image(getDefaultEmblem());
//        header.addNormalSize(preview).left();
//        header.addElement(title).left().padTop(65);
//        header.addNormalSize(emblem).colspan(2). right().padRight(20);
//        header.pack();
        header.row();
        mainInfo = new LabelX("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 20));
        mainInfo.setWrap(true);
        mainInfo.setMaxWidth(GDX.size(ItemInfoPanel.WIDTH-50)-500);
        mainInfo.setText("A touch of Fate...");

        header.add(mainInfo).maxWidth(GDX.size(ItemInfoPanel.WIDTH-50)-500). padLeft(50). padTop(50);

    }

    @Override
    protected boolean isRandomDefault() {
        return true;
    }

    private Texture getDefaultEmblem() {
        return
         TextureCache.getOrCreate(
          StrPathBuilder.build("ui",
           "emblems",
           "auto",
           "unknown.png")
         );
    }
}
