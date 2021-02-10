package libgdx.gui.panels.dc.unitinfo.old;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import libgdx.gui.LabelX;
import libgdx.gui.generic.ValueContainer;
import libgdx.StyleHolder;

import java.util.ArrayList;
import java.util.List;

import static libgdx.StyleHolder.getDefaultLabelStyle;

public class MultiValueContainer extends ValueContainer {

    private final List<Container<LabelX>> values = new ArrayList<>();

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
            getValueContainer().setActor(table);
            for (String value : values) {
                Container<LabelX> labelContainer = new Container<>(new LabelX(value, StyleHolder.getDefaultLabelStyle()));
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

    public List<ValueContainer> separate() {
        List<ValueContainer> result = new ArrayList<>();
        if (imageContainer.getActor() != null) {
            result.add(new ValueContainer(imageContainer.getActor().getContent()));
        } else {
            result.add(null);
        }
        result.add(new ValueContainer(nameContainer.getActor()));
        values.forEach(el -> result.add(new ValueContainer(el.getActor())));

        return result;
    }
}
