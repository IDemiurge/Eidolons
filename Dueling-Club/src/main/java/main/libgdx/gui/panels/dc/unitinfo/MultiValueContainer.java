package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.ArrayList;
import java.util.List;

import static main.libgdx.StyleHolder.getDefaultLabelStyle;

public class MultiValueContainer extends ValueContainer {

    private List<Container> values = new ArrayList<>();

    public MultiValueContainer(TextureRegion texture, String name, String... value) {
        init(texture, name, value);
    }

    public MultiValueContainer(TextureRegion texture) {
        init(texture, null, (String[]) null);
    }

    public MultiValueContainer(TextureRegion texture, String[] value) {
        init(texture, null, value);

    }

    public MultiValueContainer(String name, String... value) {
        init(null, name, value);
    }

    protected void init(TextureRegion texture, String name, String[] values) {
        super.init(texture, name, null);
        if (values != null) {
            Table table = new Table();
            //super.init() initialize value field as empty Container
            this.valueContainer.setActor(table);
            for (String value : values) {
                Container<Label> labelContainer = new Container<>(new Label(value, getDefaultLabelStyle()));
                this.values.add(labelContainer);
                table.add(labelContainer);
                if (isVertical()) {
                    table.row();
                }
            }
        }
    }

    @Override
    public void clear() {
        super.clear();
        values.clear();
    }

    public List getValues() {
        return values;
    }
}
