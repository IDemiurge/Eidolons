package main.libgdx.gui.menu.selection.hero;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.content.values.properties.G_PROPS;
import main.libgdx.StyleHolder;
import main.libgdx.gui.menu.selection.ItemInfoPanel;
import main.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.text.HelpMaster;

/**
 * Created by JustMe on 11/29/2017.
 */
public class HeroInfoPanel extends ItemInfoPanel {
    Label mainInfo;
    Image emblem ; //TODO implement choice/ color change
    TablePanel stats; // class, power,
    //++ view inv, spellbook, info panel
    public HeroInfoPanel(SelectableItemData item) {
        super(item);


    }

    @Override
    protected String getTitle() {
        return super.getTitle() + StringMaster.NEW_LINE + item.
         getEntity().getProperty(G_PROPS.DESCRIPTION);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        emblem.setDrawable( TextureCache.getOrCreateTextureRegionDrawable(item.getEntity().getProperty(G_PROPS.EMBLEM)));
        mainInfo.setText(HelpMaster.getHeroMainInfoText(item.getName()));
    }

    @Override
    protected boolean isRandomDefault() {
        return true;
    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {
        super.initHeader(header);
        emblem = new Image(getDefaultEmblem());
        header.addNormalSize(emblem);
        header.row();
        mainInfo = new Label("", StyleHolder.getSizedLabelStyle(FONT.MAGIC, 18));
        mainInfo.setText("A touch of Fate...");
        header.addNormalSize(mainInfo);
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
