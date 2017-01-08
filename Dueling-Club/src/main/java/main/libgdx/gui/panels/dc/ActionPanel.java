package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.libgdx.gui.panels.generic.PagedListPanel;
import main.libgdx.texture.TextureManager;
import main.system.EventCallback;
import main.system.EventCallbackParam;
import main.system.auxiliary.GuiManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 1/5/2017.
 */
public class ActionPanel<T extends DC_Obj> extends PagedListPanel {


    private DC_HeroObj hero;
    private EventCallback event;
    private List<T> actives;

    public ActionPanel(DC_HeroObj hero
            , final EventCallback event,
                       int columns) {
        super(columns, 1);
        this.hero = hero;
        this.event = event;
    }

    public void init(Collection<T> activeObjs) {
        int w = GuiManager.getSmallObjSize();
        int x = 0;
        actives = new LinkedList<T>(activeObjs);
        actives.forEach(a -> {
            addActor(new Image(TextureManager.getOrCreate(a.getImagePath())));
//            x += w;

        });
    }

    public void clicked(T obj) {
        event.call(new EventCallbackParam<T>(obj));
    }


}
