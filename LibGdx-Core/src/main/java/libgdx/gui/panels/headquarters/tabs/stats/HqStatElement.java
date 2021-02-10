package libgdx.gui.panels.headquarters.tabs.stats;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.content.DescriptionMaster;
import eidolons.content.PARAMS;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.PointMaster;
import libgdx.GDX;
import libgdx.StyleHolder;
import libgdx.gui.generic.ValueContainer;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.panels.headquarters.HqElement;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.gui.tooltips.ValueTooltip;
import libgdx.texture.Images;
import libgdx.texture.TextureCache;
import eidolons.system.text.DescriptionTooltips;
import libgdx.gui.generic.btn.ButtonStyled;
import main.content.values.parameters.PARAMETER;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StrPathBuilder;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 4/16/2018.
 * <p>
 * orientation?
 */
public class HqStatElement extends HqElement {

    private final ValueContainer container;
    private final SmartTextButton button;
    private final Runnable modifyRunnable;
    boolean mastery;
    boolean editable;
    PARAMS displayedParam;
    private PARAMETER modifyParam;
    private boolean disabled;

    public HqStatElement(PARAMS param, boolean mastery, boolean editable, Runnable modifyRunnable) {
        this.modifyRunnable = modifyRunnable;
        this.displayedParam = param;
        this.mastery = mastery;
        this.editable = editable;
//   TODO      leftToRight = mastery;
        setSize(GDX.size(80), GDX.size(50));

        container = new ValueContainer(TextureCache.getOrCreateR(
         Images.EMPTY_PERK_SLOT), "    ");
        container.setStyle(StyleHolder.getHqLabelStyle(16));
        container.overrideImageSize(50, 50);
        container.getValueContainer().size(30, 50);
        if (mastery)
            container.getValueContainer().left();
        else
            container.getValueContainer().right();

        button = new SmartTextButton(ButtonStyled.STD_BUTTON.STAT);
        button.setVisible(false);
        button.addListener(getListener());
        button.setFixedSize(true);
        TextureRegion r = TextureCache.getOrCreateR(StrPathBuilder.build(PathFinder.getUiPath(),
                "components", "hq", "stats", "cross.png"));
        button.setSize(22, 22);
//                r.getRegionWidth(), r.getRegionHeight());
//         STD_BUTTON.STAT.getTexture().getMinWidth(), TODO atlas..
//         STD_BUTTON.STAT.getTexture().getMinHeight());

        if (mastery) {
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
                modifyRunnable.run();
            }
        };
    }

    public void setDisplayedParam(PARAMS displayedParam) {
        this.displayedParam = displayedParam;
    }

    @Override
    protected void update(float delta) {
        if (dataSource == null) {
            return;
        }
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
            container.addListener(new ValueTooltip(DescriptionTooltips.tooltip(displayedParam, getUserObject().getEntity())).getController());

            container.getValueContainer().padLeft(5);
            container.getValueContainer().padRight(5);
        } else
        {
            container.addListener(new ValueTooltip("Mastery Slot\n" +
                    "Hero can have up to [10] Mastery types unlocked " +
                    "(click to learn new ones))").getController());

        }

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
