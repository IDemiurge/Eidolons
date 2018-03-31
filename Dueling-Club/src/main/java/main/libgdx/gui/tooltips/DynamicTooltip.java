package main.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.function.Supplier;

/**
 * Created by JustMe on 2/10/2018.
 */
public class DynamicTooltip extends ValueTooltip {

    Supplier<String> text;
    Supplier<TextureRegion> pic;

    public DynamicTooltip(Supplier<String> text) {
        this.text = text;
    }

    public DynamicTooltip(Supplier<TextureRegion> texture,
                          Supplier<String> text) {
        this.text = text;
        this.pic = texture;
        updateRequired = true;
    }

    @Override
    protected void onMouseEnter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        updateRequired = true;
        super.onMouseEnter(event, x, y, pointer, fromActor);
    }

    @Override
    public void act(float delta) {
        //would be better to update - maybe time could change while displayed
        setUserObject(new ValueContainer(pic == null ? null : pic.get(),
         text == null ? null : text.get()));
        super.act(delta);
    }
}
