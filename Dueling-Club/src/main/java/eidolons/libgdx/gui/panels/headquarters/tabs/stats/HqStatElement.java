package eidolons.libgdx.gui.panels.headquarters.tabs.stats;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.PointMaster;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled.STD_BUTTON;
import eidolons.libgdx.gui.generic.btn.TextButtonX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel.HQ_OPERATION;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.values.parameters.PARAMETER;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 4/16/2018.
 * <p>
 * orientation?
 */
public class HqStatElement extends HqElement {

    private final ValueContainer container;
    private final TextButtonX button;
    boolean mastery;
    boolean editable;
    PARAMS displayedParam;
    private boolean leftToRight;
    private PARAMETER modifyParam;
    private boolean disabled;

    public HqStatElement(PARAMS param, boolean mastery, boolean editable) {
        this.displayedParam = param;
        this.mastery = mastery;
        leftToRight = mastery;
        this.editable = editable;
//   TODO      leftToRight = mastery;
        setSize(GDX.size(80), GDX.size(50));

        container = new ValueContainer(TextureCache.getOrCreateR(
         Images.EMPTY_PERK_SLOT), "    ");
        container.setStyle(StyleHolder.getHqLabelStyle(16));
        container.overrideImageSize(50, 50);
        container.getValueContainer().size(30, 50);
        if (leftToRight)
            container.getValueContainer().left();
        else
            container.getValueContainer().right();

        button = new TextButtonX(STD_BUTTON.STAT);
        button.setVisible(false);
        button.addListener(getListener());
        button.setFixedSize(true);
        button.setSize(
         STD_BUTTON.STAT.getTexture().getMinWidth(),
         STD_BUTTON.STAT.getTexture().getMinHeight());

        if (leftToRight) {
            add(button).left();
            add(container).right();
        } else {
            add(container).left();
            add(button).right();
        }

    }

    private EventListener getListener() {
        return new SmartClickListener(this) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                super.onTouchDown(event, x, y);
                if (modifyParam==null )
                    return;
                if (disabled)
                    return;
                if (event.getButton() == 1) {
                    EUtils.showTextTooltip(
                     DescriptionMaster.getDescription(modifyParam));
                    return;
                }
//              redundant  if (PointMaster.canIncrease(dataSource.getEntity(), modifyParam)) {
                HqDataMaster.operation(dataSource,
                 mastery
                  ? HQ_OPERATION.MASTERY_INCREMENT
                  : HQ_OPERATION.ATTRIBUTE_INCREMENT, modifyParam);
//                }
            }
        };
    }

    public void setDisplayedParam(PARAMS displayedParam) {
        this.displayedParam = displayedParam;
    }

    @Override
    protected void update(float delta) {
        container.clearListeners();
        if (displayedParam != null) {
            button.setVisible(editable);
            disabled=!PointMaster.canIncrease(dataSource.getEntity(), modifyParam);
            button.setDisabled(disabled);
            container.setImage(ImageManager.getValueIconPath(displayedParam));
            container.setValueText(dataSource.getParamRounded(displayedParam));
            container.getValueContainer().setActor(new Label(dataSource.getParamRounded(displayedParam),
             StyleHolder.getHqLabelStyle(16)) {
                @Override
                public float getPrefWidth() {
                    return 20;
                }
            });
            container.addListener(new ValueTooltip(displayedParam.getName()).getController());

            container.getValueContainer().padLeft(5);
            container.getValueContainer().padRight(5);
        } else
            container.addListener(new ValueTooltip("Mastery Slot").getController());

        if (mastery) {
            container.addListener(getNewMasteryListener());
        }
    }

    private EventListener getNewMasteryListener() {
        return new SmartClickListener(container) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                if (displayedParam != null)
                    return;
                GuiEventManager.trigger(GuiEventType.SHOW_MASTERY_LEARN,
                 getUserObject());
            }
        };
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    public PARAMETER getModifyParam() {
        return modifyParam;
    }

    public void setModifyParam(PARAMETER modifyParam) {
        this.modifyParam = modifyParam;
    }
}
