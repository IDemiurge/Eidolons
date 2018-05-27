package eidolons.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.text.TextWrapper;

import java.util.List;

public class ValueTooltip extends Tooltip {

    public ValueTooltip(String text) {
        this(null, text);
    }

    public ValueTooltip(TextureRegion texture, String text) {
        super();
        setUserObject(new ListMaster<ValueContainer>().
         getList(new ValueContainer(texture, text)));

    }

    private String processText(String text, LabelStyle style) {
        if (text.isEmpty())
            return "";
        String newText = "";
        int maxLength = (int) (getMaxWidth() / style.font.getSpaceWidth());
        for (String substring : StringMaster.openContainer(text, StringMaster.NEW_LINE)) {
            if (substring.length()>maxLength)
                substring = TextWrapper.wrapWithNewLine(substring, maxLength);
            newText += substring+ StringMaster.NEW_LINE;
        }
        return newText.substring(0, newText.length()-1);
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
        clear();
        List<ValueContainer> values = (List<ValueContainer>) getUserObject();

        values.forEach(el -> {
            el.setValueText(processText( el.getValueText(), el.getValueLabel().getStyle()));
            el.setValueAlignment(Align.left);
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
}
