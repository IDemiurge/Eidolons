package eidolons.libgdx.gui.panels.headquarters.weave.actor;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.bf.DynamicLayeredActor;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveHighlightable;
import eidolons.libgdx.gui.panels.headquarters.weave.model.WeaveDataNode;
import eidolons.libgdx.gui.tooltips.SmartClickListener;

/**
 * Created by JustMe on 6/25/2018.
 */
public class WeaveNodeActor extends DynamicLayeredActor implements WeaveHighlightable{
    private WeaveDataNode parentNode;
    private WeaveLinkActor link;

    public WeaveNodeActor(String rootPath) {
        super(rootPath);
        addListener(getListener());
    }

    private EventListener getListener() {
        return new SmartClickListener(this){
            @Override
            protected void onDoubleClick(InputEvent event, float x, float y) {
                super.onDoubleClick(event, x, y);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }

            @Override
            protected void entered() {
                highlight();
                super.entered();
            }

            @Override
            protected void exited() {
                super.exited();
            }
        };
    }

    @Override
    public void highlight() {
        link.highlight();
        overlay.fadeOut();
    }

    @Override
    public void highlightOff() {
        link.highlightOff();
        overlay.fadeIn();

    }

    public void setParentNode(WeaveDataNode parentNode) {
        this.parentNode = parentNode;
    }

    public WeaveDataNode getParentNode() {
        return parentNode;
    }

    public void setLink(WeaveLinkActor link) {
        this.link = link;
    }

    public WeaveLinkActor getLink() {
        return link;
    }

}
