package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by JustMe on 4/5/2018.
 */
public abstract class TooltipFactory<T, A extends Actor> {

    protected List<Actor> values;
    protected TablePanelX<Actor> container;
    protected float maxWidth;
    protected boolean wrap = true;


    public void add(A actor, T data) {
        Tooltip tooltip = createTooltip(actor);
        tooltip.setUserObject(supplier(data, actor));
        actor.addListener(tooltip.getController());

    }

    protected abstract Tooltip createTooltip(A actor);

    protected abstract Supplier<List<Actor>> supplier(T object, A view);

    protected void addPropStringToValues(BattleFieldObject hero,
                                         PROPERTY v) {
        addPropStringToValues(hero, v, true);
    }

    protected void addPropStringToValues(BattleFieldObject hero,
                                         PROPERTY v, boolean showName) {
        String value = hero.getValue(v);
        if (value.trim().isEmpty()) {
            return;
        }
        value = value.replace(";", ", ");
        value = StringMaster.getWellFormattedString(value);
        final ValueContainer valueContainer =
                new ValueContainer(showName ? v.getDisplayedName() : "", value);

        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(showName ? Align.right : Align.center);
        add(valueContainer);
    }

    // private String[] splitString(String value, boolean showName) {
    //     if (showName) {
    //         return new String[]{value};
    //     }
    //     return array;
    // }

    protected void addParamStringToValues(BattleFieldObject hero,
                                          PARAMETER param) {
        if (hero.getIntParam(param) > 0) {
            String value = hero.getStrParam(param);
            String key = param.getDisplayedName();
            addKeyAndValue(key, value);
        }
    }

    protected void addKeyAndValue(String key, String value) {
        final ValueContainer valueContainer =
                new ValueContainer(key, value);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        add(valueContainer);
    }

    protected ValueContainer getValueContainer(BattleFieldObject hero, PARAMS cur, PARAMS max) {
        final Integer cv = NumberUtils.getIntParse(hero.getCachedValue(max));
        final Integer v = hero.getIntParam(cur);
        final String name = max.getDisplayedName();
        final TextureRegion iconTexture =

                TextureCache.getOrCreateR(
                        ImageManager.getValueIconPath(max)
                        //          "ui/value icons/" +
                        //         name.replaceAll("_", " ") + ".png"
                );
        final ValueContainer valueContainer = new ValueContainer(iconTexture, name, v + "/" + cv);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        return valueContainer;
    }

    protected void addStyledContainer(Label.LabelStyle style, String s, String s1) {
        add(new ValueContainer(style, s, s1));
    }

    public void add(Actor actor) {
        if (container != null) {
            container.add(actor);
            if (wrap) {
                container.row();
            }
        } else {
            if (actor instanceof ValueContainer) {
                ((ValueContainer) actor).wrapText(maxWidth);
            } else if (actor instanceof TablePanel) {
                actor.setWidth(maxWidth);
            }
            values.add(actor);
        }
    }

    protected TablePanelX addNameContainer(String toolTip) {
        TablePanelX<Actor> table = new TablePanelX<>();
        // table.setBackground(TextureCache.getOrCreateTextureRegionDrawable(Images.ZARK_BTN_LARGE));
        table.addBackgroundActor(new ImageContainer((Images.ZARK_BTN_LARGE)));
        Label.LabelStyle style = StyleHolder.getHqLabelStyle(20);
        LabelX lbl;
        table.addActor(lbl = new LabelX(toolTip, style));
        GdxMaster.center(lbl);
        // table.setX(1);

        TablePanelX table1 = new TablePanelX() {
            @Override
            public void layout() {
                // super.layout();
                for (Actor child : getChildren()) {
                    child.setY(-85);
                }
                table.setY(0);
                table.setX(GdxMaster.centerWidth(table));

            }
        };
        table1.add(table);
        add(table1);
        return table1;
    }

    protected void addTitleContainer(String name, String border, TextureRegion textureRegion) {
        TablePanelX table1 = addNameContainer(name);
        TablePanelX<Actor> table = new TablePanelX<>();
        table.addBackgroundActor(new ImageContainer(new Image(textureRegion)));
        // table.setBackground(TextureCache.getOrCreateTextureRegionDrawable(border));
        Image borderImg;
        table.addActor(borderImg = new Image(TextureCache.getOrCreateR(border)));
        // GdxMaster.center(borderImg);
        table1.row();
        table1.add(table).left();
    }

    protected void startContainer() {
        container = new TablePanelX<>();
        container.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
    }

    protected void endContainer() {
        if (container == null) {
            return;
        }
        values.add(container);
        container = null;
    }

}
