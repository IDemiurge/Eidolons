package main.libgdx.screens.map.obj;

import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.screens.map.obj.PlaceActorFactory.PlaceActorParameters;

/**
 * Created by JustMe on 2/9/2018.
 */
public class PlaceActor extends MapActor {

//    Image preview;

    public PlaceActor(PlaceActorParameters parameters) {
        super(parameters.mainIcon);
        init(parameters);
    }

    private void init(PlaceActorParameters parameters) {
//        preview = new Image(parameters.preview);
        setPosition(parameters.position.x, parameters.position.y);
//        icon.setPosition(GdxMaster.right(emblem), GdxMaster.top(emblem) );
        addListener(new ValueTooltip(parameters.preview,parameters. name).getController());
    }


}
