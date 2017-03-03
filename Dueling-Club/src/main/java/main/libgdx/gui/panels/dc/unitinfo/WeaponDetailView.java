package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.bf.mouse.ToolTipManager;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class WeaponDetailView extends Container<Image> {
    public WeaponDetailView(TextureRegion icon) {
        setActor(new Image(icon));
        fill().left().bottom();
        setClip(true);
        setTouchable(Touchable.enabled);
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                ToolTipManager.ToolTipRecordOption toolTipRecordOption = new ToolTipManager.ToolTipRecordOption();
                toolTipRecordOption.name = "тут когда то будет нормальный тултип";
                toolTipRecordOption.curVal = 100;
                toolTipRecordOption.maxVal = 100;

                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(toolTipRecordOption));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(null));
            }
        });
    }
}
