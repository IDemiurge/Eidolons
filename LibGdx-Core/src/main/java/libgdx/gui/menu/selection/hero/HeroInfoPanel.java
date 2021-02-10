package libgdx.gui.menu.selection.hero;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.GDX;
import libgdx.StyleHolder;
import libgdx.gui.LabelX;
import libgdx.gui.menu.selection.ItemInfoPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.TablePanel;
import libgdx.texture.TextureCache;
import eidolons.system.text.HelpMaster;
import libgdx.TiledNinePatchGenerator;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.Strings;
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
        c.setActorY(c.getActorY()+ TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        description.setY(description.getY()+ GDX.height(30));

        description.setX(45);
        fullsizePortrait.setY(TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        fullsizePortrait.setX(400);
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
        return super.getTitle() + Strings.NEW_LINE + item.
         getEntity().getProperty(G_PROPS.DESCRIPTION);
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == null)
            return;
        super.updateAct(delta);
        emblem.setDrawable(TextureCache.getOrCreateTextureRegionDrawable(item.getEntity().getProperty(G_PROPS.EMBLEM)));
        mainInfo.setText(getOverviewText(item.getEntity()));

    }

    protected String getOverviewText(Entity entity) {
        return HelpMaster.getHeroMainInfoText(item.getName());
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

    private TextureRegion getDefaultEmblem() {
        return
         TextureCache.getOrCreateR(
          StrPathBuilder.build(PathFinder.getEmblemAutoFindPath()
           ,"unknown.png")
         );
    }
}
