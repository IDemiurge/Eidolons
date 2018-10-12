package eidolons.libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.TextBuilder;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 7/3/2018.
 */
public class DescriptionScroll extends TablePanelX {

    private static final String SCROLL = Images.HC_SCROLL_BACKGROUND;

    LabelX title;
    DescriptionPanel description;
    FadeImageContainer preview;
    FadeImageContainer preview2;

    public DescriptionScroll() {
        super(TextureCache.getOrCreateR(SCROLL).getRegionWidth(),
         TextureCache.getOrCreateR(SCROLL).getRegionHeight());
        //        addActor(new Image(TextureCache.getOrCreateR(SCROLL)));
        setBackground(new TextureRegionDrawable(TextureCache.getOrCreateR(SCROLL)));
        boolean previewsOn = isPreviewsOn();
        if (previewsOn) {
            TablePanelX previews = new TablePanelX();
            previews.add(preview = new FadeImageContainer()).left();
            previews.add(title = new LabelX("", 20)).center();
            previews.add(preview2 = new FadeImageContainer()).row();
            add(previews).growX().top().pad(90, 5, 0, 5).row();
        } else {

            add(title = new LabelX("", 20)).top().row();
        }
        addText();


    }

    protected void addText() {
        Cell cell = add(description = new DescriptionPanel() {
            @Override
            protected float getDefaultWidth() {
                if (isFillText())
                    return 0;
                return DescriptionScroll.this.getWidth();
            }
            protected void padScroll(ScrollPanel scrollPanel) {
                scrollPanel.pad(30,10,50,150);
            }
            @Override
            protected float getDefaultHeight() {
                if (isFillText())
                    return 0;
                return DescriptionScroll.this.getHeight() - 200;
            }

            @Override
            protected float getTextLineWidth() {
                return 0;
            }

            @Override
            protected TextBuilder getTextBuilder() {
                return new TextBuilder() {
                    @Override
                    public Message build(float w) {
                        return super.build(w);
                    }

                    @Override
                    protected void pad(Message message) {
                        message.padTop(5);
                        message.padBottom(5);
                        message.padLeft(10);
                        message.padRight(10);
                    }

                    @Override
                    protected FONT getFontStyle() {
                        return FONT.METAMORPH;
                    }

                    @Override
                    protected int getFontSize() {
                        return 16;
                    }

                    @Override
                    protected Color getColor() {
                        return new Color(0, 0, 0, 1);
                    }
                };
            }
        })//.pad(57, 10, 57, 10)
         .top();
        if (isFillText()) {
            cell.grow().pad(0, 10, 57, 10);
        }
        debug();
    }

    protected boolean isFillText() {
        return true;
    }

    protected boolean isPreviewsOn() {
        return true;
    }


    @Override
    protected Class<?> getUserObjectClass() {
        return SelectableItemData.class;
    }

    @Override
    public void updateAct(float delta) {
        SelectableItemData data = (SelectableItemData) getUserObject();
        title.setText(data.getName());
        description.setText(data.getDescription());
        if (isPreviewsOn()) {
            preview.setImage(data.getFullsizeImagePath());
            if (ImageManager.isImage(data.getImagePath()))
                preview2.setImage(data.getImagePath());
            else
                preview2.setImage(
                 StringMaster.getAppendedImageFile(
                  data.getFullsizeImagePath(), " alt", true));

        }
        super.updateAct(delta);
    }

    public float getMaxWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getMaxWidth();
    }

    @Override
    public float getMaxHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getMaxHeight();
    }

    @Override
    public float getMinHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getMinHeight();
    }

    @Override
    public float getMinWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getMinWidth();
    }
}
