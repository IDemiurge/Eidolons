package eidolons.libgdx.gui.panels.headquarters.creation.selection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.menu.selection.DescriptionPanel;
import eidolons.libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.TextBuilder;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 7/3/2018.
 */
public class DescriptionScroll extends TablePanelX {

    private static final String SCROLL = Images.HC_SCROLL_BACKGROUND;

    DescriptionPanel description;
    FadeImageContainer preview;
    FadeImageContainer preview2;

    public DescriptionScroll() {
        super(TextureCache.getOrCreateR(SCROLL).getRegionWidth(),
         TextureCache.getOrCreateR(SCROLL).getRegionHeight());
        addActor(new Image(TextureCache.getOrCreateR(SCROLL)));
        boolean previewsOn = isPreviewsOn();
        if (previewsOn) {
            TablePanelX previews = new TablePanelX();
            previews.add(preview = new FadeImageContainer()).top().left().row();
            previews.add(preview2 = new FadeImageContainer()).bottom().left();
            add(previews).growY();
        }
        addText();


    }

    protected void addText() {
        Cell cell = add(description = new DescriptionPanel() {
            @Override
            protected float getDefaultWidth() {
                if (isFillText())
                    return 0;
                return super.getDefaultWidth() * (isPreviewsOn() ? 0.66f : 1f);
            }

            @Override
            protected float getDefaultHeight() {
                if (isFillText())
                    return 0;
                return super.getDefaultHeight();
            }

            @Override
            protected TextBuilder getTextBuilder() {
                return new TextBuilder() {
                    @Override
                    public Message build(float w) {
                        return super.build(0);
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
        }).pad(57, 10, 57, 10).top();
        if (isFillText())
        {
            cell.grow();
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

        description.setText(data.getDescription());
        if (isPreviewsOn()) {
            preview.setImage(data.getPreviewImagePath());
            preview2.setImage(
             StringMaster.getAppendedImageFile(
              data.getPreviewImagePath(), " alt", true));

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
