package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/13/2018.
 */
public   class HqVerticalValueTable extends HqElement {

    private final VALUE[] values;
    private final List<ValueContainer> containers = new ArrayList<>();
    private boolean newLine;
    private boolean displayProps=true;
    private boolean displayParams=true;
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
            ValueContainer container = new ValueContainer(value.getName() + ": ", "");
            containers.add(container);
            add(container).uniform().row(); //.left()
            container.setWidth(Align.left);
            container.setStyle(StyleHolder.getHqLabelStyle(GdxMaster.adjustFontSize(18)));
            container.setValueAlignment(Align.right);
            container.setNameAlignment(Align.left);

        }

    }

    @Override
    protected Drawable getDefaultBackground() {
        return NinePatchFactory.getLightPanelDrawable();
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
        for (ValueContainer sub : containers) {
            VALUE val = values[i++];
            if (val instanceof PROPERTY)
            {
                sub.setValueText(dataSource.getProperty((PROPERTY) val));
                if (!displayProps)
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
                if (!displayParams)
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

    }

    public boolean isNewLine() {
        return newLine;
    }

    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }

    public void setDisplayProps(boolean displayProps) {
        this.displayProps = displayProps;
    }

    public void setDisplayColumn(boolean displayColumn) {
        this.displayColumn = displayColumn;
    }

    public void setDisplayParams(boolean displayParams) {
        this.displayParams = displayParams;
    }
}
