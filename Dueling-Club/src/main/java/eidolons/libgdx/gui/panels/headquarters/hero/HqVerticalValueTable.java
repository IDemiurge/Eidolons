package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.AbstractValueContainer;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/13/2018.
 */
public   class HqVerticalValueTable extends HqElement {

    private final VALUE[] values;
    private final List<AbstractValueContainer> containers = new ArrayList<>();
    private boolean newLine;
    private boolean displayPropNames =true;
    private boolean displayParamNames =true;
    private boolean displayColumn=true;

    public HqVerticalValueTable(VALUE... values) {
        this(false, values);
    }
    public HqVerticalValueTable(boolean newLine, VALUE... values) {
        this.newLine = newLine;
        this.values = values;
        setBackground(getDefaultBackground());
        GdxMaster.adjustAndSetSize(this, getDefaultWidth(), getDefaultHeight());
        for (VALUE value : values) {
            if (value==null )
            {
                row();
                continue;
            }
            ValueContainer container = new ValueContainer(value.getName() + ": ", "");
//            container.getActor().setSize(200, 50);
            if (isStretch()){
                container.setFixedMinSize(true);
                container.setWidth(getWidth());
            }
            container.setHeight(getHeight()* MathMaster.getFloatWithDigitsAfterPeriod(1, 1f/values.length));
            container.setValueAlignment(getValueAlignment());
            containers.add(container);
            add(container.getActor()).uniform().left().row(); //.left()

            container.setStyle(getLabelStyle());


        }

    }

    protected boolean isStretch() {
        return false;
    }

    protected int getValueAlignment() {
        return Align.center;
    }

    protected LabelStyle getLabelStyle() {
        return StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18));
    }

    protected int getDefaultAlign() {
        return Align.center;
    }

    @Override
    protected Drawable getDefaultBackground() {
        return NinePatchFactory.getLightDecorPanelFilledDrawable();
    }

    protected   int getDefaultWidth(){
       return  380;
    }

    protected   int getDefaultHeight(){
        return 112;
    }

    @Override
    protected void update(float delta) {
        int i = 0;
        for (AbstractValueContainer sub : containers) {
            VALUE val = values[i++];
            updateContainer(sub, val);

        }

    }

    protected void updateContainer(AbstractValueContainer sub, VALUE val) {
        if (val instanceof PROPERTY)
        {
            sub.setValueText(dataSource.getProperty((PROPERTY) val));
            if (!displayPropNames)
                sub.setNameText("");
            else {
                if (displayColumn)
                    sub.setNameText(val.getName() + ": ");
                else
                    sub.setNameText(val.getName() + " ");
            }
        }
        else if (val instanceof PARAMETER)
        {
            sub.setValueText(dataSource.getParamRounded((PARAMETER) val));
            if (!displayParamNames)
                sub.setNameText("");
            else {
                if (displayColumn)
                    sub.setNameText(val.getName() + ": ");
                else
                    sub.setNameText(val.getName() + " ");
            }
        }

        if (isNewLine())
            sub.setValueText("\n" + sub.getValueText());

    }

    public boolean isNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public void setDisplayPropNames(boolean displayPropNames) {
        this.displayPropNames = displayPropNames;
    }

    public void setDisplayColumn(boolean displayColumn) {
        this.displayColumn = displayColumn;
    }

    public void setDisplayParamNames(boolean displayParamNames) {
        this.displayParamNames = displayParamNames;
    }
}
