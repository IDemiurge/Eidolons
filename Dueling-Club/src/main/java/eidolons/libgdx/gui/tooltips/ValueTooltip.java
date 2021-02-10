package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster;
import main.system.text.TextWrapper;

import java.util.ArrayList;
import java.util.List;

public class ValueTooltip extends Tooltip {

    public static final Object WAIT_FOR_RESET = new ArrayList<>();
    private int valueAlign = Align.left;
    private int nameAlign = Align.left;

    public ValueTooltip(String text) {
        this(null, text);
    }

    public ValueTooltip(TextureRegion texture, String text) {
        super();
        setUserObject(new ListMaster<ValueContainer>().
                getList(new ValueContainer(texture, text)));

    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public float getMaxWidth() {
        return getDefaultBackground().getMinWidth();
    }

    public ValueTooltip() {
        super();
    }

    public ValueTooltip(Actor actor) {
        super(actor);
        setUserObject(new ListMaster<Actor>().
                getList(actor));
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject() == WAIT_FOR_RESET || getUserObject() == null) {
            setUpdateRequired(true);
            return;
        }
        clearChildren();
        List<Actor> values = (List<Actor>) getUserObject();

        values.forEach(actor -> {
            if (actor instanceof ValueContainer) {
                ValueContainer el = (ValueContainer) actor;
                //            el.setFixedMinSize(true);
                if (el.getValueText() != null)
                    el.setValueText(TextWrapper.processText((int) (getMaxWidth() * 0.86f), el.getValueText(), el.getValueLabel().getStyle()));
                if (el.getNameLabel() != null)
                    el.getNameLabel().setText(TextWrapper.processText((int) (getMaxWidth() * 0.86f),
                            el.getNameLabel().getText().toString(),
                            el.getNameLabel().getStyle()));

                el.setValueAlignment(getValueAlign());
                el.setNameAlignment(getNameAlign());
                if (el.getNameLabel() != null)
                    if (el.getNameLabel().getStyle() == getDefaultLabelStyle()) {
                        el.setNameStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 20));
                    }
                if (el.getValueLabel() != null)
                    if (el.getValueLabel().getStyle() == getDefaultLabelStyle()) {
                        el.setValueStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 20));
                    }
            }
            if (actor.getX() != 0 || actor.getY() != 0)
            {
                if (actor.getWidth()>getWidth())
                {
                    setWidth(actor.getWidth());
                    setFixedSize(true);
                    main.system.auxiliary.log.LogMaster.log(1,actor.getWidth()+" Width from" +actor);
                }
                addActor(actor);
            }
            else {
                addElement(actor).center();
                row();
            }
        });
    }

    protected Label.LabelStyle getDefaultLabelStyle() {
        return StyleHolder.getDefaultLabelStyle();
    }

    @Override
    public float getPrefWidth() {
        return super.getPrefWidth();
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        if (getDefaultBackground() != null)
            setBackground(getDefaultBackground());
    }

    protected Drawable getDefaultBackground() {
        return new NinePatchDrawable(NinePatchFactory.getLightDecorPanelFilledDrawable());
    }

    public int getValueAlign() {
        return valueAlign;
    }

    public void setValueAlign(int valueAlign) {
        this.valueAlign = valueAlign;
    }

    public int getNameAlign() {
        return nameAlign;
    }

    public void setNameAlign(int nameAlign) {
        this.nameAlign = nameAlign;
    }
}
