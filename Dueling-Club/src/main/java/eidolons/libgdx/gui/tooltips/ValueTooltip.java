package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.system.auxiliary.data.ListMaster;
import main.system.text.TextWrapper;

import java.util.ArrayList;
import java.util.List;

public class ValueTooltip extends Tooltip {

    public static final Object WAIT_FOR_RESET =   new ArrayList<>();
    private int valueAlign= Align.left;
    private int nameAlign= Align.left;

    public ValueTooltip(String text) {
        this(null, text);
    }

    public ValueTooltip(TextureRegion texture, String text) {
        super();
        setUserObject(new ListMaster<ValueContainer>().
         getList(new ValueContainer(texture, text)));

    }

    @Override
    public float getMaxWidth() {
        return GdxMaster.getWidth()/3;
    }

    public ValueTooltip() {
        super();
    }

    public ValueTooltip(Actor actor) {
        super(actor);
    }

    @Override
    public void updateAct(float delta) {
        if (getUserObject()==WAIT_FOR_RESET || getUserObject()==null ){
            updateRequired=true;
            return;
        }
        clearChildren();
        List<ValueContainer> values = (List<ValueContainer>) getUserObject();


        values.forEach(el -> {
            if (el.getValueText() != null)
                el.setValueText(TextWrapper.processText((int) getMaxWidth(), el.getValueText(), el.getValueLabel().getStyle()));
            if (el.getNameLabel() != null)
                el.getNameLabel().setText(TextWrapper.processText((int) getMaxWidth(),
                 el.getNameLabel().getText().toString(),
                 el.getNameLabel().getStyle()));

            el.setValueAlignment(getValueAlign());
            el.setNameAlignment(getNameAlign());
            addElement(el);
            row();
        });
    }

    @Override
    public void afterUpdateAct(float delta) {
        super.afterUpdateAct(delta);
        if (getDefaultBackground()!=null )
        setBackground(getDefaultBackground());
    }

    protected Drawable getDefaultBackground() {
        return  new NinePatchDrawable(NinePatchFactory.getLightPanelFilled());
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
